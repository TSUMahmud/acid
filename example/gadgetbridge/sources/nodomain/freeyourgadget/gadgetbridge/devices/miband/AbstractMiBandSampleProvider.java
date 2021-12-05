package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public abstract class AbstractMiBandSampleProvider extends AbstractSampleProvider<MiBandActivitySample> {
    private final float movementDivisor = 180.0f;

    public AbstractMiBandSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    public float normalizeIntensity(int rawIntensity) {
        return ((float) rawIntensity) / 180.0f;
    }

    public AbstractDao<MiBandActivitySample, ?> getSampleDao() {
        return getSession().getMiBandActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return MiBandActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return MiBandActivitySampleDao.Properties.DeviceId;
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return MiBandActivitySampleDao.Properties.RawKind;
    }

    public MiBandActivitySample createActivitySample() {
        return new MiBandActivitySample();
    }
}
