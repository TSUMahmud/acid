package p005ch.qos.logback.core.pattern.util;

import org.apache.commons.lang3.CharUtils;

/* renamed from: ch.qos.logback.core.pattern.util.RegularEscapeUtil */
public class RegularEscapeUtil implements IEscapeUtil {
    public static String basicEscape(String str) {
        char c;
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length);
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            char charAt = str.charAt(i);
            if (charAt == '\\') {
                i = i2 + 1;
                c = str.charAt(i2);
                if (c == 'n') {
                    c = 10;
                } else if (c == 'r') {
                    c = CharUtils.f207CR;
                } else if (c == 't') {
                    c = 9;
                } else if (c == 'f') {
                    c = 12;
                }
            } else {
                int i3 = i2;
                c = charAt;
                i = i3;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public void escape(String str, StringBuffer stringBuffer, char c, int i) {
        char c2;
        if (str.indexOf(c) >= 0 || c == '\\') {
            stringBuffer.append(c);
        } else if (c != '_') {
            if (c == 'n') {
                c2 = 10;
            } else if (c == 'r') {
                c2 = CharUtils.f207CR;
            } else if (c == 't') {
                c2 = 9;
            } else {
                String formatEscapeCharsForListing = formatEscapeCharsForListing(str);
                throw new IllegalArgumentException("Illegal char '" + c + " at column " + i + ". Only \\\\, \\_" + formatEscapeCharsForListing + ", \\t, \\n, \\r combinations are allowed as escape characters.");
            }
            stringBuffer.append(c2);
        }
    }

    /* access modifiers changed from: package-private */
    public String formatEscapeCharsForListing(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(", \\");
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }
}
