package p005ch.qos.logback.core.joran.action;

import p005ch.qos.logback.core.joran.util.PropertySetter;
import p005ch.qos.logback.core.util.AggregationType;

/* renamed from: ch.qos.logback.core.joran.action.IADataForComplexProperty */
public class IADataForComplexProperty {
    final AggregationType aggregationType;
    final String complexPropertyName;
    boolean inError;
    private Object nestedComplexProperty;
    final PropertySetter parentBean;

    public IADataForComplexProperty(PropertySetter propertySetter, AggregationType aggregationType2, String str) {
        this.parentBean = propertySetter;
        this.aggregationType = aggregationType2;
        this.complexPropertyName = str;
    }

    public AggregationType getAggregationType() {
        return this.aggregationType;
    }

    public String getComplexPropertyName() {
        return this.complexPropertyName;
    }

    public Object getNestedComplexProperty() {
        return this.nestedComplexProperty;
    }

    public void setNestedComplexProperty(Object obj) {
        this.nestedComplexProperty = obj;
    }
}
