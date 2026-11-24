package dungeon.factory;

import dungeon.event.GameEvent;
import dungeon.event.TreasureChestEvent;

import java.util.Random;

/** Concrete creator: produces {@link TreasureChestEvent}s. */
public class TreasureChestEventCreator extends GameEventCreator {

    @Override
    protected GameEvent createEvent(int floor, Random random) {
        return new TreasureChestEvent(random);
    }
}
