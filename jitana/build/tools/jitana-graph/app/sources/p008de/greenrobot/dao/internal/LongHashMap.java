package p008de.greenrobot.dao.internal;

import java.util.Arrays;
import p008de.greenrobot.dao.DaoLog;

/* renamed from: de.greenrobot.dao.internal.LongHashMap */
public final class LongHashMap<T> {
    private int capacity;
    private int size;
    private Entry<T>[] table;
    private int threshold;

    /* renamed from: de.greenrobot.dao.internal.LongHashMap$Entry */
    static final class Entry<T> {
        final long key;
        Entry<T> next;
        T value;

        Entry(long key2, T value2, Entry<T> next2) {
            this.key = key2;
            this.value = value2;
            this.next = next2;
        }
    }

    public LongHashMap() {
        this(16);
    }

    public LongHashMap(int capacity2) {
        this.capacity = capacity2;
        this.threshold = (capacity2 * 4) / 3;
        this.table = new Entry[capacity2];
    }

    public boolean containsKey(long key) {
        for (Entry<T> entry = this.table[((((int) key) ^ ((int) (key >>> 32))) & Integer.MAX_VALUE) % this.capacity]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return true;
            }
        }
        return false;
    }

    public T get(long key) {
        for (Entry<T> entry = this.table[((((int) key) ^ ((int) (key >>> 32))) & Integer.MAX_VALUE) % this.capacity]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return entry.value;
            }
        }
        return null;
    }

    public T put(long key, T value) {
        int index = ((((int) key) ^ ((int) (key >>> 32))) & Integer.MAX_VALUE) % this.capacity;
        Entry<T> entryOriginal = this.table[index];
        for (Entry<T> entry = entryOriginal; entry != null; entry = entry.next) {
            if (entry.key == key) {
                T oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }
        this.table[index] = new Entry<>(key, value, entryOriginal);
        this.size++;
        if (this.size <= this.threshold) {
            return null;
        }
        setCapacity(this.capacity * 2);
        return null;
    }

    public T remove(long key) {
        int index = ((((int) key) ^ ((int) (key >>> 32))) & Integer.MAX_VALUE) % this.capacity;
        Entry<T> previous = null;
        Entry<T> entry = this.table[index];
        while (entry != null) {
            Entry<T> next = entry.next;
            if (entry.key == key) {
                if (previous == null) {
                    this.table[index] = next;
                } else {
                    previous.next = next;
                }
                this.size--;
                return entry.value;
            }
            previous = entry;
            entry = next;
        }
        return null;
    }

    public void clear() {
        this.size = 0;
        Arrays.fill(this.table, (Object) null);
    }

    public int size() {
        return this.size;
    }

    public void setCapacity(int newCapacity) {
        Entry<T>[] newTable = new Entry[newCapacity];
        for (Entry<T> entry : this.table) {
            while (entry != null) {
                long key = entry.key;
                int index = ((((int) key) ^ ((int) (key >>> 32))) & Integer.MAX_VALUE) % newCapacity;
                Entry<T> originalNext = entry.next;
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = originalNext;
            }
        }
        this.table = newTable;
        this.capacity = newCapacity;
        this.threshold = (newCapacity * 4) / 3;
    }

    public void reserveRoom(int entryCount) {
        setCapacity((entryCount * 5) / 3);
    }

    public void logStats() {
        int collisions = 0;
        for (Entry<T> entry : this.table) {
            while (entry != null && entry.next != null) {
                collisions++;
                entry = entry.next;
            }
        }
        DaoLog.m18d("load: " + (((float) this.size) / ((float) this.capacity)) + ", size: " + this.size + ", capa: " + this.capacity + ", collisions: " + collisions + ", collision ratio: " + (((float) collisions) / ((float) this.size)));
    }
}
