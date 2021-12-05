package nodomain.freeyourgadget.gadgetbridge.database;

import android.database.sqlite.SQLiteDatabase;

public interface DBUpdateScript {
    void downgradeSchema(SQLiteDatabase sQLiteDatabase);

    void upgradeSchema(SQLiteDatabase sQLiteDatabase);
}
