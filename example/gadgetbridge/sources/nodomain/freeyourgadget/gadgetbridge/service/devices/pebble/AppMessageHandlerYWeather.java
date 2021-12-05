package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.json.JSONException;
import org.json.JSONObject;

class AppMessageHandlerYWeather extends AppMessageHandler {
    private static int CLEAR_DAY = 0;
    private static int CLEAR_NIGHT = 1;
    private static int CLOUDY = 14;
    private static int COLD = 3;
    private static int DRIZZLE = 17;
    private static int FOG = 7;
    private static int HOT = 4;

    /* renamed from: NA */
    private static int f193NA = 16;
    private static int PARTLY_CLOUDY_DAY = 5;
    private static int PARTLY_CLOUDY_NIGHT = 6;
    private static int RAIN = 8;
    private static int RAIN_SLEET = 12;
    private static int RAIN_SNOW = 13;
    private static int SLEET = 10;
    private static int SNOW = 9;
    private static int SNOW_SLEET = 11;
    private static int STORM = 15;
    private static int WINDY = 2;
    private Integer KEY_LOCATION_NAME;
    private Integer KEY_WEATHER_D1_ICON;
    private Integer KEY_WEATHER_D1_MAXTEMP;
    private Integer KEY_WEATHER_D1_MINTEMP;
    private Integer KEY_WEATHER_D2_ICON;
    private Integer KEY_WEATHER_D2_MAXTEMP;
    private Integer KEY_WEATHER_D2_MINTEMP;
    private Integer KEY_WEATHER_D3_ICON;
    private Integer KEY_WEATHER_D3_MAXTEMP;
    private Integer KEY_WEATHER_D3_MINTEMP;
    private Integer KEY_WEATHER_ICON;
    private Integer KEY_WEATHER_TEMP;
    private Integer KEY_WEATHER_TODAY_MAXTEMP;
    private Integer KEY_WEATHER_TODAY_MINTEMP;
    private Integer KEY_WEATHER_WIND_DIRECTION;
    private Integer KEY_WEATHER_WIND_SPEED;

