package p005ch.qos.logback.classic.android;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import p005ch.qos.logback.classic.p006db.SQLBuilder;
import p005ch.qos.logback.classic.p006db.names.DBNameResolver;
import p005ch.qos.logback.classic.p006db.names.DefaultDBNameResolver;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.spi.IThrowableProxy;
import p005ch.qos.logback.classic.spi.StackTraceElementProxy;
import p005ch.qos.logback.classic.spi.ThrowableProxyUtil;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.UnsynchronizedAppenderBase;
import p005ch.qos.logback.core.android.CommonPathUtil;
import p005ch.qos.logback.core.util.Duration;

/* renamed from: ch.qos.logback.classic.android.SQLiteAppender */
public class SQLiteAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private static final int ARG0_INDEX = 7;
    private static final int CALLER_CLASS_INDEX = 12;
    private static final int CALLER_FILENAME_INDEX = 11;
    private static final int CALLER_LINE_INDEX = 14;
    private static final int CALLER_METHOD_INDEX = 13;
    private static final short EXCEPTION_EXISTS = 2;
    private static final int FORMATTED_MESSAGE_INDEX = 2;
    private static final int LEVEL_STRING_INDEX = 4;
    private static final int LOGGER_NAME_INDEX = 3;
    private static final short PROPERTIES_EXIST = 1;
    private static final int REFERENCE_FLAG_INDEX = 6;
    private static final int THREAD_NAME_INDEX = 5;
    private static final int TIMESTMP_INDEX = 1;

    /* renamed from: db */
    private SQLiteDatabase f44db;
    /* access modifiers changed from: private */
    public DBNameResolver dbNameResolver;
    private String filename;
    private String insertExceptionSQL;
    private String insertPropertiesSQL;
    private String insertSQL;
    private long lastCleanupTime = 0;
    private SQLiteLogCleaner logCleaner;
    private Duration maxHistory;

    private String asStringTruncatedTo254(Object obj) {
        String obj2 = obj != null ? obj.toString() : null;
        if (obj2 != null && obj2.length() > 254) {
            obj2 = obj2.substring(0, 254);
        }
        return obj2 == null ? "" : obj2;
    }

    private void bindCallerData(SQLiteStatement sQLiteStatement, StackTraceElement[] stackTraceElementArr) throws SQLException {
        StackTraceElement stackTraceElement;
        if (stackTraceElementArr != null && stackTraceElementArr.length > 0 && (stackTraceElement = stackTraceElementArr[0]) != null) {
            sQLiteStatement.bindString(11, stackTraceElement.getFileName());
            sQLiteStatement.bindString(12, stackTraceElement.getClassName());
            sQLiteStatement.bindString(13, stackTraceElement.getMethodName());
            sQLiteStatement.bindString(14, Integer.toString(stackTraceElement.getLineNumber()));
        }
    }

    private void bindLoggingEvent(SQLiteStatement sQLiteStatement, ILoggingEvent iLoggingEvent) throws SQLException {
        sQLiteStatement.bindLong(1, iLoggingEvent.getTimeStamp());
        sQLiteStatement.bindString(2, iLoggingEvent.getFormattedMessage());
        sQLiteStatement.bindString(3, iLoggingEvent.getLoggerName());
        sQLiteStatement.bindString(4, iLoggingEvent.getLevel().toString());
        sQLiteStatement.bindString(5, iLoggingEvent.getThreadName());
        sQLiteStatement.bindLong(6, (long) computeReferenceMask(iLoggingEvent));
    }

    private void bindLoggingEventArguments(SQLiteStatement sQLiteStatement, Object[] objArr) throws SQLException {
        int i = 0;
        int length = objArr != null ? objArr.length : 0;
        while (i < length && i < 4) {
            sQLiteStatement.bindString(i + 7, asStringTruncatedTo254(objArr[i]));
            i++;
        }
    }

    private void clearExpiredLogs(SQLiteDatabase sQLiteDatabase) {
        if (lastCheckExpired(this.maxHistory, this.lastCleanupTime)) {
            this.lastCleanupTime = System.currentTimeMillis();
            getLogCleaner().performLogCleanup(sQLiteDatabase, this.maxHistory);
        }
    }

    private static short computeReferenceMask(ILoggingEvent iLoggingEvent) {
        short s = 0;
        int size = iLoggingEvent.getMDCPropertyMap() != null ? iLoggingEvent.getMDCPropertyMap().keySet().size() : 0;
        int size2 = iLoggingEvent.getLoggerContextVO().getPropertyMap() != null ? iLoggingEvent.getLoggerContextVO().getPropertyMap().size() : 0;
        if (size > 0 || size2 > 0) {
            s = PROPERTIES_EXIST;
        }
        return iLoggingEvent.getThrowableProxy() != null ? (short) (s | EXCEPTION_EXISTS) : s;
    }

    private void insertException(SQLiteStatement sQLiteStatement, String str, short s, long j) throws SQLException {
        sQLiteStatement.bindLong(1, j);
        sQLiteStatement.bindLong(2, (long) s);
        sQLiteStatement.bindString(3, str);
        sQLiteStatement.executeInsert();
    }

    private void insertProperties(Map<String, String> map, long j) throws SQLException {
        if (map.size() > 0) {
            SQLiteStatement compileStatement = this.f44db.compileStatement(this.insertPropertiesSQL);
            try {
                for (Map.Entry next : map.entrySet()) {
                    compileStatement.bindLong(1, j);
                    compileStatement.bindString(2, (String) next.getKey());
                    compileStatement.bindString(3, (String) next.getValue());
                    compileStatement.executeInsert();
                }
            } finally {
                compileStatement.close();
            }
        }
    }

    private void insertThrowable(IThrowableProxy iThrowableProxy, long j) throws SQLException {
        SQLiteStatement compileStatement = this.f44db.compileStatement(this.insertExceptionSQL);
        short s = 0;
        while (iThrowableProxy != null) {
            try {
                StringBuilder sb = new StringBuilder();
                ThrowableProxyUtil.subjoinFirstLine(sb, iThrowableProxy);
                String sb2 = sb.toString();
                short s2 = (short) (s + PROPERTIES_EXIST);
                insertException(compileStatement, sb2, s, j);
                int commonFrames = iThrowableProxy.getCommonFrames();
                StackTraceElementProxy[] stackTraceElementProxyArray = iThrowableProxy.getStackTraceElementProxyArray();
                s = s2;
                int i = 0;
                while (i < stackTraceElementProxyArray.length - commonFrames) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(9);
                    ThrowableProxyUtil.subjoinSTEP(sb3, stackTraceElementProxyArray[i]);
                    String sb4 = sb3.toString();
                    short s3 = (short) (s + PROPERTIES_EXIST);
                    insertException(compileStatement, sb4, s, j);
                    i++;
                    s = s3;
                }
                if (commonFrames > 0) {
                    short s4 = (short) (s + PROPERTIES_EXIST);
                    insertException(compileStatement, 9 + "... " + commonFrames + " common frames omitted", s, j);
                    s = s4;
                }
                iThrowableProxy = iThrowableProxy.getCause();
            } catch (Throwable th) {
                compileStatement.close();
                throw th;
            }
        }
        compileStatement.close();
    }

    private boolean lastCheckExpired(Duration duration, long j) {
        if (duration == null || duration.getMilliseconds() <= 0) {
            return false;
        }
        return j <= 0 || System.currentTimeMillis() - j >= duration.getMilliseconds();
    }

    private Map<String, String> mergePropertyMaps(ILoggingEvent iLoggingEvent) {
        HashMap hashMap = new HashMap();
        Map<String, String> propertyMap = iLoggingEvent.getLoggerContextVO().getPropertyMap();
        if (propertyMap != null) {
            hashMap.putAll(propertyMap);
        }
        Map<String, String> mDCPropertyMap = iLoggingEvent.getMDCPropertyMap();
        if (mDCPropertyMap != null) {
            hashMap.putAll(mDCPropertyMap);
        }
        return hashMap;
    }

    private void secondarySubAppend(ILoggingEvent iLoggingEvent, long j) throws SQLException {
        insertProperties(mergePropertyMaps(iLoggingEvent), j);
        if (iLoggingEvent.getThrowableProxy() != null) {
            insertThrowable(iLoggingEvent.getThrowableProxy(), j);
        }
    }

    private long subAppend(ILoggingEvent iLoggingEvent, SQLiteStatement sQLiteStatement) throws SQLException {
        bindLoggingEvent(sQLiteStatement, iLoggingEvent);
        bindLoggingEventArguments(sQLiteStatement, iLoggingEvent.getArgumentArray());
        bindCallerData(sQLiteStatement, iLoggingEvent.getCallerData());
        try {
            return sQLiteStatement.executeInsert();
        } catch (SQLiteException e) {
            addWarn("Failed to insert loggingEvent", e);
            return -1;
        }
    }

    public void append(ILoggingEvent iLoggingEvent) {
        SQLiteStatement compileStatement;
        if (isStarted()) {
            try {
                clearExpiredLogs(this.f44db);
                compileStatement = this.f44db.compileStatement(this.insertSQL);
                this.f44db.beginTransaction();
                long subAppend = subAppend(iLoggingEvent, compileStatement);
                if (subAppend != -1) {
                    secondarySubAppend(iLoggingEvent, subAppend);
                    this.f44db.setTransactionSuccessful();
                }
                if (this.f44db.inTransaction()) {
                    this.f44db.endTransaction();
                }
                compileStatement.close();
            } catch (Throwable th) {
                addError("Cannot append event", th);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        this.f44db.close();
    }

    public File getDatabaseFile(String str) {
        File file = (str == null || str.trim().length() <= 0) ? null : new File(str);
        if (file == null || file.isDirectory()) {
            if (getContext() == null) {
                return null;
            }
            String property = getContext().getProperty(CoreConstants.PACKAGE_NAME_KEY);
            if (property != null && property.trim().length() > 0) {
                return new File(CommonPathUtil.getDatabaseDirectoryPath(property), "logback.db");
            }
        }
        return file;
    }

    public String getFilename() {
        return this.filename;
    }

    public SQLiteLogCleaner getLogCleaner() {
        if (this.logCleaner == null) {
            this.logCleaner = new SQLiteLogCleaner() {
                public void performLogCleanup(SQLiteDatabase sQLiteDatabase, Duration duration) {
                    sQLiteDatabase.execSQL(SQLBuilder.buildDeleteExpiredLogsSQL(SQLiteAppender.this.dbNameResolver, System.currentTimeMillis() - duration.getMilliseconds()));
                }
            };
        }
        return this.logCleaner;
    }

    public String getMaxHistory() {
        Duration duration = this.maxHistory;
        return duration != null ? duration.toString() : "";
    }

    public long getMaxHistoryMs() {
        Duration duration = this.maxHistory;
        if (duration != null) {
            return duration.getMilliseconds();
        }
        return 0;
    }

    public void setDbNameResolver(DBNameResolver dBNameResolver) {
        this.dbNameResolver = dBNameResolver;
    }

    public void setFilename(String str) {
        this.filename = str;
    }

    public void setLogCleaner(SQLiteLogCleaner sQLiteLogCleaner) {
        this.logCleaner = sQLiteLogCleaner;
    }

    public void setMaxHistory(String str) {
        this.maxHistory = Duration.valueOf(str);
    }

    public void start() {
        boolean z = false;
        this.started = false;
        File databaseFile = getDatabaseFile(this.filename);
        if (databaseFile == null) {
            addError("Cannot determine database filename");
            return;
        }
        try {
            databaseFile.getParentFile().mkdirs();
            addInfo("db path: " + databaseFile.getAbsolutePath());
            this.f44db = SQLiteDatabase.openOrCreateDatabase(databaseFile.getPath(), (SQLiteDatabase.CursorFactory) null);
            z = true;
        } catch (SQLiteException e) {
            addError("Cannot open database", e);
        }
        if (z) {
            if (this.dbNameResolver == null) {
                this.dbNameResolver = new DefaultDBNameResolver();
            }
            this.insertExceptionSQL = SQLBuilder.buildInsertExceptionSQL(this.dbNameResolver);
            this.insertPropertiesSQL = SQLBuilder.buildInsertPropertiesSQL(this.dbNameResolver);
            this.insertSQL = SQLBuilder.buildInsertSQL(this.dbNameResolver);
            try {
                this.f44db.execSQL(SQLBuilder.buildCreateLoggingEventTableSQL(this.dbNameResolver));
                this.f44db.execSQL(SQLBuilder.buildCreatePropertyTableSQL(this.dbNameResolver));
                this.f44db.execSQL(SQLBuilder.buildCreateExceptionTableSQL(this.dbNameResolver));
                clearExpiredLogs(this.f44db);
                super.start();
                this.started = true;
            } catch (SQLiteException e2) {
                addError("Cannot create database tables", e2);
            }
        }
    }

    public void stop() {
        this.f44db.close();
        this.lastCleanupTime = 0;
    }
}
