package nodomain.freeyourgadget.gadgetbridge.model;

import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;

public interface ActivitySample extends TimeStamped {
    public static final int NOT_MEASURED = -1;

    int getHeartRate();

    float getIntensity();

    int getKind();

    SampleProvider getProvider();

    int getRawIntensity();

    int getRawKind();

    int getSteps();

    void setHeartRate(int i);
}
