package com.google.code.regexp;

import java.util.List;
import java.util.Map;

public interface MatchResult extends java.util.regex.MatchResult {
    int end(String str);

    String group(String str);

    List<Map<String, String>> namedGroups();

    List<String> orderedGroups();

    int start(String str);
}
