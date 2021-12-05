package p005ch.qos.logback.classic.net.server;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.util.CloseUtil;

/* renamed from: ch.qos.logback.classic.net.server.RemoteAppenderStreamClient */
class RemoteAppenderStreamClient implements RemoteAppenderClient {

    /* renamed from: id */
    private final String f47id;
    private final InputStream inputStream;

    /* renamed from: lc */
    private LoggerContext f48lc;
    private Logger logger;
    private final Socket socket;

    public RemoteAppenderStreamClient(String str, InputStream inputStream2) {
        this.f47id = str;
        this.socket = null;
        this.inputStream = inputStream2;
    }

    public RemoteAppenderStreamClient(String str, Socket socket2) {
        this.f47id = str;
        this.socket = socket2;
        this.inputStream = null;
    }

    private ObjectInputStream createObjectInputStream() throws IOException {
        InputStream inputStream2 = this.inputStream;
        return inputStream2 != null ? new ObjectInputStream(inputStream2) : new ObjectInputStream(this.socket.getInputStream());
    }

    public void close() {
        Socket socket2 = this.socket;
        if (socket2 != null) {
            CloseUtil.closeQuietly(socket2);
        }
    }

    public void run() {
        StringBuilder sb;
        Logger logger2;
        Logger logger3 = this.logger;
        logger3.info(this + ": connected");
        try {
            ObjectInputStream createObjectInputStream = createObjectInputStream();
            while (true) {
                ILoggingEvent iLoggingEvent = (ILoggingEvent) createObjectInputStream.readObject();
                Logger logger4 = this.f48lc.getLogger(iLoggingEvent.getLoggerName());
                if (logger4.isEnabledFor(iLoggingEvent.getLevel())) {
                    logger4.callAppenders(iLoggingEvent);
                }
            }
        } catch (EOFException e) {
            if (0 != 0) {
                CloseUtil.closeQuietly((Closeable) null);
            }
            close();
            logger2 = this.logger;
            sb = new StringBuilder();
        } catch (IOException e2) {
            Logger logger5 = this.logger;
            logger5.info(this + ": " + e2);
            if (0 != 0) {
                CloseUtil.closeQuietly((Closeable) null);
            }
            close();
            logger2 = this.logger;
            sb = new StringBuilder();
        } catch (ClassNotFoundException e3) {
            Logger logger6 = this.logger;
            logger6.error(this + ": unknown event class");
            if (0 != 0) {
                CloseUtil.closeQuietly((Closeable) null);
            }
            close();
            logger2 = this.logger;
            sb = new StringBuilder();
        } catch (RuntimeException e4) {
            Logger logger7 = this.logger;
            logger7.error(this + ": " + e4);
            if (0 != 0) {
                CloseUtil.closeQuietly((Closeable) null);
            }
            close();
            logger2 = this.logger;
            sb = new StringBuilder();
        } catch (Throwable th) {
            if (0 != 0) {
                CloseUtil.closeQuietly((Closeable) null);
            }
            close();
            Logger logger8 = this.logger;
            logger8.info(this + ": connection closed");
            throw th;
        }
        sb.append(this);
        sb.append(": connection closed");
        logger2.info(sb.toString());
    }

    public void setLoggerContext(LoggerContext loggerContext) {
        this.f48lc = loggerContext;
        this.logger = loggerContext.getLogger(getClass().getPackage().getName());
    }

    public String toString() {
        return "client " + this.f47id;
    }
}
