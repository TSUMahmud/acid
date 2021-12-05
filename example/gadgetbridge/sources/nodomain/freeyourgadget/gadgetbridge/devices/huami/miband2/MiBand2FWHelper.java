package nodomain.freeyourgadget.gadgetbridge.devices.huami.miband2;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband2.Mi2FirmwareInfo;

public class MiBand2FWHelper extends HuamiFWHelper {
    public MiBand2FWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = new Mi2FirmwareInfo(wholeFirmwareBytes);
        if (!this.firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a Mi Band 2 firmware");
        }
    }
}
