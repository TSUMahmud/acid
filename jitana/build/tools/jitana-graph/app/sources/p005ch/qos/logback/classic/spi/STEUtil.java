package p005ch.qos.logback.classic.spi;

/* renamed from: ch.qos.logback.classic.spi.STEUtil */
public class STEUtil {
    static int findNumberOfCommonFrames(StackTraceElement[] stackTraceElementArr, StackTraceElementProxy[] stackTraceElementProxyArr) {
        int i = 0;
        if (stackTraceElementProxyArr == null) {
            return 0;
        }
        int length = stackTraceElementArr.length - 1;
        int length2 = stackTraceElementProxyArr.length - 1;
        while (length >= 0 && length2 >= 0 && stackTraceElementArr[length].equals(stackTraceElementProxyArr[length2].ste)) {
            i++;
            length--;
            length2--;
        }
        return i;
    }
}
