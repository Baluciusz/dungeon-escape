package dungeon.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * All console input and output goes through this class.
 * <p>
 * Input is always validated: invalid text or out-of-range numbers never crash
 * the game, the player is simply asked again.
 */
public class ConsoleUI {

    private static final String BYTE_ORDER_MARK = "﻿";

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleUI(InputStream in, PrintStream out) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Input and output streams must not be null");
        }
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public void print(String text) {
        out.println(text);
    }

    public void printBlankLine() {
        out.println();
    }

    /** Prints numbered options: [1] first, [2] second, ... */
    public void printOptions(List<String> options) {
        for (int i = 0; i < options.size(); i++) {
            out.println("[" + (i + 1) + "] " + options.get(i));
        }
    }

    /**
     * Reads a number between min and max (inclusive).
     * Keeps asking until the player enters a valid number.
     *
     * @throws InputClosedException if the input stream has ended
     */
    public int readChoice(int min, int max) {
        while (true) {
            out.print("Choose: ");
            String line = readLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // Not a number: fall through to the error message below.
            }
            out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
        }
    }

    /** Waits until the player presses ENTER. */
    public void waitForEnter() {
        out.print("Press ENTER to continue...");
        readLine();
    }

    private String readLine() {
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        // Some consoles send an invisible byte order mark (BOM) before the first line.
        return scanner.nextLine().replace(BYTE_ORDER_MARK, "");
    }
}
