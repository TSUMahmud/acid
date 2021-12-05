package p005ch.qos.logback.core.recovery;

/* renamed from: ch.qos.logback.core.recovery.RecoveryCoordinator */
public class RecoveryCoordinator {
    static long BACKOFF_COEFFICIENT_MAX = 327680;
    public static final long BACKOFF_COEFFICIENT_MIN = 20;
    private static long UNSET = -1;
    private long backOffCoefficient = 20;
    private long currentTime = UNSET;
    long next = (System.currentTimeMillis() + getBackoffCoefficient());

    private long getBackoffCoefficient() {
        long j = this.backOffCoefficient;
        if (j < BACKOFF_COEFFICIENT_MAX) {
            this.backOffCoefficient = 4 * j;
        }
        return j;
    }

    private long getCurrentTime() {
        long j = this.currentTime;
        return j != UNSET ? j : System.currentTimeMillis();
    }

    public boolean isTooSoon() {
        long currentTime2 = getCurrentTime();
        if (currentTime2 <= this.next) {
            return true;
        }
        this.next = currentTime2 + getBackoffCoefficient();
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setCurrentTime(long j) {
        this.currentTime = j;
    }
}
