package org.hamcrest.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsNot<T> extends BaseMatcher<T> {
    private final Matcher<T> matcher;

    public IsNot(Matcher<T> matcher2) {
        this.matcher = matcher2;
    }

    public boolean matches(Object arg) {
        return !this.matcher.matches(arg);
    }

    public void describeTo(Description description) {
        description.appendText("not ").appendDescriptionOf(this.matcher);
    }

    @Factory
    public static <T> Matcher<T> not(Matcher<T> matcher2) {
        return new IsNot(matcher2);
    }

    @Factory
    public static <T> Matcher<T> not(T value) {
        return not(IsEqual.equalTo(value));
    }
}
