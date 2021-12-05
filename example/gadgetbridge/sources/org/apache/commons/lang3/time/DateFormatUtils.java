package org.apache.commons.lang3.time;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatUtils {
    public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
    public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");
    public static final FastDateFormat ISO_8601_EXTENDED_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    public static final FastDateFormat ISO_8601_EXTENDED_TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");
    public static final FastDateFormat ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("HH:mm:ssZZ");
    @Deprecated
    public static final FastDateFormat ISO_DATETIME_FORMAT = ISO_8601_EXTENDED_DATETIME_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_DATE_FORMAT = ISO_8601_EXTENDED_DATE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_DATE_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-ddZZ");
    @Deprecated
    public static final FastDateFormat ISO_TIME_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ss");
    @Deprecated
    public static final FastDateFormat ISO_TIME_NO_T_FORMAT = ISO_8601_EXTENDED_TIME_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_TIME_NO_T_TIME_ZONE_FORMAT = ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ssZZ");
    public static final FastDateFormat SMTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    private static final TimeZone UTC_TIME_ZONE = FastTimeZone.getGmtTimeZone();

    public static String formatUTC(long millis, String pattern) {
        return format(new Date(millis), pattern, UTC_TIME_ZONE, (Locale) null);
    }

    public static String formatUTC(Date date, String pattern) {
        return format(date, pattern, UTC_TIME_ZONE, (Locale) null);
    }

    public static String formatUTC(long millis, String pattern, Locale locale) {
        return format(new Date(millis), pattern, UTC_TIME_ZONE, locale);
    }

    public static String formatUTC(Date date, String pattern, Locale locale) {
        return format(date, pattern, UTC_TIME_ZONE, locale);
    }

    public static String format(long millis, String pattern) {
        return format(new Date(millis), pattern, (TimeZone) null, (Locale) null);
    }

    public static String format(Date date, String pattern) {
        return format(date, pattern, (TimeZone) null, (Locale) null);
    }

    public static String format(Calendar calendar, String pattern) {
        return format(calendar, pattern, (TimeZone) null, (Locale) null);
    }

    public static String format(long millis, String pattern, TimeZone timeZone) {
        return format(new Date(millis), pattern, timeZone, (Locale) null);
    }

    public static String format(Date date, String pattern, TimeZone timeZone) {
        return format(date, pattern, timeZone, (Locale) null);
    }

    public static String format(Calendar calendar, String pattern, TimeZone timeZone) {
        return format(calendar, pattern, timeZone, (Locale) null);
    }

    public static String format(long millis, String pattern, Locale locale) {
        return format(new Date(millis), pattern, (TimeZone) null, locale);
    }

    public static String format(Date date, String pattern, Locale locale) {
        return format(date, pattern, (TimeZone) null, locale);
    }

    public static String format(Calendar calendar, String pattern, Locale locale) {
        return format(calendar, pattern, (TimeZone) null, locale);
    }

    public static String format(long millis, String pattern, TimeZone timeZone, Locale locale) {
        return format(new Date(millis), pattern, timeZone, locale);
    }

    public static String format(Date date, String pattern, TimeZone timeZone, Locale locale) {
        return FastDateFormat.getInstance(pattern, timeZone, locale).format(date);
    }

    public static String format(Calendar calendar, String pattern, TimeZone timeZone, Locale locale) {
        return FastDateFormat.getInstance(pattern, timeZone, locale).format(calendar);
    }
}
