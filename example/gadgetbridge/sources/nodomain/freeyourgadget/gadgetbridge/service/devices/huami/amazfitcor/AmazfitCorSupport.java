package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor;

import android.content.Context;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor.AmazfitCorFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor.AmazfitCorService;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazfitCorSupport extends AmazfitBipSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AmazfitCorSupport.class);

    /* access modifiers changed from: protected */
    public AmazfitCorSupport setDisplayItems(TransactionBuilder builder) {
        Set<String> pages = HuamiCoordinator.getDisplayItems(getDevice().getAddress());
        Logger logger = LOG;
        StringBuilder sb = new StringBuilder();
        sb.append("Setting display items to ");
        sb.append(pages == null ? "none" : pages);
        logger.info(sb.toString());
        byte[] command = (byte[]) AmazfitCorService.COMMAND_CHANGE_SCREENS.clone();
        if (pages != null) {
            if (pages.contains(NotificationCompat.CATEGORY_STATUS)) {
                command[1] = (byte) (command[1] | 2);
            }
            if (pages.contains(PackageConfigHelper.DB_TABLE)) {
                command[1] = (byte) (command[1] | 4);
            }
            if (pages.contains("activity")) {
                command[1] = (byte) (command[1] | 8);
            }
            if (pages.contains(DeviceService.EXTRA_WEATHER)) {
                command[1] = (byte) (command[1] | 16);
            }
            if (pages.contains("alarm")) {
                command[1] = (byte) (command[1] | 32);
            }
            if (pages.contains("timer")) {
                command[1] = (byte) (command[1] | 64);
            }
            if (pages.contains("settings")) {
                command[1] = (byte) (command[1] | 128);
            }
            if (pages.contains("alipay")) {
                command[2] = (byte) (command[2] | 1);
            }
            if (pages.contains("music")) {
                command[2] = (byte) (command[2] | 2);
            }
        }
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), command);
        return this;
    }

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitCorFWHelper(uri, context);
    }
}
