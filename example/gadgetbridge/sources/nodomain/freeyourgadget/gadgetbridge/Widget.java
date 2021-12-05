package nodomain.freeyourgadget.gadgetbridge;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.activities.WidgetAlarmsActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.ChartsActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DailyTotals;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Widget extends AppWidgetProvider {
    public static final String APPWIDGET_DELETED = "nodomain.freeyourgadget.gadgetbridge.APPWIDGET_DELETED";
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) Widget.class);
    public static final String WIDGET_CLICK = "nodomain.freeyourgadget.gadgetbridge.WidgetClick";
    static BroadcastReceiver broadcastReceiver = null;

    private GBDevice getSelectedDevice() {
        Context context = GBApplication.getContext();
        if (!(context instanceof GBApplication)) {
            return null;
        }
        return ((GBApplication) context).getDeviceManager().getSelectedDevice();
    }

    private int[] getSteps() {
        Context context = GBApplication.getContext();
        Calendar day = GregorianCalendar.getInstance();
        if (!(context instanceof GBApplication)) {
            return new int[]{0, 0, 0};
        }
        return new DailyTotals().getDailyTotalsForAllDevices(day);
    }

    private String getHM(long value) {
        return DateTimeUtils.formatDurationHoursMinutes(value, TimeUnit.MINUTES);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Context context2 = context;
        GBDevice device = getSelectedDevice();
        RemoteViews views = new RemoteViews(context.getPackageName(), C0889R.layout.widget);
        Intent intent = new Intent(context2, Widget.class);
        intent.setAction(WIDGET_CLICK);
        views.setOnClickPendingIntent(C0889R.C0891id.todaywidget_header_bar, PendingIntent.getBroadcast(context2, 0, intent, 134217728));
        views.setOnClickPendingIntent(C0889R.C0891id.todaywidget_header_icon, PendingIntent.getActivity(context2, 0, new Intent(context2, ControlCenterv2.class), 0));
        views.setOnClickPendingIntent(C0889R.C0891id.todaywidget_header_plus, PendingIntent.getActivity(context2, 0, new Intent(context2, WidgetAlarmsActivity.class), 0));
        if (device != null) {
            Intent startChartsIntent = new Intent(context2, ChartsActivity.class);
            startChartsIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
            views.setOnClickPendingIntent(C0889R.C0891id.todaywidget_bottom_layout, PendingIntent.getActivity(context2, 0, startChartsIntent, 0));
        }
        int[] DailyTotals = getSteps();
        views.setTextViewText(C0889R.C0891id.todaywidget_steps, context2.getString(C0889R.string.widget_steps_label, new Object[]{Integer.valueOf(DailyTotals[0])}));
        views.setTextViewText(C0889R.C0891id.todaywidget_sleep, context2.getString(C0889R.string.widget_sleep_label, new Object[]{getHM((long) DailyTotals[1])}));
        if (device != null) {
            String status = String.format("%1s", new Object[]{device.getStateString()});
            if (device.isConnected() && device.getBatteryLevel() > 1) {
                status = String.format("Battery %1s%%", new Object[]{Short.valueOf(device.getBatteryLevel())});
            }
            views.setTextViewText(C0889R.C0891id.todaywidget_device_status, status);
            views.setTextViewText(C0889R.C0891id.todaywidget_device_name, device.getName());
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void refreshData() {
        Context context = GBApplication.getContext();
        GBDevice device = getSelectedDevice();
        if (device == null || !device.isInitialized()) {
            C1238GB.toast(context, context.getString(C0889R.string.device_not_connected), 0, 3);
            GBApplication.deviceService().connect();
            C1238GB.toast(context, context.getString(C0889R.string.connecting), 0, 1);
            return;
        }
        C1238GB.toast(context, context.getString(C0889R.string.busy_task_fetch_activity_data), 0, 1);
        GBApplication.deviceService().onFetchRecordedData(RecordedDataTypes.TYPE_ACTIVITY);
    }

    public void updateWidget() {
        Context context = GBApplication.getContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), Widget.class.getName())));
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public void onEnabled(Context context) {
        if (broadcastReceiver == null) {
            LOG.debug("gbwidget BROADCAST receiver initialized.");
            broadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Logger access$000 = Widget.LOG;
                    access$000.debug("gbwidget BROADCAST, action" + intent.getAction());
                    Widget.this.updateWidget();
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GBApplication.ACTION_NEW_DATA);
            intentFilter.addAction(GBDevice.ACTION_DEVICE_CHANGED);
            LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    public void onDisabled(Context context) {
        BroadcastReceiver broadcastReceiver2 = broadcastReceiver;
        if (broadcastReceiver2 != null) {
            AndroidUtils.safeUnregisterBroadcastReceiver(context, broadcastReceiver2);
            broadcastReceiver = null;
        }
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Logger logger = LOG;
        logger.debug("gbwidget LOCAL onReceive, action: " + intent.getAction());
        if (WIDGET_CLICK.equals(intent.getAction())) {
            if (broadcastReceiver == null) {
                onEnabled(context);
            }
            refreshData();
        } else if (APPWIDGET_DELETED.equals(intent.getAction())) {
            onDisabled(context);
        }
    }
}
