package org.apache.commons.lang3;

import com.github.mikephil.charting.utils.Utils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class StringUtils {

    /* renamed from: CR */
    public static final String f209CR = "\r";
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;

    /* renamed from: LF */
    public static final String f210LF = "\n";
    private static final int PAD_LIMIT = 8192;
    public static final String SPACE = " ";

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isAnyEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return false;
        }
        for (CharSequence cs : css) {
            if (isEmpty(cs)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoneEmpty(CharSequence... css) {
        return !isAnyEmpty(css);
    }

    public static boolean isAllEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return true;
        }
        for (CharSequence cs : css) {
            if (isNotEmpty(cs)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs != null) {
            int length = cs.length();
            int strLen = length;
            if (length != 0) {
                for (int i = 0; i < strLen; i++) {
                    if (!Character.isWhitespace(cs.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isAnyBlank(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return false;
        }
        for (CharSequence cs : css) {
            if (isBlank(cs)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoneBlank(CharSequence... css) {
        return !isAnyBlank(css);
    }

    public static boolean isAllBlank(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return true;
        }
        for (CharSequence cs : css) {
            if (isNotBlank(cs)) {
                return false;
            }
        }
        return true;
    }

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static String trimToNull(String str) {
        String ts = trim(str);
        if (isEmpty(ts)) {
            return null;
        }
        return ts;
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    public static String truncate(String str, int maxWidth) {
        return truncate(str, 0, maxWidth);
    }

    public static String truncate(String str, int offset, int maxWidth) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        } else if (maxWidth < 0) {
            throw new IllegalArgumentException("maxWith cannot be negative");
        } else if (str == null) {
            return null;
        } else {
            if (offset > str.length()) {
                return "";
            }
            if (str.length() <= maxWidth) {
                return str.substring(offset);
            }
            return str.substring(offset, offset + maxWidth > str.length() ? str.length() : offset + maxWidth);
        }
    }

    public static String strip(String str) {
        return strip(str, (String) null);
    }

    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        String str2 = strip(str, (String) null);
        if (str2.isEmpty()) {
            return null;
        }
        return str2;
    }

    public static String stripToEmpty(String str) {
        return str == null ? "" : strip(str, (String) null);
    }

    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        return stripEnd(stripStart(str, stripChars), stripChars);
    }

    public static String stripStart(String str, String stripChars) {
        if (str != null) {
            int length = str.length();
            int strLen = length;
            if (length != 0) {
                int start = 0;
                if (stripChars == null) {
                    while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                        start++;
                    }
                } else if (stripChars.isEmpty()) {
                    return str;
                } else {
                    while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
                        start++;
                    }
                }
                return str.substring(start);
            }
        }
        return str;
    }

    public static String stripEnd(String str, String stripChars) {
        if (str != null) {
            int length = str.length();
            int end = length;
            if (length != 0) {
                if (stripChars == null) {
                    while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                        end--;
                    }
                } else if (stripChars.isEmpty()) {
                    return str;
                } else {
                    while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                        end--;
                    }
                }
                return str.substring(0, end);
            }
        }
        return str;
    }

    public static String[] stripAll(String... strs) {
        return stripAll(strs, (String) null);
    }

    public static String[] stripAll(String[] strs, String stripChars) {
        if (strs != null) {
            int length = strs.length;
            int strsLen = length;
            if (length != 0) {
                String[] newArr = new String[strsLen];
                for (int i = 0; i < strsLen; i++) {
                    newArr[i] = strip(strs[i], stripChars);
                }
                return newArr;
            }
        }
        return strs;
    }

    public static String stripAccents(String input) {
        if (input == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD));
        convertRemainingAccentCharacters(decomposed);
        return pattern.matcher(decomposed).replaceAll("");
    }

    private static void convertRemainingAccentCharacters(StringBuilder decomposed) {
        for (int i = 0; i < decomposed.length(); i++) {
            if (decomposed.charAt(i) == 321) {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'L');
            } else if (decomposed.charAt(i) == 322) {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'l');
            }
        }
    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null || cs1.length() != cs2.length()) {
            return false;
        }
        if ((cs1 instanceof String) && (cs2 instanceof String)) {
            return cs1.equals(cs2);
        }
        return CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, cs1.length());
    }

    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        if (str1 == null || str2 == null) {
            if (str1 == str2) {
                return true;
            }
            return false;
        } else if (str1 == str2) {
            return true;
        } else {
            if (str1.length() != str2.length()) {
                return false;
            }
            return CharSequenceUtils.regionMatches(str1, true, 0, str2, 0, str1.length());
        }
    }

    public static int compare(String str1, String str2) {
        return compare(str1, str2, true);
    }

    public static int compare(String str1, String str2, boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        if (str1 == null) {
            if (nullIsLess) {
                return -1;
            }
            return 1;
        } else if (str2 != null) {
            return str1.compareTo(str2);
        } else {
            if (nullIsLess) {
                return 1;
            }
            return -1;
        }
    }

    public static int compareIgnoreCase(String str1, String str2) {
        return compareIgnoreCase(str1, str2, true);
    }

    public static int compareIgnoreCase(String str1, String str2, boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        if (str1 == null) {
            if (nullIsLess) {
                return -1;
            }
            return 1;
        } else if (str2 != null) {
            return str1.compareToIgnoreCase(str2);
        } else {
            if (nullIsLess) {
                return 1;
            }
            return -1;
        }
    }

    public static boolean equalsAny(CharSequence string, CharSequence... searchStrings) {
        if (!ArrayUtils.isNotEmpty((T[]) searchStrings)) {
            return false;
        }
        for (CharSequence next : searchStrings) {
            if (equals(string, next)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equalsAnyIgnoreCase(CharSequence string, CharSequence... searchStrings) {
        if (!ArrayUtils.isNotEmpty((T[]) searchStrings)) {
            return false;
        }
        for (CharSequence next : searchStrings) {
            if (equalsIgnoreCase(string, next)) {
                return true;
            }
        }
        return false;
    }

    public static int indexOf(CharSequence seq, int searchChar) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, 0);
    }

    public static int indexOf(CharSequence seq, int searchChar, int startPos) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, startPos);
    }

    public static int indexOf(CharSequence seq, CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, 0);
    }

    public static int indexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, startPos);
    }

    public static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0020  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0027  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int ordinalIndexOf(java.lang.CharSequence r3, java.lang.CharSequence r4, int r5, boolean r6) {
        /*
            r0 = -1
            if (r3 == 0) goto L_0x0035
            if (r4 == 0) goto L_0x0035
            if (r5 > 0) goto L_0x0008
            goto L_0x0035
        L_0x0008:
            int r1 = r4.length()
            if (r1 != 0) goto L_0x0017
            if (r6 == 0) goto L_0x0015
            int r0 = r3.length()
            goto L_0x0016
        L_0x0015:
            r0 = 0
        L_0x0016:
            return r0
        L_0x0017:
            r1 = 0
            if (r6 == 0) goto L_0x001e
            int r0 = r3.length()
        L_0x001e:
            if (r6 == 0) goto L_0x0027
            int r2 = r0 + -1
            int r0 = org.apache.commons.lang3.CharSequenceUtils.lastIndexOf((java.lang.CharSequence) r3, (java.lang.CharSequence) r4, (int) r2)
            goto L_0x002d
        L_0x0027:
            int r2 = r0 + 1
            int r0 = org.apache.commons.lang3.CharSequenceUtils.indexOf((java.lang.CharSequence) r3, (java.lang.CharSequence) r4, (int) r2)
        L_0x002d:
            if (r0 >= 0) goto L_0x0030
            return r0
        L_0x0030:
            int r1 = r1 + 1
            if (r1 < r5) goto L_0x001e
            return r0
        L_0x0035:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.ordinalIndexOf(java.lang.CharSequence, java.lang.CharSequence, int, boolean):int");
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        int endLimit = (str.length() - searchStr.length()) + 1;
        if (startPos > endLimit) {
            return -1;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; i++) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence seq, int searchChar) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, seq.length());
    }

    public static int lastIndexOf(CharSequence seq, int searchChar, int startPos) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, startPos);
    }

    public static int lastIndexOf(CharSequence seq, CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, seq.length());
    }

    public static int lastOrdinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, true);
    }

    public static int lastIndexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, startPos);
    }

    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos > str.length() - searchStr.length()) {
            startPos = str.length() - searchStr.length();
        }
        if (startPos < 0) {
            return -1;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i >= 0; i--) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(CharSequence seq, int searchChar) {
        if (!isEmpty(seq) && CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0) {
            return true;
        }
        return false;
    }

    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        if (seq == null || searchSeq == null || CharSequenceUtils.indexOf(seq, searchSeq, 0) < 0) {
            return false;
        }
        return true;
    }

    public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsWhitespace(CharSequence seq) {
        if (isEmpty(seq)) {
            return false;
        }
        int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(seq.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static int indexOfAny(CharSequence cs, char... searchChars) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars)) {
            return -1;
        }
        int csLen = cs.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        int i = 0;
        while (i < csLen) {
            char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch && (i >= csLast || j >= searchLast || !Character.isHighSurrogate(ch) || searchChars[j + 1] == cs.charAt(i + 1))) {
                    return i;
                }
            }
            i++;
        }
        return -1;
    }

    public static int indexOfAny(CharSequence cs, String searchChars) {
        if (isEmpty(cs) || isEmpty(searchChars)) {
            return -1;
        }
        return indexOfAny(cs, searchChars.toCharArray());
    }

    public static boolean containsAny(CharSequence cs, char... searchChars) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars)) {
            return false;
        }
        int csLength = cs.length();
        int searchLength = searchChars.length;
        int csLast = csLength - 1;
        int searchLast = searchLength - 1;
        for (int i = 0; i < csLength; i++) {
            char ch = cs.charAt(i);
            for (int j = 0; j < searchLength; j++) {
                if (searchChars[j] == ch) {
                    if (!Character.isHighSurrogate(ch) || j == searchLast) {
                        return true;
                    }
                    if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean containsAny(CharSequence cs, CharSequence searchChars) {
        if (searchChars == null) {
            return false;
        }
        return containsAny(cs, CharSequenceUtils.toCharArray(searchChars));
    }

    public static boolean containsAny(CharSequence cs, CharSequence... searchCharSequences) {
        if (isEmpty(cs) || ArrayUtils.isEmpty((Object[]) searchCharSequences)) {
            return false;
        }
        for (CharSequence searchCharSequence : searchCharSequences) {
            if (contains(cs, searchCharSequence)) {
                return true;
            }
        }
        return false;
    }

    public static int indexOfAnyBut(CharSequence cs, char... searchChars) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars)) {
            return -1;
        }
        int csLen = cs.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        int i = 0;
        while (i < csLen) {
            char ch = cs.charAt(i);
            int j = 0;
            while (j < searchLen) {
                if (searchChars[j] != ch || (i < csLast && j < searchLast && Character.isHighSurrogate(ch) && searchChars[j + 1] != cs.charAt(i + 1))) {
                    j++;
                } else {
                    i++;
                }
            }
            return i;
        }
        return -1;
    }

    public static int indexOfAnyBut(CharSequence seq, CharSequence searchChars) {
        if (isEmpty(seq) || isEmpty(searchChars)) {
            return -1;
        }
        int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            char ch = seq.charAt(i);
            boolean chFound = CharSequenceUtils.indexOf(searchChars, (int) ch, 0) >= 0;
            if (i + 1 < strLen && Character.isHighSurrogate(ch)) {
                char ch2 = seq.charAt(i + 1);
                if (chFound && CharSequenceUtils.indexOf(searchChars, (int) ch2, 0) < 0) {
                    return i;
                }
            } else if (!chFound) {
                return i;
            }
        }
        return -1;
    }

    public static boolean containsOnly(CharSequence cs, char... valid) {
        if (valid == null || cs == null) {
            return false;
        }
        if (cs.length() == 0) {
            return true;
        }
        if (valid.length != 0 && indexOfAnyBut(cs, valid) == -1) {
            return true;
        }
        return false;
    }

    public static boolean containsOnly(CharSequence cs, String validChars) {
        if (cs == null || validChars == null) {
            return false;
        }
        return containsOnly(cs, validChars.toCharArray());
    }

    public static boolean containsNone(CharSequence cs, char... searchChars) {
        if (cs == null || searchChars == null) {
            return true;
        }
        int csLen = cs.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (!Character.isHighSurrogate(ch) || j == searchLast) {
                        return false;
                    }
                    if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean containsNone(CharSequence cs, String invalidChars) {
        if (cs == null || invalidChars == null) {
            return true;
        }
        return containsNone(cs, invalidChars.toCharArray());
    }

    public static int indexOfAny(CharSequence str, CharSequence... searchStrs) {
        int tmp;
        if (str == null || searchStrs == null) {
            return -1;
        }
        int ret = Integer.MAX_VALUE;
        for (CharSequence search : searchStrs) {
            if (!(search == null || (tmp = CharSequenceUtils.indexOf(str, search, 0)) == -1 || tmp >= ret)) {
                ret = tmp;
            }
        }
        if (ret == Integer.MAX_VALUE) {
            return -1;
        }
        return ret;
    }

    public static int lastIndexOfAny(CharSequence str, CharSequence... searchStrs) {
        int tmp;
        if (str == null || searchStrs == null) {
            return -1;
        }
        int ret = -1;
        for (CharSequence search : searchStrs) {
            if (search != null && (tmp = CharSequenceUtils.lastIndexOf(str, search, str.length())) > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start += str.length();
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end += str.length();
        }
        if (start < 0) {
            start += str.length();
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return "";
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringAfter(String str, String separator) {
        int pos;
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null || (pos = str.indexOf(separator)) == -1) {
            return "";
        }
        return str.substring(separator.length() + pos);
    }

    public static String substringBeforeLast(String str, String separator) {
        int pos;
        if (isEmpty(str) || isEmpty(separator) || (pos = str.lastIndexOf(separator)) == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringAfterLast(String str, String separator) {
        int pos;
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator) || (pos = str.lastIndexOf(separator)) == -1 || pos == str.length() - separator.length()) {
            return "";
        }
        return str.substring(separator.length() + pos);
    }

    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    public static String substringBetween(String str, String open, String close) {
        int start;
        int end;
        if (str == null || open == null || close == null || (start = str.indexOf(open)) == -1 || (end = str.indexOf(close, open.length() + start)) == -1) {
            return null;
        }
        return str.substring(open.length() + start, end);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0032, code lost:
        r6 = r6 + r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String[] substringsBetween(java.lang.String r9, java.lang.String r10, java.lang.String r11) {
        /*
            r0 = 0
            if (r9 == 0) goto L_0x0058
            boolean r1 = isEmpty(r10)
            if (r1 != 0) goto L_0x0058
            boolean r1 = isEmpty(r11)
            if (r1 == 0) goto L_0x0010
            goto L_0x0058
        L_0x0010:
            int r1 = r9.length()
            if (r1 != 0) goto L_0x0019
            java.lang.String[] r0 = org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY
            return r0
        L_0x0019:
            int r2 = r11.length()
            int r3 = r10.length()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r5 = 0
        L_0x0027:
            int r6 = r1 - r2
            if (r5 >= r6) goto L_0x0044
            int r6 = r9.indexOf(r10, r5)
            if (r6 >= 0) goto L_0x0032
            goto L_0x0044
        L_0x0032:
            int r6 = r6 + r3
            int r7 = r9.indexOf(r11, r6)
            if (r7 >= 0) goto L_0x003a
            goto L_0x0044
        L_0x003a:
            java.lang.String r8 = r9.substring(r6, r7)
            r4.add(r8)
            int r5 = r7 + r2
            goto L_0x0027
        L_0x0044:
            boolean r6 = r4.isEmpty()
            if (r6 == 0) goto L_0x004b
            return r0
        L_0x004b:
            int r0 = r4.size()
            java.lang.String[] r0 = new java.lang.String[r0]
            java.lang.Object[] r0 = r4.toArray(r0)
            java.lang.String[] r0 = (java.lang.String[]) r0
            return r0
        L_0x0058:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.substringsBetween(java.lang.String, java.lang.String, java.lang.String):java.lang.String[]");
    }

    public static String[] split(String str) {
        return split(str, (String) null, -1);
    }

    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (separator == null || "".equals(separator)) {
            return splitWorker(str, (String) null, max, preserveAllTokens);
        }
        int separatorLength = separator.length();
        ArrayList<String> substrings = new ArrayList<>();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);
            if (end <= -1) {
                substrings.add(str.substring(beg));
                end = len;
            } else if (end > beg) {
                numberOfSubstrings++;
                if (numberOfSubstrings == max) {
                    end = len;
                    substrings.add(str.substring(beg));
                } else {
                    substrings.add(str.substring(beg, end));
                    beg = end + separatorLength;
                }
            } else {
                if (preserveAllTokens) {
                    numberOfSubstrings++;
                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        substrings.add("");
                    }
                }
                beg = end + separatorLength;
            }
        }
        return (String[]) substrings.toArray(new String[substrings.size()]);
    }

    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, (String) null, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<>();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                i++;
                start = i;
            } else {
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        int i;
        int i2;
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i3 = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            while (i3 < len) {
                if (Character.isWhitespace(str.charAt(i3))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        int sizePlus12 = sizePlus1 + 1;
                        if (sizePlus1 == max) {
                            lastMatch = false;
                            i3 = len;
                        }
                        list.add(str.substring(start, i3));
                        match = false;
                        sizePlus1 = sizePlus12;
                    }
                    i3++;
                    start = i3;
                } else {
                    lastMatch = false;
                    match = true;
                    i3++;
                }
            }
        } else if (separatorChars.length() == 1) {
            char sep = separatorChars.charAt(0);
            while (i3 < len) {
                if (str.charAt(i3) == sep) {
                    if (match || preserveAllTokens) {
                        boolean lastMatch2 = true;
                        int sizePlus13 = sizePlus1 + 1;
                        if (sizePlus1 == max) {
                            lastMatch2 = false;
                            i3 = len;
                        }
                        list.add(str.substring(start, i3));
                        match = false;
                        sizePlus1 = sizePlus13;
                    }
                    i2 = i3 + 1;
                    start = i2;
                } else {
                    lastMatch = false;
                    match = true;
                    i2 = i3 + 1;
                }
            }
        } else {
            while (i3 < len) {
                if (separatorChars.indexOf(str.charAt(i3)) >= 0) {
                    if (match || preserveAllTokens) {
                        boolean lastMatch3 = true;
                        int sizePlus14 = sizePlus1 + 1;
                        if (sizePlus1 == max) {
                            lastMatch3 = false;
                            i3 = len;
                        }
                        list.add(str.substring(start, i3));
                        match = false;
                        sizePlus1 = sizePlus14;
                    }
                    i = i3 + 1;
                    start = i;
                } else {
                    lastMatch = false;
                    match = true;
                    i = i3 + 1;
                }
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i3));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] splitByCharacterType(String str) {
        return splitByCharacterType(str, false);
    }

    public static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        char[] c = str.toCharArray();
        List<String> list = new ArrayList<>();
        int tokenStart = 0;
        int currentType = Character.getType(c[0]);
        for (int pos = 0 + 1; pos < c.length; pos++) {
            int type = Character.getType(c[pos]);
            if (type != currentType) {
                if (camelCase && type == 2 && currentType == 1) {
                    int newTokenStart = pos - 1;
                    if (newTokenStart != tokenStart) {
                        list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                        tokenStart = newTokenStart;
                    }
                } else {
                    list.add(new String(c, tokenStart, pos - tokenStart));
                    tokenStart = pos;
                }
                currentType = type;
            }
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return (String[]) list.toArray(new String[list.size()]);
    }

    @SafeVarargs
    public static <T> String join(T... elements) {
        return join((Object[]) elements, (String) null);
    }

    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(long[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(int[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(short[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(byte[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(char[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(float[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(double[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(long[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(int[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(byte[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(short[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(char[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(double[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(float[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator<?> iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Iterable<?> iterable, char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(Iterable<?> iterable, String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String joinWith(String separator, Object... objects) {
        if (objects != null) {
            String sanitizedSeparator = defaultString(separator, "");
            StringBuilder result = new StringBuilder();
            Iterator<Object> iterator = Arrays.asList(objects).iterator();
            while (iterator.hasNext()) {
                result.append(Objects.toString(iterator.next(), ""));
                if (iterator.hasNext()) {
                    result.append(sanitizedSeparator);
                }
            }
            return result.toString();
        }
        throw new IllegalArgumentException("Object varargs must not be null");
    }

    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count] = str.charAt(i);
                count++;
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    public static String removeStart(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove) || !str.startsWith(remove)) {
            return str;
        }
        return str.substring(remove.length());
    }

    public static String removeStartIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove) || !startsWithIgnoreCase(str, remove)) {
            return str;
        }
        return str.substring(remove.length());
    }

    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove) || !str.endsWith(remove)) {
            return str;
        }
        return str.substring(0, str.length() - remove.length());
    }

    public static String removeEndIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove) || !endsWithIgnoreCase(str, remove)) {
            return str;
        }
        return str.substring(0, str.length() - remove.length());
    }

    public static String remove(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, "", -1);
    }

    public static String removeIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replaceIgnoreCase(str, remove, "", -1);
    }

    public static String remove(String str, char remove) {
        if (isEmpty(str) || str.indexOf(remove) == -1) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos] = chars[i];
                pos++;
            }
        }
        return new String(chars, 0, pos);
    }

    public static String removeAll(String text, String regex) {
        return replaceAll(text, regex, "");
    }

    public static String removeFirst(String text, String regex) {
        return replaceFirst(text, regex, "");
    }

    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    public static String replaceOnceIgnoreCase(String text, String searchString, String replacement) {
        return replaceIgnoreCase(text, searchString, replacement, 1);
    }

    public static String replacePattern(String source, String regex, String replacement) {
        if (source == null || regex == null || replacement == null) {
            return source;
        }
        return Pattern.compile(regex, 32).matcher(source).replaceAll(replacement);
    }

    public static String removePattern(String source, String regex) {
        return replacePattern(source, regex, "");
    }

    public static String replaceAll(String text, String regex, String replacement) {
        if (text == null || regex == null || replacement == null) {
            return text;
        }
        return text.replaceAll(regex, replacement);
    }

    public static String replaceFirst(String text, String regex, String replacement) {
        if (text == null || regex == null || replacement == null) {
            return text;
        }
        return text.replaceFirst(regex, replacement);
    }

    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    public static String replaceIgnoreCase(String text, String searchString, String replacement) {
        return replaceIgnoreCase(text, searchString, replacement, -1);
    }

    public static String replace(String text, String searchString, String replacement, int max) {
        return replace(text, searchString, replacement, max, false);
    }

    private static String replace(String text, String searchString, String replacement, int max, boolean ignoreCase) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        String searchText = text;
        if (ignoreCase) {
            searchText = text.toLowerCase();
            searchString = searchString.toLowerCase();
        }
        int start = 0;
        int end = searchText.indexOf(searchString, 0);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        int increase2 = increase < 0 ? 0 : increase;
        int i = 64;
        if (max < 0) {
            i = 16;
        } else if (max <= 64) {
            i = max;
        }
        StringBuilder buf = new StringBuilder(text.length() + (increase2 * i));
        while (end != -1) {
            buf.append(text, start, end);
            buf.append(replacement);
            start = end + replLength;
            max--;
            if (max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    public static String replaceIgnoreCase(String text, String searchString, String replacement, int max) {
        return replace(text, searchString, replacement, max, true);
    }

    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, true, searchList == null ? 0 : searchList.length);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
        int greater;
        String str = text;
        String[] strArr = searchList;
        String[] strArr2 = replacementList;
        boolean z = repeat;
        if (str == null || text.isEmpty() || strArr == null || strArr.length == 0 || strArr2 == null || strArr2.length == 0) {
            return str;
        }
        if (timeToLive >= 0) {
            int searchLength = strArr.length;
            int replacementLength = strArr2.length;
            if (searchLength == replacementLength) {
                boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
                int textIndex = -1;
                int replaceIndex = -1;
                for (int i = 0; i < searchLength; i++) {
                    if (!noMoreMatchesForReplIndex[i] && strArr[i] != null && !strArr[i].isEmpty() && strArr2[i] != null) {
                        int tempIndex = str.indexOf(strArr[i]);
                        if (tempIndex == -1) {
                            noMoreMatchesForReplIndex[i] = true;
                        } else if (textIndex == -1 || tempIndex < textIndex) {
                            textIndex = tempIndex;
                            replaceIndex = i;
                        }
                    }
                }
                if (textIndex == -1) {
                    return str;
                }
                int start = 0;
                int increase = 0;
                for (int i2 = 0; i2 < strArr.length; i2++) {
                    if (!(strArr[i2] == null || strArr2[i2] == null || (greater = strArr2[i2].length() - strArr[i2].length()) <= 0)) {
                        increase += greater * 3;
                    }
                }
                StringBuilder buf = new StringBuilder(text.length() + Math.min(increase, text.length() / 5));
                while (textIndex != -1) {
                    for (int i3 = start; i3 < textIndex; i3++) {
                        buf.append(str.charAt(i3));
                    }
                    buf.append(strArr2[replaceIndex]);
                    start = textIndex + strArr[replaceIndex].length();
                    textIndex = -1;
                    replaceIndex = -1;
                    for (int i4 = 0; i4 < searchLength; i4++) {
                        if (!noMoreMatchesForReplIndex[i4] && strArr[i4] != null && !strArr[i4].isEmpty()) {
                            if (strArr2[i4] != null) {
                                int tempIndex2 = str.indexOf(strArr[i4], start);
                                if (tempIndex2 == -1) {
                                    noMoreMatchesForReplIndex[i4] = true;
                                } else if (textIndex == -1 || tempIndex2 < textIndex) {
                                    textIndex = tempIndex2;
                                    replaceIndex = i4;
                                }
                            }
                        }
                    }
                }
                int textLength = text.length();
                for (int i5 = start; i5 < textLength; i5++) {
                    buf.append(str.charAt(i5));
                }
                String result = buf.toString();
                if (!z) {
                    return result;
                }
                return replaceEach(result, strArr, strArr2, z, timeToLive - 1);
            }
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        }
        throw new IllegalStateException("Aborting to protect against StackOverflowError - output of one loop is the input of another");
    }

    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = "";
        }
        boolean modified = false;
        int replaceCharsLength = replaceChars.length();
        int strLength = str.length();
        StringBuilder buf = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }

    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = "";
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return str.substring(0, start) + overlay + str.substring(end);
    }

    public static String chomp(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            char ch = str.charAt(0);
            if (ch == 13 || ch == 10) {
                return "";
            }
            return str;
        }
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        if (last == 10) {
            if (str.charAt(lastIdx - 1) == 13) {
                lastIdx--;
            }
        } else if (last != 13) {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    @Deprecated
    public static String chomp(String str, String separator) {
        return removeEnd(str, separator);
    }

    public static String chop(String str) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (strLen < 2) {
            return "";
        }
        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        if (str.charAt(lastIdx) == 10 && ret.charAt(lastIdx - 1) == 13) {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }

    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= 8192) {
            return repeat(str.charAt(0), repeat);
        }
        int outputLength = inputLength * repeat;
        if (inputLength == 1) {
            return repeat(str.charAt(0), repeat);
        }
        if (inputLength != 2) {
            StringBuilder buf = new StringBuilder(outputLength);
            for (int i = 0; i < repeat; i++) {
                buf.append(str);
            }
            return buf.toString();
        }
        char ch0 = str.charAt(0);
        char ch1 = str.charAt(1);
        char[] output2 = new char[outputLength];
        for (int i2 = (repeat * 2) - 2; i2 >= 0; i2 = (i2 - 1) - 1) {
            output2[i2] = ch0;
            output2[i2 + 1] = ch1;
        }
        return new String(output2);
    }

    public static String repeat(String str, String separator, int repeat) {
        if (str == null || separator == null) {
            return repeat(str, repeat);
        }
        return removeEnd(repeat(str + separator, repeat), separator);
    }

    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        }
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int padLen = padStr.length();
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        }
        if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padLen];
        }
        return str.concat(new String(padding));
    }

    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int padLen = padStr.length();
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padLen];
        }
        return new String(padding).concat(str);
    }

    public static int length(CharSequence cs) {
        if (cs == null) {
            return 0;
        }
        return cs.length();
    }

    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        r0 = r3.length();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String center(java.lang.String r3, int r4, char r5) {
        /*
            if (r3 == 0) goto L_0x001a
            if (r4 > 0) goto L_0x0005
            goto L_0x001a
        L_0x0005:
            int r0 = r3.length()
            int r1 = r4 - r0
            if (r1 > 0) goto L_0x000e
            return r3
        L_0x000e:
            int r2 = r1 / 2
            int r2 = r2 + r0
            java.lang.String r3 = leftPad((java.lang.String) r3, (int) r2, (char) r5)
            java.lang.String r3 = rightPad((java.lang.String) r3, (int) r4, (char) r5)
            return r3
        L_0x001a:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.center(java.lang.String, int, char):java.lang.String");
    }

    public static String center(String str, int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        return rightPad(leftPad(str, (pads / 2) + strLen, padStr), size, padStr);
    }

    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    public static String upperCase(String str, Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(locale);
    }

    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String lowerCase(String str, Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(locale);
    }

    public static String capitalize(String str) {
        int firstCodepoint;
        int newCodePoint;
        if (str != null) {
            int length = str.length();
            int strLen = length;
            if (length == 0 || (firstCodepoint = str.codePointAt(0)) == (newCodePoint = Character.toTitleCase(firstCodepoint))) {
                return str;
            }
            int[] newCodePoints = new int[strLen];
            int outOffset = 0 + 1;
            newCodePoints[0] = newCodePoint;
            int inOffset = Character.charCount(firstCodepoint);
            while (inOffset < strLen) {
                int codepoint = str.codePointAt(inOffset);
                newCodePoints[outOffset] = codepoint;
                inOffset += Character.charCount(codepoint);
                outOffset++;
            }
            return new String(newCodePoints, 0, outOffset);
        }
        return str;
    }

    public static String uncapitalize(String str) {
        int firstCodepoint;
        int newCodePoint;
        if (str != null) {
            int length = str.length();
            int strLen = length;
            if (length == 0 || (firstCodepoint = str.codePointAt(0)) == (newCodePoint = Character.toLowerCase(firstCodepoint))) {
                return str;
            }
            int[] newCodePoints = new int[strLen];
            int outOffset = 0 + 1;
            newCodePoints[0] = newCodePoint;
            int inOffset = Character.charCount(firstCodepoint);
            while (inOffset < strLen) {
                int codepoint = str.codePointAt(inOffset);
                newCodePoints[outOffset] = codepoint;
                inOffset += Character.charCount(codepoint);
                outOffset++;
            }
            return new String(newCodePoints, 0, outOffset);
        }
        return str;
    }

    public static String swapCase(String str) {
        int newCodePoint;
        if (isEmpty(str)) {
            return str;
        }
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        int i = 0;
        while (i < strLen) {
            int oldCodepoint = str.codePointAt(i);
            if (Character.isUpperCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isTitleCase(oldCodepoint) != 0) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isLowerCase(oldCodepoint) != 0) {
                newCodePoint = Character.toUpperCase(oldCodepoint);
            } else {
                newCodePoint = oldCodepoint;
            }
            newCodePoints[outOffset] = newCodePoint;
            i += Character.charCount(newCodePoint);
            outOffset++;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static int countMatches(CharSequence str, CharSequence sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while (true) {
            int indexOf = CharSequenceUtils.indexOf(str, sub, idx);
            int idx2 = indexOf;
            if (indexOf == -1) {
                return count;
            }
            count++;
            idx = idx2 + sub.length();
        }
    }

    public static int countMatches(CharSequence str, char ch) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isAlpha(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphaSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumericSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isAsciiPrintable(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!CharUtils.isAsciiPrintable(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumericSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public static String getDigits(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        StringBuilder strDigits = new StringBuilder(sz);
        for (int i = 0; i < sz; i++) {
            char tempChar = str.charAt(i);
            if (Character.isDigit(tempChar)) {
                strDigits.append(tempChar);
            }
        }
        return strDigits.toString();
    }

    public static boolean isWhitespace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllLowerCase(CharSequence cs) {
        if (cs == null || isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLowerCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllUpperCase(CharSequence cs) {
        if (cs == null || isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isUpperCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMixedCase(CharSequence cs) {
        if (isEmpty(cs) || cs.length() == 1) {
            return false;
        }
        boolean containsUppercase = false;
        boolean containsLowercase = false;
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (containsUppercase && containsLowercase) {
                return true;
            }
            if (Character.isUpperCase(cs.charAt(i))) {
                containsUppercase = true;
            } else if (Character.isLowerCase(cs.charAt(i))) {
                containsLowercase = true;
            }
        }
        if (!containsUppercase || !containsLowercase) {
            return false;
        }
        return true;
    }

    public static String defaultString(String str) {
        return str == null ? "" : str;
    }

    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    public static <T extends CharSequence> T defaultIfBlank(T str, T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    public static String rotate(String str, int shift) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (shift == 0 || strLen == 0 || shift % strLen == 0) {
            return str;
        }
        StringBuilder builder = new StringBuilder(strLen);
        int offset = -(shift % strLen);
        builder.append(substring(str, offset));
        builder.append(substring(str, 0, offset));
        return builder.toString();
    }

    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    public static String reverseDelimited(String str, char separatorChar) {
        if (str == null) {
            return null;
        }
        String[] strs = split(str, separatorChar);
        ArrayUtils.reverse((Object[]) strs);
        return join((Object[]) strs, separatorChar);
    }

    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, "...", 0, maxWidth);
    }

    public static String abbreviate(String str, int offset, int maxWidth) {
        return abbreviate(str, "...", offset, maxWidth);
    }

    public static String abbreviate(String str, String abbrevMarker, int maxWidth) {
        return abbreviate(str, abbrevMarker, 0, maxWidth);
    }

    public static String abbreviate(String str, String abbrevMarker, int offset, int maxWidth) {
        if (isEmpty(str) || isEmpty(abbrevMarker)) {
            return str;
        }
        int abbrevMarkerLength = abbrevMarker.length();
        int minAbbrevWidth = abbrevMarkerLength + 1;
        int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;
        if (maxWidth < minAbbrevWidth) {
            throw new IllegalArgumentException(String.format("Minimum abbreviation width is %d", new Object[]{Integer.valueOf(minAbbrevWidth)}));
        } else if (str.length() <= maxWidth) {
            return str;
        } else {
            if (offset > str.length()) {
                offset = str.length();
            }
            if (str.length() - offset < maxWidth - abbrevMarkerLength) {
                offset = str.length() - (maxWidth - abbrevMarkerLength);
            }
            if (offset <= abbrevMarkerLength + 1) {
                return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
            } else if (maxWidth < minAbbrevWidthOffset) {
                throw new IllegalArgumentException(String.format("Minimum abbreviation width with offset is %d", new Object[]{Integer.valueOf(minAbbrevWidthOffset)}));
            } else if ((offset + maxWidth) - abbrevMarkerLength < str.length()) {
                return abbrevMarker + abbreviate(str.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength);
            } else {
                return abbrevMarker + str.substring(str.length() - (maxWidth - abbrevMarkerLength));
            }
        }
    }

    public static String abbreviateMiddle(String str, String middle, int length) {
        if (isEmpty(str) || isEmpty(middle) || length >= str.length() || length < middle.length() + 2) {
            return str;
        }
        int targetSting = length - middle.length();
        int endOffset = str.length() - (targetSting / 2);
        return str.substring(0, (targetSting / 2) + (targetSting % 2)) + middle + str.substring(endOffset);
    }

    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return "";
        }
        return str2.substring(at);
    }

    public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return -1;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i = 0;
        while (i < cs1.length() && i < cs2.length() && cs1.charAt(i) == cs2.charAt(i)) {
            i++;
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return -1;
    }

    public static int indexOfDifference(CharSequence... css) {
        if (css == null || css.length <= 1) {
            return -1;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        int arrayLen = css.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;
        for (CharSequence cs : css) {
            if (cs == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(cs.length(), shortestStrLen);
                longestStrLen = Math.max(cs.length(), longestStrLen);
            }
        }
        if (allStringsNull || (longestStrLen == 0 && !anyStringNull)) {
            return -1;
        }
        if (shortestStrLen == 0) {
            return 0;
        }
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            char comparisonChar = css[0].charAt(stringPos);
            int arrayPos = 1;
            while (true) {
                if (arrayPos >= arrayLen) {
                    break;
                } else if (css[arrayPos].charAt(stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                } else {
                    arrayPos++;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }
        if (firstDiff != -1 || shortestStrLen == longestStrLen) {
            return firstDiff;
        }
        return shortestStrLen;
    }

    public static String getCommonPrefix(String... strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        int smallestIndexOfDiff = indexOfDifference(strs);
        if (smallestIndexOfDiff == -1) {
            if (strs[0] == null) {
                return "";
            }
            return strs[0];
        } else if (smallestIndexOfDiff == 0) {
            return "";
        } else {
            return strs[0].substring(0, smallestIndexOfDiff);
        }
    }

    @Deprecated
    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        if (n > m) {
            CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }
        int[] p = new int[(n + 1)];
        for (int i = 0; i <= n; i++) {
            p[i] = i;
        }
        for (int j = 1; j <= m; j++) {
            int upper_left = p[0];
            char t_j = t.charAt(j - 1);
            p[0] = j;
            for (int i2 = 1; i2 <= n; i2++) {
                int upper = p[i2];
                p[i2] = Math.min(Math.min(p[i2 - 1] + 1, p[i2] + 1), upper_left + (s.charAt(i2 + -1) == t_j ? 0 : 1));
                upper_left = upper;
            }
        }
        return p[n];
    }

    @Deprecated
    public static int getLevenshteinDistance(CharSequence s, CharSequence t, int threshold) {
        CharSequence t2;
        CharSequence s2;
        int i = threshold;
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        } else if (i >= 0) {
            int n = s.length();
            int m = t.length();
            int i2 = -1;
            if (n == 0) {
                if (m <= i) {
                    return m;
                }
                return -1;
            } else if (m == 0) {
                if (n <= i) {
                    return n;
                }
                return -1;
            } else if (Math.abs(n - m) > i) {
                return -1;
            } else {
                if (n > m) {
                    s2 = t;
                    t2 = s;
                    n = m;
                    m = t2.length();
                } else {
                    s2 = s;
                    t2 = t;
                }
                int[] p = new int[(n + 1)];
                int[] d = new int[(n + 1)];
                int boundary = Math.min(n, i) + 1;
                for (int i3 = 0; i3 < boundary; i3++) {
                    p[i3] = i3;
                }
                int i4 = Integer.MAX_VALUE;
                Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
                Arrays.fill(d, Integer.MAX_VALUE);
                int j = 1;
                while (j <= m) {
                    char t_j = t2.charAt(j - 1);
                    d[0] = j;
                    int min = Math.max(1, j - i);
                    int max = j > i4 - i ? n : Math.min(n, j + i);
                    if (min > max) {
                        return i2;
                    }
                    if (min > 1) {
                        d[min - 1] = i4;
                    }
                    for (int i5 = min; i5 <= max; i5++) {
                        if (s2.charAt(i5 - 1) == t_j) {
                            d[i5] = p[i5 - 1];
                        } else {
                            d[i5] = Math.min(Math.min(d[i5 - 1], p[i5]), p[i5 - 1]) + 1;
                        }
                    }
                    int[] _d = p;
                    p = d;
                    d = _d;
                    j++;
                    i2 = -1;
                    i4 = Integer.MAX_VALUE;
                }
                if (p[n] <= i) {
                    return p[n];
                }
                return -1;
            }
        } else {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
    }

    @Deprecated
    public static double getJaroWinklerDistance(CharSequence first, CharSequence second) {
        double jw;
        if (first == null || second == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int[] mtp = matches(first, second);
        double m = (double) mtp[0];
        if (m == Utils.DOUBLE_EPSILON) {
            return Utils.DOUBLE_EPSILON;
        }
        double length = (double) first.length();
        Double.isNaN(m);
        Double.isNaN(length);
        double length2 = (double) second.length();
        Double.isNaN(m);
        Double.isNaN(length2);
        double d = (m / length) + (m / length2);
        double d2 = (double) mtp[1];
        Double.isNaN(m);
        Double.isNaN(d2);
        Double.isNaN(m);
        double j = (d + ((m - d2) / m)) / 3.0d;
        if (j < 0.7d) {
            jw = j;
        } else {
            double d3 = (double) mtp[3];
            Double.isNaN(d3);
            double min = Math.min(0.1d, 1.0d / d3);
            double d4 = (double) mtp[2];
            Double.isNaN(d4);
            jw = (min * d4 * (1.0d - j)) + j;
        }
        double round = (double) Math.round(jw * 100.0d);
        Double.isNaN(round);
        return round / 100.0d;
    }

    private static int[] matches(CharSequence first, CharSequence second) {
        CharSequence min;
        CharSequence max;
        if (first.length() > second.length()) {
            max = first;
            min = second;
        } else {
            max = second;
            min = first;
        }
        int range = Math.max((max.length() / 2) - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            char c1 = min.charAt(mi);
            int xi = Math.max(mi - range, 0);
            int xn = Math.min(mi + range + 1, max.length());
            while (true) {
                if (xi < xn) {
                    if (!matchFlags[xi] && c1 == max.charAt(xi)) {
                        matchIndexes[mi] = xi;
                        matchFlags[xi] = true;
                        matches++;
                        break;
                    }
                    xi++;
                } else {
                    break;
                }
            }
        }
        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        int si = 0;
        for (int i = 0; i < min.length(); i++) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min.charAt(i);
                si++;
            }
        }
        int si2 = 0;
        for (int i2 = 0; i2 < max.length(); i2++) {
            if (matchFlags[i2]) {
                ms2[si2] = max.charAt(i2);
                si2++;
            }
        }
        int transpositions = 0;
        for (int mi2 = 0; mi2 < ms1.length; mi2++) {
            if (ms1[mi2] != ms2[mi2]) {
                transpositions++;
            }
        }
        int prefix = 0;
        int mi3 = 0;
        while (true) {
            if (mi3 < min.length()) {
                if (first.charAt(mi3) != second.charAt(mi3)) {
                    break;
                }
                prefix++;
                mi3++;
            } else {
                CharSequence charSequence = first;
                CharSequence charSequence2 = second;
                break;
            }
        }
        return new int[]{matches, transpositions / 2, prefix, max.length()};
    }

    @Deprecated
    public static int getFuzzyDistance(CharSequence term, CharSequence query, Locale locale) {
        if (term == null || query == null) {
            throw new IllegalArgumentException("Strings must not be null");
        } else if (locale != null) {
            String termLowerCase = term.toString().toLowerCase(locale);
            String queryLowerCase = query.toString().toLowerCase(locale);
            int score = 0;
            int termIndex = 0;
            int previousMatchingCharacterIndex = Integer.MIN_VALUE;
            for (int queryIndex = 0; queryIndex < queryLowerCase.length(); queryIndex++) {
                char queryChar = queryLowerCase.charAt(queryIndex);
                boolean termCharacterMatchFound = false;
                while (termIndex < termLowerCase.length() && !termCharacterMatchFound) {
                    if (queryChar == termLowerCase.charAt(termIndex)) {
                        score++;
                        if (previousMatchingCharacterIndex + 1 == termIndex) {
                            score += 2;
                        }
                        previousMatchingCharacterIndex = termIndex;
                        termCharacterMatchFound = true;
                    }
                    termIndex++;
                }
            }
            return score;
        } else {
            throw new IllegalArgumentException("Locale must not be null");
        }
    }

    public static boolean startsWith(CharSequence str, CharSequence prefix) {
        return startsWith(str, prefix, false);
    }

    public static boolean startsWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startsWith(str, prefix, true);
    }

    private static boolean startsWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            if (str == null && prefix == null) {
                return true;
            }
            return false;
        } else if (prefix.length() > str.length()) {
            return false;
        } else {
            return CharSequenceUtils.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
        }
    }

    public static boolean startsWithAny(CharSequence sequence, CharSequence... searchStrings) {
        if (isEmpty(sequence) || ArrayUtils.isEmpty((Object[]) searchStrings)) {
            return false;
        }
        for (CharSequence searchString : searchStrings) {
            if (startsWith(sequence, searchString)) {
                return true;
            }
        }
        return false;
    }

    public static boolean endsWith(CharSequence str, CharSequence suffix) {
        return endsWith(str, suffix, false);
    }

    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            if (str == null && suffix == null) {
                return true;
            }
            return false;
        } else if (suffix.length() > str.length()) {
            return false;
        } else {
            return CharSequenceUtils.regionMatches(str, ignoreCase, str.length() - suffix.length(), suffix, 0, suffix.length());
        }
    }

    public static String normalizeSpace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int size = str.length();
        char[] newChars = new char[size];
        int count = 0;
        int whitespacesCount = 0;
        boolean startWhitespaces = true;
        for (int i = 0; i < size; i++) {
            char actualChar = str.charAt(i);
            if (!Character.isWhitespace(actualChar)) {
                startWhitespaces = false;
                int count2 = count + 1;
                newChars[count] = actualChar == 160 ? ' ' : actualChar;
                whitespacesCount = 0;
                count = count2;
            } else {
                if (whitespacesCount == 0 && !startWhitespaces) {
                    newChars[count] = SPACE.charAt(0);
                    count++;
                }
                whitespacesCount++;
            }
        }
        if (startWhitespaces) {
            return "";
        }
        return new String(newChars, 0, count - (whitespacesCount > 0 ? 1 : 0)).trim();
    }

    public static boolean endsWithAny(CharSequence sequence, CharSequence... searchStrings) {
        if (isEmpty(sequence) || ArrayUtils.isEmpty((Object[]) searchStrings)) {
            return false;
        }
        for (CharSequence searchString : searchStrings) {
            if (endsWith(sequence, searchString)) {
                return true;
            }
        }
        return false;
    }

    private static String appendIfMissing(String str, CharSequence suffix, boolean ignoreCase, CharSequence... suffixes) {
        if (str == null || isEmpty(suffix) || endsWith(str, suffix, ignoreCase)) {
            return str;
        }
        if (suffixes != null && suffixes.length > 0) {
            for (CharSequence s : suffixes) {
                if (endsWith(str, s, ignoreCase)) {
                    return str;
                }
            }
        }
        return str + suffix.toString();
    }

    public static String appendIfMissing(String str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    public static String appendIfMissingIgnoreCase(String str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, true, suffixes);
    }

    private static String prependIfMissing(String str, CharSequence prefix, boolean ignoreCase, CharSequence... prefixes) {
        if (str == null || isEmpty(prefix) || startsWith(str, prefix, ignoreCase)) {
            return str;
        }
        if (prefixes != null && prefixes.length > 0) {
            for (CharSequence p : prefixes) {
                if (startsWith(str, p, ignoreCase)) {
                    return str;
                }
            }
        }
        return prefix.toString() + str;
    }

    public static String prependIfMissing(String str, CharSequence prefix, CharSequence... prefixes) {
        return prependIfMissing(str, prefix, false, prefixes);
    }

    public static String prependIfMissingIgnoreCase(String str, CharSequence prefix, CharSequence... prefixes) {
        return prependIfMissing(str, prefix, true, prefixes);
    }

    @Deprecated
    public static String toString(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
        String str;
        if (charsetName == null) {
            str = new String(bytes, Charset.defaultCharset());
        }
        return str;
    }

    public static String toEncodedString(byte[] bytes, Charset charset) {
        return new String(bytes, charset != null ? charset : Charset.defaultCharset());
    }

    public static String wrap(String str, char wrapWith) {
        if (isEmpty(str) || wrapWith == 0) {
            return str;
        }
        return wrapWith + str + wrapWith;
    }

    public static String wrap(String str, String wrapWith) {
        if (isEmpty(str) || isEmpty(wrapWith)) {
            return str;
        }
        return wrapWith.concat(str).concat(wrapWith);
    }

    public static String wrapIfMissing(String str, char wrapWith) {
        if (isEmpty(str) || wrapWith == 0) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str.length() + 2);
        if (str.charAt(0) != wrapWith) {
            builder.append(wrapWith);
        }
        builder.append(str);
        if (str.charAt(str.length() - 1) != wrapWith) {
            builder.append(wrapWith);
        }
        return builder.toString();
    }

    public static String wrapIfMissing(String str, String wrapWith) {
        if (isEmpty(str) || isEmpty(wrapWith)) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str.length() + wrapWith.length() + wrapWith.length());
        if (!str.startsWith(wrapWith)) {
            builder.append(wrapWith);
        }
        builder.append(str);
        if (!str.endsWith(wrapWith)) {
            builder.append(wrapWith);
        }
        return builder.toString();
    }

    public static String unwrap(String str, String wrapToken) {
        if (!isEmpty(str) && !isEmpty(wrapToken) && startsWith(str, wrapToken) && endsWith(str, wrapToken)) {
            int startIndex = str.indexOf(wrapToken);
            int endIndex = str.lastIndexOf(wrapToken);
            int wrapLength = wrapToken.length();
            if (!(startIndex == -1 || endIndex == -1)) {
                return str.substring(startIndex + wrapLength, endIndex);
            }
        }
        return str;
    }

    public static String unwrap(String str, char wrapChar) {
        int endIndex;
        if (isEmpty(str) || wrapChar == 0 || str.charAt(0) != wrapChar || str.charAt(str.length() - 1) != wrapChar || (endIndex = str.length() - 1) == -1) {
            return str;
        }
        return str.substring(1, endIndex);
    }

    public static int[] toCodePoints(CharSequence str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        String s = str.toString();
        int[] result = new int[s.codePointCount(0, s.length())];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = s.codePointAt(index);
            index += Character.charCount(result[i]);
        }
        return result;
    }
}
