package p008de.greenrobot.dao.identityscope;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/* renamed from: de.greenrobot.dao.identityscope.IdentityScopeObject */
public class IdentityScopeObject<K, T> implements IdentityScope<K, T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final HashMap<K, Reference<T>> map = new HashMap<>();

    public T get(K key) {
        this.lock.lock();
        try {
            Reference<T> ref = this.map.get(key);
            if (ref != null) {
                return ref.get();
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public T getNoLock(K key) {
        Reference<T> ref = this.map.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void put(K key, T entity) {
        this.lock.lock();
        try {
            this.map.put(key, new WeakReference(entity));
        } finally {
            this.lock.unlock();
        }
    }

    public void putNoLock(K key, T entity) {
        this.map.put(key, new WeakReference(entity));
    }

    public boolean detach(K key, T entity) {
        boolean z;
        this.lock.lock();
        try {
            if (get(key) != entity || entity == null) {
                z = false;
            } else {
                remove(key);
                z = true;
            }
            return z;
        } finally {
            this.lock.unlock();
        }
    }

    public void remove(K key) {
        this.lock.lock();
        try {
            this.map.remove(key);
        } finally {
            this.lock.unlock();
        }
    }

    public void remove(Iterable<K> keys) {
        this.lock.lock();
        try {
            for (K key : keys) {
                this.map.remove(key);
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void clear() {
        this.lock.lock();
        try {
            this.map.clear();
        } finally {
            this.lock.unlock();
        }
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public void reserveRoom(int count) {
    }
}
