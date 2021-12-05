package p005ch.qos.logback.classic.html;

import java.util.Map;
import p005ch.qos.logback.classic.PatternLayout;
import p005ch.qos.logback.classic.pattern.MDCConverter;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.html.HTMLLayoutBase;
import p005ch.qos.logback.core.html.IThrowableRenderer;
import p005ch.qos.logback.core.pattern.Converter;

/* renamed from: ch.qos.logback.classic.html.HTMLLayout */
public class HTMLLayout extends HTMLLayoutBase<ILoggingEvent> {
    static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";
    IThrowableRenderer<ILoggingEvent> throwableRenderer = new DefaultThrowableRenderer();

    public HTMLLayout() {
        this.pattern = DEFAULT_CONVERSION_PATTERN;
        this.cssBuilder = new DefaultCssBuilder();
    }

    private void appendEventToBuffer(StringBuilder sb, Converter<ILoggingEvent> converter, ILoggingEvent iLoggingEvent) {
        sb.append("<td class=\"");
        sb.append(computeConverterName(converter));
        sb.append("\">");
        converter.write(sb, iLoggingEvent);
        sb.append("</td>");
        sb.append(CoreConstants.LINE_SEPARATOR);
    }

    /* access modifiers changed from: protected */
    public String computeConverterName(Converter converter) {
        if (!(converter instanceof MDCConverter)) {
            return super.computeConverterName(converter);
        }
        String firstOption = ((MDCConverter) converter).getFirstOption();
        return firstOption != null ? firstOption : "MDC";
    }

    public String doLayout(ILoggingEvent iLoggingEvent) {
        StringBuilder sb = new StringBuilder();
        startNewTableIfLimitReached(sb);
        long j = this.counter;
        this.counter = j + 1;
        boolean z = (j & 1) != 0;
        String lowerCase = iLoggingEvent.getLevel().toString().toLowerCase();
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("<tr class=\"");
        sb.append(lowerCase);
        sb.append(z ? " odd\">" : " even\">");
        sb.append(CoreConstants.LINE_SEPARATOR);
        for (Converter converter = this.head; converter != null; converter = converter.getNext()) {
            appendEventToBuffer(sb, converter, iLoggingEvent);
        }
        sb.append("</tr>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        if (iLoggingEvent.getThrowableProxy() != null) {
            this.throwableRenderer.render(sb, iLoggingEvent);
        }
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public Map<String, String> getDefaultConverterMap() {
        return PatternLayout.defaultConverterMap;
    }

    public IThrowableRenderer getThrowableRenderer() {
        return this.throwableRenderer;
    }

    public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> iThrowableRenderer) {
        this.throwableRenderer = iThrowableRenderer;
    }

    public void start() {
        boolean z;
        if (this.throwableRenderer == null) {
            addError("ThrowableRender cannot be null.");
            z = true;
        } else {
            z = false;
        }
        if (!z) {
            super.start();
        }
    }
}
