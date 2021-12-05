package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteAction extends BtLEAction {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) WriteAction.class);
    private final byte[] value;

    public WriteAction(BluetoothGattCharacteristic characteristic, byte[] value2) {
        super(characteristic);
        this.value = value2;
    }

    public boolean run(BluetoothGatt gatt) {
        BluetoothGattCharacteristic characteristic = getCharacteristic();
        int properties = characteristic.getProperties();
        if ((properties & 8) > 0 || (properties & 4) > 0) {
            return writeValue(gatt, characteristic, this.value);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean writeValue(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value2) {
        if (LOG.isDebugEnabled()) {
            Logger logger = LOG;
            logger.debug("writing to characteristic: " + characteristic.getUuid() + ": " + Logging.formatBytes(value2));
        }
        if (characteristic.setValue(value2)) {
            return gatt.writeCharacteristic(characteristic);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public final byte[] getValue() {
        return this.value;
    }

    public boolean expectsResult() {
        return true;
    }
}
