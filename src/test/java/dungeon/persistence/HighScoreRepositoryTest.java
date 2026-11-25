package dungeon.persistence;

import dungeon.domain.HighScoreEntry;
import dungeon.domain.Leaderboard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Uses a temporary folder from JUnit, so no real highscores.txt is touched. */
class HighScoreRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void missingFileGivesAnEmptyLeaderboard() throws HighScoreFileException {
        HighScoreRepository repository = new HighScoreRepository(tempDir.resolve("highscores.txt"));

        assertTrue(repository.load().isEmpty());
    }

    @Test
    void savedResultsCanBeLoadedAgain() throws HighScoreFileException {
        Path file = tempDir.resolve("highscores.txt");
        HighScoreRepository repository = new HighScoreRepository(file);
        Leaderboard leaderboard = new Leaderboard(List.of(
                new HighScoreEntry("BALINT", 8400), new HighScoreEntry("ANNA", 6100)));

        repository.save(leaderboard);
        Leaderboard loaded = repository.load();

        assertTrue(Files.exists(file), "the file is created automatically");
        assertEquals(2, loaded.size());
        assertEquals("BALINT", loaded.getEntries().get(0).getPlayerName());
        assertEquals(6100, loaded.getEntries().get(1).getScore());
    }

    @Test
    void invalidLinesAreSkippedInsteadOfCrashing() throws IOException, HighScoreFileException {
        Path file = tempDir.resolve("highscores.txt");
        Files.writeString(file, "ANNA;5000\ngarbage\nBEN;-3\nCLARA;abc\n\nDAVE;7200\n", StandardCharsets.UTF_8);
        HighScoreRepository repository = new HighScoreRepository(file);

        Leaderboard loaded = repository.load();

        assertEquals(2, loaded.size());
        assertEquals("DAVE", loaded.getEntries().get(0).getPlayerName());
        assertEquals(3, repository.getSkippedLines());
    }

    @Test
    void unreadableFileThrowsCustomException() throws IOException {
        Path folderInsteadOfFile = Files.createDirectory(tempDir.resolve("highscores.txt"));
        HighScoreRepository repository = new HighScoreRepository(folderInsteadOfFile);

        assertThrows(HighScoreFileException.class, repository::load);
        assertThrows(HighScoreFileException.class, () -> repository.save(new Leaderboard()));
    }

    @Test
    void lineParserAcceptsOnlyNameAndScore() {
        assertEquals(1200, HighScoreRepository.parseLine("ANNA;1200").getScore());
        assertNull(HighScoreRepository.parseLine("ANNA;12;00"));
        assertNull(HighScoreRepository.parseLine("ANNA"));
    }
}
