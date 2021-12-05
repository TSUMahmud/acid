package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class PlayNotificationRequest extends Request {

    public enum VibrationType {
        SINGLE_SHORT(3),
        DOUBLE_SHORT(2),
        TRIPLE_SHORT(1),
        SINGLE_NORMAL(5),
        DOUBLE_NORMAL(6),
        TRIPLE_NORMAL(7),
        SINGLE_LONG(8),
        NO_VIBE(9);
        
        private byte value;

        private VibrationType(int value2) {
            this.value = (byte) value2;
        }

        public static VibrationType fromValue(byte value2) {
            for (VibrationType type : values()) {
                if (type.getValue() == value2) {
                    return type;
                }
            }
            throw new InvalidParameterException("vibration Type not supported");
        }

        public byte getValue() {
            return this.value;
        }
    }

    public PlayNotificationRequest(VibrationType vibrationType, int degreesHour, int degreesMins, int degreesActivityHand) {
        int length = degreesHour > -1 ? 0 + 1 : 0;
        length = degreesMins > -1 ? length + 1 : length;
        length = degreesActivityHand > -1 ? length + 1 : length;
        ByteBuffer buffer = createBuffer((length * 2) + 10);
        buffer.put(vibrationType.getValue());
        buffer.put((byte) 5);
        buffer.put((byte) ((length * 2) + 2));
        buffer.putShort(0);
        if (degreesHour > -1) {
            buffer.putShort((short) ((degreesHour % 360) | 4096));
        }
        if (degreesMins > -1) {
            buffer.putShort((short) ((degreesMins % 360) | 8192));
        }
        if (degreesActivityHand > -1) {
            buffer.putShort((short) ((degreesActivityHand % 360) | 12288));
        }
        this.data = buffer.array();
    }

    public PlayNotificationRequest(VibrationType vibrationType, int degreesHour, int degreesMins) {
        this(vibrationType, degreesHour, degreesMins, -1);
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 7, 15, 10, 1};
    }
}
