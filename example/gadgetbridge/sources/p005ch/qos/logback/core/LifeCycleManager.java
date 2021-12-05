package p005ch.qos.logback.core;

import java.util.HashSet;
import java.util.Set;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.core.LifeCycleManager */
public class LifeCycleManager {
    private final Set<LifeCycle> components = new HashSet();

    public void register(LifeCycle lifeCycle) {
        this.components.add(lifeCycle);
    }

    public void reset() {
        for (LifeCycle next : this.components) {
            if (next.isStarted()) {
                next.stop();
            }
        }
        this.components.clear();
    }
}
