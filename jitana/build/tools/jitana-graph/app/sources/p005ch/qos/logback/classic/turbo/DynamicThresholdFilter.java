package p005ch.qos.logback.classic.turbo;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.Marker;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.classic.turbo.DynamicThresholdFilter */
public class DynamicThresholdFilter extends TurboFilter {
    private Level defaultThreshold = Level.ERROR;
    private String key;
    private FilterReply onHigherOrEqual = FilterReply.NEUTRAL;
    private FilterReply onLower = FilterReply.DENY;
    private Map<String, Level> valueLevelMap = new HashMap();

    public void addMDCValueLevelPair(MDCValueLevelPair mDCValueLevelPair) {
        if (this.valueLevelMap.containsKey(mDCValueLevelPair.getValue())) {
            addError(mDCValueLevelPair.getValue() + " has been already set");
            return;
        }
        this.valueLevelMap.put(mDCValueLevelPair.getValue(), mDCValueLevelPair.getLevel());
    }

    public FilterReply decide(Marker marker, Logger logger, Level level, String str, Object[] objArr, Throwable th) {
        String str2 = MDC.get(this.key);
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        Level level2 = null;
        if (str2 != null) {
            level2 = this.valueLevelMap.get(str2);
        }
        if (level2 == null) {
            level2 = this.defaultThreshold;
        }
        return level.isGreaterOrEqual(level2) ? this.onHigherOrEqual : this.onLower;
    }

    public Level getDefaultThreshold() {
        return this.defaultThreshold;
    }

    public String getKey() {
        return this.key;
    }

    public FilterReply getOnHigherOrEqual() {
        return this.onHigherOrEqual;
    }

    public FilterReply getOnLower() {
        return this.onLower;
    }

    public void setDefaultThreshold(Level level) {
        this.defaultThreshold = level;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public void setOnHigherOrEqual(FilterReply filterReply) {
        this.onHigherOrEqual = filterReply;
    }

    public void setOnLower(FilterReply filterReply) {
        this.onLower = filterReply;
    }

    public void start() {
        if (this.key == null) {
            addError("No key name was specified");
        }
        super.start();
    }
}
