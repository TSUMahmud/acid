package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitgtr;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitgtr.AmazfitGTRFWHelper;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipSupport;

public class AmazfitGTRSupport extends AmazfitBipSupport {
    public byte getCryptFlags() {
        return Byte.MIN_VALUE;
    }

    /* access modifiers changed from: protected */
    public byte getAuthFlags() {
        return 0;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        super.sendNotificationNew(notificationSpec, true);
    }

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitGTRFWHelper(uri, context);
    }

    /* access modifiers changed from: protected */
    public AmazfitGTRSupport setDisplayItems(TransactionBuilder builder) {
        return this;
    }
}
