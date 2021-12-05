package p008de.greenrobot.dao.test;

import android.database.sqlite.SQLiteDatabase;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoLog;
import p008de.greenrobot.dao.InternalUnitTestDaoAccess;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.identityscope.IdentityScope;

/* renamed from: de.greenrobot.dao.test.AbstractDaoTest */
public abstract class AbstractDaoTest<D extends AbstractDao<T, K>, T, K> extends DbTest {
    protected D dao;
    protected InternalUnitTestDaoAccess<T, K> daoAccess;
    protected final Class<D> daoClass;
    protected IdentityScope<K, T> identityScopeForDao;
    protected Property pkColumn;

    public AbstractDaoTest(Class<D> daoClass2) {
        this(daoClass2, true);
    }

    public AbstractDaoTest(Class<D> daoClass2, boolean inMemory) {
        super(inMemory);
        this.daoClass = daoClass2;
    }

    public void setIdentityScopeBeforeSetUp(IdentityScope<K, T> identityScope) {
        this.identityScopeForDao = identityScope;
    }

    /* access modifiers changed from: protected */
    public void setUp() throws Exception {
        super.setUp();
        try {
            setUpTableForDao();
            this.daoAccess = new InternalUnitTestDaoAccess<>(this.f112db, this.daoClass, this.identityScopeForDao);
            this.dao = this.daoAccess.getDao();
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare DAO Test", e);
        }
    }

    /* access modifiers changed from: protected */
    public void setUpTableForDao() throws Exception {
        try {
            this.daoClass.getMethod("createTable", new Class[]{SQLiteDatabase.class, Boolean.TYPE}).invoke((Object) null, new Object[]{this.f112db, false});
        } catch (NoSuchMethodException e) {
            DaoLog.m22i("No createTable method");
        }
    }

    /* access modifiers changed from: protected */
    public void clearIdentityScopeIfAny() {
        IdentityScope<K, T> identityScope = this.identityScopeForDao;
        if (identityScope != null) {
            identityScope.clear();
            DaoLog.m18d("Identity scope cleared");
            return;
        }
        DaoLog.m18d("No identity scope to clear");
    }

    /* access modifiers changed from: protected */
    public void logTableDump() {
        logTableDump(this.dao.getTablename());
    }
}
