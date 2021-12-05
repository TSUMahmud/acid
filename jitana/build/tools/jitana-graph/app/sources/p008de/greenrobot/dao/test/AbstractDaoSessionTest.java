package p008de.greenrobot.dao.test;

import android.database.sqlite.SQLiteDatabase;
import p008de.greenrobot.dao.AbstractDaoMaster;
import p008de.greenrobot.dao.AbstractDaoSession;

/* renamed from: de.greenrobot.dao.test.AbstractDaoSessionTest */
public abstract class AbstractDaoSessionTest<T extends AbstractDaoMaster, S extends AbstractDaoSession> extends DbTest {
    protected T daoMaster;
    private final Class<T> daoMasterClass;
    protected S daoSession;

    public AbstractDaoSessionTest(Class<T> daoMasterClass2) {
        this(daoMasterClass2, true);
    }

    public AbstractDaoSessionTest(Class<T> daoMasterClass2, boolean inMemory) {
        super(inMemory);
        this.daoMasterClass = daoMasterClass2;
    }

    /* access modifiers changed from: protected */
    public void setUp() throws Exception {
        super.setUp();
        try {
            this.daoMaster = (AbstractDaoMaster) this.daoMasterClass.getConstructor(new Class[]{SQLiteDatabase.class}).newInstance(new Object[]{this.f112db});
            this.daoMasterClass.getMethod("createAllTables", new Class[]{SQLiteDatabase.class, Boolean.TYPE}).invoke((Object) null, new Object[]{this.f112db, false});
            this.daoSession = this.daoMaster.newSession();
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare DAO session test", e);
        }
    }
}
