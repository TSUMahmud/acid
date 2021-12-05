package p005ch.qos.logback.core.pattern.parser;

/* renamed from: ch.qos.logback.core.pattern.parser.Token */
class Token {
    static Token BARE_COMPOSITE_KEYWORD_TOKEN = new Token(COMPOSITE_KEYWORD, "BARE");
    static final int COMPOSITE_KEYWORD = 1005;
    static final int CURLY_LEFT = 123;
    static final int CURLY_RIGHT = 125;
    static final int DOT = 46;
    static final int EOF = Integer.MAX_VALUE;
    static Token EOF_TOKEN = new Token(Integer.MAX_VALUE, "EOF");
    static final int FORMAT_MODIFIER = 1002;
    static final int LITERAL = 1000;
    static final int MINUS = 45;
    static final int OPTION = 1006;
    static final int PERCENT = 37;
    static Token PERCENT_TOKEN = new Token(37);
    static final int RIGHT_PARENTHESIS = 41;
    static Token RIGHT_PARENTHESIS_TOKEN = new Token(41);
    static final int SIMPLE_KEYWORD = 1004;
    private final int type;
    private final Object value;

    public Token(int i) {
        this(i, (Object) null);
    }

    public Token(int i, Object obj) {
        this.type = i;
        this.value = obj;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Token)) {
            return false;
        }
        Token token = (Token) obj;
        if (this.type != token.type) {
            return false;
        }
        Object obj2 = this.value;
        Object obj3 = token.value;
        return obj2 == null ? obj3 == null : obj2.equals(obj3);
    }

    public int getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public int hashCode() {
        int i = this.type * 29;
        Object obj = this.value;
        return i + (obj != null ? obj.hashCode() : 0);
    }

    public String toString() {
        String str;
        StringBuilder sb;
        String str2;
        int i = this.type;
        if (i == 37) {
            str = "%";
        } else if (i == 41) {
            str = "RIGHT_PARENTHESIS";
        } else if (i == 1000) {
            str = "LITERAL";
        } else if (i != 1002) {
            switch (i) {
                case 1004:
                    str = "SIMPLE_KEYWORD";
                    break;
                case COMPOSITE_KEYWORD /*1005*/:
                    str = "COMPOSITE_KEYWORD";
                    break;
                case 1006:
                    str = "OPTION";
                    break;
                default:
                    str = "UNKNOWN";
                    break;
            }
        } else {
            str = "FormatModifier";
        }
        if (this.value == null) {
            sb = new StringBuilder();
            sb.append("Token(");
            sb.append(str);
            str2 = ")";
        } else {
            sb = new StringBuilder();
            sb.append("Token(");
            sb.append(str);
            sb.append(", \"");
            sb.append(this.value);
            str2 = "\")";
        }
        sb.append(str2);
        return sb.toString();
    }
}
