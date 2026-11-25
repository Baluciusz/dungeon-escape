package dungeon.controller;

import dungeon.domain.Difficulty;
import dungeon.domain.GameState;
import dungeon.domain.HighScoreEntry;
import dungeon.domain.Inventory;
import dungeon.domain.Item;
import dungeon.domain.ItemNotFoundException;
import dungeon.domain.Leaderboard;
import dungeon.domain.Player;
import dungeon.event.EventOutcome;
import dungeon.event.GameEvent;
import dungeon.factory.EventSelector;
import dungeon.persistence.HighScoreFileException;
import dungeon.persistence.HighScoreRepository;
import dungeon.ui.AsciiArt;
import dungeon.ui.ConsoleUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Runs the game: main menu, floors, events, inventory, end screens and high scores.
 * <p>
 * The controller connects the parts but contains no event rules itself:
 * events come from the {@link EventSelector} (Factory Method) and are used
 * only through the abstract {@link GameEvent} type.
 */
public class GameController {

    public static final int MAX_NAME_LENGTH = 12;

    private final ConsoleUI ui;
    private final EventSelector eventSelector;
    private final HighScoreRepository highScores;
    private final Random random;

    public GameController(ConsoleUI ui, EventSelector eventSelector,
                          HighScoreRepository highScores, Random random) {
        if (ui == null || eventSelector == null || highScores == null || random == null) {
            throw new IllegalArgumentException("Controller dependencies must not be null");
        }
        this.ui = ui;
        this.eventSelector = eventSelector;
        this.highScores = highScores;
        this.random = random;
    }

    /** Shows the main menu until the player chooses Exit. */
    public void run() {
        loadLeaderboard(); // checks the high score file at start-up and reports problems early
        boolean running = true;
        while (running) {
            ui.printBlankLine();
            ui.print(AsciiArt.mainMenuBanner());
            ui.printBlankLine();
            ui.printOptions(List.of("NEW GAME", "HIGH SCORES", "EXIT"));
            int choice = ui.readChoice(1, 3);
            if (choice == 1) {
                GameState state = startNewGame();
                playGame(state);
                recordResult(state);
            } else if (choice == 2) {
                showHighScores();
            } else {
                running = false;
            }
        }
        ui.print("Thanks for playing. Goodbye!");
    }

    private void showHighScores() {
        ui.printBlankLine();
        ui.print(AsciiArt.highScoreTable(loadLeaderboard()));
        ui.waitForEnter();
    }

    /**
     * Adds the finished run to the leaderboard, saves it and shows the ranking.
     */
    private void recordResult(GameState state) {
        Leaderboard leaderboard = loadLeaderboard();
        int rank = leaderboard.addEntry(new HighScoreEntry(state.getPlayerName(), state.getScore()));
        try {
            highScores.save(leaderboard);
        } catch (HighScoreFileException e) {
            ui.print("WARNING: " + e.getMessage() + " - this result was not saved.");
        }

        ui.printBlankLine();
        ui.print(AsciiArt.header("FINAL SCORE: " + AsciiArt.formatScore(state.getScore())));
        if (rank > 0) {
            ui.print("Congratulations " + state.getPlayerName() + ", you placed #" + rank + " on the leaderboard!");
        } else {
            ui.print("Not enough for the top " + Leaderboard.MAX_ENTRIES + " this time. Try again!");
        }
        ui.printBlankLine();
        ui.print(AsciiArt.highScoreTable(leaderboard));
        ui.waitForEnter();
    }

    /**
     * Loads the leaderboard. Problems with the file never stop the game:
     * an unreadable file gives an empty leaderboard and a warning.
     */
    private Leaderboard loadLeaderboard() {
        try {
            Leaderboard leaderboard = highScores.load();
            if (highScores.getSkippedLines() > 0) {
                ui.print("WARNING: ignored " + highScores.getSkippedLines()
                        + " invalid line(s) in " + highScores.getFile() + ".");
            }
            return leaderboard;
        } catch (HighScoreFileException e) {
            ui.print("WARNING: " + e.getMessage() + " - starting with an empty leaderboard.");
            return new Leaderboard();
        }
    }

    private GameState startNewGame() {
        ui.printBlankLine();
        ui.print(AsciiArt.header("ENTER YOUR NAME"));
        String playerName = ui.readName(MAX_NAME_LENGTH);

        ui.printBlankLine();
        ui.print(AsciiArt.header("CHOOSE DIFFICULTY"));
        ui.printBlankLine();
        ui.printOptions(List.of(
                "EASY   - 100 HP, 100 Food, 5 Bread",
                "NORMAL - 100 HP, 100 Food, no Bread",
                "CHAOS  - random HP, Food and Bread"));
        Difficulty difficulty = Difficulty.values()[ui.readChoice(1, 3) - 1];
        GameState state = GameState.newGame(playerName, difficulty, random);
        ui.printBlankLine();
        ui.print("Good luck, " + playerName + ". You wake up on the lowest floor of the dungeon. Find your way out!");
        return state;
    }

