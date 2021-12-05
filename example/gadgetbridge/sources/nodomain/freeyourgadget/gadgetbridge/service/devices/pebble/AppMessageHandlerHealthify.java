package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
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

class AppMessageHandlerHealthify extends AppMessageHandler {
    private Integer KEY_CONDITIONS;
    private Integer KEY_TEMPERATURE;

    AppMessageHandlerHealthify(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.KEY_TEMPERATURE = Integer.valueOf(appKeys.getInt("TEMPERATURE"));
            this.KEY_CONDITIONS = Integer.valueOf(appKeys.getInt("CONDITIONS"));
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the Helthify watchface configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private byte[] encodeHelthifyWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        pairs.add(new Pair(this.KEY_CONDITIONS, weatherSpec.currentCondition));
        pairs.add(new Pair(this.KEY_TEMPERATURE, Integer.valueOf(weatherSpec.currentTemp - 273)));
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
        sendBytes.encodedBytes = encodeHelthifyWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeHelthifyWeatherMessage(weatherSpec);
    }
}
