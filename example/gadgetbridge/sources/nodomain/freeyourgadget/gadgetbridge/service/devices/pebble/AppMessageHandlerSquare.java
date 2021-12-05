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

class AppMessageHandlerSquare extends AppMessageHandler {
    private int CfgKeyCelsiusTemperature;
    private int CfgKeyConditions;
    private int CfgKeyUseCelsius;
    private int CfgKeyWeatherLocation;
    private int CfgKeyWeatherMode;

    AppMessageHandlerSquare(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.CfgKeyCelsiusTemperature = appKeys.getInt("CfgKeyCelsiusTemperature");
            this.CfgKeyConditions = appKeys.getInt("CfgKeyConditions");
            this.CfgKeyWeatherMode = appKeys.getInt("CfgKeyWeatherMode");
            this.CfgKeyUseCelsius = appKeys.getInt("CfgKeyUseCelsius");
            this.CfgKeyWeatherLocation = appKeys.getInt("CfgKeyWeatherLocation");
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the Square watchface configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private byte[] encodeSquareWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        pairs.add(new Pair(Integer.valueOf(this.CfgKeyWeatherMode), 1));
        pairs.add(new Pair(Integer.valueOf(this.CfgKeyConditions), weatherSpec.currentCondition));
        pairs.add(new Pair(Integer.valueOf(this.CfgKeyUseCelsius), 1));
        pairs.add(new Pair(Integer.valueOf(this.CfgKeyCelsiusTemperature), Integer.valueOf(weatherSpec.currentTemp - 273)));
        pairs.add(new Pair(Integer.valueOf(this.CfgKeyWeatherLocation), weatherSpec.location));
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
        sendBytes.encodedBytes = encodeSquareWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeSquareWeatherMessage(weatherSpec);
    }
}
