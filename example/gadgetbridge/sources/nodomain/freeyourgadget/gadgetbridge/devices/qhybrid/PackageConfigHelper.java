package nodomain.freeyourgadget.gadgetbridge.devices.qhybrid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest;
import org.slf4j.Marker;

public class PackageConfigHelper {
    public static final String DB_APPNAME = "appName";
    public static final String DB_HOUR = "hourDegrees";
    public static final String DB_ID = "id";
    public static final String DB_MINUTE = "minDegress";
    public static final String DB_NAME = "qhybridNotifications.db";
    public static final String DB_PACKAGE = "package";
    public static final String DB_RESPECT_SILENT = "respectSilent";
    public static final String DB_TABLE = "notifications";
    public static final String DB_VIBRATION = "vibrationTtype";

    public PackageConfigHelper(Context context) throws GBException {
        initDB();
    }

    public void saveNotificationConfiguration(NotificationConfiguration settings) throws GBException {
        ContentValues values = new ContentValues(6);
        values.put("package", settings.getPackageName());
        values.put(DB_APPNAME, settings.getAppName());
        values.put(DB_HOUR, Short.valueOf(settings.getHour()));
        values.put(DB_MINUTE, Short.valueOf(settings.getMin()));
        values.put(DB_VIBRATION, Byte.valueOf(settings.getVibration().getValue()));
        values.put(DB_RESPECT_SILENT, Boolean.valueOf(settings.getRespectSilentMode()));
        SQLiteDatabase database = GBApplication.acquireDB().getDatabase();
        if (settings.getId() == -1) {
            settings.setId(database.insert(DB_TABLE, (String) null, values));
        } else {
            database.update(DB_TABLE, values, "id=?", new String[]{String.valueOf(settings.getId())});
        }
        GBApplication.releaseDB();
    }

    public ArrayList<NotificationConfiguration> getNotificationConfigurations() throws GBException {
        SQLiteDatabase database = GBApplication.acquireDB().getDatabase();
        Cursor cursor = database.query(DB_TABLE, new String[]{Marker.ANY_MARKER}, (String) null, (String[]) null, (String) null, (String) null, (String) null);
        GBApplication.releaseDB();
        int size = cursor.getCount();
        ArrayList<NotificationConfiguration> list = new ArrayList<>(size);
        if (size > 0) {
            int appNamePos = cursor.getColumnIndex(DB_APPNAME);
            int packageNamePos = cursor.getColumnIndex("package");
            int hourPos = cursor.getColumnIndex(DB_HOUR);
            int minPos = cursor.getColumnIndex(DB_MINUTE);
            int silentPos = cursor.getColumnIndex(DB_RESPECT_SILENT);
            int vibrationPos = cursor.getColumnIndex(DB_VIBRATION);
            int idPos = cursor.getColumnIndex(DB_ID);
            cursor.moveToFirst();
            while (true) {
                short s = (short) cursor.getInt(minPos);
                short s2 = (short) cursor.getInt(hourPos);
                String string = cursor.getString(packageNamePos);
                String string2 = cursor.getString(appNamePos);
                SQLiteDatabase database2 = database;
                boolean z = true;
                if (cursor.getInt(silentPos) != 1) {
                    z = false;
                }
                int appNamePos2 = appNamePos;
                int packageNamePos2 = packageNamePos;
                int size2 = size;
                NotificationConfiguration notificationConfiguration = r11;
                NotificationConfiguration notificationConfiguration2 = new NotificationConfiguration(s, s2, string, string2, z, PlayNotificationRequest.VibrationType.fromValue((byte) cursor.getInt(vibrationPos)), (long) cursor.getInt(idPos));
                list.add(notificationConfiguration);
                Log.d("Settings", "setting #" + cursor.getPosition() + ": " + cursor.getInt(silentPos));
                if (!cursor.moveToNext()) {
                    break;
                }
                database = database2;
                appNamePos = appNamePos2;
                packageNamePos = packageNamePos2;
                size = size2;
            }
        } else {
            int i = size;
        }
        cursor.close();
        return list;
    }

    public NotificationConfiguration getNotificationConfiguration(String appName) throws GBException {
        if (appName == null) {
            return null;
        }
        Cursor c = GBApplication.acquireDB().getDatabase().query(DB_TABLE, new String[]{Marker.ANY_MARKER}, "appName=?", new String[]{appName}, (String) null, (String) null, (String) null);
        GBApplication.releaseDB();
        if (c.getCount() == 0) {
            c.close();
            return null;
        }
        c.moveToFirst();
        NotificationConfiguration notificationConfiguration = new NotificationConfiguration((short) c.getInt(c.getColumnIndex(DB_MINUTE)), (short) c.getInt(c.getColumnIndex(DB_HOUR)), c.getString(c.getColumnIndex("package")), c.getString(c.getColumnIndex(DB_APPNAME)), c.getInt(c.getColumnIndex(DB_RESPECT_SILENT)) == 1, PlayNotificationRequest.VibrationType.fromValue((byte) c.getInt(c.getColumnIndex(DB_VIBRATION))), (long) c.getInt(c.getColumnIndex(DB_ID)));
        c.close();
        return notificationConfiguration;
    }

    private void initDB() throws GBException {
        GBApplication.acquireDB().getDatabase().execSQL("CREATE TABLE IF NOT EXISTS notifications(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, package TEXT, vibrationTtype INTEGER, minDegress INTEGER DEFAULT -1, appName TEXT,respectSilent INTEGER,hourDegrees INTEGER DEFAULT -1);");
        GBApplication.releaseDB();
    }

    public void deleteNotificationConfiguration(NotificationConfiguration packageSettings) throws GBException {
        Log.d("DB", "deleting id " + packageSettings.getId());
        if (packageSettings.getId() != -1) {
            GBApplication.acquireDB().getDatabase().delete(DB_TABLE, "id=?", new String[]{String.valueOf(packageSettings.getId())});
            GBApplication.releaseDB();
        }
    }
}
