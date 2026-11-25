package dungeon.domain;

/**
 * Abstract base class of every consumable item.
 * <p>
 * Each concrete item decides in {@link #applyTo(Player)} what happens when it is used.
 * Code that uses items (for example {@link Player#useItem(String)}) only knows this
 * abstract type, so the correct behaviour is selected at runtime (polymorphism).
 */
public abstract class Item {

    private final String name;

    protected Item(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** A short text describing what the item does, shown in the inventory. */
    public abstract String getDescription();

    /**
     * Applies the item's effect to the player.
     *
     * @return a message describing what happened
     */
    public abstract String applyTo(Player player);

    @Override
    public String toString() {
        return name;
    }
}
