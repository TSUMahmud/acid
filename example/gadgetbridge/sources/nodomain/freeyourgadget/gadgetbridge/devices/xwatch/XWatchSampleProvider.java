package nodomain.freeyourgadget.gadgetbridge.devices.xwatch;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.XWatchActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.XWatchActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class XWatchSampleProvider extends AbstractSampleProvider<XWatchActivitySample> {
    public static final int TYPE_ACTIVITY = -1;

    public XWatchSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    public int normalizeType(int rawType) {
        return 1;
    }

    public int toRawActivityKind(int activityKind) {
        return -1;
    }

    public float normalizeIntensity(int rawIntensity) {
        return ((float) rawIntensity) / 180.0f;
    }

    public XWatchActivitySample createActivitySample() {
        return new XWatchActivitySample();
    }

    public AbstractDao<XWatchActivitySample, ?> getSampleDao() {
        return getSession().getXWatchActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return XWatchActivitySampleDao.Properties.RawKind;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return XWatchActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return XWatchActivitySampleDao.Properties.DeviceId;
    }
}
