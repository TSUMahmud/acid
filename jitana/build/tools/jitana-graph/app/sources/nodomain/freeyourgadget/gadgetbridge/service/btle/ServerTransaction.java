package nodomain.freeyourgadget.gadgetbridge.service.btle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ServerTransaction extends AbstractTransaction {
    private GattServerCallback gattCallback;
    private final List<BtLEServerAction> mActions = new ArrayList(4);

    public ServerTransaction(String taskName) {
        super(taskName);
    }

    public void add(BtLEServerAction action) {
        this.mActions.add(action);
    }

    public List<BtLEServerAction> getActions() {
        return Collections.unmodifiableList(this.mActions);
    }

    public boolean isEmpty() {
        return this.mActions.isEmpty();
    }

    public String toString() {
        return String.format(Locale.US, "%s: Transaction task: %s with %d actions", new Object[]{getCreationTime(), getTaskName(), Integer.valueOf(this.mActions.size())});
    }

    public void setGattCallback(GattServerCallback callback) {
        this.gattCallback = callback;
    }

    public GattServerCallback getGattCallback() {
        return this.gattCallback;
    }

    public int getActionCount() {
        return this.mActions.size();
    }
}
