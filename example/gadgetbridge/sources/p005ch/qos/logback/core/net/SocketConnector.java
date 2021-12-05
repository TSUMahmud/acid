package p005ch.qos.logback.core.net;

import java.net.Socket;
import java.util.concurrent.Callable;
import javax.net.SocketFactory;

/* renamed from: ch.qos.logback.core.net.SocketConnector */
public interface SocketConnector extends Callable<Socket> {

    /* renamed from: ch.qos.logback.core.net.SocketConnector$ExceptionHandler */
    public interface ExceptionHandler {
        void connectionFailed(SocketConnector socketConnector, Exception exc);
    }

    Socket call() throws InterruptedException;

    void setExceptionHandler(ExceptionHandler exceptionHandler);

    void setSocketFactory(SocketFactory socketFactory);
}
