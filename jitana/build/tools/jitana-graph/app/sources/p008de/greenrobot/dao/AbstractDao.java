package p008de.greenrobot.dao;

import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.identityscope.IdentityScope;
import p008de.greenrobot.dao.identityscope.IdentityScopeLong;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.internal.TableStatements;
import p008de.greenrobot.dao.query.Query;
import p008de.greenrobot.dao.query.QueryBuilder;

/* renamed from: de.greenrobot.dao.AbstractDao */
public abstract class AbstractDao<T, K> {
    protected final DaoConfig config;

    /* renamed from: db */
    protected final SQLiteDatabase f106db;
    protected IdentityScope<K, T> identityScope;
    protected IdentityScopeLong<T> identityScopeLong;
    protected final int pkOrdinal;
    protected final AbstractDaoSession session;
    protected TableStatements statements;

    /* access modifiers changed from: protected */
    public abstract void bindValues(SQLiteStatement sQLiteStatement, T t);

    /* access modifiers changed from: protected */
    public abstract K getKey(T t);

    /* access modifiers changed from: protected */
    public abstract boolean isEntityUpdateable();

    /* access modifiers changed from: protected */
    public abstract T readEntity(Cursor cursor, int i);

    /* access modifiers changed from: protected */
    public abstract void readEntity(Cursor cursor, T t, int i);

    /* access modifiers changed from: protected */
    public abstract K readKey(Cursor cursor, int i);

    /* access modifiers changed from: protected */
    public abstract K updateKeyAfterInsert(T t, long j);

    public AbstractDao(DaoConfig config2) {
        this(config2, (AbstractDaoSession) null);
    }

    public AbstractDao(DaoConfig config2, AbstractDaoSession daoSession) {
        this.config = config2;
        this.session = daoSession;
        this.f106db = config2.f109db;
        this.identityScope = config2.getIdentityScope();
        IdentityScope<K, T> identityScope2 = this.identityScope;
        if (identityScope2 instanceof IdentityScopeLong) {
            this.identityScopeLong = (IdentityScopeLong) identityScope2;
        }
        this.statements = config2.statements;
        this.pkOrdinal = config2.pkProperty != null ? config2.pkProperty.ordinal : -1;
    }

    public AbstractDaoSession getSession() {
        return this.session;
    }

    /* access modifiers changed from: package-private */
    public TableStatements getStatements() {
        return this.config.statements;
    }

    public String getTablename() {
        return this.config.tablename;
    }

    public Property[] getProperties() {
        return this.config.properties;
    }

    public Property getPkProperty() {
        return this.config.pkProperty;
    }

    public String[] getAllColumns() {
        return this.config.allColumns;
    }

    public String[] getPkColumns() {
        return this.config.pkColumns;
    }

    public String[] getNonPkColumns() {
        return this.config.nonPkColumns;
    }

    public T load(K key) {
        T entity;
        assertSinglePk();
        if (key == null) {
            return null;
        }
        IdentityScope<K, T> identityScope2 = this.identityScope;
        if (identityScope2 != null && (entity = identityScope2.get(key)) != null) {
            return entity;
        }
        return loadUniqueAndCloseCursor(this.f106db.rawQuery(this.statements.getSelectByKey(), new String[]{key.toString()}));
    }

    public T loadByRowId(long rowId) {
        return loadUniqueAndCloseCursor(this.f106db.rawQuery(this.statements.getSelectByRowId(), new String[]{Long.toString(rowId)}));
    }

