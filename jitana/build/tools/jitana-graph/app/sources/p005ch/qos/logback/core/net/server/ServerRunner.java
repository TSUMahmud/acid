package p005ch.qos.logback.core.net.server;

import java.io.IOException;
import p005ch.qos.logback.core.net.server.Client;
import p005ch.qos.logback.core.spi.ContextAware;

/* renamed from: ch.qos.logback.core.net.server.ServerRunner */
public interface ServerRunner<T extends Client> extends ContextAware, Runnable {
    void accept(ClientVisitor<T> clientVisitor);

    boolean isRunning();

    void stop() throws IOException;
}
