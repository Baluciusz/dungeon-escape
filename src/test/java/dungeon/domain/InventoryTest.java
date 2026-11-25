package dungeon.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryTest {

    @Test
    void itemsAreListedAlphabetically() {
        Inventory inventory = new Inventory();
        inventory.addItem(Weapon.steelSword());
        inventory.addItem(new HealthPotion());
        inventory.addItem(new Bread());

        List<String> names = inventory.getSortedItems().stream().map(Item::getName).toList();

        assertEquals(List.of(Bread.NAME, HealthPotion.NAME, Weapon.STEEL_SWORD), names);
    }

    @Test
    void distinctNamesAndCountsAreReported() {
        Inventory inventory = new Inventory();
        inventory.addItem(new Bread());
        inventory.addItem(new Bread());
        inventory.addItem(new HealthPotion());

        assertEquals(List.of(Bread.NAME, HealthPotion.NAME), inventory.getSortedItemNames());
        assertEquals(2, inventory.countOf(Bread.NAME));
        assertEquals(3, inventory.size());
    }

    @Test
    void removingMissingItemThrowsCustomException() {
        Inventory inventory = new Inventory();
        inventory.addItem(new Bread());

        assertThrows(ItemNotFoundException.class, () -> inventory.removeItem(HealthPotion.NAME));
        assertEquals(1, inventory.size(), "a failed removal must not change the inventory");
    }

    @Test
    void sortedListCannotBeModifiedFromOutside() {
        Inventory inventory = new Inventory();
        inventory.addItem(new Bread());

        assertThrows(UnsupportedOperationException.class,
                () -> inventory.getSortedItems().add(new HealthPotion()));
    }
}
