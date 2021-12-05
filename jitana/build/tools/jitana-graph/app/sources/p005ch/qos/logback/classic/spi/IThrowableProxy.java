package p005ch.qos.logback.classic.spi;

/* renamed from: ch.qos.logback.classic.spi.IThrowableProxy */
public interface IThrowableProxy {
    IThrowableProxy getCause();

    String getClassName();

    int getCommonFrames();

    String getMessage();

    StackTraceElementProxy[] getStackTraceElementProxyArray();

    IThrowableProxy[] getSuppressed();
}
