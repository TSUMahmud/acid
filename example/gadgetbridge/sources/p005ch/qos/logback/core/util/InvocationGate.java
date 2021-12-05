package p005ch.qos.logback.core.util;

/* renamed from: ch.qos.logback.core.util.InvocationGate */
public class InvocationGate {
    private static final int MAX_MASK = 65535;
    private static final long thresholdForMaskIncrease = 100;
    private long invocationCounter = 0;
    private volatile long lastMaskCheck = System.currentTimeMillis();
    private volatile long mask = 15;
    private final long thresholdForMaskDecrease = 800;

    public boolean skipFurtherWork() {
        long j = this.invocationCounter;
        this.invocationCounter = 1 + j;
        return (j & this.mask) != this.mask;
    }

    public void updateMaskIfNecessary(long j) {
        long j2;
        long j3 = j - this.lastMaskCheck;
        this.lastMaskCheck = j;
        if (j3 < thresholdForMaskIncrease && this.mask < 65535) {
            j2 = (this.mask << 1) | 1;
        } else if (j3 > 800) {
            j2 = this.mask >>> 2;
        } else {
            return;
        }
        this.mask = j2;
    }
}
