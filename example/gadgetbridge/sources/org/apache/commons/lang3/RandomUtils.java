package org.apache.commons.lang3;

import com.github.mikephil.charting.utils.Utils;
import java.util.Random;

public class RandomUtils {
    private static final Random RANDOM = new Random();

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static byte[] nextBytes(int count) {
        Validate.isTrue(count >= 0, "Count cannot be negative.", new Object[0]);
        byte[] result = new byte[count];
        RANDOM.nextBytes(result);
        return result;
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        boolean z = true;
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        if (startInclusive < 0) {
            z = false;
        }
        Validate.isTrue(z, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return RANDOM.nextInt(endExclusive - startInclusive) + startInclusive;
    }

    public static int nextInt() {
        return nextInt(0, Integer.MAX_VALUE);
    }

    public static long nextLong(long startInclusive, long endExclusive) {
        boolean z = true;
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        if (startInclusive < 0) {
            z = false;
        }
        Validate.isTrue(z, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return (long) nextDouble((double) startInclusive, (double) endExclusive);
    }

    public static long nextLong() {
        return nextLong(0, Long.MAX_VALUE);
    }

    public static double nextDouble(double startInclusive, double endInclusive) {
        boolean z = true;
        Validate.isTrue(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        if (startInclusive < Utils.DOUBLE_EPSILON) {
            z = false;
        }
        Validate.isTrue(z, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endInclusive) {
            return startInclusive;
        }
        return ((endInclusive - startInclusive) * RANDOM.nextDouble()) + startInclusive;
    }

    public static double nextDouble() {
        return nextDouble(Utils.DOUBLE_EPSILON, Double.MAX_VALUE);
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        boolean z = true;
        Validate.isTrue(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        if (startInclusive < 0.0f) {
            z = false;
        }
        Validate.isTrue(z, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endInclusive) {
            return startInclusive;
        }
        return ((endInclusive - startInclusive) * RANDOM.nextFloat()) + startInclusive;
    }

    public static float nextFloat() {
        return nextFloat(0.0f, Float.MAX_VALUE);
    }
}
