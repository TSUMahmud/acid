package p005ch.qos.logback.core.status;

import java.util.Iterator;

/* renamed from: ch.qos.logback.core.status.Status */
public interface Status {
    public static final int ERROR = 2;
    public static final int INFO = 0;
    public static final int WARN = 1;

    void add(Status status);

    Long getDate();

    int getEffectiveLevel();

    int getLevel();

    String getMessage();

    Object getOrigin();

    Throwable getThrowable();

    boolean hasChildren();

    Iterator<Status> iterator();

    boolean remove(Status status);
}
