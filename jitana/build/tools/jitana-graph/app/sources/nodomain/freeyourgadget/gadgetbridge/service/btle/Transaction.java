package nodomain.freeyourgadget.gadgetbridge.service.btle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transaction extends AbstractTransaction {
    private GattCallback gattCallback;
    private final List<BtLEAction> mActions = new ArrayList(4);

    public Transaction(String taskName) {
        super(taskName);
    }

    public void add(BtLEAction action) {
        this.mActions.add(action);
    }

    public List<BtLEAction> getActions() {
        return Collections.unmodifiableList(this.mActions);
    }

    public boolean isEmpty() {
        return this.mActions.isEmpty();
    }

    public void setGattCallback(GattCallback callback) {
        this.gattCallback = callback;
    }

    public GattCallback getGattCallback() {
        return this.gattCallback;
    }

    public int getActionCount() {
        return this.mActions.size();
    }
}
