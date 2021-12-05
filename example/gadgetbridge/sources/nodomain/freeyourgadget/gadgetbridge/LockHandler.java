package nodomain.freeyourgadget.gadgetbridge;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoMaster;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;

public class LockHandler implements DBHandler {
    private DaoMaster daoMaster = null;
    private SQLiteOpenHelper helper = null;
    private DaoSession session = null;

    public void init(DaoMaster daoMaster2, DaoMaster.OpenHelper helper2) {
        if (isValid()) {
            throw new IllegalStateException("DB must be closed before initializing it again");
        } else if (daoMaster2 == null) {
            throw new IllegalArgumentException("daoMaster must not be null");
        } else if (helper2 != null) {
            this.daoMaster = daoMaster2;
            this.helper = helper2;
            this.session = daoMaster2.newSession();
            if (this.session == null) {
                throw new RuntimeException("Unable to create database session");
            }
        } else {
            throw new IllegalArgumentException("helper must not be null");
        }
    }

    public DaoMaster getDaoMaster() {
        return this.daoMaster;
    }

    private boolean isValid() {
        return this.daoMaster != null;
    }

    private void ensureValid() {
        if (!isValid()) {
            throw new IllegalStateException("LockHandler is not in a valid state");
        }
    }

    public void close() {
        ensureValid();
        GBApplication.releaseDB();
    }

    public synchronized void openDb() {
        if (this.session == null) {
            GBApplication.app().setupDatabase();
        } else {
            throw new IllegalStateException("session must be null");
        }
    }

    public synchronized void closeDb() {
        if (this.session != null) {
            this.session.clear();
            this.session.getDatabase().close();
            this.session = null;
            this.helper = null;
            this.daoMaster = null;
        } else {
            throw new IllegalStateException("session must not be null");
        }
    }

    public SQLiteOpenHelper getHelper() {
        ensureValid();
        return this.helper;
    }

    public DaoSession getDaoSession() {
        ensureValid();
        return this.session;
    }

    public SQLiteDatabase getDatabase() {
        ensureValid();
        return this.daoMaster.getDatabase();
    }
}
