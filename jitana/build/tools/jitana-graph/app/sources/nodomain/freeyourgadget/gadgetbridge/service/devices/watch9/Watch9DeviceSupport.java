package nodomain.freeyourgadget.gadgetbridge.service.devices.watch9;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.watch9.Watch9Constants;
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
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.operations.InitOperation;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.CoreConstants;

public class Watch9DeviceSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Watch9DeviceSupport.class);
    private byte ACK_CALIBRATION = 0;
    private final GBDeviceEventBatteryInfo batteryInfo = new GBDeviceEventBatteryInfo();
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Removed duplicated region for block: B:17:0x003d  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0066  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r8, android.content.Intent r9) {
            /*
                r7 = this;
                java.lang.String r0 = r9.getAction()
                int r1 = r0.hashCode()
                r2 = -1709653967(0xffffffff9a18c031, float:-3.1588114E-23)
                r3 = 0
                r4 = 2
                r5 = 1
                r6 = -1
                if (r1 == r2) goto L_0x0030
                r2 = -539897713(0xffffffffdfd1d08f, float:-3.0237482E19)
                if (r1 == r2) goto L_0x0026
                r2 = 278599910(0x109b18e6, float:6.117508E-29)
                if (r1 == r2) goto L_0x001c
            L_0x001b:
                goto L_0x003a
            L_0x001c:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.devices.action.watch9.keep_calibrating"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x001b
                r1 = 2
                goto L_0x003b
            L_0x0026:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.devices.action.watch9.send_calibration"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x001b
                r1 = 1
                goto L_0x003b
            L_0x0030:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.devices.action.watch9.start_calibration"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x001b
                r1 = 0
                goto L_0x003b
            L_0x003a:
                r1 = -1
            L_0x003b:
                if (r1 == 0) goto L_0x0066
                if (r1 == r5) goto L_0x0048
                if (r1 == r4) goto L_0x0042
                goto L_0x0072
            L_0x0042:
                nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport.this
                r1.holdCalibration()
                goto L_0x0072
            L_0x0048:
                java.lang.String r1 = "value.watch9.calibration_hour"
                int r1 = r9.getIntExtra(r1, r6)
                java.lang.String r2 = "value.watch9.calibration_minute"
                int r2 = r9.getIntExtra(r2, r6)
                java.lang.String r3 = "value.watch9.calibration_second"
                int r3 = r9.getIntExtra(r3, r6)
                if (r1 == r6) goto L_0x0072
                if (r2 == r6) goto L_0x0072
                if (r3 == r6) goto L_0x0072
                nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport.this
                r4.sendCalibrationData(r1, r2, r3)
                goto L_0x0072
            L_0x0066:
                nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport.this
                java.lang.String r2 = "action.watch9.enable"
                boolean r2 = r9.getBooleanExtra(r2, r3)
                r1.enableCalibration(r2)
            L_0x0072:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport.C12281.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    private boolean isCalibrationActive = false;
    private boolean needsAuth;
    private int sequenceNumber = 0;
    private final GBDeviceEventVersionInfo versionInfo = new GBDeviceEventVersionInfo();

    public Watch9DeviceSupport() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(Watch9Constants.UUID_SERVICE_WATCH9);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Watch9Constants.ACTION_CALIBRATION);
        intentFilter.addAction(Watch9Constants.ACTION_CALIBRATION_SEND);
        intentFilter.addAction(Watch9Constants.ACTION_CALIBRATION_HOLD);
        broadcastManager.registerReceiver(this.broadcastReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        try {
            boolean auth = this.needsAuth;
            this.needsAuth = false;
            new InitOperation(auth, this, builder).perform();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public boolean connectFirstTime() {
        this.needsAuth = true;
        return super.connect();
    }

    public boolean useAutoConnect() {
        return true;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        sendNotification(128, false);
    }

    private void sendNotification(int notificationChannel, boolean isStopNotification) {
        try {
            TransactionBuilder builder = performInitialized("showNotification");
            byte[] command = Watch9Constants.CMD_NOTIFICATION_TASK;
            command[1] = (byte) (isStopNotification ? 4 : 1);
            builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(command, (byte) 4, Conversion.toByteArr32(notificationChannel)));
            performImmediately(builder);
        } catch (IOException e) {
            LOG.warn("Unable to send notification", (Throwable) e);
        }
    }

    private Watch9DeviceSupport enableNotificationChannels(TransactionBuilder builder) {
        builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_NOTIFICATION_SETTINGS, (byte) 1, new byte[]{-1, -1, -1, -1}));
        return this;
    }

    public Watch9DeviceSupport authorizationRequest(TransactionBuilder builder, boolean firstConnect) {
        builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_AUTHORIZATION_TASK, (byte) 4, new byte[]{firstConnect ^ true ? (byte) 1 : 0}));
        return this;
    }

    private Watch9DeviceSupport enableDoNotDisturb(TransactionBuilder builder, boolean active) {
        builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_DO_NOT_DISTURB_SETTINGS, (byte) 1, new byte[]{active ? (byte) 1 : 0}));
        return this;
    }

    /* access modifiers changed from: private */
    public void enableCalibration(boolean enable) {
        try {
            TransactionBuilder builder = performInitialized("enableCalibration");
            BluetoothGattCharacteristic characteristic = getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE);
            byte[] bArr = Watch9Constants.CMD_CALIBRATION_INIT_TASK;
            int i = 1;
            byte[] bArr2 = new byte[1];
            if (!enable) {
                i = 0;
            }
            bArr2[0] = (byte) i;
            builder.write(characteristic, buildCommand(bArr, (byte) 4, bArr2));
            performImmediately(builder);
        } catch (IOException e) {
            LOG.warn("Unable to start/stop calibration mode", (Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void holdCalibration() {
        try {
            TransactionBuilder builder = performInitialized("holdCalibration");
            builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_CALIBRATION_KEEP_ALIVE, Byte.MIN_VALUE));
            performImmediately(builder);
        } catch (IOException e) {
            LOG.warn("Unable to keep calibration mode alive", (Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void sendCalibrationData(int hour, int minute, int second) {
        try {
            this.isCalibrationActive = true;
            TransactionBuilder builder = performInitialized("calibrate");
            builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_CALIBRATION_TASK, (byte) 4, Conversion.toByteArr16(((((hour % 12) * 60) + minute) * 60) + second)));
            performImmediately(builder);
        } catch (IOException e) {
            this.isCalibrationActive = false;
            LOG.warn("Unable to send calibration data", (Throwable) e);
        }
    }

    private void getTime() {
        try {
            TransactionBuilder builder = performInitialized("getTime");
            builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_TIME_SETTINGS, (byte) 2));
            performImmediately(builder);
        } catch (IOException e) {
            LOG.warn("Unable to get device time", (Throwable) e);
        }
    }

    private void handleTime(byte[] time) {
        GregorianCalendar now = BLETypeConversions.createCalendar();
        GregorianCalendar nowDevice = BLETypeConversions.createCalendar();
        nowDevice.set(((nowDevice.get(1) / 100) * 100) + Conversion.fromBcd8(time[8]), Conversion.fromBcd8(time[9]) - 1, Conversion.fromBcd8(time[10]), Conversion.fromBcd8(time[11]), Conversion.fromBcd8(time[12]), Conversion.fromBcd8(time[13]));
        nowDevice.set(7, Conversion.fromBcd8(time[16]) + 1);
        long timeDiff = Math.abs(now.getTimeInMillis() - nowDevice.getTimeInMillis()) / 1000;
        if (10 < timeDiff && timeDiff < 120) {
            enableCalibration(true);
            setTime(BLETypeConversions.createCalendar());
            enableCalibration(false);
        }
    }

    private void setTime(Calendar calendar) {
        try {
            TransactionBuilder builder = performInitialized("setTime");
            int timezoneOffsetMinutes = (calendar.get(15) + calendar.get(16)) / CoreConstants.MILLIS_IN_ONE_MINUTE;
            builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_TIME_SETTINGS, (byte) 1, new byte[]{Conversion.toBcd8(calendar.get(1) % 100), Conversion.toBcd8(calendar.get(2) + 1), Conversion.toBcd8(calendar.get(5)), Conversion.toBcd8(calendar.get(11)), Conversion.toBcd8(calendar.get(12)), Conversion.toBcd8(calendar.get(13)), (byte) (timezoneOffsetMinutes / 60), (byte) Math.round((((float) (Math.abs(timezoneOffsetMinutes) % 60)) * 100.0f) / 60.0f), (byte) (calendar.get(7) - 1)}));
            performImmediately(builder);
        } catch (IOException e) {
            LOG.warn("Unable to set time", (Throwable) e);
        }
    }

    public Watch9DeviceSupport getFirmwareVersion(TransactionBuilder builder) {
        builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_FIRMWARE_INFO, (byte) 2));
        return this;
    }

    private Watch9DeviceSupport getBatteryState(TransactionBuilder builder) {
        builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_BATTERY_INFO, (byte) 2));
        return this;
    }

    private Watch9DeviceSupport setFitnessGoal(TransactionBuilder builder) {
        builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_FITNESS_GOAL_SETTINGS, (byte) 1, Conversion.toByteArr16(new ActivityUser().getStepsGoal())));
        return this;
    }

    public Watch9DeviceSupport initialize(TransactionBuilder builder) {
        getFirmwareVersion(builder).getBatteryState(builder).enableNotificationChannels(builder).enableDoNotDisturb(builder, false).setFitnessGoal(builder);
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
        builder.setGattCallback(this);
        return this;
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetTime() {
        getTime();
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        try {
            TransactionBuilder builder = performInitialized("setAlarms");
            Iterator<? extends Alarm> it = alarms.iterator();
            while (it.hasNext()) {
                Alarm alarm = (Alarm) it.next();
                setAlarm(alarm, alarm.getPosition() + 1, builder);
            }
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.warn("Unable to set alarms", (Throwable) e);
        }
    }

    private void deleteAlarm(TransactionBuilder builder, int index) {
        if (index > 0 && index < 4) {
            builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_ALARM_SETTINGS, (byte) 1, new byte[]{(byte) index, 0, 0, 0, 0, 0}));
        }
    }

    private void setAlarm(Alarm alarm, int index, TransactionBuilder builder) {
        byte repetitionMask = alarm.getRepetition(64) | ((byte) ((alarm.getRepetition() << 1) | (alarm.isRepetitive() ? 128 : 0))) ? (byte) 1 : 0;
        if (index > 0 && index < 4) {
            builder.write(getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE), buildCommand(Watch9Constants.CMD_ALARM_SETTINGS, (byte) 1, new byte[]{(byte) index, Conversion.toBcd8(AlarmUtils.toCalendar(alarm).get(11)), Conversion.toBcd8(AlarmUtils.toCalendar(alarm).get(12)), repetitionMask, alarm.getEnabled() ? (byte) 1 : 0, 0}));
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        int i = callSpec.command;
        if (i == 2) {
            sendNotification(1024, false);
        } else if (i == 5 || i == 6) {
            sendNotification(1024, true);
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
    }

    public void onEnableRealtimeSteps(boolean enable) {
    }

    public void onInstallApp(Uri uri) {
    }

    public void onAppInfoReq() {
    }

    public void onAppStart(UUID uuid, boolean start) {
    }

    public void onAppDelete(UUID uuid) {
    }

    public void onAppConfiguration(UUID appUuid, String config, Integer id) {
    }

    public void onAppReorder(UUID[] uuids) {
    }

    public void onFetchRecordedData(int dataTypes) {
    }

    public void onReset(int flags) {
    }

    public void onHeartRateTest() {
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
    }

    public void onFindDevice(boolean start) {
    }

    public void onSetConstantVibration(int integer) {
    }

    public void onScreenshotReq() {
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    public void onSendConfiguration(String config) {
        try {
            TransactionBuilder builder = performInitialized("sendConfig: " + config);
            char c = 65535;
            if (config.hashCode() == 2066963661 && config.equals("mi_fitness_goal")) {
                c = 0;
            }
            if (c == 0) {
                setFitnessGoal(builder);
            }
            builder.queue(getQueue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        UUID characteristicUUID = characteristic.getUuid();
        if (Watch9Constants.UUID_CHARACTERISTIC_WRITE.equals(characteristicUUID)) {
            byte[] value = characteristic.getValue();
            if (ArrayUtils.equals(value, Watch9Constants.RESP_FIRMWARE_INFO, 5)) {
                handleFirmwareInfo(value);
                return true;
            } else if (ArrayUtils.equals(value, Watch9Constants.RESP_BATTERY_INFO, 5)) {
                handleBatteryState(value);
                return true;
            } else if (ArrayUtils.equals(value, Watch9Constants.RESP_TIME_SETTINGS, 5)) {
                handleTime(value);
                return true;
            } else if (ArrayUtils.equals(value, Watch9Constants.RESP_BUTTON_INDICATOR, 5)) {
                LOG.info("Unhandled action: Button pressed");
                return true;
            } else if (ArrayUtils.equals(value, Watch9Constants.RESP_ALARM_INDICATOR, 5)) {
                Logger logger = LOG;
                logger.info("Alarm active: id=" + value[8]);
                return true;
            } else if (!this.isCalibrationActive || value.length != 7 || value[4] != this.ACK_CALIBRATION) {
                return true;
            } else {
                setTime(BLETypeConversions.createCalendar());
                this.isCalibrationActive = false;
                return true;
            }
        } else {
            Logger logger2 = LOG;
            logger2.info("Unhandled characteristic changed: " + characteristicUUID);
            logMessageContent(characteristic.getValue());
            return false;
        }
    }

    private byte[] buildCommand(byte[] command, byte action) {
        return buildCommand(command, action, (byte[]) null);
    }

    private byte[] buildCommand(byte[] command, byte action, byte[] value) {
        if (Arrays.equals(command, Watch9Constants.CMD_CALIBRATION_TASK)) {
            this.ACK_CALIBRATION = (byte) this.sequenceNumber;
        }
        byte[] command2 = BLETypeConversions.join(command, value);
        byte[] result = new byte[(command2.length + 7)];
        System.arraycopy(Watch9Constants.CMD_HEADER, 0, result, 0, 5);
        System.arraycopy(command2, 0, result, 6, command2.length);
        result[2] = (byte) (command2.length + 1);
        result[3] = 49;
        int i = this.sequenceNumber;
        this.sequenceNumber = i + 1;
        result[4] = (byte) i;
        result[5] = action;
        result[result.length - 1] = calculateChecksum(result);
        return result;
    }

    private byte calculateChecksum(byte[] bytes) {
        byte checksum = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            checksum = (byte) (((bytes[i] ^ i) & 255) + checksum);
        }
        return (byte) (checksum & 255);
    }

    private void handleFirmwareInfo(byte[] value) {
        this.versionInfo.fwVersion = String.format(Locale.US, "%d.%d.%d", new Object[]{Byte.valueOf(value[8]), Byte.valueOf(value[9]), Byte.valueOf(value[10])});
        handleGBDeviceEvent(this.versionInfo);
    }

    private void handleBatteryState(byte[] value) {
        this.batteryInfo.state = value[9] == 1 ? BatteryState.BATTERY_NORMAL : BatteryState.BATTERY_LOW;
        GBDeviceEventBatteryInfo gBDeviceEventBatteryInfo = this.batteryInfo;
        gBDeviceEventBatteryInfo.level = -1;
        handleGBDeviceEvent(gBDeviceEventBatteryInfo);
    }

    public void dispose() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.broadcastReceiver);
        super.dispose();
    }

    private static class Conversion {
        private Conversion() {
        }

        static byte toBcd8(int value) {
            return (byte) (((value / 10) << 4) | (value % 10));
        }

        static int fromBcd8(byte value) {
            return (((value & 240) >> 4) * 10) + (value & 15);
        }

        static byte[] toByteArr16(int value) {
            return new byte[]{(byte) (value >> 8), (byte) value};
        }

        static byte[] toByteArr32(int value) {
            return new byte[]{(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
        }
    }
}
