package p005ch.qos.logback.core.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/* renamed from: ch.qos.logback.core.spi.AbstractComponentTracker */
public abstract class AbstractComponentTracker<C> implements ComponentTracker<C> {
    private static final boolean ACCESS_ORDERED = true;
    public static final long LINGERING_TIMEOUT = 10000;
    public static final long WAIT_BETWEEN_SUCCESSIVE_REMOVAL_ITERATIONS = 1000;
    private RemovalPredicator<C> byExcedent = new RemovalPredicator<C>() {
        public boolean isSlatedForRemoval(Entry<C> entry, long j) {
            if (AbstractComponentTracker.this.liveMap.size() > AbstractComponentTracker.this.maxComponents) {
                return AbstractComponentTracker.ACCESS_ORDERED;
            }
            return false;
        }
    };
    private RemovalPredicator<C> byLingering = new RemovalPredicator<C>() {
        public boolean isSlatedForRemoval(Entry<C> entry, long j) {
            return AbstractComponentTracker.this.isEntryDoneLingering(entry, j);
        }
    };
    private RemovalPredicator<C> byTimeout = new RemovalPredicator<C>() {
        public boolean isSlatedForRemoval(Entry<C> entry, long j) {
            return AbstractComponentTracker.this.isEntryStale(entry, j);
        }
    };
    long lastCheck = 0;
    LinkedHashMap<String, Entry<C>> lingerersMap = new LinkedHashMap<>(16, 0.75f, ACCESS_ORDERED);
    LinkedHashMap<String, Entry<C>> liveMap = new LinkedHashMap<>(32, 0.75f, ACCESS_ORDERED);
    protected int maxComponents = Integer.MAX_VALUE;
    protected long timeout = 1800000;

    /* renamed from: ch.qos.logback.core.spi.AbstractComponentTracker$Entry */
    private static class Entry<C> {
        C component;
        String key;
        long timestamp;

        Entry(String str, C c, long j) {
            this.key = str;
            this.component = c;
            this.timestamp = j;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return AbstractComponentTracker.ACCESS_ORDERED;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Entry entry = (Entry) obj;
            String str = this.key;
            if (str == null) {
                if (entry.key != null) {
                    return false;
                }
            } else if (!str.equals(entry.key)) {
                return false;
            }
            C c = this.component;
            C c2 = entry.component;
            if (c == null) {
                if (c2 != null) {
                    return false;
                }
            } else if (!c.equals(c2)) {
                return false;
            }
            return AbstractComponentTracker.ACCESS_ORDERED;
        }

        public int hashCode() {
            return this.key.hashCode();
        }

        public void setTimestamp(long j) {
            this.timestamp = j;
        }

        public String toString() {
            return "(" + this.key + ", " + this.component + ")";
        }
    }

    /* renamed from: ch.qos.logback.core.spi.AbstractComponentTracker$RemovalPredicator */
    private interface RemovalPredicator<C> {
        boolean isSlatedForRemoval(Entry<C> entry, long j);
    }

    private void genericStaleComponentRemover(LinkedHashMap<String, Entry<C>> linkedHashMap, long j, RemovalPredicator<C> removalPredicator) {
        Iterator<Map.Entry<String, Entry<C>>> it = linkedHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next().getValue();
            if (removalPredicator.isSlatedForRemoval(entry, j)) {
                it.remove();
                processPriorToRemoval(entry.component);
            } else {
                return;
            }
        }
    }

    private Entry<C> getFromEitherMap(String str) {
        Entry<C> entry = this.liveMap.get(str);
        return entry != null ? entry : this.lingerersMap.get(str);
    }

    /* access modifiers changed from: private */
    public boolean isEntryDoneLingering(Entry entry, long j) {
        if (entry.timestamp + LINGERING_TIMEOUT < j) {
            return ACCESS_ORDERED;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isEntryStale(Entry<C> entry, long j) {
        if (!isComponentStale(entry.component) && entry.timestamp + this.timeout >= j) {
            return false;
        }
        return ACCESS_ORDERED;
    }

    private boolean isTooSoonForRemovalIteration(long j) {
        if (this.lastCheck + 1000 > j) {
            return ACCESS_ORDERED;
        }
        this.lastCheck = j;
        return false;
    }

    private void removeExcedentComponents() {
        genericStaleComponentRemover(this.liveMap, 0, this.byExcedent);
    }

    private void removeStaleComponentsFromLingerersMap(long j) {
        genericStaleComponentRemover(this.lingerersMap, j, this.byLingering);
    }

    private void removeStaleComponentsFromMainMap(long j) {
        genericStaleComponentRemover(this.liveMap, j, this.byTimeout);
    }

    public Collection<C> allComponents() {
        ArrayList arrayList = new ArrayList();
        for (Entry<C> entry : this.liveMap.values()) {
            arrayList.add(entry.component);
        }
        for (Entry<C> entry2 : this.lingerersMap.values()) {
            arrayList.add(entry2.component);
        }
        return arrayList;
    }

    public Set<String> allKeys() {
        HashSet hashSet = new HashSet(this.liveMap.keySet());
        hashSet.addAll(this.lingerersMap.keySet());
        return hashSet;
    }

    /* access modifiers changed from: protected */
    public abstract C buildComponent(String str);

    public void endOfLife(String str) {
        Entry entry = (Entry) this.liveMap.remove(str);
        if (entry != null) {
            this.lingerersMap.put(str, entry);
        }
    }

    public synchronized C find(String str) {
        Entry fromEitherMap = getFromEitherMap(str);
        if (fromEitherMap == null) {
            return null;
        }
        return fromEitherMap.component;
    }

    public int getComponentCount() {
        return this.liveMap.size() + this.lingerersMap.size();
    }

    public int getMaxComponents() {
        return this.maxComponents;
    }

    public synchronized C getOrCreate(String str, long j) {
        Entry fromEitherMap;
        fromEitherMap = getFromEitherMap(str);
        if (fromEitherMap == null) {
            Entry entry = new Entry(str, buildComponent(str), j);
            this.liveMap.put(str, entry);
            fromEitherMap = entry;
        } else {
            fromEitherMap.setTimestamp(j);
        }
        return fromEitherMap.component;
    }

    public long getTimeout() {
        return this.timeout;
    }

    /* access modifiers changed from: protected */
    public abstract boolean isComponentStale(C c);

    /* access modifiers changed from: protected */
    public abstract void processPriorToRemoval(C c);

    public synchronized void removeStaleComponents(long j) {
        if (!isTooSoonForRemovalIteration(j)) {
            removeExcedentComponents();
            removeStaleComponentsFromMainMap(j);
            removeStaleComponentsFromLingerersMap(j);
        }
    }

    public void setMaxComponents(int i) {
        this.maxComponents = i;
    }

    public void setTimeout(long j) {
        this.timeout = j;
    }
}
