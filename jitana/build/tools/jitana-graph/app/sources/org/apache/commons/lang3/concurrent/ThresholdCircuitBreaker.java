package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicLong;

public class ThresholdCircuitBreaker extends AbstractCircuitBreaker<Long> {
    private static final long INITIAL_COUNT = 0;
    private final long threshold;
    private final AtomicLong used = new AtomicLong(0);

    public ThresholdCircuitBreaker(long threshold2) {
        this.threshold = threshold2;
    }

    public long getThreshold() {
        return this.threshold;
    }

    public boolean checkState() throws CircuitBreakingException {
        return isOpen();
    }

    public void close() {
        super.close();
        this.used.set(0);
    }

    public boolean incrementAndCheckState(Long increment) throws CircuitBreakingException {
        if (this.threshold == 0) {
            open();
        }
        if (this.used.addAndGet(increment.longValue()) > this.threshold) {
            open();
        }
        return checkState();
    }
}
