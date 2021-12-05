package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.IThrowableProxy;
import p005ch.qos.logback.classic.spi.ThrowableProxyUtil;
import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter */
public class RootCauseFirstThrowableProxyConverter extends ExtendedThrowableProxyConverter {
    /* access modifiers changed from: protected */
    public void recursiveAppendRootCauseFirst(StringBuilder sb, String str, int i, IThrowableProxy iThrowableProxy) {
        if (iThrowableProxy.getCause() != null) {
            recursiveAppendRootCauseFirst(sb, str, i, iThrowableProxy.getCause());
            str = null;
        }
        ThrowableProxyUtil.indent(sb, i - 1);
        if (str != null) {
            sb.append(str);
        }
        ThrowableProxyUtil.subjoinFirstLineRootCauseFirst(sb, iThrowableProxy);
        sb.append(CoreConstants.LINE_SEPARATOR);
        subjoinSTEPArray(sb, i, iThrowableProxy);
        IThrowableProxy[] suppressed = iThrowableProxy.getSuppressed();
        if (suppressed != null) {
            for (IThrowableProxy recursiveAppendRootCauseFirst : suppressed) {
                recursiveAppendRootCauseFirst(sb, CoreConstants.SUPPRESSED, i + 1, recursiveAppendRootCauseFirst);
            }
        }
    }

    /* access modifiers changed from: protected */
    public String throwableProxyToString(IThrowableProxy iThrowableProxy) {
        StringBuilder sb = new StringBuilder(2048);
        recursiveAppendRootCauseFirst(sb, (String) null, 1, iThrowableProxy);
        return sb.toString();
    }
}
