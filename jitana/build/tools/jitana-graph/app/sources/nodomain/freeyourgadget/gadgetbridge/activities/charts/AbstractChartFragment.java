package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBFragment;
import nodomain.freeyourgadget.gadgetbridge.activities.HeartRateUtils;
import nodomain.freeyourgadget.gadgetbridge.database.DBAccess;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChartFragment extends AbstractGBFragment {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractChartFragment.class);
    protected int AK_ACTIVITY_COLOR;
    protected int AK_DEEP_SLEEP_COLOR;
    protected int AK_LIGHT_SLEEP_COLOR;
    protected int AK_NOT_WORN_COLOR;
    protected final int ANIM_TIME = 250;
    protected int BACKGROUND_COLOR;
    protected int CHART_TEXT_COLOR;
    protected int DESCRIPTION_COLOR;
    protected int HEARTRATE_COLOR;
    protected int HEARTRATE_FILL_COLOR;
    protected String HEARTRATE_LABEL;
    protected int LEGEND_TEXT_COLOR;
    protected ActivityConfig akActivity;
    protected ActivityConfig akDeepSleep;
    protected ActivityConfig akLightSleep;
    protected ActivityConfig akNotWorn;
    private boolean mChartDirty = true;
    private final Set<String> mIntentFilterActions = new HashSet();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AbstractChartFragment.this.onReceive(context, intent);
        }
    };
    private AsyncTask refreshTask;

    /* access modifiers changed from: protected */
    public abstract List<? extends ActivitySample> getSamples(DBHandler dBHandler, GBDevice gBDevice, int i, int i2);

    public abstract String getTitle();

    /* access modifiers changed from: protected */
    public abstract ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler dBHandler, GBDevice gBDevice);

    /* access modifiers changed from: protected */
    public abstract void renderCharts();

    /* access modifiers changed from: protected */
    public abstract void setupLegend(Chart chart);

    /* access modifiers changed from: protected */
    public abstract void updateChartsnUIThread(ChartsData chartsData);

    public boolean isChartDirty() {
        return this.mChartDirty;
    }

    public boolean supportsHeartrate(GBDevice device) {
        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);
        return coordinator != null && coordinator.supportsHeartRateMeasurement(device);
    }

    protected static final class ActivityConfig {
        public final Integer color;
        public final String label;
        public final int type;

        public ActivityConfig(int kind, String label2, Integer color2) {
            this.type = kind;
            this.label = label2;
            this.color = color2;
        }
    }

    protected AbstractChartFragment(String... intentFilterActions) {
        if (intentFilterActions != null) {
            this.mIntentFilterActions.addAll(Arrays.asList(intentFilterActions));
        }
        this.mIntentFilterActions.add(ChartsHost.DATE_NEXT_DAY);
        this.mIntentFilterActions.add(ChartsHost.DATE_PREV_DAY);
        this.mIntentFilterActions.add(ChartsHost.DATE_NEXT_WEEK);
        this.mIntentFilterActions.add(ChartsHost.DATE_PREV_WEEK);
        this.mIntentFilterActions.add(ChartsHost.DATE_NEXT_MONTH);
        this.mIntentFilterActions.add(ChartsHost.DATE_PREV_MONTH);
        this.mIntentFilterActions.add(ChartsHost.REFRESH);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        IntentFilter filter = new IntentFilter();
        for (String action : this.mIntentFilterActions) {
            filter.addAction(action);
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.mReceiver, filter);
    }

    /* access modifiers changed from: protected */
    public void init() {
        Prefs prefs = GBApplication.getPrefs();
        TypedValue runningColor = new TypedValue();
        this.BACKGROUND_COLOR = GBApplication.getBackgroundColor(getContext());
        int textColor = GBApplication.getTextColor(getContext());
        this.DESCRIPTION_COLOR = textColor;
        this.LEGEND_TEXT_COLOR = textColor;
        this.CHART_TEXT_COLOR = ContextCompat.getColor(getContext(), C0889R.color.secondarytext);
        if (prefs.getBoolean("chart_heartrate_color", false)) {
            this.HEARTRATE_COLOR = ContextCompat.getColor(getContext(), C0889R.color.chart_heartrate_alternative);
        } else {
            this.HEARTRATE_COLOR = ContextCompat.getColor(getContext(), C0889R.color.chart_heartrate);
        }
        this.HEARTRATE_FILL_COLOR = ContextCompat.getColor(getContext(), C0889R.color.chart_heartrate_fill);
        getContext().getTheme().resolveAttribute(C0889R.attr.chart_activity, runningColor, true);
        this.AK_ACTIVITY_COLOR = runningColor.data;
        getContext().getTheme().resolveAttribute(C0889R.attr.chart_deep_sleep, runningColor, true);
        this.AK_DEEP_SLEEP_COLOR = runningColor.data;
        getContext().getTheme().resolveAttribute(C0889R.attr.chart_light_sleep, runningColor, true);
        this.AK_LIGHT_SLEEP_COLOR = runningColor.data;
        getContext().getTheme().resolveAttribute(C0889R.attr.chart_not_worn, runningColor, true);
        this.AK_NOT_WORN_COLOR = runningColor.data;
        this.HEARTRATE_LABEL = getContext().getString(C0889R.string.charts_legend_heartrate);
        this.akActivity = new ActivityConfig(1, getString(C0889R.string.abstract_chart_fragment_kind_activity), Integer.valueOf(this.AK_ACTIVITY_COLOR));
        this.akLightSleep = new ActivityConfig(2, getString(C0889R.string.abstract_chart_fragment_kind_light_sleep), Integer.valueOf(this.AK_LIGHT_SLEEP_COLOR));
        this.akDeepSleep = new ActivityConfig(4, getString(C0889R.string.abstract_chart_fragment_kind_deep_sleep), Integer.valueOf(this.AK_DEEP_SLEEP_COLOR));
        this.akNotWorn = new ActivityConfig(8, getString(C0889R.string.abstract_chart_fragment_kind_not_worn), Integer.valueOf(this.AK_NOT_WORN_COLOR));
    }

    private void setStartDate(Date date) {
        getChartsHost().setStartDate(date);
    }

    /* access modifiers changed from: protected */
    public ChartsHost getChartsHost() {
        return (ChartsHost) getActivity();
    }

    private void setEndDate(Date date) {
        getChartsHost().setEndDate(date);
    }

    public Date getStartDate() {
        return getChartsHost().getStartDate();
    }

    public Date getEndDate() {
        return getChartsHost().getEndDate();
    }

    /* access modifiers changed from: protected */
    public void onMadeVisibleInActivity() {
        super.onMadeVisibleInActivity();
        showDateBar(true);
        if (isChartDirty()) {
            refresh();
        }
    }

    /* access modifiers changed from: protected */
    public void showDateBar(boolean show) {
        getChartsHost().getDateBar().setVisibility(show ? 0 : 8);
    }

    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: protected */
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ChartsHost.REFRESH.equals(action)) {
            refresh();
        } else if (ChartsHost.DATE_NEXT_DAY.equals(action)) {
            handleDate(getStartDate(), getEndDate(), 1);
        } else if (ChartsHost.DATE_PREV_DAY.equals(action)) {
            handleDate(getStartDate(), getEndDate(), -1);
        } else if (ChartsHost.DATE_NEXT_WEEK.equals(action)) {
            handleDate(getStartDate(), getEndDate(), 7);
        } else if (ChartsHost.DATE_PREV_WEEK.equals(action)) {
            handleDate(getStartDate(), getEndDate(), -7);
        } else if (ChartsHost.DATE_NEXT_MONTH.equals(action)) {
            handleDate(getStartDate(), getEndDate(), 30);
        } else if (ChartsHost.DATE_PREV_MONTH.equals(action)) {
            handleDate(getStartDate(), getEndDate(), -30);
        }
    }

    /* access modifiers changed from: protected */
    public void handleDate(Date startDate, Date endDate, Integer Offset) {
        if (!isVisibleInActivity() || shiftDates(startDate, endDate, Offset.intValue())) {
            refreshIfVisible();
        }
    }

    /* access modifiers changed from: protected */
    public void refreshIfVisible() {
        if (isVisibleInActivity()) {
            refresh();
        } else {
            this.mChartDirty = true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean shiftDates(Date startDate, Date endDate, int offset) {
        return setDateRange(DateTimeUtils.shiftByDays(startDate, offset), DateTimeUtils.shiftByDays(endDate, offset));
    }

    /* access modifiers changed from: protected */
    public Integer getColorFor(int activityKind) {
        if (activityKind == 1) {
            return this.akActivity.color;
        }
        if (activityKind != 2) {
            return activityKind != 4 ? this.akActivity.color : this.akDeepSleep.color;
        }
        return this.akLightSleep.color;
    }

    /* access modifiers changed from: protected */
    public SampleProvider<? extends AbstractActivitySample> getProvider(DBHandler db, GBDevice device) {
        return DeviceHelper.getInstance().getCoordinator(device).getSampleProvider(device, db.getDaoSession());
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getAllSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return getProvider(db, device).getAllActivitySamples(tsFrom, tsTo);
    }

    /* access modifiers changed from: protected */
    public List<? extends AbstractActivitySample> getActivitySamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return getProvider(db, device).getActivitySamples(tsFrom, tsTo);
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSleepSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return getProvider(db, device).getSleepSamples(tsFrom, tsTo);
    }

    /* access modifiers changed from: protected */
    public void configureChartDefaults(Chart<?> chart) {
        chart.getXAxis().setValueFormatter(new TimestampValueFormatter());
        chart.getDescription().setText("");
        chart.setNoDataText(getString(C0889R.string.chart_no_data_synchronize));
        chart.setHighlightPerTapEnabled(false);
        chart.setTouchEnabled(true);
        setupLegend(chart);
    }

    /* access modifiers changed from: protected */
    public void configureBarLineChartDefaults(BarLineChartBase<?> chart) {
        configureChartDefaults(chart);
        if (chart instanceof BarChart) {
            ((BarChart) chart).setFitBars(true);
        }
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
    }

    /* access modifiers changed from: protected */
    public void refresh() {
        ChartsHost chartsHost = getChartsHost();
        if (chartsHost != null && chartsHost.getDevice() != null) {
            this.mChartDirty = false;
            updateDateInfo(getStartDate(), getEndDate());
            AsyncTask asyncTask = this.refreshTask;
            if (!(asyncTask == null || asyncTask.getStatus() == AsyncTask.Status.FINISHED)) {
                this.refreshTask.cancel(true);
            }
            this.refreshTask = createRefreshTask("Visualizing data", getActivity()).execute(new Object[0]);
        }
    }

    /* access modifiers changed from: protected */
    public DefaultChartsData<LineData> refresh(GBDevice gbDevice, List<? extends ActivitySample> samples) {
        TimestampTranslation tsTranslation;
        LineData lineData;
        TimestampTranslation tsTranslation2;
        TimestampTranslation tsTranslation3 = new TimestampTranslation();
        Logger logger = LOG;
        logger.info("" + getTitle() + ": number of samples:" + samples.size());
        if (samples.size() > 1) {
            boolean annotate = true;
            int last_type = 0;
            int numEntries = samples.size();
            List<Entry> activityEntries = new ArrayList<>(numEntries);
            List<Entry> deepSleepEntries = new ArrayList<>(numEntries);
            List<Entry> lightSleepEntries = new ArrayList<>(numEntries);
            List<Entry> notWornEntries = new ArrayList<>(numEntries);
            boolean hr = supportsHeartrate(gbDevice);
            List<Entry> heartrateEntries = hr ? new ArrayList<>(numEntries) : null;
            List<Integer> colors = new ArrayList<>(numEntries);
            int lastHrSampleIndex = -1;
            HeartRateUtils heartRateUtilsInstance = HeartRateUtils.getInstance();
            int i = 0;
            while (i < numEntries) {
                ActivitySample sample = (ActivitySample) samples.get(i);
                boolean annotate2 = annotate;
                int type = sample.getKind();
                int numEntries2 = numEntries;
                int ts = tsTranslation3.shorten(sample.getTimestamp());
                float value = sample.getIntensity();
                List<Integer> colors2 = colors;
                if (type == 2) {
                    tsTranslation2 = tsTranslation3;
                    float value2 = value;
                    if (last_type != type) {
                        lightSleepEntries.add(createLineEntry(0.0f, ts - 1));
                        deepSleepEntries.add(createLineEntry(0.0f, ts));
                        notWornEntries.add(createLineEntry(0.0f, ts));
                        activityEntries.add(createLineEntry(0.0f, ts));
                    }
                    lightSleepEntries.add(createLineEntry(value2, ts));
                } else if (type == 4) {
                    tsTranslation2 = tsTranslation3;
                    float value3 = value;
                    if (last_type != type) {
                        deepSleepEntries.add(createLineEntry(0.0f, ts - 1));
                        lightSleepEntries.add(createLineEntry(0.0f, ts));
                        notWornEntries.add(createLineEntry(0.0f, ts));
                        activityEntries.add(createLineEntry(0.0f, ts));
                    }
                    deepSleepEntries.add(createLineEntry(0.01f + value3, ts));
                } else if (type != 8) {
                    if (last_type != type) {
                        activityEntries.add(createLineEntry(0.0f, ts - 1));
                        lightSleepEntries.add(createLineEntry(0.0f, ts));
                        notWornEntries.add(createLineEntry(0.0f, ts));
                        deepSleepEntries.add(createLineEntry(0.0f, ts));
                    }
                    activityEntries.add(createLineEntry(value, ts));
                    tsTranslation2 = tsTranslation3;
                } else {
                    if (last_type != type) {
                        tsTranslation2 = tsTranslation3;
                        notWornEntries.add(createLineEntry(0.0f, ts - 1));
                        lightSleepEntries.add(createLineEntry(0.0f, ts));
                        deepSleepEntries.add(createLineEntry(0.0f, ts));
                        activityEntries.add(createLineEntry(0.0f, ts));
                    } else {
                        tsTranslation2 = tsTranslation3;
                    }
                    notWornEntries.add(createLineEntry(0.01f, ts));
                }
                if (hr && sample.getKind() != 8 && heartRateUtilsInstance.isValidHeartRateValue(sample.getHeartRate())) {
                    if (lastHrSampleIndex > -1 && ts - lastHrSampleIndex > 18000) {
                        heartrateEntries.add(createLineEntry(0.0f, lastHrSampleIndex + 1));
                        heartrateEntries.add(createLineEntry(0.0f, ts - 1));
                    }
                    heartrateEntries.add(createLineEntry((float) sample.getHeartRate(), ts));
                    lastHrSampleIndex = ts;
                }
                last_type = type;
                i++;
                annotate = annotate2;
                numEntries = numEntries2;
                colors = colors2;
                tsTranslation3 = tsTranslation2;
            }
            tsTranslation = tsTranslation3;
            boolean z = annotate;
            int i2 = numEntries;
            List<Integer> list = colors;
            List<ILineDataSet> lineDataSets = new ArrayList<>();
            lineDataSets.add(createDataSet(activityEntries, this.akActivity.color, "Activity"));
            lineDataSets.add(createDataSet(deepSleepEntries, this.akDeepSleep.color, "Deep Sleep"));
            lineDataSets.add(createDataSet(lightSleepEntries, this.akLightSleep.color, "Light Sleep"));
            lineDataSets.add(createDataSet(notWornEntries, this.akNotWorn.color, "Not worn"));
            if (hr && heartrateEntries.size() > 0) {
                lineDataSets.add(createHeartrateSet(heartrateEntries, "Heart Rate"));
            }
            lineData = new LineData(lineDataSets);
        } else {
            tsTranslation = tsTranslation3;
            lineData = new LineData();
        }
        return new DefaultChartsData<>(lineData, new SampleXLabelFormatter(tsTranslation));
    }

    /* access modifiers changed from: protected */
    public Entry createLineEntry(float value, int xValue) {
        return new Entry((float) xValue, value);
    }

    /* access modifiers changed from: protected */
    public LineDataSet createDataSet(List<Entry> values, Integer color, String label) {
        LineDataSet set1 = new LineDataSet(values, label);
        set1.setColor(color.intValue());
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setFillColor(color.intValue());
        set1.setFillAlpha(255);
        set1.setDrawValues(false);
        set1.setValueTextColor(this.CHART_TEXT_COLOR);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set1;
    }

    /* access modifiers changed from: protected */
    public LineDataSet createHeartrateSet(List<Entry> values, String label) {
        LineDataSet set1 = new LineDataSet(values, label);
        set1.setLineWidth(2.2f);
        set1.setColor(this.HEARTRATE_COLOR);
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set1.setCubicIntensity(0.1f);
        set1.setDrawCircles(false);
        set1.setDrawValues(true);
        set1.setValueTextColor(this.CHART_TEXT_COLOR);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return set1;
    }

    /* access modifiers changed from: protected */
    public RefreshTask createRefreshTask(String task, Context context) {
        return new RefreshTask(task, context);
    }

    public class RefreshTask extends DBAccess {
        private ChartsData chartsData;

        public RefreshTask(String task, Context context) {
            super(task, context);
        }

        /* access modifiers changed from: protected */
        public void doInBackground(DBHandler db) {
            ChartsHost chartsHost = AbstractChartFragment.this.getChartsHost();
            if (chartsHost != null) {
                this.chartsData = AbstractChartFragment.this.refreshInBackground(chartsHost, db, chartsHost.getDevice());
            } else {
                cancel(true);
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Object o) {
            super.onPostExecute(o);
            FragmentActivity activity = AbstractChartFragment.this.getActivity();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                AbstractChartFragment.LOG.info("Not rendering charts because activity is not available anymore");
                return;
            }
            AbstractChartFragment.this.updateChartsnUIThread(this.chartsData);
            AbstractChartFragment.this.renderCharts();
        }
    }

    public boolean setDateRange(Date from, Date to) {
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException("Bad date range: " + from + ".." + to);
        } else if (to.after(new Date())) {
            return false;
        } else {
            setStartDate(from);
            setEndDate(to);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void updateDateInfo(Date from, Date to) {
        if (from.equals(to)) {
            getChartsHost().setDateInfo(DateTimeUtils.formatDate(from));
        } else {
            getChartsHost().setDateInfo(DateTimeUtils.formatDateRange(from, to));
        }
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSamples(DBHandler db, GBDevice device) {
        int tsStart = getTSStart();
        int tsEnd = getTSEnd();
        List<? extends ActivitySample> samples = getSamples(db, device, tsStart, tsEnd);
        ensureStartAndEndSamples(samples, tsStart, tsEnd);
        return samples;
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSamplesofSleep(DBHandler db, GBDevice device) {
        int tsStart = getTSStart();
        Calendar day = GregorianCalendar.getInstance();
        day.setTimeInMillis(((long) tsStart) * 1000);
        day.set(11, 12);
        day.set(12, 0);
        day.set(13, 0);
        int tsStart2 = toTimestamp(day.getTime());
        day.setTimeInMillis(((long) getTSEnd()) * 1000);
        day.set(11, 12);
        day.set(12, 0);
        day.set(13, 0);
        int tsEnd = toTimestamp(day.getTime());
        List<? extends ActivitySample> samples = getSamples(db, device, tsStart2, tsEnd);
        ensureStartAndEndSamples(samples, tsStart2, tsEnd);
        return samples;
    }

    /* access modifiers changed from: protected */
    public void ensureStartAndEndSamples(List<ActivitySample> samples, int tsStart, int tsEnd) {
        if (samples != null && !samples.isEmpty()) {
            ActivitySample lastSample = samples.get(samples.size() - 1);
            if (lastSample.getTimestamp() < tsEnd) {
                samples.add(createTrailingActivitySample(lastSample, tsEnd));
            }
            ActivitySample firstSample = samples.get(0);
            if (firstSample.getTimestamp() > tsStart) {
                samples.add(createTrailingActivitySample(firstSample, tsStart));
            }
        }
    }

    private ActivitySample createTrailingActivitySample(ActivitySample referenceSample, int timestamp) {
        TrailingActivitySample sample = new TrailingActivitySample();
        if (referenceSample instanceof AbstractActivitySample) {
            AbstractActivitySample reference = (AbstractActivitySample) referenceSample;
            sample.setUserId(reference.getUserId());
            sample.setDeviceId(reference.getDeviceId());
            sample.setProvider(reference.getProvider());
        }
        sample.setTimestamp(timestamp);
        return sample;
    }

    private int getTSEnd() {
        return toTimestamp(getEndDate());
    }

    private int getTSStart() {
        return toTimestamp(getStartDate());
    }

    private int toTimestamp(Date date) {
        return (int) (date.getTime() / 1000);
    }

    public static class DefaultChartsData<T extends ChartData<?>> extends ChartsData {
        private final T data;
        private ValueFormatter xValueFormatter;

        public DefaultChartsData(T data2, ValueFormatter xValueFormatter2) {
            this.xValueFormatter = xValueFormatter2;
            this.data = data2;
        }

        public ValueFormatter getXValueFormatter() {
            return this.xValueFormatter;
        }

        public T getData() {
            return this.data;
        }
    }

    protected static class SampleXLabelFormatter extends ValueFormatter {
        SimpleDateFormat annotationDateFormat = new SimpleDateFormat("HH:mm");
        Calendar cal = GregorianCalendar.getInstance();
        private final TimestampTranslation tsTranslation;

        public SampleXLabelFormatter(TimestampTranslation tsTranslation2) {
            this.tsTranslation = tsTranslation2;
        }

        public String getFormattedValue(float value) {
            this.cal.clear();
            this.cal.setTimeInMillis(((long) this.tsTranslation.toOriginalValue((int) value)) * 1000);
            return this.annotationDateFormat.format(this.cal.getTime());
        }
    }

    protected static class PreformattedXIndexLabelFormatter extends ValueFormatter {
        private ArrayList<String> xLabels;

        public PreformattedXIndexLabelFormatter(ArrayList<String> xLabels2) {
            this.xLabels = xLabels2;
        }

        public String getFormattedValue(float value) {
            int index = (int) value;
            ArrayList<String> arrayList = this.xLabels;
            if (arrayList == null || index >= arrayList.size()) {
                return String.valueOf(value);
            }
            return this.xLabels.get(index);
        }
    }

    protected static class TimestampTranslation {
        private int tsOffset = -1;

        protected TimestampTranslation() {
        }

        public int shorten(int timestamp) {
            int i = this.tsOffset;
            if (i != -1) {
                return timestamp - i;
            }
            this.tsOffset = timestamp;
            return 0;
        }

        public int toOriginalValue(int timestamp) {
            int i = this.tsOffset;
            if (i == -1) {
                return timestamp;
            }
            return i + timestamp;
        }
    }
}
