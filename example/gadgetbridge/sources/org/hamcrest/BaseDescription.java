package org.hamcrest;

import java.util.Arrays;
import java.util.Iterator;
import org.hamcrest.internal.ArrayIterator;
import org.hamcrest.internal.SelfDescribingValueIterator;
import p005ch.qos.logback.core.CoreConstants;

public abstract class BaseDescription implements Description {
    /* access modifiers changed from: protected */
    public abstract void append(char c);

    public Description appendText(String text) {
        append(text);
        return this;
    }

    public Description appendDescriptionOf(SelfDescribing value) {
        value.describeTo(this);
        return this;
    }

    public Description appendValue(Object value) {
        if (value == null) {
            append("null");
        } else if (value instanceof String) {
            toJavaSyntax((String) value);
        } else if (value instanceof Character) {
            append((char) CoreConstants.DOUBLE_QUOTE_CHAR);
            toJavaSyntax(((Character) value).charValue());
            append((char) CoreConstants.DOUBLE_QUOTE_CHAR);
        } else if (value instanceof Short) {
            append('<');
            append(descriptionOf(value));
            append("s>");
        } else if (value instanceof Long) {
            append('<');
            append(descriptionOf(value));
            append("L>");
        } else if (value instanceof Float) {
            append('<');
            append(descriptionOf(value));
            append("F>");
        } else if (value.getClass().isArray()) {
            appendValueList("[", ", ", "]", new ArrayIterator(value));
        } else {
            append('<');
            append(descriptionOf(value));
            append('>');
        }
        return this;
    }

    private String descriptionOf(Object value) {
        try {
            return String.valueOf(value);
        } catch (Exception e) {
            return value.getClass().getName() + "@" + Integer.toHexString(value.hashCode());
        }
    }

    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return appendValueList(start, separator, end, Arrays.asList(values));
    }

    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        return appendValueList(start, separator, end, values.iterator());
    }

    private <T> Description appendValueList(String start, String separator, String end, Iterator<T> values) {
        return appendList(start, separator, end, (Iterator<? extends SelfDescribing>) new SelfDescribingValueIterator(values));
    }

    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        return appendList(start, separator, end, values.iterator());
    }

    private Description appendList(String start, String separator, String end, Iterator<? extends SelfDescribing> i) {
        boolean separate = false;
        append(start);
        while (i.hasNext()) {
            if (separate) {
                append(separator);
            }
            appendDescriptionOf((SelfDescribing) i.next());
            separate = true;
        }
        append(end);
        return this;
    }

    /* access modifiers changed from: protected */
    public void append(String str) {
        for (int i = 0; i < str.length(); i++) {
            append(str.charAt(i));
        }
    }

    private void toJavaSyntax(String unformatted) {
        append((char) CoreConstants.DOUBLE_QUOTE_CHAR);
        for (int i = 0; i < unformatted.length(); i++) {
            toJavaSyntax(unformatted.charAt(i));
        }
        append((char) CoreConstants.DOUBLE_QUOTE_CHAR);
    }

    private void toJavaSyntax(char ch) {
        if (ch == 9) {
            append("\\t");
        } else if (ch == 10) {
            append("\\n");
        } else if (ch == 13) {
            append("\\r");
        } else if (ch != '\"') {
            append(ch);
        } else {
            append("\\\"");
        }
    }
}
