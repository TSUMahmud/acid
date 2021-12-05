package org.hamcrest.beans;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasProperty<T> extends TypeSafeMatcher<T> {
    private final String propertyName;

    public HasProperty(String propertyName2) {
        this.propertyName = propertyName2;
    }

    public boolean matchesSafely(T obj) {
        try {
            return PropertyUtil.getPropertyDescriptor(this.propertyName, obj) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText("no ").appendValue(this.propertyName).appendText(" in ").appendValue(item);
    }

    public void describeTo(Description description) {
        description.appendText("hasProperty(").appendValue(this.propertyName).appendText(")");
    }

    @Factory
    public static <T> Matcher<T> hasProperty(String propertyName2) {
        return new HasProperty(propertyName2);
    }
}
