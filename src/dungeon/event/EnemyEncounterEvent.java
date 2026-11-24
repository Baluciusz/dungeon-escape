package dungeon.event;

import dungeon.domain.Enemy;
import dungeon.domain.HealthPotion;
import dungeon.domain.Player;

import java.util.List;
import java.util.Random;

/**
 * An enemy blocks the way. Combat is a single random roll, not turn-based.
 * <ul>
 *     <li>Fight: win (maybe find a potion) or lose and take damage.</li>
 *     <li>Escape: no damage on success, half damage on failure.</li>
 * </ul>
 * Either way the player continues afterwards.
 */
public class EnemyEncounterEvent extends GameEvent {

    public static final int POTION_DROP_CHANCE = 25;

    /** Composition: the enemy exists only as part of this encounter. */
    private final Enemy enemy;

    public EnemyEncounterEvent(Enemy enemy, Random random) {
        super(random);
        if (enemy == null) {
            throw new IllegalArgumentException("Enemy must not be null");
        }
        this.enemy = enemy;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    @Override
    public String getTitle() {
        return "A HOSTILE " + enemy.getName().toUpperCase() + " BLOCKS YOUR PATH!";
    }

    @Override
    public String getArt() {
        return enemy.getType().getArt()
                + "  " + enemy.getName() + " (" + enemy.getType().getDescription() + ")"
                + "  |  Damage: " + enemy.getDamage() + " HP\n";
    }

    @Override
    public List<String> getOptions() {
        return List.of(
                "Fight (" + enemy.getWinChance() + "% chance to win)",
                "Attempt to escape (" + enemy.getEscapeChance() + "% chance)");
    }

    @Override
    protected EventOutcome handleChoice(int choice, Player player) {
        return choice == 1 ? fight(player) : escape(player);
    }

    private EventOutcome fight(Player player) {
        if (chance(enemy.getWinChance())) {
            String message = "You defeated the " + enemy.getName() + "!";
            if (chance(POTION_DROP_CHANCE)) {
                player.addItem(new HealthPotion());
                message += " It dropped a Health Potion!";
            }
            return EventOutcome.of(message);
        }
        int lost = player.takeDamage(enemy.getDamage());
        return EventOutcome.of("The " + enemy.getName() + " overpowers you. HP -" + lost
                + ". You barely stagger past it.");
    }

    private EventOutcome escape(Player player) {
        if (chance(enemy.getEscapeChance())) {
            return EventOutcome.of("You slip away from the " + enemy.getName() + " unharmed.");
        }
        int lost = player.takeDamage(enemy.getEscapeFailDamage());
        return EventOutcome.of("The " + enemy.getName() + " catches you as you flee. HP -" + lost
                + ". You eventually get away.");
    }
}
