package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* compiled from: MicroAppCommand */
class AnimationCommand implements MicroAppCommand {
    private byte absoluteMovementFlag = 1;
    private MovingDirection direction = MovingDirection.SHORTEST;
    private short hour;
    private short minute;
    private MovingSpeed speed = MovingSpeed.MAX;

    public AnimationCommand(short hour2, short minute2) {
        this.hour = hour2;
        this.minute = minute2;
    }

    public byte[] getData() {
        return ByteBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN).put((byte) 9).put((byte) 4).put((byte) 1).put((byte) 3).put((byte) ((this.direction.getValue() << 6) | ((byte) (this.absoluteMovementFlag << 5)) | this.speed.getValue())).putShort(this.hour).put((byte) ((this.direction.getValue() << 6) | ((byte) (this.absoluteMovementFlag << 5)) | this.speed.getValue())).putShort(this.minute).array();
    }
}
