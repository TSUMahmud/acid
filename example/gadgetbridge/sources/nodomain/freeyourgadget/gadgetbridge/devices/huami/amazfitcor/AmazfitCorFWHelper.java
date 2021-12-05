package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor.AmazfitCorFirmwareInfo;

public class AmazfitCorFWHelper extends HuamiFWHelper {
    public AmazfitCorFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new AmazfitCorFirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a an Amazfit Cor firmware");
        }
    }
}
