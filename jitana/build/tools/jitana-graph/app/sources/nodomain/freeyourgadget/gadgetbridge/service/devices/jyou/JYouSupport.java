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
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.JYouConstants;
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
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;
import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;

public class JYouSupport extends AbstractBTLEDeviceSupport {
    protected final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    protected BluetoothGattCharacteristic ctrlCharacteristic = null;
    private Logger logger;
    protected final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public JYouSupport(Logger logger2) {
        super(logger2);
        this.logger = logger2;
        if (logger2 != null) {
            addSupportedService(JYouConstants.UUID_SERVICE_JYOU);
            return;
        }
        throw new IllegalArgumentException("logger must not be null");
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        this.logger.info("Initializing");
        this.gbDevice.setState(GBDevice.State.INITIALIZING);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        BluetoothGattCharacteristic measureCharacteristic = getCharacteristic(JYouConstants.UUID_CHARACTERISTIC_MEASURE);
        this.ctrlCharacteristic = getCharacteristic(JYouConstants.UUID_CHARACTERISTIC_CONTROL);
        builder.setGattCallback(this);
        builder.notify(measureCharacteristic, true);
        syncSettings(builder);
        this.gbDevice.setState(GBDevice.State.INITIALIZED);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        this.logger.info("Initialization Done");
        return builder;
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        return super.onCharacteristicChanged(gatt, characteristic);
    }

    /* access modifiers changed from: protected */
    public void syncDateAndTime(TransactionBuilder builder) {
        Calendar cal = Calendar.getInstance();
        String strYear = String.valueOf(cal.get(1));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 8, (((byte) Integer.parseInt(strYear.substring(0, 2))) << 24) | (((byte) Integer.parseInt(strYear.substring(2, 4))) << 16) | (((byte) cal.get(2)) << 8) | ((byte) cal.get(5)), (((byte) cal.get(11)) << 24) | (((byte) cal.get(12)) << 16) | (((byte) cal.get(13)) << 8) | ((byte) cal.get(7))));
    }

    /* access modifiers changed from: protected */
    public void syncSettings(TransactionBuilder builder) {
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
            performConnected(builder.getTransaction());
        } catch (IOException e) {
            this.logger.warn(e.getMessage());
        }
    }

    public boolean useAutoConnect() {
        return true;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        byte icon;
        String notificationTitle = StringUtils.getFirstOf(notificationSpec.sender, notificationSpec.title);
        int i = C11881.f185xadd9a595[notificationSpec.type.ordinal()];
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

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.JYouSupport$1 */
    static /* synthetic */ class C11881 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$model$NotificationType */
        static final /* synthetic */ int[] f185xadd9a595 = new int[NotificationType.values().length];

        static {
            try {
                f185xadd9a595[NotificationType.GENERIC_SMS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f185xadd9a595[NotificationType.FACEBOOK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f185xadd9a595[NotificationType.FACEBOOK_MESSENGER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f185xadd9a595[NotificationType.TWITTER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f185xadd9a595[NotificationType.WHATSAPP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public void onDeleteNotification(int id) {
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
            performConnected(builder.getTransaction());
            C1238GB.toast(getContext(), "Alarm settings applied - do note that the current device does not support day specification", 1, 1);
        } catch (IOException e) {
            this.logger.warn(e.getMessage());
        }
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("SetTime");
            syncDateAndTime(builder);
            performConnected(builder.getTransaction());
        } catch (IOException e) {
            this.logger.warn(e.getMessage());
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command == 2) {
            showNotification((byte) 0, callSpec.name, callSpec.number);
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
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
    }

    public void onReset(int flags) {
        try {
            TransactionBuilder builder = performInitialized("Reboot");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 14, 0, 0));
            performConnected(builder.getTransaction());
        } catch (Exception e) {
            this.logger.warn(e.getMessage());
        }
    }

    public void onHeartRateTest() {
        try {
            TransactionBuilder builder = performInitialized("HeartRateTest");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 13, 0, 1));
            performConnected(builder.getTransaction());
        } catch (Exception e) {
            this.logger.warn(e.getMessage());
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        try {
            TransactionBuilder builder = performInitialized("RealTimeHeartMeasurement");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 56, 0, enable ? 1 : 0));
            builder.queue(getQueue());
        } catch (Exception e) {
            this.logger.warn(e.getMessage());
        }
    }

    public void onFindDevice(boolean start) {
        if (start) {
            showNotification((byte) 3, GBApplication.DATABASE_NAME, "Bzzt! Bzzt!");
            C1238GB.toast(getContext(), "As your device doesn't have sound, it will only vibrate 3 times consecutively", 1, 1);
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

    /* access modifiers changed from: protected */
    public byte[] commandWithChecksum(byte cmd, int argSlot1, int argSlot2) {
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
            this.logger.warn(e.getMessage());
        }
        return null;
    }
}
