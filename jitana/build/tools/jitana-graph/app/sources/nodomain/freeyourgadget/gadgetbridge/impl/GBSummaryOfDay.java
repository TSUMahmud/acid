package nodomain.freeyourgadget.gadgetbridge.impl;

import nodomain.freeyourgadget.gadgetbridge.model.SummaryOfDay;

public class GBSummaryOfDay implements SummaryOfDay {
    private int dayEndFallAsleepTime;
    private int dayStartWakeupTime;
    private byte provider;
    private int steps;

    public byte getProvider() {
        return this.provider;
    }

    public int getSteps() {
        return this.steps;
    }

    public int getDayStartWakeupTime() {
        return this.dayStartWakeupTime;
    }

    public int getDayEndFallAsleepTime() {
        return this.dayEndFallAsleepTime;
    }
}
