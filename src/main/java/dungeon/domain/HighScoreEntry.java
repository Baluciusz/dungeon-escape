package dungeon.domain;

/**
 * One finished run on the high score list: the player's name and final score.
 * Instances are immutable.
 */
public final class HighScoreEntry {

    /** Separator used in the high score file, so it is not allowed inside names. */
    public static final String SEPARATOR = ";";

    private final String playerName;
    private final int score;

    public HighScoreEntry(String playerName, int score) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be empty");
        }
        if (playerName.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Player name must not contain '" + SEPARATOR + "'");
        }
        if (score < 0) {
            throw new IllegalArgumentException("Score must not be negative: " + score);
        }
        this.playerName = playerName.trim();
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return playerName + " " + score;
    }
}
