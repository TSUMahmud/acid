package p005ch.qos.logback.core.joran.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/* renamed from: ch.qos.logback.core.joran.util.Introspector */
public class Introspector {
    public static String decapitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        String lowerCase = str.substring(0, 1).toLowerCase();
        if (str.length() <= 1) {
            return lowerCase;
        }
        return lowerCase + str.substring(1);
    }

    public static MethodDescriptor[] getMethodDescriptors(Class<?> cls) {
        ArrayList arrayList = new ArrayList();
        for (Method method : cls.getMethods()) {
            arrayList.add(new MethodDescriptor(method.getName(), method));
        }
        return (MethodDescriptor[]) arrayList.toArray(new MethodDescriptor[0]);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> cls) {
        Class<?> returnType;
        int length = "set".length();
        HashMap hashMap = new HashMap();
        for (Method method : cls.getMethods()) {
            String name = method.getName();
            boolean z = name.startsWith("get") && name.length() > length;
            boolean z2 = name.startsWith("set") && name.length() > length;
            if (z || z2) {
                String decapitalize = decapitalize(name.substring(length));
                PropertyDescriptor propertyDescriptor = (PropertyDescriptor) hashMap.get(decapitalize);
                if (propertyDescriptor == null) {
                    propertyDescriptor = new PropertyDescriptor(decapitalize);
                    hashMap.put(decapitalize, propertyDescriptor);
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (z2) {
                    if (parameterTypes.length == 1) {
                        propertyDescriptor.setWriteMethod(method);
                        returnType = parameterTypes[0];
                    }
                } else if (z && parameterTypes.length == 0) {
                    propertyDescriptor.setReadMethod(method);
                    if (propertyDescriptor.getPropertyType() == null) {
                        returnType = method.getReturnType();
                    }
                }
                propertyDescriptor.setPropertyType(returnType);
            }
        }
        return (PropertyDescriptor[]) hashMap.values().toArray(new PropertyDescriptor[0]);
    }
}
