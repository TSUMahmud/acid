package p005ch.qos.logback.classic.html;

import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.spi.IThrowableProxy;
import p005ch.qos.logback.classic.spi.StackTraceElementProxy;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.helpers.Transform;
import p005ch.qos.logback.core.html.IThrowableRenderer;

/* renamed from: ch.qos.logback.classic.html.DefaultThrowableRenderer */
public class DefaultThrowableRenderer implements IThrowableRenderer<ILoggingEvent> {
    static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";

    public void printFirstLine(StringBuilder sb, IThrowableProxy iThrowableProxy) {
        if (iThrowableProxy.getCommonFrames() > 0) {
            sb.append("<br />");
            sb.append(CoreConstants.CAUSED_BY);
        }
        sb.append(iThrowableProxy.getClassName());
        sb.append(": ");
        sb.append(Transform.escapeTags(iThrowableProxy.getMessage()));
        sb.append(CoreConstants.LINE_SEPARATOR);
    }

    public void render(StringBuilder sb, ILoggingEvent iLoggingEvent) {
        sb.append("<tr><td class=\"Exception\" colspan=\"6\">");
        for (IThrowableProxy throwableProxy = iLoggingEvent.getThrowableProxy(); throwableProxy != null; throwableProxy = throwableProxy.getCause()) {
            render(sb, throwableProxy);
        }
        sb.append("</td></tr>");
    }

    /* access modifiers changed from: package-private */
    public void render(StringBuilder sb, IThrowableProxy iThrowableProxy) {
        printFirstLine(sb, iThrowableProxy);
        int commonFrames = iThrowableProxy.getCommonFrames();
        StackTraceElementProxy[] stackTraceElementProxyArray = iThrowableProxy.getStackTraceElementProxyArray();
        for (int i = 0; i < stackTraceElementProxyArray.length - commonFrames; i++) {
            StackTraceElementProxy stackTraceElementProxy = stackTraceElementProxyArray[i];
            sb.append(TRACE_PREFIX);
            sb.append(Transform.escapeTags(stackTraceElementProxy.toString()));
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0) {
            sb.append(TRACE_PREFIX);
            sb.append("\t... ");
            sb.append(commonFrames);
            sb.append(" common frames omitted");
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
    }
}
