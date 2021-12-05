package p005ch.qos.logback.core.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import p005ch.qos.logback.core.AppenderBase;
import p005ch.qos.logback.core.net.SocketConnector;
import p005ch.qos.logback.core.spi.PreSerializationTransformer;
import p005ch.qos.logback.core.util.CloseUtil;
import p005ch.qos.logback.core.util.Duration;

/* renamed from: ch.qos.logback.core.net.AbstractSocketAppender */
public abstract class AbstractSocketAppender<E> extends AppenderBase<E> implements Runnable, SocketConnector.ExceptionHandler {
    private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
    private static final int DEFAULT_EVENT_DELAY_TIMEOUT = 100;
    public static final int DEFAULT_PORT = 4560;
    public static final int DEFAULT_QUEUE_SIZE = 128;
    public static final int DEFAULT_RECONNECTION_DELAY = 30000;
    private int acceptConnectionTimeout = 5000;
    private InetAddress address;
    private Future<Socket> connectorTask;
    private Duration eventDelayLimit = new Duration(100);
    private String peerId;
    private int port = DEFAULT_PORT;
    private BlockingQueue<E> queue;
    private int queueSize = 128;
    private Duration reconnectionDelay = new Duration(30000);
    private String remoteHost;
    private volatile Socket socket;
    private Future<?> task;

    protected AbstractSocketAppender() {
    }

    @Deprecated
    protected AbstractSocketAppender(String str, int i) {
        this.remoteHost = str;
        this.port = i;
    }

    private Future<Socket> activateConnector(SocketConnector socketConnector) {
        try {
            return getContext().getExecutorService().submit(socketConnector);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    private SocketConnector createConnector(InetAddress inetAddress, int i, int i2, long j) {
        SocketConnector newConnector = newConnector(inetAddress, i, (long) i2, j);
        newConnector.setExceptionHandler(this);
        newConnector.setSocketFactory(getSocketFactory());
        return newConnector;
    }

    private void dispatchEvents() throws InterruptedException {
        try {
            this.socket.setSoTimeout(this.acceptConnectionTimeout);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.socket.setSoTimeout(0);
            addInfo(this.peerId + "connection established");
            while (true) {
                int i = 0;
                do {
                    E take = this.queue.take();
                    postProcessEvent(take);
                    objectOutputStream.writeObject(getPST().transform(take));
                    objectOutputStream.flush();
                    i++;
                } while (i < 70);
                objectOutputStream.reset();
            }
        } catch (IOException e) {
            addInfo(this.peerId + "connection failed: " + e);
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            addInfo(this.peerId + "connection closed");
        } catch (Throwable th) {
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            addInfo(this.peerId + "connection closed");
            throw th;
        }
    }

    @Deprecated
    protected static InetAddress getAddressByName(String str) {
        try {
            return InetAddress.getByName(str);
        } catch (Exception e) {
            return null;
        }
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

    /* access modifiers changed from: protected */
    public void append(E e) {
        if (e != null && isStarted()) {
            try {
                if (!this.queue.offer(e, this.eventDelayLimit.getMilliseconds(), TimeUnit.MILLISECONDS)) {
                    addInfo("Dropping event due to timeout limit of [" + this.eventDelayLimit + "] milliseconds being exceeded");
                }
            } catch (InterruptedException e2) {
                addError("Interrupted while appending event to SocketAppender", e2);
            }
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
                sb.append(this.peerId);
                sb.append("connection refused");
            } else {
                sb = new StringBuilder();
                sb.append(this.peerId);
                sb.append(exc);
            }
            sb2 = sb.toString();
        }
        addInfo(sb2);
    }

    public Duration getEventDelayLimit() {
        return this.eventDelayLimit;
    }

    /* access modifiers changed from: protected */
    public abstract PreSerializationTransformer<E> getPST();

    public int getPort() {
        return this.port;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public Duration getReconnectionDelay() {
        return this.reconnectionDelay;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    /* access modifiers changed from: protected */
    public SocketFactory getSocketFactory() {
        return SocketFactory.getDefault();
    }

    /* access modifiers changed from: package-private */
    public BlockingQueue<E> newBlockingQueue(int i) {
        return i <= 0 ? new SynchronousQueue<>() : new ArrayBlockingQueue<>(i);
    }

    /* access modifiers changed from: protected */
    public SocketConnector newConnector(InetAddress inetAddress, int i, long j, long j2) {
        return new DefaultSocketConnector(inetAddress, i, j, j2);
    }

    /* access modifiers changed from: protected */
    public abstract void postProcessEvent(E e);

    public final void run() {
        signalEntryInRunMethod();
        while (true) {
            try {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                this.connectorTask = activateConnector(createConnector(this.address, this.port, 0, this.reconnectionDelay.getMilliseconds()));
                if (this.connectorTask == null) {
                    break;
                }
                this.socket = waitForConnectorToReturnASocket();
                if (this.socket == null) {
                    break;
                }
                dispatchEvents();
            } catch (InterruptedException e) {
            }
        }
        addInfo("shutting down");
    }

    /* access modifiers changed from: package-private */
    public void setAcceptConnectionTimeout(int i) {
        this.acceptConnectionTimeout = i;
    }

    public void setEventDelayLimit(Duration duration) {
        this.eventDelayLimit = duration;
    }

    public void setPort(int i) {
        this.port = i;
    }

    public void setQueueSize(int i) {
        this.queueSize = i;
    }

    public void setReconnectionDelay(Duration duration) {
        this.reconnectionDelay = duration;
    }

    public void setRemoteHost(String str) {
        this.remoteHost = str;
    }

    /* access modifiers changed from: protected */
    public void signalEntryInRunMethod() {
    }

    public void start() {
        if (!isStarted()) {
            int i = 0;
            if (this.port <= 0) {
                addError("No port was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_port");
                i = 1;
            }
            if (this.remoteHost == null) {
                i++;
                addError("No remote host was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
            }
            if (this.queueSize < 0) {
                i++;
                addError("Queue size must be non-negative");
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
                this.queue = newBlockingQueue(this.queueSize);
                this.peerId = "remote peer " + this.remoteHost + ":" + this.port + ": ";
                this.task = getContext().getExecutorService().submit(this);
                super.start();
            }
        }
    }

    public void stop() {
        if (isStarted()) {
            CloseUtil.closeQuietly(this.socket);
            this.task.cancel(true);
            Future<Socket> future = this.connectorTask;
            if (future != null) {
                future.cancel(true);
            }
            super.stop();
        }
    }
}
