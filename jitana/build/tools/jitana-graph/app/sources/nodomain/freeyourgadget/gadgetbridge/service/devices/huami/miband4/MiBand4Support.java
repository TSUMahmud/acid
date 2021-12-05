package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband4;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband4.MiBand4FWHelper;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3.MiBand3Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.UpdateFirmwareOperationNew;

public class MiBand4Support extends MiBand3Support {
    public byte getCryptFlags() {
        return Byte.MIN_VALUE;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        super.sendNotificationNew(notificationSpec, true);
    }

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new MiBand4FWHelper(uri, context);
    }

    public UpdateFirmwareOperationNew createUpdateFirmwareOperation(Uri uri) {
        return new UpdateFirmwareOperationNew(uri, this);
    }
}
