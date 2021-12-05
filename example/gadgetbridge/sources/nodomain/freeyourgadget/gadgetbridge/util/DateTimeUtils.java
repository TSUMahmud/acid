package nodomain.freeyourgadget.gadgetbridge.util;

import android.text.format.DateUtils;
import com.github.pfichtner.durationformatter.DurationFormatter;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class DateTimeUtils {
    private static SimpleDateFormat DAY_STORAGE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static SimpleDateFormat HOURS_MINUTES_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
    public static SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US) {
        public Date parse(String text, ParsePosition pos) {
            if (text.length() > 3) {
                text = text.substring(0, text.length() - 3) + text.substring(text.length() - 2);
            }
            return super.parse(text, pos);
        }

        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer rfcFormat = super.format(date, toAppendTo, pos);
            if (!getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
                return rfcFormat.insert(rfcFormat.length() - 2, ":");
            }
            rfcFormat.setLength(rfcFormat.length() - 5);
            rfcFormat.append("Z");
            return rfcFormat;
        }
    };

    public static String formatDateTime(Date date) {
        return DateUtils.formatDateTime(GBApplication.getContext(), date.getTime(), 25);
    }

    public static String formatIso8601(Date date) {
        if (GBApplication.isRunningNougatOrLater()) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(date);
        }
        ISO_8601_FORMAT.setTimeZone(TimeZone.getDefault());
        return ISO_8601_FORMAT.format(date);
    }

    public static String formatIso8601UTC(Date date) {
        if (GBApplication.isRunningNougatOrLater()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);
        }
        ISO_8601_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        return ISO_8601_FORMAT.format(date);
    }

    public static String formatDate(Date date) {
        return DateUtils.formatDateTime(GBApplication.getContext(), date.getTime(), 16);
    }

    public static String formatDurationHoursMinutes(long duration, TimeUnit unit) {
        return DurationFormatter.Builder.SYMBOLS.maximum(TimeUnit.DAYS).minimum(TimeUnit.MINUTES).suppressZeros(DurationFormatter.SuppressZeros.LEADING, DurationFormatter.SuppressZeros.TRAILING).maximumAmountOfUnitsToShow(2).build().format(duration, unit);
    }

    public static String formatDateRange(Date from, Date to) {
        return DateUtils.formatDateRange(GBApplication.getContext(), from.getTime(), to.getTime(), 16);
    }

    public static Date shiftByDays(Date date, int offset) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        cal.add(6, offset);
        return cal.getTime();
    }

    public static Date parseTimeStamp(int timestamp) {
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        cal.setTimeInMillis(((long) timestamp) * 1000);
        return cal.getTime();
    }

    public static String dayToString(Date date) {
        return DAY_STORAGE_FORMAT.format(date);
    }

    public static Date dayFromString(String day) throws ParseException {
        return DAY_STORAGE_FORMAT.parse(day);
    }

    public static String timeToString(Date date) {
        return HOURS_MINUTES_FORMAT.format(date);
    }

    public static String formatTime(int hours, int minutes) {
        return String.format(Locale.US, "%02d", new Object[]{Integer.valueOf(hours)}) + ":" + String.format(Locale.US, "%02d", new Object[]{Integer.valueOf(minutes)});
    }

    public static Date todayUTC() {
        return getCalendarUTC().getTime();
    }

    public static Calendar getCalendarUTC() {
        return GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    public static String minutesToHHMM(int minutes) {
        return String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(minutes / 60), Integer.valueOf(minutes % 60)});
    }
}
