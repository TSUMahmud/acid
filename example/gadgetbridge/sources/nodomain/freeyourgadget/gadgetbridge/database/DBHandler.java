package nodomain.freeyourgadget.gadgetbridge.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoMaster;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;

public interface DBHandler extends AutoCloseable {
    void close() throws Exception;

    void closeDb();

    DaoMaster getDaoMaster();

    DaoSession getDaoSession();

    SQLiteDatabase getDatabase();

    SQLiteOpenHelper getHelper();

    void openDb();
}
