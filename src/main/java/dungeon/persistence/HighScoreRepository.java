package dungeon.persistence;

import dungeon.domain.HighScoreEntry;
import dungeon.domain.Leaderboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves the leaderboard in a simple text file, one result per line:
 * <pre>
 * BALINT;8400
 * PLAYER2;6100
 * </pre>
 * A missing file means "no results yet". Lines that cannot be parsed are skipped,
 * so a damaged file never crashes the game.
 */
public class HighScoreRepository {

    private final Path file;
    private int skippedLines;

    public HighScoreRepository(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("File path must not be null");
        }
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    /** Number of invalid lines ignored during the last {@link #load()}. */
    public int getSkippedLines() {
        return skippedLines;
    }

    /**
     * Reads the leaderboard from the file.
     *
     * @return the stored results, or an empty leaderboard if the file does not exist yet
     * @throws HighScoreFileException if the file exists but cannot be read
     */
    public Leaderboard load() throws HighScoreFileException {
        skippedLines = 0;
        if (!Files.exists(file)) {
            return new Leaderboard();
        }
        List<HighScoreEntry> entries = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                HighScoreEntry entry = parseLine(line);
                if (entry == null) {
                    skippedLines++;
                } else {
                    entries.add(entry);
                }
            }
        } catch (IOException e) {
            throw new HighScoreFileException("Could not read the high score file: " + file, e);
        }
        return new Leaderboard(entries);
    }

    /**
     * Writes the leaderboard to the file, creating or replacing it.
     *
     * @throws HighScoreFileException if the file cannot be written
     */
    public void save(Leaderboard leaderboard) throws HighScoreFileException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (HighScoreEntry entry : leaderboard.getEntries()) {
                writer.write(entry.getPlayerName() + HighScoreEntry.SEPARATOR + entry.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new HighScoreFileException("Could not write the high score file: " + file, e);
        }
    }

    /**
     * Parses one "NAME;SCORE" line.
     *
     * @return the entry, or null if the line is invalid
     */
    static HighScoreEntry parseLine(String line) {
        String[] parts = line.split(HighScoreEntry.SEPARATOR, -1);
        if (parts.length != 2) {
            return null;
        }
        try {
            return new HighScoreEntry(parts[0], Integer.parseInt(parts[1].trim()));
        } catch (IllegalArgumentException e) {
            // Covers both NumberFormatException and invalid names/negative scores.
            return null;
        }
    }
}
