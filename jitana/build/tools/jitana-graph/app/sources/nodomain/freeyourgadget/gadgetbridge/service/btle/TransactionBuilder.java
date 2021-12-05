package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.NotifyAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.ReadAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.RequestMtuAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.WaitAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.WriteAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionBuilder {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) TransactionBuilder.class);
    private boolean mQueued;
    private final Transaction mTransaction;

    public TransactionBuilder(String taskName) {
        this.mTransaction = new Transaction(taskName);
    }

    public TransactionBuilder read(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null) {
            return add(new ReadAction(characteristic));
        }
        LOG.warn("Unable to read characteristic: null");
        return this;
    }

    public TransactionBuilder write(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (characteristic != null) {
            return add(new WriteAction(characteristic, data));
        }
        LOG.warn("Unable to write characteristic: null");
        return this;
    }

    public TransactionBuilder requestMtu(int mtu) {
        return add(new RequestMtuAction(mtu));
    }

    public TransactionBuilder notify(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (characteristic != null) {
            return add(createNotifyAction(characteristic, enable));
        }
        LOG.warn("Unable to notify characteristic: null");
        return this;
    }

    /* access modifiers changed from: protected */
    public NotifyAction createNotifyAction(BluetoothGattCharacteristic characteristic, boolean enable) {
        return new NotifyAction(characteristic, enable);
    }

    public TransactionBuilder wait(int millis) {
        return add(new WaitAction(millis));
    }

    public TransactionBuilder add(BtLEAction action) {
        this.mTransaction.add(action);
        return this;
    }

    public void setGattCallback(GattCallback callback) {
        this.mTransaction.setGattCallback(callback);
    }

    public GattCallback getGattCallback() {
        return this.mTransaction.getGattCallback();
    }

    public void queue(BtLEQueue queue) {
        if (!this.mQueued) {
            this.mQueued = true;
            queue.add(this.mTransaction);
            return;
        }
        throw new IllegalStateException("This builder had already been queued. You must not reuse it.");
    }

    public Transaction getTransaction() {
        return this.mTransaction;
    }

    public String getTaskName() {
        return this.mTransaction.getTaskName();
    }
}
