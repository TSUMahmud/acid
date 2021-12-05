package p005ch.qos.logback.core.status;

/* renamed from: ch.qos.logback.core.status.WarnStatus */
public class WarnStatus extends StatusBase {
    public WarnStatus(String str, Object obj) {
        super(1, str, obj);
    }

    public WarnStatus(String str, Object obj, Throwable th) {
        super(1, str, obj, th);
    }
}
