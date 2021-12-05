package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview;

import android.webkit.JavascriptInterface;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;
import org.apache.commons.lang3.CharEncoding;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSInterface {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) JSInterface.class);
    private GBDevice device;
    private Integer lastTransaction = 0;
    private UUID mUuid;

    public JSInterface(GBDevice device2, UUID mUuid2) {
        Logger logger = LOG;
        logger.debug("Creating JS interface for UUID: " + mUuid2.toString());
        this.device = device2;
        this.mUuid = mUuid2;
    }

    private boolean isLocationEnabledForWatchApp() {
        return true;
    }

    @JavascriptInterface
    public void gbLog(String msg) {
        Logger logger = LOG;
        logger.debug("WEBVIEW webpage log: " + msg);
    }

    @JavascriptInterface
    public String sendAppMessage(String msg, String needsTransactionMsg) {
        boolean needsTransaction = "true".equals(needsTransactionMsg);
        Logger logger = LOG;
        logger.debug("from WEBVIEW: " + msg + " needs a transaction: " + needsTransaction);
        JSONObject knownKeys = PebbleUtils.getAppConfigurationKeys(this.mUuid);
        if (knownKeys == null) {
            Logger logger2 = LOG;
            logger2.warn("No app configuration keys for: " + this.mUuid);
            return null;
        }
        try {
            JSONObject in = new JSONObject(msg);
            JSONObject out = new JSONObject();
            Iterator<String> key = in.keys();
            while (key.hasNext()) {
                boolean passKey = false;
                String inKey = key.next();
                String outKey = null;
                int pebbleAppIndex = knownKeys.optInt(inKey, -1);
                if (pebbleAppIndex != -1) {
                    passKey = true;
                    outKey = String.valueOf(pebbleAppIndex);
                } else {
                    Scanner scanner = new Scanner(inKey);
                    if (scanner.hasNextInt()) {
                        if (inKey.equals("" + scanner.nextInt())) {
                            passKey = true;
                            outKey = inKey;
                        }
                    }
                }
                if (passKey) {
                    out.put(outKey, in.get(inKey));
                } else {
                    C1238GB.toast("Discarded key " + inKey + ", not found in the local configuration and is not an integer key.", 0, 2);
                }
            }
            Logger logger3 = LOG;
            logger3.info("WEBVIEW message to pebble: " + out.toString());
            if (needsTransaction) {
                Integer num = this.lastTransaction;
                this.lastTransaction = Integer.valueOf(this.lastTransaction.intValue() + 1);
                GBApplication.deviceService().onAppConfiguration(this.mUuid, out.toString(), this.lastTransaction);
                return this.lastTransaction.toString();
            }
            GBApplication.deviceService().onAppConfiguration(this.mUuid, out.toString(), (Integer) null);
            return null;
        } catch (JSONException e) {
            LOG.warn("Error building the appmessage JSON object", (Throwable) e);
        }
    }

    @JavascriptInterface
    public String getActiveWatchInfo() {
        JSONObject wi = new JSONObject();
        try {
            wi.put("firmware", this.device.getFirmwareVersion());
            wi.put("platform", PebbleUtils.getPlatformName(this.device.getModel()));
            wi.put("model", PebbleUtils.getModel(this.device.getModel()));
            wi.put(HuamiConst.PREF_LANGUAGE, "en");
        } catch (JSONException e) {
            LOG.warn("Error building the ActiveWathcInfo JSON object", (Throwable) e);
        }
        return wi.toString();
    }

    @JavascriptInterface
    public String getAppConfigurationFile() {
        Logger logger = LOG;
        logger.debug("WEBVIEW loading config file of " + this.mUuid.toString());
        try {
            File destDir = PebbleUtils.getPbwCacheDir();
            File configurationFile = new File(destDir, this.mUuid.toString() + "_config.js");
            if (!configurationFile.exists()) {
                return null;
            }
            return "file:///" + configurationFile.getAbsolutePath();
        } catch (IOException e) {
            LOG.warn("Error loading config file", (Throwable) e);
            return null;
        }
    }

    @JavascriptInterface
    public String getAppStoredPreset() {
        try {
            File destDir = PebbleUtils.getPbwCacheDir();
            File configurationFile = new File(destDir, this.mUuid.toString() + "_preset.json");
            if (configurationFile.exists()) {
                return FileUtils.getStringFromFile(configurationFile);
            }
            return null;
        } catch (IOException e) {
            C1238GB.toast("Error reading presets", 1, 3);
            LOG.warn("Error reading presets", (Throwable) e);
            return null;
        }
    }

    @JavascriptInterface
    public void saveAppStoredPreset(String msg) {
        try {
            File destDir = PebbleUtils.getPbwCacheDir();
            Writer writer = new BufferedWriter(new FileWriter(new File(destDir, this.mUuid.toString() + "_preset.json")));
            writer.write(msg);
            writer.close();
            C1238GB.toast("Presets stored", 0, 1);
        } catch (IOException e) {
            C1238GB.toast("Error storing presets", 1, 3);
            LOG.warn("Error storing presets", (Throwable) e);
        }
    }

    @JavascriptInterface
    public String getAppUUID() {
        return this.mUuid.toString();
    }

    @JavascriptInterface
    public String getAppLocalstoragePrefix() {
        String prefix = this.device.getAddress() + this.mUuid.toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = prefix.getBytes(CharEncoding.UTF_8);
            digest.update(bytes, 0, bytes.length);
            byte[] bytes2 = digest.digest();
            StringBuilder sb = new StringBuilder();
            int length = bytes2.length;
            for (int i = 0; i < length; i++) {
                sb.append(String.format("%02X", new Object[]{Byte.valueOf(bytes2[i])}));
            }
            return sb.toString().toLowerCase();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            LOG.warn("Error definining local storage prefix", (Throwable) e);
            return prefix;
        }
    }

    @JavascriptInterface
    public String getWatchToken() {
        return "gb" + this.mUuid.toString();
    }

    @JavascriptInterface
    public String getCurrentPosition() {
        if (!isLocationEnabledForWatchApp()) {
            return "";
        }
        JSONObject geoPosition = new JSONObject();
        JSONObject coords = new JSONObject();
        try {
            CurrentPosition currentPosition = new CurrentPosition();
            geoPosition.put("timestamp", currentPosition.timestamp);
            coords.put("latitude", (double) currentPosition.getLatitude());
            coords.put("longitude", (double) currentPosition.getLongitude());
            coords.put("accuracy", (double) currentPosition.accuracy);
            coords.put("altitude", currentPosition.altitude);
            coords.put("speed", (double) currentPosition.speed);
            geoPosition.put("coords", coords);
        } catch (JSONException e) {
            LOG.warn(e.getMessage());
        }
        Logger logger = LOG;
        logger.info("WEBVIEW - geo position" + geoPosition.toString());
        return geoPosition.toString();
    }

    @JavascriptInterface
    public void eventFinished(String event) {
        Logger logger = LOG;
        logger.debug("WEBVIEW event finished: " + event);
    }
}
