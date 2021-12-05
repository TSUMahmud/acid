package org.apache.commons.lang3.time;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastTimeZone {
    private static final Pattern GMT_PATTERN = Pattern.compile("^(?:(?i)GMT)?([+-])?(\\d\\d?)?(:?(\\d\\d?))?$");
    private static final TimeZone GREENWICH = new GmtTimeZone(false, 0, 0);

    public static TimeZone getGmtTimeZone() {
        return GREENWICH;
    }

    public static TimeZone getGmtTimeZone(String pattern) {
        if ("Z".equals(pattern) || "UTC".equals(pattern)) {
            return GREENWICH;
        }
        Matcher m = GMT_PATTERN.matcher(pattern);
        if (!m.matches()) {
            return null;
        }
        int hours = parseInt(m.group(2));
        int minutes = parseInt(m.group(4));
        if (hours == 0 && minutes == 0) {
            return GREENWICH;
        }
        return new GmtTimeZone(parseSign(m.group(1)), hours, minutes);
    }

    public static TimeZone getTimeZone(String id) {
        TimeZone tz = getGmtTimeZone(id);
        if (tz != null) {
            return tz;
        }
        return TimeZone.getTimeZone(id);
    }

    private static int parseInt(String group) {
        if (group != null) {
            return Integer.parseInt(group);
        }
        return 0;
    }

    private static boolean parseSign(String group) {
        return group != null && group.charAt(0) == '-';
    }

    private FastTimeZone() {
    }
}
