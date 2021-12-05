package p005ch.qos.logback.core.rolling.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.time.TimeZones;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.rolling.helper.RollingCalendar */
public class RollingCalendar extends GregorianCalendar {
    static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone(TimeZones.GMT_ID);
    private static final long serialVersionUID = -5937537740925066161L;
    PeriodicityType periodicityType = PeriodicityType.ERRONEOUS;

    public RollingCalendar() {
    }

    public RollingCalendar(TimeZone timeZone, Locale locale) {
        super(timeZone, locale);
    }

    public static int diffInMonths(long j, long j2) {
        if (j <= j2) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(j);
            Calendar instance2 = Calendar.getInstance();
            instance2.setTimeInMillis(j2);
            return ((instance2.get(1) - instance.get(1)) * 12) + (instance2.get(2) - instance.get(2));
        }
        throw new IllegalArgumentException("startTime cannot be larger than endTime");
    }

    private void setPeriodicityType(PeriodicityType periodicityType2) {
        this.periodicityType = periodicityType2;
    }

    public PeriodicityType computePeriodicityType(String str) {
        RollingCalendar rollingCalendar = new RollingCalendar(GMT_TIMEZONE, Locale.getDefault());
        Date date = new Date(0);
        if (str != null) {
            for (PeriodicityType periodicityType2 : PeriodicityType.VALID_ORDERED_LIST) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
                simpleDateFormat.setTimeZone(GMT_TIMEZONE);
                String format = simpleDateFormat.format(date);
                rollingCalendar.setPeriodicityType(periodicityType2);
                String format2 = simpleDateFormat.format(new Date(rollingCalendar.getNextTriggeringMillis(date)));
                if (format != null && format2 != null && !format.equals(format2)) {
                    return periodicityType2;
                }
            }
        }
        return PeriodicityType.ERRONEOUS;
    }

    public Date getNextTriggeringDate(Date date) {
        return getRelativeDate(date, 1);
    }

    public long getNextTriggeringMillis(Date date) {
        return getNextTriggeringDate(date).getTime();
    }

    public PeriodicityType getPeriodicityType() {
        return this.periodicityType;
    }

    public Date getRelativeDate(Date date, int i) {
        int i2;
        setTime(date);
        switch (this.periodicityType) {
            case TOP_OF_MILLISECOND:
                add(14, i);
                break;
            case TOP_OF_SECOND:
                set(14, 0);
                add(13, i);
                break;
            case TOP_OF_MINUTE:
                set(13, 0);
                set(14, 0);
                add(12, i);
                break;
            case TOP_OF_HOUR:
                set(12, 0);
                set(13, 0);
                set(14, 0);
                add(11, i);
                break;
            case TOP_OF_DAY:
                set(11, 0);
                set(12, 0);
                set(13, 0);
                set(14, 0);
                add(5, i);
                break;
            case TOP_OF_WEEK:
                set(7, getFirstDayOfWeek());
                set(11, 0);
                set(12, 0);
                set(13, 0);
                set(14, 0);
                i2 = 3;
                break;
            case TOP_OF_MONTH:
                set(5, 1);
                set(11, 0);
                set(12, 0);
                set(13, 0);
                set(14, 0);
                i2 = 2;
                break;
            default:
                throw new IllegalStateException("Unknown periodicity type.");
        }
        add(i2, i);
        return getTime();
    }

    public void init(String str) {
        this.periodicityType = computePeriodicityType(str);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0025, code lost:
        return r0 / r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0020, code lost:
        return (long) r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long periodsElapsed(long r5, long r7) {
        /*
            r4 = this;
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 > 0) goto L_0x0038
            long r0 = r7 - r5
            int[] r2 = p005ch.qos.logback.core.rolling.helper.RollingCalendar.C05281.$SwitchMap$ch$qos$logback$core$rolling$helper$PeriodicityType
            ch.qos.logback.core.rolling.helper.PeriodicityType r3 = r4.periodicityType
            int r3 = r3.ordinal()
            r2 = r2[r3]
            switch(r2) {
                case 1: goto L_0x0037;
                case 2: goto L_0x0034;
                case 3: goto L_0x0030;
                case 4: goto L_0x002a;
                case 5: goto L_0x0013;
                case 6: goto L_0x0026;
                case 7: goto L_0x0021;
                case 8: goto L_0x001b;
                default: goto L_0x0013;
            }
        L_0x0013:
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException
            java.lang.String r6 = "Unknown periodicity type."
            r5.<init>(r6)
            throw r5
        L_0x001b:
            int r5 = diffInMonths(r5, r7)
        L_0x001f:
            long r5 = (long) r5
            return r5
        L_0x0021:
            r5 = 604800000(0x240c8400, double:2.988109026E-315)
        L_0x0024:
            long r0 = r0 / r5
            return r0
        L_0x0026:
            r5 = 86400000(0x5265c00, double:4.2687272E-316)
            goto L_0x0024
        L_0x002a:
            int r5 = (int) r0
            r6 = 3600000(0x36ee80, float:5.044674E-39)
            int r5 = r5 / r6
            goto L_0x001f
        L_0x0030:
            r5 = 60000(0xea60, double:2.9644E-319)
            goto L_0x0024
        L_0x0034:
            r5 = 1000(0x3e8, double:4.94E-321)
            goto L_0x0024
        L_0x0037:
            return r0
        L_0x0038:
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "Start cannot come before end"
            r5.<init>(r6)
            goto L_0x0041
        L_0x0040:
            throw r5
        L_0x0041:
            goto L_0x0040
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.rolling.helper.RollingCalendar.periodsElapsed(long, long):long");
    }

    public void printPeriodicity(ContextAwareBase contextAwareBase) {
        String str;
        switch (this.periodicityType) {
            case TOP_OF_MILLISECOND:
                str = "Roll-over every millisecond.";
                break;
            case TOP_OF_SECOND:
                str = "Roll-over every second.";
                break;
            case TOP_OF_MINUTE:
                str = "Roll-over every minute.";
                break;
            case TOP_OF_HOUR:
                str = "Roll-over at the top of every hour.";
                break;
            case HALF_DAY:
                str = "Roll-over at midday and midnight.";
                break;
            case TOP_OF_DAY:
                str = "Roll-over at midnight.";
                break;
            case TOP_OF_WEEK:
                str = "Rollover at the start of week.";
                break;
            case TOP_OF_MONTH:
                str = "Rollover at start of every month.";
                break;
            default:
                str = "Unknown periodicity.";
                break;
        }
        contextAwareBase.addInfo(str);
    }
}
