package p005ch.qos.logback.core.joran.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.action.ImplicitAction;
import p005ch.qos.logback.core.joran.event.BodyEvent;
import p005ch.qos.logback.core.joran.event.EndEvent;
import p005ch.qos.logback.core.joran.event.StartEvent;

/* renamed from: ch.qos.logback.core.joran.spi.Interpreter */
public class Interpreter {
    private static List<Action> EMPTY_LIST = new Vector(0);
    Stack<List<Action>> actionListStack;
    private final CAI_WithLocatorSupport cai;
    private ElementPath elementPath;
    EventPlayer eventPlayer;
    private final ArrayList<ImplicitAction> implicitActions;
    private final InterpretationContext interpretationContext;
    Locator locator;
    private final RuleStore ruleStore;
    ElementPath skip = null;

    public Interpreter(Context context, RuleStore ruleStore2, ElementPath elementPath2) {
        this.cai = new CAI_WithLocatorSupport(context, this);
        this.ruleStore = ruleStore2;
        this.interpretationContext = new InterpretationContext(context, this);
        this.implicitActions = new ArrayList<>(3);
        this.elementPath = elementPath2;
        this.actionListStack = new Stack<>();
        this.eventPlayer = new EventPlayer(this);
    }

    private void callBodyAction(List<Action> list, String str) {
        if (list != null) {
            for (Action next : list) {
                try {
                    next.body(this.interpretationContext, str);
                } catch (ActionException e) {
                    CAI_WithLocatorSupport cAI_WithLocatorSupport = this.cai;
                    cAI_WithLocatorSupport.addError("Exception in end() methd for action [" + next + "]", e);
                }
            }
        }
    }

    private void callEndAction(List<Action> list, String str) {
        String str2;
        StringBuilder sb;
        CAI_WithLocatorSupport cAI_WithLocatorSupport;
        if (list != null) {
            for (Action end : list) {
                try {
                    end.end(this.interpretationContext, str);
                } catch (ActionException e) {
                    e = e;
                    cAI_WithLocatorSupport = this.cai;
                    sb = new StringBuilder();
                    str2 = "ActionException in Action for tag [";
                } catch (RuntimeException e2) {
                    e = e2;
                    cAI_WithLocatorSupport = this.cai;
                    sb = new StringBuilder();
                    str2 = "RuntimeException in Action for tag [";
                }
            }
            return;
        }
        return;
        sb.append(str2);
        sb.append(str);
        sb.append("]");
        cAI_WithLocatorSupport.addError(sb.toString(), e);
    }

    private void endElement(String str, String str2, String str3) {
        List<Action> pop = this.actionListStack.pop();
        ElementPath elementPath2 = this.skip;
        if (elementPath2 != null) {
            if (elementPath2.equals(this.elementPath)) {
                this.skip = null;
            }
        } else if (pop != EMPTY_LIST) {
            callEndAction(pop, getTagName(str2, str3));
        }
        this.elementPath.pop();
    }

    private void pushEmptyActionList() {
        this.actionListStack.add(EMPTY_LIST);
    }

    private void startElement(String str, String str2, String str3, Attributes attributes) {
        String tagName = getTagName(str2, str3);
        this.elementPath.push(tagName);
        if (this.skip != null) {
            pushEmptyActionList();
            return;
        }
        List<Action> applicableActionList = getApplicableActionList(this.elementPath, attributes);
        if (applicableActionList != null) {
            this.actionListStack.add(applicableActionList);
            callBeginAction(applicableActionList, tagName, attributes);
            return;
        }
        pushEmptyActionList();
        this.cai.addError("no applicable action for [" + tagName + "], current ElementPath  is [" + this.elementPath + "]");
    }

    public void addImplicitAction(ImplicitAction implicitAction) {
        this.implicitActions.add(implicitAction);
    }

    /* access modifiers changed from: package-private */
    public void callBeginAction(List<Action> list, String str, Attributes attributes) {
        String str2;
        StringBuilder sb;
        CAI_WithLocatorSupport cAI_WithLocatorSupport;
        if (list != null) {
            for (Action begin : list) {
                try {
                    begin.begin(this.interpretationContext, str, attributes);
                } catch (ActionException e) {
                    e = e;
                    this.skip = this.elementPath.duplicate();
                    cAI_WithLocatorSupport = this.cai;
                    sb = new StringBuilder();
                    str2 = "ActionException in Action for tag [";
                } catch (RuntimeException e2) {
                    e = e2;
                    this.skip = this.elementPath.duplicate();
                    cAI_WithLocatorSupport = this.cai;
                    sb = new StringBuilder();
                    str2 = "RuntimeException in Action for tag [";
                }
            }
            return;
        }
        return;
        sb.append(str2);
        sb.append(str);
        sb.append("]");
        cAI_WithLocatorSupport.addError(sb.toString(), e);
    }

    public void characters(BodyEvent bodyEvent) {
        setDocumentLocator(bodyEvent.locator);
        String text = bodyEvent.getText();
        List peek = this.actionListStack.peek();
        if (text != null) {
            String trim = text.trim();
            if (trim.length() > 0) {
                callBodyAction(peek, trim);
            }
        }
    }

    public void endElement(EndEvent endEvent) {
        setDocumentLocator(endEvent.locator);
        endElement(endEvent.namespaceURI, endEvent.localName, endEvent.qName);
    }

    /* access modifiers changed from: package-private */
    public List<Action> getApplicableActionList(ElementPath elementPath2, Attributes attributes) {
        List<Action> matchActions = this.ruleStore.matchActions(elementPath2);
        return matchActions == null ? lookupImplicitAction(elementPath2, attributes, this.interpretationContext) : matchActions;
    }

    public EventPlayer getEventPlayer() {
        return this.eventPlayer;
    }

    public InterpretationContext getExecutionContext() {
        return getInterpretationContext();
    }

    public InterpretationContext getInterpretationContext() {
        return this.interpretationContext;
    }

    public Locator getLocator() {
        return this.locator;
    }

    public RuleStore getRuleStore() {
        return this.ruleStore;
    }

    /* access modifiers changed from: package-private */
    public String getTagName(String str, String str2) {
        return (str == null || str.length() < 1) ? str2 : str;
    }

    /* access modifiers changed from: package-private */
    public List<Action> lookupImplicitAction(ElementPath elementPath2, Attributes attributes, InterpretationContext interpretationContext2) {
        int size = this.implicitActions.size();
        for (int i = 0; i < size; i++) {
            ImplicitAction implicitAction = this.implicitActions.get(i);
            if (implicitAction.isApplicable(elementPath2, attributes, interpretationContext2)) {
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(implicitAction);
                return arrayList;
            }
        }
        return null;
    }

    public void setDocumentLocator(Locator locator2) {
        this.locator = locator2;
    }

    public void setInterpretationContextPropertiesMap(Map<String, String> map) {
        this.interpretationContext.setPropertiesMap(map);
    }

    public void startDocument() {
    }

    public void startElement(StartEvent startEvent) {
        setDocumentLocator(startEvent.getLocator());
        startElement(startEvent.namespaceURI, startEvent.localName, startEvent.qName, startEvent.attributes);
    }
}
