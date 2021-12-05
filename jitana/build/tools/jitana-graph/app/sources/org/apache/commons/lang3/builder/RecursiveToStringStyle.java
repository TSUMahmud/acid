package org.apache.commons.lang3.builder;

import java.util.Collection;
import org.apache.commons.lang3.ClassUtils;

public class RecursiveToStringStyle extends ToStringStyle {
    private static final long serialVersionUID = 1;

    public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        if (ClassUtils.isPrimitiveWrapper(value.getClass()) || String.class.equals(value.getClass()) || !accept(value.getClass())) {
            super.appendDetail(buffer, fieldName, value);
        } else {
            buffer.append(ReflectionToStringBuilder.toString(value, this));
        }
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, Collection<?> coll) {
        appendClassName(buffer, coll);
        appendIdentityHashCode(buffer, coll);
        appendDetail(buffer, fieldName, coll.toArray());
    }

    /* access modifiers changed from: protected */
    public boolean accept(Class<?> cls) {
        return true;
    }
}
