package p008de.greenrobot.dao.test;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.SqlUtils;

/* renamed from: de.greenrobot.dao.test.AbstractDaoTestSinglePk */
public abstract class AbstractDaoTestSinglePk<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K> {
    private Property pkColumn;
    protected Set<K> usedPks = new HashSet();

    /* access modifiers changed from: protected */
    public abstract T createEntity(K k);

    /* access modifiers changed from: protected */
    public abstract K createRandomPk();

    public AbstractDaoTestSinglePk(Class<D> daoClass) {
        super(daoClass);
    }

    /* access modifiers changed from: protected */
    public void setUp() throws Exception {
        super.setUp();
        for (Property column : this.daoAccess.getProperties()) {
            if (column.primaryKey) {
                if (this.pkColumn == null) {
                    this.pkColumn = column;
                } else {
                    throw new RuntimeException("Test does not work with multiple PK columns");
                }
            }
        }
        if (this.pkColumn == null) {
            throw new RuntimeException("Test does not work without a PK column");
        }
    }

    public void testInsertAndLoad() {
        K pk = nextPk();
        T entity = createEntity(pk);
        this.dao.insert(entity);
        assertEquals(pk, this.daoAccess.getKey(entity));
        T entity2 = this.dao.load(pk);
        assertNotNull(entity2);
        assertEquals(this.daoAccess.getKey(entity), this.daoAccess.getKey(entity2));
    }

    public void testInsertInTx() {
        this.dao.deleteAll();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(list);
        assertEquals((long) list.size(), this.dao.count());
    }

    public void testCount() {
        this.dao.deleteAll();
        assertEquals(0, this.dao.count());
        this.dao.insert(createEntityWithRandomPk());
        assertEquals(1, this.dao.count());
        this.dao.insert(createEntityWithRandomPk());
        assertEquals(2, this.dao.count());
    }

    public void testInsertTwice() {
        T entity = createEntity(nextPk());
        this.dao.insert(entity);
        try {
            this.dao.insert(entity);
            fail("Inserting twice should not work");
        } catch (SQLException e) {
        }
    }

    public void testInsertOrReplaceTwice() {
        T entity = createEntityWithRandomPk();
        long rowId1 = this.dao.insert(entity);
        long rowId2 = this.dao.insertOrReplace(entity);
        if (this.dao.getPkProperty().type == Long.class) {
            assertEquals(rowId1, rowId2);
        }
    }

