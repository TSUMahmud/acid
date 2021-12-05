package p005ch.qos.logback.classic.sift;

import java.util.List;
import java.util.Map;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.sift.AbstractAppenderFactoryUsingJoran;
import p005ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;

/* renamed from: ch.qos.logback.classic.sift.AppenderFactoryUsingJoran */
public class AppenderFactoryUsingJoran extends AbstractAppenderFactoryUsingJoran<ILoggingEvent> {
    AppenderFactoryUsingJoran(List<SaxEvent> list, String str, Map<String, String> map) {
        super(list, str, map);
    }

    public SiftingJoranConfiguratorBase<ILoggingEvent> getSiftingJoranConfigurator(String str) {
        return new SiftingJoranConfigurator(this.key, str, this.parentPropertyMap);
    }
}
