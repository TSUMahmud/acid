package nodomain.freeyourgadget.gadgetbridge.service.devices.zetime;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.ZeTimeActivitySample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.CoreConstants;

public class ZeTimeDeviceSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ZeTimeDeviceSupport.class);
    public BluetoothGattCharacteristic ackCharacteristic = null;
    private int availableHeartRateData;
    private int availableSleepData;
    private int availableStepsData;
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    private boolean callIncoming = false;
    private final int eightHourOffset = 28800;
    private byte[] lastMsg;
    private final int maxMsgLength = 20;
    private byte msgPart;
    public byte[] music = null;
    private final GBDeviceEventMusicControl musicCmd = new GBDeviceEventMusicControl();
    private byte musicState = -1;
    public BluetoothGattCharacteristic notifyCharacteristic = null;
    private int progressHeartRate;
    private int progressSleep;
    private int progressSteps;
    public byte[][] remindersOnWatch = ((byte[][]) Array.newInstance(byte.class, new int[]{3, 10}));
    public BluetoothGattCharacteristic replyCharacteristic = null;
    private String songtitle = null;
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();
    public byte volume = 50;
    public BluetoothGattCharacteristic writeCharacteristic = null;

    public ZeTimeDeviceSupport() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(ZeTimeConstants.UUID_SERVICE_BASE);
        addSupportedService(ZeTimeConstants.UUID_SERVICE_EXTEND);
        addSupportedService(ZeTimeConstants.UUID_SERVICE_HEART_RATE);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        LOG.info("Initializing");
        this.msgPart = 0;
        this.availableStepsData = 0;
        this.availableHeartRateData = 0;
        this.availableSleepData = 0;
        this.progressSteps = 0;
        this.progressSleep = 0;
        this.progressHeartRate = 0;
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        this.notifyCharacteristic = getCharacteristic(ZeTimeConstants.UUID_NOTIFY_CHARACTERISTIC);
        this.writeCharacteristic = getCharacteristic(ZeTimeConstants.UUID_WRITE_CHARACTERISTIC);
        this.ackCharacteristic = getCharacteristic(ZeTimeConstants.UUID_ACK_CHARACTERISTIC);
        this.replyCharacteristic = getCharacteristic(ZeTimeConstants.UUID_REPLY_CHARACTERISTIC);
        builder.notify(this.ackCharacteristic, true);
        builder.notify(this.notifyCharacteristic, true);
        requestDeviceInfo(builder);
        requestBatteryInfo(builder);
        setUserInfo(builder);
        setUserGoals(builder);
        setLanguage(builder);
        requestActivityInfo(builder);
        synchronizeTime(builder);
        initMusicVolume(builder);
        onReadReminders(builder);
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
        LOG.info("Initialization Done");
        return builder;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSendConfiguration(java.lang.String r7) {
        /*
            r6 = this;
            r0 = 3
            r1 = 1
            java.lang.String r2 = "sendConfiguration"
            nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r2 = r6.performInitialized(r2)     // Catch:{ IOException -> 0x022e }
            r3 = -1
            int r4 = r7.hashCode()     // Catch:{ IOException -> 0x022e }
            switch(r4) {
                case -1982823584: goto L_0x01e2;
                case -1948623009: goto L_0x01d7;
                case -1846932400: goto L_0x01cc;
                case -1540075208: goto L_0x01c1;
                case -1295323680: goto L_0x01b7;
                case -1253707803: goto L_0x01ac;
                case -1177133493: goto L_0x01a2;
                case -952807877: goto L_0x0197;
                case -927357762: goto L_0x018c;
                case -907133590: goto L_0x0181;
                case -524086473: goto L_0x0175;
                case -343874463: goto L_0x0169;
                case -213619953: goto L_0x015d;
                case -101369558: goto L_0x0152;
                case -26330437: goto L_0x0146;
                case 337728617: goto L_0x013b;
                case 371162511: goto L_0x012f;
                case 598378432: goto L_0x0123;
                case 676491080: goto L_0x0117;
                case 731851751: goto L_0x010b;
                case 750839116: goto L_0x00ff;
                case 753885858: goto L_0x00f3;
                case 943353506: goto L_0x00e8;
                case 1332624644: goto L_0x00dc;
                case 1349258321: goto L_0x00d0;
                case 1396251991: goto L_0x00c4;
                case 1410801908: goto L_0x00b8;
                case 1500205108: goto L_0x00ad;
                case 1568476760: goto L_0x00a1;
                case 1575858128: goto L_0x0095;
                case 1774840198: goto L_0x008a;
                case 1781475806: goto L_0x007e;
                case 1938792414: goto L_0x0072;
                case 1938792628: goto L_0x0066;
                case 1938792800: goto L_0x005a;
                case 1938792820: goto L_0x004e;
                case 1938792838: goto L_0x0042;
                case 1938792851: goto L_0x0036;
                case 1938792928: goto L_0x002a;
                case 2066963661: goto L_0x001e;
                case 2132403469: goto L_0x0012;
                default: goto L_0x0010;
            }     // Catch:{ IOException -> 0x022e }
        L_0x0010:
            goto L_0x01eb
        L_0x0012:
            java.lang.String r4 = "zetime_vibration_profile_incoming_call"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 24
            goto L_0x01eb
        L_0x001e:
            java.lang.String r4 = "mi_fitness_goal"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 36
            goto L_0x01eb
        L_0x002a:
            java.lang.String r4 = "zetime_prefs_inactivity_repetitions_we"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 18
            goto L_0x01eb
        L_0x0036:
            java.lang.String r4 = "zetime_prefs_inactivity_repetitions_tu"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 17
            goto L_0x01eb
        L_0x0042:
            java.lang.String r4 = "zetime_prefs_inactivity_repetitions_th"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 19
            goto L_0x01eb
        L_0x004e:
            java.lang.String r4 = "zetime_prefs_inactivity_repetitions_su"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 22
            goto L_0x01eb
        L_0x005a:
            java.lang.String r4 = "zetime_prefs_inactivity_repetitions_sa"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 21
            goto L_0x01eb
        L_0x0066:
            java.lang.String r4 = "zetime_prefs_inactivity_repetitions_mo"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 16
            goto L_0x01eb
        L_0x0072:
            java.lang.String r4 = "zetime_prefs_inactivity_repetitions_fr"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 20
            goto L_0x01eb
        L_0x007e:
            java.lang.String r4 = "zetime_inactivity_warnings_start"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 13
            goto L_0x01eb
        L_0x008a:
            java.lang.String r4 = "zetime_do_not_disturb"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 5
            goto L_0x01eb
        L_0x0095:
            java.lang.String r4 = "activity_user_calories_burnt"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 38
            goto L_0x01eb
        L_0x00a1:
            java.lang.String r4 = "zetime_inactivity_warning_key"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 11
            goto L_0x01eb
        L_0x00ad:
            java.lang.String r4 = "wearlocation"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 0
            goto L_0x01eb
        L_0x00b8:
            java.lang.String r4 = "alarm_min_heart_rate"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 35
            goto L_0x01eb
        L_0x00c4:
            java.lang.String r4 = "zetime_inactivity_warnings_end"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 14
            goto L_0x01eb
        L_0x00d0:
            java.lang.String r4 = "zetime_heartrate_alarm_enable"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 33
            goto L_0x01eb
        L_0x00dc:
            java.lang.String r4 = "timeformat"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 9
            goto L_0x01eb
        L_0x00e8:
            java.lang.String r4 = "zetime_do_not_disturb_end"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 7
            goto L_0x01eb
        L_0x00f3:
            java.lang.String r4 = "alarm_max_heart_rate"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 34
            goto L_0x01eb
        L_0x00ff:
            java.lang.String r4 = "zetime_calories_type"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 8
            goto L_0x01eb
        L_0x010b:
            java.lang.String r4 = "zetime_inactivity_warnings_threshold"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 15
            goto L_0x01eb
        L_0x0117:
            java.lang.String r4 = "zetime_vibration_profile_calendar"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 28
            goto L_0x01eb
        L_0x0123:
            java.lang.String r4 = "activity_user_sleep_duration"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 37
            goto L_0x01eb
        L_0x012f:
            java.lang.String r4 = "zetime_vibration_profile_antiloss"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 31
            goto L_0x01eb
        L_0x013b:
            java.lang.String r4 = "zetime_do_not_disturb_start"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 6
            goto L_0x01eb
        L_0x0146:
            java.lang.String r4 = "zetime_inactivity_warnings"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 12
            goto L_0x01eb
        L_0x0152:
            java.lang.String r4 = "zetime_handmove_display"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 4
            goto L_0x01eb
        L_0x015d:
            java.lang.String r4 = "zetime_vibration_profile_sms"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 23
            goto L_0x01eb
        L_0x0169:
            java.lang.String r4 = "zetime_date_format"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 10
            goto L_0x01eb
        L_0x0175:
            java.lang.String r4 = "activity_user_activetime_minutes"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 40
            goto L_0x01eb
        L_0x0181:
            java.lang.String r4 = "zetime_vibration_profile_generic_email"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 26
            goto L_0x01eb
        L_0x018c:
            java.lang.String r4 = "zetime_vibration_profile_inactivity"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 29
            goto L_0x01eb
        L_0x0197:
            java.lang.String r4 = "zetime_vibration_profile_lowpower"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 30
            goto L_0x01eb
        L_0x01a2:
            java.lang.String r4 = "zetime_analog_mode"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 2
            goto L_0x01eb
        L_0x01ac:
            java.lang.String r4 = "zetime_shock_strength"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 32
            goto L_0x01eb
        L_0x01b7:
            java.lang.String r4 = "zetime_activity_tracking"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 3
            goto L_0x01eb
        L_0x01c1:
            java.lang.String r4 = "zetime_vibration_profile_missed_call"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 25
            goto L_0x01eb
        L_0x01cc:
            java.lang.String r4 = "activity_user_distance_meters"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 39
            goto L_0x01eb
        L_0x01d7:
            java.lang.String r4 = "zetime_vibration_profile_generic_social"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 27
            goto L_0x01eb
        L_0x01e2:
            java.lang.String r4 = "zetime_screentime"
            boolean r4 = r7.equals(r4)     // Catch:{ IOException -> 0x022e }
            if (r4 == 0) goto L_0x0010
            r3 = 1
        L_0x01eb:
            switch(r3) {
                case 0: goto L_0x0222;
                case 1: goto L_0x021e;
                case 2: goto L_0x021a;
                case 3: goto L_0x0216;
                case 4: goto L_0x0212;
                case 5: goto L_0x020e;
                case 6: goto L_0x020e;
                case 7: goto L_0x020e;
                case 8: goto L_0x020a;
                case 9: goto L_0x0206;
                case 10: goto L_0x0202;
                case 11: goto L_0x01fe;
                case 12: goto L_0x01fe;
                case 13: goto L_0x01fe;
                case 14: goto L_0x01fe;
                case 15: goto L_0x01fe;
                case 16: goto L_0x01fe;
                case 17: goto L_0x01fe;
                case 18: goto L_0x01fe;
                case 19: goto L_0x01fe;
                case 20: goto L_0x01fe;
                case 21: goto L_0x01fe;
                case 22: goto L_0x01fe;
                case 23: goto L_0x01fa;
                case 24: goto L_0x01fa;
                case 25: goto L_0x01fa;
                case 26: goto L_0x01fa;
                case 27: goto L_0x01fa;
                case 28: goto L_0x01fa;
                case 29: goto L_0x01fa;
                case 30: goto L_0x01fa;
                case 31: goto L_0x01fa;
                case 32: goto L_0x01f3;
                case 33: goto L_0x01f6;
                case 34: goto L_0x01f6;
                case 35: goto L_0x01f6;
                case 36: goto L_0x01ef;
                case 37: goto L_0x01ef;
                case 38: goto L_0x01ef;
                case 39: goto L_0x01ef;
                case 40: goto L_0x01ef;
                default: goto L_0x01ee;
            }     // Catch:{ IOException -> 0x022e }
        L_0x01ee:
            goto L_0x0226
        L_0x01ef:
            r6.setUserGoals(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x01f3:
            r6.setShockStrength(r2)     // Catch:{ IOException -> 0x022e }
        L_0x01f6:
            r6.setHeartRateLimits(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x01fa:
            r6.setSignaling(r2, r7)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x01fe:
            r6.setInactivityAlert(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x0202:
            r6.setDateFormate(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x0206:
            r6.setTimeFormate(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x020a:
            r6.setCaloriesType(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x020e:
            r6.setDoNotDisturb(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x0212:
            r6.setDisplayOnMovement(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x0216:
            r6.setActivityTracking(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x021a:
            r6.setAnalogMode(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x021e:
            r6.setScreenTime(r2)     // Catch:{ IOException -> 0x022e }
            goto L_0x0226
        L_0x0222:
            r6.setWrist(r2)     // Catch:{ IOException -> 0x022e }
        L_0x0226:
            nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue r3 = r6.getQueue()     // Catch:{ IOException -> 0x022e }
            r2.queue(r3)     // Catch:{ IOException -> 0x022e }
            goto L_0x024b
        L_0x022e:
            r2 = move-exception
            android.content.Context r3 = r6.getContext()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Error sending configuration: "
            r4.append(r5)
            java.lang.String r5 = r2.getLocalizedMessage()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r3, (java.lang.String) r4, (int) r1, (int) r0)
        L_0x024b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.zetime.ZeTimeDeviceSupport.onSendConfiguration(java.lang.String):void");
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    public void onHeartRateTest() {
    }

    public void onEnableRealtimeSteps(boolean enable) {
    }

    public void onFindDevice(boolean start) {
        try {
            TransactionBuilder builder = performInitialized("onFindDevice");
            byte[] testSignaling = new byte[7];
            int i = 0;
            testSignaling[0] = ZeTimeConstants.CMD_PREAMBLE;
            testSignaling[1] = -6;
            testSignaling[2] = 113;
            testSignaling[3] = 1;
            testSignaling[4] = 0;
            if (start) {
                i = 1;
            }
            testSignaling[5] = (byte) i;
            testSignaling[6] = ZeTimeConstants.CMD_END;
            sendMsgToWatch(builder, testSignaling);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error on function onFindDevice: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public boolean useAutoConnect() {
        return false;
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
        byte[] heartrate = {ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_AUTO_HEARTRATE, 113, 1, 0, (byte) (seconds / 60), ZeTimeConstants.CMD_END};
        try {
            TransactionBuilder builder = performInitialized("enableAutoHeartRate");
            sendMsgToWatch(builder, heartrate);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error enable auto heart rate measurement: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        byte[] alarmMessage;
        try {
            TransactionBuilder builder = performInitialized("setAlarms");
            Prefs prefs = GBApplication.getPrefs();
            Iterator<? extends Alarm> it = alarms.iterator();
            while (it.hasNext()) {
                Alarm alarm = (Alarm) it.next();
                if (this.remindersOnWatch[alarm.getPosition()][0] == 0) {
                    byte[] bArr = new byte[17];
                    bArr[0] = ZeTimeConstants.CMD_PREAMBLE;
                    bArr[1] = ZeTimeConstants.CMD_REMINDERS;
                    bArr[2] = 113;
                    bArr[3] = 11;
                    bArr[4] = 0;
                    bArr[5] = 0;
                    bArr[6] = 4;
                    bArr[7] = 0;
                    bArr[8] = 0;
                    bArr[9] = 0;
                    bArr[10] = 0;
                    bArr[11] = (byte) AlarmUtils.toCalendar(alarm).get(11);
                    bArr[12] = (byte) AlarmUtils.toCalendar(alarm).get(12);
                    bArr[13] = (byte) alarm.getRepetition();
                    bArr[14] = (byte) (alarm.getEnabled() ? 1 : 0);
                    bArr[15] = (byte) prefs.getInt(ZeTimeConstants.PREF_ALARM_SIGNALING, 11);
                    bArr[16] = ZeTimeConstants.CMD_END;
                    alarmMessage = bArr;
                    System.arraycopy(alarmMessage, 6, this.remindersOnWatch[alarm.getPosition()], 0, 10);
                } else {
                    byte[] bArr2 = new byte[27];
                    bArr2[0] = ZeTimeConstants.CMD_PREAMBLE;
                    bArr2[1] = ZeTimeConstants.CMD_REMINDERS;
                    bArr2[2] = 113;
                    bArr2[3] = 21;
                    bArr2[4] = 0;
                    bArr2[5] = 1;
                    bArr2[6] = this.remindersOnWatch[alarm.getPosition()][0];
                    bArr2[7] = this.remindersOnWatch[alarm.getPosition()][1];
                    bArr2[8] = this.remindersOnWatch[alarm.getPosition()][2];
                    bArr2[9] = this.remindersOnWatch[alarm.getPosition()][3];
                    bArr2[10] = this.remindersOnWatch[alarm.getPosition()][4];
                    bArr2[11] = this.remindersOnWatch[alarm.getPosition()][5];
                    bArr2[12] = this.remindersOnWatch[alarm.getPosition()][6];
                    bArr2[13] = this.remindersOnWatch[alarm.getPosition()][7];
                    bArr2[14] = this.remindersOnWatch[alarm.getPosition()][8];
                    bArr2[15] = this.remindersOnWatch[alarm.getPosition()][9];
                    bArr2[16] = 4;
                    bArr2[17] = 0;
                    bArr2[18] = 0;
                    bArr2[19] = 0;
                    bArr2[20] = 0;
                    bArr2[21] = (byte) AlarmUtils.toCalendar(alarm).get(11);
                    bArr2[22] = (byte) AlarmUtils.toCalendar(alarm).get(12);
                    bArr2[23] = (byte) alarm.getRepetition();
                    bArr2[24] = (byte) (alarm.getEnabled() ? 1 : 0);
                    bArr2[25] = (byte) prefs.getInt(ZeTimeConstants.PREF_ALARM_SIGNALING, 11);
                    bArr2[26] = ZeTimeConstants.CMD_END;
                    alarmMessage = bArr2;
                    System.arraycopy(alarmMessage, 16, this.remindersOnWatch[alarm.getPosition()], 0, 10);
                }
                sendMsgToWatch(builder, alarmMessage);
            }
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error set alarms: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onAppConfiguration(UUID appUuid, String config, Integer id) {
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        this.songtitle = musicSpec.track;
        if (this.musicState != -1) {
            this.music = new byte[(this.songtitle.getBytes(StandardCharsets.UTF_8).length + 7)];
            byte[] bArr = this.music;
            bArr[0] = ZeTimeConstants.CMD_PREAMBLE;
            bArr[1] = -48;
            bArr[2] = Byte.MIN_VALUE;
            bArr[3] = (byte) ((this.songtitle.getBytes(StandardCharsets.UTF_8).length + 1) & 255);
            this.music[4] = (byte) ((this.songtitle.getBytes(StandardCharsets.UTF_8).length + 1) >> 8);
            this.music[5] = this.musicState;
            System.arraycopy(this.songtitle.getBytes(StandardCharsets.UTF_8), 0, this.music, 6, this.songtitle.getBytes(StandardCharsets.UTF_8).length);
            byte[] bArr2 = this.music;
            bArr2[bArr2.length - 1] = ZeTimeConstants.CMD_END;
            try {
                TransactionBuilder builder = performInitialized("setMusicStateInfo");
                replyMsgToWatch(builder, this.music);
                builder.queue(getQueue());
            } catch (IOException e) {
                Context context = getContext();
                C1238GB.toast(context, "Error setting music state and info: " + e.getLocalizedMessage(), 1, 3);
            }
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        int notification_length;
        byte[] notification;
        int notification_length2;
        CallSpec callSpec2 = callSpec;
        int subject_length = 0;
        int notification_length3 = 0;
        byte[] subject = null;
        Calendar time = GregorianCalendar.getInstance();
        byte[] datetimeBytes = {(byte) ((time.get(1) / 1000) + 48), (byte) (((time.get(1) / 100) % 10) + 48), (byte) (((time.get(1) / 10) % 10) + 48), (byte) ((time.get(1) % 10) + 48), (byte) (((time.get(2) + 1) / 10) + 48), (byte) (((time.get(2) + 1) % 10) + 48), (byte) ((time.get(5) / 10) + 48), (byte) ((time.get(5) % 10) + 48), ZeTimeConstants.CMD_GET_STEP_COUNT, (byte) ((time.get(11) / 10) + 48), (byte) ((time.get(11) % 10) + 48), (byte) ((time.get(12) / 10) + 48), (byte) ((time.get(12) % 10) + 48), (byte) ((time.get(13) / 10) + 48), (byte) ((time.get(13) % 10) + 48)};
        if (this.callIncoming || callSpec2.command == 2) {
            if (callSpec2.command == 2) {
                if (callSpec2.name != null) {
                    notification_length3 = 0 + callSpec2.name.getBytes(StandardCharsets.UTF_8).length;
                    subject_length = callSpec2.name.getBytes(StandardCharsets.UTF_8).length;
                    subject = new byte[subject_length];
                    System.arraycopy(callSpec2.name.getBytes(StandardCharsets.UTF_8), 0, subject, 0, subject_length);
                } else if (callSpec2.number != null) {
                    notification_length3 = 0 + callSpec2.number.getBytes(StandardCharsets.UTF_8).length;
                    subject_length = callSpec2.number.getBytes(StandardCharsets.UTF_8).length;
                    subject = new byte[subject_length];
                    System.arraycopy(callSpec2.number.getBytes(StandardCharsets.UTF_8), 0, subject, 0, subject_length);
                }
                int notification_length4 = notification_length3 + datetimeBytes.length + 10;
                notification = new byte[notification_length4];
                notification[0] = ZeTimeConstants.CMD_PREAMBLE;
                notification[1] = 118;
                notification[2] = 113;
                notification[3] = (byte) ((notification_length4 - 6) & 255);
                notification[4] = (byte) ((notification_length4 - 6) >> 8);
                notification[5] = 5;
                notification[6] = 1;
                notification[7] = (byte) subject_length;
                notification[8] = 0;
                System.arraycopy(subject, 0, notification, 9, subject_length);
                System.arraycopy(datetimeBytes, 0, notification, subject_length + 9, datetimeBytes.length);
                notification[notification_length4 - 1] = ZeTimeConstants.CMD_END;
                this.callIncoming = true;
                notification_length = notification_length4;
                notification_length2 = subject_length;
            } else {
                notification_length = datetimeBytes.length + 10;
                byte[] notification2 = new byte[notification_length];
                notification2[0] = ZeTimeConstants.CMD_PREAMBLE;
                notification2[1] = 118;
                notification2[2] = 113;
                notification2[3] = (byte) ((notification_length - 6) & 255);
                notification2[4] = (byte) ((notification_length - 6) >> 8);
                notification2[5] = 6;
                notification2[6] = 1;
                notification2[7] = 0;
                notification2[8] = 0;
                System.arraycopy(datetimeBytes, 0, notification2, 9, datetimeBytes.length);
                notification2[notification_length - 1] = ZeTimeConstants.CMD_END;
                this.callIncoming = false;
                notification = notification2;
                notification_length2 = 0;
            }
            try {
                TransactionBuilder builder = performInitialized("setCallState");
                sendMsgToWatch(builder, notification);
                builder.queue(getQueue());
            } catch (IOException e) {
                C1238GB.toast(getContext(), "Error set call state: " + e.getLocalizedMessage(), 1, 3);
            }
            int i = notification_length2;
            int subject_length2 = notification_length;
        }
    }

    public void onAppStart(UUID uuid, boolean start) {
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
    }

    public void onSetConstantVibration(int integer) {
    }

    public void onFetchRecordedData(int dataTypes) {
        try {
            TransactionBuilder builder = performInitialized("fetchActivityData");
            requestActivityInfo(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error on fetching activity data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onTestNewFunction() {
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
        this.musicState = stateSpec.state;
        String str = this.songtitle;
        if (str != null) {
            this.music = new byte[(str.getBytes(StandardCharsets.UTF_8).length + 7)];
            byte[] bArr = this.music;
            bArr[0] = ZeTimeConstants.CMD_PREAMBLE;
            bArr[1] = -48;
            bArr[2] = Byte.MIN_VALUE;
            bArr[3] = (byte) ((this.songtitle.getBytes(StandardCharsets.UTF_8).length + 1) & 255);
            this.music[4] = (byte) ((this.songtitle.getBytes(StandardCharsets.UTF_8).length + 1) >> 8);
            if (stateSpec.state == 0) {
                this.music[5] = 0;
            } else {
                this.music[5] = 1;
            }
            System.arraycopy(this.songtitle.getBytes(StandardCharsets.UTF_8), 0, this.music, 6, this.songtitle.getBytes(StandardCharsets.UTF_8).length);
            byte[] bArr2 = this.music;
            bArr2[bArr2.length - 1] = ZeTimeConstants.CMD_END;
            try {
                TransactionBuilder builder = performInitialized("setMusicStateInfo");
                replyMsgToWatch(builder, this.music);
                builder.queue(getQueue());
            } catch (IOException e) {
                Context context = getContext();
                C1238GB.toast(context, "Error setting music state and info: " + e.getLocalizedMessage(), 1, 3);
            }
        }
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        Calendar time = GregorianCalendar.getInstance();
        byte[] CalendarEvent = new byte[(calendarEventSpec.title.getBytes(StandardCharsets.UTF_8).length + 16)];
        time.setTimeInMillis((long) calendarEventSpec.timestamp);
        CalendarEvent[0] = ZeTimeConstants.CMD_PREAMBLE;
        CalendarEvent[1] = ZeTimeConstants.CMD_PUSH_CALENDAR_DAY;
        CalendarEvent[2] = 113;
        CalendarEvent[3] = (byte) ((calendarEventSpec.title.getBytes(StandardCharsets.UTF_8).length + 10) & 255);
        CalendarEvent[4] = (byte) ((calendarEventSpec.title.getBytes(StandardCharsets.UTF_8).length + 10) >> 8);
        CalendarEvent[5] = (byte) (calendarEventSpec.type + 1);
        CalendarEvent[6] = (byte) (time.get(1) & 255);
        CalendarEvent[7] = (byte) (time.get(1) >> 8);
        CalendarEvent[8] = (byte) (time.get(2) + 1);
        CalendarEvent[9] = (byte) time.get(5);
        CalendarEvent[10] = (byte) (time.get(11) & 255);
        CalendarEvent[11] = (byte) (time.get(11) >> 8);
        CalendarEvent[12] = (byte) (time.get(12) & 255);
        CalendarEvent[13] = (byte) (time.get(12) >> 8);
        CalendarEvent[14] = (byte) calendarEventSpec.title.getBytes(StandardCharsets.UTF_8).length;
        System.arraycopy(calendarEventSpec.title.getBytes(StandardCharsets.UTF_8), 0, CalendarEvent, 15, calendarEventSpec.title.getBytes(StandardCharsets.UTF_8).length);
        CalendarEvent[CalendarEvent.length - 1] = ZeTimeConstants.CMD_END;
        try {
            TransactionBuilder builder = performInitialized("sendCalendarEvenr");
            sendMsgToWatch(builder, CalendarEvent);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error sending calendar event: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("synchronizeTime");
            synchronizeTime(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error setting the time: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onAppDelete(UUID uuid) {
    }

    public void onAppInfoReq() {
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    public void onReset(int flags) {
    }

    public void onScreenshotReq() {
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
        String buildnumber = this.versionCmd.fwVersion.substring(this.versionCmd.fwVersion.length() - 4);
        byte[] weather = new byte[(weatherSpec.location.getBytes(StandardCharsets.UTF_8).length + 26)];
        weather[0] = ZeTimeConstants.CMD_PREAMBLE;
        weather[1] = 119;
        weather[2] = 113;
        weather[3] = (byte) ((weatherSpec.location.getBytes(StandardCharsets.UTF_8).length + 20) & 255);
        weather[4] = (byte) ((weatherSpec.location.getBytes(StandardCharsets.UTF_8).length + 20) >> 8);
        weather[5] = 0;
        weather[6] = (byte) (weatherSpec.currentTemp - 273);
        weather[7] = (byte) (weatherSpec.todayMinTemp - 273);
        weather[8] = (byte) (weatherSpec.todayMaxTemp - 273);
        if (buildnumber.compareTo("B4.1") >= 0) {
            weather[9] = Weather.mapToZeTimeCondition(weatherSpec.currentConditionCode);
        } else {
            weather[9] = Weather.mapToZeTimeConditionOld(weatherSpec.currentConditionCode);
        }
        for (int forecast = 0; forecast < 3; forecast++) {
            weather[(forecast * 5) + 10] = 0;
            weather[(forecast * 5) + 11] = -1;
            weather[(forecast * 5) + 12] = (byte) (weatherSpec.forecasts.get(forecast).minTemp - 273);
            weather[(forecast * 5) + 13] = (byte) (weatherSpec.forecasts.get(forecast).maxTemp - 273);
            weather[(forecast * 5) + 14] = Weather.mapToZeTimeCondition(weatherSpec.forecasts.get(forecast).conditionCode);
        }
        System.arraycopy(weatherSpec.location.getBytes(StandardCharsets.UTF_8), 0, weather, 25, weatherSpec.location.getBytes(StandardCharsets.UTF_8).length);
        weather[weather.length - 1] = ZeTimeConstants.CMD_END;
        try {
            TransactionBuilder builder = performInitialized("sendWeahter");
            sendMsgToWatch(builder, weather);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error sending weather: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onAppReorder(UUID[] uuids) {
    }

    public void onInstallApp(Uri uri) {
    }

    public void onDeleteNotification(int id) {
    }

    public void onNotification(NotificationSpec notificationSpec) {
        byte[] subject;
        int subject_length;
        NotificationSpec notificationSpec2 = notificationSpec;
        int body_length = notificationSpec2.body.getBytes(StandardCharsets.UTF_8).length;
        if (body_length > 256) {
            body_length = 256;
        }
        int notification_length = body_length;
        Calendar time = GregorianCalendar.getInstance();
        byte[] datetimeBytes = {(byte) ((time.get(1) / 1000) + 48), (byte) (((time.get(1) / 100) % 10) + 48), (byte) (((time.get(1) / 10) % 10) + 48), (byte) ((time.get(1) % 10) + 48), (byte) (((time.get(2) + 1) / 10) + 48), (byte) (((time.get(2) + 1) % 10) + 48), (byte) ((time.get(5) / 10) + 48), (byte) ((time.get(5) % 10) + 48), ZeTimeConstants.CMD_GET_STEP_COUNT, (byte) ((time.get(11) / 10) + 48), (byte) ((time.get(11) % 10) + 48), (byte) ((time.get(12) / 10) + 48), (byte) ((time.get(12) % 10) + 48), (byte) ((time.get(13) / 10) + 48), (byte) ((time.get(13) % 10) + 48)};
        if (notificationSpec2.sender != null) {
            notification_length += notificationSpec2.sender.getBytes(StandardCharsets.UTF_8).length;
            int subject_length2 = notificationSpec2.sender.getBytes(StandardCharsets.UTF_8).length;
            byte[] subject2 = new byte[subject_length2];
            System.arraycopy(notificationSpec2.sender.getBytes(StandardCharsets.UTF_8), 0, subject2, 0, subject_length2);
            subject = subject2;
            subject_length = subject_length2;
        } else if (notificationSpec2.phoneNumber != null) {
            notification_length += notificationSpec2.phoneNumber.getBytes(StandardCharsets.UTF_8).length;
            int subject_length3 = notificationSpec2.phoneNumber.getBytes(StandardCharsets.UTF_8).length;
            byte[] subject3 = new byte[subject_length3];
            System.arraycopy(notificationSpec2.phoneNumber.getBytes(StandardCharsets.UTF_8), 0, subject3, 0, subject_length3);
            subject = subject3;
            subject_length = subject_length3;
        } else if (notificationSpec2.subject != null) {
            notification_length += notificationSpec2.subject.getBytes(StandardCharsets.UTF_8).length;
            int subject_length4 = notificationSpec2.subject.getBytes(StandardCharsets.UTF_8).length;
            byte[] subject4 = new byte[subject_length4];
            System.arraycopy(notificationSpec2.subject.getBytes(StandardCharsets.UTF_8), 0, subject4, 0, subject_length4);
            subject = subject4;
            subject_length = subject_length4;
        } else if (notificationSpec2.title != null) {
            notification_length += notificationSpec2.title.getBytes(StandardCharsets.UTF_8).length;
            int subject_length5 = notificationSpec2.title.getBytes(StandardCharsets.UTF_8).length;
            byte[] subject5 = new byte[subject_length5];
            System.arraycopy(notificationSpec2.title.getBytes(StandardCharsets.UTF_8), 0, subject5, 0, subject_length5);
            subject = subject5;
            subject_length = subject_length5;
        } else {
            subject = null;
            subject_length = 0;
        }
        int notification_length2 = notification_length + datetimeBytes.length + 10;
        byte[] notification = new byte[notification_length2];
        notification[0] = ZeTimeConstants.CMD_PREAMBLE;
        notification[1] = 118;
        notification[2] = 113;
        notification[3] = (byte) ((notification_length2 - 6) & 255);
        notification[4] = (byte) ((notification_length2 - 6) >> 8);
        notification[6] = 1;
        notification[7] = (byte) subject_length;
        notification[8] = (byte) body_length;
        System.arraycopy(subject, 0, notification, 9, subject_length);
        System.arraycopy(notificationSpec2.body.getBytes(StandardCharsets.UTF_8), 0, notification, subject_length + 9, body_length);
        System.arraycopy(datetimeBytes, 0, notification, subject_length + 9 + body_length, datetimeBytes.length);
        notification[notification_length2 - 1] = ZeTimeConstants.CMD_END;
        switch (notificationSpec2.type) {
            case GENERIC_SMS:
                notification[5] = 1;
                break;
            case GENERIC_PHONE:
                notification[5] = 0;
                break;
            case GMAIL:
            case GOOGLE_INBOX:
            case MAILBOX:
            case OUTLOOK:
            case YAHOO_MAIL:
            case GENERIC_EMAIL:
                notification[5] = 3;
                break;
            case WECHAT:
                notification[5] = 7;
                break;
            case VIBER:
                notification[5] = 8;
                break;
            case WHATSAPP:
                notification[5] = 10;
                break;
            case FACEBOOK:
            case FACEBOOK_MESSENGER:
                notification[5] = 12;
                break;
            case GOOGLE_HANGOUTS:
                notification[5] = 13;
                break;
            case LINE:
                notification[5] = 20;
                break;
            case SKYPE:
                notification[5] = 21;
                break;
            case CONVERSATIONS:
            case RIOT:
            case SIGNAL:
            case TELEGRAM:
            case THREEMA:
            case KONTALK:
            case ANTOX:
            case GOOGLE_MESSENGER:
            case HIPCHAT:
            case KIK:
            case KAKAO_TALK:
            case SLACK:
                notification[5] = 15;
                break;
            case SNAPCHAT:
                notification[5] = 9;
                break;
            case INSTAGRAM:
                notification[5] = 16;
                break;
            case TWITTER:
                notification[5] = 17;
                break;
            case LINKEDIN:
                notification[5] = 18;
                break;
            case GENERIC_CALENDAR:
                notification[5] = 4;
                break;
            default:
                notification[5] = 2;
                break;
        }
        try {
            TransactionBuilder builder = performInitialized("sendNotification");
            sendMsgToWatch(builder, notification);
            builder.queue(getQueue());
        } catch (IOException e) {
            C1238GB.toast(getContext(), "Error sending notification: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        UUID characteristicUUID = characteristic.getUuid();
        if (ZeTimeConstants.UUID_ACK_CHARACTERISTIC.equals(characteristicUUID)) {
            byte[] data = receiveCompleteMsg(characteristic.getValue());
            if (isMsgFormatOK(data)) {
                switch (data[1]) {
                    case -112:
                        getDisplayOnMovement(data);
                        break;
                    case -105:
                        storeActualReminders(data);
                        break;
                    case -48:
                        handleMusicControl(data);
                        break;
                    case 3:
                        handleDeviceInfo(data);
                        break;
                    case 5:
                        getDateTimeFormat(data);
                        break;
                    case 8:
                        handleBatteryInfo(data);
                        break;
                    case 10:
                        getSignaling(data);
                        break;
                    case 21:
                        getDoNotDisturb(data);
                        break;
                    case 23:
                        getAnalogMode(data);
                        break;
                    case 26:
                        getActivityTracking(data);
                        break;
                    case 37:
                        getScreenTime(data);
                        break;
                    case 49:
                        getWrist(data);
                        break;
                    case 82:
                        handleActivityFetching(data);
                        break;
                    case 84:
                        handleStepsData(data);
                        break;
                    case 86:
                        handleSleepData(data);
                        break;
                    case 92:
                        getHeartRateMeasurement(data);
                        break;
                    case 93:
                        getHeartRateLimits(data);
                        break;
                    case 94:
                        getInactivityAlert(data);
                        break;
                    case 96:
                        getCaloriesType(data);
                        break;
                    case 97:
                        handleHeartRateData(data);
                        break;
                }
            }
            return true;
        } else if (ZeTimeConstants.UUID_NOTIFY_CHARACTERISTIC.equals(characteristicUUID)) {
            byte[] data2 = receiveCompleteMsg(characteristic.getValue());
            if (!isMsgFormatOK(data2)) {
                return false;
            }
            if (data2[1] == -48) {
                handleMusicControl(data2);
            }
            return true;
        } else {
            Logger logger = LOG;
            logger.info("Unhandled characteristic changed: " + characteristicUUID);
            logMessageContent(characteristic.getValue());
            return false;
        }
    }

    private boolean isMsgFormatOK(byte[] msg) {
        if (!(msg == null || msg[0] != 111 || (msg[3] == 0 && msg[4] == 0))) {
            int msgLength = ((msg[3] & 255) | ((msg[4] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) + 6;
            if (msgLength == msg.length && msg[msgLength - 1] == -113) {
                return true;
            }
            return false;
        }
        return false;
    }

    private byte[] receiveCompleteMsg(byte[] msg) {
        if (this.msgPart != 0) {
            byte[] bArr = this.lastMsg;
            byte[] completeMsg = new byte[(bArr.length + msg.length)];
            System.arraycopy(bArr, 0, completeMsg, 0, bArr.length);
            System.arraycopy(msg, 0, completeMsg, this.lastMsg.length, msg.length);
            this.msgPart = 0;
            return completeMsg;
        } else if ((((msg[4] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (msg[3] & 255)) <= 14) {
            return msg;
        } else {
            this.lastMsg = new byte[msg.length];
            System.arraycopy(msg, 0, this.lastMsg, 0, msg.length);
            this.msgPart = (byte) (this.msgPart + 1);
            return null;
        }
    }

    private ZeTimeDeviceSupport requestBatteryInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Battery Info!");
        builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 8, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END});
        builder.write(this.ackCharacteristic, new byte[]{3});
        return this;
    }

    private ZeTimeDeviceSupport requestDeviceInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Device Info!");
        builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 2, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END});
        builder.write(this.ackCharacteristic, new byte[]{3});
        builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 3, ZeTimeConstants.CMD_REQUEST, 1, 0, 5, ZeTimeConstants.CMD_END});
        builder.write(this.ackCharacteristic, new byte[]{3});
        builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 3, ZeTimeConstants.CMD_REQUEST, 1, 0, 2, ZeTimeConstants.CMD_END});
        builder.write(this.ackCharacteristic, new byte[]{3});
        return this;
    }

    private ZeTimeDeviceSupport requestActivityInfo(TransactionBuilder builder) {
        builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 82, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END});
        builder.write(this.ackCharacteristic, new byte[]{3});
        return this;
    }

    private ZeTimeDeviceSupport requestShockStrength(TransactionBuilder builder) {
        builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 16, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END});
        builder.write(this.ackCharacteristic, new byte[]{3});
        return this;
    }

    private void handleBatteryInfo(byte[] value) {
        GBDeviceEventBatteryInfo gBDeviceEventBatteryInfo = this.batteryCmd;
        gBDeviceEventBatteryInfo.level = (short) value[5];
        if (gBDeviceEventBatteryInfo.level <= 25) {
            this.batteryCmd.state = BatteryState.BATTERY_LOW;
        } else {
            this.batteryCmd.state = BatteryState.BATTERY_NORMAL;
        }
        evaluateGBDeviceEvent(this.batteryCmd);
    }

    private void handleDeviceInfo(byte[] value) {
        value[value.length - 1] = 0;
        byte[] string = Arrays.copyOfRange(value, 6, value.length - 1);
        if (value[5] == 5) {
            this.versionCmd.fwVersion = new String(string);
        } else {
            this.versionCmd.hwVersion = new String(string);
        }
        evaluateGBDeviceEvent(this.versionCmd);
    }

    private void handleActivityFetching(byte[] msg) {
        this.availableStepsData = (msg[5] & 255) | ((msg[6] << 8) & 65280);
        this.availableSleepData = (msg[7] & 255) | ((msg[8] << 8) & 65280);
        this.availableHeartRateData = (msg[9] & 255) | ((msg[10] << 8) & 65280);
        if (this.availableStepsData > 0) {
            getStepData();
        } else if (this.availableHeartRateData > 0) {
            getHeartRateData();
        } else if (this.availableSleepData > 0) {
            getSleepData();
        }
    }

    private void getStepData() {
        try {
            TransactionBuilder builder = performInitialized("fetchStepData");
            builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_GET_STEP_COUNT, ZeTimeConstants.CMD_REQUEST, 2, 0, 0, 0, ZeTimeConstants.CMD_END});
            builder.write(this.ackCharacteristic, new byte[]{3});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error fetching activity data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void deleteStepData() {
        try {
            TransactionBuilder builder = performInitialized("deleteStepData");
            sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_DELETE_STEP_COUNT, 113, 1, 0, 0, ZeTimeConstants.CMD_END});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error deleting activity data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void getHeartRateData() {
        try {
            TransactionBuilder builder = performInitialized("fetchHeartRateData");
            builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_GET_HEARTRATE_EXDATA, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END});
            builder.write(this.ackCharacteristic, new byte[]{3});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error fetching heart rate data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void deleteHeartRateData() {
        try {
            TransactionBuilder builder = performInitialized("deleteHeartRateData");
            sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 90, 113, 1, 0, 0, ZeTimeConstants.CMD_END});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error deleting heart rate data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void getSleepData() {
        try {
            TransactionBuilder builder = performInitialized("fetchSleepData");
            builder.write(this.writeCharacteristic, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_GET_SLEEP_DATA, ZeTimeConstants.CMD_REQUEST, 2, 0, 0, 0, ZeTimeConstants.CMD_END});
            builder.write(this.ackCharacteristic, new byte[]{3});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error fetching sleep data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void deleteSleepData() {
        try {
            TransactionBuilder builder = performInitialized("deleteSleepData");
            sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 113, 1, 0, 0, ZeTimeConstants.CMD_END});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error deleting sleep data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0115, code lost:
        r10 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0116, code lost:
        if (r7 != null) goto L_0x0118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0120, code lost:
        throw r10;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleStepsData(byte[] r13) {
        /*
            r12 = this;
            nodomain.freeyourgadget.gadgetbridge.entities.ZeTimeActivitySample r0 = new nodomain.freeyourgadget.gadgetbridge.entities.ZeTimeActivitySample
            r0.<init>()
            java.util.Calendar r1 = java.util.GregorianCalendar.getInstance()
            r2 = 10
            byte r2 = r13[r2]
            r3 = 24
            int r2 = r2 << r3
            r4 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r2 = r2 & r4
            r5 = 9
            byte r5 = r13[r5]
            r6 = 16
            int r5 = r5 << r6
            r7 = 16711680(0xff0000, float:2.3418052E-38)
            r5 = r5 & r7
            r2 = r2 | r5
            r5 = 8
            byte r8 = r13[r5]
            int r8 = r8 << r5
            r9 = 65280(0xff00, float:9.1477E-41)
            r8 = r8 & r9
            r2 = r2 | r8
            r8 = 7
            byte r8 = r13[r8]
            r8 = r8 & 255(0xff, float:3.57E-43)
            r2 = r2 | r8
            int r2 = r2 + 28800
            r8 = 15
            int r10 = r1.get(r8)
            int r10 = r10 / 1000
            int r11 = r1.get(r6)
            int r11 = r11 / 1000
            int r10 = r10 + r11
            int r2 = r2 - r10
            r0.setTimestamp(r2)
            r10 = 14
            byte r10 = r13[r10]
            int r10 = r10 << r3
            r10 = r10 & r4
            r11 = 13
            byte r11 = r13[r11]
            int r11 = r11 << r6
            r11 = r11 & r7
            r10 = r10 | r11
            r11 = 12
            byte r11 = r13[r11]
            int r11 = r11 << r5
            r11 = r11 & r9
            r10 = r10 | r11
            r11 = 11
            byte r11 = r13[r11]
            r11 = r11 & 255(0xff, float:3.57E-43)
            r10 = r10 | r11
            r0.setSteps(r10)
            r10 = 18
            byte r10 = r13[r10]
            int r10 = r10 << r3
            r10 = r10 & r4
            r11 = 17
            byte r11 = r13[r11]
            int r11 = r11 << r6
            r11 = r11 & r7
            r10 = r10 | r11
            byte r11 = r13[r6]
            int r11 = r11 << r5
            r11 = r11 & r9
            r10 = r10 | r11
            byte r8 = r13[r8]
            r8 = r8 & 255(0xff, float:3.57E-43)
            r8 = r8 | r10
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r0.setCaloriesBurnt(r8)
            r8 = 22
            byte r8 = r13[r8]
            int r8 = r8 << r3
            r8 = r8 & r4
            r10 = 21
            byte r10 = r13[r10]
            int r10 = r10 << r6
            r10 = r10 & r7
            r8 = r8 | r10
            r10 = 20
            byte r10 = r13[r10]
            int r10 = r10 << r5
            r10 = r10 & r9
            r8 = r8 | r10
            r10 = 19
            byte r10 = r13[r10]
            r10 = r10 & 255(0xff, float:3.57E-43)
            r8 = r8 | r10
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r0.setDistanceMeters(r8)
            r8 = 26
            byte r8 = r13[r8]
            int r8 = r8 << r3
            r4 = r4 & r8
            r8 = 25
            byte r8 = r13[r8]
            int r6 = r8 << 16
            r6 = r6 & r7
            r4 = r4 | r6
            byte r3 = r13[r3]
            int r3 = r3 << r5
            r3 = r3 & r9
            r3 = r3 | r4
            r4 = 23
            byte r4 = r13[r4]
            r4 = r4 & 255(0xff, float:3.57E-43)
            r3 = r3 | r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.setActiveTimeMinutes(r3)
            r3 = 1
            r0.setRawKind(r3)
            int r4 = r0.getSteps()
            r0.setRawIntensity(r4)
            r4 = 0
            r6 = 0
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r7 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0121 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r8 = r7.getDaoSession()     // Catch:{ all -> 0x0113 }
            nodomain.freeyourgadget.gadgetbridge.entities.User r8 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r8)     // Catch:{ all -> 0x0113 }
            java.lang.Long r8 = r8.getId()     // Catch:{ all -> 0x0113 }
            long r10 = r8.longValue()     // Catch:{ all -> 0x0113 }
            r0.setUserId(r10)     // Catch:{ all -> 0x0113 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r8 = r12.getDevice()     // Catch:{ all -> 0x0113 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r10 = r7.getDaoSession()     // Catch:{ all -> 0x0113 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r8 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r8, r10)     // Catch:{ all -> 0x0113 }
            java.lang.Long r8 = r8.getId()     // Catch:{ all -> 0x0113 }
            long r10 = r8.longValue()     // Catch:{ all -> 0x0113 }
            r0.setDeviceId(r10)     // Catch:{ all -> 0x0113 }
            nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeSampleProvider r8 = new nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeSampleProvider     // Catch:{ all -> 0x0113 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r10 = r12.getDevice()     // Catch:{ all -> 0x0113 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r11 = r7.getDaoSession()     // Catch:{ all -> 0x0113 }
            r8.<init>(r10, r11)     // Catch:{ all -> 0x0113 }
            r8.addGBActivitySample(r0)     // Catch:{ all -> 0x0113 }
            if (r7 == 0) goto L_0x0112
            r7.close()     // Catch:{ Exception -> 0x0121 }
        L_0x0112:
            goto L_0x0148
        L_0x0113:
            r8 = move-exception
            throw r8     // Catch:{ all -> 0x0115 }
        L_0x0115:
            r10 = move-exception
            if (r7 == 0) goto L_0x0120
            r7.close()     // Catch:{ all -> 0x011c }
            goto L_0x0120
        L_0x011c:
            r11 = move-exception
            r8.addSuppressed(r11)     // Catch:{ Exception -> 0x0121 }
        L_0x0120:
            throw r10     // Catch:{ Exception -> 0x0121 }
        L_0x0121:
            r7 = move-exception
            android.content.Context r8 = r12.getContext()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Error saving steps data: "
            r10.append(r11)
            java.lang.String r11 = r7.getLocalizedMessage()
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r11 = 3
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r8, (java.lang.String) r10, (int) r3, (int) r11)
            android.content.Context r8 = r12.getContext()
            java.lang.String r10 = "Data transfer failed"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateTransferNotification(r4, r10, r6, r6, r8)
        L_0x0148:
            r7 = 5
            byte r7 = r13[r7]
            r7 = r7 & 255(0xff, float:3.57E-43)
            r8 = 6
            byte r8 = r13[r8]
            int r5 = r8 << 8
            r5 = r5 & r9
            r5 = r5 | r7
            r12.progressSteps = r5
            android.content.Context r5 = r12.getContext()
            r7 = 2131755144(0x7f100088, float:1.9141159E38)
            java.lang.String r5 = r5.getString(r7)
            int r7 = r12.progressSteps
            r8 = 100
            int r7 = r7 * 100
            int r9 = r12.availableStepsData
            int r7 = r7 / r9
            android.content.Context r9 = r12.getContext()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateTransferNotification(r4, r5, r3, r7, r9)
            int r3 = r12.progressSteps
            int r5 = r12.availableStepsData
            if (r3 != r5) goto L_0x01be
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            r12.progressSteps = r6
            r12.availableStepsData = r6
            android.content.Context r5 = r12.getContext()
            java.lang.String r7 = ""
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateTransferNotification(r4, r7, r6, r8, r5)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r12.getDevice()
            boolean r4 = r4.isBusy()
            if (r4 == 0) goto L_0x01a4
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r12.getDevice()
            r4.unsetBusyTask()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r12.getDevice()
            android.content.Context r5 = r12.getContext()
            r4.sendDeviceUpdateIntent(r5)
        L_0x01a4:
            java.lang.String r4 = "zetime_dont_del_actdata"
            boolean r4 = r3.getBoolean(r4, r6)
            if (r4 != 0) goto L_0x01af
            r12.deleteStepData()
        L_0x01af:
            int r4 = r12.availableHeartRateData
            if (r4 <= 0) goto L_0x01b7
            r12.getHeartRateData()
            goto L_0x01be
        L_0x01b7:
            int r4 = r12.availableSleepData
            if (r4 <= 0) goto L_0x01be
            r12.getSleepData()
        L_0x01be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.zetime.ZeTimeDeviceSupport.handleStepsData(byte[]):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00a2, code lost:
        r10 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00a3, code lost:
        if (r5 != null) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00ad, code lost:
        throw r10;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleSleepData(byte[] r13) {
        /*
            r12 = this;
            nodomain.freeyourgadget.gadgetbridge.entities.ZeTimeActivitySample r0 = new nodomain.freeyourgadget.gadgetbridge.entities.ZeTimeActivitySample
            r0.<init>()
            java.util.Calendar r1 = java.util.GregorianCalendar.getInstance()
            r2 = 10
            byte r2 = r13[r2]
            int r2 = r2 << 24
            r3 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r2 = r2 & r3
            r3 = 9
            byte r3 = r13[r3]
            r4 = 16
            int r3 = r3 << r4
            r5 = 16711680(0xff0000, float:2.3418052E-38)
            r3 = r3 & r5
            r2 = r2 | r3
            r3 = 8
            byte r5 = r13[r3]
            int r5 = r5 << r3
            r6 = 65280(0xff00, float:9.1477E-41)
            r5 = r5 & r6
            r2 = r2 | r5
            r5 = 7
            byte r5 = r13[r5]
            r5 = r5 & 255(0xff, float:3.57E-43)
            r2 = r2 | r5
            int r2 = r2 + 28800
            r5 = 15
            int r5 = r1.get(r5)
            int r5 = r5 / 1000
            int r4 = r1.get(r4)
            int r4 = r4 / 1000
            int r5 = r5 + r4
            int r2 = r2 - r5
            r0.setTimestamp(r2)
            r4 = 11
            byte r5 = r13[r4]
            r7 = 1
            r8 = 0
            if (r5 != 0) goto L_0x004f
            r4 = 4
            r0.setRawKind(r4)
            goto L_0x005b
        L_0x004f:
            byte r4 = r13[r4]
            if (r4 != r7) goto L_0x0058
            r4 = 2
            r0.setRawKind(r4)
            goto L_0x005b
        L_0x0058:
            r0.setRawKind(r8)
        L_0x005b:
            r4 = 0
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r5 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00ae }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r9 = r5.getDaoSession()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.User r9 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r9)     // Catch:{ all -> 0x00a0 }
            java.lang.Long r9 = r9.getId()     // Catch:{ all -> 0x00a0 }
            long r9 = r9.longValue()     // Catch:{ all -> 0x00a0 }
            r0.setUserId(r9)     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r9 = r12.getDevice()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r10 = r5.getDaoSession()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r9 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r9, r10)     // Catch:{ all -> 0x00a0 }
            java.lang.Long r9 = r9.getId()     // Catch:{ all -> 0x00a0 }
            long r9 = r9.longValue()     // Catch:{ all -> 0x00a0 }
            r0.setDeviceId(r9)     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeSampleProvider r9 = new nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeSampleProvider     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r10 = r12.getDevice()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r11 = r5.getDaoSession()     // Catch:{ all -> 0x00a0 }
            r9.<init>(r10, r11)     // Catch:{ all -> 0x00a0 }
            r9.addGBActivitySample(r0)     // Catch:{ all -> 0x00a0 }
            if (r5 == 0) goto L_0x009f
            r5.close()     // Catch:{ Exception -> 0x00ae }
        L_0x009f:
            goto L_0x00d5
        L_0x00a0:
            r9 = move-exception
            throw r9     // Catch:{ all -> 0x00a2 }
        L_0x00a2:
            r10 = move-exception
            if (r5 == 0) goto L_0x00ad
            r5.close()     // Catch:{ all -> 0x00a9 }
            goto L_0x00ad
        L_0x00a9:
            r11 = move-exception
            r9.addSuppressed(r11)     // Catch:{ Exception -> 0x00ae }
        L_0x00ad:
            throw r10     // Catch:{ Exception -> 0x00ae }
        L_0x00ae:
            r5 = move-exception
            android.content.Context r9 = r12.getContext()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Error saving steps data: "
            r10.append(r11)
            java.lang.String r11 = r5.getLocalizedMessage()
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r11 = 3
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r9, (java.lang.String) r10, (int) r7, (int) r11)
            android.content.Context r9 = r12.getContext()
            java.lang.String r10 = "Data transfer failed"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateTransferNotification(r4, r10, r8, r8, r9)
        L_0x00d5:
            r5 = 5
            byte r5 = r13[r5]
            r5 = r5 & 255(0xff, float:3.57E-43)
            r9 = 6
            byte r9 = r13[r9]
            int r3 = r9 << 8
            r3 = r3 & r6
            r3 = r3 | r5
            r12.progressSleep = r3
            android.content.Context r3 = r12.getContext()
            r5 = 2131755144(0x7f100088, float:1.9141159E38)
            java.lang.String r3 = r3.getString(r5)
            int r5 = r12.progressSleep
            r6 = 100
            int r5 = r5 * 100
            int r9 = r12.availableSleepData
            int r5 = r5 / r9
            android.content.Context r9 = r12.getContext()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateTransferNotification(r4, r3, r7, r5, r9)
            int r3 = r12.progressSleep
            int r5 = r12.availableSleepData
            if (r3 != r5) goto L_0x0134
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            r12.progressSleep = r8
            r12.availableSleepData = r8
            android.content.Context r5 = r12.getContext()
            java.lang.String r7 = ""
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateTransferNotification(r4, r7, r8, r6, r5)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r12.getDevice()
            boolean r4 = r4.isBusy()
            if (r4 == 0) goto L_0x0129
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r12.getDevice()
            r4.unsetBusyTask()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.signalActivityDataFinish()
        L_0x0129:
            java.lang.String r4 = "zetime_dont_del_actdata"
            boolean r4 = r3.getBoolean(r4, r8)
            if (r4 != 0) goto L_0x0134
            r12.deleteSleepData()
        L_0x0134:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.zetime.ZeTimeDeviceSupport.handleSleepData(byte[]):void");
    }

    private void handleHeartRateData(byte[] msg) {
        Throwable th;
        Throwable th2;
        ZeTimeActivitySample sample = new ZeTimeActivitySample();
        Calendar now = GregorianCalendar.getInstance();
        int timestamp = ((((((msg[10] << 24) & ViewCompat.MEASURED_STATE_MASK) | ((msg[9] << 16) & 16711680)) | ((msg[8] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | (msg[7] & 255)) + 28800) - ((now.get(15) / 1000) + (now.get(16) / 1000));
        sample.setHeartRate(msg[11]);
        sample.setTimestamp(timestamp);
        try {
            DBHandler dbHandler = GBApplication.acquireDB();
            try {
                sample.setUserId(DBHelper.getUser(dbHandler.getDaoSession()).getId().longValue());
                sample.setDeviceId(DBHelper.getDevice(getDevice(), dbHandler.getDaoSession()).getId().longValue());
                new ZeTimeSampleProvider(getDevice(), dbHandler.getDaoSession()).addGBActivitySample(sample);
                if (dbHandler != null) {
                    dbHandler.close();
                }
            } catch (Throwable th3) {
                Throwable th4 = th3;
                if (dbHandler != null) {
                    dbHandler.close();
                }
                throw th4;
            }
        } catch (Exception ex) {
            Context context = getContext();
            C1238GB.toast(context, "Error saving steps data: " + ex.getLocalizedMessage(), 1, 3);
            C1238GB.updateTransferNotification((String) null, "Data transfer failed", false, 0, getContext());
        } catch (Throwable th5) {
            th2.addSuppressed(th5);
        }
        this.progressHeartRate = (msg[5] & 255) | ((msg[6] << 8) & 65280);
        C1238GB.updateTransferNotification((String) null, getContext().getString(C0889R.string.busy_task_fetch_activity_data), true, (this.progressHeartRate * 100) / this.availableHeartRateData, getContext());
        if ((((msg[4] << 8) & 65280) | (msg[3] & 255)) == 14) {
            int timestamp2 = ((((((msg[17] << 24) & ViewCompat.MEASURED_STATE_MASK) | (16711680 & (msg[16] << 16))) | ((msg[15] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | (msg[14] & 255)) + 28800) - ((now.get(15) / 1000) + (now.get(16) / 1000));
            sample.setHeartRate(msg[18]);
            sample.setTimestamp(timestamp2);
            try {
                DBHandler dbHandler2 = GBApplication.acquireDB();
                try {
                    sample.setUserId(DBHelper.getUser(dbHandler2.getDaoSession()).getId().longValue());
                    sample.setDeviceId(DBHelper.getDevice(getDevice(), dbHandler2.getDaoSession()).getId().longValue());
                    new ZeTimeSampleProvider(getDevice(), dbHandler2.getDaoSession()).addGBActivitySample(sample);
                    if (dbHandler2 != null) {
                        dbHandler2.close();
                    }
                    this.progressHeartRate = (msg[12] & 255) | ((msg[13] << 8) & 65280);
                    C1238GB.updateTransferNotification((String) null, getContext().getString(C0889R.string.busy_task_fetch_activity_data), true, (this.progressHeartRate * 100) / this.availableHeartRateData, getContext());
                } catch (Throwable th6) {
                    Throwable th7 = th6;
                    if (dbHandler2 != null) {
                        dbHandler2.close();
                    }
                    throw th7;
                }
            } catch (Exception ex2) {
                Context context2 = getContext();
                C1238GB.toast(context2, "Error saving steps data: " + ex2.getLocalizedMessage(), 1, 3);
                C1238GB.updateTransferNotification((String) null, "Data transfer failed", false, 0, getContext());
            } catch (Throwable th8) {
                th.addSuppressed(th8);
            }
        }
        if (this.progressHeartRate == this.availableHeartRateData) {
            Prefs prefs = GBApplication.getPrefs();
            this.progressHeartRate = 0;
            this.availableHeartRateData = 0;
            C1238GB.updateTransferNotification((String) null, "", false, 100, getContext());
            if (getDevice().isBusy()) {
                getDevice().unsetBusyTask();
                getDevice().sendDeviceUpdateIntent(getContext());
            }
            if (!prefs.getBoolean(ZeTimeConstants.PREF_ZETIME_DONT_DEL_ACTDATA, false)) {
                deleteHeartRateData();
            }
            if (this.availableSleepData > 0) {
                getSleepData();
            }
        }
    }

    private void sendMsgToWatch(TransactionBuilder builder, byte[] msg) {
        byte[] msgpart;
        if (msg.length > 20) {
            int msgpartlength = 0;
            do {
                if (msg.length - msgpartlength < 20) {
                    msgpart = new byte[(msg.length - msgpartlength)];
                    System.arraycopy(msg, msgpartlength, msgpart, 0, msg.length - msgpartlength);
                    msgpartlength += msg.length - msgpartlength;
                } else {
                    msgpart = new byte[20];
                    System.arraycopy(msg, msgpartlength, msgpart, 0, 20);
                    msgpartlength += 20;
                }
                builder.write(this.writeCharacteristic, msgpart);
            } while (msgpartlength < msg.length);
        } else {
            builder.write(this.writeCharacteristic, msg);
        }
        builder.write(this.ackCharacteristic, new byte[]{3});
    }

    private void handleMusicControl(byte[] musicControlMsg) {
        if (musicControlMsg[2] == 113) {
            byte b = musicControlMsg[5];
            if (b == 0) {
                this.musicCmd.event = GBDeviceEventMusicControl.Event.PLAY;
            } else if (b == 1) {
                this.musicCmd.event = GBDeviceEventMusicControl.Event.PAUSE;
            } else if (b == 2) {
                this.musicCmd.event = GBDeviceEventMusicControl.Event.PREVIOUS;
            } else if (b == 3) {
                this.musicCmd.event = GBDeviceEventMusicControl.Event.NEXT;
            } else if (b == 4) {
                if (musicControlMsg[6] > this.volume) {
                    this.musicCmd.event = GBDeviceEventMusicControl.Event.VOLUMEUP;
                    byte b2 = this.volume;
                    if (b2 < 90) {
                        this.volume = (byte) (b2 + 10);
                    }
                } else {
                    this.musicCmd.event = GBDeviceEventMusicControl.Event.VOLUMEDOWN;
                    byte b3 = this.volume;
                    if (b3 > 10) {
                        this.volume = (byte) (b3 - 10);
                    }
                }
                try {
                    TransactionBuilder builder = performInitialized("replyMusicVolume");
                    replyMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, -48, Byte.MIN_VALUE, 2, 0, 2, this.volume, ZeTimeConstants.CMD_END});
                    builder.queue(getQueue());
                } catch (IOException e) {
                    Context context = getContext();
                    C1238GB.toast(context, "Error reply the music volume: " + e.getLocalizedMessage(), 1, 3);
                }
            }
            evaluateGBDeviceEvent(this.musicCmd);
            return;
        }
        byte[] bArr = this.music;
        if (bArr != null) {
            bArr[2] = Byte.MIN_VALUE;
            try {
                TransactionBuilder builder2 = performInitialized("replyMusicState");
                replyMsgToWatch(builder2, this.music);
                builder2.queue(getQueue());
            } catch (IOException e2) {
                Context context2 = getContext();
                C1238GB.toast(context2, "Error reply the music state: " + e2.getLocalizedMessage(), 1, 3);
            }
        }
    }

    private void replyMsgToWatch(TransactionBuilder builder, byte[] msg) {
        byte[] msgpart;
        if (msg.length > 20) {
            int msgpartlength = 0;
            do {
                if (msg.length - msgpartlength < 20) {
                    msgpart = new byte[(msg.length - msgpartlength)];
                    System.arraycopy(msg, msgpartlength, msgpart, 0, msg.length - msgpartlength);
                    msgpartlength += msg.length - msgpartlength;
                } else {
                    msgpart = new byte[20];
                    System.arraycopy(msg, msgpartlength, msgpart, 0, 20);
                    msgpartlength += 20;
                }
                builder.write(this.replyCharacteristic, msgpart);
            } while (msgpartlength < msg.length);
            return;
        }
        builder.write(this.replyCharacteristic, msg);
    }

    private void synchronizeTime(TransactionBuilder builder) {
        Calendar now = GregorianCalendar.getInstance();
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 4, 113, 12, 0, (byte) (now.get(1) & 255), (byte) (now.get(1) >> 8), (byte) (now.get(2) + 1), (byte) now.get(5), (byte) now.get(11), (byte) now.get(12), (byte) now.get(13), 0, 0, 1, (byte) ((now.get(15) / CoreConstants.MILLIS_IN_ONE_HOUR) + (now.get(16) / CoreConstants.MILLIS_IN_ONE_HOUR)), 0, ZeTimeConstants.CMD_END});
    }

    private void setWrist(TransactionBuilder builder) {
        String value = GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).getString(DeviceSettingsPreferenceConst.PREF_WEARLOCATION, "left");
        byte[] wrist = {ZeTimeConstants.CMD_PREAMBLE, 49, 113, 1, 0, 0, ZeTimeConstants.CMD_END};
        if ("right".equals(value)) {
            wrist[5] = 1;
        }
        Logger logger = LOG;
        logger.warn("Wrist: " + wrist[5]);
        sendMsgToWatch(builder, wrist);
    }

    private void setScreenTime(TransactionBuilder builder) {
        int value = GBApplication.getPrefs().getInt(ZeTimeConstants.PREF_SCREENTIME, 30);
        if (value > 65535) {
            C1238GB.toast(getContext(), "Value for screen on time is greater than 18h! ", 1, 3);
            value = 65535;
        } else if (value < 10) {
            C1238GB.toast(getContext(), "Value for screen on time is lesser than 10s! ", 1, 3);
            value = 10;
        }
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 37, 113, 2, 0, (byte) (value & 255), (byte) (value >> 8), ZeTimeConstants.CMD_END});
    }

    private void setUserInfo(TransactionBuilder builder) {
        byte gender;
        ActivityUser activityUser = new ActivityUser();
        byte gender2 = (byte) activityUser.getGender();
        int age = activityUser.getAge();
        int height = activityUser.getHeightCm();
        int weight = activityUser.getWeightKg() * 10;
        if (gender2 == 1) {
            gender = 0;
        } else if (gender2 == 0) {
            gender = 1;
        } else {
            gender = 2;
        }
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_USER_INFO, 113, 5, 0, gender, (byte) age, (byte) height, (byte) (weight & 255), (byte) (weight >> 8), ZeTimeConstants.CMD_END});
    }

    private void setUserGoals(TransactionBuilder builder) {
        TransactionBuilder transactionBuilder = builder;
        ActivityUser activityUser = new ActivityUser();
        int steps = activityUser.getStepsGoal() / 100;
        int calories = activityUser.getCaloriesBurnt();
        int distance = activityUser.getDistanceMeters() / 1000;
        int sleep = activityUser.getSleepDuration();
        int activeTime = activityUser.getActiveTimeMinutes();
        byte[] goal_steps = {ZeTimeConstants.CMD_PREAMBLE, 80, 113, 4, 0, 0, (byte) (steps & 255), (byte) (steps >> 8), 1, ZeTimeConstants.CMD_END};
        sendMsgToWatch(transactionBuilder, goal_steps);
        byte[] goal_calories = new byte[goal_steps.length];
        System.arraycopy(goal_steps, 0, goal_calories, 0, goal_steps.length);
        goal_calories[5] = 1;
        goal_calories[6] = (byte) (calories & 255);
        goal_calories[7] = (byte) (calories >> 8);
        sendMsgToWatch(transactionBuilder, goal_calories);
        byte[] goal_distance = new byte[goal_steps.length];
        System.arraycopy(goal_steps, 0, goal_distance, 0, goal_steps.length);
        goal_distance[5] = 2;
        goal_distance[6] = (byte) (distance & 255);
        goal_distance[7] = (byte) (distance >> 8);
        sendMsgToWatch(transactionBuilder, goal_distance);
        byte[] goal_sleep = new byte[goal_steps.length];
        System.arraycopy(goal_steps, 0, goal_sleep, 0, goal_steps.length);
        goal_sleep[5] = 3;
        goal_sleep[6] = (byte) (sleep & 255);
        goal_sleep[7] = (byte) (sleep >> 8);
        sendMsgToWatch(transactionBuilder, goal_sleep);
        byte[] goal_activeTime = new byte[goal_steps.length];
        System.arraycopy(goal_steps, 0, goal_activeTime, 0, goal_steps.length);
        goal_activeTime[5] = 4;
        goal_activeTime[6] = (byte) (activeTime & 255);
        goal_activeTime[7] = (byte) (activeTime >> 8);
        sendMsgToWatch(transactionBuilder, goal_activeTime);
    }

    private void setHeartRateLimits(TransactionBuilder builder) {
        Prefs prefs = GBApplication.getPrefs();
        boolean alarmEnabled = prefs.getBoolean(ZeTimeConstants.PREF_ZETIME_HEARTRATE_ALARM, false);
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_HEARTRATE_ALARM_LIMITS, 113, 3, 0, (byte) (prefs.getInt(ZeTimeConstants.PREF_ZETIME_MAX_HEARTRATE, 180) & 255), (byte) (prefs.getInt(ZeTimeConstants.PREF_ZETIME_MIN_HEARTRATE, 60) & 255), alarmEnabled ? (byte) 1 : 0, ZeTimeConstants.CMD_END});
    }

    private void initMusicVolume(TransactionBuilder builder) {
        replyMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, -48, Byte.MIN_VALUE, 2, 0, 2, this.volume, ZeTimeConstants.CMD_END});
    }

    private void setAnalogMode(TransactionBuilder builder) {
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 23, 113, 1, 0, (byte) GBApplication.getPrefs().getInt(ZeTimeConstants.PREF_ANALOG_MODE, 0), ZeTimeConstants.CMD_END});
    }

    private void setActivityTracking(TransactionBuilder builder) {
        boolean tracking = GBApplication.getPrefs().getBoolean(ZeTimeConstants.PREF_ACTIVITY_TRACKING, false);
        byte[] activity = {ZeTimeConstants.CMD_PREAMBLE, 26, 113, 1, 0, 9, ZeTimeConstants.CMD_END};
        if (tracking) {
            activity[5] = 10;
        }
        sendMsgToWatch(builder, activity);
    }

    private void setDisplayOnMovement(TransactionBuilder builder) {
        boolean movement = GBApplication.getPrefs().getBoolean(ZeTimeConstants.PREF_HANDMOVE_DISPLAY, false);
        byte[] handmove = {ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_SWITCH_SETTINGS, 113, 3, 0, 1, 14, 0, ZeTimeConstants.CMD_END};
        if (movement) {
            handmove[7] = 1;
        }
        sendMsgToWatch(builder, handmove);
    }

    private void setDoNotDisturb(TransactionBuilder builder) {
        Prefs prefs = GBApplication.getPrefs();
        String scheduled = prefs.getString(ZeTimeConstants.PREF_DO_NOT_DISTURB, "off");
        String dndScheduled = getContext().getString(C0889R.string.p_scheduled);
        String start = prefs.getString(ZeTimeConstants.PREF_DO_NOT_DISTURB_START, "22:00");
        String end = prefs.getString(ZeTimeConstants.PREF_DO_NOT_DISTURB_END, "07:00");
        DateFormat df_start = new SimpleDateFormat("HH:mm");
        DateFormat df_end = new SimpleDateFormat("HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        Calendar calendar_end = GregorianCalendar.getInstance();
        try {
            calendar.setTime(df_start.parse(start));
            try {
                calendar_end.setTime(df_end.parse(end));
                byte[] doNotDisturb = {ZeTimeConstants.CMD_PREAMBLE, 21, 113, 5, 0, 0, (byte) calendar.get(11), (byte) calendar.get(12), (byte) calendar_end.get(11), (byte) calendar_end.get(12), ZeTimeConstants.CMD_END};
                if (scheduled.equals(dndScheduled)) {
                    doNotDisturb[5] = 1;
                }
                try {
                    sendMsgToWatch(builder, doNotDisturb);
                } catch (Exception e) {
                    e = e;
                }
            } catch (Exception e2) {
                e = e2;
                TransactionBuilder transactionBuilder = builder;
                try {
                    Logger logger = LOG;
                    logger.error("Unexpected exception in ZeTimeDeviceSupport.setDoNotDisturb: " + e.getMessage());
                } catch (Exception e3) {
                    e = e3;
                }
            }
        } catch (Exception e4) {
            e = e4;
            TransactionBuilder transactionBuilder2 = builder;
            Logger logger2 = LOG;
            logger2.error("Unexpected exception in ZeTimeDeviceSupport.setDoNotDisturb: " + e.getMessage());
        }
    }

    private void setCaloriesType(TransactionBuilder builder) {
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_CALORIES_TYPE, 113, 1, 0, (byte) GBApplication.getPrefs().getInt(ZeTimeConstants.PREF_CALORIES_TYPE, 0), ZeTimeConstants.CMD_END});
    }

    private void setTimeFormate(TransactionBuilder builder) {
        int type = 1;
        if ("am/pm".equals(new GBPrefs(new Prefs(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()))).getTimeFormat())) {
            type = 2;
        }
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 5, 113, 8, 0, -1, (byte) type, -1, -1, -1, -1, -1, -1, ZeTimeConstants.CMD_END});
    }

    private void setDateFormate(TransactionBuilder builder) {
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 5, 113, 8, 0, (byte) GBApplication.getPrefs().getInt(ZeTimeConstants.PREF_DATE_FORMAT, 0), -1, -1, -1, -1, -1, -1, -1, ZeTimeConstants.CMD_END});
    }

    private void setInactivityAlert(TransactionBuilder builder) {
        Prefs prefs = GBApplication.getPrefs();
        boolean enabled = prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_ENABLE, false);
        int threshold = prefs.getInt(ZeTimeConstants.PREF_INACTIVITY_THRESHOLD, 60);
        if (threshold > 255) {
            threshold = 255;
            C1238GB.toast(getContext(), "Value for inactivity threshold is greater than 255min! ", 1, 3);
        }
        byte[] inactivity = {ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_INACTIVITY_ALERT, 113, 8, 0, 0, (byte) threshold, 0, 0, 0, 0, 100, 0, ZeTimeConstants.CMD_END};
        if (enabled) {
            String start = prefs.getString(ZeTimeConstants.PREF_INACTIVITY_START, "06:00");
            String end = prefs.getString(ZeTimeConstants.PREF_INACTIVITY_END, "22:00");
            DateFormat df_start = new SimpleDateFormat("HH:mm");
            DateFormat df_end = new SimpleDateFormat("HH:mm");
            Calendar calendar = GregorianCalendar.getInstance();
            Calendar calendar_end = GregorianCalendar.getInstance();
            inactivity[5] = (byte) (((prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_TU, false) ? 1 : 0) << true) | 128 | prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_MO, false) | ((prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_WE, false) ? 1 : 0) << true) | ((prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_TH, false) ? 1 : 0) << true) | ((prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_FR, false) ? 1 : 0) << true) | ((prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_SA, false) ? 1 : 0) << true) | ((prefs.getBoolean(ZeTimeConstants.PREF_INACTIVITY_SU, false) ? 1 : 0) << true));
            try {
                calendar.setTime(df_start.parse(start));
                try {
                    calendar_end.setTime(df_end.parse(end));
                    inactivity[7] = (byte) calendar.get(11);
                    inactivity[8] = (byte) calendar.get(12);
                    inactivity[9] = (byte) calendar_end.get(11);
                    inactivity[10] = (byte) calendar_end.get(12);
                } catch (Exception e) {
                    Logger logger = LOG;
                    logger.error("Unexpected exception in ZeTimeDeviceSupport.setInactivityAlert: " + e.getMessage());
                }
            } catch (Exception e2) {
                Logger logger2 = LOG;
                logger2.error("Unexpected exception in ZeTimeDeviceSupport.setInactivityAlert: " + e2.getMessage());
            }
        }
        sendMsgToWatch(builder, inactivity);
    }

    private void setShockStrength(TransactionBuilder builder) {
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, 16, 113, 1, 0, (byte) GBApplication.getPrefs().getInt(ZeTimeConstants.PREF_SHOCK_STRENGTH, 255), ZeTimeConstants.CMD_END});
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x003b, code lost:
        if (r15.equals(nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants.PREF_CALL_SIGNALING) != false) goto L_0x0090;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSignaling(nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r14, java.lang.String r15) {
        /*
            r13 = this;
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            r1 = 0
            int r2 = r0.getInt(r15, r1)
            r3 = 8
            byte[] r4 = new byte[r3]
            r5 = 111(0x6f, float:1.56E-43)
            r4[r1] = r5
            r5 = 1
            r6 = 10
            r4[r5] = r6
            r6 = 2
            r7 = 113(0x71, float:1.58E-43)
            r4[r6] = r7
            r7 = 3
            r4[r7] = r6
            r8 = 4
            r4[r8] = r1
            r9 = 5
            r4[r9] = r1
            byte r10 = (byte) r2
            r11 = 6
            r4[r11] = r10
            r10 = 7
            r12 = -113(0xffffffffffffff8f, float:NaN)
            r4[r10] = r12
            int r12 = r15.hashCode()
            switch(r12) {
                case -1948623009: goto L_0x0085;
                case -1540075208: goto L_0x007b;
                case -952807877: goto L_0x0071;
                case -927357762: goto L_0x0067;
                case -907133590: goto L_0x005d;
                case -213619953: goto L_0x0053;
                case 371162511: goto L_0x0048;
                case 676491080: goto L_0x003e;
                case 2132403469: goto L_0x0035;
                default: goto L_0x0034;
            }
        L_0x0034:
            goto L_0x008f
        L_0x0035:
            java.lang.String r12 = "zetime_vibration_profile_incoming_call"
            boolean r12 = r15.equals(r12)
            if (r12 == 0) goto L_0x0034
            goto L_0x0090
        L_0x003e:
            java.lang.String r5 = "zetime_vibration_profile_calendar"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 5
            goto L_0x0090
        L_0x0048:
            java.lang.String r5 = "zetime_vibration_profile_antiloss"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 8
            goto L_0x0090
        L_0x0053:
            java.lang.String r5 = "zetime_vibration_profile_sms"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 0
            goto L_0x0090
        L_0x005d:
            java.lang.String r5 = "zetime_vibration_profile_generic_email"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 3
            goto L_0x0090
        L_0x0067:
            java.lang.String r5 = "zetime_vibration_profile_inactivity"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 6
            goto L_0x0090
        L_0x0071:
            java.lang.String r5 = "zetime_vibration_profile_lowpower"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 7
            goto L_0x0090
        L_0x007b:
            java.lang.String r5 = "zetime_vibration_profile_missed_call"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 2
            goto L_0x0090
        L_0x0085:
            java.lang.String r5 = "zetime_vibration_profile_generic_social"
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x0034
            r5 = 4
            goto L_0x0090
        L_0x008f:
            r5 = -1
        L_0x0090:
            switch(r5) {
                case 0: goto L_0x00ae;
                case 1: goto L_0x00ab;
                case 2: goto L_0x00a8;
                case 3: goto L_0x00a5;
                case 4: goto L_0x00a2;
                case 5: goto L_0x009f;
                case 6: goto L_0x009c;
                case 7: goto L_0x0097;
                case 8: goto L_0x0094;
                default: goto L_0x0093;
            }
        L_0x0093:
            goto L_0x00b1
        L_0x0094:
            r4[r9] = r1
            goto L_0x00b1
        L_0x0097:
            r1 = 9
            r4[r9] = r1
            goto L_0x00b1
        L_0x009c:
            r4[r9] = r3
            goto L_0x00b1
        L_0x009f:
            r4[r9] = r10
            goto L_0x00b1
        L_0x00a2:
            r4[r9] = r9
            goto L_0x00b1
        L_0x00a5:
            r4[r9] = r11
            goto L_0x00b1
        L_0x00a8:
            r4[r9] = r7
            goto L_0x00b1
        L_0x00ab:
            r4[r9] = r6
            goto L_0x00b1
        L_0x00ae:
            r4[r9] = r8
        L_0x00b1:
            r13.sendMsgToWatch(r14, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.zetime.ZeTimeDeviceSupport.setSignaling(nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder, java.lang.String):void");
    }

    public void onReadConfiguration(String config) {
        try {
            TransactionBuilder builder = performInitialized("readConfiguration");
            if (!getDevice().isBusy()) {
                getDevice().setBusyTask("readConfiguration");
            }
            byte[] configRead1 = {ZeTimeConstants.CMD_PREAMBLE, 5, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END};
            sendMsgToWatch(builder, configRead1);
            byte[] configRead2 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead2, 0, configRead1.length);
            configRead2[1] = 10;
            sendMsgToWatch(builder, configRead2);
            byte[] configRead3 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead3, 0, configRead1.length);
            configRead3[1] = 21;
            sendMsgToWatch(builder, configRead3);
            byte[] configRead4 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead4, 0, configRead1.length);
            configRead4[1] = 23;
            sendMsgToWatch(builder, configRead4);
            byte[] configRead5 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead5, 0, configRead1.length);
            configRead5[1] = 26;
            sendMsgToWatch(builder, configRead5);
            byte[] configRead6 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead6, 0, configRead1.length);
            configRead6[1] = 37;
            sendMsgToWatch(builder, configRead6);
            byte[] configRead7 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead7, 0, configRead1.length);
            configRead7[1] = 49;
            sendMsgToWatch(builder, configRead7);
            byte[] configRead8 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead8, 0, configRead1.length);
            configRead8[1] = ZeTimeConstants.CMD_AUTO_HEARTRATE;
            sendMsgToWatch(builder, configRead8);
            byte[] configRead9 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead9, 0, configRead1.length);
            configRead9[1] = ZeTimeConstants.CMD_HEARTRATE_ALARM_LIMITS;
            sendMsgToWatch(builder, configRead9);
            byte[] configRead10 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead10, 0, configRead1.length);
            configRead10[1] = ZeTimeConstants.CMD_INACTIVITY_ALERT;
            sendMsgToWatch(builder, configRead10);
            byte[] configRead11 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead11, 0, configRead1.length);
            configRead11[1] = ZeTimeConstants.CMD_CALORIES_TYPE;
            sendMsgToWatch(builder, configRead11);
            byte[] configRead12 = new byte[configRead1.length];
            System.arraycopy(configRead1, 0, configRead12, 0, configRead1.length);
            configRead12[1] = ZeTimeConstants.CMD_SWITCH_SETTINGS;
            sendMsgToWatch(builder, configRead12);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error reading configuration: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void onReadReminders(TransactionBuilder builder) {
        sendMsgToWatch(builder, new byte[]{ZeTimeConstants.CMD_PREAMBLE, ZeTimeConstants.CMD_REMINDERS, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END});
        builder.queue(getQueue());
    }

    private void getDateTimeFormat(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        prefs.putString(ZeTimeConstants.PREF_DATE_FORMAT, Integer.toString(msg[5]));
        prefs.apply();
        String timeFormat = "24h";
        SharedPreferences.Editor prefsEditor = GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).edit();
        if (msg[6] == 2) {
            timeFormat = "am/pm";
        }
        prefsEditor.putString(DeviceSettingsPreferenceConst.PREF_TIMEFORMAT, timeFormat);
        prefsEditor.apply();
    }

    private void getSignaling(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        prefs.putString(ZeTimeConstants.PREF_ANTI_LOSS_SIGNALING, Integer.toString(msg[5]));
        prefs.putString(ZeTimeConstants.PREF_CALL_SIGNALING, Integer.toString(msg[7]));
        prefs.putString(ZeTimeConstants.PREF_MISSED_CALL_SIGNALING, Integer.toString(msg[8]));
        prefs.putString(ZeTimeConstants.PREF_SMS_SIGNALING, Integer.toString(msg[9]));
        prefs.putString(ZeTimeConstants.PREF_SOCIAL_SIGNALING, Integer.toString(msg[10]));
        prefs.putString(ZeTimeConstants.PREF_EMAIL_SIGNALING, Integer.toString(msg[11]));
        prefs.putString(ZeTimeConstants.PREF_CALENDAR_SIGNALING, Integer.toString(msg[12]));
        prefs.putString(ZeTimeConstants.PREF_INACTIVITY_SIGNALING, Integer.toString(msg[13]));
        prefs.putString(ZeTimeConstants.PREF_LOW_POWER_SIGNALING, Integer.toString(msg[14]));
        prefs.apply();
    }

    private void getDoNotDisturb(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        String starttime = String.format("%02d:%02d", new Object[]{Byte.valueOf(msg[6]), Byte.valueOf(msg[7])});
        String endtime = String.format("%02d:%02d", new Object[]{Byte.valueOf(msg[8]), Byte.valueOf(msg[9])});
        if (1 == msg[5]) {
            prefs.putString(ZeTimeConstants.PREF_DO_NOT_DISTURB, "scheduled");
        } else {
            prefs.putString(ZeTimeConstants.PREF_DO_NOT_DISTURB, "off");
        }
        prefs.putString(ZeTimeConstants.PREF_DO_NOT_DISTURB_START, starttime);
        prefs.putString(ZeTimeConstants.PREF_DO_NOT_DISTURB_END, endtime);
        prefs.apply();
    }

    private void getAnalogMode(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        prefs.putString(ZeTimeConstants.PREF_ANALOG_MODE, Integer.toString(msg[5]));
        prefs.apply();
    }

    private void getActivityTracking(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        if (1 == msg[6]) {
            prefs.putBoolean(ZeTimeConstants.PREF_ACTIVITY_TRACKING, false);
        } else {
            prefs.putBoolean(ZeTimeConstants.PREF_ACTIVITY_TRACKING, true);
        }
        prefs.apply();
    }

    private void getScreenTime(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        prefs.putString(ZeTimeConstants.PREF_SCREENTIME, Integer.toString(msg[5] | (msg[6] << 8)));
        prefs.apply();
    }

    private void getWrist(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).edit();
        if (msg[5] == 0) {
            prefs.putString(DeviceSettingsPreferenceConst.PREF_WEARLOCATION, "left");
        } else if (1 == msg[5]) {
            prefs.putString(DeviceSettingsPreferenceConst.PREF_WEARLOCATION, "right");
        }
        prefs.apply();
    }

    private void getHeartRateMeasurement(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        prefs.putString(ZeTimeConstants.PREF_ZETIME_HEARTRATE_INTERVAL, Integer.toString(msg[5] * 60));
        prefs.apply();
    }

    private void getHeartRateLimits(byte[] msg) {
        GBApplication.getPrefs().getPreferences().edit().apply();
    }

    private void getInactivityAlert(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        String starttime = String.format("%02d:%02d", new Object[]{Byte.valueOf(msg[7]), Byte.valueOf(msg[8])});
        String endtime = String.format("%02d:%02d", new Object[]{Byte.valueOf(msg[9]), Byte.valueOf(msg[10])});
        prefs.putString(ZeTimeConstants.PREF_INACTIVITY_THRESHOLD, Integer.toString(msg[6]));
        if (msg[5] != 0) {
            prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_ENABLE, true);
            prefs.putString(ZeTimeConstants.PREF_INACTIVITY_START, starttime);
            prefs.putString(ZeTimeConstants.PREF_INACTIVITY_END, endtime);
            if ((msg[5] & 1) != 0) {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_MO, true);
            } else {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_MO, false);
            }
            if ((2 & msg[5]) != 0) {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_TU, true);
            } else {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_TU, false);
            }
            if ((msg[5] & 4) != 0) {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_WE, true);
            } else {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_WE, false);
            }
            if ((msg[5] & 8) != 0) {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_TH, true);
            } else {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_TH, false);
            }
            if ((msg[5] & 16) != 0) {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_FR, true);
            } else {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_FR, false);
            }
            if ((msg[5] & 32) != 0) {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_SA, true);
            } else {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_SA, false);
            }
            if ((msg[5] & 64) != 0) {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_SU, true);
            } else {
                prefs.putBoolean(ZeTimeConstants.PREF_INACTIVITY_SU, false);
            }
        }
        prefs.apply();
    }

    private void getCaloriesType(byte[] msg) {
        SharedPreferences.Editor myedit = GBApplication.getPrefs().getPreferences().edit();
        myedit.putString(ZeTimeConstants.PREF_CALORIES_TYPE, Integer.toString(msg[5]));
        myedit.apply();
    }

    private void getDisplayOnMovement(byte[] msg) {
        SharedPreferences.Editor prefs = GBApplication.getPrefs().getPreferences().edit();
        if ((msg[6] & 64) != 0) {
            prefs.putBoolean(ZeTimeConstants.PREF_HANDMOVE_DISPLAY, true);
        } else {
            prefs.putBoolean(ZeTimeConstants.PREF_HANDMOVE_DISPLAY, false);
        }
        prefs.apply();
        if (getDevice().isBusy()) {
            getDevice().unsetBusyTask();
            getDevice().sendDeviceUpdateIntent(getContext());
        }
    }

    private void storeActualReminders(byte[] msg) {
        if (msg[3] == 11) {
            System.arraycopy(msg, 6, this.remindersOnWatch[msg[5] - 1], 0, 10);
        }
    }

    private void setLanguage(TransactionBuilder builder) {
        String language = GBApplication.getPrefs().getString(HuamiConst.PREF_LANGUAGE, "default");
        byte[] languageMsg = {ZeTimeConstants.CMD_PREAMBLE, 11, ZeTimeConstants.CMD_REQUEST, 1, 0, 0, ZeTimeConstants.CMD_END};
        if (language == null || language.equals("default")) {
            language = Locale.getDefault().getLanguage();
        }
        char c = 65535;
        switch (language.hashCode()) {
            case 3201:
                if (language.equals("de")) {
                    c = 9;
                    break;
                }
                break;
            case 3241:
                if (language.equals("en")) {
                    c = 17;
                    break;
                }
                break;
            case 3246:
                if (language.equals("es")) {
                    c = 8;
                    break;
                }
                break;
            case 3276:
                if (language.equals("fr")) {
                    c = 7;
                    break;
                }
                break;
            case 3331:
                if (language.equals("hk")) {
                    c = 2;
                    break;
                }
                break;
            case 3341:
                if (language.equals("hu")) {
                    c = 16;
                    break;
                }
                break;
            case 3371:
                if (language.equals("it")) {
                    c = 10;
                    break;
                }
                break;
            case 3383:
                if (language.equals("ja")) {
                    c = 6;
                    break;
                }
                break;
            case 3428:
                if (language.equals("ko")) {
                    c = 4;
                    break;
                }
                break;
            case 3490:
                if (language.equals("mo")) {
                    c = 3;
                    break;
                }
                break;
            case 3518:
                if (language.equals("nl")) {
                    c = 14;
                    break;
                }
                break;
            case 3580:
                if (language.equals("pl")) {
                    c = 11;
                    break;
                }
                break;
            case 3588:
                if (language.equals("pt")) {
                    c = 12;
                    break;
                }
                break;
            case 3645:
                if (language.equals("ro")) {
                    c = 15;
                    break;
                }
                break;
            case 3651:
                if (language.equals("ru")) {
                    c = CharUtils.f207CR;
                    break;
                }
                break;
            case 3700:
                if (language.equals("th")) {
                    c = 5;
                    break;
                }
                break;
            case 3715:
                if (language.equals("tw")) {
                    c = 1;
                    break;
                }
                break;
            case 3886:
                if (language.equals("zh")) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                languageMsg[5] = 1;
                return;
            case 1:
            case 2:
            case 3:
                languageMsg[5] = 2;
                return;
            case 4:
                languageMsg[5] = 3;
                return;
            case 5:
                languageMsg[5] = 4;
                return;
            case 6:
                languageMsg[5] = 5;
                return;
            case 7:
                languageMsg[5] = 6;
                return;
            case 8:
                languageMsg[5] = 7;
                return;
            case 9:
                languageMsg[5] = 8;
                return;
            case 10:
                languageMsg[5] = 9;
                return;
            case 11:
                languageMsg[5] = 10;
                return;
            case 12:
                languageMsg[5] = 11;
                return;
            case 13:
                languageMsg[5] = 12;
                return;
            case 14:
                languageMsg[5] = 13;
                return;
            case 15:
                languageMsg[5] = 32;
                return;
            case 16:
                languageMsg[5] = 33;
                return;
            default:
                languageMsg[5] = 0;
                return;
        }
    }
}
