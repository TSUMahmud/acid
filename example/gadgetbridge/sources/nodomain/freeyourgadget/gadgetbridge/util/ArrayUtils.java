package nodomain.freeyourgadget.gadgetbridge.util;

import java.util.Collection;

public class ArrayUtils {
    public static boolean equals(byte[] first, byte[] second, int startIndex) {
        if (first == null) {
            throw new IllegalArgumentException("first must not be null");
        } else if (second == null) {
            throw new IllegalArgumentException("second must not be null");
        } else if (startIndex < 0) {
            throw new IllegalArgumentException("startIndex must be >= 0");
        } else if (second.length + startIndex > first.length) {
            return false;
        } else {
            for (int i = 0; i < second.length; i++) {
                if (first[startIndex + i] != second[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    public static int[] toIntArray(Collection<Integer> values) {
        if (values == null) {
            return null;
        }
        int i = 0;
        int[] result = new int[values.size()];
        for (Integer value : values) {
            result[i] = value.intValue();
            i++;
        }
        return result;
    }

    public static boolean startsWith(byte[] array, byte[] values) {
        return equals(array, values, 0);
    }
}
