package org.hamcrest.number;

import java.lang.Comparable;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class OrderingComparison<T extends Comparable<T>> extends TypeSafeMatcher<T> {
    private static final int EQUAL = 0;
    private static final int GREATER_THAN = 1;
    private static final int LESS_THAN = -1;
    private static final String[] comparisonDescriptions = {"less than", "equal to", "greater than"};
    private final T expected;
    private final int maxCompare;
    private final int minCompare;

    private OrderingComparison(T expected2, int minCompare2, int maxCompare2) {
        this.expected = expected2;
        this.minCompare = minCompare2;
        this.maxCompare = maxCompare2;
    }

    public boolean matchesSafely(T actual) {
        int compare = Integer.signum(actual.compareTo(this.expected));
        return this.minCompare <= compare && compare <= this.maxCompare;
    }

    public void describeMismatchSafely(T actual, Description mismatchDescription) {
        mismatchDescription.appendValue(actual).appendText(" was ").appendText(asText(actual.compareTo(this.expected))).appendText(StringUtils.SPACE).appendValue(this.expected);
    }

    public void describeTo(Description description) {
        description.appendText("a value ").appendText(asText(this.minCompare));
        if (this.minCompare != this.maxCompare) {
            description.appendText(" or ").appendText(asText(this.maxCompare));
        }
        description.appendText(StringUtils.SPACE).appendValue(this.expected);
    }

    private static String asText(int comparison) {
        return comparisonDescriptions[Integer.signum(comparison) + 1];
    }

    @Factory
    public static <T extends Comparable<T>> Matcher<T> comparesEqualTo(T value) {
        return new OrderingComparison(value, 0, 0);
    }

    @Factory
    public static <T extends Comparable<T>> Matcher<T> greaterThan(T value) {
        return new OrderingComparison(value, 1, 1);
    }

    @Factory
    public static <T extends Comparable<T>> Matcher<T> greaterThanOrEqualTo(T value) {
        return new OrderingComparison(value, 0, 1);
    }

    @Factory
    public static <T extends Comparable<T>> Matcher<T> lessThan(T value) {
        return new OrderingComparison(value, -1, -1);
    }

    @Factory
    public static <T extends Comparable<T>> Matcher<T> lessThanOrEqualTo(T value) {
        return new OrderingComparison(value, -1, 0);
    }
}
