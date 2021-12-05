package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import java.util.Collections;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivityOverlay;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivityOverlayDao;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.query.QueryBuilder;
import p008de.greenrobot.dao.query.WhereCondition;

public class PebbleHealthSampleProvider extends AbstractSampleProvider<PebbleHealthActivitySample> {
    public static final int TYPE_ACTIVITY = -1;
    public static final int TYPE_DEEP_NAP = 4;
    public static final int TYPE_DEEP_SLEEP = 2;
    public static final int TYPE_LIGHT_NAP = 3;
    public static final int TYPE_LIGHT_SLEEP = 1;
    public static final int TYPE_RUN = 6;
    public static final int TYPE_WALK = 5;
    protected final float movementDivisor = 8000.0f;

    public PebbleHealthSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    public List<PebbleHealthActivitySample> getAllActivitySamples(int timestamp_from, int timestamp_to) {
        List<PebbleHealthActivitySample> samples = super.getGBActivitySamples(timestamp_from, timestamp_to, 15);
        Device dbDevice = DBHelper.findDevice(getDevice(), getSession());
        if (dbDevice == null) {
            return Collections.emptyList();
        }
        QueryBuilder<PebbleHealthActivityOverlay> qb = getSession().getPebbleHealthActivityOverlayDao().queryBuilder();
        qb.where(PebbleHealthActivityOverlayDao.Properties.DeviceId.mo14989eq(dbDevice.getId()), PebbleHealthActivityOverlayDao.Properties.TimestampTo.mo14990ge(Integer.valueOf(timestamp_from))).where(PebbleHealthActivityOverlayDao.Properties.TimestampFrom.mo14996le(Integer.valueOf(timestamp_to)), new WhereCondition[0]);
        for (PebbleHealthActivityOverlay overlay : qb.build().list()) {
            for (PebbleHealthActivitySample sample : samples) {
                if (overlay.getTimestampFrom() <= sample.getTimestamp() && sample.getTimestamp() < overlay.getTimestampTo()) {
                    sample.setRawKind(overlay.getRawKind());
                }
            }
        }
        detachFromSession();
        return samples;
    }

    public AbstractDao<PebbleHealthActivitySample, ?> getSampleDao() {
        return getSession().getPebbleHealthActivitySampleDao();
    }

    /* access modifiers changed from: protected */
    public Property getTimestampSampleProperty() {
        return PebbleHealthActivitySampleDao.Properties.Timestamp;
    }

    /* access modifiers changed from: protected */
    public Property getRawKindSampleProperty() {
        return null;
    }

    /* access modifiers changed from: protected */
    public Property getDeviceIdentifierSampleProperty() {
        return PebbleHealthActivitySampleDao.Properties.DeviceId;
    }

    public PebbleHealthActivitySample createActivitySample() {
        return new PebbleHealthActivitySample();
    }

    public int normalizeType(int rawType) {
        switch (rawType) {
            case -1:
            case 5:
            case 6:
                return 1;
            case 1:
            case 3:
                return 2;
            case 2:
            case 4:
                return 4;
            default:
                return 0;
        }
    }

    public int toRawActivityKind(int activityKind) {
        if (activityKind == 1) {
            return -1;
        }
        if (activityKind != 2) {
            return activityKind != 4 ? -1 : 2;
        }
        return 1;
    }

    public float normalizeIntensity(int rawIntensity) {
        return ((float) rawIntensity) / 8000.0f;
    }
}
