package nodomain.freeyourgadget.gadgetbridge.devices.no1f1;

import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.No1F1ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.No1F1ActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

public class No1F1SampleProvider extends AbstractSampleProvider<No1F1ActivitySample> {
    private GBDevice mDevice;
    private DaoSession mSession;

    public No1F1SampleProvider(GBDevice device, DaoSession session) {
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
        return ((float) rawIntensity) / 8000.0f;
    }

    public No1F1ActivitySample createActivitySample() {
        return new No1F1ActivitySample();
    }

    public AbstractDao<No1F1ActivitySample, ?> getSampleDao() {
        return getSession().getNo1F1ActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return No1F1ActivitySampleDao.Properties.RawKind;
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return No1F1ActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return No1F1ActivitySampleDao.Properties.DeviceId;
    }
}
