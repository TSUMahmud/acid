package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.AbortTransactionAction;

public class CheckAuthenticationNeededAction extends AbortTransactionAction {
    private final GBDevice mDevice;

    public CheckAuthenticationNeededAction(GBDevice device) {
        this.mDevice = device;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.miband.CheckAuthenticationNeededAction$1 */
    static /* synthetic */ class C11951 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$impl$GBDevice$State */
        static final /* synthetic */ int[] f189xe98fe9ad = new int[GBDevice.State.values().length];

        static {
            try {
                f189xe98fe9ad[GBDevice.State.AUTHENTICATION_REQUIRED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f189xe98fe9ad[GBDevice.State.AUTHENTICATING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldAbort() {
        int i = C11951.f189xe98fe9ad[this.mDevice.getState().ordinal()];
        if (i == 1 || i == 2) {
            return true;
        }
        return false;
    }
}
