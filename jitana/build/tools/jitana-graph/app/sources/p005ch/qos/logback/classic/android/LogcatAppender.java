package p005ch.qos.logback.classic.android;

import android.util.Log;
import org.slf4j.Marker;
import p005ch.qos.logback.classic.PatternLayout;
import p005ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.Layout;
import p005ch.qos.logback.core.UnsynchronizedAppenderBase;
import p005ch.qos.logback.core.pattern.PostCompileProcessor;

/* renamed from: ch.qos.logback.classic.android.LogcatAppender */
public class LogcatAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private static final int MAX_TAG_LENGTH = 23;
    private boolean checkLoggable = false;
    private PatternLayoutEncoder encoder = null;
    private PatternLayoutEncoder tagEncoder = null;

    public void append(ILoggingEvent iLoggingEvent) {
        if (isStarted()) {
            String tag = getTag(iLoggingEvent);
            int i = iLoggingEvent.getLevel().levelInt;
            if (i == Integer.MIN_VALUE || i == 5000) {
                if (!this.checkLoggable || Log.isLoggable(tag, 2)) {
                    Log.v(tag, this.encoder.getLayout().doLayout(iLoggingEvent));
                }
            } else if (i != 10000) {
                if (i != 20000) {
                    if (i != 30000) {
                        if (i == 40000) {
                            if (!this.checkLoggable || Log.isLoggable(tag, 6)) {
                                Log.e(tag, this.encoder.getLayout().doLayout(iLoggingEvent));
                            }
                        }
                    } else if (!this.checkLoggable || Log.isLoggable(tag, 5)) {
                        Log.w(tag, this.encoder.getLayout().doLayout(iLoggingEvent));
                    }
                } else if (!this.checkLoggable || Log.isLoggable(tag, 4)) {
                    Log.i(tag, this.encoder.getLayout().doLayout(iLoggingEvent));
                }
            } else if (!this.checkLoggable || Log.isLoggable(tag, 3)) {
                Log.d(tag, this.encoder.getLayout().doLayout(iLoggingEvent));
            }
        }
    }

    public boolean getCheckLoggable() {
        return this.checkLoggable;
    }

    public PatternLayoutEncoder getEncoder() {
        return this.encoder;
    }

    /* access modifiers changed from: protected */
    public String getTag(ILoggingEvent iLoggingEvent) {
        PatternLayoutEncoder patternLayoutEncoder = this.tagEncoder;
        String doLayout = patternLayoutEncoder != null ? patternLayoutEncoder.getLayout().doLayout(iLoggingEvent) : iLoggingEvent.getLoggerName();
        if (!this.checkLoggable || doLayout.length() <= 23) {
            return doLayout;
        }
        return doLayout.substring(0, 22) + Marker.ANY_MARKER;
    }

    public PatternLayoutEncoder getTagEncoder() {
        return this.tagEncoder;
    }

    public void setCheckLoggable(boolean z) {
        this.checkLoggable = z;
    }

    public void setEncoder(PatternLayoutEncoder patternLayoutEncoder) {
        this.encoder = patternLayoutEncoder;
    }

    public void setTagEncoder(PatternLayoutEncoder patternLayoutEncoder) {
        this.tagEncoder = patternLayoutEncoder;
    }

    public void start() {
        StringBuilder sb;
        String str;
        PatternLayoutEncoder patternLayoutEncoder = this.encoder;
        if (patternLayoutEncoder == null || patternLayoutEncoder.getLayout() == null) {
            sb = new StringBuilder();
            str = "No layout set for the appender named [";
        } else {
            PatternLayoutEncoder patternLayoutEncoder2 = this.tagEncoder;
            if (patternLayoutEncoder2 != null) {
                Layout layout = patternLayoutEncoder2.getLayout();
                if (layout == null) {
                    sb = new StringBuilder();
                    str = "No tag layout set for the appender named [";
                } else if (layout instanceof PatternLayout) {
                    String pattern = this.tagEncoder.getPattern();
                    if (!pattern.contains("%nopex")) {
                        this.tagEncoder.stop();
                        PatternLayoutEncoder patternLayoutEncoder3 = this.tagEncoder;
                        patternLayoutEncoder3.setPattern(pattern + "%nopex");
                        this.tagEncoder.start();
                    }
                    ((PatternLayout) layout).setPostCompileProcessor((PostCompileProcessor) null);
                }
            }
            super.start();
            return;
        }
        sb.append(str);
        sb.append(this.name);
        sb.append("].");
        addError(sb.toString());
    }
}
