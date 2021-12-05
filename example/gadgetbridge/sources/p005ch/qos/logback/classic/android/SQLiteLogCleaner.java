package p005ch.qos.logback.classic.android;

import android.database.sqlite.SQLiteDatabase;
import p005ch.qos.logback.core.util.Duration;

/* renamed from: ch.qos.logback.classic.android.SQLiteLogCleaner */
public interface SQLiteLogCleaner {
    void performLogCleanup(SQLiteDatabase sQLiteDatabase, Duration duration);
}
