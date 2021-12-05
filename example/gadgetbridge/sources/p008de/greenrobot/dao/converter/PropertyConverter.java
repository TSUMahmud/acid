package p008de.greenrobot.dao.converter;

/* renamed from: de.greenrobot.dao.converter.PropertyConverter */
public interface PropertyConverter<P, D> {
    D convertToDatabaseValue(P p);

    P convertToEntityProperty(D d);
}
