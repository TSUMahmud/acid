package nodomain.freeyourgadget.gadgetbridge.devices;

import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;

public interface SampleProvider<T extends AbstractActivitySample> {
    public static final int PROVIDER_PEBBLE_HEALTH = 4;
    public static final int PROVIDER_PEBBLE_MISFIT = 3;
    public static final int PROVIDER_PEBBLE_MORPHEUZ = 1;

    void addGBActivitySample(T t);

    void addGBActivitySamples(T[] tArr);

    T createActivitySample();

    List<T> getActivitySamples(int i, int i2);

    List<T> getAllActivitySamples(int i, int i2);

    T getLatestActivitySample();

    List<T> getSleepSamples(int i, int i2);

    float normalizeIntensity(int i);

    int normalizeType(int i);

    int toRawActivityKind(int i);
}
