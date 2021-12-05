package p005ch.qos.logback.core.spi;

import java.io.PrintStream;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.status.ErrorStatus;
import p005ch.qos.logback.core.status.InfoStatus;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusManager;
import p005ch.qos.logback.core.status.WarnStatus;

/* renamed from: ch.qos.logback.core.spi.ContextAwareImpl */
public class ContextAwareImpl implements ContextAware {
    protected Context context;
    private int noContextWarning = 0;
    final Object origin;

    public ContextAwareImpl(Context context2, Object obj) {
        this.context = context2;
        this.origin = obj;
    }

    public void addError(String str) {
        addStatus(new ErrorStatus(str, getOrigin()));
    }

    public void addError(String str, Throwable th) {
        addStatus(new ErrorStatus(str, getOrigin(), th));
    }

    public void addInfo(String str) {
        addStatus(new InfoStatus(str, getOrigin()));
    }

    public void addInfo(String str, Throwable th) {
        addStatus(new InfoStatus(str, getOrigin(), th));
    }

    public void addStatus(Status status) {
        Context context2 = this.context;
        if (context2 == null) {
            int i = this.noContextWarning;
            this.noContextWarning = i + 1;
            if (i == 0) {
                PrintStream printStream = System.out;
                printStream.println("LOGBACK: No context given for " + this);
                return;
            }
            return;
        }
        StatusManager statusManager = context2.getStatusManager();
        if (statusManager != null) {
            statusManager.add(status);
        }
    }

    public void addWarn(String str) {
        addStatus(new WarnStatus(str, getOrigin()));
    }

    public void addWarn(String str, Throwable th) {
        addStatus(new WarnStatus(str, getOrigin(), th));
    }

    public Context getContext() {
        return this.context;
    }

    /* access modifiers changed from: protected */
    public Object getOrigin() {
        return this.origin;
    }

    public StatusManager getStatusManager() {
        Context context2 = this.context;
        if (context2 == null) {
            return null;
        }
        return context2.getStatusManager();
    }

    public void setContext(Context context2) {
        Context context3 = this.context;
        if (context3 == null) {
            this.context = context2;
        } else if (context3 != context2) {
            throw new IllegalStateException("Context has been already set");
        }
    }
}
