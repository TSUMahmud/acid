package nodomain.freeyourgadget.gadgetbridge.devices.huami;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWHelper;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;

public abstract class HuamiFWHelper extends AbstractMiBandFWHelper {
    protected HuamiFirmwareInfo firmwareInfo;

    public HuamiFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    public String format(int version) {
        return this.firmwareInfo.toVersion(version);
    }

    public String getFirmwareKind() {
        int resId = C0889R.string.kind_invalid;
        switch (getFirmwareInfo().getFirmwareType()) {
            case FONT:
            case FONT_LATIN:
                resId = C0889R.string.kind_font;
                break;
            case GPS:
                resId = C0889R.string.kind_gps;
                break;
            case GPS_ALMANAC:
                resId = C0889R.string.kind_gps_almanac;
                break;
            case GPS_CEP:
                resId = C0889R.string.kind_gps_cep;
                break;
            case RES:
            case RES_COMPRESSED:
                resId = C0889R.string.kind_resources;
                break;
            case FIRMWARE:
                resId = C0889R.string.kind_firmware;
                break;
            case WATCHFACE:
                resId = C0889R.string.kind_watchface;
                break;
        }
        return GBApplication.getContext().getString(resId);
    }

    public int getFirmwareVersion() {
        return this.firmwareInfo.getFirmwareVersion();
    }

    public int getFirmware2Version() {
        return 0;
    }

    public String getHumanFirmwareVersion2() {
        return "";
    }

    /* access modifiers changed from: protected */
    public int[] getWhitelistedFirmwareVersions() {
        return this.firmwareInfo.getWhitelistedVersions();
    }

    public boolean isFirmwareGenerallyCompatibleWith(GBDevice device) {
        return this.firmwareInfo.isGenerallyCompatibleWith(device);
    }

    public boolean isSingleFirmware() {
        return true;
    }

    public void checkValid() throws IllegalArgumentException {
        this.firmwareInfo.checkValid();
    }

    public HuamiFirmwareInfo getFirmwareInfo() {
        return this.firmwareInfo;
    }
}
