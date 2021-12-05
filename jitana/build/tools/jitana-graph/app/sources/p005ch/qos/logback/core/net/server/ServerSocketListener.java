package p005ch.qos.logback.core.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import p005ch.qos.logback.core.net.server.Client;
import p005ch.qos.logback.core.util.CloseUtil;

/* renamed from: ch.qos.logback.core.net.server.ServerSocketListener */
public abstract class ServerSocketListener<T extends Client> implements ServerListener<T> {
    private final ServerSocket serverSocket;

    public ServerSocketListener(ServerSocket serverSocket2) {
        this.serverSocket = serverSocket2;
    }

    private String socketAddressToString(SocketAddress socketAddress) {
        String obj = socketAddress.toString();
        int indexOf = obj.indexOf("/");
        return indexOf >= 0 ? obj.substring(indexOf + 1) : obj;
    }

    public T acceptClient() throws IOException {
        Socket accept = this.serverSocket.accept();
        return createClient(socketAddressToString(accept.getRemoteSocketAddress()), accept);
    }

    public void close() {
        CloseUtil.closeQuietly(this.serverSocket);
    }

    /* access modifiers changed from: protected */
    public abstract T createClient(String str, Socket socket) throws IOException;

    public String toString() {
        return socketAddressToString(this.serverSocket.getLocalSocketAddress());
    }
}
