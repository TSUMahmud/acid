package p005ch.qos.logback.core.net.server;

import java.io.Closeable;
import java.io.IOException;
import p005ch.qos.logback.core.net.server.Client;

/* renamed from: ch.qos.logback.core.net.server.ServerListener */
public interface ServerListener<T extends Client> extends Closeable {
    T acceptClient() throws IOException, InterruptedException;

    void close();
}
