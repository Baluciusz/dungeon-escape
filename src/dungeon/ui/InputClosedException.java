package dungeon.ui;

/**
 * Thrown when the console input stream ends (for example the user presses Ctrl+D / Ctrl+Z),
 * so no more choices can be read. The application catches it and exits cleanly.
 */
public class InputClosedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InputClosedException() {
        super("The console input was closed.");
    }
}
