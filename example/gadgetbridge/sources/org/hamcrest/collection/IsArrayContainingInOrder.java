package org.hamcrest.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

public class IsArrayContainingInOrder<E> extends TypeSafeMatcher<E[]> {
    private final IsIterableContainingInOrder<E> iterableMatcher;
    private final Collection<Matcher<? super E>> matchers;

    public IsArrayContainingInOrder(List<Matcher<? super E>> matchers2) {
        this.iterableMatcher = new IsIterableContainingInOrder<>(matchers2);
        this.matchers = matchers2;
    }

    public boolean matchesSafely(E[] item) {
        return this.iterableMatcher.matches(Arrays.asList(item));
    }

    public void describeMismatchSafely(E[] item, Description mismatchDescription) {
        this.iterableMatcher.describeMismatch(Arrays.asList(item), mismatchDescription);
    }

    public void describeTo(Description description) {
        description.appendList("[", ", ", "]", this.matchers);
    }

    @Factory
    public static <E> Matcher<E[]> arrayContaining(E... items) {
        List<Matcher<? super E>> matchers2 = new ArrayList<>();
        for (E item : items) {
            matchers2.add(IsEqual.equalTo(item));
        }
        return arrayContaining(matchers2);
    }

    @Factory
    public static <E> Matcher<E[]> arrayContaining(Matcher<? super E>... itemMatchers) {
        return arrayContaining(Arrays.asList(itemMatchers));
    }

    @Factory
    public static <E> Matcher<E[]> arrayContaining(List<Matcher<? super E>> itemMatchers) {
        return new IsArrayContainingInOrder(itemMatchers);
    }
}
