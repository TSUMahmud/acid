package org.apache.commons.lang3.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

@Deprecated
public class CompositeFormat extends Format {
    private static final long serialVersionUID = -4329119827877627683L;
    private final Format formatter;
    private final Format parser;

    public CompositeFormat(Format parser2, Format formatter2) {
        this.parser = parser2;
        this.formatter = formatter2;
    }

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return this.formatter.format(obj, toAppendTo, pos);
    }

    public Object parseObject(String source, ParsePosition pos) {
        return this.parser.parseObject(source, pos);
    }

    public Format getParser() {
        return this.parser;
    }

    public Format getFormatter() {
        return this.formatter;
    }

    public String reformat(String input) throws ParseException {
        return format(parseObject(input));
    }
}
