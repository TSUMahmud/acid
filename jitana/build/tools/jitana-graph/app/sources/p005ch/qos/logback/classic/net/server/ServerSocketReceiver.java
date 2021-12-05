package p005ch.qos.logback.classic.net.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import javax.net.ServerSocketFactory;
import p005ch.qos.logback.classic.net.ReceiverBase;
import p005ch.qos.logback.core.net.AbstractSocketAppender;
import p005ch.qos.logback.core.net.server.ServerListener;
import p005ch.qos.logback.core.net.server.ServerRunner;
import p005ch.qos.logback.core.util.CloseUtil;

/* renamed from: ch.qos.logback.classic.net.server.ServerSocketReceiver */
public class ServerSocketReceiver extends ReceiverBase {
    public static final int DEFAULT_BACKLOG = 50;
    private String address;
    private int backlog = 50;
    private int port = AbstractSocketAppender.DEFAULT_PORT;
    private ServerRunner runner;
    private ServerSocket serverSocket;

    /* access modifiers changed from: protected */
    public ServerListener<RemoteAppenderClient> createServerListener(ServerSocket serverSocket2) {
        return new RemoteAppenderServerListener(serverSocket2);
    }

    /* access modifiers changed from: protected */
    public ServerRunner createServerRunner(ServerListener<RemoteAppenderClient> serverListener, Executor executor) {
        return new RemoteAppenderServerRunner(serverListener, executor);
    }

    public String getAddress() {
        return this.address;
    }

    public int getBacklog() {
        return this.backlog;
    }

    /* access modifiers changed from: protected */
    public InetAddress getInetAddress() throws UnknownHostException {
        if (getAddress() == null) {
            return null;
        }
        return InetAddress.getByName(getAddress());
    }

    public int getPort() {
        return this.port;
    }

    /* access modifiers changed from: protected */
    public Runnable getRunnableTask() {
        return this.runner;
    }

    /* access modifiers changed from: protected */
    public ServerSocketFactory getServerSocketFactory() throws Exception {
        return ServerSocketFactory.getDefault();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        try {
            if (this.runner != null) {
                this.runner.stop();
            }
        } catch (IOException e) {
            addError("server shutdown error: " + e, e);
        }
    }

    public void setAddress(String str) {
        this.address = str;
    }

    public void setBacklog(int i) {
        this.backlog = i;
    }

    public void setPort(int i) {
        this.port = i;
    }

    /* access modifiers changed from: protected */
    public boolean shouldStart() {
        try {
            this.runner = createServerRunner(createServerListener(getServerSocketFactory().createServerSocket(getPort(), getBacklog(), getInetAddress())), getContext().getExecutorService());
            this.runner.setContext(getContext());
            return true;
        } catch (Exception e) {
            addError("server startup error: " + e, e);
            CloseUtil.closeQuietly(this.serverSocket);
            return false;
        }
    }
}
