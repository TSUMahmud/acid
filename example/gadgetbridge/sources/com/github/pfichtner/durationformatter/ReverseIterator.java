package com.github.pfichtner.durationformatter;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class ReverseIterator<T> implements Iterator<T> {
    private final ListIterator<T> delegate;

    public ReverseIterator(List<T> list) {
        this(list.listIterator(list.size()));
    }

    private ReverseIterator(ListIterator<T> delegate2) {
        this.delegate = delegate2;
    }

    public final boolean hasNext() {
        return this.delegate.hasPrevious();
    }

    public final T next() {
        return this.delegate.previous();
    }

    public final void remove() {
        this.delegate.remove();
    }
}
