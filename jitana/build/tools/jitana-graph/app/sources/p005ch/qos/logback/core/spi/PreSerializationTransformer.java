package p005ch.qos.logback.core.spi;

import java.io.Serializable;

/* renamed from: ch.qos.logback.core.spi.PreSerializationTransformer */
public interface PreSerializationTransformer<E> {
    Serializable transform(E e);
}
