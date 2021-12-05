package org.apache.commons.lang3.math;

import com.github.mikephil.charting.utils.Utils;
import java.math.BigInteger;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public final class Fraction extends Number implements Comparable<Fraction> {
    public static final Fraction FOUR_FIFTHS = new Fraction(4, 5);
    public static final Fraction ONE = new Fraction(1, 1);
    public static final Fraction ONE_FIFTH = new Fraction(1, 5);
    public static final Fraction ONE_HALF = new Fraction(1, 2);
    public static final Fraction ONE_QUARTER = new Fraction(1, 4);
    public static final Fraction ONE_THIRD = new Fraction(1, 3);
    public static final Fraction THREE_FIFTHS = new Fraction(3, 5);
    public static final Fraction THREE_QUARTERS = new Fraction(3, 4);
    public static final Fraction TWO_FIFTHS = new Fraction(2, 5);
    public static final Fraction TWO_QUARTERS = new Fraction(2, 4);
    public static final Fraction TWO_THIRDS = new Fraction(2, 3);
    public static final Fraction ZERO = new Fraction(0, 1);
    private static final long serialVersionUID = 65382027393090L;
    private final int denominator;
    private transient int hashCode = 0;
    private final int numerator;
    private transient String toProperString = null;
    private transient String toString = null;

    private Fraction(int numerator2, int denominator2) {
        this.numerator = numerator2;
        this.denominator = denominator2;
    }

    public static Fraction getFraction(int numerator2, int denominator2) {
        if (denominator2 != 0) {
            if (denominator2 < 0) {
                if (numerator2 == Integer.MIN_VALUE || denominator2 == Integer.MIN_VALUE) {
                    throw new ArithmeticException("overflow: can't negate");
                }
                numerator2 = -numerator2;
                denominator2 = -denominator2;
            }
            return new Fraction(numerator2, denominator2);
        }
        throw new ArithmeticException("The denominator must not be zero");
    }

    public static Fraction getFraction(int whole, int numerator2, int denominator2) {
        long numeratorValue;
        if (denominator2 == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        } else if (denominator2 < 0) {
            throw new ArithmeticException("The denominator must not be negative");
        } else if (numerator2 >= 0) {
            if (whole < 0) {
                numeratorValue = (((long) whole) * ((long) denominator2)) - ((long) numerator2);
            } else {
                numeratorValue = (((long) whole) * ((long) denominator2)) + ((long) numerator2);
            }
            if (numeratorValue >= -2147483648L && numeratorValue <= 2147483647L) {
                return new Fraction((int) numeratorValue, denominator2);
            }
            throw new ArithmeticException("Numerator too large to represent as an Integer.");
        } else {
            throw new ArithmeticException("The numerator must not be negative");
        }
    }

    public static Fraction getReducedFraction(int numerator2, int denominator2) {
        if (denominator2 == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        } else if (numerator2 == 0) {
            return ZERO;
        } else {
            if (denominator2 == Integer.MIN_VALUE && (numerator2 & 1) == 0) {
                numerator2 /= 2;
                denominator2 /= 2;
            }
            if (denominator2 < 0) {
                if (numerator2 == Integer.MIN_VALUE || denominator2 == Integer.MIN_VALUE) {
                    throw new ArithmeticException("overflow: can't negate");
                }
                numerator2 = -numerator2;
                denominator2 = -denominator2;
            }
            int gcd = greatestCommonDivisor(numerator2, denominator2);
            return new Fraction(numerator2 / gcd, denominator2 / gcd);
        }
    }

    public static Fraction getFraction(double value) {
        int numer0;
        int denom0;
        int i;
        int sign = value < Utils.DOUBLE_EPSILON ? -1 : 1;
        double value2 = Math.abs(value);
        if (value2 > 2.147483647E9d || Double.isNaN(value2)) {
            throw new ArithmeticException("The value must not be greater than Integer.MAX_VALUE or NaN");
        }
        int wholeNumber = (int) value2;
        double d = (double) wholeNumber;
        Double.isNaN(d);
        double value3 = value2 - d;
        int numer02 = 0;
        int denom02 = 1;
        int numer1 = 1;
        int denom1 = 0;
        int numer2 = 0;
        int denom2 = 0;
        int a1 = (int) value3;
        double x1 = 1.0d;
        int sign2 = sign;
        double d2 = (double) a1;
        Double.isNaN(d2);
        double y1 = value3 - d2;
        double delta2 = Double.MAX_VALUE;
        int i2 = 1;
        while (true) {
            double delta1 = delta2;
            int i3 = numer2;
            int i4 = denom2;
            int a2 = (int) (x1 / y1);
            double x2 = y1;
            double d3 = (double) a2;
            Double.isNaN(d3);
            double y2 = x1 - (d3 * y1);
            numer2 = (a1 * numer1) + numer02;
            denom2 = (a1 * denom1) + denom02;
            double d4 = y1;
            double y12 = (double) numer2;
            int i5 = numer02;
            int i6 = denom02;
            double d5 = (double) denom2;
            Double.isNaN(y12);
            Double.isNaN(d5);
            double fraction = y12 / d5;
            delta2 = Math.abs(value3 - fraction);
            a1 = a2;
            x1 = x2;
            double y13 = y2;
            numer0 = numer1;
            denom0 = denom1;
            numer1 = numer2;
            denom1 = denom2;
            double d6 = fraction;
            i = i2 + 1;
            if (delta1 > delta2 && denom2 <= 10000 && denom2 > 0 && i < 25) {
                i2 = i;
                y1 = y13;
                numer02 = numer0;
                denom02 = denom0;
            }
        }
        if (i != 25) {
            int denom03 = denom0;
            int i7 = i;
            return getReducedFraction((numer0 + (wholeNumber * denom03)) * sign2, denom03);
        }
        int denom04 = denom0;
        int denom05 = i;
        int i8 = denom04;
        throw new ArithmeticException("Unable to convert double to fraction");
    }

    public static Fraction getFraction(String str) {
        Validate.isTrue(str != null, "The string must not be null", new Object[0]);
        if (str.indexOf(46) >= 0) {
            return getFraction(Double.parseDouble(str));
        }
        int pos = str.indexOf(32);
        if (pos > 0) {
            int whole = Integer.parseInt(str.substring(0, pos));
            String str2 = str.substring(pos + 1);
            int pos2 = str2.indexOf(47);
            if (pos2 >= 0) {
                return getFraction(whole, Integer.parseInt(str2.substring(0, pos2)), Integer.parseInt(str2.substring(pos2 + 1)));
            }
            throw new NumberFormatException("The fraction could not be parsed as the format X Y/Z");
        }
        int pos3 = str.indexOf(47);
        if (pos3 < 0) {
            return getFraction(Integer.parseInt(str), 1);
        }
        return getFraction(Integer.parseInt(str.substring(0, pos3)), Integer.parseInt(str.substring(pos3 + 1)));
    }

    public int getNumerator() {
        return this.numerator;
    }

    public int getDenominator() {
        return this.denominator;
    }

    public int getProperNumerator() {
        return Math.abs(this.numerator % this.denominator);
    }

    public int getProperWhole() {
        return this.numerator / this.denominator;
    }

    public int intValue() {
        return this.numerator / this.denominator;
    }

    public long longValue() {
        return ((long) this.numerator) / ((long) this.denominator);
    }

    public float floatValue() {
        return ((float) this.numerator) / ((float) this.denominator);
    }

    public double doubleValue() {
        double d = (double) this.numerator;
        double d2 = (double) this.denominator;
        Double.isNaN(d);
        Double.isNaN(d2);
        return d / d2;
    }

    public Fraction reduce() {
        int i = this.numerator;
        if (i == 0) {
            return equals(ZERO) ? this : ZERO;
        }
        int gcd = greatestCommonDivisor(Math.abs(i), this.denominator);
        if (gcd == 1) {
            return this;
        }
        return getFraction(this.numerator / gcd, this.denominator / gcd);
    }

    public Fraction invert() {
        int i = this.numerator;
        if (i == 0) {
            throw new ArithmeticException("Unable to invert zero.");
        } else if (i == Integer.MIN_VALUE) {
            throw new ArithmeticException("overflow: can't negate numerator");
        } else if (i < 0) {
            return new Fraction(-this.denominator, -i);
        } else {
            return new Fraction(this.denominator, i);
        }
    }

    public Fraction negate() {
        int i = this.numerator;
        if (i != Integer.MIN_VALUE) {
            return new Fraction(-i, this.denominator);
        }
        throw new ArithmeticException("overflow: too large to negate");
    }

    public Fraction abs() {
        if (this.numerator >= 0) {
            return this;
        }
        return negate();
    }

    public Fraction pow(int power) {
        if (power == 1) {
            return this;
        }
        if (power == 0) {
            return ONE;
        }
        if (power >= 0) {
            Fraction f = multiplyBy(this);
            if (power % 2 == 0) {
                return f.pow(power / 2);
            }
            return f.pow(power / 2).multiplyBy(this);
        } else if (power == Integer.MIN_VALUE) {
            return invert().pow(2).pow(-(power / 2));
        } else {
            return invert().pow(-power);
        }
    }

    private static int greatestCommonDivisor(int u, int v) {
        int t;
        if (u == 0 || v == 0) {
            if (u != Integer.MIN_VALUE && v != Integer.MIN_VALUE) {
                return Math.abs(u) + Math.abs(v);
            }
            throw new ArithmeticException("overflow: gcd is 2^31");
        } else if (Math.abs(u) == 1 || Math.abs(v) == 1) {
            return 1;
        } else {
            if (u > 0) {
                u = -u;
            }
            if (v > 0) {
                v = -v;
            }
            int k = 0;
            while ((u & 1) == 0 && (v & 1) == 0 && k < 31) {
                u /= 2;
                v /= 2;
                k++;
            }
            if (k != 31) {
                int t2 = (u & 1) == 1 ? v : -(u / 2);
                while (true) {
                    if ((t2 & 1) == 0) {
                        t = t2 / 2;
                    } else {
                        if (t2 > 0) {
                            u = -t2;
                        } else {
                            v = t2;
                        }
                        t = (v - u) / 2;
                        if (t == 0) {
                            return (-u) * (1 << k);
                        }
                    }
                }
            } else {
                throw new ArithmeticException("overflow: gcd is 2^31");
            }
        }
    }

    private static int mulAndCheck(int x, int y) {
        long m = ((long) x) * ((long) y);
        if (m >= -2147483648L && m <= 2147483647L) {
            return (int) m;
        }
        throw new ArithmeticException("overflow: mul");
    }

    private static int mulPosAndCheck(int x, int y) {
        long m = ((long) x) * ((long) y);
        if (m <= 2147483647L) {
            return (int) m;
        }
        throw new ArithmeticException("overflow: mulPos");
    }

    private static int addAndCheck(int x, int y) {
        long s = ((long) x) + ((long) y);
        if (s >= -2147483648L && s <= 2147483647L) {
            return (int) s;
        }
        throw new ArithmeticException("overflow: add");
    }

    private static int subAndCheck(int x, int y) {
        long s = ((long) x) - ((long) y);
        if (s >= -2147483648L && s <= 2147483647L) {
            return (int) s;
        }
        throw new ArithmeticException("overflow: add");
    }

    public Fraction add(Fraction fraction) {
        return addSub(fraction, true);
    }

    public Fraction subtract(Fraction fraction) {
        return addSub(fraction, false);
    }

    private Fraction addSub(Fraction fraction, boolean isAdd) {
        Validate.isTrue(fraction != null, "The fraction must not be null", new Object[0]);
        if (this.numerator == 0) {
            return isAdd ? fraction : fraction.negate();
        }
        if (fraction.numerator == 0) {
            return this;
        }
        int d1 = greatestCommonDivisor(this.denominator, fraction.denominator);
        if (d1 == 1) {
            int uvp = mulAndCheck(this.numerator, fraction.denominator);
            int upv = mulAndCheck(fraction.numerator, this.denominator);
            return new Fraction(isAdd ? addAndCheck(uvp, upv) : subAndCheck(uvp, upv), mulPosAndCheck(this.denominator, fraction.denominator));
        }
        BigInteger uvp2 = BigInteger.valueOf((long) this.numerator).multiply(BigInteger.valueOf((long) (fraction.denominator / d1)));
        BigInteger upv2 = BigInteger.valueOf((long) fraction.numerator).multiply(BigInteger.valueOf((long) (this.denominator / d1)));
        BigInteger t = isAdd ? uvp2.add(upv2) : uvp2.subtract(upv2);
        int tmodd1 = t.mod(BigInteger.valueOf((long) d1)).intValue();
        int d2 = tmodd1 == 0 ? d1 : greatestCommonDivisor(tmodd1, d1);
        BigInteger w = t.divide(BigInteger.valueOf((long) d2));
        if (w.bitLength() <= 31) {
            return new Fraction(w.intValue(), mulPosAndCheck(this.denominator / d1, fraction.denominator / d2));
        }
        throw new ArithmeticException("overflow: numerator too large after multiply");
    }

    public Fraction multiplyBy(Fraction fraction) {
        Validate.isTrue(fraction != null, "The fraction must not be null", new Object[0]);
        int i = this.numerator;
        if (i == 0 || fraction.numerator == 0) {
            return ZERO;
        }
        int d1 = greatestCommonDivisor(i, fraction.denominator);
        int d2 = greatestCommonDivisor(fraction.numerator, this.denominator);
        return getReducedFraction(mulAndCheck(this.numerator / d1, fraction.numerator / d2), mulPosAndCheck(this.denominator / d2, fraction.denominator / d1));
    }

    public Fraction divideBy(Fraction fraction) {
        Validate.isTrue(fraction != null, "The fraction must not be null", new Object[0]);
        if (fraction.numerator != 0) {
            return multiplyBy(fraction.invert());
        }
        throw new ArithmeticException("The fraction to divide by must not be zero");
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Fraction)) {
            return false;
        }
        Fraction other = (Fraction) obj;
        if (getNumerator() == other.getNumerator() && getDenominator() == other.getDenominator()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = ((getNumerator() + 629) * 37) + getDenominator();
        }
        return this.hashCode;
    }

    public int compareTo(Fraction other) {
        if (this == other) {
            return 0;
        }
        if (this.numerator == other.numerator && this.denominator == other.denominator) {
            return 0;
        }
        long first = ((long) this.numerator) * ((long) other.denominator);
        long second = ((long) other.numerator) * ((long) this.denominator);
        if (first == second) {
            return 0;
        }
        if (first < second) {
            return -1;
        }
        return 1;
    }

    public String toString() {
        if (this.toString == null) {
            this.toString = getNumerator() + "/" + getDenominator();
        }
        return this.toString;
    }

    public String toProperString() {
        if (this.toProperString == null) {
            int i = this.numerator;
            if (i == 0) {
                this.toProperString = "0";
            } else {
                int i2 = this.denominator;
                if (i == i2) {
                    this.toProperString = MiBandConst.MI_1;
                } else if (i == i2 * -1) {
                    this.toProperString = "-1";
                } else {
                    if (i > 0) {
                        i = -i;
                    }
                    if (i < (-this.denominator)) {
                        int properNumerator = getProperNumerator();
                        if (properNumerator == 0) {
                            this.toProperString = Integer.toString(getProperWhole());
                        } else {
                            this.toProperString = getProperWhole() + StringUtils.SPACE + properNumerator + "/" + getDenominator();
                        }
                    } else {
                        this.toProperString = getNumerator() + "/" + getDenominator();
                    }
                }
            }
        }
        return this.toProperString;
    }
}
