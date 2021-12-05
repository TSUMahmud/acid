package p005ch.qos.logback.classic.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import p005ch.qos.logback.core.net.server.ServerSocketListener;

/* renamed from: ch.qos.logback.classic.net.server.RemoteAppenderServerListener */
class RemoteAppenderServerListener extends ServerSocketListener<RemoteAppenderClient> {
    public RemoteAppenderServerListener(ServerSocket serverSocket) {
        super(serverSocket);
    }

    /* access modifiers changed from: protected */
    public RemoteAppenderClient createClient(String str, Socket socket) throws IOException {
        return new RemoteAppenderStreamClient(str, socket);
    }
}
