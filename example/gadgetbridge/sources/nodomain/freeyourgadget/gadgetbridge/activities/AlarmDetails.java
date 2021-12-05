package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TimePicker;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.entities.Alarm;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;

public class AlarmDetails extends AbstractGBActivity {
    private Alarm alarm;
    private CheckedTextView cbFriday;
    private CheckedTextView cbMonday;
    private CheckedTextView cbSaturday;
    private CheckedTextView cbSmartWakeup;
    private CheckedTextView cbSnooze;
    private CheckedTextView cbSunday;
    private CheckedTextView cbThursday;
    private CheckedTextView cbTuesday;
    private CheckedTextView cbWednesday;
    private GBDevice device;
    private TimePicker timePicker;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_alarm_details);
        this.alarm = (Alarm) getIntent().getSerializableExtra("alarm");
        this.device = (GBDevice) getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);
        this.timePicker = (TimePicker) findViewById(C0889R.C0891id.alarm_time_picker);
        this.cbSmartWakeup = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_smart_wakeup);
        this.cbSnooze = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_snooze);
        this.cbMonday = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_monday);
        this.cbTuesday = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_tuesday);
        this.cbWednesday = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_wednesday);
        this.cbThursday = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_thursday);
        this.cbFriday = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_friday);
        this.cbSaturday = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_saturday);
        this.cbSunday = (CheckedTextView) findViewById(C0889R.C0891id.alarm_cb_sunday);
        this.cbSmartWakeup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbSnooze.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbMonday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbTuesday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbWednesday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbThursday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbFriday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbSaturday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.cbSunday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        this.timePicker.setIs24HourView(Boolean.valueOf(DateFormat.is24HourFormat(GBApplication.getContext())));
        this.timePicker.setCurrentHour(Integer.valueOf(this.alarm.getHour()));
        this.timePicker.setCurrentMinute(Integer.valueOf(this.alarm.getMinute()));
        this.cbSmartWakeup.setChecked(this.alarm.getSmartWakeup());
        int snoozeVisibility = 0;
        this.cbSmartWakeup.setVisibility(supportsSmartWakeup() ? 0 : 8);
        this.cbSnooze.setChecked(this.alarm.getSnooze());
        if (!supportsSnoozing()) {
            snoozeVisibility = 8;
        }
        this.cbSnooze.setVisibility(snoozeVisibility);
        this.cbMonday.setChecked(this.alarm.getRepetition(1));
        this.cbTuesday.setChecked(this.alarm.getRepetition(2));
        this.cbWednesday.setChecked(this.alarm.getRepetition(4));
        this.cbThursday.setChecked(this.alarm.getRepetition(8));
        this.cbFriday.setChecked(this.alarm.getRepetition(16));
        this.cbSaturday.setChecked(this.alarm.getRepetition(32));
        this.cbSunday.setChecked(this.alarm.getRepetition(64));
    }

    private boolean supportsSmartWakeup() {
        if (this.device != null) {
            return DeviceHelper.getInstance().getCoordinator(this.device).supportsSmartWakeup(this.device);
        }
        return false;
    }

    private boolean supportsSnoozing() {
        if (this.device != null) {
            return DeviceHelper.getInstance().getCoordinator(this.device).supportsAlarmSnoozing();
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    private void updateAlarm() {
        boolean z = true;
        this.alarm.setSmartWakeup(supportsSmartWakeup() && this.cbSmartWakeup.isChecked());
        Alarm alarm2 = this.alarm;
        if (!supportsSnoozing() || !this.cbSnooze.isChecked()) {
            z = false;
        }
        alarm2.setSnooze(z);
        this.alarm.setRepetition(AlarmUtils.createRepetitionMassk(this.cbMonday.isChecked(), this.cbTuesday.isChecked(), this.cbWednesday.isChecked(), this.cbThursday.isChecked(), this.cbFriday.isChecked(), this.cbSaturday.isChecked(), this.cbSunday.isChecked()));
        this.alarm.setHour(this.timePicker.getCurrentHour().intValue());
        this.alarm.setMinute(this.timePicker.getCurrentMinute().intValue());
        DBHelper.store(this.alarm);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        updateAlarm();
        super.onPause();
    }
}
