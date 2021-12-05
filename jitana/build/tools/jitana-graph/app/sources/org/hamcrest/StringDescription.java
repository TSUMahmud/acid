package org.hamcrest;

import java.io.IOException;

public class StringDescription extends BaseDescription {
    private final Appendable out;

    public StringDescription() {
        this(new StringBuilder());
    }

    public StringDescription(Appendable out2) {
        this.out = out2;
    }

    public static String toString(SelfDescribing selfDescribing) {
        return new StringDescription().appendDescriptionOf(selfDescribing).toString();
    }

    public static String asString(SelfDescribing selfDescribing) {
        return toString(selfDescribing);
    }

    /* access modifiers changed from: protected */
    public void append(String str) {
        try {
            this.out.append(str);
        } catch (IOException e) {
            throw new RuntimeException("Could not write description", e);
        }
    }

    /* access modifiers changed from: protected */
    public void append(char c) {
        try {
            this.out.append(c);
        } catch (IOException e) {
            throw new RuntimeException("Could not write description", e);
        }
    }

    public String toString() {
        return this.out.toString();
    }
}
