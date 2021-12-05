package p005ch.qos.logback.core.joran.util;

import java.lang.reflect.Method;

/* renamed from: ch.qos.logback.core.joran.util.MethodDescriptor */
public class MethodDescriptor {
    private Method method;
    private String name;

    public MethodDescriptor(String str, Method method2) {
        this.name = str;
        this.method = method2;
    }

    public Method getMethod() {
        return this.method;
    }

    public String getName() {
        return this.name;
    }
}
