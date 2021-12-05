package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.Context;
import android.os.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBEnvironment;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001c, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0021, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0025, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0028, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x002d, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x002e, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0031, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyFile(java.io.File r5, java.io.File r6) throws java.io.IOException {
        /*
            boolean r0 = r5.exists()
            if (r0 == 0) goto L_0x0032
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r5)
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0026 }
            r1.<init>(r6)     // Catch:{ all -> 0x0026 }
            copyFile((java.io.FileInputStream) r0, (java.io.FileOutputStream) r1)     // Catch:{ all -> 0x001a }
            r1.close()     // Catch:{ all -> 0x0026 }
            r0.close()
            return
        L_0x001a:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x001c }
        L_0x001c:
            r3 = move-exception
            r1.close()     // Catch:{ all -> 0x0021 }
            goto L_0x0025
        L_0x0021:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ all -> 0x0026 }
        L_0x0025:
            throw r3     // Catch:{ all -> 0x0026 }
        L_0x0026:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0028 }
        L_0x0028:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x002d }
            goto L_0x0031
        L_0x002d:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0031:
            throw r2
        L_0x0032:
            java.io.IOException r0 = new java.io.IOException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Does not exist: "
            r1.append(r2)
            java.lang.String r2 = r5.getAbsolutePath()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.FileUtils.copyFile(java.io.File, java.io.File):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0021, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0022, code lost:
        if (r7 != null) goto L_0x0024;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0028, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r0.addSuppressed(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002c, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x002f, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0030, code lost:
        if (r6 != null) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0036, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0037, code lost:
        r0.addSuppressed(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x003a, code lost:
        throw r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void copyFile(java.io.FileInputStream r8, java.io.FileOutputStream r9) throws java.io.IOException {
        /*
            java.nio.channels.FileChannel r6 = r8.getChannel()
            java.nio.channels.FileChannel r0 = r9.getChannel()     // Catch:{ all -> 0x002d }
            r7 = r0
            r1 = 0
            long r3 = r6.size()     // Catch:{ all -> 0x001f }
            r0 = r6
            r5 = r7
            r0.transferTo(r1, r3, r5)     // Catch:{ all -> 0x001f }
            if (r7 == 0) goto L_0x0019
            r7.close()     // Catch:{ all -> 0x002d }
        L_0x0019:
            if (r6 == 0) goto L_0x001e
            r6.close()
        L_0x001e:
            return
        L_0x001f:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0021 }
        L_0x0021:
            r1 = move-exception
            if (r7 == 0) goto L_0x002c
            r7.close()     // Catch:{ all -> 0x0028 }
            goto L_0x002c
        L_0x0028:
            r2 = move-exception
            r0.addSuppressed(r2)     // Catch:{ all -> 0x002d }
        L_0x002c:
            throw r1     // Catch:{ all -> 0x002d }
        L_0x002d:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x002f }
        L_0x002f:
            r1 = move-exception
            if (r6 == 0) goto L_0x003a
            r6.close()     // Catch:{ all -> 0x0036 }
            goto L_0x003a
        L_0x0036:
            r2 = move-exception
            r0.addSuppressed(r2)
        L_0x003a:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.FileUtils.copyFile(java.io.FileInputStream, java.io.FileOutputStream):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0023, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0024, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyStreamToFile(java.io.InputStream r4, java.io.File r5) throws java.io.IOException {
        /*
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r0.<init>(r5)
            r1 = 4096(0x1000, float:5.74E-42)
            byte[] r1 = new byte[r1]     // Catch:{ all -> 0x001c }
        L_0x0009:
            int r2 = r4.available()     // Catch:{ all -> 0x001c }
            if (r2 <= 0) goto L_0x0018
            int r2 = r4.read(r1)     // Catch:{ all -> 0x001c }
            r3 = 0
            r0.write(r1, r3, r2)     // Catch:{ all -> 0x001c }
            goto L_0x0009
        L_0x0018:
            r0.close()
            return
        L_0x001c:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001e }
        L_0x001e:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0023 }
            goto L_0x0027
        L_0x0023:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0027:
            goto L_0x0029
        L_0x0028:
            throw r2
        L_0x0029:
            goto L_0x0028
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.FileUtils.copyStreamToFile(java.io.InputStream, java.io.File):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0023, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0024, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyFileToStream(java.io.File r4, java.io.OutputStream r5) throws java.io.IOException {
        /*
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r4)
            r1 = 4096(0x1000, float:5.74E-42)
            byte[] r1 = new byte[r1]     // Catch:{ all -> 0x001c }
        L_0x0009:
            int r2 = r0.available()     // Catch:{ all -> 0x001c }
            if (r2 <= 0) goto L_0x0018
            int r2 = r0.read(r1)     // Catch:{ all -> 0x001c }
            r3 = 0
            r5.write(r1, r3, r2)     // Catch:{ all -> 0x001c }
            goto L_0x0009
        L_0x0018:
            r0.close()
            return
        L_0x001c:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001e }
        L_0x001e:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0023 }
            goto L_0x0027
        L_0x0023:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0027:
            goto L_0x0029
        L_0x0028:
            throw r2
        L_0x0029:
            goto L_0x0028
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.FileUtils.copyFileToStream(java.io.File, java.io.OutputStream):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002a, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002f, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0030, code lost:
        r3.addSuppressed(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyURItoFile(android.content.Context r6, android.net.Uri r7, java.io.File r8) throws java.io.IOException {
        /*
            java.lang.String r0 = r7.getPath()
            java.lang.String r1 = r8.getPath()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000f
            return
        L_0x000f:
            android.content.ContentResolver r0 = r6.getContentResolver()
            java.io.InputStream r1 = r0.openInputStream(r7)
            if (r1 == 0) goto L_0x0034
            java.io.BufferedInputStream r2 = new java.io.BufferedInputStream
            r2.<init>(r1)
            copyStreamToFile(r2, r8)     // Catch:{ all -> 0x0028 }
            r2.close()     // Catch:{ all -> 0x0028 }
            r2.close()
            return
        L_0x0028:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002a }
        L_0x002a:
            r4 = move-exception
            r2.close()     // Catch:{ all -> 0x002f }
            goto L_0x0033
        L_0x002f:
            r5 = move-exception
            r3.addSuppressed(r5)
        L_0x0033:
            throw r4
        L_0x0034:
            java.io.IOException r2 = new java.io.IOException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "unable to open input stream: "
            r3.append(r4)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.FileUtils.copyURItoFile(android.content.Context, android.net.Uri, java.io.File):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0018, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001e, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0021, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyFileToURI(android.content.Context r5, java.io.File r6, android.net.Uri r7) throws java.io.IOException {
        /*
            android.content.ContentResolver r0 = r5.getContentResolver()
            java.io.OutputStream r0 = r0.openOutputStream(r7)
            if (r0 == 0) goto L_0x0022
            java.io.BufferedOutputStream r1 = new java.io.BufferedOutputStream
            r1.<init>(r0)
            copyFileToStream(r6, r1)     // Catch:{ all -> 0x0016 }
            r1.close()
            return
        L_0x0016:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0018 }
        L_0x0018:
            r3 = move-exception
            r1.close()     // Catch:{ all -> 0x001d }
            goto L_0x0021
        L_0x001d:
            r4 = move-exception
            r2.addSuppressed(r4)
        L_0x0021:
            throw r3
        L_0x0022:
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unable to open output stream for "
            r2.append(r3)
            java.lang.String r3 = r7.toString()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.FileUtils.copyFileToURI(android.content.Context, java.io.File, android.net.Uri):void");
    }

    public static String getStringFromFile(File file) throws IOException {
        return getStringFromFile(file, StandardCharsets.UTF_8.name());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0033, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0034, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0038, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getStringFromFile(java.io.File r5, java.lang.String r6) throws java.io.IOException {
        /*
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r5)
            java.io.BufferedReader r1 = new java.io.BufferedReader
            java.io.InputStreamReader r2 = new java.io.InputStreamReader
            r2.<init>(r0, r6)
            r1.<init>(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x002c }
            r2.<init>()     // Catch:{ all -> 0x002c }
        L_0x0014:
            java.lang.String r3 = r1.readLine()     // Catch:{ all -> 0x002c }
            r4 = r3
            if (r3 == 0) goto L_0x0024
            r2.append(r4)     // Catch:{ all -> 0x002c }
            java.lang.String r3 = "\n"
            r2.append(r3)     // Catch:{ all -> 0x002c }
            goto L_0x0014
        L_0x0024:
            java.lang.String r3 = r2.toString()     // Catch:{ all -> 0x002c }
            r1.close()
            return r3
        L_0x002c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x002e }
        L_0x002e:
            r3 = move-exception
            r1.close()     // Catch:{ all -> 0x0033 }
            goto L_0x0037
        L_0x0033:
            r4 = move-exception
            r2.addSuppressed(r4)
        L_0x0037:
            goto L_0x0039
        L_0x0038:
            throw r3
        L_0x0039:
            goto L_0x0038
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getStringFromFile(java.io.File, java.lang.String):java.lang.String");
    }

    public static File getExternalFilesDir() throws IOException {
        for (File dir : getWritableExternalFilesDirs()) {
            if (canWriteTo(dir)) {
                return dir;
            }
        }
        throw new IOException("no writable external directory found");
    }

    private static boolean canWriteTo(File dir) {
        File file = new File(dir, "gbtest");
        try {
            try {
                new FileOutputStream(file).close();
            } catch (IOException e) {
            }
            file.delete();
            return true;
        } catch (FileNotFoundException e2) {
            C1238GB.log("Cannot write to directory: " + dir.getAbsolutePath(), 1, e2);
            return false;
        }
    }

    private static List<File> getWritableExternalFilesDirs() throws IOException {
        File[] dirs;
        Context context = GBApplication.getContext();
        try {
            dirs = context.getExternalFilesDirs((String) null);
        } catch (NullPointerException | UnsupportedOperationException ex) {
            File dir = context.getExternalFilesDir((String) null);
            if (dir != null) {
                dirs = new File[]{dir};
            } else {
                throw ex;
            }
        }
        if (dirs != null) {
            List<File> result = new ArrayList<>(dirs.length);
            if (dirs.length != 0) {
                for (int i = 0; i < dirs.length; i++) {
                    File dir2 = dirs[i];
                    if (dir2 != null) {
                        if (!dir2.exists() && !dir2.mkdirs()) {
                            C1238GB.log("Unable to create directories: " + dir2.getAbsolutePath(), 1, (Throwable) null);
                        } else if (GBEnvironment.env().isLocalTest() || i != 0 || "mounted".equals(Environment.getExternalStorageState())) {
                            result.add(dir2);
                        } else {
                            C1238GB.log("ignoring unmounted external storage dir: " + dir2, 1, (Throwable) null);
                        }
                    }
                }
                return result;
            }
            throw new IOException("Unable to access external files dirs: 0");
        }
        throw new IOException("Unable to access external files dirs: null");
    }

    public static byte[] readAll(InputStream in, long maxLen) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(8192, in.available()));
        byte[] buf = new byte[8192];
        long totalRead = 0;
        do {
            int read = in.read(buf);
            int read2 = read;
            if (read <= 0) {
                return out.toByteArray();
            }
            out.write(buf, 0, read2);
            totalRead += (long) read2;
        } while (totalRead <= maxLen);
        throw new IOException("Too much data to read into memory. Got already " + totalRead);
    }

    public static boolean deleteRecursively(File dir) {
        if (!dir.exists()) {
            return true;
        }
        if (dir.isFile()) {
            return dir.delete();
        }
        for (File sub : dir.listFiles()) {
            if (!deleteRecursively(sub)) {
                return false;
            }
        }
        return dir.delete();
    }

    public static File createTempDir(String prefix) throws IOException {
        File parent = new File(System.getProperty("java.io.tmpdir", "/tmp"));
        for (int i = 1; i < 100; i++) {
            File dir = new File(parent, prefix + ((int) (Math.random() * 100000.0d)));
            if (dir.mkdirs()) {
                return dir;
            }
        }
        throw new IOException("Cannot create temporary directory in " + parent);
    }

    public static String makeValidFileName(String name) {
        return name.replaceAll("[\u0000/:\\r\\n\\\\]", "_");
    }
}
