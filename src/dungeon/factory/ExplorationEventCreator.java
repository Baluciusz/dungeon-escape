package dungeon.factory;

import dungeon.event.ExplorationEvent;
import dungeon.event.GameEvent;

import java.util.Random;

/** Concrete creator: produces {@link ExplorationEvent}s. */
public class ExplorationEventCreator extends GameEventCreator {

    @Override
    protected GameEvent createEvent(int floor, Random random) {
        return new ExplorationEvent(random);
    }
}
