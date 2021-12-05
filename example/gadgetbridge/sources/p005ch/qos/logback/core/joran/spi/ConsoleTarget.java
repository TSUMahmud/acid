package p005ch.qos.logback.core.joran.spi;

import java.io.IOException;
import java.io.OutputStream;

@Deprecated
/* renamed from: ch.qos.logback.core.joran.spi.ConsoleTarget */
public enum ConsoleTarget {
    SystemOut("System.out", new OutputStream() {
        public void flush() throws IOException {
            System.out.flush();
        }

        public void write(int i) throws IOException {
            System.out.write(i);
        }

        public void write(byte[] bArr) throws IOException {
            System.out.write(bArr);
        }

        public void write(byte[] bArr, int i, int i2) throws IOException {
            System.out.write(bArr, i, i2);
        }
    }),
    SystemErr("System.err", new OutputStream() {
        public void flush() throws IOException {
            System.err.flush();
        }

        public void write(int i) throws IOException {
            System.err.write(i);
        }

        public void write(byte[] bArr) throws IOException {
            System.err.write(bArr);
        }

        public void write(byte[] bArr, int i, int i2) throws IOException {
            System.err.write(bArr, i, i2);
        }
    });
    
    private final String name;
    private final OutputStream stream;

    private ConsoleTarget(String str, OutputStream outputStream) {
        this.name = str;
        this.stream = outputStream;
    }

    public static ConsoleTarget findByName(String str) {
        for (ConsoleTarget consoleTarget : values()) {
            if (consoleTarget.name.equalsIgnoreCase(str)) {
                return consoleTarget;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public OutputStream getStream() {
        return this.stream;
    }
}
