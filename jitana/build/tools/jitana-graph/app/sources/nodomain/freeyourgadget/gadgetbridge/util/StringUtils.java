package nodomain.freeyourgadget.gadgetbridge.util;

public class StringUtils {
    public static String truncate(String s, int maxLength) {
        int length;
        if (s != null && (length = Math.min(s.length(), maxLength)) >= 0) {
            return s.substring(0, length);
        }
        return "";
    }

    public static String pad(String s, int length) {
        return pad(s, length, ' ');
    }

    public static String pad(String s, int length, char padChar) {
        while (s.length() < length) {
            s = s + padChar;
        }
        return s;
    }

    public static StringBuilder join(String separator, String... elements) {
        StringBuilder builder = new StringBuilder();
        if (elements == null) {
            return builder;
        }
        boolean hasAdded = false;
        for (String element : elements) {
            if (element != null && element.length() > 0) {
                if (hasAdded) {
                    builder.append(separator);
                }
                builder.append(element);
                hasAdded = true;
            }
        }
        return builder;
    }

    public static String getFirstOf(String first, String second) {
        if (first != null && first.length() > 0) {
            return first;
        }
        if (second != null) {
            return second;
        }
        return "";
    }

    public static boolean isEmpty(String string) {
        return string != null && string.length() == 0;
    }

    public static String ensureNotNull(String message) {
        if (message != null) {
            return message;
        }
        return "";
    }
}
