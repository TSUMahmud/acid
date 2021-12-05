package nodomain.freeyourgadget.gadgetbridge.service.devices.casiogb6900;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.net.Uri;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl;
import nodomain.freeyourgadget.gadgetbridge.devices.casiogb6900.CasioGB6900Constants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.ServerTransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.casiogb6900.operations.InitOperation;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasioGB6900DeviceSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CasioGB6900DeviceSupport.class);
    private static final int mCasioSleepTime = 50;
    private BluetoothGatt mBtGatt = null;
    private MusicSpec mBufferMusicSpec = null;
    private MusicStateSpec mBufferMusicStateSpec = null;
    private ArrayList<BluetoothGattCharacteristic> mCasioCharacteristics = new ArrayList<>();
    private boolean mFirstConnect = false;
    private CasioHandlerThread mHandlerThread = null;
    private CasioGB6900Constants.Model mModel = CasioGB6900Constants.Model.MODEL_CASIO_GENERIC;

    public CasioGB6900DeviceSupport() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_IMMEDIATE_ALERT);
        addSupportedService(CasioGB6900Constants.CASIO_VIRTUAL_SERVER_SERVICE);
        addSupportedService(CasioGB6900Constants.ALERT_SERVICE_UUID);
        addSupportedService(CasioGB6900Constants.CASIO_IMMEDIATE_ALERT_SERVICE_UUID);
        addSupportedService(CasioGB6900Constants.CURRENT_TIME_SERVICE_UUID);
        addSupportedService(CasioGB6900Constants.WATCH_CTRL_SERVICE_UUID);
        addSupportedService(CasioGB6900Constants.WATCH_FEATURES_SERVICE_UUID);
        addSupportedService(CasioGB6900Constants.CASIO_PHONE_ALERT_STATUS_SERVICE);
        addSupportedService(CasioGB6900Constants.MORE_ALERT_SERVICE_UUID);
        addSupportedService(CasioGB6900Constants.TX_POWER_SERVICE_UUID);
        addSupportedService(CasioGB6900Constants.LINK_LOSS_SERVICE);
        addSupportedService(CasioGB6900Constants.IMMEDIATE_ALERT_SERVICE_UUID);
        BluetoothGattService casioGATTService = new BluetoothGattService(CasioGB6900Constants.WATCH_CTRL_SERVICE_UUID, 0);
        BluetoothGattCharacteristic bluetoothGATTCharacteristic = new BluetoothGattCharacteristic(CasioGB6900Constants.KEY_CONTAINER_CHARACTERISTIC_UUID, 4, 16);
        bluetoothGATTCharacteristic.setValue(new byte[0]);
        BluetoothGattCharacteristic bluetoothGATTCharacteristic2 = new BluetoothGattCharacteristic(CasioGB6900Constants.NAME_OF_APP_CHARACTERISTIC_UUID, 2, 3);
        bluetoothGATTCharacteristic2.setValue(CasioGB6900Constants.MUSIC_MESSAGE.getBytes());
        BluetoothGattDescriptor bluetoothGattDescriptor = new BluetoothGattDescriptor(CasioGB6900Constants.CCC_DESCRIPTOR_UUID, 17);
        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGATTCharacteristic2.addDescriptor(bluetoothGattDescriptor);
        casioGATTService.addCharacteristic(bluetoothGATTCharacteristic);
        casioGATTService.addCharacteristic(bluetoothGATTCharacteristic2);
        addSupportedServerService(casioGATTService);
    }

    public boolean connectFirstTime() {
        C1238GB.toast(getContext(), "After first connect, disable and enable bluetooth on your Casio watch to really connect", 0, 1);
        this.mFirstConnect = true;
        return super.connect();
    }

    public void dispose() {
        LOG.info("Dispose");
        close();
        super.dispose();
    }

    private void close() {
        CasioHandlerThread casioHandlerThread = this.mHandlerThread;
        if (casioHandlerThread != null) {
            casioHandlerThread.quit();
            this.mHandlerThread.interrupt();
            this.mHandlerThread = null;
        }
    }

    public void onServicesDiscovered(BluetoothGatt gatt) {
        this.mBtGatt = gatt;
        super.onServicesDiscovered(gatt);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        LOG.info("Initializing");
        if (this.mFirstConnect) {
            this.gbDevice.setState(GBDevice.State.INITIALIZED);
            this.gbDevice.sendDeviceUpdateIntent(getContext());
            getDevice().setFirmwareVersion("N/A");
            getDevice().setFirmwareVersion2("N/A");
            return builder;
        }
        String name = this.gbDevice.getName();
        if (name.contains("5600B")) {
            this.mModel = CasioGB6900Constants.Model.MODEL_CASIO_5600B;
        } else if (name.contains("6900B")) {
            this.mModel = CasioGB6900Constants.Model.MODEL_CASIO_6900B;
        } else {
            this.mModel = CasioGB6900Constants.Model.MODEL_CASIO_GENERIC;
        }
        try {
            new InitOperation(this, builder).perform();
        } catch (IOException e) {
            C1238GB.toast(getContext(), "Initializing Casio watch failed", 0, 3, e);
        }
        getDevice().setFirmwareVersion("N/A");
        getDevice().setFirmwareVersion2("N/A");
        builder.setGattCallback(this);
        configureWatch(builder);
        addCharacteristics();
        enableNotifications(builder, true);
        LOG.info("Initialization Done");
        return builder;
    }

    /* access modifiers changed from: package-private */
    public CasioGB6900Constants.Model getModel() {
        return this.mModel;
    }

    private void configureWatch(TransactionBuilder builder) {
        BluetoothGatt bluetoothGatt = this.mBtGatt;
        if (bluetoothGatt != null) {
            builder.write(bluetoothGatt.getService(CasioGB6900Constants.LINK_LOSS_SERVICE).getCharacteristic(CasioGB6900Constants.ALERT_LEVEL_CHARACTERISTIC_UUID), new byte[]{1});
            builder.wait(50);
        }
    }

    private void addCharacteristics() {
        this.mCasioCharacteristics.clear();
        this.mCasioCharacteristics.add(getCharacteristic(CasioGB6900Constants.CASIO_A_NOT_COM_SET_NOT));
        this.mCasioCharacteristics.add(getCharacteristic(CasioGB6900Constants.CASIO_A_NOT_W_REQ_NOT));
        this.mCasioCharacteristics.add(getCharacteristic(CasioGB6900Constants.ALERT_LEVEL_CHARACTERISTIC_UUID));
        this.mCasioCharacteristics.add(getCharacteristic(CasioGB6900Constants.RINGER_CONTROL_POINT));
    }

    public boolean enableNotifications(TransactionBuilder builder, boolean enable) {
        Iterator<BluetoothGattCharacteristic> it = this.mCasioCharacteristics.iterator();
        while (it.hasNext()) {
            builder.notify(it.next(), enable);
            builder.wait(50);
        }
        return true;
    }

    public void readTxPowerLevel() {
        try {
            TransactionBuilder builder = performInitialized("readTxPowerLevel");
            builder.read(getCharacteristic(CasioGB6900Constants.TX_POWER_LEVEL_CHARACTERISTIC_UUID));
            builder.queue(getQueue());
        } catch (IOException e) {
            Logger logger = LOG;
            logger.warn("readTxPowerLevel failed: " + e.getMessage());
        }
    }

    private void writeCasioCurrentTime(TransactionBuilder builder) {
        byte[] arr = new byte[10];
        Calendar cal = Calendar.getInstance();
        int year = cal.get(1);
        arr[0] = (byte) ((year >>> 0) & 255);
        arr[1] = (byte) ((year >>> 8) & 255);
        arr[2] = (byte) (cal.get(2) + 1);
        arr[3] = (byte) cal.get(5);
        arr[4] = (byte) cal.get(11);
        arr[5] = (byte) cal.get(12);
        arr[6] = (byte) (cal.get(13) + 1);
        byte dayOfWk = (byte) (cal.get(7) - 1);
        if (dayOfWk == 0) {
            dayOfWk = 7;
        }
        arr[7] = dayOfWk;
        arr[8] = (byte) ((int) TimeUnit.MILLISECONDS.toSeconds((long) (cal.get(14) * 256)));
        arr[9] = 1;
        BluetoothGattCharacteristic charact = getCharacteristic(CasioGB6900Constants.CURRENT_TIME_CHARACTERISTIC_UUID);
        if (charact != null) {
            charact.setWriteType(2);
            builder.write(charact, arr);
            return;
        }
        LOG.warn("Characteristic not found: CURRENT_TIME_CHARACTERISTIC_UUID");
    }

    private void writeCasioLocalTimeInformation(TransactionBuilder builder) {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = (int) TimeUnit.MILLISECONDS.toMinutes((long) cal.get(15));
        byte byte0 = (byte) (zoneOffset / 15);
        byte byte1 = (byte) (((int) TimeUnit.MILLISECONDS.toMinutes((long) cal.get(16))) / 15);
        BluetoothGattCharacteristic charact = getCharacteristic(CasioGB6900Constants.LOCAL_TIME_CHARACTERISTIC_UUID);
        if (charact != null) {
            builder.write(charact, new byte[]{byte0, byte1});
            return;
        }
        LOG.warn("Characteristic not found: LOCAL_TIME_CHARACTERISTIC_UUID");
    }

    private void writeCasioVirtualServerFeature(TransactionBuilder builder) {
        byte byte0 = (byte) (((byte) (((byte) (((byte) (0 | 1)) | 2)) | 4)) | 8);
        BluetoothGattCharacteristic charact = getCharacteristic(CasioGB6900Constants.CASIO_VIRTUAL_SERVER_FEATURES);
        if (charact != null) {
            builder.write(charact, new byte[]{byte0, 0});
            return;
        }
        LOG.warn("Characteristic not found: CASIO_VIRTUAL_SERVER_FEATURES");
    }

    private boolean handleInitResponse(byte data) {
        if (data != 1) {
            Logger logger = LOG;
            logger.warn("handleInitResponse: Error initializing device, received unexpected value: " + data);
            this.gbDevice.setState(GBDevice.State.NOT_CONNECTED);
            this.gbDevice.sendDeviceUpdateIntent(getContext());
            return true;
        }
        LOG.info("Initialization done, setting state to INITIALIZED");
        CasioHandlerThread casioHandlerThread = this.mHandlerThread;
        if (casioHandlerThread != null && casioHandlerThread.isAlive()) {
            this.mHandlerThread.quit();
            this.mHandlerThread.interrupt();
        }
        this.mHandlerThread = new CasioHandlerThread(getDevice(), getContext(), this);
        this.mHandlerThread.start();
        this.gbDevice.setState(GBDevice.State.INITIALIZED);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        return true;
    }

    private boolean handleTimeRequests(byte data) {
        if (data == 1) {
            try {
                TransactionBuilder builder = createTransactionBuilder("writeCasioCurrentTime");
                writeCasioCurrentTime(builder);
                performConnected(builder.getTransaction());
                return true;
            } catch (IOException e) {
                Logger logger = LOG;
                logger.warn("handleTimeRequests::writeCasioCurrentTime failed: " + e.getMessage());
                return false;
            }
        } else if (data != 2) {
            return false;
        } else {
            try {
                TransactionBuilder builder2 = createTransactionBuilder("writeCasioLocalTimeInformation");
                writeCasioLocalTimeInformation(builder2);
                performConnected(builder2.getTransaction());
                return true;
            } catch (IOException e2) {
                Logger logger2 = LOG;
                logger2.warn("handleTimeRequests::writeCasioLocalTimeInformation failed: " + e2.getMessage());
                return false;
            }
        }
    }

    private boolean handleServerFeatureRequests(byte data) {
        try {
            TransactionBuilder builder = createTransactionBuilder("writeCasioVirtualServerFeature");
            writeCasioVirtualServerFeature(builder);
            performConnected(builder.getTransaction());
            return true;
        } catch (IOException e) {
            Logger logger = LOG;
            logger.warn("handleServerFeatureRequests failed: " + e.getMessage());
            return true;
        }
    }

    private boolean handleCasioCom(byte[] data, boolean handleTime) {
        if (data.length < 3) {
            LOG.warn("handleCasioCom failed: Received unexpected request (too short)");
            return false;
        }
        byte b = data[0];
        if (b == 0) {
            return handleInitResponse(data[2]);
        }
        if (b != 2) {
            if (b != 7) {
                return false;
            }
            return handleServerFeatureRequests(data[2]);
        } else if (handleTime) {
            return handleTimeRequests(data[2]);
        } else {
            return true;
        }
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        UUID characteristicUUID = characteristic.getUuid();
        byte[] data = characteristic.getValue();
        if (data.length == 0) {
            return true;
        }
        if (!characteristicUUID.equals(CasioGB6900Constants.TX_POWER_LEVEL_CHARACTERISTIC_UUID)) {
            return super.onCharacteristicRead(gatt, characteristic, status);
        }
        String str = "onCharacteristicRead: Received power level: ";
        for (int i = 0; i < data.length; i++) {
            str = str + String.format("0x%1x ", new Object[]{Byte.valueOf(data[i])});
        }
        LOG.info(str);
        return true;
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        boolean handled = false;
        UUID characteristicUUID = characteristic.getUuid();
        byte[] data = characteristic.getValue();
        if (data.length == 0) {
            return true;
        }
        if (characteristicUUID.equals(CasioGB6900Constants.CASIO_A_NOT_W_REQ_NOT)) {
            handled = handleCasioCom(data, true);
        }
        if (characteristicUUID.equals(CasioGB6900Constants.CASIO_A_NOT_COM_SET_NOT)) {
            handled = handleCasioCom(data, false);
        }
        if (characteristicUUID.equals(CasioGB6900Constants.ALERT_LEVEL_CHARACTERISTIC_UUID)) {
            GBDeviceEventFindPhone findPhoneEvent = new GBDeviceEventFindPhone();
            if (data[0] == 2) {
                findPhoneEvent.event = GBDeviceEventFindPhone.Event.START;
            } else {
                findPhoneEvent.event = GBDeviceEventFindPhone.Event.STOP;
            }
            evaluateGBDeviceEvent(findPhoneEvent);
            handled = true;
        }
        if (characteristicUUID.equals(CasioGB6900Constants.RINGER_CONTROL_POINT)) {
            if (data[0] == 2) {
                GBDeviceEventCallControl callControlEvent = new GBDeviceEventCallControl();
                callControlEvent.event = GBDeviceEventCallControl.Event.IGNORE;
                evaluateGBDeviceEvent(callControlEvent);
            }
            handled = true;
        }
        if (handled) {
            return true;
        }
        Logger logger = LOG;
        logger.info("Unhandled characteristic change: " + characteristicUUID + " code: " + String.format("0x%1x ...", new Object[]{Byte.valueOf(data[0])}));
        return super.onCharacteristicChanged(gatt, characteristic);
    }

    private void showNotification(byte icon, String title, String message) {
        if (isConnected()) {
            try {
                TransactionBuilder builder = performInitialized("showNotification");
                byte[] titleBytes = title.getBytes(StandardCharsets.US_ASCII);
                int i = 18;
                if (titleBytes.length <= 18) {
                    i = titleBytes.length;
                }
                int len = i;
                byte[] msg = new byte[(len + 2)];
                msg[0] = icon;
                msg[1] = 1;
                for (int i2 = 0; i2 < len; i2++) {
                    msg[i2 + 2] = titleBytes[i2];
                }
                builder.write(getCharacteristic(CasioGB6900Constants.ALERT_CHARACTERISTIC_UUID), msg);
                Logger logger = LOG;
                logger.info("Showing notification, title: " + title + " message (not sent): " + message);
                builder.queue(getQueue());
            } catch (IOException e) {
                Logger logger2 = LOG;
                logger2.warn("showNotification failed: " + e.getMessage());
            }
        }
    }

    public boolean useAutoConnect() {
        return true;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        byte icon;
        String notificationTitle = StringUtils.getFirstOf(notificationSpec.sender, notificationSpec.title);
        int i = C11721.f175xadd9a595[notificationSpec.type.ordinal()];
        if (i == 1) {
            icon = 5;
        } else if (i == 2) {
            icon = 7;
        } else if (i != 3) {
            icon = 13;
        } else {
            icon = 1;
        }
        showNotification(icon, notificationTitle, notificationSpec.body);
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        byte[] data = new byte[20];
        if (isConnected()) {
            for (int i = 0; i < alarms.size(); i++) {
                Alarm alm = (Alarm) alarms.get(i);
                if (alm.getEnabled()) {
                    data[i * 4] = 64;
                } else {
                    data[i * 4] = 0;
                }
                if (alm.getRepetition(0)) {
                    int i2 = i * 4;
                    data[i2] = (byte) (data[i2] | 32);
                }
                data[(i * 4) + 1] = 0;
                data[(i * 4) + 2] = (byte) alm.getHour();
                data[(i * 4) + 3] = (byte) alm.getMinute();
            }
            try {
                TransactionBuilder builder = performInitialized("setAlarm");
                builder.write(getCharacteristic(CasioGB6900Constants.CASIO_SETTING_FOR_ALM_CHARACTERISTIC_UUID), data);
                builder.queue(getQueue());
            } catch (IOException e) {
                Logger logger = LOG;
                logger.error("Error setting alarm: " + e.getMessage());
            }
        }
    }

    public void onSetTime() {
        if (isConnected()) {
            try {
                TransactionBuilder builder = performInitialized("SetTime");
                writeCasioLocalTimeInformation(builder);
                writeCasioCurrentTime(builder);
                builder.queue(getQueue());
            } catch (IOException e) {
                Logger logger = LOG;
                logger.warn("onSetTime failed: " + e.getMessage());
            }
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command != 2) {
            LOG.info("not sending CallSpec since only CALL_INCOMING is handled");
        } else {
            showNotification((byte) 3, callSpec.name, callSpec.number);
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
        if (stateSpec != this.mBufferMusicStateSpec) {
            this.mBufferMusicStateSpec = stateSpec;
            sendMusicInfo();
        }
    }

    private void sendMusicInfo() {
        if (isConnected()) {
            try {
                TransactionBuilder builder = performInitialized("sendMusicInfo");
                String info = "";
                if (this.mBufferMusicSpec.track != null && this.mBufferMusicSpec.track.length() > 0) {
                    info = info + this.mBufferMusicSpec.track;
                }
                if (this.mBufferMusicSpec.album != null && this.mBufferMusicSpec.album.length() > 0) {
                    info = info + this.mBufferMusicSpec.album;
                }
                if (this.mBufferMusicSpec.artist != null && this.mBufferMusicSpec.artist.length() > 0) {
                    info = info + this.mBufferMusicSpec.artist;
                }
                byte[] bInfo = info.getBytes(StandardCharsets.US_ASCII);
                int i = 17;
                if (bInfo.length <= 17) {
                    i = bInfo.length;
                }
                int len = i;
                byte[] arr = new byte[(len + 3)];
                arr[0] = 0;
                arr[1] = 10;
                arr[2] = 1;
                for (int i2 = 0; i2 < len; i2++) {
                    arr[i2 + 3] = bInfo[i2];
                }
                builder.write(getCharacteristic(CasioGB6900Constants.MORE_ALERT_FOR_LONG_UUID), arr);
                builder.queue(getQueue());
            } catch (IOException e) {
                LOG.warn("sendMusicInfo failed: " + e.getMessage());
            }
        }
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        if (musicSpec != this.mBufferMusicSpec) {
            this.mBufferMusicSpec = musicSpec;
            sendMusicInfo();
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
        if (isConnected() && start) {
            try {
                TransactionBuilder builder = performInitialized("findDevice");
                builder.write(this.mBtGatt.getService(CasioGB6900Constants.IMMEDIATE_ALERT_SERVICE_UUID).getCharacteristic(CasioGB6900Constants.ALERT_LEVEL_CHARACTERISTIC_UUID), new byte[]{2});
                LOG.info("onFindDevice sent");
                builder.queue(getQueue());
            } catch (IOException e) {
                Logger logger = LOG;
                logger.warn("showNotification failed: " + e.getMessage());
            }
        }
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

    public boolean onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        if (!characteristic.getUuid().equals(CasioGB6900Constants.NAME_OF_APP_CHARACTERISTIC_UUID)) {
            LOG.warn("unexpected read request");
            return false;
        }
        Logger logger = LOG;
        logger.info("will send response to read request from device: " + device.getAddress());
        try {
            ServerTransactionBuilder builder = performServer("sendNameOfApp");
            builder.writeServerResponse(device, requestId, 0, offset, CasioGB6900Constants.MUSIC_MESSAGE.getBytes());
            builder.queue(getQueue());
            return true;
        } catch (IOException e) {
            Logger logger2 = LOG;
            logger2.warn("sendMusicInfo failed: " + e.getMessage());
            return true;
        }
    }

    private GBDeviceEventMusicControl.Event parse3Button(int button) {
        if (button == 1) {
            return GBDeviceEventMusicControl.Event.PLAYPAUSE;
        }
        if (button == 2) {
            return GBDeviceEventMusicControl.Event.PREVIOUS;
        }
        if (button == 3) {
            return GBDeviceEventMusicControl.Event.NEXT;
        }
        Logger logger = LOG;
        logger.warn("Unhandled button received: " + button);
        return GBDeviceEventMusicControl.Event.UNKNOWN;
    }

    private GBDeviceEventMusicControl.Event parse2Button(int button) {
        if (button == 1) {
            return GBDeviceEventMusicControl.Event.NEXT;
        }
        if (button == 2) {
            return GBDeviceEventMusicControl.Event.PLAYPAUSE;
        }
        Logger logger = LOG;
        logger.warn("Unhandled button received: " + button);
        return GBDeviceEventMusicControl.Event.UNKNOWN;
    }

    public boolean onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        GBDeviceEventMusicControl musicCmd = new GBDeviceEventMusicControl();
        if (!characteristic.getUuid().equals(CasioGB6900Constants.KEY_CONTAINER_CHARACTERISTIC_UUID)) {
            LOG.warn("unexpected write request");
            return false;
        }
        if ((value[0] & 3) == 0) {
            int button = value[1] & 15;
            Logger logger = LOG;
            logger.info("Button pressed: " + button);
            int i = C11721.f174x53ce6562[getModel().ordinal()];
            if (i == 1) {
                musicCmd.event = parse2Button(button);
            } else if (i == 2) {
                musicCmd.event = parse3Button(button);
            } else if (i != 3) {
                LOG.warn("Unhandled device");
                return false;
            } else {
                musicCmd.event = parse3Button(button);
            }
            evaluateGBDeviceEvent(musicCmd);
        } else {
            Logger logger2 = LOG;
            logger2.info("received from device: " + value.toString());
        }
        return true;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.casiogb6900.CasioGB6900DeviceSupport$1 */
    static /* synthetic */ class C11721 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$devices$casiogb6900$CasioGB6900Constants$Model */
        static final /* synthetic */ int[] f174x53ce6562 = new int[CasioGB6900Constants.Model.values().length];

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$model$NotificationType */
        static final /* synthetic */ int[] f175xadd9a595 = new int[NotificationType.values().length];

        static {
            try {
                f174x53ce6562[CasioGB6900Constants.Model.MODEL_CASIO_5600B.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f174x53ce6562[CasioGB6900Constants.Model.MODEL_CASIO_6900B.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f174x53ce6562[CasioGB6900Constants.Model.MODEL_CASIO_GENERIC.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f175xadd9a595[NotificationType.GENERIC_SMS.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f175xadd9a595[NotificationType.GENERIC_CALENDAR.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f175xadd9a595[NotificationType.GENERIC_EMAIL.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
        }
    }
}
