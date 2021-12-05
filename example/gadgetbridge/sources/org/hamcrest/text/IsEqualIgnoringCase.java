package org.hamcrest.text;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsEqualIgnoringCase extends TypeSafeMatcher<String> {
    private final String string;

    public IsEqualIgnoringCase(String string2) {
        if (string2 != null) {
            this.string = string2;
            return;
        }
        throw new IllegalArgumentException("Non-null value required by IsEqualIgnoringCase()");
    }

    public boolean matchesSafely(String item) {
        return this.string.equalsIgnoreCase(item);
    }

    public void describeMismatchSafely(String item, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendText(item);
    }

    public void describeTo(Description description) {
        description.appendText("equalToIgnoringCase(").appendValue(this.string).appendText(")");
    }

    @Factory
    public static Matcher<String> equalToIgnoringCase(String expectedString) {
        return new IsEqualIgnoringCase(expectedString);
    }
}
