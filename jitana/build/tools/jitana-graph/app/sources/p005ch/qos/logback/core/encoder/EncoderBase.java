package p005ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.OutputStream;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.encoder.EncoderBase */
public abstract class EncoderBase<E> extends ContextAwareBase implements Encoder<E> {
    protected OutputStream outputStream;
    protected boolean started;

    public void init(OutputStream outputStream2) throws IOException {
        this.outputStream = outputStream2;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
    }
}
