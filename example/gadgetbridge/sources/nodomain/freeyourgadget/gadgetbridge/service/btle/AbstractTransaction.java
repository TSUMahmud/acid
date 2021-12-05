package nodomain.freeyourgadget.gadgetbridge.service.btle;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class AbstractTransaction {
    private final long creationTimestamp = System.currentTimeMillis();
    private final String mName;

    public abstract int getActionCount();

    public AbstractTransaction(String taskName) {
        this.mName = taskName;
    }

    public String getTaskName() {
        return this.mName;
    }

    /* access modifiers changed from: protected */
    public String getCreationTime() {
        return DateFormat.getTimeInstance(2).format(new Date(this.creationTimestamp));
    }

    public String toString() {
        return String.format(Locale.US, "%s: Transaction task: %s with %d actions", new Object[]{getCreationTime(), getTaskName(), Integer.valueOf(getActionCount())});
    }
}
