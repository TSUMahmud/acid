package org.apache.commons.lang3.text;

import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;

@Deprecated
public class ExtendedMessageFormat extends MessageFormat {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String DUMMY_PATTERN = "";
    private static final char END_FE = '}';
    private static final int HASH_SEED = 31;
    private static final char QUOTE = '\'';
    private static final char START_FE = '{';
    private static final char START_FMT = ',';
    private static final long serialVersionUID = -2362048321261811743L;
    private final Map<String, ? extends FormatFactory> registry;
    private String toPattern;

    public ExtendedMessageFormat(String pattern) {
        this(pattern, Locale.getDefault());
    }

    public ExtendedMessageFormat(String pattern, Locale locale) {
        this(pattern, locale, (Map<String, ? extends FormatFactory>) null);
    }

    public ExtendedMessageFormat(String pattern, Map<String, ? extends FormatFactory> registry2) {
        this(pattern, Locale.getDefault(), registry2);
    }

    public ExtendedMessageFormat(String pattern, Locale locale, Map<String, ? extends FormatFactory> registry2) {
        super("");
        setLocale(locale);
        this.registry = registry2;
        applyPattern(pattern);
    }

    public String toPattern() {
        return this.toPattern;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x006b, code lost:
        r10 = parseFormatDescription(r14, next(r3));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void applyPattern(java.lang.String r14) {
        /*
            r13 = this;
            java.util.Map<java.lang.String, ? extends org.apache.commons.lang3.text.FormatFactory> r0 = r13.registry
            if (r0 != 0) goto L_0x000e
            super.applyPattern(r14)
            java.lang.String r0 = super.toPattern()
            r13.toPattern = r0
            return
        L_0x000e:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            int r3 = r14.length()
            r2.<init>(r3)
            java.text.ParsePosition r3 = new java.text.ParsePosition
            r4 = 0
            r3.<init>(r4)
            char[] r5 = r14.toCharArray()
            r6 = 0
        L_0x002c:
            int r7 = r3.getIndex()
            int r8 = r14.length()
            if (r7 >= r8) goto L_0x00d6
            int r7 = r3.getIndex()
            char r7 = r5[r7]
            r8 = 39
            if (r7 == r8) goto L_0x00d1
            r8 = 123(0x7b, float:1.72E-43)
            if (r7 == r8) goto L_0x0045
            goto L_0x00ac
        L_0x0045:
            int r6 = r6 + 1
            r13.seekNonWs(r14, r3)
            int r7 = r3.getIndex()
            java.text.ParsePosition r9 = r13.next(r3)
            int r9 = r13.readArgumentIndex(r14, r9)
            r2.append(r8)
            r2.append(r9)
            r13.seekNonWs(r14, r3)
            r8 = 0
            r10 = 0
            int r11 = r3.getIndex()
            char r11 = r5[r11]
            r12 = 44
            if (r11 != r12) goto L_0x007f
            java.text.ParsePosition r11 = r13.next(r3)
            java.lang.String r10 = r13.parseFormatDescription(r14, r11)
            java.text.Format r8 = r13.getFormat(r10)
            if (r8 != 0) goto L_0x007f
            r2.append(r12)
            r2.append(r10)
        L_0x007f:
            r0.add(r8)
            if (r8 != 0) goto L_0x0086
            r11 = 0
            goto L_0x0087
        L_0x0086:
            r11 = r10
        L_0x0087:
            r1.add(r11)
            int r11 = r0.size()
            r12 = 1
            if (r11 != r6) goto L_0x0093
            r11 = 1
            goto L_0x0094
        L_0x0093:
            r11 = 0
        L_0x0094:
            org.apache.commons.lang3.Validate.isTrue(r11)
            int r11 = r1.size()
            if (r11 != r6) goto L_0x009e
            goto L_0x009f
        L_0x009e:
            r12 = 0
        L_0x009f:
            org.apache.commons.lang3.Validate.isTrue(r12)
            int r11 = r3.getIndex()
            char r11 = r5[r11]
            r12 = 125(0x7d, float:1.75E-43)
            if (r11 != r12) goto L_0x00ba
        L_0x00ac:
            int r7 = r3.getIndex()
            char r7 = r5[r7]
            r2.append(r7)
            r13.next(r3)
            goto L_0x002c
        L_0x00ba:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "Unreadable format element at position "
            r11.append(r12)
            r11.append(r7)
            java.lang.String r11 = r11.toString()
            r4.<init>(r11)
            throw r4
        L_0x00d1:
            r13.appendQuotedString(r14, r3, r2)
            goto L_0x002c
        L_0x00d6:
            java.lang.String r4 = r2.toString()
            super.applyPattern(r4)
            java.lang.String r4 = super.toPattern()
            java.lang.String r4 = r13.insertFormats(r4, r1)
            r13.toPattern = r4
            boolean r4 = r13.containsElements(r0)
            if (r4 == 0) goto L_0x010c
            java.text.Format[] r4 = r13.getFormats()
            r7 = 0
            java.util.Iterator r8 = r0.iterator()
        L_0x00f6:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L_0x0109
            java.lang.Object r9 = r8.next()
            java.text.Format r9 = (java.text.Format) r9
            if (r9 == 0) goto L_0x0106
            r4[r7] = r9
        L_0x0106:
            int r7 = r7 + 1
            goto L_0x00f6
        L_0x0109:
            super.setFormats(r4)
        L_0x010c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.ExtendedMessageFormat.applyPattern(java.lang.String):void");
    }

    public void setFormat(int formatElementIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    public void setFormats(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    public void setFormatsByArgumentIndex(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !super.equals(obj) || ObjectUtils.notEqual(getClass(), obj.getClass())) {
            return false;
        }
        ExtendedMessageFormat rhs = (ExtendedMessageFormat) obj;
        if (ObjectUtils.notEqual(this.toPattern, rhs.toPattern)) {
            return false;
        }
        return true ^ ObjectUtils.notEqual(this.registry, rhs.registry);
    }

    public int hashCode() {
        return (((super.hashCode() * 31) + Objects.hashCode(this.registry)) * 31) + Objects.hashCode(this.toPattern);
    }

    private Format getFormat(String desc) {
        if (this.registry == null) {
            return null;
        }
        String name = desc;
        String args = null;
        int i = desc.indexOf(44);
        if (i > 0) {
            name = desc.substring(0, i).trim();
            args = desc.substring(i + 1).trim();
        }
        FormatFactory factory = (FormatFactory) this.registry.get(name);
        if (factory != null) {
            return factory.getFormat(name, args, getLocale());
        }
        return null;
    }

    private int readArgumentIndex(String pattern, ParsePosition pos) {
        int start = pos.getIndex();
        seekNonWs(pattern, pos);
        StringBuilder result = new StringBuilder();
        boolean error = false;
        while (!error && pos.getIndex() < pattern.length()) {
            char c = pattern.charAt(pos.getIndex());
            if (Character.isWhitespace(c)) {
                seekNonWs(pattern, pos);
                c = pattern.charAt(pos.getIndex());
                if (!(c == ',' || c == '}')) {
                    error = true;
                    next(pos);
                }
            }
            if ((c == ',' || c == '}') && result.length() > 0) {
                try {
                    return Integer.parseInt(result.toString());
                } catch (NumberFormatException e) {
                }
            }
            error = !Character.isDigit(c);
            result.append(c);
            next(pos);
        }
        if (error) {
            throw new IllegalArgumentException("Invalid format argument index at position " + start + ": " + pattern.substring(start, pos.getIndex()));
        }
        throw new IllegalArgumentException("Unterminated format element at position " + start);
    }

    private String parseFormatDescription(String pattern, ParsePosition pos) {
        int start = pos.getIndex();
        seekNonWs(pattern, pos);
        int text = pos.getIndex();
        int depth = 1;
        while (pos.getIndex() < pattern.length()) {
            char charAt = pattern.charAt(pos.getIndex());
            if (charAt == '\'') {
                getQuotedString(pattern, pos);
            } else if (charAt == '{') {
                depth++;
            } else if (charAt == '}' && depth - 1 == 0) {
                return pattern.substring(text, pos.getIndex());
            }
            next(pos);
        }
        throw new IllegalArgumentException("Unterminated format element at position " + start);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0055, code lost:
        r2 = r2 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String insertFormats(java.lang.String r8, java.util.ArrayList<java.lang.String> r9) {
        /*
            r7 = this;
            boolean r0 = r7.containsElements(r9)
            if (r0 != 0) goto L_0x0007
            return r8
        L_0x0007:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            int r1 = r8.length()
            int r1 = r1 * 2
            r0.<init>(r1)
            java.text.ParsePosition r1 = new java.text.ParsePosition
            r2 = 0
            r1.<init>(r2)
            r2 = -1
            r3 = 0
        L_0x001a:
            int r4 = r1.getIndex()
            int r5 = r8.length()
            if (r4 >= r5) goto L_0x006d
            int r4 = r1.getIndex()
            char r4 = r8.charAt(r4)
            r5 = 39
            if (r4 == r5) goto L_0x0068
            r5 = 123(0x7b, float:1.72E-43)
            if (r4 == r5) goto L_0x0042
            r5 = 125(0x7d, float:1.75E-43)
            if (r4 == r5) goto L_0x0039
            goto L_0x003b
        L_0x0039:
            int r3 = r3 + -1
        L_0x003b:
            r0.append(r4)
            r7.next(r1)
            goto L_0x006c
        L_0x0042:
            int r3 = r3 + 1
            r0.append(r5)
            java.text.ParsePosition r5 = r7.next(r1)
            int r5 = r7.readArgumentIndex(r8, r5)
            r0.append(r5)
            r5 = 1
            if (r3 != r5) goto L_0x006c
            int r2 = r2 + 1
            java.lang.Object r5 = r9.get(r2)
            java.lang.String r5 = (java.lang.String) r5
            if (r5 == 0) goto L_0x0067
            r6 = 44
            r0.append(r6)
            r0.append(r5)
        L_0x0067:
            goto L_0x006c
        L_0x0068:
            r7.appendQuotedString(r8, r1, r0)
        L_0x006c:
            goto L_0x001a
        L_0x006d:
            java.lang.String r4 = r0.toString()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.ExtendedMessageFormat.insertFormats(java.lang.String, java.util.ArrayList):java.lang.String");
    }

    private void seekNonWs(String pattern, ParsePosition pos) {
        char[] buffer = pattern.toCharArray();
        do {
            int len = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
            pos.setIndex(pos.getIndex() + len);
            if (len <= 0 || pos.getIndex() >= pattern.length()) {
            }
            int len2 = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
            pos.setIndex(pos.getIndex() + len2);
            return;
        } while (pos.getIndex() >= pattern.length());
    }

    private ParsePosition next(ParsePosition pos) {
        pos.setIndex(pos.getIndex() + 1);
        return pos;
    }

    private StringBuilder appendQuotedString(String pattern, ParsePosition pos, StringBuilder appendTo) {
        if (appendTo != null) {
            appendTo.append('\'');
        }
        next(pos);
        int start = pos.getIndex();
        char[] c = pattern.toCharArray();
        int lastHold = start;
        int i = pos.getIndex();
        while (i < pattern.length()) {
            if (c[pos.getIndex()] != '\'') {
                next(pos);
                i++;
            } else {
                next(pos);
                if (appendTo == null) {
                    return null;
                }
                appendTo.append(c, lastHold, pos.getIndex() - lastHold);
                return appendTo;
            }
        }
        throw new IllegalArgumentException("Unterminated quoted string at position " + start);
    }

    private void getQuotedString(String pattern, ParsePosition pos) {
        appendQuotedString(pattern, pos, (StringBuilder) null);
    }

    private boolean containsElements(Collection<?> coll) {
        if (coll == null || coll.isEmpty()) {
            return false;
        }
        for (Object name : coll) {
            if (name != null) {
                return true;
            }
        }
        return false;
    }
}
