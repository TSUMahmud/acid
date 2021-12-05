package p005ch.qos.logback.core.recovery;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/* renamed from: ch.qos.logback.core.recovery.ResilientFileOutputStream */
public class ResilientFileOutputStream extends ResilientOutputStreamBase {
    private File file;
    private FileOutputStream fos;

    public ResilientFileOutputStream(File file2, boolean z) throws FileNotFoundException {
        this.file = file2;
        this.fos = new FileOutputStream(file2, z);
        this.f56os = new BufferedOutputStream(this.fos);
        this.presumedClean = true;
    }

    public FileChannel getChannel() {
        if (this.f56os == null) {
            return null;
        }
        return this.fos.getChannel();
    }

    /* access modifiers changed from: package-private */
    public String getDescription() {
        return "file [" + this.file + "]";
    }

    public File getFile() {
        return this.file;
    }

    /* access modifiers changed from: package-private */
    public OutputStream openNewOutputStream() throws IOException {
        this.fos = new FileOutputStream(this.file, true);
        return new BufferedOutputStream(this.fos);
    }

    public String toString() {
        return "c.q.l.c.recovery.ResilientFileOutputStream@" + System.identityHashCode(this);
    }
}
