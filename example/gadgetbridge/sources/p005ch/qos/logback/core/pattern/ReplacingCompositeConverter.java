package p005ch.qos.logback.core.pattern;

import java.util.List;
import java.util.regex.Pattern;

/* renamed from: ch.qos.logback.core.pattern.ReplacingCompositeConverter */
public class ReplacingCompositeConverter<E> extends CompositeConverter<E> {
    Pattern pattern;
    String regex;
    String replacement;

    public void start() {
        List<String> optionList = getOptionList();
        if (optionList == null) {
            addError("at least two options are expected whereas you have declared none");
            return;
        }
        int size = optionList.size();
        if (size < 2) {
            addError("at least two options are expected whereas you have declared only " + size + "as [" + optionList + "]");
            return;
        }
        this.regex = optionList.get(0);
        this.pattern = Pattern.compile(this.regex);
        this.replacement = optionList.get(1);
        super.start();
    }

    /* access modifiers changed from: protected */
    public String transform(E e, String str) {
        return !this.started ? str : this.pattern.matcher(str).replaceAll(this.replacement);
    }
}
