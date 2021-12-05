package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.notification;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.zip.CRC32;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file.FilePutRequest;
import org.apache.commons.lang3.CharEncoding;

public class PlayNotificationRequest extends FilePutRequest {
    public PlayNotificationRequest(String packageName, FossilWatchAdapter adapter) {
        super(2304, createFile(packageName), adapter);
    }

    private static byte[] createFile(String packageName) {
        CRC32 crc = new CRC32();
        crc.update(packageName.getBytes());
        return createFile(packageName, packageName, packageName, (int) crc.getValue());
    }

    private static byte[] createFile(String title, String sender, String message, int packageCrc) {
        byte flags = getFlags();
        String nullTerminatedTitle = terminateNull(title);
        Charset charsetUTF8 = Charset.forName(CharEncoding.UTF_8);
        byte[] titleBytes = nullTerminatedTitle.getBytes(charsetUTF8);
        byte[] senderBytes = terminateNull(sender).getBytes(charsetUTF8);
        byte[] messageBytes = terminateNull(message).getBytes(charsetUTF8);
        short mainBufferLength = (short) (10 + 4 + 4 + titleBytes.length + senderBytes.length + messageBytes.length);
        ByteBuffer lengthBuffer = ByteBuffer.allocate(10);
        lengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
        lengthBuffer.putShort(mainBufferLength);
        lengthBuffer.put((byte) 10);
        lengthBuffer.put((byte) 3);
        lengthBuffer.put(flags);
        lengthBuffer.put((byte) 4);
        lengthBuffer.put((byte) 4);
        lengthBuffer.put((byte) titleBytes.length);
        lengthBuffer.put((byte) senderBytes.length);
        lengthBuffer.put((byte) messageBytes.length);
        ByteBuffer mainBuffer = ByteBuffer.allocate(mainBufferLength);
        mainBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mainBuffer.put(lengthBuffer.array());
        ByteBuffer lengthBuffer2 = ByteBuffer.allocate(mainBufferLength - 10);
        lengthBuffer2.order(ByteOrder.LITTLE_ENDIAN);
        lengthBuffer2.putInt(0);
        lengthBuffer2.putInt(packageCrc);
        lengthBuffer2.put(titleBytes);
        lengthBuffer2.put(senderBytes);
        lengthBuffer2.put(messageBytes);
        mainBuffer.put(lengthBuffer2.array());
        return mainBuffer.array();
    }

    private static byte getFlags() {
        return 2;
    }

    public static String terminateNull(String input) {
        if (input.length() == 0) {
            return new String(new byte[]{0});
        } else if (input.charAt(input.length() - 1) == 0) {
            return input;
        } else {
            byte[] newArray = new byte[(input.length() + 1)];
            System.arraycopy(input.getBytes(), 0, newArray, 0, input.length());
            newArray[newArray.length - 1] = 0;
            return new String(newArray);
        }
    }
}
