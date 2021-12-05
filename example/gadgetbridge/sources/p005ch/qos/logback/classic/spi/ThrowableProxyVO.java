package p005ch.qos.logback.classic.spi;

import java.io.Serializable;
import java.util.Arrays;

/* renamed from: ch.qos.logback.classic.spi.ThrowableProxyVO */
public class ThrowableProxyVO implements IThrowableProxy, Serializable {
    private static final long serialVersionUID = -773438177285807139L;
    private IThrowableProxy cause;
    private String className;
    private int commonFramesCount;
    private String message;
    private StackTraceElementProxy[] stackTraceElementProxyArray;
    private IThrowableProxy[] suppressed;

    public static ThrowableProxyVO build(IThrowableProxy iThrowableProxy) {
        if (iThrowableProxy == null) {
            return null;
        }
        ThrowableProxyVO throwableProxyVO = new ThrowableProxyVO();
        throwableProxyVO.className = iThrowableProxy.getClassName();
        throwableProxyVO.message = iThrowableProxy.getMessage();
        throwableProxyVO.commonFramesCount = iThrowableProxy.getCommonFrames();
        throwableProxyVO.stackTraceElementProxyArray = iThrowableProxy.getStackTraceElementProxyArray();
        IThrowableProxy cause2 = iThrowableProxy.getCause();
        if (cause2 != null) {
            throwableProxyVO.cause = build(cause2);
        }
        IThrowableProxy[] suppressed2 = iThrowableProxy.getSuppressed();
        if (suppressed2 != null) {
            throwableProxyVO.suppressed = new IThrowableProxy[suppressed2.length];
            for (int i = 0; i < suppressed2.length; i++) {
                throwableProxyVO.suppressed[i] = build(suppressed2[i]);
            }
        }
        return throwableProxyVO;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ThrowableProxyVO throwableProxyVO = (ThrowableProxyVO) obj;
        String str = this.className;
        if (str == null) {
            if (throwableProxyVO.className != null) {
                return false;
            }
        } else if (!str.equals(throwableProxyVO.className)) {
            return false;
        }
        if (!Arrays.equals(this.stackTraceElementProxyArray, throwableProxyVO.stackTraceElementProxyArray) || !Arrays.equals(this.suppressed, throwableProxyVO.suppressed)) {
            return false;
        }
        IThrowableProxy iThrowableProxy = this.cause;
        IThrowableProxy iThrowableProxy2 = throwableProxyVO.cause;
        if (iThrowableProxy == null) {
            if (iThrowableProxy2 != null) {
                return false;
            }
        } else if (!iThrowableProxy.equals(iThrowableProxy2)) {
            return false;
        }
        return true;
    }

    public IThrowableProxy getCause() {
        return this.cause;
    }

    public String getClassName() {
        return this.className;
    }

    public int getCommonFrames() {
        return this.commonFramesCount;
    }

    public String getMessage() {
        return this.message;
    }

    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return this.stackTraceElementProxyArray;
    }

    public IThrowableProxy[] getSuppressed() {
        return this.suppressed;
    }

    public int hashCode() {
        String str = this.className;
        return 31 + (str == null ? 0 : str.hashCode());
    }
}
