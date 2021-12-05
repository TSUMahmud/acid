package p005ch.qos.logback.core.subst;

import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.core.subst.Node */
public class Node {
    Object defaultPart;
    Node next;
    Object payload;
    Type type;

    /* renamed from: ch.qos.logback.core.subst.Node$1 */
    static /* synthetic */ class C05321 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$subst$Node$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$subst$Node$Type[Type.LITERAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$subst$Node$Type[Type.VARIABLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* renamed from: ch.qos.logback.core.subst.Node$Type */
    enum Type {
        LITERAL,
        VARIABLE
    }

    public Node(Type type2, Object obj) {
        this.type = type2;
        this.payload = obj;
    }

    public Node(Type type2, Object obj, Object obj2) {
        this.type = type2;
        this.payload = obj;
        this.defaultPart = obj2;
    }

    /* access modifiers changed from: package-private */
    public void append(Node node) {
        if (node != null) {
            Node node2 = this;
            while (true) {
                Node node3 = node2.next;
                if (node3 == null) {
                    node2.next = node;
                    return;
                }
                node2 = node3;
            }
        }
    }

    public void dump() {
        System.out.print(toString());
        System.out.print(" -> ");
        Node node = this.next;
        if (node != null) {
            node.dump();
        } else {
            System.out.print(" null");
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node node = (Node) obj;
        if (this.type != node.type) {
            return false;
        }
        Object obj2 = this.payload;
        if (obj2 == null ? node.payload != null : !obj2.equals(node.payload)) {
            return false;
        }
        Object obj3 = this.defaultPart;
        if (obj3 == null ? node.defaultPart != null : !obj3.equals(node.defaultPart)) {
            return false;
        }
        Node node2 = this.next;
        Node node3 = node.next;
        return node2 == null ? node3 == null : node2.equals(node3);
    }

    public int hashCode() {
        Type type2 = this.type;
        int i = 0;
        int hashCode = (type2 != null ? type2.hashCode() : 0) * 31;
        Object obj = this.payload;
        int hashCode2 = (hashCode + (obj != null ? obj.hashCode() : 0)) * 31;
        Object obj2 = this.defaultPart;
        int hashCode3 = (hashCode2 + (obj2 != null ? obj2.hashCode() : 0)) * 31;
        Node node = this.next;
        if (node != null) {
            i = node.hashCode();
        }
        return hashCode3 + i;
    }

    /* access modifiers changed from: package-private */
    public void recursive(Node node, StringBuilder sb) {
        while (node != null) {
            sb.append(node.toString());
            sb.append(" --> ");
            node = node.next;
        }
        sb.append("null ");
    }

    public void setNext(Node node) {
        this.next = node;
    }

    public String toString() {
        int i = C05321.$SwitchMap$ch$qos$logback$core$subst$Node$Type[this.type.ordinal()];
        if (i == 1) {
            return "Node{type=" + this.type + ", payload='" + this.payload + "'}";
        } else if (i != 2) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            Object obj = this.defaultPart;
            if (obj != null) {
                recursive((Node) obj, sb2);
            }
            recursive((Node) this.payload, sb);
            String str = "Node{type=" + this.type + ", payload='" + sb.toString() + "'";
            if (this.defaultPart != null) {
                str = str + ", defaultPart=" + sb2.toString();
            }
            return str + CoreConstants.CURLY_RIGHT;
        }
    }
}
