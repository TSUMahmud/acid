package nodomain.freeyourgadget.gadgetbridge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.activities.ConfigureAlarms;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class SleepAlarmWidget extends AppWidgetProvider {
    public static final String ACTION = "nodomain.freeyourgadget.gadgetbridge.SLEEP_ALARM_WIDGET_CLICK";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), C0889R.layout.sleep_alarm_widget);
        Intent intent = new Intent(context, SleepAlarmWidget.class);
        intent.setAction(ACTION);
        views.setOnClickPendingIntent(C0889R.C0891id.sleepalarmwidget_text, PendingIntent.getBroadcast(context, 0, intent, 134217728));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public void onEnabled(Context context) {
    }

    public void onDisabled(Context context) {
    }

    public void onReceive(Context context, Intent intent) {
        GBDevice selectedDevice;
        super.onReceive(context, intent);
        if (ACTION.equals(intent.getAction())) {
            int userSleepDuration = new ActivityUser().getSleepDuration();
            GregorianCalendar calendar = new GregorianCalendar();
            if (userSleepDuration > 0) {
                calendar.add(11, userSleepDuration);
            } else {
                calendar.add(12, 1);
            }
            Context appContext = context.getApplicationContext();
            if (!(appContext instanceof GBApplication) || ((selectedDevice = ((GBApplication) appContext).getDeviceManager().getSelectedDevice()) != null && selectedDevice.isInitialized())) {
                C1238GB.toast(context, context.getString(C0889R.string.appwidget_setting_alarm, new Object[]{Integer.valueOf(calendar.get(11)), Integer.valueOf(calendar.get(12))}), 0, 1);
                Alarm alarm = AlarmUtils.createSingleShot(0, true, false, calendar);
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(alarm);
                GBApplication.deviceService().onSetAlarms(arrayList);
                return;
            }
            C1238GB.toast(context, context.getString(C0889R.string.appwidget_not_connected), 1, 2);
        }
    }

    private void setAlarmViaAlarmManager(Context packageContext, long triggerTime) {
        PendingIntent pi = PendingIntent.getBroadcast(packageContext, 0, new Intent(packageContext, ConfigureAlarms.class), 268435456);
        ((AlarmManager) packageContext.getSystemService("alarm")).setAlarmClock(new AlarmManager.AlarmClockInfo(triggerTime, pi), pi);
    }
}
