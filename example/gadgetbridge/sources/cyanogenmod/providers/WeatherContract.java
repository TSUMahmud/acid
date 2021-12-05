package cyanogenmod.providers;

import android.net.Uri;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;

public class WeatherContract {
    public static final String AUTHORITY = "com.cyanogenmod.weather";
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.cyanogenmod.weather");

    public static class WeatherColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(WeatherContract.AUTHORITY_URI, DeviceService.EXTRA_WEATHER);
        public static final Uri CURRENT_AND_FORECAST_WEATHER_URI = Uri.withAppendedPath(CONTENT_URI, "current_and_forecast");
        public static final String CURRENT_CITY = "city";
        public static final String CURRENT_CONDITION = "condition";
        public static final String CURRENT_CONDITION_CODE = "condition_code";
        public static final String CURRENT_HUMIDITY = "humidity";
        public static final String CURRENT_TEMPERATURE = "temperature";
        public static final String CURRENT_TEMPERATURE_UNIT = "temperature_unit";
        public static final String CURRENT_TIMESTAMP = "timestamp";
        public static final Uri CURRENT_WEATHER_URI = Uri.withAppendedPath(CONTENT_URI, "current");
        public static final String CURRENT_WIND_DIRECTION = "wind_direction";
        public static final String CURRENT_WIND_SPEED = "wind_speed";
        public static final String CURRENT_WIND_SPEED_UNIT = "wind_speed_unit";
        public static final String FORECAST_CONDITION = "forecast_condition";
        public static final String FORECAST_CONDITION_CODE = "forecast_condition_code";
        public static final String FORECAST_HIGH = "forecast_high";
        public static final String FORECAST_LOW = "forecast_low";
        public static final Uri FORECAST_WEATHER_URI = Uri.withAppendedPath(CONTENT_URI, "forecast");
        public static final String TODAYS_HIGH_TEMPERATURE = "todays_high";
        public static final String TODAYS_LOW_TEMPERATURE = "todays_low";

        public static final class TempUnit {
            public static final int CELSIUS = 1;
            public static final int FAHRENHEIT = 2;

            private TempUnit() {
            }
        }

        public static final class WindSpeedUnit {
            public static final int KPH = 1;
            public static final int MPH = 2;

            private WindSpeedUnit() {
            }
        }

        public static final class WeatherCode {
            public static final int BLOWING_SNOW = 14;
            public static final int BLUSTERY = 22;
            public static final int CLEAR_NIGHT = 30;
            public static final int CLOUDY = 25;
            public static final int COLD = 24;
            public static final int DRIZZLE = 9;
            public static final int DUST = 18;
            public static final int FAIR_DAY = 33;
            public static final int FAIR_NIGHT = 32;
            public static final int FOGGY = 19;
            public static final int FREEZING_DRIZZLE = 8;
            public static final int FREEZING_RAIN = 10;
            public static final int HAIL = 16;
            public static final int HAZE = 20;
            public static final int HEAVY_SNOW = 39;
            public static final int HOT = 35;
            public static final int HURRICANE = 2;
            public static final int ISOLATED_THUNDERSHOWERS = 44;
            public static final int ISOLATED_THUNDERSTORMS = 36;
            public static final int LIGHT_SNOW_SHOWERS = 13;
            public static final int MIXED_RAIN_AND_HAIL = 34;
            public static final int MIXED_RAIN_AND_SLEET = 6;
            public static final int MIXED_RAIN_AND_SNOW = 5;
            public static final int MIXED_SNOW_AND_SLEET = 7;
            public static final int MOSTLY_CLOUDY_DAY = 27;
            public static final int MOSTLY_CLOUDY_NIGHT = 26;
            public static final int NOT_AVAILABLE = 3200;
            public static final int PARTLY_CLOUDY = 41;
            public static final int PARTLY_CLOUDY_DAY = 29;
            public static final int PARTLY_CLOUDY_NIGHT = 28;
            public static final int SCATTERED_SHOWERS = 38;
            public static final int SCATTERED_SNOW_SHOWERS = 40;
            public static final int SCATTERED_THUNDERSTORMS = 37;
            public static final int SEVERE_THUNDERSTORMS = 3;
            public static final int SHOWERS = 11;
            public static final int SLEET = 17;
            public static final int SMOKY = 21;
            public static final int SNOW = 15;
            public static final int SNOW_FLURRIES = 12;
            public static final int SNOW_SHOWERS = 43;
            public static final int SUNNY = 31;
            public static final int THUNDERSHOWER = 42;
            public static final int THUNDERSTORMS = 4;
            public static final int TORNADO = 0;
            public static final int TROPICAL_STORM = 1;
            public static final int WEATHER_CODE_MAX = 44;
            public static final int WEATHER_CODE_MIN = 0;
            public static final int WINDY = 23;

            private WeatherCode() {
            }
        }
    }
}
