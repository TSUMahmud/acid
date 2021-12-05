package nodomain.freeyourgadget.gadgetbridge.entities;

public abstract class AbstractPebbleMorpheuzActivitySample extends AbstractActivitySample {
    public int getKind() {
        int rawIntensity = getRawIntensity();
        if (rawIntensity <= 120) {
            return 4;
        }
        if (rawIntensity <= 1000) {
            return 2;
        }
        return 1;
    }
}
