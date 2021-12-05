package nodomain.freeyourgadget.gadgetbridge.util;

public class JavaExtensions {
    public static <T> T coalesce(T one, T two) {
        return one != null ? one : two;
    }
}
