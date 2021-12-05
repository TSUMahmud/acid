package p005ch.qos.logback.core.spi;

/* renamed from: ch.qos.logback.core.spi.ScanException */
public class ScanException extends Exception {
    private static final long serialVersionUID = -3132040414328475658L;
    Throwable cause;

    public ScanException(String str) {
        super(str);
    }

    public ScanException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
