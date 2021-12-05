package org.apache.commons.lang3.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import p005ch.qos.logback.core.rolling.helper.DateTokenConverter;

public class DurationFormatUtils {

    /* renamed from: H */
    static final Object f214H = "H";
    public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.SSS'S'";

    /* renamed from: M */
    static final Object f215M = "M";

    /* renamed from: S */
    static final Object f216S = "S";

    /* renamed from: d */
    static final Object f217d = DateTokenConverter.CONVERTER_KEY;

    /* renamed from: m */
    static final Object f218m = "m";

    /* renamed from: s */
    static final Object f219s = "s";

    /* renamed from: y */
    static final Object f220y = "y";

    public static String formatDurationHMS(long durationMillis) {
        return formatDuration(durationMillis, "HH:mm:ss.SSS");
    }

    public static String formatDurationISO(long durationMillis) {
        return formatDuration(durationMillis, ISO_EXTENDED_FORMAT_PATTERN, false);
    }

    public static String formatDuration(long durationMillis, String format) {
        return formatDuration(durationMillis, format, true);
    }

    public static String formatDuration(long durationMillis, String format, boolean padWithZeros) {
        long milliseconds;
        long seconds;
        Validate.inclusiveBetween(0, Long.MAX_VALUE, durationMillis, "durationMillis must not be negative");
        Token[] tokens = lexx(format);
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long milliseconds2 = durationMillis;
        if (Token.containsTokenWithValue(tokens, f217d)) {
            days = milliseconds2 / DateUtils.MILLIS_PER_DAY;
            milliseconds2 -= DateUtils.MILLIS_PER_DAY * days;
        }
        if (Token.containsTokenWithValue(tokens, f214H)) {
            hours = milliseconds2 / DateUtils.MILLIS_PER_HOUR;
            milliseconds2 -= DateUtils.MILLIS_PER_HOUR * hours;
        }
        if (Token.containsTokenWithValue(tokens, f218m)) {
            minutes = milliseconds2 / 60000;
            milliseconds2 -= 60000 * minutes;
        }
        if (Token.containsTokenWithValue(tokens, f219s)) {
            long seconds2 = milliseconds2 / 1000;
            seconds = seconds2;
            milliseconds = milliseconds2 - (1000 * seconds2);
        } else {
            seconds = 0;
            milliseconds = milliseconds2;
        }
        return format(tokens, 0, 0, days, hours, minutes, seconds, milliseconds, padWithZeros);
    }

