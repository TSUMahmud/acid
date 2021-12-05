package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor2;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor2.AmazfitCor2FirmwareInfo;

public class AmazfitCor2FWHelper extends HuamiFWHelper {
    public AmazfitCor2FWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new AmazfitCor2FirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a an Amazfit Cor 2 firmware");
        }
    }
}
