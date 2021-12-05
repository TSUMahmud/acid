package p005ch.qos.logback.core.pattern.parser;

import androidx.core.view.PointerIconCompat;
import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.core.pattern.util.IEscapeUtil;
import p005ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import p005ch.qos.logback.core.pattern.util.RestrictedEscapeUtil;
import p005ch.qos.logback.core.spi.ScanException;

/* renamed from: ch.qos.logback.core.pattern.parser.TokenStream */
class TokenStream {
    final IEscapeUtil escapeUtil;
    final IEscapeUtil optionEscapeUtil;
    final String pattern;
    final int patternLength;
    int pointer;
    TokenizerState state;

    /* renamed from: ch.qos.logback.core.pattern.parser.TokenStream$1 */
    static /* synthetic */ class C05221 {

        /* renamed from: $SwitchMap$ch$qos$logback$core$pattern$parser$TokenStream$TokenizerState */
        static final /* synthetic */ int[] f54x920259b8 = new int[TokenizerState.values().length];

        static {
            try {
                f54x920259b8[TokenizerState.LITERAL_STATE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f54x920259b8[TokenizerState.FORMAT_MODIFIER_STATE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f54x920259b8[TokenizerState.OPTION_STATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f54x920259b8[TokenizerState.KEYWORD_STATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f54x920259b8[TokenizerState.RIGHT_PARENTHESIS_STATE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    /* renamed from: ch.qos.logback.core.pattern.parser.TokenStream$TokenizerState */
    enum TokenizerState {
        LITERAL_STATE,
        FORMAT_MODIFIER_STATE,
        KEYWORD_STATE,
        OPTION_STATE,
        RIGHT_PARENTHESIS_STATE
    }

    TokenStream(String str) {
        this(str, new RegularEscapeUtil());
    }

    TokenStream(String str, IEscapeUtil iEscapeUtil) {
        this.optionEscapeUtil = new RestrictedEscapeUtil();
        this.state = TokenizerState.LITERAL_STATE;
        this.pointer = 0;
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("null or empty pattern string not allowed");
        }
        this.pattern = str;
        this.patternLength = str.length();
        this.escapeUtil = iEscapeUtil;
    }

    private void addValuedToken(int i, StringBuffer stringBuffer, List<Token> list) {
        if (stringBuffer.length() > 0) {
            list.add(new Token(i, stringBuffer.toString()));
            stringBuffer.setLength(0);
        }
    }

    private void handleFormatModifierState(char c, List<Token> list, StringBuffer stringBuffer) {
        if (c == '(') {
            addValuedToken(PointerIconCompat.TYPE_HAND, stringBuffer, list);
            list.add(Token.BARE_COMPOSITE_KEYWORD_TOKEN);
            this.state = TokenizerState.LITERAL_STATE;
            return;
        }
        if (Character.isJavaIdentifierStart(c)) {
            addValuedToken(PointerIconCompat.TYPE_HAND, stringBuffer, list);
            this.state = TokenizerState.KEYWORD_STATE;
        }
        stringBuffer.append(c);
    }

    private void handleKeywordState(char c, List<Token> list, StringBuffer stringBuffer) {
        TokenizerState tokenizerState;
        if (Character.isJavaIdentifierPart(c)) {
            stringBuffer.append(c);
            return;
        }
        if (c == '{') {
            addValuedToken(PointerIconCompat.TYPE_WAIT, stringBuffer, list);
            tokenizerState = TokenizerState.OPTION_STATE;
        } else {
            if (c == '(') {
                addValuedToken(1005, stringBuffer, list);
            } else if (c == '%') {
                addValuedToken(PointerIconCompat.TYPE_WAIT, stringBuffer, list);
                list.add(Token.PERCENT_TOKEN);
                tokenizerState = TokenizerState.FORMAT_MODIFIER_STATE;
            } else {
                addValuedToken(PointerIconCompat.TYPE_WAIT, stringBuffer, list);
                if (c == ')') {
                    tokenizerState = TokenizerState.RIGHT_PARENTHESIS_STATE;
                } else if (c == '\\') {
                    int i = this.pointer;
                    if (i < this.patternLength) {
                        String str = this.pattern;
                        this.pointer = i + 1;
                        this.escapeUtil.escape("%()", stringBuffer, str.charAt(i), this.pointer);
                    }
                } else {
                    stringBuffer.append(c);
                }
            }
            tokenizerState = TokenizerState.LITERAL_STATE;
        }
        this.state = tokenizerState;
    }

    private void handleLiteralState(char c, List<Token> list, StringBuffer stringBuffer) {
        TokenizerState tokenizerState;
        if (c == '%') {
            addValuedToken(1000, stringBuffer, list);
            list.add(Token.PERCENT_TOKEN);
            tokenizerState = TokenizerState.FORMAT_MODIFIER_STATE;
        } else if (c == ')') {
            addValuedToken(1000, stringBuffer, list);
            tokenizerState = TokenizerState.RIGHT_PARENTHESIS_STATE;
        } else if (c != '\\') {
            stringBuffer.append(c);
            return;
        } else {
            escape("%()", stringBuffer);
            return;
        }
        this.state = tokenizerState;
    }

    private void handleRightParenthesisState(char c, List<Token> list, StringBuffer stringBuffer) {
        TokenizerState tokenizerState;
        list.add(Token.RIGHT_PARENTHESIS_TOKEN);
        if (c != ')') {
            if (c == '\\') {
                escape("%{}", stringBuffer);
            } else if (c != '{') {
                stringBuffer.append(c);
            } else {
                tokenizerState = TokenizerState.OPTION_STATE;
                this.state = tokenizerState;
            }
            tokenizerState = TokenizerState.LITERAL_STATE;
            this.state = tokenizerState;
        }
    }

    private void processOption(char c, List<Token> list, StringBuffer stringBuffer) throws ScanException {
        new OptionTokenizer(this).tokenize(c, list);
    }

    /* access modifiers changed from: package-private */
    public void escape(String str, StringBuffer stringBuffer) {
        int i = this.pointer;
        if (i < this.patternLength) {
            String str2 = this.pattern;
            this.pointer = i + 1;
            this.escapeUtil.escape(str, stringBuffer, str2.charAt(i), this.pointer);
        }
    }

    /* access modifiers changed from: package-private */
    public void optionEscape(String str, StringBuffer stringBuffer) {
        int i = this.pointer;
        if (i < this.patternLength) {
            String str2 = this.pattern;
            this.pointer = i + 1;
            this.optionEscapeUtil.escape(str, stringBuffer, str2.charAt(i), this.pointer);
        }
    }

    /* access modifiers changed from: package-private */
    public List tokenize() throws ScanException {
        ArrayList arrayList = new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            int i = this.pointer;
            if (i >= this.patternLength) {
                break;
            }
            char charAt = this.pattern.charAt(i);
            this.pointer++;
            int i2 = C05221.f54x920259b8[this.state.ordinal()];
            if (i2 == 1) {
                handleLiteralState(charAt, arrayList, stringBuffer);
            } else if (i2 == 2) {
                handleFormatModifierState(charAt, arrayList, stringBuffer);
            } else if (i2 == 3) {
                processOption(charAt, arrayList, stringBuffer);
            } else if (i2 == 4) {
                handleKeywordState(charAt, arrayList, stringBuffer);
            } else if (i2 == 5) {
                handleRightParenthesisState(charAt, arrayList, stringBuffer);
            }
        }
        int i3 = C05221.f54x920259b8[this.state.ordinal()];
        if (i3 == 1) {
            addValuedToken(1000, stringBuffer, arrayList);
        } else if (i3 == 2 || i3 == 3) {
            throw new ScanException("Unexpected end of pattern string");
        } else if (i3 == 4) {
            arrayList.add(new Token(PointerIconCompat.TYPE_WAIT, stringBuffer.toString()));
        } else if (i3 == 5) {
            arrayList.add(Token.RIGHT_PARENTHESIS_TOKEN);
        }
        return arrayList;
    }
}
