package org.apache.commons.lang3;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class ArrayUtils {
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final int INDEX_NOT_FOUND = -1;

    public static String toString(Object array) {
        return toString(array, "{}");
    }

    public static String toString(Object array, String stringIfNull) {
        if (array == null) {
            return stringIfNull;
        }
        return new ToStringBuilder(array, ToStringStyle.SIMPLE_STYLE).append(array).toString();
    }

    public static int hashCode(Object array) {
        return new HashCodeBuilder().append(array).toHashCode();
    }

    @Deprecated
    public static boolean isEquals(Object array1, Object array2) {
        return new EqualsBuilder().append(array1, array2).isEquals();
    }

    public static Map<Object, Object> toMap(Object[] array) {
        if (array == null) {
            return null;
        }
        double length = (double) array.length;
        Double.isNaN(length);
        Map<Object, Object> map = new HashMap<>((int) (length * 1.5d));
        for (int i = 0; i < array.length; i++) {
            Map.Entry<?, ?> entry = array[i];
            if (entry instanceof Map.Entry) {
                Map.Entry<?, ?> entry2 = entry;
                map.put(entry2.getKey(), entry2.getValue());
            } else if (entry instanceof Object[]) {
                Object[] entry3 = (Object[]) entry;
                if (entry3.length >= 2) {
                    map.put(entry3[0], entry3[1]);
                } else {
                    throw new IllegalArgumentException("Array element " + i + ", '" + entry + "', has a length less than 2");
                }
            } else {
                throw new IllegalArgumentException("Array element " + i + ", '" + entry + "', is neither of type Map.Entry nor an Array");
            }
        }
        return map;
    }

    public static <T> T[] toArray(T... items) {
        return items;
    }

    public static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        }
        return (Object[]) array.clone();
    }

    public static long[] clone(long[] array) {
        if (array == null) {
            return null;
        }
        return (long[]) array.clone();
    }

    public static int[] clone(int[] array) {
        if (array == null) {
            return null;
        }
        return (int[]) array.clone();
    }

    public static short[] clone(short[] array) {
        if (array == null) {
            return null;
        }
        return (short[]) array.clone();
    }

    public static char[] clone(char[] array) {
        if (array == null) {
            return null;
        }
        return (char[]) array.clone();
    }

    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return (byte[]) array.clone();
    }

    public static double[] clone(double[] array) {
        if (array == null) {
            return null;
        }
        return (double[]) array.clone();
    }

    public static float[] clone(float[] array) {
        if (array == null) {
            return null;
        }
        return (float[]) array.clone();
    }

    public static boolean[] clone(boolean[] array) {
        if (array == null) {
            return null;
        }
        return (boolean[]) array.clone();
    }

    public static <T> T[] nullToEmpty(T[] array, Class<T[]> type) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        } else if (array == null) {
            return (Object[]) type.cast(Array.newInstance(type.getComponentType(), 0));
        } else {
            return array;
        }
    }

    public static Object[] nullToEmpty(Object[] array) {
        if (isEmpty(array)) {
            return EMPTY_OBJECT_ARRAY;
        }
        return array;
    }

    public static Class<?>[] nullToEmpty(Class<?>[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_CLASS_ARRAY;
        }
        return array;
    }

    public static String[] nullToEmpty(String[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_STRING_ARRAY;
        }
        return array;
    }

    public static long[] nullToEmpty(long[] array) {
        if (isEmpty(array)) {
            return EMPTY_LONG_ARRAY;
        }
        return array;
    }

    public static int[] nullToEmpty(int[] array) {
        if (isEmpty(array)) {
            return EMPTY_INT_ARRAY;
        }
        return array;
    }

    public static short[] nullToEmpty(short[] array) {
        if (isEmpty(array)) {
            return EMPTY_SHORT_ARRAY;
        }
        return array;
    }

    public static char[] nullToEmpty(char[] array) {
        if (isEmpty(array)) {
            return EMPTY_CHAR_ARRAY;
        }
        return array;
    }

    public static byte[] nullToEmpty(byte[] array) {
        if (isEmpty(array)) {
            return EMPTY_BYTE_ARRAY;
        }
        return array;
    }

    public static double[] nullToEmpty(double[] array) {
        if (isEmpty(array)) {
            return EMPTY_DOUBLE_ARRAY;
        }
        return array;
    }

    public static float[] nullToEmpty(float[] array) {
        if (isEmpty(array)) {
            return EMPTY_FLOAT_ARRAY;
        }
        return array;
    }

    public static boolean[] nullToEmpty(boolean[] array) {
        if (isEmpty(array)) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        return array;
    }

    public static Long[] nullToEmpty(Long[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_LONG_OBJECT_ARRAY;
        }
        return array;
    }

    public static Integer[] nullToEmpty(Integer[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_INTEGER_OBJECT_ARRAY;
        }
        return array;
    }

    public static Short[] nullToEmpty(Short[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_SHORT_OBJECT_ARRAY;
        }
        return array;
    }

    public static Character[] nullToEmpty(Character[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        return array;
    }

    public static Byte[] nullToEmpty(Byte[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_BYTE_OBJECT_ARRAY;
        }
        return array;
    }

    public static Double[] nullToEmpty(Double[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        return array;
    }

    public static Float[] nullToEmpty(Float[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_FLOAT_OBJECT_ARRAY;
        }
        return array;
    }

    public static Boolean[] nullToEmpty(Boolean[] array) {
        if (isEmpty((Object[]) array)) {
            return EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        return array;
    }

    public static <T> T[] subarray(T[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            return (Object[]) Array.newInstance(type, 0);
        }
        T[] subarray = (Object[]) Array.newInstance(type, newSize);
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static long[] subarray(long[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_LONG_ARRAY;
        }
        long[] subarray = new long[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static int[] subarray(int[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] subarray = new int[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static short[] subarray(short[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_SHORT_ARRAY;
        }
        short[] subarray = new short[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static char[] subarray(char[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_CHAR_ARRAY;
        }
        char[] subarray = new char[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static double[] subarray(double[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        double[] subarray = new double[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static float[] subarray(float[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] subarray = new float[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static boolean[] subarray(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] subarray = new boolean[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static boolean isSameLength(Object[] array1, Object[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(long[] array1, long[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(int[] array1, int[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(short[] array1, short[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(char[] array1, char[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(byte[] array1, byte[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(double[] array1, double[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(float[] array1, float[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static boolean isSameLength(boolean[] array1, boolean[] array2) {
        return getLength(array1) == getLength(array2);
    }

    public static int getLength(Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static boolean isSameType(Object array1, Object array2) {
        if (array1 != null && array2 != null) {
            return array1.getClass().getName().equals(array2.getClass().getName());
        }
        throw new IllegalArgumentException("The Array must not be null");
    }

    public static void reverse(Object[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(long[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(int[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(short[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(char[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(byte[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(double[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(float[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(boolean[] array) {
        if (array != null) {
            reverse(array, 0, array.length);
        }
    }

    public static void reverse(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                boolean tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                byte tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(char[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                char tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(double[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                double tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(float[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                float tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(int[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                int tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(long[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                long tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(Object[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                Object tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void reverse(short[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array != null) {
            int j = Math.min(array.length, endIndexExclusive) - 1;
            for (int i = startIndexInclusive < 0 ? 0 : startIndexInclusive; j > i; i++) {
                short tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
            }
        }
    }

    public static void swap(Object[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(long[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(int[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(short[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(char[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(byte[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(double[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(float[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(boolean[] array, int offset1, int offset2) {
        if (array != null && array.length != 0) {
            swap(array, offset1, offset2, 1);
        }
    }

    public static void swap(boolean[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                boolean aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(byte[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                byte aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(char[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                char aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(double[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                double aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(float[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                float aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(int[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                int aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(long[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                long aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(Object[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
            int i = 0;
            while (i < len2) {
                Object aux = array[offset1];
                array[offset1] = array[offset2];
                array[offset2] = aux;
                i++;
                offset1++;
                offset2++;
            }
        }
    }

    public static void swap(short[] array, int offset1, int offset2, int len) {
        if (array != null && array.length != 0 && offset1 < array.length && offset2 < array.length) {
            if (offset1 < 0) {
                offset1 = 0;
            }
            if (offset2 < 0) {
                offset2 = 0;
            }
            if (offset1 != offset2) {
                int len2 = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
                int i = 0;
                while (i < len2) {
                    short aux = array[offset1];
                    array[offset1] = array[offset2];
                    array[offset2] = aux;
                    i++;
                    offset1++;
                    offset2++;
                }
            }
        }
    }

    public static void shift(Object[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(long[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(int[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(short[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(char[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(byte[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(double[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(float[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(boolean[] array, int offset) {
        if (array != null) {
            shift(array, 0, array.length, offset);
        }
    }

    public static void shift(boolean[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(byte[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(char[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(double[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(float[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(int[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(long[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(Object[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static void shift(short[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array != null && startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
                startIndexInclusive = 0;
            }
            if (endIndexExclusive >= array.length) {
                endIndexExclusive = array.length;
            }
            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
                int offset2 = offset % n;
                if (offset2 < 0) {
                    offset2 += n;
                }
                while (n > 1 && offset2 > 0) {
                    int n_offset = n - offset2;
                    if (offset2 > n_offset) {
                        swap(array, startIndexInclusive, (startIndexInclusive + n) - n_offset, n_offset);
                        n = offset2;
                        offset2 -= n_offset;
                    } else if (offset2 < n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        startIndexInclusive += offset2;
                        n = n_offset;
                    } else {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset2);
                        return;
                    }
                }
            }
        }
    }

    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i2 = startIndex; i2 < array.length; i2++) {
                if (objectToFind.equals(array[i2])) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i2 = startIndex; i2 >= 0; i2--) {
                if (objectToFind.equals(array[i2])) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != -1;
    }

    public static int indexOf(long[] array, long valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(long[] array, long valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(long[] array, long valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(int[] array, int valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(int[] array, int valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(int[] array, int valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(short[] array, short valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(short[] array, short valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(short[] array, short valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(char[] array, char valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(char[] array, char valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(char[] array, char valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(byte[] array, byte valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(double[] array, double valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance);
    }

    public static int indexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(double[] array, double valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(double[] array, double valueToFind, double tolerance) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }

    public static int lastIndexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(double[] array, double valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static boolean contains(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance) != -1;
    }

    public static int indexOf(float[] array, float valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(float[] array, float valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(float[] array, float valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(boolean[] array, boolean valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static char[] toPrimitive(Character[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_CHAR_ARRAY;
        }
        char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].charValue();
        }
        return result;
    }

    public static char[] toPrimitive(Character[] array, char valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_CHAR_ARRAY;
        }
        char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            Character b = array[i];
            result[i] = b == null ? valueForNull : b.charValue();
        }
        return result;
    }

    public static Character[] toObject(char[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Character.valueOf(array[i]);
        }
        return result;
    }

    public static long[] toPrimitive(Long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_LONG_ARRAY;
        }
        long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }

    public static long[] toPrimitive(Long[] array, long valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_LONG_ARRAY;
        }
        long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            Long b = array[i];
            result[i] = b == null ? valueForNull : b.longValue();
        }
        return result;
    }

    public static Long[] toObject(long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_LONG_OBJECT_ARRAY;
        }
        Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Long.valueOf(array[i]);
        }
        return result;
    }

    public static int[] toPrimitive(Integer[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

    public static int[] toPrimitive(Integer[] array, int valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            Integer b = array[i];
            result[i] = b == null ? valueForNull : b.intValue();
        }
        return result;
    }

    public static Integer[] toObject(int[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_INTEGER_OBJECT_ARRAY;
        }
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Integer.valueOf(array[i]);
        }
        return result;
    }

    public static short[] toPrimitive(Short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_SHORT_ARRAY;
        }
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }

    public static short[] toPrimitive(Short[] array, short valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_SHORT_ARRAY;
        }
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            Short b = array[i];
            result[i] = b == null ? valueForNull : b.shortValue();
        }
        return result;
    }

    public static Short[] toObject(short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_SHORT_OBJECT_ARRAY;
        }
        Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Short.valueOf(array[i]);
        }
        return result;
    }

    public static byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }

    public static byte[] toPrimitive(Byte[] array, byte valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            Byte b = array[i];
            result[i] = b == null ? valueForNull : b.byteValue();
        }
        return result;
    }

    public static Byte[] toObject(byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BYTE_OBJECT_ARRAY;
        }
        Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Byte.valueOf(array[i]);
        }
        return result;
    }

    public static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }

    public static double[] toPrimitive(Double[] array, double valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            Double b = array[i];
            result[i] = b == null ? valueForNull : b.doubleValue();
        }
        return result;
    }

    public static Double[] toObject(double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Double.valueOf(array[i]);
        }
        return result;
    }

    public static float[] toPrimitive(Float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }

    public static float[] toPrimitive(Float[] array, float valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            Float b = array[i];
            result[i] = b == null ? valueForNull : b.floatValue();
        }
        return result;
    }

    public static Float[] toObject(float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_FLOAT_OBJECT_ARRAY;
        }
        Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Float.valueOf(array[i]);
        }
        return result;
    }

    public static Object toPrimitive(Object array) {
        if (array == null) {
            return null;
        }
        Class<?> pt = ClassUtils.wrapperToPrimitive(array.getClass().getComponentType());
        if (Integer.TYPE.equals(pt)) {
            return toPrimitive((Integer[]) array);
        }
        if (Long.TYPE.equals(pt)) {
            return toPrimitive((Long[]) array);
        }
        if (Short.TYPE.equals(pt)) {
            return toPrimitive((Short[]) array);
        }
        if (Double.TYPE.equals(pt)) {
            return toPrimitive((Double[]) array);
        }
        if (Float.TYPE.equals(pt)) {
            return toPrimitive((Float[]) array);
        }
        return array;
    }

    public static boolean[] toPrimitive(Boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].booleanValue();
        }
        return result;
    }

    public static boolean[] toPrimitive(Boolean[] array, boolean valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            Boolean b = array[i];
            result[i] = b == null ? valueForNull : b.booleanValue();
        }
        return result;
    }

    public static Boolean[] toObject(boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i] ? Boolean.TRUE : Boolean.FALSE;
        }
        return result;
    }

    public static boolean isEmpty(Object[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(long[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(int[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(short[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(char[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(double[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(float[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return getLength(array) == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty((Object[]) array);
    }

    public static boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(int[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(short[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(char[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(byte[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(double[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(float[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(boolean[] array) {
        return !isEmpty(array);
    }

    public static <T> T[] addAll(T[] array1, T... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        Class<?> type1 = array1.getClass().getComponentType();
        T[] joinedArray = (Object[]) Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
            return joinedArray;
        } catch (ArrayStoreException ase) {
            Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), ase);
            }
            throw ase;
        }
    }

    public static boolean[] addAll(boolean[] array1, boolean... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        boolean[] joinedArray = new boolean[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static char[] addAll(char[] array1, char... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        char[] joinedArray = new char[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static byte[] addAll(byte[] array1, byte... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static short[] addAll(short[] array1, short... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        short[] joinedArray = new short[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static int[] addAll(int[] array1, int... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        int[] joinedArray = new int[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static long[] addAll(long[] array1, long... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        long[] joinedArray = new long[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static float[] addAll(float[] array1, float... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        float[] joinedArray = new float[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static double[] addAll(double[] array1, double... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        double[] joinedArray = new double[(array1.length + array2.length)];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static <T> T[] add(T[] array, T element) {
        Class<?> type;
        if (array != null) {
            type = array.getClass().getComponentType();
        } else if (element != null) {
            type = element.getClass();
        } else {
            throw new IllegalArgumentException("Arguments cannot both be null");
        }
        T[] newArray = (Object[]) copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static boolean[] add(boolean[] array, boolean element) {
        boolean[] newArray = (boolean[]) copyArrayGrow1(array, Boolean.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static byte[] add(byte[] array, byte element) {
        byte[] newArray = (byte[]) copyArrayGrow1(array, Byte.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static char[] add(char[] array, char element) {
        char[] newArray = (char[]) copyArrayGrow1(array, Character.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static double[] add(double[] array, double element) {
        double[] newArray = (double[]) copyArrayGrow1(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static float[] add(float[] array, float element) {
        float[] newArray = (float[]) copyArrayGrow1(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static int[] add(int[] array, int element) {
        int[] newArray = (int[]) copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static long[] add(long[] array, long element) {
        long[] newArray = (long[]) copyArrayGrow1(array, Long.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static short[] add(short[] array, short element) {
        short[] newArray = (short[]) copyArrayGrow1(array, Short.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    private static Object copyArrayGrow1(Object array, Class<?> newArrayComponentType) {
        if (array == null) {
            return Array.newInstance(newArrayComponentType, 1);
        }
        int arrayLength = Array.getLength(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
        System.arraycopy(array, 0, newArray, 0, arrayLength);
        return newArray;
    }

    @Deprecated
    public static <T> T[] add(T[] array, int index, T element) {
        Class<?> clss;
        if (array != null) {
            clss = array.getClass().getComponentType();
        } else if (element != null) {
            clss = element.getClass();
        } else {
            throw new IllegalArgumentException("Array and element cannot both be null");
        }
        return (Object[]) add(array, index, element, clss);
    }

    @Deprecated
    public static boolean[] add(boolean[] array, int index, boolean element) {
        return (boolean[]) add(array, index, Boolean.valueOf(element), Boolean.TYPE);
    }

    @Deprecated
    public static char[] add(char[] array, int index, char element) {
        return (char[]) add(array, index, Character.valueOf(element), Character.TYPE);
    }

    @Deprecated
    public static byte[] add(byte[] array, int index, byte element) {
        return (byte[]) add(array, index, Byte.valueOf(element), Byte.TYPE);
    }

    @Deprecated
    public static short[] add(short[] array, int index, short element) {
        return (short[]) add(array, index, Short.valueOf(element), Short.TYPE);
    }

    @Deprecated
    public static int[] add(int[] array, int index, int element) {
        return (int[]) add(array, index, Integer.valueOf(element), Integer.TYPE);
    }

    @Deprecated
    public static long[] add(long[] array, int index, long element) {
        return (long[]) add(array, index, Long.valueOf(element), Long.TYPE);
    }

    @Deprecated
    public static float[] add(float[] array, int index, float element) {
        return (float[]) add(array, index, Float.valueOf(element), Float.TYPE);
    }

    @Deprecated
    public static double[] add(double[] array, int index, double element) {
        return (double[]) add(array, index, Double.valueOf(element), Double.TYPE);
    }

    private static Object add(Object array, int index, Object element, Class<?> clss) {
        if (array != null) {
            int length = Array.getLength(array);
            if (index > length || index < 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
            }
            Object result = Array.newInstance(clss, length + 1);
            System.arraycopy(array, 0, result, 0, index);
            Array.set(result, index, element);
            if (index < length) {
                System.arraycopy(array, index, result, index + 1, length - index);
            }
            return result;
        } else if (index == 0) {
            Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
        }
    }

    public static <T> T[] remove(T[] array, int index) {
        return (Object[]) remove((Object) array, index);
    }

    public static <T> T[] removeElement(T[] array, Object element) {
        int index = indexOf((Object[]) array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static boolean[] remove(boolean[] array, int index) {
        return (boolean[]) remove((Object) array, index);
    }

    public static boolean[] removeElement(boolean[] array, boolean element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static byte[] remove(byte[] array, int index) {
        return (byte[]) remove((Object) array, index);
    }

    public static byte[] removeElement(byte[] array, byte element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static char[] remove(char[] array, int index) {
        return (char[]) remove((Object) array, index);
    }

    public static char[] removeElement(char[] array, char element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static double[] remove(double[] array, int index) {
        return (double[]) remove((Object) array, index);
    }

    public static double[] removeElement(double[] array, double element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static float[] remove(float[] array, int index) {
        return (float[]) remove((Object) array, index);
    }

    public static float[] removeElement(float[] array, float element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static int[] remove(int[] array, int index) {
        return (int[]) remove((Object) array, index);
    }

    public static int[] removeElement(int[] array, int element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static long[] remove(long[] array, int index) {
        return (long[]) remove((Object) array, index);
    }

    public static long[] removeElement(long[] array, long element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static short[] remove(short[] array, int index) {
        return (short[]) remove((Object) array, index);
    }

    public static short[] removeElement(short[] array, short element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }

    private static Object remove(Object array, int index) {
        int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, (length - index) - 1);
        }
        return result;
    }

    public static <T> T[] removeAll(T[] array, int... indices) {
        return (Object[]) removeAll((Object) array, indices);
    }

    @SafeVarargs
    public static <T> T[] removeElements(T[] array, T... values) {
        if (isEmpty((Object[]) array) || isEmpty((Object[]) values)) {
            return clone(array);
        }
        HashMap<T, MutableInt> occurrences = new HashMap<>(values.length);
        for (T v : values) {
            MutableInt count = occurrences.get(v);
            if (count == null) {
                occurrences.put(v, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            T key = array[i];
            MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (Object[]) removeAll((Object) array, toRemove);
    }

    public static byte[] removeAll(byte[] array, int... indices) {
        return (byte[]) removeAll((Object) array, indices);
    }

    public static byte[] removeElements(byte[] array, byte... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        Map<Byte, MutableInt> occurrences = new HashMap<>(values.length);
        for (byte v : values) {
            Byte boxed = Byte.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            byte key = array[i];
            MutableInt count2 = occurrences.get(Byte.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Byte.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (byte[]) removeAll((Object) array, toRemove);
    }

    public static short[] removeAll(short[] array, int... indices) {
        return (short[]) removeAll((Object) array, indices);
    }

    public static short[] removeElements(short[] array, short... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        HashMap<Short, MutableInt> occurrences = new HashMap<>(values.length);
        for (short v : values) {
            Short boxed = Short.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            short key = array[i];
            MutableInt count2 = occurrences.get(Short.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Short.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (short[]) removeAll((Object) array, toRemove);
    }

    public static int[] removeAll(int[] array, int... indices) {
        return (int[]) removeAll((Object) array, indices);
    }

    public static int[] removeElements(int[] array, int... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        HashMap<Integer, MutableInt> occurrences = new HashMap<>(values.length);
        for (int v : values) {
            Integer boxed = Integer.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            int key = array[i];
            MutableInt count2 = occurrences.get(Integer.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Integer.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (int[]) removeAll((Object) array, toRemove);
    }

    public static char[] removeAll(char[] array, int... indices) {
        return (char[]) removeAll((Object) array, indices);
    }

    public static char[] removeElements(char[] array, char... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        HashMap<Character, MutableInt> occurrences = new HashMap<>(values.length);
        for (char v : values) {
            Character boxed = Character.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            char key = array[i];
            MutableInt count2 = occurrences.get(Character.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Character.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (char[]) removeAll((Object) array, toRemove);
    }

    public static long[] removeAll(long[] array, int... indices) {
        return (long[]) removeAll((Object) array, indices);
    }

    public static long[] removeElements(long[] array, long... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        HashMap<Long, MutableInt> occurrences = new HashMap<>(values.length);
        for (long v : values) {
            Long boxed = Long.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            long key = array[i];
            MutableInt count2 = occurrences.get(Long.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Long.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (long[]) removeAll((Object) array, toRemove);
    }

    public static float[] removeAll(float[] array, int... indices) {
        return (float[]) removeAll((Object) array, indices);
    }

    public static float[] removeElements(float[] array, float... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        HashMap<Float, MutableInt> occurrences = new HashMap<>(values.length);
        for (float v : values) {
            Float boxed = Float.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            float key = array[i];
            MutableInt count2 = occurrences.get(Float.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Float.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (float[]) removeAll((Object) array, toRemove);
    }

    public static double[] removeAll(double[] array, int... indices) {
        return (double[]) removeAll((Object) array, indices);
    }

    public static double[] removeElements(double[] array, double... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        HashMap<Double, MutableInt> occurrences = new HashMap<>(values.length);
        for (double v : values) {
            Double boxed = Double.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            double key = array[i];
            MutableInt count2 = occurrences.get(Double.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Double.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (double[]) removeAll((Object) array, toRemove);
    }

    public static boolean[] removeAll(boolean[] array, int... indices) {
        return (boolean[]) removeAll((Object) array, indices);
    }

    public static boolean[] removeElements(boolean[] array, boolean... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        HashMap<Boolean, MutableInt> occurrences = new HashMap<>(2);
        for (boolean v : values) {
            Boolean boxed = Boolean.valueOf(v);
            MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            boolean key = array[i];
            MutableInt count2 = occurrences.get(Boolean.valueOf(key));
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(Boolean.valueOf(key));
                }
                toRemove.set(i);
            }
        }
        return (boolean[]) removeAll((Object) array, toRemove);
    }

    static Object removeAll(Object array, int... indices) {
        int index;
        int length = getLength(array);
        int diff = 0;
        int[] clonedIndices = clone(indices);
        Arrays.sort(clonedIndices);
        if (isNotEmpty(clonedIndices)) {
            int i = clonedIndices.length;
            int prevIndex = length;
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                index = clonedIndices[i];
                if (index < 0 || index >= length) {
                } else if (index < prevIndex) {
                    diff++;
                    prevIndex = index;
                }
            }
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(array.getClass().getComponentType(), length - diff);
        if (diff < length) {
            int end = length;
            int dest = length - diff;
            for (int i2 = clonedIndices.length - 1; i2 >= 0; i2--) {
                int index2 = clonedIndices[i2];
                if (end - index2 > 1) {
                    int cp = (end - index2) - 1;
                    dest -= cp;
                    System.arraycopy(array, index2 + 1, result, dest, cp);
                }
                end = index2;
            }
            if (end > 0) {
                System.arraycopy(array, 0, result, 0, end);
            }
        }
        return result;
    }

    static Object removeAll(Object array, BitSet indices) {
        int srcLength = getLength(array);
        Object result = Array.newInstance(array.getClass().getComponentType(), srcLength - indices.cardinality());
        int srcIndex = 0;
        int destIndex = 0;
        while (true) {
            int nextSetBit = indices.nextSetBit(srcIndex);
            int set = nextSetBit;
            if (nextSetBit == -1) {
                break;
            }
            int count = set - srcIndex;
            if (count > 0) {
                System.arraycopy(array, srcIndex, result, destIndex, count);
                destIndex += count;
            }
            srcIndex = indices.nextClearBit(set);
        }
        int count2 = srcLength - srcIndex;
        if (count2 > 0) {
            System.arraycopy(array, srcIndex, result, destIndex, count2);
        }
        return result;
    }

    public static <T extends Comparable<? super T>> boolean isSorted(T[] array) {
        return isSorted(array, new Comparator<T>() {
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static <T> boolean isSorted(T[] array, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator should not be null.");
        } else if (array == null || array.length < 2) {
            return true;
        } else {
            T previous = array[0];
            int n = array.length;
            for (int i = 1; i < n; i++) {
                T current = array[i];
                if (comparator.compare(previous, current) > 0) {
                    return false;
                }
                previous = current;
            }
            return true;
        }
    }

    public static boolean isSorted(int[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        int previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            int current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSorted(long[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        long previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            long current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSorted(short[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        short previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            short current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSorted(double[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        double previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            double current = array[i];
            if (Double.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSorted(float[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        float previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            float current = array[i];
            if (Float.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSorted(byte[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        byte previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            byte current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSorted(char[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        char previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            char current = array[i];
            if (CharUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSorted(boolean[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        boolean previous = array[0];
        int n = array.length;
        for (int i = 1; i < n; i++) {
            boolean current = array[i];
            if (BooleanUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean[] removeAllOccurences(boolean[] array, boolean element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static char[] removeAllOccurences(char[] array, char element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static byte[] removeAllOccurences(byte[] array, byte element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static short[] removeAllOccurences(short[] array, short element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static int[] removeAllOccurences(int[] array, int element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static long[] removeAllOccurences(long[] array, long element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static float[] removeAllOccurences(float[] array, float element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static double[] removeAllOccurences(double[] array, double element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf(array, element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static <T> T[] removeAllOccurences(T[] array, T element) {
        int index = indexOf((Object[]) array, (Object) element);
        if (index == -1) {
            return clone(array);
        }
        int[] indices = new int[(array.length - index)];
        indices[0] = index;
        int count = 1;
        while (true) {
            int indexOf = indexOf((Object[]) array, (Object) element, indices[count - 1] + 1);
            int index2 = indexOf;
            if (indexOf == -1) {
                return removeAll(array, Arrays.copyOf(indices, count));
            }
            indices[count] = index2;
            count++;
        }
    }

    public static String[] toStringArray(Object[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_STRING_ARRAY;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }

    public static String[] toStringArray(Object[] array, String valueForNullElements) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_STRING_ARRAY;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            result[i] = object == null ? valueForNullElements : object.toString();
        }
        return result;
    }

    public static boolean[] insert(int index, boolean[] array, boolean... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        boolean[] result = new boolean[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static byte[] insert(int index, byte[] array, byte... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        byte[] result = new byte[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static char[] insert(int index, char[] array, char... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        char[] result = new char[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static double[] insert(int index, double[] array, double... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        double[] result = new double[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static float[] insert(int index, float[] array, float... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        float[] result = new float[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static int[] insert(int index, int[] array, int... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        int[] result = new int[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static long[] insert(int index, long[] array, long... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        long[] result = new long[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static short[] insert(int index, short[] array, short... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        short[] result = new short[(array.length + values.length)];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    @SafeVarargs
    public static <T> T[] insert(int index, T[] array, T... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        T[] result = (Object[]) Array.newInstance(array.getClass().getComponentType(), array.length + values.length);
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, values.length + index, array.length - index);
        }
        return result;
    }

    public static void shuffle(Object[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(Object[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(boolean[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(boolean[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(byte[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(byte[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(char[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(char[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(short[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(short[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(int[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(int[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(long[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(long[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(float[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(float[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    public static void shuffle(double[] array) {
        shuffle(array, new Random());
    }

    public static void shuffle(double[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
}
