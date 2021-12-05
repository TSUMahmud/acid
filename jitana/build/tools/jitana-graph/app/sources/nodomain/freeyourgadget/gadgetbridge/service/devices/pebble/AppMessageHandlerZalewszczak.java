package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

class AppMessageHandlerZalewszczak extends AppMessageHandler {
    private static final int KEY_ICON = 0;
    private static final int KEY_TEMP = 1;

    AppMessageHandlerZalewszczak(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    private int getIconForConditionCode(int conditionCode) {
        if (conditionCode < 300) {
            return 7;
        }
        if (conditionCode < 400) {
            return 6;
        }
        if (conditionCode == 511) {
            return 8;
        }
        if (conditionCode < 600) {
            return 6;
        }
        if (conditionCode < 700) {
            return 8;
        }
        if (conditionCode < 800) {
            return 10;
        }
        if (conditionCode == 800) {
            return 1;
        }
        if (conditionCode == 801) {
            return 2;
        }
        if (conditionCode < 900) {
            return 5;
        }
        return 0;
    }

    private byte[] encodeWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        StringBuilder sb = new StringBuilder();
        sb.append(weatherSpec.currentTemp - 273);
        sb.append("C");
        pairs.add(new Pair(1, sb.toString()));
        pairs.add(new Pair(0, Integer.valueOf(getIconForConditionCode(weatherSpec.currentConditionCode))));
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
        sendBytes.encodedBytes = encodeWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeWeatherMessage(weatherSpec);
    }
}
