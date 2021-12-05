package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipLiteFWHelper;

public class AmazfitBipLiteSupport extends AmazfitBipSupport {
    public byte getCryptFlags() {
        return Byte.MIN_VALUE;
    }

    /* access modifiers changed from: protected */
    public byte getAuthFlags() {
        return 0;
    }

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitBipLiteFWHelper(uri, context);
    }
}
