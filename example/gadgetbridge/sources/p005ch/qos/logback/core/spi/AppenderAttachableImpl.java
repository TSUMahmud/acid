package p005ch.qos.logback.core.spi;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import p005ch.qos.logback.core.Appender;

/* renamed from: ch.qos.logback.core.spi.AppenderAttachableImpl */
public class AppenderAttachableImpl<E> implements AppenderAttachable<E> {
    static final long START = System.currentTimeMillis();
    private final CopyOnWriteArrayList<Appender<E>> appenderList = new CopyOnWriteArrayList<>();

    public void addAppender(Appender<E> appender) {
        if (appender != null) {
            this.appenderList.addIfAbsent(appender);
            return;
        }
        throw new IllegalArgumentException("Null argument disallowed");
    }

    public int appendLoopOnAppenders(E e) {
        Iterator<Appender<E>> it = this.appenderList.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next().doAppend(e);
            i++;
        }
        return i;
    }

    public void detachAndStopAllAppenders() {
        Iterator<Appender<E>> it = this.appenderList.iterator();
        while (it.hasNext()) {
            it.next().stop();
        }
        this.appenderList.clear();
    }

    public boolean detachAppender(Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        return this.appenderList.remove(appender);
    }

    public boolean detachAppender(String str) {
        if (str == null) {
            return false;
        }
        Iterator<Appender<E>> it = this.appenderList.iterator();
        while (it.hasNext()) {
            Appender next = it.next();
            if (str.equals(next.getName())) {
                return this.appenderList.remove(next);
            }
        }
        return false;
    }

    public Appender<E> getAppender(String str) {
        if (str == null) {
            return null;
        }
        Iterator<Appender<E>> it = this.appenderList.iterator();
        while (it.hasNext()) {
            Appender<E> next = it.next();
            if (str.equals(next.getName())) {
                return next;
            }
        }
        return null;
    }

    public boolean isAttached(Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        Iterator<Appender<E>> it = this.appenderList.iterator();
        while (it.hasNext()) {
            if (it.next() == appender) {
                return true;
            }
        }
        return false;
    }

    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.appenderList.iterator();
    }
}
