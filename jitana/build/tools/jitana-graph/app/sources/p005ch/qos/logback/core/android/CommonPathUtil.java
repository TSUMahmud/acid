package p005ch.qos.logback.core.android;

import android.os.Environment;
import p005ch.qos.logback.core.util.EnvUtil;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.android.CommonPathUtil */
public abstract class CommonPathUtil {
    private static final String ASSETS_DIRECTORY = "assets";

    public static String getAssetsDirectoryPath() {
        return ASSETS_DIRECTORY;
    }

    public static String getDatabaseDirectoryPath(String str) {
        String absolutePath = EnvUtil.isAndroidOS() ? Environment.getDataDirectory().getAbsolutePath() : "/data";
        return absolutePath + "/data/" + str + "/databases";
    }

    public static String getExternalStorageDirectoryPath() {
        if (EnvUtil.isAndroidOS()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        String env = OptionHelper.getEnv("EXTERNAL_STORAGE");
        return env == null ? "/sdcard" : env;
    }

    public static String getFilesDirectoryPath(String str) {
        String absolutePath = EnvUtil.isAndroidOS() ? Environment.getDataDirectory().getAbsolutePath() : "/data";
        return absolutePath + "/data/" + str + "/files";
    }

    public static String getMountedExternalStorageDirectoryPath() {
        if (!EnvUtil.isAndroidOS()) {
            return "/mnt/sdcard";
        }
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals("mounted") || externalStorageState.equals("mounted_ro")) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }
}
