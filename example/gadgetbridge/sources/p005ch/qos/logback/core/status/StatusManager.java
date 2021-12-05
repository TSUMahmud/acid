package p005ch.qos.logback.core.status;

import java.util.List;

/* renamed from: ch.qos.logback.core.status.StatusManager */
public interface StatusManager {
    void add(Status status);

    void add(StatusListener statusListener);

    boolean addUniquely(StatusListener statusListener, Object obj);

    void clear();

    List<Status> getCopyOfStatusList();

    List<StatusListener> getCopyOfStatusListenerList();

    int getCount();

    void remove(StatusListener statusListener);
}
