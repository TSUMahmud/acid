package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedZonesFragment extends AbstractChartFragment {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) SpeedZonesFragment.class);
    private HorizontalBarChart mStatsChart;

    public SpeedZonesFragment() {
        super(new String[0]);
    }

    /* access modifiers changed from: protected */
    public ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler db, GBDevice device) {
        return new MyChartsData(refreshStats(getSamples(db, device)));
    }

    private MySpeedZonesData refreshStats(List<? extends ActivitySample> samples) {
        ActivityAnalysis analysis = new ActivityAnalysis();
        analysis.calculateActivityAmounts(samples);
        BarData data = new BarData();
        data.setValueTextColor(this.CHART_TEXT_COLOR);
        List<BarEntry> entries = new ArrayList<>();
        new ActivityUser();
        for (Map.Entry<Integer, Long> entry : analysis.stats.entrySet()) {
            entries.add(new BarEntry((float) entry.getKey().intValue(), (float) (entry.getValue().longValue() / 60)));
        }
        BarDataSet set = new BarDataSet(entries, "");
        set.setValueTextColor(this.CHART_TEXT_COLOR);
        set.setColors(getColorFor(1).intValue());
        data.addDataSet(set);
        return new MySpeedZonesData(data);
    }

    /* access modifiers changed from: protected */
    public void updateChartsnUIThread(ChartsData chartsData) {
        this.mStatsChart.setData(((MyChartsData) chartsData).getChartsData().getBarData());
    }

    public String getTitle() {
        return getString(C0889R.string.stats_title);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(C0889R.layout.fragment_statschart, container, false);
        this.mStatsChart = (HorizontalBarChart) rootView.findViewById(C0889R.C0891id.statschart);
        setupStatsChart();
        refresh();
        return rootView;
    }

    private void setupStatsChart() {
        this.mStatsChart.setBackgroundColor(this.BACKGROUND_COLOR);
        this.mStatsChart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        this.mStatsChart.setNoDataText("");
        this.mStatsChart.getLegend().setEnabled(false);
        this.mStatsChart.setTouchEnabled(false);
        this.mStatsChart.getDescription().setText("");
        this.mStatsChart.getXAxis().setTextColor(this.CHART_TEXT_COLOR);
        YAxis bottom = this.mStatsChart.getAxisRight();
        bottom.setTextColor(this.CHART_TEXT_COLOR);
        bottom.setGranularity(1.0f);
        YAxis top = this.mStatsChart.getAxisLeft();
        top.setTextColor(this.CHART_TEXT_COLOR);
        top.setGranularity(1.0f);
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return super.getAllSamples(db, device, tsFrom, tsTo);
    }

    /* access modifiers changed from: protected */
    public void setupLegend(Chart chart) {
        chart.getLegend().setEnabled(false);
    }

    /* access modifiers changed from: protected */
    public void renderCharts() {
        this.mStatsChart.invalidate();
    }

    private static class MySpeedZonesData extends ChartsData {
        private final BarData barData;

        MySpeedZonesData(BarData barData2) {
            this.barData = barData2;
        }

        /* access modifiers changed from: package-private */
        public BarData getBarData() {
            return this.barData;
        }
    }

    private static class MyChartsData extends ChartsData {
        private final MySpeedZonesData chartsData;

        MyChartsData(MySpeedZonesData chartsData2) {
            this.chartsData = chartsData2;
        }

        /* access modifiers changed from: package-private */
        public MySpeedZonesData getChartsData() {
            return this.chartsData;
        }
    }
}
