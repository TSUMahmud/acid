package nodomain.freeyourgadget.gadgetbridge.entities;

public abstract class AbstractPebbleMisfitActivitySample extends AbstractActivitySample {
    private transient int activityKind = 0;
    private transient int intensity = 0;
    private transient int steps = 0;

    public abstract int getRawPebbleMisfitSample();

    private void calculate() {
        int sample = getRawPebbleMisfitSample();
        if ((33791 & sample) != 1 || (65280 & sample) > 18432) {
            if ((sample & 1) == 0) {
                this.steps = sample & 254;
            } else {
                this.steps = sample & 14;
            }
            this.intensity = this.steps;
            this.activityKind = 1;
            return;
        }
        this.intensity = (sample & 31744) >>> 10;
        if (this.intensity <= 13) {
            this.activityKind = 4;
        }
    }

    public int getSteps() {
        calculate();
        return this.steps;
    }

    public int getKind() {
        calculate();
        return this.activityKind;
    }

    public int getRawIntensity() {
        calculate();
        return this.intensity;
    }
}
