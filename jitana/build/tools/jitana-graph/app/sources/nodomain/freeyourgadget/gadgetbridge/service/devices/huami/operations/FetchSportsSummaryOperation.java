package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSportsActivityType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchSportsSummaryOperation extends AbstractFetchOperation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) FetchSportsSummaryOperation.class);
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream(140);

    public FetchSportsSummaryOperation(HuamiSupport support) {
        super(support);
        setName("fetching sport summaries");
    }

    /* access modifiers changed from: protected */
    public void startFetching(TransactionBuilder builder) {
        Logger logger = LOG;
        logger.info("start" + getName());
        startFetching(builder, (byte) 5, getLastSuccessfulSyncTime());
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0054, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0055, code lost:
        if (r3 != null) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005f, code lost:
        throw r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleActivityFetchFinish(boolean r9) {
        /*
            r8 = this;
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = r8.getName()
            r1.append(r2)
            java.lang.String r2 = " has finished round "
            r1.append(r2)
            int r2 = r8.fetchCount
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.info(r1)
            r0 = 0
            r1 = 3
            r2 = 1
            if (r9 == 0) goto L_0x006a
            java.io.ByteArrayOutputStream r3 = r8.buffer
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary r0 = r8.parseSummary(r3)
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0060 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r3.getDaoSession()     // Catch:{ all -> 0x0052 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r5 = r8.getDevice()     // Catch:{ all -> 0x0052 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r5 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r5, r4)     // Catch:{ all -> 0x0052 }
            nodomain.freeyourgadget.gadgetbridge.entities.User r6 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r4)     // Catch:{ all -> 0x0052 }
            r0.setDevice(r5)     // Catch:{ all -> 0x0052 }
            r0.setUser(r6)     // Catch:{ all -> 0x0052 }
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummaryDao r7 = r4.getBaseActivitySummaryDao()     // Catch:{ all -> 0x0052 }
            r7.insertOrReplace(r0)     // Catch:{ all -> 0x0052 }
            if (r3 == 0) goto L_0x0051
            r3.close()     // Catch:{ Exception -> 0x0060 }
        L_0x0051:
            goto L_0x006a
        L_0x0052:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0054 }
        L_0x0054:
            r5 = move-exception
            if (r3 == 0) goto L_0x005f
            r3.close()     // Catch:{ all -> 0x005b }
            goto L_0x005f
        L_0x005b:
            r6 = move-exception
            r4.addSuppressed(r6)     // Catch:{ Exception -> 0x0060 }
        L_0x005f:
            throw r5     // Catch:{ Exception -> 0x0060 }
        L_0x0060:
            r3 = move-exception
            android.content.Context r4 = r8.getContext()
            java.lang.String r5 = "Error saving activity summary"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r4, r5, r2, r1, r3)
        L_0x006a:
            super.handleActivityFetchFinish(r9)
            if (r0 == 0) goto L_0x009f
            nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchSportsDetailsOperation r3 = new nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchSportsDetailsOperation
            nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport r4 = r8.getSupport()
            nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport r4 = (nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport) r4
            java.lang.String r5 = r8.getLastSyncTimeKey()
            r3.<init>(r0, r4, r5)
            r3.perform()     // Catch:{ IOException -> 0x0082 }
            goto L_0x009f
        L_0x0082:
            r4 = move-exception
            android.content.Context r5 = r8.getContext()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Unable to fetch activity details: "
            r6.append(r7)
            java.lang.String r7 = r4.getMessage()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r5, r6, r2, r1, r4)
        L_0x009f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchSportsSummaryOperation.handleActivityFetchFinish(boolean):void");
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Logger logger = LOG;
        logger.warn("characteristic read: " + characteristic.getUuid() + ": " + Logging.formatBytes(characteristic.getValue()));
        return super.onCharacteristicRead(gatt, characteristic, status);
    }

    /* access modifiers changed from: protected */
    public void handleActivityNotif(byte[] value) {
        Logger logger = LOG;
        logger.warn("sports summary data: " + Logging.formatBytes(value));
        if (!isOperationRunning()) {
            Logger logger2 = LOG;
            logger2.error("ignoring activity data notification because operation is not running. Data length: " + value.length);
            ((HuamiSupport) getSupport()).logMessageContent(value);
        } else if (value.length < 2) {
            Logger logger3 = LOG;
            logger3.error("unexpected sports summary data length: " + value.length);
            ((HuamiSupport) getSupport()).logMessageContent(value);
        } else if (((byte) (this.lastPacketCounter + 1)) == value[0]) {
            this.lastPacketCounter = (byte) (this.lastPacketCounter + 1);
            bufferActivityData(value);
        } else {
            C1238GB.toast("Error " + getName() + ", invalid package counter: " + value[0] + ", last was: " + this.lastPacketCounter, 1, 3);
            handleActivityFetchFinish(false);
        }
    }

    /* access modifiers changed from: protected */
    public void bufferActivityData(byte[] value) {
        this.buffer.write(value, 1, value.length - 1);
    }

    private BaseActivitySummary parseSummary(ByteArrayOutputStream stream) {
        float minAltitude;
        int steps;
        int activeSeconds;
        float caloriesBurnt;
        float maxSpeed;
        float minPace;
        float maxPace;
        float totalStride;
        BaseActivitySummary summary = new BaseActivitySummary();
        ByteBuffer buffer2 = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
        short version = buffer2.getShort();
        Logger logger = LOG;
        logger.debug("Got sport summary version " + version + "total bytes=" + buffer2.capacity());
        int activityKind = 0;
        try {
            activityKind = HuamiSportsActivityType.fromCode(BLETypeConversions.toUnsigned(buffer2.getShort())).toActivityKind();
        } catch (Exception ex) {
            Logger logger2 = LOG;
            logger2.error("Error mapping activity kind: " + ex.getMessage(), (Throwable) ex);
        }
        summary.setActivityKind(activityKind);
        long timestamp_start = BLETypeConversions.toUnsigned(buffer2.getInt()) * 1000;
        long timestamp_end = BLETypeConversions.toUnsigned(buffer2.getInt()) * 1000;
        long duration = timestamp_end - timestamp_start;
        summary.setStartTime(new Date(getLastStartTimestamp().getTimeInMillis()));
        summary.setEndTime(new Date(getLastStartTimestamp().getTimeInMillis() + duration));
        int baseLongitude = buffer2.getInt();
        int baseLatitude = buffer2.getInt();
        int baseAltitude = buffer2.getInt();
        summary.setBaseLongitude(Integer.valueOf(baseLongitude));
        summary.setBaseLatitude(Integer.valueOf(baseLatitude));
        summary.setBaseAltitude(Integer.valueOf(baseAltitude));
        float distanceMeters = buffer2.getFloat();
        float ascentMeters = buffer2.getFloat();
        float descentMeters = buffer2.getFloat();
        int i = baseLongitude;
        float maxAltitude = buffer2.getFloat();
        short s = version;
        float minAltitude2 = buffer2.getFloat();
        int i2 = buffer2.getInt();
        int i3 = buffer2.getInt();
        int i4 = buffer2.getInt();
        int i5 = buffer2.getInt();
        long j = timestamp_start;
        int steps2 = buffer2.getInt();
        int activeSeconds2 = buffer2.getInt();
        long j2 = duration;
        float caloriesBurnt2 = buffer2.getFloat();
        float maxSpeed2 = buffer2.getFloat();
        long j3 = timestamp_end;
        float minPace2 = buffer2.getFloat();
        float maxPace2 = buffer2.getFloat();
        int i6 = baseLatitude;
        float totalStride2 = buffer2.getFloat();
        buffer2.getInt();
        int i7 = baseAltitude;
        BaseActivitySummary summary2 = summary;
        if (activityKind == 64) {
            float averageStrokeDistance = buffer2.getFloat();
            int i8 = activityKind;
            float averageStrokesPerSecond = buffer2.getFloat();
            totalStride = totalStride2;
            float totalStride3 = buffer2.getFloat();
            maxPace = maxPace2;
            short strokes = buffer2.getShort();
            minPace = minPace2;
            short swolfIndex = buffer2.getShort();
            maxSpeed = maxSpeed2;
            byte swimStyle = buffer2.get();
            caloriesBurnt = caloriesBurnt2;
            byte laps = buffer2.get();
            buffer2.getInt();
            buffer2.getInt();
            buffer2.getShort();
            activeSeconds = activeSeconds2;
            Logger logger3 = LOG;
            steps = steps2;
            StringBuilder sb = new StringBuilder();
            minAltitude = minAltitude2;
            sb.append("unused swim data:\naverageStrokeDistance=");
            sb.append(averageStrokeDistance);
            sb.append("\naverageStrokesPerSecond=");
            sb.append(averageStrokesPerSecond);
            sb.append("\naverageLapPace");
            sb.append(totalStride3);
            sb.append("\nstrokes=");
            sb.append(strokes);
            sb.append("\nswolfIndex=");
            sb.append(swolfIndex);
            sb.append("\nswimStyle=");
            sb.append(swimStyle);
            sb.append("\nlaps=");
            sb.append(laps);
            sb.append("");
            logger3.debug(sb.toString());
        } else {
            minAltitude = minAltitude2;
            int i9 = activityKind;
            steps = steps2;
            activeSeconds = activeSeconds2;
            caloriesBurnt = caloriesBurnt2;
            maxSpeed = maxSpeed2;
            minPace = minPace2;
            maxPace = maxPace2;
            totalStride = totalStride2;
            buffer2.getInt();
            buffer2.getInt();
            buffer2.getInt();
            buffer2.getInt();
            Logger logger4 = LOG;
            logger4.debug("unused non-swim data:\nascentSeconds=" + (buffer2.getInt() / 1000) + "\ndescentSeconds=" + (buffer2.getInt() / 1000) + "\nflatSeconds=" + (buffer2.getInt() / 1000) + "");
        }
        int ascentSeconds = buffer2.getShort();
        short averageKMPaceSeconds = buffer2.getShort();
        short averageStride = buffer2.getShort();
        Logger logger5 = LOG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("unused common:\ndistanceMeters=");
        sb2.append(distanceMeters);
        sb2.append("\nascentMeters=");
        sb2.append(ascentMeters);
        sb2.append("\ndescentMeters=");
        sb2.append(descentMeters);
        sb2.append("\nmaxAltitude=");
        sb2.append(maxAltitude);
        sb2.append("\nminAltitude=");
        sb2.append(minAltitude);
        sb2.append("\nsteps=");
        sb2.append(steps);
        sb2.append("\nactiveSeconds=");
        sb2.append(activeSeconds);
        sb2.append("\ncaloriesBurnt=");
        sb2.append(caloriesBurnt);
        sb2.append("\nmaxSpeed=");
        sb2.append(maxSpeed);
        float f = maxAltitude;
        sb2.append("\nminPace=");
        sb2.append(minPace);
        sb2.append("\nmaxPace=");
        sb2.append(maxPace);
        sb2.append("\ntotalStride=");
        sb2.append(totalStride);
        sb2.append("\naverageHR=");
        sb2.append(ascentSeconds);
        sb2.append("\naverageKMPaceSeconds=");
        sb2.append(averageKMPaceSeconds);
        sb2.append("\naverageStride=");
        sb2.append(averageStride);
        sb2.append("");
        logger5.debug(sb2.toString());
        return summary2;
    }

    /* access modifiers changed from: protected */
    public String getLastSyncTimeKey() {
        return "lastSportsActivityTimeMillis";
    }
}
