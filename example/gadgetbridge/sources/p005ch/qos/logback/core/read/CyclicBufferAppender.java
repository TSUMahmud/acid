package p005ch.qos.logback.core.read;

import p005ch.qos.logback.core.AppenderBase;
import p005ch.qos.logback.core.helpers.CyclicBuffer;

/* renamed from: ch.qos.logback.core.read.CyclicBufferAppender */
public class CyclicBufferAppender<E> extends AppenderBase<E> {

    /* renamed from: cb */
    CyclicBuffer<E> f55cb;
    int maxSize = 512;

    /* access modifiers changed from: protected */
    public void append(E e) {
        if (isStarted()) {
            this.f55cb.add(e);
        }
    }

    public E get(int i) {
        if (isStarted()) {
            return this.f55cb.get(i);
        }
        return null;
    }

    public int getLength() {
        if (isStarted()) {
            return this.f55cb.length();
        }
        return 0;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void reset() {
        this.f55cb.clear();
    }

    public void setMaxSize(int i) {
        this.maxSize = i;
    }

    public void start() {
        this.f55cb = new CyclicBuffer<>(this.maxSize);
        super.start();
    }

    public void stop() {
        this.f55cb = null;
        super.stop();
    }
}
