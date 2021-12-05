package p005ch.qos.logback.core.util;

/* renamed from: ch.qos.logback.core.util.PropertySetterException */
public class PropertySetterException extends Exception {
    private static final long serialVersionUID = -2771077768281663949L;

    public PropertySetterException(String str) {
        super(str);
    }

    public PropertySetterException(String str, Throwable th) {
        super(str, th);
    }

    public PropertySetterException(Throwable th) {
        super(th);
    }
}
