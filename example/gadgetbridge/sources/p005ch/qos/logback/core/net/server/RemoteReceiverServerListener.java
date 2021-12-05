package p005ch.qos.logback.core.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* renamed from: ch.qos.logback.core.net.server.RemoteReceiverServerListener */
class RemoteReceiverServerListener extends ServerSocketListener<RemoteReceiverClient> {
    public RemoteReceiverServerListener(ServerSocket serverSocket) {
        super(serverSocket);
    }

    /* access modifiers changed from: protected */
    public RemoteReceiverClient createClient(String str, Socket socket) throws IOException {
        return new RemoteReceiverStreamClient(str, socket);
    }
}
