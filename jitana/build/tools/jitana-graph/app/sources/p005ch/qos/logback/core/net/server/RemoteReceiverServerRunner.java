package p005ch.qos.logback.core.net.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

/* renamed from: ch.qos.logback.core.net.server.RemoteReceiverServerRunner */
class RemoteReceiverServerRunner extends ConcurrentServerRunner<RemoteReceiverClient> {
    private final int clientQueueSize;

    public RemoteReceiverServerRunner(ServerListener<RemoteReceiverClient> serverListener, Executor executor, int i) {
        super(serverListener, executor);
        this.clientQueueSize = i;
    }

    /* access modifiers changed from: protected */
    public boolean configureClient(RemoteReceiverClient remoteReceiverClient) {
        remoteReceiverClient.setContext(getContext());
        remoteReceiverClient.setQueue(new ArrayBlockingQueue(this.clientQueueSize));
        return true;
    }
}
