package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip;

import android.content.Context;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchActivityOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.FetchSportsSummaryOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.HuamiFetchDebugLogsOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazfitBipSupport extends HuamiSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AmazfitBipSupport.class);

    public AmazfitBipSupport() {
        super(LOG);
    }

    public NotificationStrategy getNotificationStrategy() {
        return new AmazfitBipTextNotificationStrategy(this);
    }

    public void onNotification(NotificationSpec notificationSpec) {
        super.sendNotificationNew(notificationSpec, false);
    }

    public void onFindDevice(boolean start) {
        CallSpec callSpec = new CallSpec();
        callSpec.command = start ? 2 : 6;
        callSpec.name = GBApplication.DATABASE_NAME;
        onSetCallState(callSpec);
    }

    /* access modifiers changed from: protected */
    public AmazfitBipSupport setDisplayItems(TransactionBuilder builder) {
        if (this.gbDevice.getFirmwareVersion() == null) {
            LOG.warn("Device not initialized yet, won't set menu items");
            return this;
        }
        Set<String> pages = HuamiCoordinator.getDisplayItems(this.gbDevice.getAddress());
        Logger logger = LOG;
        StringBuilder sb = new StringBuilder();
        sb.append("Setting display items to ");
        sb.append(pages == null ? "none" : pages);
        logger.info(sb.toString());
        byte[] command = (byte[]) AmazfitBipService.COMMAND_CHANGE_SCREENS.clone();
        boolean shortcut_weather = false;
        boolean shortcut_alipay = false;
        if (pages != null) {
            if (pages.contains(NotificationCompat.CATEGORY_STATUS)) {
                command[1] = (byte) (command[1] | 2);
            }
            if (pages.contains("activity")) {
                command[1] = (byte) (command[1] | 4);
            }
            if (pages.contains(DeviceService.EXTRA_WEATHER)) {
                command[1] = (byte) (command[1] | 8);
            }
            if (pages.contains("alarm")) {
                command[1] = (byte) (command[1] | 16);
            }
            if (pages.contains("timer")) {
                command[1] = (byte) (command[1] | 32);
            }
            if (pages.contains("compass")) {
                command[1] = (byte) (command[1] | 64);
            }
            if (pages.contains("settings")) {
                command[1] = (byte) (command[1] | 128);
            }
            if (pages.contains("alipay")) {
                command[2] = (byte) (command[2] | 1);
            }
            if (pages.contains("shortcut_weather")) {
                shortcut_weather = true;
            }
            if (pages.contains("shortcut_alipay")) {
                shortcut_alipay = true;
            }
        }
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), command);
        setShortcuts(builder, shortcut_weather, shortcut_alipay);
        return this;
    }

    private void setShortcuts(TransactionBuilder builder, boolean weather, boolean alipay) {
        Logger logger = LOG;
        logger.info("Setting shortcuts: weather=" + weather + " alipay=" + alipay);
        byte[] command = new byte[5];
        int i = 0;
        command[0] = 16;
        if (alipay || weather) {
            i = 128;
        }
        command[1] = (byte) i;
        command[2] = (byte) (weather ? 2 : 1);
        command[3] = (byte) ((!alipay || !weather) ? 1 : 129);
        command[4] = 1;
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), command);
    }

    public void onFetchRecordedData(int dataTypes) {
        try {
            if (dataTypes == RecordedDataTypes.TYPE_ACTIVITY) {
                new FetchActivityOperation(this).perform();
            } else if (dataTypes == RecordedDataTypes.TYPE_GPS_TRACKS) {
                new FetchSportsSummaryOperation(this).perform();
            } else if (dataTypes == RecordedDataTypes.TYPE_DEBUGLOGS) {
                new HuamiFetchDebugLogsOperation(this).perform();
            } else {
                LOG.warn("fetching multiple data types at once is not supported yet");
            }
        } catch (IOException ex) {
            Logger logger = LOG;
            logger.error("Unable to fetch recorded data types" + dataTypes, (Throwable) ex);
        }
    }

    public void phase2Initialize(TransactionBuilder builder) {
        super.phase2Initialize(builder);
        LOG.info("phase2Initialize...");
        setLanguage(builder);
        requestGPSVersion(builder);
    }

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitBipFWHelper(uri, context);
    }
}
