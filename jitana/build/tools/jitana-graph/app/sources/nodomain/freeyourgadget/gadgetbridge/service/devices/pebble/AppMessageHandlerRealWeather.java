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

class AppMessageHandlerRealWeather extends AppMessageHandler {
    private static int CLEAR_DAY = 0;
    private static int CLEAR_NIGHT = 1;
    private static int CLOUD = 7;
    private static int CLOUDY = 11;
    private static int COLD = 3;
    private static int HAIL = 10;
    private static int HAZE = 6;

    /* renamed from: NA */
    private static int f192NA = 13;
    private static int PARTLY_CLOUDY_DAY = 4;
    private static int PARTLY_CLOUDY_NIGHT = 5;
    private static int RAIN = 8;
    private static int SNOW = 9;
    private static int STORM = 12;
    private static int WINDY = 2;
    private Integer KEY_WEATHER_ICON;
    private Integer KEY_WEATHER_TEMP;

    AppMessageHandlerRealWeather(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.KEY_WEATHER_TEMP = Integer.valueOf(appKeys.getInt("temperature"));
            this.KEY_WEATHER_ICON = Integer.valueOf(appKeys.getInt("icon"));
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the RealWeather watchface configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private int getIconForConditionCode(int conditionCode, boolean isNight) {
        if (conditionCode == 800 || conditionCode == 951) {
            return isNight ? CLEAR_NIGHT : CLEAR_DAY;
        }
        if (conditionCode == 801 || conditionCode == 802) {
            return isNight ? PARTLY_CLOUDY_NIGHT : PARTLY_CLOUDY_DAY;
        }
        if (conditionCode >= 300 && conditionCode < 313) {
            return RAIN;
        }
        if (conditionCode >= 313 && conditionCode < 400) {
            return RAIN;
        }
        if (conditionCode >= 500 && conditionCode < 600) {
            return RAIN;
        }
        if (conditionCode >= 700 && conditionCode < 732) {
            return CLOUDY;
        }
        if (conditionCode == 741 || conditionCode == 751 || conditionCode == 761 || conditionCode == 762) {
            return HAZE;
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
            return HAIL;
        }
        if (conditionCode == 615 || conditionCode == 616 || conditionCode == 620 || conditionCode == 621 || conditionCode == 622) {
            return SNOW;
        }
        if (conditionCode == 906) {
            return SNOW;
        }
        if (conditionCode == 803 || conditionCode == 804) {
            return CLOUD;
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
        return 0;
    }

    private byte[] encodeRealWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        Integer num = this.KEY_WEATHER_TEMP;
        Locale locale = Locale.ENGLISH;
        double d = (double) weatherSpec.currentTemp;
        Double.isNaN(d);
        pairs.add(new Pair(num, String.format(locale, "%.0f°", new Object[]{Double.valueOf(d - 273.15d)})));
        pairs.add(new Pair(this.KEY_WEATHER_ICON, Integer.valueOf(getIconForConditionCode(weatherSpec.currentConditionCode, false))));
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
        sendBytes.encodedBytes = encodeRealWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeRealWeatherMessage(weatherSpec);
    }
}
