package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;

public class AlarmClockReceiver extends BroadcastReceiver {
    public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";
    public static final String ALARM_DISMISS_ACTION = "com.android.deskclock.ALARM_DISMISS";
    public static final String ALARM_DONE_ACTION = "com.android.deskclock.ALARM_DONE";
    public static final String ALARM_SNOOZE_ACTION = "com.android.deskclock.ALARM_SNOOZE";
    public static final String GOOGLE_CLOCK_ALARM_ALERT_ACTION = "com.google.android.deskclock.action.ALARM_ALERT";
    public static final String GOOGLE_CLOCK_ALARM_DONE_ACTION = "com.google.android.deskclock.action.ALARM_DONE";
    private int lastId;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ALARM_ALERT_ACTION.equals(action) || GOOGLE_CLOCK_ALARM_ALERT_ACTION.equals(action)) {
            sendAlarm(true);
        } else if (ALARM_DONE_ACTION.equals(action) || GOOGLE_CLOCK_ALARM_DONE_ACTION.equals(action)) {
            sendAlarm(false);
        }
    }

    private synchronized void sendAlarm(boolean on) {
        dismissLastAlarm();
        if (on) {
            NotificationSpec notificationSpec = new NotificationSpec();
            this.lastId = notificationSpec.getId();
            notificationSpec.type = NotificationType.GENERIC_ALARM_CLOCK;
            notificationSpec.sourceName = "ALARMCLOCKRECEIVER";
            notificationSpec.attachedActions = new ArrayList<>();
            NotificationSpec.Action dismissAllAction = new NotificationSpec.Action();
            dismissAllAction.title = "Dismiss All";
            dismissAllAction.type = 4;
            notificationSpec.attachedActions.add(dismissAllAction);
            GBApplication.deviceService().onNotification(notificationSpec);
        }
    }

    private void dismissLastAlarm() {
        if (this.lastId != 0) {
            GBApplication.deviceService().onDeleteNotification(this.lastId);
            this.lastId = 0;
        }
    }
}
