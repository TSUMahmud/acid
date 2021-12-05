package p005ch.qos.logback.core.util;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import p005ch.qos.logback.core.Context;

/* renamed from: ch.qos.logback.core.util.Loader */
public class Loader {
    private static boolean HAS_GET_CLASS_LOADER_PERMISSION = false;
    public static final String IGNORE_TCL_PROPERTY_NAME = "logback.ignoreTCL";
    static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
    private static boolean ignoreTCL;

    static {
        ignoreTCL = false;
        HAS_GET_CLASS_LOADER_PERMISSION = false;
        String systemProperty = OptionHelper.getSystemProperty(IGNORE_TCL_PROPERTY_NAME, (String) null);
        if (systemProperty != null) {
            ignoreTCL = Boolean.valueOf(systemProperty).booleanValue();
        }
        HAS_GET_CLASS_LOADER_PERMISSION = ((Boolean) AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                try {
                    AccessController.checkPermission(new RuntimePermission("getClassLoader"));
                    return true;
                } catch (SecurityException e) {
                    return false;
                }
            }
        })).booleanValue();
    }

    public static ClassLoader getClassLoaderAsPrivileged(final Class<?> cls) {
        if (!HAS_GET_CLASS_LOADER_PERMISSION) {
            return null;
        }
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return cls.getClassLoader();
            }
        });
    }

    public static ClassLoader getClassLoaderOfClass(Class<?> cls) {
        ClassLoader classLoader = cls.getClassLoader();
        return classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
    }

    public static ClassLoader getClassLoaderOfObject(Object obj) {
        if (obj != null) {
            return getClassLoaderOfClass(obj.getClass());
        }
        throw new NullPointerException("Argument cannot be null");
    }

    public static URL getResource(String str, ClassLoader classLoader) {
        try {
            return classLoader.getResource(str);
        } catch (Throwable th) {
            return null;
        }
    }

    public static URL getResourceBySelfClassLoader(String str) {
        return getResource(str, getClassLoaderOfClass(Loader.class));
    }

    public static Set<URL> getResourceOccurrenceCount(String str, ClassLoader classLoader) throws IOException {
        HashSet hashSet = new HashSet();
        Enumeration<URL> resources = classLoader.getResources(str);
        while (resources.hasMoreElements()) {
            hashSet.add(resources.nextElement());
        }
        return hashSet;
    }

    public static ClassLoader getTCL() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String str) throws ClassNotFoundException {
        if (ignoreTCL) {
            return Class.forName(str);
        }
        try {
            return getTCL().loadClass(str);
        } catch (Throwable th) {
            return Class.forName(str);
        }
    }

    public static Class<?> loadClass(String str, Context context) throws ClassNotFoundException {
        return getClassLoaderOfObject(context).loadClass(str);
    }
}
