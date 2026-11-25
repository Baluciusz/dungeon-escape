package dungeon.event;

import dungeon.domain.Player;

import java.util.List;
import java.util.Random;

/**
 * Abstract base class of every dungeon event (the "Product" in the Factory Method pattern).
 * <p>
 * The game controller only works with this type: it shows the title, the ASCII art and
 * the options, then calls {@link #resolve(int, Player)}. Which rules actually run depends
 * on the concrete subclass chosen at runtime (polymorphism).
 * <p>
 * Events never print to the console themselves; they return an {@link EventOutcome}
 * with a message. This keeps game rules separate from the user interface.
 */
public abstract class GameEvent {

    private final Random random;

    protected GameEvent(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null");
        }
        this.random = random;
    }

    /** Short headline of the event, e.g. "A TRAP BLOCKS THE CORRIDOR!". */
    public abstract String getTitle();

    /** Small ASCII illustration of the event. */
    public abstract String getArt();

    /**
     * The choices the player can make, in display order (choice 1 is the first element).
     * The player is passed in so an option can show player-specific values,
     * e.g. a fight chance improved by the equipped weapon.
     */
    public abstract List<String> getOptions(Player player);

    /**
     * Resolves the player's choice. The choice is validated here once,
     * so subclasses can rely on receiving a valid number.
     *
     * @param choice 1-based index of the chosen option
     * @throws IllegalArgumentException if the choice is not one of the options
     */
    public final EventOutcome resolve(int choice, Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player must not be null");
        }
        int optionCount = getOptions(player).size();
        if (choice < 1 || choice > optionCount) {
            throw new IllegalArgumentException("Choice must be between 1 and " + optionCount + ": " + choice);
        }
        return handleChoice(choice, player);
    }

    /** Event-specific rules for an already validated choice. */
    protected abstract EventOutcome handleChoice(int choice, Player player);

    /** Returns true with the given probability (0-100 percent). */
    protected boolean chance(int percent) {
        return random.nextInt(100) < percent;
    }

    /** Returns a random number between min and max, both inclusive. */
    protected int randomBetween(int min, int max) {
        return random.nextInt(min, max + 1);
    }

    protected Random getRandom() {
        return random;
    }
}
