package p005ch.qos.logback.core.rolling;

import p005ch.qos.logback.core.rolling.helper.ArchiveRemover;
import p005ch.qos.logback.core.spi.ContextAware;

/* renamed from: ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy */
public interface TimeBasedFileNamingAndTriggeringPolicy<E> extends TriggeringPolicy<E>, ContextAware {
    ArchiveRemover getArchiveRemover();

    String getCurrentPeriodsFileNameWithoutCompressionSuffix();

    long getCurrentTime();

    String getElapsedPeriodsFileName();

    void setCurrentTime(long j);

    void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> timeBasedRollingPolicy);
}
