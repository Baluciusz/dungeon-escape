package dungeon.event;

import dungeon.domain.Player;

import java.util.List;
import java.util.Random;

/**
 * The corridor splits: a slow but safe path, or a risky shortcut.
 */
public class ExplorationEvent extends GameEvent {

    public static final int CAREFUL_PATH_FOOD_COST = 5;
    public static final int SHORTCUT_FALL_CHANCE = 30;
    public static final int SHORTCUT_FALL_DAMAGE = 10;

    public ExplorationEvent(Random random) {
        super(random);
    }

    @Override
    public String getTitle() {
        return "THE CORRIDOR SPLITS IN TWO.";
    }

    @Override
    public String getArt() {
        return """
                  \\    ?    /
                   \\       /
                    \\     /
                     |   |
                     | @ |
                     |___|
                """;
    }

    @Override
    public List<String> getOptions() {
        return List.of(
                "Take the careful path (-" + CAREFUL_PATH_FOOD_COST + " extra Food)",
                "Take the shortcut (" + SHORTCUT_FALL_CHANCE + "% risk of falling)");
    }

    @Override
    protected EventOutcome handleChoice(int choice, Player player) {
        if (choice == 1) {
            int lost = player.loseFood(CAREFUL_PATH_FOOD_COST);
            return EventOutcome.of("You take the long, careful way around. Food -" + lost + ".");
        }
        if (chance(SHORTCUT_FALL_CHANCE)) {
            int lost = player.takeDamage(SHORTCUT_FALL_DAMAGE);
            return EventOutcome.of("You slip on loose rocks and fall! HP -" + lost + ".");
        }
        return EventOutcome.of("The shortcut was safe. You move on quickly.");
    }
}
