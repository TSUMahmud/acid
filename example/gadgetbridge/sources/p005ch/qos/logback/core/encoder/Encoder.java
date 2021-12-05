package p005ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.OutputStream;
import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.core.encoder.Encoder */
public interface Encoder<E> extends ContextAware, LifeCycle {
    void close() throws IOException;

    void doEncode(E e) throws IOException;

    void init(OutputStream outputStream) throws IOException;
}
