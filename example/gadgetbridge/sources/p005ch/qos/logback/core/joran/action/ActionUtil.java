package p005ch.qos.logback.core.joran.action;

import java.util.Properties;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.util.ContextUtil;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.action.ActionUtil */
public class ActionUtil {

    /* renamed from: ch.qos.logback.core.joran.action.ActionUtil$1 */
    static /* synthetic */ class C05111 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope = new int[Scope.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope[Scope.LOCAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope[Scope.CONTEXT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope[Scope.SYSTEM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* renamed from: ch.qos.logback.core.joran.action.ActionUtil$Scope */
    public enum Scope {
        LOCAL,
        CONTEXT,
        SYSTEM
    }

    public static void setProperties(InterpretationContext interpretationContext, Properties properties, Scope scope) {
        int i = C05111.$SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope[scope.ordinal()];
        if (i == 1) {
            interpretationContext.addSubstitutionProperties(properties);
        } else if (i == 2) {
            new ContextUtil(interpretationContext.getContext()).addProperties(properties);
        } else if (i == 3) {
            OptionHelper.setSystemProperties(interpretationContext, properties);
        }
    }

    public static void setProperty(InterpretationContext interpretationContext, String str, String str2, Scope scope) {
        int i = C05111.$SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope[scope.ordinal()];
        if (i == 1) {
            interpretationContext.addSubstitutionProperty(str, str2);
        } else if (i == 2) {
            interpretationContext.getContext().putProperty(str, str2);
        } else if (i == 3) {
            OptionHelper.setSystemProperty(interpretationContext, str, str2);
        }
    }

    public static Scope stringToScope(String str) {
        return Scope.SYSTEM.toString().equalsIgnoreCase(str) ? Scope.SYSTEM : Scope.CONTEXT.toString().equalsIgnoreCase(str) ? Scope.CONTEXT : Scope.LOCAL;
    }
}
