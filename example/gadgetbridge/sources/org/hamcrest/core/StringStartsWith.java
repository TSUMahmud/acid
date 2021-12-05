package org.hamcrest.core;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class StringStartsWith extends SubstringMatcher {
    public StringStartsWith(String substring) {
        super(substring);
    }

    /* access modifiers changed from: protected */
    public boolean evalSubstringOf(String s) {
        return s.startsWith(this.substring);
    }

    /* access modifiers changed from: protected */
    public String relationship() {
        return "starting with";
    }

    @Factory
    public static Matcher<String> startsWith(String prefix) {
        return new StringStartsWith(prefix);
    }
}
