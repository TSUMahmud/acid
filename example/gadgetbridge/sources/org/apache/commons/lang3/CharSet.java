package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CharSet implements Serializable {
    public static final CharSet ASCII_ALPHA = new CharSet("a-zA-Z");
    public static final CharSet ASCII_ALPHA_LOWER = new CharSet("a-z");
    public static final CharSet ASCII_ALPHA_UPPER = new CharSet("A-Z");
    public static final CharSet ASCII_NUMERIC = new CharSet("0-9");
    protected static final Map<String, CharSet> COMMON = Collections.synchronizedMap(new HashMap());
    public static final CharSet EMPTY = new CharSet(null);
    private static final long serialVersionUID = 5947847346149275958L;
    private final Set<CharRange> set = Collections.synchronizedSet(new HashSet());

    static {
        COMMON.put((Object) null, EMPTY);
        COMMON.put("", EMPTY);
        COMMON.put("a-zA-Z", ASCII_ALPHA);
        COMMON.put("A-Za-z", ASCII_ALPHA);
        COMMON.put("a-z", ASCII_ALPHA_LOWER);
        COMMON.put("A-Z", ASCII_ALPHA_UPPER);
        COMMON.put("0-9", ASCII_NUMERIC);
    }

    public static CharSet getInstance(String... setStrs) {
        CharSet common;
        if (setStrs == null) {
            return null;
        }
        if (setStrs.length != 1 || (common = COMMON.get(setStrs[0])) == null) {
            return new CharSet(setStrs);
        }
        return common;
    }

    protected CharSet(String... set2) {
        for (String s : set2) {
            add(s);
        }
    }

    /* access modifiers changed from: protected */
    public void add(String str) {
        if (str != null) {
            int len = str.length();
            int pos = 0;
            while (pos < len) {
                int remainder = len - pos;
                if (remainder >= 4 && str.charAt(pos) == '^' && str.charAt(pos + 2) == '-') {
                    this.set.add(CharRange.isNotIn(str.charAt(pos + 1), str.charAt(pos + 3)));
                    pos += 4;
                } else if (remainder >= 3 && str.charAt(pos + 1) == '-') {
                    this.set.add(CharRange.isIn(str.charAt(pos), str.charAt(pos + 2)));
                    pos += 3;
                } else if (remainder < 2 || str.charAt(pos) != '^') {
                    this.set.add(CharRange.m42is(str.charAt(pos)));
                    pos++;
                } else {
                    this.set.add(CharRange.isNot(str.charAt(pos + 1)));
                    pos += 2;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public CharRange[] getCharRanges() {
        Set<CharRange> set2 = this.set;
        return (CharRange[]) set2.toArray(new CharRange[set2.size()]);
    }

    public boolean contains(char ch) {
        for (CharRange range : this.set) {
            if (range.contains(ch)) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CharSet)) {
            return false;
        }
        return this.set.equals(((CharSet) obj).set);
    }

    public int hashCode() {
        return this.set.hashCode() + 89;
    }

    public String toString() {
        return this.set.toString();
    }
}
