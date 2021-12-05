package p005ch.qos.logback.core.pattern.parser;

import java.util.List;

/* renamed from: ch.qos.logback.core.pattern.parser.SimpleKeywordNode */
public class SimpleKeywordNode extends FormattingNode {
    List<String> optionList;

    protected SimpleKeywordNode(int i, Object obj) {
        super(i, obj);
    }

    SimpleKeywordNode(Object obj) {
        super(1, obj);
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof SimpleKeywordNode)) {
            return false;
        }
        List<String> list = this.optionList;
        List<String> list2 = ((SimpleKeywordNode) obj).optionList;
        return list != null ? list.equals(list2) : list2 == null;
    }

    public List<String> getOptions() {
        return this.optionList;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public void setOptions(List<String> list) {
        this.optionList = list;
    }

    public String toString() {
        Object obj;
        StringBuilder sb;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.optionList == null) {
            sb = new StringBuilder();
            sb.append("KeyWord(");
            sb.append(this.value);
            sb.append(",");
            obj = this.formatInfo;
        } else {
            sb = new StringBuilder();
            sb.append("KeyWord(");
            sb.append(this.value);
            sb.append(", ");
            sb.append(this.formatInfo);
            sb.append(",");
            obj = this.optionList;
        }
        sb.append(obj);
        sb.append(")");
        stringBuffer.append(sb.toString());
        stringBuffer.append(printNext());
        return stringBuffer.toString();
    }
}
