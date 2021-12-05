package p005ch.qos.logback.core.net.server;

import java.io.Closeable;

/* renamed from: ch.qos.logback.core.net.server.Client */
public interface Client extends Runnable, Closeable {
    void close();
}
