package p005ch.qos.logback.core.joran.util;

import java.lang.reflect.Method;

/* renamed from: ch.qos.logback.core.joran.util.PropertyDescriptor */
public class PropertyDescriptor {
    private String name;
    private Method readMethod;
    private Class<?> type;
    private Method writeMethod;

    public PropertyDescriptor(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getPropertyType() {
        return this.type;
    }

    public Method getReadMethod() {
        return this.readMethod;
    }

    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public void setPropertyType(Class<?> cls) {
        this.type = cls;
    }

    public void setReadMethod(Method method) {
        this.readMethod = method;
    }

    public void setWriteMethod(Method method) {
        this.writeMethod = method;
    }
}
