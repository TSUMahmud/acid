package nodomain.freeyourgadget.gadgetbridge.service.devices.liveview;

import java.nio.ByteBuffer;
import java.util.Calendar;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.devices.liveview.LiveviewConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;

public class LiveviewProtocol extends GBDeviceProtocol {
    public byte[] encodeFindDevice(boolean start) {
        return encodeVibrateRequest(100, 200);
    }

    protected LiveviewProtocol(GBDevice device) {
        super(device);
    }

    public GBDeviceEvent[] decodeResponse(byte[] responseData) {
        int length = responseData.length;
        if (length < 4) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(responseData, 0, length);
        byte msgId = buffer.get();
        buffer.get();
        int payloadLen = buffer.getInt();
        GBDeviceEventSendBytes reply = new GBDeviceEventSendBytes();
        if (payloadLen + 6 != length) {
            return super.decodeResponse(responseData);
        }
        if (msgId == 6) {
            reply.encodedBytes = encodeVibrateRequest(100, 200);
        } else if (msgId == 7) {
            reply.encodedBytes = constructMessage((byte) 8, new byte[]{0});
        }
        GBDeviceEventSendBytes ack = new GBDeviceEventSendBytes();
        ack.encodedBytes = constructMessage((byte) 44, new byte[]{msgId});
        return new GBDeviceEvent[]{ack, reply};
    }

    public byte[] encodeSetTime() {
        int time = ((int) (Calendar.getInstance().getTimeInMillis() / 1000)) + (Calendar.getInstance().get(15) / 1000) + (Calendar.getInstance().get(16) / 1000);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.order(LiveviewConstants.BYTE_ORDER);
        buffer.putInt(time);
        buffer.put((byte) 0);
        return constructMessage((byte) 39, buffer.array());
    }

    public byte[] encodeNotification(NotificationSpec notificationSpec) {
        String headerText;
        if (notificationSpec.sender != null) {
            headerText = notificationSpec.sender;
        } else {
            headerText = notificationSpec.title;
        }
        String bodyText = "";
        String footerText = notificationSpec.sourceName != null ? notificationSpec.sourceName : bodyText;
        if (notificationSpec.body != null) {
            bodyText = notificationSpec.body;
        }
        byte[] headerTextArray = headerText.getBytes(LiveviewConstants.ENCODING);
        byte[] footerTextArray = footerText.getBytes(LiveviewConstants.ENCODING);
        byte[] bodyTextArray = bodyText.getBytes(LiveviewConstants.ENCODING);
        ByteBuffer buffer = ByteBuffer.allocate(headerTextArray.length + 15 + bodyTextArray.length + footerTextArray.length);
        buffer.put((byte) 1);
        buffer.putShort(0);
        buffer.putShort(0);
        buffer.putShort(0);
        buffer.put((byte) 80);
        buffer.put((byte) 0);
        buffer.putShort((short) headerTextArray.length);
        buffer.put(headerTextArray);
        buffer.putShort((short) bodyTextArray.length);
        buffer.put(bodyTextArray);
        buffer.putShort((short) footerTextArray.length);
        buffer.put(footerTextArray);
        return constructMessage((byte) 5, buffer.array());
    }

    public static byte[] constructMessage(byte messageType, byte[] payload) {
        ByteBuffer msgBuffer = ByteBuffer.allocate(payload.length + 6);
        msgBuffer.order(LiveviewConstants.BYTE_ORDER);
        msgBuffer.put(messageType);
        msgBuffer.put((byte) 4);
        msgBuffer.putInt(payload.length);
        msgBuffer.put(payload);
        return msgBuffer.array();
    }

    public byte[] encodeVibrateRequest(short delay, short time) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(LiveviewConstants.BYTE_ORDER);
        buffer.putShort(delay);
        buffer.putShort(time);
        return constructMessage((byte) 42, buffer.array());
    }

    public byte[] encodeCapabilitiesRequest() {
        byte[] version = LiveviewConstants.CLIENT_SOFTWARE_VERSION.getBytes(LiveviewConstants.ENCODING);
        ByteBuffer buffer = ByteBuffer.allocate(version.length + 1);
        buffer.order(LiveviewConstants.BYTE_ORDER);
        buffer.put((byte) version.length);
        buffer.put(version);
        return constructMessage((byte) 1, buffer.array());
    }
}
