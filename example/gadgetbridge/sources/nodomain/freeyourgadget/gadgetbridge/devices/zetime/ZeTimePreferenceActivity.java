package nodomain.freeyourgadget.gadgetbridge.devices.zetime;

import android.os.Bundle;
import android.preference.Preference;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity;

public class ZeTimePreferenceActivity extends AbstractSettingsActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(C0889R.C0894xml.zetime_preferences);
        GBApplication.deviceService().onReadConfiguration("do_it");
        findPreference(ZeTimeConstants.PREF_ZETIME_HEARTRATE_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                GBApplication.deviceService().onSetHeartRateMeasurementInterval(Integer.parseInt((String) newVal));
                return true;
            }
        });
        addPreferenceHandlerFor(ZeTimeConstants.PREF_SCREENTIME);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_ANALOG_MODE);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_ACTIVITY_TRACKING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_HANDMOVE_DISPLAY);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_DO_NOT_DISTURB);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_DO_NOT_DISTURB_START);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_DO_NOT_DISTURB_END);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_CALORIES_TYPE);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_DATE_FORMAT);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_ENABLE);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_START);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_END);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_THRESHOLD);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_MO);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_TU);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_WE);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_TH);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_FR);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_SA);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_SU);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_SMS_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_ANTI_LOSS_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_CALENDAR_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_CALL_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_MISSED_CALL_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_EMAIL_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_INACTIVITY_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_LOW_POWER_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_SOCIAL_SIGNALING);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_ZETIME_HEARTRATE_ALARM);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_ZETIME_MAX_HEARTRATE);
        addPreferenceHandlerFor(ZeTimeConstants.PREF_ZETIME_MIN_HEARTRATE);
        addPreferenceHandlerFor("mi_fitness_goal");
        addPreferenceHandlerFor("activity_user_sleep_duration");
        addPreferenceHandlerFor("activity_user_calories_burnt");
        addPreferenceHandlerFor("activity_user_distance_meters");
        addPreferenceHandlerFor("activity_user_activetime_minutes");
    }

    private void addPreferenceHandlerFor(final String preferenceKey) {
        findPreference(preferenceKey).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                GBApplication.deviceService().onSendConfiguration(preferenceKey);
                return true;
            }
        });
    }
}