    public static String formatDurationWords(long durationMillis, boolean suppressLeadingZeroElements, boolean suppressTrailingZeroElements) {
        String duration = formatDuration(durationMillis, "d' days 'H' hours 'm' minutes 's' seconds'");
        if (suppressLeadingZeroElements) {
            duration = StringUtils.SPACE + duration;
            String tmp = StringUtils.replaceOnce(duration, " 0 days", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                String tmp2 = StringUtils.replaceOnce(duration, " 0 hours", "");
                if (tmp2.length() != duration.length()) {
                    String tmp3 = StringUtils.replaceOnce(tmp2, " 0 minutes", "");
                    duration = tmp3;
                    if (tmp3.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp3, " 0 seconds", "");
                    }
                }
            }
            if (duration.length() != 0) {
                duration = duration.substring(1);
            }
        }
        if (suppressTrailingZeroElements) {
            String tmp4 = StringUtils.replaceOnce(duration, " 0 seconds", "");
            if (tmp4.length() != duration.length()) {
                duration = tmp4;
                String tmp5 = StringUtils.replaceOnce(duration, " 0 minutes", "");
                if (tmp5.length() != duration.length()) {
                    duration = tmp5;
                    String tmp6 = StringUtils.replaceOnce(duration, " 0 hours", "");
                    if (tmp6.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp6, " 0 days", "");
                    }
                }
            }
        }
        return StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.SPACE + duration, " 1 seconds", " 1 second"), " 1 minutes", " 1 minute"), " 1 hours", " 1 hour"), " 1 days", " 1 day").trim();
    }

    public static String formatPeriodISO(long startMillis, long endMillis) {
        return formatPeriod(startMillis, endMillis, ISO_EXTENDED_FORMAT_PATTERN, false, TimeZone.getDefault());
    }

    public static String formatPeriod(long startMillis, long endMillis, String format) {
        return formatPeriod(startMillis, endMillis, format, true, TimeZone.getDefault());
    }

    public static String formatPeriod(long startMillis, long endMillis, String format, boolean padWithZeros, TimeZone timezone) {
        int months;
        int months2;
        int hours;
        int seconds;
        int days;
        int days2;
        long j = startMillis;
        long j2 = endMillis;
        Validate.isTrue(j <= j2, "startMillis must not be greater than endMillis", new Object[0]);
        Token[] tokens = lexx(format);
        Calendar start = Calendar.getInstance(timezone);
        start.setTime(new Date(j));
        Calendar end = Calendar.getInstance(timezone);
        end.setTime(new Date(j2));
        int milliseconds = end.get(14) - start.get(14);
        int seconds2 = end.get(13) - start.get(13);
        int minutes = end.get(12) - start.get(12);
        int hours2 = end.get(11) - start.get(11);
        int days3 = end.get(5) - start.get(5);
        int i = 2;
        int months3 = end.get(2) - start.get(2);
        int years = end.get(1) - start.get(1);
        while (milliseconds < 0) {
            milliseconds += 1000;
            seconds2--;
        }
        while (seconds2 < 0) {
            seconds2 += 60;
            minutes--;
        }
        while (minutes < 0) {
            minutes += 60;
            hours2--;
        }
        while (hours2 < 0) {
            hours2 += 24;
            days3--;
        }
        if (Token.containsTokenWithValue(tokens, f215M)) {
            while (days3 < 0) {
                days3 += start.getActualMaximum(5);
                months3--;
                start.add(2, 1);
            }
            while (months3 < 0) {
                months3 += 12;
                years--;
            }
            if (Token.containsTokenWithValue(tokens, f220y) || years == 0) {
                months2 = months3;
                months = years;
            } else {
                while (years != 0) {
                    months3 += years * 12;
                    years = 0;
                }
                months2 = months3;
                months = years;
            }
        } else {
            if (!Token.containsTokenWithValue(tokens, f220y)) {
                int i2 = 1;
                int target = end.get(1);
                if (months3 < 0) {
                    days = days3;
                    days2 = target - 1;
                } else {
                    days = days3;
                    days2 = target;
                }
                while (start.get(i2) != days2) {
                    int days4 = days + (start.getActualMaximum(6) - start.get(6));
                    if ((start instanceof GregorianCalendar) && start.get(i) == 1 && start.get(5) == 29) {
                        days4++;
                    }
                    start.add(1, 1);
                    days = days4 + start.get(6);
                    i2 = 1;
                    i = 2;
                }
                years = 0;
                days3 = days;
            }
            while (start.get(2) != end.get(2)) {
                days3 += start.getActualMaximum(5);
                start.add(2, 1);
            }
            int months4 = 0;
            while (days3 < 0) {
                days3 += start.getActualMaximum(5);
                months4--;
                start.add(2, 1);
            }
            months2 = months4;
            months = years;
        }
        if (!Token.containsTokenWithValue(tokens, f217d)) {
            hours2 += days3 * 24;
            days3 = 0;
        }
        if (!Token.containsTokenWithValue(tokens, f214H)) {
            minutes += hours2 * 60;
            hours = 0;
        } else {
            hours = hours2;
        }
        if (!Token.containsTokenWithValue(tokens, f218m)) {
            seconds2 += minutes * 60;
            minutes = 0;
        }
        if (!Token.containsTokenWithValue(tokens, f219s)) {
            milliseconds += seconds2 * 1000;
            seconds = 0;
        } else {
            seconds = seconds2;
        }
        int i3 = months2;
        Calendar calendar = start;
        long j3 = (long) minutes;
        int i4 = milliseconds;
        int i5 = minutes;
        int i6 = seconds;
        int i7 = days3;
        int i8 = hours;
        int i9 = months;
        return format(tokens, (long) months, (long) months2, (long) days3, (long) hours, j3, (long) seconds, (long) milliseconds, padWithZeros);
    }

    static String format(Token[] tokens, long years, long months, long days, long hours, long minutes, long seconds, long milliseconds, boolean padWithZeros) {
        int len$;
        Token[] arr$;
        boolean z;
        long j = milliseconds;
        boolean z2 = padWithZeros;
        StringBuilder buffer = new StringBuilder();
        boolean lastOutputSeconds = false;
        Token[] arr$2 = tokens;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            Token token = arr$2[i$];
            Object value = token.getValue();
            int count = token.getCount();
            if (value instanceof StringBuilder) {
                buffer.append(value.toString());
                long j2 = years;
                long j3 = months;
                z = lastOutputSeconds;
                arr$ = arr$2;
                len$ = len$2;
                long j4 = seconds;
            } else {
                if (value.equals(f220y)) {
                    buffer.append(paddedValue(years, z2, count));
                    lastOutputSeconds = false;
                    long j5 = months;
                    arr$ = arr$2;
                    len$ = len$2;
                    long j6 = seconds;
                } else {
                    long j7 = years;
                    if (value.equals(f215M)) {
                        buffer.append(paddedValue(months, z2, count));
                        lastOutputSeconds = false;
                        arr$ = arr$2;
                        len$ = len$2;
                        long j8 = seconds;
                    } else {
                        long j9 = months;
                        if (value.equals(f217d)) {
                            arr$ = arr$2;
                            len$ = len$2;
                            Token token2 = token;
                            buffer.append(paddedValue(days, z2, count));
                            lastOutputSeconds = false;
                            long j10 = seconds;
                        } else {
                            arr$ = arr$2;
                            len$ = len$2;
                            Token token3 = token;
                            long j11 = days;
                            if (value.equals(f214H)) {
                                buffer.append(paddedValue(hours, z2, count));
                                lastOutputSeconds = false;
                                long j12 = seconds;
                            } else {
                                long j13 = hours;
                                if (value.equals(f218m)) {
                                    buffer.append(paddedValue(minutes, z2, count));
                                    lastOutputSeconds = false;
                                    long j14 = seconds;
                                } else {
                                    long j15 = minutes;
                                    if (value.equals(f219s)) {
                                        buffer.append(paddedValue(seconds, z2, count));
                                        lastOutputSeconds = true;
                                    } else {
                                        long j16 = seconds;
                                        if (value.equals(f216S)) {
                                            if (lastOutputSeconds) {
                                                int width = 3;
                                                if (z2) {
                                                    width = Math.max(3, count);
                                                }
                                                boolean z3 = lastOutputSeconds;
                                                buffer.append(paddedValue(j, true, width));
                                            } else {
                                                buffer.append(paddedValue(j, z2, count));
                                            }
                                            lastOutputSeconds = false;
                                        } else {
                                            z = lastOutputSeconds;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                i$++;
                arr$2 = arr$;
                len$2 = len$;
            }
            lastOutputSeconds = z;
            i$++;
            arr$2 = arr$;
            len$2 = len$;
        }
        boolean z4 = lastOutputSeconds;
        return buffer.toString();
    }

    private static String paddedValue(long value, boolean padWithZeros, int count) {
        String longString = Long.toString(value);
        return padWithZeros ? StringUtils.leftPad(longString, count, '0') : longString;
    }

    static Token[] lexx(String format) {
        ArrayList<Token> list = new ArrayList<>(format.length());
        boolean inLiteral = false;
        StringBuilder buffer = null;
        Token previous = null;
        for (int i = 0; i < format.length(); i++) {
            char ch = format.charAt(i);
            if (!inLiteral || ch == '\'') {
                Object value = null;
                if (ch != '\'') {
                    if (ch == 'H') {
                        value = f214H;
                    } else if (ch == 'M') {
                        value = f215M;
                    } else if (ch == 'S') {
                        value = f216S;
                    } else if (ch == 'd') {
                        value = f217d;
                    } else if (ch == 'm') {
                        value = f218m;
                    } else if (ch == 's') {
                        value = f219s;
                    } else if (ch != 'y') {
                        if (buffer == null) {
                            buffer = new StringBuilder();
                            list.add(new Token(buffer));
                        }
                        buffer.append(ch);
                    } else {
                        value = f220y;
                    }
                } else if (inLiteral) {
                    buffer = null;
                    inLiteral = false;
                } else {
                    buffer = new StringBuilder();
                    list.add(new Token(buffer));
                    inLiteral = true;
                }
                if (value != null) {
                    if (previous == null || !previous.getValue().equals(value)) {
                        Token token = new Token(value);
                        list.add(token);
                        previous = token;
                    } else {
                        previous.increment();
                    }
                    buffer = null;
                }
            } else {
                buffer.append(ch);
            }
        }
        if (!inLiteral) {
            return (Token[]) list.toArray(new Token[list.size()]);
        }
        throw new IllegalArgumentException("Unmatched quote in format: " + format);
    }

    static class Token {
        private int count;
        private final Object value;

        static boolean containsTokenWithValue(Token[] tokens, Object value2) {
            for (Token token : tokens) {
                if (token.getValue() == value2) {
                    return true;
                }
            }
            return false;
        }

        Token(Object value2) {
            this.value = value2;
            this.count = 1;
        }

        Token(Object value2, int count2) {
            this.value = value2;
            this.count = count2;
        }

        /* access modifiers changed from: package-private */
        public void increment() {
            this.count++;
        }

        /* access modifiers changed from: package-private */
        public int getCount() {
            return this.count;
        }

        /* access modifiers changed from: package-private */
        public Object getValue() {
            return this.value;
        }

        public boolean equals(Object obj2) {
            if (!(obj2 instanceof Token)) {
                return false;
            }
            Token tok2 = (Token) obj2;
            if (this.value.getClass() != tok2.value.getClass() || this.count != tok2.count) {
                return false;
            }
            Object obj = this.value;
            if (obj instanceof StringBuilder) {
                return obj.toString().equals(tok2.value.toString());
            }
            if (obj instanceof Number) {
                return obj.equals(tok2.value);
            }
            if (obj == tok2.value) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return StringUtils.repeat(this.value.toString(), this.count);
        }
    }
}
