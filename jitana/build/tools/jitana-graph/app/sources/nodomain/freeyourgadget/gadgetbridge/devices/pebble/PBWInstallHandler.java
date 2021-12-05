package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import android.content.Context;
import android.net.Uri;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.InstallActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PBWInstallHandler implements InstallHandler {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PBWInstallHandler.class);
    private final Context mContext;
    private PBWReader mPBWReader;
    private final Uri mUri;

    public PBWInstallHandler(Uri uri, Context context) {
        this.mContext = context;
        this.mUri = uri;
    }

    public void validateInstallation(InstallActivity installActivity, GBDevice device) {
        int drawable;
        if (device.isBusy()) {
            installActivity.setInfoText(device.getBusyTask());
            installActivity.setInstallEnabled(false);
        } else if (device.getType() != DeviceType.PEBBLE || !device.isConnected()) {
            installActivity.setInfoText("Element cannot be installed");
            installActivity.setInstallEnabled(false);
        } else {
            try {
                this.mPBWReader = new PBWReader(this.mUri, this.mContext, PebbleUtils.getPlatformName(device.getModel()));
                if (!this.mPBWReader.isValid()) {
                    installActivity.setInfoText("pbw/pbz is broken or incompatible with your Hardware or Firmware.");
                    installActivity.setInstallEnabled(false);
                    return;
                }
                GenericItem installItem = new GenericItem();
                installItem.setIcon(C0889R.C0890drawable.ic_watchapp);
                if (this.mPBWReader.isFirmware()) {
                    installItem.setIcon(C0889R.C0890drawable.ic_firmware);
                    String hwRevision = this.mPBWReader.getHWRevision();
                    if (hwRevision == null || !hwRevision.equals(device.getModel())) {
                        if (hwRevision != null) {
                            installItem.setName(this.mContext.getString(C0889R.string.pbw_installhandler_pebble_firmware, new Object[]{hwRevision}));
                            installItem.setDetails(this.mContext.getString(C0889R.string.pbwinstallhandler_incorrect_hw_revision));
                        }
                        installActivity.setInfoText(this.mContext.getString(C0889R.string.pbw_install_handler_hw_revision_mismatch));
                        installActivity.setInstallEnabled(false);
                    } else {
                        installItem.setName(this.mContext.getString(C0889R.string.pbw_installhandler_pebble_firmware, new Object[]{""}));
                        installItem.setDetails(this.mContext.getString(C0889R.string.pbwinstallhandler_correct_hw_revision));
                        installActivity.setInfoText(this.mContext.getString(C0889R.string.firmware_install_warning, new Object[]{hwRevision}));
                        installActivity.setInstallEnabled(true);
                    }
                } else {
                    GBDeviceApp app = this.mPBWReader.getGBDeviceApp();
                    if (app != null) {
                        installItem.setName(app.getName());
                        installItem.setDetails(this.mContext.getString(C0889R.string.pbwinstallhandler_app_item, new Object[]{app.getCreator(), app.getVersion()}));
                        if (this.mPBWReader.isLanguage()) {
                            drawable = C0889R.C0890drawable.ic_languagepack;
                        } else {
                            int i = C11111.f131x7d95cefd[app.getType().ordinal()];
                            if (i == 1) {
                                drawable = C0889R.C0890drawable.ic_watchface;
                            } else if (i != 2) {
                                drawable = C0889R.C0890drawable.ic_watchapp;
                            } else {
                                drawable = C0889R.C0890drawable.ic_activitytracker;
                            }
                        }
                        installItem.setIcon(drawable);
                        installActivity.setInfoText(this.mContext.getString(C0889R.string.app_install_info, new Object[]{app.getName(), app.getVersion(), app.getCreator()}));
                        installActivity.setInstallEnabled(true);
                    } else {
                        installActivity.setInfoText(this.mContext.getString(C0889R.string.pbw_install_handler_unable_to_install, new Object[]{this.mUri.getPath()}));
                        installActivity.setInstallEnabled(false);
                    }
                }
                if (installItem.getName() != null) {
                    installActivity.setInstallItem(installItem);
                }
            } catch (FileNotFoundException e) {
                installActivity.setInfoText("file not found");
                installActivity.setInstallEnabled(false);
            } catch (IOException e2) {
                installActivity.setInfoText("error reading file");
                installActivity.setInstallEnabled(false);
            }
        }
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWInstallHandler$1 */
    static /* synthetic */ class C11111 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$impl$GBDeviceApp$Type */
        static final /* synthetic */ int[] f131x7d95cefd = new int[GBDeviceApp.Type.values().length];

        static {
            try {
                f131x7d95cefd[GBDeviceApp.Type.WATCHFACE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f131x7d95cefd[GBDeviceApp.Type.APP_ACTIVITYTRACKER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public void onStartInstall(GBDevice device) {
        if (!this.mPBWReader.isFirmware() && !this.mPBWReader.isLanguage()) {
            GBDeviceApp app = this.mPBWReader.getGBDeviceApp();
            try {
                File destDir = PebbleUtils.getPbwCacheDir();
                destDir.mkdirs();
                Context context = this.mContext;
                Uri uri = this.mUri;
                FileUtils.copyURItoFile(context, uri, new File(destDir, app.getUUID().toString() + ".pbw"));
                AppManagerActivity.addToAppOrderFile("pbwcacheorder.txt", app.getUUID());
                try {
                    Writer writer = new BufferedWriter(new FileWriter(new File(destDir, app.getUUID().toString() + ".json")));
                    try {
                        LOG.info(app.getJSON().toString());
                        JSONObject appJSON = app.getJSON();
                        JSONObject appKeysJSON = this.mPBWReader.getAppKeysJSON();
                        if (appKeysJSON != null) {
                            appJSON.put("appKeys", appKeysJSON);
                        }
                        writer.write(appJSON.toString());
                        writer.close();
                    } catch (IOException e) {
                        Logger logger = LOG;
                        logger.error("Failed to write to output file: " + e.getMessage(), (Throwable) e);
                    } catch (JSONException e2) {
                        LOG.error(e2.getMessage(), (Throwable) e2);
                    }
                    InputStream jsConfigFile = this.mPBWReader.getInputStreamFile("pebble-js-app.js");
                    if (jsConfigFile != null) {
                        try {
                            FileUtils.copyStreamToFile(jsConfigFile, new File(destDir, app.getUUID().toString() + "_config.js"));
                            try {
                                jsConfigFile.close();
                            } catch (IOException e3) {
                            }
                        } catch (IOException e4) {
                            Logger logger2 = LOG;
                            logger2.error("Failed to open output file: " + e4.getMessage(), (Throwable) e4);
                            jsConfigFile.close();
                        } catch (Throwable th) {
                            try {
                                jsConfigFile.close();
                            } catch (IOException e5) {
                            }
                            throw th;
                        }
                    }
                } catch (IOException e6) {
                    Logger logger3 = LOG;
                    logger3.error("Failed to open output file: " + e6.getMessage(), (Throwable) e6);
                }
            } catch (IOException e7) {
                Logger logger4 = LOG;
                logger4.error("Installation failed: " + e7.getMessage(), (Throwable) e7);
            }
        }
    }

    public boolean isValid() {
        return true;
    }
}
