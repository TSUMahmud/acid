package nodomain.freeyourgadget.gadgetbridge.activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class WidgetAlarmsActivity extends Activity implements View.OnClickListener {
    TextView textView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context appContext = getApplicationContext();
        if (appContext instanceof GBApplication) {
            GBDevice selectedDevice = ((GBApplication) appContext).getDeviceManager().getSelectedDevice();
            if (selectedDevice == null || !selectedDevice.isInitialized()) {
                C1238GB.toast((Context) this, getString(C0889R.string.not_connected), 1, 2);
                return;
            }
            setContentView(C0889R.layout.widget_alarms_activity_list);
            int userSleepDuration = new ActivityUser().getSleepDuration();
            this.textView = (TextView) findViewById(C0889R.C0891id.alarm5);
            if (userSleepDuration > 0) {
                Resources res = getResources();
                this.textView.setText(String.format(res.getQuantityString(C0889R.plurals.widget_alarm_target_hours, userSleepDuration, new Object[]{Integer.valueOf(userSleepDuration)}), new Object[0]));
                return;
            }
            this.textView.setVisibility(8);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0889R.C0891id.alarm1:
                setAlarm(5);
                break;
            case C0889R.C0891id.alarm2:
                setAlarm(10);
                break;
            case C0889R.C0891id.alarm3:
                setAlarm(20);
                break;
            case C0889R.C0891id.alarm4:
                setAlarm(60);
                break;
            case C0889R.C0891id.alarm5:
                setAlarm(0);
                break;
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                WidgetAlarmsActivity.this.finish();
            }
        }, 150);
    }

    public void setAlarm(int duration) {
        GBDevice selectedDevice;
        GregorianCalendar calendar = new GregorianCalendar();
        if (duration > 0) {
            calendar.add(12, duration);
        } else {
            int userSleepDuration = new ActivityUser().getSleepDuration();
            if (userSleepDuration > 0) {
                calendar.add(11, userSleepDuration);
            } else {
                calendar.add(12, 1);
            }
        }
        Context appContext = getApplicationContext();
        if (!(appContext instanceof GBApplication) || ((selectedDevice = ((GBApplication) appContext).getDeviceManager().getSelectedDevice()) != null && selectedDevice.isInitialized())) {
            C1238GB.toast((Context) this, getString(C0889R.string.appwidget_setting_alarm, new Object[]{Integer.valueOf(calendar.get(11)), Integer.valueOf(calendar.get(12))}), 0, 1);
            Alarm alarm = AlarmUtils.createSingleShot(0, true, false, calendar);
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(alarm);
            GBApplication.deviceService().onSetAlarms(arrayList);
            return;
        }
        C1238GB.toast((Context) this, getString(C0889R.string.appwidget_not_connected), 1, 2);
    }
}
