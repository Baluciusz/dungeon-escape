package dungeon.event;

import dungeon.domain.Player;

import java.util.List;
import java.util.Random;

/**
 * A trap blocks the corridor. Disarming is safer but hurts more if it fails.
 * When an attempt fails, the trap may still turn out to be defective.
 */
public class TrapEvent extends GameEvent {

    public static final int DISARM_SUCCESS_CHANCE = 60;
    public static final int DISARM_FAIL_DAMAGE = 15;
    public static final int RUN_SUCCESS_CHANCE = 40;
    public static final int RUN_FAIL_DAMAGE = 10;
    public static final int DEFECTIVE_CHANCE = 20;

    public TrapEvent(Random random) {
        super(random);
    }

    @Override
    public String getTitle() {
        return "A TRAP BLOCKS THE CORRIDOR!";
    }

    @Override
    public String getArt() {
        return """
                   ___________________
                  |  ^  ^  ^  ^  ^  ^ |
                  | /|\\/|\\/|\\/|\\/|\\/|\\|
                  |___________________|
                """;
    }

    @Override
    public List<String> getOptions() {
        return List.of(
                "Try to disarm it (" + DISARM_SUCCESS_CHANCE + "% success, -" + DISARM_FAIL_DAMAGE + " HP on failure)",
                "Risk running through (" + RUN_SUCCESS_CHANCE + "% success, -" + RUN_FAIL_DAMAGE + " HP on failure)");
    }

    @Override
    protected EventOutcome handleChoice(int choice, Player player) {
        if (choice == 1) {
            return attempt(player, DISARM_SUCCESS_CHANCE, DISARM_FAIL_DAMAGE,
                    "You carefully disarm the trap.");
        }
        return attempt(player, RUN_SUCCESS_CHANCE, RUN_FAIL_DAMAGE,
                "You dash through before the trap can trigger.");
    }

    private EventOutcome attempt(Player player, int successChance, int damage, String successMessage) {
        if (chance(successChance)) {
            return EventOutcome.of(successMessage);
        }
        if (chance(DEFECTIVE_CHANCE)) {
            return EventOutcome.of("The trap clicks... but nothing happens. It was defective!");
        }
        int lost = player.takeDamage(damage);
        return EventOutcome.of("The trap springs! HP -" + lost + ".");
    }
}
