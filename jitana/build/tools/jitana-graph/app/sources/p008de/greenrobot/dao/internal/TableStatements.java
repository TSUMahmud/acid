package p008de.greenrobot.dao.internal;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/* renamed from: de.greenrobot.dao.internal.TableStatements */
public class TableStatements {
    private final String[] allColumns;

    /* renamed from: db */
    private final SQLiteDatabase f110db;
    private volatile SQLiteStatement deleteStatement;
    private volatile SQLiteStatement insertOrReplaceStatement;
    private volatile SQLiteStatement insertStatement;
    private final String[] pkColumns;
    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;
    private volatile String selectKeys;
    private final String tablename;
    private volatile SQLiteStatement updateStatement;

    public TableStatements(SQLiteDatabase db, String tablename2, String[] allColumns2, String[] pkColumns2) {
        this.f110db = db;
        this.tablename = tablename2;
        this.allColumns = allColumns2;
        this.pkColumns = pkColumns2;
    }

    public SQLiteStatement getInsertStatement() {
        if (this.insertStatement == null) {
            SQLiteStatement newInsertStatement = this.f110db.compileStatement(SqlUtils.createSqlInsert("INSERT INTO ", this.tablename, this.allColumns));
            synchronized (this) {
                if (this.insertStatement == null) {
                    this.insertStatement = newInsertStatement;
                }
            }
            if (this.insertStatement != newInsertStatement) {
                newInsertStatement.close();
            }
        }
        return this.insertStatement;
    }

    public SQLiteStatement getInsertOrReplaceStatement() {
        if (this.insertOrReplaceStatement == null) {
            SQLiteStatement newInsertOrReplaceStatement = this.f110db.compileStatement(SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ", this.tablename, this.allColumns));
            synchronized (this) {
                if (this.insertOrReplaceStatement == null) {
                    this.insertOrReplaceStatement = newInsertOrReplaceStatement;
                }
            }
            if (this.insertOrReplaceStatement != newInsertOrReplaceStatement) {
                newInsertOrReplaceStatement.close();
            }
        }
        return this.insertOrReplaceStatement;
    }

    public SQLiteStatement getDeleteStatement() {
        if (this.deleteStatement == null) {
            SQLiteStatement newDeleteStatement = this.f110db.compileStatement(SqlUtils.createSqlDelete(this.tablename, this.pkColumns));
            synchronized (this) {
                if (this.deleteStatement == null) {
                    this.deleteStatement = newDeleteStatement;
                }
            }
            if (this.deleteStatement != newDeleteStatement) {
                newDeleteStatement.close();
            }
        }
        return this.deleteStatement;
    }

    public SQLiteStatement getUpdateStatement() {
        if (this.updateStatement == null) {
            SQLiteStatement newUpdateStatement = this.f110db.compileStatement(SqlUtils.createSqlUpdate(this.tablename, this.allColumns, this.pkColumns));
            synchronized (this) {
                if (this.updateStatement == null) {
                    this.updateStatement = newUpdateStatement;
                }
            }
            if (this.updateStatement != newUpdateStatement) {
                newUpdateStatement.close();
            }
        }
        return this.updateStatement;
    }

    public String getSelectAll() {
        if (this.selectAll == null) {
            this.selectAll = SqlUtils.createSqlSelect(this.tablename, "T", this.allColumns, false);
        }
        return this.selectAll;
    }

    public String getSelectKeys() {
        if (this.selectKeys == null) {
            this.selectKeys = SqlUtils.createSqlSelect(this.tablename, "T", this.pkColumns, false);
        }
        return this.selectKeys;
    }

    public String getSelectByKey() {
        if (this.selectByKey == null) {
            StringBuilder builder = new StringBuilder(getSelectAll());
            builder.append("WHERE ");
            SqlUtils.appendColumnsEqValue(builder, "T", this.pkColumns);
            this.selectByKey = builder.toString();
        }
        return this.selectByKey;
    }

    public String getSelectByRowId() {
        if (this.selectByRowId == null) {
            this.selectByRowId = getSelectAll() + "WHERE ROWID=?";
        }
        return this.selectByRowId;
    }
}
