package p005ch.qos.logback.core.pattern;

/* renamed from: ch.qos.logback.core.pattern.IdentityCompositeConverter */
public class IdentityCompositeConverter<E> extends CompositeConverter<E> {
    /* access modifiers changed from: protected */
    public String transform(E e, String str) {
        return str;
    }
}
