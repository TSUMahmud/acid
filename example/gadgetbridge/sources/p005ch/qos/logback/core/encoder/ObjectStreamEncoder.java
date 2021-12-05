package p005ch.qos.logback.core.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/* renamed from: ch.qos.logback.core.encoder.ObjectStreamEncoder */
public class ObjectStreamEncoder<E> extends EncoderBase<E> {
    public static final int START_PEBBLE = 1853421169;
    public static final int STOP_PEBBLE = 640373619;
    private int MAX_BUFFER_SIZE = 100;
    List<E> bufferList = new ArrayList(this.MAX_BUFFER_SIZE);

    public void close() throws IOException {
        writeBuffer();
    }

    public void doEncode(E e) throws IOException {
        this.bufferList.add(e);
        if (this.bufferList.size() == this.MAX_BUFFER_SIZE) {
            writeBuffer();
        }
    }

    public void init(OutputStream outputStream) throws IOException {
        super.init(outputStream);
        this.bufferList.clear();
    }

    /* access modifiers changed from: package-private */
    public void writeBuffer() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10000);
        int size = this.bufferList.size();
        writeHeader(byteArrayOutputStream, size);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        for (E writeObject : this.bufferList) {
            objectOutputStream.writeObject(writeObject);
        }
        this.bufferList.clear();
        objectOutputStream.flush();
        writeFooter(byteArrayOutputStream, size);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        objectOutputStream.close();
        writeEndPosition(byteArray);
        this.outputStream.write(byteArray);
    }

    /* access modifiers changed from: package-private */
    public void writeEndPosition(byte[] bArr) {
        ByteArrayUtil.writeInt(bArr, 8, bArr.length - 8);
    }

    /* access modifiers changed from: package-private */
    public void writeFooter(ByteArrayOutputStream byteArrayOutputStream, int i) {
        ByteArrayUtil.writeInt(byteArrayOutputStream, STOP_PEBBLE);
        ByteArrayUtil.writeInt(byteArrayOutputStream, i ^ STOP_PEBBLE);
    }

    /* access modifiers changed from: package-private */
    public void writeHeader(ByteArrayOutputStream byteArrayOutputStream, int i) {
        ByteArrayUtil.writeInt(byteArrayOutputStream, START_PEBBLE);
        ByteArrayUtil.writeInt(byteArrayOutputStream, i);
        ByteArrayUtil.writeInt(byteArrayOutputStream, 0);
        ByteArrayUtil.writeInt(byteArrayOutputStream, i ^ START_PEBBLE);
    }
}
