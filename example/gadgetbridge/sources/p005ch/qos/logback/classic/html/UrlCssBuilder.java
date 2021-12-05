package p005ch.qos.logback.classic.html;

import p005ch.qos.logback.core.html.CssBuilder;

/* renamed from: ch.qos.logback.classic.html.UrlCssBuilder */
public class UrlCssBuilder implements CssBuilder {
    String url = "http://logback.qos.ch/css/classic.css";

    public void addCss(StringBuilder sb) {
        sb.append("<link REL=StyleSheet HREF=\"");
        sb.append(this.url);
        sb.append("\" TITLE=\"Basic\" />");
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }
}
