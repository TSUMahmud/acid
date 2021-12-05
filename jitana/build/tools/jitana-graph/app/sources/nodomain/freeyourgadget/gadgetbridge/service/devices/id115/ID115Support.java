package nodomain.freeyourgadget.gadgetbridge.service.devices.id115;

import android.bluetooth.BluetoothGattCharacteristic;
import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.devices.id115.ID115Constants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ID115Support extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ID115Support.class);
    public BluetoothGattCharacteristic healthWriteCharacteristic = null;
    public BluetoothGattCharacteristic normalWriteCharacteristic = null;

    public ID115Support() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(ID115Constants.UUID_SERVICE_ID115);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        this.normalWriteCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_WRITE_NORMAL);
        this.healthWriteCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_WRITE_HEALTH);
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        setTime(builder).setWrist(builder).setScreenOrientation(builder).setGoal(builder).setInitialized(builder);
        getDevice().setFirmwareVersion("N/A");
        getDevice().setFirmwareVersion2("N/A");
        return builder;
    }

    public boolean useAutoConnect() {
        return true;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        try {
            new SendNotificationOperation(this, notificationSpec).perform();
        } catch (IOException ex) {
            LOG.error("Unable to send ID115 notification", (Throwable) ex);
        }
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("time");
            setTime(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.warn("Unable to send current time", (Throwable) e);
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> arrayList) {
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command == 2) {
            try {
                new SendNotificationOperation(this, callSpec).perform();
            } catch (IOException ex) {
                LOG.error("Unable to send ID115 notification", (Throwable) ex);
            }
        } else {
            sendStopCallNotification();
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
        try {
            new FetchActivityOperation(this).perform();
        } catch (IOException ex) {
            LOG.error("Unable to fetch ID115 activity data", (Throwable) ex);
        }
    }

    public void onReset(int flags) {
        try {
            getQueue().clear();
            TransactionBuilder builder = performInitialized("reboot");
            builder.write(this.normalWriteCharacteristic, new byte[]{-16, 1});
            builder.queue(getQueue());
        } catch (Exception e) {
        }
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
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
    }

    private void setInitialized(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
    }

    /* access modifiers changed from: package-private */
    public ID115Support setTime(TransactionBuilder builder) {
        byte dayOfWeek;
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int day = c.get(7);
        if (day == 1) {
            dayOfWeek = 6;
        } else {
            dayOfWeek = (byte) (day - 2);
        }
        int year = c.get(1);
        builder.write(this.normalWriteCharacteristic, new byte[]{3, 1, (byte) (year & 255), (byte) (year >> 8), (byte) (c.get(2) + 1), (byte) c.get(5), (byte) c.get(11), (byte) c.get(12), (byte) c.get(13), dayOfWeek});
        return this;
    }

    /* access modifiers changed from: package-private */
    public ID115Support setWrist(TransactionBuilder builder) {
        byte wrist;
        if ("left".equals(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).getString(DeviceSettingsPreferenceConst.PREF_WEARLOCATION, "left"))) {
            wrist = 0;
        } else {
            wrist = 1;
        }
        builder.write(this.normalWriteCharacteristic, new byte[]{3, 34, wrist});
        return this;
    }

    /* access modifiers changed from: package-private */
    public ID115Support setScreenOrientation(TransactionBuilder builder) {
        byte orientation;
        if ("horizontal".equals(GBApplication.getDeviceSpecificSharedPrefs(this.gbDevice.getAddress()).getString(DeviceSettingsPreferenceConst.PREF_SCREEN_ORIENTATION, "horizontal"))) {
            orientation = 0;
        } else {
            orientation = 2;
        }
        builder.write(this.normalWriteCharacteristic, new byte[]{3, 43, orientation});
        return this;
    }

    private ID115Support setGoal(TransactionBuilder transaction) {
        int value = new ActivityUser().getStepsGoal();
        transaction.write(this.normalWriteCharacteristic, new byte[]{3, 3, 0, (byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255), 0, 0});
        return this;
    }

    /* access modifiers changed from: package-private */
    public void sendStopCallNotification() {
        try {
            TransactionBuilder builder = performInitialized("stop_call_notification");
            builder.write(this.normalWriteCharacteristic, new byte[]{5, 2, 1});
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.warn("Unable to stop call notification", (Throwable) e);
        }
    }
}
