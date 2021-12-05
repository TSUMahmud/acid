package p005ch.qos.logback.classic.net;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.net.ServerSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.joran.JoranConfigurator;
import p005ch.qos.logback.core.joran.spi.JoranException;

/* renamed from: ch.qos.logback.classic.net.SimpleSocketServer */
public class SimpleSocketServer extends Thread {
    private boolean closed = false;
    private CountDownLatch latch;

    /* renamed from: lc */
    private final LoggerContext f46lc;
    Logger logger = LoggerFactory.getLogger((Class<?>) SimpleSocketServer.class);
    private final int port;
    private ServerSocket serverSocket;
    private List<SocketNode> socketNodeList = new ArrayList();

    public SimpleSocketServer(LoggerContext loggerContext, int i) {
        this.f46lc = loggerContext;
        this.port = i;
    }

    public static void configureLC(LoggerContext loggerContext, String str) throws JoranException {
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        loggerContext.reset();
        joranConfigurator.setContext(loggerContext);
        joranConfigurator.doConfigure(str);
    }

    protected static void doMain(Class<? extends SimpleSocketServer> cls, String[] strArr) throws Exception {
        int i;
        if (strArr.length == 2) {
            i = parsePortNumber(strArr[0]);
        } else {
            usage("Wrong number of arguments.");
            i = -1;
        }
        String str = strArr[1];
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        configureLC(loggerContext, str);
        new SimpleSocketServer(loggerContext, i).start();
    }

    public static void main(String[] strArr) throws Exception {
        doMain(SimpleSocketServer.class, strArr);
    }

    static int parsePortNumber(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            usage("Could not interpret port number [" + str + "].");
            return -1;
        }
    }

    static void usage(String str) {
        System.err.println(str);
        PrintStream printStream = System.err;
        printStream.println("Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
        System.exit(1);
    }

    public void close() {
        this.closed = true;
        ServerSocket serverSocket2 = this.serverSocket;
        if (serverSocket2 != null) {
            try {
                serverSocket2.close();
            } catch (IOException e) {
                this.logger.error("Failed to close serverSocket", (Throwable) e);
            } catch (Throwable th) {
                this.serverSocket = null;
                throw th;
            }
            this.serverSocket = null;
        }
        this.logger.info("closing this server");
        synchronized (this.socketNodeList) {
            for (SocketNode close : this.socketNodeList) {
                close.close();
            }
        }
        if (this.socketNodeList.size() != 0) {
            this.logger.warn("Was expecting a 0-sized socketNodeList after server shutdown");
        }
    }

    /* access modifiers changed from: protected */
    public String getClientThreadName(Socket socket) {
        return String.format("Logback SocketNode (client: %s)", new Object[]{socket.getRemoteSocketAddress()});
    }

    public CountDownLatch getLatch() {
        return this.latch;
    }

    /* access modifiers changed from: protected */
    public ServerSocketFactory getServerSocketFactory() {
        return ServerSocketFactory.getDefault();
    }

    /* access modifiers changed from: protected */
    public String getServerThreadName() {
        return String.format("Logback %s (port %d)", new Object[]{getClass().getSimpleName(), Integer.valueOf(this.port)});
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void run() {
        String name = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(getServerThreadName());
            Logger logger2 = this.logger;
            logger2.info("Listening on port " + this.port);
            this.serverSocket = getServerSocketFactory().createServerSocket(this.port);
            while (!this.closed) {
                this.logger.info("Waiting to accept a new client.");
                signalAlmostReadiness();
                Socket accept = this.serverSocket.accept();
                Logger logger3 = this.logger;
                logger3.info("Connected to client at " + accept.getInetAddress());
                this.logger.info("Starting new socket node.");
                SocketNode socketNode = new SocketNode(this, accept, this.f46lc);
                synchronized (this.socketNodeList) {
                    this.socketNodeList.add(socketNode);
                }
                new Thread(socketNode, getClientThreadName(accept)).start();
            }
        } catch (Exception e) {
            try {
                if (this.closed) {
                    this.logger.info("Exception in run method for a closed server. This is normal.");
                } else {
                    this.logger.error("Unexpected failure in run method", (Throwable) e);
                }
            } catch (Throwable th) {
                Thread.currentThread().setName(name);
                throw th;
            }
        }
        Thread.currentThread().setName(name);
    }

    /* access modifiers changed from: package-private */
    public void setLatch(CountDownLatch countDownLatch) {
        this.latch = countDownLatch;
    }

    /* access modifiers changed from: package-private */
    public void signalAlmostReadiness() {
        CountDownLatch countDownLatch = this.latch;
        if (countDownLatch != null && countDownLatch.getCount() != 0) {
            this.latch.countDown();
        }
    }

    public void socketNodeClosing(SocketNode socketNode) {
        this.logger.debug("Removing {}", (Object) socketNode);
        synchronized (this.socketNodeList) {
            this.socketNodeList.remove(socketNode);
        }
    }
}
