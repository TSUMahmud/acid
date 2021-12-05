package org.hamcrest.core;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class StringContains extends SubstringMatcher {
    public StringContains(String substring) {
        super(substring);
    }

    /* access modifiers changed from: protected */
    public boolean evalSubstringOf(String s) {
        return s.indexOf(this.substring) >= 0;
    }

    /* access modifiers changed from: protected */
    public String relationship() {
        return "containing";
    }

    @Factory
    public static Matcher<String> containsString(String substring) {
        return new StringContains(substring);
    }
}
