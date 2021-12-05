package com.google.code.regexp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

public class Pattern implements Serializable {
    private static final java.util.regex.Pattern BACKREF_NAMED_GROUP_PATTERN = java.util.regex.Pattern.compile("\\\\k<([^!=].*?)>", 32);
    public static final int CANON_EQ = 128;
    public static final int CASE_INSENSITIVE = 2;
    public static final int COMMENTS = 4;
    public static final int DOTALL = 32;
    private static final int INDEX_GROUP_NAME = 1;
    public static final int LITERAL = 16;
    public static final int MULTILINE = 8;
    private static final java.util.regex.Pattern NAMED_GROUP_PATTERN = java.util.regex.Pattern.compile("\\(\\?<([^!=].*?)>", 32);
    private static final String NAME_PATTERN = "[^!=].*?";
    private static final java.util.regex.Pattern PROPERTY_PATTERN = java.util.regex.Pattern.compile("\\$\\{([^!=].*?)\\}", 32);
    public static final int UNICODE_CASE = 64;
    public static final int UNIX_LINES = 1;
    private static final long serialVersionUID = 1;
    private Map<String, List<GroupInfo>> groupInfo;
    private List<String> groupNames;
    private String namedPattern;
    private java.util.regex.Pattern pattern;

    protected Pattern(String str, int i) {
        this.namedPattern = str;
        this.groupInfo = extractGroupInfo(str);
        this.pattern = buildStandardPattern(str, Integer.valueOf(i));
    }

    private java.util.regex.Pattern buildStandardPattern(String str, Integer num) {
        return java.util.regex.Pattern.compile(replaceGroupNameWithIndex(replace(new StringBuilder(str), NAMED_GROUP_PATTERN, "("), BACKREF_NAMED_GROUP_PATTERN, "\\").toString(), num.intValue());
    }

    public static Pattern compile(String str) {
        return new Pattern(str, 0);
    }

    public static Pattern compile(String str, int i) {
        return new Pattern(str, i);
    }

    private static int countOpenParens(String str, int i) {
        int i2 = 0;
        Matcher matcher = java.util.regex.Pattern.compile("\\(").matcher(str.subSequence(0, i));
        while (matcher.find()) {
            if (!isInsideCharClass(str, matcher.start()) && !isEscapedChar(str, matcher.start()) && !isNoncapturingParen(str, matcher.start())) {
                i2++;
            }
        }
        return i2;
    }

