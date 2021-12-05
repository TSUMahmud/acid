package p005ch.qos.logback.core.pattern;

import p005ch.qos.logback.core.Layout;
import p005ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/* renamed from: ch.qos.logback.core.pattern.PatternLayoutEncoderBase */
public class PatternLayoutEncoderBase<E> extends LayoutWrappingEncoder<E> {
    protected boolean outputPatternAsHeader = false;
    String pattern;

    public String getPattern() {
        return this.pattern;
    }

    public boolean isOutputPatternAsHeader() {
        return this.outputPatternAsHeader;
    }

    public boolean isOutputPatternAsPresentationHeader() {
        return this.outputPatternAsHeader;
    }

    public void setLayout(Layout<E> layout) {
        throw new UnsupportedOperationException("one cannot set the layout of " + getClass().getName());
    }

    public void setOutputPatternAsHeader(boolean z) {
        this.outputPatternAsHeader = z;
    }

    public void setOutputPatternAsPresentationHeader(boolean z) {
        addWarn("[outputPatternAsPresentationHeader] property is deprecated. Please use [outputPatternAsHeader] option instead.");
        this.outputPatternAsHeader = z;
    }

    public void setPattern(String str) {
        this.pattern = str;
    }
}
