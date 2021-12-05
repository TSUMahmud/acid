package org.apache.commons.lang3.math;

import org.apache.commons.lang3.Validate;

public class IEEE754rUtils {
    public static double min(double... array) {
        boolean z = true;
        Validate.isTrue(array != null, "The Array must not be null", new Object[0]);
        if (array.length == 0) {
            z = false;
        }
        Validate.isTrue(z, "Array cannot be empty.", new Object[0]);
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            min = min(array[i], min);
        }
        return min;
    }

    public static float min(float... array) {
        boolean z = true;
        Validate.isTrue(array != null, "The Array must not be null", new Object[0]);
        if (array.length == 0) {
            z = false;
        }
        Validate.isTrue(z, "Array cannot be empty.", new Object[0]);
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            min = min(array[i], min);
        }
        return min;
    }

    public static double min(double a, double b, double c) {
        return min(min(a, b), c);
    }

    public static double min(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    public static float min(float a, float b, float c) {
        return min(min(a, b), c);
    }

    public static float min(float a, float b) {
        if (Float.isNaN(a)) {
            return b;
        }
        if (Float.isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    public static double max(double... array) {
        boolean z = true;
        Validate.isTrue(array != null, "The Array must not be null", new Object[0]);
        if (array.length == 0) {
            z = false;
        }
        Validate.isTrue(z, "Array cannot be empty.", new Object[0]);
        double max = array[0];
        for (int j = 1; j < array.length; j++) {
            max = max(array[j], max);
        }
        return max;
    }

    public static float max(float... array) {
        boolean z = true;
        Validate.isTrue(array != null, "The Array must not be null", new Object[0]);
        if (array.length == 0) {
            z = false;
        }
        Validate.isTrue(z, "Array cannot be empty.", new Object[0]);
        float max = array[0];
        for (int j = 1; j < array.length; j++) {
            max = max(array[j], max);
        }
        return max;
    }

    public static double max(double a, double b, double c) {
        return max(max(a, b), c);
    }

    public static double max(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.max(a, b);
    }

    public static float max(float a, float b, float c) {
        return max(max(a, b), c);
    }

    public static float max(float a, float b) {
        if (Float.isNaN(a)) {
            return b;
        }
        if (Float.isNaN(b)) {
            return a;
        }
        return Math.max(a, b);
    }
}
