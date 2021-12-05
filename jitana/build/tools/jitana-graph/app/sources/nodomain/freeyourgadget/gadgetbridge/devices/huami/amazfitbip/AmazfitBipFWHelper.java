package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipFirmwareInfo;

public class AmazfitBipFWHelper extends HuamiFWHelper {
    public AmazfitBipFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new AmazfitBipFirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a an Amazifit Bip firmware");
        }
    }
}
