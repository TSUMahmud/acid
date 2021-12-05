package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.json.JSONException;
import org.json.JSONObject;

class AppMessageHandlerTrekVolle extends AppMessageHandler {
    private Integer MESSAGE_KEY_WEATHER_CONDITIONS;
    private Integer MESSAGE_KEY_WEATHER_ICON;
    private Integer MESSAGE_KEY_WEATHER_LOCATION;
    private Integer MESSAGE_KEY_WEATHER_TEMPERATURE;
    private Integer MESSAGE_KEY_WEATHER_TEMPERATURE_MAX;
    private Integer MESSAGE_KEY_WEATHER_TEMPERATURE_MIN;

    AppMessageHandlerTrekVolle(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.MESSAGE_KEY_WEATHER_TEMPERATURE = Integer.valueOf(appKeys.getInt("WEATHER_TEMPERATURE"));
            this.MESSAGE_KEY_WEATHER_CONDITIONS = Integer.valueOf(appKeys.getInt("WEATHER_CONDITIONS"));
            this.MESSAGE_KEY_WEATHER_ICON = Integer.valueOf(appKeys.getInt("WEATHER_ICON"));
            this.MESSAGE_KEY_WEATHER_TEMPERATURE_MIN = Integer.valueOf(appKeys.getInt("WEATHER_TEMPERATURE_MIN"));
            this.MESSAGE_KEY_WEATHER_TEMPERATURE_MAX = Integer.valueOf(appKeys.getInt("WEATHER_TEMPERATURE_MAX"));
            this.MESSAGE_KEY_WEATHER_LOCATION = Integer.valueOf(appKeys.getInt("WEATHER_LOCATION"));
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the TrekVolle watchface configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private int getIconForConditionCode(int conditionCode, boolean isNight) {
        return 2;
    }

    private byte[] encodeTrekVolleWeather(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        pairs.add(new Pair(this.MESSAGE_KEY_WEATHER_TEMPERATURE, Integer.valueOf(weatherSpec.currentTemp - 273)));
        pairs.add(new Pair(this.MESSAGE_KEY_WEATHER_CONDITIONS, weatherSpec.currentCondition));
        pairs.add(new Pair(this.MESSAGE_KEY_WEATHER_ICON, Integer.valueOf(getIconForConditionCode(weatherSpec.currentConditionCode, false))));
        pairs.add(new Pair(this.MESSAGE_KEY_WEATHER_TEMPERATURE_MAX, Integer.valueOf(weatherSpec.todayMaxTemp - 273)));
        pairs.add(new Pair(this.MESSAGE_KEY_WEATHER_TEMPERATURE_MIN, Integer.valueOf(weatherSpec.todayMinTemp - 273)));
        pairs.add(new Pair(this.MESSAGE_KEY_WEATHER_LOCATION, weatherSpec.location));
        return this.mPebbleProtocol.encodeApplicationMessagePush(48, this.mUUID, pairs, (Integer) null);
    }

    public GBDeviceEvent[] onAppStart() {
        WeatherSpec weatherSpec = Weather.getInstance().getWeatherSpec();
        if (weatherSpec == null) {
            return new GBDeviceEvent[]{null};
        }
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodeTrekVolleWeather(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeTrekVolleWeather(weatherSpec);
    }
}
