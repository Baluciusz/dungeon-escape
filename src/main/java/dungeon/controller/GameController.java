package dungeon.controller;

import dungeon.domain.Difficulty;
import dungeon.domain.GameState;
import dungeon.domain.Inventory;
import dungeon.domain.Item;
import dungeon.domain.ItemNotFoundException;
import dungeon.domain.Player;
import dungeon.event.EventOutcome;
import dungeon.event.GameEvent;
import dungeon.factory.EventSelector;
import dungeon.ui.AsciiArt;
import dungeon.ui.ConsoleUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Runs the game: main menu, floors, events, inventory and the end screens.
 * <p>
 * The controller connects the parts but contains no event rules itself:
 * events come from the {@link EventSelector} (Factory Method) and are used
 * only through the abstract {@link GameEvent} type.
 */
public class GameController {

    public static final int MAX_NAME_LENGTH = 12;

    private final ConsoleUI ui;
    private final EventSelector eventSelector;
    private final Random random;

    public GameController(ConsoleUI ui, EventSelector eventSelector, Random random) {
        if (ui == null || eventSelector == null || random == null) {
            throw new IllegalArgumentException("Controller dependencies must not be null");
        }
        this.ui = ui;
        this.eventSelector = eventSelector;
        this.random = random;
    }

    /** Shows the main menu until the player chooses Exit. */
    public void run() {
        boolean running = true;
        while (running) {
            ui.printBlankLine();
            ui.print(AsciiArt.mainMenuBanner());
            ui.printBlankLine();
            ui.printOptions(List.of("New Game", "Exit"));
            int choice = ui.readChoice(1, 2);
            if (choice == 1) {
                playGame(startNewGame());
            } else {
                running = false;
            }
        }
        ui.print("Thanks for playing. Goodbye!");
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
