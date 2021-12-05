package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.InstallActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMiBandFWInstallHandler implements InstallHandler {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractMiBandFWInstallHandler.class);
    private String errorMessage;
    protected AbstractMiBandFWHelper helper;
    protected final Context mContext;

    /* access modifiers changed from: protected */
    public abstract AbstractMiBandFWHelper createHelper(Uri uri, Context context) throws IOException;

    /* access modifiers changed from: protected */
    public abstract boolean isSupportedDeviceType(GBDevice gBDevice);

    public AbstractMiBandFWInstallHandler(Uri uri, Context context) {
        this.mContext = context;
        try {
            this.helper = createHelper(uri, context);
        } catch (IOException e) {
            this.errorMessage = e.getMessage();
            LOG.warn(this.errorMessage, (Throwable) e);
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public AbstractMiBandFWHelper getHelper() {
        return this.helper;
    }

    /* access modifiers changed from: protected */
    public GenericItem createInstallItem(GBDevice device) {
        Context context = this.mContext;
        return new GenericItem(context.getString(C0889R.string.installhandler_firmware_name, new Object[]{context.getString(device.getType().getName()), this.helper.getFirmwareKind(), this.helper.getHumanFirmwareVersion()}));
    }

    /* access modifiers changed from: protected */
    public String getFwUpgradeNotice() {
        return this.mContext.getString(C0889R.string.fw_upgrade_notice, new Object[]{this.helper.getHumanFirmwareVersion()});
    }

    public void validateInstallation(InstallActivity installActivity, GBDevice device) {
        if (device.isBusy()) {
            installActivity.setInfoText(device.getBusyTask());
            installActivity.setInstallEnabled(false);
        } else if (!isSupportedDeviceType(device) || !device.isInitialized()) {
            installActivity.setInfoText(this.mContext.getString(C0889R.string.fwapp_install_device_not_ready));
            installActivity.setInstallEnabled(false);
        } else {
            try {
                this.helper.checkValid();
                GenericItem fwItem = createInstallItem(device);
                fwItem.setIcon(device.getType().getIcon());
                if (!this.helper.isFirmwareGenerallyCompatibleWith(device)) {
                    fwItem.setDetails(this.mContext.getString(C0889R.string.miband_fwinstaller_incompatible_version));
                    installActivity.setInfoText(this.mContext.getString(C0889R.string.fwinstaller_firmware_not_compatible_to_device));
                    installActivity.setInstallEnabled(false);
                    return;
                }
                StringBuilder builder = new StringBuilder();
                if (this.helper.isSingleFirmware()) {
                    getFwUpgradeNotice();
                    builder.append(getFwUpgradeNotice());
                } else {
                    builder.append(this.mContext.getString(C0889R.string.fw_multi_upgrade_notice, new Object[]{this.helper.getHumanFirmwareVersion(), this.helper.getHumanFirmwareVersion2()}));
                }
                if (this.helper.isFirmwareWhitelisted()) {
                    builder.append(StringUtils.SPACE);
                    builder.append(this.mContext.getString(C0889R.string.miband_firmware_known));
                    fwItem.setDetails(this.mContext.getString(C0889R.string.miband_fwinstaller_compatible_version));
                } else {
                    builder.append("  ");
                    builder.append(this.mContext.getString(C0889R.string.miband_firmware_unknown_warning));
                    builder.append(" \n\n");
                    builder.append(this.mContext.getString(C0889R.string.miband_firmware_suggest_whitelist, new Object[]{String.valueOf(this.helper.getFirmwareVersion())}));
                    fwItem.setDetails(this.mContext.getString(C0889R.string.miband_fwinstaller_untested_version));
                }
                installActivity.setInfoText(builder.toString());
                installActivity.setInstallItem(fwItem);
                installActivity.setInstallEnabled(true);
            } catch (IllegalArgumentException ex) {
                installActivity.setInfoText(ex.getLocalizedMessage());
                installActivity.setInstallEnabled(false);
            }
        }
    }

    public void onStartInstall(GBDevice device) {
    }

    public boolean isValid() {
        return this.helper != null;
    }
}
