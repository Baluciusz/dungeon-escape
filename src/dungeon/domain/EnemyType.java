package dungeon.domain;

import java.util.Random;

/**
 * The three kinds of enemies with their base combat values (valid on floor 1).
 * <p>
 * Large enemies are easy to escape from but hit hard; small enemies are
 * hard to escape from but deal less damage.
 */
public enum EnemyType {

    OGRE("Ogre", "large and slow", 65, 25, 80, """
                 _______
                /       \\
               |  O   O  |
               |    ^    |
               |  \\___/  |
                \\_______/
             __/|       |\\__
            /   |  ___  |   \\
                |_|   |_|
            """),

    ORC("Orc", "medium", 60, 18, 55, """
                ,       ,
               (\\_____/)
               ( o   o )
                \\  v  /
               /|=====|\\
                |     |
                /     \\
            """),

    GOBLIN("Goblin", "small and fast", 55, 12, 30, """
               /\\   /\\
               (o . o)
                > ^ <
               /|   |\\
                / \\
            """);

    private final String displayName;
    private final String description;
    private final int baseWinChance;
    private final int baseDamage;
    private final int escapeChance;
    private final String art;

    EnemyType(String displayName, String description, int baseWinChance,
              int baseDamage, int escapeChance, String art) {
        this.displayName = displayName;
        this.description = description;
        this.baseWinChance = baseWinChance;
        this.baseDamage = baseDamage;
        this.escapeChance = escapeChance;
        this.art = art;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /** Chance (in percent) that the player wins a fight on floor 1. */
    public int getBaseWinChance() {
        return baseWinChance;
    }

    /** HP lost by the player when losing a fight on floor 1. */
    public int getBaseDamage() {
        return baseDamage;
    }

    /** Chance (in percent) that an escape attempt succeeds. */
    public int getEscapeChance() {
        return escapeChance;
    }

    public String getArt() {
        return art;
    }

    /** Picks one enemy type with equal probability. */
    public static EnemyType random(Random random) {
        EnemyType[] types = values();
        return types[random.nextInt(types.length)];
    }
}
