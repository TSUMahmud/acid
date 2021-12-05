package org.hamcrest.core;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class StringEndsWith extends SubstringMatcher {
    public StringEndsWith(String substring) {
        super(substring);
    }

    /* access modifiers changed from: protected */
    public boolean evalSubstringOf(String s) {
        return s.endsWith(this.substring);
    }

    /* access modifiers changed from: protected */
    public String relationship() {
        return "ending with";
    }

    @Factory
    public static Matcher<String> endsWith(String suffix) {
        return new StringEndsWith(suffix);
    }
}
