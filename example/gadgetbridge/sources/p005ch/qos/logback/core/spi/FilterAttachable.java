package p005ch.qos.logback.core.spi;

import java.util.List;
import p005ch.qos.logback.core.filter.Filter;

/* renamed from: ch.qos.logback.core.spi.FilterAttachable */
public interface FilterAttachable<E> {
    void addFilter(Filter<E> filter);

    void clearAllFilters();

    List<Filter<E>> getCopyOfAttachedFiltersList();

    FilterReply getFilterChainDecision(E e);
}
