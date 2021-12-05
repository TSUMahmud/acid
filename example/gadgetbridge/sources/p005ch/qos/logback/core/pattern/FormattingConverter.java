package p005ch.qos.logback.core.pattern;

/* renamed from: ch.qos.logback.core.pattern.FormattingConverter */
public abstract class FormattingConverter<E> extends Converter<E> {
    static final int INITIAL_BUF_SIZE = 256;
    static final int MAX_CAPACITY = 1024;
    FormatInfo formattingInfo;

    public final FormatInfo getFormattingInfo() {
        return this.formattingInfo;
    }

    public final void setFormattingInfo(FormatInfo formatInfo) {
        if (this.formattingInfo == null) {
            this.formattingInfo = formatInfo;
            return;
        }
        throw new IllegalStateException("FormattingInfo has been already set");
    }

    public final void write(StringBuilder sb, E e) {
        String convert = convert(e);
        FormatInfo formatInfo = this.formattingInfo;
        if (formatInfo == null) {
            sb.append(convert);
            return;
        }
        int min = formatInfo.getMin();
        int max = this.formattingInfo.getMax();
        if (convert != null) {
            int length = convert.length();
            if (length > max) {
                convert = this.formattingInfo.isLeftTruncate() ? convert.substring(length - max) : convert.substring(0, max);
            } else if (length < min) {
                if (this.formattingInfo.isLeftPad()) {
                    SpacePadder.leftPad(sb, convert, min);
                    return;
                } else {
                    SpacePadder.rightPad(sb, convert, min);
                    return;
                }
            }
            sb.append(convert);
        } else if (min > 0) {
            SpacePadder.spacePad(sb, min);
        }
    }
}
