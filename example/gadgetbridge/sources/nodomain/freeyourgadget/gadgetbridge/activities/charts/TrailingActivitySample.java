package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;

public class TrailingActivitySample extends AbstractActivitySample {
    private long deviceId;
    private int timestamp;
    private long userId;

    public void setTimestamp(int timestamp2) {
        this.timestamp = timestamp2;
    }

    public void setUserId(long userId2) {
        this.userId = userId2;
    }

    public void setDeviceId(long deviceId2) {
        this.deviceId = deviceId2;
    }

    public long getDeviceId() {
        return this.deviceId;
    }

    public long getUserId() {
        return this.userId;
    }

    public int getTimestamp() {
        return this.timestamp;
    }
}
