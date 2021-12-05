package org.apache.commons.lang3.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class StrTokenizer implements ListIterator<String>, Cloneable {
    private static final StrTokenizer CSV_TOKENIZER_PROTOTYPE = new StrTokenizer();
    private static final StrTokenizer TSV_TOKENIZER_PROTOTYPE = new StrTokenizer();
    private char[] chars;
    private StrMatcher delimMatcher;
    private boolean emptyAsNull;
    private boolean ignoreEmptyTokens;
    private StrMatcher ignoredMatcher;
    private StrMatcher quoteMatcher;
    private int tokenPos;
    private String[] tokens;
    private StrMatcher trimmerMatcher;

    static {
        CSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StrMatcher.commaMatcher());
        CSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StrMatcher.doubleQuoteMatcher());
        CSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StrMatcher.noneMatcher());
        CSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StrMatcher.trimMatcher());
        CSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        CSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
        TSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StrMatcher.tabMatcher());
        TSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StrMatcher.doubleQuoteMatcher());
        TSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StrMatcher.noneMatcher());
        TSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StrMatcher.trimMatcher());
        TSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        TSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
    }

    private static StrTokenizer getCSVClone() {
        return (StrTokenizer) CSV_TOKENIZER_PROTOTYPE.clone();
    }

    public static StrTokenizer getCSVInstance() {
        return getCSVClone();
    }

    public static StrTokenizer getCSVInstance(String input) {
        StrTokenizer tok = getCSVClone();
        tok.reset(input);
        return tok;
    }

    public static StrTokenizer getCSVInstance(char[] input) {
        StrTokenizer tok = getCSVClone();
        tok.reset(input);
        return tok;
    }

    private static StrTokenizer getTSVClone() {
        return (StrTokenizer) TSV_TOKENIZER_PROTOTYPE.clone();
    }

    public static StrTokenizer getTSVInstance() {
        return getTSVClone();
    }

    public static StrTokenizer getTSVInstance(String input) {
        StrTokenizer tok = getTSVClone();
        tok.reset(input);
        return tok;
    }

    public static StrTokenizer getTSVInstance(char[] input) {
        StrTokenizer tok = getTSVClone();
        tok.reset(input);
        return tok;
    }

    public StrTokenizer() {
        this.delimMatcher = StrMatcher.splitMatcher();
        this.quoteMatcher = StrMatcher.noneMatcher();
        this.ignoredMatcher = StrMatcher.noneMatcher();
        this.trimmerMatcher = StrMatcher.noneMatcher();
        this.emptyAsNull = false;
        this.ignoreEmptyTokens = true;
        this.chars = null;
    }

    public StrTokenizer(String input) {
        this.delimMatcher = StrMatcher.splitMatcher();
        this.quoteMatcher = StrMatcher.noneMatcher();
        this.ignoredMatcher = StrMatcher.noneMatcher();
        this.trimmerMatcher = StrMatcher.noneMatcher();
        this.emptyAsNull = false;
        this.ignoreEmptyTokens = true;
        if (input != null) {
            this.chars = input.toCharArray();
        } else {
            this.chars = null;
        }
    }

    public StrTokenizer(String input, char delim) {
        this(input);
        setDelimiterChar(delim);
    }

    public StrTokenizer(String input, String delim) {
        this(input);
        setDelimiterString(delim);
    }

    public StrTokenizer(String input, StrMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }

    public StrTokenizer(String input, char delim, char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }

    public StrTokenizer(String input, StrMatcher delim, StrMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }

    public StrTokenizer(char[] input) {
        this.delimMatcher = StrMatcher.splitMatcher();
        this.quoteMatcher = StrMatcher.noneMatcher();
        this.ignoredMatcher = StrMatcher.noneMatcher();
        this.trimmerMatcher = StrMatcher.noneMatcher();
        this.emptyAsNull = false;
        this.ignoreEmptyTokens = true;
        this.chars = ArrayUtils.clone(input);
    }

    public StrTokenizer(char[] input, char delim) {
        this(input);
        setDelimiterChar(delim);
    }

    public StrTokenizer(char[] input, String delim) {
        this(input);
        setDelimiterString(delim);
    }

    public StrTokenizer(char[] input, StrMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }

    public StrTokenizer(char[] input, char delim, char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }

    public StrTokenizer(char[] input, StrMatcher delim, StrMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }

    public int size() {
        checkTokenized();
        return this.tokens.length;
    }

    public String nextToken() {
        if (!hasNext()) {
            return null;
        }
        String[] strArr = this.tokens;
        int i = this.tokenPos;
        this.tokenPos = i + 1;
        return strArr[i];
    }

    public String previousToken() {
        if (!hasPrevious()) {
            return null;
        }
        String[] strArr = this.tokens;
        int i = this.tokenPos - 1;
        this.tokenPos = i;
        return strArr[i];
    }

    public String[] getTokenArray() {
        checkTokenized();
        return (String[]) this.tokens.clone();
    }

    public List<String> getTokenList() {
        checkTokenized();
        List<String> list = new ArrayList<>(this.tokens.length);
        list.addAll(Arrays.asList(this.tokens));
        return list;
    }

    public StrTokenizer reset() {
        this.tokenPos = 0;
        this.tokens = null;
        return this;
    }

    public StrTokenizer reset(String input) {
        reset();
        if (input != null) {
            this.chars = input.toCharArray();
        } else {
            this.chars = null;
        }
        return this;
    }

    public StrTokenizer reset(char[] input) {
        reset();
        this.chars = ArrayUtils.clone(input);
        return this;
    }

    public boolean hasNext() {
        checkTokenized();
        return this.tokenPos < this.tokens.length;
    }

    public String next() {
        if (hasNext()) {
            String[] strArr = this.tokens;
            int i = this.tokenPos;
            this.tokenPos = i + 1;
            return strArr[i];
        }
        throw new NoSuchElementException();
    }

    public int nextIndex() {
        return this.tokenPos;
    }

    public boolean hasPrevious() {
        checkTokenized();
        return this.tokenPos > 0;
    }

    public String previous() {
        if (hasPrevious()) {
            String[] strArr = this.tokens;
            int i = this.tokenPos - 1;
            this.tokenPos = i;
            return strArr[i];
        }
        throw new NoSuchElementException();
    }

    public int previousIndex() {
        return this.tokenPos - 1;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is unsupported");
    }

    public void set(String obj) {
        throw new UnsupportedOperationException("set() is unsupported");
    }

    public void add(String obj) {
        throw new UnsupportedOperationException("add() is unsupported");
    }

    private void checkTokenized() {
        if (this.tokens == null) {
            char[] cArr = this.chars;
            if (cArr == null) {
                List<String> split = tokenize((char[]) null, 0, 0);
                this.tokens = (String[]) split.toArray(new String[split.size()]);
                return;
            }
            List<String> split2 = tokenize(cArr, 0, cArr.length);
            this.tokens = (String[]) split2.toArray(new String[split2.size()]);
        }
    }

    /* access modifiers changed from: protected */
    public List<String> tokenize(char[] srcChars, int offset, int count) {
        if (srcChars == null || count == 0) {
            return Collections.emptyList();
        }
        StrBuilder buf = new StrBuilder();
        List<String> tokenList = new ArrayList<>();
        int pos = offset;
        while (pos >= 0 && pos < count) {
            pos = readNextToken(srcChars, pos, count, buf, tokenList);
            if (pos >= count) {
                addToken(tokenList, "");
            }
        }
        return tokenList;
    }

    private void addToken(List<String> list, String tok) {
        if (StringUtils.isEmpty(tok)) {
            if (!isIgnoreEmptyTokens()) {
                if (isEmptyTokenAsNull()) {
                    tok = null;
                }
            } else {
                return;
            }
        }
        list.add(tok);
    }

    private int readNextToken(char[] srcChars, int start, int len, StrBuilder workArea, List<String> tokenList) {
        while (start < len) {
            int removeLen = Math.max(getIgnoredMatcher().isMatch(srcChars, start, start, len), getTrimmerMatcher().isMatch(srcChars, start, start, len));
            if (removeLen == 0 || getDelimiterMatcher().isMatch(srcChars, start, start, len) > 0 || getQuoteMatcher().isMatch(srcChars, start, start, len) > 0) {
                break;
            }
            start += removeLen;
        }
        if (start >= len) {
            addToken(tokenList, "");
            return -1;
        }
        int delimLen = getDelimiterMatcher().isMatch(srcChars, start, start, len);
        if (delimLen > 0) {
            addToken(tokenList, "");
            return start + delimLen;
        }
        int quoteLen = getQuoteMatcher().isMatch(srcChars, start, start, len);
        if (quoteLen <= 0) {
            return readWithQuotes(srcChars, start, len, workArea, tokenList, 0, 0);
        }
        return readWithQuotes(srcChars, start + quoteLen, len, workArea, tokenList, start, quoteLen);
    }

    private int readWithQuotes(char[] srcChars, int start, int len, StrBuilder workArea, List<String> tokenList, int quoteStart, int quoteLen) {
        char[] cArr = srcChars;
        int i = start;
        int i2 = len;
        StrBuilder strBuilder = workArea;
        List<String> list = tokenList;
        int i3 = quoteLen;
        workArea.clear();
        int pos = start;
        boolean quoting = i3 > 0;
        int trimStart = 0;
        while (pos < i2) {
            if (!quoting) {
                int delimLen = getDelimiterMatcher().isMatch(cArr, pos, i, i2);
                if (delimLen > 0) {
                    addToken(list, strBuilder.substring(0, trimStart));
                    return pos + delimLen;
                } else if (i3 <= 0 || !isQuote(srcChars, pos, len, quoteStart, quoteLen)) {
                    int ignoredLen = getIgnoredMatcher().isMatch(cArr, pos, i, i2);
                    if (ignoredLen > 0) {
                        pos += ignoredLen;
                    } else {
                        int trimmedLen = getTrimmerMatcher().isMatch(cArr, pos, i, i2);
                        if (trimmedLen > 0) {
                            strBuilder.append(cArr, pos, trimmedLen);
                            pos += trimmedLen;
                        } else {
                            strBuilder.append(cArr[pos]);
                            trimStart = workArea.size();
                            pos++;
                        }
                    }
                } else {
                    quoting = true;
                    pos += i3;
                }
            } else if (isQuote(srcChars, pos, len, quoteStart, quoteLen)) {
                if (isQuote(srcChars, pos + i3, len, quoteStart, quoteLen)) {
                    strBuilder.append(cArr, pos, i3);
                    pos += i3 * 2;
                    trimStart = workArea.size();
                } else {
                    quoting = false;
                    pos += i3;
                }
            } else {
                strBuilder.append(cArr[pos]);
                trimStart = workArea.size();
                pos++;
            }
        }
        addToken(list, strBuilder.substring(0, trimStart));
        return -1;
    }

    private boolean isQuote(char[] srcChars, int pos, int len, int quoteStart, int quoteLen) {
        for (int i = 0; i < quoteLen; i++) {
            if (pos + i >= len || srcChars[pos + i] != srcChars[quoteStart + i]) {
                return false;
            }
        }
        return true;
    }

    public StrMatcher getDelimiterMatcher() {
        return this.delimMatcher;
    }

    public StrTokenizer setDelimiterMatcher(StrMatcher delim) {
        if (delim == null) {
            this.delimMatcher = StrMatcher.noneMatcher();
        } else {
            this.delimMatcher = delim;
        }
        return this;
    }

    public StrTokenizer setDelimiterChar(char delim) {
        return setDelimiterMatcher(StrMatcher.charMatcher(delim));
    }

    public StrTokenizer setDelimiterString(String delim) {
        return setDelimiterMatcher(StrMatcher.stringMatcher(delim));
    }

    public StrMatcher getQuoteMatcher() {
        return this.quoteMatcher;
    }

    public StrTokenizer setQuoteMatcher(StrMatcher quote) {
        if (quote != null) {
            this.quoteMatcher = quote;
        }
        return this;
    }

    public StrTokenizer setQuoteChar(char quote) {
        return setQuoteMatcher(StrMatcher.charMatcher(quote));
    }

    public StrMatcher getIgnoredMatcher() {
        return this.ignoredMatcher;
    }

    public StrTokenizer setIgnoredMatcher(StrMatcher ignored) {
        if (ignored != null) {
            this.ignoredMatcher = ignored;
        }
        return this;
    }

    public StrTokenizer setIgnoredChar(char ignored) {
        return setIgnoredMatcher(StrMatcher.charMatcher(ignored));
    }

    public StrMatcher getTrimmerMatcher() {
        return this.trimmerMatcher;
    }

    public StrTokenizer setTrimmerMatcher(StrMatcher trimmer) {
        if (trimmer != null) {
            this.trimmerMatcher = trimmer;
        }
        return this;
    }

    public boolean isEmptyTokenAsNull() {
        return this.emptyAsNull;
    }

    public StrTokenizer setEmptyTokenAsNull(boolean emptyAsNull2) {
        this.emptyAsNull = emptyAsNull2;
        return this;
    }

    public boolean isIgnoreEmptyTokens() {
        return this.ignoreEmptyTokens;
    }

    public StrTokenizer setIgnoreEmptyTokens(boolean ignoreEmptyTokens2) {
        this.ignoreEmptyTokens = ignoreEmptyTokens2;
        return this;
    }

    public String getContent() {
        char[] cArr = this.chars;
        if (cArr == null) {
            return null;
        }
        return new String(cArr);
    }

    public Object clone() {
        try {
            return cloneReset();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public Object cloneReset() throws CloneNotSupportedException {
        StrTokenizer cloned = (StrTokenizer) super.clone();
        char[] cArr = cloned.chars;
        if (cArr != null) {
            cloned.chars = (char[]) cArr.clone();
        }
        cloned.reset();
        return cloned;
    }

    public String toString() {
        if (this.tokens == null) {
            return "StrTokenizer[not tokenized yet]";
        }
        return "StrTokenizer" + getTokenList();
    }
}
