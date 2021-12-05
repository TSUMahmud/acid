package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.AbstractChartFragment;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmounts;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.util.LimitedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWeekChartFragment extends AbstractChartFragment {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractWeekChartFragment.class);
    protected final int TOTAL_DAYS = getRangeDays();
    private TextView mBalanceView;
    private Locale mLocale;
    private int mOffsetHours = getOffsetHours();
    private int mTargetValue = 0;
    private PieChart mTodayPieChart;
    private BarChart mWeekChart;

    /* access modifiers changed from: protected */
    public abstract long calculateBalance(ActivityAmounts activityAmounts);

    /* access modifiers changed from: package-private */
    public abstract String formatPieValue(long j);

    /* access modifiers changed from: package-private */
    public abstract String getAverage(float f);

    /* access modifiers changed from: protected */
    public abstract String getBalanceMessage(long j, int i);

    /* access modifiers changed from: package-private */
    public abstract ValueFormatter getBarValueFormatter();

    /* access modifiers changed from: package-private */
    public abstract int[] getColors();

    /* access modifiers changed from: package-private */
    public abstract int getGoal();

    /* access modifiers changed from: package-private */
    public abstract int getOffsetHours();

    /* access modifiers changed from: package-private */
    public abstract String getPieDescription(int i);

    /* access modifiers changed from: package-private */
    public abstract String[] getPieLabels();

    /* access modifiers changed from: package-private */
    public abstract ValueFormatter getPieValueFormatter();

    /* access modifiers changed from: package-private */
    public abstract float[] getTotalsForActivityAmounts(ActivityAmounts activityAmounts);

    /* access modifiers changed from: package-private */
    public abstract ValueFormatter getYAxisFormatter();

    public AbstractWeekChartFragment() {
        super(new String[0]);
    }

    /* access modifiers changed from: protected */
    public ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler db, GBDevice device) {
        Calendar day = Calendar.getInstance();
        day.setTime(chartsHost.getEndDate());
        return new MyChartsData(refreshDayPie(db, day, device), refreshWeekBeforeData(db, this.mWeekChart, day, device));
    }

    /* access modifiers changed from: protected */
    public void updateChartsnUIThread(ChartsData chartsData) {
        MyChartsData mcd = (MyChartsData) chartsData;
        setupLegend(this.mWeekChart);
        this.mTodayPieChart.setCenterText(mcd.getDayData().centerText);
        this.mTodayPieChart.setData(mcd.getDayData().data);
        if (GBApplication.getPrefs().getBoolean("charts_range", true)) {
            BarChart barChart = this.mWeekChart;
            barChart.setRenderer(new AngledLabelsChartRenderer(barChart, barChart.getAnimator(), this.mWeekChart.getViewPortHandler()));
        }
        this.mWeekChart.setData(null);
        this.mWeekChart.setData(mcd.getWeekBeforeData().getData());
        this.mWeekChart.getXAxis().setValueFormatter(mcd.getWeekBeforeData().getXValueFormatter());
        this.mBalanceView.setText(mcd.getWeekBeforeData().getBalanceMessage());
    }

    /* access modifiers changed from: protected */
    public void renderCharts() {
        this.mWeekChart.invalidate();
        this.mTodayPieChart.invalidate();
    }

    private String getWeeksChartsLabel(Calendar day) {
        if (GBApplication.getPrefs().getBoolean("charts_range", true)) {
            return String.valueOf(day.get(5));
        }
        return day.getDisplayName(7, 1, this.mLocale);
    }

    private WeekChartsData<BarData> refreshWeekBeforeData(DBHandler db, BarChart barChart, Calendar day, GBDevice device) {
        Calendar day2 = (Calendar) day.clone();
        day2.add(5, -this.TOTAL_DAYS);
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        long balance = 0;
        for (int counter = 0; counter < this.TOTAL_DAYS; counter++) {
            ActivityAmounts amounts = getActivityAmountsForDay(db, day2, device);
            balance += calculateBalance(amounts);
            entries.add(new BarEntry((float) counter, getTotalsForActivityAmounts(amounts)));
            labels.add(getWeeksChartsLabel(day2));
            day2.add(5, 1);
        }
        DBHandler dBHandler = db;
        GBDevice gBDevice = device;
        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(getColors());
        set.setValueFormatter(getBarValueFormatter());
        BarData barData = new BarData(set);
        barData.setValueTextColor(-7829368);
        barData.setValueTextSize(10.0f);
        LimitLine target = new LimitLine((float) this.mTargetValue);
        barChart.getAxisLeft().removeAllLimitLines();
        barChart.getAxisLeft().addLimitLine(target);
        float average = 0.0f;
        int i = this.TOTAL_DAYS;
        if (i > 0) {
            average = (float) Math.abs(balance / ((long) i));
        }
        LimitLine average_line = new LimitLine(average);
        average_line.setLabel(getString(C0889R.string.average, getAverage(average)));
        if (average > ((float) this.mTargetValue)) {
            average_line.setLineColor(-16711936);
            average_line.setTextColor(-16711936);
        } else {
            average_line.setLineColor(SupportMenu.CATEGORY_MASK);
            average_line.setTextColor(SupportMenu.CATEGORY_MASK);
        }
        if (average > 0.0f && GBApplication.getPrefs().getBoolean("charts_show_average", true)) {
            barChart.getAxisLeft().addLimitLine(average_line);
        }
        return new WeekChartsData<>(barData, new AbstractChartFragment.PreformattedXIndexLabelFormatter(labels), getBalanceMessage(balance, this.mTargetValue));
    }

    private DayData refreshDayPie(DBHandler db, Calendar day, GBDevice device) {
        PieData data = new PieData();
        List<PieEntry> entries = new ArrayList<>();
        PieDataSet set = new PieDataSet(entries, "");
        float[] totalValues = getTotalsForActivityAmounts(getActivityAmountsForDay(db, day, device));
        String[] pieLabels = getPieLabels();
        float totalValue = 0.0f;
        for (int i = 0; i < totalValues.length; i++) {
            float value = totalValues[i];
            totalValue += value;
            entries.add(new PieEntry(value, pieLabels[i]));
        }
        set.setColors(getColors());
        if (totalValues.length < 2) {
            int i2 = this.mTargetValue;
            if (totalValue < ((float) i2)) {
                entries.add(new PieEntry(((float) i2) - totalValue));
                set.addColor(-7829368);
            }
        }
        data.setDataSet(set);
        if (totalValues.length < 2) {
            data.setDrawValues(false);
        } else {
            set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            set.setValueTextColor(this.DESCRIPTION_COLOR);
            set.setValueTextSize(13.0f);
            set.setValueFormatter(getPieValueFormatter());
        }
        return new DayData(data, formatPieValue((long) totalValue));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mLocale = getResources().getConfiguration().locale;
        View rootView = inflater.inflate(C0889R.layout.fragment_weeksteps_chart, container, false);
        int goal = getGoal();
        if (goal >= 0) {
            this.mTargetValue = goal;
        }
        this.mTodayPieChart = (PieChart) rootView.findViewById(C0889R.C0891id.todaystepschart);
        this.mWeekChart = (BarChart) rootView.findViewById(C0889R.C0891id.weekstepschart);
        this.mBalanceView = (TextView) rootView.findViewById(C0889R.C0891id.balance);
        setupWeekChart();
        setupTodayPieChart();
        refresh();
        return rootView;
    }

    private void setupTodayPieChart() {
        this.mTodayPieChart.setBackgroundColor(this.BACKGROUND_COLOR);
        this.mTodayPieChart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        this.mTodayPieChart.setEntryLabelColor(this.DESCRIPTION_COLOR);
        this.mTodayPieChart.getDescription().setText(getPieDescription(this.mTargetValue));
        this.mTodayPieChart.setNoDataText("");
        this.mTodayPieChart.getLegend().setEnabled(false);
    }

    private void setupWeekChart() {
        this.mWeekChart.setBackgroundColor(this.BACKGROUND_COLOR);
        this.mWeekChart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        this.mWeekChart.getDescription().setText("");
        this.mWeekChart.setFitBars(true);
        configureBarLineChartDefaults(this.mWeekChart);
        XAxis x = this.mWeekChart.getXAxis();
        x.setDrawLabels(true);
        x.setDrawGridLines(false);
        x.setEnabled(true);
        x.setTextColor(this.CHART_TEXT_COLOR);
        x.setDrawLimitLinesBehindData(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis y = this.mWeekChart.getAxisLeft();
        y.setDrawGridLines(false);
        y.setDrawTopYLabelEntry(false);
        y.setTextColor(this.CHART_TEXT_COLOR);
        y.setDrawZeroLine(true);
        y.setSpaceBottom(0.0f);
        y.setAxisMinimum(0.0f);
        y.setValueFormatter(getYAxisFormatter());
        y.setEnabled(true);
        YAxis yAxisRight = this.mWeekChart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawTopYLabelEntry(false);
        yAxisRight.setTextColor(this.CHART_TEXT_COLOR);
    }

    private List<? extends ActivitySample> getSamplesOfDay(DBHandler db, Calendar day, int offsetHours, GBDevice device) {
        Calendar day2 = (Calendar) day.clone();
        day2.set(11, 0);
        day2.set(12, 0);
        day2.set(13, 0);
        day2.add(10, offsetHours);
        int startTs = (int) (day2.getTimeInMillis() / 1000);
        return getSamples(db, device, startTs, (86400 + startTs) - 1);
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return super.getAllSamples(db, device, tsFrom, tsTo);
    }

    private static class DayData {
        /* access modifiers changed from: private */
        public final CharSequence centerText;
        /* access modifiers changed from: private */
        public final PieData data;

        DayData(PieData data2, String centerText2) {
            this.data = data2;
            this.centerText = centerText2;
        }
    }

    private static class MyChartsData extends ChartsData {
        private final DayData dayData;
        private final WeekChartsData<BarData> weekBeforeData;

        MyChartsData(DayData dayData2, WeekChartsData<BarData> weekBeforeData2) {
            this.dayData = dayData2;
            this.weekBeforeData = weekBeforeData2;
        }

        /* access modifiers changed from: package-private */
        public DayData getDayData() {
            return this.dayData;
        }

        /* access modifiers changed from: package-private */
        public WeekChartsData<BarData> getWeekBeforeData() {
            return this.weekBeforeData;
        }
    }

    private ActivityAmounts getActivityAmountsForDay(DBHandler db, Calendar day, GBDevice device) {
        LimitedQueue activityAmountCache = null;
        ActivityAmounts amounts = null;
        Activity activity = getActivity();
        int key = ((int) (day.getTimeInMillis() / 1000)) + (this.mOffsetHours * 3600);
        if (activity != null) {
            activityAmountCache = ((ChartsActivity) activity).mActivityAmountCache;
            amounts = (ActivityAmounts) activityAmountCache.lookup(key);
        }
        if (amounts == null) {
            amounts = new ActivityAnalysis().calculateActivityAmounts(getSamplesOfDay(db, day, this.mOffsetHours, device));
            if (activityAmountCache != null) {
                activityAmountCache.add(key, amounts);
            }
        }
        return amounts;
    }

    private int getRangeDays() {
        if (GBApplication.getPrefs().getBoolean("charts_range", true)) {
            return 30;
        }
        return 7;
    }

    private class WeekChartsData<T extends ChartData<?>> extends AbstractChartFragment.DefaultChartsData<T> {
        private final String balanceMessage;

        public WeekChartsData(T data, AbstractChartFragment.PreformattedXIndexLabelFormatter xIndexLabelFormatter, String balanceMessage2) {
            super(data, xIndexLabelFormatter);
            this.balanceMessage = balanceMessage2;
        }

        public String getBalanceMessage() {
            return this.balanceMessage;
        }
    }
}
