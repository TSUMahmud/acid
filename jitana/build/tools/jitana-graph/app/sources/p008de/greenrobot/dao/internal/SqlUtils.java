package p008de.greenrobot.dao.internal;

import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.DaoException;
import p008de.greenrobot.dao.Property;

/* renamed from: de.greenrobot.dao.internal.SqlUtils */
public class SqlUtils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static StringBuilder appendProperty(StringBuilder builder, String tablePrefix, Property property) {
        if (tablePrefix != null) {
            builder.append(tablePrefix);
            builder.append('.');
        }
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(property.columnName);
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        return builder;
    }

    public static StringBuilder appendColumn(StringBuilder builder, String column) {
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(column);
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        return builder;
    }

    public static StringBuilder appendColumn(StringBuilder builder, String tableAlias, String column) {
        builder.append(tableAlias);
        builder.append(".\"");
        builder.append(column);
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        return builder;
    }

    public static StringBuilder appendColumns(StringBuilder builder, String tableAlias, String[] columns) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            appendColumn(builder, tableAlias, columns[i]);
            if (i < length - 1) {
                builder.append(CoreConstants.COMMA_CHAR);
            }
        }
        return builder;
    }

    public static StringBuilder appendColumns(StringBuilder builder, String[] columns) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
            builder.append(columns[i]);
            builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
            if (i < length - 1) {
                builder.append(CoreConstants.COMMA_CHAR);
            }
        }
        return builder;
    }

    public static StringBuilder appendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }
        return builder;
    }

    public static StringBuilder appendColumnsEqualPlaceholders(StringBuilder builder, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            appendColumn(builder, columns[i]).append("=?");
            if (i < columns.length - 1) {
                builder.append(CoreConstants.COMMA_CHAR);
            }
        }
        return builder;
    }

    public static StringBuilder appendColumnsEqValue(StringBuilder builder, String tableAlias, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            appendColumn(builder, tableAlias, columns[i]).append("=?");
            if (i < columns.length - 1) {
                builder.append(CoreConstants.COMMA_CHAR);
            }
        }
        return builder;
    }

    public static String createSqlInsert(String insertInto, String tablename, String[] columns) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(tablename);
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(" (");
        appendColumns(builder, columns);
        builder.append(") VALUES (");
        appendPlaceholders(builder, columns.length);
        builder.append(CoreConstants.RIGHT_PARENTHESIS_CHAR);
        return builder.toString();
    }

    public static String createSqlSelect(String tablename, String tableAlias, String[] columns, boolean distinct) {
        if (tableAlias == null || tableAlias.length() < 0) {
            throw new DaoException("Table alias required");
        }
        StringBuilder builder = new StringBuilder(distinct ? "SELECT DISTINCT " : "SELECT ");
        appendColumns(builder, tableAlias, columns).append(" FROM ");
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(tablename);
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(' ');
        builder.append(tableAlias);
        builder.append(' ');
        return builder.toString();
    }

    public static String createSqlSelectCountStar(String tablename, String tableAliasOrNull) {
        StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM ");
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(tablename);
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(' ');
        if (tableAliasOrNull != null) {
            builder.append(tableAliasOrNull);
            builder.append(' ');
        }
        return builder.toString();
    }

    public static String createSqlDelete(String tablename, String[] columns) {
        String quotedTablename = CoreConstants.DOUBLE_QUOTE_CHAR + tablename + CoreConstants.DOUBLE_QUOTE_CHAR;
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append(quotedTablename);
        if (columns != null && columns.length > 0) {
            builder.append(" WHERE ");
            appendColumnsEqValue(builder, quotedTablename, columns);
        }
        return builder.toString();
    }

    public static String createSqlUpdate(String tablename, String[] updateColumns, String[] whereColumns) {
        String quotedTablename = CoreConstants.DOUBLE_QUOTE_CHAR + tablename + CoreConstants.DOUBLE_QUOTE_CHAR;
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append(quotedTablename);
        builder.append(" SET ");
        appendColumnsEqualPlaceholders(builder, updateColumns);
        builder.append(" WHERE ");
        appendColumnsEqValue(builder, quotedTablename, whereColumns);
        return builder.toString();
    }

    public static String escapeBlobArgument(byte[] bytes) {
        return "X'" + toHex(bytes) + CoreConstants.SINGLE_QUOTE_CHAR;
    }

    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[(bytes.length * 2)];
        for (int i = 0; i < bytes.length; i++) {
            int byteValue = bytes[i] & 255;
            char[] cArr = HEX_ARRAY;
            hexChars[i * 2] = cArr[byteValue >>> 4];
            hexChars[(i * 2) + 1] = cArr[byteValue & 15];
        }
        return new String(hexChars);
    }
}
