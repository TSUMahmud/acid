package cyanogenmod.alarmclock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import java.util.Iterator;

public class CyanogenModAlarmClock {
    public static final String ACTION_SET_ALARM_ENABLED = "cyanogenmod.alarmclock.SET_ALARM_ENABLED";
    private static final String DESKCLOCK_PACKAGE = "com.android.deskclock";
    public static final String EXTRA_ALARM_ID = "cyanogenmod.intent.extra.alarmclock.ID";
    public static final String EXTRA_ENABLED = "cyanogenmod.intent.extra.alarmclock.ENABLED";
    public static final String MODIFY_ALARMS_PERMISSION = "cyanogenmod.alarmclock.permission.MODIFY_ALARMS";
    public static final String READ_ALARMS_PERMISSION = "cyanogenmod.alarmclock.permission.READ_ALARMS";
    public static final String WRITE_ALARMS_PERMISSION = "cyanogenmod.alarmclock.permission.WRITE_ALARMS";

    public static Intent createAlarmIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SET_ALARM");
        ComponentName selectedSystemComponent = null;
        Iterator i$ = context.getPackageManager().queryIntentActivities(intent, 0).iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            ResolveInfo info = i$.next();
            if ((info.activityInfo.applicationInfo.flags & 1) != 0) {
                selectedSystemComponent = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                break;
            }
        }
        if (selectedSystemComponent != null) {
            intent.setComponent(selectedSystemComponent);
        }
        return intent;
    }
}
