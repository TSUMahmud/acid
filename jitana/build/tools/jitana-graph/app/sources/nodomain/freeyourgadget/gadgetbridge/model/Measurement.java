package nodomain.freeyourgadget.gadgetbridge.model;

public class Measurement {
    private final long timestamp;
    private final int value;

    public Measurement(int value2, long timestamp2) {
        this.value = value2;
        this.timestamp = timestamp2;
    }

    public int getValue() {
        return this.value;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public int hashCode() {
        return (int) (((long) (this.value ^ 71)) ^ this.timestamp);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Measurement)) {
            return super.equals(o);
        }
        Measurement m = (Measurement) o;
        return this.timestamp == m.timestamp && this.value == m.value;
    }
}
