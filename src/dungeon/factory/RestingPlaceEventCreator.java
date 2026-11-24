package dungeon.factory;

import dungeon.event.GameEvent;
import dungeon.event.RestingPlaceEvent;

import java.util.Random;

/** Concrete creator: produces {@link RestingPlaceEvent}s. */
public class RestingPlaceEventCreator extends GameEventCreator {

    @Override
    protected GameEvent createEvent(int floor, Random random) {
        return new RestingPlaceEvent(random);
    }
}
