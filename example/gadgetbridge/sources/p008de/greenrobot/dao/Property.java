package p008de.greenrobot.dao;

import java.util.Collection;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.internal.SqlUtils;
import p008de.greenrobot.dao.query.WhereCondition;

/* renamed from: de.greenrobot.dao.Property */
public class Property {
    public final String columnName;
    public final String name;
    public final int ordinal;
    public final boolean primaryKey;
    public final Class<?> type;

    public Property(int ordinal2, Class<?> type2, String name2, boolean primaryKey2, String columnName2) {
        this.ordinal = ordinal2;
        this.type = type2;
        this.name = name2;
        this.primaryKey = primaryKey2;
        this.columnName = columnName2;
    }

    /* renamed from: eq */
    public WhereCondition mo14989eq(Object value) {
        return new WhereCondition.PropertyCondition(this, "=?", value);
    }

    public WhereCondition notEq(Object value) {
        return new WhereCondition.PropertyCondition(this, "<>?", value);
    }

    public WhereCondition like(String value) {
        return new WhereCondition.PropertyCondition(this, " LIKE ?", (Object) value);
    }

    public WhereCondition between(Object value1, Object value2) {
        return new WhereCondition.PropertyCondition(this, " BETWEEN ? AND ?", new Object[]{value1, value2});
    }

    /* renamed from: in */
    public WhereCondition mo14993in(Object... inValues) {
        StringBuilder condition = new StringBuilder(" IN (");
        SqlUtils.appendPlaceholders(condition, inValues.length).append(CoreConstants.RIGHT_PARENTHESIS_CHAR);
        return new WhereCondition.PropertyCondition(this, condition.toString(), inValues);
    }

    /* renamed from: in */
    public WhereCondition mo14992in(Collection<?> inValues) {
        return mo14993in(inValues.toArray());
    }

    public WhereCondition notIn(Object... notInValues) {
        StringBuilder condition = new StringBuilder(" NOT IN (");
        SqlUtils.appendPlaceholders(condition, notInValues.length).append(CoreConstants.RIGHT_PARENTHESIS_CHAR);
        return new WhereCondition.PropertyCondition(this, condition.toString(), notInValues);
    }

    public WhereCondition notIn(Collection<?> notInValues) {
        return notIn(notInValues.toArray());
    }

    /* renamed from: gt */
    public WhereCondition mo14991gt(Object value) {
        return new WhereCondition.PropertyCondition(this, ">?", value);
    }

    /* renamed from: lt */
    public WhereCondition mo14998lt(Object value) {
        return new WhereCondition.PropertyCondition(this, "<?", value);
    }

    /* renamed from: ge */
    public WhereCondition mo14990ge(Object value) {
        return new WhereCondition.PropertyCondition(this, ">=?", value);
    }

    /* renamed from: le */
    public WhereCondition mo14996le(Object value) {
        return new WhereCondition.PropertyCondition(this, "<=?", value);
    }

    public WhereCondition isNull() {
        return new WhereCondition.PropertyCondition(this, " IS NULL");
    }

    public WhereCondition isNotNull() {
        return new WhereCondition.PropertyCondition(this, " IS NOT NULL");
    }
}
