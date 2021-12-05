package p005ch.qos.logback.core.status;

import java.io.PrintStream;
import p005ch.qos.logback.core.Context;

/* renamed from: ch.qos.logback.core.status.OnConsoleStatusListener */
public class OnConsoleStatusListener extends OnPrintStreamStatusListenerBase {
    public static void addNewInstanceToContext(Context context) {
        OnConsoleStatusListener onConsoleStatusListener = new OnConsoleStatusListener();
        onConsoleStatusListener.setContext(context);
        if (context.getStatusManager().addUniquely(onConsoleStatusListener, context)) {
            onConsoleStatusListener.start();
        }
    }

    public /* bridge */ /* synthetic */ void addStatusEvent(Status status) {
        super.addStatusEvent(status);
    }

    /* access modifiers changed from: protected */
    public PrintStream getPrintStream() {
        return System.out;
    }

    public /* bridge */ /* synthetic */ long getRetrospective() {
        return super.getRetrospective();
    }

    public /* bridge */ /* synthetic */ boolean isStarted() {
        return super.isStarted();
    }

    public /* bridge */ /* synthetic */ void setRetrospective(long j) {
        super.setRetrospective(j);
    }

    public /* bridge */ /* synthetic */ void start() {
        super.start();
    }

    public /* bridge */ /* synthetic */ void stop() {
        super.stop();
    }
}
