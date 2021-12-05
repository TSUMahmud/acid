package net.e175.klaus.solarpositioning;

import java.util.GregorianCalendar;

public final class DeltaT {
    private DeltaT() {
    }

    public static double estimate(GregorianCalendar forDate) {
        double year = decimalYear(forDate);
        if (year < -500.0d) {
            return (Math.pow((year - 1820.0d) / 100.0d, 2.0d) * 32.0d) - 0.21875d;
        }
        if (year < 500.0d) {
            double u = year / 100.0d;
            return ((((10583.6d - (1014.41d * u)) + (Math.pow(u, 2.0d) * 33.78311d)) - (Math.pow(u, 3.0d) * 5.952053d)) - (Math.pow(u, 4.0d) * 0.1798452d)) + (Math.pow(u, 5.0d) * 0.022174192d) + (Math.pow(u, 6.0d) * 0.0090316521d);
        } else if (year < 1600.0d) {
            double u2 = (year - 1000.0d) / 100.0d;
            return (((((1574.2d - (556.01d * u2)) + (Math.pow(u2, 2.0d) * 71.23472d)) + (Math.pow(u2, 3.0d) * 0.319781d)) - (Math.pow(u2, 4.0d) * 0.8503463d)) - (Math.pow(u2, 5.0d) * 0.005050998d)) + (Math.pow(u2, 6.0d) * 0.0083572073d);
        } else if (year < 1700.0d) {
            double t = year - 1600.0d;
            return ((120.0d - (0.9808d * t)) - (Math.pow(t, 2.0d) * 0.01532d)) + (Math.pow(t, 3.0d) / 7129.0d);
        } else if (year < 1800.0d) {
            double t2 = year - 1700.0d;
            return ((((0.1603d * t2) + 8.83d) - (Math.pow(t2, 2.0d) * 0.0059285d)) + (Math.pow(t2, 3.0d) * 1.3336E-4d)) - (Math.pow(t2, 4.0d) / 1174000.0d);
        } else if (year < 1860.0d) {
            double t3 = year - 1800.0d;
            return ((((((13.72d - (0.332447d * t3)) + (Math.pow(t3, 2.0d) * 0.0068612d)) + (Math.pow(t3, 3.0d) * 0.0041116d)) - (Math.pow(t3, 4.0d) * 3.7436E-4d)) + (Math.pow(t3, 5.0d) * 1.21272E-5d)) - (Math.pow(t3, 6.0d) * 1.699E-7d)) + (Math.pow(t3, 7.0d) * 8.75E-10d);
        } else if (year < 1900.0d) {
            double t4 = year - 1860.0d;
            return (((((0.5737d * t4) + 7.62d) - (Math.pow(t4, 2.0d) * 0.251754d)) + (Math.pow(t4, 3.0d) * 0.01680668d)) - (Math.pow(t4, 4.0d) * 4.473624E-4d)) + (Math.pow(t4, 5.0d) / 233174.0d);
        } else if (year < 1920.0d) {
            double t5 = year - 1900.0d;
            return ((((1.494119d * t5) - 1.605d) - (Math.pow(t5, 2.0d) * 0.0598939d)) + (Math.pow(t5, 3.0d) * 0.0061966d)) - (Math.pow(t5, 4.0d) * 1.97E-4d);
        } else if (year < 1941.0d) {
            double t6 = year - 1920.0d;
            return (((0.84493d * t6) + 21.2d) - (Math.pow(t6, 2.0d) * 0.0761d)) + (Math.pow(t6, 3.0d) * 0.0020936d);
        } else if (year < 1961.0d) {
            double t7 = year - 1950.0d;
            return (((0.407d * t7) + 29.07d) - (Math.pow(t7, 2.0d) / 233.0d)) + (Math.pow(t7, 3.0d) / 2547.0d);
        } else if (year < 1986.0d) {
            double t8 = year - 1975.0d;
            return (((1.067d * t8) + 45.45d) - (Math.pow(t8, 2.0d) / 260.0d)) - (Math.pow(t8, 3.0d) / 718.0d);
        } else if (year < 2005.0d) {
            double t9 = year - 2000.0d;
            return (((0.3345d * t9) + 63.86d) - (Math.pow(t9, 2.0d) * 0.060374d)) + (Math.pow(t9, 3.0d) * 0.0017275d) + (Math.pow(t9, 4.0d) * 6.51814E-4d) + (Math.pow(t9, 5.0d) * 2.373599E-5d);
        } else if (year < 2050.0d) {
            double t10 = year - 2000.0d;
            return (Math.pow(t10, 2.0d) * 0.005589d) + (0.32217d * t10) + 62.92d;
        } else if (year < 2150.0d) {
            return ((Math.pow((year - 1820.0d) / 100.0d, 2.0d) * 32.0d) - 0.21875d) - ((2150.0d - year) * 0.5628d);
        } else {
            return (Math.pow((year - 1820.0d) / 100.0d, 2.0d) * 32.0d) - 0.21875d;
        }
    }

    private static double decimalYear(GregorianCalendar forDate) {
        double rawYear = (double) forDate.get(1);
        if (forDate.get(0) == 0) {
            Double.isNaN(rawYear);
            rawYear = -rawYear;
        }
        double d = (double) (forDate.get(2) + 1);
        Double.isNaN(d);
        return ((d - 0.5d) / 12.0d) + rawYear;
    }
}
