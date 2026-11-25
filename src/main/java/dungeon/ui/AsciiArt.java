package dungeon.ui;

import dungeon.domain.GameState;
import dungeon.domain.HighScoreEntry;
import dungeon.domain.Leaderboard;
import dungeon.domain.Player;

/**
 * Builds the ASCII screens of the game (menu, map, status bar, end screens).
 * <p>
 * Every method only returns a String; printing is done by {@link ConsoleUI}.
 * Only plain ASCII characters are used to avoid console encoding problems.
 */
public final class AsciiArt {

    private static final String LINE = "================================";
    private static final int BAR_WIDTH = 10;

    private AsciiArt() {
        // Utility class: no instances.
    }

    /** A title framed by two lines of '=' characters. */
    public static String header(String title) {
        int padding = Math.max(0, (LINE.length() - title.length()) / 2);
        return LINE + "\n" + " ".repeat(padding) + title + "\n" + LINE;
    }

    public static String mainMenuBanner() {
        return """
                      _______________
                     |  ___________  |
                     | |  _     _  | |
                     | | | |   | | | |
                     | | |_|   |_| | |
                     | |    ___    | |
                     |_|___|___|___|_|
                """ + header("DUNGEON ESCAPE");
    }

    /**
     * The vertical dungeon overview: START at the bottom, EXIT at the top,
     * cleared floors and the player's current position are marked.
     */
    public static String dungeonMap(GameState state) {
        StringBuilder map = new StringBuilder(header("DUNGEON MAP")).append("\n\n");
        map.append("             [ EXIT ]");
        if (state.hasEscaped()) {
            map.append("  <-- YOU");
        }
        map.append("\n                |\n");
        for (int floor = GameState.FLOOR_COUNT; floor >= 1; floor--) {
            map.append("          +-----------+\n");
            map.append("          |  FLOOR ").append(floor).append("  |");
            if (floor <= state.getClearedFloors()) {
                map.append("  (cleared)");
            } else if (!state.hasEscaped() && floor == state.getCurrentFloor()) {
                map.append("  <-- YOU");
            }
            map.append("\n          +-----------+\n");
            map.append("                |\n");
        }
        map.append("              START\n");
        return map.toString();
    }

    /** One-line status, e.g. "HP [#####-----] 50/100 | FOOD [...] | FLOOR 2/5 | NORMAL". */
    public static String statusBar(GameState state) {
        Player player = state.getPlayer();
        return "HP " + bar(player.getHp(), Player.MAX_HP) + " " + player.getHp() + "/" + Player.MAX_HP
                + " | FOOD " + bar(player.getFood(), Player.MAX_FOOD) + " " + player.getFood() + "/" + Player.MAX_FOOD
                + " | FLOOR " + state.getCurrentFloor() + "/" + GameState.FLOOR_COUNT
                + " | " + state.getDifficulty()
                + " | SCORE " + formatScore(state.getScore());
    }

    /** The leaderboard as a retro ASCII table. */
    public static String highScoreTable(Leaderboard leaderboard) {
        String border = "+------+--------------+--------+";
        StringBuilder table = new StringBuilder(header("HIGH SCORES")).append("\n");
        table.append(border).append("\n");
        table.append("| RANK | NAME         |  SCORE |\n");
        table.append(border).append("\n");
        if (leaderboard.isEmpty()) {
            table.append("|        NO SCORES YET!        |\n");
        }
        int rank = 1;
        for (HighScoreEntry entry : leaderboard.getEntries()) {
            table.append(String.format("| %3d. | %-12s | %6s |\n", rank++, entry.getPlayerName(),
                    formatScore(entry.getScore())));
        }
        table.append(border);
        return table.toString();
    }

    /** Arcade-style score with leading zeros, e.g. 001200. */
    public static String formatScore(int score) {
        return String.format("%06d", score);
    }

    /** E.g. "WEAPON: Rusty Sword (+10%)" or "WEAPON: none (bare hands)". */
    public static String weaponLine(Player player) {
        if (!player.hasWeaponEquipped()) {
            return "WEAPON: none (bare hands)";
        }
        return "WEAPON: " + player.getEquippedWeapon().getName()
                + " (+" + player.getWeaponBonus() + "% to win fights)";
    }

    private static String bar(int value, int max) {
        int filled = (int) Math.round((double) value / max * BAR_WIDTH);
        return "[" + "#".repeat(filled) + "-".repeat(BAR_WIDTH - filled) + "]";
    }

    public static String floorIntro(int floor) {
        return """
                                      ______
                               ______|
                        ______|
                 ______|
                """ + header("ENTERING FLOOR " + floor);
    }

    public static String floorCleared(int floor) {
        return header("FLOOR " + floor + " CLEARED");
    }

    public static String gameOver() {
        return """
                        _______
                       /       \\
                      |  R.I.P  |
                      |         |
                      |  _____  |
                 _____|_________|_____
                """ + header("GAME OVER");
    }

    public static String victory() {
        return """
                        \\   |   /
                      --  [EXIT]  --
                        /   |   \\
                     _______|_______
                    |   __     __   |
                    |  |  |   |  |  |
                    |__|__|___|__|__|
                """ + header("VICTORY!");
    }
}
