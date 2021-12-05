package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary;
import nodomain.freeyourgadget.gadgetbridge.export.ActivityTrackExporter;
import nodomain.freeyourgadget.gadgetbridge.export.GPXExporter;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchSportsDetailsOperation extends AbstractFetchOperation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) FetchSportsDetailsOperation.class);
    private ByteArrayOutputStream buffer;
    private final String lastSyncTimeKey;
    private final BaseActivitySummary summary;

    FetchSportsDetailsOperation(BaseActivitySummary summary2, HuamiSupport support, String lastSyncTimeKey2) {
        super(support);
        setName("fetching sport details");
        this.summary = summary2;
        this.lastSyncTimeKey = lastSyncTimeKey2;
    }

    /* access modifiers changed from: protected */
    public void startFetching(TransactionBuilder builder) {
        Logger logger = LOG;
        logger.info("start " + getName());
        this.buffer = new ByteArrayOutputStream(1024);
        startFetching(builder, (byte) 6, getLastSuccessfulSyncTime());
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00e7, code lost:
        r10 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00e8, code lost:
        if (r8 != null) goto L_0x00ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r8.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00f2, code lost:
        throw r10;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleActivityFetchFinish(boolean r13) {
        /*
            r12 = this;
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = r12.getName()
            r1.append(r2)
            java.lang.String r2 = " has finished round "
            r1.append(r2)
            int r2 = r12.fetchCount
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.info(r1)
            if (r13 == 0) goto L_0x012b
            nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiActivityDetailsParser r0 = new nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiActivityDetailsParser
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary r1 = r12.summary
            r0.<init>(r1)
            r1 = 0
            r0.setSkipCounterByte(r1)
            r1 = 3
            r2 = 1
            java.io.ByteArrayOutputStream r3 = r12.buffer     // Catch:{ Exception -> 0x010e }
            byte[] r3 = r3.toByteArray()     // Catch:{ Exception -> 0x010e }
            nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack r3 = r0.parse(r3)     // Catch:{ Exception -> 0x010e }
            nodomain.freeyourgadget.gadgetbridge.export.ActivityTrackExporter r4 = r12.createExporter()     // Catch:{ Exception -> 0x010e }
            java.lang.String r5 = "track"
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary r6 = r12.summary     // Catch:{ Exception -> 0x010e }
            int r6 = r6.getActivityKind()     // Catch:{ Exception -> 0x010e }
            r7 = 16
            if (r6 == r7) goto L_0x007c
            r7 = 32
            if (r6 == r7) goto L_0x006f
            r7 = 64
            if (r6 == r7) goto L_0x0062
            r7 = 128(0x80, float:1.794E-43)
            if (r6 == r7) goto L_0x0055
            goto L_0x0089
        L_0x0055:
            android.content.Context r6 = r12.getContext()     // Catch:{ Exception -> 0x010e }
            r7 = 2131755085(0x7f10004d, float:1.914104E38)
            java.lang.String r6 = r6.getString(r7)     // Catch:{ Exception -> 0x010e }
            r5 = r6
            goto L_0x0089
        L_0x0062:
            android.content.Context r6 = r12.getContext()     // Catch:{ Exception -> 0x010e }
            r7 = 2131755092(0x7f100054, float:1.9141054E38)
            java.lang.String r6 = r6.getString(r7)     // Catch:{ Exception -> 0x010e }
            r5 = r6
            goto L_0x0089
        L_0x006f:
            android.content.Context r6 = r12.getContext()     // Catch:{ Exception -> 0x010e }
            r7 = 2131755095(0x7f100057, float:1.914106E38)
            java.lang.String r6 = r6.getString(r7)     // Catch:{ Exception -> 0x010e }
            r5 = r6
            goto L_0x0089
        L_0x007c:
            android.content.Context r6 = r12.getContext()     // Catch:{ Exception -> 0x010e }
            r7 = 2131755091(0x7f100053, float:1.9141051E38)
            java.lang.String r6 = r6.getString(r7)     // Catch:{ Exception -> 0x010e }
            r5 = r6
        L_0x0089:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x010e }
            r6.<init>()     // Catch:{ Exception -> 0x010e }
            java.lang.String r7 = "gadgetbridge-"
            r6.append(r7)     // Catch:{ Exception -> 0x010e }
            java.lang.String r7 = r5.toLowerCase()     // Catch:{ Exception -> 0x010e }
            r6.append(r7)     // Catch:{ Exception -> 0x010e }
            java.lang.String r7 = "-"
            r6.append(r7)     // Catch:{ Exception -> 0x010e }
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary r7 = r12.summary     // Catch:{ Exception -> 0x010e }
            java.util.Date r7 = r7.getStartTime()     // Catch:{ Exception -> 0x010e }
            java.lang.String r7 = nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils.formatIso8601(r7)     // Catch:{ Exception -> 0x010e }
            r6.append(r7)     // Catch:{ Exception -> 0x010e }
            java.lang.String r7 = ".gpx"
            r6.append(r7)     // Catch:{ Exception -> 0x010e }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x010e }
            java.lang.String r6 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.makeValidFileName(r6)     // Catch:{ Exception -> 0x010e }
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x010e }
            java.io.File r8 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ Exception -> 0x010e }
            r7.<init>(r8, r6)     // Catch:{ Exception -> 0x010e }
            r4.performExport(r3, r7)     // Catch:{ GPXTrackEmptyException -> 0x00f3 }
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r8 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ GPXTrackEmptyException -> 0x00f3 }
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary r9 = r12.summary     // Catch:{ all -> 0x00e5 }
            java.lang.String r10 = r7.getAbsolutePath()     // Catch:{ all -> 0x00e5 }
            r9.setGpxTrack(r10)     // Catch:{ all -> 0x00e5 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r9 = r8.getDaoSession()     // Catch:{ all -> 0x00e5 }
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummaryDao r9 = r9.getBaseActivitySummaryDao()     // Catch:{ all -> 0x00e5 }
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary r10 = r12.summary     // Catch:{ all -> 0x00e5 }
            r9.update(r10)     // Catch:{ all -> 0x00e5 }
            if (r8 == 0) goto L_0x00e4
            r8.close()     // Catch:{ GPXTrackEmptyException -> 0x00f3 }
        L_0x00e4:
            goto L_0x00fd
        L_0x00e5:
            r9 = move-exception
            throw r9     // Catch:{ all -> 0x00e7 }
        L_0x00e7:
            r10 = move-exception
            if (r8 == 0) goto L_0x00f2
            r8.close()     // Catch:{ all -> 0x00ee }
            goto L_0x00f2
        L_0x00ee:
            r11 = move-exception
            r9.addSuppressed(r11)     // Catch:{ GPXTrackEmptyException -> 0x00f3 }
        L_0x00f2:
            throw r10     // Catch:{ GPXTrackEmptyException -> 0x00f3 }
        L_0x00f3:
            r8 = move-exception
            android.content.Context r9 = r12.getContext()     // Catch:{ Exception -> 0x010e }
            java.lang.String r10 = "This activity does not contain GPX tracks."
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r9, r10, r2, r1, r8)     // Catch:{ Exception -> 0x010e }
        L_0x00fd:
            java.util.GregorianCalendar r8 = nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions.createCalendar()     // Catch:{ Exception -> 0x010e }
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary r9 = r12.summary     // Catch:{ Exception -> 0x010e }
            java.util.Date r9 = r9.getEndTime()     // Catch:{ Exception -> 0x010e }
            r8.setTime(r9)     // Catch:{ Exception -> 0x010e }
            r12.saveLastSyncTimestamp(r8)     // Catch:{ Exception -> 0x010e }
            goto L_0x012b
        L_0x010e:
            r3 = move-exception
            android.content.Context r4 = r12.getContext()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error getting activity details: "
            r5.append(r6)
            java.lang.String r6 = r3.getMessage()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r4, r5, r2, r1, r3)
        L_0x012b:
            super.handleActivityFetchFinish(r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchSportsDetailsOperation.handleActivityFetchFinish(boolean):void");
    }

    private ActivityTrackExporter createExporter() {
        GPXExporter exporter = new GPXExporter();
        exporter.setCreator(GBApplication.app().getNameAndVersion());
        return exporter;
    }

    /* access modifiers changed from: protected */
    public void handleActivityNotif(byte[] value) {
        Logger logger = LOG;
        logger.warn("sports details: " + Logging.formatBytes(value));
        if (!isOperationRunning()) {
            Logger logger2 = LOG;
            logger2.error("ignoring sports details notification because operation is not running. Data length: " + value.length);
            ((HuamiSupport) getSupport()).logMessageContent(value);
        } else if (value.length < 2) {
            Logger logger3 = LOG;
            logger3.error("unexpected sports details data length: " + value.length);
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

    /* access modifiers changed from: protected */
    public String getLastSyncTimeKey() {
        return this.lastSyncTimeKey;
    }

    /* access modifiers changed from: protected */
    public GregorianCalendar getLastSuccessfulSyncTime() {
        GregorianCalendar calendar = BLETypeConversions.createCalendar();
        calendar.setTime(this.summary.getStartTime());
        return calendar;
    }
}
