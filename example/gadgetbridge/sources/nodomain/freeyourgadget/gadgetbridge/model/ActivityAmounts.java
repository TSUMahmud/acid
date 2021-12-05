package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.ArrayList;
import java.util.List;

public class ActivityAmounts {
    private final List<ActivityAmount> amounts = new ArrayList(4);
    private long totalSeconds;

    public void addAmount(ActivityAmount amount) {
        this.amounts.add(amount);
        this.totalSeconds += amount.getTotalSeconds();
    }

    public List<ActivityAmount> getAmounts() {
        return this.amounts;
    }

    public long getTotalSeconds() {
        return this.totalSeconds;
    }

    public void calculatePercentages() {
        for (ActivityAmount amount : this.amounts) {
            amount.setPercent((short) ((int) (100.0f * (((float) amount.getTotalSeconds()) / ((float) this.totalSeconds)))));
        }
    }
}
