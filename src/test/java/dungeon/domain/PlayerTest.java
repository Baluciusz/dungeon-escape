package dungeon.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {

    @Test
    void constructorRejectsInvalidStartingValues() {
        assertThrows(IllegalArgumentException.class, () -> new Player(0, 50));
        assertThrows(IllegalArgumentException.class, () -> new Player(101, 50));
        assertThrows(IllegalArgumentException.class, () -> new Player(50, -1));
    }

    @Test
    void hpNeverDropsBelowZero() {
        Player player = new Player(30, 50);

        int lost = player.takeDamage(100);

        assertEquals(30, lost);
        assertEquals(0, player.getHp());
        assertFalse(player.isAlive());
    }

    @Test
    void healingNeverExceedsMaximum() {
        Player player = new Player(90, 50);

        int restored = player.heal(50);

        assertEquals(10, restored);
        assertEquals(Player.MAX_HP, player.getHp());
    }

    @Test
    void hungerReducesFoodAndStarvationCostsHp() {
        Player player = new Player(100, 7);

        assertFalse(player.applyHunger(), "7 food -> 2 food, not starving yet");
        assertEquals(2, player.getFood());

        assertTrue(player.applyHunger(), "2 food -> 0 food, starving");
        assertEquals(0, player.getFood());
        assertEquals(100 - Player.STARVATION_DAMAGE, player.getHp());
    }

    @Test
    void usingBreadConsumesItAndRestoresFood() throws ItemNotFoundException {
        Player player = new Player(100, 10);
        player.addItem(new Bread());

        player.useItem(Bread.NAME);

        assertEquals(10 + Bread.FOOD_RESTORED, player.getFood());
        assertTrue(player.getInventory().isEmpty());
    }

    @Test
    void usingMissingItemThrowsCustomException() {
        Player player = new Player(100, 100);

        ItemNotFoundException exception =
                assertThrows(ItemNotFoundException.class, () -> player.useItem(HealthPotion.NAME));

        assertEquals(HealthPotion.NAME, exception.getItemName());
    }

    @Test
    void equippingSwordPutsPreviousSwordBackIntoBag() throws ItemNotFoundException {
        Player player = new Player(100, 100);
        player.addItem(Weapon.rustySword());
        player.addItem(Weapon.steelSword());

        player.useItem(Weapon.RUSTY_SWORD);
        player.useItem(Weapon.STEEL_SWORD);

        assertEquals(Weapon.STEEL_SWORD_BONUS, player.getWeaponBonus());
        assertEquals(1, player.getInventory().countOf(Weapon.RUSTY_SWORD));
        assertEquals(0, player.getInventory().countOf(Weapon.STEEL_SWORD));
    }

    @Test
    void unequippingReturnsSwordAndRemovesBonus() throws ItemNotFoundException {
        Player player = new Player(100, 100);
        player.addItem(Weapon.rustySword());
        player.useItem(Weapon.RUSTY_SWORD);

        player.unequipWeapon();

        assertFalse(player.hasWeaponEquipped());
        assertEquals(0, player.getWeaponBonus());
        assertTrue(player.ownsWeapon(Weapon.RUSTY_SWORD));
    }
}
