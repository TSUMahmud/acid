package p005ch.qos.logback.core.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import p005ch.qos.logback.core.net.SocketConnector;
import p005ch.qos.logback.core.util.DelayStrategy;
import p005ch.qos.logback.core.util.FixedDelay;

/* renamed from: ch.qos.logback.core.net.DefaultSocketConnector */
public class DefaultSocketConnector implements SocketConnector {
    private final InetAddress address;
    private final DelayStrategy delayStrategy;
    private SocketConnector.ExceptionHandler exceptionHandler;
    private final int port;
    private SocketFactory socketFactory;

    /* renamed from: ch.qos.logback.core.net.DefaultSocketConnector$ConsoleExceptionHandler */
    private static class ConsoleExceptionHandler implements SocketConnector.ExceptionHandler {
        private ConsoleExceptionHandler() {
        }

        public void connectionFailed(SocketConnector socketConnector, Exception exc) {
            System.out.println(exc);
        }
    }

    public DefaultSocketConnector(InetAddress inetAddress, int i, long j, long j2) {
        this(inetAddress, i, new FixedDelay(j, j2));
    }

    public DefaultSocketConnector(InetAddress inetAddress, int i, DelayStrategy delayStrategy2) {
        this.address = inetAddress;
        this.port = i;
        this.delayStrategy = delayStrategy2;
    }

    private Socket createSocket() {
        try {
            return this.socketFactory.createSocket(this.address, this.port);
        } catch (IOException e) {
            this.exceptionHandler.connectionFailed(this, e);
            return null;
        }
    }

    private void useDefaultsForMissingFields() {
        if (this.exceptionHandler == null) {
            this.exceptionHandler = new ConsoleExceptionHandler();
        }
        if (this.socketFactory == null) {
            this.socketFactory = SocketFactory.getDefault();
        }
    }

    public Socket call() throws InterruptedException {
        Socket createSocket;
        useDefaultsForMissingFields();
        while (true) {
            createSocket = createSocket();
            if (createSocket != null || Thread.currentThread().isInterrupted()) {
                return createSocket;
            }
            Thread.sleep(this.delayStrategy.nextDelay());
        }
        return createSocket;
    }

    public void setExceptionHandler(SocketConnector.ExceptionHandler exceptionHandler2) {
        this.exceptionHandler = exceptionHandler2;
    }

    public void setSocketFactory(SocketFactory socketFactory2) {
        this.socketFactory = socketFactory2;
    }
}
