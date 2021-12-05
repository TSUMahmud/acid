package p005ch.qos.logback.core.rolling.helper;

import p005ch.qos.logback.core.pattern.DynamicConverter;

/* renamed from: ch.qos.logback.core.rolling.helper.IntegerTokenConverter */
public class IntegerTokenConverter extends DynamicConverter implements MonoTypedConverter {
    public static final String CONVERTER_KEY = "i";

    public String convert(int i) {
        return Integer.toString(i);
    }

    public String convert(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        } else if (obj instanceof Integer) {
            return convert(((Integer) obj).intValue());
        } else {
            throw new IllegalArgumentException("Cannot convert " + obj + " of type" + obj.getClass().getName());
        }
    }

    public boolean isApplicable(Object obj) {
        return obj instanceof Integer;
    }
}
