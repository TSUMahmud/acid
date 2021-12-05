package nodomain.freeyourgadget.gadgetbridge.devices.jyou;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.JYouActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.JYouActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class JYouSampleProvider extends AbstractSampleProvider<JYouActivitySample> {
    public static final int TYPE_ACTIVITY = -1;
    private GBDevice mDevice;
    private DaoSession mSession;
    private final float movementDivisor = 6000.0f;

    public JYouSampleProvider(GBDevice device, DaoSession session) {
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

    public JYouActivitySample createActivitySample() {
        return new JYouActivitySample();
    }

    public AbstractDao<JYouActivitySample, ?> getSampleDao() {
        return getSession().getJYouActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return JYouActivitySampleDao.Properties.RawKind;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return JYouActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return JYouActivitySampleDao.Properties.DeviceId;
    }
}
