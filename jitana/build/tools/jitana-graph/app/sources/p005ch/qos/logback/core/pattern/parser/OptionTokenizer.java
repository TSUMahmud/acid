package p005ch.qos.logback.core.pattern.parser;

import androidx.core.view.PointerIconCompat;
import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.core.pattern.parser.TokenStream;
import p005ch.qos.logback.core.pattern.util.AsIsEscapeUtil;
import p005ch.qos.logback.core.pattern.util.IEscapeUtil;
import p005ch.qos.logback.core.spi.ScanException;

/* renamed from: ch.qos.logback.core.pattern.parser.OptionTokenizer */
public class OptionTokenizer {
    private static final int EXPECTING_STATE = 0;
    private static final int QUOTED_COLLECTING_STATE = 2;
    private static final int RAW_COLLECTING_STATE = 1;
    final IEscapeUtil escapeUtil;
    final String pattern;
    final int patternLength;
    char quoteChar;
    int state;
    final TokenStream tokenStream;

    OptionTokenizer(TokenStream tokenStream2) {
        this(tokenStream2, new AsIsEscapeUtil());
    }

    OptionTokenizer(TokenStream tokenStream2, IEscapeUtil iEscapeUtil) {
        this.state = 0;
        this.tokenStream = tokenStream2;
        this.pattern = tokenStream2.pattern;
        this.patternLength = tokenStream2.patternLength;
        this.escapeUtil = iEscapeUtil;
    }

    /* access modifiers changed from: package-private */
    public void emitOptionToken(List<Token> list, List<String> list2) {
        list.add(new Token(PointerIconCompat.TYPE_CELL, list2));
        this.tokenStream.state = TokenStream.TokenizerState.LITERAL_STATE;
    }

    /* access modifiers changed from: package-private */
    public void escape(String str, StringBuffer stringBuffer) {
        if (this.tokenStream.pointer < this.patternLength) {
            String str2 = this.pattern;
            TokenStream tokenStream2 = this.tokenStream;
            int i = tokenStream2.pointer;
            tokenStream2.pointer = i + 1;
            this.escapeUtil.escape(str, stringBuffer, str2.charAt(i), this.tokenStream.pointer);
        }
    }

    /* access modifiers changed from: package-private */
    public void tokenize(char c, List<Token> list) throws ScanException {
        String str;
        StringBuffer stringBuffer = new StringBuffer();
        ArrayList arrayList = new ArrayList();
        while (this.tokenStream.pointer < this.patternLength) {
            int i = this.state;
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                        char c2 = this.quoteChar;
                        if (c == c2) {
                            str = stringBuffer.toString();
                            arrayList.add(str);
                            stringBuffer.setLength(0);
                            this.state = 0;
                        } else if (c == '\\') {
                            escape(String.valueOf(c2), stringBuffer);
                        }
                    }
                } else if (c == ',') {
                    str = stringBuffer.toString().trim();
                    arrayList.add(str);
                    stringBuffer.setLength(0);
                    this.state = 0;
                } else if (c == '}') {
                    arrayList.add(stringBuffer.toString().trim());
                    emitOptionToken(list, arrayList);
                    return;
                }
                stringBuffer.append(c);
            } else if (!(c == 9 || c == 10 || c == 13 || c == ' ')) {
                if (c == '\"' || c == '\'') {
                    this.state = 2;
                    this.quoteChar = c;
                } else if (c == ',') {
                    continue;
                } else if (c != '}') {
                    stringBuffer.append(c);
                    this.state = 1;
                } else {
                    emitOptionToken(list, arrayList);
                    return;
                }
            }
            c = this.pattern.charAt(this.tokenStream.pointer);
            this.tokenStream.pointer++;
        }
        if (c == '}') {
            int i2 = this.state;
            if (i2 != 0) {
                if (i2 == 1) {
                    arrayList.add(stringBuffer.toString().trim());
                } else {
                    throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
                }
            }
            emitOptionToken(list, arrayList);
            return;
        }
        throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
    }
}
