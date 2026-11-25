package dungeon;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * Test helper: a Random that returns pre-defined numbers in order,
 * so random game outcomes become predictable in unit tests.
 * <p>
 * Example: {@code new FixedRandom(0, 99)} makes the first 1-100 roll succeed
 * for any chance above 0% and the second roll fail for any chance below 100%.
 */
public class FixedRandom extends Random {

    private static final long serialVersionUID = 1L;

    private final Deque<Integer> values = new ArrayDeque<>();

    public FixedRandom(int... values) {
        for (int value : values) {
            this.values.add(value);
        }
    }

    @Override
    public int nextInt(int bound) {
        return next(0, bound);
    }

    @Override
    public int nextInt(int origin, int bound) {
        return next(origin, bound);
    }

    private int next(int origin, int bound) {
        if (values.isEmpty()) {
            throw new IllegalStateException("FixedRandom ran out of values");
        }
        int value = values.poll();
        if (value < origin || value >= bound) {
            throw new IllegalStateException(value + " is outside [" + origin + ", " + bound + ")");
        }
        return value;
    }
}
