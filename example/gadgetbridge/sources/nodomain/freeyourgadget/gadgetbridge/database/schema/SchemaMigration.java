package nodomain.freeyourgadget.gadgetbridge.database.schema;

import android.database.sqlite.SQLiteDatabase;
import nodomain.freeyourgadget.gadgetbridge.database.DBUpdateScript;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaMigration {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SchemaMigration.class);
    private final String classNamePrefix;

    public SchemaMigration(String updaterClassNamePrefix) {
        this.classNamePrefix = updaterClassNamePrefix;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger logger = LOG;
        logger.info("ActivityDatabase: schema upgrade requested from " + oldVersion + " to " + newVersion);
        int i = oldVersion + 1;
        while (i <= newVersion) {
            try {
                DBUpdateScript updater = getUpdateScript(db, i);
                if (updater != null) {
                    Logger logger2 = LOG;
                    logger2.info("upgrading activity database to version " + i);
                    updater.upgradeSchema(db);
                }
                i++;
            } catch (RuntimeException ex) {
                C1238GB.toast("Error upgrading database.", 0, 3, (Throwable) ex);
                throw ex;
            }
        }
        Logger logger3 = LOG;
        logger3.info("activity database is now at version " + newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOG.info("ActivityDatabase: schema downgrade requested from " + oldVersion + " to " + newVersion);
        int i = oldVersion;
        while (i >= newVersion) {
            try {
                DBUpdateScript updater = getUpdateScript(db, i);
                if (updater != null) {
                    Logger logger = LOG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("downgrading activity database to version ");
                    sb.append(i - 1);
                    logger.info(sb.toString());
                    updater.downgradeSchema(db);
                }
                i--;
            } catch (RuntimeException ex) {
                C1238GB.toast("Error downgrading database.", 0, 3, (Throwable) ex);
                throw ex;
            }
        }
        LOG.info("activity database is now at version " + newVersion);
    }

    private DBUpdateScript getUpdateScript(SQLiteDatabase db, int version) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return (DBUpdateScript) classLoader.loadClass(getClass().getPackage().getName() + "." + this.classNamePrefix + version).newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException | InstantiationException e2) {
            throw new RuntimeException("Error instantiating DBUpdate class for version " + version, e2);
        }
    }
}
