package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleHealthSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivitySample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DatalogSessionHealthSteps extends DatalogSessionPebbleHealth {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DatalogSessionHealthSteps.class);

    DatalogSessionHealthSteps(byte id, UUID uuid, int timestamp, int tag, byte item_type, short item_size, GBDevice device) {
        super(id, uuid, timestamp, tag, item_type, item_size, device);
        this.taginfo = "(Health - steps)";
    }

    public GBDeviceEvent[] handleMessage(ByteBuffer datalogMessage, int length) {
        ByteBuffer byteBuffer = datalogMessage;
        int i = length;
        Logger logger = LOG;
        logger.info("DATALOG " + this.taginfo + C1238GB.hexdump(datalogMessage.array(), datalogMessage.position(), i));
        GBDeviceEvent[] gBDeviceEventArr = null;
        if (!isPebbleHealthEnabled()) {
            return null;
        }
        int initialPosition = datalogMessage.position();
        if (i % this.itemSize != 0) {
            return null;
        }
        int packetCount = i / this.itemSize;
        int packetIdx = 0;
        while (packetIdx < packetCount) {
            byteBuffer.position((this.itemSize * packetIdx) + initialPosition);
            short recordVersion = datalogMessage.getShort();
            if (recordVersion != 5 && recordVersion != 6 && recordVersion != 7 && recordVersion != 12 && recordVersion != 13) {
                return gBDeviceEventArr;
            }
            int timestamp = datalogMessage.getInt();
            datalogMessage.get();
            int recordLength = datalogMessage.get();
            int recordNum = datalogMessage.get();
            int beginOfRecordPosition = datalogMessage.position();
            StepsRecord[] stepsRecords = new StepsRecord[recordNum];
            byte[] tempRecord = new byte[recordLength];
            for (int recordIdx = 0; recordIdx < recordNum; recordIdx++) {
                byteBuffer.position(beginOfRecordPosition + (recordIdx * recordLength));
                byteBuffer.get(tempRecord);
                stepsRecords[recordIdx] = new StepsRecord(timestamp, recordVersion, tempRecord);
                timestamp += 60;
            }
            store(stepsRecords);
            packetIdx++;
            gBDeviceEventArr = null;
        }
        return new GBDeviceEvent[]{null};
    }

    private void store(StepsRecord[] stepsRecords) {
        Throwable th;
        StepsRecord[] stepsRecordArr = stepsRecords;
        try {
            DBHandler dbHandler = GBApplication.acquireDB();
            try {
                PebbleHealthSampleProvider sampleProvider = new PebbleHealthSampleProvider(getDevice(), dbHandler.getDaoSession());
                PebbleHealthActivitySample[] samples = new PebbleHealthActivitySample[stepsRecordArr.length];
                Long userId = DBHelper.getUser(dbHandler.getDaoSession()).getId();
                Long deviceId = DBHelper.getDevice(getDevice(), dbHandler.getDaoSession()).getId();
                int j = 0;
                while (j < stepsRecordArr.length) {
                    StepsRecord stepsRecord = stepsRecordArr[j];
                    samples[j] = new PebbleHealthActivitySample(stepsRecord.timestamp, deviceId.longValue(), userId.longValue(), stepsRecord.getRawData(), stepsRecord.intensity, stepsRecord.steps, stepsRecord.heart_rate);
                    samples[j].setProvider(sampleProvider);
                    j++;
                    stepsRecordArr = stepsRecords;
                }
                sampleProvider.addGBActivitySamples(samples);
                if (dbHandler != null) {
                    dbHandler.close();
                }
            } catch (Throwable th2) {
                Throwable th3 = th2;
                if (dbHandler != null) {
                    dbHandler.close();
                }
                throw th3;
            }
        } catch (Exception ex) {
            LOG.debug(ex.getMessage());
        } catch (Throwable th4) {
            th.addSuppressed(th4);
        }
    }

    private class StepsRecord {
        int heart_rate;
        int intensity;
        byte[] knownVersions = {5, 6, 7, 12, 13};
        int light_intensity;
        int orientation;
        byte[] rawData;
        int steps;
        int timestamp;
        short version;

        StepsRecord(int timestamp2, short version2, byte[] rawData2) {
            this.timestamp = timestamp2;
            this.rawData = rawData2;
            ByteBuffer record = ByteBuffer.wrap(rawData2);
            record.order(ByteOrder.LITTLE_ENDIAN);
            this.version = version2;
            this.steps = record.get() & 255;
            this.orientation = record.get() & 255;
            this.intensity = record.getShort() & GBDevice.BATTERY_UNKNOWN;
            this.light_intensity = record.get() & 255;
            if (version2 >= 7) {
                record.getInt();
                record.getShort();
                record.get();
                this.heart_rate = record.get() & 255;
            }
        }

        /* access modifiers changed from: package-private */
        public byte[] getRawData() {
            if (DatalogSessionHealthSteps.this.storePebbleHealthRawRecord()) {
                return this.rawData;
            }
            return null;
        }
    }
}
