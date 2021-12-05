package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipLiteFirmwareInfo;

public class AmazfitBipLiteFWHelper extends HuamiFWHelper {
    public AmazfitBipLiteFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new AmazfitBipLiteFirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a an Amazifit Bip Lite firmware");
        }
    }
}
