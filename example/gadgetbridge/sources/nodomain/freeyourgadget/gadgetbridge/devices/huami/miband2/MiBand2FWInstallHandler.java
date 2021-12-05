package nodomain.freeyourgadget.gadgetbridge.devices.huami.miband2;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.InstallActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWInstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiBand2FWInstallHandler extends AbstractMiBandFWInstallHandler {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBand2FWInstallHandler.class);

    MiBand2FWInstallHandler(Uri uri, Context context) {
        super(uri, context);
    }

    public void validateInstallation(InstallActivity installActivity, GBDevice device) {
        super.validateInstallation(installActivity, device);
        maybeAddFw53Hint(installActivity, device);
        maybeAddFontHint(installActivity);
    }

    private void maybeAddFontHint(InstallActivity installActivity) {
        if (getFirmwareType() == HuamiFirmwareType.FIRMWARE) {
            installActivity.setInfoText(installActivity.getInfoText() + "\n\nNote: you may install Mili_pro.ft or Mili_pro.ft.en to enable text notifications.");
        }
    }

    private void maybeAddFw53Hint(InstallActivity installActivity, GBDevice device) {
        Version deviceVersion;
        if (getFirmwareType() == HuamiFirmwareType.FIRMWARE && (deviceVersion = getFirmwareVersionOf(device)) != null) {
            Version v53 = MiBandConst.MI2_FW_VERSION_INTERMEDIATE_UPGRADE_53;
            if (deviceVersion.compareTo(v53) < 0) {
                String vInstall = getHelper().format(getHelper().getFirmwareVersion());
                if (vInstall == null || new Version(vInstall).compareTo(v53) > 0) {
                    installActivity.setInfoText(getContext().getString(C0889R.string.mi2_fw_installhandler_fw53_hint, new Object[]{v53.get()}) + "\n\n" + installActivity.getInfoText());
                }
            }
        }
    }

    private Version getFirmwareVersionOf(GBDevice device) {
        String version = device.getFirmwareVersion();
        if (version == null || version.length() == 0) {
            return null;
        }
        if (version.charAt(0) == 'V') {
            version = version.substring(1);
        }
        try {
            return new Version(version);
        } catch (Exception e) {
            Logger logger = LOG;
            logger.error("Unable to parse version: " + version);
            return null;
        }
    }

    private HuamiFirmwareType getFirmwareType() {
        AbstractMiBandFWHelper helper = getHelper();
        if (helper instanceof MiBand2FWHelper) {
            return ((MiBand2FWHelper) helper).getFirmwareInfo().getFirmwareType();
        }
        return HuamiFirmwareType.INVALID;
    }

    /* access modifiers changed from: protected */
    public AbstractMiBandFWHelper createHelper(Uri uri, Context context) throws IOException {
        return new MiBand2FWHelper(uri, context);
    }

    /* access modifiers changed from: protected */
    public boolean isSupportedDeviceType(GBDevice device) {
        return device.getType() == DeviceType.MIBAND2;
    }
}
