package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitgtr;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWInstallHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

class AmazfitGTRFWInstallHandler extends AbstractMiBandFWInstallHandler {
    AmazfitGTRFWInstallHandler(Uri uri, Context context) {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public String getFwUpgradeNotice() {
        return this.mContext.getString(C0889R.string.fw_upgrade_notice_amazfitgtr, new Object[]{this.helper.getHumanFirmwareVersion()});
    }

    /* access modifiers changed from: protected */
    public AbstractMiBandFWHelper createHelper(Uri uri, Context context) throws IOException {
        return new AmazfitGTRFWHelper(uri, context);
    }

    /* access modifiers changed from: protected */
    public boolean isSupportedDeviceType(GBDevice device) {
        return device.getType() == DeviceType.AMAZFITGTR;
    }
}
