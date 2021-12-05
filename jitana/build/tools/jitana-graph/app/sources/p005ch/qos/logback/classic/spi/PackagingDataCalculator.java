package p005ch.qos.logback.classic.spi;

import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.classic.spi.PackagingDataCalculator */
public class PackagingDataCalculator {
    static final StackTraceElementProxy[] STEP_ARRAY_TEMPLATE = new StackTraceElementProxy[0];
    HashMap<String, ClassPackagingData> cache = new HashMap<>();

    private Class bestEffortLoadClass(ClassLoader classLoader, String str) {
        Class loadClass = loadClass(classLoader, str);
        if (loadClass != null) {
            return loadClass;
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != classLoader) {
            loadClass = loadClass(contextClassLoader, str);
        }
        if (loadClass != null) {
            return loadClass;
        }
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoClassDefFoundError e2) {
            return null;
        } catch (Exception e3) {
            e3.printStackTrace();
            return null;
        }
    }

    private ClassPackagingData computeBySTEP(StackTraceElementProxy stackTraceElementProxy, ClassLoader classLoader) {
        String className = stackTraceElementProxy.ste.getClassName();
        ClassPackagingData classPackagingData = this.cache.get(className);
        if (classPackagingData != null) {
            return classPackagingData;
        }
        Class bestEffortLoadClass = bestEffortLoadClass(classLoader, className);
        ClassPackagingData classPackagingData2 = new ClassPackagingData(getCodeLocation(bestEffortLoadClass), getImplementationVersion(bestEffortLoadClass), false);
        this.cache.put(className, classPackagingData2);
        return classPackagingData2;
    }

    private String getCodeLocation(String str, char c) {
        int lastIndexOf = str.lastIndexOf(c);
        if (isFolder(lastIndexOf, str)) {
            return str.substring(str.lastIndexOf(c, lastIndexOf - 1) + 1);
        }
        if (lastIndexOf > 0) {
            return str.substring(lastIndexOf + 1);
        }
        return null;
    }

    private boolean isFolder(int i, String str) {
        return i != -1 && i + 1 == str.length();
    }

    private Class loadClass(ClassLoader classLoader, String str) {
        if (classLoader == null) {
            return null;
        }
        try {
            return classLoader.loadClass(str);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoClassDefFoundError e2) {
            return null;
        } catch (Exception e3) {
            e3.printStackTrace();
            return null;
        }
    }

    public void calculate(IThrowableProxy iThrowableProxy) {
        while (iThrowableProxy != null) {
            populateFrames(iThrowableProxy.getStackTraceElementProxyArray());
            IThrowableProxy[] suppressed = iThrowableProxy.getSuppressed();
            if (suppressed != null) {
                for (IThrowableProxy stackTraceElementProxyArray : suppressed) {
                    populateFrames(stackTraceElementProxyArray.getStackTraceElementProxyArray());
                }
            }
            iThrowableProxy = iThrowableProxy.getCause();
        }
    }

    /* access modifiers changed from: package-private */
    public String getCodeLocation(Class cls) {
        URL location;
        if (cls == null) {
            return "na";
        }
        try {
            CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
            if (codeSource == null || (location = codeSource.getLocation()) == null) {
                return "na";
            }
            String url = location.toString();
            String codeLocation = getCodeLocation(url, '/');
            return codeLocation != null ? codeLocation : getCodeLocation(url, CoreConstants.ESCAPE_CHAR);
        } catch (Exception e) {
            return "na";
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000b, code lost:
        r2 = (r2 = r2.getPackage()).getImplementationVersion();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getImplementationVersion(java.lang.Class r2) {
        /*
            r1 = this;
            java.lang.String r0 = "na"
            if (r2 != 0) goto L_0x0005
            return r0
        L_0x0005:
            java.lang.Package r2 = r2.getPackage()
            if (r2 == 0) goto L_0x0013
            java.lang.String r2 = r2.getImplementationVersion()
            if (r2 != 0) goto L_0x0012
            return r0
        L_0x0012:
            return r2
        L_0x0013:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.classic.spi.PackagingDataCalculator.getImplementationVersion(java.lang.Class):java.lang.String");
    }

    /* access modifiers changed from: package-private */
    public void populateFrames(StackTraceElementProxy[] stackTraceElementProxyArr) {
        int findNumberOfCommonFrames = STEUtil.findNumberOfCommonFrames(new Throwable("local stack reference").getStackTrace(), stackTraceElementProxyArr);
        int length = stackTraceElementProxyArr.length - findNumberOfCommonFrames;
        for (int i = 0; i < findNumberOfCommonFrames; i++) {
            StackTraceElementProxy stackTraceElementProxy = stackTraceElementProxyArr[length + i];
            stackTraceElementProxy.setClassPackagingData(computeBySTEP(stackTraceElementProxy, (ClassLoader) null));
        }
        populateUncommonFrames(findNumberOfCommonFrames, stackTraceElementProxyArr, (ClassLoader) null);
    }

    /* access modifiers changed from: package-private */
    public void populateUncommonFrames(int i, StackTraceElementProxy[] stackTraceElementProxyArr, ClassLoader classLoader) {
        int length = stackTraceElementProxyArr.length - i;
        for (int i2 = 0; i2 < length; i2++) {
            StackTraceElementProxy stackTraceElementProxy = stackTraceElementProxyArr[i2];
            stackTraceElementProxy.setClassPackagingData(computeBySTEP(stackTraceElementProxy, classLoader));
        }
    }
}
