package p005ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: ch.qos.logback.core.status.StatusBase */
public abstract class StatusBase implements Status {
    private static final List<Status> EMPTY_LIST = new ArrayList(0);
    List<Status> childrenList;
    long date;
    int level;
    final String message;
    final Object origin;
    Throwable throwable;

    StatusBase(int i, String str, Object obj) {
        this(i, str, obj, (Throwable) null);
    }

    StatusBase(int i, String str, Object obj, Throwable th) {
        this.level = i;
        this.message = str;
        this.origin = obj;
        this.throwable = th;
        this.date = System.currentTimeMillis();
    }

    public synchronized void add(Status status) {
        if (status != null) {
            if (this.childrenList == null) {
                this.childrenList = new ArrayList();
            }
            this.childrenList.add(status);
        } else {
            throw new NullPointerException("Null values are not valid Status.");
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StatusBase statusBase = (StatusBase) obj;
        if (this.level != statusBase.level) {
            return false;
        }
        String str = this.message;
        String str2 = statusBase.message;
        if (str == null) {
            if (str2 != null) {
                return false;
            }
        } else if (!str.equals(str2)) {
            return false;
        }
        return true;
    }

    public Long getDate() {
        return Long.valueOf(this.date);
    }

    public synchronized int getEffectiveLevel() {
        int i;
        i = this.level;
        Iterator<Status> it = iterator();
        while (it.hasNext()) {
            int effectiveLevel = it.next().getEffectiveLevel();
            if (effectiveLevel > i) {
                i = effectiveLevel;
            }
        }
        return i;
    }

    public int getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getOrigin() {
        return this.origin;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public synchronized boolean hasChildren() {
        return this.childrenList != null && this.childrenList.size() > 0;
    }

    public int hashCode() {
        int i = (this.level + 31) * 31;
        String str = this.message;
        return i + (str == null ? 0 : str.hashCode());
    }

    public synchronized Iterator<Status> iterator() {
        if (this.childrenList != null) {
            return this.childrenList.iterator();
        }
        return EMPTY_LIST.iterator();
    }

    public synchronized boolean remove(Status status) {
        if (this.childrenList == null) {
            return false;
        }
        return this.childrenList.remove(status);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x003e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String toString() {
        /*
            r3 = this;
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            int r1 = r3.getEffectiveLevel()
            if (r1 == 0) goto L_0x0018
            r2 = 1
            if (r1 == r2) goto L_0x0015
            r2 = 2
            if (r1 == r2) goto L_0x0012
            goto L_0x001d
        L_0x0012:
            java.lang.String r1 = "ERROR"
            goto L_0x001a
        L_0x0015:
            java.lang.String r1 = "WARN"
            goto L_0x001a
        L_0x0018:
            java.lang.String r1 = "INFO"
        L_0x001a:
            r0.append(r1)
        L_0x001d:
            java.lang.Object r1 = r3.origin
            if (r1 == 0) goto L_0x0030
            java.lang.String r1 = " in "
            r0.append(r1)
            java.lang.Object r1 = r3.origin
            r0.append(r1)
            java.lang.String r1 = " -"
            r0.append(r1)
        L_0x0030:
            java.lang.String r1 = " "
            r0.append(r1)
            java.lang.String r2 = r3.message
            r0.append(r2)
            java.lang.Throwable r2 = r3.throwable
            if (r2 == 0) goto L_0x0046
            r0.append(r1)
            java.lang.Throwable r1 = r3.throwable
            r0.append(r1)
        L_0x0046:
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.status.StatusBase.toString():java.lang.String");
    }
}
