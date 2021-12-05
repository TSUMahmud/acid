package org.apache.commons.lang3.math;

import com.github.mikephil.charting.utils.Utils;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class NumberUtils {
    public static final Byte BYTE_MINUS_ONE = (byte) -1;
    public static final Byte BYTE_ONE = (byte) 1;
    public static final Byte BYTE_ZERO = (byte) 0;
    public static final Double DOUBLE_MINUS_ONE = Double.valueOf(-1.0d);
    public static final Double DOUBLE_ONE = Double.valueOf(1.0d);
    public static final Double DOUBLE_ZERO = Double.valueOf(Utils.DOUBLE_EPSILON);
    public static final Float FLOAT_MINUS_ONE = Float.valueOf(-1.0f);
    public static final Float FLOAT_ONE = Float.valueOf(1.0f);
    public static final Float FLOAT_ZERO = Float.valueOf(0.0f);
    public static final Integer INTEGER_MINUS_ONE = -1;
    public static final Integer INTEGER_ONE = 1;
    public static final Integer INTEGER_ZERO = 0;
    public static final Long LONG_MINUS_ONE = -1L;
    public static final Long LONG_ONE = 1L;
    public static final Long LONG_ZERO = 0L;
    public static final Short SHORT_MINUS_ONE = -1;
    public static final Short SHORT_ONE = 1;
    public static final Short SHORT_ZERO = 0;

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long toLong(String str) {
        return toLong(str, 0);
    }

    public static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }

    public static float toFloat(String str, float defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double toDouble(String str) {
        return toDouble(str, Utils.DOUBLE_EPSILON);
    }

    public static double toDouble(String str, double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static byte toByte(String str) {
        return toByte(str, (byte) 0);
    }

    public static byte toByte(String str, byte defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static short toShort(String str) {
        return toShort(str, 0);
    }

    public static short toShort(String str, short defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0143, code lost:
        if (r5 == 'l') goto L_0x0145;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Number createNumber(java.lang.String r18) throws java.lang.NumberFormatException {
        /*
            r1 = r18
            if (r1 != 0) goto L_0x0006
            r0 = 0
            return r0
        L_0x0006:
            boolean r0 = org.apache.commons.lang3.StringUtils.isBlank(r18)
            if (r0 != 0) goto L_0x025b
            java.lang.String r2 = "0x"
            java.lang.String r3 = "0X"
            java.lang.String r4 = "-0x"
            java.lang.String r5 = "-0X"
            java.lang.String r6 = "#"
            java.lang.String r7 = "-#"
            java.lang.String[] r0 = new java.lang.String[]{r2, r3, r4, r5, r6, r7}
            r2 = r0
            r0 = 0
            r3 = r2
            int r4 = r3.length
            r5 = 0
        L_0x0021:
            if (r5 >= r4) goto L_0x0035
            r6 = r3[r5]
            boolean r7 = r1.startsWith(r6)
            if (r7 == 0) goto L_0x0032
            int r7 = r6.length()
            int r0 = r0 + r7
            r3 = r0
            goto L_0x0036
        L_0x0032:
            int r5 = r5 + 1
            goto L_0x0021
        L_0x0035:
            r3 = r0
        L_0x0036:
            if (r3 <= 0) goto L_0x0075
            r0 = 0
            r4 = r3
        L_0x003a:
            int r5 = r18.length()
            if (r4 >= r5) goto L_0x004d
            char r0 = r1.charAt(r4)
            r5 = 48
            if (r0 != r5) goto L_0x004d
            int r3 = r3 + 1
            int r4 = r4 + 1
            goto L_0x003a
        L_0x004d:
            int r4 = r18.length()
            int r4 = r4 - r3
            r5 = 16
            if (r4 > r5) goto L_0x0070
            r6 = 55
            if (r4 != r5) goto L_0x005d
            if (r0 <= r6) goto L_0x005d
            goto L_0x0070
        L_0x005d:
            r5 = 8
            if (r4 > r5) goto L_0x006b
            if (r4 != r5) goto L_0x0066
            if (r0 <= r6) goto L_0x0066
            goto L_0x006b
        L_0x0066:
            java.lang.Integer r5 = createInteger(r18)
            return r5
        L_0x006b:
            java.lang.Long r5 = createLong(r18)
            return r5
        L_0x0070:
            java.math.BigInteger r5 = createBigInteger(r18)
            return r5
        L_0x0075:
            int r0 = r18.length()
            r4 = 1
            int r0 = r0 - r4
            char r5 = r1.charAt(r0)
            r0 = 46
            int r6 = r1.indexOf(r0)
            r7 = 101(0x65, float:1.42E-43)
            int r7 = r1.indexOf(r7)
            r8 = 69
            int r8 = r1.indexOf(r8)
            int r7 = r7 + r8
            int r7 = r7 + r4
            java.lang.String r8 = " is not a valid number."
            r9 = -1
            if (r6 <= r9) goto L_0x00c9
            if (r7 <= r9) goto L_0x00be
            if (r7 < r6) goto L_0x00a9
            int r10 = r18.length()
            if (r7 > r10) goto L_0x00a9
            int r10 = r6 + 1
            java.lang.String r10 = r1.substring(r10, r7)
            goto L_0x00c4
        L_0x00a9:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            r4.append(r8)
            java.lang.String r4 = r4.toString()
            r0.<init>(r4)
            throw r0
        L_0x00be:
            int r10 = r6 + 1
            java.lang.String r10 = r1.substring(r10)
        L_0x00c4:
            java.lang.String r11 = getMantissa(r1, r6)
            goto L_0x00f2
        L_0x00c9:
            if (r7 <= r9) goto L_0x00ec
            int r10 = r18.length()
            if (r7 > r10) goto L_0x00d7
            java.lang.String r10 = getMantissa(r1, r7)
            r11 = r10
            goto L_0x00f1
        L_0x00d7:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            r4.append(r8)
            java.lang.String r4 = r4.toString()
            r0.<init>(r4)
            throw r0
        L_0x00ec:
            java.lang.String r10 = getMantissa(r18)
            r11 = r10
        L_0x00f1:
            r10 = 0
        L_0x00f2:
            boolean r12 = java.lang.Character.isDigit(r5)
            r13 = 0
            r14 = 0
            if (r12 != 0) goto L_0x01cd
            if (r5 == r0) goto L_0x01cd
            if (r7 <= r9) goto L_0x0112
            int r0 = r18.length()
            int r0 = r0 - r4
            if (r7 >= r0) goto L_0x0112
            int r0 = r7 + 1
            int r9 = r18.length()
            int r9 = r9 - r4
            java.lang.String r0 = r1.substring(r0, r9)
            r9 = r0
            goto L_0x0114
        L_0x0112:
            r0 = 0
            r9 = r0
        L_0x0114:
            int r0 = r18.length()
            int r0 = r0 - r4
            java.lang.String r12 = r1.substring(r14, r0)
            boolean r0 = isAllZeros(r11)
            if (r0 == 0) goto L_0x012b
            boolean r0 = isAllZeros(r9)
            if (r0 == 0) goto L_0x012b
            r0 = 1
            goto L_0x012c
        L_0x012b:
            r0 = 0
        L_0x012c:
            r15 = r0
            r0 = 68
            if (r5 == r0) goto L_0x0198
            r0 = 70
            if (r5 == r0) goto L_0x0181
            r0 = 76
            if (r5 == r0) goto L_0x0145
            r0 = 100
            if (r5 == r0) goto L_0x0198
            r0 = 102(0x66, float:1.43E-43)
            if (r5 == r0) goto L_0x0181
            r0 = 108(0x6c, float:1.51E-43)
            if (r5 != r0) goto L_0x01b8
        L_0x0145:
            if (r10 != 0) goto L_0x016c
            if (r9 != 0) goto L_0x016c
            char r0 = r12.charAt(r14)
            r13 = 45
            if (r0 != r13) goto L_0x015b
            java.lang.String r0 = r12.substring(r4)
            boolean r0 = isDigits(r0)
            if (r0 != 0) goto L_0x0161
        L_0x015b:
            boolean r0 = isDigits(r12)
            if (r0 == 0) goto L_0x016c
        L_0x0161:
            java.lang.Long r0 = createLong(r12)     // Catch:{ NumberFormatException -> 0x0166 }
            return r0
        L_0x0166:
            r0 = move-exception
            java.math.BigInteger r0 = createBigInteger(r12)
            return r0
        L_0x016c:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            r4.append(r8)
            java.lang.String r4 = r4.toString()
            r0.<init>(r4)
            throw r0
        L_0x0181:
            java.lang.Float r0 = createFloat(r18)     // Catch:{ NumberFormatException -> 0x0197 }
            boolean r4 = r0.isInfinite()     // Catch:{ NumberFormatException -> 0x0197 }
            if (r4 != 0) goto L_0x0196
            float r4 = r0.floatValue()     // Catch:{ NumberFormatException -> 0x0197 }
            int r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r4 != 0) goto L_0x0195
            if (r15 == 0) goto L_0x0196
        L_0x0195:
            return r0
        L_0x0196:
            goto L_0x0198
        L_0x0197:
            r0 = move-exception
        L_0x0198:
            java.lang.Double r0 = createDouble(r18)     // Catch:{ NumberFormatException -> 0x01b1 }
            boolean r4 = r0.isInfinite()     // Catch:{ NumberFormatException -> 0x01b1 }
            if (r4 != 0) goto L_0x01b0
            float r4 = r0.floatValue()     // Catch:{ NumberFormatException -> 0x01b1 }
            double r13 = (double) r4
            r16 = 0
            int r4 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x01af
            if (r15 == 0) goto L_0x01b0
        L_0x01af:
            return r0
        L_0x01b0:
            goto L_0x01b2
        L_0x01b1:
            r0 = move-exception
        L_0x01b2:
            java.math.BigDecimal r0 = createBigDecimal(r12)     // Catch:{ NumberFormatException -> 0x01b7 }
            return r0
        L_0x01b7:
            r0 = move-exception
        L_0x01b8:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            r4.append(r8)
            java.lang.String r4 = r4.toString()
            r0.<init>(r4)
            throw r0
        L_0x01cd:
            if (r7 <= r9) goto L_0x01e2
            int r0 = r18.length()
            int r0 = r0 - r4
            if (r7 >= r0) goto L_0x01e2
            int r0 = r7 + 1
            int r8 = r18.length()
            java.lang.String r0 = r1.substring(r0, r8)
            r8 = r0
            goto L_0x01e4
        L_0x01e2:
            r0 = 0
            r8 = r0
        L_0x01e4:
            if (r10 != 0) goto L_0x01f9
            if (r8 != 0) goto L_0x01f9
            java.lang.Integer r0 = createInteger(r18)     // Catch:{ NumberFormatException -> 0x01ed }
            return r0
        L_0x01ed:
            r0 = move-exception
            java.lang.Long r0 = createLong(r18)     // Catch:{ NumberFormatException -> 0x01f3 }
            return r0
        L_0x01f3:
            r0 = move-exception
            java.math.BigInteger r0 = createBigInteger(r18)
            return r0
        L_0x01f9:
            boolean r0 = isAllZeros(r11)
            if (r0 == 0) goto L_0x0206
            boolean r0 = isAllZeros(r8)
            if (r0 == 0) goto L_0x0206
            goto L_0x0207
        L_0x0206:
            r4 = 0
        L_0x0207:
            java.lang.Float r0 = createFloat(r18)     // Catch:{ NumberFormatException -> 0x0255 }
            java.lang.Double r9 = createDouble(r18)     // Catch:{ NumberFormatException -> 0x0255 }
            boolean r12 = r0.isInfinite()     // Catch:{ NumberFormatException -> 0x0255 }
            if (r12 != 0) goto L_0x022e
            float r12 = r0.floatValue()     // Catch:{ NumberFormatException -> 0x0255 }
            int r12 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
            if (r12 != 0) goto L_0x021f
            if (r4 == 0) goto L_0x022e
        L_0x021f:
            java.lang.String r12 = r0.toString()     // Catch:{ NumberFormatException -> 0x0255 }
            java.lang.String r13 = r9.toString()     // Catch:{ NumberFormatException -> 0x0255 }
            boolean r12 = r12.equals(r13)     // Catch:{ NumberFormatException -> 0x0255 }
            if (r12 == 0) goto L_0x022e
            return r0
        L_0x022e:
            boolean r12 = r9.isInfinite()     // Catch:{ NumberFormatException -> 0x0255 }
            if (r12 != 0) goto L_0x0254
            double r12 = r9.doubleValue()     // Catch:{ NumberFormatException -> 0x0255 }
            r14 = 0
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x0240
            if (r4 == 0) goto L_0x0254
        L_0x0240:
            java.math.BigDecimal r12 = createBigDecimal(r18)     // Catch:{ NumberFormatException -> 0x0255 }
            double r13 = r9.doubleValue()     // Catch:{ NumberFormatException -> 0x0255 }
            java.math.BigDecimal r13 = java.math.BigDecimal.valueOf(r13)     // Catch:{ NumberFormatException -> 0x0255 }
            int r13 = r12.compareTo(r13)     // Catch:{ NumberFormatException -> 0x0255 }
            if (r13 != 0) goto L_0x0253
            return r9
        L_0x0253:
            return r12
        L_0x0254:
            goto L_0x0256
        L_0x0255:
            r0 = move-exception
        L_0x0256:
            java.math.BigDecimal r0 = createBigDecimal(r18)
            return r0
        L_0x025b:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.String r2 = "A blank string is not a valid number"
            r0.<init>(r2)
            goto L_0x0264
        L_0x0263:
            throw r0
        L_0x0264:
            goto L_0x0263
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.createNumber(java.lang.String):java.lang.Number");
    }

    private static String getMantissa(String str) {
        return getMantissa(str, str.length());
    }

    private static String getMantissa(String str, int stopPos) {
        char firstChar = str.charAt(0);
        return firstChar == '-' || firstChar == '+' ? str.substring(1, stopPos) : str.substring(0, stopPos);
    }

    private static boolean isAllZeros(String str) {
        if (str == null) {
            return true;
        }
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) != '0') {
                return false;
            }
        }
        if (str.length() > 0) {
            return true;
        }
        return false;
    }

    public static Float createFloat(String str) {
        if (str == null) {
            return null;
        }
        return Float.valueOf(str);
    }

    public static Double createDouble(String str) {
        if (str == null) {
            return null;
        }
        return Double.valueOf(str);
    }

    public static Integer createInteger(String str) {
        if (str == null) {
            return null;
        }
        return Integer.decode(str);
    }

    public static Long createLong(String str) {
        if (str == null) {
            return null;
        }
        return Long.decode(str);
    }

    public static BigInteger createBigInteger(String str) {
        if (str == null) {
            return null;
        }
        int pos = 0;
        int radix = 10;
        boolean negate = false;
        if (str.startsWith("-")) {
            negate = true;
            pos = 1;
        }
        if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) {
            radix = 16;
            pos += 2;
        } else if (str.startsWith("#", pos)) {
            radix = 16;
            pos++;
        } else if (str.startsWith("0", pos) && str.length() > pos + 1) {
            radix = 8;
            pos++;
        }
        BigInteger value = new BigInteger(str.substring(pos), radix);
        return negate ? value.negate() : value;
    }

    public static BigDecimal createBigDecimal(String str) {
        if (str == null) {
            return null;
        }
        if (StringUtils.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        } else if (!str.trim().startsWith("--")) {
            return new BigDecimal(str);
        } else {
            throw new NumberFormatException(str + " is not a valid number.");
        }
    }

    public static long min(long... array) {
        validateArray(array);
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static int min(int... array) {
        validateArray(array);
        int min = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] < min) {
                min = array[j];
            }
        }
        return min;
    }

    public static short min(short... array) {
        validateArray(array);
        short min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static byte min(byte... array) {
        validateArray(array);
        byte min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static double min(double... array) {
        validateArray(array);
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (Double.isNaN(array[i])) {
                return Double.NaN;
            }
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static float min(float... array) {
        validateArray(array);
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (Float.isNaN(array[i])) {
                return Float.NaN;
            }
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static long max(long... array) {
        validateArray(array);
        long max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static int max(int... array) {
        validateArray(array);
        int max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static short max(short... array) {
        validateArray(array);
        short max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static byte max(byte... array) {
        validateArray(array);
        byte max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static double max(double... array) {
        validateArray(array);
        double max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (Double.isNaN(array[j])) {
                return Double.NaN;
            }
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static float max(float... array) {
        validateArray(array);
        float max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (Float.isNaN(array[j])) {
                return Float.NaN;
            }
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    private static void validateArray(Object array) {
        boolean z = true;
        Validate.isTrue(array != null, "The Array must not be null", new Object[0]);
        if (Array.getLength(array) == 0) {
            z = false;
        }
        Validate.isTrue(z, "Array cannot be empty.", new Object[0]);
    }

    public static long min(long a, long b, long c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static short min(short a, short b, short c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static byte min(byte a, byte b, byte c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }

    public static long max(long a, long b, long c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static int max(int a, int b, int c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static short max(short a, short b, short c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static byte max(byte a, byte b, byte c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    public static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }

    public static boolean isDigits(String str) {
        return StringUtils.isNumeric(str);
    }

    @Deprecated
    public static boolean isNumber(String str) {
        return isCreatable(str);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0106, code lost:
        if (r3 != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x0108, code lost:
        if (r4 != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x010a, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x010c, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x010d, code lost:
        if (r5 != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x010f, code lost:
        if (r6 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0111, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00b1, code lost:
        if (r9 >= r0.length) goto L_0x010d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00b5, code lost:
        if (r0[r9] < r8) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00bb, code lost:
        if (r0[r9] > '9') goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00bf, code lost:
        if (org.apache.commons.lang3.SystemUtils.IS_JAVA_1_6 == false) goto L_0x00c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00c1, code lost:
        if (r11 == false) goto L_0x00c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x00c3, code lost:
        if (r4 != false) goto L_0x00c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x00c5, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x00c6, code lost:
        return r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x00c9, code lost:
        if (r0[r9] == 'e') goto L_0x010c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x00cd, code lost:
        if (r0[r9] != 'E') goto L_0x00d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x00d2, code lost:
        if (r0[r9] != '.') goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x00d4, code lost:
        if (r4 != false) goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x00d6, code lost:
        if (r3 == false) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x00d9, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x00da, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x00db, code lost:
        if (r5 != false) goto L_0x00f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x00e1, code lost:
        if (r0[r9] == 'd') goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x00e7, code lost:
        if (r0[r9] == 'D') goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x00ed, code lost:
        if (r0[r9] == 'f') goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x00f3, code lost:
        if (r0[r9] != 'F') goto L_0x00f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x00f5, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x00fa, code lost:
        if (r0[r9] == 'l') goto L_0x0104;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0100, code lost:
        if (r0[r9] != 'L') goto L_0x0103;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0103, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0104, code lost:
        if (r6 == false) goto L_?;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isCreatable(java.lang.String r17) {
        /*
            boolean r0 = org.apache.commons.lang3.StringUtils.isEmpty(r17)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            char[] r0 = r17.toCharArray()
            int r2 = r0.length
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            char r7 = r0[r1]
            r8 = 45
            r9 = 43
            r10 = 1
            if (r7 == r8) goto L_0x0021
            char r7 = r0[r1]
            if (r7 != r9) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            r7 = 0
            goto L_0x0022
        L_0x0021:
            r7 = 1
        L_0x0022:
            if (r7 != r10) goto L_0x002a
            char r11 = r0[r1]
            if (r11 != r9) goto L_0x002a
            r11 = 1
            goto L_0x002b
        L_0x002a:
            r11 = 0
        L_0x002b:
            int r12 = r7 + 1
            r13 = 70
            r14 = 102(0x66, float:1.43E-43)
            r15 = 57
            r8 = 48
            if (r2 <= r12) goto L_0x0097
            char r12 = r0[r7]
            if (r12 != r8) goto L_0x0097
            int r12 = r7 + 1
            char r12 = r0[r12]
            r9 = 120(0x78, float:1.68E-43)
            if (r12 == r9) goto L_0x006b
            int r9 = r7 + 1
            char r9 = r0[r9]
            r12 = 88
            if (r9 != r12) goto L_0x004c
            goto L_0x006b
        L_0x004c:
            int r9 = r7 + 1
            char r9 = r0[r9]
            boolean r9 = java.lang.Character.isDigit(r9)
            if (r9 == 0) goto L_0x0097
            int r9 = r7 + 1
        L_0x0058:
            int r12 = r0.length
            if (r9 >= r12) goto L_0x006a
            char r12 = r0[r9]
            if (r12 < r8) goto L_0x0069
            char r12 = r0[r9]
            r13 = 55
            if (r12 <= r13) goto L_0x0066
            goto L_0x0069
        L_0x0066:
            int r9 = r9 + 1
            goto L_0x0058
        L_0x0069:
            return r1
        L_0x006a:
            return r10
        L_0x006b:
            int r9 = r7 + 2
            if (r9 != r2) goto L_0x0070
            return r1
        L_0x0070:
            int r12 = r0.length
            if (r9 >= r12) goto L_0x0096
            char r12 = r0[r9]
            if (r12 < r8) goto L_0x007b
            char r12 = r0[r9]
            if (r12 <= r15) goto L_0x0090
        L_0x007b:
            char r12 = r0[r9]
            r15 = 97
            if (r12 < r15) goto L_0x0085
            char r12 = r0[r9]
            if (r12 <= r14) goto L_0x0090
        L_0x0085:
            char r12 = r0[r9]
            r15 = 65
            if (r12 < r15) goto L_0x0095
            char r12 = r0[r9]
            if (r12 <= r13) goto L_0x0090
            goto L_0x0095
        L_0x0090:
            int r9 = r9 + 1
            r15 = 57
            goto L_0x0070
        L_0x0095:
            return r1
        L_0x0096:
            return r10
        L_0x0097:
            int r2 = r2 + -1
            r9 = r7
        L_0x009a:
            r12 = 69
            r15 = 101(0x65, float:1.42E-43)
            r13 = 46
            if (r9 < r2) goto L_0x0113
            int r14 = r2 + 1
            if (r9 >= r14) goto L_0x00b0
            if (r5 == 0) goto L_0x00b0
            if (r6 != 0) goto L_0x00b0
            r14 = 102(0x66, float:1.43E-43)
            r16 = 70
            goto L_0x0115
        L_0x00b0:
            int r14 = r0.length
            if (r9 >= r14) goto L_0x010d
            char r14 = r0[r9]
            if (r14 < r8) goto L_0x00c7
            char r8 = r0[r9]
            r14 = 57
            if (r8 > r14) goto L_0x00c7
            boolean r8 = org.apache.commons.lang3.SystemUtils.IS_JAVA_1_6
            if (r8 == 0) goto L_0x00c6
            if (r11 == 0) goto L_0x00c6
            if (r4 != 0) goto L_0x00c6
            return r1
        L_0x00c6:
            return r10
        L_0x00c7:
            char r8 = r0[r9]
            if (r8 == r15) goto L_0x010c
            char r8 = r0[r9]
            if (r8 != r12) goto L_0x00d0
            goto L_0x010c
        L_0x00d0:
            char r8 = r0[r9]
            if (r8 != r13) goto L_0x00db
            if (r4 != 0) goto L_0x00da
            if (r3 == 0) goto L_0x00d9
            goto L_0x00da
        L_0x00d9:
            return r6
        L_0x00da:
            return r1
        L_0x00db:
            if (r5 != 0) goto L_0x00f6
            char r8 = r0[r9]
            r12 = 100
            if (r8 == r12) goto L_0x00f5
            char r8 = r0[r9]
            r12 = 68
            if (r8 == r12) goto L_0x00f5
            char r8 = r0[r9]
            r14 = 102(0x66, float:1.43E-43)
            if (r8 == r14) goto L_0x00f5
            char r8 = r0[r9]
            r12 = 70
            if (r8 != r12) goto L_0x00f6
        L_0x00f5:
            return r6
        L_0x00f6:
            char r8 = r0[r9]
            r12 = 108(0x6c, float:1.51E-43)
            if (r8 == r12) goto L_0x0104
            char r8 = r0[r9]
            r12 = 76
            if (r8 != r12) goto L_0x0103
            goto L_0x0104
        L_0x0103:
            return r1
        L_0x0104:
            if (r6 == 0) goto L_0x010b
            if (r3 != 0) goto L_0x010b
            if (r4 != 0) goto L_0x010b
            r1 = 1
        L_0x010b:
            return r1
        L_0x010c:
            return r1
        L_0x010d:
            if (r5 != 0) goto L_0x0112
            if (r6 == 0) goto L_0x0112
            r1 = 1
        L_0x0112:
            return r1
        L_0x0113:
            r16 = 70
        L_0x0115:
            char r10 = r0[r9]
            if (r10 < r8) goto L_0x0126
            char r10 = r0[r9]
            r8 = 57
            if (r10 > r8) goto L_0x0128
            r6 = 1
            r5 = 0
            r12 = 43
            r13 = 45
            goto L_0x0167
        L_0x0126:
            r8 = 57
        L_0x0128:
            char r10 = r0[r9]
            if (r10 != r13) goto L_0x0138
            if (r4 != 0) goto L_0x0137
            if (r3 == 0) goto L_0x0131
            goto L_0x0137
        L_0x0131:
            r4 = 1
            r12 = 43
            r13 = 45
            goto L_0x0167
        L_0x0137:
            return r1
        L_0x0138:
            char r10 = r0[r9]
            if (r10 == r15) goto L_0x015b
            char r10 = r0[r9]
            if (r10 != r12) goto L_0x0145
            r12 = 43
            r13 = 45
            goto L_0x015f
        L_0x0145:
            char r10 = r0[r9]
            r12 = 43
            if (r10 == r12) goto L_0x0153
            char r10 = r0[r9]
            r13 = 45
            if (r10 != r13) goto L_0x0152
            goto L_0x0155
        L_0x0152:
            return r1
        L_0x0153:
            r13 = 45
        L_0x0155:
            if (r5 != 0) goto L_0x0158
            return r1
        L_0x0158:
            r5 = 0
            r6 = 0
            goto L_0x0167
        L_0x015b:
            r12 = 43
            r13 = 45
        L_0x015f:
            if (r3 == 0) goto L_0x0162
            return r1
        L_0x0162:
            if (r6 != 0) goto L_0x0165
            return r1
        L_0x0165:
            r3 = 1
            r5 = 1
        L_0x0167:
            int r9 = r9 + 1
            r8 = 48
            r10 = 1
            r13 = 70
            goto L_0x009a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.isCreatable(java.lang.String):boolean");
    }

    public static boolean isParsable(String str) {
        if (StringUtils.isEmpty(str) || str.charAt(str.length() - 1) == '.') {
            return false;
        }
        if (str.charAt(0) != '-') {
            return withDecimalsParsing(str, 0);
        }
        if (str.length() == 1) {
            return false;
        }
        return withDecimalsParsing(str, 1);
    }

    private static boolean withDecimalsParsing(String str, int beginIdx) {
        int decimalPoints = 0;
        for (int i = beginIdx; i < str.length(); i++) {
            boolean isDecimalPoint = str.charAt(i) == '.';
            if (isDecimalPoint) {
                decimalPoints++;
            }
            if (decimalPoints > 1) {
                return false;
            }
            if (!isDecimalPoint && !Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int compare(int x, int y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(long x, long y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(short x, short y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(byte x, byte y) {
        return x - y;
    }
}
