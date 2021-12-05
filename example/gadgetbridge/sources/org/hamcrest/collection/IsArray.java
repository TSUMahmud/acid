package org.hamcrest.collection;

import java.util.Arrays;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsArray<T> extends TypeSafeMatcher<T[]> {
    private final Matcher<? super T>[] elementMatchers;

    public IsArray(Matcher<? super T>[] elementMatchers2) {
        this.elementMatchers = (Matcher[]) elementMatchers2.clone();
    }

    public boolean matchesSafely(T[] array) {
        if (array.length != this.elementMatchers.length) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            if (!this.elementMatchers[i].matches(array[i])) {
                return false;
            }
        }
        return true;
    }

    public void describeMismatchSafely(T[] actual, Description mismatchDescription) {
        if (actual.length != this.elementMatchers.length) {
            mismatchDescription.appendText("array length was " + actual.length);
            return;
        }
        for (int i = 0; i < actual.length; i++) {
            if (!this.elementMatchers[i].matches(actual[i])) {
                mismatchDescription.appendText("element " + i + " was ").appendValue(actual[i]);
                return;
            }
        }
    }

    public void describeTo(Description description) {
        description.appendList(descriptionStart(), descriptionSeparator(), descriptionEnd(), Arrays.asList(this.elementMatchers));
    }

    /* access modifiers changed from: protected */
    public String descriptionStart() {
        return "[";
    }

    /* access modifiers changed from: protected */
    public String descriptionSeparator() {
        return ", ";
    }

    /* access modifiers changed from: protected */
    public String descriptionEnd() {
        return "]";
    }

    @Factory
    public static <T> IsArray<T> array(Matcher<? super T>... elementMatchers2) {
        return new IsArray<>(elementMatchers2);
    }
}
