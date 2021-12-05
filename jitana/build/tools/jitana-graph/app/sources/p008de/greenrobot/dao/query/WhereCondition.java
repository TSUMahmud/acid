package p008de.greenrobot.dao.query;

import java.util.Date;
import java.util.List;
import p008de.greenrobot.dao.DaoException;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.SqlUtils;

/* renamed from: de.greenrobot.dao.query.WhereCondition */
public interface WhereCondition {
    void appendTo(StringBuilder sb, String str);

    void appendValuesTo(List<Object> list);

    /* renamed from: de.greenrobot.dao.query.WhereCondition$AbstractCondition */
    public static abstract class AbstractCondition implements WhereCondition {
        protected final boolean hasSingleValue;
        protected final Object value;
        protected final Object[] values;

        public AbstractCondition() {
            this.hasSingleValue = false;
            this.value = null;
            this.values = null;
        }

        public AbstractCondition(Object value2) {
            this.value = value2;
            this.hasSingleValue = true;
            this.values = null;
        }

        public AbstractCondition(Object[] values2) {
            this.value = null;
            this.hasSingleValue = false;
            this.values = values2;
        }

        public void appendValuesTo(List<Object> valuesTarget) {
            if (this.hasSingleValue) {
                valuesTarget.add(this.value);
                return;
            }
            Object[] objArr = this.values;
            if (objArr != null) {
                for (Object value2 : objArr) {
                    valuesTarget.add(value2);
                }
            }
        }
    }

    /* renamed from: de.greenrobot.dao.query.WhereCondition$PropertyCondition */
    public static class PropertyCondition extends AbstractCondition {

        /* renamed from: op */
        public final String f111op;
        public final Property property;

        private static Object checkValueForType(Property property2, Object value) {
            if (value != null && value.getClass().isArray()) {
                throw new DaoException("Illegal value: found array, but simple object required");
            } else if (property2.type != Date.class) {
                if (property2.type == Boolean.TYPE || property2.type == Boolean.class) {
                    if (value instanceof Boolean) {
                        return Integer.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
                    }
                    if (value instanceof Number) {
                        int intValue = ((Number) value).intValue();
                        if (!(intValue == 0 || intValue == 1)) {
                            throw new DaoException("Illegal boolean value: numbers must be 0 or 1, but was " + value);
                        }
                    } else if ((value instanceof String) != 0) {
                        String stringValue = (String) value;
                        if ("TRUE".equalsIgnoreCase(stringValue)) {
                            return 1;
                        }
                        if ("FALSE".equalsIgnoreCase(stringValue)) {
                            return 0;
                        }
                        throw new DaoException("Illegal boolean value: Strings must be \"TRUE\" or \"FALSE\" (case insensitive), but was " + value);
                    }
                }
                return value;
            } else if (value instanceof Date) {
                return Long.valueOf(((Date) value).getTime());
            } else {
                if (value instanceof Long) {
                    return value;
                }
                throw new DaoException("Illegal date value: expected java.util.Date or Long for value " + value);
            }
        }

        private static Object[] checkValuesForType(Property property2, Object[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = checkValueForType(property2, values[i]);
            }
            return values;
        }

        public PropertyCondition(Property property2, String op) {
            this.property = property2;
            this.f111op = op;
        }

        public PropertyCondition(Property property2, String op, Object value) {
            super(checkValueForType(property2, value));
            this.property = property2;
            this.f111op = op;
        }

        public PropertyCondition(Property property2, String op, Object[] values) {
            super(checkValuesForType(property2, values));
            this.property = property2;
            this.f111op = op;
        }

        public void appendTo(StringBuilder builder, String tableAlias) {
            SqlUtils.appendProperty(builder, tableAlias, this.property).append(this.f111op);
        }
    }

    /* renamed from: de.greenrobot.dao.query.WhereCondition$StringCondition */
    public static class StringCondition extends AbstractCondition {
        protected final String string;

        public StringCondition(String string2) {
            this.string = string2;
        }

        public StringCondition(String string2, Object value) {
            super(value);
            this.string = string2;
        }

        public StringCondition(String string2, Object... values) {
            super(values);
            this.string = string2;
        }

        public void appendTo(StringBuilder builder, String tableAlias) {
            builder.append(this.string);
        }
    }
}
