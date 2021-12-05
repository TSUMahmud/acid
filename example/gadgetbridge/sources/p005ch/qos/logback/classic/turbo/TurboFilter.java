package p005ch.qos.logback.classic.turbo;

import org.slf4j.Marker;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.FilterReply;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.classic.turbo.TurboFilter */
public abstract class TurboFilter extends ContextAwareBase implements LifeCycle {
    private String name;
    boolean start = false;

    public abstract FilterReply decide(Marker marker, Logger logger, Level level, String str, Object[] objArr, Throwable th);

    public String getName() {
        return this.name;
    }

    public boolean isStarted() {
        return this.start;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void start() {
        this.start = true;
    }

    public void stop() {
        this.start = false;
    }
}
