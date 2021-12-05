package p005ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/* renamed from: ch.qos.logback.core.encoder.EventObjectInputStream */
public class EventObjectInputStream<E> extends InputStream {
    List<E> buffer = new ArrayList();
    int index = 0;
    NonClosableInputStream ncis;

    EventObjectInputStream(InputStream inputStream) throws IOException {
        this.ncis = new NonClosableInputStream(inputStream);
    }

    private void internalReset() {
        this.index = 0;
        this.buffer.clear();
    }

    public int available() throws IOException {
        return this.ncis.available();
    }

    public void close() throws IOException {
        this.ncis.realClose();
    }

    /* access modifiers changed from: package-private */
    public E getFromBuffer() {
        if (this.index >= this.buffer.size()) {
            return null;
        }
        List<E> list = this.buffer;
        int i = this.index;
        this.index = i + 1;
        return list.get(i);
    }

    public int read() throws IOException {
        throw new UnsupportedOperationException("Only the readEvent method is supported.");
    }

    public E readEvent() throws IOException {
        E fromBuffer = getFromBuffer();
        if (fromBuffer != null) {
            return fromBuffer;
        }
        internalReset();
        int readHeader = readHeader();
        if (readHeader == -1) {
            return null;
        }
        readPayload(readHeader);
        readFooter(readHeader);
        return getFromBuffer();
    }

    /* access modifiers changed from: package-private */
    public E readEvents(ObjectInputStream objectInputStream) throws IOException {
        E e;
        try {
            e = objectInputStream.readObject();
            try {
                this.buffer.add(e);
            } catch (ClassNotFoundException e2) {
                e = e2;
            }
        } catch (ClassNotFoundException e3) {
            e = e3;
            e = null;
            e.printStackTrace();
            return e;
        }
        return e;
    }

    /* access modifiers changed from: package-private */
    public void readFooter(int i) throws IOException {
        byte[] bArr = new byte[8];
        if (this.ncis.read(bArr) == -1) {
            throw new IllegalStateException("Looks like a corrupt stream");
        } else if (ByteArrayUtil.readInt(bArr, 0) != 640373619) {
            throw new IllegalStateException("Looks like a corrupt stream");
        } else if (ByteArrayUtil.readInt(bArr, 4) != (i ^ ObjectStreamEncoder.STOP_PEBBLE)) {
            throw new IllegalStateException("Invalid checksum");
        }
    }

    /* access modifiers changed from: package-private */
    public int readHeader() throws IOException {
        byte[] bArr = new byte[16];
        if (this.ncis.read(bArr) == -1) {
            return -1;
        }
        if (ByteArrayUtil.readInt(bArr, 0) == 1853421169) {
            int readInt = ByteArrayUtil.readInt(bArr, 4);
            if (ByteArrayUtil.readInt(bArr, 12) == (1853421169 ^ readInt)) {
                return readInt;
            }
            throw new IllegalStateException("Invalid checksum");
        }
        throw new IllegalStateException("Does not look like data created by ObjectStreamEncoder");
    }

    /* access modifiers changed from: package-private */
    public void readPayload(int i) throws IOException {
        ArrayList arrayList = new ArrayList(i);
        ObjectInputStream objectInputStream = new ObjectInputStream(this.ncis);
        for (int i2 = 0; i2 < i; i2++) {
            arrayList.add(readEvents(objectInputStream));
        }
        objectInputStream.close();
    }
}
