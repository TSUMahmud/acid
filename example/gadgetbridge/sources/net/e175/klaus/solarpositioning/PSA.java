package net.e175.klaus.solarpositioning;

import com.github.mikephil.charting.utils.Utils;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.commons.lang3.time.TimeZones;

public final class PSA {
    private static final double D_ASTRONOMICAL_UNIT = 1.4959789E8d;
    private static final double D_EARTH_MEAN_RADIUS = 6371.01d;

    /* renamed from: PI */
    private static final double f114PI = 3.141592653589793d;
    private static final double RAD = 0.017453292519943295d;
    private static final double TWOPI = 6.283185307179586d;

    private PSA() {
    }

    public static AzimuthZenithAngle calculateSolarPosition(GregorianCalendar date, double latitude, double longitude) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone(TimeZones.GMT_ID));
        gregorianCalendar.setTimeInMillis(date.getTimeInMillis());
        double d = (double) gregorianCalendar.get(11);
        double d2 = (double) gregorianCalendar.get(12);
        double d3 = (double) gregorianCalendar.get(13);
        Double.isNaN(d3);
        Double.isNaN(d2);
        Double.isNaN(d);
        double dDecimalHours = d + ((d2 + (d3 / 60.0d)) / 60.0d);
        long liAux1 = (long) (((gregorianCalendar.get(2) + 1) - 14) / 12);
        double d4 = (double) (((((((((long) (gregorianCalendar.get(1) + 4800)) + liAux1) * 1461) / 4) + (((((long) ((gregorianCalendar.get(2) + 1) - 2)) - (liAux1 * 12)) * 367) / 12)) - ((((((long) (gregorianCalendar.get(1) + 4900)) + liAux1) / 100) * 3) / 4)) + ((long) gregorianCalendar.get(5))) - 32075);
        Double.isNaN(d4);
        double dJulianDate = ((d4 - 0.5d) + (dDecimalHours / 24.0d)) - 2451545.0d;
        double dOmega = 2.1429d - (0.0010394594d * dJulianDate);
        double dMeanAnomaly = (0.0172019699d * dJulianDate) + 6.24006d;
        double dEclipticLongitude = ((((Math.sin(dMeanAnomaly) * 0.03341607d) + ((0.017202791698d * dJulianDate) + 4.895063d)) + (Math.sin(2.0d * dMeanAnomaly) * 3.4894E-4d)) - 1.134E-4d) - (Math.sin(dOmega) * 2.03E-5d);
        double dEclipticObliquity = (0.4090928d - (6.214E-9d * dJulianDate)) + (Math.cos(dOmega) * 3.96E-5d);
        double dMeanLongitude = Math.sin(dEclipticLongitude);
        double dRightAscension = Math.atan2(Math.cos(dEclipticObliquity) * dMeanLongitude, Math.cos(dEclipticLongitude));
        if (dRightAscension < Utils.DOUBLE_EPSILON) {
            dRightAscension += TWOPI;
        }
        double dSinEclipticLongitude = Math.asin(Math.sin(dEclipticObliquity) * dMeanLongitude);
        double dHourAngle = (((15.0d * (((0.0657098283d * dJulianDate) + 6.6974243242d) + dDecimalHours)) + longitude) * 0.017453292519943295d) - dRightAscension;
        double dLatitudeInRadians = latitude * 0.017453292519943295d;
        double dCosLatitude = Math.cos(dLatitudeInRadians);
        double dSinLatitude = Math.sin(dLatitudeInRadians);
        double dCosHourAngle = Math.cos(dHourAngle);
        double zenithAngle = Math.acos((dCosLatitude * dCosHourAngle * Math.cos(dSinEclipticLongitude)) + (Math.sin(dSinEclipticLongitude) * dSinLatitude));
        GregorianCalendar gregorianCalendar2 = gregorianCalendar;
        double d5 = dDecimalHours;
        double dY = -Math.sin(dHourAngle);
        double azimuth = Math.atan2(dY, (Math.tan(dSinEclipticLongitude) * dCosLatitude) - (dSinLatitude * dCosHourAngle));
        if (azimuth < Utils.DOUBLE_EPSILON) {
            azimuth += TWOPI;
        }
        double d6 = dY;
        return new AzimuthZenithAngle(azimuth / 0.017453292519943295d, (zenithAngle + (Math.sin(zenithAngle) * 4.2587565907513806E-5d)) / 0.017453292519943295d);
    }
}