    public static Map<String, List<GroupInfo>> extractGroupInfo(String str) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Matcher matcher = NAMED_GROUP_PATTERN.matcher(str);
        while (matcher.find()) {
            int start = matcher.start();
            if (!isEscapedChar(str, start)) {
                String group = matcher.group(1);
                int countOpenParens = countOpenParens(str, start);
                List arrayList = linkedHashMap.containsKey(group) ? (List) linkedHashMap.get(group) : new ArrayList();
                arrayList.add(new GroupInfo(countOpenParens, start));
                linkedHashMap.put(group, arrayList);
            }
        }
        return linkedHashMap;
    }

    private boolean groupInfoMatches(Map<String, List<GroupInfo>> map, Map<String, List<GroupInfo>> map2) {
        if (map == null && map2 == null) {
            return true;
        }
        if (!(map == null || map2 == null)) {
            if (map.isEmpty() && map2.isEmpty()) {
                return true;
            }
            if (map.size() == map2.size()) {
                boolean z = false;
                for (Map.Entry next : map.entrySet()) {
                    List list = map2.get(next.getKey());
                    boolean z2 = list != null;
                    if (!z2) {
                        return z2;
                    }
                    List list2 = (List) next.getValue();
                    if (!list.containsAll(list2) || !list2.containsAll(list)) {
                        z = false;
                        continue;
                    } else {
                        z = true;
                        continue;
                    }
                    if (!z) {
                        return z;
                    }
                }
                return z;
            }
        }
        return false;
    }

    private static boolean isEscapedChar(String str, int i) {
        return isSlashEscapedChar(str, i) || isQuoteEscapedChar(str, i);
    }

    private static boolean isInsideCharClass(String str, int i) {
        boolean z;
        boolean z2;
        String substring = str.substring(0, i);
        int i2 = i;
        while (true) {
            i2 = substring.lastIndexOf(91, i2 - 1);
            if (i2 != -1) {
                if (!isEscapedChar(substring, i2)) {
                    z = true;
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        if (z) {
            String substring2 = str.substring(i2, i);
            int i3 = -1;
            while (true) {
                i3 = substring2.indexOf(93, i3 + 1);
                if (i3 != -1) {
                    if (!isEscapedChar(substring2, i3)) {
                        z2 = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            return z && !z2;
        }
        z2 = false;
        if (z) {
            return false;
        }
    }

    private static boolean isNoncapturingParen(String str, int i) {
        String substring = str.substring(i, i + 4);
        boolean z = substring.equals("(?<=") || substring.equals("(?<!");
        if (str.charAt(i + 1) == '?') {
            return z || str.charAt(i + 2) != '<';
        }
        return false;
    }

    private static boolean isQuoteEscapedChar(String str, int i) {
        boolean z;
        String substring = str.substring(0, i);
        while (true) {
            i = substring.lastIndexOf("\\Q", i - 1);
            if (i != -1) {
                if (!isSlashEscapedChar(substring, i)) {
                    z = true;
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        return z && !(z && substring.indexOf("\\E", i) != -1);
    }

    private static boolean isSlashEscapedChar(String str, int i) {
        int i2 = 0;
        while (i > 0 && str.charAt(i - 1) == '\\') {
            i--;
            i2++;
        }
        return i2 % 2 != 0;
    }

    private static StringBuilder replace(StringBuilder sb, java.util.regex.Pattern pattern2, String str) {
        Matcher matcher = pattern2.matcher(sb);
        while (matcher.find()) {
            if (!isEscapedChar(sb.toString(), matcher.start())) {
                sb.replace(matcher.start(), matcher.end(), str);
                matcher.reset(sb);
            }
        }
        return sb;
    }

    private StringBuilder replaceGroupNameWithIndex(StringBuilder sb, java.util.regex.Pattern pattern2, String str) {
        Matcher matcher = pattern2.matcher(sb);
        while (matcher.find()) {
            if (!isEscapedChar(sb.toString(), matcher.start())) {
                int indexOf = indexOf(matcher.group(1));
                if (indexOf >= 0) {
                    int start = matcher.start();
                    int end = matcher.end();
                    sb.replace(start, end, str + (indexOf + 1));
                    matcher.reset(sb);
                } else {
                    throw new PatternSyntaxException("unknown group name", sb.toString(), matcher.start(1));
                }
            }
        }
        return sb;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0017, code lost:
        r2 = r5.groupNames;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
            r5 = this;
            r0 = 1
            if (r6 != r5) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 0
            if (r6 != 0) goto L_0x0008
            return r1
        L_0x0008:
            boolean r2 = r6 instanceof com.google.code.regexp.Pattern
            if (r2 != 0) goto L_0x000d
            return r1
        L_0x000d:
            com.google.code.regexp.Pattern r6 = (com.google.code.regexp.Pattern) r6
            java.util.List<java.lang.String> r2 = r5.groupNames
            if (r2 != 0) goto L_0x0017
            java.util.List<java.lang.String> r2 = r6.groupNames
            if (r2 == 0) goto L_0x0023
        L_0x0017:
            java.util.List<java.lang.String> r2 = r5.groupNames
            if (r2 == 0) goto L_0x0025
            java.util.List<java.lang.String> r3 = r6.groupNames
            boolean r2 = java.util.Collections.disjoint(r2, r3)
            if (r2 != 0) goto L_0x0025
        L_0x0023:
            r2 = 1
            goto L_0x0026
        L_0x0025:
            r2 = 0
        L_0x0026:
            if (r2 == 0) goto L_0x0034
            java.util.Map<java.lang.String, java.util.List<com.google.code.regexp.GroupInfo>> r3 = r5.groupInfo
            java.util.Map<java.lang.String, java.util.List<com.google.code.regexp.GroupInfo>> r4 = r6.groupInfo
            boolean r3 = r5.groupInfoMatches(r3, r4)
            if (r3 == 0) goto L_0x0034
            r3 = 1
            goto L_0x0035
        L_0x0034:
            r3 = 0
        L_0x0035:
            if (r2 == 0) goto L_0x0052
            if (r3 == 0) goto L_0x0052
            java.lang.String r2 = r5.namedPattern
            java.lang.String r3 = r6.namedPattern
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0052
            java.util.regex.Pattern r2 = r5.pattern
            int r2 = r2.flags()
            java.util.regex.Pattern r6 = r6.pattern
            int r6 = r6.flags()
            if (r2 != r6) goto L_0x0052
            goto L_0x0053
        L_0x0052:
            r0 = 0
        L_0x0053:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.code.regexp.Pattern.equals(java.lang.Object):boolean");
    }

    public int flags() {
        return this.pattern.flags();
    }

    public Map<String, List<GroupInfo>> groupInfo() {
        return Collections.unmodifiableMap(this.groupInfo);
    }

    public List<String> groupNames() {
        if (this.groupNames == null) {
            this.groupNames = new ArrayList(this.groupInfo.keySet());
        }
        return Collections.unmodifiableList(this.groupNames);
    }

    public int hashCode() {
        int hashCode = this.namedPattern.hashCode() ^ this.pattern.hashCode();
        Map<String, List<GroupInfo>> map = this.groupInfo;
        if (map != null) {
            hashCode ^= map.hashCode();
        }
        List<String> list = this.groupNames;
        return list != null ? hashCode ^ list.hashCode() : hashCode;
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public int indexOf(String str, int i) {
        if (this.groupInfo.containsKey(str)) {
            return ((GroupInfo) this.groupInfo.get(str).get(i)).groupIndex();
        }
        return -1;
    }

    public Matcher matcher(CharSequence charSequence) {
        return new Matcher(this, charSequence);
    }

    public String namedPattern() {
        return this.namedPattern;
    }

    public java.util.regex.Pattern pattern() {
        return this.pattern;
    }

    public String replaceProperties(String str) {
        return replaceGroupNameWithIndex(new StringBuilder(str), PROPERTY_PATTERN, "$").toString();
    }

    public String[] split(CharSequence charSequence) {
        return this.pattern.split(charSequence);
    }

    public String[] split(CharSequence charSequence, int i) {
        return this.pattern.split(charSequence, i);
    }

    public String standardPattern() {
        return this.pattern.pattern();
    }

    public String toString() {
        return this.namedPattern;
    }
}
