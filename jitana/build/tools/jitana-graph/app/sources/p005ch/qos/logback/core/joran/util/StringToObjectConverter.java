package p005ch.qos.logback.core.joran.util;

import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.spi.ContextAware;

/* renamed from: ch.qos.logback.core.joran.util.StringToObjectConverter */
public class StringToObjectConverter {
    private static final Class<?>[] STING_CLASS_PARAMETER = {String.class};

    public static boolean canBeBuiltFromSimpleString(Class<?> cls) {
        Package packageR = cls.getPackage();
        if (cls.isPrimitive()) {
            return true;
        }
        return (packageR != null && "java.lang".equals(packageR.getName())) || followsTheValueOfConvention(cls) || cls.isEnum() || isOfTypeCharset(cls);
    }

    public static Object convertArg(ContextAware contextAware, String str, Class<?> cls) {
        if (str == null) {
            return null;
        }
        String trim = str.trim();
        if (String.class.isAssignableFrom(cls)) {
            return trim;
        }
        if (Integer.TYPE.isAssignableFrom(cls)) {
            return new Integer(trim);
        }
        if (Long.TYPE.isAssignableFrom(cls)) {
            return new Long(trim);
        }
        if (Float.TYPE.isAssignableFrom(cls)) {
            return new Float(trim);
        }
        if (Double.TYPE.isAssignableFrom(cls)) {
            return new Double(trim);
        }
        if (Boolean.TYPE.isAssignableFrom(cls)) {
            if ("true".equalsIgnoreCase(trim)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(trim)) {
                return Boolean.FALSE;
            }
        } else if (cls.isEnum()) {
            return convertToEnum(contextAware, trim, cls);
        } else {
            if (followsTheValueOfConvention(cls)) {
                return convertByValueOfMethod(contextAware, cls, trim);
            }
            if (isOfTypeCharset(cls)) {
                return convertToCharset(contextAware, str);
            }
        }
        return null;
    }

    private static Object convertByValueOfMethod(ContextAware contextAware, Class<?> cls, String str) {
        try {
            return cls.getMethod(CoreConstants.VALUE_OF, STING_CLASS_PARAMETER).invoke((Object) null, new Object[]{str});
        } catch (Exception e) {
            contextAware.addError("Failed to invoke valueOf{} method in class [" + cls.getName() + "] with value [" + str + "]");
            return null;
        }
    }

    private static Charset convertToCharset(ContextAware contextAware, String str) {
        try {
            return Charset.forName(str);
        } catch (UnsupportedCharsetException e) {
            contextAware.addError("Failed to get charset [" + str + "]", e);
            return null;
        }
    }

    private static Object convertToEnum(ContextAware contextAware, String str, Class<? extends Enum> cls) {
        return Enum.valueOf(cls, str);
    }

    private static boolean followsTheValueOfConvention(Class<?> cls) {
        try {
            return Modifier.isStatic(cls.getMethod(CoreConstants.VALUE_OF, STING_CLASS_PARAMETER).getModifiers());
        } catch (NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    private static boolean isOfTypeCharset(Class<?> cls) {
        return Charset.class.isAssignableFrom(cls);
    }

    /* access modifiers changed from: package-private */
    public boolean isBuildableFromSimpleString() {
        return false;
    }
}
