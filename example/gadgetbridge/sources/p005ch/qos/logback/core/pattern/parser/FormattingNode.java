package p005ch.qos.logback.core.pattern.parser;

import p005ch.qos.logback.core.pattern.FormatInfo;

/* renamed from: ch.qos.logback.core.pattern.parser.FormattingNode */
public class FormattingNode extends Node {
    FormatInfo formatInfo;

    FormattingNode(int i) {
        super(i);
    }

    FormattingNode(int i, Object obj) {
        super(i, obj);
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof FormattingNode)) {
            return false;
        }
        FormatInfo formatInfo2 = this.formatInfo;
        FormatInfo formatInfo3 = ((FormattingNode) obj).formatInfo;
        return formatInfo2 != null ? formatInfo2.equals(formatInfo3) : formatInfo3 == null;
    }

    public FormatInfo getFormatInfo() {
        return this.formatInfo;
    }

    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        FormatInfo formatInfo2 = this.formatInfo;
        return hashCode + (formatInfo2 != null ? formatInfo2.hashCode() : 0);
    }

    public void setFormatInfo(FormatInfo formatInfo2) {
        this.formatInfo = formatInfo2;
    }
}
