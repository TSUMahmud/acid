package p008de.greenrobot.dao.test;

import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoLog;

/* renamed from: de.greenrobot.dao.test.AbstractDaoTestLongPk */
public abstract class AbstractDaoTestLongPk<D extends AbstractDao<T, Long>, T> extends AbstractDaoTestSinglePk<D, T, Long> {
    public AbstractDaoTestLongPk(Class<D> daoClass) {
        super(daoClass);
    }

    /* access modifiers changed from: protected */
    public Long createRandomPk() {
        return Long.valueOf(this.random.nextLong());
    }

    public void testAssignPk() {
        if (this.daoAccess.isEntityUpdateable()) {
            T entity1 = createEntity(null);
            if (entity1 != null) {
                T entity2 = createEntity(null);
                this.dao.insert(entity1);
                this.dao.insert(entity2);
                Long pk1 = (Long) this.daoAccess.getKey(entity1);
                assertNotNull(pk1);
                Long pk2 = (Long) this.daoAccess.getKey(entity2);
                assertNotNull(pk2);
                assertFalse(pk1.equals(pk2));
                assertNotNull(this.dao.load(pk1));
                assertNotNull(this.dao.load(pk2));
                return;
            }
            DaoLog.m18d("Skipping testAssignPk for " + this.daoClass + " (createEntity returned null for null key)");
            return;
        }
        DaoLog.m18d("Skipping testAssignPk for not updateable " + this.daoClass);
    }
}
