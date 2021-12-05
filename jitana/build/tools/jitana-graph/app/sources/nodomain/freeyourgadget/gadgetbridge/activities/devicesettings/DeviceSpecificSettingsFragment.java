package nodomain.freeyourgadget.gadgetbridge.activities.devicesettings;

import android.os.Bundle;
import android.widget.EditText;
import androidx.core.view.InputDeviceCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import nodomain.freeyourgadget.gadgetbridge.util.XTimePreference;
import nodomain.freeyourgadget.gadgetbridge.util.XTimePreferenceFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceSpecificSettingsFragment extends PreferenceFragmentCompat {
    static final String FRAGMENT_TAG = "DEVICE_SPECIFIC_SETTINGS_FRAGMENT";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DeviceSpecificSettingsFragment.class);

    private void setSettingsFileSuffix(String settingsFileSuffix, int[] supportedSettings) {
        Bundle args = new Bundle();
        args.putString("settingsFileSuffix", settingsFileSuffix);
        args.putIntArray("supportedSettings", supportedSettings);
        setArguments(args);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String settingsFileSuffix = arguments.getString("settingsFileSuffix", (String) null);
            int[] supportedSettings = arguments.getIntArray("supportedSettings");
            if (settingsFileSuffix != null && supportedSettings != null) {
                PreferenceManager preferenceManager = getPreferenceManager();
                preferenceManager.setSharedPreferencesName("devicesettings_" + settingsFileSuffix);
                int i = 0;
                if (rootKey == null) {
                    boolean first = true;
                    int length = supportedSettings.length;
                    while (i < length) {
                        int setting = supportedSettings[i];
                        if (first) {
                            setPreferencesFromResource(setting, (String) null);
                            first = false;
                        } else {
                            addPreferencesFromResource(setting);
                        }
                        i++;
                    }
                } else {
                    int length2 = supportedSettings.length;
                    while (i < length2) {
                        try {
                            setPreferencesFromResource(supportedSettings[i], rootKey);
                            break;
                        } catch (Exception e) {
                            i++;
                        }
                    }
                }
                setChangeListener();
            }
        }
    }

    /* access modifiers changed from: private */
    public void invokeLater(Runnable runnable) {
        getListView().post(runnable);
    }

    private void setChangeListener() {
        Prefs prefs = new Prefs(getPreferenceManager().getSharedPreferences());
        String disconnectNotificationState = prefs.getString("disconnect_notification", "off");
        boolean disconnectNotificationScheduled = disconnectNotificationState.equals("scheduled");
        final Preference disconnectNotificationStart = findPreference(HuamiConst.PREF_DISCONNECT_NOTIFICATION_START);
        if (disconnectNotificationStart != null) {
            disconnectNotificationStart.setEnabled(disconnectNotificationScheduled);
            disconnectNotificationStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(HuamiConst.PREF_DISCONNECT_NOTIFICATION_START);
                        }
                    });
                    return true;
                }
            });
        }
        final Preference disconnectNotificationEnd = findPreference(HuamiConst.PREF_DISCONNECT_NOTIFICATION_END);
        if (disconnectNotificationEnd != null) {
            disconnectNotificationEnd.setEnabled(disconnectNotificationScheduled);
            disconnectNotificationEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(HuamiConst.PREF_DISCONNECT_NOTIFICATION_END);
                        }
                    });
                    return true;
                }
            });
        }
        Preference disconnectNotification = findPreference("disconnect_notification");
        if (disconnectNotification != null) {
            disconnectNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    boolean scheduled = "scheduled".equals(newVal.toString());
                    ((Preference) Objects.requireNonNull(disconnectNotificationStart)).setEnabled(scheduled);
                    ((Preference) Objects.requireNonNull(disconnectNotificationEnd)).setEnabled(scheduled);
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration("disconnect_notification");
                        }
                    });
                    return true;
                }
            });
        }
        boolean nightModeScheduled = prefs.getString(MiBandConst.PREF_NIGHT_MODE, "off").equals("scheduled");
        final Preference nightModeStart = findPreference(MiBandConst.PREF_NIGHT_MODE_START);
        if (nightModeStart != null) {
            nightModeStart.setEnabled(nightModeScheduled);
            nightModeStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_NIGHT_MODE_START);
                        }
                    });
                    return true;
                }
            });
        }
        final Preference nightModeEnd = findPreference(MiBandConst.PREF_NIGHT_MODE_END);
        if (nightModeEnd != null) {
            nightModeEnd.setEnabled(nightModeScheduled);
            nightModeEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_NIGHT_MODE_END);
                        }
                    });
                    return true;
                }
            });
        }
        Preference nightMode = findPreference(MiBandConst.PREF_NIGHT_MODE);
        if (nightMode != null) {
            nightMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    boolean scheduled = "scheduled".equals(newVal.toString());
                    ((Preference) Objects.requireNonNull(nightModeStart)).setEnabled(scheduled);
                    ((Preference) Objects.requireNonNull(nightModeEnd)).setEnabled(scheduled);
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_NIGHT_MODE);
                        }
                    });
                    return true;
                }
            });
        }
        Preference preference = disconnectNotification;
        boolean doNotDisturbScheduled = prefs.getString(MiBandConst.PREF_DO_NOT_DISTURB, "off").equals("scheduled");
        String str = disconnectNotificationState;
        final Preference doNotDisturbStart = findPreference(MiBandConst.PREF_DO_NOT_DISTURB_START);
        if (doNotDisturbStart != null) {
            doNotDisturbStart.setEnabled(doNotDisturbScheduled);
            boolean z = disconnectNotificationScheduled;
            doNotDisturbStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_DO_NOT_DISTURB_START);
                        }
                    });
                    return true;
                }
            });
        }
        final Preference doNotDisturbEnd = findPreference(MiBandConst.PREF_DO_NOT_DISTURB_END);
        if (doNotDisturbEnd != null) {
            doNotDisturbEnd.setEnabled(doNotDisturbScheduled);
            boolean z2 = doNotDisturbScheduled;
            doNotDisturbEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_DO_NOT_DISTURB_END);
                        }
                    });
                    return true;
                }
            });
        }
        Preference doNotDisturb = findPreference(MiBandConst.PREF_DO_NOT_DISTURB);
        if (doNotDisturb != null) {
            doNotDisturb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    boolean scheduled = "scheduled".equals(newVal.toString());
                    ((Preference) Objects.requireNonNull(doNotDisturbStart)).setEnabled(scheduled);
                    ((Preference) Objects.requireNonNull(doNotDisturbEnd)).setEnabled(scheduled);
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_DO_NOT_DISTURB);
                        }
                    });
                    return true;
                }
            });
        }
        addPreferenceHandlerFor(MiBandConst.PREF_SWIPE_UNLOCK);
        addPreferenceHandlerFor(MiBandConst.PREF_MI2_DATEFORMAT);
        addPreferenceHandlerFor(DeviceSettingsPreferenceConst.PREF_DATEFORMAT);
        addPreferenceHandlerFor(HuamiConst.PREF_DISPLAY_ITEMS);
        addPreferenceHandlerFor(HuamiConst.PREF_LANGUAGE);
        addPreferenceHandlerFor(HuamiConst.PREF_EXPOSE_HR_THIRDPARTY);
        addPreferenceHandlerFor(DeviceSettingsPreferenceConst.PREF_WEARLOCATION);
        addPreferenceHandlerFor(DeviceSettingsPreferenceConst.PREF_SCREEN_ORIENTATION);
        addPreferenceHandlerFor(DeviceSettingsPreferenceConst.PREF_TIMEFORMAT);
        Preference preference2 = doNotDisturb;
        String displayOnLiftState = prefs.getString("activate_display_on_lift_wrist", "off");
        boolean displayOnLiftScheduled = displayOnLiftState.equals("scheduled");
        Prefs prefs2 = prefs;
        final Preference rotateWristCycleInfo = findPreference(MiBandConst.PREF_MI2_ROTATE_WRIST_TO_SWITCH_INFO);
        if (rotateWristCycleInfo != null) {
            rotateWristCycleInfo.setEnabled(!"off".equals(displayOnLiftState));
            rotateWristCycleInfo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(MiBandConst.PREF_MI2_ROTATE_WRIST_TO_SWITCH_INFO);
                        }
                    });
                    return true;
                }
            });
        }
        final Preference displayOnLiftStart = findPreference(HuamiConst.PREF_DISPLAY_ON_LIFT_START);
        if (displayOnLiftStart != null) {
            displayOnLiftStart.setEnabled(displayOnLiftScheduled);
            String str2 = displayOnLiftState;
            displayOnLiftStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(HuamiConst.PREF_DISPLAY_ON_LIFT_START);
                        }
                    });
                    return true;
                }
            });
        }
        final Preference displayOnLiftEnd = findPreference(HuamiConst.PREF_DISPLAY_ON_LIFT_END);
        if (displayOnLiftEnd != null) {
            displayOnLiftEnd.setEnabled(displayOnLiftScheduled);
            Preference preference3 = doNotDisturbStart;
            displayOnLiftEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(HuamiConst.PREF_DISPLAY_ON_LIFT_END);
                        }
                    });
                    return true;
                }
            });
        }
        Preference displayOnLift = findPreference("activate_display_on_lift_wrist");
        if (displayOnLift != null) {
            displayOnLift.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    boolean scheduled = "scheduled".equals(newVal.toString());
                    ((Preference) Objects.requireNonNull(displayOnLiftStart)).setEnabled(scheduled);
                    ((Preference) Objects.requireNonNull(displayOnLiftEnd)).setEnabled(scheduled);
                    Preference preference2 = rotateWristCycleInfo;
                    if (preference2 != null) {
                        preference2.setEnabled(!"off".equals(newVal.toString()));
                    }
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration("activate_display_on_lift_wrist");
                        }
                    });
                    return true;
                }
            });
        }
        Preference preference4 = rotateWristCycleInfo;
        setInputTypeFor(HuamiConst.PREF_BUTTON_ACTION_BROADCAST_DELAY, 2);
        setInputTypeFor(HuamiConst.PREF_BUTTON_ACTION_PRESS_MAX_INTERVAL, 2);
        setInputTypeFor(HuamiConst.PREF_BUTTON_ACTION_PRESS_COUNT, 2);
        setInputTypeFor(MiBandConst.PREF_MIBAND_DEVICE_TIME_OFFSET_HOURS, InputDeviceCompat.SOURCE_TOUCHSCREEN);
        setInputTypeFor(MakibesHR3Constants.PREF_FIND_PHONE_DURATION, 2);
        setInputTypeFor(DeviceSettingsPreferenceConst.PREF_RESERVER_ALARMS_CALENDAR, 2);
    }

    static DeviceSpecificSettingsFragment newInstance(String settingsFileSuffix, int[] supportedSettings) {
        DeviceSpecificSettingsFragment fragment = new DeviceSpecificSettingsFragment();
        fragment.setSettingsFileSuffix(settingsFileSuffix, supportedSettings);
        return fragment;
    }

    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof XTimePreference) {
            DialogFragment dialogFragment = new XTimePreferenceFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
            dialogFragment.setTargetFragment(this, 0);
            if (getFragmentManager() != null) {
                dialogFragment.show(getFragmentManager(), "androidx.preference.PreferenceFragment.DIALOG");
                return;
            }
            return;
        }
        super.onDisplayPreferenceDialog(preference);
    }

    private void addPreferenceHandlerFor(final String preferenceKey) {
        Preference pref = findPreference(preferenceKey);
        if (pref != null) {
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    DeviceSpecificSettingsFragment.this.invokeLater(new Runnable() {
                        public void run() {
                            GBApplication.deviceService().onSendConfiguration(preferenceKey);
                        }
                    });
                    return true;
                }
            });
        }
    }

    private void setInputTypeFor(String preferenceKey, final int editTypeFlags) {
        EditTextPreference textPreference = (EditTextPreference) findPreference(preferenceKey);
        if (textPreference != null) {
            textPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                public void onBindEditText(EditText editText) {
                    editText.setInputType(editTypeFlags);
                }
            });
        }
    }
}
