package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

import android.bluetooth.BluetoothGattCharacteristic;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.AbstractBleProfile;
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertNotificationProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile<T> {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AlertNotificationProfile.class);
    private int maxLength = 18;

    public AlertNotificationProfile(T support) {
        super(support);
    }

    public void setMaxLength(int maxLength2) {
        this.maxLength = maxLength2;
    }

    public void configure(TransactionBuilder builder, AlertNotificationControl control) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_NOTIFICATION_CONTROL_POINT);
        if (characteristic != null) {
            builder.write(characteristic, control.getControlMessage());
        }
    }

    public void updateAlertLevel(TransactionBuilder builder, AlertLevel level) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL);
        if (characteristic != null) {
            builder.write(characteristic, new byte[]{BLETypeConversions.fromUint8(level.getId())});
        }
    }

    public void newAlert(TransactionBuilder builder, NewAlert alert, OverflowStrategy strategy) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_NEW_ALERT);
        if (characteristic != null) {
            String message = StringUtils.ensureNotNull(alert.getMessage());
            if (message.length() > this.maxLength && strategy == OverflowStrategy.TRUNCATE) {
                message = StringUtils.truncate(message, this.maxLength);
            }
            int numChunks = message.length() / this.maxLength;
            if (message.length() % this.maxLength > 0) {
                numChunks++;
            }
            boolean hasAlerted = false;
            int i = 0;
            while (true) {
                if (i >= numChunks) {
                    break;
                }
                try {
                    int offset = this.maxLength * i;
                    message = message.substring(offset, Math.min(this.maxLength, message.length() - offset) + offset);
                    if (hasAlerted && message.length() == 0) {
                        break;
                    }
                    builder.write(characteristic, getAlertMessage(alert, message, 1));
                    hasAlerted = true;
                    i++;
                } catch (IOException e) {
                    LOG.error("Error writing alert message to ByteArrayOutputStream");
                    return;
                }
            }
            if (!hasAlerted) {
                builder.write(characteristic, getAlertMessage(alert, "", 1));
                return;
            }
            return;
        }
        LOG.warn("NEW_ALERT characteristic not available");
    }

    public void newAlert(TransactionBuilder builder, NewAlert alert) {
        newAlert(builder, alert, OverflowStrategy.TRUNCATE);
    }

    /* access modifiers changed from: protected */
    public byte[] getAlertMessage(NewAlert alert, String message, int chunk) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(100);
        stream.write(BLETypeConversions.fromUint8(alert.getCategory().getId()));
        stream.write(BLETypeConversions.fromUint8(alert.getNumAlerts()));
        if (alert.getCategory() == AlertCategory.CustomHuami) {
            stream.write(BLETypeConversions.fromUint8(alert.getCustomIcon()));
        }
        if (message.length() > 0) {
            stream.write(BLETypeConversions.toUtf8s(message));
        }
        return stream.toByteArray();
    }
}
