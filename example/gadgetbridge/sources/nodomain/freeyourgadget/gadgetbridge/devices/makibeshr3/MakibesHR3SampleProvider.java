package nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class MakibesHR3SampleProvider extends AbstractSampleProvider<MakibesHR3ActivitySample> {
    private GBDevice mDevice;
    private DaoSession mSession;

    public MakibesHR3SampleProvider(GBDevice device, DaoSession session) {
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
        return (float) rawIntensity;
    }

    public MakibesHR3ActivitySample createActivitySample() {
        return new MakibesHR3ActivitySample();
    }

    public AbstractDao<MakibesHR3ActivitySample, ?> getSampleDao() {
        return getSession().getMakibesHR3ActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return MakibesHR3ActivitySampleDao.Properties.RawKind;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return MakibesHR3ActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return MakibesHR3ActivitySampleDao.Properties.DeviceId;
    }
}
