package dungeon.factory;

import dungeon.domain.Enemy;
import dungeon.domain.EnemyType;
import dungeon.event.EnemyEncounterEvent;
import dungeon.event.GameEvent;

import java.util.Random;

/**
 * Concrete creator: produces {@link EnemyEncounterEvent}s with a random
 * enemy type whose strength depends on the current floor.
 */
public class EnemyEncounterEventCreator extends GameEventCreator {

    @Override
    protected GameEvent createEvent(int floor, Random random) {
        Enemy enemy = new Enemy(EnemyType.random(random), floor);
        return new EnemyEncounterEvent(enemy, random);
    }
}
