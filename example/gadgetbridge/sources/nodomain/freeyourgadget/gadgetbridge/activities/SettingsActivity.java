package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.ChartsPreferencesActivity;
import nodomain.freeyourgadget.gadgetbridge.database.PeriodicExporter;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandPreferencesActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimePreferenceActivity;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsActivity extends AbstractSettingsActivity {
    private static final int FILE_REQUEST_CODE = 4711;
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) SettingsActivity.class);
    public static final String PREF_MEASUREMENT_SYSTEM = "measurement_system";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(C0889R.C0894xml.preferences);
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Prefs prefs = GBApplication.getPrefs();
        findPreference("notifications_generic").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity.this.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                return true;
            }
        });
        findPreference("pref_charts").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, ChartsPreferencesActivity.class));
                return true;
            }
        });
        findPreference("pref_key_miband").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, MiBandPreferencesActivity.class));
                return true;
            }
        });
        findPreference("pref_key_qhybrid").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity settingsActivity = SettingsActivity.this;
                settingsActivity.startActivity(new Intent(settingsActivity, ConfigActivity.class));
                return true;
            }
        });
        findPreference("pref_key_zetime").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, ZeTimePreferenceActivity.class));
                return true;
            }
        });
        findPreference("pref_key_blacklist").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, AppBlacklistActivity.class));
                return true;
            }
        });
        findPreference("pref_key_blacklist_calendars").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, CalBlacklistActivity.class));
                return true;
            }
        });
        findPreference("pebble_emu_addr").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                LocalBroadcastManager.getInstance(SettingsActivity.this.getApplicationContext()).sendBroadcast(new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST));
                preference.setSummary(newVal.toString());
                return true;
            }
        });
        findPreference("pebble_emu_port").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                LocalBroadcastManager.getInstance(SettingsActivity.this.getApplicationContext()).sendBroadcast(new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST));
                preference.setSummary(newVal.toString());
                return true;
            }
        });
        findPreference("log_to_file").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                boolean doEnable = Boolean.TRUE.equals(newVal);
                if (doEnable) {
                    try {
                        FileUtils.getExternalFilesDir();
                    } catch (IOException ex) {
                        C1238GB.toast(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(C0889R.string.error_creating_directory_for_logfiles, new Object[]{ex.getLocalizedMessage()}), 1, 3, ex);
                    }
                }
                GBApplication.setupLogging(doEnable);
                return true;
            }
        });
        findPreference(HuamiConst.PREF_LANGUAGE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                try {
                    GBApplication.setLanguage(newVal.toString());
                    SettingsActivity.this.recreate();
                } catch (Exception ex) {
                    Context applicationContext = SettingsActivity.this.getApplicationContext();
                    C1238GB.toast(applicationContext, "Error setting language: " + ex.getLocalizedMessage(), 1, 3, ex);
                }
                return true;
            }
        });
        findPreference(PREF_MEASUREMENT_SYSTEM).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                SettingsActivity.this.invokeLater(new Runnable() {
                    public void run() {
                        GBApplication.deviceService().onSendConfiguration(SettingsActivity.PREF_MEASUREMENT_SYSTEM);
                    }
                });
                preference.setSummary(newVal.toString());
                return true;
            }
        });
        if (!GBApplication.isRunningMarshmallowOrLater()) {
            ((PreferenceCategory) findPreference("pref_key_notifications")).removePreference(findPreference("notification_filter"));
        }
        findPreference("location_aquire").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (ActivityCompat.checkSelfPermission(SettingsActivity.this.getApplicationContext(), "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 0);
                }
                LocationManager locationManager = (LocationManager) SettingsActivity.this.getSystemService("location");
                String provider = locationManager.getBestProvider(new Criteria(), false);
                if (provider != null) {
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        SettingsActivity.this.setLocationPreferences(location);
                        return true;
                    }
                    locationManager.requestSingleUpdate(provider, new LocationListener() {
                        public void onLocationChanged(Location location) {
                            SettingsActivity.this.setLocationPreferences(location);
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Logger access$200 = SettingsActivity.LOG;
                            access$200.info("provider status changed to " + status + " (" + provider + ")");
                        }

                        public void onProviderEnabled(String provider) {
                            Logger access$200 = SettingsActivity.LOG;
                            access$200.info("provider enabled (" + provider + ")");
                        }

                        public void onProviderDisabled(String provider) {
                            Logger access$200 = SettingsActivity.LOG;
                            access$200.info("provider disabled (" + provider + ")");
                            C1238GB.toast((Context) SettingsActivity.this, SettingsActivity.this.getString(C0889R.string.toast_enable_networklocationprovider), (int) PathInterpolatorCompat.MAX_NUM_POINTS, 0);
                        }
                    }, (Looper) null);
                    return true;
                }
                SettingsActivity.LOG.warn("No location provider found, did you deny location permission?");
                return true;
            }
        });
        findPreference("canned_messages_dismisscall_send").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Prefs prefs = GBApplication.getPrefs();
                ArrayList<String> messages = new ArrayList<>();
                for (int i = 1; i <= 16; i++) {
                    String message = prefs.getString("canned_message_dismisscall_" + i, (String) null);
                    if (message != null && !message.equals("")) {
                        messages.add(message);
                    }
                }
                CannedMessagesSpec cannedMessagesSpec = new CannedMessagesSpec();
                cannedMessagesSpec.type = 1;
                cannedMessagesSpec.cannedMessages = (String[]) messages.toArray(new String[messages.size()]);
                GBApplication.deviceService().onSetCannedMessages(cannedMessagesSpec);
                return true;
            }
        });
        findPreference("weather_city").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                GBApplication.getPrefs().getPreferences().edit().putString("weather_cityid", (String) null).apply();
                preference.setSummary(newVal.toString());
                Intent intent = new Intent("GB_UPDATE_WEATHER");
                intent.setPackage(BuildConfig.APPLICATION_ID);
                SettingsActivity.this.sendBroadcast(intent);
                return true;
            }
        });
        Preference pref = findPreference(GBPrefs.AUTO_EXPORT_LOCATION);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent("android.intent.action.CREATE_DOCUMENT");
                i.setType("application/x-sqlite3");
                i.addCategory("android.intent.category.OPENABLE");
                i.addFlags(66);
                SettingsActivity.this.startActivityForResult(Intent.createChooser(i, SettingsActivity.this.getApplicationContext().getString(C0889R.string.choose_auto_export_location)), SettingsActivity.FILE_REQUEST_CODE);
                return true;
            }
        });
        pref.setSummary(getAutoExportLocationSummary());
        Preference pref2 = findPreference(GBPrefs.AUTO_EXPORT_INTERVAL);
        pref2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object autoExportInterval) {
                preference.setSummary(String.format(SettingsActivity.this.getApplicationContext().getString(C0889R.string.pref_summary_auto_export_interval), new Object[]{Integer.valueOf((String) autoExportInterval)}));
                PeriodicExporter.sheduleAlarm(SettingsActivity.this.getApplicationContext(), Integer.valueOf((String) autoExportInterval), GBApplication.getPrefs().getBoolean(GBPrefs.AUTO_EXPORT_ENABLED, false));
                return true;
            }
        });
        int autoExportInterval = GBApplication.getPrefs().getInt(GBPrefs.AUTO_EXPORT_INTERVAL, 0);
        pref2.setSummary(String.format(getApplicationContext().getString(C0889R.string.pref_summary_auto_export_interval), new Object[]{Integer.valueOf(autoExportInterval)}));
        findPreference(GBPrefs.AUTO_EXPORT_ENABLED).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object autoExportEnabled) {
                PeriodicExporter.sheduleAlarm(SettingsActivity.this.getApplicationContext(), Integer.valueOf(GBApplication.getPrefs().getInt(GBPrefs.AUTO_EXPORT_INTERVAL, 0)), ((Boolean) autoExportEnabled).booleanValue());
                return true;
            }
        });
        Preference pref3 = findPreference("auto_fetch_interval_limit");
        pref3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object autoFetchInterval) {
                preference.setSummary(String.format(SettingsActivity.this.getApplicationContext().getString(C0889R.string.pref_auto_fetch_limit_fetches_summary), new Object[]{Integer.valueOf((String) autoFetchInterval)}));
                return true;
            }
        });
        int autoFetchInterval = GBApplication.getPrefs().getInt("auto_fetch_interval_limit", 0);
        pref3.setSummary(String.format(getApplicationContext().getString(C0889R.string.pref_auto_fetch_limit_fetches_summary), new Object[]{Integer.valueOf(autoFetchInterval)}));
        Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
        PackageManager pm = getPackageManager();
        List<ResolveInfo> mediaReceivers = pm.queryBroadcastReceivers(mediaButtonIntent, 96);
        CharSequence[] newEntries = new CharSequence[(mediaReceivers.size() + 1)];
        CharSequence[] newValues = new CharSequence[(mediaReceivers.size() + 1)];
        newEntries[0] = getString(C0889R.string.pref_default);
        newValues[0] = "default";
        int i = 1;
        for (ResolveInfo resolveInfo : mediaReceivers) {
            newEntries[i] = resolveInfo.activityInfo.loadLabel(pm);
            newValues[i] = resolveInfo.activityInfo.packageName;
            i++;
        }
        ListPreference audioPlayer = (ListPreference) findPreference("audio_player");
        audioPlayer.setEntries(newEntries);
        audioPlayer.setEntryValues(newValues);
        audioPlayer.setDefaultValue(newValues[0]);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILE_REQUEST_CODE && intent != null) {
            Uri uri = intent.getData();
            getContentResolver().takePersistableUriPermission(uri, 2);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(GBPrefs.AUTO_EXPORT_LOCATION, uri.toString()).apply();
            findPreference(GBPrefs.AUTO_EXPORT_LOCATION).setSummary(getAutoExportLocationSummary());
            PeriodicExporter.sheduleAlarm(getApplicationContext(), Integer.valueOf(GBApplication.getPrefs().getInt(GBPrefs.AUTO_EXPORT_INTERVAL, 0)), GBApplication.getPrefs().getBoolean(GBPrefs.AUTO_EXPORT_ENABLED, false));
        }
    }

    private String getAutoExportLocationSummary() {
        String autoExportLocation = GBApplication.getPrefs().getString(GBPrefs.AUTO_EXPORT_LOCATION, (String) null);
        if (autoExportLocation == null) {
            return "";
        }
        Uri uri = Uri.parse(autoExportLocation);
        try {
            return AndroidUtils.getFilePath(getApplicationContext(), uri);
        } catch (IllegalArgumentException e) {
            IllegalArgumentException illegalArgumentException = e;
            try {
                Cursor cursor = getContentResolver().query(uri, new String[]{"_display_name"}, (String) null, (String[]) null, (String) null, (CancellationSignal) null);
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex("_display_name"));
                }
            } catch (Exception e2) {
                LOG.warn("fuck");
            }
            return "";
        }
    }

    /* access modifiers changed from: private */
    public void invokeLater(Runnable runnable) {
        getListView().post(runnable);
    }

    /* access modifiers changed from: protected */
    public String[] getPreferenceKeysWithSummary() {
        return new String[]{"pebble_emu_addr", "pebble_emu_port", "pebble_reconnect_attempts", "location_latitude", "location_longitude", "canned_reply_suffix", "canned_reply_1", "canned_reply_2", "canned_reply_3", "canned_reply_4", "canned_reply_5", "canned_reply_6", "canned_reply_7", "canned_reply_8", "canned_reply_9", "canned_reply_10", "canned_reply_11", "canned_reply_12", "canned_reply_13", "canned_reply_14", "canned_reply_15", "canned_reply_16", "canned_message_dismisscall_1", "canned_message_dismisscall_2", "canned_message_dismisscall_3", "canned_message_dismisscall_4", "canned_message_dismisscall_5", "canned_message_dismisscall_6", "canned_message_dismisscall_7", "canned_message_dismisscall_8", "canned_message_dismisscall_9", "canned_message_dismisscall_10", "canned_message_dismisscall_11", "canned_message_dismisscall_12", "canned_message_dismisscall_13", "canned_message_dismisscall_14", "canned_message_dismisscall_15", "canned_message_dismisscall_16", ActivityUser.PREF_USER_YEAR_OF_BIRTH, ActivityUser.PREF_USER_HEIGHT_CM, ActivityUser.PREF_USER_WEIGHT_KG, "activity_user_sleep_duration", "mi_fitness_goal", "weather_city"};
    }

    /* access modifiers changed from: private */
    public void setLocationPreferences(Location location) {
        String latitude = String.format(Locale.US, "%.6g", new Object[]{Double.valueOf(location.getLatitude())});
        String longitude = String.format(Locale.US, "%.6g", new Object[]{Double.valueOf(location.getLongitude())});
        Logger logger = LOG;
        logger.info("got location. Lat: " + latitude + " Lng: " + longitude);
        C1238GB.toast((Context) this, getString(C0889R.string.toast_aqurired_networklocation), (int) ActivityUser.defaultUserCaloriesBurnt, 0);
        EditTextPreference pref_latitude = (EditTextPreference) findPreference("location_latitude");
        EditTextPreference pref_longitude = (EditTextPreference) findPreference("location_longitude");
        pref_latitude.setText(latitude);
        pref_longitude.setText(longitude);
        pref_latitude.setSummary(latitude);
        pref_longitude.setSummary(longitude);
    }
}
