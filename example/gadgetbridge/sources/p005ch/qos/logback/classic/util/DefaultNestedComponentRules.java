package p005ch.qos.logback.classic.util;

import p005ch.qos.logback.classic.PatternLayout;
import p005ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import p005ch.qos.logback.core.AppenderBase;
import p005ch.qos.logback.core.UnsynchronizedAppenderBase;
import p005ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import p005ch.qos.logback.core.net.ssl.SSLNestedComponentRegistryRules;

/* renamed from: ch.qos.logback.classic.util.DefaultNestedComponentRules */
public class DefaultNestedComponentRules {
    public static void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry defaultNestedComponentRegistry) {
        defaultNestedComponentRegistry.add(AppenderBase.class, "layout", PatternLayout.class);
        defaultNestedComponentRegistry.add(UnsynchronizedAppenderBase.class, "layout", PatternLayout.class);
        defaultNestedComponentRegistry.add(AppenderBase.class, "encoder", PatternLayoutEncoder.class);
        defaultNestedComponentRegistry.add(UnsynchronizedAppenderBase.class, "encoder", PatternLayoutEncoder.class);
        SSLNestedComponentRegistryRules.addDefaultNestedComponentRegistryRules(defaultNestedComponentRegistry);
    }
}
