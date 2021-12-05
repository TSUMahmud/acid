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

public class IsArrayContainingInAnyOrder<E> extends TypeSafeMatcher<E[]> {
    private final IsIterableContainingInAnyOrder<E> iterableMatcher;
    private final Collection<Matcher<? super E>> matchers;

    public IsArrayContainingInAnyOrder(Collection<Matcher<? super E>> matchers2) {
        this.iterableMatcher = new IsIterableContainingInAnyOrder<>(matchers2);
        this.matchers = matchers2;
    }

    public boolean matchesSafely(E[] item) {
        return this.iterableMatcher.matches(Arrays.asList(item));
    }

    public void describeMismatchSafely(E[] item, Description mismatchDescription) {
        this.iterableMatcher.describeMismatch(Arrays.asList(item), mismatchDescription);
    }

    public void describeTo(Description description) {
        description.appendList("[", ", ", "]", this.matchers).appendText(" in any order");
    }

    @Factory
    public static <E> Matcher<E[]> arrayContainingInAnyOrder(Matcher<? super E>... itemMatchers) {
        return arrayContainingInAnyOrder(Arrays.asList(itemMatchers));
    }

    @Factory
    public static <E> Matcher<E[]> arrayContainingInAnyOrder(Collection<Matcher<? super E>> itemMatchers) {
        return new IsArrayContainingInAnyOrder(itemMatchers);
    }

    @Factory
    public static <E> Matcher<E[]> arrayContainingInAnyOrder(E... items) {
        List<Matcher<? super E>> matchers2 = new ArrayList<>();
        for (E item : items) {
            matchers2.add(IsEqual.equalTo(item));
        }
        return new IsArrayContainingInAnyOrder(matchers2);
    }
}
