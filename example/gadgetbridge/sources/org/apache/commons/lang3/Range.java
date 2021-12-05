package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Comparator;

public final class Range<T> implements Serializable {
    private static final long serialVersionUID = 1;
    private final Comparator<T> comparator;
    private transient int hashCode;
    private final T maximum;
    private final T minimum;
    private transient String toString;

    /* renamed from: is */
    public static <T extends Comparable<T>> Range<T> m43is(T element) {
        return between(element, element, (Comparator) null);
    }

    /* renamed from: is */
    public static <T> Range<T> m44is(T element, Comparator<T> comparator2) {
        return between(element, element, comparator2);
    }

    public static <T extends Comparable<T>> Range<T> between(T fromInclusive, T toInclusive) {
        return between(fromInclusive, toInclusive, (Comparator) null);
    }

    public static <T> Range<T> between(T fromInclusive, T toInclusive, Comparator<T> comparator2) {
        return new Range<>(fromInclusive, toInclusive, comparator2);
    }

    private Range(T element1, T element2, Comparator<T> comp) {
        if (element1 == null || element2 == null) {
            throw new IllegalArgumentException("Elements in a range must not be null: element1=" + element1 + ", element2=" + element2);
        }
        if (comp == null) {
            this.comparator = ComparableComparator.INSTANCE;
        } else {
            this.comparator = comp;
        }
        if (this.comparator.compare(element1, element2) < 1) {
            this.minimum = element1;
            this.maximum = element2;
            return;
        }
        this.minimum = element2;
        this.maximum = element1;
    }

    public T getMinimum() {
        return this.minimum;
    }

    public T getMaximum() {
        return this.maximum;
    }

    public Comparator<T> getComparator() {
        return this.comparator;
    }

    public boolean isNaturalOrdering() {
        return this.comparator == ComparableComparator.INSTANCE;
    }

    public boolean contains(T element) {
        if (element != null && this.comparator.compare(element, this.minimum) > -1 && this.comparator.compare(element, this.maximum) < 1) {
            return true;
        }
        return false;
    }

    public boolean isAfter(T element) {
        if (element != null && this.comparator.compare(element, this.minimum) < 0) {
            return true;
        }
        return false;
    }

    public boolean isStartedBy(T element) {
        if (element != null && this.comparator.compare(element, this.minimum) == 0) {
            return true;
        }
        return false;
    }

    public boolean isEndedBy(T element) {
        if (element != null && this.comparator.compare(element, this.maximum) == 0) {
            return true;
        }
        return false;
    }

    public boolean isBefore(T element) {
        if (element != null && this.comparator.compare(element, this.maximum) > 0) {
            return true;
        }
        return false;
    }

    public int elementCompareTo(T element) {
        Validate.notNull(element, "Element is null", new Object[0]);
        if (isAfter(element)) {
            return -1;
        }
        if (isBefore(element)) {
            return 1;
        }
        return 0;
    }

    public boolean containsRange(Range<T> otherRange) {
        if (otherRange != null && contains(otherRange.minimum) && contains(otherRange.maximum)) {
            return true;
        }
        return false;
    }

    public boolean isAfterRange(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        return isAfter(otherRange.maximum);
    }

    public boolean isOverlappedBy(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        if (otherRange.contains(this.minimum) || otherRange.contains(this.maximum) || contains(otherRange.minimum)) {
            return true;
        }
        return false;
    }

    public boolean isBeforeRange(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        return isBefore(otherRange.minimum);
    }

    public Range<T> intersectionWith(Range<T> other) {
        if (!isOverlappedBy(other)) {
            throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", new Object[]{other}));
        } else if (equals(other)) {
            return this;
        } else {
            return between(getComparator().compare(this.minimum, other.minimum) < 0 ? other.minimum : this.minimum, getComparator().compare(this.maximum, other.maximum) < 0 ? this.maximum : other.maximum, getComparator());
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Range<T> range = (Range) obj;
        if (!this.minimum.equals(range.minimum) || !this.maximum.equals(range.maximum)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.hashCode;
        if (this.hashCode != 0) {
            return result;
        }
        int result2 = (((((17 * 37) + getClass().hashCode()) * 37) + this.minimum.hashCode()) * 37) + this.maximum.hashCode();
        this.hashCode = result2;
        return result2;
    }

    public String toString() {
        if (this.toString == null) {
            this.toString = "[" + this.minimum + ".." + this.maximum + "]";
        }
        return this.toString;
    }

    public String toString(String format) {
        return String.format(format, new Object[]{this.minimum, this.maximum, this.comparator});
    }

    private enum ComparableComparator implements Comparator {
        INSTANCE;

        public int compare(Object obj1, Object obj2) {
            return ((Comparable) obj1).compareTo(obj2);
        }
    }
}
