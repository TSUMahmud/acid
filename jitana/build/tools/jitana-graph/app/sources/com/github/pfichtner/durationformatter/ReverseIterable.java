package com.github.pfichtner.durationformatter;

import java.util.Iterator;
import java.util.List;

public final class ReverseIterable<T> implements Iterable<T> {
    private List<T> list;

    public ReverseIterable(List<T> list2) {
        this.list = list2;
    }

    public final Iterator<T> iterator() {
        return new ReverseIterator(this.list);
    }
}
