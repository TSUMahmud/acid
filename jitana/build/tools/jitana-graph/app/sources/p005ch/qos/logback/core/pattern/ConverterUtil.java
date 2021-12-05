package p005ch.qos.logback.core.pattern;

import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.spi.ContextAware;

/* renamed from: ch.qos.logback.core.pattern.ConverterUtil */
public class ConverterUtil {
    public static <E> Converter<E> findTail(Converter<E> converter) {
        while (converter != null) {
            Converter<E> next = converter.getNext();
            if (next == null) {
                break;
            }
            converter = next;
        }
        return converter;
    }

    public static <E> void setContextForConverters(Context context, Converter<E> converter) {
        while (converter != null) {
            if (converter instanceof ContextAware) {
                ((ContextAware) converter).setContext(context);
            }
            converter = converter.getNext();
        }
    }

    public static <E> void startConverters(Converter<E> converter) {
        while (converter != null) {
            if (converter instanceof CompositeConverter) {
                CompositeConverter compositeConverter = (CompositeConverter) converter;
                startConverters(compositeConverter.childConverter);
                compositeConverter.start();
            } else if (converter instanceof DynamicConverter) {
                ((DynamicConverter) converter).start();
            }
            converter = converter.getNext();
        }
    }
}
