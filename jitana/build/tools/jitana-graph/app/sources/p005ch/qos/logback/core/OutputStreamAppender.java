package p005ch.qos.logback.core;

import java.io.IOException;
import java.io.OutputStream;
import p005ch.qos.logback.core.encoder.Encoder;
import p005ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import p005ch.qos.logback.core.spi.DeferredProcessingAware;
import p005ch.qos.logback.core.spi.LogbackLock;
import p005ch.qos.logback.core.status.ErrorStatus;

/* renamed from: ch.qos.logback.core.OutputStreamAppender */
public class OutputStreamAppender<E> extends UnsynchronizedAppenderBase<E> {
    protected Encoder<E> encoder;
    protected LogbackLock lock = new LogbackLock();
    private OutputStream outputStream;

    /* access modifiers changed from: protected */
    public void append(E e) {
        if (isStarted()) {
            subAppend(e);
        }
    }

    /* access modifiers changed from: protected */
    public void closeOutputStream() {
        if (this.outputStream != null) {
            try {
                encoderClose();
                this.outputStream.close();
                this.outputStream = null;
            } catch (IOException e) {
                addStatus(new ErrorStatus("Could not close output stream for OutputStreamAppender.", this, e));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void encoderClose() {
        Encoder<E> encoder2 = this.encoder;
        if (encoder2 != null && this.outputStream != null) {
            try {
                encoder2.close();
            } catch (IOException e) {
                this.started = false;
                addStatus(new ErrorStatus("Failed to write footer for appender named [" + this.name + "].", this, e));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void encoderInit() {
        OutputStream outputStream2;
        Encoder<E> encoder2 = this.encoder;
        if (encoder2 != null && (outputStream2 = this.outputStream) != null) {
            try {
                encoder2.init(outputStream2);
            } catch (IOException e) {
                this.started = false;
                addStatus(new ErrorStatus("Failed to initialize encoder for appender named [" + this.name + "].", this, e));
            }
        }
    }

    public Encoder<E> getEncoder() {
        return this.encoder;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    public void setEncoder(Encoder<E> encoder2) {
        this.encoder = encoder2;
    }

    public void setLayout(Layout<E> layout) {
        addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
        addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
        addWarn("See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details");
        LayoutWrappingEncoder layoutWrappingEncoder = new LayoutWrappingEncoder();
        layoutWrappingEncoder.setLayout(layout);
        layoutWrappingEncoder.setContext(this.context);
        this.encoder = layoutWrappingEncoder;
    }

    public void setOutputStream(OutputStream outputStream2) {
        synchronized (this.lock) {
            closeOutputStream();
            this.outputStream = outputStream2;
            if (this.encoder == null) {
                addWarn("Encoder has not been set. Cannot invoke its init method.");
            } else {
                encoderInit();
            }
        }
    }

    public void start() {
        int i;
        if (this.encoder == null) {
            addStatus(new ErrorStatus("No encoder set for the appender named \"" + this.name + "\".", this));
            i = 1;
        } else {
            i = 0;
        }
        if (this.outputStream == null) {
            addStatus(new ErrorStatus("No output stream set for the appender named \"" + this.name + "\".", this));
            i++;
        }
        if (i == 0) {
            super.start();
        }
    }

    public void stop() {
        synchronized (this.lock) {
            closeOutputStream();
            super.stop();
        }
    }

    /* access modifiers changed from: protected */
    public void subAppend(E e) {
        if (isStarted()) {
            try {
                if (e instanceof DeferredProcessingAware) {
                    ((DeferredProcessingAware) e).prepareForDeferredProcessing();
                }
                synchronized (this.lock) {
                    writeOut(e);
                }
            } catch (IOException e2) {
                this.started = false;
                addStatus(new ErrorStatus("IO failure in appender", this, e2));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void writeOut(E e) throws IOException {
        this.encoder.doEncode(e);
    }
}
