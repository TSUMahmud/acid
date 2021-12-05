package nodomain.freeyourgadget.gadgetbridge.devices.huami.miband4;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband4.MiBand4FirmwareInfo;

public class MiBand4FWHelper extends HuamiFWHelper {
    public MiBand4FWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new MiBand4FirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a Mi Band 4 firmware");
        }
    }
}
