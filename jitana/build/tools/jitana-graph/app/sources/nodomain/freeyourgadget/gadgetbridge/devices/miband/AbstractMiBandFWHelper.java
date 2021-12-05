package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMiBandFWHelper {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractMiBandFWHelper.class);

    /* renamed from: fw */
    private final byte[] f129fw;

    public abstract void checkValid() throws IllegalArgumentException;

    /* access modifiers changed from: protected */
    public abstract void determineFirmwareInfo(byte[] bArr);

    public abstract int getFirmware2Version();

    public abstract String getFirmwareKind();

    public abstract int getFirmwareVersion();

    public abstract String getHumanFirmwareVersion2();

    /* access modifiers changed from: protected */
    public abstract int[] getWhitelistedFirmwareVersions();

    public abstract boolean isFirmwareGenerallyCompatibleWith(GBDevice gBDevice);

    public abstract boolean isSingleFirmware();

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0031, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003a, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AbstractMiBandFWHelper(android.net.Uri r7, android.content.Context r8) throws java.io.IOException {
        /*
            r6 = this;
            r6.<init>()
            nodomain.freeyourgadget.gadgetbridge.util.UriHelper r0 = nodomain.freeyourgadget.gadgetbridge.util.UriHelper.get(r7, r8)
            java.lang.String r1 = ".*\\.(pbw|pbz|pbl)"
            java.lang.String r2 = r0.getFileName()
            boolean r2 = r2.matches(r1)
            if (r2 != 0) goto L_0x0075
            java.io.BufferedInputStream r2 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0073, IllegalArgumentException -> 0x0057, Exception -> 0x003b }
            java.io.InputStream r3 = r0.openInputStream()     // Catch:{ IOException -> 0x0073, IllegalArgumentException -> 0x0057, Exception -> 0x003b }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0073, IllegalArgumentException -> 0x0057, Exception -> 0x003b }
            r3 = 2097152(0x200000, double:1.0361308E-317)
            byte[] r3 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.readAll(r2, r3)     // Catch:{ all -> 0x002f }
            r6.f129fw = r3     // Catch:{ all -> 0x002f }
            byte[] r3 = r6.f129fw     // Catch:{ all -> 0x002f }
            r6.determineFirmwareInfo(r3)     // Catch:{ all -> 0x002f }
            r2.close()     // Catch:{ IOException -> 0x0073, IllegalArgumentException -> 0x0057, Exception -> 0x003b }
            return
        L_0x002f:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0031 }
        L_0x0031:
            r4 = move-exception
            r2.close()     // Catch:{ all -> 0x0036 }
            goto L_0x003a
        L_0x0036:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ IOException -> 0x0073, IllegalArgumentException -> 0x0057, Exception -> 0x003b }
        L_0x003a:
            throw r4     // Catch:{ IOException -> 0x0073, IllegalArgumentException -> 0x0057, Exception -> 0x003b }
        L_0x003b:
            r2 = move-exception
            java.io.IOException r3 = new java.io.IOException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Error reading firmware file: "
            r4.append(r5)
            java.lang.String r5 = r7.toString()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4, r2)
            throw r3
        L_0x0057:
            r2 = move-exception
            java.io.IOException r3 = new java.io.IOException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "This doesn't seem to be a Mi Band firmware: "
            r4.append(r5)
            java.lang.String r5 = r2.getLocalizedMessage()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4, r2)
            throw r3
        L_0x0073:
            r2 = move-exception
            throw r2
        L_0x0075:
            java.io.IOException r2 = new java.io.IOException
            java.lang.String r3 = "Firmware has a filename that looks like a Pebble app/firmware."
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWHelper.<init>(android.net.Uri, android.content.Context):void");
    }

    public static String formatFirmwareVersion(int version) {
        if (version == -1) {
            return GBApplication.getContext().getString(C0889R.string._unknown_);
        }
        return String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf((version >> 24) & 255), Integer.valueOf((version >> 16) & 255), Integer.valueOf((version >> 8) & 255), Integer.valueOf(version & 255)});
    }

    public String getHumanFirmwareVersion() {
        return format(getFirmwareVersion());
    }

    public String format(int version) {
        return formatFirmwareVersion(version);
    }

    public byte[] getFw() {
        return this.f129fw;
    }

    public boolean isFirmwareWhitelisted() {
        for (int wlf : getWhitelistedFirmwareVersions()) {
            if (wlf == getFirmwareVersion()) {
                return true;
            }
        }
        return false;
    }
}
