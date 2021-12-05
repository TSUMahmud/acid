package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import androidx.preference.DialogPreference;

public class XTimePreference extends DialogPreference {
    protected int hour = 0;
    protected int minute = 0;

    public XTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    /* access modifiers changed from: package-private */
    public void updateSummary() {
        if (DateFormat.is24HourFormat(getContext())) {
            setSummary((CharSequence) getTime24h());
        } else {
            setSummary((CharSequence) getTime12h());
        }
    }

    /* access modifiers changed from: package-private */
    public String getTime24h() {
        return String.format("%02d", new Object[]{Integer.valueOf(this.hour)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute)});
    }

    private String getTime12h() {
        String suffix = this.hour < 12 ? " AM" : " PM";
        int h = this.hour;
        if (h > 12) {
            h -= 12;
        }
        return h + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute)}) + suffix;
    }

    /* access modifiers changed from: package-private */
    public void persistStringValue(String value) {
        persistString(value);
    }
}
