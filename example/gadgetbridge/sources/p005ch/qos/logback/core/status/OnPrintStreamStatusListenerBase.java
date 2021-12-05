package p005ch.qos.logback.core.status;

import java.io.PrintStream;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.util.StatusPrinter;

/* renamed from: ch.qos.logback.core.status.OnPrintStreamStatusListenerBase */
abstract class OnPrintStreamStatusListenerBase extends ContextAwareBase implements StatusListener, LifeCycle {
    static final long DEFAULT_RETROSPECTIVE = 300;
    boolean isStarted = false;
    long retrospective = 300;

    OnPrintStreamStatusListenerBase() {
    }

    private void print(Status status) {
        StringBuilder sb = new StringBuilder();
        StatusPrinter.buildStr(sb, "", status);
        getPrintStream().print(sb);
    }

    private void retrospectivePrint() {
        if (this.context != null) {
            long currentTimeMillis = System.currentTimeMillis();
            for (Status next : this.context.getStatusManager().getCopyOfStatusList()) {
                if (currentTimeMillis - next.getDate().longValue() < this.retrospective) {
                    print(next);
                }
            }
        }
    }

    public void addStatusEvent(Status status) {
        if (this.isStarted) {
            print(status);
        }
    }

    /* access modifiers changed from: protected */
    public abstract PrintStream getPrintStream();

    public long getRetrospective() {
        return this.retrospective;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public void setRetrospective(long j) {
        this.retrospective = j;
    }

    public void start() {
        this.isStarted = true;
        if (this.retrospective > 0) {
            retrospectivePrint();
        }
    }

    public void stop() {
        this.isStarted = false;
    }
}
