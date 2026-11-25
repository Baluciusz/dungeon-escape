package dungeon.domain;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DifficultyTest {

    @Test
    void easyStartsWithFiveBreadAndSixEventsPerFloor() {
        Random random = new Random(1);
        Player player = Difficulty.EASY.createPlayer(random);

        assertEquals(100, player.getHp());
        assertEquals(100, player.getFood());
        assertEquals(5, player.getInventory().countOf(Bread.NAME));
        assertEquals(6, Difficulty.EASY.rollEventsPerFloor(random));
    }

    @Test
    void normalStartsWithoutBreadAndTwelveEventsPerFloor() {
        Random random = new Random(1);
        Player player = Difficulty.NORMAL.createPlayer(random);

        assertTrue(player.getInventory().isEmpty());
        assertEquals(12, Difficulty.NORMAL.rollEventsPerFloor(random));
    }

    @Test
    void chaosValuesStayWithinTheirRanges() {
        Random random = new Random(42);
        for (int i = 0; i < 500; i++) {
            Player player = Difficulty.CHAOS.createPlayer(random);
            int events = Difficulty.CHAOS.rollEventsPerFloor(random);

            assertTrue(player.getHp() >= 40 && player.getHp() <= 100, "HP " + player.getHp());
            assertTrue(player.getFood() >= 30 && player.getFood() <= 100, "Food " + player.getFood());
            assertTrue(player.getInventory().countOf(Bread.NAME) <= 5);
            assertTrue(events >= 13 && events <= 16, "always more than 12 events: " + events);
        }
    }
}
