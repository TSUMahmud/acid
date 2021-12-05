package org.apache.commons.lang3.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

public class DateUtils {
    public static final long MILLIS_PER_DAY = 86400000;
    public static final long MILLIS_PER_HOUR = 3600000;
    public static final long MILLIS_PER_MINUTE = 60000;
    public static final long MILLIS_PER_SECOND = 1000;
    public static final int RANGE_MONTH_MONDAY = 6;
    public static final int RANGE_MONTH_SUNDAY = 5;
    public static final int RANGE_WEEK_CENTER = 4;
    public static final int RANGE_WEEK_MONDAY = 2;
    public static final int RANGE_WEEK_RELATIVE = 3;
    public static final int RANGE_WEEK_SUNDAY = 1;
    public static final int SEMI_MONTH = 1001;
    private static final int[][] fields = {new int[]{14}, new int[]{13}, new int[]{12}, new int[]{11, 10}, new int[]{5, 5, 9}, new int[]{2, 1001}, new int[]{1}, new int[]{0}};

    private enum ModifyType {
        TRUNCATE,
        ROUND,
        CEILING
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static boolean isSameInstant(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return date1.getTime() == date2.getTime();
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.getTime().getTime() == cal2.getTime().getTime();
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(14) == cal2.get(14) && cal1.get(13) == cal2.get(13) && cal1.get(12) == cal2.get(12) && cal1.get(11) == cal2.get(11) && cal1.get(6) == cal2.get(6) && cal1.get(1) == cal2.get(1) && cal1.get(0) == cal2.get(0) && cal1.getClass() == cal2.getClass();
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static Date parseDate(String str, String... parsePatterns) throws ParseException {
        return parseDate(str, (Locale) null, parsePatterns);
    }

    public static Date parseDate(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, true);
    }

    public static Date parseDateStrictly(String str, String... parsePatterns) throws ParseException {
        return parseDateStrictly(str, (Locale) null, parsePatterns);
    }

    public static Date parseDateStrictly(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, false);
    }

    private static Date parseDateWithLeniency(String str, Locale locale, String[] parsePatterns, boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        TimeZone tz = TimeZone.getDefault();
        Locale lcl = locale == null ? Locale.getDefault() : locale;
        ParsePosition pos = new ParsePosition(0);
        Calendar calendar = Calendar.getInstance(tz, lcl);
        calendar.setLenient(lenient);
        for (String parsePattern : parsePatterns) {
            FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
            calendar.clear();
            try {
                if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
                    return calendar.getTime();
                }
            } catch (IllegalArgumentException e) {
            }
            pos.setIndex(0);
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }

    private static Date add(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static Date setYears(Date date, int amount) {
        return set(date, 1, amount);
    }

    public static Date setMonths(Date date, int amount) {
        return set(date, 2, amount);
    }

    public static Date setDays(Date date, int amount) {
        return set(date, 5, amount);
    }

    public static Date setHours(Date date, int amount) {
        return set(date, 11, amount);
    }

    public static Date setMinutes(Date date, int amount) {
        return set(date, 12, amount);
    }

    public static Date setSeconds(Date date, int amount) {
        return set(date, 13, amount);
    }

    public static Date setMilliseconds(Date date, int amount) {
        return set(date, 14, amount);
    }

    private static Date set(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }

    public static Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static Calendar toCalendar(Date date, TimeZone tz) {
        Calendar c = Calendar.getInstance(tz);
        c.setTime(date);
        return c;
    }

    public static Date round(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.ROUND);
        return gval.getTime();
    }

