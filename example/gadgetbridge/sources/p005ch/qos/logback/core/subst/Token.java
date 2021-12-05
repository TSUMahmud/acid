package p005ch.qos.logback.core.subst;

import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.core.subst.Token */
public class Token {
    public static final Token CURLY_LEFT_TOKEN = new Token(Type.CURLY_LEFT, (String) null);
    public static final Token CURLY_RIGHT_TOKEN = new Token(Type.CURLY_RIGHT, (String) null);
    public static final Token DEFAULT_SEP_TOKEN = new Token(Type.DEFAULT, (String) null);
    public static final Token START_TOKEN = new Token(Type.START, (String) null);
    String payload;
    Type type;

    /* renamed from: ch.qos.logback.core.subst.Token$Type */
    public enum Type {
        LITERAL,
        START,
        CURLY_LEFT,
        CURLY_RIGHT,
        DEFAULT
    }

    public Token(Type type2, String str) {
        this.type = type2;
        this.payload = str;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Token token = (Token) obj;
        if (this.type != token.type) {
            return false;
        }
        String str = this.payload;
        String str2 = token.payload;
        return str == null ? str2 == null : str.equals(str2);
    }

    public int hashCode() {
        Type type2 = this.type;
        int i = 0;
        int hashCode = (type2 != null ? type2.hashCode() : 0) * 31;
        String str = this.payload;
        if (str != null) {
            i = str.hashCode();
        }
        return hashCode + i;
    }

    public String toString() {
        String str = "Token{type=" + this.type;
        if (this.payload != null) {
            str = str + ", payload='" + this.payload + CoreConstants.SINGLE_QUOTE_CHAR;
        }
        return str + CoreConstants.CURLY_RIGHT;
    }
}
