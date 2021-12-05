package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

@Deprecated
public class UnicodeUnpairedSurrogateRemover extends CodePointTranslator {
    public boolean translate(int codepoint, Writer out) throws IOException {
        if (codepoint < 55296 || codepoint > 57343) {
            return false;
        }
        return true;
    }
}
