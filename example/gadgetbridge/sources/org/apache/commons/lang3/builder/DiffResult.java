package org.apache.commons.lang3.builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;

public class DiffResult implements Iterable<Diff<?>> {
    private static final String DIFFERS_STRING = "differs from";
    public static final String OBJECTS_SAME_STRING = "";
    private final List<Diff<?>> diffs;
    private final Object lhs;
    private final Object rhs;
    private final ToStringStyle style;

    DiffResult(Object lhs2, Object rhs2, List<Diff<?>> diffs2, ToStringStyle style2) {
        boolean z = true;
        Validate.isTrue(lhs2 != null, "Left hand object cannot be null", new Object[0]);
        Validate.isTrue(rhs2 != null, "Right hand object cannot be null", new Object[0]);
        Validate.isTrue(diffs2 == null ? false : z, "List of differences cannot be null", new Object[0]);
        this.diffs = diffs2;
        this.lhs = lhs2;
        this.rhs = rhs2;
        if (style2 == null) {
            this.style = ToStringStyle.DEFAULT_STYLE;
        } else {
            this.style = style2;
        }
    }

    public List<Diff<?>> getDiffs() {
        return Collections.unmodifiableList(this.diffs);
    }

    public int getNumberOfDiffs() {
        return this.diffs.size();
    }

    public ToStringStyle getToStringStyle() {
        return this.style;
    }

    public String toString() {
        return toString(this.style);
    }

    public String toString(ToStringStyle style2) {
        if (this.diffs.size() == 0) {
            return "";
        }
        ToStringBuilder lhsBuilder = new ToStringBuilder(this.lhs, style2);
        ToStringBuilder rhsBuilder = new ToStringBuilder(this.rhs, style2);
        for (Diff<?> diff : this.diffs) {
            lhsBuilder.append(diff.getFieldName(), diff.getLeft());
            rhsBuilder.append(diff.getFieldName(), diff.getRight());
        }
        return String.format("%s %s %s", new Object[]{lhsBuilder.build(), DIFFERS_STRING, rhsBuilder.build()});
    }

    public Iterator<Diff<?>> iterator() {
        return this.diffs.iterator();
    }
}
