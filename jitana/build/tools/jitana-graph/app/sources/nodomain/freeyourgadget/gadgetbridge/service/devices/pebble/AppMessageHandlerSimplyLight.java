package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.json.JSONException;
import org.json.JSONObject;

class AppMessageHandlerSimplyLight extends AppMessageHandler {
    private static final int CLEAR = 0;
    private static final int CLOUDY = 1;
    private static final int EXTREME_COLD = 12;
    private static final int EXTREME_HEAT = 13;
    private static final int EXTREME_WIND = 9;
    private static final int FOG = 2;
    private static final int HAIL = 7;
    private static final int HURRICANE = 11;
    private static final int LIGHT_RAIN = 3;
    private static final int RAIN = 4;
    private static final int SNOW = 6;
    private static final int SNOW_THUNDERSTORM = 14;
    private static final int THUNDERSTORM = 5;
    private static final int TORNADO = 10;
    private static final int WIND = 8;
    private Integer KEY_CONDITION;
    private Integer KEY_ERR;
    private Integer KEY_TEMPERATURE;

    AppMessageHandlerSimplyLight(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.KEY_TEMPERATURE = Integer.valueOf(appKeys.getInt("temperature"));
            this.KEY_CONDITION = Integer.valueOf(appKeys.getInt("condition"));
            this.KEY_ERR = Integer.valueOf(appKeys.getInt(NotificationCompat.CATEGORY_ERROR));
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the Simply Light watchface configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private int getConditionForConditionCode(int conditionCode) {
        if (conditionCode == 800 || conditionCode == 951) {
            return 0;
        }
        if (conditionCode > 800 && conditionCode < 900) {
            return 1;
        }
        if (conditionCode >= 700 && conditionCode < 800) {
            return 2;
        }
        if (conditionCode >= 300 && conditionCode < 400) {
            return 3;
        }
        if (conditionCode >= 500 && conditionCode < 600) {
            return 4;
        }
        if (conditionCode >= 200 && conditionCode < 300) {
            return 5;
        }
        if (conditionCode >= 600 && conditionCode < 700) {
            return 6;
        }
        if (conditionCode == 906) {
            return 7;
        }
        if (conditionCode >= 907 && conditionCode < 957) {
            return 8;
        }
        if (conditionCode == 905) {
            return 9;
        }
        if (conditionCode == 900) {
            return 10;
        }
        if (conditionCode == 901 || conditionCode == 902 || conditionCode == 962) {
            return 11;
        }
        if (conditionCode == 903) {
            return 12;
        }
        if (conditionCode == 904) {
            return 13;
        }
        return 0;
    }

    private byte[] encodeSimplyLightWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        pairs.add(new Pair(this.KEY_TEMPERATURE, Integer.valueOf(weatherSpec.currentTemp - 273)));
        pairs.add(new Pair(this.KEY_CONDITION, Integer.valueOf(getConditionForConditionCode(weatherSpec.currentConditionCode))));
        pairs.add(new Pair(this.KEY_ERR, 0));
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
        sendBytes.encodedBytes = encodeSimplyLightWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeSimplyLightWeatherMessage(weatherSpec);
    }
}
