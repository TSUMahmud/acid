package p005ch.qos.logback.classic.encoder;

import p005ch.qos.logback.classic.PatternLayout;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

/* renamed from: ch.qos.logback.classic.encoder.PatternLayoutEncoder */
public class PatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    public void start() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(this.context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
}
