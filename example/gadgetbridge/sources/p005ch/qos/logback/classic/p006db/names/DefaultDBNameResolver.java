package p005ch.qos.logback.classic.p006db.names;

/* renamed from: ch.qos.logback.classic.db.names.DefaultDBNameResolver */
public class DefaultDBNameResolver implements DBNameResolver {
    public <N extends Enum<?>> String getColumnName(N n) {
        return n.toString().toLowerCase();
    }

    public <N extends Enum<?>> String getTableName(N n) {
        return n.toString().toLowerCase();
    }
}
