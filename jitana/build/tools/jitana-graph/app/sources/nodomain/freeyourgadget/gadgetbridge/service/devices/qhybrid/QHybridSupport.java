package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.github.mikephil.charting.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.NotificationConfiguration;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import nodomain.freeyourgadget.gadgetbridge.externalevents.NotificationListener;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.DownloadFileRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QHybridSupport extends QHybridBaseSupport {
    public static final String ITEM_ACTIVITY_POINT = "ACTIVITY_POINT";
    public static final String ITEM_EXTENDED_VIBRATION_SUPPORT = "EXTENDED_VIBRATION";
    public static final String ITEM_HAS_ACTIVITY_HAND = "HAS_ACTIVITY_HAND";
    public static final String ITEM_LAST_HEARTBEAT = "LAST_HEARTBEAT";
    public static final String ITEM_STEP_COUNT = "STEP_COUNT";
    public static final String ITEM_STEP_GOAL = "STEP_GOAL";
    public static final String ITEM_TIMEZONE_OFFSET = "STEPTIMEZONE_OFFSET_COUNT";
    public static final String ITEM_USE_ACTIVITY_HAND = "USE_ACTIVITY_HAND";
    public static final String ITEM_VIBRATION_STRENGTH = "VIBRATION_STRENGTH";
    private static final String QHYBRID_ACTION_SET_ACTIVITY_HAND = "nodomain.freeyourgadget.gadgetbridge.Q_SET_ACTIVITY_HAND";
    public static final String QHYBRID_COMMAND_CONTROL = "qhybrid_command_control";
    public static final String QHYBRID_COMMAND_NOTIFICATION = "qhybrid_command_notification";
    public static final String QHYBRID_COMMAND_NOTIFICATION_CONFIG_CHANGED = "nodomain.freeyourgadget.gadgetbridge.Q_NOTIFICATION_CONFIG_CHANGED";
    public static final String QHYBRID_COMMAND_OVERWRITE_BUTTONS = "nodomain.freeyourgadget.gadgetbridge.Q_OVERWRITE_BUTTONS";
    public static final String QHYBRID_COMMAND_SET = "qhybrid_command_set";
    public static final String QHYBRID_COMMAND_UNCONTROL = "qhybrid_command_uncontrol";
    public static final String QHYBRID_COMMAND_UPDATE = "qhybrid_command_update";
    public static final String QHYBRID_COMMAND_UPDATE_SETTINGS = "nodomain.freeyourgadget.gadgetbridge.Q_UPDATE_SETTINGS";
    public static final String QHYBRID_COMMAND_UPDATE_TIMEZONE = "qhybrid_command_update_timezone";
    public static final String QHYBRID_COMMAND_VIBRATE = "qhybrid_command_vibrate";
    public static final String QHYBRID_EVENT_BUTTON_PRESS = "nodomain.freeyourgadget.gadgetbridge.Q_BUTTON_PRESSED";
    public static final String QHYBRID_EVENT_FILE_UPLOADED = "nodomain.freeyourgadget.gadgetbridge.Q_FILE_UPLOADED";
    public static final String QHYBRID_EVENT_MULTI_BUTTON_PRESS = "nodomain.freeyourgadget.gadgetbridge.Q_MULTI_BUTTON_PRESSED";
    public static final String QHYBRID_EVENT_SETTINGS_UPDATED = "nodomain.freeyourgadget.gadgetbridge.Q_SETTINGS_UPDATED";
    /* access modifiers changed from: private */
    public static final Logger logger = LoggerFactory.getLogger((Class<?>) QHybridSupport.class);
    private PackageConfigHelper helper;
    /* access modifiers changed from: private */
    public volatile boolean searchDevice = false;
    private long timeOffset;
    /* access modifiers changed from: private */
    public boolean useActivityHand;
    /* access modifiers changed from: private */
    public WatchAdapter watchAdapter;

    public /* bridge */ /* synthetic */ void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        super.onAddCalendarEvent(calendarEventSpec);
    }

    public /* bridge */ /* synthetic */ void onAppConfiguration(UUID uuid, String str, Integer num) {
        super.onAppConfiguration(uuid, str, num);
    }

    public /* bridge */ /* synthetic */ void onAppDelete(UUID uuid) {
        super.onAppDelete(uuid);
    }

    public /* bridge */ /* synthetic */ void onAppInfoReq() {
        super.onAppInfoReq();
    }

    public /* bridge */ /* synthetic */ void onAppReorder(UUID[] uuidArr) {
        super.onAppReorder(uuidArr);
    }

    public /* bridge */ /* synthetic */ void onAppStart(UUID uuid, boolean z) {
        super.onAppStart(uuid, z);
    }

    public /* bridge */ /* synthetic */ void onDeleteCalendarEvent(byte b, long j) {
        super.onDeleteCalendarEvent(b, j);
    }

    public /* bridge */ /* synthetic */ void onEnableHeartRateSleepSupport(boolean z) {
        super.onEnableHeartRateSleepSupport(z);
    }

    public /* bridge */ /* synthetic */ void onEnableRealtimeHeartRateMeasurement(boolean z) {
        super.onEnableRealtimeHeartRateMeasurement(z);
    }

    public /* bridge */ /* synthetic */ void onEnableRealtimeSteps(boolean z) {
        super.onEnableRealtimeSteps(z);
    }

    public /* bridge */ /* synthetic */ void onHeartRateTest() {
        super.onHeartRateTest();
    }

    public /* bridge */ /* synthetic */ void onInstallApp(Uri uri) {
        super.onInstallApp(uri);
    }

    public /* bridge */ /* synthetic */ void onReadConfiguration(String str) {
        super.onReadConfiguration(str);
    }

    public /* bridge */ /* synthetic */ void onReset(int i) {
        super.onReset(i);
    }

    public /* bridge */ /* synthetic */ void onScreenshotReq() {
        super.onScreenshotReq();
    }

    public /* bridge */ /* synthetic */ void onSendConfiguration(String str) {
        super.onSendConfiguration(str);
    }

    public /* bridge */ /* synthetic */ void onSendWeather(WeatherSpec weatherSpec) {
        super.onSendWeather(weatherSpec);
    }

    public /* bridge */ /* synthetic */ void onSetCallState(CallSpec callSpec) {
        super.onSetCallState(callSpec);
    }

    public /* bridge */ /* synthetic */ void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        super.onSetCannedMessages(cannedMessagesSpec);
    }

    public /* bridge */ /* synthetic */ void onSetConstantVibration(int i) {
        super.onSetConstantVibration(i);
    }

    public /* bridge */ /* synthetic */ void onSetHeartRateMeasurementInterval(int i) {
        super.onSetHeartRateMeasurementInterval(i);
    }

    public /* bridge */ /* synthetic */ void onSetMusicInfo(MusicSpec musicSpec) {
        super.onSetMusicInfo(musicSpec);
    }

    public /* bridge */ /* synthetic */ void onSetMusicState(MusicStateSpec musicStateSpec) {
        super.onSetMusicState(musicStateSpec);
    }

    public /* bridge */ /* synthetic */ boolean useAutoConnect() {
        return super.useAutoConnect();
    }

    public QHybridSupport() {
        super(logger);
        addSupportedService(UUID.fromString("3dda0001-957f-7d4a-34a6-74696673696d"));
        addSupportedService(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        addSupportedService(UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"));
        addSupportedService(UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb"));
        IntentFilter commandFilter = new IntentFilter(QHYBRID_COMMAND_CONTROL);
        commandFilter.addAction(QHYBRID_COMMAND_UNCONTROL);
        commandFilter.addAction(QHYBRID_COMMAND_SET);
        commandFilter.addAction(QHYBRID_COMMAND_VIBRATE);
        commandFilter.addAction(QHYBRID_COMMAND_UPDATE);
        commandFilter.addAction(QHYBRID_COMMAND_UPDATE_TIMEZONE);
        commandFilter.addAction(QHYBRID_COMMAND_NOTIFICATION);
        commandFilter.addAction(QHYBRID_COMMAND_UPDATE_SETTINGS);
        commandFilter.addAction(QHYBRID_COMMAND_OVERWRITE_BUTTONS);
        commandFilter.addAction(QHYBRID_COMMAND_NOTIFICATION_CONFIG_CHANGED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            /* JADX WARNING: Can't fix incorrect switch cases order */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onReceive(android.content.Context r13, android.content.Intent r14) {
                /*
                    r12 = this;
                    android.os.Bundle r0 = r14.getExtras()
                    if (r0 != 0) goto L_0x0008
                    r1 = 0
                    goto L_0x0014
                L_0x0008:
                    android.os.Bundle r1 = r14.getExtras()
                    java.lang.String r2 = "CONFIG"
                    java.lang.Object r1 = r1.get(r2)
                    nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.NotificationConfiguration r1 = (nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.NotificationConfiguration) r1
                L_0x0014:
                    java.lang.String r2 = r14.getAction()
                    int r3 = r2.hashCode()
                    r4 = -1
                    r5 = 2
                    r6 = 1
                    r7 = 0
                    switch(r3) {
                        case -2111418097: goto L_0x0081;
                        case -1715339938: goto L_0x0076;
                        case -1621694167: goto L_0x006c;
                        case -1469705408: goto L_0x0061;
                        case -1348283215: goto L_0x0057;
                        case -1142807906: goto L_0x004d;
                        case -343710231: goto L_0x0043;
                        case 156521943: goto L_0x0039;
                        case 338509900: goto L_0x002f;
                        case 1951267292: goto L_0x0025;
                        default: goto L_0x0023;
                    }
                L_0x0023:
                    goto L_0x008b
                L_0x0025:
                    java.lang.String r3 = "qhybrid_command_set"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 2
                    goto L_0x008c
                L_0x002f:
                    java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.Q_UPDATE_SETTINGS"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 7
                    goto L_0x008c
                L_0x0039:
                    java.lang.String r3 = "qhybrid_command_control"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 0
                    goto L_0x008c
                L_0x0043:
                    java.lang.String r3 = "qhybrid_command_vibrate"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 3
                    goto L_0x008c
                L_0x004d:
                    java.lang.String r3 = "qhybrid_command_uncontrol"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 1
                    goto L_0x008c
                L_0x0057:
                    java.lang.String r3 = "qhybrid_command_notification"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 4
                    goto L_0x008c
                L_0x0061:
                    java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.Q_OVERWRITE_BUTTONS"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 8
                    goto L_0x008c
                L_0x006c:
                    java.lang.String r3 = "qhybrid_command_update_timezone"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 6
                    goto L_0x008c
                L_0x0076:
                    java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.Q_NOTIFICATION_CONFIG_CHANGED"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 9
                    goto L_0x008c
                L_0x0081:
                    java.lang.String r3 = "qhybrid_command_update"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0023
                    r2 = 5
                    goto L_0x008c
                L_0x008b:
                    r2 = -1
                L_0x008c:
                    switch(r2) {
                        case 0: goto L_0x01b1;
                        case 1: goto L_0x01a7;
                        case 2: goto L_0x0195;
                        case 3: goto L_0x0187;
                        case 4: goto L_0x017d;
                        case 5: goto L_0x0172;
                        case 6: goto L_0x016c;
                        case 7: goto L_0x00ad;
                        case 8: goto L_0x009c;
                        case 9: goto L_0x0091;
                        default: goto L_0x008f;
                    }
                L_0x008f:
                    goto L_0x01df
                L_0x0091:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    r2.syncNotificationSettings()
                    goto L_0x01df
                L_0x009c:
                    java.lang.String r2 = "BUTTONS"
                    java.lang.String r2 = r14.getStringExtra(r2)
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r3 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r3 = r3.watchAdapter
                    r3.overwriteButtons(r2)
                    goto L_0x01df
                L_0x00ad:
                    java.lang.String r2 = "EXTRA_SETTING"
                    java.lang.String r2 = r14.getStringExtra(r2)
                    int r3 = r2.hashCode()
                    r8 = -955248786(0xffffffffc7100f6e, float:-36879.43)
                    java.lang.String r9 = "USE_ACTIVITY_HAND"
                    java.lang.String r10 = "STEP_GOAL"
                    java.lang.String r11 = "VIBRATION_STRENGTH"
                    if (r3 == r8) goto L_0x00dd
                    r7 = -134180698(0xfffffffff80090a6, float:-1.0430435E34)
                    if (r3 == r7) goto L_0x00d5
                    r7 = 1910258375(0x71dc3ac7, float:2.1810483E30)
                    if (r3 == r7) goto L_0x00cd
                L_0x00cc:
                    goto L_0x00e4
                L_0x00cd:
                    boolean r3 = r2.equals(r9)
                    if (r3 == 0) goto L_0x00cc
                    r4 = 2
                    goto L_0x00e4
                L_0x00d5:
                    boolean r3 = r2.equals(r10)
                    if (r3 == 0) goto L_0x00cc
                    r4 = 1
                    goto L_0x00e4
                L_0x00dd:
                    boolean r3 = r2.equals(r11)
                    if (r3 == 0) goto L_0x00cc
                    r4 = 0
                L_0x00e4:
                    if (r4 == 0) goto L_0x013a
                    if (r4 == r6) goto L_0x011e
                    if (r4 == r5) goto L_0x00eb
                    goto L_0x0156
                L_0x00eb:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r3 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r3.gbDevice
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r4 = r4.getDeviceInfo(r9)
                    java.lang.String r4 = r4.getDetails()
                    java.lang.String r5 = "true"
                    boolean r4 = r4.equals(r5)
                    boolean unused = r3.useActivityHand = r4
                    nodomain.freeyourgadget.gadgetbridge.util.Prefs r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
                    android.content.SharedPreferences r3 = r3.getPreferences()
                    android.content.SharedPreferences$Editor r3 = r3.edit()
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    boolean r4 = r4.useActivityHand
                    java.lang.String r5 = "QHYBRID_USE_ACTIVITY_HAND"
                    android.content.SharedPreferences$Editor r3 = r3.putBoolean(r5, r4)
                    r3.apply()
                    goto L_0x0156
                L_0x011e:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r3 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r3 = r3.watchAdapter
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r4.gbDevice
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r4 = r4.getDeviceInfo(r10)
                    java.lang.String r4 = r4.getDetails()
                    int r4 = java.lang.Integer.parseInt(r4)
                    r3.setStepGoal(r4)
                    goto L_0x0156
                L_0x013a:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r3 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r3 = r3.watchAdapter
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r4.gbDevice
                    nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails r4 = r4.getDeviceInfo(r11)
                    java.lang.String r4 = r4.getDetails()
                    short r4 = java.lang.Short.parseShort(r4)
                    r3.setVibrationStrength(r4)
                L_0x0156:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r3 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    android.content.Context r3 = r3.getContext()
                    androidx.localbroadcastmanager.content.LocalBroadcastManager r3 = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(r3)
                    android.content.Intent r4 = new android.content.Intent
                    java.lang.String r5 = "nodomain.freeyourgadget.gadgetbridge.Q_SETTINGS_UPDATED"
                    r4.<init>(r5)
                    r3.sendBroadcast(r4)
                    goto L_0x01df
                L_0x016c:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    r2.loadTimezoneOffset()
                    goto L_0x01df
                L_0x0172:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    r2.loadTimeOffset()
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    r2.onSetTime()
                    goto L_0x01df
                L_0x017d:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    r2.playNotification(r1)
                    goto L_0x01df
                L_0x0187:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest$VibrationType r3 = r1.getVibration()
                    r2.vibrate(r3)
                    goto L_0x01df
                L_0x0195:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    short r3 = r1.getHour()
                    short r4 = r1.getMin()
                    r2.setHands(r3, r4)
                    goto L_0x01df
                L_0x01a7:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    r2.releaseHandsControl()
                    goto L_0x01df
                L_0x01b1:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    java.lang.String r3 = "sending control request"
                    r2.log(r3)
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    r2.requestHandsControl()
                    if (r1 == 0) goto L_0x01d5
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    short r3 = r1.getHour()
                    short r4 = r1.getMin()
                    r2.setHands(r3, r4)
                    goto L_0x01df
                L_0x01d5:
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.this
                    nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r2 = r2.watchAdapter
                    r2.setHands(r7, r7)
                L_0x01df:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.C12141.onReceive(android.content.Context, android.content.Intent):void");
            }
        }, commandFilter);
        try {
            this.helper = new PackageConfigHelper(GBApplication.getContext());
        } catch (GBException e) {
            C1238GB.log("error getting database", 3, e);
            C1238GB.toast("error getting database", 0, 3, (Throwable) e);
            throw e;
        } catch (GBException ex) {
            ex.printStackTrace();
        }
        IntentFilter globalFilter = new IntentFilter();
        globalFilter.addAction(QHYBRID_ACTION_SET_ACTIVITY_HAND);
        GBApplication.getContext().registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (((action.hashCode() == 1262961429 && action.equals(QHybridSupport.QHYBRID_ACTION_SET_ACTIVITY_HAND)) ? (char) 0 : 65535) == 0) {
                    try {
                        float progress = Float.parseFloat(String.valueOf(intent.getExtras().get("EXTRA_PROGRESS")));
                        QHybridSupport.this.watchAdapter.setActivityHand((double) progress);
                        QHybridSupport.this.watchAdapter.playNotification(new NotificationConfiguration(-1, -1, (short) ((int) (180.0f * progress)), PlayNotificationRequest.VibrationType.NO_VIBE));
                    } catch (Exception e) {
                        C1238GB.log("wrong number format", 3, e);
                        QHybridSupport.logger.debug("trash extra should be number 0.0-1.0");
                    }
                }
            }
        }, globalFilter);
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        super.onSetAlarms(alarms);
        WatchAdapter watchAdapter2 = this.watchAdapter;
        if (watchAdapter2 == null) {
            C1238GB.toast("watch not connected", 1, 3);
        } else {
            watchAdapter2.onSetAlarms(alarms);
        }
    }

    /* access modifiers changed from: private */
    public void loadTimeOffset() {
        this.timeOffset = (long) getContext().getSharedPreferences(getContext().getPackageName(), 0).getInt("QHYBRID_TIME_OFFSET", 0);
    }

    /* access modifiers changed from: private */
    public void loadTimezoneOffset() {
        this.watchAdapter.setTimezoneOffsetMinutes((short) getContext().getSharedPreferences(getContext().getPackageName(), 0).getInt("QHYBRID_TIMEZONE_OFFSET", 0));
    }

    public long getTimeOffset() {
        return this.timeOffset;
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        this.useActivityHand = GBApplication.getPrefs().getBoolean("QHYBRID_USE_ACTIVITY_HAND", false);
        getDevice().addDeviceInfo(new GenericItem(ITEM_USE_ACTIVITY_HAND, String.valueOf(this.useActivityHand)));
        getDevice().setNotificationIconConnected(C0889R.C0890drawable.ic_notification_qhybrid);
        getDevice().setNotificationIconDisconnected(C0889R.C0890drawable.ic_notification_disconnected_qhybrid);
        for (int i = 2; i <= 7; i++) {
            builder.notify(getCharacteristic(UUID.fromString("3dda000" + i + "-957f-7d4a-34a6-74696673696d")), true);
        }
        builder.read(getCharacteristic(UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"))).read(getCharacteristic(UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb"))).read(getCharacteristic(UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb")));
        loadTimeOffset();
        return builder;
    }

    public void onFetchRecordedData(int dataTypes) {
        if ((RecordedDataTypes.TYPE_ACTIVITY & dataTypes) != 0) {
            this.watchAdapter.onFetchActivityData();
        }
    }

    public void onNotification(NotificationSpec notificationSpec) {
        log("notif from " + notificationSpec.sourceAppId + "  " + notificationSpec.sender + "   " + notificationSpec.phoneNumber);
        NotificationConfiguration config = null;
        boolean enforceActivityHandNotification = false;
        try {
            config = this.helper.getNotificationConfiguration(notificationSpec.sourceName);
        } catch (GBException e) {
            C1238GB.log("error getting notification configuration", 3, e);
            C1238GB.toast("error getting notification configuration", 0, 3, (Throwable) e);
        }
        if (config != null) {
            log("handling notification");
            if (!config.getRespectSilentMode() || ((AudioManager) getContext().getApplicationContext().getSystemService("audio")).getRingerMode() != 0) {
                if (config.getHour() == -1 && config.getMin() == -1) {
                    enforceActivityHandNotification = true;
                }
                playNotification(config);
                showNotificationsByAllActive(enforceActivityHandNotification);
            }
        }
    }

    /* access modifiers changed from: private */
    public void log(String message) {
        logger.debug(message);
    }

    public void onDeleteNotification(int id) {
        super.onDeleteNotification(id);
        showNotificationsByAllActive(true);
    }

    private void showNotificationsByAllActive(boolean enforceByNotification) {
        if (this.useActivityHand) {
            double progress = calculateNotificationProgress();
            showNotificationCountOnActivityHand(progress);
            if (enforceByNotification) {
                this.watchAdapter.playNotification(new NotificationConfiguration(-1, -1, (short) ((int) (180.0d * progress)), PlayNotificationRequest.VibrationType.NO_VIBE));
            }
        }
    }

    public double calculateNotificationProgress() {
        HashMap<NotificationConfiguration, Boolean> configs = new HashMap<>(0);
        try {
            Iterator<NotificationConfiguration> it = this.helper.getNotificationConfigurations().iterator();
            while (it.hasNext()) {
                configs.put(it.next(), false);
            }
        } catch (GBException e) {
            C1238GB.log("error getting notification configuration", 3, e);
            C1238GB.toast("error getting notification configs", 0, 3, (Throwable) e);
        }
        double notificationProgress = Utils.DOUBLE_EPSILON;
        Iterator<String> it2 = NotificationListener.notificationStack.iterator();
        while (it2.hasNext()) {
            String notificationPackage = it2.next();
            for (NotificationConfiguration notificationConfiguration : configs.keySet()) {
                if (!configs.get(notificationConfiguration).booleanValue() && notificationConfiguration.getPackageName().equals(notificationPackage)) {
                    notificationProgress += 0.25d;
                    configs.put(notificationConfiguration, true);
                }
            }
        }
        return notificationProgress;
    }

    private void showNotificationCountOnActivityHand(double progress) {
        if (this.useActivityHand) {
            this.watchAdapter.setActivityHand(progress);
        }
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        WatchAdapter watchAdapter2 = this.watchAdapter;
        if (watchAdapter2 != null) {
            watchAdapter2.onMtuChanged(gatt, mtu, status);
        }
    }

    private void playNotification(NotificationConfiguration config) {
        if (config.getMin() != -1 || config.getHour() != -1 || config.getVibration() != PlayNotificationRequest.VibrationType.NO_VIBE) {
            this.watchAdapter.playNotification(config);
        }
    }

    public void onSetTime() {
        this.watchAdapter.setTime();
    }

    public void onFindDevice(boolean start) {
        try {
            if (this.watchAdapter.supportsExtendedVibration()) {
                C1238GB.toast("Device does not support brr brr", 0, 1);
            }
        } catch (UnsupportedOperationException e) {
            notifiyException(e);
            C1238GB.toast("Please contact dakhnod@gmail.com\n", 0, 1);
        }
        if (!start || !this.searchDevice) {
            this.searchDevice = start;
            if (start) {
                new Thread(new Runnable() {
                    public void run() {
                        while (QHybridSupport.this.searchDevice) {
                            QHybridSupport.this.watchAdapter.vibrateFindMyDevicePattern();
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                C1238GB.log("error", 3, e);
                            }
                        }
                    }
                }).start();
            }
        }
    }

    public void onTestNewFunction() {
        this.watchAdapter.onTestNewFunction();
    }

    private void backupFile(DownloadFileRequest request) {
        try {
            File f = new File("/sdcard/qFiles/");
            if (!f.exists()) {
                f.mkdir();
            }
            File file = new File("/sdcard/qFiles/" + request.timeStamp);
            if (!file.exists()) {
                Logger logger2 = logger;
                logger2.debug("Writing file " + file.getPath());
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(request.file);
                fos.close();
                logger.debug("file written.");
                FileOutputStream fos2 = new FileOutputStream("/sdcard/qFiles/steps", true);
                fos2.write(("file " + request.timeStamp + " cut\n\n").getBytes());
                fos2.close();
                return;
            }
            throw new Exception("file " + file.getPath() + " exists");
        } catch (Exception e) {
            C1238GB.log("error", 3, e);
            int i = request.fileHandle;
        }
    }

    public void handleGBDeviceEvent(GBDeviceEventBatteryInfo deviceEvent) {
        super.handleGBDeviceEvent(deviceEvent);
    }

    public void notifiyException(Exception e) {
        Notification.Builder notificationBuilder;
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String sStackTrace = sw.toString();
        if (Build.VERSION.SDK_INT >= 26) {
            notificationBuilder = new Notification.Builder(getContext(), C1238GB.NOTIFICATION_CHANNEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(getContext());
        }
        notificationBuilder.setContentTitle("Q Error").setSmallIcon(C0889R.C0890drawable.ic_notification_qhybrid).setContentText(sStackTrace).setStyle(new Notification.BigTextStyle()).build();
        Intent emailIntent = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "dakhnod@gmail.com", (String) null));
        emailIntent.putExtra("android.intent.extra.SUBJECT", "Exception Report");
        emailIntent.putExtra("android.intent.extra.TEXT", "Here's a crash from your stupid app: \n\n" + sStackTrace);
        PendingIntent intent = PendingIntent.getActivity(getContext(), 0, emailIntent, 134217728);
        if (Build.VERSION.SDK_INT >= 20) {
            notificationBuilder.addAction(new Notification.Action(0, "report", intent));
        } else {
            notificationBuilder.addAction(0, "report", intent);
        }
        ((NotificationManager) getContext().getSystemService("notification")).notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onCharacteristicRead(android.bluetooth.BluetoothGatt r9, android.bluetooth.BluetoothGattCharacteristic r10, int r11) {
        /*
            r8 = this;
            java.lang.String r0 = "EXTENDED_VIBRATION"
            java.util.UUID r1 = r10.getUuid()
            java.lang.String r1 = r1.toString()
            int r2 = r1.hashCode()
            r3 = -892660755(0xffffffffcacb13ed, float:-6654454.5)
            r4 = 2
            r5 = 1
            r6 = 0
            if (r2 == r3) goto L_0x0035
            r3 = -51885817(0xfffffffffce84907, float:-9.6487523E36)
            if (r2 == r3) goto L_0x002b
            r3 = 1334317577(0x4f881209, float:4.5657667E9)
            if (r2 == r3) goto L_0x0021
        L_0x0020:
            goto L_0x003f
        L_0x0021:
            java.lang.String r2 = "00002a26-0000-1000-8000-00805f9b34fb"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0020
            r1 = 0
            goto L_0x0040
        L_0x002b:
            java.lang.String r2 = "00002a24-0000-1000-8000-00805f9b34fb"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0020
            r1 = 1
            goto L_0x0040
        L_0x0035:
            java.lang.String r2 = "00002a19-0000-1000-8000-00805f9b34fb"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0020
            r1 = 2
            goto L_0x0040
        L_0x003f:
            r1 = -1
        L_0x0040:
            if (r1 == 0) goto L_0x00c3
            if (r1 == r5) goto L_0x006e
            if (r1 == r4) goto L_0x0048
            goto L_0x00e0
        L_0x0048:
            byte[] r0 = r10.getValue()
            byte r0 = r0[r6]
            short r0 = (short) r0
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r8.gbDevice
            r1.setBatteryLevel(r0)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r8.gbDevice
            r1.setBatteryThresholdPercent(r4)
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo r1 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo
            r1.<init>()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice
            short r2 = r2.getBatteryLevel()
            r1.level = r2
            nodomain.freeyourgadget.gadgetbridge.model.BatteryState r2 = nodomain.freeyourgadget.gadgetbridge.model.BatteryState.BATTERY_NORMAL
            r1.state = r2
            r8.handleGBDeviceEvent(r1)
            goto L_0x00e0
        L_0x006e:
            java.lang.String r1 = r10.getStringValue(r6)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice
            r2.setModel(r1)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r3 = r8.watchAdapter
            java.lang.String r3 = r3.getModelName()
            r2.setName(r3)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice     // Catch:{ UnsupportedOperationException -> 0x00ad }
            nodomain.freeyourgadget.gadgetbridge.model.GenericItem r3 = new nodomain.freeyourgadget.gadgetbridge.model.GenericItem     // Catch:{ UnsupportedOperationException -> 0x00ad }
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r4 = r8.watchAdapter     // Catch:{ UnsupportedOperationException -> 0x00ad }
            boolean r4 = r4.supportsExtendedVibration()     // Catch:{ UnsupportedOperationException -> 0x00ad }
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ UnsupportedOperationException -> 0x00ad }
            r3.<init>(r0, r4)     // Catch:{ UnsupportedOperationException -> 0x00ad }
            r2.addDeviceInfo(r3)     // Catch:{ UnsupportedOperationException -> 0x00ad }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice     // Catch:{ UnsupportedOperationException -> 0x00ad }
            nodomain.freeyourgadget.gadgetbridge.model.GenericItem r3 = new nodomain.freeyourgadget.gadgetbridge.model.GenericItem     // Catch:{ UnsupportedOperationException -> 0x00ad }
            java.lang.String r4 = "HAS_ACTIVITY_HAND"
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r7 = r8.watchAdapter     // Catch:{ UnsupportedOperationException -> 0x00ad }
            boolean r7 = r7.supportsActivityHand()     // Catch:{ UnsupportedOperationException -> 0x00ad }
            java.lang.String r7 = java.lang.String.valueOf(r7)     // Catch:{ UnsupportedOperationException -> 0x00ad }
            r3.<init>(r4, r7)     // Catch:{ UnsupportedOperationException -> 0x00ad }
            r2.addDeviceInfo(r3)     // Catch:{ UnsupportedOperationException -> 0x00ad }
            goto L_0x00e0
        L_0x00ad:
            r2 = move-exception
            r8.notifiyException(r2)
            java.lang.String r3 = "Please contact dakhnod@gmail.com\n"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r3, r6, r5)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r8.gbDevice
            nodomain.freeyourgadget.gadgetbridge.model.GenericItem r4 = new nodomain.freeyourgadget.gadgetbridge.model.GenericItem
            java.lang.String r6 = "false"
            r4.<init>(r0, r6)
            r3.addDeviceInfo(r4)
            goto L_0x00e0
        L_0x00c3:
            java.lang.String r0 = r10.getStringValue(r6)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r8.gbDevice
            r1.setFirmwareVersion(r0)
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapterFactory r1 = new nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapterFactory
            r1.<init>()
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r1 = r1.createWatchAdapter(r0, r8)
            r8.watchAdapter = r1
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter r1 = r8.watchAdapter
            r1.initialize()
            r8.showNotificationsByAllActive(r6)
        L_0x00e0:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport.onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int):boolean");
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        WatchAdapter watchAdapter2 = this.watchAdapter;
        if (watchAdapter2 == null) {
            return super.onCharacteristicChanged(gatt, characteristic);
        }
        return watchAdapter2.onCharacteristicChanged(gatt, characteristic);
    }
}
