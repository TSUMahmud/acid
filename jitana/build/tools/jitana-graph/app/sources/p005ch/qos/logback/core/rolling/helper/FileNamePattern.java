package p005ch.qos.logback.core.rolling.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.pattern.Converter;
import p005ch.qos.logback.core.pattern.ConverterUtil;
import p005ch.qos.logback.core.pattern.LiteralConverter;
import p005ch.qos.logback.core.pattern.parser.Parser;
import p005ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.ScanException;

/* renamed from: ch.qos.logback.core.rolling.helper.FileNamePattern */
public class FileNamePattern extends ContextAwareBase {
    static final Map<String, String> CONVERTER_MAP = new HashMap();
    Converter<Object> headTokenConverter;
    String pattern;

    static {
        CONVERTER_MAP.put(IntegerTokenConverter.CONVERTER_KEY, IntegerTokenConverter.class.getName());
        CONVERTER_MAP.put(DateTokenConverter.CONVERTER_KEY, DateTokenConverter.class.getName());
    }

    public FileNamePattern(String str, Context context) {
        setPattern(FileFilterUtil.slashify(str));
        setContext(context);
        parse();
        ConverterUtil.startConverters(this.headTokenConverter);
    }

    public String convert(Object obj) {
        StringBuilder sb = new StringBuilder();
        for (Converter<Object> converter = this.headTokenConverter; converter != null; converter = converter.getNext()) {
            sb.append(converter.convert(obj));
        }
        return sb.toString();
    }

    public String convertInt(int i) {
        return convert(Integer.valueOf(i));
    }

    public String convertMultipleArguments(Object... objArr) {
        StringBuilder sb = new StringBuilder();
        for (Converter<Object> converter = this.headTokenConverter; converter != null; converter = converter.getNext()) {
            if (converter instanceof MonoTypedConverter) {
                MonoTypedConverter monoTypedConverter = (MonoTypedConverter) converter;
                for (Object obj : objArr) {
                    if (monoTypedConverter.isApplicable(obj)) {
                        sb.append(converter.convert(obj));
                    }
                }
            } else {
                sb.append(converter.convert(objArr));
            }
        }
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public String escapeRightParantesis(String str) {
        return this.pattern.replace(")", "\\)");
    }

    public IntegerTokenConverter getIntegerTokenConverter() {
        for (Converter<Object> converter = this.headTokenConverter; converter != null; converter = converter.getNext()) {
            if (converter instanceof IntegerTokenConverter) {
                return (IntegerTokenConverter) converter;
            }
        }
        return null;
    }

    public String getPattern() {
        return this.pattern;
    }

    public DateTokenConverter getPrimaryDateTokenConverter() {
        for (Converter<Object> converter = this.headTokenConverter; converter != null; converter = converter.getNext()) {
            if (converter instanceof DateTokenConverter) {
                DateTokenConverter dateTokenConverter = (DateTokenConverter) converter;
                if (dateTokenConverter.isPrimary()) {
                    return dateTokenConverter;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void parse() {
        try {
            Parser parser = new Parser(escapeRightParantesis(this.pattern), new AlmostAsIsEscapeUtil());
            parser.setContext(this.context);
            this.headTokenConverter = parser.compile(parser.parse(), CONVERTER_MAP);
        } catch (ScanException e) {
            addError("Failed to parse pattern \"" + this.pattern + "\".", e);
        }
    }

    public void setPattern(String str) {
        if (str != null) {
            this.pattern = str.trim();
        }
    }

    public String toRegex() {
        String regex;
        StringBuilder sb = new StringBuilder();
        for (Converter<Object> converter = this.headTokenConverter; converter != null; converter = converter.getNext()) {
            if (converter instanceof LiteralConverter) {
                regex = converter.convert(null);
            } else if (converter instanceof IntegerTokenConverter) {
                regex = "\\d{1,2}";
            } else if (converter instanceof DateTokenConverter) {
                regex = ((DateTokenConverter) converter).toRegex();
            }
            sb.append(regex);
        }
        return sb.toString();
    }

    public String toRegexForFixedDate(Date date) {
        String convert;
        StringBuilder sb = new StringBuilder();
        for (Converter<Object> converter = this.headTokenConverter; converter != null; converter = converter.getNext()) {
            if (converter instanceof LiteralConverter) {
                convert = converter.convert(null);
            } else if (converter instanceof IntegerTokenConverter) {
                convert = "(\\d{1,3})";
            } else if (converter instanceof DateTokenConverter) {
                convert = converter.convert(date);
            }
            sb.append(convert);
        }
        return sb.toString();
    }

    public String toString() {
        return this.pattern;
    }
}
