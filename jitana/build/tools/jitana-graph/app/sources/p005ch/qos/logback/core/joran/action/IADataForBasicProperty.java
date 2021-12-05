package p005ch.qos.logback.core.joran.action;

import p005ch.qos.logback.core.joran.util.PropertySetter;
import p005ch.qos.logback.core.util.AggregationType;

/* renamed from: ch.qos.logback.core.joran.action.IADataForBasicProperty */
class IADataForBasicProperty {
    final AggregationType aggregationType;
    boolean inError;
    final PropertySetter parentBean;
    final String propertyName;

    IADataForBasicProperty(PropertySetter propertySetter, AggregationType aggregationType2, String str) {
        this.parentBean = propertySetter;
        this.aggregationType = aggregationType2;
        this.propertyName = str;
    }
}
