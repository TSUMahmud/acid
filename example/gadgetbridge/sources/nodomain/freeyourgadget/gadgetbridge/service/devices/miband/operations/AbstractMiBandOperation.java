package nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations;

import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEOperation;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCallback;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public abstract class AbstractMiBandOperation<T extends AbstractBTLEDeviceSupport> extends AbstractBTLEOperation<T> {
    /* access modifiers changed from: protected */
    public abstract void enableNeededNotifications(TransactionBuilder transactionBuilder, boolean z);

    /* access modifiers changed from: protected */
    public abstract void enableOtherNotifications(TransactionBuilder transactionBuilder, boolean z);

    protected AbstractMiBandOperation(T support) {
        super(support);
    }

    /* access modifiers changed from: protected */
    public void prePerform() throws IOException {
        super.prePerform();
        getDevice().setBusyTask("Operation starting...");
        TransactionBuilder builder = performInitialized("disabling some notifications");
        enableOtherNotifications(builder, false);
        enableNeededNotifications(builder, true);
        builder.queue(getQueue());
    }

    /* access modifiers changed from: protected */
    public void operationFinished() {
        this.operationStatus = OperationStatus.FINISHED;
        if (getDevice() != null && getDevice().isConnected()) {
            unsetBusy();
            try {
                TransactionBuilder builder = performInitialized("reenabling disabled notifications");
                handleFinished(builder);
                builder.setGattCallback((GattCallback) null);
                builder.queue(getQueue());
            } catch (IOException ex) {
                C1238GB.toast(getContext(), "Error enabling Mi Band notifications, you may need to connect and disconnect", 1, 3, ex);
            }
        }
    }

    private void handleFinished(TransactionBuilder builder) {
        enableNeededNotifications(builder, false);
        enableOtherNotifications(builder, true);
    }
}
