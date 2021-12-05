package p005ch.qos.logback.classic.util;

import java.util.HashMap;

/* renamed from: ch.qos.logback.classic.util.CopyOnInheritThreadLocal */
public class CopyOnInheritThreadLocal extends InheritableThreadLocal<HashMap<String, String>> {
    /* access modifiers changed from: protected */
    public HashMap<String, String> childValue(HashMap<String, String> hashMap) {
        if (hashMap == null) {
            return null;
        }
        return new HashMap<>(hashMap);
    }
}
