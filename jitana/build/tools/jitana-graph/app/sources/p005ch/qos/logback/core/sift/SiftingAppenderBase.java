package p005ch.qos.logback.core.sift;

import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.AppenderBase;
import p005ch.qos.logback.core.util.Duration;

/* renamed from: ch.qos.logback.core.sift.SiftingAppenderBase */
public abstract class SiftingAppenderBase<E> extends AppenderBase<E> {
    AppenderFactory<E> appenderFactory;
    protected AppenderTracker<E> appenderTracker;
    Discriminator<E> discriminator;
    int maxAppenderCount = Integer.MAX_VALUE;
    Duration timeout = new Duration(1800000);

    /* access modifiers changed from: protected */
    public void append(E e) {
        if (isStarted()) {
            String discriminatingValue = this.discriminator.getDiscriminatingValue(e);
            long timestamp = getTimestamp(e);
            Appender appender = (Appender) this.appenderTracker.getOrCreate(discriminatingValue, timestamp);
            if (eventMarksEndOfLife(e)) {
                this.appenderTracker.endOfLife(discriminatingValue);
            }
            this.appenderTracker.removeStaleComponents(timestamp);
            appender.doAppend(e);
        }
    }

    /* access modifiers changed from: protected */
    public abstract boolean eventMarksEndOfLife(E e);

    public AppenderTracker<E> getAppenderTracker() {
        return this.appenderTracker;
    }

    public Discriminator<E> getDiscriminator() {
        return this.discriminator;
    }

    public String getDiscriminatorKey() {
        Discriminator<E> discriminator2 = this.discriminator;
        if (discriminator2 != null) {
            return discriminator2.getKey();
        }
        return null;
    }

    public int getMaxAppenderCount() {
        return this.maxAppenderCount;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    /* access modifiers changed from: protected */
    public abstract long getTimestamp(E e);

    public void setAppenderFactory(AppenderFactory<E> appenderFactory2) {
        this.appenderFactory = appenderFactory2;
    }

    public void setDiscriminator(Discriminator<E> discriminator2) {
        this.discriminator = discriminator2;
    }

    public void setMaxAppenderCount(int i) {
        this.maxAppenderCount = i;
    }

    public void setTimeout(Duration duration) {
        this.timeout = duration;
    }

    public void start() {
        int i;
        if (this.discriminator == null) {
            addError("Missing discriminator. Aborting");
            i = 1;
        } else {
            i = 0;
        }
        if (!this.discriminator.isStarted()) {
            addError("Discriminator has not started successfully. Aborting");
            i++;
        }
        if (this.appenderFactory == null) {
            addError("AppenderFactory has not been set. Aborting");
            i++;
        } else {
            this.appenderTracker = new AppenderTracker<>(this.context, this.appenderFactory);
            this.appenderTracker.setMaxComponents(this.maxAppenderCount);
            this.appenderTracker.setTimeout(this.timeout.getMilliseconds());
        }
        if (i == 0) {
            super.start();
        }
    }

    public void stop() {
        for (C stop : this.appenderTracker.allComponents()) {
            stop.stop();
        }
    }
}
