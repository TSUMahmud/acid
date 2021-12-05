package p005ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.core.status.StatusUtil */
public class StatusUtil {

    /* renamed from: sm */
    StatusManager f60sm;

    public StatusUtil(Context context) {
        this.f60sm = context.getStatusManager();
    }

    public StatusUtil(StatusManager statusManager) {
        this.f60sm = statusManager;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r1 = r1.getCopyOfStatusListenerList();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean contextHasStatusListener(p005ch.qos.logback.core.Context r1) {
        /*
            ch.qos.logback.core.status.StatusManager r1 = r1.getStatusManager()
            r0 = 0
            if (r1 != 0) goto L_0x0008
            return r0
        L_0x0008:
            java.util.List r1 = r1.getCopyOfStatusListenerList()
            if (r1 == 0) goto L_0x0017
            int r1 = r1.size()
            if (r1 != 0) goto L_0x0015
            goto L_0x0017
        L_0x0015:
            r1 = 1
            return r1
        L_0x0017:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.status.StatusUtil.contextHasStatusListener(ch.qos.logback.core.Context):boolean");
    }

    public static List<Status> filterStatusListByTimeThreshold(List<Status> list, long j) {
        ArrayList arrayList = new ArrayList();
        for (Status next : list) {
            if (next.getDate().longValue() >= j) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void addError(Object obj, String str, Throwable th) {
        addStatus(new ErrorStatus(str, obj, th));
    }

    public void addInfo(Object obj, String str) {
        addStatus(new InfoStatus(str, obj));
    }

    public void addStatus(Status status) {
        StatusManager statusManager = this.f60sm;
        if (statusManager != null) {
            statusManager.add(status);
        }
    }

    public void addWarn(Object obj, String str) {
        addStatus(new WarnStatus(str, obj));
    }

    public boolean containsException(Class<?> cls) {
        for (Status throwable : this.f60sm.getCopyOfStatusList()) {
            Throwable throwable2 = throwable.getThrowable();
            if (throwable2 != null && throwable2.getClass().getName().equals(cls.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsMatch(int i, String str) {
        return containsMatch(0, i, str);
    }

    public boolean containsMatch(long j, int i, String str) {
        List<Status> filterStatusListByTimeThreshold = filterStatusListByTimeThreshold(this.f60sm.getCopyOfStatusList(), j);
        Pattern compile = Pattern.compile(str);
        for (Status next : filterStatusListByTimeThreshold) {
            if (i == next.getLevel() && compile.matcher(next.getMessage()).lookingAt()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsMatch(String str) {
        Pattern compile = Pattern.compile(str);
        for (Status message : this.f60sm.getCopyOfStatusList()) {
            if (compile.matcher(message.getMessage()).lookingAt()) {
                return true;
            }
        }
        return false;
    }

    public int getHighestLevel(long j) {
        int i = 0;
        for (Status next : filterStatusListByTimeThreshold(this.f60sm.getCopyOfStatusList(), j)) {
            if (next.getLevel() > i) {
                i = next.getLevel();
            }
        }
        return i;
    }

    public boolean hasXMLParsingErrors(long j) {
        return containsMatch(j, 2, CoreConstants.XML_PARSING);
    }

    public boolean isErrorFree(long j) {
        return 2 > getHighestLevel(j);
    }

    public int matchCount(String str) {
        Pattern compile = Pattern.compile(str);
        int i = 0;
        for (Status message : this.f60sm.getCopyOfStatusList()) {
            if (compile.matcher(message.getMessage()).lookingAt()) {
                i++;
            }
        }
        return i;
    }

    public boolean noXMLParsingErrorsOccurred(long j) {
        return !hasXMLParsingErrors(j);
    }

    public long timeOfLastReset() {
        List<Status> copyOfStatusList = this.f60sm.getCopyOfStatusList();
        if (copyOfStatusList == null) {
            return -1;
        }
        for (int size = copyOfStatusList.size() - 1; size >= 0; size--) {
            Status status = copyOfStatusList.get(size);
            if (CoreConstants.RESET_MSG_PREFIX.equals(status.getMessage())) {
                return status.getDate().longValue();
            }
        }
        return -1;
    }
}
