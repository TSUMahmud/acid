package org.hamcrest.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsSame<T> extends BaseMatcher<T> {
    private final T object;

    public IsSame(T object2) {
        this.object = object2;
    }

    public boolean matches(Object arg) {
        return arg == this.object;
    }

    public void describeTo(Description description) {
        description.appendText("sameInstance(").appendValue(this.object).appendText(")");
    }

    @Factory
    public static <T> Matcher<T> sameInstance(T target) {
        return new IsSame(target);
    }

    @Factory
    public static <T> Matcher<T> theInstance(T target) {
        return new IsSame(target);
    }
}
