package org.apache.commons.lang3.concurrent;

import java.lang.Thread;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.Validate;

public class BasicThreadFactory implements ThreadFactory {
    private final Boolean daemonFlag;
    private final String namingPattern;
    private final Integer priority;
    private final AtomicLong threadCounter;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private final ThreadFactory wrappedFactory;

    private BasicThreadFactory(Builder builder) {
        if (builder.wrappedFactory == null) {
            this.wrappedFactory = Executors.defaultThreadFactory();
        } else {
            this.wrappedFactory = builder.wrappedFactory;
        }
        this.namingPattern = builder.namingPattern;
        this.priority = builder.priority;
        this.daemonFlag = builder.daemonFlag;
        this.uncaughtExceptionHandler = builder.exceptionHandler;
        this.threadCounter = new AtomicLong();
    }

    public final ThreadFactory getWrappedFactory() {
        return this.wrappedFactory;
    }

    public final String getNamingPattern() {
        return this.namingPattern;
    }

    public final Boolean getDaemonFlag() {
        return this.daemonFlag;
    }

    public final Integer getPriority() {
        return this.priority;
    }

    public final Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.uncaughtExceptionHandler;
    }

    public long getThreadCount() {
        return this.threadCounter.get();
    }

    public Thread newThread(Runnable r) {
        Thread t = getWrappedFactory().newThread(r);
        initializeThread(t);
        return t;
    }

    private void initializeThread(Thread t) {
        if (getNamingPattern() != null) {
            Long count = Long.valueOf(this.threadCounter.incrementAndGet());
            t.setName(String.format(getNamingPattern(), new Object[]{count}));
        }
        if (getUncaughtExceptionHandler() != null) {
            t.setUncaughtExceptionHandler(getUncaughtExceptionHandler());
        }
        if (getPriority() != null) {
            t.setPriority(getPriority().intValue());
        }
        if (getDaemonFlag() != null) {
            t.setDaemon(getDaemonFlag().booleanValue());
        }
    }

    public static class Builder implements org.apache.commons.lang3.builder.Builder<BasicThreadFactory> {
        /* access modifiers changed from: private */
        public Boolean daemonFlag;
        /* access modifiers changed from: private */
        public Thread.UncaughtExceptionHandler exceptionHandler;
        /* access modifiers changed from: private */
        public String namingPattern;
        /* access modifiers changed from: private */
        public Integer priority;
        /* access modifiers changed from: private */
        public ThreadFactory wrappedFactory;

        public Builder wrappedFactory(ThreadFactory factory) {
            Validate.notNull(factory, "Wrapped ThreadFactory must not be null!", new Object[0]);
            this.wrappedFactory = factory;
            return this;
        }

        public Builder namingPattern(String pattern) {
            Validate.notNull(pattern, "Naming pattern must not be null!", new Object[0]);
            this.namingPattern = pattern;
            return this;
        }

        public Builder daemon(boolean f) {
            this.daemonFlag = Boolean.valueOf(f);
            return this;
        }

        public Builder priority(int prio) {
            this.priority = Integer.valueOf(prio);
            return this;
        }

        public Builder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
            Validate.notNull(handler, "Uncaught exception handler must not be null!", new Object[0]);
            this.exceptionHandler = handler;
            return this;
        }

        public void reset() {
            this.wrappedFactory = null;
            this.exceptionHandler = null;
            this.namingPattern = null;
            this.priority = null;
            this.daemonFlag = null;
        }

        public BasicThreadFactory build() {
            BasicThreadFactory factory = new BasicThreadFactory(this);
            reset();
            return factory;
        }
    }
}
