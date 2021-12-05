package p005ch.qos.logback.core.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import p005ch.qos.logback.core.filter.Filter;

/* renamed from: ch.qos.logback.core.spi.FilterAttachableImpl */
public final class FilterAttachableImpl<E> implements FilterAttachable<E> {
    CopyOnWriteArrayList<Filter<E>> filterList = new CopyOnWriteArrayList<>();

    public void addFilter(Filter<E> filter) {
        this.filterList.add(filter);
    }

    public void clearAllFilters() {
        this.filterList.clear();
    }

    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return new ArrayList(this.filterList);
    }

    /* JADX WARNING: Removed duplicated region for block: B:3:0x000c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public p005ch.qos.logback.core.spi.FilterReply getFilterChainDecision(E r4) {
        /*
            r3 = this;
            java.util.concurrent.CopyOnWriteArrayList<ch.qos.logback.core.filter.Filter<E>> r0 = r3.filterList
            java.util.Iterator r0 = r0.iterator()
        L_0x0006:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x001f
            java.lang.Object r1 = r0.next()
            ch.qos.logback.core.filter.Filter r1 = (p005ch.qos.logback.core.filter.Filter) r1
            ch.qos.logback.core.spi.FilterReply r1 = r1.decide(r4)
            ch.qos.logback.core.spi.FilterReply r2 = p005ch.qos.logback.core.spi.FilterReply.DENY
            if (r1 == r2) goto L_0x001e
            ch.qos.logback.core.spi.FilterReply r2 = p005ch.qos.logback.core.spi.FilterReply.ACCEPT
            if (r1 != r2) goto L_0x0006
        L_0x001e:
            return r1
        L_0x001f:
            ch.qos.logback.core.spi.FilterReply r4 = p005ch.qos.logback.core.spi.FilterReply.NEUTRAL
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.spi.FilterAttachableImpl.getFilterChainDecision(java.lang.Object):ch.qos.logback.core.spi.FilterReply");
    }
}