    /* access modifiers changed from: protected */
    public T loadUniqueAndCloseCursor(Cursor cursor) {
        try {
            return loadUnique(cursor);
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: protected */
    public T loadUnique(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return null;
        }
        if (cursor.isLast()) {
            return loadCurrent(cursor, 0, true);
        }
        throw new DaoException("Expected unique result, but count was " + cursor.getCount());
    }

    public List<T> loadAll() {
        return loadAllAndCloseCursor(this.f106db.rawQuery(this.statements.getSelectAll(), (String[]) null));
    }

    public boolean detach(T entity) {
        if (this.identityScope == null) {
            return false;
        }
        return this.identityScope.detach(getKeyVerified(entity), entity);
    }

    public void detachAll() {
        IdentityScope<K, T> identityScope2 = this.identityScope;
        if (identityScope2 != null) {
            identityScope2.clear();
        }
    }

    /* access modifiers changed from: protected */
    public List<T> loadAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public void insertInTx(Iterable<T> entities) {
        insertInTx(entities, isEntityUpdateable());
    }

    public void insertInTx(T... entities) {
        insertInTx(Arrays.asList(entities), isEntityUpdateable());
    }

    public void insertInTx(Iterable<T> entities, boolean setPrimaryKey) {
        executeInsertInTx(this.statements.getInsertStatement(), entities, setPrimaryKey);
    }

    public void insertOrReplaceInTx(Iterable<T> entities, boolean setPrimaryKey) {
        executeInsertInTx(this.statements.getInsertOrReplaceStatement(), entities, setPrimaryKey);
    }

    public void insertOrReplaceInTx(Iterable<T> entities) {
        insertOrReplaceInTx(entities, isEntityUpdateable());
    }

    public void insertOrReplaceInTx(T... entities) {
        insertOrReplaceInTx(Arrays.asList(entities), isEntityUpdateable());
    }

    private void executeInsertInTx(SQLiteStatement stmt, Iterable<T> entities, boolean setPrimaryKey) {
        this.f106db.beginTransaction();
        try {
            synchronized (stmt) {
                if (this.identityScope != null) {
                    this.identityScope.lock();
                }
                try {
                    for (T entity : entities) {
                        bindValues(stmt, entity);
                        if (setPrimaryKey) {
                            updateKeyAfterInsertAndAttach(entity, stmt.executeInsert(), false);
                        } else {
                            stmt.execute();
                        }
                    }
                } finally {
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                }
            }
            this.f106db.setTransactionSuccessful();
        } finally {
            this.f106db.endTransaction();
        }
    }

    public long insert(T entity) {
        return executeInsert(entity, this.statements.getInsertStatement());
    }

    public long insertWithoutSettingPk(T entity) {
        long rowId;
        SQLiteStatement stmt = this.statements.getInsertStatement();
        if (this.f106db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                bindValues(stmt, entity);
                rowId = stmt.executeInsert();
            }
        } else {
            this.f106db.beginTransaction();
            try {
                synchronized (stmt) {
                    bindValues(stmt, entity);
                    rowId = stmt.executeInsert();
                }
                this.f106db.setTransactionSuccessful();
                this.f106db.endTransaction();
            } catch (Throwable th) {
                this.f106db.endTransaction();
                throw th;
            }
        }
        return rowId;
    }

    public long insertOrReplace(T entity) {
        return executeInsert(entity, this.statements.getInsertOrReplaceStatement());
    }

