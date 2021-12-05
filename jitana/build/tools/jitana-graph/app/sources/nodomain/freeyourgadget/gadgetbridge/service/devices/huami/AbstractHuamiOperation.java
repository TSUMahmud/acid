package nodomain.freeyourgadget.gadgetbridge.service.devices.huami;

import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations.AbstractMiBandOperation;

public abstract class AbstractHuamiOperation extends AbstractMiBandOperation<HuamiSupport> {
    protected AbstractHuamiOperation(HuamiSupport support) {
        super(support);
    }

    /* access modifiers changed from: protected */
    public void enableOtherNotifications(TransactionBuilder builder, boolean enable) {
    }
}
