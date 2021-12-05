package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.AbstractMiFirmwareInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiBandFWHelper extends AbstractMiBandFWHelper {
    public static final int FW_16779790 = 16779790;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBandFWHelper.class);
    private AbstractMiFirmwareInfo firmwareInfo;
    private final int[] whitelistedFirmwareVersion = {16779534, 16779547, 16779568, 16779585, 16779779, 16779782, 16779787, 68094986, 68158215, 68158486, 68160271, 84870926};

    public MiBandFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    public String getFirmwareKind() {
        return GBApplication.getContext().getString(C0889R.string.kind_firmware);
    }

    public int getFirmwareVersion() {
        return this.firmwareInfo.getFirst().getFirmwareVersion();
    }

    public int getFirmware2Version() {
        return this.firmwareInfo.getFirst().getFirmwareVersion();
    }

    public String getHumanFirmwareVersion2() {
        return format(this.firmwareInfo.getSecond().getFirmwareVersion());
    }

    /* access modifiers changed from: protected */
    public int[] getWhitelistedFirmwareVersions() {
        return this.whitelistedFirmwareVersion;
    }

    public boolean isFirmwareGenerallyCompatibleWith(GBDevice device) {
        return this.firmwareInfo.isGenerallyCompatibleWith(device);
    }

    public boolean isSingleFirmware() {
        return this.firmwareInfo.isSingleMiBandFirmware();
    }

    /* access modifiers changed from: protected */
    public void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        this.firmwareInfo = AbstractMiFirmwareInfo.determineFirmwareInfoFor(wholeFirmwareBytes);
    }

    public void checkValid() throws IllegalArgumentException {
        this.firmwareInfo.checkValid();
    }

    public static AbstractMiFirmwareInfo determineFirmwareInfoFor(byte[] wholeFirmwareBytes) {
        return AbstractMiFirmwareInfo.determineFirmwareInfoFor(wholeFirmwareBytes);
    }

    public AbstractMiFirmwareInfo getFirmwareInfo() {
        return this.firmwareInfo;
    }
}
