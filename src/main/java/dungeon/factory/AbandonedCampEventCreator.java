package dungeon.factory;

import dungeon.event.AbandonedCampEvent;
import dungeon.event.GameEvent;

import java.util.Random;

/** Concrete creator: produces {@link AbandonedCampEvent}s. */
public class AbandonedCampEventCreator extends GameEventCreator {

    @Override
    protected GameEvent createEvent(int floor, Random random) {
        return new AbandonedCampEvent(floor, random);
    }
}
