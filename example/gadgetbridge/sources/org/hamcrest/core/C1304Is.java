package org.hamcrest.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/* renamed from: org.hamcrest.core.Is */
public class C1304Is<T> extends BaseMatcher<T> {
    private final Matcher<T> matcher;

    public C1304Is(Matcher<T> matcher2) {
        this.matcher = matcher2;
    }

    public boolean matches(Object arg) {
        return this.matcher.matches(arg);
    }

    public void describeTo(Description description) {
        description.appendText("is ").appendDescriptionOf(this.matcher);
    }

    public void describeMismatch(Object item, Description mismatchDescription) {
        this.matcher.describeMismatch(item, mismatchDescription);
    }

    @Factory
    /* renamed from: is */
    public static <T> Matcher<T> m63is(Matcher<T> matcher2) {
        return new C1304Is(matcher2);
    }

    @Factory
    /* renamed from: is */
    public static <T> Matcher<T> m62is(T value) {
        return m63is(IsEqual.equalTo(value));
    }

    @Deprecated
    @Factory
    /* renamed from: is */
    public static <T> Matcher<T> m61is(Class<T> type) {
        return m63is(IsInstanceOf.instanceOf(type));
    }

    @Factory
    public static <T> Matcher<T> isA(Class<T> type) {
        return m63is(IsInstanceOf.instanceOf(type));
    }
}
