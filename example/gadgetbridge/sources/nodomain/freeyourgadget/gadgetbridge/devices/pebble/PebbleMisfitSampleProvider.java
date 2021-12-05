package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMisfitSample;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMisfitSampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class PebbleMisfitSampleProvider extends AbstractSampleProvider<PebbleMisfitSample> {
    protected final float movementDivisor = 300.0f;

    public PebbleMisfitSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    public int normalizeType(int rawType) {
        return rawType;
    }

    public int toRawActivityKind(int activityKind) {
        return activityKind;
    }

    public float normalizeIntensity(int rawIntensity) {
        return ((float) rawIntensity) / 300.0f;
    }

    public PebbleMisfitSample createActivitySample() {
        return new PebbleMisfitSample();
    }

    public AbstractDao<PebbleMisfitSample, ?> getSampleDao() {
        return getSession().getPebbleMisfitSampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return null;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return PebbleMisfitSampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return PebbleMisfitSampleDao.Properties.DeviceId;
    }
}
