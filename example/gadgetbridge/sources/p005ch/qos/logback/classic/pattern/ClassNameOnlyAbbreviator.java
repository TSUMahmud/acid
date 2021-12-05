package p005ch.qos.logback.classic.pattern;

/* renamed from: ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator */
public class ClassNameOnlyAbbreviator implements Abbreviator {
    public String abbreviate(String str) {
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf != -1 ? str.substring(lastIndexOf + 1, str.length()) : str;
    }
}
