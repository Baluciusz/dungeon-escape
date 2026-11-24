package dungeon.domain;

/**
 * Thrown when the player tries to use or remove an item that is not in the inventory.
 * <p>
 * This is a checked exception because it represents a domain rule
 * ("you can only use what you have") that the caller is expected to handle.
 */
public class ItemNotFoundException extends Exception {

    private final String itemName;

    public ItemNotFoundException(String itemName) {
        super("No '" + itemName + "' in the inventory.");
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }
}
