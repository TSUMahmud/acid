package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.formatter.ValueFormatter;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmount;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmounts;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;

public class WeekStepsChartFragment extends AbstractWeekChartFragment {
    public String getTitle() {
        if (GBApplication.getPrefs().getBoolean("charts_range", true)) {
            return getString(C0889R.string.weekstepschart_steps_a_month);
        }
        return getString(C0889R.string.weekstepschart_steps_a_week);
    }

    /* access modifiers changed from: package-private */
    public String getPieDescription(int targetValue) {
        return getString(C0889R.string.weeksteps_today_steps_description, String.valueOf(targetValue));
    }

    /* access modifiers changed from: package-private */
    public int getGoal() {
        return GBApplication.getPrefs().getInt("mi_fitness_goal", ActivityUser.defaultUserStepsGoal);
    }

    /* access modifiers changed from: package-private */
    public int getOffsetHours() {
        return 0;
    }

    /* access modifiers changed from: package-private */
    public float[] getTotalsForActivityAmounts(ActivityAmounts activityAmounts) {
        long totalSteps = 0;
        for (ActivityAmount amount : activityAmounts.getAmounts()) {
            totalSteps += amount.getTotalSteps();
        }
        return new float[]{(float) totalSteps};
    }

    /* access modifiers changed from: protected */
    public long calculateBalance(ActivityAmounts activityAmounts) {
        long balance = 0;
        for (ActivityAmount amount : activityAmounts.getAmounts()) {
            balance += amount.getTotalSteps();
        }
        return balance;
    }

    /* access modifiers changed from: protected */
    public String formatPieValue(long value) {
        return String.valueOf(value);
    }

    /* access modifiers changed from: package-private */
    public String[] getPieLabels() {
        return new String[]{""};
    }

    /* access modifiers changed from: package-private */
    public ValueFormatter getPieValueFormatter() {
        return null;
    }

    /* access modifiers changed from: package-private */
    public ValueFormatter getBarValueFormatter() {
        return null;
    }

    /* access modifiers changed from: package-private */
    public ValueFormatter getYAxisFormatter() {
        return null;
    }

    /* access modifiers changed from: package-private */
    public int[] getColors() {
        return new int[]{this.akActivity.color.intValue()};
    }

    /* access modifiers changed from: protected */
    public void setupLegend(Chart chart) {
        chart.getLegend().setEnabled(false);
    }

    /* access modifiers changed from: protected */
    public String getBalanceMessage(long balance, int targetValue) {
        if (balance <= 0) {
            return getString(C0889R.string.no_data);
        }
        long totalBalance = balance - (((long) targetValue) * ((long) this.TOTAL_DAYS));
        if (totalBalance > 0) {
            return getString(C0889R.string.overstep, Long.valueOf(Math.abs(totalBalance)));
        }
        return getString(C0889R.string.lack_of_step, Long.valueOf(Math.abs(totalBalance)));
    }

    /* access modifiers changed from: package-private */
    public String getAverage(float value) {
        return String.format("%.0f", new Object[]{Float.valueOf(value)});
    }
}
