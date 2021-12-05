package p005ch.qos.logback.classic.net;

import org.slf4j.Marker;
import p005ch.qos.logback.classic.ClassicConstants;
import p005ch.qos.logback.classic.PatternLayout;
import p005ch.qos.logback.classic.boolex.OnErrorEvaluator;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.Layout;
import p005ch.qos.logback.core.boolex.EventEvaluator;
import p005ch.qos.logback.core.helpers.CyclicBuffer;
import p005ch.qos.logback.core.net.SMTPAppenderBase;
import p005ch.qos.logback.core.pattern.PostCompileProcessor;

/* renamed from: ch.qos.logback.classic.net.SMTPAppender */
public class SMTPAppender extends SMTPAppenderBase<ILoggingEvent> {
    static final String DEFAULT_SUBJECT_PATTERN = "%logger{20} - %m";
    private boolean includeCallerData = false;

    public SMTPAppender() {
    }

    public SMTPAppender(EventEvaluator<ILoggingEvent> eventEvaluator) {
        this.eventEvaluator = eventEvaluator;
    }

    /* access modifiers changed from: protected */
    public boolean eventMarksEndOfLife(ILoggingEvent iLoggingEvent) {
        Marker marker = iLoggingEvent.getMarker();
        if (marker == null) {
            return false;
        }
        return marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER);
    }

    /* access modifiers changed from: protected */
    public void fillBuffer(CyclicBuffer<ILoggingEvent> cyclicBuffer, StringBuffer stringBuffer) {
        int length = cyclicBuffer.length();
        for (int i = 0; i < length; i++) {
            stringBuffer.append(this.layout.doLayout(cyclicBuffer.get()));
        }
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    /* access modifiers changed from: protected */
    public PatternLayout makeNewToPatternLayout(String str) {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setPattern(str + "%nopex");
        return patternLayout;
    }

    /* access modifiers changed from: protected */
    public Layout<ILoggingEvent> makeSubjectLayout(String str) {
        if (str == null) {
            str = DEFAULT_SUBJECT_PATTERN;
        }
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(getContext());
        patternLayout.setPattern(str);
        patternLayout.setPostCompileProcessor((PostCompileProcessor) null);
        patternLayout.start();
        return patternLayout;
    }

    public void setIncludeCallerData(boolean z) {
        this.includeCallerData = z;
    }

    public void start() {
        if (this.eventEvaluator == null) {
            OnErrorEvaluator onErrorEvaluator = new OnErrorEvaluator();
            onErrorEvaluator.setContext(getContext());
            onErrorEvaluator.setName("onError");
            onErrorEvaluator.start();
            this.eventEvaluator = onErrorEvaluator;
        }
        super.start();
    }

    /* access modifiers changed from: protected */
    public void subAppend(CyclicBuffer<ILoggingEvent> cyclicBuffer, ILoggingEvent iLoggingEvent) {
        if (this.includeCallerData) {
            iLoggingEvent.getCallerData();
        }
        iLoggingEvent.prepareForDeferredProcessing();
        cyclicBuffer.add(iLoggingEvent);
    }
}
