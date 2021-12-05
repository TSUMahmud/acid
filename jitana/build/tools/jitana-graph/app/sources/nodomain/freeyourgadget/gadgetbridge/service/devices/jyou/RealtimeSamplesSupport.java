package nodomain.freeyourgadget.gadgetbridge.service.devices.jyou;

import java.util.Timer;
import java.util.TimerTask;

public abstract class RealtimeSamplesSupport {
    private final long delay;
    protected int heartrateBpm;
    private int lastSteps;
    private final long period;
    private Timer realtimeStorageTimer;
    protected int steps;

    /* access modifiers changed from: protected */
    public abstract void doCurrentSample();

    public RealtimeSamplesSupport(long delay2, long period2) {
        this.delay = delay2;
        this.period = period2;
    }

    public synchronized void start() {
        if (!isRunning()) {
            this.realtimeStorageTimer = new Timer("JYou Realtime Storage Timer");
            this.realtimeStorageTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    RealtimeSamplesSupport.this.triggerCurrentSample();
                }
            }, this.delay, this.period);
        }
    }

    public synchronized void stop() {
        if (this.realtimeStorageTimer != null) {
            this.realtimeStorageTimer.cancel();
            this.realtimeStorageTimer.purge();
            this.realtimeStorageTimer = null;
        }
    }

    public synchronized boolean isRunning() {
        return this.realtimeStorageTimer != null;
    }

    public synchronized void setSteps(int stepsPerMinute) {
        this.steps = stepsPerMinute;
    }

    public synchronized int getSteps() {
        if (this.steps == -1) {
            return -1;
        }
        if (this.lastSteps == 0) {
            return -1;
        }
        int delta = this.steps - this.lastSteps;
        if (delta < 0) {
            return 0;
        }
        return delta;
    }

    public void setHeartrateBpm(int hrBpm) {
        this.heartrateBpm = hrBpm;
    }

    public int getHeartrateBpm() {
        return this.heartrateBpm;
    }

    public void triggerCurrentSample() {
        doCurrentSample();
        resetCurrentValues();
    }

    /* access modifiers changed from: protected */
    public synchronized void resetCurrentValues() {
        if (this.steps >= this.lastSteps) {
            this.lastSteps = this.steps;
        }
        this.steps = -1;
        this.heartrateBpm = -1;
    }
}
