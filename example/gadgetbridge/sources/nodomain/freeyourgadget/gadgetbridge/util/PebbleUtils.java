package nodomain.freeyourgadget.gadgetbridge.util;

import android.graphics.Color;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PebbleUtils {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleUtils.class);

    public static String getPlatformName(String hwRev) {
        if (hwRev.startsWith("snowy")) {
            return "basalt";
        }
        if (hwRev.startsWith("spalding")) {
            return "chalk";
        }
        if (hwRev.startsWith("silk")) {
            return "diorite";
        }
        if (hwRev.startsWith("robert")) {
            return "emery";
        }
        return "aplite";
    }

    public static String getModel(String hwRev) {
        if (hwRev.startsWith("snowy")) {
            return "pebble_time_black";
        }
        if (hwRev.startsWith("spalding")) {
            return "pebble_time_round_black_20mm";
        }
        if (hwRev.startsWith("silk")) {
            return "pebble2_black";
        }
        if (hwRev.startsWith("robert")) {
            return "pebble_time2_black";
        }
        return "pebble_black";
    }

    public static int getFwMajor(String fwString) {
        return fwString.charAt(1) - '0';
    }

    public static boolean hasHRM(String hwRev) {
        String platformName = getPlatformName(hwRev);
        return "diorite".equals(platformName) || "emery".equals(platformName);
    }

    public static boolean hasHealth(String hwRev) {
        return !"aplite".equals(getPlatformName(hwRev));
    }

    public static byte getPebbleColor(int color) {
        return (byte) ((((((color >> 16) & 255) / 85) & 3) << 4) | 192 | (((((color >> 8) & 255) / 85) & 3) << 2) | (((color & 255) / 85) & 3));
    }

    public static byte getPebbleColor(String colorHex) {
        return getPebbleColor(Color.parseColor(colorHex));
    }

    public static File getPbwCacheDir() throws IOException {
        return new File(FileUtils.getExternalFilesDir(), "pbw-cache");
    }

    public static JSONObject getAppConfigurationKeys(UUID uuid) {
        try {
            File destDir = getPbwCacheDir();
            File configurationFile = new File(destDir, uuid.toString() + ".json");
            if (configurationFile.exists()) {
                return new JSONObject(FileUtils.getStringFromFile(configurationFile)).getJSONObject("appKeys");
            }
            return null;
        } catch (IOException | JSONException e) {
            LOG.warn("Unable to parse configuration JSON file", (Throwable) e);
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0081 A[Catch:{ Exception -> 0x00bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x008a A[Catch:{ Exception -> 0x00bc }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String parseIncomingAppMessage(java.lang.String r16, java.util.UUID r17, int r18) {
        /*
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            r1 = r0
            org.json.JSONObject r2 = getAppConfigurationKeys(r17)
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r3 = r0
            if (r2 == 0) goto L_0x0030
            if (r16 != 0) goto L_0x0015
            goto L_0x0030
        L_0x0015:
            java.util.Iterator r0 = r2.keys()
        L_0x0019:
            boolean r4 = r0.hasNext()
            if (r4 == 0) goto L_0x002d
            java.lang.Object r4 = r0.next()
            java.lang.String r4 = (java.lang.String) r4
            int r5 = r2.optInt(r4)
            r3.put(r5, r4)
            goto L_0x0019
        L_0x002d:
            r4 = r16
            goto L_0x0033
        L_0x0030:
            java.lang.String r0 = "[]"
            r4 = r0
        L_0x0033:
            org.json.JSONArray r0 = new org.json.JSONArray     // Catch:{ Exception -> 0x00bc }
            r0.<init>(r4)     // Catch:{ Exception -> 0x00bc }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x00bc }
            r5.<init>()     // Catch:{ Exception -> 0x00bc }
            r6 = 0
            r7 = r6
        L_0x003f:
            int r8 = r0.length()     // Catch:{ Exception -> 0x00bc }
            if (r7 >= r8) goto L_0x00a2
            org.json.JSONObject r8 = r0.getJSONObject(r7)     // Catch:{ Exception -> 0x00bc }
            r9 = 0
            r10 = 0
            java.util.Iterator r11 = r8.keys()     // Catch:{ Exception -> 0x00bc }
        L_0x004f:
            boolean r12 = r11.hasNext()     // Catch:{ Exception -> 0x00bc }
            if (r12 == 0) goto L_0x0097
            java.lang.Object r12 = r11.next()     // Catch:{ Exception -> 0x00bc }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ Exception -> 0x00bc }
            r13 = -1
            int r14 = r12.hashCode()     // Catch:{ Exception -> 0x00bc }
            r15 = 106079(0x19e5f, float:1.48648E-40)
            r6 = 1
            if (r14 == r15) goto L_0x0076
            r15 = 111972721(0x6ac9171, float:6.4912916E-35)
            if (r14 == r15) goto L_0x006c
        L_0x006b:
            goto L_0x007f
        L_0x006c:
            java.lang.String r14 = "value"
            boolean r14 = r12.equals(r14)     // Catch:{ Exception -> 0x00bc }
            if (r14 == 0) goto L_0x006b
            r13 = 1
            goto L_0x007f
        L_0x0076:
            java.lang.String r14 = "key"
            boolean r14 = r12.equals(r14)     // Catch:{ Exception -> 0x00bc }
            if (r14 == 0) goto L_0x006b
            r13 = 0
        L_0x007f:
            if (r13 == 0) goto L_0x008a
            if (r13 == r6) goto L_0x0084
            goto L_0x0095
        L_0x0084:
            java.lang.Object r6 = r8.get(r12)     // Catch:{ Exception -> 0x00bc }
            r10 = r6
            goto L_0x0095
        L_0x008a:
            int r6 = r8.optInt(r12)     // Catch:{ Exception -> 0x00bc }
            java.lang.Object r6 = r3.get(r6)     // Catch:{ Exception -> 0x00bc }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x00bc }
            r9 = r6
        L_0x0095:
            r6 = 0
            goto L_0x004f
        L_0x0097:
            if (r9 == 0) goto L_0x009e
            if (r10 == 0) goto L_0x009e
            r5.put(r9, r10)     // Catch:{ Exception -> 0x00bc }
        L_0x009e:
            int r7 = r7 + 1
            r6 = 0
            goto L_0x003f
        L_0x00a2:
            java.lang.String r6 = "payload"
            r1.put(r6, r5)     // Catch:{ Exception -> 0x00bc }
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ Exception -> 0x00bc }
            r6.<init>()     // Catch:{ Exception -> 0x00bc }
            java.lang.String r7 = "transactionId"
            r8 = r18
            r6.put(r7, r8)     // Catch:{ Exception -> 0x00ba }
            java.lang.String r7 = "data"
            r1.put(r7, r6)     // Catch:{ Exception -> 0x00ba }
            goto L_0x00c6
        L_0x00ba:
            r0 = move-exception
            goto L_0x00bf
        L_0x00bc:
            r0 = move-exception
            r8 = r18
        L_0x00bf:
            org.slf4j.Logger r5 = LOG
            java.lang.String r6 = "Unable to parse incoming app message"
            r5.warn((java.lang.String) r6, (java.lang.Throwable) r0)
        L_0x00c6:
            java.lang.String r0 = r1.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils.parseIncomingAppMessage(java.lang.String, java.util.UUID, int):java.lang.String");
    }
}
