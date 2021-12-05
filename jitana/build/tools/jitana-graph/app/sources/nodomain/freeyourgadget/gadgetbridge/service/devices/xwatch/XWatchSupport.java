package nodomain.freeyourgadget.gadgetbridge.service.devices.xwatch;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.view.KeyEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.xwatch.XWatchSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.xwatch.XWatchService;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.entities.XWatchActivitySample;
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
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XWatchSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XWatchSupport.class);
    TransactionBuilder builder = null;
    private byte dayToFetch;
    long lastButtonTimestamp;
    private DeviceInfo mDeviceInfo;
    private byte maxDayToFetch;
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public XWatchSupport() {
        super(LOG);
        addSupportedService(XWatchService.UUID_SERVICE);
        addSupportedService(XWatchService.UUID_WRITE);
        addSupportedService(XWatchService.UUID_NOTIFY);
    }

    public static byte[] crcChecksum(byte[] data) {
        byte[] return_data = new byte[(data.length + 1)];
        byte checksum = 0;
        for (int i = 0; i < data.length; i++) {
            return_data[i] = data[i];
            checksum = (byte) (data[i] + checksum);
        }
        return_data[return_data.length - 1] = checksum;
        return return_data;
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder2) {
        builder2.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        enableNotifications(builder2).setDateTime(builder2).setInitialized(builder2);
        return builder2;
    }

    private void setInitialized(TransactionBuilder builder2) {
        builder2.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
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

    private XWatchSupport setDateTime(TransactionBuilder builder2) {
        LOG.debug("Sending current date to the XWatch");
        BluetoothGattCharacteristic deviceData = getCharacteristic(XWatchService.UUID_WRITE);
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String y = time.substring(2, 4);
        String M = time.substring(4, 6);
        String d = time.substring(6, 8);
        String H = time.substring(8, 10);
        String m = time.substring(10, 12);
        String s = time.substring(12, 14);
        PrintStream printStream = System.out;
        printStream.println(y + ":" + M + ":" + d + ":" + H + ":" + m + ":" + time.substring(12, 14));
        TransactionBuilder transactionBuilder = builder2;
        transactionBuilder.write(deviceData, crcChecksum(new byte[]{1, (byte) Integer.parseInt(y, 16), (byte) Integer.parseInt(M, 16), (byte) Integer.parseInt(d, 16), (byte) Integer.parseInt(H, 16), (byte) Integer.parseInt(m, 16), (byte) Integer.parseInt(s, 16), 0, 0, 0, 0, 0, 0, 0, 0}));
        return this;
    }

    private XWatchSupport enableNotifications(TransactionBuilder builder2) {
        LOG.debug("Enabling action button");
        builder2.notify(getCharacteristic(XWatchService.UUID_NOTIFY), true);
        return this;
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    public void onSetAlarms(ArrayList<? extends Alarm> arrayList) {
    }

    public void onNotification(NotificationSpec notificationSpec) {
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder2 = performInitialized("Set date and time");
            setDateTime(builder2);
            builder2.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to set time and date on XWatch device", (Throwable) ex);
        }
    }

    public void onSetCallState(CallSpec callSpec) {
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
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

    public void onFetchRecordedData(int dataTypes) {
        try {
            if (this.builder == null) {
                this.builder = performInitialized("fetchActivityData");
            }
            requestSummarizedData(this.builder);
            performConnected(this.builder.getTransaction());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error fetching activity data: " + e.getLocalizedMessage(), 1, 3);
        }
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

    public void onAppReorder(UUID[] uuids) {
    }

    public void onScreenshotReq() {
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        UUID characteristicUUID = characteristic.getUuid();
        if (XWatchService.UUID_NOTIFY.equals(characteristicUUID)) {
            byte[] data = characteristic.getValue();
            if (data[0] == 70) {
                handleSummarizedData(characteristic.getValue());
            } else if (data[0] == 67) {
                handleDetailedData(characteristic.getValue());
            } else if (data[0] == 76) {
                handleButtonPressed(characteristic.getValue());
            } else if (data[0] == 1) {
                handleDeviceInfo(data, 0);
            } else {
                Logger logger = LOG;
                logger.info("Handled characteristic with unknown data: " + characteristicUUID);
                logMessageContent(characteristic.getValue());
            }
        } else {
            Logger logger2 = LOG;
            logger2.info("Unhandled characteristic changed: " + characteristicUUID);
            logMessageContent(characteristic.getValue());
        }
        return false;
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        return super.onCharacteristicChanged(gatt, characteristic);
    }

    public boolean onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        return super.onCharacteristicWrite(gatt, characteristic, status);
    }

    public XWatchActivitySample createActivitySample(Device device, User user, int timestampInSeconds, SampleProvider provider) {
        XWatchActivitySample sample = new XWatchActivitySample();
        sample.setDevice(device);
        sample.setUser(user);
        sample.setTimestamp(timestampInSeconds);
        sample.setProvider(provider);
        return sample;
    }

    private void handleDeviceInfo(byte[] value, int status) {
        if (status == 0) {
            this.mDeviceInfo = new DeviceInfo(value);
            Logger logger = LOG;
            logger.warn("Device info: " + this.mDeviceInfo);
            GBDeviceEventVersionInfo gBDeviceEventVersionInfo = this.versionCmd;
            gBDeviceEventVersionInfo.hwVersion = "1.0";
            gBDeviceEventVersionInfo.fwVersion = "1.0";
            handleGBDeviceEvent(gBDeviceEventVersionInfo);
        }
    }

    public void onSendConfiguration(String config) {
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
    }

    private void handleSummarizedData(byte[] value) {
        if (value.length != 16) {
            LOG.warn("GOT UNEXPECTED SENSOR DATA WITH LENGTH: " + value.length);
            for (byte b : value) {
                LOG.warn("DATA: " + String.format("0x%4x", new Object[]{Byte.valueOf(b)}));
            }
            return;
        }
        this.dayToFetch = 0;
        this.maxDayToFetch = (byte) Integer.bitCount(((value[1] & 255) << 24) + ((value[2] & 255) << 16) + ((value[3] & 255) << 8) + (value[4] & 255));
        try {
            requestDetailedData(this.builder);
            performConnected(this.builder.getTransaction());
        } catch (IOException e) {
            C1238GB.toast(getContext(), "Error fetching activity data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void handleDetailedData(byte[] value) {
        byte[] bArr = value;
        int steps = 0;
        if (bArr.length != 16) {
            LOG.warn("GOT UNEXPECTED SENSOR DATA WITH LENGTH: " + bArr.length);
            for (byte b : bArr) {
                LOG.warn("DATA: " + String.format("0x%4x", new Object[]{Byte.valueOf(b)}));
            }
            return;
        }
        try {
            DBHandler dbHandler = GBApplication.acquireDB();
            try {
                XWatchSampleProvider provider = new XWatchSampleProvider(getDevice(), dbHandler.getDaoSession());
                User user = DBHelper.getUser(dbHandler.getDaoSession());
                Device device = DBHelper.getDevice(getDevice(), dbHandler.getDaoSession());
                int timestampInSeconds = getTimestampFromData(bArr[2], bArr[3], bArr[4], bArr[5]);
                int intensity = ((bArr[8] & 255) << 8) + (bArr[7] & 255);
                steps = (bArr[9] & 255) + ((bArr[10] & 255) << 8);
                XWatchActivitySample sample = createActivitySample(device, user, timestampInSeconds, provider);
                sample.setRawIntensity(intensity);
                sample.setSteps(steps);
                sample.setRawKind(1);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("sample: " + sample);
                }
                provider.addGBActivitySample(sample);
                if (bArr[5] == 95) {
                    this.dayToFetch = (byte) (this.dayToFetch + 1);
                    if (this.dayToFetch <= this.maxDayToFetch) {
                        this.builder = performInitialized("fetchActivityData");
                        requestDetailedData(this.builder);
                        this.builder.queue(getQueue());
                    }
                }
            } catch (IOException e) {
                C1238GB.toast(getContext(), "Error fetching activity data: " + e.getLocalizedMessage(), 1, 3);
            } catch (Throwable th) {
                int steps2 = steps;
                Throwable th2 = th;
                try {
                    throw th2;
                } catch (Throwable th3) {
                    Throwable th4 = th3;
                    if (dbHandler != null) {
                        try {
                            dbHandler.close();
                        } catch (Exception e2) {
                            ex = e2;
                            int i = steps2;
                            C1238GB.toast(getContext(), ex.getMessage(), 1, 3, ex);
                            return;
                        } catch (Throwable th5) {
                            th2.addSuppressed(th5);
                        }
                    }
                    throw th4;
                }
            }
            if (dbHandler != null) {
                dbHandler.close();
            }
        } catch (Exception e3) {
            ex = e3;
        }
    }

    private void handleButtonPressed(byte[] value) {
        long currentTimestamp = System.currentTimeMillis();
        AudioManager audioManager = (AudioManager) getContext().getSystemService("audio");
        if (audioManager.isWiredHeadsetOn()) {
            if (currentTimestamp - this.lastButtonTimestamp >= 1000) {
                audioManager.dispatchMediaKeyEvent(new KeyEvent(0, 85));
                audioManager.dispatchMediaKeyEvent(new KeyEvent(1, 85));
            } else if (audioManager.isMusicActive()) {
                audioManager.dispatchMediaKeyEvent(new KeyEvent(0, 87));
                audioManager.dispatchMediaKeyEvent(new KeyEvent(1, 87));
            } else {
                audioManager.dispatchMediaKeyEvent(new KeyEvent(0, 85));
                audioManager.dispatchMediaKeyEvent(new KeyEvent(1, 85));
                audioManager.dispatchMediaKeyEvent(new KeyEvent(0, 87));
                audioManager.dispatchMediaKeyEvent(new KeyEvent(1, 87));
            }
        }
        this.lastButtonTimestamp = currentTimestamp;
    }

    public void onAppConfiguration(UUID appUuid, String config, Integer id) {
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
    }

    private void requestSummarizedData(TransactionBuilder builder2) {
        builder2.write(getCharacteristic(XWatchService.UUID_WRITE), crcChecksum(new byte[]{XWatchService.COMMAND_ACTIVITY_TOTALS, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
    }

    private void requestDetailedData(TransactionBuilder builder2) {
        builder2.write(getCharacteristic(XWatchService.UUID_WRITE), crcChecksum(new byte[]{67, this.dayToFetch, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
    }

    private int getTimestampFromData(byte year, byte month, byte day, byte hoursminutes) {
        int yearInt = Integer.valueOf(String.format("%02x", new Object[]{Byte.valueOf(year)}), 16).intValue();
        int monthInt = Integer.valueOf(String.format("%02x", new Object[]{Byte.valueOf(month)}), 16).intValue();
        int dayInt = Integer.valueOf(String.format("%02x", new Object[]{Byte.valueOf(day)}), 16).intValue();
        int hoursMinutesInt = Integer.valueOf(String.format("%02x", new Object[]{Byte.valueOf(hoursminutes)}), 16).intValue();
        int minutes = hoursMinutesInt % 4;
        return (int) (new GregorianCalendar(yearInt + ActivityUser.defaultUserCaloriesBurnt, monthInt - 1, dayInt, (hoursMinutesInt - minutes) / 4, minutes * 15).getTimeInMillis() / 1000);
    }
}
