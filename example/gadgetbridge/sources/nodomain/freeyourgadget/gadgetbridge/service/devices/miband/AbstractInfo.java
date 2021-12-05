package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

public class AbstractInfo {
    protected final byte[] mData;

    public AbstractInfo(byte[] data) {
        this.mData = new byte[data.length];
        System.arraycopy(data, 0, this.mData, 0, data.length);
    }
}
