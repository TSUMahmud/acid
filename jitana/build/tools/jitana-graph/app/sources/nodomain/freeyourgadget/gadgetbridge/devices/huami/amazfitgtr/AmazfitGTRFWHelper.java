package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitgtr;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitgtr.AmazfitGTRFirmwareInfo;

public class AmazfitGTRFWHelper extends HuamiFWHelper {
    public AmazfitGTRFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new AmazfitGTRFirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a an Amazifit GTR firmware");
        }
    }
}
