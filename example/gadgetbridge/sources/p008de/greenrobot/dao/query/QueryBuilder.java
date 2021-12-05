package p008de.greenrobot.dao.query;

import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoException;
import p008de.greenrobot.dao.DaoLog;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.SqlUtils;

/* renamed from: de.greenrobot.dao.query.QueryBuilder */
public class QueryBuilder<T> {
    public static boolean LOG_SQL;
    public static boolean LOG_VALUES;
    private final AbstractDao<T, ?> dao;
    private boolean distinct;
    private final List<Join<T, ?>> joins;
    private Integer limit;
    private Integer offset;
    private StringBuilder orderBuilder;
    private final String tablePrefix;
    private final List<Object> values;
    private final WhereCollector<T> whereCollector;

    public static <T2> QueryBuilder<T2> internalCreate(AbstractDao<T2, ?> dao2) {
        return new QueryBuilder<>(dao2);
    }

    protected QueryBuilder(AbstractDao<T, ?> dao2) {
        this(dao2, "T");
    }

    protected QueryBuilder(AbstractDao<T, ?> dao2, String tablePrefix2) {
        this.dao = dao2;
        this.tablePrefix = tablePrefix2;
        this.values = new ArrayList();
        this.joins = new ArrayList();
        this.whereCollector = new WhereCollector<>(dao2, tablePrefix2);
    }

    private void checkOrderBuilder() {
        StringBuilder sb = this.orderBuilder;
        if (sb == null) {
            this.orderBuilder = new StringBuilder();
        } else if (sb.length() > 0) {
            this.orderBuilder.append(",");
        }
    }

    public QueryBuilder<T> distinct() {
        this.distinct = true;
        return this;
    }

    public QueryBuilder<T> where(WhereCondition cond, WhereCondition... condMore) {
        this.whereCollector.add(cond, condMore);
        return this;
    }

