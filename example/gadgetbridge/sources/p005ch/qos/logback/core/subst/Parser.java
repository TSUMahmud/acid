package p005ch.qos.logback.core.subst;

import java.util.List;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.spi.ScanException;
import p005ch.qos.logback.core.subst.Node;
import p005ch.qos.logback.core.subst.Token;

/* renamed from: ch.qos.logback.core.subst.Parser */
public class Parser {
    int pointer = 0;
    final List<Token> tokenList;

    /* renamed from: ch.qos.logback.core.subst.Parser$1 */
    static /* synthetic */ class C05341 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$subst$Token$Type = new int[Token.Type.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$subst$Token$Type[Token.Type.LITERAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$subst$Token$Type[Token.Type.CURLY_LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$subst$Token$Type[Token.Type.START.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public Parser(List<Token> list) {
        this.tokenList = list;
    }

    /* renamed from: C */
    private Node m14C() throws ScanException {
        Node E = m15E();
        if (isDefaultToken(peekAtCurentToken())) {
            advanceTokenPointer();
            E.append(makeNewLiteralNode(CoreConstants.DEFAULT_VALUE_SEPARATOR));
            E.append(m15E());
        }
        return E;
    }

    /* renamed from: E */
    private Node m15E() throws ScanException {
        Node T = m16T();
        if (T == null) {
            return null;
        }
        Node Eopt = Eopt();
        if (Eopt != null) {
            T.append(Eopt);
        }
        return T;
    }

    private Node Eopt() throws ScanException {
        if (peekAtCurentToken() == null) {
            return null;
        }
        return m15E();
    }

    /* renamed from: T */
    private Node m16T() throws ScanException {
        Token peekAtCurentToken = peekAtCurentToken();
        if (peekAtCurentToken == null) {
            return null;
        }
        int i = C05341.$SwitchMap$ch$qos$logback$core$subst$Token$Type[peekAtCurentToken.type.ordinal()];
        if (i == 1) {
            advanceTokenPointer();
            return makeNewLiteralNode(peekAtCurentToken.payload);
        } else if (i == 2) {
            advanceTokenPointer();
            Node C = m14C();
            expectCurlyRight(peekAtCurentToken());
            advanceTokenPointer();
            Node makeNewLiteralNode = makeNewLiteralNode(CoreConstants.LEFT_ACCOLADE);
            makeNewLiteralNode.append(C);
            makeNewLiteralNode.append(makeNewLiteralNode(CoreConstants.RIGHT_ACCOLADE));
            return makeNewLiteralNode;
        } else if (i != 3) {
            return null;
        } else {
            advanceTokenPointer();
            Node V = m17V();
            expectCurlyRight(peekAtCurentToken());
            advanceTokenPointer();
            return V;
        }
    }

    /* renamed from: V */
    private Node m17V() throws ScanException {
        Node node = new Node(Node.Type.VARIABLE, m15E());
        if (isDefaultToken(peekAtCurentToken())) {
            advanceTokenPointer();
            node.defaultPart = m15E();
        }
        return node;
    }

    private boolean isDefaultToken(Token token) {
        return token != null && token.type == Token.Type.DEFAULT;
    }

    private Node makeNewLiteralNode(String str) {
        return new Node(Node.Type.LITERAL, str);
    }

    /* access modifiers changed from: package-private */
    public void advanceTokenPointer() {
        this.pointer++;
    }

    /* access modifiers changed from: package-private */
    public void expectCurlyRight(Token token) throws ScanException {
        expectNotNull(token, "}");
        if (token.type != Token.Type.CURLY_RIGHT) {
            throw new ScanException("Expecting }");
        }
    }

    /* access modifiers changed from: package-private */
    public void expectNotNull(Token token, String str) {
        if (token == null) {
            throw new IllegalArgumentException("All tokens consumed but was expecting \"" + str + "\"");
        }
    }

    public Node parse() throws ScanException {
        return m15E();
    }

    /* access modifiers changed from: package-private */
    public Token peekAtCurentToken() {
        if (this.pointer < this.tokenList.size()) {
            return this.tokenList.get(this.pointer);
        }
        return null;
    }
}
