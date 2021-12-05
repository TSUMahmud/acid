package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.HashSet;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

public class MiBandPreferencesActivity extends AbstractSettingsActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(C0889R.C0894xml.miband_preferences);
        addTryListeners();
        Prefs prefs = GBApplication.getPrefs();
        findPreference(MiBandConst.PREF_MIBAND_USE_HR_FOR_SLEEP_DETECTION).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                GBApplication.deviceService().onEnableHeartRateSleepSupport(Boolean.TRUE.equals(newVal));
                return true;
            }
        });
        findPreference(ZeTimeConstants.PREF_ZETIME_HEARTRATE_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                GBApplication.deviceService().onSetHeartRateMeasurementInterval(Integer.parseInt((String) newVal));
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_GOAL_NOTIFICATION).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_GOAL_NOTIFICATION);
                    }
                });
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS);
                    }
                });
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_THRESHOLD).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_THRESHOLD);
                    }
                });
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_START).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_START);
                    }
                });
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_END).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_END);
                    }
                });
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND);
                    }
                });
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND_START).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND_START);
                    }
                });
                return true;
            }
        });
        findPreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND_END).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND_END);
                    }
                });
                return true;
            }
        });
        findPreference("mi_fitness_goal").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                MiBandPreferencesActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration("mi_fitness_goal");
                    }
                });
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    public void invokeLater(Runnable runnable) {
        getListView().post(runnable);
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        findPreference(MiBandConst.PREF_MIBAND_ADDRESS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                LocalBroadcastManager.getInstance(MiBandPreferencesActivity.this.getApplicationContext()).sendBroadcast(new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST));
                preference.setSummary(newVal.toString());
                return true;
            }
        });
    }

    private void addTryListeners() {
        for (final NotificationType type : NotificationType.values()) {
            String prefKey = "mi_try_" + type.getGenericType();
            Preference tryPref = findPreference(prefKey);
            if (tryPref != null) {
                tryPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        MiBandPreferencesActivity.this.tryVibration(type);
                        return true;
                    }
                });
            } else {
                C1238GB.toast(getBaseContext(), "Unable to find preference key: " + prefKey + ", trying the vibration won't work", 1, 2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void tryVibration(NotificationType type) {
        NotificationSpec spec = new NotificationSpec();
        spec.type = type;
        GBApplication.deviceService().onNotification(spec);
    }

    /* access modifiers changed from: protected */
    public String[] getPreferenceKeysWithSummary() {
        Set<String> prefKeys = new HashSet<>();
        prefKeys.add("mi_user_alias");
        prefKeys.add(MiBandConst.PREF_MIBAND_ADDRESS);
        prefKeys.add("mi_fitness_goal");
        prefKeys.add(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_THRESHOLD);
        prefKeys.add(MiBandConst.getNotificationPrefKey(MiBandConst.VIBRATION_COUNT, MiBandConst.ORIGIN_ALARM_CLOCK));
        prefKeys.add(MiBandConst.getNotificationPrefKey(MiBandConst.VIBRATION_COUNT, MiBandConst.ORIGIN_INCOMING_CALL));
        for (NotificationType type : NotificationType.values()) {
            prefKeys.add(MiBandConst.getNotificationPrefKey(MiBandConst.VIBRATION_COUNT, type.getGenericType()));
        }
        return (String[]) prefKeys.toArray(new String[0]);
    }
}
