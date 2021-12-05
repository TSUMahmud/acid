package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

public class EqualsBuilder implements Builder<Boolean> {
    private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal<>();
    private String[] excludeFields = null;
    private boolean isEquals = true;
    private Class<?> reflectUpToClass = null;
    private boolean testRecursive = false;
    private boolean testTransients = false;

    static Set<Pair<IDKey, IDKey>> getRegistry() {
        return REGISTRY.get();
    }

    static Pair<IDKey, IDKey> getRegisterPair(Object lhs, Object rhs) {
        return Pair.m51of(new IDKey(lhs), new IDKey(rhs));
    }

    static boolean isRegistered(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
        return registry != null && (registry.contains(pair) || registry.contains(Pair.m51of(pair.getRight(), pair.getLeft())));
    }

    private static void register(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        if (registry == null) {
            registry = new HashSet<>();
            REGISTRY.set(registry);
        }
        registry.add(getRegisterPair(lhs, rhs));
    }

    private static void unregister(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        if (registry != null) {
            registry.remove(getRegisterPair(lhs, rhs));
            if (registry.isEmpty()) {
                REGISTRY.remove();
            }
        }
    }

    public EqualsBuilder setTestTransients(boolean testTransients2) {
        this.testTransients = testTransients2;
        return this;
    }

    public EqualsBuilder setTestRecursive(boolean testRecursive2) {
        this.testRecursive = testRecursive2;
        return this;
    }

    public EqualsBuilder setReflectUpToClass(Class<?> reflectUpToClass2) {
        this.reflectUpToClass = reflectUpToClass2;
        return this;
    }

    public EqualsBuilder setExcludeFields(String... excludeFields2) {
        this.excludeFields = excludeFields2;
        return this;
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, Collection<String> excludeFields2) {
        return reflectionEquals(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray(excludeFields2));
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, String... excludeFields2) {
        return reflectionEquals(lhs, rhs, false, (Class<?>) null, excludeFields2);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients2) {
        return reflectionEquals(lhs, rhs, testTransients2, (Class<?>) null, new String[0]);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients2, Class<?> reflectUpToClass2, String... excludeFields2) {
        return reflectionEquals(lhs, rhs, testTransients2, reflectUpToClass2, false, excludeFields2);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients2, Class<?> reflectUpToClass2, boolean testRecursive2, String... excludeFields2) {
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        return new EqualsBuilder().setExcludeFields(excludeFields2).setReflectUpToClass(reflectUpToClass2).setTestTransients(testTransients2).setTestRecursive(testRecursive2).reflectionAppend(lhs, rhs).isEquals();
    }

    public EqualsBuilder reflectionAppend(Object lhs, Object rhs) {
        Class<? super Object> testClass;
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        Class<?> lhsClass = lhs.getClass();
        Class<?> rhsClass = rhs.getClass();
        if (lhsClass.isInstance(rhs)) {
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {
                testClass = rhsClass;
            }
        } else if (rhsClass.isInstance(lhs)) {
            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {
                testClass = lhsClass;
            }
        } else {
            this.isEquals = false;
            return this;
        }
        try {
            if (testClass.isArray()) {
                append(lhs, rhs);
            } else {
                reflectionAppend(lhs, rhs, testClass);
                while (testClass.getSuperclass() != null && testClass != this.reflectUpToClass) {
                    testClass = testClass.getSuperclass();
                    reflectionAppend(lhs, rhs, testClass);
                }
            }
            return this;
        } catch (IllegalArgumentException e) {
            this.isEquals = false;
            return this;
        }
    }

    private void reflectionAppend(Object lhs, Object rhs, Class<?> clazz) {
        if (!isRegistered(lhs, rhs)) {
            try {
                register(lhs, rhs);
                Field[] fields = clazz.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);
                for (int i = 0; i < fields.length && this.isEquals; i++) {
                    Field f = fields[i];
                    if (!ArrayUtils.contains((Object[]) this.excludeFields, (Object) f.getName()) && !f.getName().contains("$") && ((this.testTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()) && !f.isAnnotationPresent(EqualsExclude.class))) {
                        append(f.get(lhs), f.get(rhs));
                    }
                }
                unregister(lhs, rhs);
            } catch (IllegalAccessException e) {
                throw new InternalError("Unexpected IllegalAccessException");
            } catch (Throwable th) {
                unregister(lhs, rhs);
                throw th;
            }
        }
    }

    public EqualsBuilder appendSuper(boolean superEquals) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = superEquals;
        return this;
    }

    public EqualsBuilder append(Object lhs, Object rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        Class<?> lhsClass = lhs.getClass();
        if (lhsClass.isArray()) {
            appendArray(lhs, rhs);
        } else if (!this.testRecursive || ClassUtils.isPrimitiveOrWrapper(lhsClass)) {
            this.isEquals = lhs.equals(rhs);
        } else {
            reflectionAppend(lhs, rhs);
        }
        return this;
    }

    private void appendArray(Object lhs, Object rhs) {
        if (lhs.getClass() != rhs.getClass()) {
            setEquals(false);
        } else if (lhs instanceof long[]) {
            append((long[]) lhs, (long[]) rhs);
        } else if (lhs instanceof int[]) {
            append((int[]) lhs, (int[]) rhs);
        } else if (lhs instanceof short[]) {
            append((short[]) lhs, (short[]) rhs);
        } else if (lhs instanceof char[]) {
            append((char[]) lhs, (char[]) rhs);
        } else if (lhs instanceof byte[]) {
            append((byte[]) lhs, (byte[]) rhs);
        } else if (lhs instanceof double[]) {
            append((double[]) lhs, (double[]) rhs);
        } else if (lhs instanceof float[]) {
            append((float[]) lhs, (float[]) rhs);
        } else if (lhs instanceof boolean[]) {
            append((boolean[]) lhs, (boolean[]) rhs);
        } else {
            append((Object[]) lhs, (Object[]) rhs);
        }
    }

    public EqualsBuilder append(long lhs, long rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(int lhs, int rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(short lhs, short rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(char lhs, char rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(byte lhs, byte rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(double lhs, double rhs) {
        if (!this.isEquals) {
            return this;
        }
        return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
    }

    public EqualsBuilder append(float lhs, float rhs) {
        if (!this.isEquals) {
            return this;
        }
        return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
    }

    public EqualsBuilder append(boolean lhs, boolean rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(Object[] lhs, Object[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(long[] lhs, long[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(int[] lhs, int[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(short[] lhs, short[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(char[] lhs, char[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(byte[] lhs, byte[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(double[] lhs, double[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(float[] lhs, float[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public EqualsBuilder append(boolean[] lhs, boolean[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        } else if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        } else {
            for (int i = 0; i < lhs.length && this.isEquals; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public boolean isEquals() {
        return this.isEquals;
    }

    public Boolean build() {
        return Boolean.valueOf(isEquals());
    }

    /* access modifiers changed from: protected */
    public void setEquals(boolean isEquals2) {
        this.isEquals = isEquals2;
    }

    public void reset() {
        this.isEquals = true;
    }
}
