package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.HeartRateUtils;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.AbstractChartFragment;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivitySleepChartFragment extends AbstractChartFragment {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) ActivitySleepChartFragment.class);
    private LineChart mChart;
    private int mSmartAlarmFrom = -1;
    private int mSmartAlarmGoneOff = -1;
    private int mSmartAlarmTo = -1;
    private int mTimestampFrom = -1;

    public ActivitySleepChartFragment() {
        super(new String[0]);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(C0889R.layout.fragment_charts, container, false);
        this.mChart = (LineChart) rootView.findViewById(C0889R.C0891id.activitysleepchart);
        setupChart();
        return rootView;
    }

    public String getTitle() {
        return getString(C0889R.string.activity_sleepchart_activity_and_sleep);
    }

    private void setupChart() {
        this.mChart.setBackgroundColor(this.BACKGROUND_COLOR);
        this.mChart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        configureBarLineChartDefaults(this.mChart);
        XAxis x = this.mChart.getXAxis();
        x.setDrawLabels(true);
        x.setDrawGridLines(false);
        x.setEnabled(true);
        x.setTextColor(this.CHART_TEXT_COLOR);
        x.setDrawLimitLinesBehindData(true);
        YAxis y = this.mChart.getAxisLeft();
        y.setDrawGridLines(false);
        y.setAxisMaximum(1.0f);
        y.setAxisMinimum(0.0f);
        y.setDrawTopYLabelEntry(false);
        y.setTextColor(this.CHART_TEXT_COLOR);
        y.setEnabled(true);
        YAxis yAxisRight = this.mChart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(supportsHeartrate(getChartsHost().getDevice()));
        yAxisRight.setDrawLabels(true);
        yAxisRight.setDrawTopYLabelEntry(true);
        yAxisRight.setTextColor(this.CHART_TEXT_COLOR);
        yAxisRight.setAxisMaximum((float) HeartRateUtils.getInstance().getMaxHeartRate());
        yAxisRight.setAxisMinimum((float) HeartRateUtils.getInstance().getMinHeartRate());
        refresh();
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

    /* access modifiers changed from: protected */
    public ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler db, GBDevice device) {
        return refresh(device, getSamples(db, device));
    }

    /* access modifiers changed from: protected */
    public void updateChartsnUIThread(ChartsData chartsData) {
        AbstractChartFragment.DefaultChartsData dcd = (AbstractChartFragment.DefaultChartsData) chartsData;
        this.mChart.getLegend().setTextColor(this.LEGEND_TEXT_COLOR);
        this.mChart.setData(null);
        this.mChart.getXAxis().setValueFormatter(dcd.getXValueFormatter());
        this.mChart.setData((LineData) dcd.getData());
    }

    /* access modifiers changed from: protected */
    public void renderCharts() {
        this.mChart.animateX(250, Easing.EaseInOutQuart);
    }

    /* access modifiers changed from: protected */
    public void setupLegend(Chart chart) {
        List<LegendEntry> legendEntries = new ArrayList<>(5);
        LegendEntry activityEntry = new LegendEntry();
        activityEntry.label = this.akActivity.label;
        activityEntry.formColor = this.akActivity.color.intValue();
        legendEntries.add(activityEntry);
        LegendEntry lightSleepEntry = new LegendEntry();
        lightSleepEntry.label = this.akLightSleep.label;
        lightSleepEntry.formColor = this.akLightSleep.color.intValue();
        legendEntries.add(lightSleepEntry);
        LegendEntry deepSleepEntry = new LegendEntry();
        deepSleepEntry.label = this.akDeepSleep.label;
        deepSleepEntry.formColor = this.akDeepSleep.color.intValue();
        legendEntries.add(deepSleepEntry);
        LegendEntry notWornEntry = new LegendEntry();
        notWornEntry.label = this.akNotWorn.label;
        notWornEntry.formColor = this.akNotWorn.color.intValue();
        legendEntries.add(notWornEntry);
        if (supportsHeartrate(getChartsHost().getDevice())) {
            LegendEntry hrEntry = new LegendEntry();
            hrEntry.label = this.HEARTRATE_LABEL;
            hrEntry.formColor = this.HEARTRATE_COLOR;
            legendEntries.add(hrEntry);
        }
        chart.getLegend().setCustom(legendEntries);
        chart.getLegend().setWordWrapEnabled(true);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return getAllSamples(db, device, tsFrom, tsTo);
    }
}
