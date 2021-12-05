package p005ch.qos.logback.core.pattern.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import p005ch.qos.logback.core.pattern.Converter;
import p005ch.qos.logback.core.pattern.FormatInfo;
import p005ch.qos.logback.core.pattern.IdentityCompositeConverter;
import p005ch.qos.logback.core.pattern.ReplacingCompositeConverter;
import p005ch.qos.logback.core.pattern.util.IEscapeUtil;
import p005ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.ScanException;

/* renamed from: ch.qos.logback.core.pattern.parser.Parser */
public class Parser<E> extends ContextAwareBase {
    public static final Map<String, String> DEFAULT_COMPOSITE_CONVERTER_MAP = new HashMap();
    public static final String MISSING_RIGHT_PARENTHESIS = "http://logback.qos.ch/codes.html#missingRightParenthesis";
    public static final String REPLACE_CONVERTER_WORD = "replace";
    int pointer;
    final List tokenList;

    static {
        DEFAULT_COMPOSITE_CONVERTER_MAP.put(Token.BARE_COMPOSITE_KEYWORD_TOKEN.getValue().toString(), IdentityCompositeConverter.class.getName());
        DEFAULT_COMPOSITE_CONVERTER_MAP.put(REPLACE_CONVERTER_WORD, ReplacingCompositeConverter.class.getName());
    }

    Parser(TokenStream tokenStream) throws ScanException {
        this.pointer = 0;
        this.tokenList = tokenStream.tokenize();
    }

    public Parser(String str) throws ScanException {
        this(str, new RegularEscapeUtil());
    }

    public Parser(String str, IEscapeUtil iEscapeUtil) throws ScanException {
        this.pointer = 0;
        try {
            this.tokenList = new TokenStream(str, iEscapeUtil).tokenize();
        } catch (IllegalArgumentException e) {
            throw new ScanException("Failed to initialize Parser", e);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: C */
    public FormattingNode mo10262C() throws ScanException {
        Token curentToken = getCurentToken();
        expectNotNull(curentToken, "a LEFT_PARENTHESIS or KEYWORD");
        int type = curentToken.getType();
        if (type == 1004) {
            return SINGLE();
        }
        if (type == 1005) {
            advanceTokenPointer();
            return COMPOSITE(curentToken.getValue().toString());
        }
        throw new IllegalStateException("Unexpected token " + curentToken);
    }

    /* access modifiers changed from: package-private */
    public FormattingNode COMPOSITE(String str) throws ScanException {
        CompositeNode compositeNode = new CompositeNode(str);
        compositeNode.setChildNode(mo10264E());
        Token nextToken = getNextToken();
        if (nextToken == null || nextToken.getType() != 41) {
            String str2 = "Expecting RIGHT_PARENTHESIS token but got " + nextToken;
            addError(str2);
            addError("See also http://logback.qos.ch/codes.html#missingRightParenthesis");
            throw new ScanException(str2);
        }
        Token curentToken = getCurentToken();
        if (curentToken != null && curentToken.getType() == 1006) {
            compositeNode.setOptions((List) curentToken.getValue());
            advanceTokenPointer();
        }
        return compositeNode;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: E */
    public Node mo10264E() throws ScanException {
        Node T = mo10267T();
        if (T == null) {
            return null;
        }
        Node Eopt = Eopt();
        if (Eopt != null) {
            T.setNext(Eopt);
        }
        return T;
    }

    /* access modifiers changed from: package-private */
    public Node Eopt() throws ScanException {
        if (getCurentToken() == null) {
            return null;
        }
        return mo10264E();
    }

    /* access modifiers changed from: package-private */
    public FormattingNode SINGLE() throws ScanException {
        SimpleKeywordNode simpleKeywordNode = new SimpleKeywordNode(getNextToken().getValue());
        Token curentToken = getCurentToken();
        if (curentToken != null && curentToken.getType() == 1006) {
            simpleKeywordNode.setOptions((List) curentToken.getValue());
            advanceTokenPointer();
        }
        return simpleKeywordNode;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: T */
    public Node mo10267T() throws ScanException {
        Token curentToken = getCurentToken();
        expectNotNull(curentToken, "a LITERAL or '%'");
        int type = curentToken.getType();
        if (type == 37) {
            advanceTokenPointer();
            Token curentToken2 = getCurentToken();
            expectNotNull(curentToken2, "a FORMAT_MODIFIER, SIMPLE_KEYWORD or COMPOUND_KEYWORD");
            if (curentToken2.getType() != 1002) {
                return mo10262C();
            }
            FormatInfo valueOf = FormatInfo.valueOf((String) curentToken2.getValue());
            advanceTokenPointer();
            FormattingNode C = mo10262C();
            C.setFormatInfo(valueOf);
            return C;
        } else if (type != 1000) {
            return null;
        } else {
            advanceTokenPointer();
            return new Node(0, curentToken.getValue());
        }
    }

    /* access modifiers changed from: package-private */
    public void advanceTokenPointer() {
        this.pointer++;
    }

    public Converter<E> compile(Node node, Map map) {
        Compiler compiler = new Compiler(node, map);
        compiler.setContext(this.context);
        return compiler.compile();
    }

    /* access modifiers changed from: package-private */
    public void expectNotNull(Token token, String str) {
        if (token == null) {
            throw new IllegalStateException("All tokens consumed but was expecting " + str);
        }
    }

    /* access modifiers changed from: package-private */
    public Token getCurentToken() {
        if (this.pointer < this.tokenList.size()) {
            return (Token) this.tokenList.get(this.pointer);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public Token getNextToken() {
        if (this.pointer >= this.tokenList.size()) {
            return null;
        }
        List list = this.tokenList;
        int i = this.pointer;
        this.pointer = i + 1;
        return (Token) list.get(i);
    }

    public Node parse() throws ScanException {
        return mo10264E();
    }
}
