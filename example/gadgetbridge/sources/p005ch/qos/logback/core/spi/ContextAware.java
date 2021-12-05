package p005ch.qos.logback.core.spi;

import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.status.Status;

/* renamed from: ch.qos.logback.core.spi.ContextAware */
public interface ContextAware {
    void addError(String str);

    void addError(String str, Throwable th);

    void addInfo(String str);

    void addInfo(String str, Throwable th);

    void addStatus(Status status);

    void addWarn(String str);

    void addWarn(String str, Throwable th);

    Context getContext();

    void setContext(Context context);
}
