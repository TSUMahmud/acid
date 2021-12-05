package nodomain.freeyourgadget.gadgetbridge.database.schema;

import android.database.sqlite.SQLiteDatabase;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.database.DBUpdateScript;
import nodomain.freeyourgadget.gadgetbridge.entities.No1F1ActivitySampleDao;

public class GadgetbridgeUpdate_17 implements DBUpdateScript {
    public void upgradeSchema(SQLiteDatabase db) {
        if (!DBHelper.existsColumn(No1F1ActivitySampleDao.TABLENAME, No1F1ActivitySampleDao.Properties.HeartRate.columnName, db)) {
            db.execSQL("ALTER TABLE NO1_F1_ACTIVITY_SAMPLE ADD COLUMN " + No1F1ActivitySampleDao.Properties.HeartRate.columnName + " INTEGER NOT NULL DEFAULT 0;");
        }
    }

    public void downgradeSchema(SQLiteDatabase db) {
    }
}
