package p008de.greenrobot.dao.query;

import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;

/* renamed from: de.greenrobot.dao.query.Join */
public class Join<SRC, DST> {
    final AbstractDao<DST, ?> daoDestination;
    final Property joinPropertyDestination;
    final Property joinPropertySource;
    final String sourceTablePrefix;
    final String tablePrefix;
    final WhereCollector<DST> whereCollector;

    public Join(String sourceTablePrefix2, Property sourceJoinProperty, AbstractDao<DST, ?> daoDestination2, Property destinationJoinProperty, String joinTablePrefix) {
        this.sourceTablePrefix = sourceTablePrefix2;
        this.joinPropertySource = sourceJoinProperty;
        this.daoDestination = daoDestination2;
        this.joinPropertyDestination = destinationJoinProperty;
        this.tablePrefix = joinTablePrefix;
        this.whereCollector = new WhereCollector<>(daoDestination2, joinTablePrefix);
    }

    public Join<SRC, DST> where(WhereCondition cond, WhereCondition... condMore) {
        this.whereCollector.add(cond, condMore);
        return this;
    }

    public Join<SRC, DST> whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        this.whereCollector.add(mo15198or(cond1, cond2, condMore), new WhereCondition[0]);
        return this;
    }

    /* renamed from: or */
    public WhereCondition mo15198or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return this.whereCollector.combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return this.whereCollector.combineWhereConditions(" AND ", cond1, cond2, condMore);
    }

    public String getTablePrefix() {
        return this.tablePrefix;
    }
}
