package p005ch.qos.logback.core.subst;

import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.spi.ScanException;
import p005ch.qos.logback.core.subst.Token;

/* renamed from: ch.qos.logback.core.subst.Tokenizer */
public class Tokenizer {
    final String pattern;
    final int patternLength;
    int pointer = 0;
    TokenizerState state = TokenizerState.LITERAL_STATE;

    /* renamed from: ch.qos.logback.core.subst.Tokenizer$1 */
    static /* synthetic */ class C05351 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState = new int[TokenizerState.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState[TokenizerState.LITERAL_STATE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState[TokenizerState.START_STATE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState[TokenizerState.DEFAULT_VAL_STATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* renamed from: ch.qos.logback.core.subst.Tokenizer$TokenizerState */
    enum TokenizerState {
        LITERAL_STATE,
        START_STATE,
        DEFAULT_VAL_STATE
    }

    public Tokenizer(String str) {
        this.pattern = str;
        this.patternLength = str.length();
    }

    private void addLiteralToken(List<Token> list, StringBuilder sb) {
        if (sb.length() != 0) {
            list.add(new Token(Token.Type.LITERAL, sb.toString()));
        }
    }

    private void handleDefaultValueState(char c, List<Token> list, StringBuilder sb) {
        TokenizerState tokenizerState;
        if (c != '$') {
            if (c != '-') {
                sb.append(CoreConstants.COLON_CHAR);
                sb.append(c);
            } else {
                list.add(Token.DEFAULT_SEP_TOKEN);
            }
            tokenizerState = TokenizerState.LITERAL_STATE;
        } else {
            sb.append(CoreConstants.COLON_CHAR);
            addLiteralToken(list, sb);
            sb.setLength(0);
            tokenizerState = TokenizerState.START_STATE;
        }
        this.state = tokenizerState;
    }

    private void handleLiteralState(char c, List<Token> list, StringBuilder sb) {
        Token token;
        TokenizerState tokenizerState;
        if (c == '$') {
            addLiteralToken(list, sb);
            sb.setLength(0);
            tokenizerState = TokenizerState.START_STATE;
        } else if (c == ':') {
            addLiteralToken(list, sb);
            sb.setLength(0);
            tokenizerState = TokenizerState.DEFAULT_VAL_STATE;
        } else {
            if (c == '{') {
                addLiteralToken(list, sb);
                token = Token.CURLY_LEFT_TOKEN;
            } else if (c == '}') {
                addLiteralToken(list, sb);
                token = Token.CURLY_RIGHT_TOKEN;
            } else {
                sb.append(c);
                return;
            }
            list.add(token);
            sb.setLength(0);
            return;
        }
        this.state = tokenizerState;
    }

    private void handleStartState(char c, List<Token> list, StringBuilder sb) {
        if (c == '{') {
            list.add(Token.START_TOKEN);
        } else {
            sb.append('$');
            sb.append(c);
        }
        this.state = TokenizerState.LITERAL_STATE;
    }

    /* access modifiers changed from: package-private */
    public List<Token> tokenize() throws ScanException {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        while (true) {
            int i = this.pointer;
            if (i >= this.patternLength) {
                break;
            }
            char charAt = this.pattern.charAt(i);
            this.pointer++;
            int i2 = C05351.$SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState[this.state.ordinal()];
            if (i2 == 1) {
                handleLiteralState(charAt, arrayList, sb);
            } else if (i2 == 2) {
                handleStartState(charAt, arrayList, sb);
            } else if (i2 == 3) {
                handleDefaultValueState(charAt, arrayList, sb);
            }
        }
        int i3 = C05351.$SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState[this.state.ordinal()];
        if (i3 == 1) {
            addLiteralToken(arrayList, sb);
        } else if (i3 == 2) {
            throw new ScanException("Unexpected end of pattern string");
        }
        return arrayList;
    }
}
