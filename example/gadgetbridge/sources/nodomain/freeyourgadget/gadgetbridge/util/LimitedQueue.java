package nodomain.freeyourgadget.gadgetbridge.util;

import android.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;

public class LimitedQueue {
    private final int limit;
    private LinkedList<Pair> list = new LinkedList<>();

    public LimitedQueue(int limit2) {
        this.limit = limit2;
    }

    public synchronized void add(int id, Object obj) {
        if (this.list.size() > this.limit - 1) {
            this.list.removeFirst();
        }
        this.list.add(new Pair(Integer.valueOf(id), obj));
    }

    public synchronized void remove(int id) {
        Iterator<Pair> iter = this.list.iterator();
        while (iter.hasNext()) {
            if (((Integer) iter.next().first).intValue() == id) {
                iter.remove();
            }
        }
    }

    public synchronized Object lookup(int id) {
        Iterator it = this.list.iterator();
        while (it.hasNext()) {
            Pair entry = (Pair) it.next();
            if (id == ((Integer) entry.first).intValue()) {
                return entry.second;
            }
        }
        return null;
    }

    public synchronized Object lookupByValue(Object value) {
        Iterator it = this.list.iterator();
        while (it.hasNext()) {
            Pair entry = (Pair) it.next();
            if (value.equals(entry.second)) {
                return entry.first;
            }
        }
        return null;
    }
}
