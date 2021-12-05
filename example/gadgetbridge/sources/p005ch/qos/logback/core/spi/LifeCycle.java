package p005ch.qos.logback.core.spi;

/* renamed from: ch.qos.logback.core.spi.LifeCycle */
public interface LifeCycle {
    boolean isStarted();

    void start();

    void stop();
}
