package dungeon.factory;

import dungeon.event.GameEvent;
import dungeon.event.TrapEvent;

import java.util.Random;

/** Concrete creator: produces {@link TrapEvent}s. */
public class TrapEventCreator extends GameEventCreator {

    @Override
    protected GameEvent createEvent(int floor, Random random) {
        return new TrapEvent(random);
    }
}
