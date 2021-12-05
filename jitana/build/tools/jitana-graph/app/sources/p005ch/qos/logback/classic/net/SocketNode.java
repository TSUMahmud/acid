package p005ch.qos.logback.classic.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.net.SocketNode */
public class SocketNode implements Runnable {
    boolean closed = false;
    LoggerContext context;
    Logger logger;
    ObjectInputStream ois;
    SocketAddress remoteSocketAddress;
    Socket socket;
    SimpleSocketServer socketServer;

    public SocketNode(SimpleSocketServer simpleSocketServer, Socket socket2, LoggerContext loggerContext) {
        this.socketServer = simpleSocketServer;
        this.socket = socket2;
        this.remoteSocketAddress = socket2.getRemoteSocketAddress();
        this.context = loggerContext;
        this.logger = loggerContext.getLogger(SocketNode.class);
    }

    /* access modifiers changed from: package-private */
    public void close() {
        if (!this.closed) {
            this.closed = true;
            ObjectInputStream objectInputStream = this.ois;
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    this.logger.warn("Could not close connection.", (Throwable) e);
                } catch (Throwable th) {
                    this.ois = null;
                    throw th;
                }
                this.ois = null;
            }
        }
    }

    public void run() {
        String str;
        Logger logger2;
        try {
            this.ois = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
        } catch (Exception e) {
            Logger logger3 = this.logger;
            logger3.error("Could not open ObjectInputStream to " + this.socket, (Throwable) e);
            this.closed = true;
        }
        while (!this.closed) {
            try {
                ILoggingEvent iLoggingEvent = (ILoggingEvent) this.ois.readObject();
                Logger logger4 = this.context.getLogger(iLoggingEvent.getLoggerName());
                if (logger4.isEnabledFor(iLoggingEvent.getLevel())) {
                    logger4.callAppenders(iLoggingEvent);
                }
            } catch (EOFException e2) {
                logger2 = this.logger;
                str = "Caught java.io.EOFException closing connection.";
                logger2.info(str);
            } catch (SocketException e3) {
                logger2 = this.logger;
                str = "Caught java.net.SocketException closing connection.";
                logger2.info(str);
            } catch (IOException e4) {
                Logger logger5 = this.logger;
                logger5.info("Caught java.io.IOException: " + e4);
                logger2 = this.logger;
                str = "Closing connection.";
                logger2.info(str);
            } catch (Exception e5) {
                this.logger.error("Unexpected exception. Closing connection.", (Throwable) e5);
            }
        }
        this.socketServer.socketNodeClosing(this);
        close();
    }

    public String toString() {
        return getClass().getName() + this.remoteSocketAddress.toString();
    }
}
