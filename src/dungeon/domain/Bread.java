package dungeon.domain;

/**
 * A consumable item that restores Food.
 */
public class Bread extends Item {

    public static final String NAME = "Bread";
    public static final int FOOD_RESTORED = 25;

    public Bread() {
        super(NAME);
    }

    @Override
    public String getDescription() {
        return "Restores " + FOOD_RESTORED + " Food";
    }

    @Override
    public String applyTo(Player player) {
        int restored = player.eat(FOOD_RESTORED);
        return "You eat the bread. Food +" + restored + ".";
    }
}
