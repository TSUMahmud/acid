package nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.io.IOException;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.watch9.Watch9Constants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEOperation;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitOperation extends AbstractBTLEOperation<Watch9DeviceSupport> {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) InitOperation.class);
    private final TransactionBuilder builder;
    private final BluetoothGattCharacteristic cmdCharacteristic = getCharacteristic(Watch9Constants.UUID_CHARACTERISTIC_WRITE);
    private final boolean needsAuth;

    public InitOperation(boolean needsAuth2, Watch9DeviceSupport support, TransactionBuilder builder2) {
        super(support);
        this.needsAuth = needsAuth2;
        this.builder = builder2;
        builder2.setGattCallback(this);
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        this.builder.notify(this.cmdCharacteristic, true);
        if (this.needsAuth) {
            this.builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.AUTHENTICATING, getContext()));
            ((Watch9DeviceSupport) getSupport()).authorizationRequest(this.builder, this.needsAuth);
            return;
        }
        this.builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        ((Watch9DeviceSupport) getSupport()).initialize(this.builder);
        ((Watch9DeviceSupport) getSupport()).performImmediately(this.builder);
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID characteristicUUID = characteristic.getUuid();
        if (!Watch9Constants.UUID_CHARACTERISTIC_WRITE.equals(characteristicUUID) || !this.needsAuth) {
            Logger logger = LOG;
            logger.info("Unhandled characteristic changed: " + characteristicUUID);
            return super.onCharacteristicChanged(gatt, characteristic);
        }
        try {
            byte[] value = characteristic.getValue();
            ((Watch9DeviceSupport) getSupport()).logMessageContent(value);
            if (!ArrayUtils.equals(value, Watch9Constants.RESP_AUTHORIZATION_TASK, 5) || value[8] != 1) {
                return super.onCharacteristicChanged(gatt, characteristic);
            }
            TransactionBuilder builder2 = ((Watch9DeviceSupport) getSupport()).createTransactionBuilder("authInit");
            builder2.setGattCallback(this);
            builder2.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
            ((Watch9DeviceSupport) getSupport()).initialize(builder2).performImmediately(builder2);
            return true;
        } catch (Exception e) {
            C1238GB.toast(getContext(), "Error authenticating Watch 9", 1, 3, e);
        }
    }
}
