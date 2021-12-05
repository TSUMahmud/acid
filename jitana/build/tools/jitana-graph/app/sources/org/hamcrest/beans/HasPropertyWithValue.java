package org.hamcrest.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class HasPropertyWithValue<T> extends TypeSafeDiagnosingMatcher<T> {
    private static final Condition.Step<PropertyDescriptor, Method> WITH_READ_METHOD = withReadMethod();
    private final String propertyName;
    private final Matcher<Object> valueMatcher;

    public HasPropertyWithValue(String propertyName2, Matcher<?> valueMatcher2) {
        this.propertyName = propertyName2;
        this.valueMatcher = nastyGenericsWorkaround(valueMatcher2);
    }

    public boolean matchesSafely(T bean, Description mismatch) {
        Condition<U> and = propertyOn(bean, mismatch).and(WITH_READ_METHOD).and(withPropertyValue(bean));
        Matcher<Object> matcher = this.valueMatcher;
        return and.matching(matcher, "property '" + this.propertyName + "' ");
    }

    public void describeTo(Description description) {
        description.appendText("hasProperty(").appendValue(this.propertyName).appendText(", ").appendDescriptionOf(this.valueMatcher).appendText(")");
    }

    private Condition<PropertyDescriptor> propertyOn(T bean, Description mismatch) {
        PropertyDescriptor property = PropertyUtil.getPropertyDescriptor(this.propertyName, bean);
        if (property != null) {
            return Condition.matched(property, mismatch);
        }
        mismatch.appendText("No property \"" + this.propertyName + "\"");
        return Condition.notMatched();
    }

    private Condition.Step<Method, Object> withPropertyValue(final T bean) {
        return new Condition.Step<Method, Object>() {
            public Condition<Object> apply(Method readMethod, Description mismatch) {
                try {
                    return Condition.matched(readMethod.invoke(bean, PropertyUtil.NO_ARGUMENTS), mismatch);
                } catch (Exception e) {
                    mismatch.appendText(e.getMessage());
                    return Condition.notMatched();
                }
            }
        };
    }

    private static Matcher<Object> nastyGenericsWorkaround(Matcher<?> valueMatcher2) {
        return valueMatcher2;
    }

    private static Condition.Step<PropertyDescriptor, Method> withReadMethod() {
        return new Condition.Step<PropertyDescriptor, Method>() {
            public Condition<Method> apply(PropertyDescriptor property, Description mismatch) {
                Method readMethod = property.getReadMethod();
                if (readMethod != null) {
                    return Condition.matched(readMethod, mismatch);
                }
                mismatch.appendText("property \"" + property.getName() + "\" is not readable");
                return Condition.notMatched();
            }
        };
    }

    @Factory
    public static <T> Matcher<T> hasProperty(String propertyName2, Matcher<?> valueMatcher2) {
        return new HasPropertyWithValue(propertyName2, valueMatcher2);
    }
}
