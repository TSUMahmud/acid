package p005ch.qos.logback.core.spi;

import java.util.Collection;
import java.util.Set;

/* renamed from: ch.qos.logback.core.spi.ComponentTracker */
public interface ComponentTracker<C> {
    public static final int DEFAULT_MAX_COMPONENTS = Integer.MAX_VALUE;
    public static final int DEFAULT_TIMEOUT = 1800000;

    Collection<C> allComponents();

    Set<String> allKeys();

    void endOfLife(String str);

    C find(String str);

    int getComponentCount();

    C getOrCreate(String str, long j);

    void removeStaleComponents(long j);
}
