package nodomain.freeyourgadget.gadgetbridge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationSpec {

    /* renamed from: c */
    private static final AtomicInteger f160c = new AtomicInteger((int) (System.currentTimeMillis() / 1000));
    public ArrayList<Action> attachedActions;
    public String body;
    public String[] cannedReplies;
    public int flags;

    /* renamed from: id */
    private int f161id;
    public byte pebbleColor;
    public String phoneNumber;
    public String sender;
    public String sourceAppId;
    public String sourceName;
    public String subject;
    public String title;
    public NotificationType type;

    public static class Action implements Serializable {
        public static final int TYPE_SYNTECTIC_DISMISS = 3;
        public static final int TYPE_SYNTECTIC_DISMISS_ALL = 4;
        public static final int TYPE_SYNTECTIC_MUTE = 5;
        public static final int TYPE_SYNTECTIC_OPEN = 6;
        public static final int TYPE_SYNTECTIC_REPLY_PHONENR = 2;
        static final int TYPE_UNDEFINED = -1;
        public static final int TYPE_WEARABLE_REPLY = 1;
        public static final int TYPE_WEARABLE_SIMPLE = 0;
        public long handle;
        public String title;
        public int type = -1;
    }

    public NotificationSpec() {
        this.f161id = f160c.incrementAndGet();
    }

    public NotificationSpec(int id) {
        if (id != -1) {
            this.f161id = id;
        } else {
            this.f161id = f160c.incrementAndGet();
        }
    }

    public int getId() {
        return this.f161id;
    }
}
