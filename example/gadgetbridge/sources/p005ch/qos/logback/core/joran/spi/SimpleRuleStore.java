package p005ch.qos.logback.core.joran.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Marker;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.spi.SimpleRuleStore */
public class SimpleRuleStore extends ContextAwareBase implements RuleStore {
    static String KLEENE_STAR = Marker.ANY_MARKER;
    HashMap<ElementSelector, List<Action>> rules = new HashMap<>();

    public SimpleRuleStore(Context context) {
        setContext(context);
    }

    private boolean isKleeneStar(String str) {
        return KLEENE_STAR.equals(str);
    }

    private boolean isSuffixPattern(ElementSelector elementSelector) {
        return elementSelector.size() > 1 && elementSelector.get(0).equals(KLEENE_STAR);
    }

    public void addRule(ElementSelector elementSelector, Action action) {
        action.setContext(this.context);
        List list = this.rules.get(elementSelector);
        if (list == null) {
            list = new ArrayList();
            this.rules.put(elementSelector, list);
        }
        list.add(action);
    }

    public void addRule(ElementSelector elementSelector, String str) {
        Action action;
        try {
            action = (Action) OptionHelper.instantiateByClassName(str, (Class<?>) Action.class, this.context);
        } catch (Exception e) {
            addError("Could not instantiate class [" + str + "]", e);
            action = null;
        }
        if (action != null) {
            addRule(elementSelector, action);
        }
    }

    /* access modifiers changed from: package-private */
    public List<Action> fullPathMatch(ElementPath elementPath) {
        for (ElementSelector next : this.rules.keySet()) {
            if (next.fullPathMatch(elementPath)) {
                return this.rules.get(next);
            }
        }
        return null;
    }

    public List<Action> matchActions(ElementPath elementPath) {
        List<Action> fullPathMatch = fullPathMatch(elementPath);
        if (fullPathMatch != null) {
            return fullPathMatch;
        }
        List<Action> suffixMatch = suffixMatch(elementPath);
        if (suffixMatch != null) {
            return suffixMatch;
        }
        List<Action> prefixMatch = prefixMatch(elementPath);
        if (prefixMatch != null) {
            return prefixMatch;
        }
        List<Action> middleMatch = middleMatch(elementPath);
        if (middleMatch != null) {
            return middleMatch;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public List<Action> middleMatch(ElementPath elementPath) {
        ElementSelector elementSelector = null;
        int i = 0;
        for (ElementSelector next : this.rules.keySet()) {
            String peekLast = next.peekLast();
            String str = next.size() > 1 ? next.get(0) : null;
            if (isKleeneStar(peekLast) && isKleeneStar(str)) {
                List<String> copyOfPartList = next.getCopyOfPartList();
                if (copyOfPartList.size() > 2) {
                    copyOfPartList.remove(0);
                    copyOfPartList.remove(copyOfPartList.size() - 1);
                }
                ElementSelector elementSelector2 = new ElementSelector(copyOfPartList);
                int size = elementSelector2.isContainedIn(elementPath) ? elementSelector2.size() : 0;
                if (size > i) {
                    elementSelector = next;
                    i = size;
                }
            }
        }
        if (elementSelector != null) {
            return this.rules.get(elementSelector);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public List<Action> prefixMatch(ElementPath elementPath) {
        int prefixMatchLength;
        ElementSelector elementSelector = null;
        int i = 0;
        for (ElementSelector next : this.rules.keySet()) {
            if (isKleeneStar(next.peekLast()) && (prefixMatchLength = next.getPrefixMatchLength(elementPath)) == next.size() - 1 && prefixMatchLength > i) {
                elementSelector = next;
                i = prefixMatchLength;
            }
        }
        if (elementSelector != null) {
            return this.rules.get(elementSelector);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public List<Action> suffixMatch(ElementPath elementPath) {
        int tailMatchLength;
        ElementSelector elementSelector = null;
        int i = 0;
        for (ElementSelector next : this.rules.keySet()) {
            if (isSuffixPattern(next) && (tailMatchLength = next.getTailMatchLength(elementPath)) > i) {
                elementSelector = next;
                i = tailMatchLength;
            }
        }
        if (elementSelector != null) {
            return this.rules.get(elementSelector);
        }
        return null;
    }

    public String toString() {
        return "SimpleRuleStore ( " + "rules = " + this.rules + "  " + " )";
    }
}
