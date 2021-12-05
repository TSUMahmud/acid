package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p008de.greenrobot.dao.query.QueryBuilder;

public class MiBand2SampleProvider extends AbstractMiBandSampleProvider {
    public MiBand2SampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    /* access modifiers changed from: protected */
    public List<MiBandActivitySample> getGBActivitySamples(int timestamp_from, int timestamp_to, int activityType) {
        List<MiBandActivitySample> samples = super.getGBActivitySamples(timestamp_from, timestamp_to, activityType);
        postprocess(samples);
        return samples;
    }

    private void postprocess(List<MiBandActivitySample> samples) {
        if (!samples.isEmpty()) {
            int lastValidKind = determinePreviousValidActivityType(samples.get(0));
            for (MiBandActivitySample sample : samples) {
                int rawKind = sample.getRawKind();
                if (rawKind != -1) {
                    rawKind &= 15;
                    sample.setRawKind(rawKind);
                }
                if (rawKind != 0 && rawKind != 10) {
                    lastValidKind = rawKind;
                } else if (lastValidKind != -1) {
                    sample.setRawKind(lastValidKind);
                }
            }
        }
    }

    private int determinePreviousValidActivityType(MiBandActivitySample sample) {
        QueryBuilder<MiBandActivitySample> qb = getSampleDao().queryBuilder();
        qb.where(MiBandActivitySampleDao.Properties.DeviceId.mo14989eq(Long.valueOf(sample.getDeviceId())), MiBandActivitySampleDao.Properties.UserId.mo14989eq(Long.valueOf(sample.getUserId())), MiBandActivitySampleDao.Properties.Timestamp.mo14998lt(Integer.valueOf(sample.getTimestamp())), MiBandActivitySampleDao.Properties.RawKind.notIn(0, 10, -1, 16, 80, 96, 112));
        qb.orderDesc(MiBandActivitySampleDao.Properties.Timestamp);
        qb.limit(1);
        List<MiBandActivitySample> result = qb.build().list();
        if (result.size() > 0) {
            return result.get(0).getRawKind() & 15;
        }
        return -1;
    }

    public int normalizeType(int rawType) {
        return HuamiConst.toActivityKind(rawType);
    }

    public int toRawActivityKind(int activityKind) {
        return HuamiConst.toRawActivityType(activityKind);
    }
}
