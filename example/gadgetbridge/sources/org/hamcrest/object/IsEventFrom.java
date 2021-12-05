package org.hamcrest.object;

import java.util.EventObject;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class IsEventFrom extends TypeSafeDiagnosingMatcher<EventObject> {
    private final Class<?> eventClass;
    private final Object source;

    public IsEventFrom(Class<?> eventClass2, Object source2) {
        this.eventClass = eventClass2;
        this.source = source2;
    }

    public boolean matchesSafely(EventObject item, Description mismatchDescription) {
        if (!this.eventClass.isInstance(item)) {
            mismatchDescription.appendText("item type was " + item.getClass().getName());
            return false;
        } else if (eventHasSameSource(item)) {
            return true;
        } else {
            mismatchDescription.appendText("source was ").appendValue(item.getSource());
            return false;
        }
    }

    private boolean eventHasSameSource(EventObject ev) {
        return ev.getSource() == this.source;
    }

    public void describeTo(Description description) {
        description.appendText("an event of type ").appendText(this.eventClass.getName()).appendText(" from ").appendValue(this.source);
    }

    @Factory
    public static Matcher<EventObject> eventFrom(Class<? extends EventObject> eventClass2, Object source2) {
        return new IsEventFrom(eventClass2, source2);
    }

    @Factory
    public static Matcher<EventObject> eventFrom(Object source2) {
        return eventFrom(EventObject.class, source2);
    }
}
