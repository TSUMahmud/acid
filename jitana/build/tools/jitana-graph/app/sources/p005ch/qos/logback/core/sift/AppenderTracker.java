package p005ch.qos.logback.core.sift;

import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.helpers.NOPAppender;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.spi.AbstractComponentTracker;
import p005ch.qos.logback.core.spi.ContextAwareImpl;

/* renamed from: ch.qos.logback.core.sift.AppenderTracker */
public class AppenderTracker<E> extends AbstractComponentTracker<Appender<E>> {
    final AppenderFactory<E> appenderFactory;
    final Context context;
    final ContextAwareImpl contextAware;
    int nopaWarningCount = 0;

    public AppenderTracker(Context context2, AppenderFactory<E> appenderFactory2) {
        this.context = context2;
        this.appenderFactory = appenderFactory2;
        this.contextAware = new ContextAwareImpl(context2, this);
    }

    private NOPAppender<E> buildNOPAppender(String str) {
        int i = this.nopaWarningCount;
        if (i < 4) {
            this.nopaWarningCount = i + 1;
            ContextAwareImpl contextAwareImpl = this.contextAware;
            contextAwareImpl.addError("Building NOPAppender for discriminating value [" + str + "]");
        }
        NOPAppender<E> nOPAppender = new NOPAppender<>();
        nOPAppender.setContext(this.context);
        nOPAppender.start();
        return nOPAppender;
    }

    /* access modifiers changed from: protected */
    public Appender<E> buildComponent(String str) {
        Appender<E> appender;
        try {
            appender = this.appenderFactory.buildAppender(this.context, str);
        } catch (JoranException e) {
            ContextAwareImpl contextAwareImpl = this.contextAware;
            contextAwareImpl.addError("Error while building appender with discriminating value [" + str + "]");
            appender = null;
        }
        return appender == null ? buildNOPAppender(str) : appender;
    }

    /* access modifiers changed from: protected */
    public boolean isComponentStale(Appender<E> appender) {
        return !appender.isStarted();
    }

    /* access modifiers changed from: protected */
    public void processPriorToRemoval(Appender<E> appender) {
        appender.stop();
    }
}
