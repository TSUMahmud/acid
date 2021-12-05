package p005ch.qos.logback.core.net.server;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import p005ch.qos.logback.core.spi.ContextAware;

/* renamed from: ch.qos.logback.core.net.server.RemoteReceiverClient */
interface RemoteReceiverClient extends Client, ContextAware {
    boolean offer(Serializable serializable);

    void setQueue(BlockingQueue<Serializable> blockingQueue);
}
