package p005ch.qos.logback.classic.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import javax.net.SocketFactory;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.net.DefaultSocketConnector;
import p005ch.qos.logback.core.net.SocketConnector;
import p005ch.qos.logback.core.util.CloseUtil;

/* renamed from: ch.qos.logback.classic.net.SocketReceiver */
public class SocketReceiver extends ReceiverBase implements Runnable, SocketConnector.ExceptionHandler {
    private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
    private int acceptConnectionTimeout = 5000;
    private InetAddress address;
    private Future<Socket> connectorTask;
    private int port;
    private String receiverId;
    private int reconnectionDelay;
    private String remoteHost;
    private volatile Socket socket;

    private Future<Socket> activateConnector(SocketConnector socketConnector) {
        try {
            return getContext().getExecutorService().submit(socketConnector);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    private SocketConnector createConnector(InetAddress inetAddress, int i, int i2, int i3) {
        SocketConnector newConnector = newConnector(inetAddress, i, i2, i3);
        newConnector.setExceptionHandler(this);
        newConnector.setSocketFactory(getSocketFactory());
        return newConnector;
    }

    private void dispatchEvents(LoggerContext loggerContext) {
        StringBuilder sb;
        try {
            this.socket.setSoTimeout(this.acceptConnectionTimeout);
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            this.socket.setSoTimeout(0);
            addInfo(this.receiverId + "connection established");
            while (true) {
                ILoggingEvent iLoggingEvent = (ILoggingEvent) objectInputStream.readObject();
                Logger logger = loggerContext.getLogger(iLoggingEvent.getLoggerName());
                if (logger.isEnabledFor(iLoggingEvent.getLevel())) {
                    logger.callAppenders(iLoggingEvent);
                }
            }
        } catch (EOFException e) {
            addInfo(this.receiverId + "end-of-stream detected");
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            sb = new StringBuilder();
        } catch (IOException e2) {
            addInfo(this.receiverId + "connection failed: " + e2);
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            sb = new StringBuilder();
        } catch (ClassNotFoundException e3) {
            addInfo(this.receiverId + "unknown event class: " + e3);
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            sb = new StringBuilder();
        } catch (Throwable th) {
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            addInfo(this.receiverId + "connection closed");
            throw th;
        }
        sb.append(this.receiverId);
        sb.append("connection closed");
        addInfo(sb.toString());
    }

    private Socket waitForConnectorToReturnASocket() throws InterruptedException {
        try {
            Socket socket2 = this.connectorTask.get();
            this.connectorTask = null;
            return socket2;
        } catch (ExecutionException e) {
            return null;
        }
    }

    public void connectionFailed(SocketConnector socketConnector, Exception exc) {
        StringBuilder sb;
        String sb2;
        if (exc instanceof InterruptedException) {
            sb2 = "connector interrupted";
        } else {
            if (exc instanceof ConnectException) {
                sb = new StringBuilder();
                sb.append(this.receiverId);
                sb.append("connection refused");
            } else {
                sb = new StringBuilder();
                sb.append(this.receiverId);
                sb.append(exc);
            }
            sb2 = sb.toString();
        }
        addInfo(sb2);
    }

    /* access modifiers changed from: protected */
    public Runnable getRunnableTask() {
        return this;
    }

    /* access modifiers changed from: protected */
    public SocketFactory getSocketFactory() {
        return SocketFactory.getDefault();
    }

    /* access modifiers changed from: protected */
    public SocketConnector newConnector(InetAddress inetAddress, int i, int i2, int i3) {
        return new DefaultSocketConnector(inetAddress, i, (long) i2, (long) i3);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        if (this.socket != null) {
            CloseUtil.closeQuietly(this.socket);
        }
    }

    public void run() {
        try {
            LoggerContext loggerContext = (LoggerContext) getContext();
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                this.connectorTask = activateConnector(createConnector(this.address, this.port, 0, this.reconnectionDelay));
                if (this.connectorTask == null) {
                    break;
                }
                this.socket = waitForConnectorToReturnASocket();
                if (this.socket == null) {
                    break;
                }
                dispatchEvents(loggerContext);
            }
        } catch (InterruptedException e) {
        }
        addInfo("shutting down");
    }

    public void setAcceptConnectionTimeout(int i) {
        this.acceptConnectionTimeout = i;
    }

    public void setPort(int i) {
        this.port = i;
    }

    public void setReconnectionDelay(int i) {
        this.reconnectionDelay = i;
    }

    public void setRemoteHost(String str) {
        this.remoteHost = str;
    }

    /* access modifiers changed from: protected */
    public boolean shouldStart() {
        int i;
        if (this.port == 0) {
            addError("No port was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_port");
            i = 1;
        } else {
            i = 0;
        }
        if (this.remoteHost == null) {
            i++;
            addError("No host name or address was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_host");
        }
        if (this.reconnectionDelay == 0) {
            this.reconnectionDelay = 30000;
        }
        if (i == 0) {
            try {
                this.address = InetAddress.getByName(this.remoteHost);
            } catch (UnknownHostException e) {
                addError("unknown host: " + this.remoteHost);
                i++;
            }
        }
        if (i == 0) {
            this.receiverId = "receiver " + this.remoteHost + ":" + this.port + ": ";
        }
        return i == 0;
    }
}
