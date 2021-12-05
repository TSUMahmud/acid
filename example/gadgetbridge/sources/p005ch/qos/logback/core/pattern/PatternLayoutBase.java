package p005ch.qos.logback.core.pattern;

import java.util.HashMap;
import java.util.Map;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.LayoutBase;
import p005ch.qos.logback.core.pattern.parser.Parser;
import p005ch.qos.logback.core.spi.ScanException;
import p005ch.qos.logback.core.status.ErrorStatus;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusManager;

/* renamed from: ch.qos.logback.core.pattern.PatternLayoutBase */
public abstract class PatternLayoutBase<E> extends LayoutBase<E> {
    Converter<E> head;
    Map<String, String> instanceConverterMap = new HashMap();
    protected boolean outputPatternAsHeader = false;
    String pattern;
    protected PostCompileProcessor<E> postCompileProcessor;

    public abstract Map<String, String> getDefaultConverterMap();

    public Map<String, String> getEffectiveConverterMap() {
        Map map;
        HashMap hashMap = new HashMap();
        Map<String, String> defaultConverterMap = getDefaultConverterMap();
        if (defaultConverterMap != null) {
            hashMap.putAll(defaultConverterMap);
        }
        Context context = getContext();
        if (!(context == null || (map = (Map) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY)) == null)) {
            hashMap.putAll(map);
        }
        hashMap.putAll(this.instanceConverterMap);
        return hashMap;
    }

    public Map<String, String> getInstanceConverterMap() {
        return this.instanceConverterMap;
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getPresentationHeader() {
        if (!this.outputPatternAsHeader) {
            return super.getPresentationHeader();
        }
        return getPresentationHeaderPrefix() + this.pattern;
    }

    /* access modifiers changed from: protected */
    public String getPresentationHeaderPrefix() {
        return "";
    }

    public boolean isOutputPatternAsHeader() {
        return this.outputPatternAsHeader;
    }

    /* access modifiers changed from: protected */
    public void setContextForConverters(Converter<E> converter) {
        ConverterUtil.setContextForConverters(getContext(), converter);
    }

    public void setOutputPatternAsHeader(boolean z) {
        this.outputPatternAsHeader = z;
    }

    public void setPattern(String str) {
        this.pattern = str;
    }

    public void setPostCompileProcessor(PostCompileProcessor<E> postCompileProcessor2) {
        this.postCompileProcessor = postCompileProcessor2;
    }

    public void start() {
        String str = this.pattern;
        if (str == null || str.length() == 0) {
            addError("Empty or null pattern.");
            return;
        }
        try {
            Parser parser = new Parser(this.pattern);
            if (getContext() != null) {
                parser.setContext(getContext());
            }
            this.head = parser.compile(parser.parse(), getEffectiveConverterMap());
            if (this.postCompileProcessor != null) {
                this.postCompileProcessor.process(this.head);
            }
            ConverterUtil.setContextForConverters(getContext(), this.head);
            ConverterUtil.startConverters(this.head);
            super.start();
        } catch (ScanException e) {
            StatusManager statusManager = getContext().getStatusManager();
            statusManager.add((Status) new ErrorStatus("Failed to parse pattern \"" + getPattern() + "\".", this, e));
        }
    }

    public String toString() {
        return getClass().getName() + "(\"" + getPattern() + "\")";
    }

    /* access modifiers changed from: protected */
    public String writeLoopOnConverters(E e) {
        StringBuilder sb = new StringBuilder(128);
        for (Converter<E> converter = this.head; converter != null; converter = converter.getNext()) {
            converter.write(sb, e);
        }
        return sb.toString();
    }
}
