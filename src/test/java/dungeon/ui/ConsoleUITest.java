package dungeon.ui;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests input validation with simulated keyboard input. */
class ConsoleUITest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private ConsoleUI uiWithInput(String typed) {
        return new ConsoleUI(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @Test
    void invalidChoicesAreRejectedUntilAValidNumberIsEntered() {
        ConsoleUI ui = uiWithInput("abc\n7\n\n2\n");

        int choice = ui.readChoice(1, 3);

        assertEquals(2, choice);
        String printed = output.toString(StandardCharsets.UTF_8);
        assertEquals(3, printed.split("Invalid choice", -1).length - 1, "three bad inputs, three warnings");
    }

    @Test
    void nameIsCleanedForTheHighScoreList() {
        ConsoleUI ui = uiWithInput("   \n  bal;int the brave knight \n");

        String name = ui.readName(12);

        assertEquals("BALINT THE B", name);
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Please enter a name"));
    }

    @Test
    void closedInputThrowsInsteadOfLoopingForever() {
        ConsoleUI ui = uiWithInput("");

        assertThrows(InputClosedException.class, () -> ui.readChoice(1, 3));
    }
}
