package p005ch.qos.logback.core.net.server;

import p005ch.qos.logback.core.net.server.Client;

/* renamed from: ch.qos.logback.core.net.server.ClientVisitor */
public interface ClientVisitor<T extends Client> {
    void visit(T t);
}
