package nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.TeclastH30;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.JYouConstants;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.JYouSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.net.SyslogConstants;

public class TeclastH30Support extends JYouSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) TeclastH30Support.class);

    public TeclastH30Support() {
        super(LOG);
        addSupportedService(JYouConstants.UUID_SERVICE_JYOU);
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
        if (b == -10) {
            int fwVerNum = data[4] & 255;
            GBDeviceEventVersionInfo gBDeviceEventVersionInfo = this.versionCmd;
            gBDeviceEventVersionInfo.fwVersion = (fwVerNum / 100) + "." + ((fwVerNum % 100) / 10) + "." + ((fwVerNum % 100) % 10);
            handleGBDeviceEvent(this.versionCmd);
            Logger logger = LOG;
            StringBuilder sb = new StringBuilder();
            sb.append("Firmware version is: ");
            sb.append(this.versionCmd.fwVersion);
            logger.info(sb.toString());
            return true;
        } else if (b == -9) {
            this.batteryCmd.level = (short) data[8];
            handleGBDeviceEvent(this.batteryCmd);
            Logger logger2 = LOG;
            logger2.info("Battery level is: " + this.batteryCmd.level);
            return true;
        } else if (b == -7) {
            int steps = ByteBuffer.wrap(data, 5, 4).getInt();
            Logger logger3 = LOG;
            logger3.info("Number of walked steps: " + steps);
            return true;
        } else if (b != -4) {
            Logger logger4 = LOG;
            logger4.info("Unhandled characteristic change: " + characteristicUUID + " code: " + String.format("0x%1x ...", new Object[]{Byte.valueOf(data[0])}));
            return true;
        } else {
            Logger logger5 = LOG;
            logger5.info("Current heart rate: " + data[8]);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void syncSettings(TransactionBuilder builder) {
        syncDateAndTime(builder);
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 1, 0, SyslogConstants.LOG_LOCAL3));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 3, 0, 10000));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 29, 0, 0));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 50, 0, 0));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 38, 43200, 50400));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 39, 75600, 28800));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 36, 0, 0));
        builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 57, (22 << 24) | (0 << 16) | (8 << 8) | 0, ((0 ^ 1) << 2) | (1 << 1) | 1));
    }
}
