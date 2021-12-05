package p005ch.qos.logback.classic.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.classic.spi.ThrowableProxy */
public class ThrowableProxy implements IThrowableProxy {
    private static final Method GET_SUPPRESSED_METHOD;
    private static final ThrowableProxy[] NO_SUPPRESSED = new ThrowableProxy[0];
    private boolean calculatedPackageData;
    private ThrowableProxy cause;
    private String className;
    int commonFrames;
    private String message;
    private transient PackagingDataCalculator packagingDataCalculator;
    StackTraceElementProxy[] stackTraceElementProxyArray;
    private ThrowableProxy[] suppressed = NO_SUPPRESSED;
    private Throwable throwable;

    static {
        Method method;
        try {
            method = Throwable.class.getMethod("getSuppressed", new Class[0]);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        GET_SUPPRESSED_METHOD = method;
    }

    public ThrowableProxy(Throwable th) {
        this.calculatedPackageData = false;
        this.throwable = th;
        this.className = th.getClass().getName();
        this.message = th.getMessage();
        this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(th.getStackTrace());
        Throwable cause2 = th.getCause();
        if (cause2 != null) {
            this.cause = new ThrowableProxy(cause2);
            this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(cause2.getStackTrace(), this.stackTraceElementProxyArray);
        }
        Method method = GET_SUPPRESSED_METHOD;
        if (method != null) {
            try {
                Object invoke = method.invoke(th, new Object[0]);
                if (invoke instanceof Throwable[]) {
                    Throwable[] thArr = (Throwable[]) invoke;
                    if (thArr.length > 0) {
                        this.suppressed = new ThrowableProxy[thArr.length];
                        for (int i = 0; i < thArr.length; i++) {
                            this.suppressed[i] = new ThrowableProxy(thArr[i]);
                            this.suppressed[i].commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(thArr[i].getStackTrace(), this.stackTraceElementProxyArray);
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
    }

    public void calculatePackagingData() {
        PackagingDataCalculator packagingDataCalculator2;
        if (!this.calculatedPackageData && (packagingDataCalculator2 = getPackagingDataCalculator()) != null) {
            this.calculatedPackageData = true;
            packagingDataCalculator2.calculate(this);
        }
    }

    public void fullDump() {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElementProxy stackTraceElementProxy : this.stackTraceElementProxyArray) {
            String stackTraceElementProxy2 = stackTraceElementProxy.toString();
            sb.append(9);
            sb.append(stackTraceElementProxy2);
            ThrowableProxyUtil.subjoinPackagingData(sb, stackTraceElementProxy);
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        System.out.println(sb.toString());
    }

    public IThrowableProxy getCause() {
        return this.cause;
    }

    public String getClassName() {
        return this.className;
    }

    public int getCommonFrames() {
        return this.commonFrames;
    }

    public String getMessage() {
        return this.message;
    }

    public PackagingDataCalculator getPackagingDataCalculator() {
        if (this.throwable != null && this.packagingDataCalculator == null) {
            this.packagingDataCalculator = new PackagingDataCalculator();
        }
        return this.packagingDataCalculator;
    }

    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return this.stackTraceElementProxyArray;
    }

    public IThrowableProxy[] getSuppressed() {
        return this.suppressed;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
