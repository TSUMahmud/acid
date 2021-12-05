package nodomain.freeyourgadget.gadgetbridge.devices.id115;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.ID115ActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class ID115SampleProvider extends AbstractSampleProvider<ID115ActivitySample> {
    public ID115SampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    public AbstractDao<ID115ActivitySample, ?> getSampleDao() {
        return getSession().getID115ActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return ID115ActivitySampleDao.Properties.RawKind;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return ID115ActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return ID115ActivitySampleDao.Properties.DeviceId;
    }

    public int normalizeType(int rawType) {
        return rawType;
    }

    public int toRawActivityKind(int activityKind) {
        return activityKind;
    }

    public float normalizeIntensity(int rawIntensity) {
        return (float) rawIntensity;
    }

    public ID115ActivitySample createActivitySample() {
        return new ID115ActivitySample();
    }
}
