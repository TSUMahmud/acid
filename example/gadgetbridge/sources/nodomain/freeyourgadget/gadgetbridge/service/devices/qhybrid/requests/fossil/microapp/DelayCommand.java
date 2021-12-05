package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* compiled from: MicroAppCommand */
class DelayCommand implements MicroAppCommand {
    private double delayInSeconds;

    public DelayCommand(double delayInSeconds2) {
        this.delayInSeconds = delayInSeconds2;
    }

    public byte[] getData() {
        return ByteBuffer.wrap(new byte[]{8, 1, 0, 0}).order(ByteOrder.LITTLE_ENDIAN).putShort(2, (short) ((int) (this.delayInSeconds * 10.0d))).array();
    }
}
