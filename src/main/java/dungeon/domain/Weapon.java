package dungeon.domain;

/**
 * A sword that can be equipped to improve the chance of winning fights.
 * <p>
 * Weapons are kept in the inventory like other items. "Using" a weapon
 * from the inventory equips it instead of consuming it (polymorphism:
 * {@link #applyTo(Player)} behaves differently from Bread or HealthPotion).
 */
public class Weapon extends Item {

    public static final String RUSTY_SWORD = "Rusty Sword";
    public static final int RUSTY_SWORD_BONUS = 10;

    public static final String STEEL_SWORD = "Steel Sword";
    public static final int STEEL_SWORD_BONUS = 20;

    private final int winChanceBonus;

    public Weapon(String name, int winChanceBonus) {
        super(name);
        if (winChanceBonus <= 0) {
            throw new IllegalArgumentException("Weapon bonus must be positive: " + winChanceBonus);
        }
        this.winChanceBonus = winChanceBonus;
    }

    /** Creates the common Rusty Sword (+10% win chance). */
    public static Weapon rustySword() {
        return new Weapon(RUSTY_SWORD, RUSTY_SWORD_BONUS);
    }

    /** Creates the rare Steel Sword (+20% win chance). */
    public static Weapon steelSword() {
        return new Weapon(STEEL_SWORD, STEEL_SWORD_BONUS);
    }

    /** Extra chance (in percentage points) to win a fight while equipped. */
    public int getWinChanceBonus() {
        return winChanceBonus;
    }

    @Override
    public String getDescription() {
        return "+" + winChanceBonus + "% chance to win fights when equipped";
    }

    @Override
    public String applyTo(Player player) {
        return player.equipWeapon(this);
    }
}
