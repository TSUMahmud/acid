package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmount;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmounts;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityAnalysis {
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) ActivityAnalysis.class);
    private int maxSpeed = 0;
    protected HashMap<Integer, Long> stats = new HashMap<>();

    public ActivityAmounts calculateActivityAmounts(List<? extends ActivitySample> samples) {
        ActivityAmount amount;
        ActivityAmount notWorn;
        Iterator<? extends ActivitySample> it;
        int i = 4;
        ActivityAmount deepSleep = new ActivityAmount(4);
        int i2 = 2;
        ActivityAmount lightSleep = new ActivityAmount(2);
        int i3 = 8;
        ActivityAmount notWorn2 = new ActivityAmount(8);
        int i4 = 1;
        ActivityAmount activity = new ActivityAmount(1);
        ActivityAmount previousAmount = null;
        ActivitySample previousSample = null;
        Iterator<? extends ActivitySample> it2 = samples.iterator();
        while (it2.hasNext()) {
            ActivitySample sample = (ActivitySample) it2.next();
            int kind = sample.getKind();
            if (kind == i2) {
                amount = lightSleep;
            } else if (kind == i) {
                amount = deepSleep;
            } else if (kind != i3) {
                amount = activity;
            } else {
                amount = notWorn2;
            }
            int steps = sample.getSteps();
            if (steps > 0) {
                notWorn = notWorn2;
                amount.addSteps((long) steps);
            } else {
                notWorn = notWorn2;
            }
            if (previousSample != null) {
                long timeDifference = (long) (sample.getTimestamp() - previousSample.getTimestamp());
                if (previousSample.getRawKind() == sample.getRawKind()) {
                    amount.addSeconds(timeDifference);
                    ActivitySample activitySample = previousSample;
                    it = it2;
                } else {
                    ActivitySample activitySample2 = previousSample;
                    it = it2;
                    long sharedTimeDifference = (long) (((float) timeDifference) / 2.0f);
                    previousAmount.addSeconds(sharedTimeDifference);
                    amount.addSeconds(sharedTimeDifference);
                }
                if (steps > 0 && sample.getKind() == i4) {
                    if (steps > this.maxSpeed) {
                        this.maxSpeed = steps;
                    }
                    if (!this.stats.containsKey(Integer.valueOf(steps))) {
                        this.stats.put(Integer.valueOf(steps), Long.valueOf(timeDifference));
                    } else {
                        this.stats.put(Integer.valueOf(steps), Long.valueOf(timeDifference + this.stats.get(Integer.valueOf(steps)).longValue()));
                    }
                }
            } else {
                it = it2;
            }
            amount.setStartDate(sample.getTimestamp());
            amount.setEndDate(sample.getTimestamp());
            previousAmount = amount;
            previousSample = sample;
            it2 = it;
            notWorn2 = notWorn;
            i = 4;
            i2 = 2;
            i3 = 8;
            i4 = 1;
        }
        ActivitySample activitySample3 = previousSample;
        ActivityAmounts result = new ActivityAmounts();
        if (deepSleep.getTotalSeconds() > 0) {
            result.addAmount(deepSleep);
        }
        if (lightSleep.getTotalSeconds() > 0) {
            result.addAmount(lightSleep);
        }
        if (activity.getTotalSeconds() > 0) {
            result.addAmount(activity);
        }
        result.calculatePercentages();
        return result;
    }

    /* access modifiers changed from: package-private */
    public int calculateTotalSteps(List<? extends ActivitySample> samples) {
        int totalSteps = 0;
        for (ActivitySample sample : samples) {
            int steps = sample.getSteps();
            if (steps > 0) {
                totalSteps += steps;
            }
        }
        return totalSteps;
    }
}
