package org.apache.commons.lang3;

public class CharSequenceUtils {
    private static final int NOT_FOUND = -1;

    public static CharSequence subSequence(CharSequence cs, int start) {
        if (cs == null) {
            return null;
        }
        return cs.subSequence(start, cs.length());
    }

    static int indexOf(CharSequence cs, int searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar, start);
        }
        int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        if (searchChar < 65536) {
            for (int i = start; i < sz; i++) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
        }
        if (searchChar > 1114111) {
            return -1;
        }
        char[] chars = Character.toChars(searchChar);
        for (int i2 = start; i2 < sz - 1; i2++) {
            char high = cs.charAt(i2);
            char low = cs.charAt(i2 + 1);
            if (high == chars[0] && low == chars[1]) {
                return i2;
            }
        }
        return -1;
    }

    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    static int lastIndexOf(CharSequence cs, int searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).lastIndexOf(searchChar, start);
        }
        int sz = cs.length();
        if (start < 0) {
            return -1;
        }
        if (start >= sz) {
            start = sz - 1;
        }
        if (searchChar < 65536) {
            for (int i = start; i >= 0; i--) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
        }
        if (searchChar <= 1114111) {
            char[] chars = Character.toChars(searchChar);
            if (start == sz - 1) {
                return -1;
            }
            for (int i2 = start; i2 >= 0; i2--) {
                char high = cs.charAt(i2);
                char low = cs.charAt(i2 + 1);
                if (chars[0] == high && chars[1] == low) {
                    return i2;
                }
            }
        }
        return -1;
    }

    static int lastIndexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().lastIndexOf(searchChar.toString(), start);
    }

    static char[] toCharArray(CharSequence cs) {
        if (cs instanceof String) {
            return ((String) cs).toCharArray();
        }
        int sz = cs.length();
        char[] array = new char[cs.length()];
        for (int i = 0; i < sz; i++) {
            array[i] = cs.charAt(i);
        }
        return array;
    }

    static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if (!(cs instanceof String) || !(substring instanceof String)) {
            int index1 = thisStart;
            int index2 = start;
            int index12 = length;
            int srcLen = cs.length() - thisStart;
            int otherLen = substring.length() - start;
            if (thisStart < 0 || start < 0 || length < 0 || srcLen < length || otherLen < length) {
                return false;
            }
            while (true) {
                int tmpLen = index12 - 1;
                if (index12 <= 0) {
                    return true;
                }
                int index13 = index1 + 1;
                char c1 = cs.charAt(index1);
                int index22 = index2 + 1;
                char c2 = substring.charAt(index2);
                if (c1 != c2) {
                    if (!ignoreCase) {
                        return false;
                    }
                    if (!(Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
                        return false;
                    }
                }
                index1 = index13;
                index12 = tmpLen;
                index2 = index22;
            }
        } else {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
    }
}
