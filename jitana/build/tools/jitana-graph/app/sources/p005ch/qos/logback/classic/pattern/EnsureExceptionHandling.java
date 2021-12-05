package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.pattern.Converter;
import p005ch.qos.logback.core.pattern.ConverterUtil;
import p005ch.qos.logback.core.pattern.PostCompileProcessor;

/* renamed from: ch.qos.logback.classic.pattern.EnsureExceptionHandling */
public class EnsureExceptionHandling implements PostCompileProcessor<ILoggingEvent> {
    public boolean chainHandlesThrowable(Converter converter) {
        while (converter != null) {
            if (converter instanceof ThrowableHandlingConverter) {
                return true;
            }
            converter = converter.getNext();
        }
        return false;
    }

    public void process(Converter<ILoggingEvent> converter) {
        if (converter == null) {
            throw new IllegalArgumentException("cannot process empty chain");
        } else if (!chainHandlesThrowable(converter)) {
            ConverterUtil.findTail(converter).setNext(new ExtendedThrowableProxyConverter());
        }
    }
}