    public static Calendar round(Calendar date, int field) {
        if (date != null) {
            Calendar rounded = (Calendar) date.clone();
            modify(rounded, field, ModifyType.ROUND);
            return rounded;
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static Date round(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (date instanceof Date) {
            return round((Date) date, field);
        } else {
            if (date instanceof Calendar) {
                return round((Calendar) date, field).getTime();
            }
            throw new ClassCastException("Could not round " + date);
        }
    }

    public static Date truncate(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.TRUNCATE);
        return gval.getTime();
    }

    public static Calendar truncate(Calendar date, int field) {
        if (date != null) {
            Calendar truncated = (Calendar) date.clone();
            modify(truncated, field, ModifyType.TRUNCATE);
            return truncated;
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static Date truncate(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (date instanceof Date) {
            return truncate((Date) date, field);
        } else {
            if (date instanceof Calendar) {
                return truncate((Calendar) date, field).getTime();
            }
            throw new ClassCastException("Could not truncate " + date);
        }
    }

    public static Date ceiling(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.CEILING);
        return gval.getTime();
    }

    public static Calendar ceiling(Calendar date, int field) {
        if (date != null) {
            Calendar ceiled = (Calendar) date.clone();
            modify(ceiled, field, ModifyType.CEILING);
            return ceiled;
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static Date ceiling(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (date instanceof Date) {
            return ceiling((Date) date, field);
        } else {
            if (date instanceof Calendar) {
                return ceiling((Calendar) date, field).getTime();
            }
            throw new ClassCastException("Could not find ceiling of for type: " + date.getClass());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00e1, code lost:
        r22 = r3;
        r2 = 0;
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00e7, code lost:
        if (r1 == 9) goto L_0x0114;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00eb, code lost:
        if (r1 == 1001) goto L_0x00f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00f4, code lost:
        if (r16[0] != 5) goto L_0x0110;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00f6, code lost:
        r4 = r0.get(5) - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00fe, code lost:
        if (r4 < 15) goto L_0x0104;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0100, code lost:
        r2 = r4 - 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0104, code lost:
        r2 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0106, code lost:
        if (r2 <= 7) goto L_0x010a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0108, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x010a, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x010b, code lost:
        r10 = r4;
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0119, code lost:
        if (r16[0] != 11) goto L_0x012e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x011b, code lost:
        r2 = r0.get(11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0121, code lost:
        if (r2 < 12) goto L_0x0125;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0123, code lost:
        r2 = r2 - 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0126, code lost:
        if (r2 < 6) goto L_0x012a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0128, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x012a, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x012b, code lost:
        r10 = r11;
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0130, code lost:
        if (r3 != false) goto L_0x0152;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0132, code lost:
        r11 = r0.getActualMinimum(r16[0]);
        r4 = r0.getActualMaximum(r16[0]);
        r2 = r0.get(r16[0]) - r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x014c, code lost:
        if (r2 <= ((r4 - r11) / 2)) goto L_0x0150;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x014e, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0150, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0151, code lost:
        r10 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0152, code lost:
        if (r2 == 0) goto L_0x0160;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0154, code lost:
        r0.set(r16[0], r0.get(r16[0]) - r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0160, code lost:
        r15 = r15 + 1;
        r2 = r25;
        r5 = r18;
        r4 = r19;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void modify(java.util.Calendar r23, int r24, org.apache.commons.lang3.time.DateUtils.ModifyType r25) {
        /*
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = 1
            int r4 = r0.get(r3)
            r5 = 280000000(0x10b07600, float:6.960157E-29)
            if (r4 > r5) goto L_0x018b
            r4 = 14
            if (r1 != r4) goto L_0x0015
            return
        L_0x0015:
            java.util.Date r5 = r23.getTime()
            long r6 = r5.getTime()
            r8 = 0
            int r4 = r0.get(r4)
            org.apache.commons.lang3.time.DateUtils$ModifyType r9 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE
            if (r9 == r2) goto L_0x002a
            r9 = 500(0x1f4, float:7.0E-43)
            if (r4 >= r9) goto L_0x002c
        L_0x002a:
            long r9 = (long) r4
            long r6 = r6 - r9
        L_0x002c:
            r9 = 13
            if (r1 != r9) goto L_0x0031
            r8 = 1
        L_0x0031:
            int r9 = r0.get(r9)
            r10 = 30
            if (r8 != 0) goto L_0x0045
            org.apache.commons.lang3.time.DateUtils$ModifyType r11 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE
            if (r11 == r2) goto L_0x003f
            if (r9 >= r10) goto L_0x0045
        L_0x003f:
            long r11 = (long) r9
            r13 = 1000(0x3e8, double:4.94E-321)
            long r11 = r11 * r13
            long r6 = r6 - r11
        L_0x0045:
            r11 = 12
            if (r1 != r11) goto L_0x004a
            r8 = 1
        L_0x004a:
            int r12 = r0.get(r11)
            if (r8 != 0) goto L_0x005d
            org.apache.commons.lang3.time.DateUtils$ModifyType r13 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE
            if (r13 == r2) goto L_0x0056
            if (r12 >= r10) goto L_0x005d
        L_0x0056:
            long r13 = (long) r12
            r15 = 60000(0xea60, double:2.9644E-319)
            long r13 = r13 * r15
            long r6 = r6 - r13
        L_0x005d:
            long r13 = r5.getTime()
            int r10 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x006b
            r5.setTime(r6)
            r0.setTime(r5)
        L_0x006b:
            r10 = 0
            int[][] r13 = fields
            int r14 = r13.length
            r15 = 0
        L_0x0070:
            if (r15 >= r14) goto L_0x016d
            r16 = r13[r15]
            r17 = r16
            r11 = r17
            int r3 = r11.length
            r18 = 0
            r19 = r4
            r4 = r18
        L_0x007f:
            r18 = r5
            r21 = 0
            if (r4 >= r3) goto L_0x00e1
            r5 = r11[r4]
            if (r5 != r1) goto L_0x00d8
            r22 = r3
            org.apache.commons.lang3.time.DateUtils$ModifyType r3 = org.apache.commons.lang3.time.DateUtils.ModifyType.CEILING
            if (r2 == r3) goto L_0x0095
            org.apache.commons.lang3.time.DateUtils$ModifyType r3 = org.apache.commons.lang3.time.DateUtils.ModifyType.ROUND
            if (r2 != r3) goto L_0x00d7
            if (r10 == 0) goto L_0x00d7
        L_0x0095:
            r3 = 1001(0x3e9, float:1.403E-42)
            if (r1 != r3) goto L_0x00b4
            r3 = 5
            int r2 = r0.get(r3)
            r3 = 1
            if (r2 != r3) goto L_0x00a8
            r2 = 15
            r3 = 5
            r0.add(r3, r2)
            goto L_0x00d7
        L_0x00a8:
            r2 = 5
            r3 = -15
            r0.add(r2, r3)
            r2 = 2
            r3 = 1
            r0.add(r2, r3)
            goto L_0x00d7
        L_0x00b4:
            r2 = 9
            if (r1 != r2) goto L_0x00d1
            r2 = 11
            int r3 = r0.get(r2)
            if (r3 != 0) goto L_0x00c6
            r3 = 12
            r0.add(r2, r3)
            goto L_0x00d7
        L_0x00c6:
            r3 = -12
            r0.add(r2, r3)
            r2 = 5
            r3 = 1
            r0.add(r2, r3)
            goto L_0x00d7
        L_0x00d1:
            r3 = 1
            r2 = r16[r21]
            r0.add(r2, r3)
        L_0x00d7:
            return
        L_0x00d8:
            r22 = r3
            int r4 = r4 + 1
            r2 = r25
            r5 = r18
            goto L_0x007f
        L_0x00e1:
            r22 = r3
            r2 = 0
            r3 = 0
            r4 = 9
            if (r1 == r4) goto L_0x0114
            r4 = 1001(0x3e9, float:1.403E-42)
            if (r1 == r4) goto L_0x00f1
            r4 = 12
            r5 = 1
            goto L_0x0130
        L_0x00f1:
            r4 = r16[r21]
            r5 = 5
            if (r4 != r5) goto L_0x0110
            int r4 = r0.get(r5)
            r5 = 1
            int r4 = r4 - r5
            r2 = 15
            if (r4 < r2) goto L_0x0104
            int r4 = r4 + -15
            r2 = r4
            goto L_0x0105
        L_0x0104:
            r2 = r4
        L_0x0105:
            r4 = 7
            if (r2 <= r4) goto L_0x010a
            r4 = 1
            goto L_0x010b
        L_0x010a:
            r4 = 0
        L_0x010b:
            r10 = r4
            r3 = 1
            r4 = 12
            goto L_0x0130
        L_0x0110:
            r5 = 1
            r4 = 12
            goto L_0x0130
        L_0x0114:
            r5 = 1
            r4 = r16[r21]
            r11 = 11
            if (r4 != r11) goto L_0x012e
            int r2 = r0.get(r11)
            r4 = 12
            if (r2 < r4) goto L_0x0125
            int r2 = r2 + -12
        L_0x0125:
            r11 = 6
            if (r2 < r11) goto L_0x012a
            r11 = 1
            goto L_0x012b
        L_0x012a:
            r11 = 0
        L_0x012b:
            r10 = r11
            r3 = 1
            goto L_0x0130
        L_0x012e:
            r4 = 12
        L_0x0130:
            if (r3 != 0) goto L_0x0152
            r11 = r16[r21]
            int r11 = r0.getActualMinimum(r11)
            r4 = r16[r21]
            int r4 = r0.getActualMaximum(r4)
            r5 = r16[r21]
            int r5 = r0.get(r5)
            int r2 = r5 - r11
            int r5 = r4 - r11
            r20 = 2
            int r5 = r5 / 2
            if (r2 <= r5) goto L_0x0150
            r5 = 1
            goto L_0x0151
        L_0x0150:
            r5 = 0
        L_0x0151:
            r10 = r5
        L_0x0152:
            if (r2 == 0) goto L_0x0160
            r4 = r16[r21]
            r5 = r16[r21]
            int r5 = r0.get(r5)
            int r5 = r5 - r2
            r0.set(r4, r5)
        L_0x0160:
            int r15 = r15 + 1
            r2 = r25
            r5 = r18
            r4 = r19
            r3 = 1
            r11 = 12
            goto L_0x0070
        L_0x016d:
            r19 = r4
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "The field "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = " is not supported"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x018b:
            java.lang.ArithmeticException r2 = new java.lang.ArithmeticException
            java.lang.String r3 = "Calendar value too large for accurate calculations"
            r2.<init>(r3)
            goto L_0x0194
        L_0x0193:
            throw r2
        L_0x0194:
            goto L_0x0193
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.modify(java.util.Calendar, int, org.apache.commons.lang3.time.DateUtils$ModifyType):void");
    }

    public static Iterator<Calendar> iterator(Date focus, int rangeStyle) {
        validateDateNotNull(focus);
        Calendar gval = Calendar.getInstance();
        gval.setTime(focus);
        return iterator(gval, rangeStyle);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.util.Calendar} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Iterator<java.util.Calendar> iterator(java.util.Calendar r10, int r11) {
        /*
            if (r10 == 0) goto L_0x0096
            r0 = 0
            r1 = 0
            r2 = 1
            r3 = 7
            r4 = -1
            r5 = 2
            r6 = 5
            r7 = 1
            r8 = 7
            switch(r11) {
                case 1: goto L_0x0041;
                case 2: goto L_0x0041;
                case 3: goto L_0x0041;
                case 4: goto L_0x0041;
                case 5: goto L_0x002a;
                case 6: goto L_0x002a;
                default: goto L_0x000e;
            }
        L_0x000e:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "The range style "
            r5.append(r6)
            r5.append(r11)
            java.lang.String r6 = " is not valid."
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            throw r4
        L_0x002a:
            java.util.Calendar r0 = truncate((java.util.Calendar) r10, (int) r5)
            java.lang.Object r9 = r0.clone()
            r1 = r9
            java.util.Calendar r1 = (java.util.Calendar) r1
            r1.add(r5, r7)
            r1.add(r6, r4)
            r5 = 6
            if (r11 != r5) goto L_0x006c
            r2 = 2
            r3 = 1
            goto L_0x006c
        L_0x0041:
            java.util.Calendar r0 = truncate((java.util.Calendar) r10, (int) r6)
            java.util.Calendar r1 = truncate((java.util.Calendar) r10, (int) r6)
            if (r11 == r7) goto L_0x006b
            if (r11 == r5) goto L_0x0068
            r5 = 3
            if (r11 == r5) goto L_0x0061
            r9 = 4
            if (r11 == r9) goto L_0x0054
            goto L_0x006c
        L_0x0054:
            int r9 = r10.get(r8)
            int r2 = r9 + -3
            int r9 = r10.get(r8)
            int r3 = r9 + 3
            goto L_0x006c
        L_0x0061:
            int r2 = r10.get(r8)
            int r3 = r2 + -1
            goto L_0x006c
        L_0x0068:
            r2 = 2
            r3 = 1
            goto L_0x006c
        L_0x006b:
        L_0x006c:
            if (r2 >= r7) goto L_0x0070
            int r2 = r2 + 7
        L_0x0070:
            if (r2 <= r8) goto L_0x0074
            int r2 = r2 + -7
        L_0x0074:
            if (r3 >= r7) goto L_0x0078
            int r3 = r3 + 7
        L_0x0078:
            if (r3 <= r8) goto L_0x007c
            int r3 = r3 + -7
        L_0x007c:
            int r5 = r0.get(r8)
            if (r5 == r2) goto L_0x0086
            r0.add(r6, r4)
            goto L_0x007c
        L_0x0086:
            int r4 = r1.get(r8)
            if (r4 == r3) goto L_0x0090
            r1.add(r6, r7)
            goto L_0x0086
        L_0x0090:
            org.apache.commons.lang3.time.DateUtils$DateIterator r4 = new org.apache.commons.lang3.time.DateUtils$DateIterator
            r4.<init>(r0, r1)
            return r4
        L_0x0096:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "The date must not be null"
            r0.<init>(r1)
            goto L_0x009f
        L_0x009e:
            throw r0
        L_0x009f:
            goto L_0x009e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.iterator(java.util.Calendar, int):java.util.Iterator");
    }

    public static Iterator<?> iterator(Object focus, int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (focus instanceof Date) {
            return iterator((Date) focus, rangeStyle);
        } else {
            if (focus instanceof Calendar) {
                return iterator((Calendar) focus, rangeStyle);
            }
            throw new ClassCastException("Could not iterate based on " + focus);
        }
    }

    public static long getFragmentInMilliseconds(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.DAYS);
    }

    public static long getFragmentInMilliseconds(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.DAYS);
    }

    private static long getFragment(Date date, int fragment, TimeUnit unit) {
        validateDateNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFragment(calendar, fragment, unit);
    }

    private static long getFragment(Calendar calendar, int fragment, TimeUnit unit) {
        if (calendar != null) {
            long result = 0;
            int offset = unit == TimeUnit.DAYS ? 0 : 1;
            if (fragment == 1) {
                result = 0 + unit.convert((long) (calendar.get(6) - offset), TimeUnit.DAYS);
            } else if (fragment == 2) {
                result = 0 + unit.convert((long) (calendar.get(5) - offset), TimeUnit.DAYS);
            }
            if (fragment == 1 || fragment == 2 || fragment == 5 || fragment == 6) {
                result += unit.convert((long) calendar.get(11), TimeUnit.HOURS);
            } else {
                switch (fragment) {
                    case 11:
                        break;
                    case 12:
                        break;
                    case 13:
                        break;
                    case 14:
                        return result;
                    default:
                        throw new IllegalArgumentException("The fragment " + fragment + " is not supported");
                }
            }
            result += unit.convert((long) calendar.get(12), TimeUnit.MINUTES);
            result += unit.convert((long) calendar.get(13), TimeUnit.SECONDS);
            return result + unit.convert((long) calendar.get(14), TimeUnit.MILLISECONDS);
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static boolean truncatedEquals(Calendar cal1, Calendar cal2, int field) {
        return truncatedCompareTo(cal1, cal2, field) == 0;
    }

    public static boolean truncatedEquals(Date date1, Date date2, int field) {
        return truncatedCompareTo(date1, date2, field) == 0;
    }

    public static int truncatedCompareTo(Calendar cal1, Calendar cal2, int field) {
        return truncate(cal1, field).compareTo(truncate(cal2, field));
    }

    public static int truncatedCompareTo(Date date1, Date date2, int field) {
        return truncate(date1, field).compareTo(truncate(date2, field));
    }

    private static void validateDateNotNull(Date date) {
        Validate.isTrue(date != null, "The date must not be null", new Object[0]);
    }

    static class DateIterator implements Iterator<Calendar> {
        private final Calendar endFinal;
        private final Calendar spot;

        DateIterator(Calendar startFinal, Calendar endFinal2) {
            this.endFinal = endFinal2;
            this.spot = startFinal;
            this.spot.add(5, -1);
        }

        public boolean hasNext() {
            return this.spot.before(this.endFinal);
        }

        public Calendar next() {
            if (!this.spot.equals(this.endFinal)) {
                this.spot.add(5, 1);
                return (Calendar) this.spot.clone();
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
