package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Triple<L, M, R> implements Comparable<Triple<L, M, R>>, Serializable {
    private static final long serialVersionUID = 1;

    public abstract L getLeft();

    public abstract M getMiddle();

    public abstract R getRight();

    /* renamed from: of */
    public static <L, M, R> Triple<L, M, R> m52of(L left, M middle, R right) {
        return new ImmutableTriple(left, middle, right);
    }

    public int compareTo(Triple<L, M, R> other) {
        return new CompareToBuilder().append(getLeft(), (Object) other.getLeft()).append(getMiddle(), (Object) other.getMiddle()).append(getRight(), (Object) other.getRight()).toComparison();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Triple)) {
            return false;
        }
        Triple<?, ?, ?> other = (Triple) obj;
        if (!Objects.equals(getLeft(), other.getLeft()) || !Objects.equals(getMiddle(), other.getMiddle()) || !Objects.equals(getRight(), other.getRight())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (getLeft() == null ? 0 : getLeft().hashCode()) ^ (getMiddle() == null ? 0 : getMiddle().hashCode());
        if (getRight() != null) {
            i = getRight().hashCode();
        }
        return hashCode ^ i;
    }

    public String toString() {
        return "(" + getLeft() + "," + getMiddle() + "," + getRight() + ")";
    }

    public String toString(String format) {
        return String.format(format, new Object[]{getLeft(), getMiddle(), getRight()});
    }
}
