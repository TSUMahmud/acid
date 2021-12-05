package p005ch.qos.logback.classic.p006db.names;

/* renamed from: ch.qos.logback.classic.db.names.DBNameResolver */
public interface DBNameResolver {
    <N extends Enum<?>> String getColumnName(N n);

    <N extends Enum<?>> String getTableName(N n);
}
