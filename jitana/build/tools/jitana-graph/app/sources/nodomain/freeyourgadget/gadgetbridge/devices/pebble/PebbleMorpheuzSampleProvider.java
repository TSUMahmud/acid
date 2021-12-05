package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMorpheuzSample;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMorpheuzSampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class PebbleMorpheuzSampleProvider extends AbstractSampleProvider<PebbleMorpheuzSample> {
    protected float movementDivisor = 5000.0f;

    public PebbleMorpheuzSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    public AbstractDao<PebbleMorpheuzSample, ?> getSampleDao() {
        return getSession().getPebbleMorpheuzSampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return PebbleMorpheuzSampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return null;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return PebbleMorpheuzSampleDao.Properties.DeviceId;
    }

    public PebbleMorpheuzSample createActivitySample() {
        return new PebbleMorpheuzSample();
    }

    public float normalizeIntensity(int rawIntensity) {
        return ((float) rawIntensity) / this.movementDivisor;
    }

    public int normalizeType(int rawType) {
        return rawType;
    }

    public int toRawActivityKind(int activityKind) {
        return activityKind;
    }
}
