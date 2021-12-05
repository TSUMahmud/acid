package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import p005ch.qos.logback.core.CoreConstants;

public class FastDatePrinter implements DatePrinter, Serializable {
    public static final int FULL = 0;
    public static final int LONG = 1;
    private static final int MAX_DIGITS = 10;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);
    private static final long serialVersionUID = 1;
    private final Locale mLocale;
    private transient int mMaxLengthEstimate;
    private final String mPattern;
    private transient Rule[] mRules;
    private final TimeZone mTimeZone;

    private interface NumberRule extends Rule {
        void appendTo(Appendable appendable, int i) throws IOException;
    }

    private interface Rule {
        void appendTo(Appendable appendable, Calendar calendar) throws IOException;

        int estimateLength();
    }

    protected FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
        this.mPattern = pattern;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private void init() {
        List<Rule> rulesList = parsePattern();
        this.mRules = (Rule[]) rulesList.toArray(new Rule[rulesList.size()]);
        int len = 0;
        int i = this.mRules.length;
        while (true) {
            i--;
            if (i >= 0) {
                len += this.mRules[i].estimateLength();
            } else {
                this.mMaxLengthEstimate = len;
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public List<Rule> parsePattern() {
        String[] weekdays;
        String[] ERAs;
        Rule rule;
        DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
        List<Rule> rules = new ArrayList<>();
        String[] ERAs2 = symbols.getEras();
        String[] months = symbols.getMonths();
        String[] shortMonths = symbols.getShortMonths();
        String[] weekdays2 = symbols.getWeekdays();
        String[] shortWeekdays = symbols.getShortWeekdays();
        String[] AmPmStrings = symbols.getAmPmStrings();
        int length = this.mPattern.length();
        int[] indexRef = new int[1];
        int i = 0;
        while (true) {
            if (i < length) {
                indexRef[0] = i;
                String token = parseToken(this.mPattern, indexRef);
                int i2 = indexRef[0];
                int tokenLen = token.length();
                if (tokenLen == 0) {
                    DateFormatSymbols dateFormatSymbols = symbols;
                    String[] strArr = ERAs2;
                    String[] strArr2 = weekdays2;
                } else {
                    char c = token.charAt(0);
                    DateFormatSymbols symbols2 = symbols;
                    int i3 = 4;
                    if (c != 'y') {
                        if (c != 'z') {
                            switch (c) {
                                case '\'':
                                    weekdays = weekdays2;
                                    String sub = token.substring(1);
                                    if (sub.length() != 1) {
                                        rule = new StringLiteral(sub);
                                        ERAs = ERAs2;
                                        break;
                                    } else {
                                        rule = new CharacterLiteral(sub.charAt(0));
                                        ERAs = ERAs2;
                                        continue;
                                    }
                                case 'K':
                                    weekdays = weekdays2;
                                    rule = selectNumberRule(10, tokenLen);
                                    ERAs = ERAs2;
                                    continue;
                                case 'M':
                                    weekdays = weekdays2;
                                    if (tokenLen < 4) {
                                        if (tokenLen != 3) {
                                            if (tokenLen != 2) {
                                                rule = UnpaddedMonthField.INSTANCE;
                                                ERAs = ERAs2;
                                                break;
                                            } else {
                                                rule = TwoDigitMonthField.INSTANCE;
                                                ERAs = ERAs2;
                                                break;
                                            }
                                        } else {
                                            rule = new TextField(2, shortMonths);
                                            ERAs = ERAs2;
                                            break;
                                        }
                                    } else {
                                        rule = new TextField(2, months);
                                        ERAs = ERAs2;
                                        continue;
                                    }
                                case 'S':
                                    weekdays = weekdays2;
                                    rule = selectNumberRule(14, tokenLen);
                                    ERAs = ERAs2;
                                    continue;
                                case 'a':
                                    weekdays = weekdays2;
                                    rule = new TextField(9, AmPmStrings);
                                    ERAs = ERAs2;
                                    continue;
                                case 'd':
                                    weekdays = weekdays2;
                                    rule = selectNumberRule(5, tokenLen);
                                    ERAs = ERAs2;
                                    continue;
                                case 'h':
                                    weekdays = weekdays2;
                                    rule = new TwelveHourField(selectNumberRule(10, tokenLen));
                                    ERAs = ERAs2;
                                    continue;
                                case 'k':
                                    weekdays = weekdays2;
                                    rule = new TwentyFourHourField(selectNumberRule(11, tokenLen));
                                    ERAs = ERAs2;
                                    continue;
                                case 'm':
                                    weekdays = weekdays2;
                                    rule = selectNumberRule(12, tokenLen);
                                    ERAs = ERAs2;
                                    continue;
                                case 's':
                                    weekdays = weekdays2;
                                    rule = selectNumberRule(13, tokenLen);
                                    ERAs = ERAs2;
                                    continue;
                                case 'u':
                                    weekdays = weekdays2;
                                    rule = new DayInWeekField(selectNumberRule(7, tokenLen));
                                    ERAs = ERAs2;
                                    continue;
                                case 'w':
                                    weekdays = weekdays2;
                                    rule = selectNumberRule(3, tokenLen);
                                    ERAs = ERAs2;
                                    continue;
                                default:
                                    switch (c) {
                                        case 'D':
                                            weekdays = weekdays2;
                                            rule = selectNumberRule(6, tokenLen);
                                            ERAs = ERAs2;
                                            continue;
                                        case 'E':
                                            weekdays = weekdays2;
                                            rule = new TextField(7, tokenLen < 4 ? shortWeekdays : weekdays);
                                            ERAs = ERAs2;
                                            continue;
                                        case 'F':
                                            weekdays = weekdays2;
                                            rule = selectNumberRule(8, tokenLen);
                                            ERAs = ERAs2;
                                            continue;
                                        case 'G':
                                            weekdays = weekdays2;
                                            rule = new TextField(0, ERAs2);
                                            ERAs = ERAs2;
                                            continue;
                                        case 'H':
                                            weekdays = weekdays2;
                                            rule = selectNumberRule(11, tokenLen);
                                            ERAs = ERAs2;
                                            continue;
                                        default:
                                            switch (c) {
                                                case 'W':
                                                    weekdays = weekdays2;
                                                    rule = selectNumberRule(4, tokenLen);
                                                    ERAs = ERAs2;
                                                    continue;
                                                case 'X':
                                                    weekdays = weekdays2;
                                                    rule = Iso8601_Rule.getRule(tokenLen);
                                                    ERAs = ERAs2;
                                                    continue;
                                                case 'Y':
                                                    weekdays = weekdays2;
                                                    ERAs = ERAs2;
                                                    break;
                                                case 'Z':
                                                    weekdays = weekdays2;
                                                    if (tokenLen != 1) {
                                                        if (tokenLen != 2) {
                                                            rule = TimeZoneNumberRule.INSTANCE_COLON;
                                                            ERAs = ERAs2;
                                                            break;
                                                        } else {
                                                            rule = Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES;
                                                            ERAs = ERAs2;
                                                            break;
                                                        }
                                                    } else {
                                                        rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                                                        ERAs = ERAs2;
                                                        continue;
                                                        continue;
                                                        continue;
                                                    }
                                                default:
                                                    StringBuilder sb = new StringBuilder();
                                                    String[] strArr3 = weekdays2;
                                                    sb.append("Illegal pattern component: ");
                                                    sb.append(token);
                                                    throw new IllegalArgumentException(sb.toString());
                                            }
                                    }
                            }
                        } else {
                            weekdays = weekdays2;
                            if (tokenLen >= 4) {
                                ERAs = ERAs2;
                                rule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 1);
                            } else {
                                ERAs = ERAs2;
                                rule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 0);
                            }
                        }
                        rules.add(rule);
                        i = i2 + 1;
                        symbols = symbols2;
                        ERAs2 = ERAs;
                        weekdays2 = weekdays;
                    } else {
                        ERAs = ERAs2;
                        weekdays = weekdays2;
                    }
                    if (tokenLen == 2) {
                        rule = TwoDigitYearField.INSTANCE;
                    } else {
                        if (tokenLen >= 4) {
                            i3 = tokenLen;
                        }
                        rule = selectNumberRule(1, i3);
                    }
                    if (c == 'Y') {
                        rule = new WeekYear((NumberRule) rule);
                    }
                    rules.add(rule);
                    i = i2 + 1;
                    symbols = symbols2;
                    ERAs2 = ERAs;
                    weekdays2 = weekdays;
                }
            } else {
                String[] strArr4 = ERAs2;
                String[] strArr5 = weekdays2;
            }
        }
        return rules;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0068, code lost:
        r2 = r2 - 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String parseToken(java.lang.String r14, int[] r15) {
        /*
            r13 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 0
            r2 = r15[r1]
            int r3 = r14.length()
            char r4 = r14.charAt(r2)
            r5 = 90
            r6 = 65
            if (r4 < r6) goto L_0x0018
            if (r4 <= r5) goto L_0x0020
        L_0x0018:
            r7 = 122(0x7a, float:1.71E-43)
            r8 = 97
            if (r4 < r8) goto L_0x0036
            if (r4 > r7) goto L_0x0036
        L_0x0020:
            r0.append(r4)
        L_0x0023:
            int r5 = r2 + 1
            if (r5 >= r3) goto L_0x0070
            int r5 = r2 + 1
            char r5 = r14.charAt(r5)
            if (r5 != r4) goto L_0x0070
            r0.append(r4)
            int r2 = r2 + 1
            goto L_0x0023
        L_0x0036:
            r9 = 39
            r0.append(r9)
            r10 = 0
        L_0x003c:
            if (r2 >= r3) goto L_0x0070
            char r4 = r14.charAt(r2)
            r11 = 1
            if (r4 != r9) goto L_0x005e
            int r12 = r2 + 1
            if (r12 >= r3) goto L_0x0057
            int r12 = r2 + 1
            char r12 = r14.charAt(r12)
            if (r12 != r9) goto L_0x0057
            int r2 = r2 + 1
            r0.append(r4)
            goto L_0x006e
        L_0x0057:
            if (r10 != 0) goto L_0x005b
            r12 = 1
            goto L_0x005c
        L_0x005b:
            r12 = 0
        L_0x005c:
            r10 = r12
            goto L_0x006e
        L_0x005e:
            if (r10 != 0) goto L_0x006b
            if (r4 < r6) goto L_0x0064
            if (r4 <= r5) goto L_0x0068
        L_0x0064:
            if (r4 < r8) goto L_0x006b
            if (r4 > r7) goto L_0x006b
        L_0x0068:
            int r2 = r2 + -1
            goto L_0x0070
        L_0x006b:
            r0.append(r4)
        L_0x006e:
            int r2 = r2 + r11
            goto L_0x003c
        L_0x0070:
            r15[r1] = r2
            java.lang.String r1 = r0.toString()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDatePrinter.parseToken(java.lang.String, int[]):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public NumberRule selectNumberRule(int field, int padding) {
        if (padding == 1) {
            return new UnpaddedNumberField(field);
        }
        if (padding != 2) {
            return new PaddedNumberField(field, padding);
        }
        return new TwoDigitNumberField(field);
    }

    @Deprecated
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Date) {
            return format((Date) obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, toAppendTo);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), toAppendTo);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        sb.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: package-private */
    public String format(Object obj) {
        if (obj instanceof Date) {
            return format((Date) obj);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        sb.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public String format(long millis) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRulesToString(c);
    }

    private String applyRulesToString(Calendar c) {
        return ((StringBuilder) applyRules(c, new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    private Calendar newCalendar() {
        return Calendar.getInstance(this.mTimeZone, this.mLocale);
    }

    public String format(Date date) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRulesToString(c);
    }

    public String format(Calendar calendar) {
        return ((StringBuilder) format(calendar, new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    public StringBuffer format(long millis, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return (StringBuffer) applyRules(c, buf);
    }

    public StringBuffer format(Date date, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTime(date);
        return (StringBuffer) applyRules(c, buf);
    }

    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        return format(calendar.getTime(), buf);
    }

    public <B extends Appendable> B format(long millis, B buf) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRules(c, buf);
    }

    public <B extends Appendable> B format(Date date, B buf) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRules(c, buf);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: java.util.Calendar} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <B extends java.lang.Appendable> B format(java.util.Calendar r3, B r4) {
        /*
            r2 = this;
            java.util.TimeZone r0 = r3.getTimeZone()
            java.util.TimeZone r1 = r2.mTimeZone
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0018
            java.lang.Object r0 = r3.clone()
            r3 = r0
            java.util.Calendar r3 = (java.util.Calendar) r3
            java.util.TimeZone r0 = r2.mTimeZone
            r3.setTimeZone(r0)
        L_0x0018:
            java.lang.Appendable r0 = r2.applyRules((java.util.Calendar) r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDatePrinter.format(java.util.Calendar, java.lang.Appendable):java.lang.Appendable");
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        return (StringBuffer) applyRules(calendar, buf);
    }

    private <B extends Appendable> B applyRules(Calendar calendar, B buf) {
        try {
            for (Rule rule : this.mRules) {
                rule.appendTo(buf, calendar);
            }
        } catch (IOException ioe) {
            ExceptionUtils.rethrow(ioe);
        }
        return buf;
    }

    public String getPattern() {
        return this.mPattern;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter other = (FastDatePrinter) obj;
        if (!this.mPattern.equals(other.mPattern) || !this.mTimeZone.equals(other.mTimeZone) || !this.mLocale.equals(other.mLocale)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mPattern.hashCode() + ((this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)) * 13);
    }

    public String toString() {
        return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    /* access modifiers changed from: private */
    public static void appendDigits(Appendable buffer, int value) throws IOException {
        buffer.append((char) ((value / 10) + 48));
        buffer.append((char) ((value % 10) + 48));
    }

    /* access modifiers changed from: private */
    public static void appendFullDigits(Appendable buffer, int value, int minFieldWidth) throws IOException {
        if (value < 10000) {
            int nDigits = 4;
            if (value < 1000) {
                nDigits = 4 - 1;
                if (value < 100) {
                    nDigits--;
                    if (value < 10) {
                        nDigits--;
                    }
                }
            }
            for (int i = minFieldWidth - nDigits; i > 0; i--) {
                buffer.append('0');
            }
            if (nDigits != 1) {
                if (nDigits != 2) {
                    if (nDigits != 3) {
                        if (nDigits == 4) {
                            buffer.append((char) ((value / 1000) + 48));
                            value %= 1000;
                        } else {
                            return;
                        }
                    }
                    if (value >= 100) {
                        buffer.append((char) ((value / 100) + 48));
                        value %= 100;
                    } else {
                        buffer.append('0');
                    }
                }
                if (value >= 10) {
                    buffer.append((char) ((value / 10) + 48));
                    value %= 10;
                } else {
                    buffer.append('0');
                }
            }
            buffer.append((char) (value + 48));
            return;
        }
        char[] work = new char[10];
        int digit = 0;
        while (value != 0) {
            work[digit] = (char) ((value % 10) + 48);
            value /= 10;
            digit++;
        }
        while (digit < minFieldWidth) {
            buffer.append('0');
            minFieldWidth--;
        }
        while (true) {
            digit--;
            if (digit >= 0) {
                buffer.append(work[digit]);
            } else {
                return;
            }
        }
    }

    private static class CharacterLiteral implements Rule {
        private final char mValue;

        CharacterLiteral(char value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return 1;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    private static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return this.mValue.length();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    private static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int field, String[] values) {
            this.mField = field;
            this.mValues = values;
        }

        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (true) {
                i--;
                if (i < 0) {
                    return max;
                }
                int len = this.mValues[i].length();
                if (len > max) {
                    max = len;
                }
            }
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    private static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        UnpaddedNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 4;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 1);
            }
        }
    }

    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else {
                FastDatePrinter.appendDigits(buffer, value);
            }
        }
    }

    private static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size >= 3) {
                this.mField = field;
                this.mSize = size;
                return;
            }
            throw new IllegalArgumentException();
        }

        public int estimateLength() {
            return this.mSize;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendFullDigits(buffer, value, this.mSize);
        }
    }

    private static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 2);
            }
        }
    }

    private static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(1) % 100);
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    private static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    private static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class DayInWeekField implements NumberRule {
        private final NumberRule mRule;

        DayInWeekField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int i = 7;
            int value = calendar.get(7);
            NumberRule numberRule = this.mRule;
            if (value != 1) {
                i = value - 1;
            }
            numberRule.appendTo(buffer, i);
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class WeekYear implements NumberRule {
        private final NumberRule mRule;

        WeekYear(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            this.mRule.appendTo(buffer, calendar.getWeekYear());
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        String value = (String) cTimeZoneDisplayCache.get(key);
        if (value != null) {
            return value;
        }
        String value2 = tz.getDisplayName(daylight, style, locale);
        String prior = cTimeZoneDisplayCache.putIfAbsent(key, value2);
        if (prior != null) {
            return prior;
        }
        return value2;
    }

    private static class TimeZoneNameRule implements Rule {
        private final String mDaylight;
        private final Locale mLocale;
        private final String mStandard;
        private final int mStyle;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
            this.mLocale = locale;
            this.mStyle = style;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
        }

        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            TimeZone zone = calendar.getTimeZone();
            if (calendar.get(16) != 0) {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
            } else {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
            }
        }
    }

    private static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        TimeZoneNumberRule(boolean colon) {
            this.mColon = colon;
        }

        public int estimateLength() {
            return 5;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append(CoreConstants.DASH_CHAR);
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / CoreConstants.MILLIS_IN_ONE_HOUR;
            FastDatePrinter.appendDigits(buffer, hours);
            if (this.mColon) {
                buffer.append(CoreConstants.COLON_CHAR);
            }
            FastDatePrinter.appendDigits(buffer, (offset / CoreConstants.MILLIS_IN_ONE_MINUTE) - (hours * 60));
        }
    }

    private static class Iso8601_Rule implements Rule {
        static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
        static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);
        static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
        final int length;

        static Iso8601_Rule getRule(int tokenLen) {
            if (tokenLen == 1) {
                return ISO8601_HOURS;
            }
            if (tokenLen == 2) {
                return ISO8601_HOURS_MINUTES;
            }
            if (tokenLen == 3) {
                return ISO8601_HOURS_COLON_MINUTES;
            }
            throw new IllegalArgumentException("invalid number of X");
        }

        Iso8601_Rule(int length2) {
            this.length = length2;
        }

        public int estimateLength() {
            return this.length;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset == 0) {
                buffer.append("Z");
                return;
            }
            if (offset < 0) {
                buffer.append(CoreConstants.DASH_CHAR);
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / CoreConstants.MILLIS_IN_ONE_HOUR;
            FastDatePrinter.appendDigits(buffer, hours);
            int i = this.length;
            if (i >= 5) {
                if (i == 6) {
                    buffer.append(CoreConstants.COLON_CHAR);
                }
                FastDatePrinter.appendDigits(buffer, (offset / CoreConstants.MILLIS_IN_ONE_MINUTE) - (hours * 60));
            }
        }
    }

    private static class TimeZoneDisplayKey {
        private final Locale mLocale;
        private final int mStyle;
        private final TimeZone mTimeZone;

        TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                this.mStyle = Integer.MIN_VALUE | style;
            } else {
                this.mStyle = style;
            }
            this.mLocale = locale;
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
            if (!this.mTimeZone.equals(other.mTimeZone) || this.mStyle != other.mStyle || !this.mLocale.equals(other.mLocale)) {
                return false;
            }
            return true;
        }
    }
}
