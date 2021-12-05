package nodomain.freeyourgadget.gadgetbridge.service.devices.huami;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.ActivateDisplayOnLift;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.DisconnectNotificationSetting;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband2.MiBand2FWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Service;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.DateTimeDisplay;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.DoNotDisturb;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandService;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEvents;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.AbortTransactionAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.ConditionalWriteAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.IntentListener;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertCategory;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertNotificationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.NewAlert;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfoProfile;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.actions.StopNotificationAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband2.Mi2NotificationStrategy;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband2.Mi2TextNotificationStrategy;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchActivityOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.InitOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.UpdateFirmwareOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.NotificationStrategy;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.RealtimeSamplesSupport;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.NotificationUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Version;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuamiSupport extends AbstractBTLEDeviceSupport {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) HuamiSupport.class);
    private static int currentButtonActionId = 0;
    private static int currentButtonPressCount = 0;
    private static long currentButtonPressTime = 0;
    private static long currentButtonTimerActivationTime = 0;
    private static final byte[] startHeartMeasurementContinuous = {21, 1, 1};
    private static final byte[] startHeartMeasurementManual = {21, 2, 1};
    private static final byte[] stopHeartMeasurementContinuous = {21, 1, 0};
    private static final byte[] stopHeartMeasurementManual = {21, 2, 0};
    private boolean alarmClockRinging;
    private final GBDeviceEventBatteryInfo batteryCmd;
    private MusicSpec bufferMusicSpec;
    private MusicStateSpec bufferMusicStateSpec;
    /* access modifiers changed from: private */
    public Timer buttonActionTimer;
    private BluetoothGattCharacteristic characteristicChunked;
    private BluetoothGattCharacteristic characteristicHRControlPoint;
    private final DeviceInfoProfile<HuamiSupport> deviceInfoProfile;
    private final GBDeviceEventFindPhone findPhoneEvent;
    private boolean heartRateNotifyEnabled;
    /* access modifiers changed from: private */
    public volatile boolean isLocatingDevice;
    private boolean isMusicAppStarted;
    private final IntentListener mListener;
    private int mMTU;
    private boolean needsAuth;
    private RealtimeSamplesSupport realtimeSamplesSupport;
    private volatile boolean telephoneRinging;
    private final GBDeviceEventVersionInfo versionCmd;

    public HuamiSupport() {
        this(LOG);
    }

    public HuamiSupport(Logger logger) {
        super(logger);
        this.buttonActionTimer = null;
        this.mListener = new IntentListener() {
            public void notify(Intent intent) {
                if (DeviceInfoProfile.ACTION_DEVICE_INFO.equals(intent.getAction())) {
                    HuamiSupport.this.handleDeviceInfo((DeviceInfo) intent.getParcelableExtra(DeviceInfoProfile.EXTRA_DEVICE_INFO));
                }
            }
        };
        this.versionCmd = new GBDeviceEventVersionInfo();
        this.batteryCmd = new GBDeviceEventBatteryInfo();
        this.findPhoneEvent = new GBDeviceEventFindPhone();
        this.isMusicAppStarted = false;
        this.bufferMusicSpec = null;
        this.bufferMusicStateSpec = null;
        this.mMTU = 23;
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(GattService.UUID_SERVICE_HEART_RATE);
        addSupportedService(GattService.UUID_SERVICE_IMMEDIATE_ALERT);
        addSupportedService(GattService.UUID_SERVICE_DEVICE_INFORMATION);
        addSupportedService(GattService.UUID_SERVICE_ALERT_NOTIFICATION);
        addSupportedService(MiBandService.UUID_SERVICE_MIBAND_SERVICE);
        addSupportedService(MiBandService.UUID_SERVICE_MIBAND2_SERVICE);
        addSupportedService(HuamiService.UUID_SERVICE_FIRMWARE_SERVICE);
        this.deviceInfoProfile = new DeviceInfoProfile<>(this);
        this.deviceInfoProfile.addListener(this.mListener);
        addSupportedProfile(this.deviceInfoProfile);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        try {
            byte authFlags = getAuthFlags();
            byte cryptFlags = getCryptFlags();
            this.heartRateNotifyEnabled = false;
            boolean authenticate = this.needsAuth && cryptFlags == 0;
            this.needsAuth = false;
            new InitOperation(authenticate, authFlags, cryptFlags, this, builder).perform();
            this.characteristicHRControlPoint = getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT);
            this.characteristicChunked = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_CHUNKEDTRANSFER);
        } catch (IOException e) {
            C1238GB.toast(getContext(), "Initializing Huami device failed", 0, 3, e);
        }
        return builder;
    }

    /* access modifiers changed from: protected */
    public byte getAuthFlags() {
        return 8;
    }

    public byte getCryptFlags() {
        return 0;
    }

    public byte[] getTimeBytes(Calendar calendar, TimeUnit precision) {
        byte[] bytes;
        if (precision == TimeUnit.MINUTES) {
            bytes = BLETypeConversions.shortCalendarToRawBytes(calendar);
        } else if (precision == TimeUnit.SECONDS) {
            bytes = BLETypeConversions.calendarToRawBytes(calendar);
        } else {
            throw new IllegalArgumentException("Unsupported precision, only MINUTES and SECONDS are supported till now");
        }
        return BLETypeConversions.join(bytes, new byte[]{0, BLETypeConversions.mapTimeZone(calendar.getTimeZone(), 1)});
    }

    public Calendar fromTimeBytes(byte[] bytes) {
        return BLETypeConversions.rawBytesToCalendar(bytes);
    }

    public HuamiSupport setCurrentTimeWithService(TransactionBuilder builder) {
        builder.write(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_CURRENT_TIME), getTimeBytes(BLETypeConversions.createCalendar(), TimeUnit.SECONDS));
        return this;
    }

    public HuamiSupport setLowLatency(TransactionBuilder builder) {
        return this;
    }

    public HuamiSupport setHighLatency(TransactionBuilder builder) {
        return this;
    }

    public void setInitialized(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(this.gbDevice, GBDevice.State.INITIALIZED, getContext()));
    }

    public HuamiSupport enableNotifications(TransactionBuilder builder, boolean enable) {
        builder.notify(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_NOTIFICATION), enable);
        builder.notify(getCharacteristic(GattService.UUID_SERVICE_CURRENT_TIME), enable);
        builder.notify(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_AUTH), enable);
        return this;
    }

    public HuamiSupport enableFurtherNotifications(TransactionBuilder builder, boolean enable) {
        builder.notify(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), enable);
        builder.notify(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_6_BATTERY_INFO), enable);
        builder.notify(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_AUDIO), enable);
        builder.notify(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_AUDIODATA), enable);
        builder.notify(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_DEVICEEVENT), enable);
        return this;
    }

    public boolean useAutoConnect() {
        return true;
    }

    public boolean connectFirstTime() {
        this.needsAuth = true;
        return super.connect();
    }

    private HuamiSupport sendDefaultNotification(TransactionBuilder builder, SimpleNotification simpleNotification, short repeat, BtLEAction extraAction) {
        Logger logger = LOG;
        logger.info("Sending notification to MiBand: (" + repeat + " times)");
        NotificationStrategy strategy = getNotificationStrategy();
        for (short i = 0; i < repeat; i = (short) (i + 1)) {
            strategy.sendDefaultNotification(builder, simpleNotification, extraAction);
        }
        return this;
    }

    public NotificationStrategy getNotificationStrategy() {
        String firmwareVersion = this.gbDevice.getFirmwareVersion();
        if (firmwareVersion != null) {
            if (MiBandConst.MI2_FW_VERSION_MIN_TEXT_NOTIFICATIONS.compareTo(new Version(firmwareVersion)) > 0) {
                return new Mi2NotificationStrategy(this);
            }
        }
        if (GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).getBoolean(MiBandConst.PREF_MI2_ENABLE_TEXT_NOTIFICATIONS, true)) {
            return new Mi2TextNotificationStrategy(this);
        }
        return new Mi2NotificationStrategy(this);
    }

    private HuamiSupport requestBatteryInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Battery Info!");
        builder.read(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_6_BATTERY_INFO));
        return this;
    }

    public HuamiSupport requestDeviceInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Device Info!");
        this.deviceInfoProfile.requestDeviceInfo(builder);
        return this;
    }

    private HuamiSupport setFitnessGoal(TransactionBuilder transaction) {
        LOG.info("Attempting to set Fitness Goal...");
        BluetoothGattCharacteristic characteristic = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_8_USER_SETTINGS);
        if (characteristic != null) {
            transaction.write(characteristic, ArrayUtils.addAll(ArrayUtils.addAll(HuamiService.COMMAND_SET_FITNESS_GOAL_START, BLETypeConversions.fromUint16(GBApplication.getPrefs().getInt("mi_fitness_goal", ActivityUser.defaultUserStepsGoal))), HuamiService.COMMAND_SET_FITNESS_GOAL_END));
        } else {
            LOG.info("Unable to set Fitness Goal");
        }
        return this;
    }

    private HuamiSupport setUserInfo(TransactionBuilder transaction) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_8_USER_SETTINGS);
        if (characteristic == null) {
            return this;
        }
        LOG.info("Attempting to set user info...");
        String alias = GBApplication.getPrefs().getString("mi_user_alias", (String) null);
        ActivityUser activityUser = new ActivityUser();
        int height = activityUser.getHeightCm();
        int weight = activityUser.getWeightKg();
        int birth_year = activityUser.getYearOfBirth();
        if (alias == null || weight == 0 || height == 0) {
            TransactionBuilder transactionBuilder = transaction;
        } else if (birth_year == 0) {
            TransactionBuilder transactionBuilder2 = transaction;
        } else {
            byte sex = 2;
            int gender = activityUser.getGender();
            if (gender == 0) {
                sex = 1;
            } else if (gender == 1) {
                sex = 0;
            }
            int userid = alias.hashCode();
            transaction.write(characteristic, new byte[]{79, 0, 0, (byte) (birth_year & 255), (byte) ((birth_year >> 8) & 255), 7, 1, sex, (byte) (height & 255), (byte) ((height >> 8) & 255), (byte) ((weight * ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION) & 255), (byte) (((weight * ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION) >> 8) & 255), (byte) (userid & 255), (byte) ((userid >> 8) & 255), (byte) ((userid >> 16) & 255), (byte) ((userid >> 24) & 255)});
            return this;
        }
        LOG.warn("Unable to set user info, make sure it is set up");
        return this;
    }

    private HuamiSupport setWearLocation(TransactionBuilder builder) {
        LOG.info("Attempting to set wear location...");
        BluetoothGattCharacteristic characteristic = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_8_USER_SETTINGS);
        if (characteristic != null) {
            builder.notify(characteristic, true);
            int location = MiBandCoordinator.getWearLocation(this.gbDevice.getAddress());
            if (location == 0) {
                builder.write(characteristic, HuamiService.WEAR_LOCATION_LEFT_WRIST);
            } else if (location == 1) {
                builder.write(characteristic, HuamiService.WEAR_LOCATION_RIGHT_WRIST);
            }
            builder.notify(characteristic, false);
        }
        return this;
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
        try {
            TransactionBuilder builder = performInitialized("enable heart rate sleep support: " + enable);
            setHeartrateSleepSupport(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error toggling heart rate sleep support: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
        try {
            int minuteInterval = Math.max(0, Math.min(seconds / 60, 120));
            TransactionBuilder builder = performInitialized("set heart rate interval to: " + minuteInterval + " minutes");
            setHeartrateMeasurementInterval(builder, minuteInterval);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error toggling heart rate sleep support: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    private HuamiSupport setHeartrateSleepSupport(TransactionBuilder builder) {
        boolean enableHrSleepSupport = MiBandCoordinator.getHeartrateSleepSupport(this.gbDevice.getAddress());
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.characteristicHRControlPoint;
        if (bluetoothGattCharacteristic != null) {
            builder.notify(bluetoothGattCharacteristic, true);
            if (enableHrSleepSupport) {
                LOG.info("Enabling heartrate sleep support...");
                builder.write(this.characteristicHRControlPoint, HuamiService.COMMAND_ENABLE_HR_SLEEP_MEASUREMENT);
            } else {
                LOG.info("Disabling heartrate sleep support...");
                builder.write(this.characteristicHRControlPoint, HuamiService.COMMAND_DISABLE_HR_SLEEP_MEASUREMENT);
            }
            builder.notify(this.characteristicHRControlPoint, false);
        }
        return this;
    }

    private HuamiSupport setHeartrateMeasurementInterval(TransactionBuilder builder, int minutes) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.characteristicHRControlPoint;
        if (bluetoothGattCharacteristic != null) {
            builder.notify(bluetoothGattCharacteristic, true);
            Logger logger = LOG;
            logger.info("Setting heart rate measurement interval to " + minutes + " minutes");
            builder.write(this.characteristicHRControlPoint, new byte[]{20, (byte) minutes});
            builder.notify(this.characteristicHRControlPoint, false);
        }
        return this;
    }

    private void performDefaultNotification(String task, SimpleNotification simpleNotification, short repeat, BtLEAction extraAction) {
        try {
            TransactionBuilder builder = performInitialized(task);
            sendDefaultNotification(builder, simpleNotification, repeat, extraAction);
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to send notification to MI device", (Throwable) ex);
        }
    }

    private void performPreferredNotification(String task, String notificationOrigin, SimpleNotification simpleNotification, int alertLevel, BtLEAction extraAction) {
        String str = notificationOrigin;
        try {
            TransactionBuilder builder = performInitialized(task);
            Prefs prefs = GBApplication.getPrefs();
            VibrationProfile profile = getPreferredVibrateProfile(str, prefs, getPreferredVibrateCount(str, prefs));
            profile.setAlertLevel(alertLevel);
            getNotificationStrategy().sendCustomNotification(profile, simpleNotification, 0, 0, 0, 0, extraAction, builder);
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to send notification to device", (Throwable) ex);
        }
    }

    private short getPreferredVibrateCount(String notificationOrigin, Prefs prefs) {
        return (short) Math.min(32767, MiBandConst.getNotificationPrefIntValue(MiBandConst.VIBRATION_COUNT, notificationOrigin, prefs, 3));
    }

    private VibrationProfile getPreferredVibrateProfile(String notificationOrigin, Prefs prefs, short repeat) {
        return VibrationProfile.getProfile(MiBandConst.getNotificationPrefStringValue(MiBandConst.VIBRATION_PROFILE, notificationOrigin, prefs, MiBandConst.DEFAULT_VALUE_VIBRATION_PROFILE), repeat);
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        try {
            BluetoothGattCharacteristic characteristic = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION);
            TransactionBuilder builder = performInitialized("Set alarm");
            boolean anyAlarmEnabled = false;
            Iterator<? extends Alarm> it = alarms.iterator();
            while (it.hasNext()) {
                Alarm alarm = (Alarm) it.next();
                anyAlarmEnabled |= alarm.getEnabled();
                queueAlarm(alarm, builder, characteristic);
            }
            builder.queue(getQueue());
            if (anyAlarmEnabled) {
                C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_miband_set_alarms_ok), 0, 1);
            } else {
                C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_all_alarms_disabled), 0, 1);
            }
        } catch (IOException ex) {
            C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_miband_set_alarms_failed), 1, 3, ex);
        }
    }

    /* access modifiers changed from: protected */
    public void sendNotificationNew(NotificationSpec notificationSpec, boolean hasExtraHeader) {
        String message;
        AlertCategory alertCategory;
        int prefixlength;
        ApplicationInfo ai;
        String appName;
        NotificationSpec notificationSpec2 = notificationSpec;
        if (notificationSpec2.type == NotificationType.GENERIC_ALARM_CLOCK) {
            onAlarmClock(notificationSpec);
            return;
        }
        String senderOrTitle = StringUtils.getFirstOf(notificationSpec2.sender, notificationSpec2.title);
        String message2 = StringUtils.truncate(senderOrTitle, 32) + "\u0000";
        if (notificationSpec2.subject != null) {
            message2 = message2 + StringUtils.truncate(notificationSpec2.subject, 128) + "\n\n";
        }
        if (notificationSpec2.body != null) {
            message = message2 + StringUtils.truncate(notificationSpec2.body, 128);
        } else {
            message = message2;
        }
        try {
            TransactionBuilder builder = performInitialized("new notification");
            byte customIconId = HuamiIcon.mapToIconId(notificationSpec2.type);
            AlertCategory alertCategory2 = AlertCategory.CustomHuami;
            if (notificationSpec2.type == NotificationType.GENERIC_SMS) {
                alertCategory = AlertCategory.SMS;
            } else if (customIconId == 34) {
                alertCategory = AlertCategory.Email;
            } else {
                alertCategory = alertCategory2;
            }
            if (this.characteristicChunked != null) {
                byte[] appSuffix = "\u0000 \u0000".getBytes();
                int suffixlength = appSuffix.length;
                if (alertCategory == AlertCategory.CustomHuami) {
                    prefixlength = 3;
                    PackageManager pm = getContext().getPackageManager();
                    try {
                        ai = pm.getApplicationInfo(notificationSpec2.sourceAppId, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        ai = null;
                    }
                    if (ai != null) {
                        appName = "\u0000" + pm.getApplicationLabel(ai) + "\u0000";
                    } else {
                        appName = "\u0000UNKNOWN\u0000";
                    }
                    appSuffix = appName.getBytes();
                    suffixlength = appSuffix.length;
                } else {
                    prefixlength = 2;
                }
                if (hasExtraHeader) {
                    prefixlength += 4;
                }
                byte[] rawmessage = message.getBytes();
                int length = Math.min(rawmessage.length, 230 - prefixlength);
                byte[] command = new byte[(length + prefixlength + suffixlength)];
                int pos = 0 + 1;
                command[0] = (byte) alertCategory.getId();
                if (hasExtraHeader) {
                    int pos2 = pos + 1;
                    command[pos] = 0;
                    int pos3 = pos2 + 1;
                    command[pos2] = 0;
                    int pos4 = pos3 + 1;
                    command[pos3] = 0;
                    pos = pos4 + 1;
                    command[pos4] = 0;
                }
                int pos5 = pos + 1;
                command[pos] = 1;
                if (alertCategory == AlertCategory.CustomHuami) {
                    command[pos5] = customIconId;
                }
                System.arraycopy(rawmessage, 0, command, prefixlength, length);
                int i = pos5;
                System.arraycopy(appSuffix, 0, command, prefixlength + length, appSuffix.length);
                writeToChunked(builder, 0, command);
            } else {
                AlertNotificationProfile<?> profile = new AlertNotificationProfile<>(this);
                NewAlert alert = new NewAlert(alertCategory, 1, message, customIconId);
                profile.setMaxLength(230);
                profile.newAlert(builder, alert);
            }
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to send notification to device", (Throwable) ex);
        }
    }

    public void onNotification(NotificationSpec notificationSpec) {
        if (notificationSpec.type == NotificationType.GENERIC_ALARM_CLOCK) {
            onAlarmClock(notificationSpec);
            return;
        }
        int alertLevel = 1;
        if (notificationSpec.type == NotificationType.UNKNOWN) {
            alertLevel = 3;
        }
        String message = NotificationUtils.getPreferredTextFor(notificationSpec, 40, 40, getContext()).trim();
        String origin = notificationSpec.type.getGenericType();
        SimpleNotification simpleNotification = new SimpleNotification(message, BLETypeConversions.toAlertCategory(notificationSpec.type), notificationSpec.type);
        performPreferredNotification(origin + " received", origin, simpleNotification, alertLevel, (BtLEAction) null);
    }

    /* access modifiers changed from: protected */
    public void onAlarmClock(NotificationSpec notificationSpec) {
        this.alarmClockRinging = true;
        AbortTransactionAction abortAction = new StopNotificationAction(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL)) {
            /* access modifiers changed from: protected */
            public boolean shouldAbort() {
                return !HuamiSupport.this.isAlarmClockRinging();
            }
        };
        performPreferredNotification("alarm clock ringing", MiBandConst.ORIGIN_ALARM_CLOCK, new SimpleNotification(NotificationUtils.getPreferredTextFor(notificationSpec, 40, 40, getContext()), AlertCategory.HighPriorityAlert, notificationSpec.type), 3, abortAction);
    }

    public void onDeleteNotification(int id) {
        this.alarmClockRinging = false;
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("Set date and time");
            setCurrentTimeWithService(builder);
            sendCalendarEvents(builder);
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to set time on Huami device", (Throwable) ex);
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command == 2) {
            this.telephoneRinging = true;
            AbortTransactionAction abortAction = new StopNotificationAction(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL)) {
                /* access modifiers changed from: protected */
                public boolean shouldAbort() {
                    return !HuamiSupport.this.isTelephoneRinging();
                }
            };
            performPreferredNotification("incoming call", MiBandConst.ORIGIN_INCOMING_CALL, new SimpleNotification(NotificationUtils.getPreferredTextFor(callSpec), AlertCategory.IncomingCall, (NotificationType) null), 2, abortAction);
        } else if (callSpec.command == 5 || callSpec.command == 6) {
            this.telephoneRinging = false;
            stopCurrentCallNotification();
        }
    }

    private void stopCurrentCallNotification() {
        try {
            TransactionBuilder builder = performInitialized("stop notification");
            getNotificationStrategy().stopCurrentNotification(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.error("Error stopping call notification");
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    /* access modifiers changed from: private */
    public boolean isAlarmClockRinging() {
        return this.alarmClockRinging;
    }

    /* access modifiers changed from: private */
    public boolean isTelephoneRinging() {
        return this.telephoneRinging;
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
        if (DeviceHelper.getInstance().getCoordinator(this.gbDevice).supportsMusicInfo() && this.bufferMusicStateSpec != stateSpec) {
            this.bufferMusicStateSpec = stateSpec;
            sendMusicStateToDevice();
        }
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        if (DeviceHelper.getInstance().getCoordinator(this.gbDevice).supportsMusicInfo() && this.bufferMusicSpec != musicSpec) {
            this.bufferMusicSpec = musicSpec;
            if (this.isMusicAppStarted) {
                sendMusicStateToDevice();
            }
        }
    }

    private void sendMusicStateToDevice() {
        byte state;
        if (this.characteristicChunked != null) {
            MusicSpec musicSpec = this.bufferMusicSpec;
            if (musicSpec == null || this.bufferMusicStateSpec == null) {
                try {
                    TransactionBuilder builder = performInitialized("send dummy playback info to enable music controls");
                    writeToChunked(builder, 3, new byte[]{1, 0, 1, 0, 0, 0, 1, 0});
                    builder.queue(getQueue());
                } catch (IOException e) {
                    LOG.error("Unable to send dummy music controls");
                }
            } else {
                byte flags = (byte) (0 | 1);
                int length = 8;
                if (musicSpec.track != null && this.bufferMusicSpec.track.getBytes().length > 0) {
                    length = 8 + this.bufferMusicSpec.track.getBytes().length + 1;
                    flags = (byte) (flags | 2);
                }
                if (this.bufferMusicSpec.album != null && this.bufferMusicSpec.album.getBytes().length > 0) {
                    length += this.bufferMusicSpec.album.getBytes().length + 1;
                    flags = (byte) (flags | 4);
                }
                if (this.bufferMusicSpec.artist != null && this.bufferMusicSpec.artist.getBytes().length > 0) {
                    length += this.bufferMusicSpec.artist.getBytes().length + 1;
                    flags = (byte) (flags | 8);
                }
                try {
                    ByteBuffer buf = ByteBuffer.allocate(length);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    buf.put(flags);
                    if (this.bufferMusicStateSpec.state != 0) {
                        state = 0;
                    } else {
                        state = 1;
                    }
                    buf.put(state);
                    buf.put(new byte[]{1, 0, 0, 0});
                    buf.put(new byte[]{1, 0});
                    if (this.bufferMusicSpec.track != null && this.bufferMusicSpec.track.getBytes().length > 0) {
                        buf.put(this.bufferMusicSpec.track.getBytes());
                        buf.put((byte) 0);
                    }
                    if (this.bufferMusicSpec.album != null && this.bufferMusicSpec.album.getBytes().length > 0) {
                        buf.put(this.bufferMusicSpec.album.getBytes());
                        buf.put((byte) 0);
                    }
                    if (this.bufferMusicSpec.artist != null && this.bufferMusicSpec.artist.getBytes().length > 0) {
                        buf.put(this.bufferMusicSpec.artist.getBytes());
                        buf.put((byte) 0);
                    }
                    TransactionBuilder builder2 = performInitialized("send playback info");
                    writeToChunked(builder2, 3, buf.array());
                    builder2.queue(getQueue());
                } catch (IOException e2) {
                    LOG.error("Unable to send playback state");
                }
            }
        }
    }

    public void onReset(int flags) {
        try {
            TransactionBuilder builder = performInitialized("Reset");
            if ((flags & 2) != 0) {
                sendFactoryReset(builder);
            } else {
                sendReboot(builder);
            }
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to reset", (Throwable) ex);
        }
    }

    public HuamiSupport sendReboot(TransactionBuilder builder) {
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_FIRMWARE), new byte[]{5});
        return this;
    }

    public HuamiSupport sendFactoryReset(TransactionBuilder builder) {
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_FACTORY_RESET);
        return this;
    }

    public void onHeartRateTest() {
        if (this.characteristicHRControlPoint != null) {
            try {
                TransactionBuilder builder = performInitialized("HeartRateTest");
                enableNotifyHeartRateMeasurements(true, builder);
                builder.write(this.characteristicHRControlPoint, stopHeartMeasurementContinuous);
                builder.write(this.characteristicHRControlPoint, stopHeartMeasurementManual);
                builder.write(this.characteristicHRControlPoint, startHeartMeasurementManual);
                builder.queue(getQueue());
            } catch (IOException ex) {
                LOG.error("Unable to read heart rate from Huami device", (Throwable) ex);
            }
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        if (this.characteristicHRControlPoint != null) {
            try {
                TransactionBuilder builder = performInitialized("Enable realtime heart rate measurement");
                enableNotifyHeartRateMeasurements(enable, builder);
                if (enable) {
                    builder.write(this.characteristicHRControlPoint, stopHeartMeasurementManual);
                    builder.write(this.characteristicHRControlPoint, startHeartMeasurementContinuous);
                } else {
                    builder.write(this.characteristicHRControlPoint, stopHeartMeasurementContinuous);
                }
                builder.queue(getQueue());
                enableRealtimeSamplesTimer(enable);
            } catch (IOException ex) {
                LOG.error("Unable to enable realtime heart rate measurement", (Throwable) ex);
            }
        }
    }

    private void enableNotifyHeartRateMeasurements(boolean enable, TransactionBuilder builder) {
        BluetoothGattCharacteristic heartrateCharacteristic;
        if (this.heartRateNotifyEnabled != enable && (heartrateCharacteristic = getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT)) != null) {
            builder.notify(heartrateCharacteristic, enable);
            this.heartRateNotifyEnabled = enable;
        }
    }

    public void onFindDevice(boolean start) {
        this.isLocatingDevice = start;
        if (start) {
            performDefaultNotification("locating device", new SimpleNotification(getContext().getString(C0889R.string.find_device_you_found_it), AlertCategory.HighPriorityAlert, (NotificationType) null), 255, new AbortTransactionAction() {
                /* access modifiers changed from: protected */
                public boolean shouldAbort() {
                    return !HuamiSupport.this.isLocatingDevice;
                }
            });
        }
    }

    public void onSetConstantVibration(int intensity) {
    }

    public void onFetchRecordedData(int dataTypes) {
        try {
            new FetchActivityOperation(this).perform();
        } catch (IOException ex) {
            LOG.error("Unable to fetch activity data", (Throwable) ex);
        }
    }

    public void onEnableRealtimeSteps(boolean enable) {
        try {
            TransactionBuilder builder = performInitialized(enable ? "Enabling realtime steps notifications" : "Disabling realtime steps notifications");
            if (enable) {
                builder.read(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_7_REALTIME_STEPS));
            }
            builder.notify(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_7_REALTIME_STEPS), enable);
            builder.queue(getQueue());
            enableRealtimeSamplesTimer(enable);
        } catch (IOException e) {
            Logger logger = LOG;
            logger.error("Unable to change realtime steps notification to: " + enable, (Throwable) e);
        }
    }

    private byte[] getHighLatency() {
        return getLatency(460, 500, 0, 500, 0);
    }

    private byte[] getLatency(int minConnectionInterval, int maxConnectionInterval, int latency, int timeout, int advertisementInterval) {
        return new byte[]{(byte) (minConnectionInterval & 255), (byte) ((minConnectionInterval >> 8) & 255), (byte) (maxConnectionInterval & 255), (byte) ((maxConnectionInterval >> 8) & 255), (byte) (latency & 255), (byte) ((latency >> 8) & 255), (byte) (timeout & 255), (byte) ((timeout >> 8) & 255), 0, 0, (byte) (advertisementInterval & 255), (byte) ((advertisementInterval >> 8) & 255)};
    }

    private byte[] getLowLatency() {
        return getLatency(39, 49, 0, 500, 0);
    }

    public void onInstallApp(Uri uri) {
        try {
            createUpdateFirmwareOperation(uri).perform();
        } catch (IOException ex) {
            Context context = getContext();
            C1238GB.toast(context, "Firmware cannot be installed: " + ex.getMessage(), 1, 3, ex);
        }
    }

    public void onAppInfoReq() {
    }

    public void onAppStart(UUID uuid, boolean start) {
    }

    public void onAppDelete(UUID uuid) {
    }

    public void onAppConfiguration(UUID uuid, String config, Integer id) {
    }

    public void onAppReorder(UUID[] uuids) {
    }

    public void onScreenshotReq() {
    }

    private void vibrateOnce() {
        BluetoothGattCharacteristic characteristic = getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL);
        try {
            TransactionBuilder builder = performInitialized("Vibrate once");
            builder.write(characteristic, new byte[]{3});
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.error("error while sending simple vibrate command", (Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void runButtonAction() {
        Prefs prefs = new Prefs(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()));
        if (currentButtonTimerActivationTime == currentButtonPressTime) {
            int i = currentButtonActionId;
            if (i == 0) {
                handleMediaButton(prefs.getString("button_long_press_action_selection", "UNKNOWN"));
            } else if (i == 1) {
                handleMediaButton(prefs.getString("button_single_press_action_selection", "UNKNOWN"));
            } else if (i == 2) {
                handleMediaButton(prefs.getString("button_double_press_action_selection", "UNKNOWN"));
            } else if (i == 3) {
                handleMediaButton(prefs.getString("button_triple_press_action_selection", "UNKNOWN"));
            }
            String requiredButtonPressMessage = prefs.getString(HuamiConst.PREF_BUTTON_ACTION_BROADCAST, getContext().getString(C0889R.string.mi2_prefs_button_press_broadcast_default_value));
            Intent in = new Intent();
            in.setAction(requiredButtonPressMessage);
            in.putExtra("button_id", currentButtonActionId);
            Logger logger = LOG;
            logger.info("Sending " + requiredButtonPressMessage + " with button_id " + currentButtonActionId);
            getContext().getApplicationContext().sendBroadcast(in);
            if (prefs.getBoolean(HuamiConst.PREF_BUTTON_ACTION_VIBRATE, false)) {
                vibrateOnce();
            }
            currentButtonActionId = 0;
            currentButtonPressCount = 0;
            currentButtonPressTime = System.currentTimeMillis();
        }
    }

    private void handleMediaButton(String MediaAction) {
        if (!MediaAction.equals("UNKNOWN")) {
            GBDeviceEventMusicControl deviceEventMusicControl = new GBDeviceEventMusicControl();
            deviceEventMusicControl.event = GBDeviceEventMusicControl.Event.valueOf(MediaAction);
            evaluateGBDeviceEvent(deviceEventMusicControl);
        }
    }

    private void handleDeviceEvent(byte[] value) {
        if (value != null && value.length != 0) {
            GBDeviceEventCallControl callCmd = new GBDeviceEventCallControl();
            byte b = value[0];
            if (b == -2) {
                LOG.info("got music control");
                GBDeviceEventMusicControl deviceEventMusicControl = new GBDeviceEventMusicControl();
                byte b2 = value[1];
                if (b2 == -32) {
                    LOG.info("Music app started");
                    this.isMusicAppStarted = true;
                    sendMusicStateToDevice();
                } else if (b2 == -31) {
                    LOG.info("Music app terminated");
                    this.isMusicAppStarted = false;
                } else if (b2 == 0) {
                    deviceEventMusicControl.event = GBDeviceEventMusicControl.Event.PLAY;
                } else if (b2 == 1) {
                    deviceEventMusicControl.event = GBDeviceEventMusicControl.Event.PAUSE;
                } else if (b2 == 3) {
                    deviceEventMusicControl.event = GBDeviceEventMusicControl.Event.NEXT;
                } else if (b2 == 4) {
                    deviceEventMusicControl.event = GBDeviceEventMusicControl.Event.PREVIOUS;
                } else if (b2 == 5) {
                    deviceEventMusicControl.event = GBDeviceEventMusicControl.Event.VOLUMEUP;
                } else if (b2 != 6) {
                    Logger logger = LOG;
                    logger.info("unhandled music control event " + value[1]);
                    return;
                } else {
                    deviceEventMusicControl.event = GBDeviceEventMusicControl.Event.VOLUMEDOWN;
                }
                evaluateGBDeviceEvent(deviceEventMusicControl);
            } else if (b == 22) {
                int mtu = ((value[2] & 255) << 8) | (value[1] & 255);
                Logger logger2 = LOG;
                logger2.info("device announced MTU of " + mtu);
                this.mMTU = mtu;
            } else if (b == 1) {
                LOG.info("Fell asleep");
            } else if (b == 2) {
                LOG.info("Woke up");
            } else if (b == 3) {
                LOG.info("Steps goal reached");
            } else if (b == 4) {
                LOG.info("button pressed");
                handleButtonEvent();
            } else if (b == 14) {
                LOG.info("Tick 30 min (?)");
            } else if (b != 15) {
                switch (b) {
                    case 6:
                        LOG.info("non-wear start detected");
                        return;
                    case 7:
                        LOG.info("call rejected");
                        callCmd.event = GBDeviceEventCallControl.Event.REJECT;
                        evaluateGBDeviceEvent(callCmd);
                        return;
                    case 8:
                        LOG.info("find phone started");
                        acknowledgeFindPhone();
                        this.findPhoneEvent.event = GBDeviceEventFindPhone.Event.START;
                        evaluateGBDeviceEvent(this.findPhoneEvent);
                        return;
                    case 9:
                        LOG.info("call ignored");
                        callCmd.event = GBDeviceEventCallControl.Event.IGNORE;
                        evaluateGBDeviceEvent(callCmd);
                        return;
                    case 10:
                        LOG.info("An alarm was toggled");
                        TransactionBuilder builder = new TransactionBuilder("requestAlarms");
                        requestAlarms(builder);
                        builder.queue(getQueue());
                        return;
                    case 11:
                        LOG.info("button long-pressed ");
                        handleLongButtonEvent();
                        return;
                    default:
                        Logger logger3 = LOG;
                        logger3.warn("unhandled event " + value[0]);
                        return;
                }
            } else {
                LOG.info("find phone stopped");
                this.findPhoneEvent.event = GBDeviceEventFindPhone.Event.STOP;
                evaluateGBDeviceEvent(this.findPhoneEvent);
            }
        }
    }

    private void requestMTU(int mtu) {
        if (GBApplication.isRunningLollipopOrLater()) {
            new TransactionBuilder("requestMtu").requestMtu(mtu).queue(getQueue());
            this.mMTU = mtu;
        }
    }

    private void acknowledgeFindPhone() {
        try {
            TransactionBuilder builder = performInitialized("acknowledge find phone");
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), AmazfitBipService.COMMAND_ACK_FIND_PHONE_IN_PROGRESS);
            builder.queue(getQueue());
        } catch (Exception ex) {
            LOG.error("Error sending current weather", (Throwable) ex);
        }
    }

    private void handleLongButtonEvent() {
        if (new Prefs(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress())).getBoolean(HuamiConst.PREF_BUTTON_ACTION_ENABLE, false)) {
            currentButtonActionId = 0;
            currentButtonPressTime = System.currentTimeMillis();
            currentButtonTimerActivationTime = currentButtonPressTime;
            runButtonAction();
        }
    }

    private void handleButtonEvent() {
        Prefs prefs = new Prefs(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()));
        if (prefs.getBoolean(HuamiConst.PREF_BUTTON_ACTION_ENABLE, false)) {
            int buttonPressMaxDelay = prefs.getInt(HuamiConst.PREF_BUTTON_ACTION_PRESS_MAX_INTERVAL, ActivityUser.defaultUserCaloriesBurnt);
            int requiredButtonPressCount = prefs.getInt(HuamiConst.PREF_BUTTON_ACTION_PRESS_COUNT, 0);
            if (requiredButtonPressCount > 0) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentButtonPressTime;
                long timeSinceLastPress = currentTimeMillis - j;
                if (j == 0 || timeSinceLastPress < ((long) buttonPressMaxDelay)) {
                    currentButtonPressCount++;
                } else {
                    currentButtonPressCount = 1;
                    currentButtonActionId = 0;
                }
                Timer timer = this.buttonActionTimer;
                if (timer != null) {
                    timer.cancel();
                }
                currentButtonPressTime = System.currentTimeMillis();
                if (currentButtonPressCount == requiredButtonPressCount) {
                    currentButtonTimerActivationTime = currentButtonPressTime;
                    LOG.info("Activating button timer");
                    this.buttonActionTimer = new Timer("Huami Button Action Timer");
                    this.buttonActionTimer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            HuamiSupport.this.runButtonAction();
                            HuamiSupport.this.buttonActionTimer.cancel();
                        }
                    }, (long) buttonPressMaxDelay, (long) buttonPressMaxDelay);
                    currentButtonActionId++;
                    currentButtonPressCount = 0;
                }
            }
        }
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        UUID characteristicUUID = characteristic.getUuid();
        if (HuamiService.UUID_CHARACTERISTIC_6_BATTERY_INFO.equals(characteristicUUID)) {
            handleBatteryInfo(characteristic.getValue(), 0);
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_REALTIME_STEPS.equals(characteristicUUID)) {
            handleRealtimeSteps(characteristic.getValue());
            return true;
        } else if (GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT.equals(characteristicUUID)) {
            handleHeartrate(characteristic.getValue());
            return true;
        } else if (HuamiService.UUID_CHARACTERISTIC_AUTH.equals(characteristicUUID)) {
            Logger logger = LOG;
            logger.info("AUTHENTICATION?? " + characteristicUUID);
            logMessageContent(characteristic.getValue());
            return true;
        } else if (HuamiService.UUID_CHARACTERISTIC_DEVICEEVENT.equals(characteristicUUID)) {
            handleDeviceEvent(characteristic.getValue());
            return true;
        } else if (HuamiService.UUID_CHARACTERISTIC_7_REALTIME_STEPS.equals(characteristicUUID)) {
            handleRealtimeSteps(characteristic.getValue());
            return true;
        } else if (HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION.equals(characteristicUUID)) {
            handleConfigurationInfo(characteristic.getValue());
            return true;
        } else {
            Logger logger2 = LOG;
            logger2.info("Unhandled characteristic changed: " + characteristicUUID);
            logMessageContent(characteristic.getValue());
            return false;
        }
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        UUID characteristicUUID = characteristic.getUuid();
        if (GattCharacteristic.UUID_CHARACTERISTIC_GAP_DEVICE_NAME.equals(characteristicUUID)) {
            handleDeviceName(characteristic.getValue(), status);
            return true;
        } else if (HuamiService.UUID_CHARACTERISTIC_6_BATTERY_INFO.equals(characteristicUUID)) {
            handleBatteryInfo(characteristic.getValue(), status);
            return true;
        } else if (GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT.equals(characteristicUUID)) {
            logHeartrate(characteristic.getValue(), status);
            return true;
        } else if (HuamiService.UUID_CHARACTERISTIC_7_REALTIME_STEPS.equals(characteristicUUID)) {
            handleRealtimeSteps(characteristic.getValue());
            return true;
        } else if (HuamiService.UUID_CHARACTERISTIC_DEVICEEVENT.equals(characteristicUUID)) {
            handleDeviceEvent(characteristic.getValue());
            return true;
        } else {
            Logger logger = LOG;
            logger.info("Unhandled characteristic read: " + characteristicUUID);
            logMessageContent(characteristic.getValue());
            return false;
        }
    }

    public boolean onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (!HuamiService.UUID_CHARACTERISTIC_AUTH.equals(characteristic.getUuid())) {
            return false;
        }
        LOG.info("KEY AES SEND");
        logMessageContent(characteristic.getValue());
        return true;
    }

    public void logHeartrate(byte[] value, int status) {
        if (status != 0 || value == null) {
            logMessageContent(value);
            return;
        }
        LOG.info("Got heartrate:");
        if (value.length == 2 && value[0] == 0) {
            Context context = getContext();
            C1238GB.toast(context, "Heart Rate measured: " + (value[1] & 255), 1, 1);
        }
    }

    private void handleHeartrate(byte[] value) {
        if (value.length == 2 && value[0] == 0) {
            int hrValue = value[1] & 255;
            if (LOG.isDebugEnabled()) {
                Logger logger = LOG;
                logger.debug("heart rate: " + hrValue);
            }
            RealtimeSamplesSupport realtimeSamplesSupport2 = getRealtimeSamplesSupport();
            realtimeSamplesSupport2.setHeartrateBpm(hrValue);
            if (!realtimeSamplesSupport2.isRunning()) {
                realtimeSamplesSupport2.triggerCurrentSample();
            }
        }
    }

    private void handleRealtimeSteps(byte[] value) {
        if (value == null) {
            LOG.error("realtime steps: value is null");
        } else if (value.length == 13) {
            int steps = BLETypeConversions.toUint16(value[1], value[2]);
            if (LOG.isDebugEnabled()) {
                Logger logger = LOG;
                logger.debug("realtime steps: " + steps);
            }
            getRealtimeSamplesSupport().setSteps(steps);
        } else {
            Logger logger2 = LOG;
            logger2.warn("Unrecognized realtime steps value: " + Logging.formatBytes(value));
        }
    }

    private void handleConfigurationInfo(byte[] value) {
        if (value != null && value.length >= 4) {
            if (value[0] != 16 || value[2] != 1) {
                Logger logger = LOG;
                logger.warn("error received from configuration request " + C1238GB.hexdump(value, 0, -1));
            } else if (value[1] == 14) {
                String gpsVersion = new String(value, 3, value.length - 3);
                Logger logger2 = LOG;
                logger2.info("got gps version = " + gpsVersion);
                this.gbDevice.setFirmwareVersion2(gpsVersion);
            } else if (value[1] == 13) {
                LOG.info("got alarms from watch");
                decodeAndUpdateAlarmStatus(value);
            } else {
                Logger logger3 = LOG;
                logger3.warn("got configuration info we do not handle yet " + C1238GB.hexdump(value, 3, -1));
            }
        }
    }

    private void decodeAndUpdateAlarmStatus(byte[] response) {
        List<nodomain.freeyourgadget.gadgetbridge.entities.Alarm> alarms = DBHelper.getAlarms(this.gbDevice);
        boolean[] alarmsInUse = new boolean[10];
        boolean[] alarmsEnabled = new boolean[10];
        byte nr_alarms = response[8];
        int i = 0;
        while (true) {
            boolean enabled = true;
            if (i >= nr_alarms) {
                break;
            }
            byte alarm_data = response[i + 9];
            int index = alarm_data & 15;
            alarmsInUse[index] = true;
            if ((alarm_data & 16) != 16) {
                enabled = false;
            }
            alarmsEnabled[index] = enabled;
            LOG.info("alarm " + index + " is enabled:" + enabled);
            i++;
        }
        for (nodomain.freeyourgadget.gadgetbridge.entities.Alarm alarm : alarms) {
            boolean enabled2 = alarmsEnabled[alarm.getPosition()];
            boolean unused = !alarmsInUse[alarm.getPosition()];
            if (alarm.getEnabled() != enabled2 || alarm.getUnused() != unused) {
                LOG.info("updating alarm index " + alarm.getPosition() + " unused=" + unused + ", enabled=" + enabled2);
                alarm.setEnabled(enabled2);
                alarm.setUnused(unused);
                DBHelper.store(alarm);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(DeviceService.ACTION_SAVE_ALARMS));
            }
        }
    }

    private void enableRealtimeSamplesTimer(boolean enable) {
        if (enable) {
            getRealtimeSamplesSupport().start();
            return;
        }
        RealtimeSamplesSupport realtimeSamplesSupport2 = this.realtimeSamplesSupport;
        if (realtimeSamplesSupport2 != null) {
            realtimeSamplesSupport2.stop();
        }
    }

    public MiBandActivitySample createActivitySample(Device device, User user, int timestampInSeconds, SampleProvider provider) {
        MiBandActivitySample sample = new MiBandActivitySample();
        sample.setDevice(device);
        sample.setUser(user);
        sample.setTimestamp(timestampInSeconds);
        sample.setProvider(provider);
        return sample;
    }

    private RealtimeSamplesSupport getRealtimeSamplesSupport() {
        if (this.realtimeSamplesSupport == null) {
            this.realtimeSamplesSupport = new RealtimeSamplesSupport(1000, 1000) {
                /* JADX WARNING: Code restructure failed: missing block: B:13:0x008d, code lost:
                    r2 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:14:0x008e, code lost:
                    if (r0 != null) goto L_0x0090;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
                    r0.close();
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:20:0x0098, code lost:
                    throw r2;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void doCurrentSample() {
                    /*
                        r10 = this;
                        nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0099 }
                        nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r1 = r0.getDaoSession()     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.this     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r2.gbDevice     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.entities.Device r2 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r2, r1)     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.entities.User r3 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r1)     // Catch:{ all -> 0x008b }
                        long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x008b }
                        r6 = 1000(0x3e8, double:4.94E-321)
                        long r4 = r4 / r6
                        int r5 = (int) r4     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBand2SampleProvider r4 = new nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBand2SampleProvider     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.this     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r6 = r6.gbDevice     // Catch:{ all -> 0x008b }
                        r4.<init>(r6, r1)     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.this     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample r6 = r6.createActivitySample(r2, r3, r5, r4)     // Catch:{ all -> 0x008b }
                        int r7 = r10.getHeartrateBpm()     // Catch:{ all -> 0x008b }
                        r6.setHeartRate(r7)     // Catch:{ all -> 0x008b }
                        r7 = -1
                        r6.setRawIntensity(r7)     // Catch:{ all -> 0x008b }
                        r7 = 1
                        r6.setRawKind(r7)     // Catch:{ all -> 0x008b }
                        r4.addGBActivitySample(r6)     // Catch:{ all -> 0x008b }
                        int r7 = r10.getSteps()     // Catch:{ all -> 0x008b }
                        r6.setSteps(r7)     // Catch:{ all -> 0x008b }
                        org.slf4j.Logger r7 = nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.LOG     // Catch:{ all -> 0x008b }
                        boolean r7 = r7.isDebugEnabled()     // Catch:{ all -> 0x008b }
                        if (r7 == 0) goto L_0x006a
                        org.slf4j.Logger r7 = nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.LOG     // Catch:{ all -> 0x008b }
                        java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x008b }
                        r8.<init>()     // Catch:{ all -> 0x008b }
                        java.lang.String r9 = "realtime sample: "
                        r8.append(r9)     // Catch:{ all -> 0x008b }
                        r8.append(r6)     // Catch:{ all -> 0x008b }
                        java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x008b }
                        r7.debug(r8)     // Catch:{ all -> 0x008b }
                    L_0x006a:
                        android.content.Intent r7 = new android.content.Intent     // Catch:{ all -> 0x008b }
                        java.lang.String r8 = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_samples"
                        r7.<init>(r8)     // Catch:{ all -> 0x008b }
                        java.lang.String r8 = "realtime_sample"
                        android.content.Intent r7 = r7.putExtra(r8, r6)     // Catch:{ all -> 0x008b }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport r8 = nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.this     // Catch:{ all -> 0x008b }
                        android.content.Context r8 = r8.getContext()     // Catch:{ all -> 0x008b }
                        androidx.localbroadcastmanager.content.LocalBroadcastManager r8 = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(r8)     // Catch:{ all -> 0x008b }
                        r8.sendBroadcast(r7)     // Catch:{ all -> 0x008b }
                        if (r0 == 0) goto L_0x008a
                        r0.close()     // Catch:{ Exception -> 0x0099 }
                    L_0x008a:
                        goto L_0x00a3
                    L_0x008b:
                        r1 = move-exception
                        throw r1     // Catch:{ all -> 0x008d }
                    L_0x008d:
                        r2 = move-exception
                        if (r0 == 0) goto L_0x0098
                        r0.close()     // Catch:{ all -> 0x0094 }
                        goto L_0x0098
                    L_0x0094:
                        r3 = move-exception
                        r1.addSuppressed(r3)     // Catch:{ Exception -> 0x0099 }
                    L_0x0098:
                        throw r2     // Catch:{ Exception -> 0x0099 }
                    L_0x0099:
                        r0 = move-exception
                        org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.LOG
                        java.lang.String r2 = "Unable to acquire db for saving realtime samples"
                        r1.warn((java.lang.String) r2, (java.lang.Throwable) r0)
                    L_0x00a3:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.C11826.doCurrentSample():void");
                }
            };
        }
        return this.realtimeSamplesSupport;
    }

    private void handleDeviceName(byte[] value, int status) {
    }

    private void queueAlarm(Alarm alarm, TransactionBuilder builder, BluetoothGattCharacteristic characteristic) {
        Calendar calendar = AlarmUtils.toCalendar(alarm);
        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(this.gbDevice);
        int maxAlarms = coordinator.getAlarmSlotCount();
        if (alarm.getPosition() < maxAlarms) {
            int actionMask = 0;
            int daysMask = 0;
            if (alarm.getEnabled() && !alarm.getUnused()) {
                actionMask = 128;
                if (coordinator.supportsAlarmSnoozing() && !alarm.getSnooze()) {
                    actionMask = 128 | 64;
                }
            }
            if (!alarm.getUnused()) {
                daysMask = alarm.getRepetition();
                if (!alarm.isRepetitive()) {
                    daysMask = 128;
                }
            }
            builder.write(characteristic, new byte[]{2, (byte) (alarm.getPosition() | actionMask), (byte) calendar.get(11), (byte) calendar.get(12), (byte) daysMask});
        } else if (alarm.getEnabled()) {
            Context context = getContext();
            C1238GB.toast(context, "Only " + maxAlarms + " alarms are currently supported.", 1, 2);
        }
    }

    /* access modifiers changed from: private */
    public void handleDeviceInfo(DeviceInfo info) {
        Logger logger = LOG;
        logger.warn("Device info: " + info);
        this.versionCmd.hwVersion = info.getHardwareRevision();
        this.versionCmd.fwVersion = info.getFirmwareRevision();
        if (this.versionCmd.fwVersion == null) {
            this.versionCmd.fwVersion = info.getSoftwareRevision();
        }
        if (this.versionCmd.fwVersion != null && this.versionCmd.fwVersion.length() > 0 && this.versionCmd.fwVersion.charAt(0) == 'V') {
            GBDeviceEventVersionInfo gBDeviceEventVersionInfo = this.versionCmd;
            gBDeviceEventVersionInfo.fwVersion = gBDeviceEventVersionInfo.fwVersion.substring(1);
        }
        handleGBDeviceEvent(this.versionCmd);
    }

    private void handleBatteryInfo(byte[] value, int status) {
        if (status == 0) {
            HuamiBatteryInfo info = new HuamiBatteryInfo(value);
            this.batteryCmd.level = (short) info.getLevelInPercent();
            this.batteryCmd.state = info.getState();
            this.batteryCmd.lastChargeTime = info.getLastChargeTime();
            this.batteryCmd.numCharges = info.getNumCharges();
            handleGBDeviceEvent(this.batteryCmd);
        }
    }

    private HuamiSupport sendCalendarEvents(TransactionBuilder builder) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION);
        int availableSlots = new Prefs(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress())).getInt(DeviceSettingsPreferenceConst.PREF_RESERVER_ALARMS_CALENDAR, 0);
        if (availableSlots > 0) {
            int iteration = 0;
            for (CalendarEvents.CalendarEvent mEvt : new CalendarEvents().getCalendarEventList(getContext())) {
                if (iteration >= availableSlots || iteration > 2) {
                    break;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mEvt.getBegin());
                queueAlarm(AlarmUtils.createSingleShot(2 - iteration, false, true, calendar), builder, characteristic);
                iteration++;
            }
        }
        return this;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSendConfiguration(java.lang.String r6) {
        /*
            r5 = this;
            r0 = 3
            r1 = 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01aa }
            r2.<init>()     // Catch:{ IOException -> 0x01aa }
            java.lang.String r3 = "Sending configuration for option: "
            r2.append(r3)     // Catch:{ IOException -> 0x01aa }
            r2.append(r6)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x01aa }
            nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r2 = r5.performInitialized(r2)     // Catch:{ IOException -> 0x01aa }
            r3 = -1
            int r4 = r6.hashCode()     // Catch:{ IOException -> 0x01aa }
            switch(r4) {
                case -2051748129: goto L_0x0154;
                case -1613589672: goto L_0x0149;
                case -1475982845: goto L_0x013e;
                case -1413502774: goto L_0x0134;
                case -1195476231: goto L_0x0129;
                case -1169080146: goto L_0x011f;
                case -1153391471: goto L_0x0115;
                case -1121936374: goto L_0x010b;
                case -820369390: goto L_0x0100;
                case -817028265: goto L_0x00f5;
                case -284098693: goto L_0x00e9;
                case -130955311: goto L_0x00de;
                case 10031854: goto L_0x00d3;
                case 274427728: goto L_0x00c7;
                case 502427956: goto L_0x00bb;
                case 502428917: goto L_0x00af;
                case 643491817: goto L_0x00a3;
                case 655550706: goto L_0x0098;
                case 735302917: goto L_0x008c;
                case 1071377701: goto L_0x0080;
                case 1332624644: goto L_0x0074;
                case 1364201817: goto L_0x0068;
                case 1500205108: goto L_0x005c;
                case 1745150359: goto L_0x0050;
                case 1810960892: goto L_0x0044;
                case 1846704046: goto L_0x0039;
                case 1872171778: goto L_0x002d;
                case 2066963661: goto L_0x0021;
                default: goto L_0x001f;
            }     // Catch:{ IOException -> 0x01aa }
        L_0x001f:
            goto L_0x015e
        L_0x0021:
            java.lang.String r4 = "mi_fitness_goal"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 10
            goto L_0x015e
        L_0x002d:
            java.lang.String r4 = "do_not_disturb_start"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 12
            goto L_0x015e
        L_0x0039:
            java.lang.String r4 = "disconnect_notification"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 5
            goto L_0x015e
        L_0x0044:
            java.lang.String r4 = "mi2_inactivity_warnings_start"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 16
            goto L_0x015e
        L_0x0050:
            java.lang.String r4 = "mi2_inactivity_warnings_dnd_start"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 19
            goto L_0x015e
        L_0x005c:
            java.lang.String r4 = "wearlocation"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 27
            goto L_0x015e
        L_0x0068:
            java.lang.String r4 = "mi2_inactivity_warnings"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 14
            goto L_0x015e
        L_0x0074:
            java.lang.String r4 = "timeformat"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 23
            goto L_0x015e
        L_0x0080:
            java.lang.String r4 = "dateformat"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 24
            goto L_0x015e
        L_0x008c:
            java.lang.String r4 = "mi2_inactivity_warnings_threshold"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 15
            goto L_0x015e
        L_0x0098:
            java.lang.String r4 = "activate_display_on_lift_wrist"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 2
            goto L_0x015e
        L_0x00a3:
            java.lang.String r4 = "swipe_unlock"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 22
            goto L_0x015e
        L_0x00af:
            java.lang.String r4 = "mi2_inactivity_warnings_end"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 17
            goto L_0x015e
        L_0x00bb:
            java.lang.String r4 = "mi2_inactivity_warnings_dnd"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 18
            goto L_0x015e
        L_0x00c7:
            java.lang.String r4 = "mi2_inactivity_warnings_dnd_end"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 20
            goto L_0x015e
        L_0x00d3:
            java.lang.String r4 = "mi2_dateformat"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 0
            goto L_0x015e
        L_0x00de:
            java.lang.String r4 = "display_on_lift_start"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 3
            goto L_0x015e
        L_0x00e9:
            java.lang.String r4 = "do_not_disturb_end"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 13
            goto L_0x015e
        L_0x00f5:
            java.lang.String r4 = "rotate_wrist_to_cycle_info"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 9
            goto L_0x015e
        L_0x0100:
            java.lang.String r4 = "measurement_system"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 21
            goto L_0x015e
        L_0x010b:
            java.lang.String r4 = "display_on_lift_end"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 4
            goto L_0x015e
        L_0x0115:
            java.lang.String r4 = "disconnect_notification_start"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 6
            goto L_0x015e
        L_0x011f:
            java.lang.String r4 = "mi2_goal_notification"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 1
            goto L_0x015e
        L_0x0129:
            java.lang.String r4 = "expose_hr_thirdparty"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 26
            goto L_0x015e
        L_0x0134:
            java.lang.String r4 = "disconnect_notification_end"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 7
            goto L_0x015e
        L_0x013e:
            java.lang.String r4 = "display_items"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 8
            goto L_0x015e
        L_0x0149:
            java.lang.String r4 = "language"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 25
            goto L_0x015e
        L_0x0154:
            java.lang.String r4 = "do_not_disturb"
            boolean r4 = r6.equals(r4)     // Catch:{ IOException -> 0x01aa }
            if (r4 == 0) goto L_0x001f
            r3 = 11
        L_0x015e:
            switch(r3) {
                case 0: goto L_0x019e;
                case 1: goto L_0x019a;
                case 2: goto L_0x0196;
                case 3: goto L_0x0196;
                case 4: goto L_0x0196;
                case 5: goto L_0x0192;
                case 6: goto L_0x0192;
                case 7: goto L_0x0192;
                case 8: goto L_0x018e;
                case 9: goto L_0x018a;
                case 10: goto L_0x0186;
                case 11: goto L_0x0182;
                case 12: goto L_0x0182;
                case 13: goto L_0x0182;
                case 14: goto L_0x017e;
                case 15: goto L_0x017e;
                case 16: goto L_0x017e;
                case 17: goto L_0x017e;
                case 18: goto L_0x017e;
                case 19: goto L_0x017e;
                case 20: goto L_0x017e;
                case 21: goto L_0x017a;
                case 22: goto L_0x0176;
                case 23: goto L_0x0172;
                case 24: goto L_0x016e;
                case 25: goto L_0x016a;
                case 26: goto L_0x0166;
                case 27: goto L_0x0162;
                default: goto L_0x0161;
            }     // Catch:{ IOException -> 0x01aa }
        L_0x0161:
            goto L_0x01a2
        L_0x0162:
            r5.setWearLocation(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x0166:
            r5.setExposeHRThridParty(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x016a:
            r5.setLanguage(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x016e:
            r5.setDateFormat(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x0172:
            r5.setTimeFormat(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x0176:
            r5.setBandScreenUnlock(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x017a:
            r5.setDistanceUnit(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x017e:
            r5.setInactivityWarnings(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x0182:
            r5.setDoNotDisturb(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x0186:
            r5.setFitnessGoal(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x018a:
            r5.setRotateWristToSwitchInfo(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x018e:
            r5.setDisplayItems(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x0192:
            r5.setDisconnectNotification(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x0196:
            r5.setActivateDisplayOnLiftWrist(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x019a:
            r5.setGoalNotification(r2)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01a2
        L_0x019e:
            r5.setDateDisplay(r2)     // Catch:{ IOException -> 0x01aa }
        L_0x01a2:
            nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue r3 = r5.getQueue()     // Catch:{ IOException -> 0x01aa }
            r2.queue(r3)     // Catch:{ IOException -> 0x01aa }
            goto L_0x01b0
        L_0x01aa:
            r2 = move-exception
            java.lang.String r3 = "Error setting configuration"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((java.lang.String) r3, (int) r1, (int) r0, (java.lang.Throwable) r2)
        L_0x01b0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.onSendConfiguration(java.lang.String):void");
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:108:0x02b2 A[Catch:{ Exception -> 0x02d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x02bb A[Catch:{ Exception -> 0x02d0 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSendWeather(nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec r22) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r1.gbDevice
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = r0.getType()
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r3 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            if (r0 != r3) goto L_0x000f
            return
        L_0x000f:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r1.gbDevice
            java.lang.String r0 = r0.getFirmwareVersion()
            if (r0 != 0) goto L_0x001f
            org.slf4j.Logger r0 = LOG
            java.lang.String r3 = "Device not initialized yet, so not sending weather info"
            r0.warn(r3)
            return
        L_0x001f:
            r0 = 1
            nodomain.freeyourgadget.gadgetbridge.util.Version r3 = new nodomain.freeyourgadget.gadgetbridge.util.Version
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r1.gbDevice
            java.lang.String r4 = r4.getFirmwareVersion()
            r3.<init>(r4)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r1.gbDevice
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r4 = r4.getType()
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r5 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.AMAZFITBIP
            if (r4 != r5) goto L_0x0045
            nodomain.freeyourgadget.gadgetbridge.util.Version r4 = new nodomain.freeyourgadget.gadgetbridge.util.Version
            java.lang.String r5 = "0.0.8.74"
            r4.<init>(r5)
            int r4 = r3.compareTo((nodomain.freeyourgadget.gadgetbridge.util.Version) r4)
            if (r4 >= 0) goto L_0x0045
            r0 = 0
            r4 = r0
            goto L_0x0046
        L_0x0045:
            r4 = r0
        L_0x0046:
            nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst$DistanceUnit r5 = nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiCoordinator.getDistanceUnit()
            java.util.TimeZone r0 = java.util.SimpleTimeZone.getDefault()
            int r6 = r2.timestamp
            long r6 = (long) r6
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 * r8
            int r0 = r0.getOffset(r6)
            r6 = 3600000(0x36ee80, float:5.044674E-39)
            int r6 = r0 / r6
            r7 = 2
            r8 = 0
            r9 = 1
            java.lang.String r0 = "Sending current temp"
            nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r0 = r1.performInitialized(r0)     // Catch:{ Exception -> 0x00d5 }
            int r10 = r2.currentConditionCode     // Catch:{ Exception -> 0x00d5 }
            byte r10 = nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiWeatherConditions.mapToAmazfitBipWeatherCode(r10)     // Catch:{ Exception -> 0x00d5 }
            r11 = 8
            if (r4 == 0) goto L_0x007a
            java.lang.String r12 = r2.currentCondition     // Catch:{ Exception -> 0x00d5 }
            byte[] r12 = r12.getBytes()     // Catch:{ Exception -> 0x00d5 }
            int r12 = r12.length     // Catch:{ Exception -> 0x00d5 }
            int r12 = r12 + r9
            int r11 = r11 + r12
        L_0x007a:
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocate(r11)     // Catch:{ Exception -> 0x00d5 }
            java.nio.ByteOrder r13 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ Exception -> 0x00d5 }
            r12.order(r13)     // Catch:{ Exception -> 0x00d5 }
            r12.put(r7)     // Catch:{ Exception -> 0x00d5 }
            int r13 = r2.timestamp     // Catch:{ Exception -> 0x00d5 }
            r12.putInt(r13)     // Catch:{ Exception -> 0x00d5 }
            int r13 = r6 * 4
            byte r13 = (byte) r13     // Catch:{ Exception -> 0x00d5 }
            r12.put(r13)     // Catch:{ Exception -> 0x00d5 }
            r12.put(r10)     // Catch:{ Exception -> 0x00d5 }
            int r13 = r2.currentTemp     // Catch:{ Exception -> 0x00d5 }
            int r13 = r13 + -273
            nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst$DistanceUnit r14 = nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst.DistanceUnit.IMPERIAL     // Catch:{ Exception -> 0x00d5 }
            if (r5 != r14) goto L_0x00a2
            double r14 = (double) r13     // Catch:{ Exception -> 0x00d5 }
            double r14 = cyanogenmod.weather.util.WeatherUtils.celsiusToFahrenheit(r14)     // Catch:{ Exception -> 0x00d5 }
            int r13 = (int) r14     // Catch:{ Exception -> 0x00d5 }
        L_0x00a2:
            byte r14 = (byte) r13     // Catch:{ Exception -> 0x00d5 }
            r12.put(r14)     // Catch:{ Exception -> 0x00d5 }
            if (r4 == 0) goto L_0x00b4
            java.lang.String r14 = r2.currentCondition     // Catch:{ Exception -> 0x00d5 }
            byte[] r14 = r14.getBytes()     // Catch:{ Exception -> 0x00d5 }
            r12.put(r14)     // Catch:{ Exception -> 0x00d5 }
            r12.put(r8)     // Catch:{ Exception -> 0x00d5 }
        L_0x00b4:
            android.bluetooth.BluetoothGattCharacteristic r14 = r1.characteristicChunked     // Catch:{ Exception -> 0x00d5 }
            if (r14 == 0) goto L_0x00c0
            byte[] r14 = r12.array()     // Catch:{ Exception -> 0x00d5 }
            r1.writeToChunked(r0, r9, r14)     // Catch:{ Exception -> 0x00d5 }
            goto L_0x00cd
        L_0x00c0:
            java.util.UUID r14 = nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService.UUID_CHARACTERISTIC_WEATHER     // Catch:{ Exception -> 0x00d5 }
            android.bluetooth.BluetoothGattCharacteristic r14 = r1.getCharacteristic(r14)     // Catch:{ Exception -> 0x00d5 }
            byte[] r15 = r12.array()     // Catch:{ Exception -> 0x00d5 }
            r0.write(r14, r15)     // Catch:{ Exception -> 0x00d5 }
        L_0x00cd:
            nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue r14 = r21.getQueue()     // Catch:{ Exception -> 0x00d5 }
            r0.queue(r14)     // Catch:{ Exception -> 0x00d5 }
            goto L_0x00dd
        L_0x00d5:
            r0 = move-exception
            org.slf4j.Logger r10 = LOG
            java.lang.String r11 = "Error sending current weather"
            r10.error((java.lang.String) r11, (java.lang.Throwable) r0)
        L_0x00dd:
            java.lang.String r0 = "Sending air quality index"
            nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r0 = r1.performInitialized(r0)     // Catch:{ IOException -> 0x0138 }
            r10 = 8
            java.lang.String r11 = "(n/a)"
            if (r4 == 0) goto L_0x00f0
            byte[] r12 = r11.getBytes()     // Catch:{ IOException -> 0x0138 }
            int r12 = r12.length     // Catch:{ IOException -> 0x0138 }
            int r12 = r12 + r9
            int r10 = r10 + r12
        L_0x00f0:
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocate(r10)     // Catch:{ IOException -> 0x0138 }
            java.nio.ByteOrder r13 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ IOException -> 0x0138 }
            r12.order(r13)     // Catch:{ IOException -> 0x0138 }
            r13 = 4
            r12.put(r13)     // Catch:{ IOException -> 0x0138 }
            int r13 = r2.timestamp     // Catch:{ IOException -> 0x0138 }
            r12.putInt(r13)     // Catch:{ IOException -> 0x0138 }
            int r13 = r6 * 4
            byte r13 = (byte) r13     // Catch:{ IOException -> 0x0138 }
            r12.put(r13)     // Catch:{ IOException -> 0x0138 }
            r12.putShort(r8)     // Catch:{ IOException -> 0x0138 }
            if (r4 == 0) goto L_0x0117
            byte[] r13 = r11.getBytes()     // Catch:{ IOException -> 0x0138 }
            r12.put(r13)     // Catch:{ IOException -> 0x0138 }
            r12.put(r8)     // Catch:{ IOException -> 0x0138 }
        L_0x0117:
            android.bluetooth.BluetoothGattCharacteristic r13 = r1.characteristicChunked     // Catch:{ IOException -> 0x0138 }
            if (r13 == 0) goto L_0x0123
            byte[] r13 = r12.array()     // Catch:{ IOException -> 0x0138 }
            r1.writeToChunked(r0, r9, r13)     // Catch:{ IOException -> 0x0138 }
            goto L_0x0130
        L_0x0123:
            java.util.UUID r13 = nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService.UUID_CHARACTERISTIC_WEATHER     // Catch:{ IOException -> 0x0138 }
            android.bluetooth.BluetoothGattCharacteristic r13 = r1.getCharacteristic(r13)     // Catch:{ IOException -> 0x0138 }
            byte[] r14 = r12.array()     // Catch:{ IOException -> 0x0138 }
            r0.write(r13, r14)     // Catch:{ IOException -> 0x0138 }
        L_0x0130:
            nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue r13 = r21.getQueue()     // Catch:{ IOException -> 0x0138 }
            r0.queue(r13)     // Catch:{ IOException -> 0x0138 }
            goto L_0x0140
        L_0x0138:
            r0 = move-exception
            org.slf4j.Logger r10 = LOG
            java.lang.String r11 = "Error sending air quality"
            r10.error(r11)
        L_0x0140:
            java.lang.String r0 = "Sending weather forecast"
            nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r0 = r1.performInitialized(r0)     // Catch:{ Exception -> 0x0278 }
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec$Forecast> r10 = r2.forecasts     // Catch:{ Exception -> 0x0278 }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0278 }
            int r10 = r10 + r9
            byte r10 = (byte) r10
            r11 = 4
            r12 = 0
            if (r4 == 0) goto L_0x0181
            r11 = 5
            java.lang.String r13 = r2.currentCondition     // Catch:{ Exception -> 0x017a }
            byte[] r13 = r13.getBytes()     // Catch:{ Exception -> 0x017a }
            int r13 = r13.length     // Catch:{ Exception -> 0x017a }
            r12 = r13
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec$Forecast> r13 = r2.forecasts     // Catch:{ Exception -> 0x017a }
            java.util.Iterator r13 = r13.iterator()     // Catch:{ Exception -> 0x017a }
        L_0x0161:
            boolean r14 = r13.hasNext()     // Catch:{ Exception -> 0x017a }
            if (r14 == 0) goto L_0x0181
            java.lang.Object r14 = r13.next()     // Catch:{ Exception -> 0x017a }
            nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec$Forecast r14 = (nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec.Forecast) r14     // Catch:{ Exception -> 0x017a }
            int r15 = r14.conditionCode     // Catch:{ Exception -> 0x017a }
            java.lang.String r15 = nodomain.freeyourgadget.gadgetbridge.model.Weather.getConditionString(r15)     // Catch:{ Exception -> 0x017a }
            byte[] r15 = r15.getBytes()     // Catch:{ Exception -> 0x017a }
            int r15 = r15.length     // Catch:{ Exception -> 0x017a }
            int r12 = r12 + r15
            goto L_0x0161
        L_0x017a:
            r0 = move-exception
            r17 = r3
            r18 = r6
            goto L_0x027d
        L_0x0181:
            int r13 = r11 * r10
            int r13 = r13 + 7
            int r13 = r13 + r12
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r13)     // Catch:{ Exception -> 0x0278 }
            java.nio.ByteOrder r15 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ Exception -> 0x0278 }
            r14.order(r15)     // Catch:{ Exception -> 0x0278 }
            r14.put(r9)     // Catch:{ Exception -> 0x0278 }
            int r15 = r2.timestamp     // Catch:{ Exception -> 0x0278 }
            r14.putInt(r15)     // Catch:{ Exception -> 0x0278 }
            int r15 = r6 * 4
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0278 }
            r14.put(r15)     // Catch:{ Exception -> 0x0278 }
            r14.put(r10)     // Catch:{ Exception -> 0x0278 }
            int r15 = r2.currentConditionCode     // Catch:{ Exception -> 0x0278 }
            byte r15 = nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiWeatherConditions.mapToAmazfitBipWeatherCode(r15)     // Catch:{ Exception -> 0x0278 }
            r14.put(r15)     // Catch:{ Exception -> 0x0278 }
            r14.put(r15)     // Catch:{ Exception -> 0x0278 }
            int r7 = r2.todayMaxTemp     // Catch:{ Exception -> 0x0278 }
            int r7 = r7 + -273
            int r9 = r2.todayMinTemp     // Catch:{ Exception -> 0x0278 }
            int r9 = r9 + -273
            nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst$DistanceUnit r8 = nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst.DistanceUnit.IMPERIAL     // Catch:{ Exception -> 0x0278 }
            if (r5 != r8) goto L_0x01c8
            r8 = r10
            r16 = r11
            double r10 = (double) r7
            double r10 = cyanogenmod.weather.util.WeatherUtils.celsiusToFahrenheit(r10)     // Catch:{ Exception -> 0x017a }
            int r7 = (int) r10     // Catch:{ Exception -> 0x017a }
            double r10 = (double) r9     // Catch:{ Exception -> 0x017a }
            double r10 = cyanogenmod.weather.util.WeatherUtils.celsiusToFahrenheit(r10)     // Catch:{ Exception -> 0x017a }
            int r9 = (int) r10
            goto L_0x01cb
        L_0x01c8:
            r8 = r10
            r16 = r11
        L_0x01cb:
            byte r10 = (byte) r7
            r14.put(r10)     // Catch:{ Exception -> 0x0278 }
            byte r10 = (byte) r9     // Catch:{ Exception -> 0x0278 }
            r14.put(r10)     // Catch:{ Exception -> 0x0278 }
            if (r4 == 0) goto L_0x01e2
            java.lang.String r10 = r2.currentCondition     // Catch:{ Exception -> 0x017a }
            byte[] r10 = r10.getBytes()     // Catch:{ Exception -> 0x017a }
            r14.put(r10)     // Catch:{ Exception -> 0x017a }
            r10 = 0
            r14.put(r10)     // Catch:{ Exception -> 0x017a }
        L_0x01e2:
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec$Forecast> r10 = r2.forecasts     // Catch:{ Exception -> 0x0278 }
            java.util.Iterator r10 = r10.iterator()     // Catch:{ Exception -> 0x0278 }
        L_0x01e8:
            boolean r11 = r10.hasNext()     // Catch:{ Exception -> 0x0278 }
            if (r11 == 0) goto L_0x024c
            java.lang.Object r11 = r10.next()     // Catch:{ Exception -> 0x0278 }
            nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec$Forecast r11 = (nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec.Forecast) r11     // Catch:{ Exception -> 0x0278 }
            r17 = r3
            int r3 = r11.conditionCode     // Catch:{ Exception -> 0x0248 }
            byte r3 = nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiWeatherConditions.mapToAmazfitBipWeatherCode(r3)     // Catch:{ Exception -> 0x0248 }
            r15 = r3
            r14.put(r15)     // Catch:{ Exception -> 0x0248 }
            r14.put(r15)     // Catch:{ Exception -> 0x0248 }
            int r3 = r11.maxTemp     // Catch:{ Exception -> 0x0248 }
            int r3 = r3 + -273
            r18 = r6
            int r6 = r11.minTemp     // Catch:{ Exception -> 0x0276 }
            int r6 = r6 + -273
            r19 = r7
            nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst$DistanceUnit r7 = nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst.DistanceUnit.IMPERIAL     // Catch:{ Exception -> 0x0276 }
            if (r5 != r7) goto L_0x0222
            r20 = r8
            double r7 = (double) r3     // Catch:{ Exception -> 0x0276 }
            double r7 = cyanogenmod.weather.util.WeatherUtils.celsiusToFahrenheit(r7)     // Catch:{ Exception -> 0x0276 }
            int r3 = (int) r7     // Catch:{ Exception -> 0x0276 }
            double r7 = (double) r6     // Catch:{ Exception -> 0x0276 }
            double r7 = cyanogenmod.weather.util.WeatherUtils.celsiusToFahrenheit(r7)     // Catch:{ Exception -> 0x0276 }
            int r6 = (int) r7     // Catch:{ Exception -> 0x0276 }
            goto L_0x0224
        L_0x0222:
            r20 = r8
        L_0x0224:
            byte r7 = (byte) r3     // Catch:{ Exception -> 0x0276 }
            r14.put(r7)     // Catch:{ Exception -> 0x0276 }
            byte r7 = (byte) r6     // Catch:{ Exception -> 0x0276 }
            r14.put(r7)     // Catch:{ Exception -> 0x0276 }
            if (r4 == 0) goto L_0x023f
            int r7 = r11.conditionCode     // Catch:{ Exception -> 0x0276 }
            java.lang.String r7 = nodomain.freeyourgadget.gadgetbridge.model.Weather.getConditionString(r7)     // Catch:{ Exception -> 0x0276 }
            byte[] r7 = r7.getBytes()     // Catch:{ Exception -> 0x0276 }
            r14.put(r7)     // Catch:{ Exception -> 0x0276 }
            r7 = 0
            r14.put(r7)     // Catch:{ Exception -> 0x0276 }
        L_0x023f:
            r3 = r17
            r6 = r18
            r7 = r19
            r8 = r20
            goto L_0x01e8
        L_0x0248:
            r0 = move-exception
            r18 = r6
            goto L_0x027d
        L_0x024c:
            r17 = r3
            r18 = r6
            r19 = r7
            r20 = r8
            android.bluetooth.BluetoothGattCharacteristic r3 = r1.characteristicChunked     // Catch:{ Exception -> 0x0276 }
            if (r3 == 0) goto L_0x0261
            byte[] r3 = r14.array()     // Catch:{ Exception -> 0x0276 }
            r6 = 1
            r1.writeToChunked(r0, r6, r3)     // Catch:{ Exception -> 0x0276 }
            goto L_0x026e
        L_0x0261:
            java.util.UUID r3 = nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService.UUID_CHARACTERISTIC_WEATHER     // Catch:{ Exception -> 0x0276 }
            android.bluetooth.BluetoothGattCharacteristic r3 = r1.getCharacteristic(r3)     // Catch:{ Exception -> 0x0276 }
            byte[] r6 = r14.array()     // Catch:{ Exception -> 0x0276 }
            r0.write(r3, r6)     // Catch:{ Exception -> 0x0276 }
        L_0x026e:
            nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue r3 = r21.getQueue()     // Catch:{ Exception -> 0x0276 }
            r0.queue(r3)     // Catch:{ Exception -> 0x0276 }
            goto L_0x0284
        L_0x0276:
            r0 = move-exception
            goto L_0x027d
        L_0x0278:
            r0 = move-exception
            r17 = r3
            r18 = r6
        L_0x027d:
            org.slf4j.Logger r3 = LOG
            java.lang.String r6 = "Error sending weather forecast"
            r3.error((java.lang.String) r6, (java.lang.Throwable) r0)
        L_0x0284:
            java.lang.String r0 = "Sending forecast location"
            nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r0 = r1.performInitialized(r0)     // Catch:{ Exception -> 0x02d0 }
            java.lang.String r3 = r2.location     // Catch:{ Exception -> 0x02d0 }
            byte[] r3 = r3.getBytes()     // Catch:{ Exception -> 0x02d0 }
            int r3 = r3.length     // Catch:{ Exception -> 0x02d0 }
            r6 = 2
            int r3 = r3 + r6
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x02d0 }
            java.nio.ByteOrder r7 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ Exception -> 0x02d0 }
            r6.order(r7)     // Catch:{ Exception -> 0x02d0 }
            r7 = 8
            r6.put(r7)     // Catch:{ Exception -> 0x02d0 }
            java.lang.String r7 = r2.location     // Catch:{ Exception -> 0x02d0 }
            byte[] r7 = r7.getBytes()     // Catch:{ Exception -> 0x02d0 }
            r6.put(r7)     // Catch:{ Exception -> 0x02d0 }
            r7 = 0
            r6.put(r7)     // Catch:{ Exception -> 0x02d0 }
            android.bluetooth.BluetoothGattCharacteristic r7 = r1.characteristicChunked     // Catch:{ Exception -> 0x02d0 }
            if (r7 == 0) goto L_0x02bb
            byte[] r7 = r6.array()     // Catch:{ Exception -> 0x02d0 }
            r8 = 1
            r1.writeToChunked(r0, r8, r7)     // Catch:{ Exception -> 0x02d0 }
            goto L_0x02c8
        L_0x02bb:
            java.util.UUID r7 = nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService.UUID_CHARACTERISTIC_WEATHER     // Catch:{ Exception -> 0x02d0 }
            android.bluetooth.BluetoothGattCharacteristic r7 = r1.getCharacteristic(r7)     // Catch:{ Exception -> 0x02d0 }
            byte[] r8 = r6.array()     // Catch:{ Exception -> 0x02d0 }
            r0.write(r7, r8)     // Catch:{ Exception -> 0x02d0 }
        L_0x02c8:
            nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue r7 = r21.getQueue()     // Catch:{ Exception -> 0x02d0 }
            r0.queue(r7)     // Catch:{ Exception -> 0x02d0 }
            goto L_0x02d8
        L_0x02d0:
            r0 = move-exception
            org.slf4j.Logger r3 = LOG
            java.lang.String r6 = "Error sending current forecast location"
            r3.error((java.lang.String) r6, (java.lang.Throwable) r0)
        L_0x02d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport.onSendWeather(nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec):void");
    }

    private HuamiSupport setDateDisplay(TransactionBuilder builder) {
        DateTimeDisplay dateTimeDisplay = HuamiCoordinator.getDateDisplay(getContext(), this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Setting date display to " + dateTimeDisplay);
        int i = C11848.f181xae499cf0[dateTimeDisplay.ordinal()];
        if (i == 1) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.DATEFORMAT_TIME);
        } else if (i == 2) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.DATEFORMAT_DATE_TIME);
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public HuamiSupport setDateFormat(TransactionBuilder builder) {
        String dateFormat = GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).getString(DeviceSettingsPreferenceConst.PREF_DATEFORMAT, "MM/dd/yyyy");
        if (dateFormat == null) {
            return null;
        }
        char c = 65535;
        int hashCode = dateFormat.hashCode();
        if (hashCode != -650712384) {
            if (hashCode != 1900521056) {
                if (hashCode == 2087096576 && dateFormat.equals("MM/dd/yyyy")) {
                    c = 0;
                }
            } else if (dateFormat.equals("dd.MM.yyyy")) {
                c = 1;
            }
        } else if (dateFormat.equals("dd/MM/yyyy")) {
            c = 2;
        }
        if (c == 0 || c == 1 || c == 2) {
            byte[] command = HuamiService.DATEFORMAT_DATE_MM_DD_YYYY;
            System.arraycopy(dateFormat.getBytes(), 0, command, 3, 10);
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), command);
        } else {
            Logger logger = LOG;
            logger.warn("unsupported date format " + dateFormat);
        }
        return this;
    }

    private HuamiSupport setTimeFormat(TransactionBuilder builder) {
        String timeFormat = new GBPrefs(new Prefs(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()))).getTimeFormat();
        Logger logger = LOG;
        logger.info("Setting time format to " + timeFormat);
        if (timeFormat.equals("24h")) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.DATEFORMAT_TIME_24_HOURS);
        } else {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.DATEFORMAT_TIME_12_HOURS);
        }
        return this;
    }

    private HuamiSupport setGoalNotification(TransactionBuilder builder) {
        boolean enable = HuamiCoordinator.getGoalNotification();
        Logger logger = LOG;
        logger.info("Setting goal notification to " + enable);
        if (enable) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_ENABLE_GOAL_NOTIFICATION);
        } else {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISABLE_GOAL_NOTIFICATION);
        }
        return this;
    }

    private HuamiSupport setActivateDisplayOnLiftWrist(TransactionBuilder builder) {
        ActivateDisplayOnLift displayOnLift = HuamiCoordinator.getActivateDisplayOnLiftWrist(getContext(), this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Setting activate display on lift wrist to " + displayOnLift);
        int i = C11848.f179xfaaf2ec9[displayOnLift.ordinal()];
        if (i == 1) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_ENABLE_DISPLAY_ON_LIFT_WRIST);
        } else if (i == 2) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISABLE_DISPLAY_ON_LIFT_WRIST);
        } else if (i == 3) {
            byte[] cmd = (byte[]) HuamiService.COMMAND_SCHEDULE_DISPLAY_ON_LIFT_WRIST.clone();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(HuamiCoordinator.getDisplayOnLiftStart(this.gbDevice.getAddress()));
            cmd[4] = (byte) calendar.get(11);
            cmd[5] = (byte) calendar.get(12);
            calendar.setTime(HuamiCoordinator.getDisplayOnLiftEnd(this.gbDevice.getAddress()));
            cmd[6] = (byte) calendar.get(11);
            cmd[7] = (byte) calendar.get(12);
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), cmd);
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public HuamiSupport setDisplayItems(TransactionBuilder builder) {
        Set<String> pages = HuamiCoordinator.getDisplayItems(this.gbDevice.getAddress());
        Logger logger = LOG;
        StringBuilder sb = new StringBuilder();
        sb.append("Setting display items to ");
        sb.append(pages == null ? "none" : pages);
        logger.info(sb.toString());
        byte[] data = (byte[]) HuamiService.COMMAND_CHANGE_SCREENS.clone();
        if (pages != null) {
            if (pages.contains(MiBandConst.PREF_MI2_DISPLAY_ITEM_STEPS)) {
                int i = HuamiService.SCREEN_CHANGE_BYTE;
                data[i] = (byte) (data[i] | HuamiService.DISPLAY_ITEM_BIT_STEPS);
            }
            if (pages.contains(MiBandConst.PREF_MI2_DISPLAY_ITEM_DISTANCE)) {
                int i2 = HuamiService.SCREEN_CHANGE_BYTE;
                data[i2] = (byte) (data[i2] | HuamiService.DISPLAY_ITEM_BIT_DISTANCE);
            }
            if (pages.contains(MiBandConst.PREF_MI2_DISPLAY_ITEM_CALORIES)) {
                int i3 = HuamiService.SCREEN_CHANGE_BYTE;
                data[i3] = (byte) (data[i3] | HuamiService.DISPLAY_ITEM_BIT_CALORIES);
            }
            if (pages.contains(MiBandConst.PREF_MI2_DISPLAY_ITEM_HEART_RATE)) {
                int i4 = HuamiService.SCREEN_CHANGE_BYTE;
                data[i4] = (byte) (data[i4] | HuamiService.DISPLAY_ITEM_BIT_HEART_RATE);
            }
            if (pages.contains(MiBandConst.PREF_MI2_DISPLAY_ITEM_BATTERY)) {
                int i5 = HuamiService.SCREEN_CHANGE_BYTE;
                data[i5] = (byte) (data[i5] | HuamiService.DISPLAY_ITEM_BIT_BATTERY);
            }
        }
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), data);
        return this;
    }

    private HuamiSupport setRotateWristToSwitchInfo(TransactionBuilder builder) {
        boolean enable = HuamiCoordinator.getRotateWristToSwitchInfo(this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Setting rotate wrist to cycle info to " + enable);
        if (enable) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_ENABLE_ROTATE_WRIST_TO_SWITCH_INFO);
        } else {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISABLE_ROTATE_WRIST_TO_SWITCH_INFO);
        }
        return this;
    }

    private HuamiSupport setDisplayCaller(TransactionBuilder builder) {
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_ENABLE_DISPLAY_CALLER);
        return this;
    }

    private HuamiSupport setDoNotDisturb(TransactionBuilder builder) {
        DoNotDisturb doNotDisturb = HuamiCoordinator.getDoNotDisturb(this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Setting do not disturb to " + doNotDisturb);
        int i = C11848.f182x3c9ceaee[doNotDisturb.ordinal()];
        if (i == 1) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DO_NOT_DISTURB_OFF);
        } else if (i == 2) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DO_NOT_DISTURB_AUTOMATIC);
        } else if (i == 3) {
            byte[] data = (byte[]) HuamiService.COMMAND_DO_NOT_DISTURB_SCHEDULED.clone();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(HuamiCoordinator.getDoNotDisturbStart(this.gbDevice.getAddress()));
            data[HuamiService.DND_BYTE_START_HOURS] = (byte) calendar.get(11);
            data[HuamiService.DND_BYTE_START_MINUTES] = (byte) calendar.get(12);
            calendar.setTime(HuamiCoordinator.getDoNotDisturbEnd(this.gbDevice.getAddress()));
            data[HuamiService.DND_BYTE_END_HOURS] = (byte) calendar.get(11);
            data[HuamiService.DND_BYTE_END_MINUTES] = (byte) calendar.get(12);
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), data);
        }
        return this;
    }

    private HuamiSupport setInactivityWarnings(TransactionBuilder builder) {
        boolean enable = HuamiCoordinator.getInactivityWarnings();
        Logger logger = LOG;
        logger.info("Setting inactivity warnings to " + enable);
        if (enable) {
            byte[] data = (byte[]) HuamiService.COMMAND_ENABLE_INACTIVITY_WARNINGS.clone();
            data[HuamiService.INACTIVITY_WARNINGS_THRESHOLD] = (byte) HuamiCoordinator.getInactivityWarningsThreshold();
            Calendar calendar = GregorianCalendar.getInstance();
            boolean enableDnd = HuamiCoordinator.getInactivityWarningsDnd();
            Date intervalStart = HuamiCoordinator.getInactivityWarningsStart();
            Date intervalEnd = HuamiCoordinator.getInactivityWarningsEnd();
            Date dndStart = HuamiCoordinator.getInactivityWarningsDndStart();
            Date dndEnd = HuamiCoordinator.getInactivityWarningsDndEnd();
            calendar.setTime(intervalStart);
            data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_1_START_HOURS] = (byte) calendar.get(11);
            data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_1_START_MINUTES] = (byte) calendar.get(12);
            if (enableDnd) {
                calendar.setTime(dndStart);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_1_END_HOURS] = (byte) calendar.get(11);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_1_END_MINUTES] = (byte) calendar.get(12);
                calendar.setTime(dndEnd);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_2_START_HOURS] = (byte) calendar.get(11);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_2_START_MINUTES] = (byte) calendar.get(12);
                calendar.setTime(intervalEnd);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_2_END_HOURS] = (byte) calendar.get(11);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_2_END_MINUTES] = (byte) calendar.get(12);
            } else {
                calendar.setTime(intervalEnd);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_1_END_HOURS] = (byte) calendar.get(11);
                data[HuamiService.INACTIVITY_WARNINGS_INTERVAL_1_END_MINUTES] = (byte) calendar.get(12);
            }
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), data);
        } else {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISABLE_INACTIVITY_WARNINGS);
        }
        return this;
    }

    private HuamiSupport setDisconnectNotification(TransactionBuilder builder) {
        DisconnectNotificationSetting disconnectNotificationSetting = HuamiCoordinator.getDisconnectNotificationSetting(getContext(), this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Setting disconnect notification to " + disconnectNotificationSetting);
        int i = C11848.f180xee692cb9[disconnectNotificationSetting.ordinal()];
        if (i == 1) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_ENABLE_DISCONNECT_NOTIFCATION);
        } else if (i == 2) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISABLE_DISCONNECT_NOTIFCATION);
        } else if (i == 3) {
            byte[] cmd = (byte[]) HuamiService.COMMAND_ENABLE_DISCONNECT_NOTIFCATION.clone();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(HuamiCoordinator.getDisconnectNotificationStart(this.gbDevice.getAddress()));
            cmd[4] = (byte) calendar.get(11);
            cmd[5] = (byte) calendar.get(12);
            calendar.setTime(HuamiCoordinator.getDisconnectNotificationEnd(this.gbDevice.getAddress()));
            cmd[6] = (byte) calendar.get(11);
            cmd[7] = (byte) calendar.get(12);
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), cmd);
        }
        return this;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport$8 */
    static /* synthetic */ class C11848 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$devices$huami$ActivateDisplayOnLift */
        static final /* synthetic */ int[] f179xfaaf2ec9 = new int[ActivateDisplayOnLift.values().length];

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$devices$huami$DisconnectNotificationSetting */
        static final /* synthetic */ int[] f180xee692cb9 = new int[DisconnectNotificationSetting.values().length];

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$devices$miband$DateTimeDisplay */
        static final /* synthetic */ int[] f181xae499cf0 = new int[DateTimeDisplay.values().length];

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$devices$miband$DoNotDisturb */
        static final /* synthetic */ int[] f182x3c9ceaee = new int[DoNotDisturb.values().length];

        static {
            try {
                f180xee692cb9[DisconnectNotificationSetting.ON.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f180xee692cb9[DisconnectNotificationSetting.OFF.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f180xee692cb9[DisconnectNotificationSetting.SCHEDULED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f182x3c9ceaee[DoNotDisturb.OFF.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f182x3c9ceaee[DoNotDisturb.AUTOMATIC.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f182x3c9ceaee[DoNotDisturb.SCHEDULED.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f179xfaaf2ec9[ActivateDisplayOnLift.ON.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f179xfaaf2ec9[ActivateDisplayOnLift.OFF.ordinal()] = 2;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f179xfaaf2ec9[ActivateDisplayOnLift.SCHEDULED.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f181xae499cf0[DateTimeDisplay.TIME.ordinal()] = 1;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f181xae499cf0[DateTimeDisplay.DATE_TIME.ordinal()] = 2;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    private HuamiSupport setDistanceUnit(TransactionBuilder builder) {
        MiBandConst.DistanceUnit unit = HuamiCoordinator.getDistanceUnit();
        Logger logger = LOG;
        logger.info("Setting distance unit to " + unit);
        if (unit == MiBandConst.DistanceUnit.METRIC) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISTANCE_UNIT_METRIC);
        } else {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISTANCE_UNIT_IMPERIAL);
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public HuamiSupport setBandScreenUnlock(TransactionBuilder builder) {
        boolean enable = MiBand3Coordinator.getBandScreenUnlock(this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Setting band screen unlock to " + enable);
        if (enable) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), MiBand3Service.COMMAND_ENABLE_BAND_SCREEN_UNLOCK);
        } else {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), MiBand3Service.COMMAND_DISABLE_BAND_SCREEN_UNLOCK);
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public HuamiSupport setLanguage(TransactionBuilder builder) {
        byte[] command_old;
        String localeString = GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).getString(HuamiConst.PREF_LANGUAGE, "auto");
        if (localeString == null || localeString.equals("auto")) {
            String language = Locale.getDefault().getLanguage();
            String country = Locale.getDefault().getCountry();
            if (country == null) {
                country = language;
            }
            localeString = language + "_" + country.toUpperCase();
        }
        LOG.info("Setting device to locale: " + localeString);
        final byte[] command_new = (byte[]) HuamiService.COMMAND_SET_LANGUAGE_NEW_TEMPLATE.clone();
        System.arraycopy(localeString.getBytes(), 0, command_new, 3, localeString.getBytes().length);
        String substring = localeString.substring(0, 2);
        char c = 65535;
        int hashCode = substring.hashCode();
        if (hashCode != 3246) {
            if (hashCode == 3886 && substring.equals("zh")) {
                c = 1;
            }
        } else if (substring.equals("es")) {
            c = 0;
        }
        if (c == 0) {
            command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_SPANISH;
        } else if (c != 1) {
            command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_ENGLISH;
        } else if (localeString.equals("zh_CN")) {
            command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_SIMPLIFIED_CHINESE;
        } else {
            command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_TRADITIONAL_CHINESE;
        }
        final byte[] finalCommand_old = command_old;
        builder.add(new ConditionalWriteAction(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION)) {
            /* access modifiers changed from: protected */
            public byte[] checkCondition() {
                if ((HuamiSupport.this.gbDevice.getType() != DeviceType.AMAZFITBIP || new Version(HuamiSupport.this.gbDevice.getFirmwareVersion()).compareTo(new Version("0.1.0.77")) >= 0) && (HuamiSupport.this.gbDevice.getType() != DeviceType.AMAZFITCOR || new Version(HuamiSupport.this.gbDevice.getFirmwareVersion()).compareTo(new Version("1.0.7.23")) >= 0)) {
                    return command_new;
                }
                return finalCommand_old;
            }
        });
        return this;
    }

    private HuamiSupport setExposeHRThridParty(TransactionBuilder builder) {
        boolean enable = HuamiCoordinator.getExposeHRThirdParty(this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Setting exposure of HR to third party apps to: " + enable);
        if (enable) {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_ENBALE_HR_CONNECTION);
        } else {
            builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_DISABLE_HR_CONNECTION);
        }
        return this;
    }

    private void writeToChunked(TransactionBuilder builder, int type, byte[] data) {
        int MAX_CHUNKLENGTH = this.mMTU - 6;
        int remaining = data.length;
        byte count = 0;
        while (remaining > 0) {
            int copybytes = Math.min(remaining, MAX_CHUNKLENGTH);
            byte[] chunk = new byte[(copybytes + 3)];
            byte flags = 0;
            if (remaining <= MAX_CHUNKLENGTH) {
                flags = (byte) (0 | 128);
                if (count == 0) {
                    flags = (byte) (flags | 64);
                }
            } else if (count > 0) {
                flags = (byte) (0 | 64);
            }
            chunk[0] = 0;
            chunk[1] = (byte) (flags | type);
            chunk[2] = (byte) (count & 255);
            System.arraycopy(data, count * MAX_CHUNKLENGTH, chunk, 3, copybytes);
            builder.write(this.characteristicChunked, chunk);
            remaining -= copybytes;
            count = (byte) (count + 1);
        }
    }

    /* access modifiers changed from: protected */
    public HuamiSupport requestGPSVersion(TransactionBuilder builder) {
        LOG.info("Requesting GPS version");
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_REQUEST_GPS_VERSION);
        return this;
    }

    private HuamiSupport requestAlarms(TransactionBuilder builder) {
        LOG.info("Requesting alarms");
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), HuamiService.COMMAND_REQUEST_ALARMS);
        return this;
    }

    public String customStringFilter(String inputString) {
        if (HuamiCoordinator.getUseCustomFont(this.gbDevice.getAddress())) {
            return convertEmojiToCustomFont(inputString);
        }
        return inputString;
    }

    private String convertEmojiToCustomFont(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (Character.isHighSurrogate(charAt)) {
                int i2 = i + 1;
                try {
                    int codePoint = Character.toCodePoint(charAt, str.charAt(i2));
                    if (codePoint >= 127744) {
                        if (codePoint <= 129510) {
                            sb.append((char) (codePoint - 83712));
                            i = i2;
                        }
                    }
                    sb.append(charAt);
                } catch (StringIndexOutOfBoundsException e) {
                    LOG.warn("error while converting emoji to custom font", (Throwable) e);
                    sb.append(charAt);
                }
            } else {
                sb.append(charAt);
            }
            i++;
        }
        return sb.toString();
    }

    public void phase2Initialize(TransactionBuilder builder) {
        LOG.info("phase2Initialize...");
        requestBatteryInfo(builder);
    }

    public void phase3Initialize(TransactionBuilder builder) {
        LOG.info("phase3Initialize...");
        setDateDisplay(builder);
        setTimeFormat(builder);
        setUserInfo(builder);
        setDistanceUnit(builder);
        setWearLocation(builder);
        setFitnessGoal(builder);
        setDisplayItems(builder);
        setDoNotDisturb(builder);
        setRotateWristToSwitchInfo(builder);
        setActivateDisplayOnLiftWrist(builder);
        setDisplayCaller(builder);
        setGoalNotification(builder);
        setInactivityWarnings(builder);
        setHeartrateSleepSupport(builder);
        setDisconnectNotification(builder);
        setExposeHRThridParty(builder);
        setHeartrateMeasurementInterval(builder, getHeartRateMeasurementInterval());
        requestAlarms(builder);
    }

    private int getHeartRateMeasurementInterval() {
        return GBApplication.getPrefs().getInt(ZeTimeConstants.PREF_ZETIME_HEARTRATE_INTERVAL, 0) / 60;
    }

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new MiBand2FWHelper(uri, context);
    }

    public UpdateFirmwareOperation createUpdateFirmwareOperation(Uri uri) {
        return new UpdateFirmwareOperation(uri, this);
    }

    public int getMTU() {
        return this.mMTU;
    }
}
