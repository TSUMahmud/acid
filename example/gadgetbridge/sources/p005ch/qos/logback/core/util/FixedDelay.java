package p005ch.qos.logback.core.util;

/* renamed from: ch.qos.logback.core.util.FixedDelay */
public class FixedDelay implements DelayStrategy {
    private long nextDelay;
    private final long subsequentDelay;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FixedDelay(int r3) {
        /*
            r2 = this;
            long r0 = (long) r3
            r2.<init>(r0, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.util.FixedDelay.<init>(int):void");
    }

    public FixedDelay(long j, long j2) {
        new String();
        this.nextDelay = j;
        this.subsequentDelay = j2;
    }

    public long nextDelay() {
        long j = this.nextDelay;
        this.nextDelay = this.subsequentDelay;
        return j;
    }
}
