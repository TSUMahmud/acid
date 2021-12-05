package nodomain.freeyourgadget.gadgetbridge.activities;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

public class HeartRateUtils {
    public static final int MAX_HEART_RATE_VALUE = 250;
    public static final int MAX_HR_MEASUREMENTS_GAP_MINUTES = 10;
    public static final int MIN_HEART_RATE_VALUE = 10;
    private static final HeartRateUtils instance = new HeartRateUtils();
    private int maxHeartRateValue;
    private int minHeartRateValue;

    public static HeartRateUtils getInstance() {
        return instance;
    }

    private HeartRateUtils() {
        updateCachedHeartRatePreferences();
    }

    public void updateCachedHeartRatePreferences() {
        Prefs prefs = GBApplication.getPrefs();
        this.maxHeartRateValue = prefs.getInt(GBPrefs.CHART_MAX_HEART_RATE, 250);
        this.minHeartRateValue = prefs.getInt(GBPrefs.CHART_MIN_HEART_RATE, 10);
    }

    public int getMaxHeartRate() {
        return this.maxHeartRateValue;
    }

    public int getMinHeartRate() {
        return this.minHeartRateValue;
    }

    public boolean isValidHeartRateValue(int value) {
        return value >= getMinHeartRate() && value <= getMaxHeartRate();
    }
}
