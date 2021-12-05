package nodomain.freeyourgadget.gadgetbridge.entities;

public abstract class AbstractPebbleHealthActivitySample extends AbstractActivitySample {
    private transient int rawActivityKind = 0;

    public abstract byte[] getRawPebbleHealthData();

    public int getRawKind() {
        return this.rawActivityKind;
    }

    public void setRawKind(int kind) {
        this.rawActivityKind = kind;
    }
}
