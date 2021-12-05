package p005ch.qos.logback.core.util;

import java.util.ArrayList;

/* renamed from: ch.qos.logback.core.util.EnvUtil */
public class EnvUtil {
    public static boolean isAndroidOS() {
        String systemProperty = OptionHelper.getSystemProperty("os.name");
        String env = OptionHelper.getEnv("ANDROID_ROOT");
        String env2 = OptionHelper.getEnv("ANDROID_DATA");
        return systemProperty != null && systemProperty.contains("Linux") && env != null && env.contains("/system") && env2 != null && env2.contains("/data");
    }

    public static boolean isJDK5() {
        return isJDK_N_OrHigher(5);
    }

    public static boolean isJDK6OrHigher() {
        return isJDK_N_OrHigher(6);
    }

    public static boolean isJDK7OrHigher() {
        return isJDK_N_OrHigher(7);
    }

    private static boolean isJDK_N_OrHigher(int i) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i2 = 0; i2 < 5; i2++) {
            arrayList.add("1." + (i + i2));
        }
        String property = System.getProperty("java.version");
        if (property == null) {
            return false;
        }
        for (String startsWith : arrayList) {
            if (property.startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
