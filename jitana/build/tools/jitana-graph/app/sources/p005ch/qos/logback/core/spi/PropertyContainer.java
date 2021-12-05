package p005ch.qos.logback.core.spi;

import java.util.Map;

/* renamed from: ch.qos.logback.core.spi.PropertyContainer */
public interface PropertyContainer {
    Map<String, String> getCopyOfPropertyMap();

    String getProperty(String str);
}
