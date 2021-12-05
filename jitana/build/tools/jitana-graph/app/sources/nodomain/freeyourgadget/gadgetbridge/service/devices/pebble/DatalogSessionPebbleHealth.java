package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

abstract class DatalogSessionPebbleHealth extends DatalogSession {
    private final GBDevice mDevice;

    DatalogSessionPebbleHealth(byte id, UUID uuid, int timestamp, int tag, byte itemType, short itemSize, GBDevice device) {
        super(id, uuid, timestamp, tag, itemType, itemSize);
        this.mDevice = device;
    }

    public GBDevice getDevice() {
        return this.mDevice;
    }

    /* access modifiers changed from: package-private */
    public boolean isPebbleHealthEnabled() {
        return GBApplication.getPrefs().getBoolean("pebble_sync_health", true);
    }

    /* access modifiers changed from: package-private */
    public boolean storePebbleHealthRawRecord() {
        return GBApplication.getPrefs().getBoolean("pebble_health_store_raw", true);
    }
}
