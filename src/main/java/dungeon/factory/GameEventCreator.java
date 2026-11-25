package dungeon.factory;

import dungeon.domain.GameState;
import dungeon.event.GameEvent;

import java.util.Random;

/**
 * The "Creator" of the Factory Method pattern.
 * <p>
 * {@link #prepareEvent(int, Random)} is the common algorithm shared by every creator
 * (like {@code order()} in the lecture's {@code PizzaStore} example). It calls the
 * factory method {@link #createEvent(int, Random)}, which each concrete creator
 * overrides to decide which concrete {@link GameEvent} is instantiated.
 * <p>
 * The caller therefore never writes {@code new TrapEvent(...)} itself and never
 * needs a switch on the event type.
 */
public abstract class GameEventCreator {

    /**
     * Validates the input, creates the event through the factory method
     * and checks the result.
     *
     * @param floor  the current floor (1-5); some events depend on it
     * @param random the source of randomness the event will use
     */
    public final GameEvent prepareEvent(int floor, Random random) {
        if (floor < 1 || floor > GameState.FLOOR_COUNT) {
            throw new IllegalArgumentException(
                    "Floor must be between 1 and " + GameState.FLOOR_COUNT + ": " + floor);
        }
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null");
        }
        GameEvent event = createEvent(floor, random);
        if (event == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " did not create an event");
        }
        return event;
    }

    /**
     * The factory method: subclasses decide which concrete event to create.
     */
    protected abstract GameEvent createEvent(int floor, Random random);
}
