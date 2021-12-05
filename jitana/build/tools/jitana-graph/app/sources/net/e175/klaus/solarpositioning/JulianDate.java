package net.e175.klaus.solarpositioning;

import com.github.mikephil.charting.utils.Utils;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class JulianDate {
    private final double deltaT;
    private final double julianDate;

    public JulianDate(GregorianCalendar date) {
        this.julianDate = calcJulianDate(createUtcCalendar(date));
        this.deltaT = Utils.DOUBLE_EPSILON;
    }

    public JulianDate(double fromJulianDate, double deltaT2) {
        this.julianDate = fromJulianDate;
        this.deltaT = deltaT2;
    }

    public JulianDate(GregorianCalendar date, double deltaT2) {
        this.julianDate = calcJulianDate(createUtcCalendar(date));
        this.deltaT = deltaT2;
    }

    static GregorianCalendar createUtcCalendar(GregorianCalendar fromCalendar) {
        GregorianCalendar utcCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        utcCalendar.setTimeInMillis(fromCalendar.getTimeInMillis());
        utcCalendar.set(0, fromCalendar.get(0));
        return utcCalendar;
    }

    private double calcJulianDate(GregorianCalendar calendar) {
        int y;
        if (calendar.get(0) == 1) {
            y = calendar.get(1);
        } else {
            y = -calendar.get(1);
        }
        int m = calendar.get(2) + 1;
        if (m < 3) {
            y--;
            m += 12;
        }
        double d = (double) calendar.get(5);
        double d2 = (double) calendar.get(11);
        double d3 = (double) calendar.get(12);
        double d4 = (double) calendar.get(13);
        Double.isNaN(d4);
        Double.isNaN(d3);
        Double.isNaN(d2);
        Double.isNaN(d);
        double d5 = d + ((d2 + ((d3 + (d4 / 60.0d)) / 60.0d)) / 24.0d);
        double d6 = (double) y;
        Double.isNaN(d6);
        double floor = Math.floor((d6 + 4716.0d) * 365.25d);
        double d7 = (double) (m + 1);
        Double.isNaN(d7);
        double jd = ((floor + Math.floor(d7 * 30.6001d)) + d5) - 1524.5d;
        double d8 = (double) y;
        Double.isNaN(d8);
        double a = Math.floor(d8 / 100.0d);
        return jd + (jd > 2299160.0d ? (2.0d - a) + Math.floor(a / 4.0d) : Utils.DOUBLE_EPSILON);
    }

    public double getJulianDate() {
        return this.julianDate;
    }

    public double getJulianEphemerisDay() {
        return this.julianDate + (this.deltaT / 86400.0d);
    }

    public double getJulianCentury() {
        return (this.julianDate - 2451545.0d) / 36525.0d;
    }

    public double getJulianEphemerisCentury() {
        return (getJulianEphemerisDay() - 2451545.0d) / 36525.0d;
    }

    public double getJulianEphemerisMillennium() {
        return getJulianEphemerisCentury() / 10.0d;
    }

    public String toString() {
        return String.format("%.5f", new Object[]{Double.valueOf(this.julianDate)});
    }
}
