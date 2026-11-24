package dungeon.domain;

/**
 * A consumable item that restores HP.
 */
public class HealthPotion extends Item {

    public static final String NAME = "Health Potion";
    public static final int HP_RESTORED = 30;

    public HealthPotion() {
        super(NAME);
    }

    @Override
    public String getDescription() {
        return "Restores " + HP_RESTORED + " HP";
    }

    @Override
    public String applyTo(Player player) {
        int restored = player.heal(HP_RESTORED);
        return "You drink the health potion. HP +" + restored + ".";
    }
}
