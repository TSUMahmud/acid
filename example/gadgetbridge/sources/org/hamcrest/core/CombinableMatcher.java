package org.hamcrest.core;

import java.util.ArrayList;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class CombinableMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
    private final Matcher<? super T> matcher;

    public CombinableMatcher(Matcher<? super T> matcher2) {
        this.matcher = matcher2;
    }

    /* access modifiers changed from: protected */
    public boolean matchesSafely(T item, Description mismatch) {
        if (this.matcher.matches(item)) {
            return true;
        }
        this.matcher.describeMismatch(item, mismatch);
        return false;
    }

    public void describeTo(Description description) {
        description.appendDescriptionOf(this.matcher);
    }

    public CombinableMatcher<T> and(Matcher<? super T> other) {
        return new CombinableMatcher<>(new AllOf(templatedListWith(other)));
    }

    /* renamed from: or */
    public CombinableMatcher<T> mo18964or(Matcher<? super T> other) {
        return new CombinableMatcher<>(new AnyOf(templatedListWith(other)));
    }

    private ArrayList<Matcher<? super T>> templatedListWith(Matcher<? super T> other) {
        ArrayList<Matcher<? super T>> matchers = new ArrayList<>();
        matchers.add(this.matcher);
        matchers.add(other);
        return matchers;
    }

    @Factory
    public static <LHS> CombinableBothMatcher<LHS> both(Matcher<? super LHS> matcher2) {
        return new CombinableBothMatcher<>(matcher2);
    }

    public static final class CombinableBothMatcher<X> {
        private final Matcher<? super X> first;

        public CombinableBothMatcher(Matcher<? super X> matcher) {
            this.first = matcher;
        }

        public CombinableMatcher<X> and(Matcher<? super X> other) {
            return new CombinableMatcher(this.first).and(other);
        }
    }

    @Factory
    public static <LHS> CombinableEitherMatcher<LHS> either(Matcher<? super LHS> matcher2) {
        return new CombinableEitherMatcher<>(matcher2);
    }

    public static final class CombinableEitherMatcher<X> {
        private final Matcher<? super X> first;

        public CombinableEitherMatcher(Matcher<? super X> matcher) {
            this.first = matcher;
        }

        /* renamed from: or */
        public CombinableMatcher<X> mo18966or(Matcher<? super X> other) {
            return new CombinableMatcher(this.first).mo18964or(other);
        }
    }
}
