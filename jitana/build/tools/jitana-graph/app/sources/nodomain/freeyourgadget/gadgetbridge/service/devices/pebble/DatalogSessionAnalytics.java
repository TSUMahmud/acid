package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DatalogSessionAnalytics extends DatalogSession {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DatalogSessionAnalytics.class);
    private GBDeviceEventBatteryInfo mGBDeviceEventBatteryInfo = new GBDeviceEventBatteryInfo();

    DatalogSessionAnalytics(byte id, UUID uuid, int timestamp, int tag, byte itemType, short itemSize, GBDevice device) {
        super(id, uuid, timestamp, tag, itemType, itemSize);
        this.taginfo = "(analytics - " + tag + ")";
    }

    /* access modifiers changed from: package-private */
    public GBDeviceEvent[] handleMessage(ByteBuffer datalogMessage, int length) {
        Logger logger = LOG;
        logger.info("DATALOG " + this.taginfo + C1238GB.hexdump(datalogMessage.array(), datalogMessage.position(), length));
        datalogMessage.position(datalogMessage.position() + 3);
        int messageTS = datalogMessage.getInt();
        datalogMessage.position(datalogMessage.position() + 12);
        short reportedMilliVolts = datalogMessage.getShort();
        datalogMessage.position(datalogMessage.position() + 2);
        byte reportedPercentage = datalogMessage.get();
        Logger logger2 = LOG;
        logger2.info("Battery reading for TS " + messageTS + " is: " + reportedMilliVolts + " milliVolts, percentage: " + reportedPercentage);
        if (messageTS <= 0 || reportedMilliVolts >= 5000) {
            return new GBDeviceEvent[]{null};
        }
        this.mGBDeviceEventBatteryInfo.state = BatteryState.BATTERY_NORMAL;
        GBDeviceEventBatteryInfo gBDeviceEventBatteryInfo = this.mGBDeviceEventBatteryInfo;
        gBDeviceEventBatteryInfo.level = (short) reportedPercentage;
        return new GBDeviceEvent[]{gBDeviceEventBatteryInfo, null};
    }
}
