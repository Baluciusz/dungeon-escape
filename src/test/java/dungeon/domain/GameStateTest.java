package dungeon.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the score calculation and floor progression of a run. */
class GameStateTest {

    private GameState newRun() {
        return new GameState("TESTER", new Player(100, 100), Difficulty.NORMAL);
    }

    @Test
    void newRunStartsOnFloorOneWithZeroPoints() {
        GameState state = newRun();

        assertEquals(1, state.getCurrentFloor());
        assertEquals(0, state.getScore());
        assertFalse(state.hasEscaped());
    }

    @Test
    void defeatedEnemyIsWorthMoreOnHigherFloors() {
        GameState state = newRun();

        assertEquals(100, state.recordEnemyDefeated()); // floor 1
        state.completeCurrentFloor();
        state.completeCurrentFloor();
        assertEquals(300, state.recordEnemyDefeated()); // floor 3

        assertEquals(100 + 2 * GameState.POINTS_PER_FLOOR + 300, state.getScore());
    }

    @Test
    void clearingAllFloorsMeansEscapeAndFullFloorPoints() {
        GameState state = newRun();
        for (int i = 0; i < GameState.FLOOR_COUNT; i++) {
            state.completeCurrentFloor();
        }

        assertTrue(state.hasEscaped());
        assertEquals(GameState.FLOOR_COUNT * GameState.POINTS_PER_FLOOR, state.getScore());
        assertThrows(IllegalStateException.class, state::completeCurrentFloor);
    }

    @Test
    void playerNameIsRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameState(" ", new Player(100, 100), Difficulty.EASY));
    }
}
