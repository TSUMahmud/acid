package p005ch.qos.logback.core.net.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.util.CloseUtil;

/* renamed from: ch.qos.logback.core.net.server.RemoteReceiverStreamClient */
class RemoteReceiverStreamClient extends ContextAwareBase implements RemoteReceiverClient {
    private final String clientId;
    private final OutputStream outputStream;
    private BlockingQueue<Serializable> queue;
    private final Socket socket;

    RemoteReceiverStreamClient(String str, OutputStream outputStream2) {
        this.clientId = "client " + str + ": ";
        this.socket = null;
        this.outputStream = outputStream2;
    }

    public RemoteReceiverStreamClient(String str, Socket socket2) {
        this.clientId = "client " + str + ": ";
        this.socket = socket2;
        this.outputStream = null;
    }

    private ObjectOutputStream createObjectOutputStream() throws IOException {
        Socket socket2 = this.socket;
        return socket2 == null ? new ObjectOutputStream(this.outputStream) : new ObjectOutputStream(socket2.getOutputStream());
    }

    public void close() {
        Socket socket2 = this.socket;
        if (socket2 != null) {
            CloseUtil.closeQuietly(socket2);
        }
    }

    public boolean offer(Serializable serializable) {
        BlockingQueue<Serializable> blockingQueue = this.queue;
        if (blockingQueue != null) {
            return blockingQueue.offer(serializable);
        }
        throw new IllegalStateException("client has no event queue");
    }

    public void run() {
        StringBuilder sb;
        addInfo(this.clientId + "connected");
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = createObjectOutputStream();
            loop0:
            while (true) {
                int i = 0;
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        break loop0;
                    }
                    try {
                        objectOutputStream.writeObject(this.queue.take());
                        objectOutputStream.flush();
                        i++;
                        if (i >= 70) {
                            try {
                                objectOutputStream.reset();
                                break;
                            } catch (InterruptedException e) {
                                i = 0;
                            }
                        } else {
                            continue;
                        }
                    } catch (InterruptedException e2) {
                        Thread.currentThread().interrupt();
                    }
                }
                sb.append(this.clientId);
                sb.append("connection closed");
                addInfo(sb.toString());
            }
            if (objectOutputStream != null) {
                CloseUtil.closeQuietly((Closeable) objectOutputStream);
            }
            close();
            sb = new StringBuilder();
        } catch (SocketException e3) {
            addInfo(this.clientId + e3);
            if (objectOutputStream != null) {
                CloseUtil.closeQuietly((Closeable) objectOutputStream);
            }
            close();
            sb = new StringBuilder();
        } catch (IOException e4) {
            addError(this.clientId + e4);
            if (objectOutputStream != null) {
                CloseUtil.closeQuietly((Closeable) objectOutputStream);
            }
            close();
            sb = new StringBuilder();
        } catch (RuntimeException e5) {
            addError(this.clientId + e5);
            if (objectOutputStream != null) {
                CloseUtil.closeQuietly((Closeable) objectOutputStream);
            }
            close();
            sb = new StringBuilder();
        } catch (Throwable th) {
            if (objectOutputStream != null) {
                CloseUtil.closeQuietly((Closeable) objectOutputStream);
            }
            close();
            addInfo(this.clientId + "connection closed");
            throw th;
        }
        sb.append(this.clientId);
        sb.append("connection closed");
        addInfo(sb.toString());
    }

    public void setQueue(BlockingQueue<Serializable> blockingQueue) {
        this.queue = blockingQueue;
    }
}
