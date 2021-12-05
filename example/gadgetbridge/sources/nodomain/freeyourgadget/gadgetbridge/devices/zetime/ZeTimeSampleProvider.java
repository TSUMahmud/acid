package nodomain.freeyourgadget.gadgetbridge.devices.zetime;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.ZeTimeActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.ZeTimeActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class ZeTimeSampleProvider extends AbstractSampleProvider<ZeTimeActivitySample> {
    private GBDevice mDevice;
    private DaoSession mSession;
    private final float movementDivisor = 6000.0f;

    public ZeTimeSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
        this.mSession = session;
        this.mDevice = device;
    }

    public int normalizeType(int rawType) {
        return rawType;
    }

    public int toRawActivityKind(int activityKind) {
        return activityKind;
    }

    public float normalizeIntensity(int rawIntensity) {
        return ((float) rawIntensity) / 6000.0f;
    }

    public ZeTimeActivitySample createActivitySample() {
        return new ZeTimeActivitySample();
    }

    public AbstractDao<ZeTimeActivitySample, ?> getSampleDao() {
        return getSession().getZeTimeActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return ZeTimeActivitySampleDao.Properties.RawKind;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return ZeTimeActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return ZeTimeActivitySampleDao.Properties.DeviceId;
    }
}
