package p005ch.qos.logback.core.rolling.helper;

import java.util.Date;
import java.util.List;
import p005ch.qos.logback.core.pattern.DynamicConverter;
import p005ch.qos.logback.core.util.CachingDateFormatter;
import p005ch.qos.logback.core.util.DatePatternToRegexUtil;

/* renamed from: ch.qos.logback.core.rolling.helper.DateTokenConverter */
public class DateTokenConverter<E> extends DynamicConverter<E> implements MonoTypedConverter {
    public static final String AUXILIARY_TOKEN = "AUX";
    public static final String CONVERTER_KEY = "d";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private CachingDateFormatter cdf;
    private String datePattern;
    private boolean primary = true;

    public String convert(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        } else if (obj instanceof Date) {
            return convert((Date) obj);
        } else {
            throw new IllegalArgumentException("Cannot convert " + obj + " of type" + obj.getClass().getName());
        }
    }

    public String convert(Date date) {
        return this.cdf.format(date.getTime());
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    public boolean isApplicable(Object obj) {
        return obj instanceof Date;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public void start() {
        this.datePattern = getFirstOption();
        if (this.datePattern == null) {
            this.datePattern = "yyyy-MM-dd";
        }
        List<String> optionList = getOptionList();
        if (optionList != null && optionList.size() > 1 && AUXILIARY_TOKEN.equalsIgnoreCase(optionList.get(1))) {
            this.primary = false;
        }
        this.cdf = new CachingDateFormatter(this.datePattern);
    }

    public String toRegex() {
        return new DatePatternToRegexUtil(this.datePattern).toRegex();
    }
}
