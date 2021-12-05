package nodomain.freeyourgadget.gadgetbridge.devices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityKind;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.query.QueryBuilder;
import p008de.greenrobot.dao.query.WhereCondition;

public abstract class AbstractSampleProvider<T extends AbstractActivitySample> implements SampleProvider<T> {
    private static final WhereCondition[] NO_CONDITIONS = new WhereCondition[0];
    private final GBDevice mDevice;
    private final DaoSession mSession;

    /* access modifiers changed from: protected */
    public abstract Property getDeviceIdentifierSampleProperty();

    /* access modifiers changed from: protected */
    public abstract Property getRawKindSampleProperty();

    public abstract AbstractDao<T, ?> getSampleDao();

    /* access modifiers changed from: protected */
    public abstract Property getTimestampSampleProperty();

    protected AbstractSampleProvider(GBDevice device, DaoSession session) {
        this.mDevice = device;
        this.mSession = session;
    }

    public GBDevice getDevice() {
        return this.mDevice;
    }

    public DaoSession getSession() {
        return this.mSession;
    }

    public List<T> getAllActivitySamples(int timestamp_from, int timestamp_to) {
        return getGBActivitySamples(timestamp_from, timestamp_to, 15);
    }

    public List<T> getActivitySamples(int timestamp_from, int timestamp_to) {
        if (getRawKindSampleProperty() != null) {
            return getGBActivitySamples(timestamp_from, timestamp_to, 1);
        }
        return getActivitySamplesByActivityFilter(timestamp_from, timestamp_to, 1);
    }

    public List<T> getSleepSamples(int timestamp_from, int timestamp_to) {
        if (getRawKindSampleProperty() != null) {
            return getGBActivitySamples(timestamp_from, timestamp_to, 6);
        }
        return getActivitySamplesByActivityFilter(timestamp_from, timestamp_to, 6);
    }

    public void addGBActivitySample(T activitySample) {
        getSampleDao().insertOrReplace(activitySample);
    }

    public void addGBActivitySamples(T[] activitySamples) {
        getSampleDao().insertOrReplaceInTx(activitySamples);
    }

    public T getLatestActivitySample() {
        QueryBuilder<T> qb = getSampleDao().queryBuilder();
        Device dbDevice = DBHelper.findDevice(getDevice(), getSession());
        if (dbDevice == null) {
            return null;
        }
        qb.where(getDeviceIdentifierSampleProperty().mo14989eq(dbDevice.getId()), new WhereCondition[0]).orderDesc(getTimestampSampleProperty()).limit(1);
        List<T> samples = qb.build().list();
        if (samples.isEmpty()) {
            return null;
        }
        T sample = (AbstractActivitySample) samples.get(0);
        sample.setProvider(this);
        return sample;
    }

    /* access modifiers changed from: protected */
    public List<T> getGBActivitySamples(int timestamp_from, int timestamp_to, int activityType) {
        if (getRawKindSampleProperty() == null && activityType != 15) {
            return Collections.emptyList();
        }
        QueryBuilder<T> qb = getSampleDao().queryBuilder();
        Property timestampProperty = getTimestampSampleProperty();
        Device dbDevice = DBHelper.findDevice(getDevice(), getSession());
        if (dbDevice == null) {
            return Collections.emptyList();
        }
        qb.where(getDeviceIdentifierSampleProperty().mo14989eq(dbDevice.getId()), timestampProperty.mo14990ge(Integer.valueOf(timestamp_from))).where(timestampProperty.mo14996le(Integer.valueOf(timestamp_to)), getClauseForActivityType(qb, activityType));
        List<T> samples = qb.build().list();
        for (T sample : samples) {
            sample.setProvider(this);
        }
        detachFromSession();
        return samples;
    }

    /* access modifiers changed from: protected */
    public void detachFromSession() {
        getSampleDao().detachAll();
    }

    private WhereCondition[] getClauseForActivityType(QueryBuilder qb, int activityTypes) {
        if (activityTypes == 15) {
            return NO_CONDITIONS;
        }
        return new WhereCondition[]{getActivityTypeConditions(qb, ActivityKind.mapToDBActivityTypes(activityTypes, this))};
    }

    private WhereCondition getActivityTypeConditions(QueryBuilder qb, int[] dbActivityTypes) {
        Property rawKindProperty;
        if (dbActivityTypes.length == 0 || (rawKindProperty = getRawKindSampleProperty()) == null) {
            return null;
        }
        if (dbActivityTypes.length == 1) {
            return rawKindProperty.mo14989eq(Integer.valueOf(dbActivityTypes[0]));
        }
        if (dbActivityTypes.length == 2) {
            return qb.mo15268or(rawKindProperty.mo14989eq(Integer.valueOf(dbActivityTypes[0])), rawKindProperty.mo14989eq(Integer.valueOf(dbActivityTypes[1])), new WhereCondition[0]);
        }
        int len = dbActivityTypes.length - 2;
        WhereCondition[] trailingConditions = new WhereCondition[len];
        for (int i = 0; i < len; i++) {
            trailingConditions[i] = rawKindProperty.mo14989eq(Integer.valueOf(dbActivityTypes[i + 2]));
        }
        return qb.mo15268or(rawKindProperty.mo14989eq(Integer.valueOf(dbActivityTypes[0])), rawKindProperty.mo14989eq(Integer.valueOf(dbActivityTypes[1])), trailingConditions);
    }

    private List<T> getActivitySamplesByActivityFilter(int timestamp_from, int timestamp_to, int activityFilter) {
        List<T> samples = getAllActivitySamples(timestamp_from, timestamp_to);
        List<T> filteredSamples = new ArrayList<>();
        for (T sample : samples) {
            if ((sample.getKind() & activityFilter) != 0) {
                filteredSamples.add(sample);
            }
        }
        return filteredSamples;
    }
}
