package p005ch.qos.logback.classic.spi;

/* renamed from: ch.qos.logback.classic.spi.EventArgUtil */
public class EventArgUtil {
    public static Object[] arrangeArguments(Object[] objArr) {
        return objArr;
    }

    public static final Throwable extractThrowable(Object[] objArr) {
        if (!(objArr == null || objArr.length == 0)) {
            Throwable th = objArr[objArr.length - 1];
            if (th instanceof Throwable) {
                return th;
            }
        }
        return null;
    }

    public static boolean successfulExtraction(Throwable th) {
        return th != null;
    }

    public static Object[] trimmedCopy(Object[] objArr) {
        if (objArr == null || objArr.length == 0) {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
        int length = objArr.length - 1;
        Object[] objArr2 = new Object[length];
        System.arraycopy(objArr, 0, objArr2, 0, length);
        return objArr2;
    }
}
