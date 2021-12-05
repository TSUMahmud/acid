package p005ch.qos.logback.core.html;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.LayoutBase;
import p005ch.qos.logback.core.pattern.Converter;
import p005ch.qos.logback.core.pattern.ConverterUtil;
import p005ch.qos.logback.core.pattern.parser.Parser;
import p005ch.qos.logback.core.spi.AbstractComponentTracker;
import p005ch.qos.logback.core.spi.ScanException;

/* renamed from: ch.qos.logback.core.html.HTMLLayoutBase */
public abstract class HTMLLayoutBase<E> extends LayoutBase<E> {
    protected long counter = 0;
    protected CssBuilder cssBuilder;
    protected Converter<E> head;
    protected String pattern;
    protected String title = "Logback Log Messages";

    private void buildHeaderRowForTable(StringBuilder sb) {
        sb.append("<tr class=\"header\">");
        sb.append(CoreConstants.LINE_SEPARATOR);
        for (Converter<E> converter = this.head; converter != null; converter = converter.getNext()) {
            if (computeConverterName(converter) != null) {
                sb.append("<td class=\"");
                sb.append(computeConverterName(converter));
                sb.append("\">");
                sb.append(computeConverterName(converter));
                sb.append("</td>");
                sb.append(CoreConstants.LINE_SEPARATOR);
            }
        }
        sb.append("</tr>");
        sb.append(CoreConstants.LINE_SEPARATOR);
    }

    /* access modifiers changed from: protected */
    public String computeConverterName(Converter converter) {
        String simpleName = converter.getClass().getSimpleName();
        int indexOf = simpleName.indexOf("Converter");
        return indexOf == -1 ? simpleName : simpleName.substring(0, indexOf);
    }

    public String getContentType() {
        return "text/html";
    }

    public CssBuilder getCssBuilder() {
        return this.cssBuilder;
    }

    /* access modifiers changed from: protected */
    public abstract Map<String, String> getDefaultConverterMap();

    public Map<String, String> getEffectiveConverterMap() {
        Map map;
        HashMap hashMap = new HashMap();
        Map<String, String> defaultConverterMap = getDefaultConverterMap();
        if (defaultConverterMap != null) {
            hashMap.putAll(defaultConverterMap);
        }
        Context context = getContext();
        if (!(context == null || (map = (Map) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY)) == null)) {
            hashMap.putAll(map);
        }
        return hashMap;
    }

    public String getFileFooter() {
        return CoreConstants.LINE_SEPARATOR + "</body></html>";
    }

    public String getFileHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
        sb.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("<html>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("  <head>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("    <title>");
        sb.append(this.title);
        sb.append("</title>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        this.cssBuilder.addCss(sb);
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("  </head>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("<body>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        return sb.toString();
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getPresentationFooter() {
        return "</table>";
    }

    public String getPresentationHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("<hr/>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("<p>Log session start time ");
        sb.append(new Date());
        sb.append("</p><p></p>");
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append("<table cellspacing=\"0\">");
        sb.append(CoreConstants.LINE_SEPARATOR);
        buildHeaderRowForTable(sb);
        return sb.toString();
    }

    public String getTitle() {
        return this.title;
    }

    public void setCssBuilder(CssBuilder cssBuilder2) {
        this.cssBuilder = cssBuilder2;
    }

    public void setPattern(String str) {
        this.pattern = str;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void start() {
        boolean z;
        try {
            Parser parser = new Parser(this.pattern);
            parser.setContext(getContext());
            this.head = parser.compile(parser.parse(), getEffectiveConverterMap());
            ConverterUtil.startConverters(this.head);
            z = false;
        } catch (ScanException e) {
            addError("Incorrect pattern found", e);
            z = true;
        }
        if (!z) {
            this.started = true;
        }
    }

    /* access modifiers changed from: protected */
    public void startNewTableIfLimitReached(StringBuilder sb) {
        if (this.counter >= AbstractComponentTracker.LINGERING_TIMEOUT) {
            this.counter = 0;
            sb.append("</table>");
            sb.append(CoreConstants.LINE_SEPARATOR);
            sb.append("<p></p>");
            sb.append("<table cellspacing=\"0\">");
            sb.append(CoreConstants.LINE_SEPARATOR);
            buildHeaderRowForTable(sb);
        }
    }
}
