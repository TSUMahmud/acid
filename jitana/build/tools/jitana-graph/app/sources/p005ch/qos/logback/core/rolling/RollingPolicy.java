package p005ch.qos.logback.core.rolling;

import p005ch.qos.logback.core.FileAppender;
import p005ch.qos.logback.core.rolling.helper.CompressionMode;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.core.rolling.RollingPolicy */
public interface RollingPolicy extends LifeCycle {
    String getActiveFileName();

    CompressionMode getCompressionMode();

    void rollover() throws RolloverFailure;

    void setParent(FileAppender fileAppender);
}
