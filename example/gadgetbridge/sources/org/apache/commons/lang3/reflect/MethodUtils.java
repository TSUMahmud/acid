package org.apache.commons.lang3.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

public class MethodUtils {
    public static Object invokeMethod(Object object, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class<?>[]) null);
    }

    public static Object invokeMethod(Object object, boolean forceAccess, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, forceAccess, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class<?>[]) null);
    }

    public static Object invokeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        return invokeMethod(object, methodName, args2, ClassUtils.toClass(args2));
    }

    public static Object invokeMethod(Object object, boolean forceAccess, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        return invokeMethod(object, forceAccess, methodName, args2, ClassUtils.toClass(args2));
    }

    public static Object invokeMethod(Object object, boolean forceAccess, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String messagePrefix;
        Method method;
        Class<?>[] parameterTypes2 = ArrayUtils.nullToEmpty(parameterTypes);
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        if (forceAccess) {
            messagePrefix = "No such method: ";
            method = getMatchingMethod(object.getClass(), methodName, parameterTypes2);
            if (method != null && !method.isAccessible()) {
                method.setAccessible(true);
            }
        } else {
            messagePrefix = "No such accessible method: ";
            method = getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes2);
        }
        if (method != null) {
            return method.invoke(object, toVarArgs(method, args2));
        }
        throw new NoSuchMethodException(messagePrefix + methodName + "() on object: " + object.getClass().getName());
    }

    public static Object invokeMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, false, methodName, args, parameterTypes);
    }

    public static Object invokeExactMethod(Object object, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeExactMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class<?>[]) null);
    }

    public static Object invokeExactMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        return invokeExactMethod(object, methodName, args2, ClassUtils.toClass(args2));
    }

    public static Object invokeExactMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        Method method = getAccessibleMethod(object.getClass(), methodName, ArrayUtils.nullToEmpty(parameterTypes));
        if (method != null) {
            return method.invoke(object, args2);
        }
        throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
    }

    public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        Method method = getAccessibleMethod(cls, methodName, ArrayUtils.nullToEmpty(parameterTypes));
        if (method != null) {
            return method.invoke((Object) null, args2);
        }
        throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + cls.getName());
    }

    public static Object invokeStaticMethod(Class<?> cls, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        return invokeStaticMethod(cls, methodName, args2, ClassUtils.toClass(args2));
    }

    public static Object invokeStaticMethod(Class<?> cls, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        Method method = getMatchingAccessibleMethod(cls, methodName, ArrayUtils.nullToEmpty(parameterTypes));
        if (method != null) {
            return method.invoke((Object) null, toVarArgs(method, args2));
        }
        throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + cls.getName());
    }

    private static Object[] toVarArgs(Method method, Object[] args) {
        if (method.isVarArgs()) {
            return getVarArgs(args, method.getParameterTypes());
        }
        return args;
    }

    static Object[] getVarArgs(Object[] args, Class<?>[] methodParameterTypes) {
        if (args.length == methodParameterTypes.length && args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1])) {
            return args;
        }
        Object[] newArgs = new Object[methodParameterTypes.length];
        System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);
        Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        int varArgLength = (args.length - methodParameterTypes.length) + 1;
        Object varArgsArray = Array.newInstance(ClassUtils.primitiveToWrapper(varArgComponentType), varArgLength);
        System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);
        if (varArgComponentType.isPrimitive()) {
            varArgsArray = ArrayUtils.toPrimitive(varArgsArray);
        }
        newArgs[methodParameterTypes.length - 1] = varArgsArray;
        return newArgs;
    }

    public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args2 = ArrayUtils.nullToEmpty(args);
        return invokeExactStaticMethod(cls, methodName, args2, ClassUtils.toClass(args2));
    }

    public static Method getAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        try {
            return getAccessibleMethod(cls.getMethod(methodName, parameterTypes));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getAccessibleMethod(Method method) {
        if (!MemberUtils.isAccessible(method)) {
            return null;
        }
        Class<?> cls = method.getDeclaringClass();
        if (Modifier.isPublic(cls.getModifiers())) {
            return method;
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Method method2 = getAccessibleMethodFromInterfaceNest(cls, methodName, parameterTypes);
        if (method2 == null) {
            return getAccessibleMethodFromSuperclass(cls, methodName, parameterTypes);
        }
        return method2;
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [java.lang.Class<?>, java.lang.Class] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.reflect.Method getAccessibleMethodFromSuperclass(java.lang.Class<?> r3, java.lang.String r4, java.lang.Class<?>... r5) {
        /*
            java.lang.Class r0 = r3.getSuperclass()
        L_0x0004:
            r1 = 0
            if (r0 == 0) goto L_0x001d
            int r2 = r0.getModifiers()
            boolean r2 = java.lang.reflect.Modifier.isPublic(r2)
            if (r2 == 0) goto L_0x0018
            java.lang.reflect.Method r1 = r0.getMethod(r4, r5)     // Catch:{ NoSuchMethodException -> 0x0016 }
            return r1
        L_0x0016:
            r2 = move-exception
            return r1
        L_0x0018:
            java.lang.Class r0 = r0.getSuperclass()
            goto L_0x0004
        L_0x001d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.reflect.MethodUtils.getAccessibleMethodFromSuperclass(java.lang.Class, java.lang.String, java.lang.Class[]):java.lang.reflect.Method");
    }

    private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Class<? super Object> cls2;
        while (cls2 != null) {
            for (Class<?> anInterface : cls2.getInterfaces()) {
                if (Modifier.isPublic(anInterface.getModifiers())) {
                    try {
                        return anInterface.getDeclaredMethod(methodName, parameterTypes);
                    } catch (NoSuchMethodException e) {
                        Method method = getAccessibleMethodFromInterfaceNest(anInterface, methodName, parameterTypes);
                        if (method != null) {
                            return method;
                        }
                    }
                }
            }
            Class<? super Object> superclass = cls2.getSuperclass();
            cls2 = cls;
            cls2 = superclass;
        }
        return null;
    }

    public static Method getMatchingAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Method accessibleMethod;
        try {
            Method method = cls.getMethod(methodName, parameterTypes);
            MemberUtils.setAccessibleWorkaround(method);
            return method;
        } catch (NoSuchMethodException e) {
            Method bestMatch = null;
            for (Method method2 : cls.getMethods()) {
                if (method2.getName().equals(methodName) && MemberUtils.isMatchingMethod(method2, parameterTypes) && (accessibleMethod = getAccessibleMethod(method2)) != null && (bestMatch == null || MemberUtils.compareMethodFit(accessibleMethod, bestMatch, parameterTypes) < 0)) {
                    bestMatch = accessibleMethod;
                }
            }
            if (bestMatch != null) {
                MemberUtils.setAccessibleWorkaround(bestMatch);
            }
            if (bestMatch != null && bestMatch.isVarArgs() && bestMatch.getParameterTypes().length > 0 && parameterTypes.length > 0) {
                Class<?>[] methodParameterTypes = bestMatch.getParameterTypes();
                String methodParameterComponentTypeName = ClassUtils.primitiveToWrapper(methodParameterTypes[methodParameterTypes.length - 1].getComponentType()).getName();
                String parameterTypeName = parameterTypes[parameterTypes.length - 1].getName();
                String parameterTypeSuperClassName = parameterTypes[parameterTypes.length - 1].getSuperclass().getName();
                if (methodParameterComponentTypeName.equals(parameterTypeName) || methodParameterComponentTypeName.equals(parameterTypeSuperClassName)) {
                    return bestMatch;
                }
                return null;
            }
            return bestMatch;
        }
    }

    /* JADX WARNING: type inference failed for: r4v2, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.reflect.Method getMatchingMethod(java.lang.Class<?> r9, java.lang.String r10, java.lang.Class<?>... r11) {
        /*
            r0 = 0
            java.lang.Object[] r1 = new java.lang.Object[r0]
            java.lang.String r2 = "Null class not allowed."
            org.apache.commons.lang3.Validate.notNull(r9, r2, r1)
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r1 = "Null or blank methodName not allowed."
            org.apache.commons.lang3.Validate.notEmpty(r10, (java.lang.String) r1, (java.lang.Object[]) r0)
            java.lang.reflect.Method[] r0 = r9.getDeclaredMethods()
            java.util.List r1 = org.apache.commons.lang3.ClassUtils.getAllSuperclasses(r9)
            java.util.Iterator r2 = r1.iterator()
        L_0x001b:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0033
            java.lang.Object r3 = r2.next()
            java.lang.Class r3 = (java.lang.Class) r3
            java.lang.reflect.Method[] r4 = r3.getDeclaredMethods()
            java.lang.Object[] r4 = org.apache.commons.lang3.ArrayUtils.addAll((T[]) r0, (T[]) r4)
            r0 = r4
            java.lang.reflect.Method[] r0 = (java.lang.reflect.Method[]) r0
            goto L_0x001b
        L_0x0033:
            r2 = 0
            r3 = r0
            int r4 = r3.length
            r5 = 0
        L_0x0037:
            if (r5 >= r4) goto L_0x007f
            r6 = r3[r5]
            java.lang.String r7 = r6.getName()
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x0050
            java.lang.Class[] r7 = r6.getParameterTypes()
            boolean r7 = java.util.Objects.deepEquals(r11, r7)
            if (r7 == 0) goto L_0x0050
            return r6
        L_0x0050:
            java.lang.String r7 = r6.getName()
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x007c
            java.lang.Class[] r7 = r6.getParameterTypes()
            r8 = 1
            boolean r7 = org.apache.commons.lang3.ClassUtils.isAssignable((java.lang.Class<?>[]) r11, (java.lang.Class<?>[]) r7, (boolean) r8)
            if (r7 == 0) goto L_0x007c
            if (r2 != 0) goto L_0x0069
            r2 = r6
            goto L_0x007c
        L_0x0069:
            java.lang.Class[] r7 = r6.getParameterTypes()
            int r7 = distance(r11, r7)
            java.lang.Class[] r8 = r2.getParameterTypes()
            int r8 = distance(r11, r8)
            if (r7 >= r8) goto L_0x007c
            r2 = r6
        L_0x007c:
            int r5 = r5 + 1
            goto L_0x0037
        L_0x007f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.reflect.MethodUtils.getMatchingMethod(java.lang.Class, java.lang.String, java.lang.Class[]):java.lang.reflect.Method");
    }

    private static int distance(Class<?>[] classArray, Class<?>[] toClassArray) {
        int answer = 0;
        if (!ClassUtils.isAssignable(classArray, toClassArray, true)) {
            return -1;
        }
        for (int offset = 0; offset < classArray.length; offset++) {
            if (!classArray[offset].equals(toClassArray[offset])) {
                if (!ClassUtils.isAssignable(classArray[offset], toClassArray[offset], true) || ClassUtils.isAssignable(classArray[offset], toClassArray[offset], false)) {
                    answer += 2;
                } else {
                    answer++;
                }
            }
        }
        return answer;
    }

    public static Set<Method> getOverrideHierarchy(Method method, ClassUtils.Interfaces interfacesBehavior) {
        Validate.notNull(method);
        Set<Method> result = new LinkedHashSet<>();
        result.add(method);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> declaringClass = method.getDeclaringClass();
        Iterator<Class<?>> hierarchy = ClassUtils.hierarchy(declaringClass, interfacesBehavior).iterator();
        hierarchy.next();
        while (hierarchy.hasNext()) {
            Method m = getMatchingAccessibleMethod(hierarchy.next(), method.getName(), parameterTypes);
            if (m != null) {
                if (!Arrays.equals(m.getParameterTypes(), parameterTypes)) {
                    Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(declaringClass, m.getDeclaringClass());
                    int i = 0;
                    while (true) {
                        if (i >= parameterTypes.length) {
                            result.add(m);
                            break;
                        } else if (!TypeUtils.equals(TypeUtils.unrollVariables(typeArguments, method.getGenericParameterTypes()[i]), TypeUtils.unrollVariables(typeArguments, m.getGenericParameterTypes()[i]))) {
                            break;
                        } else {
                            i++;
                        }
                    }
                } else {
                    result.add(m);
                }
            }
        }
        return result;
    }

    public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
        return getMethodsWithAnnotation(cls, annotationCls, false, false);
    }

    public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
        return getMethodsListWithAnnotation(cls, annotationCls, false, false);
    }

    public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        List<Method> annotatedMethodsList = getMethodsListWithAnnotation(cls, annotationCls, searchSupers, ignoreAccess);
        return (Method[]) annotatedMethodsList.toArray(new Method[annotatedMethodsList.size()]);
    }

    public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        boolean z = true;
        Validate.isTrue(cls != null, "The class must not be null", new Object[0]);
        if (annotationCls == null) {
            z = false;
        }
        Validate.isTrue(z, "The annotation class must not be null", new Object[0]);
        List<Class<?>> classes = searchSupers ? getAllSuperclassesAndInterfaces(cls) : new ArrayList<>();
        classes.add(0, cls);
        List<Method> annotatedMethods = new ArrayList<>();
        for (Class<?> acls : classes) {
            for (Method method : ignoreAccess ? acls.getDeclaredMethods() : acls.getMethods()) {
                if (method.getAnnotation(annotationCls) != null) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }

    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        Method equivalentMethod;
        boolean z = true;
        Validate.isTrue(method != null, "The method must not be null", new Object[0]);
        if (annotationCls == null) {
            z = false;
        }
        Validate.isTrue(z, "The annotation class must not be null", new Object[0]);
        if (!ignoreAccess && !MemberUtils.isAccessible(method)) {
            return null;
        }
        A annotation = method.getAnnotation(annotationCls);
        if (annotation == null && searchSupers) {
            for (Class<?> acls : getAllSuperclassesAndInterfaces(method.getDeclaringClass())) {
                if (ignoreAccess) {
                    try {
                        equivalentMethod = acls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    } catch (NoSuchMethodException e) {
                    }
                } else {
                    equivalentMethod = acls.getMethod(method.getName(), method.getParameterTypes());
                }
                annotation = equivalentMethod.getAnnotation(annotationCls);
                if (annotation != null) {
                    break;
                }
            }
        }
        return annotation;
    }

    private static List<Class<?>> getAllSuperclassesAndInterfaces(Class<?> cls) {
        int superClassIndex;
        Class<?> acls;
        if (cls == null) {
            return null;
        }
        List<Class<?>> allSuperClassesAndInterfaces = new ArrayList<>();
        List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(cls);
        int superClassIndex2 = 0;
        List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(cls);
        int interfaceIndex = 0;
        while (true) {
            if (interfaceIndex >= allInterfaces.size() && superClassIndex2 >= allSuperclasses.size()) {
                return allSuperClassesAndInterfaces;
            }
            if (interfaceIndex >= allInterfaces.size()) {
                int i = interfaceIndex;
                acls = allSuperclasses.get(superClassIndex2);
                superClassIndex2++;
                superClassIndex = i;
            } else if (superClassIndex2 >= allSuperclasses.size()) {
                superClassIndex = interfaceIndex + 1;
                acls = allInterfaces.get(interfaceIndex);
            } else if (interfaceIndex < superClassIndex2) {
                superClassIndex = interfaceIndex + 1;
                acls = allInterfaces.get(interfaceIndex);
            } else if (superClassIndex2 < interfaceIndex) {
                int i2 = interfaceIndex;
                acls = allSuperclasses.get(superClassIndex2);
                superClassIndex2++;
                superClassIndex = i2;
            } else {
                superClassIndex = interfaceIndex + 1;
                acls = allInterfaces.get(interfaceIndex);
            }
            allSuperClassesAndInterfaces.add(acls);
            interfaceIndex = superClassIndex;
        }
    }
}
