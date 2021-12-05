package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.SimpleTimeZone;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleMisfitSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMisfitSample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppMessageHandlerMisfit extends AppMessageHandler {
    private static final int KEY_INCOMING_DATA = 7;
    private static final int KEY_INCOMING_DATA_BEGIN = 6;
    private static final int KEY_INCOMING_DATA_END = 8;
    private static final int KEY_SLEEPGOAL = 1;
    private static final int KEY_SLEEP_PROGRESS = 3;
    private static final int KEY_STEP_ROGRESS = 2;
    private static final int KEY_SYNC = 5;
    private static final int KEY_SYNC_RESULT = 9;
    private static final int KEY_VERSION = 4;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AppMessageHandlerMisfit.class);

    AppMessageHandlerMisfit(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    public boolean isEnabled() {
        return GBApplication.getPrefs().getBoolean("pebble_sync_misfit", true);
    }

    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        GBDevice device;
        Iterator<Pair<Integer, Object>> it;
        Throwable th;
        Throwable th2;
        GBDevice device2 = getDevice();
        Iterator<Pair<Integer, Object>> it2 = pairs.iterator();
        while (it2.hasNext()) {
            Pair<Integer, Object> pair = it2.next();
            int intValue = ((Integer) pair.first).intValue();
            if (intValue == 6) {
                it = it2;
                device = device2;
                Pair<Integer, Object> pair2 = pair;
                LOG.info("incoming data start");
            } else if (intValue == 7) {
                byte[] data = (byte[]) pair.second;
                ByteBuffer buf = ByteBuffer.wrap(data);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                int timestamp = buf.getInt();
                int i = buf.getInt();
                int samples = (data.length - 8) / 2;
                if (samples <= 0) {
                    it = it2;
                    device = device2;
                } else {
                    if (this.mPebbleProtocol.mFwMajor < 3) {
                        timestamp -= SimpleTimeZone.getDefault().getOffset(((long) timestamp) * 1000) / 1000;
                    }
                    Date startDate = new Date(((long) timestamp) * 1000);
                    Date endDate = new Date(((long) ((samples * 60) + timestamp)) * 1000);
                    Logger logger = LOG;
                    logger.info("got data from " + startDate + " to " + endDate);
                    int totalSteps = 0;
                    PebbleMisfitSample[] misfitSamples = new PebbleMisfitSample[samples];
                    try {
                        DBHandler db = GBApplication.acquireDB();
                        try {
                            PebbleMisfitSampleProvider sampleProvider = new PebbleMisfitSampleProvider(device2, db.getDaoSession());
                            Long userId = DBHelper.getUser(db.getDaoSession()).getId();
                            it = it2;
                            device = device2;
                            try {
                                Long deviceId = DBHelper.getDevice(getDevice(), db.getDaoSession()).getId();
                                int i2 = 0;
                                while (i2 < samples) {
                                    short sample = buf.getShort();
                                    misfitSamples[i2] = new PebbleMisfitSample(timestamp + (i2 * 60), deviceId.longValue(), userId.longValue(), sample & GBDevice.BATTERY_UNKNOWN);
                                    Long deviceId2 = deviceId;
                                    misfitSamples[i2].setProvider(sampleProvider);
                                    int steps = misfitSamples[i2].getSteps();
                                    totalSteps += steps;
                                    Pair<Integer, Object> pair3 = pair;
                                    try {
                                        Logger logger2 = LOG;
                                        byte[] data2 = data;
                                        try {
                                            StringBuilder sb = new StringBuilder();
                                            ByteBuffer buf2 = buf;
                                            sb.append("got steps for sample ");
                                            sb.append(i2);
                                            sb.append(" : ");
                                            sb.append(steps);
                                            sb.append("(");
                                            sb.append(Integer.toHexString(sample & GBDevice.BATTERY_UNKNOWN));
                                            sb.append(")");
                                            logger2.info(sb.toString());
                                            i2++;
                                            deviceId = deviceId2;
                                            pair = pair3;
                                            data = data2;
                                            buf = buf2;
                                        } catch (Throwable th3) {
                                            th = th3;
                                            throw th;
                                        }
                                    } catch (Throwable th4) {
                                        byte[] bArr = data;
                                        ByteBuffer byteBuffer = buf;
                                        th = th4;
                                        throw th;
                                    }
                                }
                                Long l = deviceId;
                                Pair<Integer, Object> pair4 = pair;
                                byte[] bArr2 = data;
                                ByteBuffer byteBuffer2 = buf;
                                Logger logger3 = LOG;
                                logger3.info("total steps for above period: " + totalSteps);
                                sampleProvider.addGBActivitySamples(misfitSamples);
                                if (db != null) {
                                    try {
                                        db.close();
                                    } catch (Exception e) {
                                        e = e;
                                    } catch (Throwable th5) {
                                        th.addSuppressed(th5);
                                    }
                                }
                            } catch (Throwable th6) {
                                Pair<Integer, Object> pair5 = pair;
                                byte[] bArr3 = data;
                                ByteBuffer byteBuffer3 = buf;
                                th = th6;
                                throw th;
                            }
                        } catch (Throwable th7) {
                            GBDevice gBDevice = device2;
                            Pair<Integer, Object> pair6 = pair;
                            byte[] bArr4 = data;
                            ByteBuffer byteBuffer4 = buf;
                            th = th7;
                            throw th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        GBDevice gBDevice2 = device2;
                        Pair<Integer, Object> pair7 = pair;
                        byte[] bArr5 = data;
                        ByteBuffer byteBuffer5 = buf;
                        LOG.error("Error acquiring database", (Throwable) e);
                        return null;
                    }
                }
            } else if (intValue != 8) {
                Logger logger4 = LOG;
                logger4.info("unhandled key: " + pair.first);
                it = it2;
                device = device2;
            } else {
                C1238GB.signalActivityDataFinish();
                LOG.info("incoming data end");
                it = it2;
                device = device2;
            }
            it2 = it;
            device2 = device;
        }
        GBDeviceEventSendBytes sendBytesAck = new GBDeviceEventSendBytes();
        sendBytesAck.encodedBytes = this.mPebbleProtocol.encodeApplicationMessageAck(this.mUUID, this.mPebbleProtocol.last_id);
        return new GBDeviceEvent[]{sendBytesAck};
        throw th2;
    }
}
