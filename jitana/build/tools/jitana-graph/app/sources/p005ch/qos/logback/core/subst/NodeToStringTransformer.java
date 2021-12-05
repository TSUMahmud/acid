package p005ch.qos.logback.core.subst;

import java.util.Iterator;
import java.util.Stack;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.spi.PropertyContainer;
import p005ch.qos.logback.core.spi.ScanException;
import p005ch.qos.logback.core.subst.Node;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.subst.NodeToStringTransformer */
public class NodeToStringTransformer {
    final Node node;
    final PropertyContainer propertyContainer0;
    final PropertyContainer propertyContainer1;

    /* renamed from: ch.qos.logback.core.subst.NodeToStringTransformer$1 */
    static /* synthetic */ class C05331 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$subst$Node$Type = new int[Node.Type.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$subst$Node$Type[Node.Type.LITERAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$subst$Node$Type[Node.Type.VARIABLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public NodeToStringTransformer(Node node2, PropertyContainer propertyContainer) {
        this(node2, propertyContainer, (PropertyContainer) null);
    }

    public NodeToStringTransformer(Node node2, PropertyContainer propertyContainer, PropertyContainer propertyContainer2) {
        this.node = node2;
        this.propertyContainer0 = propertyContainer;
        this.propertyContainer1 = propertyContainer2;
    }

    private void compileNode(Node node2, StringBuilder sb, Stack<Node> stack) throws ScanException {
        while (node2 != null) {
            int i = C05331.$SwitchMap$ch$qos$logback$core$subst$Node$Type[node2.type.ordinal()];
            if (i == 1) {
                handleLiteral(node2, sb);
            } else if (i == 2) {
                handleVariable(node2, sb, stack);
            }
            node2 = node2.next;
        }
    }

    private String constructRecursionErrorMessage(Stack<Node> stack) {
        StringBuilder sb = new StringBuilder("Circular variable reference detected while parsing input [");
        Iterator it = stack.iterator();
        while (it.hasNext()) {
            Node node2 = (Node) it.next();
            sb.append("${");
            sb.append(variableNodeValue(node2));
            sb.append("}");
            if (stack.lastElement() != node2) {
                sb.append(" --> ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private boolean equalNodes(Node node2, Node node3) {
        if (node2.type != null && !node2.type.equals(node3.type)) {
            return false;
        }
        if (node2.payload == null || node2.payload.equals(node3.payload)) {
            return node2.defaultPart == null || node2.defaultPart.equals(node3.defaultPart);
        }
        return false;
    }

    private void handleLiteral(Node node2, StringBuilder sb) {
        sb.append((String) node2.payload);
    }

    private void handleVariable(Node node2, StringBuilder sb, Stack<Node> stack) throws ScanException {
        boolean haveVisitedNodeAlready = haveVisitedNodeAlready(node2, stack);
        stack.push(node2);
        if (!haveVisitedNodeAlready) {
            StringBuilder sb2 = new StringBuilder();
            compileNode((Node) node2.payload, sb2, stack);
            String sb3 = sb2.toString();
            String lookupKey = lookupKey(sb3);
            if (lookupKey != null) {
                compileNode(tokenizeAndParseString(lookupKey), sb, stack);
                stack.pop();
            } else if (node2.defaultPart == null) {
                sb.append(sb3 + CoreConstants.UNDEFINED_PROPERTY_SUFFIX);
                stack.pop();
            } else {
                StringBuilder sb4 = new StringBuilder();
                compileNode((Node) node2.defaultPart, sb4, stack);
                stack.pop();
                sb.append(sb4.toString());
            }
        } else {
            throw new IllegalArgumentException(constructRecursionErrorMessage(stack));
        }
    }

    private boolean haveVisitedNodeAlready(Node node2, Stack<Node> stack) {
        Iterator it = stack.iterator();
        while (it.hasNext()) {
            if (equalNodes(node2, (Node) it.next())) {
                return true;
            }
        }
        return false;
    }

    private String lookupKey(String str) {
        String property;
        String property2 = this.propertyContainer0.getProperty(str);
        if (property2 != null) {
            return property2;
        }
        PropertyContainer propertyContainer = this.propertyContainer1;
        if (propertyContainer != null && (property = propertyContainer.getProperty(str)) != null) {
            return property;
        }
        String systemProperty = OptionHelper.getSystemProperty(str, (String) null);
        if (systemProperty != null) {
            return systemProperty;
        }
        String env = OptionHelper.getEnv(str);
        if (env != null) {
            return env;
        }
        return null;
    }

    public static String substituteVariable(String str, PropertyContainer propertyContainer, PropertyContainer propertyContainer2) throws ScanException {
        return new NodeToStringTransformer(tokenizeAndParseString(str), propertyContainer, propertyContainer2).transform();
    }

    private static Node tokenizeAndParseString(String str) throws ScanException {
        return new Parser(new Tokenizer(str).tokenize()).parse();
    }

    private String variableNodeValue(Node node2) {
        return (String) ((Node) node2.payload).payload;
    }

    public String transform() throws ScanException {
        StringBuilder sb = new StringBuilder();
        compileNode(this.node, sb, new Stack());
        return sb.toString();
    }
}
