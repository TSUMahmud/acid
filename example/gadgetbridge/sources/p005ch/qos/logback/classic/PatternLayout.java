package p005ch.qos.logback.classic;

import androidx.core.app.NotificationCompat;
import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import p005ch.qos.logback.classic.pattern.CallerDataConverter;
import p005ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import p005ch.qos.logback.classic.pattern.ContextNameConverter;
import p005ch.qos.logback.classic.pattern.DateConverter;
import p005ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import p005ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import p005ch.qos.logback.classic.pattern.FileOfCallerConverter;
import p005ch.qos.logback.classic.pattern.LevelConverter;
import p005ch.qos.logback.classic.pattern.LineOfCallerConverter;
import p005ch.qos.logback.classic.pattern.LineSeparatorConverter;
import p005ch.qos.logback.classic.pattern.LocalSequenceNumberConverter;
import p005ch.qos.logback.classic.pattern.LoggerConverter;
import p005ch.qos.logback.classic.pattern.MDCConverter;
import p005ch.qos.logback.classic.pattern.MarkerConverter;
import p005ch.qos.logback.classic.pattern.MessageConverter;
import p005ch.qos.logback.classic.pattern.MethodOfCallerConverter;
import p005ch.qos.logback.classic.pattern.NopThrowableInformationConverter;
import p005ch.qos.logback.classic.pattern.PropertyConverter;
import p005ch.qos.logback.classic.pattern.RelativeTimeConverter;
import p005ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import p005ch.qos.logback.classic.pattern.ThreadConverter;
import p005ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.pattern.PatternLayoutBase;
import p005ch.qos.logback.core.pattern.parser.Parser;
import p005ch.qos.logback.core.rolling.helper.DateTokenConverter;

/* renamed from: ch.qos.logback.classic.PatternLayout */
public class PatternLayout extends PatternLayoutBase<ILoggingEvent> {
    public static final Map<String, String> defaultConverterMap = new HashMap();

    static {
        defaultConverterMap.putAll(Parser.DEFAULT_COMPOSITE_CONVERTER_MAP);
        defaultConverterMap.put(DateTokenConverter.CONVERTER_KEY, DateConverter.class.getName());
        defaultConverterMap.put("date", DateConverter.class.getName());
        defaultConverterMap.put("r", RelativeTimeConverter.class.getName());
        defaultConverterMap.put("relative", RelativeTimeConverter.class.getName());
        defaultConverterMap.put("level", LevelConverter.class.getName());
        defaultConverterMap.put("le", LevelConverter.class.getName());
        defaultConverterMap.put("p", LevelConverter.class.getName());
        defaultConverterMap.put("t", ThreadConverter.class.getName());
        defaultConverterMap.put("thread", ThreadConverter.class.getName());
        defaultConverterMap.put("lo", LoggerConverter.class.getName());
        defaultConverterMap.put("logger", LoggerConverter.class.getName());
        defaultConverterMap.put("c", LoggerConverter.class.getName());
        defaultConverterMap.put("m", MessageConverter.class.getName());
        defaultConverterMap.put(NotificationCompat.CATEGORY_MESSAGE, MessageConverter.class.getName());
        defaultConverterMap.put(C1238GB.DISPLAY_MESSAGE_MESSAGE, MessageConverter.class.getName());
        defaultConverterMap.put("C", ClassOfCallerConverter.class.getName());
        defaultConverterMap.put(Action.CLASS_ATTRIBUTE, ClassOfCallerConverter.class.getName());
        defaultConverterMap.put("M", MethodOfCallerConverter.class.getName());
        defaultConverterMap.put("method", MethodOfCallerConverter.class.getName());
        defaultConverterMap.put("L", LineOfCallerConverter.class.getName());
        defaultConverterMap.put("line", LineOfCallerConverter.class.getName());
        defaultConverterMap.put("F", FileOfCallerConverter.class.getName());
        defaultConverterMap.put(Action.FILE_ATTRIBUTE, FileOfCallerConverter.class.getName());
        defaultConverterMap.put("X", MDCConverter.class.getName());
        defaultConverterMap.put("mdc", MDCConverter.class.getName());
        defaultConverterMap.put("ex", ThrowableProxyConverter.class.getName());
        defaultConverterMap.put("exception", ThrowableProxyConverter.class.getName());
        defaultConverterMap.put("rEx", RootCauseFirstThrowableProxyConverter.class.getName());
        defaultConverterMap.put("rootException", RootCauseFirstThrowableProxyConverter.class.getName());
        defaultConverterMap.put("throwable", ThrowableProxyConverter.class.getName());
        defaultConverterMap.put("xEx", ExtendedThrowableProxyConverter.class.getName());
        defaultConverterMap.put("xException", ExtendedThrowableProxyConverter.class.getName());
        defaultConverterMap.put("xThrowable", ExtendedThrowableProxyConverter.class.getName());
        defaultConverterMap.put("nopex", NopThrowableInformationConverter.class.getName());
        defaultConverterMap.put("nopexception", NopThrowableInformationConverter.class.getName());
        defaultConverterMap.put("cn", ContextNameConverter.class.getName());
        defaultConverterMap.put("contextName", ContextNameConverter.class.getName());
        defaultConverterMap.put("caller", CallerDataConverter.class.getName());
        defaultConverterMap.put("marker", MarkerConverter.class.getName());
        defaultConverterMap.put("property", PropertyConverter.class.getName());
        defaultConverterMap.put("n", LineSeparatorConverter.class.getName());
        defaultConverterMap.put("lsn", LocalSequenceNumberConverter.class.getName());
    }

    public PatternLayout() {
        this.postCompileProcessor = new EnsureExceptionHandling();
    }

    public String doLayout(ILoggingEvent iLoggingEvent) {
        return !isStarted() ? "" : writeLoopOnConverters(iLoggingEvent);
    }

    public Map<String, String> getDefaultConverterMap() {
        return defaultConverterMap;
    }
}
