package nodomain.freeyourgadget.gadgetbridge.devices.hplus;

import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class HPlusHealthSampleProvider extends AbstractSampleProvider<HPlusHealthActivitySample> {
    private GBDevice mDevice;
    private DaoSession mSession;

    public HPlusHealthSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
        this.mSession = session;
        this.mDevice = device;
    }

    public int normalizeType(int rawType) {
        if (rawType == 0) {
            return 0;
        }
        switch (rawType) {
            case 100:
            case 101:
            case 102:
            case 103:
                return 0;
            default:
                return rawType;
        }
    }

    public int toRawActivityKind(int activityKind) {
        if (activityKind == 2) {
            return 2;
        }
        if (activityKind != 4) {
            return 102;
        }
        return 4;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return HPlusHealthActivitySampleDao.Properties.Timestamp;
    }

    public HPlusHealthActivitySample createActivitySample() {
        return new HPlusHealthActivitySample();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return null;
    }

    public float normalizeIntensity(int rawIntensity) {
        return ((float) rawIntensity) / 100.0f;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return HPlusHealthActivitySampleDao.Properties.DeviceId;
    }

    public AbstractDao<HPlusHealthActivitySample, ?> getSampleDao() {
        return getSession().getHPlusHealthActivitySampleDao();
    }

    public List<HPlusHealthActivitySample> getActivityamples(int timestamp_from, int timestamp_to) {
        return getAllActivitySamples(timestamp_from, timestamp_to);
    }

    public List<HPlusHealthActivitySample> getSleepSamples(int timestamp_from, int timestamp_to) {
        return getAllActivitySamples(timestamp_from, timestamp_to);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0162, code lost:
        if (((long) r14.getTimestamp()) <= r4) goto L_0x0185;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample> getAllActivitySamples(int r22, int r23) {
        /*
            r21 = this;
            r7 = r21
            r8 = r22
            r9 = r23
            r0 = 15
            java.util.List r10 = super.getGBActivitySamples(r8, r9, r0)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r21.getDevice()
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r1 = r21.getSession()
            nodomain.freeyourgadget.gadgetbridge.entities.Device r11 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.findDevice(r0, r1)
            if (r11 != 0) goto L_0x001f
            java.util.List r0 = java.util.Collections.emptyList()
            return r0
        L_0x001f:
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r0 = r21.getSession()
            nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlayDao r0 = r0.getHPlusHealthActivityOverlayDao()
            de.greenrobot.dao.query.QueryBuilder r12 = r0.queryBuilder()
            de.greenrobot.dao.Property r0 = nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlayDao.Properties.DeviceId
            java.lang.Long r1 = r11.getId()
            de.greenrobot.dao.query.WhereCondition r0 = r0.mo14989eq(r1)
            r1 = 3
            de.greenrobot.dao.query.WhereCondition[] r1 = new p008de.greenrobot.dao.query.WhereCondition[r1]
            de.greenrobot.dao.Property r2 = nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlayDao.Properties.TimestampFrom
            r3 = 86400(0x15180, float:1.21072E-40)
            int r3 = r8 - r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            de.greenrobot.dao.query.WhereCondition r2 = r2.mo14990ge(r3)
            r13 = 0
            r1[r13] = r2
            de.greenrobot.dao.Property r2 = nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlayDao.Properties.TimestampTo
            java.lang.Integer r3 = java.lang.Integer.valueOf(r23)
            de.greenrobot.dao.query.WhereCondition r2 = r2.mo14996le(r3)
            r14 = 1
            r1[r14] = r2
            de.greenrobot.dao.Property r2 = nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlayDao.Properties.TimestampTo
            java.lang.Integer r3 = java.lang.Integer.valueOf(r22)
            de.greenrobot.dao.query.WhereCondition r2 = r2.mo14990ge(r3)
            r15 = 2
            r1[r15] = r2
            r12.where(r0, r1)
            de.greenrobot.dao.query.Query r0 = r12.build()
            java.util.List r16 = r0.list()
            java.util.Iterator r17 = r16.iterator()
        L_0x0073:
            boolean r0 = r17.hasNext()
            if (r0 == 0) goto L_0x00dc
            java.lang.Object r0 = r17.next()
            r18 = r0
            nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlay r18 = (nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlay) r18
            int r0 = r18.getTimestampFrom()
            int r0 = r0 - r14
            int r2 = java.lang.Math.max(r0, r8)
            long r3 = r18.getDeviceId()
            long r5 = r18.getUserId()
            r0 = r21
            r1 = r10
            r0.insertVirtualItem(r1, r2, r3, r5)
            int r0 = r18.getTimestampFrom()
            int r2 = java.lang.Math.max(r0, r8)
            long r3 = r18.getDeviceId()
            long r5 = r18.getUserId()
            r0 = r21
            r0.insertVirtualItem(r1, r2, r3, r5)
            int r0 = r18.getTimestampTo()
            int r0 = r0 - r14
            int r1 = r9 + -1
            int r2 = java.lang.Math.min(r0, r1)
            long r3 = r18.getDeviceId()
            long r5 = r18.getUserId()
            r0 = r21
            r1 = r10
            r0.insertVirtualItem(r1, r2, r3, r5)
            int r0 = r18.getTimestampTo()
            int r2 = java.lang.Math.min(r0, r9)
            long r3 = r18.getDeviceId()
            long r5 = r18.getUserId()
            r0 = r21
            r0.insertVirtualItem(r1, r2, r3, r5)
            goto L_0x0073
        L_0x00dc:
            nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider$1 r0 = new nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider$1
            r0.<init>()
            java.util.Collections.sort(r10, r0)
            java.util.Iterator r0 = r16.iterator()
        L_0x00e8:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0190
            java.lang.Object r1 = r0.next()
            nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlay r1 = (nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlay) r1
            r4 = 0
            java.util.Iterator r6 = r10.iterator()
        L_0x00fa:
            boolean r14 = r6.hasNext()
            if (r14 == 0) goto L_0x018b
            java.lang.Object r14 = r6.next()
            nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample r14 = (nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample) r14
            int r13 = r14.getRawKind()
            r3 = 8
            if (r13 != r3) goto L_0x0111
            r13 = r6
            goto L_0x0185
        L_0x0111:
            int r13 = r14.getTimestamp()
            int r2 = r1.getTimestampFrom()
            if (r13 < r2) goto L_0x0183
            int r2 = r14.getTimestamp()
            int r13 = r1.getTimestampTo()
            if (r2 >= r13) goto L_0x0183
            int r2 = r1.getRawKind()
            if (r2 == r3) goto L_0x013b
            int r2 = r1.getRawKind()
            if (r2 == r15) goto L_0x013b
            int r2 = r1.getRawKind()
            r13 = 4
            if (r2 != r13) goto L_0x0139
            goto L_0x013b
        L_0x0139:
            r13 = r6
            goto L_0x0184
        L_0x013b:
            int r2 = r14.getRawKind()
            r13 = 102(0x66, float:1.43E-43)
            if (r2 != r13) goto L_0x0152
            int r2 = r14.getSteps()
            if (r2 <= 0) goto L_0x0152
            int r2 = r14.getTimestamp()
            int r2 = r2 + 600
            long r4 = (long) r2
            r13 = 0
            goto L_0x00fa
        L_0x0152:
            int r2 = r14.getRawKind()
            r13 = 103(0x67, float:1.44E-43)
            if (r2 != r13) goto L_0x0165
            int r2 = r14.getTimestamp()
            r13 = r6
            long r6 = (long) r2
            int r2 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r2 > 0) goto L_0x0166
            goto L_0x0185
        L_0x0165:
            r13 = r6
        L_0x0166:
            int r2 = r1.getRawKind()
            if (r2 != r3) goto L_0x0170
            r2 = 0
            r14.setHeartRate(r2)
        L_0x0170:
            int r2 = r14.getRawKind()
            if (r2 == r3) goto L_0x017d
            int r2 = r1.getRawKind()
            r14.setRawKind(r2)
        L_0x017d:
            r2 = 10
            r14.setRawIntensity(r2)
            goto L_0x0184
        L_0x0183:
            r13 = r6
        L_0x0184:
        L_0x0185:
            r7 = r21
            r6 = r13
            r13 = 0
            goto L_0x00fa
        L_0x018b:
            r13 = 0
            r7 = r21
            goto L_0x00e8
        L_0x0190:
            java.util.Calendar r0 = java.util.GregorianCalendar.getInstance()
            r1 = 11
            r2 = 0
            r0.set(r1, r2)
            r1 = 12
            r0.set(r1, r2)
            r1 = 13
            r0.set(r1, r2)
            r1 = 14
            r0.set(r1, r2)
            r1 = 0
            r2 = 0
            r3 = 0
            java.util.Iterator r4 = r10.iterator()
        L_0x01b0:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0203
            java.lang.Object r5 = r4.next()
            nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample r5 = (nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample) r5
            int r6 = r5.getTimestamp()
            long r6 = (long) r6
            long r13 = r0.getTimeInMillis()
            r19 = 1000(0x3e8, double:4.94E-321)
            long r13 = r13 / r19
            r15 = -1
            int r17 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r17 < 0) goto L_0x01f3
            int r6 = r5.getRawKind()
            r7 = 103(0x67, float:1.44E-43)
            if (r6 != r7) goto L_0x01e1
            int r6 = r5.getSteps()
            int r1 = java.lang.Math.max(r1, r6)
            r13 = 102(0x66, float:1.43E-43)
            goto L_0x01ee
        L_0x01e1:
            int r6 = r5.getRawKind()
            r13 = 102(0x66, float:1.43E-43)
            if (r6 != r13) goto L_0x01ee
            int r6 = r5.getSteps()
            int r2 = r2 + r6
        L_0x01ee:
            r5.setSteps(r15)
            r3 = r5
            goto L_0x0202
        L_0x01f3:
            r7 = 103(0x67, float:1.44E-43)
            r13 = 102(0x66, float:1.43E-43)
            int r6 = r5.getRawKind()
            r14 = 101(0x65, float:1.42E-43)
            if (r6 == r14) goto L_0x0202
            r5.setSteps(r15)
        L_0x0202:
            goto L_0x01b0
        L_0x0203:
            if (r3 == 0) goto L_0x020c
            int r4 = java.lang.Math.max(r2, r1)
            r3.setSteps(r4)
        L_0x020c:
            r21.detachFromSession()
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider.getAllActivitySamples(int, int):java.util.List");
    }

    private List<HPlusHealthActivitySample> insertVirtualItem(List<HPlusHealthActivitySample> samples, int timestamp, long deviceId, long userId) {
        List<HPlusHealthActivitySample> list = samples;
        HPlusHealthActivitySample sample = new HPlusHealthActivitySample(timestamp, deviceId, userId, (byte[]) null, 0, 1, -1, -1, -1, -1);
        sample.setProvider(this);
        list.add(sample);
        return list;
    }
}
