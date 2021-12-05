package nodomain.freeyourgadget.gadgetbridge.service.devices.id115;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import nodomain.freeyourgadget.gadgetbridge.devices.id115.ID115Constants;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendNotificationOperation extends AbstractID115Operation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SendNotificationOperation.class);
    byte[] currentNotificationBuffer;
    int currentNotificationIndex;
    int currentNotificationSize;
    byte currentNotificationType;

    SendNotificationOperation(ID115Support support, NotificationSpec notificationSpec) {
        super(support);
        String phone = notificationSpec.phoneNumber != null ? notificationSpec.phoneNumber : "";
        String title = "";
        if (notificationSpec.sender != null) {
            title = notificationSpec.sender;
        } else if (notificationSpec.title != null) {
            title = notificationSpec.title;
        } else if (notificationSpec.subject != null) {
            title = notificationSpec.subject;
        }
        this.currentNotificationBuffer = encodeMessageNotification(notificationSpec.type, title, phone, notificationSpec.body != null ? notificationSpec.body : "");
        this.currentNotificationSize = (this.currentNotificationBuffer.length + 15) / 16;
        this.currentNotificationType = 3;
    }

    SendNotificationOperation(ID115Support support, CallSpec callSpec) {
        super(support);
        this.currentNotificationBuffer = encodeCallNotification(callSpec.name != null ? callSpec.name : "", callSpec.number != null ? callSpec.number : "");
        this.currentNotificationSize = (this.currentNotificationBuffer.length + 15) / 16;
        this.currentNotificationType = 1;
    }

    /* access modifiers changed from: package-private */
    public boolean isHealthOperation() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        sendNotificationChunk(1);
    }

    /* access modifiers changed from: package-private */
    public void sendNotificationChunk(int chunkIndex) throws IOException {
        this.currentNotificationIndex = chunkIndex;
        int offset = (chunkIndex - 1) * 16;
        int tailSize = this.currentNotificationBuffer.length - offset;
        byte[] raw = new byte[16];
        System.arraycopy(this.currentNotificationBuffer, offset, raw, 0, tailSize > 16 ? 16 : tailSize);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(5);
        outputStream.write(this.currentNotificationType);
        outputStream.write((byte) this.currentNotificationSize);
        outputStream.write((byte) this.currentNotificationIndex);
        outputStream.write(raw);
        byte[] cmd = outputStream.toByteArray();
        TransactionBuilder builder = performInitialized("send notification chunk");
        builder.write(this.controlCharacteristic, cmd);
        builder.queue(getQueue());
    }

    /* access modifiers changed from: package-private */
    public void handleResponse(byte[] data) {
        int i;
        if (!isOperationRunning()) {
            Logger logger = LOG;
            logger.error("ignoring notification because operation is not running. Data length: " + data.length);
            ((ID115Support) getSupport()).logMessageContent(data);
        } else if (data.length < 2) {
            LOG.warn("short GATT response");
        } else if (data[0] != 5) {
        } else {
            if (data.length < 4) {
                LOG.warn("short GATT response for NOTIFY");
            } else if (data[1] != this.currentNotificationType || data[3] != (i = this.currentNotificationIndex)) {
            } else {
                if (i != this.currentNotificationSize) {
                    try {
                        sendNotificationChunk(i + 1);
                    } catch (IOException ex) {
                        C1238GB.toast(getContext(), "Error sending ID115 notification, you may need to connect and disconnect", 1, 3, ex);
                    }
                } else {
                    LOG.info("Notification transfer has finished.");
                    operationFinished();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeCallNotification(String name, String phone) {
        if (name.length() > 20) {
            name = name.substring(0, 20);
        }
        if (phone.length() > 20) {
            phone = phone.substring(0, 20);
        }
        byte[] name_bytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] phone_bytes = phone.getBytes(StandardCharsets.UTF_8);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write((byte) phone_bytes.length);
            outputStream.write((byte) name_bytes.length);
            outputStream.write(phone_bytes);
            outputStream.write(name_bytes);
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeMessageNotification(NotificationType type, String title, String phone, String text) {
        if (title.length() > 20) {
            title = title.substring(0, 20);
        }
        if (phone.length() > 20) {
            phone = phone.substring(0, 20);
        }
        if (text.length() > 20) {
            text = text.substring(0, 20);
        }
        byte[] title_bytes = title.getBytes(StandardCharsets.UTF_8);
        byte[] phone_bytes = phone.getBytes(StandardCharsets.UTF_8);
        byte[] text_bytes = text.getBytes(StandardCharsets.UTF_8);
        byte nativeType = ID115Constants.getNotificationType(type);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(nativeType);
            outputStream.write((byte) text_bytes.length);
            outputStream.write((byte) phone_bytes.length);
            outputStream.write((byte) title_bytes.length);
            outputStream.write(phone_bytes);
            outputStream.write(title_bytes);
            outputStream.write(text_bytes);
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
