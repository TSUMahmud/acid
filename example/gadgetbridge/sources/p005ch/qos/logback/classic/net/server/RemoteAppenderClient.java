package p005ch.qos.logback.classic.net.server;

import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.net.server.Client;

/* renamed from: ch.qos.logback.classic.net.server.RemoteAppenderClient */
interface RemoteAppenderClient extends Client {
    void setLoggerContext(LoggerContext loggerContext);
}
