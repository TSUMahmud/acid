package org.hamcrest.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsEqual;

public class IsIterableContainingInOrder<E> extends TypeSafeDiagnosingMatcher<Iterable<? extends E>> {
    private final List<Matcher<? super E>> matchers;

    public IsIterableContainingInOrder(List<Matcher<? super E>> matchers2) {
        this.matchers = matchers2;
    }

    /* access modifiers changed from: protected */
    public boolean matchesSafely(Iterable<? extends E> iterable, Description mismatchDescription) {
        MatchSeries<E> matchSeries = new MatchSeries<>(this.matchers, mismatchDescription);
        for (E item : iterable) {
            if (!matchSeries.matches(item)) {
                return false;
            }
        }
        return matchSeries.isFinished();
    }

    public void describeTo(Description description) {
        description.appendText("iterable containing ").appendList("[", ", ", "]", this.matchers);
    }

    private static class MatchSeries<F> {
        public final List<Matcher<? super F>> matchers;
        private final Description mismatchDescription;
        public int nextMatchIx = 0;

        public MatchSeries(List<Matcher<? super F>> matchers2, Description mismatchDescription2) {
            this.mismatchDescription = mismatchDescription2;
            if (!matchers2.isEmpty()) {
                this.matchers = matchers2;
                return;
            }
            throw new IllegalArgumentException("Should specify at least one expected element");
        }

        public boolean matches(F item) {
            return isNotSurplus(item) && isMatched(item);
        }

        public boolean isFinished() {
            if (this.nextMatchIx >= this.matchers.size()) {
                return true;
            }
            this.mismatchDescription.appendText("No item matched: ").appendDescriptionOf(this.matchers.get(this.nextMatchIx));
            return false;
        }

        private boolean isMatched(F item) {
            Matcher<? super F> matcher = this.matchers.get(this.nextMatchIx);
            if (!matcher.matches(item)) {
                describeMismatch(matcher, item);
                return false;
            }
            this.nextMatchIx++;
            return true;
        }

        private boolean isNotSurplus(F item) {
            if (this.matchers.size() > this.nextMatchIx) {
                return true;
            }
            this.mismatchDescription.appendText("Not matched: ").appendValue(item);
            return false;
        }

        private void describeMismatch(Matcher<? super F> matcher, F item) {
            Description description = this.mismatchDescription;
            description.appendText("item " + this.nextMatchIx + ": ");
            matcher.describeMismatch(item, this.mismatchDescription);
        }
    }

    @Factory
    public static <E> Matcher<Iterable<? extends E>> contains(E... items) {
        List<Matcher<? super E>> matchers2 = new ArrayList<>();
        for (E item : items) {
            matchers2.add(IsEqual.equalTo(item));
        }
        return contains(matchers2);
    }

    @Factory
    public static <E> Matcher<Iterable<? extends E>> contains(Matcher<? super E> itemMatcher) {
        return contains(new ArrayList(Arrays.asList(new Matcher[]{itemMatcher})));
    }

    @Factory
    public static <E> Matcher<Iterable<? extends E>> contains(Matcher<? super E>... itemMatchers) {
        return contains(Arrays.asList(itemMatchers));
    }

    @Factory
    public static <E> Matcher<Iterable<? extends E>> contains(List<Matcher<? super E>> itemMatchers) {
        return new IsIterableContainingInOrder(itemMatchers);
    }
}
