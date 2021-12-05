package p005ch.qos.logback.classic.turbo;

import java.util.LinkedHashMap;
import java.util.Map;

/* renamed from: ch.qos.logback.classic.turbo.LRUMessageCache */
class LRUMessageCache extends LinkedHashMap<String, Integer> {
    private static final long serialVersionUID = 1;
    final int cacheSize;

    LRUMessageCache(int i) {
        super((int) (((float) i) * 1.3333334f), 0.75f, true);
        if (i >= 1) {
            this.cacheSize = i;
            return;
        }
        throw new IllegalArgumentException("Cache size cannot be smaller than 1");
    }

    public synchronized void clear() {
        super.clear();
    }

    /* access modifiers changed from: package-private */
    public int getMessageCountAndThenIncrement(String str) {
        Integer valueOf;
        if (str == null) {
            return 0;
        }
        synchronized (this) {
            Integer num = (Integer) super.get(str);
            valueOf = num == null ? 0 : Integer.valueOf(num.intValue() + 1);
            super.put(str, valueOf);
        }
        return valueOf.intValue();
    }

    /* access modifiers changed from: protected */
    public boolean removeEldestEntry(Map.Entry entry) {
        return size() > this.cacheSize;
    }
}
