package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitgts;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitgts.AmazfitGTSFirmwareInfo;

public class AmazfitGTSFWHelper extends HuamiFWHelper {
    public AmazfitGTSFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new AmazfitGTSFirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a an Amazifit GTS firmware");
        }
    }
}
