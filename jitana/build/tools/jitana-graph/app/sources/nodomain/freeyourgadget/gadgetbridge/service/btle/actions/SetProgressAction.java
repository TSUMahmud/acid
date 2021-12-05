package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetProgressAction extends PlainAction {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SetProgressAction.class);
    private final Context context;
    private final boolean ongoing;
    private final int percentage;
    private final String text;

    public SetProgressAction(String text2, boolean ongoing2, int percentage2, Context context2) {
        this.text = text2;
        this.ongoing = ongoing2;
        this.percentage = percentage2;
        this.context = context2;
    }

    public boolean run(BluetoothGatt gatt) {
        LOG.info(toString());
        C1238GB.updateInstallNotification(this.text, this.ongoing, this.percentage, this.context);
        return true;
    }

    public String toString() {
        return getCreationTime() + ": " + getClass().getSimpleName() + ": " + this.text + "; " + this.percentage + "%";
    }
}
