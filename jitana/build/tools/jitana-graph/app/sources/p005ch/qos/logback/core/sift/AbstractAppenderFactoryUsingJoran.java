package p005ch.qos.logback.core.sift;

import java.util.List;
import java.util.Map;
import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.joran.spi.JoranException;

/* renamed from: ch.qos.logback.core.sift.AbstractAppenderFactoryUsingJoran */
public abstract class AbstractAppenderFactoryUsingJoran<E> implements AppenderFactory<E> {
    final List<SaxEvent> eventList;
    protected String key;
    protected Map<String, String> parentPropertyMap;

    protected AbstractAppenderFactoryUsingJoran(List<SaxEvent> list, String str, Map<String, String> map) {
        this.eventList = removeSiftElement(list);
        this.key = str;
        this.parentPropertyMap = map;
    }

    public Appender<E> buildAppender(Context context, String str) throws JoranException {
        SiftingJoranConfiguratorBase siftingJoranConfigurator = getSiftingJoranConfigurator(str);
        siftingJoranConfigurator.setContext(context);
        siftingJoranConfigurator.doConfigure(this.eventList);
        return siftingJoranConfigurator.getAppender();
    }

    public List<SaxEvent> getEventList() {
        return this.eventList;
    }

    public abstract SiftingJoranConfiguratorBase<E> getSiftingJoranConfigurator(String str);

    /* access modifiers changed from: package-private */
    public List<SaxEvent> removeSiftElement(List<SaxEvent> list) {
        return list.subList(1, list.size() - 1);
    }
}
