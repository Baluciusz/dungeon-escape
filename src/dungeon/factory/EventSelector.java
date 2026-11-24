package dungeon.factory;

import dungeon.event.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Chooses which kind of event happens next.
 * <p>
 * It holds a list of creators (aggregation) and picks one at random, then
 * delegates the actual creation to that creator's Factory Method.
 * It only knows the abstract {@link GameEventCreator} and {@link GameEvent} types,
 * so a new event type can be added without changing this class.
 */
public class EventSelector {

    private final List<GameEventCreator> creators;
    private final Random random;

    public EventSelector(List<GameEventCreator> creators, Random random) {
        if (creators == null || creators.isEmpty()) {
            throw new IllegalArgumentException("At least one event creator is required");
        }
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null");
        }
        this.creators = new ArrayList<>(creators);
        this.random = random;
    }

    /** Creates a random event for the given floor. Repetitions are allowed. */
    public GameEvent nextEvent(int floor) {
        GameEventCreator creator = creators.get(random.nextInt(creators.size()));
        return creator.prepareEvent(floor, random);
    }
}
