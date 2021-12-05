package p005ch.qos.logback.classic.net;

import java.io.IOException;
import java.io.OutputStream;
import p005ch.qos.logback.classic.PatternLayout;
import p005ch.qos.logback.classic.pattern.SyslogStartConverter;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.spi.IThrowableProxy;
import p005ch.qos.logback.classic.util.LevelToSyslogSeverity;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.Layout;
import p005ch.qos.logback.core.net.SyslogAppenderBase;

/* renamed from: ch.qos.logback.classic.net.SyslogAppender */
public class SyslogAppender extends SyslogAppenderBase<ILoggingEvent> {
    public static final String DEFAULT_STACKTRACE_PATTERN = "\t";
    public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
    PatternLayout stackTraceLayout = new PatternLayout();
    String stackTracePattern = DEFAULT_STACKTRACE_PATTERN;
    boolean throwableExcluded = false;

    private void handleThrowableFirstLine(OutputStream outputStream, IThrowableProxy iThrowableProxy, String str, boolean z) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        if (!z) {
            sb.append(CoreConstants.CAUSED_BY);
        }
        sb.append(iThrowableProxy.getClassName());
        sb.append(": ");
        sb.append(iThrowableProxy.getMessage());
        outputStream.write(sb.toString().getBytes());
        outputStream.flush();
    }

    private void setupStackTraceLayout() {
        this.stackTraceLayout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        PatternLayout patternLayout = this.stackTraceLayout;
        patternLayout.setPattern(getPrefixPattern() + this.stackTracePattern);
        this.stackTraceLayout.setContext(getContext());
        this.stackTraceLayout.start();
    }

    public Layout<ILoggingEvent> buildLayout() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        if (this.suffixPattern == null) {
            this.suffixPattern = DEFAULT_SUFFIX_PATTERN;
        }
        patternLayout.setPattern(getPrefixPattern() + this.suffixPattern);
        patternLayout.setContext(getContext());
        patternLayout.start();
        return patternLayout;
    }

    /* access modifiers changed from: package-private */
    public String getPrefixPattern() {
        return "%syslogStart{" + getFacility() + "}%nopex{}";
    }

    public int getSeverityForEvent(Object obj) {
        return LevelToSyslogSeverity.convert((ILoggingEvent) obj);
    }

    public String getStackTracePattern() {
        return this.stackTracePattern;
    }

    public boolean isThrowableExcluded() {
        return this.throwableExcluded;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0005, code lost:
        r8 = (p005ch.qos.logback.classic.spi.ILoggingEvent) r8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void postProcess(java.lang.Object r8, java.io.OutputStream r9) {
        /*
            r7 = this;
            boolean r0 = r7.throwableExcluded
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            ch.qos.logback.classic.spi.ILoggingEvent r8 = (p005ch.qos.logback.classic.spi.ILoggingEvent) r8
            ch.qos.logback.classic.spi.IThrowableProxy r0 = r8.getThrowableProxy()
            if (r0 != 0) goto L_0x000e
            return
        L_0x000e:
            ch.qos.logback.classic.PatternLayout r1 = r7.stackTraceLayout
            java.lang.String r8 = r1.doLayout((p005ch.qos.logback.classic.spi.ILoggingEvent) r8)
            r1 = 0
            r2 = 1
        L_0x0016:
            if (r0 == 0) goto L_0x0048
            ch.qos.logback.classic.spi.StackTraceElementProxy[] r3 = r0.getStackTraceElementProxyArray()
            r7.handleThrowableFirstLine(r9, r0, r8, r2)     // Catch:{ IOException -> 0x0047 }
            int r2 = r3.length     // Catch:{ IOException -> 0x0047 }
            r4 = 0
        L_0x0021:
            if (r4 >= r2) goto L_0x0041
            r5 = r3[r4]     // Catch:{ IOException -> 0x0047 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0047 }
            r6.<init>()     // Catch:{ IOException -> 0x0047 }
            r6.append(r8)     // Catch:{ IOException -> 0x0047 }
            r6.append(r5)     // Catch:{ IOException -> 0x0047 }
            java.lang.String r5 = r6.toString()     // Catch:{ IOException -> 0x0047 }
            byte[] r5 = r5.getBytes()     // Catch:{ IOException -> 0x0047 }
            r9.write(r5)     // Catch:{ IOException -> 0x0047 }
            r9.flush()     // Catch:{ IOException -> 0x0047 }
            int r4 = r4 + 1
            goto L_0x0021
        L_0x0041:
            ch.qos.logback.classic.spi.IThrowableProxy r0 = r0.getCause()
            r2 = 0
            goto L_0x0016
        L_0x0047:
            r8 = move-exception
        L_0x0048:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.classic.net.SyslogAppender.postProcess(java.lang.Object, java.io.OutputStream):void");
    }

    public void setStackTracePattern(String str) {
        this.stackTracePattern = str;
    }

    public void setThrowableExcluded(boolean z) {
        this.throwableExcluded = z;
    }

    /* access modifiers changed from: package-private */
    public boolean stackTraceHeaderLine(StringBuilder sb, boolean z) {
        return false;
    }

    public void start() {
        super.start();
        setupStackTraceLayout();
    }
}
