package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceBusyAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateFirmwareOperationNew extends UpdateFirmwareOperation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) UpdateFirmwareOperationNew.class);

    public UpdateFirmwareOperationNew(Uri uri, HuamiSupport support) {
        super(uri, support);
    }

    public boolean sendFwInfo() {
        try {
            TransactionBuilder builder = performInitialized("send firmware info");
            builder.add(new SetDeviceBusyAction(getDevice(), getContext().getString(C0889R.string.updating_firmware), getContext()));
            byte[] sizeBytes = BLETypeConversions.fromUint24(getFirmwareInfo().getSize());
            byte[] bytes = new byte[10];
            int i = 0 + 1;
            bytes[0] = 1;
            int i2 = i + 1;
            bytes[i] = getFirmwareInfo().getFirmwareType().getValue();
            int i3 = i2 + 1;
            bytes[i2] = sizeBytes[0];
            int i4 = i3 + 1;
            bytes[i3] = sizeBytes[1];
            int i5 = i4 + 1;
            bytes[i4] = sizeBytes[2];
            int i6 = i5 + 1;
            bytes[i5] = 0;
            byte[] crcBytes = BLETypeConversions.fromUint32(this.firmwareInfo.getCrc32());
            int i7 = i6 + 1;
            bytes[i6] = crcBytes[0];
            int i8 = i7 + 1;
            bytes[i7] = crcBytes[1];
            bytes[i8] = crcBytes[2];
            bytes[i8 + 1] = crcBytes[3];
            builder.write(this.fwCControlChar, bytes);
            builder.queue(getQueue());
            return true;
        } catch (IOException e) {
            Logger logger = LOG;
            logger.error("Error sending firmware info: " + e.getLocalizedMessage(), (Throwable) e);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void sendChecksum(HuamiFirmwareInfo firmwareInfo) throws IOException {
        TransactionBuilder builder = performInitialized("send firmware upload finished");
        builder.write(this.fwCControlChar, new byte[]{4});
        builder.queue(getQueue());
    }

    /* access modifiers changed from: protected */
    public byte[] getFirmwareStartCommand() {
        return new byte[]{3, 1};
    }
}
