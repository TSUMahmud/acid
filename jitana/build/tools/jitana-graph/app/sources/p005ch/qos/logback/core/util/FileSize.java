package p005ch.qos.logback.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* renamed from: ch.qos.logback.core.util.FileSize */
public class FileSize {
    private static final int DOUBLE_GROUP = 1;
    private static final Pattern FILE_SIZE_PATTERN = Pattern.compile("([0-9]+)\\s*(|kb|mb|gb)s?", 2);
    static final long GB_COEFFICIENT = 1073741824;
    static final long KB_COEFFICIENT = 1024;
    private static final String LENGTH_PART = "([0-9]+)";
    static final long MB_COEFFICIENT = 1048576;
    private static final int UNIT_GROUP = 2;
    private static final String UNIT_PART = "(|kb|mb|gb)s?";
    final long size;

    FileSize(long j) {
        this.size = j;
    }

    public static FileSize valueOf(String str) {
        long j;
        Matcher matcher = FILE_SIZE_PATTERN.matcher(str);
        if (matcher.matches()) {
            String group = matcher.group(1);
            String group2 = matcher.group(2);
            long longValue = Long.valueOf(group).longValue();
            if (group2.equalsIgnoreCase("")) {
                j = 1;
            } else if (group2.equalsIgnoreCase("kb")) {
                j = 1024;
            } else if (group2.equalsIgnoreCase("mb")) {
                j = 1048576;
            } else if (group2.equalsIgnoreCase("gb")) {
                j = GB_COEFFICIENT;
            } else {
                throw new IllegalStateException("Unexpected " + group2);
            }
            return new FileSize(longValue * j);
        }
        throw new IllegalArgumentException("String value [" + str + "] is not in the expected format.");
    }

    public long getSize() {
        return this.size;
    }
}
