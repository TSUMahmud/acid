package brut.androlib.res.xml;

import java.lang.Character;
import java.util.List;
import p005ch.qos.logback.core.CoreConstants;

public final class ResXmlEncoders {
    public static String encodeAsResXmlAttr(String str) {
        String str2;
        if (str.isEmpty()) {
            return str;
        }
        char[] charArray = str.toCharArray();
        StringBuilder sb = new StringBuilder(str.length() + 10);
        char c = charArray[0];
        if (c == '#' || c == '?' || c == '@') {
            sb.append(CoreConstants.ESCAPE_CHAR);
        }
        for (char c2 : charArray) {
            if (c2 == 10) {
                str2 = "\\n";
            } else if (c2 != '\"') {
                if (c2 == '\\') {
                    sb.append(CoreConstants.ESCAPE_CHAR);
                } else if (!isPrintableChar(c2)) {
                    str2 = String.format("\\u%04x", new Object[]{Integer.valueOf(c2)});
                }
                sb.append(c2);
            } else {
                str2 = "&quot;";
            }
            sb.append(str2);
        }
        return sb.toString();
    }

    public static String encodeAsXmlValue(String str) {
        if (str.isEmpty()) {
            return str;
        }
        char[] charArray = str.toCharArray();
        StringBuilder sb = new StringBuilder(str.length() + 10);
        char c = charArray[0];
        if (c == '#' || c == '?' || c == '@') {
            sb.append(CoreConstants.ESCAPE_CHAR);
        }
        boolean z = false;
        boolean z2 = false;
        boolean z3 = true;
        int i = 0;
        for (char c2 : charArray) {
            if (z2) {
                if (c2 == '>') {
                    i = sb.length() + 1;
                    z = false;
                    z2 = false;
                }
            } else if (c2 == ' ') {
                if (z3) {
                    z = true;
                }
                z3 = true;
            } else {
                if (c2 != 10) {
                    if (c2 != '\"') {
                        if (c2 != '\'') {
                            if (c2 == '<') {
                                if (z) {
                                    sb.insert(i, CoreConstants.DOUBLE_QUOTE_CHAR).append(CoreConstants.DOUBLE_QUOTE_CHAR);
                                }
                                z2 = true;
                            } else if (c2 != '\\') {
                                if (!isPrintableChar(c2)) {
                                    sb.append(String.format("\\u%04x", new Object[]{Integer.valueOf(c2)}));
                                    z3 = false;
                                }
                            }
                            z3 = false;
                        }
                    }
                    sb.append(CoreConstants.ESCAPE_CHAR);
                    z3 = false;
                }
                z = true;
                z3 = false;
            }
            sb.append(c2);
        }
        if (z || z3) {
            sb.insert(i, CoreConstants.DOUBLE_QUOTE_CHAR).append(CoreConstants.DOUBLE_QUOTE_CHAR);
        }
        return sb.toString();
    }

    public static String enumerateNonPositionalSubstitutions(String str) {
        List<Integer> findNonPositionalSubstitutions = findNonPositionalSubstitutions(str, -1);
        if (findNonPositionalSubstitutions.size() < 2) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int i2 = 0;
        for (Integer intValue : findNonPositionalSubstitutions) {
            Integer valueOf = Integer.valueOf(intValue.intValue() + 1);
            sb.append(str.substring(i, valueOf.intValue()));
            i2++;
            sb.append(i2);
            sb.append('$');
            i = valueOf.intValue();
        }
        sb.append(str.substring(i));
        return sb.toString();
    }

    public static String escapeXmlChars(String str) {
        return str.replace("&", "&amp;").replace("<", "&lt;");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
        if (r6 == '$') goto L_0x0050;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.List<java.lang.Integer> findNonPositionalSubstitutions(java.lang.String r8, int r9) {
        /*
            int r0 = r8.length()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = 0
            r3 = 0
        L_0x000b:
            r4 = 37
            int r2 = r8.indexOf(r4, r2)
            int r5 = r2 + 1
            if (r5 == 0) goto L_0x0052
            if (r5 != r0) goto L_0x0018
            goto L_0x0052
        L_0x0018:
            int r6 = r5 + 1
            char r5 = r8.charAt(r5)
            if (r5 != r4) goto L_0x0022
            r2 = r6
            goto L_0x000b
        L_0x0022:
            r4 = 48
            if (r5 < r4) goto L_0x0040
            r7 = 57
            if (r5 > r7) goto L_0x0040
            if (r6 >= r0) goto L_0x0040
        L_0x002c:
            int r5 = r6 + 1
            char r6 = r8.charAt(r6)
            if (r6 < r4) goto L_0x003b
            if (r6 > r7) goto L_0x003b
            if (r5 < r0) goto L_0x0039
            goto L_0x003b
        L_0x0039:
            r6 = r5
            goto L_0x002c
        L_0x003b:
            r4 = 36
            if (r6 != r4) goto L_0x0041
            goto L_0x0050
        L_0x0040:
            r5 = r6
        L_0x0041:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1.add(r2)
            r2 = -1
            if (r9 == r2) goto L_0x0050
            int r3 = r3 + 1
            if (r3 < r9) goto L_0x0050
            goto L_0x0052
        L_0x0050:
            r2 = r5
            goto L_0x000b
        L_0x0052:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: brut.androlib.res.xml.ResXmlEncoders.findNonPositionalSubstitutions(java.lang.String, int):java.util.List");
    }

    public static boolean hasMultipleNonPositionalSubstitutions(String str) {
        return findNonPositionalSubstitutions(str, 2).size() > 1;
    }

    private static boolean isPrintableChar(char c) {
        Character.UnicodeBlock of = Character.UnicodeBlock.of(c);
        return (Character.isISOControl(c) || c == 65535 || of == null || of == Character.UnicodeBlock.SPECIALS) ? false : true;
    }
}
