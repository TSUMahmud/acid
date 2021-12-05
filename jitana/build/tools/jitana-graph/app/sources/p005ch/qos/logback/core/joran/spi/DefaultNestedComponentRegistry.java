package p005ch.qos.logback.core.joran.spi;

import java.util.HashMap;
import java.util.Map;

/* renamed from: ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry */
public class DefaultNestedComponentRegistry {
    Map<HostClassAndPropertyDouble, Class<?>> defaultComponentMap = new HashMap();

    private Class<?> oneShotFind(Class<?> cls, String str) {
        return this.defaultComponentMap.get(new HostClassAndPropertyDouble(cls, str));
    }

    public void add(Class<?> cls, String str, Class<?> cls2) {
        this.defaultComponentMap.put(new HostClassAndPropertyDouble(cls, str.toLowerCase()), cls2);
    }

    public Class<?> findDefaultComponentType(Class<?> cls, String str) {
        String lowerCase = str.toLowerCase();
        for (Class<? super Object> cls2 = cls; cls2 != null; cls2 = cls2.getSuperclass()) {
            Class<?> oneShotFind = oneShotFind(cls2, lowerCase);
            if (oneShotFind != null) {
                return oneShotFind;
            }
        }
        return null;
    }
}
