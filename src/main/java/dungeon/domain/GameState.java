package dungeon.domain;

import java.util.Random;

/**
 * The state of one run: the player's name and character, the chosen difficulty,
 * how many of the five floors have been cleared and the score so far.
 * <p>
 * Scoring rules:
 * <ul>
 *     <li>Defeating an enemy: {@link #POINTS_PER_ENEMY_FLOOR} x the floor number.</li>
 *     <li>Clearing a floor: {@link #POINTS_PER_FLOOR}.</li>
 * </ul>
 */
public class GameState {

    public static final int FLOOR_COUNT = 5;
    public static final int POINTS_PER_ENEMY_FLOOR = 100;
    public static final int POINTS_PER_FLOOR = 1000;

    private final String playerName;
    /** Composition: the player character belongs to exactly one run. */
    private final Player player;
    private final Difficulty difficulty;
    private int clearedFloors;
    private int score;

    public GameState(String playerName, Player player, Difficulty difficulty) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be empty");
        }
        if (player == null || difficulty == null) {
            throw new IllegalArgumentException("Player and difficulty must not be null");
        }
        this.playerName = playerName;
        this.player = player;
        this.difficulty = difficulty;
    }

    /** Starts a new run with the starting conditions of the given difficulty. */
    public static GameState newGame(String playerName, Difficulty difficulty, Random random) {
        return new GameState(playerName, difficulty.createPlayer(random), difficulty);
    }

    public String getPlayerName() {
        return playerName;
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

    public int getScore() {
        return score;
    }

    /** The floor the player is currently on (1-5). */
    public int getCurrentFloor() {
        return Math.min(clearedFloors + 1, FLOOR_COUNT);
    }

    /** True when all floors are cleared and the player reached the exit. */
    public boolean hasEscaped() {
        return clearedFloors == FLOOR_COUNT;
    }

    /**
     * Awards points for an enemy defeated on the current floor.
     *
     * @return the points awarded
     */
    public int recordEnemyDefeated() {
        int points = POINTS_PER_ENEMY_FLOOR * getCurrentFloor();
        score += points;
        return points;
    }

    /**
     * Marks the current floor as cleared and awards the floor points.
     *
     * @return the points awarded
     */
    public int completeCurrentFloor() {
        if (hasEscaped()) {
            throw new IllegalStateException("All floors are already cleared");
        }
        clearedFloors++;
        score += POINTS_PER_FLOOR;
        return POINTS_PER_FLOOR;
    }
}
