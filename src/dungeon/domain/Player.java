package dungeon.domain;

/**
 * The player character with its two survival attributes: HP and Food.
 * <p>
 * Both values are always kept between 0 and their maximum (100).
 * The fields are private and can only change through the methods below,
 * so a Player object can never get into an invalid state.
 */
public class Player {

    public static final int MAX_HP = 100;
    public static final int MAX_FOOD = 100;

    /** Food lost after every completed event. */
    public static final int FOOD_PER_EVENT = 10;

    /** HP lost after an event when the player has no food left. */
    public static final int STARVATION_DAMAGE = 5;

    private int hp;
    private int food;

    /**
     * Creates a player with the given starting values.
     *
     * @param hp   starting HP, must be between 1 and {@link #MAX_HP}
     * @param food starting food, must be between 0 and {@link #MAX_FOOD}
     */
    public Player(int hp, int food) {
        if (hp < 1 || hp > MAX_HP) {
            throw new IllegalArgumentException("HP must be between 1 and " + MAX_HP + ": " + hp);
        }
        if (food < 0 || food > MAX_FOOD) {
            throw new IllegalArgumentException("Food must be between 0 and " + MAX_FOOD + ": " + food);
        }
        this.hp = hp;
        this.food = food;
    }

    public int getHp() {
        return hp;
    }

    public int getFood() {
        return food;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    /**
     * Reduces HP. HP never drops below 0.
     *
     * @return the amount of HP actually lost
     */
    public int takeDamage(int amount) {
        requireNonNegative(amount);
        int lost = Math.min(amount, hp);
        hp -= lost;
        return lost;
    }

    /**
     * Restores HP. HP never rises above {@link #MAX_HP}.
     *
     * @return the amount of HP actually restored
     */
    public int heal(int amount) {
        requireNonNegative(amount);
        int restored = Math.min(amount, MAX_HP - hp);
        hp += restored;
        return restored;
    }

    /**
     * Restores Food. Food never rises above {@link #MAX_FOOD}.
     *
     * @return the amount of Food actually restored
     */
    public int eat(int amount) {
        requireNonNegative(amount);
        int restored = Math.min(amount, MAX_FOOD - food);
        food += restored;
        return restored;
    }

    /**
     * Reduces Food. Food never drops below 0.
     *
     * @return the amount of Food actually lost
     */
    public int loseFood(int amount) {
        requireNonNegative(amount);
        int lost = Math.min(amount, food);
        food -= lost;
        return lost;
    }

    /**
     * Applies the survival rule after a completed event:
     * the player loses {@link #FOOD_PER_EVENT} Food, and if Food is then 0,
     * the player also loses {@link #STARVATION_DAMAGE} HP.
     *
     * @return true if the player starved (took starvation damage)
     */
    public boolean applyHunger() {
        loseFood(FOOD_PER_EVENT);
        if (food == 0) {
            takeDamage(STARVATION_DAMAGE);
            return true;
        }
        return false;
    }

    private static void requireNonNegative(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative: " + amount);
        }
    }
}
