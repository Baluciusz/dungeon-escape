package dungeon.event;

import dungeon.domain.Bread;
import dungeon.domain.HealthPotion;
import dungeon.domain.Item;
import dungeon.domain.Player;
import dungeon.domain.Weapon;

import java.util.List;
import java.util.Random;

/**
 * A treasure chest that always contains one item:
 * Bread (40%), a Health Potion (35%), a Rusty Sword (17%) or a Steel Sword (8%).
 * A sword the player already owns is replaced by Bread.
 */
public class TreasureChestEvent extends GameEvent {

    public static final int BREAD_CHANCE = 40;
    public static final int POTION_CHANCE = 35;
    public static final int RUSTY_SWORD_CHANCE = 17;
    public static final int STEEL_SWORD_CHANCE = 8;

    public TreasureChestEvent(Random random) {
        super(random);
    }

    @Override
    public String getTitle() {
        return "YOU FIND A TREASURE CHEST.";
    }

    @Override
    public String getArt() {
        return """
                     ______________
                    /\\  ________  /\\
                   /__\\/________\\/__\\
                   |   |   []   |   |
                   |___|________|___|
                """;
    }

    @Override
    public List<String> getOptions(Player player) {
        return List.of("Open the chest", "Leave it closed");
    }

    @Override
    protected EventOutcome handleChoice(int choice, Player player) {
        if (choice == 2) {
            return EventOutcome.of("You leave the chest untouched.");
        }
        Item loot = rollLoot(player);
        player.addItem(loot);
        if (loot instanceof Weapon) {
            return EventOutcome.of("Inside you find a " + loot.getName()
                    + "! Equip it from your inventory.");
        }
        return EventOutcome.of("Inside you find a " + loot.getName() + "!");
    }

    /** Picks the chest's content; a sword the player already owns becomes Bread. */
    private Item rollLoot(Player player) {
        int roll = randomBetween(0, 99);
        if (roll < BREAD_CHANCE) {
            return new Bread();
        }
        if (roll < BREAD_CHANCE + POTION_CHANCE) {
            return new HealthPotion();
        }
        Weapon sword = roll < BREAD_CHANCE + POTION_CHANCE + RUSTY_SWORD_CHANCE
                ? Weapon.rustySword()
                : Weapon.steelSword();
        return player.ownsWeapon(sword.getName()) ? new Bread() : sword;
    }
}
