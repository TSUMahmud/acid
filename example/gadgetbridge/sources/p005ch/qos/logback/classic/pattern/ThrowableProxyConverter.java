package p005ch.qos.logback.classic.pattern;

import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.spi.IThrowableProxy;
import p005ch.qos.logback.classic.spi.StackTraceElementProxy;
import p005ch.qos.logback.classic.spi.ThrowableProxyUtil;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.boolex.EvaluationException;
import p005ch.qos.logback.core.boolex.EventEvaluator;
import p005ch.qos.logback.core.status.ErrorStatus;

/* renamed from: ch.qos.logback.classic.pattern.ThrowableProxyConverter */
public class ThrowableProxyConverter extends ThrowableHandlingConverter {
    protected static final int BUILDER_CAPACITY = 2048;
    int errorCount = 0;
    List<EventEvaluator<ILoggingEvent>> evaluatorList = null;
    int lengthOption;

    private void addEvaluator(EventEvaluator<ILoggingEvent> eventEvaluator) {
        if (this.evaluatorList == null) {
            this.evaluatorList = new ArrayList();
        }
        this.evaluatorList.add(eventEvaluator);
    }

    private void recursiveAppend(StringBuilder sb, String str, int i, IThrowableProxy iThrowableProxy) {
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

    private void subjoinExceptionMessage(StringBuilder sb, IThrowableProxy iThrowableProxy) {
        sb.append(iThrowableProxy.getClassName());
        sb.append(": ");
        sb.append(iThrowableProxy.getMessage());
    }

    private void subjoinFirstLine(StringBuilder sb, String str, int i, IThrowableProxy iThrowableProxy) {
        ThrowableProxyUtil.indent(sb, i - 1);
        if (str != null) {
            sb.append(str);
        }
        subjoinExceptionMessage(sb, iThrowableProxy);
    }

    public String convert(ILoggingEvent iLoggingEvent) {
        IThrowableProxy throwableProxy = iLoggingEvent.getThrowableProxy();
        if (throwableProxy == null) {
            return "";
        }
        if (this.evaluatorList != null) {
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= this.evaluatorList.size()) {
                    z = true;
                    break;
                }
                EventEvaluator eventEvaluator = this.evaluatorList.get(i);
                try {
                    if (eventEvaluator.evaluate(iLoggingEvent)) {
                        break;
                    }
                    i++;
                } catch (EvaluationException e) {
                    this.errorCount++;
                    int i2 = this.errorCount;
                    if (i2 < 4) {
                        addError("Exception thrown for evaluator named [" + eventEvaluator.getName() + "]", e);
                    } else if (i2 == 4) {
                        ErrorStatus errorStatus = new ErrorStatus("Exception thrown for evaluator named [" + eventEvaluator.getName() + "].", this, e);
                        errorStatus.add(new ErrorStatus("This was the last warning about this evaluator's errors.We don't want the StatusManager to get flooded.", this));
                        addStatus(errorStatus);
                    }
                }
            }
            if (!z) {
                return "";
            }
        }
        return throwableProxyToString(throwableProxy);
    }

    /* access modifiers changed from: protected */
    public void extraData(StringBuilder sb, StackTraceElementProxy stackTraceElementProxy) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0059 A[LOOP:0: B:17:0x0057->B:18:0x0059, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void start() {
        /*
            r6 = this;
            java.lang.String r0 = r6.getFirstOption()
            r1 = 2147483647(0x7fffffff, float:NaN)
            r2 = 1
            if (r0 != 0) goto L_0x000d
        L_0x000a:
            r6.lengthOption = r1
            goto L_0x0047
        L_0x000d:
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r3 = "full"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x001a
            goto L_0x000a
        L_0x001a:
            java.lang.String r3 = "short"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x0025
            r6.lengthOption = r2
            goto L_0x0047
        L_0x0025:
            int r3 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x002c }
            r6.lengthOption = r3     // Catch:{ NumberFormatException -> 0x002c }
            goto L_0x0047
        L_0x002c:
            r3 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Could not parse ["
            r3.append(r4)
            r3.append(r0)
            java.lang.String r0 = "] as an integer"
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r6.addError(r0)
            goto L_0x000a
        L_0x0047:
            java.util.List r0 = r6.getOptionList()
            if (r0 == 0) goto L_0x0077
            int r1 = r0.size()
            if (r1 <= r2) goto L_0x0077
            int r1 = r0.size()
        L_0x0057:
            if (r2 >= r1) goto L_0x0077
            java.lang.Object r3 = r0.get(r2)
            java.lang.String r3 = (java.lang.String) r3
            ch.qos.logback.core.Context r4 = r6.getContext()
            java.lang.String r5 = "EVALUATOR_MAP"
            java.lang.Object r4 = r4.getObject(r5)
            java.util.Map r4 = (java.util.Map) r4
            java.lang.Object r3 = r4.get(r3)
            ch.qos.logback.core.boolex.EventEvaluator r3 = (p005ch.qos.logback.core.boolex.EventEvaluator) r3
            r6.addEvaluator(r3)
            int r2 = r2 + 1
            goto L_0x0057
        L_0x0077:
            super.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.classic.pattern.ThrowableProxyConverter.start():void");
    }

    public void stop() {
        this.evaluatorList = null;
        super.stop();
    }

    /* access modifiers changed from: protected */
    public void subjoinSTEPArray(StringBuilder sb, int i, IThrowableProxy iThrowableProxy) {
        StackTraceElementProxy[] stackTraceElementProxyArray = iThrowableProxy.getStackTraceElementProxyArray();
        int commonFrames = iThrowableProxy.getCommonFrames();
        boolean z = this.lengthOption > stackTraceElementProxyArray.length;
        int length = z ? stackTraceElementProxyArray.length : this.lengthOption;
        if (commonFrames > 0 && z) {
            length -= commonFrames;
        }
        for (int i2 = 0; i2 < length; i2++) {
            ThrowableProxyUtil.indent(sb, i);
            sb.append(stackTraceElementProxyArray[i2]);
            extraData(sb, stackTraceElementProxyArray[i2]);
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0 && z) {
            ThrowableProxyUtil.indent(sb, i);
            sb.append("... ");
            sb.append(iThrowableProxy.getCommonFrames());
            sb.append(" common frames omitted");
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
    }

    /* access modifiers changed from: protected */
    public String throwableProxyToString(IThrowableProxy iThrowableProxy) {
        StringBuilder sb = new StringBuilder(2048);
        recursiveAppend(sb, (String) null, 1, iThrowableProxy);
        return sb.toString();
    }
}