    /** Plays floor after floor until the player escapes or dies. */
    private void playGame(GameState state) {
        while (!state.hasEscaped()) {
            int floor = state.getCurrentFloor();
            if (!playFloor(state)) {
                ui.printBlankLine();
                ui.print(AsciiArt.gameOver());
                ui.print("You died on floor " + floor + ". The dungeon claims another soul.");
                ui.waitForEnter();
                return;
            }
            int points = state.completeCurrentFloor();
            ui.printBlankLine();
            ui.print(AsciiArt.floorCleared(floor));
            ui.print("+" + points + " POINTS   SCORE: " + AsciiArt.formatScore(state.getScore()));
            ui.waitForEnter();
        }
        ui.printBlankLine();
        ui.print(AsciiArt.dungeonMap(state));
        ui.print(AsciiArt.victory());
        ui.print("Sunlight! You escaped the dungeon with " + state.getPlayer().getHp() + " HP left.");
        ui.waitForEnter();
    }

    /**
     * Plays the random events of the current floor (the count depends on the difficulty).
     *
     * @return true if the player survived the floor
     */
    private boolean playFloor(GameState state) {
        int floor = state.getCurrentFloor();
        Player player = state.getPlayer();
        int eventCount = state.getDifficulty().rollEventsPerFloor(random);

        ui.printBlankLine();
        ui.print(AsciiArt.floorIntro(floor));
        for (int eventNumber = 1; eventNumber <= eventCount; eventNumber++) {
            showBetweenEventsMenu(state, eventNumber, eventCount);
            runEvent(eventSelector.nextEvent(floor), state);
            if (player.isAlive()) {
                applyHunger(player);
            }
            if (!player.isAlive()) {
                return false;
            }
        }
        return true;
    }

    /** Status line plus [1] Continue [2] Inventory [3] Map, until the player continues. */
    private void showBetweenEventsMenu(GameState state, int eventNumber, int eventCount) {
        while (true) {
            ui.printBlankLine();
            ui.print(AsciiArt.statusBar(state));
            ui.print(AsciiArt.weaponLine(state.getPlayer()));
            ui.print("Floor " + state.getCurrentFloor() + " - encounter " + eventNumber + " of " + eventCount);
            ui.printOptions(List.of("Continue", "Inventory", "Map"));
            int choice = ui.readChoice(1, 3);
            if (choice == 1) {
                return;
            } else if (choice == 2) {
                openInventory(state.getPlayer());
            } else {
                ui.printBlankLine();
                ui.print(AsciiArt.dungeonMap(state));
            }
        }
    }

    /** Shows an event, lets the player choose, awards points and runs any follow-up event. */
    private void runEvent(GameEvent event, GameState state) {
        Player player = state.getPlayer();
        ui.printBlankLine();
        ui.print(event.getArt());
        ui.print(event.getTitle());
        ui.printBlankLine();
        ui.printOptions(event.getOptions(player));
        int choice = ui.readChoice(1, event.getOptions(player).size());

        EventOutcome outcome = event.resolve(choice, player);
        ui.printBlankLine();
        ui.print(">> " + outcome.getMessage());
        if (outcome.isEnemyDefeated()) {
            ui.print(">> +" + state.recordEnemyDefeated() + " POINTS");
        }
        if (outcome.hasFollowUp() && player.isAlive()) {
            runEvent(outcome.getFollowUpEvent(), state);
        }
    }

    private void applyHunger(Player player) {
        int foodBefore = player.getFood();
        int hpBefore = player.getHp();
        boolean starving = player.applyHunger();
        ui.print(">> Time passes. Food -" + (foodBefore - player.getFood()) + ".");
        if (starving) {
            ui.print(">> You are starving! HP -" + (hpBefore - player.getHp()) + ". Eat some Bread!");
        }
    }

    /**
     * Lists the items alphabetically. Choosing an item uses it
     * (food and potions are consumed, swords are equipped).
     * An equipped sword can also be put back into the bag.
     */
    private void openInventory(Player player) {
        Inventory inventory = player.getInventory();
        ui.printBlankLine();
        ui.print(AsciiArt.header("INVENTORY"));
        ui.print(AsciiArt.weaponLine(player));
        if (inventory.isEmpty() && !player.hasWeaponEquipped()) {
            ui.print("Your bag is empty.");
            return;
        }

        List<String> names = inventory.getSortedItemNames();
        List<String> options = new ArrayList<>();
        for (String name : names) {
            options.add(name + " x" + inventory.countOf(name) + "  (" + descriptionOf(inventory, name) + ")");
        }
        int unequipChoice = -1;
        if (player.hasWeaponEquipped()) {
            options.add("Unequip " + player.getEquippedWeapon().getName());
            unequipChoice = options.size();
        }
        options.add("Back");
        ui.printBlankLine();
        ui.printOptions(options);

        int choice = ui.readChoice(1, options.size());
        if (choice <= names.size()) {
            try {
                ui.print(">> " + player.useItem(names.get(choice - 1)));
            } catch (ItemNotFoundException e) {
                ui.print(">> " + e.getMessage());
            }
        } else if (choice == unequipChoice) {
            ui.print(">> " + player.unequipWeapon());
        }
    }

    private static String descriptionOf(Inventory inventory, String itemName) {
        for (Item item : inventory.getSortedItems()) {
            if (item.getName().equals(itemName)) {
                return item.getDescription();
            }
        }
        return "";
    }
}
