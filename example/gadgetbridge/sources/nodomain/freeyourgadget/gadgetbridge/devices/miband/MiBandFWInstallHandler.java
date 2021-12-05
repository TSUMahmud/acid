package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiBandFWInstallHandler extends AbstractMiBandFWInstallHandler {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBandFWInstallHandler.class);

    public MiBandFWInstallHandler(Uri uri, Context context) {
        super(uri, context);
    }

    /* access modifiers changed from: protected */
    public AbstractMiBandFWHelper createHelper(Uri uri, Context context) throws IOException {
        return new MiBandFWHelper(uri, context);
    }

    /* access modifiers changed from: protected */
    public boolean isSupportedDeviceType(GBDevice device) {
        return device.getType() == DeviceType.MIBAND;
    }
}
