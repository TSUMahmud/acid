package org.apache.commons.lang3;

import org.apache.commons.lang3.math.NumberUtils;

public class BooleanUtils {
    public static Boolean negate(Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
    }

    public static boolean isTrue(Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }

    public static boolean isNotTrue(Boolean bool) {
        return !isTrue(bool);
    }

    public static boolean isFalse(Boolean bool) {
        return Boolean.FALSE.equals(bool);
    }

    public static boolean isNotFalse(Boolean bool) {
        return !isFalse(bool);
    }

    public static boolean toBoolean(Boolean bool) {
        return bool != null && bool.booleanValue();
    }

    public static boolean toBooleanDefaultIfNull(Boolean bool, boolean valueIfNull) {
        if (bool == null) {
            return valueIfNull;
        }
        return bool.booleanValue();
    }

    public static boolean toBoolean(int value) {
        return value != 0;
    }

    public static Boolean toBooleanObject(int value) {
        return value == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Boolean toBooleanObject(Integer value) {
        if (value == null) {
            return null;
        }
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static boolean toBoolean(int value, int trueValue, int falseValue) {
        if (value == trueValue) {
            return true;
        }
        if (value == falseValue) {
            return false;
        }
        throw new IllegalArgumentException("The Integer did not match either specified value");
    }

    public static boolean toBoolean(Integer value, Integer trueValue, Integer falseValue) {
        if (value == null) {
            if (trueValue == null) {
                return true;
            }
            if (falseValue == null) {
                return false;
            }
        } else if (value.equals(trueValue)) {
            return true;
        } else {
            if (value.equals(falseValue)) {
                return false;
            }
        }
        throw new IllegalArgumentException("The Integer did not match either specified value");
    }

    public static Boolean toBooleanObject(int value, int trueValue, int falseValue, int nullValue) {
        if (value == trueValue) {
            return Boolean.TRUE;
        }
        if (value == falseValue) {
            return Boolean.FALSE;
        }
        if (value == nullValue) {
            return null;
        }
        throw new IllegalArgumentException("The Integer did not match any specified value");
    }

    public static Boolean toBooleanObject(Integer value, Integer trueValue, Integer falseValue, Integer nullValue) {
        if (value == null) {
            if (trueValue == null) {
                return Boolean.TRUE;
            }
            if (falseValue == null) {
                return Boolean.FALSE;
            }
            if (nullValue == null) {
                return null;
            }
        } else if (value.equals(trueValue)) {
            return Boolean.TRUE;
        } else {
            if (value.equals(falseValue)) {
                return Boolean.FALSE;
            }
            if (value.equals(nullValue)) {
                return null;
            }
        }
        throw new IllegalArgumentException("The Integer did not match any specified value");
    }

    public static int toInteger(boolean bool) {
        return bool;
    }

    public static Integer toIntegerObject(boolean bool) {
        return bool ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }

    public static Integer toIntegerObject(Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool.booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }

    public static int toInteger(boolean bool, int trueValue, int falseValue) {
        return bool ? trueValue : falseValue;
    }

    public static int toInteger(Boolean bool, int trueValue, int falseValue, int nullValue) {
        if (bool == null) {
            return nullValue;
        }
        return bool.booleanValue() ? trueValue : falseValue;
    }

    public static Integer toIntegerObject(boolean bool, Integer trueValue, Integer falseValue) {
        return bool ? trueValue : falseValue;
    }

    public static Integer toIntegerObject(Boolean bool, Integer trueValue, Integer falseValue, Integer nullValue) {
        if (bool == null) {
            return nullValue;
        }
        return bool.booleanValue() ? trueValue : falseValue;
    }

    public static Boolean toBooleanObject(String str) {
        if (str == "true") {
            return Boolean.TRUE;
        }
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length == 1) {
            char ch0 = str.charAt(0);
            if (ch0 == 'y' || ch0 == 'Y' || ch0 == 't' || ch0 == 'T') {
                return Boolean.TRUE;
            }
            if (ch0 == 'n' || ch0 == 'N' || ch0 == 'f' || ch0 == 'F') {
                return Boolean.FALSE;
            }
        } else if (length == 2) {
            char ch02 = str.charAt(0);
            char ch1 = str.charAt(1);
            if ((ch02 == 'o' || ch02 == 'O') && (ch1 == 'n' || ch1 == 'N')) {
                return Boolean.TRUE;
            }
            if ((ch02 == 'n' || ch02 == 'N') && (ch1 == 'o' || ch1 == 'O')) {
                return Boolean.FALSE;
            }
        } else if (length == 3) {
            char ch03 = str.charAt(0);
            char ch12 = str.charAt(1);
            char ch2 = str.charAt(2);
            if ((ch03 == 'y' || ch03 == 'Y') && ((ch12 == 'e' || ch12 == 'E') && (ch2 == 's' || ch2 == 'S'))) {
                return Boolean.TRUE;
            }
            if ((ch03 == 'o' || ch03 == 'O') && ((ch12 == 'f' || ch12 == 'F') && (ch2 == 'f' || ch2 == 'F'))) {
                return Boolean.FALSE;
            }
        } else if (length == 4) {
            char ch04 = str.charAt(0);
            char ch13 = str.charAt(1);
            char ch22 = str.charAt(2);
            char ch3 = str.charAt(3);
            if ((ch04 == 't' || ch04 == 'T') && ((ch13 == 'r' || ch13 == 'R') && ((ch22 == 'u' || ch22 == 'U') && (ch3 == 'e' || ch3 == 'E')))) {
                return Boolean.TRUE;
            }
        } else if (length == 5) {
            char ch05 = str.charAt(0);
            char ch14 = str.charAt(1);
            char ch23 = str.charAt(2);
            char ch32 = str.charAt(3);
            char ch4 = str.charAt(4);
            if ((ch05 == 'f' || ch05 == 'F') && ((ch14 == 'a' || ch14 == 'A') && ((ch23 == 'l' || ch23 == 'L') && ((ch32 == 's' || ch32 == 'S') && (ch4 == 'e' || ch4 == 'E'))))) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    public static Boolean toBooleanObject(String str, String trueString, String falseString, String nullString) {
        if (str == null) {
            if (trueString == null) {
                return Boolean.TRUE;
            }
            if (falseString == null) {
                return Boolean.FALSE;
            }
            if (nullString == null) {
                return null;
            }
        } else if (str.equals(trueString)) {
            return Boolean.TRUE;
        } else {
            if (str.equals(falseString)) {
                return Boolean.FALSE;
            }
            if (str.equals(nullString)) {
                return null;
            }
        }
        throw new IllegalArgumentException("The String did not match any specified value");
    }

    public static boolean toBoolean(String str) {
        return toBooleanObject(str) == Boolean.TRUE;
    }

    public static boolean toBoolean(String str, String trueString, String falseString) {
        if (str == trueString) {
            return true;
        }
        if (str == falseString) {
            return false;
        }
        if (str != null) {
            if (str.equals(trueString)) {
                return true;
            }
            if (str.equals(falseString)) {
                return false;
            }
        }
        throw new IllegalArgumentException("The String did not match either specified value");
    }

    public static String toStringTrueFalse(Boolean bool) {
        return toString(bool, "true", "false", (String) null);
    }

    public static String toStringOnOff(Boolean bool) {
        return toString(bool, "on", "off", (String) null);
    }

    public static String toStringYesNo(Boolean bool) {
        return toString(bool, "yes", "no", (String) null);
    }

    public static String toString(Boolean bool, String trueString, String falseString, String nullString) {
        if (bool == null) {
            return nullString;
        }
        return bool.booleanValue() ? trueString : falseString;
    }

    public static String toStringTrueFalse(boolean bool) {
        return toString(bool, "true", "false");
    }

    public static String toStringOnOff(boolean bool) {
        return toString(bool, "on", "off");
    }

    public static String toStringYesNo(boolean bool) {
        return toString(bool, "yes", "no");
    }

    public static String toString(boolean bool, String trueString, String falseString) {
        return bool ? trueString : falseString;
    }

    public static boolean and(boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            for (boolean element : array) {
                if (!element) {
                    return false;
                }
            }
            return true;
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    public static Boolean and(Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            try {
                return and(ArrayUtils.toPrimitive(array)) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    /* renamed from: or */
    public static boolean m41or(boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            for (boolean element : array) {
                if (element) {
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    /* renamed from: or */
    public static Boolean m40or(Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            try {
                return m41or(ArrayUtils.toPrimitive(array)) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    public static boolean xor(boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            boolean result = false;
            for (boolean element : array) {
                result ^= element;
            }
            return result;
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    public static Boolean xor(Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            try {
                return xor(ArrayUtils.toPrimitive(array)) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    public static int compare(boolean x, boolean y) {
        if (x == y) {
            return 0;
        }
        return x ? 1 : -1;
    }
}
