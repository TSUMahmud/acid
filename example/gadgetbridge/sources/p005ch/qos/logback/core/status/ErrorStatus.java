package p005ch.qos.logback.core.status;

/* renamed from: ch.qos.logback.core.status.ErrorStatus */
public class ErrorStatus extends StatusBase {
    public ErrorStatus(String str, Object obj) {
        super(2, str, obj);
    }

    public ErrorStatus(String str, Object obj, Throwable th) {
        super(2, str, obj, th);
    }
}