    public QueryBuilder<T> whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        this.whereCollector.add(mo15268or(cond1, cond2, condMore), new WhereCondition[0]);
        return this;
    }

    /* renamed from: or */
    public WhereCondition mo15268or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return this.whereCollector.combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return this.whereCollector.combineWhereConditions(" AND ", cond1, cond2, condMore);
    }

    public <J> Join<T, J> join(Class<J> destinationEntityClass, Property destinationProperty) {
        return join(this.dao.getPkProperty(), destinationEntityClass, destinationProperty);
    }

    public <J> Join<T, J> join(Property sourceProperty, Class<J> destinationEntityClass) {
        AbstractDao<?, ?> dao2 = this.dao.getSession().getDao(destinationEntityClass);
        return addJoin(this.tablePrefix, sourceProperty, dao2, dao2.getPkProperty());
    }

    public <J> Join<T, J> join(Property sourceProperty, Class<J> destinationEntityClass, Property destinationProperty) {
        return addJoin(this.tablePrefix, sourceProperty, this.dao.getSession().getDao(destinationEntityClass), destinationProperty);
    }

    public <J> Join<T, J> join(Join<?, T> sourceJoin, Property sourceProperty, Class<J> destinationEntityClass, Property destinationProperty) {
        return addJoin(sourceJoin.tablePrefix, sourceProperty, this.dao.getSession().getDao(destinationEntityClass), destinationProperty);
    }

    private <J> Join<T, J> addJoin(String sourceTablePrefix, Property sourceProperty, AbstractDao<J, ?> destinationDao, Property destinationProperty) {
        Join join = new Join(sourceTablePrefix, sourceProperty, destinationDao, destinationProperty, "J" + (this.joins.size() + 1));
        this.joins.add(join);
        return join;
    }

    public QueryBuilder<T> orderAsc(Property... properties) {
        orderAscOrDesc(" ASC", properties);
        return this;
    }

    public QueryBuilder<T> orderDesc(Property... properties) {
        orderAscOrDesc(" DESC", properties);
        return this;
    }

    private void orderAscOrDesc(String ascOrDescWithLeadingSpace, Property... properties) {
        for (Property property : properties) {
            checkOrderBuilder();
            append(this.orderBuilder, property);
            if (String.class.equals(property.type)) {
                this.orderBuilder.append(" COLLATE LOCALIZED");
            }
            this.orderBuilder.append(ascOrDescWithLeadingSpace);
        }
    }

    public QueryBuilder<T> orderCustom(Property property, String customOrderForProperty) {
        checkOrderBuilder();
        append(this.orderBuilder, property).append(' ');
        this.orderBuilder.append(customOrderForProperty);
        return this;
    }

    public QueryBuilder<T> orderRaw(String rawOrder) {
        checkOrderBuilder();
        this.orderBuilder.append(rawOrder);
        return this;
    }

    /* access modifiers changed from: protected */
    public StringBuilder append(StringBuilder builder, Property property) {
        this.whereCollector.checkProperty(property);
        builder.append(this.tablePrefix);
        builder.append('.');
        builder.append(CoreConstants.SINGLE_QUOTE_CHAR);
        builder.append(property.columnName);
        builder.append(CoreConstants.SINGLE_QUOTE_CHAR);
        return builder;
    }

    public QueryBuilder<T> limit(int limit2) {
        this.limit = Integer.valueOf(limit2);
        return this;
    }

    public QueryBuilder<T> offset(int offset2) {
        this.offset = Integer.valueOf(offset2);
        return this;
    }

    public Query<T> build() {
        StringBuilder builder = createSelectBuilder();
        int limitPosition = checkAddLimit(builder);
        int offsetPosition = checkAddOffset(builder);
        String sql = builder.toString();
        checkLog(sql);
        return Query.create(this.dao, sql, this.values.toArray(), limitPosition, offsetPosition);
    }

    public CursorQuery buildCursor() {
        StringBuilder builder = createSelectBuilder();
        int limitPosition = checkAddLimit(builder);
        int offsetPosition = checkAddOffset(builder);
        String sql = builder.toString();
        checkLog(sql);
        return CursorQuery.create(this.dao, sql, this.values.toArray(), limitPosition, offsetPosition);
    }

    private StringBuilder createSelectBuilder() {
        StringBuilder builder = new StringBuilder(SqlUtils.createSqlSelect(this.dao.getTablename(), this.tablePrefix, this.dao.getAllColumns(), this.distinct));
        appendJoinsAndWheres(builder, this.tablePrefix);
        StringBuilder sb = this.orderBuilder;
        if (sb != null && sb.length() > 0) {
            builder.append(" ORDER BY ");
            builder.append(this.orderBuilder);
        }
        return builder;
    }

    private int checkAddLimit(StringBuilder builder) {
        if (this.limit == null) {
            return -1;
        }
        builder.append(" LIMIT ?");
        this.values.add(this.limit);
        return this.values.size() - 1;
    }

    private int checkAddOffset(StringBuilder builder) {
        if (this.offset == null) {
            return -1;
        }
        if (this.limit != null) {
            builder.append(" OFFSET ?");
            this.values.add(this.offset);
            return this.values.size() - 1;
        }
        throw new IllegalStateException("Offset cannot be set without limit");
    }

    public DeleteQuery<T> buildDelete() {
        if (this.joins.isEmpty()) {
            String tablename = this.dao.getTablename();
            StringBuilder builder = new StringBuilder(SqlUtils.createSqlDelete(tablename, (String[]) null));
            appendJoinsAndWheres(builder, this.tablePrefix);
            String sql = builder.toString().replace(this.tablePrefix + ".\"", CoreConstants.DOUBLE_QUOTE_CHAR + tablename + "\".\"");
            checkLog(sql);
            return DeleteQuery.create(this.dao, sql, this.values.toArray());
        }
        throw new DaoException("JOINs are not supported for DELETE queries");
    }

    public CountQuery<T> buildCount() {
        StringBuilder builder = new StringBuilder(SqlUtils.createSqlSelectCountStar(this.dao.getTablename(), this.tablePrefix));
        appendJoinsAndWheres(builder, this.tablePrefix);
        String sql = builder.toString();
        checkLog(sql);
        return CountQuery.create(this.dao, sql, this.values.toArray());
    }

    private void checkLog(String sql) {
        if (LOG_SQL) {
            DaoLog.m18d("Built SQL for query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.m18d("Values for query: " + this.values);
        }
    }

    private void appendJoinsAndWheres(StringBuilder builder, String tablePrefixOrNull) {
        this.values.clear();
        for (Join<T, ?> join : this.joins) {
            builder.append(" JOIN ");
            builder.append(join.daoDestination.getTablename());
            builder.append(' ');
            builder.append(join.tablePrefix);
            builder.append(" ON ");
            SqlUtils.appendProperty(builder, join.sourceTablePrefix, join.joinPropertySource).append('=');
            SqlUtils.appendProperty(builder, join.tablePrefix, join.joinPropertyDestination);
        }
        boolean whereAppended = !this.whereCollector.isEmpty();
        if (whereAppended) {
            builder.append(" WHERE ");
            this.whereCollector.appendWhereClause(builder, tablePrefixOrNull, this.values);
        }
        for (Join<T, ?> join2 : this.joins) {
            if (!join2.whereCollector.isEmpty()) {
                if (!whereAppended) {
                    builder.append(" WHERE ");
                    whereAppended = true;
                } else {
                    builder.append(" AND ");
                }
                join2.whereCollector.appendWhereClause(builder, join2.tablePrefix, this.values);
            }
        }
    }

    public List<T> list() {
        return build().list();
    }

    public LazyList<T> listLazy() {
        return build().listLazy();
    }

    public LazyList<T> listLazyUncached() {
        return build().listLazyUncached();
    }

    public CloseableListIterator<T> listIterator() {
        return build().listIterator();
    }

    public T unique() {
        return build().unique();
    }

    public T uniqueOrThrow() {
        return build().uniqueOrThrow();
    }

    public long count() {
        return buildCount().count();
    }
}
