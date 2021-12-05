package nodomain.freeyourgadget.gadgetbridge.service.devices.jyou;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.net.Uri;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.BFH16Constants;
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
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.net.SyslogConstants;

public class BFH16DeviceSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) BFH16DeviceSupport.class);
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    public BluetoothGattCharacteristic ctrlCharacteristic = null;
    public BluetoothGattCharacteristic measureCharacteristic = null;
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public BFH16DeviceSupport() {
        super(LOG);
        addSupportedService(BFH16Constants.BFH16_SERVICE1);
        addSupportedService(BFH16Constants.BFH16_SERVICE2);
    }

    public boolean useAutoConnect() {
        return true;
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        LOG.info("Initializing BFH16");
        this.gbDevice.setState(GBDevice.State.INITIALIZING);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        this.measureCharacteristic = getCharacteristic(BFH16Constants.BFH16_SERVICE1_NOTIFY);
        this.ctrlCharacteristic = getCharacteristic(BFH16Constants.BFH16_SERVICE1_WRITE);
        builder.setGattCallback(this);
        builder.notify(this.measureCharacteristic, true);
        syncSettings(builder);
        this.gbDevice.setState(GBDevice.State.INITIALIZED);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        LOG.info("Initialization BFH16 Done");
        return builder;
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
        if (b == -24) {
            Logger logger = LOG;
            logger.info("Current heart rate: " + data[1]);
            Logger logger2 = LOG;
            logger2.info("Current blood pressure: " + data[2] + "/" + data[3]);
            Logger logger3 = LOG;
            StringBuilder sb = new StringBuilder();
            sb.append("Current satiation: ");
            sb.append(data[4]);
            logger3.info(sb.toString());
            return true;
        } else if (b == -13) {
            Logger logger4 = LOG;
            logger4.info("Received photo trigger: " + data[8]);
            return true;
        } else if (b == -7) {
            int steps = ByteBuffer.wrap(data, 5, 4).getInt();
            Logger logger5 = LOG;
            logger5.info("Number of walked steps: " + steps);
            return true;
        } else if (b == -10) {
            int fwVerNum = data[4] & 255;
            GBDeviceEventVersionInfo gBDeviceEventVersionInfo = this.versionCmd;
            gBDeviceEventVersionInfo.fwVersion = (fwVerNum / 100) + "." + ((fwVerNum % 100) / 10) + "." + ((fwVerNum % 100) % 10);
            handleGBDeviceEvent(this.versionCmd);
            Logger logger6 = LOG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Firmware version is: ");
            sb2.append(this.versionCmd.fwVersion);
            logger6.info(sb2.toString());
            return true;
        } else if (b != -9) {
            Logger logger7 = LOG;
            logger7.info("Unhandled characteristic change: " + characteristicUUID + " code: " + String.format("0x%1x ...", new Object[]{Byte.valueOf(data[0])}));
            Logger logger8 = LOG;
            logger8.info("Unhandled characteristic data: " + data[0] + StringUtils.SPACE + data[1] + StringUtils.SPACE + data[2] + StringUtils.SPACE + data[3] + StringUtils.SPACE + data[4] + StringUtils.SPACE + data[5] + StringUtils.SPACE + data[6] + StringUtils.SPACE + data[7] + StringUtils.SPACE + data[8]);
            return true;
        } else {
            GBDeviceEventBatteryInfo gBDeviceEventBatteryInfo = this.batteryCmd;
            gBDeviceEventBatteryInfo.level = (short) data[8];
            handleGBDeviceEvent(gBDeviceEventBatteryInfo);
            Logger logger9 = LOG;
            logger9.info("Battery level is: " + this.batteryCmd.level);
            return true;
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        byte cmd;
        try {
            TransactionBuilder builder = performInitialized("SetAlarms");
            for (int i = 0; i < alarms.size(); i++) {
                if (i == 0) {
                    cmd = 9;
                } else if (i == 1) {
                    cmd = 34;
                } else if (i == 2) {
                    cmd = 35;
                } else {
                    return;
                }
                Calendar cal = AlarmUtils.toCalendar((Alarm) alarms.get(i));
                BluetoothGattCharacteristic bluetoothGattCharacteristic = this.ctrlCharacteristic;
                int i2 = -1;
                int i3 = ((Alarm) alarms.get(i)).getEnabled() ? cal.get(11) : -1;
                if (((Alarm) alarms.get(i)).getEnabled()) {
                    i2 = cal.get(12);
                }
                builder.write(bluetoothGattCharacteristic, commandWithChecksum(cmd, i3, i2));
            }
            builder.queue(getQueue());
            C1238GB.toast(getContext(), "Alarm settings applied - do note that the current device does not support day specification", 1, 1);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("SetTime");
            syncDateAndTime(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    public void onFindDevice(boolean start) {
        try {
            TransactionBuilder builder = performInitialized("FindDevice");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 7, 0, start ? 1 : 0));
            builder.queue(getQueue());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        C1238GB.toast(getContext(), "Your device will vibrate 3 times!", 1, 1);
    }

    public void onNotification(NotificationSpec notificationSpec) {
        byte icon;
        String notificationTitle = nodomain.freeyourgadget.gadgetbridge.util.StringUtils.getFirstOf(notificationSpec.sender, notificationSpec.title);
        int i = C11871.f184xadd9a595[notificationSpec.type.ordinal()];
        if (i == 1) {
            icon = 1;
        } else if (i == 2 || i == 3) {
            icon = 4;
        } else if (i == 4) {
            icon = 6;
        } else if (i != 5) {
            icon = 8;
        } else {
            icon = 7;
        }
        showNotification(icon, notificationTitle, notificationSpec.body);
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.BFH16DeviceSupport$1 */
    static /* synthetic */ class C11871 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$model$NotificationType */
        static final /* synthetic */ int[] f184xadd9a595 = new int[NotificationType.values().length];

        static {
            try {
                f184xadd9a595[NotificationType.GENERIC_SMS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f184xadd9a595[NotificationType.FACEBOOK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f184xadd9a595[NotificationType.FACEBOOK_MESSENGER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f184xadd9a595[NotificationType.TWITTER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f184xadd9a595[NotificationType.WHATSAPP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command == 2) {
            showNotification((byte) 0, callSpec.name, callSpec.number);
        }
    }

    public void onEnableRealtimeSteps(boolean enable) {
        onEnableRealtimeHeartRateMeasurement(enable);
    }

    public void onReset(int flags) {
        try {
            TransactionBuilder builder = performInitialized("Reboot");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 14, 0, 0));
            builder.queue(getQueue());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
    }

    public void onHeartRateTest() {
        try {
            TransactionBuilder builder = performInitialized("HeartRateTest");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 13, 0, 1));
            builder.queue(getQueue());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        try {
            TransactionBuilder builder = performInitialized("RealTimeHeartMeasurement");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 13, 0, enable ? 1 : 0));
            builder.queue(getQueue());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
    }

    public void onSetConstantVibration(int integer) {
        try {
            TransactionBuilder builder = performInitialized("Vibrate");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 7, 0, 1));
            builder.queue(getQueue());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
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

    public void onSendWeather(WeatherSpec weatherSpec) {
    }

    public void onTestNewFunction() {
        showNotification((byte) -1, "", "");
    }

    private void showNotification(byte icon, String title, String message) {
        try {
            TransactionBuilder builder = performInitialized("ShowNotification");
            byte[] titleBytes = stringToUTF8Bytes(title, 16);
            byte[] messageBytes = stringToUTF8Bytes(message, 80);
            for (int i = 1; i <= 7; i++) {
                byte[] currentPacket = new byte[20];
                currentPacket[0] = 44;
                currentPacket[1] = 7;
                currentPacket[2] = (byte) i;
                if (i == 1) {
                    currentPacket[4] = icon;
                } else if (i != 2) {
                    if (messageBytes != null) {
                        System.arraycopy(messageBytes, (i - 3) * 16, currentPacket, 3, 6);
                        System.arraycopy(messageBytes, ((i - 3) * 16) + 6, currentPacket, 10, 10);
                    }
                } else if (titleBytes != null) {
                    System.arraycopy(titleBytes, 0, currentPacket, 3, 6);
                    System.arraycopy(titleBytes, 6, currentPacket, 10, 10);
                }
                builder.write(this.ctrlCharacteristic, currentPacket);
            }
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    private void syncSettings(TransactionBuilder builder) {
        syncDateAndTime(builder);
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 1, 0, SyslogConstants.LOG_LOCAL3));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 3, 0, 10000));
        builder.write(this.ctrlCharacteristic, commandWithChecksum(BFH16Constants.CMD_SWITCH_METRIC_IMPERIAL, 0, 0));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 50, 0, 0));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 38, 43200, 50400));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 39, 75600, 28800));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 36, 0, 0));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 57, (22 << 24) | (0 << 16) | (8 << 8) | 0, ((0 ^ 1) << 2) | (1 << 1) | 1));
    }

    private void syncDateAndTime(TransactionBuilder builder) {
        Calendar cal = Calendar.getInstance();
        String strYear = String.valueOf(cal.get(1));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 8, (((byte) Integer.parseInt(strYear.substring(0, 2))) << 24) | (((byte) Integer.parseInt(strYear.substring(2, 4))) << 16) | (((byte) cal.get(2)) << 8) | ((byte) cal.get(5)), (((byte) cal.get(11)) << 24) | (((byte) cal.get(12)) << 16) | (((byte) cal.get(13)) << 8) | ((byte) cal.get(7))));
    }

    private byte[] commandWithChecksum(byte cmd, int argSlot1, int argSlot2) {
        ByteBuffer buf = ByteBuffer.allocate(10);
        buf.put(cmd);
        buf.putInt(argSlot1);
        buf.putInt(argSlot2);
        byte[] bytesToWrite = buf.array();
        byte checksum = 0;
        for (byte b : bytesToWrite) {
            checksum = (byte) (checksum + b);
        }
        bytesToWrite[9] = checksum;
        return bytesToWrite;
    }

    private byte[] commandWithChecksum(byte s0, byte s1, byte s2, byte s3, byte s4, byte s5, byte s6, byte s7, byte s8) {
        ByteBuffer buf = ByteBuffer.allocate(10);
        buf.put(s0);
        buf.put(s1);
        buf.put(s2);
        buf.put(s3);
        buf.put(s4);
        buf.put(s5);
        buf.put(s6);
        buf.put(s7);
        buf.put(s8);
        byte[] bytesToWrite = buf.array();
        byte checksum = 0;
        for (byte b : bytesToWrite) {
            checksum = (byte) (checksum + b);
        }
        LOG.debug("Checksum = " + checksum);
        bytesToWrite[9] = checksum;
        return bytesToWrite;
    }

    private byte[] stringToUTF8Bytes(String src, int byteCount) {
        if (src == null) {
            return null;
        }
        try {
            for (int i = src.length(); i > 0; i--) {
                byte[] subUTF8 = src.substring(0, i).getBytes(CharEncoding.UTF_8);
                if (subUTF8.length == byteCount) {
                    return subUTF8;
                }
                if (subUTF8.length < byteCount) {
                    byte[] largerSubUTF8 = new byte[byteCount];
                    System.arraycopy(subUTF8, 0, largerSubUTF8, 0, subUTF8.length);
                    return largerSubUTF8;
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOG.warn(e.getMessage());
        }
        return null;
    }
}
