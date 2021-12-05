package nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3.MiBand3FirmwareInfo;

public class MiBand3FWHelper extends HuamiFWHelper {
    public MiBand3FWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new MiBand3FirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a Mi Band 3 firmware");
        }
    }
}
