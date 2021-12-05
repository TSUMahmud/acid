package cyanogenmod.p007os;

import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.SparseArray;

/* renamed from: cyanogenmod.os.Build */
public class Build {
    public static final String CYANOGENMOD_DISPLAY_VERSION = getString("ro.cm.display.version");
    public static final String CYANOGENMOD_VERSION = getString("ro.cm.version");
    public static final String UNKNOWN = "unknown";
    private static final SparseArray<String> sdkMap = new SparseArray<>();

    /* renamed from: cyanogenmod.os.Build$CM_VERSION */
    public static class CM_VERSION {
        public static final int SDK_INT = SystemProperties.getInt("ro.cm.build.version.plat.sdk", 0);
    }

    /* renamed from: cyanogenmod.os.Build$CM_VERSION_CODES */
    public static class CM_VERSION_CODES {
        public static final int APRICOT = 1;
        public static final int BOYSENBERRY = 2;
        public static final int CANTALOUPE = 3;
        public static final int DRAGON_FRUIT = 4;
        public static final int ELDERBERRY = 5;
        public static final int FIG = 6;
    }

    static {
        sdkMap.put(1, "Apricot");
        sdkMap.put(2, "Boysenberry");
        sdkMap.put(3, "Cantaloupe");
        sdkMap.put(4, "Dragon Fruit");
        sdkMap.put(5, "Elderberry");
        sdkMap.put(6, "Fig");
    }

    public static String getNameForSDKInt(int sdkInt) {
        String name = sdkMap.get(sdkInt);
        if (TextUtils.isEmpty(name)) {
            return "unknown";
        }
        return name;
    }

    private static String getString(String property) {
        return SystemProperties.get(property, "unknown");
    }
}
