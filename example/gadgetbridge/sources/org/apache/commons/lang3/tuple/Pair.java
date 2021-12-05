package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;
import p005ch.qos.logback.core.CoreConstants;

public abstract class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {
    private static final long serialVersionUID = 4954918890077093841L;

    public abstract L getLeft();

    public abstract R getRight();

    /* renamed from: of */
    public static <L, R> Pair<L, R> m51of(L left, R right) {
        return new ImmutablePair(left, right);
    }

    public final L getKey() {
        return getLeft();
    }

    public R getValue() {
        return getRight();
    }

    public int compareTo(Pair<L, R> other) {
        return new CompareToBuilder().append(getLeft(), (Object) other.getLeft()).append(getRight(), (Object) other.getRight()).toComparison();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        Map.Entry<?, ?> other = (Map.Entry) obj;
        if (!Objects.equals(getKey(), other.getKey()) || !Objects.equals(getValue(), other.getValue())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = getKey() == null ? 0 : getKey().hashCode();
        if (getValue() != null) {
            i = getValue().hashCode();
        }
        return hashCode ^ i;
    }

    public String toString() {
        return "(" + getLeft() + CoreConstants.COMMA_CHAR + getRight() + CoreConstants.RIGHT_PARENTHESIS_CHAR;
    }

    public String toString(String format) {
        return String.format(format, new Object[]{getLeft(), getRight()});
    }
}
