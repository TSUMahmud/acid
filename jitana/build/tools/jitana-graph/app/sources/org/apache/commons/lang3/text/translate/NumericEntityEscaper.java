package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

@Deprecated
public class NumericEntityEscaper extends CodePointTranslator {
    private final int above;
    private final int below;
    private final boolean between;

    private NumericEntityEscaper(int below2, int above2, boolean between2) {
        this.below = below2;
        this.above = above2;
        this.between = between2;
    }

    public NumericEntityEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }

    public static NumericEntityEscaper below(int codepoint) {
        return outsideOf(codepoint, Integer.MAX_VALUE);
    }

    public static NumericEntityEscaper above(int codepoint) {
        return outsideOf(0, codepoint);
    }

    public static NumericEntityEscaper between(int codepointLow, int codepointHigh) {
        return new NumericEntityEscaper(codepointLow, codepointHigh, true);
    }

    public static NumericEntityEscaper outsideOf(int codepointLow, int codepointHigh) {
        return new NumericEntityEscaper(codepointLow, codepointHigh, false);
    }

    public boolean translate(int codepoint, Writer out) throws IOException {
        if (this.between) {
            if (codepoint < this.below || codepoint > this.above) {
                return false;
            }
        } else if (codepoint >= this.below && codepoint <= this.above) {
            return false;
        }
        out.write("&#");
        out.write(Integer.toString(codepoint, 10));
        out.write(59);
        return true;
    }
}
