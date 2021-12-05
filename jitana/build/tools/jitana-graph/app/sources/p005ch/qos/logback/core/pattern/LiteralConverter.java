package p005ch.qos.logback.core.pattern;

/* renamed from: ch.qos.logback.core.pattern.LiteralConverter */
public final class LiteralConverter<E> extends Converter<E> {
    String literal;

    public LiteralConverter(String str) {
        this.literal = str;
    }

    public String convert(E e) {
        return this.literal;
    }
}