    AppMessageHandlerYWeather(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.KEY_LOCATION_NAME = Integer.valueOf(appKeys.getInt("city"));
            this.KEY_WEATHER_TEMP = Integer.valueOf(appKeys.getInt("temperature"));
            this.KEY_WEATHER_ICON = Integer.valueOf(appKeys.getInt("icon"));
            this.KEY_WEATHER_WIND_SPEED = Integer.valueOf(appKeys.getInt("wind"));
            this.KEY_WEATHER_WIND_DIRECTION = Integer.valueOf(appKeys.getInt("wdirection"));
            this.KEY_WEATHER_TODAY_MINTEMP = Integer.valueOf(appKeys.getInt("low"));
            this.KEY_WEATHER_TODAY_MAXTEMP = Integer.valueOf(appKeys.getInt("high"));
            this.KEY_WEATHER_D1_ICON = Integer.valueOf(appKeys.getInt("code1"));
            this.KEY_WEATHER_D1_MINTEMP = Integer.valueOf(appKeys.getInt("low1"));
            this.KEY_WEATHER_D1_MAXTEMP = Integer.valueOf(appKeys.getInt("high1"));
            this.KEY_WEATHER_D2_ICON = Integer.valueOf(appKeys.getInt("code2"));
            this.KEY_WEATHER_D2_MINTEMP = Integer.valueOf(appKeys.getInt("low2"));
            this.KEY_WEATHER_D2_MAXTEMP = Integer.valueOf(appKeys.getInt("high2"));
            this.KEY_WEATHER_D3_ICON = Integer.valueOf(appKeys.getInt("code3"));
            this.KEY_WEATHER_D3_MINTEMP = Integer.valueOf(appKeys.getInt("low3"));
            this.KEY_WEATHER_D3_MAXTEMP = Integer.valueOf(appKeys.getInt("high3"));
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the YWeather watchface configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private int getIconForConditionCode(int conditionCode, boolean isNight) {
        if (conditionCode == 800 || conditionCode == 951) {
            return isNight ? CLEAR_NIGHT : CLEAR_DAY;
        }
        if (conditionCode > 800 && conditionCode < 900) {
            return isNight ? PARTLY_CLOUDY_NIGHT : PARTLY_CLOUDY_DAY;
        }
        if (conditionCode >= 300 && conditionCode < 313) {
            return DRIZZLE;
        }
        if (conditionCode >= 313 && conditionCode < 400) {
            return DRIZZLE;
        }
        if (conditionCode >= 500 && conditionCode < 600) {
            return RAIN;
        }
        if (conditionCode >= 700 && conditionCode < 732) {
            return CLOUDY;
        }
        if (conditionCode == 741 || conditionCode == 751 || conditionCode == 761 || conditionCode == 762) {
            return FOG;
        }
        if (conditionCode == 771) {
            return WINDY;
        }
        if (conditionCode == 781) {
            return STORM;
        }
        if (conditionCode >= 200 && conditionCode < 300) {
            return STORM;
        }
        if (conditionCode == 600 || conditionCode == 601 || conditionCode == 602) {
            return SNOW;
        }
        if (conditionCode == 611 || conditionCode == 612) {
            return SLEET;
        }
        if (conditionCode == 615 || conditionCode == 616 || conditionCode == 620 || conditionCode == 621 || conditionCode == 622) {
            return RAIN_SNOW;
        }
        if (conditionCode == 906) {
            return SLEET;
        }
        if (conditionCode >= 907 && conditionCode < 957) {
            return STORM;
        }
        if (conditionCode == 905) {
            return STORM;
        }
        if (conditionCode == 900) {
            return STORM;
        }
        if (conditionCode == 901 || conditionCode == 902 || conditionCode == 962) {
            return STORM;
        }
        if (conditionCode == 903) {
            return COLD;
        }
        if (conditionCode == 904) {
            return HOT;
        }
        return 0;
    }

    private String formatWindDirection(float wdirection) {
        if (Float.isNaN(wdirection)) {
            return "n/a";
        }
        if (((double) wdirection) >= 348.75d || ((double) wdirection) <= 11.25d) {
            return "N";
        }
        if (((double) wdirection) > 11.25d && ((double) wdirection) <= 33.75d) {
            return "NNE";
        }
        if (((double) wdirection) > 33.75d && ((double) wdirection) <= 56.25d) {
            return "NE";
        }
        if (((double) wdirection) > 56.25d && ((double) wdirection) <= 78.75d) {
            return "ENE";
        }
        if (((double) wdirection) > 78.75d && ((double) wdirection) <= 101.25d) {
            return "E";
        }
        if (((double) wdirection) > 101.25d && ((double) wdirection) <= 123.75d) {
            return "ESE";
        }
        if (((double) wdirection) > 123.75d && ((double) wdirection) <= 146.25d) {
            return "SE";
        }
        if (((double) wdirection) > 146.25d && ((double) wdirection) <= 168.75d) {
            return "SSE";
        }
        if (((double) wdirection) > 168.75d && ((double) wdirection) <= 191.25d) {
            return "S";
        }
        if (((double) wdirection) > 191.25d && ((double) wdirection) <= 213.75d) {
            return "SSW";
        }
        if (((double) wdirection) > 213.75d && ((double) wdirection) <= 236.25d) {
            return "SW";
        }
        if (((double) wdirection) > 236.25d && ((double) wdirection) <= 258.75d) {
            return "WSW";
        }
        if (((double) wdirection) > 258.75d && ((double) wdirection) <= 281.25d) {
            return "W";
        }
        if (((double) wdirection) > 281.25d && ((double) wdirection) <= 303.75d) {
            return "WNW";
        }
        if (((double) wdirection) > 303.75d && ((double) wdirection) <= 326.25d) {
            return "NW";
        }
        if (((double) wdirection) <= 326.25d || ((double) wdirection) > 348.75d) {
            return "n/a";
        }
        return "NNW";
    }

    private byte[] encodeYWeatherMessage(WeatherSpec weatherSpec) {
        WeatherSpec weatherSpec2 = weatherSpec;
        if (weatherSpec2 == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        pairs.add(new Pair(this.KEY_LOCATION_NAME, weatherSpec2.location));
        Integer num = this.KEY_WEATHER_TEMP;
        Locale locale = Locale.ENGLISH;
        double d = (double) weatherSpec2.currentTemp;
        Double.isNaN(d);
        pairs.add(new Pair(num, String.format(locale, "%.0f°", new Object[]{Double.valueOf(d - 273.15d)})));
        Integer num2 = this.KEY_WEATHER_TODAY_MINTEMP;
        Locale locale2 = Locale.ENGLISH;
        double d2 = (double) weatherSpec2.todayMinTemp;
        Double.isNaN(d2);
        pairs.add(new Pair(num2, String.format(locale2, "%.0f°C", new Object[]{Double.valueOf(d2 - 273.15d)})));
        Integer num3 = this.KEY_WEATHER_TODAY_MAXTEMP;
        Locale locale3 = Locale.ENGLISH;
        double d3 = (double) weatherSpec2.todayMaxTemp;
        Double.isNaN(d3);
        pairs.add(new Pair(num3, String.format(locale3, "%.0f°C", new Object[]{Double.valueOf(d3 - 273.15d)})));
        pairs.add(new Pair(this.KEY_WEATHER_ICON, Integer.valueOf(getIconForConditionCode(weatherSpec2.currentConditionCode, false))));
        pairs.add(new Pair(this.KEY_WEATHER_WIND_SPEED, String.format(Locale.ENGLISH, "%.0f", new Object[]{Float.valueOf(weatherSpec2.windSpeed)})));
        pairs.add(new Pair(this.KEY_WEATHER_WIND_DIRECTION, formatWindDirection((float) weatherSpec2.windDirection)));
        if (weatherSpec2.forecasts.size() > 0) {
            WeatherSpec.Forecast day1 = weatherSpec2.forecasts.get(0);
            pairs.add(new Pair(this.KEY_WEATHER_D1_ICON, Integer.valueOf(getIconForConditionCode(day1.conditionCode, false))));
            Integer num4 = this.KEY_WEATHER_D1_MINTEMP;
            Locale locale4 = Locale.ENGLISH;
            double d4 = (double) day1.minTemp;
            Double.isNaN(d4);
            pairs.add(new Pair(num4, String.format(locale4, "%.0f°C", new Object[]{Double.valueOf(d4 - 273.15d)})));
            Integer num5 = this.KEY_WEATHER_D1_MAXTEMP;
            Locale locale5 = Locale.ENGLISH;
            double d5 = (double) day1.maxTemp;
            Double.isNaN(d5);
            pairs.add(new Pair(num5, String.format(locale5, "%.0f°C", new Object[]{Double.valueOf(d5 - 273.15d)})));
        }
        if (weatherSpec2.forecasts.size() > 1) {
            WeatherSpec.Forecast day2 = weatherSpec2.forecasts.get(1);
            pairs.add(new Pair(this.KEY_WEATHER_D2_ICON, Integer.valueOf(getIconForConditionCode(day2.conditionCode, false))));
            Integer num6 = this.KEY_WEATHER_D2_MINTEMP;
            Locale locale6 = Locale.ENGLISH;
            double d6 = (double) day2.minTemp;
            Double.isNaN(d6);
            pairs.add(new Pair(num6, String.format(locale6, "%.0f°C", new Object[]{Double.valueOf(d6 - 273.15d)})));
            Integer num7 = this.KEY_WEATHER_D2_MAXTEMP;
            Locale locale7 = Locale.ENGLISH;
            double d7 = (double) day2.maxTemp;
            Double.isNaN(d7);
            pairs.add(new Pair(num7, String.format(locale7, "%.0f°C", new Object[]{Double.valueOf(d7 - 273.15d)})));
        }
        if (weatherSpec2.forecasts.size() > 2) {
            WeatherSpec.Forecast day3 = weatherSpec2.forecasts.get(2);
            pairs.add(new Pair(this.KEY_WEATHER_D3_ICON, Integer.valueOf(getIconForConditionCode(day3.conditionCode, false))));
            Integer num8 = this.KEY_WEATHER_D3_MINTEMP;
            Locale locale8 = Locale.ENGLISH;
            double d8 = (double) day3.minTemp;
            Double.isNaN(d8);
            pairs.add(new Pair(num8, String.format(locale8, "%.0f°C", new Object[]{Double.valueOf(d8 - 273.15d)})));
            Integer num9 = this.KEY_WEATHER_D3_MAXTEMP;
            Locale locale9 = Locale.ENGLISH;
            double d9 = (double) day3.maxTemp;
            Double.isNaN(d9);
            pairs.add(new Pair(num9, String.format(locale9, "%.0f°C", new Object[]{Double.valueOf(d9 - 273.15d)})));
        }
        byte[] weatherMessage = this.mPebbleProtocol.encodeApplicationMessagePush(48, this.mUUID, pairs, (Integer) null);
        ByteBuffer buf = ByteBuffer.allocate(weatherMessage.length);
        buf.put(weatherMessage);
        return buf.array();
    }

    public GBDeviceEvent[] onAppStart() {
        WeatherSpec weatherSpec = Weather.getInstance().getWeatherSpec();
        if (weatherSpec == null) {
            return new GBDeviceEvent[]{null};
        }
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodeYWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeYWeatherMessage(weatherSpec);
    }
}
