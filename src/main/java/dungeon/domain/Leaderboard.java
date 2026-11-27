package dungeon.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The best {@value #MAX_ENTRIES} results, sorted by score (highest first).
 * <p>
 * Entries are kept in a generic {@code List<HighScoreEntry>} and sorted with a
 * {@link Comparator}. Equal scores are ordered alphabetically by name.
 */
public final class Leaderboard { 

    public static final int MAX_ENTRIES = 10;

    /** Highest score first; ties broken alphabetically by player name. */
    public static final Comparator<HighScoreEntry> HIGHEST_SCORE_FIRST =
            Comparator.comparingInt(HighScoreEntry::getScore).reversed()
                    .thenComparing(HighScoreEntry::getPlayerName);

    private final List<HighScoreEntry> entries = new ArrayList<>();

    /** Creates an empty leaderboard. */
    public Leaderboard() {
    }

    /** Creates a leaderboard from existing results (e.g. loaded from a file). */
    public Leaderboard(List<HighScoreEntry> results) {
        for (HighScoreEntry entry : results) {
            addEntry(entry);
        }
    }

    /**
     * Adds a result, re-sorts the list and keeps only the best {@value #MAX_ENTRIES}.
     *
     * @return the 1-based rank of the new entry, or -1 if it did not make the list
     */
    public int addEntry(HighScoreEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be null");
        }
        entries.add(entry);
        entries.sort(HIGHEST_SCORE_FIRST);
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
        int index = entries.indexOf(entry);
        return index >= 0 ? index + 1 : -1;
    }

    /** Returns a read-only view of the entries, best first. */
    public List<HighScoreEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
