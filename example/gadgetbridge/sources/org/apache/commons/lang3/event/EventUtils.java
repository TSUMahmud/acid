package org.apache.commons.lang3.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.reflect.MethodUtils;

public class EventUtils {
    public static <L> void addEventListener(Object eventSource, Class<L> listenerType, L listener) {
        try {
            MethodUtils.invokeMethod(eventSource, "add" + listenerType.getSimpleName(), listener);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + eventSource.getClass().getName() + " does not have a public add" + listenerType.getSimpleName() + " method which takes a parameter of type " + listenerType.getName() + ".");
        } catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Class " + eventSource.getClass().getName() + " does not have an accessible add" + listenerType.getSimpleName() + " method which takes a parameter of type " + listenerType.getName() + ".");
        } catch (InvocationTargetException e3) {
            throw new RuntimeException("Unable to add listener.", e3.getCause());
        }
    }

    public static <L> void bindEventsToMethod(Object target, String methodName, Object eventSource, Class<L> listenerType, String... eventTypes) {
        addEventListener(eventSource, listenerType, listenerType.cast(Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{listenerType}, new EventBindingInvocationHandler(target, methodName, eventTypes))));
    }

    private static class EventBindingInvocationHandler implements InvocationHandler {
        private final Set<String> eventTypes;
        private final String methodName;
        private final Object target;

        EventBindingInvocationHandler(Object target2, String methodName2, String[] eventTypes2) {
            this.target = target2;
            this.methodName = methodName2;
            this.eventTypes = new HashSet(Arrays.asList(eventTypes2));
        }

        public Object invoke(Object proxy, Method method, Object[] parameters) throws Throwable {
            if (!this.eventTypes.isEmpty() && !this.eventTypes.contains(method.getName())) {
                return null;
            }
            if (hasMatchingParametersMethod(method)) {
                return MethodUtils.invokeMethod(this.target, this.methodName, parameters);
            }
            return MethodUtils.invokeMethod(this.target, this.methodName);
        }

        private boolean hasMatchingParametersMethod(Method method) {
            return MethodUtils.getAccessibleMethod(this.target.getClass(), this.methodName, method.getParameterTypes()) != null;
        }
    }
}
