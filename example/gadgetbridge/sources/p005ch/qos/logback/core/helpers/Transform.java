package p005ch.qos.logback.core.helpers;

/* renamed from: ch.qos.logback.core.helpers.Transform */
public class Transform {
    private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final int CDATA_END_LEN = CDATA_END.length();
    private static final String CDATA_PSEUDO_END = "]]&gt;";
    private static final String CDATA_START = "<![CDATA[";

    public static void appendEscapingCDATA(StringBuilder sb, String str) {
        if (str != null) {
            int indexOf = str.indexOf(CDATA_END);
            if (indexOf < 0) {
                sb.append(str);
                return;
            }
            int i = 0;
            while (indexOf > -1) {
                sb.append(str.substring(i, indexOf));
                sb.append(CDATA_EMBEDED_END);
                i = CDATA_END_LEN + indexOf;
                if (i < str.length()) {
                    indexOf = str.indexOf(CDATA_END, i);
                } else {
                    return;
                }
            }
            sb.append(str.substring(i));
        }
    }

    public static String escapeTags(String str) {
        return (str == null || str.length() == 0) ? str : (str.indexOf("<") == -1 && str.indexOf(">") == -1) ? str : escapeTags(new StringBuffer(str));
    }

    public static String escapeTags(StringBuffer stringBuffer) {
        int i;
        String str;
        for (int i2 = 0; i2 < stringBuffer.length(); i2++) {
            char charAt = stringBuffer.charAt(i2);
            if (charAt == '<') {
                i = i2 + 1;
                str = "&lt;";
            } else if (charAt == '>') {
                i = i2 + 1;
                str = "&gt;";
            }
            stringBuffer.replace(i2, i, str);
        }
        return stringBuffer.toString();
    }
}
