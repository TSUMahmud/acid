package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.HeartRateUtils;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.AbstractChartFragment;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveActivityFragment extends AbstractChartFragment {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) LiveActivityFragment.class);
    private static final int MAX_STEPS_PER_MINUTE = 300;
    private static final int MIN_STEPS_PER_MINUTE = 60;
    private static final int RESET_COUNT = 10;
    private int mHeartRate;
    private LineDataSet mHeartRateSet;
    private LineDataSet mHistorySet;
    private int mMaxHeartRate = 0;
    private TextView mMaxHeartRateView;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == 179490685 && action.equals(DeviceService.ACTION_REALTIME_SAMPLES)) ? (char) 0 : 65535) == 0) {
                LiveActivityFragment.this.addSample((ActivitySample) intent.getSerializableExtra(DeviceService.EXTRA_REALTIME_SAMPLE));
            }
        }
    };
    private final Steps mSteps = new Steps();
    private CustomBarChart mStepsPerMinuteCurrentChart;
    private BarDataSet mStepsPerMinuteData;
    private BarLineChartBase mStepsPerMinuteHistoryChart;
    private CustomBarChart mTotalStepsChart;
    private BarDataSet mTotalStepsData;
    /* access modifiers changed from: private */
    public int maxStepsResetCounter;
    private ScheduledExecutorService pulseScheduler;
    private BarEntry stepsPerMinuteEntry;
    private BarEntry totalStepsEntry;
    private AbstractChartFragment.TimestampTranslation tsTranslation;

    public LiveActivityFragment() {
        super(new String[0]);
    }

    private class Steps {
        private int currentStepsPerMinute;
        private int lastStepsPerMinute;
        private int lastTimestamp;
        /* access modifiers changed from: private */
        public int maxStepsPerMinute;
        private int steps;

        private Steps() {
        }

        public int getStepsPerMinute(boolean reset) {
            this.lastStepsPerMinute = this.currentStepsPerMinute;
            int result = this.currentStepsPerMinute;
            if (reset) {
                this.currentStepsPerMinute = 0;
            }
            return result;
        }

        public int getTotalSteps() {
            return this.steps;
        }

        public int getMaxStepsPerMinute() {
            return this.maxStepsPerMinute;
        }

        public void updateCurrentSteps(int stepsDelta, int timestamp) {
            try {
                if (this.steps == 0) {
                    this.steps += stepsDelta;
                    this.lastTimestamp = timestamp;
                    return;
                }
                this.currentStepsPerMinute = calculateStepsPerMinute(stepsDelta, timestamp - this.lastTimestamp);
                if (this.currentStepsPerMinute > this.maxStepsPerMinute) {
                    this.maxStepsPerMinute = this.currentStepsPerMinute;
                    int unused = LiveActivityFragment.this.maxStepsResetCounter = 0;
                }
                this.steps += stepsDelta;
                this.lastTimestamp = timestamp;
            } catch (Exception ex) {
                C1238GB.toast(LiveActivityFragment.this.getContext(), ex.getMessage(), 0, 3, ex);
            }
        }

        private int calculateStepsPerMinute(int stepsDelta, int seconds) {
            if (stepsDelta == 0) {
                return 0;
            }
            if (seconds > 0) {
                int result = (int) (((float) stepsDelta) * ((float) (60 / seconds)));
                if (result > LiveActivityFragment.MAX_STEPS_PER_MINUTE) {
                    return this.lastStepsPerMinute;
                }
                return result;
            }
            throw new IllegalArgumentException("delta in seconds is <= 0 -- time change?");
        }
    }

    /* access modifiers changed from: private */
    public void addSample(ActivitySample sample) {
        int heartRate = sample.getHeartRate();
        int timestamp = this.tsTranslation.shorten(sample.getTimestamp());
        if (HeartRateUtils.getInstance().isValidHeartRateValue(heartRate)) {
            setCurrentHeartRate(heartRate, timestamp);
        }
        int steps = sample.getSteps();
        if (steps > 0) {
            addEntries(steps, timestamp);
        }
    }

    private int translateTimestampFrom(Intent intent) {
        return translateTimestamp(intent.getLongExtra("timestamp", System.currentTimeMillis()));
    }

    private int translateTimestamp(long tsMillis) {
        return this.tsTranslation.shorten((int) (tsMillis / 1000));
    }

    private void setCurrentHeartRate(int heartRate, int timestamp) {
        addHistoryDataSet(true);
        this.mHeartRate = heartRate;
        int i = this.mMaxHeartRate;
        int i2 = this.mHeartRate;
        if (i < i2) {
            this.mMaxHeartRate = i2;
        }
        this.mMaxHeartRateView.setText(getContext().getString(C0889R.string.live_activity_max_heart_rate, new Object[]{Integer.valueOf(heartRate), Integer.valueOf(this.mMaxHeartRate)}));
    }

    private int getCurrentHeartRate() {
        int result = this.mHeartRate;
        this.mHeartRate = -1;
        return result;
    }

    private void addEntries(int steps, int timestamp) {
        this.mSteps.updateCurrentSteps(steps, timestamp);
        int i = this.maxStepsResetCounter + 1;
        this.maxStepsResetCounter = i;
        if (i > 10) {
            this.maxStepsResetCounter = 0;
            int unused = this.mSteps.maxStepsPerMinute = 0;
        }
        Logger logger = LOG;
        logger.info("Steps: " + steps + ", total: " + this.mSteps.getTotalSteps() + ", current: " + this.mSteps.getStepsPerMinute(false));
    }

    private void addEntries(int timestamp) {
        this.mTotalStepsChart.setSingleEntryYValue((float) this.mSteps.getTotalSteps());
        YAxis stepsPerMinuteCurrentYAxis = this.mStepsPerMinuteCurrentChart.getAxisLeft();
        LimitLine target = new LimitLine((float) this.mSteps.getMaxStepsPerMinute());
        stepsPerMinuteCurrentYAxis.removeAllLimitLines();
        stepsPerMinuteCurrentYAxis.addLimitLine(target);
        int stepsPerMinute = this.mSteps.getStepsPerMinute(true);
        this.mStepsPerMinuteCurrentChart.setSingleEntryYValue((float) stepsPerMinute);
        if (addHistoryDataSet(false)) {
            ChartData data = this.mStepsPerMinuteHistoryChart.getData();
            if (stepsPerMinute < 0) {
                stepsPerMinute = 0;
            }
            this.mHistorySet.addEntry(new Entry((float) timestamp, (float) stepsPerMinute));
            int hr = getCurrentHeartRate();
            if (hr > HeartRateUtils.getInstance().getMinHeartRate()) {
                this.mHeartRateSet.addEntry(new Entry((float) timestamp, (float) hr));
            }
        }
    }

    private boolean addHistoryDataSet(boolean force) {
        if (this.mStepsPerMinuteHistoryChart.getData() != null) {
            return true;
        }
        if (!force && this.mSteps.getTotalSteps() <= 0) {
            return false;
        }
        LineData data = new LineData();
        data.addDataSet(this.mHistorySet);
        data.addDataSet(this.mHeartRateSet);
        this.mStepsPerMinuteHistoryChart.setData(data);
        return true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        this.tsTranslation = new AbstractChartFragment.TimestampTranslation();
        View rootView = inflater.inflate(C0889R.layout.fragment_live_activity, container, false);
        this.mStepsPerMinuteCurrentChart = (CustomBarChart) rootView.findViewById(C0889R.C0891id.livechart_steps_per_minute_current);
        this.mTotalStepsChart = (CustomBarChart) rootView.findViewById(C0889R.C0891id.livechart_steps_total);
        this.mStepsPerMinuteHistoryChart = (BarLineChartBase) rootView.findViewById(C0889R.C0891id.livechart_steps_per_minute_history);
        this.totalStepsEntry = new BarEntry(1.0f, 0.0f);
        this.stepsPerMinuteEntry = new BarEntry(1.0f, 0.0f);
        this.mStepsPerMinuteData = setupCurrentChart(this.mStepsPerMinuteCurrentChart, this.stepsPerMinuteEntry, getString(C0889R.string.live_activity_current_steps_per_minute));
        this.mStepsPerMinuteData.setDrawValues(true);
        this.mStepsPerMinuteData.setValueTextColor(this.DESCRIPTION_COLOR);
        this.mTotalStepsData = setupTotalStepsChart(this.mTotalStepsChart, this.totalStepsEntry, getString(C0889R.string.live_activity_total_steps));
        this.mTotalStepsData.setDrawValues(true);
        this.mTotalStepsData.setValueTextColor(this.DESCRIPTION_COLOR);
        setupHistoryChart(this.mStepsPerMinuteHistoryChart);
        this.mMaxHeartRateView = (TextView) rootView.findViewById(C0889R.C0891id.livechart_max_heart_rate);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.mReceiver, filterLocal);
        return rootView;
    }

    public void onPause() {
        enableRealtimeTracking(false);
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        enableRealtimeTracking(true);
    }

    private ScheduledExecutorService startActivityPulse() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            public void run() {
                FragmentActivity activity = LiveActivityFragment.this.getActivity();
                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            LiveActivityFragment.this.pulse();
                        }
                    });
                }
            }
        }, 0, (long) getPulseIntervalMillis(), TimeUnit.MILLISECONDS);
        return service;
    }

    private void stopActivityPulse() {
        ScheduledExecutorService scheduledExecutorService = this.pulseScheduler;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            this.pulseScheduler = null;
        }
    }

    /* access modifiers changed from: private */
    public void pulse() {
        addEntries(translateTimestamp(System.currentTimeMillis()));
        LineData historyData = (LineData) this.mStepsPerMinuteHistoryChart.getData();
        if (historyData != null) {
            historyData.notifyDataChanged();
            this.mTotalStepsData.notifyDataSetChanged();
            this.mStepsPerMinuteData.notifyDataSetChanged();
            this.mStepsPerMinuteHistoryChart.notifyDataSetChanged();
            renderCharts();
            GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(true);
        }
    }

    private int getPulseIntervalMillis() {
        return 1000;
    }

    /* access modifiers changed from: protected */
    public void onMadeVisibleInActivity() {
        super.onMadeVisibleInActivity();
        enableRealtimeTracking(true);
    }

    private void enableRealtimeTracking(boolean enable) {
        if (!enable || this.pulseScheduler == null) {
            GBApplication.deviceService().onEnableRealtimeSteps(enable);
            GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(enable);
            if (enable) {
                if (getActivity() != null) {
                    getActivity().getWindow().addFlags(128);
                }
                this.pulseScheduler = startActivityPulse();
                return;
            }
            stopActivityPulse();
            if (getActivity() != null) {
                getActivity().getWindow().clearFlags(128);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMadeInvisibleInActivity() {
        enableRealtimeTracking(false);
        super.onMadeInvisibleInActivity();
    }

    public void onDestroyView() {
        onMadeInvisibleInActivity();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.mReceiver);
        super.onDestroyView();
    }

    private BarDataSet setupCurrentChart(CustomBarChart chart, BarEntry entry, String title) {
        this.mStepsPerMinuteCurrentChart.getAxisLeft().setAxisMaximum(300.0f);
        return setupCommonChart(chart, entry, title);
    }

    private BarDataSet setupCommonChart(CustomBarChart chart, BarEntry entry, String title) {
        chart.setSinglAnimationEntry(entry);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setEnabled(false);
        chart.getXAxis().setTextColor(this.CHART_TEXT_COLOR);
        chart.getAxisLeft().setTextColor(this.CHART_TEXT_COLOR);
        chart.setBackgroundColor(this.BACKGROUND_COLOR);
        chart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        chart.getDescription().setText(title);
        chart.setNoDataText("");
        chart.getAxisRight().setEnabled(false);
        List<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        entries.add(entry);
        colors.add(this.akActivity.color);
        colors.add(this.akActivity.color);
        colors.add(this.akActivity.color);
        BarDataSet set = new BarDataSet(entries, "");
        set.setDrawValues(false);
        set.setColors(colors);
        chart.setData(new BarData(set));
        chart.getLegend().setEnabled(false);
        return set;
    }

    private BarDataSet setupTotalStepsChart(CustomBarChart chart, BarEntry entry, String label) {
        this.mTotalStepsChart.getAxisLeft().addLimitLine(new LimitLine((float) GBApplication.getPrefs().getInt("mi_fitness_goal", ActivityUser.defaultUserStepsGoal), "ss"));
        this.mTotalStepsChart.getAxisLeft().setAxisMinimum(0.0f);
        this.mTotalStepsChart.setAutoScaleMinMaxEnabled(true);
        return setupCommonChart(chart, entry, label);
    }

    private void setupHistoryChart(BarLineChartBase chart) {
        configureBarLineChartDefaults(chart);
        chart.setTouchEnabled(false);
        chart.setBackgroundColor(this.BACKGROUND_COLOR);
        chart.getDescription().setTextColor(this.DESCRIPTION_COLOR);
        chart.getDescription().setText(getString(C0889R.string.live_activity_steps_per_minute_history));
        chart.setNoDataText(getString(C0889R.string.live_activity_start_your_activity));
        chart.getLegend().setEnabled(false);
        Paint infoPaint = chart.getPaint(7);
        infoPaint.setTextSize(Utils.convertDpToPixel(20.0f));
        infoPaint.setFakeBoldText(true);
        chart.setPaint(infoPaint, 7);
        XAxis x = chart.getXAxis();
        x.setDrawLabels(true);
        x.setDrawGridLines(false);
        x.setEnabled(true);
        x.setTextColor(this.CHART_TEXT_COLOR);
        x.setValueFormatter(new AbstractChartFragment.SampleXLabelFormatter(this.tsTranslation));
        x.setDrawLimitLinesBehindData(true);
        YAxis y = chart.getAxisLeft();
        y.setDrawGridLines(false);
        y.setDrawTopYLabelEntry(false);
        y.setTextColor(this.CHART_TEXT_COLOR);
        y.setEnabled(true);
        y.setAxisMinimum(0.0f);
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(true);
        yAxisRight.setDrawLabels(true);
        yAxisRight.setDrawTopYLabelEntry(false);
        yAxisRight.setTextColor(this.CHART_TEXT_COLOR);
        this.mHistorySet = new LineDataSet(new ArrayList(), getString(C0889R.string.live_activity_steps_history));
        this.mHistorySet.setAxisDependency(YAxis.AxisDependency.LEFT);
        this.mHistorySet.setColor(this.akActivity.color.intValue());
        this.mHistorySet.setDrawCircles(false);
        this.mHistorySet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        this.mHistorySet.setDrawFilled(true);
        this.mHistorySet.setDrawValues(false);
        this.mHeartRateSet = createHeartrateSet(new ArrayList(), getString(C0889R.string.live_activity_heart_rate));
        this.mHeartRateSet.setDrawValues(false);
    }

    public String getTitle() {
        return getContext().getString(C0889R.string.liveactivity_live_activity);
    }

    /* access modifiers changed from: protected */
    public void showDateBar(boolean show) {
        super.showDateBar(false);
    }

    /* access modifiers changed from: protected */
    public void refresh() {
    }

    /* access modifiers changed from: protected */
    public ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler db, GBDevice device) {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: protected */
    public void updateChartsnUIThread(ChartsData chartsData) {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: protected */
    public void renderCharts() {
        this.mStepsPerMinuteCurrentChart.animateY(150);
        this.mTotalStepsChart.animateY(150);
        this.mStepsPerMinuteHistoryChart.invalidate();
    }

    /* access modifiers changed from: protected */
    public List<ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        throw new UnsupportedOperationException("no db access supported for live activity");
    }

    /* access modifiers changed from: protected */
    public void setupLegend(Chart chart) {
    }
}
