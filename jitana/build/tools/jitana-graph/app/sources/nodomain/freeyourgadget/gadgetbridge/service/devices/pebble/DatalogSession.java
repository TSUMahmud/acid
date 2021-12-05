package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DatalogSession {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DatalogSession.class);

    /* renamed from: id */
    final byte f194id;
    final short itemSize;
    final byte itemType;
    final int tag;
    String taginfo = "(unknown)";
    final int timestamp;
    final UUID uuid;

    DatalogSession(byte id, UUID uuid2, int timestamp2, int tag2, byte itemType2, short itemSize2) {
        this.f194id = id;
        this.tag = tag2;
        this.uuid = uuid2;
        this.timestamp = timestamp2;
        this.itemType = itemType2;
        this.itemSize = itemSize2;
    }

    /* access modifiers changed from: package-private */
    public GBDeviceEvent[] handleMessage(ByteBuffer buf, int length) {
        return new GBDeviceEvent[]{null};
    }

    /* access modifiers changed from: package-private */
    public String getTaginfo() {
        return this.taginfo;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] handleMessageForPebbleKit(java.nio.ByteBuffer r11, int r12) {
        /*
            r10 = this;
            short r0 = r10.itemSize
            int r1 = r12 % r0
            r2 = 0
            if (r1 == 0) goto L_0x000f
            org.slf4j.Logger r0 = LOG
            java.lang.String r1 = "invalid length"
            r0.warn(r1)
            return r2
        L_0x000f:
            int r0 = r12 / r0
            if (r0 > 0) goto L_0x001b
            org.slf4j.Logger r1 = LOG
            java.lang.String r3 = "invalid number of datalog elements"
            r1.warn(r3)
            return r2
        L_0x001b:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.pebble.GBDeviceEventDataLogging r1 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.pebble.GBDeviceEventDataLogging
            r1.<init>()
            r3 = 1
            r1.command = r3
            java.util.UUID r4 = r10.uuid
            r1.appUUID = r4
            int r4 = r10.timestamp
            long r4 = (long) r4
            r6 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r4 = r4 & r6
            r1.timestamp = r4
            int r4 = r10.tag
            long r4 = (long) r4
            r1.tag = r4
            byte r4 = r10.itemType
            r1.pebbleDataType = r4
            java.lang.Object[] r4 = new java.lang.Object[r0]
            r1.data = r4
            r4 = 0
        L_0x0040:
            r5 = 2
            if (r4 >= r0) goto L_0x0078
            byte r8 = r10.itemType
            if (r8 == 0) goto L_0x0069
            if (r8 == r5) goto L_0x005a
            r5 = 3
            if (r8 == r5) goto L_0x004d
            goto L_0x0075
        L_0x004d:
            java.lang.Object[] r5 = r1.data
            int r8 = r11.getInt()
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r5[r4] = r8
            goto L_0x0075
        L_0x005a:
            java.lang.Object[] r5 = r1.data
            int r8 = r11.getInt()
            long r8 = (long) r8
            long r8 = r8 & r6
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r5[r4] = r8
            goto L_0x0075
        L_0x0069:
            short r5 = r10.itemSize
            byte[] r5 = new byte[r5]
            r11.get(r5)
            java.lang.Object[] r8 = r1.data
            r8[r4] = r5
        L_0x0075:
            int r4 = r4 + 1
            goto L_0x0040
        L_0x0078:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r4 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r5]
            r5 = 0
            r4[r5] = r1
            r4[r3] = r2
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.DatalogSession.handleMessageForPebbleKit(java.nio.ByteBuffer, int):nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[]");
    }
}
