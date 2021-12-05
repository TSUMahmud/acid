package p008de.greenrobot.dao.query;

import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoException;
import p008de.greenrobot.dao.InternalQueryDaoAccess;

/* renamed from: de.greenrobot.dao.query.AbstractQuery */
abstract class AbstractQuery<T> {
    protected final AbstractDao<T, ?> dao;
    protected final InternalQueryDaoAccess<T> daoAccess;
    protected final Thread ownerThread = Thread.currentThread();
    protected final String[] parameters;
    protected final String sql;

    protected static String[] toStringArray(Object[] values) {
        int length = values.length;
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            Object object = values[i];
            if (object != null) {
                strings[i] = object.toString();
            } else {
                strings[i] = null;
            }
        }
        return strings;
    }

    protected AbstractQuery(AbstractDao<T, ?> dao2, String sql2, String[] parameters2) {
        this.dao = dao2;
        this.daoAccess = new InternalQueryDaoAccess<>(dao2);
        this.sql = sql2;
        this.parameters = parameters2;
    }

    public AbstractQuery<T> setParameter(int index, Object parameter) {
        checkThread();
        if (parameter != null) {
            this.parameters[index] = parameter.toString();
        } else {
            this.parameters[index] = null;
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public void checkThread() {
        if (Thread.currentThread() != this.ownerThread) {
            throw new DaoException("Method may be called only in owner thread, use forCurrentThread to get an instance for this thread");
        }
    }
}
