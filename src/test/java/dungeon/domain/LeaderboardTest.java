package dungeon.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeaderboardTest {

    private static List<String> names(Leaderboard leaderboard) {
        return leaderboard.getEntries().stream().map(HighScoreEntry::getPlayerName).toList();
    }

    @Test
    void entriesAreSortedHighestScoreFirst() {
        Leaderboard leaderboard = new Leaderboard(List.of(
                new HighScoreEntry("ANNA", 3000),
                new HighScoreEntry("BEN", 9000),
                new HighScoreEntry("CLARA", 6000)));

        assertEquals(List.of("BEN", "CLARA", "ANNA"), names(leaderboard));
    }

    @Test
    void equalScoresAreOrderedByName() {
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.addEntry(new HighScoreEntry("ZOE", 5000));
        leaderboard.addEntry(new HighScoreEntry("ADAM", 5000));

        assertEquals(List.of("ADAM", "ZOE"), names(leaderboard));
    }

    @Test
    void addingAResultReturnsItsRank() {
        Leaderboard leaderboard = new Leaderboard(List.of(
                new HighScoreEntry("A", 9000), new HighScoreEntry("B", 5000)));

        int rank = leaderboard.addEntry(new HighScoreEntry("NEW", 7000));

        assertEquals(2, rank);
        assertEquals(3, leaderboard.size());
    }

    @Test
    void onlyTheBestTenResultsAreKept() {
        Leaderboard leaderboard = new Leaderboard();
        for (int i = 1; i <= Leaderboard.MAX_ENTRIES; i++) {
            leaderboard.addEntry(new HighScoreEntry("P" + i, i * 1000));
        }

        int lowRank = leaderboard.addEntry(new HighScoreEntry("LOW", 10));
        int topRank = leaderboard.addEntry(new HighScoreEntry("TOP", 99999));

        assertEquals(-1, lowRank, "too low for the top 10");
        assertEquals(1, topRank);
        assertEquals(Leaderboard.MAX_ENTRIES, leaderboard.size());
        assertEquals("P2", names(leaderboard).get(Leaderboard.MAX_ENTRIES - 1), "P1 was pushed out");
    }

    @Test
    void invalidEntriesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HighScoreEntry("", 100));
        assertThrows(IllegalArgumentException.class, () -> new HighScoreEntry("A;B", 100));
        assertThrows(IllegalArgumentException.class, () -> new HighScoreEntry("ANNA", -1));
    }
}
