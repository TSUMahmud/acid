package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmount;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmounts;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

public class WeekSleepChartFragment extends AbstractWeekChartFragment {
    public String getTitle() {
        if (GBApplication.getPrefs().getBoolean("charts_range", true)) {
            return getString(C0889R.string.weeksleepchart_sleep_a_month);
        }
        return getString(C0889R.string.weeksleepchart_sleep_a_week);
    }

    /* access modifiers changed from: package-private */
    public String getPieDescription(int targetValue) {
        return getString(C0889R.string.weeksleepchart_today_sleep_description, DateTimeUtils.formatDurationHoursMinutes((long) targetValue, TimeUnit.MINUTES));
    }

    /* access modifiers changed from: package-private */
    public int getGoal() {
        return GBApplication.getPrefs().getInt("activity_user_sleep_duration", 8) * 60;
    }

    /* access modifiers changed from: package-private */
    public int getOffsetHours() {
        return -12;
    }

    /* access modifiers changed from: protected */
    public long calculateBalance(ActivityAmounts activityAmounts) {
        long balance = 0;
        for (ActivityAmount amount : activityAmounts.getAmounts()) {
            if (amount.getActivityKind() == 4 || amount.getActivityKind() == 2) {
                balance += amount.getTotalSeconds();
            }
        }
        return (long) ((int) (balance / 60));
    }

    /* access modifiers changed from: protected */
    public String getBalanceMessage(long balance, int targetValue) {
        if (balance <= 0) {
            return getString(C0889R.string.no_data);
        }
        long totalBalance = balance - (((long) targetValue) * ((long) this.TOTAL_DAYS));
        if (totalBalance > 0) {
            return getString(C0889R.string.overslept, getHM(totalBalance));
        }
        return getString(C0889R.string.lack_of_sleep, getHM(Math.abs(totalBalance)));
    }

    /* access modifiers changed from: package-private */
    public float[] getTotalsForActivityAmounts(ActivityAmounts activityAmounts) {
        long totalSecondsDeepSleep = 0;
        long totalSecondsLightSleep = 0;
        for (ActivityAmount amount : activityAmounts.getAmounts()) {
            if (amount.getActivityKind() == 4) {
                totalSecondsDeepSleep += amount.getTotalSeconds();
            } else if (amount.getActivityKind() == 2) {
                totalSecondsLightSleep += amount.getTotalSeconds();
            }
        }
        return new float[]{(float) ((int) (totalSecondsDeepSleep / 60)), (float) ((int) (totalSecondsLightSleep / 60))};
    }

    /* access modifiers changed from: protected */
    public String formatPieValue(long value) {
        return DateTimeUtils.formatDurationHoursMinutes(value, TimeUnit.MINUTES);
    }

    /* access modifiers changed from: package-private */
    public String[] getPieLabels() {
        return new String[]{getString(C0889R.string.abstract_chart_fragment_kind_deep_sleep), getString(C0889R.string.abstract_chart_fragment_kind_light_sleep)};
    }

    /* access modifiers changed from: package-private */
    public ValueFormatter getPieValueFormatter() {
        return new ValueFormatter() {
            public String getFormattedValue(float value) {
                return WeekSleepChartFragment.this.formatPieValue((long) value);
            }
        };
    }

    /* access modifiers changed from: package-private */
    public ValueFormatter getBarValueFormatter() {
        return new ValueFormatter() {
            public String getFormattedValue(float value) {
                return DateTimeUtils.minutesToHHMM((int) value);
            }
        };
    }

    /* access modifiers changed from: package-private */
    public ValueFormatter getYAxisFormatter() {
        return new ValueFormatter() {
            public String getFormattedValue(float value) {
                return DateTimeUtils.minutesToHHMM((int) value);
            }
        };
    }

    /* access modifiers changed from: package-private */
    public int[] getColors() {
        return new int[]{this.akDeepSleep.color.intValue(), this.akLightSleep.color.intValue()};
    }

    /* access modifiers changed from: protected */
    public void setupLegend(Chart chart) {
        List<LegendEntry> legendEntries = new ArrayList<>(2);
        LegendEntry lightSleepEntry = new LegendEntry();
        lightSleepEntry.label = this.akLightSleep.label;
        lightSleepEntry.formColor = this.akLightSleep.color.intValue();
        legendEntries.add(lightSleepEntry);
        LegendEntry deepSleepEntry = new LegendEntry();
        deepSleepEntry.label = this.akDeepSleep.label;
        deepSleepEntry.formColor = this.akDeepSleep.color.intValue();
        legendEntries.add(deepSleepEntry);
        chart.getLegend().setCustom(legendEntries);
        chart.getLegend().setTextColor(this.LEGEND_TEXT_COLOR);
        chart.getLegend().setWordWrapEnabled(true);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    }

    private String getHM(long value) {
        return DateTimeUtils.formatDurationHoursMinutes(value, TimeUnit.MINUTES);
    }

    /* access modifiers changed from: package-private */
    public String getAverage(float value) {
        return getHM((long) value);
    }
}
