package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppMessageHandlerGBPebble extends AppMessageHandler {
    private static final int KEY_FIND_PHONE_START = 1;
    private static final int KEY_FIND_PHONE_STOP = 2;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AppMessageHandlerMisfit.class);

    public /* bridge */ /* synthetic */ byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return super.encodeUpdateWeather(weatherSpec);
    }

    public /* bridge */ /* synthetic */ UUID getUUID() {
        return super.getUUID();
    }

    public /* bridge */ /* synthetic */ boolean isEnabled() {
        return super.isEnabled();
    }

    public /* bridge */ /* synthetic */ GBDeviceEvent[] onAppStart() {
        return super.onAppStart();
    }

    AppMessageHandlerGBPebble(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        GBDeviceEventFindPhone gbDeviceEventFindPhone = null;
        Iterator<Pair<Integer, Object>> it = pairs.iterator();
        while (it.hasNext()) {
            Pair<Integer, Object> pair = it.next();
            int intValue = ((Integer) pair.first).intValue();
            if (intValue == 1) {
                LOG.info("find phone start");
                gbDeviceEventFindPhone = new GBDeviceEventFindPhone();
                gbDeviceEventFindPhone.event = GBDeviceEventFindPhone.Event.START;
            } else if (intValue != 2) {
                Logger logger = LOG;
                logger.info("unhandled key: " + pair.first);
            } else {
                LOG.info("find phone stop");
                gbDeviceEventFindPhone = new GBDeviceEventFindPhone();
                gbDeviceEventFindPhone.event = GBDeviceEventFindPhone.Event.STOP;
            }
        }
        GBDeviceEventSendBytes sendBytesAck = new GBDeviceEventSendBytes();
        sendBytesAck.encodedBytes = this.mPebbleProtocol.encodeApplicationMessageAck(this.mUUID, this.mPebbleProtocol.last_id);
        return new GBDeviceEvent[]{sendBytesAck, gbDeviceEventFindPhone};
    }
}
