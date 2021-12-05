package p008de.greenrobot.dao.query;

import android.database.Cursor;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;
import p008de.greenrobot.dao.DaoException;
import p008de.greenrobot.dao.InternalQueryDaoAccess;

/* renamed from: de.greenrobot.dao.query.LazyList */
public class LazyList<E> implements List<E>, Closeable {
    private final Cursor cursor;
    private final InternalQueryDaoAccess<E> daoAccess;
    private final List<E> entities;
    private volatile int loadedCount;
    private final ReentrantLock lock;
    /* access modifiers changed from: private */
    public final int size;

    /* renamed from: de.greenrobot.dao.query.LazyList$LazyIterator */
    protected class LazyIterator implements CloseableListIterator<E> {
        private final boolean closeWhenDone;
        private int index;

        public LazyIterator(int startLocation, boolean closeWhenDone2) {
            this.index = startLocation;
            this.closeWhenDone = closeWhenDone2;
        }

        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        public boolean hasPrevious() {
            return this.index > 0;
        }

        public int nextIndex() {
            return this.index;
        }

        public E previous() {
            int i = this.index;
            if (i > 0) {
                this.index = i - 1;
                return LazyList.this.get(this.index);
            }
            throw new NoSuchElementException();
        }

        public int previousIndex() {
            return this.index - 1;
        }

        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return this.index < LazyList.this.size;
        }

        public E next() {
            if (this.index < LazyList.this.size) {
                E entity = LazyList.this.get(this.index);
                this.index++;
                if (this.index == LazyList.this.size && this.closeWhenDone) {
                    close();
                }
                return entity;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void close() {
            LazyList.this.close();
        }
    }

    LazyList(InternalQueryDaoAccess<E> daoAccess2, Cursor cursor2, boolean cacheEntities) {
        this.cursor = cursor2;
        this.daoAccess = daoAccess2;
        this.size = cursor2.getCount();
        if (cacheEntities) {
            this.entities = new ArrayList(this.size);
            for (int i = 0; i < this.size; i++) {
                this.entities.add((Object) null);
            }
        } else {
            this.entities = null;
        }
        if (this.size == 0) {
            cursor2.close();
        }
        this.lock = new ReentrantLock();
    }

    public void loadRemaining() {
        checkCached();
        int size2 = this.entities.size();
        for (int i = 0; i < size2; i++) {
            get(i);
        }
    }

    /* access modifiers changed from: protected */
    public void checkCached() {
        if (this.entities == null) {
            throw new DaoException("This operation only works with cached lazy lists");
        }
    }

    public E peak(int location) {
        List<E> list = this.entities;
        if (list != null) {
            return list.get(location);
        }
        return null;
    }

    public void close() {
        this.cursor.close();
    }

    public boolean isClosed() {
        return this.cursor.isClosed();
    }

    public int getLoadedCount() {
        return this.loadedCount;
    }

    public boolean isLoadedCompletely() {
        return this.loadedCount == this.size;
    }

    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    public void add(int location, E e) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int arg0, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object object) {
        loadRemaining();
        return this.entities.contains(object);
    }

    public boolean containsAll(Collection<?> collection) {
        loadRemaining();
        return this.entities.containsAll(collection);
    }

    public E get(int location) {
        List<E> list = this.entities;
        if (list != null) {
            E entity = list.get(location);
            if (entity == null) {
                this.lock.lock();
                try {
                    entity = this.entities.get(location);
                    if (entity == null) {
                        entity = loadEntity(location);
                        this.entities.set(location, entity);
                        this.loadedCount++;
                        if (this.loadedCount == this.size) {
                            this.cursor.close();
                        }
                    }
                } finally {
                    this.lock.unlock();
                }
            }
            return entity;
        }
        this.lock.lock();
        try {
            return loadEntity(location);
        } finally {
            this.lock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public E loadEntity(int location) {
        if (this.cursor.moveToPosition(location)) {
            E entity = this.daoAccess.loadCurrent(this.cursor, 0, true);
            if (entity != null) {
                return entity;
            }
            throw new DaoException("Loading of entity failed (null) at position " + location);
        }
        throw new DaoException("Could not move to cursor location " + location);
    }

    public int indexOf(Object object) {
        loadRemaining();
        return this.entities.indexOf(object);
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public Iterator<E> iterator() {
        return new LazyIterator(0, false);
    }

    public int lastIndexOf(Object object) {
        loadRemaining();
        return this.entities.lastIndexOf(object);
    }

    public CloseableListIterator<E> listIterator() {
        return new LazyIterator(0, false);
    }

    public CloseableListIterator<E> listIteratorAutoClose() {
        return new LazyIterator(0, true);
    }

    public ListIterator<E> listIterator(int location) {
        return new LazyIterator(location, false);
    }

    public E remove(int location) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    public E set(int location, E e) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return this.size;
    }

    public List<E> subList(int start, int end) {
        checkCached();
        for (int i = start; i < end; i++) {
            get(i);
        }
        return this.entities.subList(start, end);
    }

    public Object[] toArray() {
        loadRemaining();
        return this.entities.toArray();
    }

    public <T> T[] toArray(T[] array) {
        loadRemaining();
        return this.entities.toArray(array);
    }
}
