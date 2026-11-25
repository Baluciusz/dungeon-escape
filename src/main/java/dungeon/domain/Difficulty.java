package dungeon.domain;

import java.util.Random;

/**
 * The three difficulty levels. Each level decides the player's starting
 * HP, Food and Bread, and how many events every floor has.
 * <p>
 * Every constant overrides {@link #createPlayer(Random)} and
 * {@link #rollEventsPerFloor(Random)}, so the caller does not need
 * an if/switch on the difficulty.
 */
public enum Difficulty {

    /** 100 HP, 100 Food, 5 Bread; 6 events per floor. */
    EASY {
        @Override
        public Player createPlayer(Random random) {
            return newPlayer(100, 100, 5);
        }

        @Override
        public int rollEventsPerFloor(Random random) {
            return 6;
        }
    },

    /** 100 HP, 100 Food, no Bread; 12 events per floor. */
    NORMAL {
        @Override
        public Player createPlayer(Random random) {
            return newPlayer(100, 100, 0);
        }

        @Override
        public int rollEventsPerFloor(Random random) {
            return 12;
        }
    },

    /** Random start: 40-100 HP, 30-100 Food, 0-5 Bread; 13-16 events per floor. */
    CHAOS {
        @Override
        public Player createPlayer(Random random) {
            int hp = random.nextInt(40, 101);
            int food = random.nextInt(30, 101);
            int bread = random.nextInt(0, 6);
            return newPlayer(hp, food, bread);
        }

        @Override
        public int rollEventsPerFloor(Random random) {
            return random.nextInt(13, 17);
        }
    };

    /**
     * Creates a new player with the starting conditions of this difficulty.
     *
     * @param random source of randomness (only used by CHAOS)
     */
    public abstract Player createPlayer(Random random);

    /**
     * Returns how many events the next floor has.
     *
     * @param random source of randomness (only used by CHAOS)
     */
    public abstract int rollEventsPerFloor(Random random);

    private static Player newPlayer(int hp, int food, int breadCount) {
        Player player = new Player(hp, food);
        for (int i = 0; i < breadCount; i++) {
            player.addItem(new Bread());
        }
        return player;
    }
}
