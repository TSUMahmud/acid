package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.ServerResponseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerTransactionBuilder {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ServerTransactionBuilder.class);
    private boolean mQueued;
    private final ServerTransaction mTransaction;

    public ServerTransactionBuilder(String taskName) {
        this.mTransaction = new ServerTransaction(taskName);
    }

    public ServerTransactionBuilder writeServerResponse(BluetoothDevice device, int requestId, int status, int offset, byte[] data) {
        if (device != null) {
            return add(new ServerResponseAction(device, requestId, status, offset, data));
        }
        LOG.warn("Unable to write to device: null");
        return this;
    }

    public ServerTransactionBuilder add(BtLEServerAction action) {
        this.mTransaction.add(action);
        return this;
    }

    public void setGattCallback(GattServerCallback callback) {
        this.mTransaction.setGattCallback(callback);
    }

    public GattServerCallback getGattCallback() {
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

    public ServerTransaction getTransaction() {
        return this.mTransaction;
    }

    public String getTaskName() {
        return this.mTransaction.getTaskName();
    }
}
