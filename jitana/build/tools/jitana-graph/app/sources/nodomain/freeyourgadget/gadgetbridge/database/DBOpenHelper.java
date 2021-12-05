package nodomain.freeyourgadget.gadgetbridge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import nodomain.freeyourgadget.gadgetbridge.database.schema.SchemaMigration;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoMaster;

public class DBOpenHelper extends DaoMaster.OpenHelper {
    private final Context context;
    private final String updaterClassNamePrefix;

    public DBOpenHelper(Context context2, String dbName, SQLiteDatabase.CursorFactory factory) {
        super(context2, dbName, factory);
        this.updaterClassNamePrefix = dbName + "Update_";
        this.context = context2;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DaoMaster.createAllTables(db, true);
        new SchemaMigration(this.updaterClassNamePrefix).onUpgrade(db, oldVersion, newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DaoMaster.createAllTables(db, true);
        new SchemaMigration(this.updaterClassNamePrefix).onDowngrade(db, oldVersion, newVersion);
    }

    public Context getContext() {
        return this.context;
    }
}
