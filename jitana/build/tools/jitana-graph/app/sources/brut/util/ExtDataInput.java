package brut.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExtDataInput extends DataInputDelegate {
    public ExtDataInput(DataInput dataInput) {
        super(dataInput);
    }

    public ExtDataInput(InputStream inputStream) {
        this((DataInput) new DataInputStream(inputStream));
    }

    public int[] readIntArray(int i) throws IOException {
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            iArr[i2] = readInt();
        }
        return iArr;
    }

    /* JADX WARNING: Removed duplicated region for block: B:7:0x001a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String readNulEndedString(int r3, boolean r4) throws java.io.IOException {
        /*
            r2 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = 16
            r0.<init>(r1)
        L_0x0007:
            int r1 = r3 + -1
            if (r3 == 0) goto L_0x0018
            short r3 = r2.readShort()
            if (r3 != 0) goto L_0x0012
            goto L_0x0018
        L_0x0012:
            char r3 = (char) r3
            r0.append(r3)
            r3 = r1
            goto L_0x0007
        L_0x0018:
            if (r4 == 0) goto L_0x001f
            int r1 = r1 * 2
            r2.skipBytes(r1)
        L_0x001f:
            java.lang.String r3 = r0.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: brut.util.ExtDataInput.readNulEndedString(int, boolean):java.lang.String");
    }

    public void skipCheckByte(byte b) throws IOException {
        byte readByte = readByte();
        if (readByte != b) {
            throw new IOException(String.format("Expected: 0x%08x, got: 0x%08x", new Object[]{Byte.valueOf(b), Byte.valueOf(readByte)}));
        }
    }

    public void skipCheckInt(int i) throws IOException {
        int readInt = readInt();
        if (readInt != i) {
            throw new IOException(String.format("Expected: 0x%08x, got: 0x%08x", new Object[]{Integer.valueOf(i), Integer.valueOf(readInt)}));
        }
    }

    public void skipCheckShort(short s) throws IOException {
        short readShort = readShort();
        if (readShort != s) {
            throw new IOException(String.format("Expected: 0x%08x, got: 0x%08x", new Object[]{Short.valueOf(s), Short.valueOf(readShort)}));
        }
    }

    public void skipInt() throws IOException {
        skipBytes(4);
    }
}
