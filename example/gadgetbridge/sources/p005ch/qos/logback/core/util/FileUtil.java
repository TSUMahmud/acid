package p005ch.qos.logback.core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.util.FileUtil */
public class FileUtil extends ContextAwareBase {
    static final int BUF_SIZE = 32768;

    public FileUtil(Context context) {
        setContext(context);
    }

    public static boolean createMissingParentDirectories(File file) {
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            throw new IllegalStateException(file + " should not have a null parent");
        } else if (!parentFile.exists()) {
            return parentFile.mkdirs();
        } else {
            throw new IllegalStateException(file + " should not have existing parent directory");
        }
    }

    public static URL fileToURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unexpected exception on file [" + file + "]", e);
        }
    }

    public static boolean isParentDirectoryCreationRequired(File file) {
        File parentFile = file.getParentFile();
        return parentFile != null && !parentFile.exists();
    }

    public static String prefixRelativePath(String str, String str2) {
        if (str == null || OptionHelper.isEmpty(str.trim()) || new File(str2).isAbsolute()) {
            return str2;
        }
        return str + "/" + str2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x006f A[SYNTHETIC, Splitter:B:33:0x006f] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0076 A[SYNTHETIC, Splitter:B:37:0x0076] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void copy(java.lang.String r8, java.lang.String r9) throws p005ch.qos.logback.core.rolling.RolloverFailure {
        /*
            r7 = this;
            r0 = 0
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0043, all -> 0x0040 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0043, all -> 0x0040 }
            r2.<init>(r8)     // Catch:{ IOException -> 0x0043, all -> 0x0040 }
            r1.<init>(r2)     // Catch:{ IOException -> 0x0043, all -> 0x0040 }
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x003a, all -> 0x0036 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x003a, all -> 0x0036 }
            r3.<init>(r9)     // Catch:{ IOException -> 0x003a, all -> 0x0036 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x003a, all -> 0x0036 }
            r3 = 32768(0x8000, float:4.5918E-41)
            byte[] r3 = new byte[r3]     // Catch:{ IOException -> 0x0031, all -> 0x002f }
        L_0x001a:
            int r4 = r1.read(r3)     // Catch:{ IOException -> 0x0031, all -> 0x002f }
            r5 = -1
            if (r4 == r5) goto L_0x0026
            r5 = 0
            r2.write(r3, r5, r4)     // Catch:{ IOException -> 0x0031, all -> 0x002f }
            goto L_0x001a
        L_0x0026:
            r1.close()     // Catch:{ IOException -> 0x0031, all -> 0x002f }
            r2.close()     // Catch:{ IOException -> 0x002d }
            return
        L_0x002d:
            r1 = move-exception
            goto L_0x0045
        L_0x002f:
            r8 = move-exception
            goto L_0x0038
        L_0x0031:
            r0 = move-exception
            r6 = r1
            r1 = r0
            r0 = r6
            goto L_0x0045
        L_0x0036:
            r8 = move-exception
            r2 = r0
        L_0x0038:
            r0 = r1
            goto L_0x006d
        L_0x003a:
            r2 = move-exception
            r6 = r2
            r2 = r0
            r0 = r1
            r1 = r6
            goto L_0x0045
        L_0x0040:
            r8 = move-exception
            r2 = r0
            goto L_0x006d
        L_0x0043:
            r1 = move-exception
            r2 = r0
        L_0x0045:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x006c }
            r3.<init>()     // Catch:{ all -> 0x006c }
            java.lang.String r4 = "Failed to copy ["
            r3.append(r4)     // Catch:{ all -> 0x006c }
            r3.append(r8)     // Catch:{ all -> 0x006c }
            java.lang.String r8 = "] to ["
            r3.append(r8)     // Catch:{ all -> 0x006c }
            r3.append(r9)     // Catch:{ all -> 0x006c }
            java.lang.String r8 = "]"
            r3.append(r8)     // Catch:{ all -> 0x006c }
            java.lang.String r8 = r3.toString()     // Catch:{ all -> 0x006c }
            r7.addError(r8, r1)     // Catch:{ all -> 0x006c }
            ch.qos.logback.core.rolling.RolloverFailure r9 = new ch.qos.logback.core.rolling.RolloverFailure     // Catch:{ all -> 0x006c }
            r9.<init>(r8)     // Catch:{ all -> 0x006c }
            throw r9     // Catch:{ all -> 0x006c }
        L_0x006c:
            r8 = move-exception
        L_0x006d:
            if (r0 == 0) goto L_0x0074
            r0.close()     // Catch:{ IOException -> 0x0073 }
            goto L_0x0074
        L_0x0073:
            r9 = move-exception
        L_0x0074:
            if (r2 == 0) goto L_0x007b
            r2.close()     // Catch:{ IOException -> 0x007a }
            goto L_0x007b
        L_0x007a:
            r9 = move-exception
        L_0x007b:
            goto L_0x007d
        L_0x007c:
            throw r8
        L_0x007d:
            goto L_0x007c
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.util.FileUtil.copy(java.lang.String, java.lang.String):void");
    }
}
