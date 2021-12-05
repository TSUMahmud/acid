package p008de.greenrobot.dao.query;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoException;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.query.WhereCondition;

/* renamed from: de.greenrobot.dao.query.WhereCollector */
class WhereCollector<T> {
    private final AbstractDao<T, ?> dao;
    private final String tablePrefix;
    private final List<WhereCondition> whereConditions = new ArrayList();

    WhereCollector(AbstractDao<T, ?> dao2, String tablePrefix2) {
        this.dao = dao2;
        this.tablePrefix = tablePrefix2;
    }

    /* access modifiers changed from: package-private */
    public void add(WhereCondition cond, WhereCondition... condMore) {
        checkCondition(cond);
        this.whereConditions.add(cond);
        for (WhereCondition whereCondition : condMore) {
            checkCondition(whereCondition);
            this.whereConditions.add(whereCondition);
        }
    }

    /* access modifiers changed from: package-private */
    public WhereCondition combineWhereConditions(String combineOp, WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        StringBuilder builder = new StringBuilder("(");
        List<Object> combinedValues = new ArrayList<>();
        addCondition(builder, combinedValues, cond1);
        builder.append(combineOp);
        addCondition(builder, combinedValues, cond2);
        for (WhereCondition cond : condMore) {
            builder.append(combineOp);
            addCondition(builder, combinedValues, cond);
        }
        builder.append(CoreConstants.RIGHT_PARENTHESIS_CHAR);
        return new WhereCondition.StringCondition(builder.toString(), combinedValues.toArray());
    }

    /* access modifiers changed from: package-private */
    public void addCondition(StringBuilder builder, List<Object> values, WhereCondition condition) {
        checkCondition(condition);
        condition.appendTo(builder, this.tablePrefix);
        condition.appendValuesTo(values);
    }

    /* access modifiers changed from: package-private */
    public void checkCondition(WhereCondition whereCondition) {
        if (whereCondition instanceof WhereCondition.PropertyCondition) {
            checkProperty(((WhereCondition.PropertyCondition) whereCondition).property);
        }
    }

    /* access modifiers changed from: package-private */
    public void checkProperty(Property property) {
        AbstractDao<T, ?> abstractDao = this.dao;
        if (abstractDao != null) {
            Property[] properties = abstractDao.getProperties();
            boolean found = false;
            int length = properties.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                } else if (property == properties[i]) {
                    found = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!found) {
                throw new DaoException("Property '" + property.name + "' is not part of " + this.dao);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void appendWhereClause(StringBuilder builder, String tablePrefixOrNull, List<Object> values) {
        ListIterator<WhereCondition> iter = this.whereConditions.listIterator();
        while (iter.hasNext()) {
            if (iter.hasPrevious()) {
                builder.append(" AND ");
            }
            WhereCondition condition = iter.next();
            condition.appendTo(builder, tablePrefixOrNull);
            condition.appendValuesTo(values);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.whereConditions.isEmpty();
    }
}
