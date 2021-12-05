package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class XTimePreferenceFragment extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment {
    private TimePicker picker = null;

    /* access modifiers changed from: protected */
    public View onCreateDialogView(Context context) {
        this.picker = new TimePicker(context);
        this.picker.setIs24HourView(Boolean.valueOf(DateFormat.is24HourFormat(getContext())));
        this.picker.setPadding(0, 50, 0, 50);
        return this.picker;
    }

    /* access modifiers changed from: protected */
    public void onBindDialogView(View v) {
        super.onBindDialogView(v);
        XTimePreference pref = (XTimePreference) getPreference();
        this.picker.setCurrentHour(Integer.valueOf(pref.hour));
        this.picker.setCurrentMinute(Integer.valueOf(pref.minute));
    }

    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            XTimePreference pref = (XTimePreference) getPreference();
            pref.hour = this.picker.getCurrentHour().intValue();
            pref.minute = this.picker.getCurrentMinute().intValue();
            String time = pref.getTime24h();
            if (pref.callChangeListener(time)) {
                pref.persistStringValue(time);
                pref.updateSummary();
            }
        }
    }

    public Preference findPreference(CharSequence key) {
        return getPreference();
    }
}
