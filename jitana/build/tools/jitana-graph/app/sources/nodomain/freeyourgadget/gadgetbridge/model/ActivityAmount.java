package nodomain.freeyourgadget.gadgetbridge.model;

import android.content.Context;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.C0889R;

public class ActivityAmount {
    private final int activityKind;
    private Date endDate = null;
    private short percent;
    private Date startDate = null;
    private long totalSeconds;
    private long totalSteps;

    public ActivityAmount(int activityKind2) {
        this.activityKind = activityKind2;
    }

    public void addSeconds(long seconds) {
        this.totalSeconds += seconds;
    }

    public void addSteps(long steps) {
        this.totalSteps += steps;
    }

    public long getTotalSeconds() {
        return this.totalSeconds;
    }

    public long getTotalSteps() {
        return this.totalSteps;
    }

    public int getActivityKind() {
        return this.activityKind;
    }

    public short getPercent() {
        return this.percent;
    }

    public void setPercent(short percent2) {
        this.percent = percent2;
    }

    public String getName(Context context) {
        int i = this.activityKind;
        if (i == 2) {
            return context.getString(C0889R.string.abstract_chart_fragment_kind_light_sleep);
        }
        if (i != 4) {
            return context.getString(C0889R.string.abstract_chart_fragment_kind_activity);
        }
        return context.getString(C0889R.string.abstract_chart_fragment_kind_deep_sleep);
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(int seconds) {
        if (this.startDate == null) {
            this.startDate = new Date(((long) seconds) * 1000);
        }
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(int seconds) {
        this.endDate = new Date(((long) seconds) * 1000);
    }
}
