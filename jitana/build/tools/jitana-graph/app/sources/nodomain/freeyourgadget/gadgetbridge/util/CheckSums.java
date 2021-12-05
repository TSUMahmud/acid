package nodomain.freeyourgadget.gadgetbridge.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

public class CheckSums {
    public static int getCRC8(byte[] seq) {
        int i = seq.length;
        int i2 = 0;
        byte crc = 0;
        while (true) {
            int len = i - 1;
            if (i <= 0) {
                return crc & 255;
            }
            int i3 = i2 + 1;
            byte extract = seq[i2];
            for (byte tempI = 8; tempI != 0; tempI = (byte) (tempI - 1)) {
                byte sum = (byte) (((byte) ((crc & 255) ^ (extract & 255))) & 255 & 1);
                crc = (byte) ((crc & 255) >>> 1);
                if (sum != 0) {
                    crc = (byte) ((crc & 255) ^ 140);
                }
                extract = (byte) ((extract & 255) >>> 1);
            }
            i2 = i3;
            i = len;
        }
    }

    public static int getCRC16(byte[] seq) {
        int crc = 65535;
        for (byte b : seq) {
            int crc2 = (((crc >>> 8) | (crc << 8)) & 65535) ^ (b & 255);
            int crc3 = crc2 ^ ((crc2 & 255) >> 4);
            int crc4 = crc3 ^ ((crc3 << 12) & 65535);
            crc = crc4 ^ (65535 & ((crc4 & 255) << 5));
        }
        return crc & 65535;
    }

    public static int getCRC32(byte[] seq) {
        CRC32 crc = new CRC32();
        crc.update(seq);
        return (int) crc.getValue();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003c, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0041, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0042, code lost:
        r0.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0045, code lost:
        throw r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void main(java.lang.String[] r8) throws java.io.IOException {
        /*
            if (r8 == 0) goto L_0x0047
            int r0 = r8.length
            if (r0 == 0) goto L_0x0047
            int r0 = r8.length
            r1 = 0
        L_0x0007:
            if (r1 >= r0) goto L_0x0046
            r2 = r8[r1]
            java.io.FileInputStream r3 = new java.io.FileInputStream
            r3.<init>(r2)
            r4 = 1000000(0xf4240, double:4.940656E-318)
            byte[] r4 = readAll(r3, r4)     // Catch:{ all -> 0x003a }
            java.io.PrintStream r5 = java.lang.System.out     // Catch:{ all -> 0x003a }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x003a }
            r6.<init>()     // Catch:{ all -> 0x003a }
            r6.append(r2)     // Catch:{ all -> 0x003a }
            java.lang.String r7 = " : "
            r6.append(r7)     // Catch:{ all -> 0x003a }
            int r7 = getCRC16(r4)     // Catch:{ all -> 0x003a }
            r6.append(r7)     // Catch:{ all -> 0x003a }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x003a }
            r5.println(r6)     // Catch:{ all -> 0x003a }
            r3.close()
            int r1 = r1 + 1
            goto L_0x0007
        L_0x003a:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x003c }
        L_0x003c:
            r1 = move-exception
            r3.close()     // Catch:{ all -> 0x0041 }
            goto L_0x0045
        L_0x0041:
            r4 = move-exception
            r0.addSuppressed(r4)
        L_0x0045:
            throw r1
        L_0x0046:
            return
        L_0x0047:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Pass the files to be checksummed as arguments"
            r0.<init>(r1)
            goto L_0x0050
        L_0x004f:
            throw r0
        L_0x0050:
            goto L_0x004f
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.CheckSums.main(java.lang.String[]):void");
    }

    private static byte[] readAll(InputStream in, long maxLen) throws IOException {
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
}
