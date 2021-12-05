package p005ch.qos.logback.core.pattern;

/* renamed from: ch.qos.logback.core.pattern.Converter */
public abstract class Converter<E> {
    Converter<E> next;

    public abstract String convert(E e);

    public final Converter<E> getNext() {
        return this.next;
    }

    public final void setNext(Converter<E> converter) {
        if (this.next == null) {
            this.next = converter;
            return;
        }
        throw new IllegalStateException("Next converter has been already set");
    }

    public void write(StringBuilder sb, E e) {
        sb.append(convert(e));
    }
}
