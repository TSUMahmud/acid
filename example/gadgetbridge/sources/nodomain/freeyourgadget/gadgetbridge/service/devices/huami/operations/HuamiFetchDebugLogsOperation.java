package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuamiFetchDebugLogsOperation extends AbstractFetchOperation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HuamiFetchDebugLogsOperation.class);
    private FileOutputStream logOutputStream;

    public HuamiFetchDebugLogsOperation(AmazfitBipSupport support) {
        super(support);
        setName("fetch debug logs");
    }

    /* access modifiers changed from: protected */
    public void startFetching(TransactionBuilder builder) {
        try {
            File dir = FileUtils.getExternalFilesDir();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
            File outputFile = new File(dir, "huamidebug_" + dateFormat.format(new Date()) + ".log");
            try {
                this.logOutputStream = new FileOutputStream(outputFile);
                GregorianCalendar sinceWhen = BLETypeConversions.createCalendar();
                sinceWhen.add(5, -10);
                startFetching(builder, (byte) 7, sinceWhen);
            } catch (IOException e) {
                Logger logger = LOG;
                logger.warn("could not create file " + outputFile, (Throwable) e);
            }
        } catch (IOException e2) {
        }
    }

    /* access modifiers changed from: protected */
    public String getLastSyncTimeKey() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void handleActivityFetchFinish(boolean success) {
        Logger logger = LOG;
        logger.info(getName() + " data has finished");
        try {
            this.logOutputStream.close();
            this.logOutputStream = null;
            super.handleActivityFetchFinish(success);
        } catch (IOException e) {
            LOG.warn("could not close output stream", (Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void handleActivityNotif(byte[] value) {
        if (!isOperationRunning()) {
            Logger logger = LOG;
            logger.error("ignoring notification because operation is not running. Data length: " + value.length);
            ((HuamiSupport) getSupport()).logMessageContent(value);
        } else if (((byte) (this.lastPacketCounter + 1)) == value[0]) {
            this.lastPacketCounter = (byte) (this.lastPacketCounter + 1);
            bufferActivityData(value);
        } else {
            C1238GB.toast("Error " + getName() + " invalid package counter: " + value[0], 1, 3);
            handleActivityFetchFinish(false);
        }
    }

    /* access modifiers changed from: protected */
    public void bufferActivityData(byte[] value) {
        try {
            this.logOutputStream.write(value, 1, value.length - 1);
        } catch (IOException e) {
            LOG.warn("could not write to output stream", (Throwable) e);
            handleActivityFetchFinish(false);
        }
    }
}
