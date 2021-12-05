package nodomain.freeyourgadget.gadgetbridge.deviceevents;

import java.util.UUID;
import p005ch.qos.logback.core.CoreConstants;

public class GBDeviceEventAppMessage extends GBDeviceEvent {
    public static int TYPE_ACK = 1;
    public static int TYPE_APPMESSAGE = 0;
    public static int TYPE_NACK = 2;
    public UUID appUUID;

    /* renamed from: id */
    public int f124id;
    public String message;
    public int type;

    public String toString() {
        return "GBDeviceEventAppMessage{type=" + this.type + ", appUUID=" + this.appUUID + ", message='" + this.message + CoreConstants.SINGLE_QUOTE_CHAR + ", id=" + this.f124id + CoreConstants.CURLY_RIGHT;
    }
}
