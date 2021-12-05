package p005ch.qos.logback.core.sift;

/* renamed from: ch.qos.logback.core.sift.DefaultDiscriminator */
public class DefaultDiscriminator<E> extends AbstractDiscriminator<E> {
    public static final String DEFAULT = "default";

    public String getDiscriminatingValue(E e) {
        return "default";
    }

    public String getKey() {
        return "default";
    }
}
