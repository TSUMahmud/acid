package nodomain.freeyourgadget.gadgetbridge.devices.qhybrid;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.TimePicker;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Marker;

public class ConfigActivity extends AbstractGBActivity {
    final int REQUEST_CODE_ADD_APP = 0;
    PackageAdapter adapter;
    BroadcastReceiver buttonReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            C1238GB.toast("Button " + intent.getIntExtra("BUTTON", -1) + " pressed", 0, 1);
        }
    };
    GBDevice device;
    BroadcastReceiver fileReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("EXTRA_ERROR", false)) {
                C1238GB.toast(ConfigActivity.this.getString(C0889R.string.qhybrid_buttons_overwrite_error), 0, 3);
            } else {
                C1238GB.toast(ConfigActivity.this.getString(C0889R.string.qhybrid_buttons_overwrite_success), 0, 1);
            }
        }
    };
    private boolean hasControl = false;
    PackageConfigHelper helper;
    ArrayList<NotificationConfiguration> list;
    SharedPreferences prefs;
    BroadcastReceiver settingsReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            C1238GB.toast("Setting updated", 0, 1);
            ConfigActivity.this.updateSettings();
        }
    };
    TextView timeOffsetView;
    TextView timezoneOffsetView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_qhybrid_settings);
        findViewById(C0889R.C0891id.buttonOverwriteButtons).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(ConfigActivity.this).sendBroadcast(new Intent(QHybridSupport.QHYBRID_COMMAND_OVERWRITE_BUTTONS));
            }
        });
        this.prefs = getSharedPreferences(getPackageName(), 0);
        this.timeOffsetView = (TextView) findViewById(C0889R.C0891id.qhybridTimeOffset);
        this.timeOffsetView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int timeOffset = ConfigActivity.this.prefs.getInt("QHYBRID_TIME_OFFSET", 0);
                LinearLayout layout2 = new LinearLayout(ConfigActivity.this);
                layout2.setOrientation(0);
                final NumberPicker hourPicker = new NumberPicker(ConfigActivity.this);
                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(23);
                hourPicker.setValue(timeOffset / 60);
                final NumberPicker minPicker = new NumberPicker(ConfigActivity.this);
                minPicker.setMinValue(0);
                minPicker.setMaxValue(59);
                minPicker.setValue(timeOffset % 60);
                layout2.addView(hourPicker);
                TextView tw = new TextView(ConfigActivity.this);
                tw.setText(":");
                layout2.addView(tw);
                layout2.addView(minPicker);
                layout2.setGravity(17);
                new AlertDialog.Builder(ConfigActivity.this).setTitle(ConfigActivity.this.getString(C0889R.string.qhybrid_offset_time_by)).setView(layout2).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfigActivity.this.prefs.edit().putInt("QHYBRID_TIME_OFFSET", (hourPicker.getValue() * 60) + minPicker.getValue()).apply();
                        ConfigActivity.this.updateTimeOffset();
                        LocalBroadcastManager.getInstance(ConfigActivity.this).sendBroadcast(new Intent(QHybridSupport.QHYBRID_COMMAND_UPDATE));
                        C1238GB.toast(ConfigActivity.this.getString(C0889R.string.qhybrid_changes_delay_prompt), 0, 1);
                    }
                }).setNegativeButton("cancel", (DialogInterface.OnClickListener) null).show();
            }
        });
        updateTimeOffset();
        this.timezoneOffsetView = (TextView) findViewById(C0889R.C0891id.timezoneOffset);
        this.timezoneOffsetView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int timeOffset = ConfigActivity.this.prefs.getInt("QHYBRID_TIMEZONE_OFFSET", 0);
                LinearLayout layout2 = new LinearLayout(ConfigActivity.this);
                layout2.setOrientation(0);
                final NumberPicker hourPicker = new NumberPicker(ConfigActivity.this);
                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(23);
                hourPicker.setValue(timeOffset / 60);
                final NumberPicker minPicker = new NumberPicker(ConfigActivity.this);
                minPicker.setMinValue(0);
                minPicker.setMaxValue(59);
                minPicker.setValue(timeOffset % 60);
                layout2.addView(hourPicker);
                TextView tw = new TextView(ConfigActivity.this);
                tw.setText(":");
                layout2.addView(tw);
                layout2.addView(minPicker);
                layout2.setGravity(17);
                new AlertDialog.Builder(ConfigActivity.this).setTitle(ConfigActivity.this.getString(C0889R.string.qhybrid_offset_timezone)).setView(layout2).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfigActivity.this.prefs.edit().putInt("QHYBRID_TIMEZONE_OFFSET", (hourPicker.getValue() * 60) + minPicker.getValue()).apply();
                        ConfigActivity.this.updateTimezoneOffset();
                        LocalBroadcastManager.getInstance(ConfigActivity.this).sendBroadcast(new Intent(QHybridSupport.QHYBRID_COMMAND_UPDATE_TIMEZONE));
                        C1238GB.toast(ConfigActivity.this.getString(C0889R.string.qhybrid_changes_delay_prompt), 0, 1);
                    }
                }).setNegativeButton("cancel", (DialogInterface.OnClickListener) null).show();
            }
        });
        updateTimezoneOffset();
        setTitle(C0889R.string.preferences_qhybrid_settings);
        ListView appList = (ListView) findViewById(C0889R.C0891id.qhybrid_appList);
        try {
            this.helper = new PackageConfigHelper(getApplicationContext());
            this.list = this.helper.getNotificationConfigurations();
        } catch (GBException e) {
            C1238GB.log("error getting configurations", 3, e);
            C1238GB.toast("error getting configurations", 0, 3, (Throwable) e);
            this.list = new ArrayList<>();
        }
        this.list.add((Object) null);
        PackageAdapter packageAdapter = new PackageAdapter(this, C0889R.layout.qhybrid_package_settings_item, this.list);
        this.adapter = packageAdapter;
        appList.setAdapter(packageAdapter);
        appList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                PopupMenu menu = new PopupMenu(ConfigActivity.this, view);
                menu.getMenu().add(0, 0, 0, "edit");
                menu.getMenu().add(0, 1, 1, "delete");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == 0) {
                            TimePicker picker = new TimePicker((Context) ConfigActivity.this, (NotificationConfiguration) adapterView.getItemAtPosition(i));
                            picker.finishListener = new TimePicker.OnFinishListener() {
                                public void onFinish(boolean success, NotificationConfiguration config) {
                                    ConfigActivity.this.setControl(false, (NotificationConfiguration) null);
                                    if (success) {
                                        try {
                                            ConfigActivity.this.helper.saveNotificationConfiguration(config);
                                            LocalBroadcastManager.getInstance(ConfigActivity.this).sendBroadcast(new Intent(QHybridSupport.QHYBRID_COMMAND_NOTIFICATION_CONFIG_CHANGED));
                                        } catch (GBException e) {
                                            C1238GB.log("error saving configuration", 3, e);
                                            C1238GB.toast("error saving notification", 0, 3, (Throwable) e);
                                        }
                                        ConfigActivity.this.refreshList();
                                    }
                                }
                            };
                            picker.handsListener = new TimePicker.OnHandsSetListener() {
                                public void onHandsSet(NotificationConfiguration config) {
                                    ConfigActivity.this.setHands(config);
                                }
                            };
                            picker.vibrationListener = new TimePicker.OnVibrationSetListener() {
                                public void onVibrationSet(NotificationConfiguration config) {
                                    ConfigActivity.this.vibrate(config);
                                }
                            };
                            ConfigActivity.this.setControl(true, picker.getSettings());
                        } else if (itemId == 1) {
                            try {
                                ConfigActivity.this.helper.deleteNotificationConfiguration((NotificationConfiguration) adapterView.getItemAtPosition(i));
                                LocalBroadcastManager.getInstance(ConfigActivity.this).sendBroadcast(new Intent(QHybridSupport.QHYBRID_COMMAND_NOTIFICATION_CONFIG_CHANGED));
                            } catch (GBException e) {
                                C1238GB.log("error deleting configuration", 3, e);
                                C1238GB.toast("error deleting setting", 0, 3, (Throwable) e);
                            }
                            ConfigActivity.this.refreshList();
                        }
                        return true;
                    }
                });
                menu.show();
                return true;
            }
        });
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent notificationIntent = new Intent(QHybridSupport.QHYBRID_COMMAND_NOTIFICATION);
                notificationIntent.putExtra("CONFIG", (NotificationConfiguration) adapterView.getItemAtPosition(i));
                LocalBroadcastManager.getInstance(ConfigActivity.this).sendBroadcast(notificationIntent);
            }
        });
        ((SeekBar) findViewById(C0889R.C0891id.vibrationStrengthBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int start;

            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                this.start = seekBar.getProgress();
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int progress2 = progress;
                if (progress != this.start) {
                    ConfigActivity.this.device.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_VIBRATION_STRENGTH, new String[]{"25", "50", "100"}[progress2]));
                    Intent intent = new Intent(QHybridSupport.QHYBRID_COMMAND_UPDATE_SETTINGS);
                    intent.putExtra("EXTRA_SETTING", QHybridSupport.ITEM_VIBRATION_STRENGTH);
                    LocalBroadcastManager.getInstance(ConfigActivity.this).sendBroadcast(intent);
                }
            }
        });
        this.device = GBApplication.app().getDeviceManager().getSelectedDevice();
        GBDevice gBDevice = this.device;
        if (gBDevice == null || gBDevice.getType() != DeviceType.FOSSILQHYBRID) {
            setSettingsError(getString(C0889R.string.watch_not_connected));
        } else {
            updateSettings();
        }
    }

    /* access modifiers changed from: private */
    public void updateTimeOffset() {
        int timeOffset = this.prefs.getInt("QHYBRID_TIME_OFFSET", 0);
        DecimalFormat format = new DecimalFormat("00");
        TextView textView = this.timeOffsetView;
        textView.setText(format.format((long) (timeOffset / 60)) + ":" + format.format((long) (timeOffset % 60)));
    }

    /* access modifiers changed from: private */
    public void updateTimezoneOffset() {
        int timeOffset = this.prefs.getInt("QHYBRID_TIMEZONE_OFFSET", 0);
        DecimalFormat format = new DecimalFormat("00");
        TextView textView = this.timezoneOffsetView;
        textView.setText(format.format((long) (timeOffset / 60)) + ":" + format.format((long) (timeOffset % 60)));
    }

    /* access modifiers changed from: private */
    public void setSettingsEnabled(boolean enables) {
        findViewById(C0889R.C0891id.settingsLayout).setAlpha(enables ? 1.0f : 0.2f);
    }

    /* access modifiers changed from: private */
    public void updateSettings() {
        runOnUiThread(new Runnable() {
            /* JADX WARNING: Removed duplicated region for block: B:22:0x013d A[Catch:{ JSONException -> 0x01ce }, LOOP:0: B:20:0x013a->B:22:0x013d, LOOP_END] */
            /* JADX WARNING: Removed duplicated region for block: B:26:0x014f A[Catch:{ JSONException -> 0x01ce }] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r20 = this;
                    r7 = r20
                    java.lang.String r8 = "Button "
                    java.lang.String r0 = ""
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    r2 = 2131296690(0x7f0901b2, float:1.8211304E38)
                    android.view.View r1 = r1.findViewById(r2)
                    r9 = r1
                    android.widget.EditText r9 = (android.widget.EditText) r9
                    r1 = 0
                    r9.setOnEditorActionListener(r1)
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r1.device
                    java.lang.String r2 = "STEP_GOAL"
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r1 = r1.getDeviceInfo(r2)
                    java.lang.String r10 = r1.getDetails()
                    r9.setText(r10)
                    int r1 = r10.length()
                    r9.setSelection(r1)
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$1 r1 = new nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$1
                    r1.<init>()
                    r9.setOnEditorActionListener(r1)
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r1.device
                    java.lang.String r2 = "EXTENDED_VIBRATION"
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r1 = r1.getDeviceInfo(r2)
                    java.lang.String r1 = r1.getDetails()
                    java.lang.String r2 = "true"
                    boolean r1 = r2.equals(r1)
                    r3 = 2131296759(0x7f0901f7, float:1.8211444E38)
                    r4 = 0
                    r11 = 1
                    if (r1 == 0) goto L_0x0083
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r1.device
                    java.lang.String r5 = "VIBRATION_STRENGTH"
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r1 = r1.getDeviceInfo(r5)
                    java.lang.String r1 = r1.getDetails()
                    double r5 = java.lang.Double.parseDouble(r1)
                    r12 = 4627730092099895296(0x4039000000000000, double:25.0)
                    double r5 = r5 / r12
                    double r5 = java.lang.Math.log(r5)
                    r12 = 4611686018427387904(0x4000000000000000, double:2.0)
                    double r12 = java.lang.Math.log(r12)
                    double r5 = r5 / r12
                    int r1 = (int) r5
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r5 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    r5.setSettingsEnabled(r11)
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r5 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    android.view.View r3 = r5.findViewById(r3)
                    android.widget.SeekBar r3 = (android.widget.SeekBar) r3
                    r3.setProgress(r1)
                    goto L_0x009a
                L_0x0083:
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    android.view.View r1 = r1.findViewById(r3)
                    r1.setEnabled(r4)
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    r3 = 2131296760(0x7f0901f8, float:1.8211446E38)
                    android.view.View r1 = r1.findViewById(r3)
                    r3 = 1056964608(0x3f000000, float:0.5)
                    r1.setAlpha(r3)
                L_0x009a:
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    r3 = 2131296419(0x7f0900a3, float:1.8210754E38)
                    android.view.View r1 = r1.findViewById(r3)
                    r12 = r1
                    android.widget.CheckBox r12 = (android.widget.CheckBox) r12
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r1.device
                    java.lang.String r3 = "HAS_ACTIVITY_HAND"
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r1 = r1.getDeviceInfo(r3)
                    java.lang.String r1 = r1.getDetails()
                    boolean r1 = r1.equals(r2)
                    if (r1 == 0) goto L_0x00da
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r1.device
                    java.lang.String r3 = "USE_ACTIVITY_HAND"
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r1 = r1.getDeviceInfo(r3)
                    java.lang.String r1 = r1.getDetails()
                    boolean r1 = r1.equals(r2)
                    if (r1 == 0) goto L_0x00d1
                    r12.setChecked(r11)
                L_0x00d1:
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$2 r1 = new nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$2
                    r1.<init>()
                    r12.setOnCheckedChangeListener(r1)
                    goto L_0x00e8
                L_0x00da:
                    r1 = 1045220557(0x3e4ccccd, float:0.2)
                    r12.setAlpha(r1)
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$3 r1 = new nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$3
                    r1.<init>()
                    r12.setOnClickListener(r1)
                L_0x00e8:
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r1.device
                    java.lang.String r2 = "BUTTONS"
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r1 = r1.getDeviceInfo(r2)
                    java.lang.String r13 = r1.getDetails()
                    if (r13 == 0) goto L_0x0106
                    boolean r1 = r13.isEmpty()     // Catch:{ JSONException -> 0x01ce }
                    if (r1 == 0) goto L_0x00ff
                    goto L_0x0106
                L_0x00ff:
                    org.json.JSONArray r0 = new org.json.JSONArray     // Catch:{ JSONException -> 0x01ce }
                    r0.<init>(r13)     // Catch:{ JSONException -> 0x01ce }
                    r14 = r0
                    goto L_0x0111
                L_0x0106:
                    org.json.JSONArray r1 = new org.json.JSONArray     // Catch:{ JSONException -> 0x01ce }
                    java.lang.String[] r0 = new java.lang.String[]{r0, r0, r0}     // Catch:{ JSONException -> 0x01ce }
                    r1.<init>(r0)     // Catch:{ JSONException -> 0x01ce }
                    r0 = r1
                    r14 = r0
                L_0x0111:
                    r15 = r14
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r0 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this     // Catch:{ JSONException -> 0x01ce }
                    r1 = 2131296392(0x7f090088, float:1.82107E38)
                    android.view.View r0 = r0.findViewById(r1)     // Catch:{ JSONException -> 0x01ce }
                    android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0     // Catch:{ JSONException -> 0x01ce }
                    r5 = r0
                    r5.removeAllViews()     // Catch:{ JSONException -> 0x01ce }
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r0 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this     // Catch:{ JSONException -> 0x01ce }
                    r1 = 2131296393(0x7f090089, float:1.8210701E38)
                    android.view.View r0 = r0.findViewById(r1)     // Catch:{ JSONException -> 0x01ce }
                    r1 = 8
                    r0.setVisibility(r1)     // Catch:{ JSONException -> 0x01ce }
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig.ConfigPayload[] r0 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig.ConfigPayload.values()     // Catch:{ JSONException -> 0x01ce }
                    r3 = r0
                    int r0 = r3.length     // Catch:{ JSONException -> 0x01ce }
                    java.lang.String[] r0 = new java.lang.String[r0]     // Catch:{ JSONException -> 0x01ce }
                    r16 = r0
                    r0 = r4
                L_0x013a:
                    int r1 = r3.length     // Catch:{ JSONException -> 0x01ce }
                    if (r0 >= r1) goto L_0x0148
                    r1 = r3[r0]     // Catch:{ JSONException -> 0x01ce }
                    java.lang.String r1 = r1.getDescription()     // Catch:{ JSONException -> 0x01ce }
                    r16[r0] = r1     // Catch:{ JSONException -> 0x01ce }
                    int r0 = r0 + 1
                    goto L_0x013a
                L_0x0148:
                    r0 = r4
                L_0x0149:
                    int r0 = r15.length()     // Catch:{ JSONException -> 0x01ce }
                    if (r4 >= r0) goto L_0x01c8
                    r6 = r4
                    java.lang.String r0 = r15.getString(r4)     // Catch:{ JSONException -> 0x01ce }
                    r17 = r0
                    android.widget.TextView r0 = new android.widget.TextView     // Catch:{ JSONException -> 0x01ce }
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity r1 = nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.this     // Catch:{ JSONException -> 0x01ce }
                    r0.<init>(r1)     // Catch:{ JSONException -> 0x01ce }
                    r2 = r0
                    r0 = -1
                    r2.setTextColor(r0)     // Catch:{ JSONException -> 0x01ce }
                    r0 = 1101004800(0x41a00000, float:20.0)
                    r2.setTextSize(r0)     // Catch:{ JSONException -> 0x01ce }
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig.ConfigPayload r0 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig.ConfigPayload.valueOf(r17)     // Catch:{ IllegalArgumentException -> 0x018c }
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x018c }
                    r1.<init>()     // Catch:{ IllegalArgumentException -> 0x018c }
                    r1.append(r8)     // Catch:{ IllegalArgumentException -> 0x018c }
                    int r11 = r4 + 1
                    r1.append(r11)     // Catch:{ IllegalArgumentException -> 0x018c }
                    java.lang.String r11 = ": "
                    r1.append(r11)     // Catch:{ IllegalArgumentException -> 0x018c }
                    java.lang.String r11 = r0.getDescription()     // Catch:{ IllegalArgumentException -> 0x018c }
                    r1.append(r11)     // Catch:{ IllegalArgumentException -> 0x018c }
                    java.lang.String r1 = r1.toString()     // Catch:{ IllegalArgumentException -> 0x018c }
                    r2.setText(r1)     // Catch:{ IllegalArgumentException -> 0x018c }
                    goto L_0x01a6
                L_0x018c:
                    r0 = move-exception
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x01ce }
                    r1.<init>()     // Catch:{ JSONException -> 0x01ce }
                    r1.append(r8)     // Catch:{ JSONException -> 0x01ce }
                    int r11 = r4 + 1
                    r1.append(r11)     // Catch:{ JSONException -> 0x01ce }
                    java.lang.String r11 = ": Unknown"
                    r1.append(r11)     // Catch:{ JSONException -> 0x01ce }
                    java.lang.String r1 = r1.toString()     // Catch:{ JSONException -> 0x01ce }
                    r2.setText(r1)     // Catch:{ JSONException -> 0x01ce }
                L_0x01a6:
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$4 r0 = new nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity$7$4     // Catch:{ JSONException -> 0x01ce }
                    r1 = r0
                    r11 = r2
                    r2 = r20
                    r18 = r3
                    r3 = r16
                    r19 = r4
                    r4 = r18
                    r7 = r5
                    r5 = r15
                    r1.<init>(r3, r4, r5, r6)     // Catch:{ JSONException -> 0x01ce }
                    r11.setOnClickListener(r0)     // Catch:{ JSONException -> 0x01ce }
                    r7.addView(r11)     // Catch:{ JSONException -> 0x01ce }
                    int r4 = r19 + 1
                    r11 = 1
                    r5 = r7
                    r3 = r18
                    r7 = r20
                    goto L_0x0149
                L_0x01c8:
                    r18 = r3
                    r19 = r4
                    r7 = r5
                    goto L_0x01d9
                L_0x01ce:
                    r0 = move-exception
                    r1 = 3
                    java.lang.String r2 = "error parsing button config"
                    nodomain.freeyourgadget.gadgetbridge.util.C1238GB.log(r2, r1, r0)
                    r3 = 1
                    nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r2, r3, r1)
                L_0x01d9:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.ConfigActivity.C11287.run():void");
            }
        });
    }

    /* access modifiers changed from: private */
    public void setControl(boolean control, NotificationConfiguration config) {
        if (this.hasControl != control) {
            Intent intent = new Intent(control ? QHybridSupport.QHYBRID_COMMAND_CONTROL : QHybridSupport.QHYBRID_COMMAND_UNCONTROL);
            intent.putExtra("CONFIG", config);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            this.hasControl = control;
        }
    }

    /* access modifiers changed from: private */
    public void setHands(NotificationConfiguration config) {
        sendControl(config, QHybridSupport.QHYBRID_COMMAND_SET);
    }

    /* access modifiers changed from: private */
    public void vibrate(NotificationConfiguration config) {
        sendControl(config, QHybridSupport.QHYBRID_COMMAND_VIBRATE);
    }

    private void sendControl(NotificationConfiguration config, String request) {
        Intent intent = new Intent(request);
        intent.putExtra("CONFIG", config);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /* access modifiers changed from: private */
    public void refreshList() {
        this.list.clear();
        try {
            this.list.addAll(this.helper.getNotificationConfigurations());
        } catch (GBException e) {
            C1238GB.log("error getting configurations", 3, e);
            C1238GB.toast("error getting configurations", 0, 3, (Throwable) e);
        }
        this.list.add((Object) null);
        this.adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        refreshList();
        registerReceiver(this.buttonReceiver, new IntentFilter(QHybridSupport.QHYBRID_EVENT_BUTTON_PRESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.settingsReceiver, new IntentFilter(QHybridSupport.QHYBRID_EVENT_SETTINGS_UPDATED));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.fileReceiver, new IntentFilter(QHybridSupport.QHYBRID_EVENT_FILE_UPLOADED));
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        unregisterReceiver(this.buttonReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.settingsReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.fileReceiver);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setSettingsError(final String error) {
        runOnUiThread(new Runnable() {
            public void run() {
                ConfigActivity.this.setSettingsEnabled(false);
                ((TextView) ConfigActivity.this.findViewById(C0889R.C0891id.settingsErrorText)).setVisibility(0);
                ((TextView) ConfigActivity.this.findViewById(C0889R.C0891id.settingsErrorText)).setText(error);
            }
        });
    }

    class PackageAdapter extends ArrayAdapter<NotificationConfiguration> {
        PackageManager manager;

        PackageAdapter(Context context, int resource, List<NotificationConfiguration> objects) {
            super(context, resource, objects);
            this.manager = context.getPackageManager();
        }

        public View getView(int position, View view, ViewGroup parent) {
            View view2;
            Bitmap bitmap;
            View view3 = view;
            if (!(view3 instanceof RelativeLayout)) {
                view2 = ((LayoutInflater) ConfigActivity.this.getSystemService("layout_inflater")).inflate(C0889R.layout.qhybrid_package_settings_item, (ViewGroup) null);
            } else {
                view2 = view3;
            }
            NotificationConfiguration settings = (NotificationConfiguration) getItem(position);
            if (settings == null) {
                Button addButton = new Button(ConfigActivity.this);
                addButton.setText(Marker.ANY_NON_NULL_MARKER);
                addButton.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
                addButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        ConfigActivity.this.startActivityForResult(new Intent(ConfigActivity.this, QHybridAppChoserActivity.class), 0);
                    }
                });
                return addButton;
            }
            try {
                ((ImageView) view2.findViewById(C0889R.C0891id.packageIcon)).setImageDrawable(this.manager.getApplicationIcon(settings.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                C1238GB.log("error", 3, e);
            }
            ((TextView) view2.findViewById(C0889R.C0891id.packageName)).setText(settings.getAppName());
            Bitmap bitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap2);
            Paint black = new Paint();
            black.setColor(ViewCompat.MEASURED_STATE_MASK);
            black.setStyle(Paint.Style.STROKE);
            black.setStrokeWidth(5.0f);
            c.drawCircle(50.0f, 50.0f, 47.0f, black);
            if (settings.getHour() != -1) {
                double d = (double) 50;
                bitmap = bitmap2;
                Double.isNaN(d);
                double d2 = (double) 50;
                Double.isNaN(d2);
                c.drawLine((float) 50, (float) 50, (float) (d + (Math.sin(Math.toRadians((double) settings.getHour())) * 25.0d)), (float) (d2 - (Math.cos(Math.toRadians((double) settings.getHour())) * 25.0d)), black);
            } else {
                bitmap = bitmap2;
            }
            if (settings.getMin() != -1) {
                double d3 = (double) 50;
                Double.isNaN(d3);
                float sin = (float) (d3 + (Math.sin(Math.toRadians((double) settings.getMin())) * 33.0d));
                double d4 = (double) 50;
                Double.isNaN(d4);
                c.drawLine((float) 50, (float) 50, sin, (float) (d4 - (Math.cos(Math.toRadians((double) settings.getMin())) * 33.0d)), black);
            }
            ((ImageView) view2.findViewById(C0889R.C0891id.packageClock)).setImageBitmap(bitmap);
            return view2;
        }
    }
}
