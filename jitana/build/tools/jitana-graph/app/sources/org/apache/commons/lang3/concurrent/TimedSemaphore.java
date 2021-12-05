package org.apache.commons.lang3.concurrent;

import com.github.mikephil.charting.utils.Utils;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

public class TimedSemaphore {
    public static final int NO_LIMIT = 0;
    private static final int THREAD_POOL_SIZE = 1;
    private int acquireCount;
    private final ScheduledExecutorService executorService;
    private int lastCallsPerPeriod;
    private int limit;
    private final boolean ownExecutor;
    private final long period;
    private long periodCount;
    private boolean shutdown;
    private ScheduledFuture<?> task;
    private long totalAcquireCount;
    private final TimeUnit unit;

    public TimedSemaphore(long timePeriod, TimeUnit timeUnit, int limit2) {
        this((ScheduledExecutorService) null, timePeriod, timeUnit, limit2);
    }

    public TimedSemaphore(ScheduledExecutorService service, long timePeriod, TimeUnit timeUnit, int limit2) {
        Validate.inclusiveBetween(1, Long.MAX_VALUE, timePeriod, "Time period must be greater than 0!");
        this.period = timePeriod;
        this.unit = timeUnit;
        if (service != null) {
            this.executorService = service;
            this.ownExecutor = false;
        } else {
            ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
            s.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            s.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            this.executorService = s;
            this.ownExecutor = true;
        }
        setLimit(limit2);
    }

    public final synchronized int getLimit() {
        return this.limit;
    }

    public final synchronized void setLimit(int limit2) {
        this.limit = limit2;
    }

    public synchronized void shutdown() {
        if (!this.shutdown) {
            if (this.ownExecutor) {
                getExecutorService().shutdownNow();
            }
            if (this.task != null) {
                this.task.cancel(false);
            }
            this.shutdown = true;
        }
    }

    public synchronized boolean isShutdown() {
        return this.shutdown;
    }

    public synchronized void acquire() throws InterruptedException {
        boolean canPass;
        prepareAcquire();
        do {
            canPass = acquirePermit();
            if (!canPass) {
                wait();
                continue;
            }
        } while (!canPass);
    }

    public synchronized boolean tryAcquire() {
        prepareAcquire();
        return acquirePermit();
    }

    public synchronized int getLastAcquiresPerPeriod() {
        return this.lastCallsPerPeriod;
    }

    public synchronized int getAcquireCount() {
        return this.acquireCount;
    }

    public synchronized int getAvailablePermits() {
        return getLimit() - getAcquireCount();
    }

    public synchronized double getAverageCallsPerPeriod() {
        double d;
        if (this.periodCount == 0) {
            d = Utils.DOUBLE_EPSILON;
        } else {
            double d2 = (double) this.totalAcquireCount;
            double d3 = (double) this.periodCount;
            Double.isNaN(d2);
            Double.isNaN(d3);
            d = d2 / d3;
        }
        return d;
    }

    public long getPeriod() {
        return this.period;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    /* access modifiers changed from: protected */
    public ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }

    /* access modifiers changed from: protected */
    public ScheduledFuture<?> startTimer() {
        return getExecutorService().scheduleAtFixedRate(new Runnable() {
            public void run() {
                TimedSemaphore.this.endOfPeriod();
            }
        }, getPeriod(), getPeriod(), getUnit());
    }

    /* access modifiers changed from: package-private */
    public synchronized void endOfPeriod() {
        this.lastCallsPerPeriod = this.acquireCount;
        this.totalAcquireCount += (long) this.acquireCount;
        this.periodCount++;
        this.acquireCount = 0;
        notifyAll();
    }

    private void prepareAcquire() {
        if (isShutdown()) {
            throw new IllegalStateException("TimedSemaphore is shut down!");
        } else if (this.task == null) {
            this.task = startTimer();
        }
    }

    private boolean acquirePermit() {
        if (getLimit() > 0 && this.acquireCount >= getLimit()) {
            return false;
        }
        this.acquireCount++;
        return true;
    }
}
