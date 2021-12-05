package nodomain.freeyourgadget.gadgetbridge.database.schema;

import android.database.sqlite.SQLiteDatabase;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.database.DBUpdateScript;
import nodomain.freeyourgadget.gadgetbridge.entities.DeviceAttributesDao;

public class GadgetbridgeUpdate_15 implements DBUpdateScript {
    public void upgradeSchema(SQLiteDatabase db) {
        if (!DBHelper.existsColumn(DeviceAttributesDao.TABLENAME, DeviceAttributesDao.Properties.VolatileIdentifier.columnName, db)) {
            db.execSQL("ALTER TABLE DEVICE_ATTRIBUTES ADD COLUMN " + DeviceAttributesDao.Properties.VolatileIdentifier.columnName + " TEXT;");
        }
    }

    public void downgradeSchema(SQLiteDatabase db) {
    }
}
