package p005ch.qos.logback.core.joran.spi;

import java.util.List;
import p005ch.qos.logback.core.joran.action.Action;

/* renamed from: ch.qos.logback.core.joran.spi.RuleStore */
public interface RuleStore {
    void addRule(ElementSelector elementSelector, Action action);

    void addRule(ElementSelector elementSelector, String str) throws ClassNotFoundException;

    List<Action> matchActions(ElementPath elementPath);
}
