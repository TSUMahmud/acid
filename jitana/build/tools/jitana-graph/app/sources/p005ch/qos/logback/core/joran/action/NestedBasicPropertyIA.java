package p005ch.qos.logback.core.joran.action;

import java.util.Stack;
import org.xml.sax.Attributes;
import p005ch.qos.logback.core.joran.spi.ElementPath;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.joran.util.PropertySetter;
import p005ch.qos.logback.core.util.AggregationType;

/* renamed from: ch.qos.logback.core.joran.action.NestedBasicPropertyIA */
public class NestedBasicPropertyIA extends ImplicitAction {
    Stack<IADataForBasicProperty> actionDataStack = new Stack<>();

    /* renamed from: ch.qos.logback.core.joran.action.NestedBasicPropertyIA$1 */
    static /* synthetic */ class C05121 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$util$AggregationType = new int[AggregationType.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.NOT_FOUND.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.AS_COMPLEX_PROPERTY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.AS_COMPLEX_PROPERTY_COLLECTION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.AS_BASIC_PROPERTY.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.AS_BASIC_PROPERTY_COLLECTION.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
    }

    public void body(InterpretationContext interpretationContext, String str) {
        String subst = interpretationContext.subst(str);
        IADataForBasicProperty peek = this.actionDataStack.peek();
        int i = C05121.$SwitchMap$ch$qos$logback$core$util$AggregationType[peek.aggregationType.ordinal()];
        if (i == 4) {
            peek.parentBean.setProperty(peek.propertyName, subst);
        } else if (i == 5) {
            peek.parentBean.addBasicProperty(peek.propertyName, subst);
        }
    }

    public void end(InterpretationContext interpretationContext, String str) {
        this.actionDataStack.pop();
    }

    public boolean isApplicable(ElementPath elementPath, Attributes attributes, InterpretationContext interpretationContext) {
        String peekLast = elementPath.peekLast();
        if (interpretationContext.isEmpty()) {
            return false;
        }
        PropertySetter propertySetter = new PropertySetter(interpretationContext.peekObject());
        propertySetter.setContext(this.context);
        AggregationType computeAggregationType = propertySetter.computeAggregationType(peekLast);
        int i = C05121.$SwitchMap$ch$qos$logback$core$util$AggregationType[computeAggregationType.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            return false;
        }
        if (i == 4 || i == 5) {
            this.actionDataStack.push(new IADataForBasicProperty(propertySetter, computeAggregationType, peekLast));
            return true;
        }
        addError("PropertySetter.canContainComponent returned " + computeAggregationType);
        return false;
    }
}
