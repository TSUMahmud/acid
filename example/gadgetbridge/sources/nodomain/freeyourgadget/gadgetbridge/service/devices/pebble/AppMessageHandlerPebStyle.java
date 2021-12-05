package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppMessageHandlerPebStyle extends AppMessageHandler {
    public static final int KEY_AMPM_TEXT = 21;
    public static final int KEY_BLUETOOTH_ALERT = 2;
    public static final int KEY_BLUETOOTH_ICON = 20;
    public static final int KEY_CITY_NAME = 9;
    public static final int KEY_COLOR_SELECTION = 15;
    public static final int KEY_JSREADY = 6;
    public static final int KEY_JS_TIMEZONE_OFFSET = 13;
    public static final int KEY_LOCATION_SERVICE = 7;
    public static final int KEY_MAIN_BG_COLOR = 16;
    public static final int KEY_MAIN_CLOCK = 0;
    public static final int KEY_MAIN_COLOR = 17;
    public static final int KEY_SECONDARY_INFO_TYPE = 10;
    public static final int KEY_SECOND_HAND = 1;
    public static final int KEY_SIDEBAR_BG_COLOR = 18;
    public static final int KEY_SIDEBAR_COLOR = 19;
    public static final int KEY_SIDEBAR_LOCATION = 14;
    public static final int KEY_TEMPERATURE_FORMAT = 8;
    public static final int KEY_TIMEZONE_NAME = 11;
    public static final int KEY_TIME_SEPARATOR = 12;
    public static final int KEY_WEATHER_CODE = 3;
    public static final int KEY_WEATHER_INTERVAL = 5;
    public static final int KEY_WEATHER_TEMP = 4;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AppMessageHandlerPebStyle.class);

    public AppMessageHandlerPebStyle(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    private byte[] encodeAck() {
        byte[] ackMessage = this.mPebbleProtocol.encodeApplicationMessageAck(this.mUUID, this.mPebbleProtocol.last_id);
        ByteBuffer buf = ByteBuffer.allocate(ackMessage.length);
        buf.put(ackMessage);
        return buf.array();
    }

    private byte[] encodePebStyleConfig() {
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        pairs.add(new Pair(1, 0));
        pairs.add(new Pair(2, 0));
        pairs.add(new Pair(8, 1));
        pairs.add(new Pair(7, 2));
        pairs.add(new Pair(14, 1));
        pairs.add(new Pair(15, 1));
        pairs.add(new Pair(17, Byte.valueOf(PebbleColor.Black)));
        pairs.add(new Pair(16, (byte) -1));
        pairs.add(new Pair(18, Byte.valueOf(PebbleColor.MediumSpringGreen)));
        pairs.add(new Pair(0, 0));
        pairs.add(new Pair(10, 1));
        WeatherSpec weather = Weather.getInstance().getWeatherSpec();
        if (weather != null) {
            pairs.add(new Pair(7, 0));
            pairs.add(new Pair(3, Integer.valueOf(Weather.mapToYahooCondition(weather.currentConditionCode))));
            pairs.add(new Pair(4, Integer.valueOf(weather.currentTemp - 273)));
        }
        byte[] testMessage = this.mPebbleProtocol.encodeApplicationMessagePush(48, this.mUUID, pairs, (Integer) null);
        ByteBuffer buf = ByteBuffer.allocate(testMessage.length);
        buf.put(testMessage);
        return buf.array();
    }

    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> arrayList) {
        return null;
    }

    public GBDeviceEvent[] onAppStart() {
        return null;
    }
}
