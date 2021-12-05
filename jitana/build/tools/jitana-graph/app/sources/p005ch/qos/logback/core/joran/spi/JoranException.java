package p005ch.qos.logback.core.joran.spi;

/* renamed from: ch.qos.logback.core.joran.spi.JoranException */
public class JoranException extends Exception {
    private static final long serialVersionUID = 1112493363728774021L;

    public JoranException(String str) {
        super(str);
    }

    public JoranException(String str, Throwable th) {
        super(str, th);
    }
}
