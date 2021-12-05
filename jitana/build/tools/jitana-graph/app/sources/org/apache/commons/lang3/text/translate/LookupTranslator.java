package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

@Deprecated
public class LookupTranslator extends CharSequenceTranslator {
    private final int longest;
    private final HashMap<String, String> lookupMap = new HashMap<>();
    private final HashSet<Character> prefixSet = new HashSet<>();
    private final int shortest;

    public LookupTranslator(CharSequence[]... lookup) {
        int _shortest = Integer.MAX_VALUE;
        int _longest = 0;
        if (lookup != null) {
            for (CharSequence[] seq : lookup) {
                this.lookupMap.put(seq[0].toString(), seq[1].toString());
                this.prefixSet.add(Character.valueOf(seq[0].charAt(0)));
                int sz = seq[0].length();
                _shortest = sz < _shortest ? sz : _shortest;
                if (sz > _longest) {
                    _longest = sz;
                }
            }
        }
        this.shortest = _shortest;
        this.longest = _longest;
    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        if (!this.prefixSet.contains(Character.valueOf(input.charAt(index)))) {
            return 0;
        }
        int max = this.longest;
        if (this.longest + index > input.length()) {
            max = input.length() - index;
        }
        for (int i = max; i >= this.shortest; i--) {
            String result = this.lookupMap.get(input.subSequence(index, index + i).toString());
            if (result != null) {
                out.write(result);
                return i;
            }
        }
        return 0;
    }
}
