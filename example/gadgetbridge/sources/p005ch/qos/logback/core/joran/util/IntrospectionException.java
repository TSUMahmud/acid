package p005ch.qos.logback.core.joran.util;

/* renamed from: ch.qos.logback.core.joran.util.IntrospectionException */
public class IntrospectionException extends RuntimeException {
    private static final long serialVersionUID = -6760181416658938878L;

    public IntrospectionException(Exception exc) {
        super(exc);
    }

    public IntrospectionException(String str) {
        super(str);
    }
}
