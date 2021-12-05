package nodomain.freeyourgadget.gadgetbridge.database.schema;

import android.database.sqlite.SQLiteDatabase;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.database.DBUpdateScript;
import nodomain.freeyourgadget.gadgetbridge.entities.AlarmDao;

public class GadgetbridgeUpdate_22 implements DBUpdateScript {
    public void upgradeSchema(SQLiteDatabase db) {
        if (!DBHelper.existsColumn(AlarmDao.TABLENAME, AlarmDao.Properties.Unused.columnName, db)) {
            db.execSQL("ALTER TABLE ALARM ADD COLUMN " + AlarmDao.Properties.Unused.columnName + " INTEGER NOT NULL DEFAULT 0;");
        }
    }

    public void downgradeSchema(SQLiteDatabase db) {
    }
}
