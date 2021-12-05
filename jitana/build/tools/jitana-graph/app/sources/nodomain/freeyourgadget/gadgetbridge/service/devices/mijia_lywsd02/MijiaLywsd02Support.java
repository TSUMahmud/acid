package nodomain.freeyourgadget.gadgetbridge.service.devices.mijia_lywsd02;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
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
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.IntentListener;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfoProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.CoreConstants;

public class MijiaLywsd02Support extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MijiaLywsd02Support.class);
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    private final DeviceInfoProfile<MijiaLywsd02Support> deviceInfoProfile;
    private final IntentListener mListener = new IntentListener() {
        public void notify(Intent intent) {
            if (Objects.equals(intent.getAction(), DeviceInfoProfile.ACTION_DEVICE_INFO)) {
                MijiaLywsd02Support.this.handleDeviceInfo((DeviceInfo) intent.getParcelableExtra(DeviceInfoProfile.EXTRA_DEVICE_INFO));
            }
        }
    };
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public MijiaLywsd02Support() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(GattService.UUID_SERVICE_DEVICE_INFORMATION);
        addSupportedService(UUID.fromString("ebe0ccb0-7a0a-4b0c-8a1a-6ff2997da3a6"));
        this.deviceInfoProfile = new DeviceInfoProfile<>(this);
        this.deviceInfoProfile.addListener(this.mListener);
        addSupportedProfile(this.deviceInfoProfile);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        requestDeviceInfo(builder);
        setTime(builder);
        setInitialized(builder);
        return builder;
    }

    private void setTime(TransactionBuilder builder) {
        BluetoothGattCharacteristic timeCharacteristc = getCharacteristic(UUID.fromString("ebe0ccb7-7a0a-4b0c-8a1a-6ff2997da3a6"));
        long ts = System.currentTimeMillis();
        long ts2 = ts / 1000;
        builder.write(timeCharacteristc, new byte[]{(byte) ((int) (ts2 & 255)), (byte) ((int) ((ts2 >> 8) & 255)), (byte) ((int) ((ts2 >> 16) & 255)), (byte) ((int) (255 & (ts2 >> 24))), (byte) (SimpleTimeZone.getDefault().getOffset(ts) / CoreConstants.MILLIS_IN_ONE_HOUR)});
    }

    private void requestDeviceInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Device Info!");
        this.deviceInfoProfile.requestDeviceInfo(builder);
    }

    private void setInitialized(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
    }

    public boolean useAutoConnect() {
        return false;
    }

    /* access modifiers changed from: private */
    public void handleDeviceInfo(DeviceInfo info) {
        Logger logger = LOG;
        logger.warn("Device info: " + info);
        this.versionCmd.hwVersion = info.getHardwareRevision();
        this.versionCmd.fwVersion = info.getFirmwareRevision();
        handleGBDeviceEvent(this.versionCmd);
    }

    public void onNotification(NotificationSpec notificationSpec) {
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetTime() {
    }

    public void onSetAlarms(ArrayList<? extends Alarm> arrayList) {
    }

    public void onSetCallState(CallSpec callSpec) {
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

    public void onSetConstantVibration(int intensity) {
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

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (super.onCharacteristicChanged(gatt, characteristic)) {
            return true;
        }
        UUID characteristicUUID = characteristic.getUuid();
        Logger logger = LOG;
        logger.info("Unhandled characteristic changed: " + characteristicUUID);
        return false;
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (super.onCharacteristicRead(gatt, characteristic, status)) {
            return true;
        }
        UUID characteristicUUID = characteristic.getUuid();
        Logger logger = LOG;
        logger.info("Unhandled characteristic read: " + characteristicUUID);
        return false;
    }

    public void onSendConfiguration(String config) {
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
    }
}
