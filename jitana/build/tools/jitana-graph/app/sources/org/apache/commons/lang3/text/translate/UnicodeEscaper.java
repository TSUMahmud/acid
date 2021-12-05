package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

@Deprecated
public class UnicodeEscaper extends CodePointTranslator {
    private final int above;
    private final int below;
    private final boolean between;

    public UnicodeEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }

    protected UnicodeEscaper(int below2, int above2, boolean between2) {
        this.below = below2;
        this.above = above2;
        this.between = between2;
    }

    public static UnicodeEscaper below(int codepoint) {
        return outsideOf(codepoint, Integer.MAX_VALUE);
    }

    public static UnicodeEscaper above(int codepoint) {
        return outsideOf(0, codepoint);
    }

    public static UnicodeEscaper outsideOf(int codepointLow, int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, false);
    }

    public static UnicodeEscaper between(int codepointLow, int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, true);
    }

    public boolean translate(int codepoint, Writer out) throws IOException {
        if (this.between) {
            if (codepoint < this.below || codepoint > this.above) {
                return false;
            }
        } else if (codepoint >= this.below && codepoint <= this.above) {
            return false;
        }
        if (codepoint > 65535) {
            out.write(toUtf16Escape(codepoint));
            return true;
        }
        out.write("\\u");
        out.write(HEX_DIGITS[(codepoint >> 12) & 15]);
        out.write(HEX_DIGITS[(codepoint >> 8) & 15]);
        out.write(HEX_DIGITS[(codepoint >> 4) & 15]);
        out.write(HEX_DIGITS[codepoint & 15]);
        return true;
    }

    /* access modifiers changed from: protected */
    public String toUtf16Escape(int codepoint) {
        return "\\u" + hex(codepoint);
    }
}
