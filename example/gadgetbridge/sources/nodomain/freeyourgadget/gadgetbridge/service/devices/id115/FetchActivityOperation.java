package nodomain.freeyourgadget.gadgetbridge.service.devices.id115;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchActivityOperation extends AbstractID115Operation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) FetchActivityOperation.class);
    private byte expectedCmd;
    private byte expectedSeq;
    private ArrayList<byte[]> packets;

    protected FetchActivityOperation(ID115Support support) {
        super(support);
    }

    /* access modifiers changed from: package-private */
    public boolean isHealthOperation() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(8);
        outputStream.write(3);
        outputStream.write(1);
        outputStream.write(0);
        outputStream.write(0);
        byte[] cmd = outputStream.toByteArray();
        this.expectedCmd = 3;
        this.expectedSeq = 1;
        this.packets = new ArrayList<>();
        TransactionBuilder builder = performInitialized("send activity fetch request");
        builder.write(this.controlCharacteristic, cmd);
        builder.queue(getQueue());
    }

    /* access modifiers changed from: package-private */
    public void handleResponse(byte[] data) {
        byte b;
        if (!isOperationRunning()) {
            Logger logger = LOG;
            logger.error("ignoring notification because operation is not running. Data length: " + data.length);
            ((ID115Support) getSupport()).logMessageContent(data);
        } else if (data.length < 4) {
            LOG.warn("short GATT response");
        } else if (data[0] != 8) {
        } else {
            if (data[1] == -18) {
                LOG.info("Activity data transfer has finished.");
                parseAndStore();
                operationFinished();
            } else if (data[1] == this.expectedCmd && data[2] == (b = this.expectedSeq)) {
                this.expectedSeq = (byte) (b + 1);
                byte[] payload = new byte[(data.length - 4)];
                System.arraycopy(data, 4, payload, 0, payload.length);
                this.packets.add(payload);
            } else {
                C1238GB.toast(getContext(), "Error fetching ID115 activity data, you may need to connect and disconnect", 1, 3);
                operationFinished();
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00fc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00fd, code lost:
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00fe, code lost:
        if (r2 != null) goto L_0x0100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0104, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        r3.addSuppressed(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x010a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:26:0x00ef, B:32:0x00fb, B:37:0x0100] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void parseAndStore() {
        /*
            r22 = this;
            r1 = r22
            java.util.ArrayList<byte[]> r0 = r1.packets
            int r0 = r0.size()
            r2 = 1
            if (r0 > r2) goto L_0x000c
            return
        L_0x000c:
            java.util.ArrayList<byte[]> r0 = r1.packets
            r3 = 0
            java.lang.Object r0 = r0.get(r3)
            r4 = r0
            byte[] r4 = (byte[]) r4
            byte r0 = r4[r2]
            r0 = r0 & 255(0xff, float:3.57E-43)
            int r0 = r0 << 8
            byte r5 = r4[r3]
            r5 = r5 & 255(0xff, float:3.57E-43)
            r5 = r5 | r0
            r0 = 2
            byte r0 = r4[r0]
            r6 = r0 & 255(0xff, float:3.57E-43)
            r7 = 3
            byte r0 = r4[r7]
            r8 = r0 & 255(0xff, float:3.57E-43)
            r0 = 6
            byte r0 = r4[r0]
            r9 = r0 & 255(0xff, float:3.57E-43)
            java.util.GregorianCalendar r0 = new java.util.GregorianCalendar
            int r10 = r6 + -1
            r0.<init>(r5, r10, r8)
            r10 = r0
            long r11 = r10.getTimeInMillis()
            r13 = 1000(0x3e8, double:4.94E-321)
            long r11 = r11 / r13
            int r0 = (int) r11
            int r11 = r9 * 60
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r13 = 2
            r14 = r0
        L_0x0049:
            java.util.ArrayList<byte[]> r0 = r1.packets
            int r0 = r0.size()
            if (r13 >= r0) goto L_0x0082
            java.util.ArrayList<byte[]> r0 = r1.packets
            java.lang.Object r0 = r0.get(r13)
            byte[] r0 = (byte[]) r0
            r15 = 0
        L_0x005a:
            int r7 = r0.length
            r2 = 5
            int r7 = r7 - r2
            if (r15 > r7) goto L_0x007c
            byte[] r2 = new byte[r2]
            int r7 = r2.length
            java.lang.System.arraycopy(r0, r15, r2, r3, r7)
            nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySample r7 = r1.parseSample(r2)
            if (r7 == 0) goto L_0x0075
            r7.setTimestamp(r14)
            r3 = 1
            r7.setRawKind(r3)
            r12.add(r7)
        L_0x0075:
            int r14 = r14 + r11
            int r15 = r15 + 5
            r2 = 1
            r3 = 0
            r7 = 3
            goto L_0x005a
        L_0x007c:
            int r13 = r13 + 1
            r2 = 1
            r3 = 0
            r7 = 3
            goto L_0x0049
        L_0x0082:
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x010c }
            r2 = r0
            r0 = 0
            nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySample[] r3 = new nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySample[r0]     // Catch:{ all -> 0x00f6 }
            java.lang.Object[] r3 = r12.toArray(r3)     // Catch:{ all -> 0x00f6 }
            nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySample[] r3 = (nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySample[]) r3     // Catch:{ all -> 0x00f6 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r7 = r2.getDaoSession()     // Catch:{ all -> 0x00f6 }
            nodomain.freeyourgadget.gadgetbridge.entities.User r7 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r7)     // Catch:{ all -> 0x00f6 }
            java.lang.Long r7 = r7.getId()     // Catch:{ all -> 0x00f6 }
            long r16 = r7.longValue()     // Catch:{ all -> 0x00f6 }
            r18 = r16
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r7 = r22.getDevice()     // Catch:{ all -> 0x00f6 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r13 = r2.getDaoSession()     // Catch:{ all -> 0x00f6 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r7 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r7, r13)     // Catch:{ all -> 0x00f6 }
            java.lang.Long r7 = r7.getId()     // Catch:{ all -> 0x00f6 }
            long r16 = r7.longValue()     // Catch:{ all -> 0x00f6 }
            r20 = r16
            int r7 = r3.length     // Catch:{ all -> 0x00f6 }
        L_0x00b9:
            if (r0 >= r7) goto L_0x00d6
            r13 = r3[r0]     // Catch:{ all -> 0x00f6 }
            r15 = r4
            r16 = r5
            r4 = r18
            r13.setUserId(r4)     // Catch:{ all -> 0x00f3 }
            r17 = r4
            r4 = r20
            r13.setDeviceId(r4)     // Catch:{ all -> 0x00f3 }
            int r0 = r0 + 1
            r20 = r4
            r4 = r15
            r5 = r16
            r18 = r17
            goto L_0x00b9
        L_0x00d6:
            r15 = r4
            r16 = r5
            r17 = r18
            r4 = r20
            nodomain.freeyourgadget.gadgetbridge.devices.id115.ID115SampleProvider r0 = new nodomain.freeyourgadget.gadgetbridge.devices.id115.ID115SampleProvider     // Catch:{ all -> 0x00f3 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r7 = r22.getDevice()     // Catch:{ all -> 0x00f3 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r13 = r2.getDaoSession()     // Catch:{ all -> 0x00f3 }
            r0.<init>(r7, r13)     // Catch:{ all -> 0x00f3 }
            r0.addGBActivitySamples(r3)     // Catch:{ all -> 0x00f3 }
            if (r2 == 0) goto L_0x00f2
            r2.close()     // Catch:{ Exception -> 0x010a }
        L_0x00f2:
            goto L_0x012e
        L_0x00f3:
            r0 = move-exception
            r3 = r0
            goto L_0x00fb
        L_0x00f6:
            r0 = move-exception
            r15 = r4
            r16 = r5
            r3 = r0
        L_0x00fb:
            throw r3     // Catch:{ all -> 0x00fc }
        L_0x00fc:
            r0 = move-exception
            r4 = r0
            if (r2 == 0) goto L_0x0109
            r2.close()     // Catch:{ all -> 0x0104 }
            goto L_0x0109
        L_0x0104:
            r0 = move-exception
            r5 = r0
            r3.addSuppressed(r5)     // Catch:{ Exception -> 0x010a }
        L_0x0109:
            throw r4     // Catch:{ Exception -> 0x010a }
        L_0x010a:
            r0 = move-exception
            goto L_0x0110
        L_0x010c:
            r0 = move-exception
            r15 = r4
            r16 = r5
        L_0x0110:
            android.content.Context r2 = r22.getContext()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error saving activity data: "
            r3.append(r4)
            java.lang.String r4 = r0.getLocalizedMessage()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 3
            r5 = 1
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r2, (java.lang.String) r3, (int) r5, (int) r4)
        L_0x012e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.id115.FetchActivityOperation.parseAndStore():void");
    }

    /* access modifiers changed from: package-private */
    public ID115ActivitySample parseSample(byte[] data) {
        int d01 = ((data[1] & 255) << 8) | (data[0] & 255);
        int d12 = (data[1] & 255) | ((data[2] & 255) << 8);
        int d23 = (data[2] & 255) | ((data[3] & 255) << 8);
        int stepCount = (d01 >> 2) & 4095;
        int activeTime = (d12 >> 6) & 15;
        int calories = (d23 >> 2) & 1023;
        int distance = ((data[3] & 255) | ((data[4] & 255) << 8)) >> 4;
        if (stepCount == 0) {
            return null;
        }
        ID115ActivitySample sample = new ID115ActivitySample();
        sample.setSteps(stepCount);
        sample.setActiveTimeMinutes(Integer.valueOf(activeTime));
        sample.setCaloriesBurnt(Integer.valueOf(calories));
        sample.setDistanceMeters(Integer.valueOf(distance));
        return sample;
    }
}
