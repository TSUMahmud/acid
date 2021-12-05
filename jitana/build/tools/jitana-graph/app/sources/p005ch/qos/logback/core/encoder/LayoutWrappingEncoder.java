package p005ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.Layout;

/* renamed from: ch.qos.logback.core.encoder.LayoutWrappingEncoder */
public class LayoutWrappingEncoder<E> extends EncoderBase<E> {
    private Charset charset;
    private boolean immediateFlush = true;
    protected Layout<E> layout;

    private void appendIfNotNull(StringBuilder sb, String str) {
        if (str != null) {
            sb.append(str);
        }
    }

    private byte[] convertToBytes(String str) {
        Charset charset2 = this.charset;
        if (charset2 == null) {
            return str.getBytes();
        }
        try {
            return str.getBytes(charset2.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("An existing charset cannot possibly be unsupported.");
        }
    }

    public void close() throws IOException {
        writeFooter();
    }

    public void doEncode(E e) throws IOException {
        this.outputStream.write(convertToBytes(this.layout.doLayout(e)));
        if (this.immediateFlush) {
            this.outputStream.flush();
        }
    }

    public Charset getCharset() {
        return this.charset;
    }

    public Layout<E> getLayout() {
        return this.layout;
    }

    public void init(OutputStream outputStream) throws IOException {
        super.init(outputStream);
        writeHeader();
    }

    public boolean isImmediateFlush() {
        return this.immediateFlush;
    }

    public boolean isStarted() {
        return false;
    }

    public void setCharset(Charset charset2) {
        this.charset = charset2;
    }

    public void setImmediateFlush(boolean z) {
        this.immediateFlush = z;
    }

    public void setLayout(Layout<E> layout2) {
        this.layout = layout2;
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
        if (this.outputStream != null) {
            try {
                this.outputStream.flush();
            } catch (IOException e) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeFooter() throws IOException {
        if (this.layout != null && this.outputStream != null) {
            StringBuilder sb = new StringBuilder();
            appendIfNotNull(sb, this.layout.getPresentationFooter());
            appendIfNotNull(sb, this.layout.getFileFooter());
            if (sb.length() > 0) {
                this.outputStream.write(convertToBytes(sb.toString()));
                this.outputStream.flush();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeHeader() throws IOException {
        if (this.layout != null && this.outputStream != null) {
            StringBuilder sb = new StringBuilder();
            appendIfNotNull(sb, this.layout.getFileHeader());
            appendIfNotNull(sb, this.layout.getPresentationHeader());
            if (sb.length() > 0) {
                sb.append(CoreConstants.LINE_SEPARATOR);
                this.outputStream.write(convertToBytes(sb.toString()));
                this.outputStream.flush();
            }
        }
    }
}
