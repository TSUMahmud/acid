package p005ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.zip.ZipEntry;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.util.FileUtil;

/* renamed from: ch.qos.logback.core.rolling.helper.Compressor */
public class Compressor extends ContextAwareBase {
    static final int BUFFER_SIZE = 8192;
    final CompressionMode compressionMode;

    /* renamed from: ch.qos.logback.core.rolling.helper.Compressor$1 */
    static /* synthetic */ class C05241 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode = new int[CompressionMode.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[CompressionMode.GZ.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[CompressionMode.ZIP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[CompressionMode.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public Compressor(CompressionMode compressionMode2) {
        this.compressionMode = compressionMode2;
    }

    public static String computeFileNameStr_WCS(String str, CompressionMode compressionMode2) {
        int i;
        int length = str.length();
        int i2 = C05241.$SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[compressionMode2.ordinal()];
        if (i2 != 1) {
            if (i2 != 2) {
                if (i2 == 3) {
                    return str;
                }
                throw new IllegalStateException("Execution should not reach this point");
            } else if (!str.endsWith(".zip")) {
                return str;
            } else {
                i = length - 4;
            }
        } else if (!str.endsWith(".gz")) {
            return str;
        } else {
            i = length - 3;
        }
        return str.substring(0, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x0113 A[SYNTHETIC, Splitter:B:45:0x0113] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x011a A[SYNTHETIC, Splitter:B:49:0x011a] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0123 A[SYNTHETIC, Splitter:B:54:0x0123] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x012a A[SYNTHETIC, Splitter:B:58:0x012a] */
    /* JADX WARNING: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void gzCompress(java.lang.String r9, java.lang.String r10) {
        /*
            r8 = this;
            java.lang.String r0 = "]."
            java.io.File r1 = new java.io.File
            r1.<init>(r9)
            boolean r2 = r1.exists()
            if (r2 != 0) goto L_0x002c
            ch.qos.logback.core.status.WarnStatus r10 = new ch.qos.logback.core.status.WarnStatus
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "The file to compress named ["
            r0.append(r1)
            r0.append(r9)
            java.lang.String r9 = "] does not exist."
            r0.append(r9)
            java.lang.String r9 = r0.toString()
            r10.<init>(r9, r8)
            r8.addStatus(r10)
            return
        L_0x002c:
            java.lang.String r2 = ".gz"
            boolean r3 = r10.endsWith(r2)
            if (r3 != 0) goto L_0x0043
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r3.append(r2)
            java.lang.String r10 = r3.toString()
        L_0x0043:
            java.io.File r2 = new java.io.File
            r2.<init>(r10)
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x0068
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r0 = "The target compressed file named ["
            r9.append(r0)
            r9.append(r10)
            java.lang.String r10 = "] exist already. Aborting file compression."
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.addWarn(r9)
            return
        L_0x0068:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "GZ compressing ["
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "] as ["
            r3.append(r4)
            r3.append(r2)
            java.lang.String r4 = "]"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r8.addInfo(r3)
            r8.createMissingTargetDirsIfNecessary(r2)
            r2 = 0
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r4.<init>(r9)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            java.util.zip.GZIPOutputStream r4 = new java.util.zip.GZIPOutputStream     // Catch:{ Exception -> 0x00e4, all -> 0x00e0 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00e4, all -> 0x00e0 }
            r5.<init>(r10)     // Catch:{ Exception -> 0x00e4, all -> 0x00e0 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x00e4, all -> 0x00e0 }
            r5 = 8192(0x2000, float:1.14794E-41)
            byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x00de, all -> 0x00dc }
        L_0x00a5:
            int r6 = r3.read(r5)     // Catch:{ Exception -> 0x00de, all -> 0x00dc }
            r7 = -1
            if (r6 == r7) goto L_0x00b1
            r7 = 0
            r4.write(r5, r7, r6)     // Catch:{ Exception -> 0x00de, all -> 0x00dc }
            goto L_0x00a5
        L_0x00b1:
            r3.close()     // Catch:{ Exception -> 0x00de, all -> 0x00dc }
            r4.close()     // Catch:{ Exception -> 0x00da }
            boolean r1 = r1.delete()     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            if (r1 != 0) goto L_0x011f
            ch.qos.logback.core.status.WarnStatus r1 = new ch.qos.logback.core.status.WarnStatus     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r3.<init>()     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            java.lang.String r4 = "Could not delete ["
            r3.append(r4)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r3.append(r9)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r3.append(r0)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r1.<init>(r3, r8)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r8.addStatus(r1)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            goto L_0x011f
        L_0x00da:
            r1 = move-exception
            goto L_0x00ed
        L_0x00dc:
            r9 = move-exception
            goto L_0x00e2
        L_0x00de:
            r1 = move-exception
            goto L_0x00e6
        L_0x00e0:
            r9 = move-exception
            r4 = r2
        L_0x00e2:
            r2 = r3
            goto L_0x0121
        L_0x00e4:
            r1 = move-exception
            r4 = r2
        L_0x00e6:
            r2 = r3
            goto L_0x00ed
        L_0x00e8:
            r9 = move-exception
            r4 = r2
            goto L_0x0121
        L_0x00eb:
            r1 = move-exception
            r4 = r2
        L_0x00ed:
            ch.qos.logback.core.status.ErrorStatus r3 = new ch.qos.logback.core.status.ErrorStatus     // Catch:{ all -> 0x0120 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0120 }
            r5.<init>()     // Catch:{ all -> 0x0120 }
            java.lang.String r6 = "Error occurred while compressing ["
            r5.append(r6)     // Catch:{ all -> 0x0120 }
            r5.append(r9)     // Catch:{ all -> 0x0120 }
            java.lang.String r9 = "] into ["
            r5.append(r9)     // Catch:{ all -> 0x0120 }
            r5.append(r10)     // Catch:{ all -> 0x0120 }
            r5.append(r0)     // Catch:{ all -> 0x0120 }
            java.lang.String r9 = r5.toString()     // Catch:{ all -> 0x0120 }
            r3.<init>(r9, r8, r1)     // Catch:{ all -> 0x0120 }
            r8.addStatus(r3)     // Catch:{ all -> 0x0120 }
            if (r2 == 0) goto L_0x0118
            r2.close()     // Catch:{ IOException -> 0x0117 }
            goto L_0x0118
        L_0x0117:
            r9 = move-exception
        L_0x0118:
            if (r4 == 0) goto L_0x011f
            r4.close()     // Catch:{ IOException -> 0x011e }
            goto L_0x011f
        L_0x011e:
            r9 = move-exception
        L_0x011f:
            return
        L_0x0120:
            r9 = move-exception
        L_0x0121:
            if (r2 == 0) goto L_0x0128
            r2.close()     // Catch:{ IOException -> 0x0127 }
            goto L_0x0128
        L_0x0127:
            r10 = move-exception
        L_0x0128:
            if (r4 == 0) goto L_0x012f
            r4.close()     // Catch:{ IOException -> 0x012e }
            goto L_0x012f
        L_0x012e:
            r10 = move-exception
        L_0x012f:
            goto L_0x0131
        L_0x0130:
            throw r9
        L_0x0131:
            goto L_0x0130
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.rolling.helper.Compressor.gzCompress(java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x012c A[SYNTHETIC, Splitter:B:47:0x012c] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0133 A[SYNTHETIC, Splitter:B:51:0x0133] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x013c A[SYNTHETIC, Splitter:B:56:0x013c] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0143 A[SYNTHETIC, Splitter:B:60:0x0143] */
    /* JADX WARNING: Removed duplicated region for block: B:67:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zipCompress(java.lang.String r8, java.lang.String r9, java.lang.String r10) {
        /*
            r7 = this;
            java.lang.String r0 = "]."
            java.io.File r1 = new java.io.File
            r1.<init>(r8)
            boolean r2 = r1.exists()
            if (r2 != 0) goto L_0x002c
            ch.qos.logback.core.status.WarnStatus r9 = new ch.qos.logback.core.status.WarnStatus
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "The file to compress named ["
            r10.append(r0)
            r10.append(r8)
            java.lang.String r8 = "] does not exist."
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            r9.<init>(r8, r7)
            r7.addStatus(r9)
            return
        L_0x002c:
            if (r10 != 0) goto L_0x0039
            ch.qos.logback.core.status.WarnStatus r8 = new ch.qos.logback.core.status.WarnStatus
            java.lang.String r9 = "The innerEntryName parameter cannot be null"
            r8.<init>(r9, r7)
            r7.addStatus(r8)
            return
        L_0x0039:
            java.lang.String r2 = ".zip"
            boolean r3 = r9.endsWith(r2)
            if (r3 != 0) goto L_0x0050
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r9)
            r3.append(r2)
            java.lang.String r9 = r3.toString()
        L_0x0050:
            java.io.File r2 = new java.io.File
            r2.<init>(r9)
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x007a
            ch.qos.logback.core.status.WarnStatus r8 = new ch.qos.logback.core.status.WarnStatus
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "The target compressed file named ["
            r10.append(r0)
            r10.append(r9)
            java.lang.String r9 = "] exist already."
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r8.<init>(r9, r7)
            r7.addStatus(r8)
            return
        L_0x007a:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "ZIP compressing ["
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "] as ["
            r3.append(r4)
            r3.append(r2)
            java.lang.String r4 = "]"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r7.addInfo(r3)
            r7.createMissingTargetDirsIfNecessary(r2)
            r2 = 0
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            r4.<init>(r8)     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            java.util.zip.ZipOutputStream r4 = new java.util.zip.ZipOutputStream     // Catch:{ Exception -> 0x00fd, all -> 0x00f9 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00fd, all -> 0x00f9 }
            r5.<init>(r9)     // Catch:{ Exception -> 0x00fd, all -> 0x00f9 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x00fd, all -> 0x00f9 }
            java.util.zip.ZipEntry r10 = r7.computeZipEntry((java.lang.String) r10)     // Catch:{ Exception -> 0x00f7, all -> 0x00f5 }
            r4.putNextEntry(r10)     // Catch:{ Exception -> 0x00f7, all -> 0x00f5 }
            r10 = 8192(0x2000, float:1.14794E-41)
            byte[] r10 = new byte[r10]     // Catch:{ Exception -> 0x00f7, all -> 0x00f5 }
        L_0x00be:
            int r5 = r3.read(r10)     // Catch:{ Exception -> 0x00f7, all -> 0x00f5 }
            r6 = -1
            if (r5 == r6) goto L_0x00ca
            r6 = 0
            r4.write(r10, r6, r5)     // Catch:{ Exception -> 0x00f7, all -> 0x00f5 }
            goto L_0x00be
        L_0x00ca:
            r3.close()     // Catch:{ Exception -> 0x00f7, all -> 0x00f5 }
            r4.close()     // Catch:{ Exception -> 0x00f3 }
            boolean r10 = r1.delete()     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            if (r10 != 0) goto L_0x0138
            ch.qos.logback.core.status.WarnStatus r10 = new ch.qos.logback.core.status.WarnStatus     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            r1.<init>()     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            java.lang.String r3 = "Could not delete ["
            r1.append(r3)     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            r1.append(r8)     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            r1.append(r0)     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            r10.<init>(r1, r7)     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            r7.addStatus(r10)     // Catch:{ Exception -> 0x0104, all -> 0x0101 }
            goto L_0x0138
        L_0x00f3:
            r10 = move-exception
            goto L_0x0106
        L_0x00f5:
            r8 = move-exception
            goto L_0x00fb
        L_0x00f7:
            r10 = move-exception
            goto L_0x00ff
        L_0x00f9:
            r8 = move-exception
            r4 = r2
        L_0x00fb:
            r2 = r3
            goto L_0x013a
        L_0x00fd:
            r10 = move-exception
            r4 = r2
        L_0x00ff:
            r2 = r3
            goto L_0x0106
        L_0x0101:
            r8 = move-exception
            r4 = r2
            goto L_0x013a
        L_0x0104:
            r10 = move-exception
            r4 = r2
        L_0x0106:
            ch.qos.logback.core.status.ErrorStatus r1 = new ch.qos.logback.core.status.ErrorStatus     // Catch:{ all -> 0x0139 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0139 }
            r3.<init>()     // Catch:{ all -> 0x0139 }
            java.lang.String r5 = "Error occurred while compressing ["
            r3.append(r5)     // Catch:{ all -> 0x0139 }
            r3.append(r8)     // Catch:{ all -> 0x0139 }
            java.lang.String r8 = "] into ["
            r3.append(r8)     // Catch:{ all -> 0x0139 }
            r3.append(r9)     // Catch:{ all -> 0x0139 }
            r3.append(r0)     // Catch:{ all -> 0x0139 }
            java.lang.String r8 = r3.toString()     // Catch:{ all -> 0x0139 }
            r1.<init>(r8, r7, r10)     // Catch:{ all -> 0x0139 }
            r7.addStatus(r1)     // Catch:{ all -> 0x0139 }
            if (r2 == 0) goto L_0x0131
            r2.close()     // Catch:{ IOException -> 0x0130 }
            goto L_0x0131
        L_0x0130:
            r8 = move-exception
        L_0x0131:
            if (r4 == 0) goto L_0x0138
            r4.close()     // Catch:{ IOException -> 0x0137 }
            goto L_0x0138
        L_0x0137:
            r8 = move-exception
        L_0x0138:
            return
        L_0x0139:
            r8 = move-exception
        L_0x013a:
            if (r2 == 0) goto L_0x0141
            r2.close()     // Catch:{ IOException -> 0x0140 }
            goto L_0x0141
        L_0x0140:
            r9 = move-exception
        L_0x0141:
            if (r4 == 0) goto L_0x0148
            r4.close()     // Catch:{ IOException -> 0x0147 }
            goto L_0x0148
        L_0x0147:
            r9 = move-exception
        L_0x0148:
            goto L_0x014a
        L_0x0149:
            throw r8
        L_0x014a:
            goto L_0x0149
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.rolling.helper.Compressor.zipCompress(java.lang.String, java.lang.String, java.lang.String):void");
    }

    public void compress(String str, String str2, String str3) {
        int i = C05241.$SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[this.compressionMode.ordinal()];
        if (i == 1) {
            gzCompress(str, str2);
        } else if (i == 2) {
            zipCompress(str, str2, str3);
        } else if (i == 3) {
            throw new UnsupportedOperationException("compress method called in NONE compression mode");
        }
    }

    /* access modifiers changed from: package-private */
    public ZipEntry computeZipEntry(File file) {
        return computeZipEntry(file.getName());
    }

    /* access modifiers changed from: package-private */
    public ZipEntry computeZipEntry(String str) {
        return new ZipEntry(computeFileNameStr_WCS(str, this.compressionMode));
    }

    /* access modifiers changed from: package-private */
    public void createMissingTargetDirsIfNecessary(File file) {
        if (!FileUtil.isParentDirectoryCreationRequired(file)) {
            return;
        }
        if (!FileUtil.createMissingParentDirectories(file)) {
            addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
            return;
        }
        addInfo("Created missing parent directories for [" + file.getAbsolutePath() + "]");
    }

    public String toString() {
        return getClass().getName();
    }
}
