package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

public class PebbleInstallable {
    private final int crc;
    private final String fileName;
    private final int fileSize;
    private final byte type;

    public PebbleInstallable(String fileName2, int fileSize2, int crc2, byte type2) {
        this.fileName = fileName2;
        this.fileSize = fileSize2;
        this.crc = crc2;
        this.type = type2;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public byte getType() {
        return this.type;
    }

    public int getCRC() {
        return this.crc;
    }
}
