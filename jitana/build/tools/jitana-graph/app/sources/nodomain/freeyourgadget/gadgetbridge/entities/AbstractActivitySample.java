package nodomain.freeyourgadget.gadgetbridge.entities;

import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import p005ch.qos.logback.core.CoreConstants;

public abstract class AbstractActivitySample implements ActivitySample {
    private SampleProvider mProvider;

    public abstract long getDeviceId();

    public abstract long getUserId();

    public abstract void setDeviceId(long j);

    public abstract void setTimestamp(int i);

    public abstract void setUserId(long j);

    public SampleProvider getProvider() {
        return this.mProvider;
    }

    public void setProvider(SampleProvider provider) {
        this.mProvider = provider;
    }

    public int getKind() {
        return getProvider().normalizeType(getRawKind());
    }

    public int getRawKind() {
        return -1;
    }

    public float getIntensity() {
        return getProvider().normalizeIntensity(getRawIntensity());
    }

    public void setRawKind(int kind) {
    }

    public void setRawIntensity(int intensity) {
    }

    public void setSteps(int steps) {
    }

    public void setHeartRate(int heartRate) {
    }

    public int getHeartRate() {
        return -1;
    }

    public int getRawIntensity() {
        return -1;
    }

    public int getSteps() {
        return -1;
    }

    public String toString() {
        int kind = getProvider() != null ? getKind() : -1;
        float intensity = getProvider() != null ? getIntensity() : -1.0f;
        return getClass().getSimpleName() + "{timestamp=" + DateTimeUtils.formatDateTime(DateTimeUtils.parseTimeStamp(getTimestamp())) + ", intensity=" + intensity + ", steps=" + getSteps() + ", heartrate=" + getHeartRate() + ", type=" + kind + ", userId=" + getUserId() + ", deviceId=" + getDeviceId() + CoreConstants.CURLY_RIGHT;
    }
}
