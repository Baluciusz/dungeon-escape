package dungeon.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Holds the player's consumable items.
 * <p>
 * The inventory aggregates {@link Item} objects in a generic {@code List<Item>}.
 * The list itself is never exposed, so items can only be changed through these methods.
 */
public class Inventory {

    /** Sorts items alphabetically by name; used when the inventory is displayed. */
    private static final Comparator<Item> BY_NAME = Comparator.comparing(Item::getName);

    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        items.add(item);
    }

    /**
     * Removes one item with the given name and returns it.
     *
     * @throws ItemNotFoundException if there is no such item in the inventory
     */
    public Item removeItem(String itemName) throws ItemNotFoundException {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getName().equalsIgnoreCase(itemName)) {
                iterator.remove();
                return item;
            }
        }
        throw new ItemNotFoundException(itemName);
    }

    /** Returns how many items with the given name the inventory contains. */
    public int countOf(String itemName) {
        int count = 0;
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                count++;
            }
        }
        return count;
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns a read-only copy of all items, sorted alphabetically by name.
     */
    public List<Item> getSortedItems() {
        List<Item> sorted = new ArrayList<>(items);
        sorted.sort(BY_NAME);
        return Collections.unmodifiableList(sorted);
    }

    /**
     * Returns the distinct item names, sorted alphabetically.
     * Useful for an inventory screen that shows "Bread x2" style lines.
     */
    public List<String> getSortedItemNames() {
        List<String> names = new ArrayList<>();
        for (Item item : getSortedItems()) {
            if (!names.contains(item.getName())) {
                names.add(item.getName());
            }
        }
        return Collections.unmodifiableList(names);
    }
}
