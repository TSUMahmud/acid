package org.hamcrest.core;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public abstract class SubstringMatcher extends TypeSafeMatcher<String> {
    protected final String substring;

    /* access modifiers changed from: protected */
    public abstract boolean evalSubstringOf(String str);

    /* access modifiers changed from: protected */
    public abstract String relationship();

    protected SubstringMatcher(String substring2) {
        this.substring = substring2;
    }

    public boolean matchesSafely(String item) {
        return evalSubstringOf(item);
    }

    public void describeMismatchSafely(String item, Description mismatchDescription) {
        mismatchDescription.appendText("was \"").appendText(item).appendText("\"");
    }

    public void describeTo(Description description) {
        description.appendText("a string ").appendText(relationship()).appendText(StringUtils.SPACE).appendValue(this.substring);
    }
}
