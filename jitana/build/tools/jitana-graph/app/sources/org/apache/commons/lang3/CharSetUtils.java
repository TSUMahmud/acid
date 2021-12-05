package org.apache.commons.lang3;

public class CharSetUtils {
    public static String squeeze(String str, String... set) {
        if (StringUtils.isEmpty(str) || deepEmpty(set)) {
            return str;
        }
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        char[] chrs = str.toCharArray();
        int sz = chrs.length;
        char lastChar = chrs[0];
        Character inChars = null;
        Character notInChars = null;
        buffer.append(lastChar);
        for (int i = 1; i < sz; i++) {
            char ch = chrs[i];
            if (ch == lastChar) {
                if (inChars != null && ch == inChars.charValue()) {
                } else if (notInChars == null || ch != notInChars.charValue()) {
                    if (chars.contains(ch)) {
                        inChars = Character.valueOf(ch);
                    } else {
                        notInChars = Character.valueOf(ch);
                    }
                }
            }
            buffer.append(ch);
            lastChar = ch;
        }
        return buffer.toString();
    }

    public static boolean containsAny(String str, String... set) {
        if (StringUtils.isEmpty(str) || deepEmpty(set)) {
            return false;
        }
        CharSet chars = CharSet.getInstance(set);
        for (char c : str.toCharArray()) {
            if (chars.contains(c)) {
                return true;
            }
        }
        return false;
    }

    public static int count(String str, String... set) {
        if (StringUtils.isEmpty(str) || deepEmpty(set)) {
            return 0;
        }
        CharSet chars = CharSet.getInstance(set);
        int count = 0;
        for (char c : str.toCharArray()) {
            if (chars.contains(c)) {
                count++;
            }
        }
        return count;
    }

    public static String keep(String str, String... set) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty() || deepEmpty(set)) {
            return "";
        }
        return modify(str, set, true);
    }

    public static String delete(String str, String... set) {
        if (StringUtils.isEmpty(str) || deepEmpty(set)) {
            return str;
        }
        return modify(str, set, false);
    }

    private static String modify(String str, String[] set, boolean expect) {
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        for (char chr : str.toCharArray()) {
            if (chars.contains(chr) == expect) {
                buffer.append(chr);
            }
        }
        return buffer.toString();
    }

    private static boolean deepEmpty(String[] strings) {
        if (strings == null) {
            return true;
        }
        for (String s : strings) {
            if (StringUtils.isNotEmpty(s)) {
                return false;
            }
        }
        return true;
    }
}
