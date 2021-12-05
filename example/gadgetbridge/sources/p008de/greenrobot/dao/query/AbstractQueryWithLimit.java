package p008de.greenrobot.dao.query;

import java.util.Date;
import p008de.greenrobot.dao.AbstractDao;

/* renamed from: de.greenrobot.dao.query.AbstractQueryWithLimit */
abstract class AbstractQueryWithLimit<T> extends AbstractQuery<T> {
    protected final int limitPosition;
    protected final int offsetPosition;

    protected AbstractQueryWithLimit(AbstractDao<T, ?> dao, String sql, String[] initialValues, int limitPosition2, int offsetPosition2) {
        super(dao, sql, initialValues);
        this.limitPosition = limitPosition2;
        this.offsetPosition = offsetPosition2;
    }

    public AbstractQueryWithLimit<T> setParameter(int index, Object parameter) {
        if (index < 0 || (index != this.limitPosition && index != this.offsetPosition)) {
            return (AbstractQueryWithLimit) super.setParameter(index, parameter);
        }
        throw new IllegalArgumentException("Illegal parameter index: " + index);
    }

    public AbstractQueryWithLimit<T> setParameter(int index, Date parameter) {
        return setParameter(index, (Object) parameter != null ? Long.valueOf(parameter.getTime()) : null);
    }

    public AbstractQueryWithLimit<T> setParameter(int index, Boolean parameter) {
        return setParameter(index, (Object) parameter != null ? Integer.valueOf(parameter.booleanValue() ? 1 : 0) : null);
    }

    public void setLimit(int limit) {
        checkThread();
        if (this.limitPosition != -1) {
            this.parameters[this.limitPosition] = Integer.toString(limit);
            return;
        }
        throw new IllegalStateException("Limit must be set with QueryBuilder before it can be used here");
    }

    public void setOffset(int offset) {
        checkThread();
        if (this.offsetPosition != -1) {
            this.parameters[this.offsetPosition] = Integer.toString(offset);
            return;
        }
        throw new IllegalStateException("Offset must be set with QueryBuilder before it can be used here");
    }
}
