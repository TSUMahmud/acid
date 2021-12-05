package org.hamcrest.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class AllOf<T> extends DiagnosingMatcher<T> {
    private final Iterable<Matcher<? super T>> matchers;

    public AllOf(Iterable<Matcher<? super T>> matchers2) {
        this.matchers = matchers2;
    }

    public boolean matches(Object o, Description mismatch) {
        for (Matcher<? super T> matcher : this.matchers) {
            if (!matcher.matches(o)) {
                mismatch.appendDescriptionOf(matcher).appendText(StringUtils.SPACE);
                matcher.describeMismatch(o, mismatch);
                return false;
            }
        }
        return true;
    }

    public void describeTo(Description description) {
        description.appendList("(", " and ", ")", this.matchers);
    }

    @Factory
    public static <T> Matcher<T> allOf(Iterable<Matcher<? super T>> matchers2) {
        return new AllOf(matchers2);
    }

    @Factory
    public static <T> Matcher<T> allOf(Matcher<? super T>... matchers2) {
        return allOf(Arrays.asList(matchers2));
    }

    @Factory
    public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second) {
        List<Matcher<? super T>> matchers2 = new ArrayList<>(2);
        matchers2.add(first);
        matchers2.add(second);
        return allOf(matchers2);
    }

    @Factory
    public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third) {
        List<Matcher<? super T>> matchers2 = new ArrayList<>(3);
        matchers2.add(first);
        matchers2.add(second);
        matchers2.add(third);
        return allOf(matchers2);
    }

    @Factory
    public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth) {
        List<Matcher<? super T>> matchers2 = new ArrayList<>(4);
        matchers2.add(first);
        matchers2.add(second);
        matchers2.add(third);
        matchers2.add(fourth);
        return allOf(matchers2);
    }

    @Factory
    public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth) {
        List<Matcher<? super T>> matchers2 = new ArrayList<>(5);
        matchers2.add(first);
        matchers2.add(second);
        matchers2.add(third);
        matchers2.add(fourth);
        matchers2.add(fifth);
        return allOf(matchers2);
    }

    @Factory
    public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth, Matcher<? super T> sixth) {
        List<Matcher<? super T>> matchers2 = new ArrayList<>(6);
        matchers2.add(first);
        matchers2.add(second);
        matchers2.add(third);
        matchers2.add(fourth);
        matchers2.add(fifth);
        matchers2.add(sixth);
        return allOf(matchers2);
    }
}
