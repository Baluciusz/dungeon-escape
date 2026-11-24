package dungeon.event;

import dungeon.domain.Bread;
import dungeon.domain.Enemy;
import dungeon.domain.EnemyType;
import dungeon.domain.Player;

import java.util.List;
import java.util.Random;

/**
 * An old camp that may contain Bread - or an enemy that is not so "abandoned" after all.
 */
public class AbandonedCampEvent extends GameEvent {

    public static final int OCCUPIED_CHANCE = 20;
    public static final int BREAD_CHANCE = 70;
    public static final int MIN_BREAD = 1;
    public static final int MAX_BREAD = 2;

    private final int floor;

    public AbandonedCampEvent(int floor, Random random) {
        super(random);
        if (floor < 1) {
            throw new IllegalArgumentException("Floor must be at least 1: " + floor);
        }
        this.floor = floor;
    }

    @Override
    public String getTitle() {
        return "YOU DISCOVER AN OLD CAMP.";
    }

    @Override
    public String getArt() {
        return """
                        /\\
                       /  \\          (
                      /    \\        ) )
                     /  ||  \\      (__)
                    /___||___\\    /____\\
                """;
    }

    @Override
    public List<String> getOptions() {
        return List.of("Search the camp", "Leave it alone");
    }

    @Override
    protected EventOutcome handleChoice(int choice, Player player) {
        if (choice == 2) {
            return EventOutcome.of("You leave the camp behind.");
        }
        if (chance(OCCUPIED_CHANCE)) {
            Enemy enemy = new Enemy(EnemyType.random(getRandom()), floor);
            return EventOutcome.withFollowUp(
                    "The camp is not abandoned! A " + enemy.getName() + " jumps out of the shadows!",
                    new EnemyEncounterEvent(enemy, getRandom()));
        }
        if (chance(BREAD_CHANCE)) {
            int amount = randomBetween(MIN_BREAD, MAX_BREAD);
            for (int i = 0; i < amount; i++) {
                player.addItem(new Bread());
            }
            return EventOutcome.of("You find " + amount + " Bread in an old bag!");
        }
        return EventOutcome.of("You search everywhere, but find nothing useful.");
    }
}
