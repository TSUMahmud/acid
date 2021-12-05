package p005ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.List;

/* renamed from: ch.qos.logback.core.status.StatusListenerAsList */
public class StatusListenerAsList implements StatusListener {
    List<Status> statusList = new ArrayList();

    public void addStatusEvent(Status status) {
        this.statusList.add(status);
    }

    public List<Status> getStatusList() {
        return this.statusList;
    }
}
