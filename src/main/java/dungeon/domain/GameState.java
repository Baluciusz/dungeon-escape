package dungeon.domain;

import java.util.Random;

/**
 * The state of one playthrough: the player, the chosen difficulty
 * and how many of the five floors have been cleared.
 */
public class GameState {

    public static final int FLOOR_COUNT = 5;

    /** Composition: the player belongs to exactly one game. */
    private final Player player;
    private final Difficulty difficulty;
    private int clearedFloors;

    /**
     * @param clearedFloors number of already cleared floors (0 for a new game)
     */
    public GameState(Player player, Difficulty difficulty, int clearedFloors) {
        if (player == null || difficulty == null) {
            throw new IllegalArgumentException("Player and difficulty must not be null");
        }
        if (clearedFloors < 0 || clearedFloors >= FLOOR_COUNT) {
            throw new IllegalArgumentException(
                    "Cleared floors must be between 0 and " + (FLOOR_COUNT - 1) + ": " + clearedFloors);
        }
        this.player = player;
        this.difficulty = difficulty;
        this.clearedFloors = clearedFloors;
    }

    /** Starts a new game with the starting conditions of the given difficulty. */
    public static GameState newGame(Difficulty difficulty, Random random) {
        return new GameState(difficulty.createPlayer(random), difficulty, 0);
    }

    public Player getPlayer() {
        return player;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getClearedFloors() {
        return clearedFloors;
    }

    /** The floor the player is currently on (1-5). */
    public int getCurrentFloor() {
        return Math.min(clearedFloors + 1, FLOOR_COUNT);
    }

    /** True when all floors are cleared and the player reached the exit. */
    public boolean hasEscaped() {
        return clearedFloors == FLOOR_COUNT;
    }

    /** Marks the current floor as cleared. */
    public void completeCurrentFloor() {
        if (hasEscaped()) {
            throw new IllegalStateException("All floors are already cleared");
        }
        clearedFloors++;
    }
}
