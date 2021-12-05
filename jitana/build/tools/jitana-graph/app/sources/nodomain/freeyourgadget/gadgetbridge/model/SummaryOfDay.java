package nodomain.freeyourgadget.gadgetbridge.model;

public interface SummaryOfDay {
    int getDayEndFallAsleepTime();

    int getDayStartWakeupTime();

    byte getProvider();

    int getSteps();
}