    public void testInsertOrReplaceInTx() {
        this.dao.deleteAll();
        List<T> listPartial = new ArrayList<>();
        List<T> listAll = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            T entity = createEntityWithRandomPk();
            if (i % 2 == 0) {
                listPartial.add(entity);
            }
            listAll.add(entity);
        }
        this.dao.insertOrReplaceInTx(listPartial);
        this.dao.insertOrReplaceInTx(listAll);
        assertEquals((long) listAll.size(), this.dao.count());
    }

    public void testDelete() {
        K pk = nextPk();
        this.dao.deleteByKey(pk);
        this.dao.insert(createEntity(pk));
        assertNotNull(this.dao.load(pk));
        this.dao.deleteByKey(pk);
        assertNull(this.dao.load(pk));
    }

    public void testDeleteAll() {
        List<T> entityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entityList.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(entityList);
        this.dao.deleteAll();
        assertEquals(0, this.dao.count());
        for (T entity : entityList) {
            K key = this.daoAccess.getKey(entity);
            assertNotNull(key);
            assertNull(this.dao.load(key));
        }
    }

    public void testDeleteInTx() {
        List<T> entityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entityList.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(entityList);
        List<T> entitiesToDelete = new ArrayList<>();
        entitiesToDelete.add(entityList.get(0));
        entitiesToDelete.add(entityList.get(3));
        entitiesToDelete.add(entityList.get(4));
        entitiesToDelete.add(entityList.get(8));
        this.dao.deleteInTx(entitiesToDelete);
        assertEquals((long) (entityList.size() - entitiesToDelete.size()), this.dao.count());
        for (T deletedEntity : entitiesToDelete) {
            K key = this.daoAccess.getKey(deletedEntity);
            assertNotNull(key);
            assertNull(this.dao.load(key));
        }
    }

    public void testDeleteByKeyInTx() {
        List<T> entityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entityList.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(entityList);
        List<K> keysToDelete = new ArrayList<>();
        keysToDelete.add(this.daoAccess.getKey(entityList.get(0)));
        keysToDelete.add(this.daoAccess.getKey(entityList.get(3)));
        keysToDelete.add(this.daoAccess.getKey(entityList.get(4)));
        keysToDelete.add(this.daoAccess.getKey(entityList.get(8)));
        this.dao.deleteByKeyInTx(keysToDelete);
        assertEquals((long) (entityList.size() - keysToDelete.size()), this.dao.count());
        for (K key : keysToDelete) {
            assertNotNull(key);
            assertNull(this.dao.load(key));
        }
    }

    public void testRowId() {
        assertTrue(this.dao.insert(createEntityWithRandomPk()) != this.dao.insert(createEntityWithRandomPk()));
    }

    public void testLoadAll() {
        this.dao.deleteAll();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add(createEntity(nextPk()));
        }
        this.dao.insertInTx(list);
        assertEquals(list.size(), this.dao.loadAll().size());
    }

    public void testQuery() {
        this.dao.insert(createEntityWithRandomPk());
        K pkForQuery = nextPk();
        this.dao.insert(createEntity(pkForQuery));
        this.dao.insert(createEntityWithRandomPk());
        List<T> list = this.dao.queryRaw("WHERE " + this.dao.getPkColumns()[0] + "=?", pkForQuery.toString());
        assertEquals(1, list.size());
        assertEquals(pkForQuery, this.daoAccess.getKey(list.get(0)));
    }

    public void testUpdate() {
        this.dao.deleteAll();
        T entity = createEntityWithRandomPk();
        this.dao.insert(entity);
        this.dao.update(entity);
        assertEquals(1, this.dao.count());
    }

    public void testReadWithOffset() {
        K pk = nextPk();
        this.dao.insert(createEntity(pk));
        Cursor cursor = queryWithDummyColumnsInFront(5, "42", pk);
        try {
            assertEquals(pk, this.daoAccess.getKey(this.daoAccess.readEntity(cursor, 5)));
        } finally {
            cursor.close();
        }
    }

    public void testLoadPkWithOffset() {
        runLoadPkTest(10);
    }

    public void testLoadPk() {
        runLoadPkTest(0);
    }

    /* access modifiers changed from: protected */
    public void runLoadPkTest(int offset) {
        K pk = nextPk();
        this.dao.insert(createEntity(pk));
        Cursor cursor = queryWithDummyColumnsInFront(offset, "42", pk);
        try {
            assertEquals(pk, this.daoAccess.readKey(cursor, offset));
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: protected */
    public Cursor queryWithDummyColumnsInFront(int dummyCount, String valueForColumn, K pk) {
        StringBuilder builder = new StringBuilder("SELECT ");
        for (int i = 0; i < dummyCount; i++) {
            builder.append(valueForColumn);
            builder.append(",");
        }
        SqlUtils.appendColumns(builder, "T", this.dao.getAllColumns()).append(" FROM ");
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(this.dao.getTablename());
        builder.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        builder.append(" T");
        if (pk != null) {
            builder.append(" WHERE ");
            assertEquals(1, this.dao.getPkColumns().length);
            builder.append(this.dao.getPkColumns()[0]);
            builder.append("=");
            DatabaseUtils.appendValueToSql(builder, pk);
        }
        Cursor cursor = this.f112db.rawQuery(builder.toString(), (String[]) null);
        assertTrue(cursor.moveToFirst());
        int i2 = 0;
        while (i2 < dummyCount) {
            try {
                assertEquals(valueForColumn, cursor.getString(i2));
                i2++;
            } catch (RuntimeException ex) {
                cursor.close();
                throw ex;
            }
        }
        if (pk != null) {
            assertEquals(1, cursor.getCount());
        }
        return cursor;
    }

    /* access modifiers changed from: protected */
    public K nextPk() {
        for (int i = 0; i < 100000; i++) {
            K pk = createRandomPk();
            if (this.usedPks.add(pk)) {
                return pk;
            }
        }
        throw new IllegalStateException("Could not find a new PK");
    }

    /* access modifiers changed from: protected */
    public T createEntityWithRandomPk() {
        return createEntity(nextPk());
    }
}
