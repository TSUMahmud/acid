package net.e175.klaus.solarpositioning;

import com.github.mikephil.charting.utils.Utils;
import java.util.GregorianCalendar;

public final class Grena3 {
    private Grena3() {
    }

    public static AzimuthZenithAngle calculateSolarPosition(GregorianCalendar date, double latitude, double longitude, double deltaT) {
        return calculateSolarPosition(date, latitude, longitude, deltaT, Double.MIN_VALUE, Double.MIN_VALUE);
    }

    public static AzimuthZenithAngle calculateSolarPosition(GregorianCalendar date, double latitude, double longitude, double deltaT, double pressure, double temperature) {
        double t = calcT(date);
        double tE = (1.1574E-5d * deltaT) + t;
        double omegaAtE = 0.0172019715d * tE;
        double lambda = ((0.01720279216d * tE) - 3.222394d) + (Math.sin(omegaAtE - 0.06172d) * 0.033366d) + (Math.sin((2.0d * omegaAtE) - 0.1163d) * 3.53E-4d);
        double sLambda = Math.sin(lambda);
        double cLambda = Math.cos(lambda);
        double sEpsilon = Math.sin(0.4089567d - (6.19E-9d * tE));
        double d = tE;
        double alpha = Math.atan2(sLambda * Math.sqrt(1.0d - (sEpsilon * sEpsilon)), cLambda);
        double d2 = Utils.DOUBLE_EPSILON;
        if (alpha < Utils.DOUBLE_EPSILON) {
            alpha += 6.283185307179586d;
        }
        double delta = Math.asin(sLambda * sEpsilon);
        double H = ((((((6.300388099d * t) + 1.7528311d) + Math.toRadians(longitude)) - alpha) + 3.141592653589793d) % 6.283185307179586d) - 3.141592653589793d;
        if (H < -3.141592653589793d) {
            H += 6.283185307179586d;
        }
        double sPhi = Math.sin(Math.toRadians(latitude));
        double cPhi = Math.sqrt(1.0d - (sPhi * sPhi));
        double sDelta = Math.sin(delta);
        double cDelta = Math.sqrt(1.0d - (sDelta * sDelta));
        double d3 = t;
        double t2 = Math.sin(H);
        double cH = Math.cos(H);
        double sEpsilon0 = (sPhi * sDelta) + (cPhi * cDelta * cH);
        double eP = Math.asin(sEpsilon0) - (Math.sqrt(1.0d - (sEpsilon0 * sEpsilon0)) * 4.26E-5d);
        double d4 = alpha;
        double gamma = Math.atan2(t2, (cH * sPhi) - ((sDelta * cPhi) / cDelta));
        if (temperature >= -273.0d && temperature <= 273.0d && pressure >= Utils.DOUBLE_EPSILON && pressure <= 3000.0d && eP > Utils.DOUBLE_EPSILON) {
            d2 = ((pressure / 1000.0d) * 0.08422d) / ((temperature + 273.0d) * Math.tan(eP + (0.003138d / (eP + 0.08919d))));
        }
        double deltaRe = d2;
        double d5 = t2;
        double d6 = gamma;
        double d7 = omegaAtE;
        return new AzimuthZenithAngle(Math.toDegrees(gamma + 3.141592653589793d) % 360.0d, Math.toDegrees((1.5707963267948966d - eP) - deltaRe));
    }

    private static double calcT(GregorianCalendar date) {
        GregorianCalendar utc = JulianDate.createUtcCalendar(date);
        int m = utc.get(2) + 1;
        int y = utc.get(1);
        int d = utc.get(5);
        double d2 = (double) utc.get(11);
        double d3 = (double) utc.get(12);
        Double.isNaN(d3);
        Double.isNaN(d2);
        double d4 = d2 + (d3 / 60.0d);
        double d5 = (double) utc.get(13);
        Double.isNaN(d5);
        double h = d4 + (d5 / 3600.0d);
        if (m <= 2) {
            m += 12;
            y--;
        }
        double d6 = (double) (y - 2000);
        Double.isNaN(d6);
        int i = (int) (d6 * 365.25d);
        double d7 = (double) (m + 1);
        Double.isNaN(d7);
        double d8 = (double) y;
        Double.isNaN(d8);
        double d9 = (double) (((i + ((int) (d7 * 30.6001d))) - ((int) (d8 * 0.01d))) + d);
        Double.isNaN(d9);
        return (d9 + (0.0416667d * h)) - 21958.0d;
    }
}
