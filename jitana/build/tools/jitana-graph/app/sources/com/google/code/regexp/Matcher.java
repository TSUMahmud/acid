package com.google.code.regexp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Matcher implements MatchResult {
    private java.util.regex.Matcher matcher;
    private Pattern parentPattern;

    Matcher(Pattern pattern, CharSequence charSequence) {
        this.parentPattern = pattern;
        this.matcher = pattern.pattern().matcher(charSequence);
    }

    Matcher(Pattern pattern, MatchResult matchResult) {
        this.parentPattern = pattern;
        this.matcher = (java.util.regex.Matcher) matchResult;
    }

    private int groupIndex(String str) {
        int indexOf = this.parentPattern.indexOf(str);
        if (indexOf > -1) {
            return indexOf + 1;
        }
        return -1;
    }

    public Matcher appendReplacement(StringBuffer stringBuffer, String str) {
        this.matcher.appendReplacement(stringBuffer, this.parentPattern.replaceProperties(str));
        return this;
    }

    public StringBuffer appendTail(StringBuffer stringBuffer) {
        return this.matcher.appendTail(stringBuffer);
    }

    public int end() {
        return this.matcher.end();
    }

    public int end(int i) {
        return this.matcher.end(i);
    }

    public int end(String str) {
        return end(groupIndex(str));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof Matcher)) {
            return false;
        }
        Matcher matcher2 = (Matcher) obj;
        if (!this.parentPattern.equals(matcher2.parentPattern)) {
            return false;
        }
        return this.matcher.equals(matcher2.matcher);
    }

    public boolean find() {
        return this.matcher.find();
    }

    public boolean find(int i) {
        return this.matcher.find(i);
    }

    public String group() {
        return this.matcher.group();
    }

    public String group(int i) {
        return this.matcher.group(i);
    }

    public String group(String str) {
        int groupIndex = groupIndex(str);
        if (groupIndex >= 0) {
            return group(groupIndex);
        }
        throw new IndexOutOfBoundsException("No group \"" + str + "\"");
    }

    public int groupCount() {
        return this.matcher.groupCount();
    }

    public boolean hasAnchoringBounds() {
        return this.matcher.hasAnchoringBounds();
    }

    public boolean hasTransparentBounds() {
        return this.matcher.hasTransparentBounds();
    }

    public int hashCode() {
        return this.parentPattern.hashCode() ^ this.matcher.hashCode();
    }

    public boolean hitEnd() {
        return this.matcher.hitEnd();
    }

    public boolean lookingAt() {
        return this.matcher.lookingAt();
    }

    public boolean matches() {
        return this.matcher.matches();
    }

    public List<Map<String, String>> namedGroups() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (this.matcher.find(i)) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (String next : this.parentPattern.groupNames()) {
                linkedHashMap.put(next, this.matcher.group(groupIndex(next)));
                i = this.matcher.end();
            }
            arrayList.add(linkedHashMap);
        }
        return arrayList;
    }

    public Pattern namedPattern() {
        return this.parentPattern;
    }

    public List<String> orderedGroups() {
        int groupCount = groupCount();
        ArrayList arrayList = new ArrayList(groupCount);
        for (int i = 1; i <= groupCount; i++) {
            arrayList.add(group(i));
        }
        return arrayList;
    }

    public Matcher region(int i, int i2) {
        this.matcher.region(i, i2);
        return this;
    }

    public int regionEnd() {
        return this.matcher.regionEnd();
    }

    public int regionStart() {
        return this.matcher.regionStart();
    }

    public String replaceAll(String str) {
        return this.matcher.replaceAll(this.parentPattern.replaceProperties(str));
    }

    public String replaceFirst(String str) {
        return this.matcher.replaceFirst(this.parentPattern.replaceProperties(str));
    }

    public boolean requireEnd() {
        return this.matcher.requireEnd();
    }

    public Matcher reset() {
        this.matcher.reset();
        return this;
    }

    public Matcher reset(CharSequence charSequence) {
        this.matcher.reset(charSequence);
        return this;
    }

    public Pattern standardPattern() {
        return this.matcher.pattern();
    }

    public int start() {
        return this.matcher.start();
    }

    public int start(int i) {
        return this.matcher.start(i);
    }

    public int start(String str) {
        return start(groupIndex(str));
    }

    public MatchResult toMatchResult() {
        return new Matcher(this.parentPattern, this.matcher.toMatchResult());
    }

    public String toString() {
        return this.matcher.toString();
    }

    public Matcher useAnchoringBounds(boolean z) {
        this.matcher.useAnchoringBounds(z);
        return this;
    }

    public Matcher usePattern(Pattern pattern) {
        if (pattern != null) {
            this.parentPattern = pattern;
            this.matcher.usePattern(pattern.pattern());
            return this;
        }
        throw new IllegalArgumentException("newPattern cannot be null");
    }

    public Matcher useTransparentBounds(boolean z) {
        this.matcher.useTransparentBounds(z);
        return this;
    }
}
