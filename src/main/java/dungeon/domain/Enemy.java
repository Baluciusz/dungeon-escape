package dungeon.domain;

/**
 * A single enemy met on a given floor.
 * <p>
 * The enemy gets stronger on deeper progress: each floor above the first
 * lowers the player's win chance and raises the damage the enemy deals.
 */
public class Enemy {

    public static final int WIN_CHANCE_DROP_PER_FLOOR = 5;
    public static final int DAMAGE_INCREASE_PER_FLOOR = 6;

    /** A fight can never be a sure win, even with the best weapon. */
    public static final int MAX_WIN_CHANCE = 95;

    private final EnemyType type;
    private final int floor;

    public Enemy(EnemyType type, int floor) {
        if (type == null) {
            throw new IllegalArgumentException("Enemy type must not be null");
        }
        if (floor < 1) {
            throw new IllegalArgumentException("Floor must be at least 1: " + floor);
        }
        this.type = type;
        this.floor = floor;
    }

    public EnemyType getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }

    public String getName() {
        return type.getDisplayName();
    }

    /** Chance (in percent) that the player wins a fight against this enemy. */
    public int getWinChance() {
        return type.getBaseWinChance() - WIN_CHANCE_DROP_PER_FLOOR * (floor - 1);
    }

    /** HP lost by the player when losing a fight against this enemy. */
    public int getDamage() {
        return type.getBaseDamage() + DAMAGE_INCREASE_PER_FLOOR * (floor - 1);
    }

    /**
     * Win chance including a weapon bonus, capped at {@link #MAX_WIN_CHANCE}.
     *
     * @param weaponBonus extra percentage points from the equipped weapon (0 if none)
     */
    public int getWinChanceWith(int weaponBonus) {
        return Math.min(MAX_WIN_CHANCE, getWinChance() + weaponBonus);
    }

    /** Chance (in percent) that the player escapes from this enemy. */
    public int getEscapeChance() {
        return type.getEscapeChance();
    }

    /** HP lost when an escape attempt fails: half of the normal damage. */
    public int getEscapeFailDamage() {
        return getDamage() / 2;
    }
}
