package org.apache.commons.lang3.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class ExceptionUtils {
    private static final String[] CAUSE_METHOD_NAMES = {"getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable"};
    static final String WRAPPED_MARKER = " [wrapped] ";

    @Deprecated
    public static String[] getDefaultCauseMethodNames() {
        return (String[]) ArrayUtils.clone((T[]) CAUSE_METHOD_NAMES);
    }

    @Deprecated
    public static Throwable getCause(Throwable throwable) {
        return getCause(throwable, (String[]) null);
    }

    @Deprecated
    public static Throwable getCause(Throwable throwable, String[] methodNames) {
        Throwable legacyCause;
        if (throwable == null) {
            return null;
        }
        if (methodNames == null) {
            Throwable cause = throwable.getCause();
            if (cause != null) {
                return cause;
            }
            methodNames = CAUSE_METHOD_NAMES;
        }
        for (String methodName : methodNames) {
            if (methodName != null && (legacyCause = getCauseUsingMethodName(throwable, methodName)) != null) {
                return legacyCause;
            }
        }
        return null;
    }

    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        if (list.size() < 2) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName, new Class[0]);
        } catch (NoSuchMethodException | SecurityException e) {
        }
        if (method == null || !Throwable.class.isAssignableFrom(method.getReturnType())) {
            return null;
        }
        try {
            return (Throwable) method.invoke(throwable, new Object[0]);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
            return null;
        }
    }

    public static int getThrowableCount(Throwable throwable) {
        return getThrowableList(throwable).size();
    }

    public static Throwable[] getThrowables(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        return (Throwable[]) list.toArray(new Throwable[list.size()]);
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }

    public static int indexOfThrowable(Throwable throwable, Class<?> clazz) {
        return indexOf(throwable, clazz, 0, false);
    }

    public static int indexOfThrowable(Throwable throwable, Class<?> clazz, int fromIndex) {
        return indexOf(throwable, clazz, fromIndex, false);
    }

    public static int indexOfType(Throwable throwable, Class<?> type) {
        return indexOf(throwable, type, 0, true);
    }

    public static int indexOfType(Throwable throwable, Class<?> type, int fromIndex) {
        return indexOf(throwable, type, fromIndex, true);
    }

    private static int indexOf(Throwable throwable, Class<?> type, int fromIndex, boolean subclass) {
        if (throwable == null || type == null) {
            return -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        Throwable[] throwables = getThrowables(throwable);
        if (fromIndex >= throwables.length) {
            return -1;
        }
        if (subclass) {
            for (int i = fromIndex; i < throwables.length; i++) {
                if (type.isAssignableFrom(throwables[i].getClass())) {
                    return i;
                }
            }
        } else {
            for (int i2 = fromIndex; i2 < throwables.length; i2++) {
                if (type.equals(throwables[i2].getClass())) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public static void printRootCauseStackTrace(Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintStream stream) {
        if (throwable != null) {
            Validate.isTrue(stream != null, "The PrintStream must not be null", new Object[0]);
            for (String element : getRootCauseStackTrace(throwable)) {
                stream.println(element);
            }
            stream.flush();
        }
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintWriter writer) {
        if (throwable != null) {
            Validate.isTrue(writer != null, "The PrintWriter must not be null", new Object[0]);
            for (String element : getRootCauseStackTrace(throwable)) {
                writer.println(element);
            }
            writer.flush();
        }
    }

    public static String[] getRootCauseStackTrace(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        Throwable[] throwables = getThrowables(throwable);
        int count = throwables.length;
        List<String> frames = new ArrayList<>();
        List<String> nextTrace = getStackFrameList(throwables[count - 1]);
        int i = count;
        while (true) {
            i--;
            if (i < 0) {
                return (String[]) frames.toArray(new String[frames.size()]);
            }
            List<String> trace = nextTrace;
            if (i != 0) {
                nextTrace = getStackFrameList(throwables[i - 1]);
                removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            } else {
                frames.add(WRAPPED_MARKER + throwables[i].toString());
            }
            frames.addAll(trace);
        }
    }

    public static void removeCommonFrames(List<String> causeFrames, List<String> wrapperFrames) {
        if (causeFrames == null || wrapperFrames == null) {
            throw new IllegalArgumentException("The List must not be null");
        }
        int causeFrameIndex = causeFrames.size() - 1;
        int wrapperFrameIndex = wrapperFrames.size() - 1;
        while (causeFrameIndex >= 0 && wrapperFrameIndex >= 0) {
            if (causeFrames.get(causeFrameIndex).equals(wrapperFrames.get(wrapperFrameIndex))) {
                causeFrames.remove(causeFrameIndex);
            }
            causeFrameIndex--;
            wrapperFrameIndex--;
        }
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    public static String[] getStackFrames(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return getStackFrames(getStackTrace(throwable));
    }

    static String[] getStackFrames(String stackTrace) {
        StringTokenizer frames = new StringTokenizer(stackTrace, System.lineSeparator());
        List<String> list = new ArrayList<>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    static List<String> getStackFrameList(Throwable t) {
        StringTokenizer frames = new StringTokenizer(getStackTrace(t), System.lineSeparator());
        List<String> list = new ArrayList<>();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            String token = frames.nextToken();
            int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().isEmpty()) {
                traceStarted = true;
                list.add(token);
            } else if (traceStarted) {
                break;
            }
        }
        return list;
    }

    public static String getMessage(Throwable th) {
        if (th == null) {
            return "";
        }
        String clsName = ClassUtils.getShortClassName(th, (String) null);
        String msg = th.getMessage();
        return clsName + ": " + StringUtils.defaultString(msg);
    }

    public static String getRootCauseMessage(Throwable th) {
        Throwable root = getRootCause(th);
        return getMessage(root == null ? th : root);
    }

    public static <R> R rethrow(Throwable throwable) {
        return typeErasure(throwable);
    }

    private static <R, T extends Throwable> R typeErasure(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public static <R> R wrapAndThrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        } else if (throwable instanceof Error) {
            throw ((Error) throwable);
        } else {
            throw new UndeclaredThrowableException(throwable);
        }
    }

    public static boolean hasCause(Throwable chain, Class<? extends Throwable> type) {
        if (chain instanceof UndeclaredThrowableException) {
            chain = chain.getCause();
        }
        return type.isInstance(chain);
    }
}
