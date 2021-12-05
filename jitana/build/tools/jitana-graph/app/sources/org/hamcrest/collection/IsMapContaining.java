package org.hamcrest.collection;

import java.util.Map;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;

public class IsMapContaining<K, V> extends TypeSafeMatcher<Map<? extends K, ? extends V>> {
    private final Matcher<? super K> keyMatcher;
    private final Matcher<? super V> valueMatcher;

    public IsMapContaining(Matcher<? super K> keyMatcher2, Matcher<? super V> valueMatcher2) {
        this.keyMatcher = keyMatcher2;
        this.valueMatcher = valueMatcher2;
    }

    public boolean matchesSafely(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            if (this.keyMatcher.matches(entry.getKey()) && this.valueMatcher.matches(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public void describeMismatchSafely(Map<? extends K, ? extends V> map, Description mismatchDescription) {
        mismatchDescription.appendText("map was ").appendValueList("[", ", ", "]", map.entrySet());
    }

    public void describeTo(Description description) {
        description.appendText("map containing [").appendDescriptionOf(this.keyMatcher).appendText("->").appendDescriptionOf(this.valueMatcher).appendText("]");
    }

    @Factory
    public static <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(Matcher<? super K> keyMatcher2, Matcher<? super V> valueMatcher2) {
        return new IsMapContaining(keyMatcher2, valueMatcher2);
    }

    @Factory
    public static <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(K key, V value) {
        return new IsMapContaining(IsEqual.equalTo(key), IsEqual.equalTo(value));
    }

    @Factory
    public static <K> Matcher<Map<? extends K, ?>> hasKey(Matcher<? super K> keyMatcher2) {
        return new IsMapContaining(keyMatcher2, IsAnything.anything());
    }

    @Factory
    public static <K> Matcher<Map<? extends K, ?>> hasKey(K key) {
        return new IsMapContaining(IsEqual.equalTo(key), IsAnything.anything());
    }

    @Factory
    public static <V> Matcher<Map<?, ? extends V>> hasValue(Matcher<? super V> valueMatcher2) {
        return new IsMapContaining(IsAnything.anything(), valueMatcher2);
    }

    @Factory
    public static <V> Matcher<Map<?, ? extends V>> hasValue(V value) {
        return new IsMapContaining(IsAnything.anything(), IsEqual.equalTo(value));
    }
}
