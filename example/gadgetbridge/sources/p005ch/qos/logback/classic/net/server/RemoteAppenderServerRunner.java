package p005ch.qos.logback.classic.net.server;

import java.util.concurrent.Executor;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.net.server.ConcurrentServerRunner;
import p005ch.qos.logback.core.net.server.ServerListener;

/* renamed from: ch.qos.logback.classic.net.server.RemoteAppenderServerRunner */
class RemoteAppenderServerRunner extends ConcurrentServerRunner<RemoteAppenderClient> {
    public RemoteAppenderServerRunner(ServerListener<RemoteAppenderClient> serverListener, Executor executor) {
        super(serverListener, executor);
    }

    /* access modifiers changed from: protected */
    public boolean configureClient(RemoteAppenderClient remoteAppenderClient) {
        remoteAppenderClient.setLoggerContext((LoggerContext) getContext());
        return true;
    }
}
