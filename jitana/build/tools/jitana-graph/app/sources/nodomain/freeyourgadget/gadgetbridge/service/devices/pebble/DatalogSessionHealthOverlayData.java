package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivityOverlay;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivityOverlayDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DatalogSessionHealthOverlayData extends DatalogSessionPebbleHealth {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DatalogSessionHealthOverlayData.class);

    DatalogSessionHealthOverlayData(byte id, UUID uuid, int timestamp, int tag, byte item_type, short item_size, GBDevice device) {
        super(id, uuid, timestamp, tag, item_type, item_size, device);
        this.taginfo = "(Health - overlay data " + tag + " )";
    }

    public GBDeviceEvent[] handleMessage(ByteBuffer datalogMessage, int length) {
        Logger logger = LOG;
        logger.info("DATALOG " + this.taginfo + C1238GB.hexdump(datalogMessage.array(), datalogMessage.position(), length));
        if (!isPebbleHealthEnabled()) {
            return null;
        }
        int initialPosition = datalogMessage.position();
        if (length % this.itemSize != 0) {
            return null;
        }
        int recordCount = length / this.itemSize;
        OverlayRecord[] overlayRecords = new OverlayRecord[recordCount];
        byte[] tempRecord = new byte[this.itemSize];
        for (int recordIdx = 0; recordIdx < recordCount; recordIdx++) {
            datalogMessage.position((this.itemSize * recordIdx) + initialPosition);
            datalogMessage.get(tempRecord);
            overlayRecords[recordIdx] = new OverlayRecord(tempRecord);
        }
        store(overlayRecords);
        return new GBDeviceEvent[]{null};
    }

    private void store(OverlayRecord[] overlayRecords) {
        Throwable th;
        OverlayRecord[] overlayRecordArr = overlayRecords;
        try {
            DBHandler dbHandler = GBApplication.acquireDB();
            try {
                DaoSession session = dbHandler.getDaoSession();
                Long userId = DBHelper.getUser(session).getId();
                Long deviceId = DBHelper.getDevice(getDevice(), session).getId();
                PebbleHealthActivityOverlayDao overlayDao = session.getPebbleHealthActivityOverlayDao();
                List<PebbleHealthActivityOverlay> overlayList = new ArrayList<>();
                int length = overlayRecordArr.length;
                int i = 0;
                while (i < length) {
                    OverlayRecord overlayRecord = overlayRecordArr[i];
                    int i2 = overlayRecord.timestampStart;
                    int i3 = overlayRecord.timestampStart;
                    DaoSession session2 = session;
                    PebbleHealthActivityOverlay pebbleHealthActivityOverlay = r10;
                    PebbleHealthActivityOverlay pebbleHealthActivityOverlay2 = new PebbleHealthActivityOverlay(i2, overlayRecord.durationSeconds + i3, overlayRecord.type, deviceId.longValue(), userId.longValue(), overlayRecord.getRawData());
                    overlayList.add(pebbleHealthActivityOverlay);
                    i++;
                    session = session2;
                }
                overlayDao.insertOrReplaceInTx(overlayList);
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

    private class OverlayRecord {
        int durationSeconds;
        byte[] knownVersions = {1, 3};
        int offsetUTC;
        byte[] rawData;
        int timestampStart;
        int type;
        short version;

        OverlayRecord(byte[] rawData2) {
            this.rawData = rawData2;
            ByteBuffer record = ByteBuffer.wrap(rawData2);
            record.order(ByteOrder.LITTLE_ENDIAN);
            this.version = record.getShort();
            record.getShort();
            this.type = record.getShort();
            this.offsetUTC = record.getInt();
            this.timestampStart = record.getInt();
            this.durationSeconds = record.getInt();
        }

        /* access modifiers changed from: package-private */
        public byte[] getRawData() {
            if (DatalogSessionHealthOverlayData.this.storePebbleHealthRawRecord()) {
                return this.rawData;
            }
            return null;
        }
    }
}
