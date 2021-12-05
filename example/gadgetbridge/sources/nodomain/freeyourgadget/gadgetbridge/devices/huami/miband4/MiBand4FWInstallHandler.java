package nodomain.freeyourgadget.gadgetbridge.devices.huami.miband4;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWInstallHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

class MiBand4FWInstallHandler extends AbstractMiBandFWInstallHandler {
    MiBand4FWInstallHandler(Uri uri, Context context) {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public String getFwUpgradeNotice() {
        return this.mContext.getString(C0889R.string.fw_upgrade_notice_miband4, new Object[]{this.helper.getHumanFirmwareVersion()});
    }

    /* access modifiers changed from: protected */
    public AbstractMiBandFWHelper createHelper(Uri uri, Context context) throws IOException {
        return new MiBand4FWHelper(uri, context);
    }

    /* access modifiers changed from: protected */
    public boolean isSupportedDeviceType(GBDevice device) {
        return device.getType() == DeviceType.MIBAND4;
    }
}