    private long executeInsert(T entity, SQLiteStatement stmt) {
        long rowId;
        if (this.f106db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                bindValues(stmt, entity);
                rowId = stmt.executeInsert();
            }
        } else {
            this.f106db.beginTransaction();
            try {
                synchronized (stmt) {
                    bindValues(stmt, entity);
                    rowId = stmt.executeInsert();
                }
                this.f106db.setTransactionSuccessful();
                this.f106db.endTransaction();
            } catch (Throwable th) {
                this.f106db.endTransaction();
                throw th;
            }
        }
        updateKeyAfterInsertAndAttach(entity, rowId, true);
        return rowId;
    }

    /* access modifiers changed from: protected */
    public void updateKeyAfterInsertAndAttach(T entity, long rowId, boolean lock) {
        if (rowId != -1) {
            attachEntity(updateKeyAfterInsert(entity, rowId), entity, lock);
        } else {
            DaoLog.m26w("Could not insert row (executeInsert returned -1)");
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<T> loadAllFromCursor(android.database.Cursor r7) {
        /*
            r6 = this;
            int r0 = r7.getCount()
            if (r0 != 0) goto L_0x000c
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            return r1
        L_0x000c:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>(r0)
            r2 = 0
            r3 = 0
            boolean r4 = r7 instanceof android.database.CrossProcessCursor
            if (r4 == 0) goto L_0x004e
            r4 = r7
            android.database.CrossProcessCursor r4 = (android.database.CrossProcessCursor) r4
            android.database.CursorWindow r2 = r4.getWindow()
            if (r2 == 0) goto L_0x004e
            int r4 = r2.getNumRows()
            if (r4 != r0) goto L_0x002e
            de.greenrobot.dao.internal.FastCursor r4 = new de.greenrobot.dao.internal.FastCursor
            r4.<init>(r2)
            r7 = r4
            r3 = 1
            goto L_0x004e
        L_0x002e:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Window vs. result size: "
            r4.append(r5)
            int r5 = r2.getNumRows()
            r4.append(r5)
            java.lang.String r5 = "/"
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            p008de.greenrobot.dao.DaoLog.m18d(r4)
        L_0x004e:
            boolean r4 = r7.moveToFirst()
            if (r4 == 0) goto L_0x008b
            de.greenrobot.dao.identityscope.IdentityScope<K, T> r4 = r6.identityScope
            if (r4 == 0) goto L_0x0060
            r4.lock()
            de.greenrobot.dao.identityscope.IdentityScope<K, T> r4 = r6.identityScope
            r4.reserveRoom(r0)
        L_0x0060:
            if (r3 != 0) goto L_0x006c
            if (r2 == 0) goto L_0x006c
            de.greenrobot.dao.identityscope.IdentityScope<K, T> r4 = r6.identityScope     // Catch:{ all -> 0x0082 }
            if (r4 == 0) goto L_0x006c
            r6.loadAllUnlockOnWindowBounds(r7, r2, r1)     // Catch:{ all -> 0x0082 }
            goto L_0x007a
        L_0x006c:
            r4 = 0
            java.lang.Object r4 = r6.loadCurrent(r7, r4, r4)     // Catch:{ all -> 0x0082 }
            r1.add(r4)     // Catch:{ all -> 0x0082 }
            boolean r4 = r7.moveToNext()     // Catch:{ all -> 0x0082 }
            if (r4 != 0) goto L_0x006c
        L_0x007a:
            de.greenrobot.dao.identityscope.IdentityScope<K, T> r4 = r6.identityScope
            if (r4 == 0) goto L_0x008b
            r4.unlock()
            goto L_0x008b
        L_0x0082:
            r4 = move-exception
            de.greenrobot.dao.identityscope.IdentityScope<K, T> r5 = r6.identityScope
            if (r5 == 0) goto L_0x008a
            r5.unlock()
        L_0x008a:
            throw r4
        L_0x008b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: p008de.greenrobot.dao.AbstractDao.loadAllFromCursor(android.database.Cursor):java.util.List");
    }

    private void loadAllUnlockOnWindowBounds(Cursor cursor, CursorWindow window, List<T> list) {
        int windowEnd = window.getStartPosition() + window.getNumRows();
        int row = 0;
        while (true) {
            list.add(loadCurrent(cursor, 0, false));
            int row2 = row + 1;
            if (row2 >= windowEnd) {
                CursorWindow window2 = moveToNextUnlocked(cursor);
                if (window2 != null) {
                    windowEnd = window2.getStartPosition() + window2.getNumRows();
                } else {
                    return;
                }
            } else if (cursor.moveToNext() == 0) {
                return;
            }
            row = row2 + 1;
        }
    }

    private CursorWindow moveToNextUnlocked(Cursor cursor) {
        CursorWindow cursorWindow;
        this.identityScope.unlock();
        try {
            if (cursor.moveToNext()) {
                cursorWindow = ((CrossProcessCursor) cursor).getWindow();
            } else {
                cursorWindow = null;
            }
            return cursorWindow;
        } finally {
            this.identityScope.lock();
        }
    }

    /* access modifiers changed from: protected */
    public final T loadCurrent(Cursor cursor, int offset, boolean lock) {
        if (this.identityScopeLong != null) {
            if (offset != 0 && cursor.isNull(this.pkOrdinal + offset)) {
                return null;
            }
            long key = cursor.getLong(this.pkOrdinal + offset);
            IdentityScopeLong<T> identityScopeLong2 = this.identityScopeLong;
            T entity = lock ? identityScopeLong2.get2(key) : identityScopeLong2.get2NoLock(key);
            if (entity != null) {
                return entity;
            }
            T entity2 = readEntity(cursor, offset);
            attachEntity(entity2);
            if (lock) {
                this.identityScopeLong.put2(key, entity2);
            } else {
                this.identityScopeLong.put2NoLock(key, entity2);
            }
            return entity2;
        } else if (this.identityScope != null) {
            K key2 = readKey(cursor, offset);
            if (offset != 0 && key2 == null) {
                return null;
            }
            IdentityScope<K, T> identityScope2 = this.identityScope;
            T entity3 = lock ? identityScope2.get(key2) : identityScope2.getNoLock(key2);
            if (entity3 != null) {
                return entity3;
            }
            T entity4 = readEntity(cursor, offset);
            attachEntity(key2, entity4, lock);
            return entity4;
        } else if (offset != 0 && readKey(cursor, offset) == null) {
            return null;
        } else {
            T entity5 = readEntity(cursor, offset);
            attachEntity(entity5);
            return entity5;
        }
    }

    /* access modifiers changed from: protected */
    public final <O> O loadCurrentOther(AbstractDao<O, ?> dao, Cursor cursor, int offset) {
        return dao.loadCurrent(cursor, offset, true);
    }

    public List<T> queryRaw(String where, String... selectionArg) {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return loadAllAndCloseCursor(sQLiteDatabase.rawQuery(this.statements.getSelectAll() + where, selectionArg));
    }

    public Query<T> queryRawCreate(String where, Object... selectionArg) {
        return queryRawCreateListArgs(where, Arrays.asList(selectionArg));
    }

    public Query<T> queryRawCreateListArgs(String where, Collection<Object> selectionArg) {
        return Query.internalCreate(this, this.statements.getSelectAll() + where, selectionArg.toArray());
    }

    public void deleteAll() {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        sQLiteDatabase.execSQL("DELETE FROM '" + this.config.tablename + "'");
        IdentityScope<K, T> identityScope2 = this.identityScope;
        if (identityScope2 != null) {
            identityScope2.clear();
        }
    }

    public void delete(T entity) {
        assertSinglePk();
        deleteByKey(getKeyVerified(entity));
    }

    public void deleteByKey(K key) {
        assertSinglePk();
        SQLiteStatement stmt = this.statements.getDeleteStatement();
        if (this.f106db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                deleteByKeyInsideSynchronized(key, stmt);
            }
        } else {
            this.f106db.beginTransaction();
            try {
                synchronized (stmt) {
                    deleteByKeyInsideSynchronized(key, stmt);
                }
                this.f106db.setTransactionSuccessful();
                this.f106db.endTransaction();
            } catch (Throwable th) {
                this.f106db.endTransaction();
                throw th;
            }
        }
        IdentityScope<K, T> identityScope2 = this.identityScope;
        if (identityScope2 != null) {
            identityScope2.remove(key);
        }
    }

    private void deleteByKeyInsideSynchronized(K key, SQLiteStatement stmt) {
        if (key instanceof Long) {
            stmt.bindLong(1, ((Long) key).longValue());
        } else if (key != null) {
            stmt.bindString(1, key.toString());
        } else {
            throw new DaoException("Cannot delete entity, key is null");
        }
        stmt.execute();
    }

    private void deleteInTxInternal(Iterable<T> entities, Iterable<K> keys) {
        assertSinglePk();
        SQLiteStatement stmt = this.statements.getDeleteStatement();
        List<K> keysToRemoveFromIdentityScope = null;
        this.f106db.beginTransaction();
        try {
            synchronized (stmt) {
                if (this.identityScope != null) {
                    this.identityScope.lock();
                    keysToRemoveFromIdentityScope = new ArrayList<>();
                }
                if (entities != null) {
                    try {
                        for (T entity : entities) {
                            K key = getKeyVerified(entity);
                            deleteByKeyInsideSynchronized(key, stmt);
                            if (keysToRemoveFromIdentityScope != null) {
                                keysToRemoveFromIdentityScope.add(key);
                            }
                        }
                    } catch (Throwable th) {
                        if (this.identityScope != null) {
                            this.identityScope.unlock();
                        }
                        throw th;
                    }
                }
                if (keys != null) {
                    for (K key2 : keys) {
                        deleteByKeyInsideSynchronized(key2, stmt);
                        if (keysToRemoveFromIdentityScope != null) {
                            keysToRemoveFromIdentityScope.add(key2);
                        }
                    }
                }
                if (this.identityScope != null) {
                    this.identityScope.unlock();
                }
            }
            this.f106db.setTransactionSuccessful();
            if (!(keysToRemoveFromIdentityScope == null || this.identityScope == null)) {
                this.identityScope.remove(keysToRemoveFromIdentityScope);
            }
        } finally {
            this.f106db.endTransaction();
        }
    }

    public void deleteInTx(Iterable<T> entities) {
        deleteInTxInternal(entities, (Iterable) null);
    }

    public void deleteInTx(T... entities) {
        deleteInTxInternal(Arrays.asList(entities), (Iterable) null);
    }

    public void deleteByKeyInTx(Iterable<K> keys) {
        deleteInTxInternal((Iterable) null, keys);
    }

    public void deleteByKeyInTx(K... keys) {
        deleteInTxInternal((Iterable) null, Arrays.asList(keys));
    }

    public void refresh(T entity) {
        assertSinglePk();
        K key = getKeyVerified(entity);
        Cursor cursor = this.f106db.rawQuery(this.statements.getSelectByKey(), new String[]{key.toString()});
        try {
            if (!cursor.moveToFirst()) {
                throw new DaoException("Entity does not exist in the database anymore: " + entity.getClass() + " with key " + key);
            } else if (cursor.isLast()) {
                readEntity(cursor, entity, 0);
                attachEntity(key, entity, true);
            } else {
                throw new DaoException("Expected unique result, but count was " + cursor.getCount());
            }
        } finally {
            cursor.close();
        }
    }

    public void update(T entity) {
        assertSinglePk();
        SQLiteStatement stmt = this.statements.getUpdateStatement();
        if (this.f106db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                updateInsideSynchronized(entity, stmt, true);
            }
            return;
        }
        this.f106db.beginTransaction();
        try {
            synchronized (stmt) {
                updateInsideSynchronized(entity, stmt, true);
            }
            this.f106db.setTransactionSuccessful();
            this.f106db.endTransaction();
        } catch (Throwable th) {
            this.f106db.endTransaction();
            throw th;
        }
    }

    public QueryBuilder<T> queryBuilder() {
        return QueryBuilder.internalCreate(this);
    }

    /* access modifiers changed from: protected */
    public void updateInsideSynchronized(T entity, SQLiteStatement stmt, boolean lock) {
        bindValues(stmt, entity);
        int index = this.config.allColumns.length + 1;
        K key = getKey(entity);
        if (key instanceof Long) {
            stmt.bindLong(index, ((Long) key).longValue());
        } else if (key != null) {
            stmt.bindString(index, key.toString());
        } else {
            throw new DaoException("Cannot update entity without key - was it inserted before?");
        }
        stmt.execute();
        attachEntity(key, entity, lock);
    }

    /* access modifiers changed from: protected */
    public final void attachEntity(K key, T entity, boolean lock) {
        attachEntity(entity);
        IdentityScope<K, T> identityScope2 = this.identityScope;
        if (identityScope2 != null && key != null) {
            if (lock) {
                identityScope2.put(key, entity);
            } else {
                identityScope2.putNoLock(key, entity);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void attachEntity(T t) {
    }

    public void updateInTx(Iterable<T> entities) {
        SQLiteStatement stmt = this.statements.getUpdateStatement();
        this.f106db.beginTransaction();
        try {
            synchronized (stmt) {
                if (this.identityScope != null) {
                    this.identityScope.lock();
                }
                try {
                    for (T entity : entities) {
                        updateInsideSynchronized(entity, stmt, false);
                    }
                } finally {
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                }
            }
            this.f106db.setTransactionSuccessful();
            try {
                this.f106db.endTransaction();
            } catch (RuntimeException e) {
                if (0 != 0) {
                    DaoLog.m27w("Could not end transaction (rethrowing initial exception)", e);
                    throw null;
                }
                throw e;
            }
        } catch (RuntimeException e2) {
            RuntimeException txEx = e2;
            try {
                this.f106db.endTransaction();
            } catch (RuntimeException e3) {
                DaoLog.m27w("Could not end transaction (rethrowing initial exception)", e3);
                throw txEx;
            }
        } catch (Throwable th) {
            try {
                this.f106db.endTransaction();
                throw th;
            } catch (RuntimeException e4) {
                if (0 != 0) {
                    DaoLog.m27w("Could not end transaction (rethrowing initial exception)", e4);
                    throw null;
                }
                throw e4;
            }
        }
    }

    public void updateInTx(T... entities) {
        updateInTx(Arrays.asList(entities));
    }

    /* access modifiers changed from: protected */
    public void assertSinglePk() {
        if (this.config.pkColumns.length != 1) {
            throw new DaoException(this + " (" + this.config.tablename + ") does not have a single-column primary key");
        }
    }

    public long count() {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return DatabaseUtils.queryNumEntries(sQLiteDatabase, CoreConstants.SINGLE_QUOTE_CHAR + this.config.tablename + CoreConstants.SINGLE_QUOTE_CHAR);
    }

    /* access modifiers changed from: protected */
    public K getKeyVerified(T entity) {
        K key = getKey(entity);
        if (key != null) {
            return key;
        }
        if (entity == null) {
            throw new NullPointerException("Entity may not be null");
        }
        throw new DaoException("Entity has no key");
    }

    public SQLiteDatabase getDatabase() {
        return this.f106db;
    }
}
