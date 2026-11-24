package dungeon.domain;

import java.util.Random;

/**
 * The three difficulty levels. Each level knows how to create
 * a player with its own starting HP, Food and Bread.
 * <p>
 * Every constant overrides {@link #createPlayer(Random)}, so the caller
 * does not need an if/switch on the difficulty.
 */
public enum Difficulty {

    /** 100 HP, 100 Food, 5 Bread. */
    EASY {
        @Override
        public Player createPlayer(Random random) {
            return newPlayer(100, 100, 5);
        }
    },

    /** 100 HP, 100 Food, no Bread. */
    NORMAL {
        @Override
        public Player createPlayer(Random random) {
            return newPlayer(100, 100, 0);
        }
    },

    /** Random start: 40-100 HP, 30-100 Food, 0-5 Bread. */
    CHAOS {
        @Override
        public Player createPlayer(Random random) {
            int hp = random.nextInt(40, 101);
            int food = random.nextInt(30, 101);
            int bread = random.nextInt(0, 6);
            return newPlayer(hp, food, bread);
        }
    };

    /**
     * Creates a new player with the starting conditions of this difficulty.
     *
     * @param random source of randomness (only used by CHAOS)
     */
    public abstract Player createPlayer(Random random);

    private static Player newPlayer(int hp, int food, int breadCount) {
        Player player = new Player(hp, food);
        for (int i = 0; i < breadCount; i++) {
            player.addItem(new Bread());
        }
        return player;
    }
}
