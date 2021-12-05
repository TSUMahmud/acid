package nodomain.freeyourgadget.gadgetbridge.database.schema;

import android.database.sqlite.SQLiteDatabase;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.database.DBUpdateScript;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivitySampleDao;

public class GadgetbridgeUpdate_14 implements DBUpdateScript {
    public void upgradeSchema(SQLiteDatabase db) {
        if (!DBHelper.existsColumn(PebbleHealthActivitySampleDao.TABLENAME, PebbleHealthActivitySampleDao.Properties.HeartRate.columnName, db)) {
            db.execSQL("ALTER TABLE PEBBLE_HEALTH_ACTIVITY_SAMPLE ADD COLUMN " + PebbleHealthActivitySampleDao.Properties.HeartRate.columnName + " INTEGER NOT NULL DEFAULT 0;");
        }
    }

    public void downgradeSchema(SQLiteDatabase db) {
    }
}
