package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public class MiBandSampleProvider extends AbstractMiBandSampleProvider {
    public static final int TYPE_ACTIVITY = -1;
    public static final int TYPE_CHARGING = 6;
    public static final int TYPE_DEEP_SLEEP = 4;
    public static final int TYPE_LIGHT_SLEEP = 5;
    public static final int TYPE_NONWEAR = 3;
    public static final int TYPE_UNKNOWN = -1;

    public MiBandSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    public int normalizeType(int rawType) {
        if (rawType == -1) {
            return 1;
        }
        if (rawType == 3) {
            return 8;
        }
        if (rawType == 4) {
            return 4;
        }
        if (rawType != 5) {
            return rawType != 6 ? 0 : 8;
        }
        return 2;
    }

    public int toRawActivityKind(int activityKind) {
        if (activityKind == 1) {
            return -1;
        }
        if (activityKind == 2) {
            return 5;
        }
        if (activityKind == 4) {
            return 4;
        }
        if (activityKind != 8) {
            return -1;
        }
        return 3;
    }
}
