package p005ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.List;

/* renamed from: ch.qos.logback.core.util.DatePatternToRegexUtil */
public class DatePatternToRegexUtil {
    final String datePattern;
    final int datePatternLength;
    final CharSequenceToRegexMapper regexMapper = new CharSequenceToRegexMapper();

    public DatePatternToRegexUtil(String str) {
        this.datePattern = str;
        this.datePatternLength = str.length();
    }

    private List<CharSequenceState> tokenize() {
        ArrayList arrayList = new ArrayList();
        CharSequenceState charSequenceState = null;
        for (int i = 0; i < this.datePatternLength; i++) {
            char charAt = this.datePattern.charAt(i);
            if (charSequenceState == null || charSequenceState.f61c != charAt) {
                charSequenceState = new CharSequenceState(charAt);
                arrayList.add(charSequenceState);
            } else {
                charSequenceState.incrementOccurrences();
            }
        }
        return arrayList;
    }

    public String toRegex() {
        List<CharSequenceState> list = tokenize();
        StringBuilder sb = new StringBuilder();
        for (CharSequenceState regex : list) {
            sb.append(this.regexMapper.toRegex(regex));
        }
        return sb.toString();
    }
}
