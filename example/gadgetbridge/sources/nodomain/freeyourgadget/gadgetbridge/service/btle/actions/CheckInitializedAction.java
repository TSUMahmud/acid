package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckInitializedAction extends AbortTransactionAction {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CheckInitializedAction.class);
    private final GBDevice device;

    public CheckInitializedAction(GBDevice gbDevice) {
        this.device = gbDevice;
    }

    /* access modifiers changed from: protected */
    public boolean shouldAbort() {
        boolean abort = this.device.isInitialized();
        if (abort) {
            Logger logger = LOG;
            logger.info("Aborting device initialization, because already initialized: " + this.device);
        }
        return abort;
    }
}
