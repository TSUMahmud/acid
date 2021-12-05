package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor2;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor2.AmazfitCor2FWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor.AmazfitCorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazfitCor2Support extends AmazfitCorSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AmazfitCor2Support.class);

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitCor2FWHelper(uri, context);
    }
}
