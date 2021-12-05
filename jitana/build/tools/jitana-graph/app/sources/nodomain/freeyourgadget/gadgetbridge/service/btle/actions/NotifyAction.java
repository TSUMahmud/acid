package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifyAction extends BtLEAction {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) NotifyAction.class);
    protected final boolean enableFlag;
    private boolean hasWrittenDescriptor = true;

    public NotifyAction(BluetoothGattCharacteristic characteristic, boolean enable) {
        super(characteristic);
        this.enableFlag = enable;
    }

    public boolean run(BluetoothGatt gatt) {
        boolean result = gatt.setCharacteristicNotification(getCharacteristic(), this.enableFlag);
        if (result) {
            BluetoothGattDescriptor notifyDescriptor = getCharacteristic().getDescriptor(GattDescriptor.UUID_DESCRIPTOR_GATT_CLIENT_CHARACTERISTIC_CONFIGURATION);
            if (notifyDescriptor != null) {
                int properties = getCharacteristic().getProperties();
                if ((properties & 16) > 0) {
                    LOG.debug("use NOTIFICATION");
                    notifyDescriptor.setValue(this.enableFlag ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    return gatt.writeDescriptor(notifyDescriptor);
                } else if ((properties & 32) > 0) {
                    LOG.debug("use INDICATION");
                    notifyDescriptor.setValue(this.enableFlag ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    boolean result2 = gatt.writeDescriptor(notifyDescriptor);
                    this.hasWrittenDescriptor = true;
                    return result2;
                } else {
                    this.hasWrittenDescriptor = false;
                    return result;
                }
            } else {
                Logger logger = LOG;
                logger.warn("Descriptor CLIENT_CHARACTERISTIC_CONFIGURATION for characteristic " + getCharacteristic().getUuid() + " is null");
                this.hasWrittenDescriptor = false;
                return result;
            }
        } else {
            this.hasWrittenDescriptor = false;
            Logger logger2 = LOG;
            logger2.error("Unable to enable notification for " + getCharacteristic().getUuid());
            return result;
        }
    }

    public boolean expectsResult() {
        return this.hasWrittenDescriptor;
    }
}
