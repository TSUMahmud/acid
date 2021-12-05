package brut.util;

import java.io.DataInput;
import java.io.IOException;

public abstract class DataInputDelegate implements DataInput {
    protected final DataInput mDelegate;

    public DataInputDelegate(DataInput dataInput) {
        this.mDelegate = dataInput;
    }

    public boolean readBoolean() throws IOException {
        return this.mDelegate.readBoolean();
    }

    public byte readByte() throws IOException {
        return this.mDelegate.readByte();
    }

    public char readChar() throws IOException {
        return this.mDelegate.readChar();
    }

    public double readDouble() throws IOException {
        return this.mDelegate.readDouble();
    }

    public float readFloat() throws IOException {
        return this.mDelegate.readFloat();
    }

    public void readFully(byte[] bArr) throws IOException {
        this.mDelegate.readFully(bArr);
    }

    public void readFully(byte[] bArr, int i, int i2) throws IOException {
        this.mDelegate.readFully(bArr, i, i2);
    }

    public int readInt() throws IOException {
        return this.mDelegate.readInt();
    }

    public String readLine() throws IOException {
        return this.mDelegate.readLine();
    }

    public long readLong() throws IOException {
        return this.mDelegate.readLong();
    }

    public short readShort() throws IOException {
        return this.mDelegate.readShort();
    }

    public String readUTF() throws IOException {
        return this.mDelegate.readUTF();
    }

    public int readUnsignedByte() throws IOException {
        return this.mDelegate.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return this.mDelegate.readUnsignedShort();
    }

    public int skipBytes(int i) throws IOException {
        return this.mDelegate.skipBytes(i);
    }
}
