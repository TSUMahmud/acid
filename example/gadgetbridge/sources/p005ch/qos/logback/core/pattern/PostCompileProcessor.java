package p005ch.qos.logback.core.pattern;

/* renamed from: ch.qos.logback.core.pattern.PostCompileProcessor */
public interface PostCompileProcessor<E> {
    void process(Converter<E> converter);
}
