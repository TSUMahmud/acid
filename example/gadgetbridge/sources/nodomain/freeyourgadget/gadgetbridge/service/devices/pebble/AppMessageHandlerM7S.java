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

class AppMessageHandlerM7S extends AppMessageHandler {
    private static final int CLEAR = 73;
    private static final int CLOUDY = 34;
    private static final int DRIZZLE = 45;
    private static final int EXTREME_COLD = 90;
    private static final int EXTREME_HEAT = 93;
    private static final int EXTREME_WIND = 66;
    private static final int FOG = 60;
    private static final int HAIL = 51;
    private static final int HAZE = 63;
    private static final int HURRICANE = 88;
    private static final int LIGHT_RAIN = 36;
    private static final int RAIN = 36;
    private static final int SNOW = 57;
    private static final int THUNDERSTORM = 70;
    private static final int TORNADO = 88;
    private static final int WIND = 66;
    private Integer KEY_LOCATION_NAME;
    private Integer KEY_WEATHER_DATA_TIME;
    private Integer KEY_WEATHER_ICON;
    private Integer KEY_WEATHER_STRING_1;
    private Integer KEY_WEATHER_STRING_2;
    private Integer KEY_WEATHER_TEMP;

    AppMessageHandlerM7S(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.KEY_LOCATION_NAME = Integer.valueOf(appKeys.getInt("KEY_LOCATION_NAME"));
            this.KEY_WEATHER_TEMP = Integer.valueOf(appKeys.getInt("KEY_WEATHER_TEMP"));
            this.KEY_WEATHER_STRING_1 = Integer.valueOf(appKeys.getInt("KEY_WEATHER_STRING_1"));
            this.KEY_WEATHER_STRING_2 = Integer.valueOf(appKeys.getInt("KEY_WEATHER_STRING_2"));
            this.KEY_WEATHER_ICON = Integer.valueOf(appKeys.getInt("KEY_WEATHER_ICON"));
            this.KEY_WEATHER_DATA_TIME = Integer.valueOf(appKeys.getInt("KEY_WEATHER_DATA_TIME"));
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the M7S watchface configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private int getIconForConditionCode(int conditionCode) {
        if (conditionCode == 800 || conditionCode == 951) {
            return 73;
        }
        if (conditionCode > 800 && conditionCode < 900) {
            return 34;
        }
        if (conditionCode >= 300 && conditionCode < 313) {
            return 45;
        }
        if (conditionCode >= 313 && conditionCode < 400) {
            return 36;
        }
        if (conditionCode >= 500 && conditionCode < 600) {
            return 36;
        }
        if (conditionCode >= 700 && conditionCode < 732) {
            return 63;
        }
        if (conditionCode == 741) {
            return 60;
        }
        if (conditionCode == 751 || conditionCode == 761 || conditionCode == 762) {
            return 63;
        }
        if (conditionCode == 771) {
            return 66;
        }
        if (conditionCode == 781) {
            return 88;
        }
        if (conditionCode >= 200 && conditionCode < 300) {
            return 70;
        }
        if (conditionCode >= 600 && conditionCode < 700) {
            return 57;
        }
        if (conditionCode == 906) {
            return 51;
        }
        if ((conditionCode >= 907 && conditionCode < 957) || conditionCode == 905) {
            return 66;
        }
        if (conditionCode == 900 || conditionCode == 901 || conditionCode == 902 || conditionCode == 962) {
            return 88;
        }
        if (conditionCode == 903) {
            return 90;
        }
        if (conditionCode == 904) {
            return 93;
        }
        return 0;
    }

    private byte[] encodeM7SWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        Locale locale = Locale.ENGLISH;
        double d = (double) weatherSpec.todayMaxTemp;
        Double.isNaN(d);
        double d2 = (double) weatherSpec.todayMinTemp;
        Double.isNaN(d2);
        String wString1 = String.format(locale, "%.0f / %.0f__C \n%.0f %s", new Object[]{Double.valueOf(d - 273.15d), Double.valueOf(d2 - 273.15d), Float.valueOf(weatherSpec.windSpeed), "km/h"});
        String wString2 = String.format(Locale.ENGLISH, "%d %%", new Object[]{Integer.valueOf(weatherSpec.currentHumidity)});
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        pairs.add(new Pair(this.KEY_LOCATION_NAME, weatherSpec.location));
        Integer num = this.KEY_WEATHER_TEMP;
        double d3 = (double) weatherSpec.currentTemp;
        Double.isNaN(d3);
        pairs.add(new Pair(num, Integer.valueOf((int) Math.round(d3 - 273.15d))));
        pairs.add(new Pair(this.KEY_WEATHER_DATA_TIME, Integer.valueOf(weatherSpec.timestamp)));
        pairs.add(new Pair(this.KEY_WEATHER_STRING_1, wString1));
        pairs.add(new Pair(this.KEY_WEATHER_STRING_2, wString2));
        pairs.add(new Pair(this.KEY_WEATHER_ICON, Integer.valueOf(getIconForConditionCode(weatherSpec.currentConditionCode))));
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
        sendBytes.encodedBytes = encodeM7SWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeM7SWeatherMessage(weatherSpec);
    }
}
