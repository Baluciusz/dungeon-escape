package dungeon.persistence;

/**
 * Thrown when the high score file cannot be read or written
 * (for example because of missing permissions or a locked file).
 * <p>
 * It is a checked exception so the caller must decide how to continue;
 * the game keeps running and only the high score feature is affected.
 */
public class HighScoreFileException extends Exception {

    private static final long serialVersionUID = 1L;

    public HighScoreFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
