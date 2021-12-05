package p005ch.qos.logback.core;

import java.util.List;
import p005ch.qos.logback.core.filter.Filter;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.FilterAttachableImpl;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.core.AppenderBase */
public abstract class AppenderBase<E> extends ContextAwareBase implements Appender<E> {
    static final int ALLOWED_REPEATS = 5;
    private int exceptionCount = 0;
    private FilterAttachableImpl<E> fai = new FilterAttachableImpl<>();
    private boolean guard = false;
    protected String name;
    protected boolean started = false;
    private int statusRepeatCount = 0;

    public void addFilter(Filter<E> filter) {
        this.fai.addFilter(filter);
    }

    /* access modifiers changed from: protected */
    public abstract void append(E e);

    public void clearAllFilters() {
        this.fai.clearAllFilters();
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:13:0x0038=Splitter:B:13:0x0038, B:26:0x004b=Splitter:B:26:0x004b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void doAppend(E r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.guard     // Catch:{ all -> 0x007a }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r4)
            return
        L_0x0007:
            r0 = 5
            r1 = 1
            r2 = 0
            r4.guard = r1     // Catch:{ Exception -> 0x0050 }
            boolean r1 = r4.started     // Catch:{ Exception -> 0x0050 }
            if (r1 != 0) goto L_0x003c
            int r5 = r4.statusRepeatCount     // Catch:{ Exception -> 0x0050 }
            int r1 = r5 + 1
            r4.statusRepeatCount = r1     // Catch:{ Exception -> 0x0050 }
            if (r5 >= r0) goto L_0x0038
            ch.qos.logback.core.status.WarnStatus r5 = new ch.qos.logback.core.status.WarnStatus     // Catch:{ Exception -> 0x0050 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0050 }
            r1.<init>()     // Catch:{ Exception -> 0x0050 }
            java.lang.String r3 = "Attempted to append to non started appender ["
            r1.append(r3)     // Catch:{ Exception -> 0x0050 }
            java.lang.String r3 = r4.name     // Catch:{ Exception -> 0x0050 }
            r1.append(r3)     // Catch:{ Exception -> 0x0050 }
            java.lang.String r3 = "]."
            r1.append(r3)     // Catch:{ Exception -> 0x0050 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0050 }
            r5.<init>(r1, r4)     // Catch:{ Exception -> 0x0050 }
            r4.addStatus(r5)     // Catch:{ Exception -> 0x0050 }
        L_0x0038:
            r4.guard = r2     // Catch:{ all -> 0x007a }
            monitor-exit(r4)
            return
        L_0x003c:
            ch.qos.logback.core.spi.FilterReply r1 = r4.getFilterChainDecision(r5)     // Catch:{ Exception -> 0x0050 }
            ch.qos.logback.core.spi.FilterReply r3 = p005ch.qos.logback.core.spi.FilterReply.DENY     // Catch:{ Exception -> 0x0050 }
            if (r1 != r3) goto L_0x0048
            r4.guard = r2     // Catch:{ all -> 0x007a }
            monitor-exit(r4)
            return
        L_0x0048:
            r4.append(r5)     // Catch:{ Exception -> 0x0050 }
        L_0x004b:
            r4.guard = r2     // Catch:{ all -> 0x007a }
            goto L_0x0075
        L_0x004e:
            r5 = move-exception
            goto L_0x0077
        L_0x0050:
            r5 = move-exception
            int r1 = r4.exceptionCount     // Catch:{ all -> 0x004e }
            int r3 = r1 + 1
            r4.exceptionCount = r3     // Catch:{ all -> 0x004e }
            if (r1 >= r0) goto L_0x004b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x004e }
            r0.<init>()     // Catch:{ all -> 0x004e }
            java.lang.String r1 = "Appender ["
            r0.append(r1)     // Catch:{ all -> 0x004e }
            java.lang.String r1 = r4.name     // Catch:{ all -> 0x004e }
            r0.append(r1)     // Catch:{ all -> 0x004e }
            java.lang.String r1 = "] failed to append."
            r0.append(r1)     // Catch:{ all -> 0x004e }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x004e }
            r4.addError(r0, r5)     // Catch:{ all -> 0x004e }
            goto L_0x004b
        L_0x0075:
            monitor-exit(r4)
            return
        L_0x0077:
            r4.guard = r2     // Catch:{ all -> 0x007a }
            throw r5     // Catch:{ all -> 0x007a }
        L_0x007a:
            r5 = move-exception
            monitor-exit(r4)
            goto L_0x007e
        L_0x007d:
            throw r5
        L_0x007e:
            goto L_0x007d
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.AppenderBase.doAppend(java.lang.Object):void");
    }

    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return this.fai.getCopyOfAttachedFiltersList();
    }

    public FilterReply getFilterChainDecision(E e) {
        return this.fai.getFilterChainDecision(e);
    }

    public String getName() {
        return this.name;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
    }

    public String toString() {
        return getClass().getName() + "[" + this.name + "]";
    }
}
