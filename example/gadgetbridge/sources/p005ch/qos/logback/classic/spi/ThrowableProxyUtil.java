package p005ch.qos.logback.classic.spi;

import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.classic.spi.ThrowableProxyUtil */
public class ThrowableProxyUtil {
    private static final int BUILDER_CAPACITY = 2048;
    public static final int REGULAR_EXCEPTION_INDENT = 1;
    public static final int SUPPRESSED_EXCEPTION_INDENT = 1;

    public static String asString(IThrowableProxy iThrowableProxy) {
        StringBuilder sb = new StringBuilder(2048);
        recursiveAppend(sb, (String) null, 1, iThrowableProxy);
        return sb.toString();
    }

    public static void build(ThrowableProxy throwableProxy, Throwable th, ThrowableProxy throwableProxy2) {
        StackTraceElement[] stackTrace = th.getStackTrace();
        throwableProxy.commonFrames = throwableProxy2 != null ? findNumberOfCommonFrames(stackTrace, throwableProxy2.getStackTraceElementProxyArray()) : -1;
        throwableProxy.stackTraceElementProxyArray = steArrayToStepArray(stackTrace);
    }

    static int findNumberOfCommonFrames(StackTraceElement[] stackTraceElementArr, StackTraceElementProxy[] stackTraceElementProxyArr) {
        int i = 0;
        if (!(stackTraceElementProxyArr == null || stackTraceElementArr == null)) {
            int length = stackTraceElementArr.length - 1;
            int length2 = stackTraceElementProxyArr.length - 1;
            while (length >= 0 && length2 >= 0 && stackTraceElementArr[length].equals(stackTraceElementProxyArr[length2].ste)) {
                i++;
                length--;
                length2--;
            }
        }
        return i;
    }

    public static void indent(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(9);
        }
    }

    private static void recursiveAppend(StringBuilder sb, String str, int i, IThrowableProxy iThrowableProxy) {
        if (iThrowableProxy != null) {
            subjoinFirstLine(sb, str, i, iThrowableProxy);
            sb.append(CoreConstants.LINE_SEPARATOR);
            subjoinSTEPArray(sb, i, iThrowableProxy);
            IThrowableProxy[] suppressed = iThrowableProxy.getSuppressed();
            if (suppressed != null) {
                for (IThrowableProxy recursiveAppend : suppressed) {
                    recursiveAppend(sb, CoreConstants.SUPPRESSED, i + 1, recursiveAppend);
                }
            }
            recursiveAppend(sb, CoreConstants.CAUSED_BY, i, iThrowableProxy.getCause());
        }
    }

    static StackTraceElementProxy[] steArrayToStepArray(StackTraceElement[] stackTraceElementArr) {
        if (stackTraceElementArr == null) {
            return new StackTraceElementProxy[0];
        }
        StackTraceElementProxy[] stackTraceElementProxyArr = new StackTraceElementProxy[stackTraceElementArr.length];
        for (int i = 0; i < stackTraceElementProxyArr.length; i++) {
            stackTraceElementProxyArr[i] = new StackTraceElementProxy(stackTraceElementArr[i]);
        }
        return stackTraceElementProxyArr;
    }

    private static void subjoinExceptionMessage(StringBuilder sb, IThrowableProxy iThrowableProxy) {
        sb.append(iThrowableProxy.getClassName());
        sb.append(": ");
        sb.append(iThrowableProxy.getMessage());
    }

    public static void subjoinFirstLine(StringBuilder sb, IThrowableProxy iThrowableProxy) {
        if (iThrowableProxy.getCommonFrames() > 0) {
            sb.append(CoreConstants.CAUSED_BY);
        }
        subjoinExceptionMessage(sb, iThrowableProxy);
    }

    private static void subjoinFirstLine(StringBuilder sb, String str, int i, IThrowableProxy iThrowableProxy) {
        indent(sb, i - 1);
        if (str != null) {
            sb.append(str);
        }
        subjoinExceptionMessage(sb, iThrowableProxy);
    }

    public static void subjoinFirstLineRootCauseFirst(StringBuilder sb, IThrowableProxy iThrowableProxy) {
        if (iThrowableProxy.getCause() != null) {
            sb.append(CoreConstants.WRAPPED_BY);
        }
        subjoinExceptionMessage(sb, iThrowableProxy);
    }

    public static void subjoinPackagingData(StringBuilder sb, StackTraceElementProxy stackTraceElementProxy) {
        ClassPackagingData classPackagingData;
        if (stackTraceElementProxy != null && (classPackagingData = stackTraceElementProxy.getClassPackagingData()) != null) {
            sb.append(!classPackagingData.isExact() ? " ~[" : " [");
            sb.append(classPackagingData.getCodeLocation());
            sb.append(CoreConstants.COLON_CHAR);
            sb.append(classPackagingData.getVersion());
            sb.append(']');
        }
    }

    public static void subjoinSTEP(StringBuilder sb, StackTraceElementProxy stackTraceElementProxy) {
        sb.append(stackTraceElementProxy.toString());
        subjoinPackagingData(sb, stackTraceElementProxy);
    }

    public static void subjoinSTEPArray(StringBuilder sb, int i, IThrowableProxy iThrowableProxy) {
        StackTraceElementProxy[] stackTraceElementProxyArray = iThrowableProxy.getStackTraceElementProxyArray();
        int commonFrames = iThrowableProxy.getCommonFrames();
        for (int i2 = 0; i2 < stackTraceElementProxyArray.length - commonFrames; i2++) {
            StackTraceElementProxy stackTraceElementProxy = stackTraceElementProxyArray[i2];
            indent(sb, i);
            subjoinSTEP(sb, stackTraceElementProxy);
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0) {
            indent(sb, i);
            sb.append("... ");
            sb.append(commonFrames);
            sb.append(" common frames omitted");
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
    }

    public static void subjoinSTEPArray(StringBuilder sb, IThrowableProxy iThrowableProxy) {
        subjoinSTEPArray(sb, 1, iThrowableProxy);
    }
}
