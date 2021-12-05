package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;
import nodomain.freeyourgadget.gadgetbridge.util.UriHelper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PBWReader {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PBWReader.class);
    private static final HashMap<String, Byte> appFileTypesMap = new HashMap<>();
    private static final HashMap<String, Byte> fwFileTypesMap = new HashMap<>();
    private GBDeviceApp app;
    private String hwRevision;
    private boolean isFirmware;
    private boolean isLanguage;
    private boolean isValid;
    private JSONObject mAppKeys;
    private short mAppVersion;
    private int mFlags;
    private int mIconId;
    private short mSdkVersion;
    private ArrayList<PebbleInstallable> pebbleInstallables = null;
    private final UriHelper uriHelper;

    static {
        appFileTypesMap.put("application", (byte) 5);
        appFileTypesMap.put("resources", (byte) 4);
        appFileTypesMap.put("worker", (byte) 7);
        fwFileTypesMap.put("firmware", (byte) 1);
        fwFileTypesMap.put("resources", (byte) 3);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:138:?, code lost:
        LOG.warn(r14 + " exeeds maximum of 500000 bytes");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0319, code lost:
        r7 = r30;
     */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x04b6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x04ea A[Catch:{ all -> 0x04f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01a7 A[Catch:{ JSONException -> 0x02a2 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PBWReader(android.net.Uri r34, android.content.Context r35, java.lang.String r36) throws java.io.IOException {
        /*
            r33 = this;
            r1 = r33
            r2 = r36
            java.lang.String r3 = "appKeys"
            r33.<init>()
            r0 = 0
            r1.pebbleInstallables = r0
            r4 = 0
            r1.isFirmware = r4
            r1.isLanguage = r4
            r1.isValid = r4
            r1.hwRevision = r0
            r1.mAppKeys = r0
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r0 = nodomain.freeyourgadget.gadgetbridge.util.UriHelper.get(r34, r35)
            r1.uriHelper = r0
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r0 = r1.uriHelper
            java.lang.String r0 = r0.getFileName()
            java.lang.String r5 = ".pbl"
            boolean r0 = r0.endsWith(r5)
            r5 = 1
            if (r0 == 0) goto L_0x0098
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.STM32CRC r0 = new nodomain.freeyourgadget.gadgetbridge.devices.pebble.STM32CRC
            r0.<init>()
            r3 = r0
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r0 = r1.uriHelper
            java.io.InputStream r4 = r0.openInputStream()
            r0 = 2000(0x7d0, float:2.803E-42)
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x0087 }
        L_0x003c:
            int r6 = r4.available()     // Catch:{ all -> 0x0087 }
            if (r6 <= 0) goto L_0x004a
            int r6 = r4.read(r0)     // Catch:{ all -> 0x0087 }
            r3.addData(r0, r6)     // Catch:{ all -> 0x0087 }
            goto L_0x003c
        L_0x004a:
            if (r4 == 0) goto L_0x004f
            r4.close()
        L_0x004f:
            int r0 = r3.getResult()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r4 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r7 = java.util.UUID.randomUUID()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r11 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.UNKNOWN
            java.lang.String r8 = "Language File"
            java.lang.String r9 = "unknown"
            java.lang.String r10 = "unknown"
            r6 = r4
            r6.<init>(r7, r8, r9, r10, r11)
            r1.app = r4
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r1.pebbleInstallables = r4
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable> r4 = r1.pebbleInstallables
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable r6 = new nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r7 = r1.uriHelper
            long r7 = r7.getFileSize()
            int r8 = (int) r7
            r7 = 6
            java.lang.String r9 = "lang"
            r6.<init>(r9, r8, r0, r7)
            r4.add(r6)
            r1.isValid = r5
            r1.isLanguage = r5
            return
        L_0x0087:
            r0 = move-exception
            r5 = r0
            throw r5     // Catch:{ all -> 0x008a }
        L_0x008a:
            r0 = move-exception
            r6 = r0
            if (r4 == 0) goto L_0x0097
            r4.close()     // Catch:{ all -> 0x0092 }
            goto L_0x0097
        L_0x0092:
            r0 = move-exception
            r7 = r0
            r5.addSuppressed(r7)
        L_0x0097:
            throw r6
        L_0x0098:
            java.lang.String r0 = ""
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r6 = r1.uriHelper
            java.lang.String r6 = r6.getFileName()
            java.lang.String r7 = ".pbz"
            boolean r6 = r6.endsWith(r7)
            if (r6 != 0) goto L_0x00c1
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r6 = r1.uriHelper
            java.lang.String r0 = r1.determinePlatformDir(r6, r2)
            java.lang.String r6 = "chalk"
            boolean r6 = r2.equals(r6)
            if (r6 == 0) goto L_0x00bf
            java.lang.String r6 = ""
            boolean r6 = r0.equals(r6)
            if (r6 == 0) goto L_0x00bf
            return
        L_0x00bf:
            r6 = r0
            goto L_0x00c2
        L_0x00c1:
            r6 = r0
        L_0x00c2:
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "using platformdir: '"
            r7.append(r8)
            r7.append(r6)
            java.lang.String r8 = "'"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r0.info(r7)
            r0 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r1.pebbleInstallables = r10
            r10 = 1024(0x400, float:1.435E-42)
            byte[] r10 = new byte[r10]
            java.util.zip.ZipInputStream r11 = new java.util.zip.ZipInputStream
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r12 = r1.uriHelper
            java.io.InputStream r12 = r12.openInputStream()
            r11.<init>(r12)
            r12 = r9
            r9 = r8
            r8 = r7
            r7 = r0
        L_0x00fb:
            java.util.zip.ZipEntry r0 = r11.getNextEntry()     // Catch:{ all -> 0x04fa }
            r13 = r0
            if (r0 == 0) goto L_0x04a5
            java.lang.String r0 = r13.getName()     // Catch:{ all -> 0x04fa }
            r14 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04fa }
            r0.<init>()     // Catch:{ all -> 0x04fa }
            r0.append(r6)     // Catch:{ all -> 0x04fa }
            java.lang.String r15 = "manifest.json"
            r0.append(r15)     // Catch:{ all -> 0x04fa }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04fa }
            boolean r0 = r14.equals(r0)     // Catch:{ all -> 0x04fa }
            r15 = -1
            if (r0 == 0) goto L_0x02e6
            long r16 = r13.getSize()     // Catch:{ all -> 0x02d9 }
            r18 = 8192(0x2000, double:4.0474E-320)
            int r0 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r0 <= 0) goto L_0x0134
            r26 = r7
            r29 = r8
            r31 = r9
            r7 = r10
            r27 = r12
            goto L_0x04ae
        L_0x0134:
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x02d9 }
            r0.<init>()     // Catch:{ all -> 0x02d9 }
            r18 = r0
        L_0x013b:
            int r0 = r11.read(r10)     // Catch:{ all -> 0x02d9 }
            r19 = r0
            if (r0 == r15) goto L_0x015c
            r15 = r18
            r5 = r19
            r15.write(r10, r4, r5)     // Catch:{ all -> 0x014f }
            r18 = r15
            r5 = 1
            r15 = -1
            goto L_0x013b
        L_0x014f:
            r0 = move-exception
            r2 = r0
            r26 = r7
            r29 = r8
            r31 = r9
            r7 = r10
            r27 = r12
            goto L_0x0505
        L_0x015c:
            r15 = r18
            r5 = r19
            java.lang.String r0 = r15.toString()     // Catch:{ all -> 0x02d9 }
            r19 = r0
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x02c0 }
            r4 = r19
            r0.<init>(r4)     // Catch:{ JSONException -> 0x02b0 }
            r19 = r0
            java.lang.String r0 = "firmware"
            r2 = r19
            org.json.JSONObject r0 = r2.getJSONObject(r0)     // Catch:{ JSONException -> 0x0189 }
            java.util.HashMap<java.lang.String, java.lang.Byte> r19 = fwFileTypesMap     // Catch:{ JSONException -> 0x0189 }
            r20 = r4
            r4 = 1
            r1.isFirmware = r4     // Catch:{ JSONException -> 0x0187 }
            java.lang.String r4 = "hwrev"
            java.lang.String r4 = r0.getString(r4)     // Catch:{ JSONException -> 0x0187 }
            r1.hwRevision = r4     // Catch:{ JSONException -> 0x0187 }
            goto L_0x0199
        L_0x0187:
            r0 = move-exception
            goto L_0x0192
        L_0x0189:
            r0 = move-exception
            r20 = r4
            goto L_0x0192
        L_0x018d:
            r0 = move-exception
            r20 = r4
            r2 = r19
        L_0x0192:
            java.util.HashMap<java.lang.String, java.lang.Byte> r4 = appFileTypesMap     // Catch:{ JSONException -> 0x02a2 }
            r19 = r4
            r4 = 0
            r1.isFirmware = r4     // Catch:{ JSONException -> 0x02a2 }
        L_0x0199:
            java.util.Set r0 = r19.entrySet()     // Catch:{ JSONException -> 0x02a2 }
            java.util.Iterator r4 = r0.iterator()     // Catch:{ JSONException -> 0x02a2 }
        L_0x01a1:
            boolean r0 = r4.hasNext()     // Catch:{ JSONException -> 0x02a2 }
            if (r0 == 0) goto L_0x028d
            java.lang.Object r0 = r4.next()     // Catch:{ JSONException -> 0x02a2 }
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ JSONException -> 0x02a2 }
            r21 = r0
            java.lang.Object r0 = r21.getKey()     // Catch:{ JSONException -> 0x026a }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ JSONException -> 0x026a }
            org.json.JSONObject r0 = r2.getJSONObject(r0)     // Catch:{ JSONException -> 0x026a }
            r22 = r2
            java.lang.String r2 = "name"
            java.lang.String r2 = r0.getString(r2)     // Catch:{ JSONException -> 0x025a }
            r23 = r4
            java.lang.String r4 = "size"
            int r4 = r0.getInt(r4)     // Catch:{ JSONException -> 0x0258 }
            r24 = r5
            java.lang.String r5 = "crc"
            long r25 = r0.getLong(r5)     // Catch:{ JSONException -> 0x024c }
            r27 = r25
            java.lang.Object r5 = r21.getValue()     // Catch:{ JSONException -> 0x024c }
            java.lang.Byte r5 = (java.lang.Byte) r5     // Catch:{ JSONException -> 0x024c }
            byte r5 = r5.byteValue()     // Catch:{ JSONException -> 0x024c }
            r25 = r0
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable> r0 = r1.pebbleInstallables     // Catch:{ JSONException -> 0x024c }
            r26 = r7
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable r7 = new nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable     // Catch:{ JSONException -> 0x0242, all -> 0x0237 }
            r29 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x022f, all -> 0x0226 }
            r8.<init>()     // Catch:{ JSONException -> 0x022f, all -> 0x0226 }
            r8.append(r6)     // Catch:{ JSONException -> 0x022f, all -> 0x0226 }
            r8.append(r2)     // Catch:{ JSONException -> 0x022f, all -> 0x0226 }
            java.lang.String r8 = r8.toString()     // Catch:{ JSONException -> 0x022f, all -> 0x0226 }
            r31 = r9
            r30 = r10
            r9 = r27
            r27 = r12
            int r12 = (int) r9
            r7.<init>(r8, r4, r12, r5)     // Catch:{ JSONException -> 0x0223 }
            r0.add(r7)     // Catch:{ JSONException -> 0x0223 }
            org.slf4j.Logger r0 = LOG     // Catch:{ JSONException -> 0x0223 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0223 }
            r7.<init>()     // Catch:{ JSONException -> 0x0223 }
            java.lang.String r8 = "found file to install: "
            r7.append(r8)     // Catch:{ JSONException -> 0x0223 }
            r7.append(r6)     // Catch:{ JSONException -> 0x0223 }
            r7.append(r2)     // Catch:{ JSONException -> 0x0223 }
            java.lang.String r7 = r7.toString()     // Catch:{ JSONException -> 0x0223 }
            r0.info(r7)     // Catch:{ JSONException -> 0x0223 }
            r7 = 1
            r1.isValid = r7     // Catch:{ JSONException -> 0x0223 }
            goto L_0x027b
        L_0x0223:
            r0 = move-exception
            goto L_0x027b
        L_0x0226:
            r0 = move-exception
            r31 = r9
            r27 = r12
            r2 = r0
            r7 = r10
            goto L_0x0505
        L_0x022f:
            r0 = move-exception
            r31 = r9
            r30 = r10
            r27 = r12
            goto L_0x027b
        L_0x0237:
            r0 = move-exception
            r29 = r8
            r31 = r9
            r27 = r12
            r2 = r0
            r7 = r10
            goto L_0x0505
        L_0x0242:
            r0 = move-exception
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            goto L_0x027b
        L_0x024c:
            r0 = move-exception
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            goto L_0x027b
        L_0x0258:
            r0 = move-exception
            goto L_0x025d
        L_0x025a:
            r0 = move-exception
            r23 = r4
        L_0x025d:
            r24 = r5
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            goto L_0x027b
        L_0x026a:
            r0 = move-exception
            r22 = r2
            r23 = r4
            r24 = r5
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
        L_0x027b:
            r2 = r22
            r4 = r23
            r5 = r24
            r7 = r26
            r12 = r27
            r8 = r29
            r10 = r30
            r9 = r31
            goto L_0x01a1
        L_0x028d:
            r22 = r2
            r24 = r5
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            r20 = r3
            r7 = r30
            goto L_0x0486
        L_0x02a2:
            r0 = move-exception
            r24 = r5
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            goto L_0x02cf
        L_0x02b0:
            r0 = move-exception
            r20 = r4
            r24 = r5
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            goto L_0x02cf
        L_0x02c0:
            r0 = move-exception
            r24 = r5
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            r20 = r19
        L_0x02cf:
            r2 = 0
            r1.isValid = r2     // Catch:{ all -> 0x031d }
            r0.printStackTrace()     // Catch:{ all -> 0x031d }
            r7 = r30
            goto L_0x04ae
        L_0x02d9:
            r0 = move-exception
            r26 = r7
            r29 = r8
            r31 = r9
            r27 = r12
            r2 = r0
            r7 = r10
            goto L_0x0505
        L_0x02e6:
            r26 = r7
            r29 = r8
            r31 = r9
            r30 = r10
            r27 = r12
            java.lang.String r2 = "appinfo.json"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x049f }
            if (r2 == 0) goto L_0x03f7
            long r4 = r13.getSize()     // Catch:{ all -> 0x049f }
            r7 = 500000(0x7a120, double:2.47033E-318)
            int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r2 <= 0) goto L_0x0323
            org.slf4j.Logger r0 = LOG     // Catch:{ all -> 0x031d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x031d }
            r2.<init>()     // Catch:{ all -> 0x031d }
            r2.append(r14)     // Catch:{ all -> 0x031d }
            java.lang.String r3 = " exeeds maximum of 500000 bytes"
            r2.append(r3)     // Catch:{ all -> 0x031d }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x031d }
            r0.warn(r2)     // Catch:{ all -> 0x031d }
            r7 = r30
            goto L_0x04ae
        L_0x031d:
            r0 = move-exception
            r2 = r0
            r7 = r30
            goto L_0x0505
        L_0x0323:
            java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x049f }
            r2.<init>()     // Catch:{ all -> 0x049f }
        L_0x0328:
            r7 = r30
            int r8 = r11.read(r7)     // Catch:{ all -> 0x049b }
            r9 = r8
            r0 = -1
            if (r8 == r0) goto L_0x0339
            r8 = 0
            r2.write(r7, r8, r9)     // Catch:{ all -> 0x049b }
            r30 = r7
            goto L_0x0328
        L_0x0339:
            java.lang.String r0 = r2.toString()     // Catch:{ all -> 0x049b }
            r8 = r0
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x03d6 }
            r0.<init>(r8)     // Catch:{ JSONException -> 0x03d6 }
            java.lang.String r10 = "shortName"
            java.lang.String r10 = r0.getString(r10)     // Catch:{ JSONException -> 0x03d6 }
            java.lang.String r12 = "companyName"
            java.lang.String r12 = r0.getString(r12)     // Catch:{ JSONException -> 0x03cc, all -> 0x03c6 }
            java.lang.String r15 = "versionLabel"
            java.lang.String r15 = r0.getString(r15)     // Catch:{ JSONException -> 0x03be, all -> 0x03b6 }
            r16 = r2
            java.lang.String r2 = "uuid"
            java.lang.String r2 = r0.getString(r2)     // Catch:{ JSONException -> 0x03b2 }
            java.util.UUID r2 = java.util.UUID.fromString(r2)     // Catch:{ JSONException -> 0x03b2 }
            boolean r17 = r0.has(r3)     // Catch:{ JSONException -> 0x03ae, all -> 0x03a0 }
            if (r17 == 0) goto L_0x0393
            r17 = r2
            org.json.JSONObject r2 = r0.getJSONObject(r3)     // Catch:{ JSONException -> 0x0390 }
            r1.mAppKeys = r2     // Catch:{ JSONException -> 0x0390 }
            org.slf4j.Logger r2 = LOG     // Catch:{ JSONException -> 0x0390 }
            r19 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0390 }
            r0.<init>()     // Catch:{ JSONException -> 0x0390 }
            r20 = r3
            java.lang.String r3 = "found appKeys:"
            r0.append(r3)     // Catch:{ JSONException -> 0x0390 }
            org.json.JSONObject r3 = r1.mAppKeys     // Catch:{ JSONException -> 0x0390 }
            java.lang.String r3 = r3.toString()     // Catch:{ JSONException -> 0x0390 }
            r0.append(r3)     // Catch:{ JSONException -> 0x0390 }
            java.lang.String r0 = r0.toString()     // Catch:{ JSONException -> 0x0390 }
            r2.info(r0)     // Catch:{ JSONException -> 0x0390 }
            goto L_0x0399
        L_0x0390:
            r0 = move-exception
            goto L_0x03e1
        L_0x0393:
            r19 = r0
            r17 = r2
            r20 = r3
        L_0x0399:
            r8 = r12
            r9 = r15
            r12 = r17
            goto L_0x048e
        L_0x03a0:
            r0 = move-exception
            r17 = r2
            r2 = r0
            r26 = r10
            r29 = r12
            r31 = r15
            r27 = r17
            goto L_0x0505
        L_0x03ae:
            r0 = move-exception
            r17 = r2
            goto L_0x03e1
        L_0x03b2:
            r0 = move-exception
            r17 = r27
            goto L_0x03e1
        L_0x03b6:
            r0 = move-exception
            r2 = r0
            r26 = r10
            r29 = r12
            goto L_0x0505
        L_0x03be:
            r0 = move-exception
            r16 = r2
            r17 = r27
            r15 = r31
            goto L_0x03e1
        L_0x03c6:
            r0 = move-exception
            r2 = r0
            r26 = r10
            goto L_0x0505
        L_0x03cc:
            r0 = move-exception
            r16 = r2
            r17 = r27
            r12 = r29
            r15 = r31
            goto L_0x03e1
        L_0x03d6:
            r0 = move-exception
            r16 = r2
            r10 = r26
            r17 = r27
            r12 = r29
            r15 = r31
        L_0x03e1:
            r2 = 0
            r1.isValid = r2     // Catch:{ all -> 0x03eb }
            r0.printStackTrace()     // Catch:{ all -> 0x03eb }
            r27 = r17
            goto L_0x04b4
        L_0x03eb:
            r0 = move-exception
            r2 = r0
            r26 = r10
            r29 = r12
            r31 = r15
            r27 = r17
            goto L_0x0505
        L_0x03f7:
            r20 = r3
            r7 = r30
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x049b }
            r0.<init>()     // Catch:{ all -> 0x049b }
            r0.append(r6)     // Catch:{ all -> 0x049b }
            java.lang.String r2 = "pebble-app.bin"
            r0.append(r2)     // Catch:{ all -> 0x049b }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x049b }
            boolean r0 = r14.equals(r0)     // Catch:{ all -> 0x049b }
            if (r0 == 0) goto L_0x0486
            r0 = 108(0x6c, float:1.51E-43)
            r2 = 0
            r11.read(r7, r2, r0)     // Catch:{ all -> 0x049b }
            r0 = 32
            byte[] r2 = new byte[r0]     // Catch:{ all -> 0x049b }
            java.nio.ByteBuffer r3 = java.nio.ByteBuffer.wrap(r7)     // Catch:{ all -> 0x049b }
            java.nio.ByteOrder r4 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ all -> 0x049b }
            r3.order(r4)     // Catch:{ all -> 0x049b }
            r3.getLong()     // Catch:{ all -> 0x049b }
            r3.getShort()     // Catch:{ all -> 0x049b }
            short r4 = r3.getShort()     // Catch:{ all -> 0x049b }
            r1.mSdkVersion = r4     // Catch:{ all -> 0x049b }
            short r4 = r3.getShort()     // Catch:{ all -> 0x049b }
            r1.mAppVersion = r4     // Catch:{ all -> 0x049b }
            r3.getShort()     // Catch:{ all -> 0x049b }
            r3.getInt()     // Catch:{ all -> 0x049b }
            r3.getInt()     // Catch:{ all -> 0x049b }
            r4 = 0
            r3.get(r2, r4, r0)     // Catch:{ all -> 0x049b }
            r3.get(r2, r4, r0)     // Catch:{ all -> 0x049b }
            int r0 = r3.getInt()     // Catch:{ all -> 0x049b }
            r1.mIconId = r0     // Catch:{ all -> 0x049b }
            org.slf4j.Logger r0 = LOG     // Catch:{ all -> 0x049b }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x049b }
            r4.<init>()     // Catch:{ all -> 0x049b }
            java.lang.String r5 = "got icon id from pebble-app.bin: "
            r4.append(r5)     // Catch:{ all -> 0x049b }
            int r5 = r1.mIconId     // Catch:{ all -> 0x049b }
            r4.append(r5)     // Catch:{ all -> 0x049b }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x049b }
            r0.info(r4)     // Catch:{ all -> 0x049b }
            r3.getInt()     // Catch:{ all -> 0x049b }
            int r0 = r3.getInt()     // Catch:{ all -> 0x049b }
            r1.mFlags = r0     // Catch:{ all -> 0x049b }
            org.slf4j.Logger r0 = LOG     // Catch:{ all -> 0x049b }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x049b }
            r4.<init>()     // Catch:{ all -> 0x049b }
            java.lang.String r5 = "got flags from pebble-app.bin: "
            r4.append(r5)     // Catch:{ all -> 0x049b }
            int r5 = r1.mFlags     // Catch:{ all -> 0x049b }
            r4.append(r5)     // Catch:{ all -> 0x049b }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x049b }
            r0.info(r4)     // Catch:{ all -> 0x049b }
        L_0x0486:
            r10 = r26
            r12 = r27
            r8 = r29
            r9 = r31
        L_0x048e:
            r2 = r36
            r3 = r20
            r4 = 0
            r5 = 1
            r32 = r10
            r10 = r7
            r7 = r32
            goto L_0x00fb
        L_0x049b:
            r0 = move-exception
            r2 = r0
            goto L_0x0505
        L_0x049f:
            r0 = move-exception
            r7 = r30
            r2 = r0
            goto L_0x0505
        L_0x04a5:
            r26 = r7
            r29 = r8
            r31 = r9
            r7 = r10
            r27 = r12
        L_0x04ae:
            r10 = r26
            r12 = r29
            r15 = r31
        L_0x04b4:
            if (r27 == 0) goto L_0x04e6
            if (r10 == 0) goto L_0x04e6
            if (r12 == 0) goto L_0x04e6
            if (r15 == 0) goto L_0x04e6
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r0 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_GENERIC     // Catch:{ all -> 0x04f1 }
            int r2 = r1.mFlags     // Catch:{ all -> 0x04f1 }
            r3 = 16
            r2 = r2 & r3
            if (r2 != r3) goto L_0x04c9
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r2 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_ACTIVITYTRACKER     // Catch:{ all -> 0x04f1 }
            r0 = r2
            goto L_0x04d2
        L_0x04c9:
            int r2 = r1.mFlags     // Catch:{ all -> 0x04f1 }
            r3 = 1
            r2 = r2 & r3
            if (r2 != r3) goto L_0x04d2
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r2 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.WATCHFACE     // Catch:{ all -> 0x04f1 }
            r0 = r2
        L_0x04d2:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r2 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp     // Catch:{ all -> 0x04f1 }
            r21 = r2
            r22 = r27
            r23 = r10
            r24 = r12
            r25 = r15
            r26 = r0
            r21.<init>(r22, r23, r24, r25, r26)     // Catch:{ all -> 0x04f1 }
            r1.app = r2     // Catch:{ all -> 0x04f1 }
            goto L_0x04ed
        L_0x04e6:
            boolean r0 = r1.isFirmware     // Catch:{ all -> 0x04f1 }
            if (r0 != 0) goto L_0x04ed
            r2 = 0
            r1.isValid = r2     // Catch:{ all -> 0x04f1 }
        L_0x04ed:
            r11.close()
            return
        L_0x04f1:
            r0 = move-exception
            r2 = r0
            r26 = r10
            r29 = r12
            r31 = r15
            goto L_0x0505
        L_0x04fa:
            r0 = move-exception
            r26 = r7
            r29 = r8
            r31 = r9
            r7 = r10
            r27 = r12
            r2 = r0
        L_0x0505:
            throw r2     // Catch:{ all -> 0x0506 }
        L_0x0506:
            r0 = move-exception
            r3 = r0
            r11.close()     // Catch:{ all -> 0x050c }
            goto L_0x0511
        L_0x050c:
            r0 = move-exception
            r4 = r0
            r2.addSuppressed(r4)
        L_0x0511:
            goto L_0x0513
        L_0x0512:
            throw r3
        L_0x0513:
            goto L_0x0512
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWReader.<init>(android.net.Uri, android.content.Context, java.lang.String):void");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x008e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0093, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0094, code lost:
        r2.addSuppressed(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0097, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String determinePlatformDir(nodomain.freeyourgadget.gadgetbridge.util.UriHelper r9, java.lang.String r10) throws java.io.IOException {
        /*
            r8 = this;
            java.lang.String r0 = ""
            int r1 = r10.hashCode()
            r2 = 0
            r3 = 3
            r4 = 2
            r5 = 1
            switch(r1) {
                case -1396206315: goto L_0x002c;
                case 94623515: goto L_0x0022;
                case 96623556: goto L_0x0018;
                case 1668127346: goto L_0x000e;
                default: goto L_0x000d;
            }
        L_0x000d:
            goto L_0x0036
        L_0x000e:
            java.lang.String r1 = "diorite"
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = 2
            goto L_0x0037
        L_0x0018:
            java.lang.String r1 = "emery"
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = 3
            goto L_0x0037
        L_0x0022:
            java.lang.String r1 = "chalk"
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = 1
            goto L_0x0037
        L_0x002c:
            java.lang.String r1 = "basalt"
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = 0
            goto L_0x0037
        L_0x0036:
            r1 = -1
        L_0x0037:
            java.lang.String r6 = "basalt/"
            if (r1 == 0) goto L_0x005d
            if (r1 == r5) goto L_0x0056
            java.lang.String r5 = "aplite/"
            if (r1 == r4) goto L_0x004f
            if (r1 == r3) goto L_0x0048
            java.lang.String[] r1 = new java.lang.String[]{r5}
            goto L_0x0062
        L_0x0048:
            java.lang.String r1 = "emery/"
            java.lang.String[] r1 = new java.lang.String[]{r1, r6}
            goto L_0x0062
        L_0x004f:
            java.lang.String r1 = "diorite/"
            java.lang.String[] r1 = new java.lang.String[]{r1, r5}
            goto L_0x0062
        L_0x0056:
            java.lang.String r1 = "chalk/"
            java.lang.String[] r1 = new java.lang.String[]{r1}
            goto L_0x0062
        L_0x005d:
            java.lang.String[] r1 = new java.lang.String[]{r6}
        L_0x0062:
            int r3 = r1.length
        L_0x0063:
            if (r2 >= r3) goto L_0x0098
            r4 = r1[r2]
            java.util.zip.ZipInputStream r5 = new java.util.zip.ZipInputStream
            java.io.InputStream r6 = r9.openInputStream()
            r5.<init>(r6)
        L_0x0070:
            java.util.zip.ZipEntry r6 = r5.getNextEntry()     // Catch:{ all -> 0x008c }
            r7 = r6
            if (r6 == 0) goto L_0x0086
            java.lang.String r6 = r7.getName()     // Catch:{ all -> 0x008c }
            boolean r6 = r6.startsWith(r4)     // Catch:{ all -> 0x008c }
            if (r6 == 0) goto L_0x0070
            r5.close()
            return r4
        L_0x0086:
            r5.close()
            int r2 = r2 + 1
            goto L_0x0063
        L_0x008c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x008e }
        L_0x008e:
            r3 = move-exception
            r5.close()     // Catch:{ all -> 0x0093 }
            goto L_0x0097
        L_0x0093:
            r6 = move-exception
            r2.addSuppressed(r6)
        L_0x0097:
            throw r3
        L_0x0098:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWReader.determinePlatformDir(nodomain.freeyourgadget.gadgetbridge.util.UriHelper, java.lang.String):java.lang.String");
    }

    public boolean isFirmware() {
        return this.isFirmware;
    }

    public boolean isLanguage() {
        return this.isLanguage;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public GBDeviceApp getGBDeviceApp() {
        return this.app;
    }

    public InputStream getInputStreamFile(String filename) {
        ZipEntry ze;
        if (this.isLanguage) {
            try {
                return this.uriHelper.openInputStream();
            } catch (FileNotFoundException e) {
                Logger logger = LOG;
                logger.warn("file not found: " + e);
                return null;
            }
        } else {
            ZipInputStream zis = null;
            try {
                ZipInputStream zis2 = new ZipInputStream(this.uriHelper.openInputStream());
                do {
                    ZipEntry nextEntry = zis2.getNextEntry();
                    ze = nextEntry;
                    if (nextEntry == null) {
                        zis2.close();
                        return null;
                    }
                } while (!ze.getName().equals(filename));
                return zis2;
            } catch (Throwable e2) {
                if (zis != null) {
                    try {
                        zis.close();
                    } catch (IOException e3) {
                    }
                }
                e2.printStackTrace();
            }
        }
    }

    public PebbleInstallable[] getPebbleInstallables() {
        ArrayList<PebbleInstallable> arrayList = this.pebbleInstallables;
        if (arrayList == null) {
            return null;
        }
        return (PebbleInstallable[]) arrayList.toArray(new PebbleInstallable[arrayList.size()]);
    }

    public String getHWRevision() {
        return this.hwRevision;
    }

    public short getSdkVersion() {
        return this.mSdkVersion;
    }

    public short getAppVersion() {
        return this.mAppVersion;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getIconId() {
        return this.mIconId;
    }

    public JSONObject getAppKeysJSON() {
        return this.mAppKeys;
    }
}
