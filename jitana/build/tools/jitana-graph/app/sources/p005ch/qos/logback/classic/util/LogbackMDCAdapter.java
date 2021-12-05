package p005ch.qos.logback.classic.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

/* renamed from: ch.qos.logback.classic.util.LogbackMDCAdapter */
public final class LogbackMDCAdapter implements MDCAdapter {
    private static final int READ_OPERATION = 2;
    private static final int WRITE_OPERATION = 1;
    final InheritableThreadLocal<Map<String, String>> copyOnInheritThreadLocal = new InheritableThreadLocal<>();
    final ThreadLocal<Integer> lastOperation = new ThreadLocal<>();

    private Map<String, String> duplicateAndInsertNewMap(Map<String, String> map) {
        Map<String, String> synchronizedMap = Collections.synchronizedMap(new HashMap());
        if (map != null) {
            synchronized (map) {
                synchronizedMap.putAll(map);
            }
        }
        this.copyOnInheritThreadLocal.set(synchronizedMap);
        return synchronizedMap;
    }

    private Integer getAndSetLastOperation(int i) {
        Integer num = this.lastOperation.get();
        this.lastOperation.set(Integer.valueOf(i));
        return num;
    }

    private boolean wasLastOpReadOrNull(Integer num) {
        return num == null || num.intValue() == 2;
    }

    public void clear() {
        this.lastOperation.set(1);
        this.copyOnInheritThreadLocal.remove();
    }

    public String get(String str) {
        Map<String, String> propertyMap = getPropertyMap();
        if (propertyMap == null || str == null) {
            return null;
        }
        return propertyMap.get(str);
    }

    public Map getCopyOfContextMap() {
        this.lastOperation.set(2);
        Map map = (Map) this.copyOnInheritThreadLocal.get();
        if (map == null) {
            return null;
        }
        return new HashMap(map);
    }

    public Set<String> getKeys() {
        Map<String, String> propertyMap = getPropertyMap();
        if (propertyMap != null) {
            return propertyMap.keySet();
        }
        return null;
    }

    public Map<String, String> getPropertyMap() {
        this.lastOperation.set(2);
        return (Map) this.copyOnInheritThreadLocal.get();
    }

    public void put(String str, String str2) throws IllegalArgumentException {
        if (str != null) {
            Map<String, String> map = (Map) this.copyOnInheritThreadLocal.get();
            if (wasLastOpReadOrNull(getAndSetLastOperation(1)) || map == null) {
                map = duplicateAndInsertNewMap(map);
            }
            map.put(str, str2);
            return;
        }
        throw new IllegalArgumentException("key cannot be null");
    }

    public void remove(String str) {
        Map<String, String> map;
        if (str != null && (map = (Map) this.copyOnInheritThreadLocal.get()) != null) {
            if (wasLastOpReadOrNull(getAndSetLastOperation(1))) {
                map = duplicateAndInsertNewMap(map);
            }
            map.remove(str);
        }
    }

    public void setContextMap(Map map) {
        this.lastOperation.set(1);
        Map synchronizedMap = Collections.synchronizedMap(new HashMap());
        synchronizedMap.putAll(map);
        this.copyOnInheritThreadLocal.set(synchronizedMap);
    }
}
