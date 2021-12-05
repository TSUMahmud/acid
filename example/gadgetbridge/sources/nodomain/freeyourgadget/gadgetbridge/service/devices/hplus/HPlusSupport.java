package nodomain.freeyourgadget.gadgetbridge.service.devices.hplus;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusCoordinator;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HPlusSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HPlusSupport.class);
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    public BluetoothGattCharacteristic ctrlCharacteristic = null;
    private DeviceType deviceType = DeviceType.UNKNOWN;
    public BluetoothGattCharacteristic measureCharacteristic = null;
    private HPlusHandlerThread syncHelper;

    public HPlusSupport(DeviceType type) {
        super(LOG);
        LOG.info("HPlusSupport Instance Created");
        this.deviceType = type;
        addSupportedService(HPlusConstants.UUID_SERVICE_HP);
    }

    public void dispose() {
        LOG.info("Dispose");
        close();
        super.dispose();
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        LOG.info("Initializing");
        this.gbDevice.setState(GBDevice.State.INITIALIZING);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        this.measureCharacteristic = getCharacteristic(HPlusConstants.UUID_CHARACTERISTIC_MEASURE);
        this.ctrlCharacteristic = getCharacteristic(HPlusConstants.UUID_CHARACTERISTIC_CONTROL);
        builder.notify(getCharacteristic(HPlusConstants.UUID_CHARACTERISTIC_MEASURE), true);
        builder.setGattCallback(this);
        builder.notify(this.measureCharacteristic, true);
        sendUserInfo(builder);
        this.gbDevice.setState(GBDevice.State.INITIALIZED);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        if (this.syncHelper == null) {
            this.syncHelper = new HPlusHandlerThread(getDevice(), getContext(), this);
            this.syncHelper.start();
        }
        this.syncHelper.sync();
        getDevice().setFirmwareVersion("N/A");
        getDevice().setFirmwareVersion2("N/A");
        requestDeviceInfo(builder);
        LOG.info("Initialization Done");
        return builder;
    }

    private HPlusSupport sendUserInfo(TransactionBuilder builder) {
        builder.write(this.ctrlCharacteristic, HPlusConstants.CMD_SET_PREF_START);
        builder.write(this.ctrlCharacteristic, HPlusConstants.CMD_SET_PREF_START1);
        syncPreferences(builder);
        builder.write(this.ctrlCharacteristic, new byte[]{79});
        return this;
    }

    private HPlusSupport syncPreferences(TransactionBuilder transaction) {
        if (this.deviceType == DeviceType.HPLUS || this.deviceType == DeviceType.EXRIZUK8) {
            setSIT(transaction);
        }
        setCurrentDate(transaction);
        setCurrentTime(transaction);
        setDayOfWeek(transaction);
        setTimeMode(transaction);
        setGender(transaction);
        setAge(transaction);
        setWeight(transaction);
        setHeight(transaction);
        setGoal(transaction);
        setLanguage(transaction);
        setScreenTime(transaction);
        setUnit(transaction);
        setAllDayHeart(transaction);
        return this;
    }

    private HPlusSupport setLanguage(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getLanguage(getDevice().getAddress());
        transaction.write(this.ctrlCharacteristic, new byte[]{34, value});
        return this;
    }

    private HPlusSupport setTimeMode(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getTimeMode(getDevice().getAddress());
        transaction.write(this.ctrlCharacteristic, new byte[]{HPlusConstants.CMD_SET_TIMEMODE, value});
        return this;
    }

    private HPlusSupport setUnit(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getUnit(getDevice().getAddress());
        transaction.write(this.ctrlCharacteristic, new byte[]{HPlusConstants.CMD_SET_UNITS, value});
        return this;
    }

    private HPlusSupport setCurrentDate(TransactionBuilder transaction) {
        Calendar c = GregorianCalendar.getInstance();
        int year = c.get(1);
        int month = c.get(2);
        int day = c.get(5);
        transaction.write(this.ctrlCharacteristic, new byte[]{8, (byte) ((year / 256) & 255), (byte) (year % 256), (byte) (month + 1), (byte) day});
        return this;
    }

    private HPlusSupport setCurrentTime(TransactionBuilder transaction) {
        Calendar c = GregorianCalendar.getInstance();
        transaction.write(this.ctrlCharacteristic, new byte[]{9, (byte) c.get(11), (byte) c.get(12), (byte) c.get(13)});
        return this;
    }

    private HPlusSupport setDayOfWeek(TransactionBuilder transaction) {
        Calendar c = GregorianCalendar.getInstance();
        transaction.write(this.ctrlCharacteristic, new byte[]{42, (byte) (c.get(7) - 1)});
        return this;
    }

    private HPlusSupport setSIT(TransactionBuilder transaction) {
        if (this.deviceType == DeviceType.MAKIBESF68) {
            return this;
        }
        int startTime = HPlusCoordinator.getSITStartTime(getDevice().getAddress());
        int endTime = HPlusCoordinator.getSITEndTime(getDevice().getAddress());
        Calendar now = GregorianCalendar.getInstance();
        transaction.write(this.ctrlCharacteristic, new byte[]{81, (byte) ((startTime / 256) & 255), (byte) (startTime % 256), (byte) ((endTime / 256) & 255), (byte) (endTime % 256), 0, 0, (byte) ((now.get(1) / 256) & 255), (byte) (now.get(1) % 256), (byte) (now.get(2) + 1), (byte) now.get(5), (byte) now.get(11), (byte) now.get(12), (byte) now.get(13), 0, 0, 0, 0});
        return this;
    }

    private HPlusSupport setWeight(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getUserWeight();
        transaction.write(this.ctrlCharacteristic, new byte[]{5, value});
        return this;
    }

    private HPlusSupport setHeight(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getUserHeight();
        transaction.write(this.ctrlCharacteristic, new byte[]{4, value});
        return this;
    }

    private HPlusSupport setAge(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getUserAge();
        transaction.write(this.ctrlCharacteristic, new byte[]{44, value});
        return this;
    }

    private HPlusSupport setGender(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getUserGender();
        transaction.write(this.ctrlCharacteristic, new byte[]{HPlusConstants.CMD_SET_GENDER, value});
        return this;
    }

    private HPlusSupport setGoal(TransactionBuilder transaction) {
        int value = HPlusCoordinator.getGoal();
        transaction.write(this.ctrlCharacteristic, new byte[]{38, (byte) ((value / 256) & 255), (byte) (value % 256)});
        return this;
    }

    private HPlusSupport setScreenTime(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getScreenTime(getDevice().getAddress());
        transaction.write(this.ctrlCharacteristic, new byte[]{11, value});
        return this;
    }

    private HPlusSupport setAllDayHeart(TransactionBuilder transaction) {
        byte value = HPlusCoordinator.getAllDayHR(getDevice().getAddress());
        transaction.write(this.ctrlCharacteristic, new byte[]{HPlusConstants.CMD_SET_ALLDAY_HRM, value});
        return this;
    }

    private HPlusSupport setAlarm(TransactionBuilder transaction, Calendar t) {
        byte hour = -1;
        byte minute = -1;
        if (t != null) {
            hour = (byte) t.get(11);
            minute = (byte) t.get(12);
        }
        transaction.write(this.ctrlCharacteristic, new byte[]{12, hour, minute});
        return this;
    }

    private HPlusSupport setFindMe(TransactionBuilder transaction, boolean state) {
        byte[] msg = new byte[2];
        msg[0] = 10;
        if (state) {
            msg[1] = 1;
        } else {
            msg[1] = 2;
        }
        transaction.write(this.ctrlCharacteristic, msg);
        return this;
    }

    private HPlusSupport requestDeviceInfo(TransactionBuilder builder) {
        builder.write(this.ctrlCharacteristic, new byte[]{36});
        builder.write(this.ctrlCharacteristic, new byte[]{23});
        return this;
    }

    public boolean useAutoConnect() {
        return true;
    }

    private void handleDeviceInfo(DeviceInfo info) {
        Logger logger = LOG;
        logger.warn("Device info: " + info);
    }

    public void onNotification(NotificationSpec notificationSpec) {
        showText(notificationSpec.title, notificationSpec.body);
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("time");
            setCurrentDate(builder);
            setCurrentTime(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        try {
            TransactionBuilder builder = performInitialized("alarm");
            Iterator<? extends Alarm> it = alarms.iterator();
            while (it.hasNext()) {
                Alarm alarm = (Alarm) it.next();
                if (alarm.getEnabled()) {
                    if (!alarm.getSmartWakeup()) {
                        setAlarm(builder, AlarmUtils.toCalendar(alarm));
                        builder.queue(getQueue());
                        C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_miband_set_alarms_ok), 0, 1);
                        return;
                    }
                }
            }
            setAlarm(builder, (Calendar) null);
            builder.queue(getQueue());
            C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_all_alarms_disabled), 0, 1);
        } catch (Exception e) {
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command == 2) {
            showIncomingCall(callSpec.name, callSpec.number);
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        Logger logger = LOG;
        logger.info("Canned Messages: " + cannedMessagesSpec);
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
    }

    public void onEnableRealtimeSteps(boolean enable) {
        onEnableRealtimeHeartRateMeasurement(enable);
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
        if (this.syncHelper == null) {
            this.syncHelper = new HPlusHandlerThread(this.gbDevice, getContext(), this);
            this.syncHelper.start();
        }
        this.syncHelper.sync();
    }

    public void onReset(int flags) {
        try {
            getQueue().clear();
            TransactionBuilder builder = performInitialized("Shutdown");
            builder.write(this.ctrlCharacteristic, new byte[]{91, 90});
            builder.queue(getQueue());
        } catch (Exception e) {
        }
    }

    public void onHeartRateTest() {
        getQueue().clear();
        try {
            TransactionBuilder builder = performInitialized("HeartRateTest");
            builder.write(this.ctrlCharacteristic, new byte[]{50, 11});
            builder.queue(getQueue());
        } catch (Exception e) {
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        byte state;
        try {
            TransactionBuilder builder = performInitialized("realTimeHeartMeasurement");
            if (enable) {
                state = 10;
            } else {
                state = -1;
            }
            builder.write(this.ctrlCharacteristic, new byte[]{HPlusConstants.CMD_SET_ALLDAY_HRM, state});
            builder.queue(getQueue());
        } catch (Exception e) {
        }
    }

    public void onFindDevice(boolean start) {
        try {
            TransactionBuilder builder = performInitialized("findMe");
            setFindMe(builder, start);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error toggling Find Me: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onSetConstantVibration(int intensity) {
        try {
            TransactionBuilder builder = performInitialized("vibration");
            byte[] msg = new byte[15];
            msg[0] = 35;
            for (int i = 0; i < msg.length - 1; i++) {
                msg[i + 1] = (byte) GBApplication.DATABASE_NAME.charAt(i);
            }
            builder.write(this.ctrlCharacteristic, msg);
            builder.queue(getQueue());
        } catch (IOException e) {
            C1238GB.toast(getContext(), "Error setting Vibration: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onScreenshotReq() {
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
        onEnableRealtimeHeartRateMeasurement(enable);
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    public void onSendConfiguration(String config) {
        try {
            TransactionBuilder builder = performInitialized("Sending configuration for option: " + config);
            char c = 65535;
            if (config.hashCode() == -820369390 && config.equals(SettingsActivity.PREF_MEASUREMENT_SYSTEM)) {
                c = 0;
            }
            if (c == 0) {
                setUnit(builder);
            }
            builder.queue(getQueue());
        } catch (IOException e) {
            C1238GB.toast("Error setting configuration", 1, 3, (Throwable) e);
        }
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
        LOG.info("Test New Function");
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
    }

    public void setUnicodeSupport(boolean support) {
        HPlusCoordinator.setUnicodeSupport(this.gbDevice.getAddress(), support);
    }

    private void showIncomingCall(String name, String rawNumber) {
        try {
            TransactionBuilder builder = performInitialized("incomingCall");
            builder.write(this.ctrlCharacteristic, new byte[]{65, 1});
            builder.write(this.ctrlCharacteristic, new byte[]{6, -86});
            if (name != null) {
                byte[] msg = new byte[13];
                for (int i = 0; i < msg.length; i++) {
                    msg[i] = 32;
                }
                byte[] nameBytes = encodeStringToDevice(name);
                int i2 = 0;
                while (i2 < nameBytes.length && i2 < msg.length - 1) {
                    msg[i2 + 1] = nameBytes[i2];
                    i2++;
                }
                msg[0] = 63;
                builder.write(this.ctrlCharacteristic, msg);
                msg[0] = 62;
                builder.write(this.ctrlCharacteristic, msg);
            }
            if (rawNumber != null) {
                StringBuilder number = new StringBuilder();
                for (char c : rawNumber.toCharArray()) {
                    if (Character.isDigit(c)) {
                        number.append(c);
                    }
                }
                byte[] msg2 = new byte[13];
                for (int i3 = 0; i3 < msg2.length; i3++) {
                    msg2[i3] = 32;
                }
                int i4 = 0;
                while (i4 < number.length() && i4 < msg2.length - 1) {
                    msg2[i4 + 1] = (byte) number.charAt(i4);
                    i4++;
                }
                msg2[0] = 35;
                builder.wait(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                builder.write(this.ctrlCharacteristic, msg2);
            }
            builder.queue(getQueue());
        } catch (IOException e) {
            C1238GB.toast(getContext(), "Error showing incoming call: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void showText(String title, String body) {
        String str = title;
        String str2 = body;
        try {
            TransactionBuilder builder = performInitialized("notification");
            String message = "";
            if (str != null && title.length() > 0) {
                message = StringUtils.pad(StringUtils.truncate(str, 16), 16);
            }
            if (str2 != null) {
                message = message + str2;
            }
            byte[] messageBytes = encodeStringToDevice(message);
            int length = messageBytes.length / 17;
            int i = 5;
            if (length <= 5) {
                i = length;
            }
            int length2 = i;
            builder.write(this.ctrlCharacteristic, new byte[]{7, -86});
            int remaining = Math.min(255, messageBytes.length % 17 > 0 ? length2 + 1 : length2);
            byte[] msg = new byte[20];
            msg[0] = 67;
            msg[1] = (byte) remaining;
            for (int i2 = 2; i2 < msg.length; i2++) {
                msg[i2] = 32;
            }
            int message_index = 0;
            int i3 = 3;
            for (byte b : messageBytes) {
                int i4 = i3 + 1;
                msg[i3] = b;
                if (i4 == msg.length) {
                    message_index++;
                    msg[2] = (byte) message_index;
                    builder.write(this.ctrlCharacteristic, msg);
                    msg = (byte[]) msg.clone();
                    for (int i5 = 3; i5 < msg.length; i5++) {
                        msg[i5] = 32;
                    }
                    if (message_index >= remaining) {
                        break;
                    }
                    i3 = 3;
                } else {
                    i3 = i4;
                }
            }
            msg[2] = (byte) remaining;
            builder.write(this.ctrlCharacteristic, msg);
            builder.queue(getQueue());
        } catch (IOException e) {
            C1238GB.toast(getContext(), "Error showing device Notification: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void close() {
        HPlusHandlerThread hPlusHandlerThread = this.syncHelper;
        if (hPlusHandlerThread != null) {
            hPlusHandlerThread.quit();
            this.syncHelper.interrupt();
            this.syncHelper = null;
        }
    }

    private byte[] encodeStringToDevice(String s) {
        byte[] cs;
        List<Byte> outBytes = new ArrayList<>();
        boolean unicode = HPlusCoordinator.getUnicodeSupport(this.gbDevice.getAddress());
        Logger logger = LOG;
        logger.info("Encode String: Unicode=" + unicode);
        int i = 0;
        while (true) {
            int j0 = 0;
            if (i >= s.length()) {
                return ArrayUtils.toPrimitive((Byte[]) outBytes.toArray(new Byte[0]));
            }
            Character c = Character.valueOf(s.charAt(i));
            if (HPlusConstants.transliterateMap.containsKey(c)) {
                cs = HPlusConstants.transliterateMap.get(c);
            } else if (unicode) {
                try {
                    cs = c.toString().getBytes("Unicode");
                } catch (UnsupportedEncodingException e) {
                    byte[] cs2 = c.toString().getBytes();
                    Logger logger2 = LOG;
                    logger2.error("Could not convert String to Bytes: " + e.getMessage());
                    cs = cs2;
                }
            } else {
                cs = c.toString().getBytes("GB2312");
            }
            if (unicode && i != 0) {
                j0 = 2;
            }
            for (int j = j0; j < cs.length; j++) {
                outBytes.add(Byte.valueOf(cs[j]));
            }
            i++;
        }
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (super.onCharacteristicChanged(gatt, characteristic)) {
            return true;
        }
        UUID characteristicUUID = characteristic.getUuid();
        byte[] data = characteristic.getValue();
        if (data.length == 0) {
            return true;
        }
        byte b = data[0];
        if (b != 24) {
            if (b == 26) {
                return this.syncHelper.processIncomingSleepData(data);
            }
            if (b != 46) {
                if (b == 51) {
                    boolean result = this.syncHelper.processRealtimeStats(data, HPlusCoordinator.getUserAge());
                    if (result) {
                        processExtraInfo(data);
                    }
                    return result;
                } else if (b == 54) {
                    return this.syncHelper.processDaySummary(data);
                } else {
                    if (b == 77) {
                        return true;
                    }
                    if (b == 56 || b == 57) {
                        return this.syncHelper.processIncomingDaySlotData(data, HPlusCoordinator.getUserAge());
                    }
                    Logger logger = LOG;
                    logger.info("Unhandled characteristic change: " + characteristicUUID + " code: " + Arrays.toString(data));
                    return true;
                }
            }
        }
        return this.syncHelper.processVersion(data);
    }

    private void processExtraInfo(byte[] data) {
        try {
            HPlusDataRecordRealtime record = new HPlusDataRecordRealtime(data, HPlusCoordinator.getUserAge());
            handleBatteryInfo(record.battery);
            String DEVINFO_STEP = getContext().getString(C0889R.string.chart_steps) + ": ";
            String DEVINFO_DISTANCE = getContext().getString(C0889R.string.distance) + ": ";
            String DEVINFO_CALORY = getContext().getString(C0889R.string.calories) + ": ";
            String DEVINFO_HEART = getContext().getString(C0889R.string.charts_legend_heartrate) + ": ";
            String info = "";
            if (record.steps > 0) {
                info = info + DEVINFO_STEP + String.valueOf(record.steps) + "   ";
            }
            if (record.distance > 0) {
                info = info + DEVINFO_DISTANCE + String.valueOf(record.distance) + "   ";
            }
            if (record.calories > 0) {
                info = info + DEVINFO_CALORY + String.valueOf(record.calories) + "   ";
            }
            if (record.heartRate > 0) {
                info = info + DEVINFO_HEART + String.valueOf(record.heartRate) + "   ";
            }
            if (!info.equals("")) {
                getDevice().addDeviceInfo(new GenericItem("", info));
            }
        } catch (IllegalArgumentException e) {
            LOG.info(e.getMessage());
        }
    }

    private void handleBatteryInfo(byte data) {
        if (this.batteryCmd.level != ((short) data)) {
            GBDeviceEventBatteryInfo gBDeviceEventBatteryInfo = this.batteryCmd;
            gBDeviceEventBatteryInfo.level = (short) data;
            handleGBDeviceEvent(gBDeviceEventBatteryInfo);
        }
    }
}
