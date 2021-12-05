package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
    private int hour = 0;
    private int minute = 0;
    private TimePicker picker = null;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public View onCreateDialogView() {
        this.picker = new TimePicker(getContext());
        this.picker.setIs24HourView(Boolean.valueOf(DateFormat.is24HourFormat(getContext())));
        this.picker.setPadding(0, 50, 0, 50);
        return this.picker;
    }

    /* access modifiers changed from: protected */
    public void onBindDialogView(View v) {
        super.onBindDialogView(v);
        this.picker.setCurrentHour(Integer.valueOf(this.hour));
        this.picker.setCurrentMinute(Integer.valueOf(this.minute));
    }

    /* access modifiers changed from: protected */
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            this.hour = this.picker.getCurrentHour().intValue();
            this.minute = this.picker.getCurrentMinute().intValue();
            String time = getTime24h();
            if (callChangeListener(time)) {
                persistString(time);
                updateSummary();
            }
        }
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;
        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else if (defaultValue != null) {
            time = defaultValue.toString();
        } else {
            time = "00:00";
        }
        String[] pieces = time.split(":");
        this.hour = Integer.parseInt(pieces[0]);
        this.minute = Integer.parseInt(pieces[1]);
        updateSummary();
    }

    public void updateSummary() {
        if (DateFormat.is24HourFormat(getContext())) {
            setSummary(getTime24h());
        } else {
            setSummary(getTime12h());
        }
    }

    public String getTime24h() {
        return String.format("%02d", new Object[]{Integer.valueOf(this.hour)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute)});
    }

    public String getTime12h() {
        String suffix = this.hour < 12 ? " AM" : " PM";
        int i = this.hour;
        if (i > 12) {
            i -= 12;
        }
        int h = i;
        return String.valueOf(h) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute)}) + suffix;
    }
}
