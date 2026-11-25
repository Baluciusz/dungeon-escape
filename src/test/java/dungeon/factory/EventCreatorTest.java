package dungeon.factory;

import dungeon.event.AbandonedCampEvent;
import dungeon.event.EnemyEncounterEvent;
import dungeon.event.ExplorationEvent;
import dungeon.event.GameEvent;
import dungeon.event.RestingPlaceEvent;
import dungeon.event.TrapEvent;
import dungeon.event.TreasureChestEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests the Factory Method: every concrete creator decides which concrete event is created. */
class EventCreatorTest {

    private final Random random = new Random(1);

    @Test
    void eachConcreteCreatorProducesItsOwnEventType() {
        assertInstanceOf(ExplorationEvent.class, new ExplorationEventCreator().prepareEvent(1, random));
        assertInstanceOf(EnemyEncounterEvent.class, new EnemyEncounterEventCreator().prepareEvent(1, random));
        assertInstanceOf(TrapEvent.class, new TrapEventCreator().prepareEvent(1, random));
        assertInstanceOf(AbandonedCampEvent.class, new AbandonedCampEventCreator().prepareEvent(1, random));
        assertInstanceOf(TreasureChestEvent.class, new TreasureChestEventCreator().prepareEvent(1, random));
        assertInstanceOf(RestingPlaceEvent.class, new RestingPlaceEventCreator().prepareEvent(1, random));
    }

    @Test
    void enemyCreatorUsesTheRequestedFloor() {
        GameEvent event = new EnemyEncounterEventCreator().prepareEvent(4, random);

        EnemyEncounterEvent encounter = assertInstanceOf(EnemyEncounterEvent.class, event);
        assertEquals(4, encounter.getEnemy().getFloor());
    }

    @Test
    void creatorRejectsFloorsOutsideTheDungeon() {
        GameEventCreator creator = new TrapEventCreator();

        assertThrows(IllegalArgumentException.class, () -> creator.prepareEvent(0, random));
        assertThrows(IllegalArgumentException.class, () -> creator.prepareEvent(6, random));
    }

    @Test
    void selectorDelegatesCreationToItsCreators() {
        EventSelector selector = new EventSelector(List.of(new RestingPlaceEventCreator()), random);

        for (int i = 0; i < 20; i++) {
            assertInstanceOf(RestingPlaceEvent.class, selector.nextEvent(2));
        }
    }

    @Test
    void selectorNeedsAtLeastOneCreator() {
        assertThrows(IllegalArgumentException.class, () -> new EventSelector(List.of(), random));
    }
}
