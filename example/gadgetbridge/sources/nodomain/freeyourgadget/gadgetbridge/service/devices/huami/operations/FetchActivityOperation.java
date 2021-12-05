package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import android.text.format.DateUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchActivityOperation extends AbstractFetchOperation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) FetchActivityOperation.class);
    private List<MiBandActivitySample> samples = new ArrayList(1440);

    public FetchActivityOperation(HuamiSupport support) {
        super(support);
        setName("fetching activity data");
    }

    /* access modifiers changed from: protected */
    public void startFetching() throws IOException {
        this.samples.clear();
        super.startFetching();
    }

    /* access modifiers changed from: protected */
    public void startFetching(TransactionBuilder builder) {
        String ensureNotNull = StringUtils.ensureNotNull(builder.getTaskName());
        startFetching(builder, (byte) 1, getLastSuccessfulSyncTime());
    }

    /* access modifiers changed from: protected */
    public void handleActivityFetchFinish(boolean success) {
        Logger logger = LOG;
        logger.info(getName() + " has finished round " + this.fetchCount);
        GregorianCalendar lastSyncTimestamp = saveSamples();
        if (lastSyncTimestamp != null && needsAnotherFetch(lastSyncTimestamp)) {
            try {
                startFetching();
                return;
            } catch (IOException ex) {
                Logger logger2 = LOG;
                logger2.error("Error starting another round of " + getName(), (Throwable) ex);
            }
        }
        super.handleActivityFetchFinish(success);
        C1238GB.signalActivityDataFinish();
    }

    private boolean needsAnotherFetch(GregorianCalendar lastSyncTimestamp) {
        if (this.fetchCount > 5) {
            LOG.warn("Already have 5 fetch rounds, not doing another one.");
            return false;
        } else if (DateUtils.isToday(lastSyncTimestamp.getTimeInMillis())) {
            LOG.info("Hopefully no further fetch needed, last synced timestamp is from today.");
            return false;
        } else if (lastSyncTimestamp.getTimeInMillis() > System.currentTimeMillis()) {
            Logger logger = LOG;
            logger.warn("Not doing another fetch since last synced timestamp is in the future: " + DateTimeUtils.formatDateTime(lastSyncTimestamp.getTime()));
            return false;
        } else {
            Logger logger2 = LOG;
            logger2.info("Doing another fetch since last sync timestamp is still too old: " + DateTimeUtils.formatDateTime(lastSyncTimestamp.getTime()));
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x009c, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x009d, code lost:
        if (r1 != null) goto L_0x009f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00a7, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.GregorianCalendar saveSamples() {
        /*
            r13 = this;
            java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample> r0 = r13.samples
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x00c1
            r0 = 1
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00aa }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r2 = r1.getDaoSession()     // Catch:{ all -> 0x009a }
            nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandSampleProvider r3 = new nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandSampleProvider     // Catch:{ all -> 0x009a }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r13.getDevice()     // Catch:{ all -> 0x009a }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x009a }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r13.getDevice()     // Catch:{ all -> 0x009a }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r4 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r4, r2)     // Catch:{ all -> 0x009a }
            nodomain.freeyourgadget.gadgetbridge.entities.User r5 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r2)     // Catch:{ all -> 0x009a }
            java.util.Calendar r6 = r13.startTimestamp     // Catch:{ all -> 0x009a }
            java.lang.Object r6 = r6.clone()     // Catch:{ all -> 0x009a }
            java.util.GregorianCalendar r6 = (java.util.GregorianCalendar) r6     // Catch:{ all -> 0x009a }
            java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample> r7 = r13.samples     // Catch:{ all -> 0x009a }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x009a }
        L_0x0034:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x009a }
            if (r8 == 0) goto L_0x005f
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x009a }
            nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample r8 = (nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample) r8     // Catch:{ all -> 0x009a }
            r8.setDevice(r4)     // Catch:{ all -> 0x009a }
            r8.setUser(r5)     // Catch:{ all -> 0x009a }
            long r9 = r6.getTimeInMillis()     // Catch:{ all -> 0x009a }
            r11 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 / r11
            int r10 = (int) r9     // Catch:{ all -> 0x009a }
            r8.setTimestamp(r10)     // Catch:{ all -> 0x009a }
            r8.setProvider(r3)     // Catch:{ all -> 0x009a }
            org.slf4j.Logger r9 = LOG     // Catch:{ all -> 0x009a }
            r9.isDebugEnabled()     // Catch:{ all -> 0x009a }
            r9 = 12
            r6.add(r9, r0)     // Catch:{ all -> 0x009a }
            goto L_0x0034
        L_0x005f:
            java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample> r7 = r13.samples     // Catch:{ all -> 0x009a }
            r8 = 0
            nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample[] r8 = new nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample[r8]     // Catch:{ all -> 0x009a }
            java.lang.Object[] r7 = r7.toArray(r8)     // Catch:{ all -> 0x009a }
            nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample[] r7 = (nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample[]) r7     // Catch:{ all -> 0x009a }
            r3.addGBActivitySamples(r7)     // Catch:{ all -> 0x009a }
            r13.saveLastSyncTimestamp(r6)     // Catch:{ all -> 0x009a }
            org.slf4j.Logger r7 = LOG     // Catch:{ all -> 0x009a }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x009a }
            r8.<init>()     // Catch:{ all -> 0x009a }
            java.lang.String r9 = "Mi2 activity data: last sample timestamp: "
            r8.append(r9)     // Catch:{ all -> 0x009a }
            java.util.Date r9 = r6.getTime()     // Catch:{ all -> 0x009a }
            java.lang.String r9 = nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils.formatDateTime(r9)     // Catch:{ all -> 0x009a }
            r8.append(r9)     // Catch:{ all -> 0x009a }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x009a }
            r7.info(r8)     // Catch:{ all -> 0x009a }
            if (r1 == 0) goto L_0x0094
            r1.close()     // Catch:{ Exception -> 0x00aa }
        L_0x0094:
            java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample> r0 = r13.samples
            r0.clear()
            return r6
        L_0x009a:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x009c }
        L_0x009c:
            r3 = move-exception
            if (r1 == 0) goto L_0x00a7
            r1.close()     // Catch:{ all -> 0x00a3 }
            goto L_0x00a7
        L_0x00a3:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x00aa }
        L_0x00a7:
            throw r3     // Catch:{ Exception -> 0x00aa }
        L_0x00a8:
            r0 = move-exception
            goto L_0x00bb
        L_0x00aa:
            r1 = move-exception
            android.content.Context r2 = r13.getContext()     // Catch:{ all -> 0x00a8 }
            java.lang.String r3 = "Error saving activity samples"
            r4 = 3
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r2, (java.lang.String) r3, (int) r0, (int) r4)     // Catch:{ all -> 0x00a8 }
            java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample> r0 = r13.samples
            r0.clear()
            goto L_0x00c1
        L_0x00bb:
            java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample> r1 = r13.samples
            r1.clear()
            throw r0
        L_0x00c1:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchActivityOperation.saveSamples():java.util.GregorianCalendar");
    }

    /* access modifiers changed from: protected */
    public void handleActivityNotif(byte[] value) {
        if (!isOperationRunning()) {
            Logger logger = LOG;
            logger.error("ignoring activity data notification because operation is not running. Data length: " + value.length);
            ((HuamiSupport) getSupport()).logMessageContent(value);
        } else if (value.length % 4 != 1) {
            C1238GB.toast("Error " + getName() + ", unexpected package length: " + value.length, 1, 3);
            handleActivityFetchFinish(false);
        } else if (((byte) (this.lastPacketCounter + 1)) == value[0]) {
            this.lastPacketCounter = (byte) (this.lastPacketCounter + 1);
            bufferActivityData(value);
        } else {
            C1238GB.toast("Error " + getName() + ", invalid package counter: " + value[0], 1, 3);
            handleActivityFetchFinish(false);
        }
    }

    /* access modifiers changed from: protected */
    public void bufferActivityData(byte[] value) {
        int len = value.length;
        if (len % 4 == 1) {
            for (int i = 1; i < len; i += 4) {
                this.samples.add(createSample(value[i], value[i + 1], value[i + 2], value[i + 3]));
            }
            return;
        }
        throw new AssertionError("Unexpected activity array size: " + len);
    }

    private MiBandActivitySample createSample(byte category, byte intensity, byte steps, byte heartrate) {
        MiBandActivitySample sample = new MiBandActivitySample();
        sample.setRawKind(category & 255);
        sample.setRawIntensity(intensity & 255);
        sample.setSteps(steps & 255);
        sample.setHeartRate(heartrate & 255);
        return sample;
    }

    /* access modifiers changed from: protected */
    public String getLastSyncTimeKey() {
        return "lastSyncTimeMillis";
    }
}
