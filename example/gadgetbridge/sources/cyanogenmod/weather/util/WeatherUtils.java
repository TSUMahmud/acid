package cyanogenmod.weather.util;

import java.text.DecimalFormat;

public class WeatherUtils {
    public static double celsiusToFahrenheit(double celsius) {
        return (1.8d * celsius) + 32.0d;
    }

    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32.0d) * 0.5555555555555556d;
    }

    public static String formatTemperature(double temperature, int tempUnit) {
        if (!isValidTempUnit(tempUnit)) {
            return null;
        }
        if (Double.isNaN(temperature)) {
            return "-";
        }
        String noDigitsTemp = new DecimalFormat("0").format(temperature);
        if (noDigitsTemp.equals("-0")) {
            noDigitsTemp = "0";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(noDigitsTemp);
        StringBuilder formatted = sb.append("°");
        if (tempUnit == 1) {
            formatted.append("C");
        } else if (tempUnit == 2) {
            formatted.append("F");
        }
        return formatted.toString();
    }

    private static boolean isValidTempUnit(int unit) {
        if (unit == 1 || unit == 2) {
            return true;
        }
        return false;
    }
}
