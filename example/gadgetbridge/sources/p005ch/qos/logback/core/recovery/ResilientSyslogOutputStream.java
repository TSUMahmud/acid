package p005ch.qos.logback.core.recovery;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import p005ch.qos.logback.core.net.SyslogOutputStream;

/* renamed from: ch.qos.logback.core.recovery.ResilientSyslogOutputStream */
public class ResilientSyslogOutputStream extends ResilientOutputStreamBase {
    int port;
    String syslogHost;

    public ResilientSyslogOutputStream(String str, int i) throws UnknownHostException, SocketException {
        this.syslogHost = str;
        this.port = i;
        this.f56os = new SyslogOutputStream(str, i);
        this.presumedClean = true;
    }

    /* access modifiers changed from: package-private */
    public String getDescription() {
        return "syslog [" + this.syslogHost + ":" + this.port + "]";
    }

    /* access modifiers changed from: package-private */
    public OutputStream openNewOutputStream() throws IOException {
        return new SyslogOutputStream(this.syslogHost, this.port);
    }

    public String toString() {
        return "c.q.l.c.recovery.ResilientSyslogOutputStream@" + System.identityHashCode(this);
    }
}
