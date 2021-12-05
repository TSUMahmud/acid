package org.hamcrest.number;

import com.github.mikephil.charting.utils.Utils;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsCloseTo extends TypeSafeMatcher<Double> {
    private final double delta;
    private final double value;

    public IsCloseTo(double value2, double error) {
        this.delta = error;
        this.value = value2;
    }

    public boolean matchesSafely(Double item) {
        return actualDelta(item) <= Utils.DOUBLE_EPSILON;
    }

    public void describeMismatchSafely(Double item, Description mismatchDescription) {
        mismatchDescription.appendValue(item).appendText(" differed by ").appendValue(Double.valueOf(actualDelta(item)));
    }

    public void describeTo(Description description) {
        description.appendText("a numeric value within ").appendValue(Double.valueOf(this.delta)).appendText(" of ").appendValue(Double.valueOf(this.value));
    }

    private double actualDelta(Double item) {
        return Math.abs(item.doubleValue() - this.value) - this.delta;
    }

    @Factory
    public static Matcher<Double> closeTo(double operand, double error) {
        return new IsCloseTo(operand, error);
    }
}
