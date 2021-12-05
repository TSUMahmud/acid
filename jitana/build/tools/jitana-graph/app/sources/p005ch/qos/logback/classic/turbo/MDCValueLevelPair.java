package p005ch.qos.logback.classic.turbo;

import p005ch.qos.logback.classic.Level;

/* renamed from: ch.qos.logback.classic.turbo.MDCValueLevelPair */
public class MDCValueLevelPair {
    private Level level;
    private String value;

    public Level getLevel() {
        return this.level;
    }

    public String getValue() {
        return this.value;
    }

    public void setLevel(Level level2) {
        this.level = level2;
    }

    public void setValue(String str) {
        this.value = str;
    }
}
