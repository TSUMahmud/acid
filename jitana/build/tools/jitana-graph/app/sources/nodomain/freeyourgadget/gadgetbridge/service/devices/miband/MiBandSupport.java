package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import com.github.mikephil.charting.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandDateConverter;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandService;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
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
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.AbortTransactionAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.ConditionalWriteAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.WriteAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertCategory;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations.FetchActivityOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations.UpdateFirmwareOperation;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.NotificationUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiBandSupport extends AbstractBTLEDeviceSupport {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBandSupport.class);
    public static final boolean MI_1A_HR_FW_UPDATE_TEST_MODE_ENABLED = false;
    static final byte[] factoryReset = {9};
    static final byte[] reboot = {12};
    static final byte[] startHeartMeasurementContinuous = {21, 1, 1};
    static final byte[] startHeartMeasurementManual = {21, 2, 1};
    static final byte[] startHeartMeasurementSleep = {21, 0, 1};
    static final byte[] startRealTimeStepsNotifications = {3, 1};
    private static final byte[] startSensorRead = {18, 1};
    static final byte[] stopHeartMeasurementContinuous = {21, 1, 0};
    static final byte[] stopHeartMeasurementManual = {21, 2, 0};
    static final byte[] stopHeartMeasurementSleep = {21, 0, 0};
    static final byte[] stopRealTimeStepsNotifications = {3, 0};
    private static final byte[] stopSensorRead = {18, 0};
    private boolean alarmClockRinging;
    private boolean alarmClockRining;
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    /* access modifiers changed from: private */
    public volatile boolean isLocatingDevice;
    private volatile boolean isReadingSensorData;
    /* access modifiers changed from: private */
    public DeviceInfo mDeviceInfo;
    private RealtimeSamplesSupport realtimeSamplesSupport;
    private volatile boolean telephoneRinging;
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public MiBandSupport() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(MiBandService.UUID_SERVICE_MIBAND_SERVICE);
        addSupportedService(MiBandService.UUID_SERVICE_HEART_RATE);
        addSupportedService(GattService.UUID_SERVICE_IMMEDIATE_ALERT);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        enableNotifications(builder, true).setLowLatency(builder).readDate(builder).pair(builder).requestDeviceInfo(builder).sendUserInfo(builder).checkAuthenticationNeeded(builder, getDevice()).setWearLocation(builder).setHeartrateSleepSupport(builder).setFitnessGoal(builder).enableFurtherNotifications(builder, true).setCurrentTime(builder).requestBatteryInfo(builder).setHighLatency(builder).setInitialized(builder);
        return builder;
    }

    private MiBandSupport readDate(TransactionBuilder builder) {
        builder.read(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_DATE_TIME));
        return this;
    }

    public MiBandSupport setLowLatency(TransactionBuilder builder) {
        builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_LE_PARAMS), getLowLatency());
        return this;
    }

    public MiBandSupport setHighLatency(TransactionBuilder builder) {
        builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_LE_PARAMS), getHighLatency());
        return this;
    }

    private MiBandSupport checkAuthenticationNeeded(TransactionBuilder builder, GBDevice device) {
        builder.add(new CheckAuthenticationNeededAction(device));
        return this;
    }

    private void setInitialized(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
    }

    private MiBandSupport enableNotifications(TransactionBuilder builder, boolean enable) {
        builder.notify(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_NOTIFICATION), enable);
        return this;
    }

    private MiBandSupport enableFurtherNotifications(TransactionBuilder builder, boolean enable) {
        builder.notify(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_REALTIME_STEPS), enable).notify(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_ACTIVITY_DATA), enable).notify(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_BATTERY), enable).notify(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_SENSOR_DATA), enable);
        BluetoothGattCharacteristic heartrateCharacteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT);
        if (heartrateCharacteristic != null) {
            builder.notify(heartrateCharacteristic, enable);
        }
        return this;
    }

    public boolean useAutoConnect() {
        return true;
    }

    public boolean connectFirstTime() {
        for (int i = 0; i < 5; i++) {
            if (connect()) {
                return true;
            }
        }
        return false;
    }

    public DeviceInfo getDeviceInfo() {
        return this.mDeviceInfo;
    }

    private MiBandSupport sendDefaultNotification(TransactionBuilder builder, SimpleNotification simpleNotification, short repeat, BtLEAction extraAction) {
        Logger logger = LOG;
        logger.info("Sending notification to MiBand: (" + repeat + " times)");
        NotificationStrategy strategy = getNotificationStrategy();
        for (short i = 0; i < repeat; i = (short) (i + 1)) {
            strategy.sendDefaultNotification(builder, simpleNotification, extraAction);
        }
        return this;
    }

    private MiBandSupport sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        getNotificationStrategy().sendCustomNotification(vibrationProfile, simpleNotification, flashTimes, flashColour, originalColour, flashDuration, extraAction, builder);
        LOG.info("Sending notification to MiBand");
        return this;
    }

    private NotificationStrategy getNotificationStrategy() {
        DeviceInfo deviceInfo = this.mDeviceInfo;
        if (deviceInfo == null) {
            return new NoNotificationStrategy();
        }
        if (deviceInfo.getFirmwareVersion() < 16779790) {
            return new V1NotificationStrategy(this);
        }
        return new V2NotificationStrategy(this);
    }

    private MiBandSupport sendUserInfo(TransactionBuilder builder) {
        LOG.debug("Writing User Info!");
        builder.add(new BtLEAction(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_USER_INFO)) {
            public boolean expectsResult() {
                return true;
            }

            public boolean run(BluetoothGatt gatt) {
                return new WriteAction(getCharacteristic(), MiBandCoordinator.getAnyUserInfo(MiBandSupport.this.getDevice().getAddress()).getData(MiBandSupport.this.mDeviceInfo)).run(gatt);
            }
        });
        return this;
    }

    private MiBandSupport requestBatteryInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Battery Info!");
        builder.read(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_BATTERY));
        return this;
    }

    private MiBandSupport requestDeviceInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Device Info!");
        builder.read(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_DEVICE_INFO));
        builder.read(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_GAP_DEVICE_NAME));
        return this;
    }

    private MiBandSupport pair(TransactionBuilder transaction) {
        if (GBApplication.getPrefs().getBoolean(MiBandConst.PREF_MIBAND_DONT_ACK_TRANSFER, false)) {
            LOG.info("Attempting to pair MI device...");
            BluetoothGattCharacteristic characteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_PAIR);
            if (characteristic != null) {
                transaction.write(characteristic, new byte[]{2});
            } else {
                LOG.info("Unable to pair MI device -- characteristic not available");
            }
        }
        return this;
    }

    private MiBandSupport setFitnessGoal(TransactionBuilder transaction) {
        LOG.info("Attempting to set Fitness Goal...");
        BluetoothGattCharacteristic characteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT);
        if (characteristic != null) {
            int fitnessGoal = GBApplication.getPrefs().getInt("mi_fitness_goal", ActivityUser.defaultUserStepsGoal);
            transaction.write(characteristic, new byte[]{5, 0, (byte) (fitnessGoal & 255), (byte) ((fitnessGoal >>> 8) & 255)});
        } else {
            LOG.info("Unable to set Fitness Goal");
        }
        return this;
    }

    private MiBandSupport setWearLocation(TransactionBuilder transaction) {
        LOG.info("Attempting to set wear location...");
        BluetoothGattCharacteristic characteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT);
        if (characteristic != null) {
            transaction.add(new ConditionalWriteAction(characteristic) {
                /* access modifiers changed from: protected */
                public byte[] checkCondition() {
                    if (MiBandSupport.this.getDeviceInfo() != null && MiBandSupport.this.getDeviceInfo().isAmazFit()) {
                        return null;
                    }
                    return new byte[]{15, (byte) MiBandCoordinator.getWearLocation(MiBandSupport.this.getDevice().getAddress())};
                }
            });
        } else {
            LOG.info("Unable to set Wear Location");
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
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    private MiBandSupport setHeartrateSleepSupport(TransactionBuilder builder) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT);
        if (characteristic != null) {
            builder.add(new ConditionalWriteAction(characteristic) {
                /* access modifiers changed from: protected */
                public byte[] checkCondition() {
                    if (!MiBandSupport.this.supportsHeartRate()) {
                        return null;
                    }
                    if (MiBandCoordinator.getHeartrateSleepSupport(MiBandSupport.this.getDevice().getAddress())) {
                        MiBandSupport.LOG.info("Enabling heartrate sleep support...");
                        return MiBandSupport.startHeartMeasurementSleep;
                    }
                    MiBandSupport.LOG.info("Disabling heartrate sleep support...");
                    return MiBandSupport.stopHeartMeasurementSleep;
                }
            });
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

    private void performPreferredNotification(String task, SimpleNotification simpleNotification, String notificationOrigin, BtLEAction extraAction) {
        String str = notificationOrigin;
        try {
            TransactionBuilder builder = performInitialized(task);
            Prefs prefs = GBApplication.getPrefs();
            int preferredVibrateDuration = getPreferredVibrateDuration(str, prefs);
            int preferredVibratePause = getPreferredVibratePause(str, prefs);
            short vibrateTimes = getPreferredVibrateCount(str, prefs);
            VibrationProfile profile = getPreferredVibrateProfile(str, prefs, vibrateTimes);
            int flashTimes = getPreferredFlashCount(str, prefs);
            int flashColour = getPreferredFlashColour(str, prefs);
            int originalColour = getPreferredOriginalColour(str, prefs);
            int flashDuration = getPreferredFlashDuration(str, prefs);
            int i = flashDuration;
            short s = vibrateTimes;
            sendCustomNotification(profile, simpleNotification, flashTimes, flashColour, originalColour, (long) flashDuration, extraAction, builder);
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to send notification to MI device", (Throwable) ex);
        }
    }

    private int getPreferredFlashDuration(String notificationOrigin, Prefs prefs) {
        return MiBandConst.getNotificationPrefIntValue(MiBandConst.FLASH_DURATION, notificationOrigin, prefs, 500);
    }

    private int getPreferredOriginalColour(String notificationOrigin, Prefs prefs) {
        return MiBandConst.getNotificationPrefIntValue(MiBandConst.FLASH_ORIGINAL_COLOUR, notificationOrigin, prefs, 1);
    }

    private int getPreferredFlashColour(String notificationOrigin, Prefs prefs) {
        return MiBandConst.getNotificationPrefIntValue(MiBandConst.FLASH_COLOUR, notificationOrigin, prefs, 1);
    }

    private int getPreferredFlashCount(String notificationOrigin, Prefs prefs) {
        return MiBandConst.getNotificationPrefIntValue(MiBandConst.FLASH_COUNT, notificationOrigin, prefs, 10);
    }

    private int getPreferredVibratePause(String notificationOrigin, Prefs prefs) {
        return MiBandConst.getNotificationPrefIntValue(MiBandConst.VIBRATION_PAUSE, notificationOrigin, prefs, 500);
    }

    private short getPreferredVibrateCount(String notificationOrigin, Prefs prefs) {
        return (short) Math.min(32767, MiBandConst.getNotificationPrefIntValue(MiBandConst.VIBRATION_COUNT, notificationOrigin, prefs, 3));
    }

    private int getPreferredVibrateDuration(String notificationOrigin, Prefs prefs) {
        return MiBandConst.getNotificationPrefIntValue(MiBandConst.VIBRATION_DURATION, notificationOrigin, prefs, 500);
    }

    private VibrationProfile getPreferredVibrateProfile(String notificationOrigin, Prefs prefs, short repeat) {
        return VibrationProfile.getProfile(MiBandConst.getNotificationPrefStringValue(MiBandConst.VIBRATION_PROFILE, notificationOrigin, prefs, MiBandConst.DEFAULT_VALUE_VIBRATION_PROFILE), repeat);
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        try {
            BluetoothGattCharacteristic characteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT);
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

    public void onNotification(NotificationSpec notificationSpec) {
        if (notificationSpec.type == NotificationType.GENERIC_ALARM_CLOCK) {
            onAlarmClock(notificationSpec);
            return;
        }
        String origin = notificationSpec.type.getGenericType();
        performPreferredNotification(origin + " received", (SimpleNotification) null, origin, (BtLEAction) null);
    }

    private void onAlarmClock(NotificationSpec notificationSpec) {
        this.alarmClockRining = true;
        performPreferredNotification("alarm clock ringing", new SimpleNotification(NotificationUtils.getPreferredTextFor(notificationSpec, 40, 40, getContext()), AlertCategory.HighPriorityAlert, (NotificationType) null), MiBandConst.ORIGIN_ALARM_CLOCK, new AbortTransactionAction() {
            /* access modifiers changed from: protected */
            public boolean shouldAbort() {
                return !MiBandSupport.this.isAlarmClockRinging();
            }
        });
    }

    public void onDeleteNotification(int id) {
        this.alarmClockRining = false;
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("Set date and time");
            setCurrentTime(builder);
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to set time on MI device", (Throwable) ex);
        }
        sendCalendarEvents();
    }

    private MiBandSupport setCurrentTime(TransactionBuilder builder) {
        Calendar now = GregorianCalendar.getInstance();
        Date date = now.getTime();
        Logger logger = LOG;
        logger.info("Sending current time to Mi Band: " + DateTimeUtils.formatDate(date) + " (" + date.toGMTString() + ")");
        byte[] nowBytes = MiBandDateConverter.calendarToRawBytes(now, this.gbDevice.getAddress());
        byte[] time = {nowBytes[0], nowBytes[1], nowBytes[2], nowBytes[3], nowBytes[4], nowBytes[5], 15, 15, 15, 15, 15, 15};
        BluetoothGattCharacteristic characteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_DATE_TIME);
        if (characteristic != null) {
            builder.write(characteristic, time);
        } else {
            LOG.info("Unable to set time -- characteristic not available");
        }
        return this;
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command == 2) {
            this.telephoneRinging = true;
            performPreferredNotification("incoming call", new SimpleNotification(NotificationUtils.getPreferredTextFor(callSpec), AlertCategory.IncomingCall, (NotificationType) null), MiBandConst.ORIGIN_INCOMING_CALL, new AbortTransactionAction() {
                /* access modifiers changed from: protected */
                public boolean shouldAbort() {
                    return !MiBandSupport.this.isTelephoneRinging();
                }
            });
        } else if (callSpec.command == 5 || callSpec.command == 6) {
            this.telephoneRinging = false;
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
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
    }

    public void onReset(int flags) {
        try {
            TransactionBuilder builder = performInitialized("reset");
            if ((flags & 2) != 0) {
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), factoryReset);
            } else {
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), reboot);
            }
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to reset", (Throwable) ex);
        }
    }

    public void onHeartRateTest() {
        if (supportsHeartRate()) {
            try {
                TransactionBuilder builder = performInitialized("HeartRateTest");
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT), stopHeartMeasurementContinuous);
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT), stopHeartMeasurementManual);
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT), startHeartMeasurementManual);
                builder.queue(getQueue());
            } catch (IOException ex) {
                LOG.error("Unable to read HearRate in  MI1S", (Throwable) ex);
            }
        } else {
            C1238GB.toast(getContext(), "Heart rate is not supported on this device", 1, 3);
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        if (supportsHeartRate()) {
            try {
                TransactionBuilder builder = performInitialized("EnableRealtimeHeartRateMeasurement");
                if (enable) {
                    builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT), stopHeartMeasurementManual);
                    builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT), startHeartMeasurementContinuous);
                } else {
                    builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT), stopHeartMeasurementContinuous);
                }
                builder.queue(getQueue());
                enableRealtimeSamplesTimer(enable);
            } catch (IOException ex) {
                LOG.error("Unable to enable realtime heart rate measurement in  MI1S", (Throwable) ex);
            }
        }
    }

    public boolean supportsHeartRate() {
        return getDeviceInfo() != null && getDeviceInfo().supportsHeartrate();
    }

    public void onFindDevice(boolean start) {
        this.isLocatingDevice = start;
        if (start) {
            performDefaultNotification("locating device", new SimpleNotification(getContext().getString(C0889R.string.find_device_you_found_it), AlertCategory.HighPriorityAlert, (NotificationType) null), 255, new AbortTransactionAction() {
                /* access modifiers changed from: protected */
                public boolean shouldAbort() {
                    return !MiBandSupport.this.isLocatingDevice;
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
            LOG.error("Unable to fetch MI activity data", (Throwable) ex);
        }
    }

    public void onEnableRealtimeSteps(boolean enable) {
        try {
            BluetoothGattCharacteristic controlPoint = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT);
            if (enable) {
                performInitialized("Read realtime steps").read(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_REALTIME_STEPS)).queue(getQueue());
            }
            performInitialized(enable ? "Enabling realtime steps notifications" : "Disabling realtime steps notifications").write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_LE_PARAMS), enable ? getLowLatency() : getHighLatency()).write(controlPoint, enable ? startRealTimeStepsNotifications : stopRealTimeStepsNotifications).queue(getQueue());
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
            new UpdateFirmwareOperation(uri, this).perform();
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

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        UUID characteristicUUID = characteristic.getUuid();
        if (MiBandService.UUID_CHARACTERISTIC_BATTERY.equals(characteristicUUID)) {
            handleBatteryInfo(characteristic.getValue(), 0);
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_NOTIFICATION.equals(characteristicUUID)) {
            handleNotificationNotif(characteristic.getValue());
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_REALTIME_STEPS.equals(characteristicUUID)) {
            handleRealtimeSteps(characteristic.getValue());
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT.equals(characteristicUUID)) {
            handleHeartrate(characteristic.getValue());
            return true;
        } else {
            if (MiBandService.UUID_CHARACTERISTIC_SENSOR_DATA.equals(characteristicUUID)) {
                handleSensorData(characteristic.getValue());
            } else {
                Logger logger = LOG;
                logger.info("Unhandled characteristic changed: " + characteristicUUID);
                logMessageContent(characteristic.getValue());
            }
            return false;
        }
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        UUID characteristicUUID = characteristic.getUuid();
        if (MiBandService.UUID_CHARACTERISTIC_DEVICE_INFO.equals(characteristicUUID)) {
            handleDeviceInfo(characteristic.getValue(), status);
            return true;
        } else if (GattCharacteristic.UUID_CHARACTERISTIC_GAP_DEVICE_NAME.equals(characteristicUUID)) {
            handleDeviceName(characteristic.getValue(), status);
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_BATTERY.equals(characteristicUUID)) {
            handleBatteryInfo(characteristic.getValue(), status);
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT.equals(characteristicUUID)) {
            logHeartrate(characteristic.getValue(), status);
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_DATE_TIME.equals(characteristicUUID)) {
            logDate(characteristic.getValue(), status);
            return true;
        } else {
            Logger logger = LOG;
            logger.info("Unhandled characteristic read: " + characteristicUUID);
            logMessageContent(characteristic.getValue());
            return false;
        }
    }

    public boolean onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        UUID characteristicUUID = characteristic.getUuid();
        if (MiBandService.UUID_CHARACTERISTIC_PAIR.equals(characteristicUUID)) {
            handlePairResult(characteristic.getValue(), status);
            return true;
        } else if (MiBandService.UUID_CHARACTERISTIC_USER_INFO.equals(characteristicUUID)) {
            handleUserInfoResult(characteristic.getValue(), status);
            return true;
        } else if (!MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT.equals(characteristicUUID)) {
            return false;
        } else {
            handleControlPointResult(characteristic.getValue(), status);
            return true;
        }
    }

    public void logDate(byte[] value, int status) {
        if (status == 0) {
            GregorianCalendar calendar = MiBandDateConverter.rawBytesToCalendar(value, this.gbDevice.getAddress());
            Logger logger = LOG;
            logger.info("Got Mi Band Date: " + DateTimeUtils.formatDateTime(calendar.getTime()));
            return;
        }
        logMessageContent(value);
    }

    public void logHeartrate(byte[] value, int status) {
        if (status != 0 || value == null) {
            logMessageContent(value);
            return;
        }
        LOG.info("Got heartrate:");
        if (value.length == 2 && value[0] == 6) {
            Context context = getContext();
            C1238GB.toast(context, "Heart Rate measured: " + (value[1] & 255), 1, 1);
        }
    }

    private void handleHeartrate(byte[] value) {
        if (value.length == 2 && value[0] == 6) {
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
        int steps = BLETypeConversions.toUint16(value);
        if (LOG.isDebugEnabled()) {
            Logger logger = LOG;
            logger.debug("realtime steps: " + steps);
        }
        getRealtimeSamplesSupport().setSteps(steps);
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
                /* JADX WARNING: Code restructure failed: missing block: B:13:0x008c, code lost:
                    r2 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:14:0x008d, code lost:
                    if (r0 != null) goto L_0x008f;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
                    r0.close();
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:20:0x0097, code lost:
                    throw r2;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void doCurrentSample() {
                    /*
                        r10 = this;
                        nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0098 }
                        nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r1 = r0.getDaoSession()     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.this     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r2.getDevice()     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.entities.Device r2 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r2, r1)     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.entities.User r3 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r1)     // Catch:{ all -> 0x008a }
                        long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x008a }
                        r6 = 1000(0x3e8, double:4.94E-321)
                        long r4 = r4 / r6
                        int r5 = (int) r4     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandSampleProvider r4 = new nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandSampleProvider     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.this     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r6 = r6.gbDevice     // Catch:{ all -> 0x008a }
                        r4.<init>(r6, r1)     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.this     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample r6 = r6.createActivitySample(r2, r3, r5, r4)     // Catch:{ all -> 0x008a }
                        int r7 = r10.getHeartrateBpm()     // Catch:{ all -> 0x008a }
                        r6.setHeartRate(r7)     // Catch:{ all -> 0x008a }
                        r7 = -1
                        r6.setRawIntensity(r7)     // Catch:{ all -> 0x008a }
                        r6.setRawKind(r7)     // Catch:{ all -> 0x008a }
                        r4.addGBActivitySample(r6)     // Catch:{ all -> 0x008a }
                        int r7 = r10.getSteps()     // Catch:{ all -> 0x008a }
                        r6.setSteps(r7)     // Catch:{ all -> 0x008a }
                        org.slf4j.Logger r7 = nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.LOG     // Catch:{ all -> 0x008a }
                        boolean r7 = r7.isDebugEnabled()     // Catch:{ all -> 0x008a }
                        if (r7 == 0) goto L_0x0069
                        org.slf4j.Logger r7 = nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.LOG     // Catch:{ all -> 0x008a }
                        java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x008a }
                        r8.<init>()     // Catch:{ all -> 0x008a }
                        java.lang.String r9 = "realtime sample: "
                        r8.append(r9)     // Catch:{ all -> 0x008a }
                        r8.append(r6)     // Catch:{ all -> 0x008a }
                        java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x008a }
                        r7.debug(r8)     // Catch:{ all -> 0x008a }
                    L_0x0069:
                        android.content.Intent r7 = new android.content.Intent     // Catch:{ all -> 0x008a }
                        java.lang.String r8 = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_samples"
                        r7.<init>(r8)     // Catch:{ all -> 0x008a }
                        java.lang.String r8 = "realtime_sample"
                        android.content.Intent r7 = r7.putExtra(r8, r6)     // Catch:{ all -> 0x008a }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport r8 = nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.this     // Catch:{ all -> 0x008a }
                        android.content.Context r8 = r8.getContext()     // Catch:{ all -> 0x008a }
                        androidx.localbroadcastmanager.content.LocalBroadcastManager r8 = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(r8)     // Catch:{ all -> 0x008a }
                        r8.sendBroadcast(r7)     // Catch:{ all -> 0x008a }
                        if (r0 == 0) goto L_0x0089
                        r0.close()     // Catch:{ Exception -> 0x0098 }
                    L_0x0089:
                        goto L_0x00a2
                    L_0x008a:
                        r1 = move-exception
                        throw r1     // Catch:{ all -> 0x008c }
                    L_0x008c:
                        r2 = move-exception
                        if (r0 == 0) goto L_0x0097
                        r0.close()     // Catch:{ all -> 0x0093 }
                        goto L_0x0097
                    L_0x0093:
                        r3 = move-exception
                        r1.addSuppressed(r3)     // Catch:{ Exception -> 0x0098 }
                    L_0x0097:
                        throw r2     // Catch:{ Exception -> 0x0098 }
                    L_0x0098:
                        r0 = move-exception
                        org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.LOG
                        java.lang.String r2 = "Unable to acquire db for saving realtime samples"
                        r1.warn((java.lang.String) r2, (java.lang.Throwable) r0)
                    L_0x00a2:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport.C12027.doCurrentSample():void");
                }
            };
        }
        return this.realtimeSamplesSupport;
    }

    private void handleNotificationNotif(byte[] value) {
        if (value.length != 1) {
            LOG.error("Notifications should be 1 byte long.");
            Logger logger = LOG;
            logger.info("RECEIVED DATA WITH LENGTH: " + value.length);
            Logger logger2 = LOG;
            logger2.warn("DATA: " + C1238GB.hexdump(value, 0, value.length));
            return;
        }
        byte b = value[0];
        if (b != 5) {
            if (b == 6) {
                getDevice().setState(GBDevice.State.AUTHENTICATION_REQUIRED);
                getDevice().sendDeviceUpdateIntent(getContext());
                C1238GB.toast(getContext(), "Band needs pairing", 1, 3);
                return;
            } else if (b == 8) {
                LOG.info("Setting latency succeeded.");
                return;
            } else if (b != 10) {
                if (b == 19) {
                    LOG.info("Band needs authentication (MOTOR_AUTH)");
                    getDevice().setState(GBDevice.State.AUTHENTICATING);
                    getDevice().sendDeviceUpdateIntent(getContext());
                    return;
                } else if (b != 21) {
                    Logger logger3 = LOG;
                    logger3.warn("DATA: " + C1238GB.hexdump(value, 0, value.length));
                    return;
                }
            }
        }
        LOG.info("Band successfully authenticated");
        doInitialize();
    }

    private void doInitialize() {
        try {
            performInitialized("just initializing after authentication").queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to initialize device after authentication", (Throwable) ex);
        }
    }

    private void handleDeviceInfo(byte[] value, int status) {
        if (status == 0) {
            this.mDeviceInfo = new DeviceInfo(value);
            this.mDeviceInfo.setTest1AHRMode(false);
            if (getDeviceInfo().supportsHeartrate()) {
                getDevice().setFirmwareVersion2(MiBandFWHelper.formatFirmwareVersion(this.mDeviceInfo.getHeartrateFirmwareVersion()));
            }
            Logger logger = LOG;
            logger.warn("Device info: " + this.mDeviceInfo);
            this.versionCmd.hwVersion = this.mDeviceInfo.getHwVersion();
            this.versionCmd.fwVersion = MiBandFWHelper.formatFirmwareVersion(this.mDeviceInfo.getFirmwareVersion());
            handleGBDeviceEvent(this.versionCmd);
        }
    }

    private void handleDeviceName(byte[] value, int status) {
    }

    private void queueAlarm(Alarm alarm, TransactionBuilder builder, BluetoothGattCharacteristic characteristic) {
        byte[] alarmCalBytes = MiBandDateConverter.calendarToRawBytes(AlarmUtils.toCalendar(alarm), this.gbDevice.getAddress());
        byte[] alarmMessage = new byte[11];
        int i = 0;
        alarmMessage[0] = 4;
        alarmMessage[1] = (byte) alarm.getPosition();
        alarmMessage[2] = alarm.getEnabled() ? (byte) 1 : 0;
        alarmMessage[3] = alarmCalBytes[0];
        alarmMessage[4] = alarmCalBytes[1];
        alarmMessage[5] = alarmCalBytes[2];
        alarmMessage[6] = alarmCalBytes[3];
        alarmMessage[7] = alarmCalBytes[4];
        alarmMessage[8] = alarmCalBytes[5];
        if (alarm.getSmartWakeup()) {
            i = 30;
        }
        alarmMessage[9] = (byte) i;
        alarmMessage[10] = (byte) alarm.getRepetition();
        builder.write(characteristic, alarmMessage);
    }

    private void handleControlPointResult(byte[] value, int status) {
        if (status != 0) {
            LOG.warn("Could not write to the control point.");
        }
        Logger logger = LOG;
        StringBuilder sb = new StringBuilder();
        sb.append("handleControlPoint write status:");
        sb.append(status);
        sb.append("; length: ");
        sb.append(value != null ? Integer.valueOf(value.length) : "(null)");
        logger.info(sb.toString());
        if (value != null) {
            for (byte b : value) {
                LOG.info("handleControlPoint WROTE DATA:" + String.format("0x%8x", new Object[]{Byte.valueOf(b)}));
            }
            return;
        }
        LOG.warn("handleControlPoint WROTE null");
    }

    private void handleBatteryInfo(byte[] value, int status) {
        if (status == 0) {
            BatteryInfo info = new BatteryInfo(value);
            this.batteryCmd.level = (short) info.getLevelInPercent();
            this.batteryCmd.state = info.getState();
            this.batteryCmd.lastChargeTime = info.getLastChargeTime(this.gbDevice.getAddress());
            this.batteryCmd.numCharges = info.getNumCharges();
            handleGBDeviceEvent(this.batteryCmd);
        }
    }

    private void handleUserInfoResult(byte[] value, int status) {
    }

    private void setConnectionState(GBDevice.State newState) {
        getDevice().setState(newState);
        getDevice().sendDeviceUpdateIntent(getContext());
    }

    private void handlePairResult(byte[] pairResult, int status) {
        if (status != 0) {
            Logger logger = LOG;
            logger.info("Pairing MI device failed: " + status);
            return;
        }
        String value = null;
        if (pairResult != null) {
            if (pairResult.length == 1) {
                try {
                    if (pairResult[0] == 2) {
                        LOG.info("Successfully paired  MI device");
                        return;
                    }
                } catch (Exception ex) {
                    LOG.warn("Error identifying pairing result", (Throwable) ex);
                    return;
                }
            }
            value = Arrays.toString(pairResult);
        }
        Logger logger2 = LOG;
        logger2.info("MI Band pairing result: " + value);
    }

    private void sendCalendarEvents() {
        try {
            TransactionBuilder builder = performInitialized("Send upcoming events");
            BluetoothGattCharacteristic characteristic = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT);
            int availableSlots = new Prefs(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress())).getInt(DeviceSettingsPreferenceConst.PREF_RESERVER_ALARMS_CALENDAR, 0);
            if (availableSlots > 3) {
                availableSlots = 3;
            }
            if (availableSlots > 0) {
                int iteration = 0;
                Iterator<CalendarEvents.CalendarEvent> it = new CalendarEvents().getCalendarEventList(getContext()).iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    CalendarEvents.CalendarEvent mEvt = it.next();
                    if (iteration >= availableSlots) {
                        break;
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(mEvt.getBegin());
                    queueAlarm(AlarmUtils.createSingleShot(2 - iteration, false, false, calendar), builder, characteristic);
                    iteration++;
                }
                builder.queue(getQueue());
            }
        } catch (IOException ex) {
            LOG.error("Unable to send Events to MI device", (Throwable) ex);
        }
    }

    public void onSendConfiguration(String config) {
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
        try {
            TransactionBuilder builder = performInitialized("Toggle sensor reading");
            if (this.isReadingSensorData) {
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), stopSensorRead);
                this.isReadingSensorData = false;
            } else {
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), startSensorRead);
                this.isReadingSensorData = true;
            }
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to toggle sensor reading MI", (Throwable) ex);
        }
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
    }

    private static void handleSensorData(byte[] value) {
        double xAxis;
        double yAxis;
        int idx;
        int i;
        byte[] bArr = value;
        double xAxis2 = Utils.DOUBLE_EPSILON;
        double yAxis2 = Utils.DOUBLE_EPSILON;
        double zAxis = Utils.DOUBLE_EPSILON;
        if ((bArr.length - 2) % 6 != 0) {
            LOG.warn("GOT UNEXPECTED SENSOR DATA WITH LENGTH: " + bArr.length);
            LOG.warn("DATA: " + C1238GB.hexdump(bArr, 0, bArr.length));
            return;
        }
        int counter = (bArr[0] & 255) | ((bArr[1] & 255) << 8);
        int idx2 = 0;
        while (idx2 < (bArr.length - 2) / 6) {
            int step = idx2 * 6;
            int i2 = ((bArr[step + 3] & 255) << 8) | (bArr[step + 2] & 255);
            int xAxisSign = (bArr[step + 3] & ZeTimeConstants.CMD_USER_INFO) >> 4;
            double d = xAxis2;
            int xAxisType = (bArr[step + 3] & PebbleColor.Black) >> 6;
            if (xAxisSign == 0) {
                int i3 = xAxisType;
                xAxis = (double) (i2 & 4095);
            } else {
                xAxis = (double) ((i2 & 4095) - 4097);
            }
            xAxis2 = ((1.0d * xAxis) / 1000.0d) * 9.81d;
            double d2 = yAxis2;
            int i4 = (bArr[step + 4] & 255) | ((bArr[step + 5] & 255) << 8);
            int yAxisSign = (bArr[step + 5] & ZeTimeConstants.CMD_USER_INFO) >> 4;
            double d3 = zAxis;
            int yAxisType = (bArr[step + 5] & PebbleColor.Black) >> 6;
            if (yAxisSign == 0) {
                int i5 = yAxisSign;
                int i6 = yAxisType;
                yAxis = (double) (i4 & 4095);
            } else {
                int i7 = yAxisType;
                yAxis = (double) ((i4 & 4095) - 4097);
            }
            double yAxis3 = (yAxis / 1000.0d) * 9.81d;
            int i8 = i4;
            byte b = ((bArr[step + 7] & 255) << 8) | (bArr[step + 6] & 255);
            int zAxisSign = (bArr[step + 7] & ZeTimeConstants.CMD_USER_INFO) >> 4;
            int i9 = i2;
            int zAxisType = (bArr[step + 7] & PebbleColor.Black) >> 6;
            if (zAxisSign == 0) {
                i = b & 4095;
                int i10 = zAxisType;
                idx = idx2;
            } else {
                idx = idx2;
                i = (b & 4095) - 4097;
            }
            double zAxis2 = (((double) i) / 1000.0d) * 9.81d;
            Logger logger = LOG;
            byte b2 = b;
            StringBuilder sb = new StringBuilder();
            int i11 = zAxisSign;
            sb.append("READ SENSOR DATA VALUES: counter:");
            sb.append(counter);
            sb.append(" step:");
            sb.append(step);
            sb.append(" x-axis:");
            sb.append(String.format("%.03f", new Object[]{Double.valueOf(xAxis2)}));
            sb.append(" y-axis:");
            sb.append(String.format("%.03f", new Object[]{Double.valueOf(yAxis3)}));
            sb.append(" z-axis:");
            sb.append(String.format("%.03f", new Object[]{Double.valueOf(zAxis2)}));
            sb.append(";");
            logger.info(sb.toString());
            yAxis2 = yAxis3;
            zAxis = zAxis2;
            counter = counter;
            int i12 = step;
            idx2 = idx + 1;
            bArr = value;
        }
        double d4 = xAxis2;
        double d5 = yAxis2;
        double d6 = zAxis;
        int i13 = idx2;
        int i14 = counter;
    }
}
