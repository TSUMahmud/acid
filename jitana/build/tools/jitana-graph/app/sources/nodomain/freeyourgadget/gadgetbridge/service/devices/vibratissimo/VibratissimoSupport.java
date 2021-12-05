package nodomain.freeyourgadget.gadgetbridge.service.devices.vibratissimo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
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
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.battery.BatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.battery.BatteryInfoProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfoProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibratissimoSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) VibratissimoSupport.class);
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    private final BatteryInfoProfile<VibratissimoSupport> batteryInfoProfile;
    private final DeviceInfoProfile<VibratissimoSupport> deviceInfoProfile;
    private final IntentListener mListener = new IntentListener() {
        public void notify(Intent intent) {
            String s = intent.getAction();
            if (s.equals(DeviceInfoProfile.ACTION_DEVICE_INFO)) {
                VibratissimoSupport.this.handleDeviceInfo((DeviceInfo) intent.getParcelableExtra(DeviceInfoProfile.EXTRA_DEVICE_INFO));
            } else if (s.equals(BatteryInfoProfile.ACTION_BATTERY_INFO)) {
                VibratissimoSupport.this.handleBatteryInfo((BatteryInfo) intent.getParcelableExtra(BatteryInfoProfile.EXTRA_BATTERY_INFO));
            }
        }
    };
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public VibratissimoSupport() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(GattService.UUID_SERVICE_DEVICE_INFORMATION);
        addSupportedService(GattService.UUID_SERVICE_BATTERY_SERVICE);
        addSupportedService(UUID.fromString("00001523-1212-efde-1523-785feabcd123"));
        this.deviceInfoProfile = new DeviceInfoProfile<>(this);
        this.deviceInfoProfile.addListener(this.mListener);
        this.batteryInfoProfile = new BatteryInfoProfile<>(this);
        this.batteryInfoProfile.addListener(this.mListener);
        addSupportedProfile(this.deviceInfoProfile);
        addSupportedProfile(this.batteryInfoProfile);
    }

    /* access modifiers changed from: private */
    public void handleBatteryInfo(BatteryInfo info) {
        this.batteryCmd.level = (short) info.getPercentCharged();
        handleGBDeviceEvent(this.batteryCmd);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        requestDeviceInfo(builder);
        setInitialized(builder);
        this.batteryInfoProfile.requestBatteryInfo(builder);
        return builder;
    }

    private void requestDeviceInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Device Info!");
        this.deviceInfoProfile.requestDeviceInfo(builder);
    }

    private void setInitialized(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
    }

    public boolean useAutoConnect() {
        return true;
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
        onSetConstantVibration(start ? 255 : 0);
    }

    public void onSetConstantVibration(int intensity) {
        getQueue().clear();
        BluetoothGattCharacteristic characteristic2 = getCharacteristic(UUID.fromString("00001526-1212-efde-1523-785feabcd123"));
        BluetoothGattCharacteristic characteristic1 = getCharacteristic(UUID.fromString("00001524-1212-efde-1523-785feabcd123"));
        TransactionBuilder builder = new TransactionBuilder("vibration");
        builder.write(characteristic1, new byte[]{3, Byte.MIN_VALUE});
        builder.write(characteristic2, new byte[]{(byte) intensity, 0});
        builder.queue(getQueue());
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
