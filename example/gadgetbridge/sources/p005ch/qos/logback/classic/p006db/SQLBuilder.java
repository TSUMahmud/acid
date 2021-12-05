package p005ch.qos.logback.classic.p006db;

import p005ch.qos.logback.classic.p006db.names.ColumnName;
import p005ch.qos.logback.classic.p006db.names.DBNameResolver;
import p005ch.qos.logback.classic.p006db.names.TableName;

/* renamed from: ch.qos.logback.classic.db.SQLBuilder */
public class SQLBuilder {
    public static String buildCreateExceptionTableSQL(DBNameResolver dBNameResolver) {
        return "CREATE TABLE IF NOT EXISTS " + dBNameResolver.getTableName(TableName.LOGGING_EVENT_EXCEPTION) + " (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + " BIGINT NOT NULL, " + dBNameResolver.getColumnName(ColumnName.I) + " SMALLINT NOT NULL, " + dBNameResolver.getColumnName(ColumnName.TRACE_LINE) + " VARCHAR(254) NOT NULL, " + "PRIMARY KEY (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ", " + dBNameResolver.getColumnName(ColumnName.I) + "), " + "FOREIGN KEY (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ") " + "REFERENCES " + dBNameResolver.getTableName(TableName.LOGGING_EVENT) + " (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ") " + ")";
    }

    public static String buildCreateLoggingEventTableSQL(DBNameResolver dBNameResolver) {
        return "CREATE TABLE IF NOT EXISTS " + dBNameResolver.getTableName(TableName.LOGGING_EVENT) + " (" + dBNameResolver.getColumnName(ColumnName.TIMESTMP) + " BIGINT NOT NULL, " + dBNameResolver.getColumnName(ColumnName.FORMATTED_MESSAGE) + " TEXT NOT NULL, " + dBNameResolver.getColumnName(ColumnName.LOGGER_NAME) + " VARCHAR(254) NOT NULL, " + dBNameResolver.getColumnName(ColumnName.LEVEL_STRING) + " VARCHAR(254) NOT NULL, " + dBNameResolver.getColumnName(ColumnName.THREAD_NAME) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.REFERENCE_FLAG) + " SMALLINT, " + dBNameResolver.getColumnName(ColumnName.ARG0) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.ARG1) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.ARG2) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.ARG3) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.CALLER_FILENAME) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.CALLER_CLASS) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.CALLER_METHOD) + " VARCHAR(254), " + dBNameResolver.getColumnName(ColumnName.CALLER_LINE) + " CHAR(4), " + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" + ")";
    }

    public static String buildCreatePropertyTableSQL(DBNameResolver dBNameResolver) {
        return "CREATE TABLE IF NOT EXISTS " + dBNameResolver.getTableName(TableName.LOGGING_EVENT_PROPERTY) + " (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + " BIGINT NOT NULL, " + dBNameResolver.getColumnName(ColumnName.MAPPED_KEY) + " VARCHAR(254) NOT NULL, " + dBNameResolver.getColumnName(ColumnName.MAPPED_VALUE) + " VARCHAR(254) NOT NULL, " + "PRIMARY KEY (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ", " + dBNameResolver.getColumnName(ColumnName.MAPPED_KEY) + "), " + "FOREIGN KEY (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ") " + "REFERENCES " + dBNameResolver.getTableName(TableName.LOGGING_EVENT) + " (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ") " + ")";
    }

    public static String buildDeleteExpiredLogsSQL(DBNameResolver dBNameResolver, long j) {
        return "DELETE FROM " + dBNameResolver.getTableName(TableName.LOGGING_EVENT) + " WHERE " + dBNameResolver.getColumnName(ColumnName.TIMESTMP) + " <= " + j + ";";
    }

    public static String buildInsertExceptionSQL(DBNameResolver dBNameResolver) {
        return "INSERT INTO " + dBNameResolver.getTableName(TableName.LOGGING_EVENT_EXCEPTION) + " (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ", " + dBNameResolver.getColumnName(ColumnName.I) + ", " + dBNameResolver.getColumnName(ColumnName.TRACE_LINE) + ") " + "VALUES (?, ?, ?)";
    }

    public static String buildInsertPropertiesSQL(DBNameResolver dBNameResolver) {
        return "INSERT INTO " + dBNameResolver.getTableName(TableName.LOGGING_EVENT_PROPERTY) + " (" + dBNameResolver.getColumnName(ColumnName.EVENT_ID) + ", " + dBNameResolver.getColumnName(ColumnName.MAPPED_KEY) + ", " + dBNameResolver.getColumnName(ColumnName.MAPPED_VALUE) + ") " + "VALUES (?, ?, ?)";
    }

    public static String buildInsertSQL(DBNameResolver dBNameResolver) {
        return "INSERT INTO " + dBNameResolver.getTableName(TableName.LOGGING_EVENT) + " (" + dBNameResolver.getColumnName(ColumnName.TIMESTMP) + ", " + dBNameResolver.getColumnName(ColumnName.FORMATTED_MESSAGE) + ", " + dBNameResolver.getColumnName(ColumnName.LOGGER_NAME) + ", " + dBNameResolver.getColumnName(ColumnName.LEVEL_STRING) + ", " + dBNameResolver.getColumnName(ColumnName.THREAD_NAME) + ", " + dBNameResolver.getColumnName(ColumnName.REFERENCE_FLAG) + ", " + dBNameResolver.getColumnName(ColumnName.ARG0) + ", " + dBNameResolver.getColumnName(ColumnName.ARG1) + ", " + dBNameResolver.getColumnName(ColumnName.ARG2) + ", " + dBNameResolver.getColumnName(ColumnName.ARG3) + ", " + dBNameResolver.getColumnName(ColumnName.CALLER_FILENAME) + ", " + dBNameResolver.getColumnName(ColumnName.CALLER_CLASS) + ", " + dBNameResolver.getColumnName(ColumnName.CALLER_METHOD) + ", " + dBNameResolver.getColumnName(ColumnName.CALLER_LINE) + ") " + "VALUES (?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
}
