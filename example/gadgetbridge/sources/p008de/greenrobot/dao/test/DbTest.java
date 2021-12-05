package p008de.greenrobot.dao.test;

import android.app.Application;
import android.app.Instrumentation;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import java.util.Random;
import p008de.greenrobot.dao.DbUtils;

/* renamed from: de.greenrobot.dao.test.DbTest */
public abstract class DbTest extends AndroidTestCase {
    public static final String DB_NAME = "greendao-unittest-db.temp";
    private Application application;

    /* renamed from: db */
    protected SQLiteDatabase f112db;
    protected final boolean inMemory;
    protected final Random random;

    public DbTest() {
        this(true);
    }

    public DbTest(boolean inMemory2) {
        this.inMemory = inMemory2;
        this.random = new Random();
    }

    /* access modifiers changed from: protected */
    public void setUp() throws Exception {
        DbTest.super.setUp();
        this.f112db = createDatabase();
    }

    public <T extends Application> T createApplication(Class<T> appClass) {
        assertNull("Application already created", this.application);
        try {
            T app = Instrumentation.newApplication(appClass, getContext());
            app.onCreate();
            this.application = app;
            return app;
        } catch (Exception e) {
            throw new RuntimeException("Could not create application " + appClass, e);
        }
    }

    public void terminateApplication() {
        assertNotNull("Application not yet created", this.application);
        this.application.onTerminate();
        this.application = null;
    }

    public <T extends Application> T getApplication() {
        assertNotNull("Application not yet created", this.application);
        return this.application;
    }

    /* access modifiers changed from: protected */
    public SQLiteDatabase createDatabase() {
        if (this.inMemory) {
            return SQLiteDatabase.create((SQLiteDatabase.CursorFactory) null);
        }
        getContext().deleteDatabase(DB_NAME);
        return getContext().openOrCreateDatabase(DB_NAME, 0, (SQLiteDatabase.CursorFactory) null);
    }

    /* access modifiers changed from: protected */
    public void tearDown() throws Exception {
        if (this.application != null) {
            terminateApplication();
        }
        this.f112db.close();
        if (!this.inMemory) {
            getContext().deleteDatabase(DB_NAME);
        }
        DbTest.super.tearDown();
    }

    /* access modifiers changed from: protected */
    public void logTableDump(String tablename) {
        DbUtils.logTableDump(this.f112db, tablename);
    }
}
