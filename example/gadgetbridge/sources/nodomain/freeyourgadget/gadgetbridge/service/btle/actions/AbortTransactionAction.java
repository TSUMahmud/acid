package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbortTransactionAction extends PlainAction {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbortTransactionAction.class);

    /* access modifiers changed from: protected */
    public abstract boolean shouldAbort();

    public boolean run(BluetoothGatt gatt) {
        if (!shouldAbort()) {
            return true;
        }
        LOG.info("Aborting transaction because abort criteria met.");
        return false;
    }

    public String toString() {
        return getCreationTime() + ": " + getClass().getSimpleName() + ": aborting? " + shouldAbort();
    }
}
