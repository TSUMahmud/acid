package org.apache.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ClassUtils;

abstract class MemberUtils {
    private static final int ACCESS_TEST = 7;
    private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = {Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};

    MemberUtils() {
    }

    static boolean setAccessibleWorkaround(AccessibleObject o) {
        if (o == null || o.isAccessible()) {
            return false;
        }
        Member m = (Member) o;
        if (!o.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                o.setAccessible(true);
                return true;
            } catch (SecurityException e) {
            }
        }
        return false;
    }

    static boolean isPackageAccess(int modifiers) {
        return (modifiers & 7) == 0;
    }

    static boolean isAccessible(Member m) {
        return m != null && Modifier.isPublic(m.getModifiers()) && !m.isSynthetic();
    }

    static int compareConstructorFit(Constructor<?> left, Constructor<?> right, Class<?>[] actual) {
        return compareParameterTypes(Executable.m45of(left), Executable.m45of(right), actual);
    }

    static int compareMethodFit(Method left, Method right, Class<?>[] actual) {
        return compareParameterTypes(Executable.m46of(left), Executable.m46of(right), actual);
    }

    private static int compareParameterTypes(Executable left, Executable right, Class<?>[] actual) {
        float leftCost = getTotalTransformationCost(actual, left);
        float rightCost = getTotalTransformationCost(actual, right);
        if (leftCost < rightCost) {
            return -1;
        }
        return rightCost < leftCost ? 1 : 0;
    }

    private static float getTotalTransformationCost(Class<?>[] srcArgs, Executable executable) {
        Class<?>[] destArgs = executable.getParameterTypes();
        boolean isVarArgs = executable.isVarArgs();
        float totalCost = 0.0f;
        int length = destArgs.length;
        if (isVarArgs) {
            length--;
        }
        long normalArgsLen = (long) length;
        if (((long) srcArgs.length) < normalArgsLen) {
            return Float.MAX_VALUE;
        }
        for (int i = 0; ((long) i) < normalArgsLen; i++) {
            totalCost += getObjectTransformationCost(srcArgs[i], destArgs[i]);
        }
        if (!isVarArgs) {
            return totalCost;
        }
        boolean z = false;
        boolean noVarArgsPassed = srcArgs.length < destArgs.length;
        if (srcArgs.length == destArgs.length && srcArgs[srcArgs.length - 1].isArray()) {
            z = true;
        }
        boolean explicitArrayForVarags = z;
        Class<?> destClass = destArgs[destArgs.length - 1].getComponentType();
        if (noVarArgsPassed) {
            return totalCost + getObjectTransformationCost(destClass, Object.class) + 0.001f;
        }
        if (explicitArrayForVarags) {
            return totalCost + getObjectTransformationCost(srcArgs[srcArgs.length - 1].getComponentType(), destClass) + 0.001f;
        }
        for (int i2 = destArgs.length - 1; i2 < srcArgs.length; i2++) {
            totalCost += getObjectTransformationCost(srcArgs[i2], destClass) + 0.001f;
        }
        return totalCost;
    }

    private static float getObjectTransformationCost(Class<?> srcClass, Class<?> destClass) {
        if (destClass.isPrimitive()) {
            return getPrimitivePromotionCost(srcClass, destClass);
        }
        float cost = 0.0f;
        Class<? super Object> srcClass2 = srcClass;
        while (true) {
            if (srcClass2 != null && !destClass.equals(srcClass2)) {
                if (destClass.isInterface() && ClassUtils.isAssignable(srcClass2, destClass)) {
                    cost += 0.25f;
                    break;
                }
                cost += 1.0f;
                srcClass2 = srcClass2.getSuperclass();
            } else {
                break;
            }
        }
        if (srcClass2 == null) {
            return cost + 1.5f;
        }
        return cost;
    }

    private static float getPrimitivePromotionCost(Class<?> srcClass, Class<?> destClass) {
        float cost = 0.0f;
        Class<?> cls = srcClass;
        if (!cls.isPrimitive()) {
            cost = 0.0f + 0.1f;
            cls = ClassUtils.wrapperToPrimitive(cls);
        }
        int i = 0;
        while (cls != destClass) {
            Class<?>[] clsArr = ORDERED_PRIMITIVE_TYPES;
            if (i >= clsArr.length) {
                break;
            }
            if (cls == clsArr[i]) {
                cost += 0.1f;
                if (i < clsArr.length - 1) {
                    cls = clsArr[i + 1];
                }
            }
            i++;
        }
        return cost;
    }

    static boolean isMatchingMethod(Method method, Class<?>[] parameterTypes) {
        return isMatchingExecutable(Executable.m46of(method), parameterTypes);
    }

    static boolean isMatchingConstructor(Constructor<?> method, Class<?>[] parameterTypes) {
        return isMatchingExecutable(Executable.m45of(method), parameterTypes);
    }

    private static boolean isMatchingExecutable(Executable method, Class<?>[] parameterTypes) {
        Class<?>[] methodParameterTypes = method.getParameterTypes();
        if (ClassUtils.isAssignable(parameterTypes, methodParameterTypes, true)) {
            return true;
        }
        if (!method.isVarArgs()) {
            return false;
        }
        int i = 0;
        while (i < methodParameterTypes.length - 1 && i < parameterTypes.length) {
            if (!ClassUtils.isAssignable(parameterTypes[i], methodParameterTypes[i], true)) {
                return false;
            }
            i++;
        }
        Class<?> varArgParameterType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        while (i < parameterTypes.length) {
            if (!ClassUtils.isAssignable(parameterTypes[i], varArgParameterType, true)) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static final class Executable {
        private final boolean isVarArgs;
        private final Class<?>[] parameterTypes;

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static Executable m46of(Method method) {
            return new Executable(method);
        }

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static Executable m45of(Constructor<?> constructor) {
            return new Executable(constructor);
        }

        private Executable(Method method) {
            this.parameterTypes = method.getParameterTypes();
            this.isVarArgs = method.isVarArgs();
        }

        private Executable(Constructor<?> constructor) {
            this.parameterTypes = constructor.getParameterTypes();
            this.isVarArgs = constructor.isVarArgs();
        }

        public Class<?>[] getParameterTypes() {
            return this.parameterTypes;
        }

        public boolean isVarArgs() {
            return this.isVarArgs;
        }
    }
}
