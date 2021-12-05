package nodomain.freeyourgadget.gadgetbridge.service.devices.miscale2;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandService;
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
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.IntentListener;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfoProfile;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiScale2DeviceSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiScale2DeviceSupport.class);
    private static final String UNIT_JIN = "jīn";
    private static final String UNIT_KG = "kg";
    private static final String UNIT_LBS = "lb";
    private final DeviceInfoProfile<MiScale2DeviceSupport> deviceInfoProfile;
    private final IntentListener mListener = new IntentListener() {
        public void notify(Intent intent) {
            if (intent.getAction().equals(DeviceInfoProfile.ACTION_DEVICE_INFO)) {
                MiScale2DeviceSupport.this.handleDeviceInfo((DeviceInfo) intent.getParcelableExtra(DeviceInfoProfile.EXTRA_DEVICE_INFO));
            }
        }
    };
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public MiScale2DeviceSupport() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(GattService.UUID_SERVICE_DEVICE_INFORMATION);
        addSupportedService(GattService.UUID_SERVICE_BODY_COMPOSITION);
        addSupportedService(UUID.fromString(MiBandService.UUID_SERVICE_WEIGHT_SERVICE));
        this.deviceInfoProfile = new DeviceInfoProfile<>(this);
        this.deviceInfoProfile.addListener(this.mListener);
        addSupportedProfile(this.deviceInfoProfile);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        LOG.debug("Requesting Device Info!");
        this.deviceInfoProfile.requestDeviceInfo(builder);
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
        builder.setGattCallback(this);
        builder.notify(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_BODY_COMPOSITION_MEASUREMENT), true);
        return builder;
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = characteristic;
        super.onCharacteristicChanged(gatt, characteristic);
        UUID characteristicUUID = characteristic.getUuid();
        boolean isKg = false;
        if (!characteristicUUID.equals(GattCharacteristic.UUID_CHARACTERISTIC_BODY_COMPOSITION_MEASUREMENT)) {
            return false;
        }
        byte[] data = characteristic.getValue();
        boolean stabilized = testBit(data[1], 5) && !testBit(data[1], 7);
        boolean isLbs = testBit(data[1], 0);
        boolean isJin = testBit(data[1], 4);
        if (!isLbs || !isJin) {
            isKg = true;
        }
        String unit = "";
        if (isKg) {
            unit = UNIT_KG;
        } else if (isLbs) {
            unit = UNIT_LBS;
        } else if (isJin) {
            unit = UNIT_JIN;
        }
        if (stabilized) {
            int year = bluetoothGattCharacteristic.getIntValue(18, 2).intValue();
            int month = bluetoothGattCharacteristic.getIntValue(17, 4).intValue();
            int day = bluetoothGattCharacteristic.getIntValue(17, 5).intValue();
            int hour = bluetoothGattCharacteristic.getIntValue(17, 6).intValue();
            int minute = bluetoothGattCharacteristic.getIntValue(17, 7).intValue();
            int second = bluetoothGattCharacteristic.getIntValue(17, 8).intValue();
            Calendar c = GregorianCalendar.getInstance();
            c.set(year, month - 1, day, hour, minute, second);
            UUID uuid = characteristicUUID;
            byte[] bArr = data;
            handleWeightInfo(c.getTime(), ((float) bluetoothGattCharacteristic.getIntValue(18, 11).intValue()) / (isKg ? 200.0f : 100.0f), unit);
            return true;
        }
        byte[] bArr2 = data;
        return true;
    }

    private boolean testBit(byte value, int offset) {
        return ((value >> offset) & 1) == 1;
    }

    /* access modifiers changed from: private */
    public void handleDeviceInfo(DeviceInfo info) {
        Logger logger = LOG;
        logger.warn("Device info: " + info);
        this.versionCmd.hwVersion = info.getHardwareRevision();
        this.versionCmd.fwVersion = info.getSoftwareRevision();
        handleGBDeviceEvent(this.versionCmd);
    }

    private void handleWeightInfo(Date date, float weight, String unit) {
        Logger logger = LOG;
        logger.warn("Weight info: " + weight + unit);
        StringBuilder sb = new StringBuilder();
        sb.append(weight);
        sb.append(unit);
        C1238GB.toast(sb.toString(), 0, 1);
    }

    public boolean useAutoConnect() {
        return false;
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
}
