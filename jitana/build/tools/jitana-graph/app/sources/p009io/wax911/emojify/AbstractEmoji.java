package p009io.wax911.emojify;

import com.google.code.regexp.Pattern;

/* renamed from: io.wax911.emojify.AbstractEmoji */
abstract class AbstractEmoji {
    static final Pattern htmlEntityPattern = Pattern.compile("&#\\w+;");
    static final Pattern htmlSurrogateEntityPattern = Pattern.compile("(?<H>&#\\w+;)(?<L>&#\\w+;)");
    static final Pattern htmlSurrogateEntityPattern2 = Pattern.compile("&#\\w+;&#\\w+;&#\\w+;&#\\w+;");
    static final Pattern shortCodeOrHtmlEntityPattern = Pattern.compile(":\\w+:|(?<H1>&#\\w+;)(?<H2>&#\\w+;)(?<L1>&#\\w+;)(?<L2>&#\\w+;)|(?<H>&#\\w+;)(?<L>&#\\w+;)|&#\\w+;");
    static final Pattern shortCodePattern = Pattern.compile(":(\\w+):");

    AbstractEmoji() {
    }

    static String htmlifyHelper(String text, boolean isHex, boolean isSurrogate) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            int ch = text.codePointAt(i);
            if (ch <= 128) {
                sb.appendCodePoint(ch);
            } else if (ch <= 128 || (ch >= 159 && (ch < 55296 || ch > 57343))) {
                if (isHex) {
                    sb.append("&#x" + Integer.toHexString(ch) + ";");
                } else if (isSurrogate) {
                    sb.append("&#" + String.format("%.0f", new Object[]{Double.valueOf(Math.floor((double) ((ch - 65536) / 1024)) + 55296.0d)}) + ";&#" + String.format("%.0f", new Object[]{Double.valueOf((double) (((ch - 65536) % 1024) + 56320))}) + ";");
                } else {
                    sb.append("&#" + ch + ";");
                }
            }
        }
        return sb.toString();
    }
}
