package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.HeartRateUtils;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.AbstractChartFragment;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.SleepAnalysis;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepChartFragment extends AbstractChartFragment {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) ActivitySleepChartFragment.class);
    private LineChart mActivityChart;
    private PieChart mSleepAmountChart;
    private TextView mSleepchartInfo;
    private int mSmartAlarmFrom = -1;
    private int mSmartAlarmGoneOff = -1;
    private int mSmartAlarmTo = -1;
    private int mTimestampFrom = -1;

    public SleepChartFragment() {
        super(new String[0]);
    }

    /* access modifiers changed from: protected */
    public ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler db, GBDevice device) {
        List<? extends ActivitySample> samples;
        Prefs prefs = GBApplication.getPrefs();
        if (prefs.getBoolean("chart_sleep_range_24h", false)) {
            samples = getSamples(db, device);
        } else {
            samples = getSamplesofSleep(db, device);
        }
        MySleepChartsData mySleepChartsData = refreshSleepAmounts(device, samples);
        if (!prefs.getBoolean("chart_sleep_range_24h", false) && mySleepChartsData.sleepSessions.size() > 0) {
            long tstart = ((SleepAnalysis.SleepSession) mySleepChartsData.sleepSessions.get(0)).getSleepStart().getTime() / 1000;
            long tend = ((SleepAnalysis.SleepSession) mySleepChartsData.sleepSessions.get(mySleepChartsData.sleepSessions.size() - 1)).getSleepEnd().getTime() / 1000;
            Iterator<? extends ActivitySample> it = samples.iterator();
            while (it.hasNext()) {
                ActivitySample sample = (ActivitySample) it.next();
                if (((long) sample.getTimestamp()) < tstart || ((long) sample.getTimestamp()) > tend) {
                    it.remove();
                }
            }
        }
        return new MyChartsData(mySleepChartsData, refresh(device, samples));
    }

    private MySleepChartsData refreshSleepAmounts(GBDevice mGBDevice, List<? extends ActivitySample> samples) {
        List<PieEntry> entries;
        List<PieEntry> entries2;
        List<SleepAnalysis.SleepSession> sleepSessions = new SleepAnalysis().calculateSleepSessions(samples);
        PieData data = new PieData();
        long lightSleepDuration = calculateLightSleepDuration(sleepSessions);
        long deepSleepDuration = calculateDeepSleepDuration(sleepSessions);
        long totalSeconds = lightSleepDuration + deepSleepDuration;
        if (sleepSessions.isEmpty()) {
            entries2 = Collections.emptyList();
            entries = Collections.emptyList();
        } else {
            List<PieEntry> entries3 = Arrays.asList(new PieEntry[]{new PieEntry((float) lightSleepDuration, getActivity().getString(C0889R.string.abstract_chart_fragment_kind_light_sleep)), new PieEntry((float) deepSleepDuration, getActivity().getString(C0889R.string.abstract_chart_fragment_kind_deep_sleep))});
            List<PieEntry> list = entries3;
            entries = Arrays.asList(new Integer[]{getColorFor(2), getColorFor(4)});
            entries2 = list;
        }
        String totalSleep = DateTimeUtils.formatDurationHoursMinutes(totalSeconds, TimeUnit.SECONDS);
        PieDataSet set = new PieDataSet(entries2, "");
        set.setValueFormatter(new ValueFormatter() {
            public String getFormattedValue(float value) {
                return DateTimeUtils.formatDurationHoursMinutes((long) value, TimeUnit.SECONDS);
            }
        });
        set.setColors((List<Integer>) entries);
        set.setValueTextColor(this.DESCRIPTION_COLOR);
        set.setValueTextSize(13.0f);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        data.setDataSet(set);
        return new MySleepChartsData(totalSleep, data, sleepSessions);
    }

    private long calculateLightSleepDuration(List<SleepAnalysis.SleepSession> sleepSessions) {
        long result = 0;
        for (SleepAnalysis.SleepSession sleepSession : sleepSessions) {
            result += sleepSession.getLightSleepDuration();
        }
        return result;
    }

    private long calculateDeepSleepDuration(List<SleepAnalysis.SleepSession> sleepSessions) {
        long result = 0;
        for (SleepAnalysis.SleepSession sleepSession : sleepSessions) {
            result += sleepSession.getDeepSleepDuration();
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void updateChartsnUIThread(ChartsData chartsData) {
        MyChartsData mcd = (MyChartsData) chartsData;
        MySleepChartsData pieData = mcd.getPieData();
        this.mSleepAmountChart.setCenterText(pieData.getTotalSleep());
        this.mSleepAmountChart.setData(pieData.getPieData());
        this.mActivityChart.setData(null);
        this.mActivityChart.getXAxis().setValueFormatter(mcd.getChartsData().getXValueFormatter());
        this.mActivityChart.setData(mcd.getChartsData().getData());
        this.mSleepchartInfo.setText(buildYouSleptText(pieData));
    }

    private String buildYouSleptText(MySleepChartsData pieData) {
        StringBuilder result = new StringBuilder();
        if (pieData.getSleepSessions().isEmpty()) {
            result.append(getContext().getString(C0889R.string.you_did_not_sleep));
        } else {
            for (SleepAnalysis.SleepSession sleepSession : pieData.getSleepSessions()) {
                result.append(getContext().getString(C0889R.string.you_slept, new Object[]{DateTimeUtils.timeToString(sleepSession.getSleepStart()), DateTimeUtils.timeToString(sleepSession.getSleepEnd())}));
                result.append(10);
            }
        }
        return result.toString();
    }

    public String getTitle() {
        return getString(C0889R.string.sleepchart_your_sleep);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(C0889R.layout.fragment_sleepchart, container, false);
        this.mActivityChart = (LineChart) rootView.findViewById(C0889R.C0891id.sleepchart);
        this.mSleepAmountChart = (PieChart) rootView.findViewById(C0889R.C0891id.sleepchart_pie_light_deep);
        this.mSleepchartInfo = (TextView) rootView.findViewById(C0889R.C0891id.sleepchart_info);
        setupActivityChart();
        setupSleepAmountChart();
        refresh();
        return rootView;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ChartsHost.REFRESH)) {
            this.mSmartAlarmFrom = intent.getIntExtra("smartalarm_from", -1);
            this.mSmartAlarmTo = intent.getIntExtra("smartalarm_to", -1);
            this.mTimestampFrom = intent.getIntExtra("recording_base_timestamp", -1);
            this.mSmartAlarmGoneOff = intent.getIntExtra("alarm_gone_off", -1);
            refresh();
            return;
        }
        super.onReceive(context, intent);
    }

    private void setupSleepAmountChart() {
        this.mSleepAmountChart.setBackgroundColor(this.BACKGROUND_COLOR);
        this.mSleepAmountChart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        this.mSleepAmountChart.setEntryLabelColor(this.DESCRIPTION_COLOR);
        this.mSleepAmountChart.getDescription().setText("");
        this.mSleepAmountChart.setNoDataText("");
        this.mSleepAmountChart.getLegend().setEnabled(false);
    }

    private void setupActivityChart() {
        this.mActivityChart.setBackgroundColor(this.BACKGROUND_COLOR);
        this.mActivityChart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        configureBarLineChartDefaults(this.mActivityChart);
        XAxis x = this.mActivityChart.getXAxis();
        x.setDrawLabels(true);
        x.setDrawGridLines(false);
        x.setEnabled(true);
        x.setTextColor(this.CHART_TEXT_COLOR);
        x.setDrawLimitLinesBehindData(true);
        YAxis y = this.mActivityChart.getAxisLeft();
        y.setDrawGridLines(false);
        y.setAxisMaximum(1.0f);
        y.setAxisMinimum(0.0f);
        y.setDrawTopYLabelEntry(false);
        y.setTextColor(this.CHART_TEXT_COLOR);
        y.setEnabled(true);
        YAxis yAxisRight = this.mActivityChart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(supportsHeartrate(getChartsHost().getDevice()));
        yAxisRight.setDrawLabels(true);
        yAxisRight.setDrawTopYLabelEntry(true);
        yAxisRight.setTextColor(this.CHART_TEXT_COLOR);
        yAxisRight.setAxisMaxValue((float) HeartRateUtils.getInstance().getMaxHeartRate());
        yAxisRight.setAxisMinValue((float) HeartRateUtils.getInstance().getMinHeartRate());
    }

    /* access modifiers changed from: protected */
    public void setupLegend(Chart chart) {
        List<LegendEntry> legendEntries = new ArrayList<>(3);
        LegendEntry lightSleepEntry = new LegendEntry();
        lightSleepEntry.label = this.akLightSleep.label;
        lightSleepEntry.formColor = this.akLightSleep.color.intValue();
        legendEntries.add(lightSleepEntry);
        LegendEntry deepSleepEntry = new LegendEntry();
        deepSleepEntry.label = this.akDeepSleep.label;
        deepSleepEntry.formColor = this.akDeepSleep.color.intValue();
        legendEntries.add(deepSleepEntry);
        if (supportsHeartrate(getChartsHost().getDevice())) {
            LegendEntry hrEntry = new LegendEntry();
            hrEntry.label = this.HEARTRATE_LABEL;
            hrEntry.formColor = this.HEARTRATE_COLOR;
            legendEntries.add(hrEntry);
        }
        chart.getLegend().setCustom(legendEntries);
        chart.getLegend().setTextColor(this.LEGEND_TEXT_COLOR);
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return super.getAllSamples(db, device, tsFrom, tsTo);
    }

    /* access modifiers changed from: protected */
    public void renderCharts() {
        this.mActivityChart.animateX(250, Easing.EaseInOutQuart);
        this.mSleepAmountChart.invalidate();
    }

    private static class MySleepChartsData extends ChartsData {
        private final PieData pieData;
        /* access modifiers changed from: private */
        public final List<SleepAnalysis.SleepSession> sleepSessions;
        private String totalSleep;

        public MySleepChartsData(String totalSleep2, PieData pieData2, List<SleepAnalysis.SleepSession> sleepSessions2) {
            this.totalSleep = totalSleep2;
            this.pieData = pieData2;
            this.sleepSessions = sleepSessions2;
        }

        public PieData getPieData() {
            return this.pieData;
        }

        public CharSequence getTotalSleep() {
            return this.totalSleep;
        }

        public List<SleepAnalysis.SleepSession> getSleepSessions() {
            return this.sleepSessions;
        }
    }

    private static class MyChartsData extends ChartsData {
        private final AbstractChartFragment.DefaultChartsData<LineData> chartsData;
        private final MySleepChartsData pieData;

        public MyChartsData(MySleepChartsData pieData2, AbstractChartFragment.DefaultChartsData<LineData> chartsData2) {
            this.pieData = pieData2;
            this.chartsData = chartsData2;
        }

        public MySleepChartsData getPieData() {
            return this.pieData;
        }

        public AbstractChartFragment.DefaultChartsData<LineData> getChartsData() {
            return this.chartsData;
        }
    }
}
