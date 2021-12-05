package p005ch.qos.logback.core.net.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import p005ch.qos.logback.core.net.server.Client;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.net.server.ConcurrentServerRunner */
public abstract class ConcurrentServerRunner<T extends Client> extends ContextAwareBase implements Runnable, ServerRunner<T> {
    private final Collection<T> clients = new ArrayList();
    private final Lock clientsLock = new ReentrantLock();
    private final Executor executor;
    private final ServerListener<T> listener;
    private boolean running;

    /* renamed from: ch.qos.logback.core.net.server.ConcurrentServerRunner$ClientWrapper */
    private class ClientWrapper implements Client {
        private final T delegate;

        public ClientWrapper(T t) {
            this.delegate = t;
        }

        public void close() {
            this.delegate.close();
        }

        public void run() {
            ConcurrentServerRunner.this.addClient(this.delegate);
            try {
                this.delegate.run();
            } finally {
                ConcurrentServerRunner.this.removeClient(this.delegate);
            }
        }
    }

    public ConcurrentServerRunner(ServerListener<T> serverListener, Executor executor2) {
        this.listener = serverListener;
        this.executor = executor2;
    }

    /* access modifiers changed from: private */
    public void addClient(T t) {
        this.clientsLock.lock();
        try {
            this.clients.add(t);
        } finally {
            this.clientsLock.unlock();
        }
    }

    private Collection<T> copyClients() {
        this.clientsLock.lock();
        try {
            return new ArrayList(this.clients);
        } finally {
            this.clientsLock.unlock();
        }
    }

    /* access modifiers changed from: private */
    public void removeClient(T t) {
        this.clientsLock.lock();
        try {
            this.clients.remove(t);
        } finally {
            this.clientsLock.unlock();
        }
    }

    public void accept(ClientVisitor<T> clientVisitor) {
        for (Client client : copyClients()) {
            try {
                clientVisitor.visit(client);
            } catch (RuntimeException e) {
                addError(client + ": " + e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract boolean configureClient(T t);

    public boolean isRunning() {
        return this.running;
    }

    public void run() {
        setRunning(true);
        try {
            addInfo("listening on " + this.listener);
            while (!Thread.currentThread().isInterrupted()) {
                T acceptClient = this.listener.acceptClient();
                if (!configureClient(acceptClient)) {
                    addError(acceptClient + ": connection dropped");
                } else {
                    try {
                        this.executor.execute(new ClientWrapper(acceptClient));
                    } catch (RejectedExecutionException e) {
                        addError(acceptClient + ": connection dropped");
                    }
                }
                acceptClient.close();
            }
        } catch (InterruptedException e2) {
        } catch (Exception e3) {
            addError("listener: " + e3);
        }
        setRunning(false);
        addInfo("shutting down");
        this.listener.close();
    }

    /* access modifiers changed from: protected */
    public void setRunning(boolean z) {
        this.running = z;
    }

    public void stop() throws IOException {
        this.listener.close();
        accept(new ClientVisitor<T>() {
            public void visit(T t) {
                t.close();
            }
        });
    }
}
