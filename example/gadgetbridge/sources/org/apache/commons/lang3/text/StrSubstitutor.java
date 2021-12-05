package org.apache.commons.lang3.text;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import p005ch.qos.logback.core.CoreConstants;

@Deprecated
public class StrSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';
    public static final StrMatcher DEFAULT_PREFIX = StrMatcher.stringMatcher("${");
    public static final StrMatcher DEFAULT_SUFFIX = StrMatcher.stringMatcher("}");
    public static final StrMatcher DEFAULT_VALUE_DELIMITER = StrMatcher.stringMatcher(CoreConstants.DEFAULT_VALUE_SEPARATOR);
    private boolean enableSubstitutionInVariables;
    private char escapeChar;
    private StrMatcher prefixMatcher;
    private boolean preserveEscapes;
    private StrMatcher suffixMatcher;
    private StrMatcher valueDelimiterMatcher;
    private StrLookup<?> variableResolver;

    public static <V> String replace(Object source, Map<String, V> valueMap) {
        return new StrSubstitutor(valueMap).replace(source);
    }

    public static <V> String replace(Object source, Map<String, V> valueMap, String prefix, String suffix) {
        return new StrSubstitutor(valueMap, prefix, suffix).replace(source);
    }

    public static String replace(Object source, Properties valueProperties) {
        if (valueProperties == null) {
            return source.toString();
        }
        Map<String, String> valueMap = new HashMap<>();
        Enumeration<?> propNames = valueProperties.propertyNames();
        while (propNames.hasMoreElements()) {
            String propName = (String) propNames.nextElement();
            valueMap.put(propName, valueProperties.getProperty(propName));
        }
        return replace(source, valueMap);
    }

    public static String replaceSystemProperties(Object source) {
        return new StrSubstitutor((StrLookup<?>) StrLookup.systemPropertiesLookup()).replace(source);
    }

    public StrSubstitutor() {
        this((StrLookup<?>) null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StrSubstitutor(Map<String, V> valueMap) {
        this((StrLookup<?>) StrLookup.mapLookup(valueMap), DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StrSubstitutor(Map<String, V> valueMap, String prefix, String suffix) {
        this((StrLookup<?>) StrLookup.mapLookup(valueMap), prefix, suffix, '$');
    }

    public <V> StrSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape) {
        this((StrLookup<?>) StrLookup.mapLookup(valueMap), prefix, suffix, escape);
    }

    public <V> StrSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape, String valueDelimiter) {
        this((StrLookup<?>) StrLookup.mapLookup(valueMap), prefix, suffix, escape, valueDelimiter);
    }

    public StrSubstitutor(StrLookup<?> variableResolver2) {
        this(variableResolver2, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StrSubstitutor(StrLookup<?> variableResolver2, String prefix, String suffix, char escape) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver2);
        setVariablePrefix(prefix);
        setVariableSuffix(suffix);
        setEscapeChar(escape);
        setValueDelimiterMatcher(DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(StrLookup<?> variableResolver2, String prefix, String suffix, char escape, String valueDelimiter) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver2);
        setVariablePrefix(prefix);
        setVariableSuffix(suffix);
        setEscapeChar(escape);
        setValueDelimiter(valueDelimiter);
    }

    public StrSubstitutor(StrLookup<?> variableResolver2, StrMatcher prefixMatcher2, StrMatcher suffixMatcher2, char escape) {
        this(variableResolver2, prefixMatcher2, suffixMatcher2, escape, DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(StrLookup<?> variableResolver2, StrMatcher prefixMatcher2, StrMatcher suffixMatcher2, char escape, StrMatcher valueDelimiterMatcher2) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver2);
        setVariablePrefixMatcher(prefixMatcher2);
        setVariableSuffixMatcher(suffixMatcher2);
        setEscapeChar(escape);
        setValueDelimiterMatcher(valueDelimiterMatcher2);
    }

    public String replace(String source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source);
        if (!substitute(buf, 0, source.length())) {
            return source;
        }
        return buf.toString();
    }

    public String replace(String source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (!substitute(buf, 0, length)) {
            return source.substring(offset, offset + length);
        }
        return buf.toString();
    }

    public String replace(char[] source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length).append(source);
        substitute(buf, 0, source.length);
        return buf.toString();
    }

    public String replace(char[] source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(StringBuffer source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StringBuffer source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(CharSequence source) {
        if (source == null) {
            return null;
        }
        return replace(source, 0, source.length());
    }

    public String replace(CharSequence source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(StrBuilder source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StrBuilder source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(Object source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder().append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public boolean replaceIn(StringBuffer source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(StringBuffer source, int offset, int length) {
        if (source == null) {
            return false;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (!substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    public boolean replaceIn(StringBuilder source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(StringBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (!substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    public boolean replaceIn(StrBuilder source) {
        if (source == null) {
            return false;
        }
        return substitute(source, 0, source.length());
    }

    public boolean replaceIn(StrBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        return substitute(source, offset, length);
    }

    /* access modifiers changed from: protected */
    public boolean substitute(StrBuilder buf, int offset, int length) {
        return substitute(buf, offset, length, (List<String>) null) > 0;
    }

    private int substitute(StrBuilder buf, int offset, int length, List<String> priorVariables) {
        boolean top;
        char escape;
        StrMatcher suffMatcher;
        int lengthChange;
        String varName;
        List<String> priorVariables2;
        String varName2;
        StrBuilder strBuilder = buf;
        int i = offset;
        int i2 = length;
        StrMatcher pfxMatcher = getVariablePrefixMatcher();
        StrMatcher suffMatcher2 = getVariableSuffixMatcher();
        char escape2 = getEscapeChar();
        StrMatcher valueDelimMatcher = getValueDelimiterMatcher();
        boolean substitutionInVariablesEnabled = isEnableSubstitutionInVariables();
        boolean top2 = priorVariables == null;
        int lengthChange2 = 0;
        char[] chars = strBuilder.buffer;
        int bufEnd = i + i2;
        boolean altered = false;
        int pos = offset;
        List<String> priorVariables3 = priorVariables;
        while (pos < bufEnd) {
            int startMatchLen = pfxMatcher.isMatch(chars, pos, i, bufEnd);
            if (startMatchLen == 0) {
                pos++;
                suffMatcher = suffMatcher2;
                escape = escape2;
                top = top2;
            } else if (pos <= i || chars[pos - 1] != escape2) {
                int startPos = pos;
                pos += startMatchLen;
                int nestedVarCount = 0;
                while (true) {
                    if (pos >= bufEnd) {
                        suffMatcher = suffMatcher2;
                        escape = escape2;
                        top = top2;
                        int i3 = lengthChange2;
                        break;
                    }
                    if (substitutionInVariablesEnabled) {
                        int isMatch = pfxMatcher.isMatch(chars, pos, i, bufEnd);
                        int endMatchLen = isMatch;
                        if (isMatch != 0) {
                            nestedVarCount++;
                            pos += endMatchLen;
                        }
                    }
                    int endMatchLen2 = suffMatcher2.isMatch(chars, pos, i, bufEnd);
                    if (endMatchLen2 == 0) {
                        pos++;
                    } else if (nestedVarCount == 0) {
                        suffMatcher = suffMatcher2;
                        escape = escape2;
                        String varNameExpr = new String(chars, startPos + startMatchLen, (pos - startPos) - startMatchLen);
                        if (substitutionInVariablesEnabled) {
                            StrBuilder bufName = new StrBuilder(varNameExpr);
                            String str = varNameExpr;
                            substitute(bufName, 0, bufName.length());
                            varNameExpr = bufName.toString();
                        } else {
                            String str2 = varNameExpr;
                        }
                        int pos2 = pos + endMatchLen2;
                        int endPos = pos2;
                        String varName3 = varNameExpr;
                        String varDefaultValue = null;
                        if (valueDelimMatcher != null) {
                            varName2 = varName3;
                            char[] varNameExprChars = varNameExpr.toCharArray();
                            top = top2;
                            int i4 = 0;
                            while (true) {
                                lengthChange = lengthChange2;
                                if (i4 >= varNameExprChars.length || (!substitutionInVariablesEnabled && pfxMatcher.isMatch(varNameExprChars, i4, i4, varNameExprChars.length) != 0)) {
                                    break;
                                }
                                int isMatch2 = valueDelimMatcher.isMatch(varNameExprChars, i4);
                                int valueDelimiterMatchLen = isMatch2;
                                if (isMatch2 != 0) {
                                    String varName4 = varNameExpr.substring(0, i4);
                                    varDefaultValue = varNameExpr.substring(i4 + valueDelimiterMatchLen);
                                    varName = varName4;
                                    break;
                                }
                                i4++;
                                lengthChange2 = lengthChange;
                            }
                        } else {
                            varName2 = varName3;
                            top = top2;
                            lengthChange = lengthChange2;
                        }
                        varName = varName2;
                        if (priorVariables3 == null) {
                            priorVariables2 = new ArrayList<>();
                            priorVariables2.add(new String(chars, i, i2));
                        } else {
                            priorVariables2 = priorVariables3;
                        }
                        checkCyclicSubstitution(varName, priorVariables2);
                        priorVariables2.add(varName);
                        String varValue = resolveVariable(varName, strBuilder, startPos, endPos);
                        if (varValue == null) {
                            varValue = varDefaultValue;
                        }
                        if (varValue != null) {
                            int varLen = varValue.length();
                            strBuilder.replace(startPos, endPos, varValue);
                            altered = true;
                            int change = (substitute(strBuilder, startPos, varLen, priorVariables2) + varLen) - (endPos - startPos);
                            pos2 += change;
                            bufEnd += change;
                            chars = strBuilder.buffer;
                            lengthChange += change;
                        }
                        priorVariables2.remove(priorVariables2.size() - 1);
                        priorVariables3 = priorVariables2;
                        lengthChange2 = lengthChange;
                    } else {
                        char c = escape2;
                        boolean z = top2;
                        int i5 = lengthChange2;
                        nestedVarCount--;
                        pos += endMatchLen2;
                        i = offset;
                    }
                }
            } else if (this.preserveEscapes) {
                pos++;
            } else {
                strBuilder.deleteCharAt(pos - 1);
                lengthChange2--;
                bufEnd--;
                suffMatcher = suffMatcher2;
                escape = escape2;
                top = top2;
                altered = true;
                chars = strBuilder.buffer;
            }
            i = offset;
            suffMatcher2 = suffMatcher;
            escape2 = escape;
            top2 = top;
        }
        char c2 = escape2;
        int lengthChange3 = lengthChange2;
        if (top2) {
            return altered;
        }
        return lengthChange3;
    }

    private void checkCyclicSubstitution(String varName, List<String> priorVariables) {
        if (priorVariables.contains(varName)) {
            StrBuilder buf = new StrBuilder(256);
            buf.append("Infinite loop in property interpolation of ");
            buf.append(priorVariables.remove(0));
            buf.append(": ");
            buf.appendWithSeparators((Iterable<?>) priorVariables, "->");
            throw new IllegalStateException(buf.toString());
        }
    }

    /* access modifiers changed from: protected */
    public String resolveVariable(String variableName, StrBuilder buf, int startPos, int endPos) {
        StrLookup<?> resolver = getVariableResolver();
        if (resolver == null) {
            return null;
        }
        return resolver.lookup(variableName);
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public void setEscapeChar(char escapeCharacter) {
        this.escapeChar = escapeCharacter;
    }

    public StrMatcher getVariablePrefixMatcher() {
        return this.prefixMatcher;
    }

    public StrSubstitutor setVariablePrefixMatcher(StrMatcher prefixMatcher2) {
        if (prefixMatcher2 != null) {
            this.prefixMatcher = prefixMatcher2;
            return this;
        }
        throw new IllegalArgumentException("Variable prefix matcher must not be null!");
    }

    public StrSubstitutor setVariablePrefix(char prefix) {
        return setVariablePrefixMatcher(StrMatcher.charMatcher(prefix));
    }

    public StrSubstitutor setVariablePrefix(String prefix) {
        if (prefix != null) {
            return setVariablePrefixMatcher(StrMatcher.stringMatcher(prefix));
        }
        throw new IllegalArgumentException("Variable prefix must not be null!");
    }

    public StrMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }

    public StrSubstitutor setVariableSuffixMatcher(StrMatcher suffixMatcher2) {
        if (suffixMatcher2 != null) {
            this.suffixMatcher = suffixMatcher2;
            return this;
        }
        throw new IllegalArgumentException("Variable suffix matcher must not be null!");
    }

    public StrSubstitutor setVariableSuffix(char suffix) {
        return setVariableSuffixMatcher(StrMatcher.charMatcher(suffix));
    }

    public StrSubstitutor setVariableSuffix(String suffix) {
        if (suffix != null) {
            return setVariableSuffixMatcher(StrMatcher.stringMatcher(suffix));
        }
        throw new IllegalArgumentException("Variable suffix must not be null!");
    }

    public StrMatcher getValueDelimiterMatcher() {
        return this.valueDelimiterMatcher;
    }

    public StrSubstitutor setValueDelimiterMatcher(StrMatcher valueDelimiterMatcher2) {
        this.valueDelimiterMatcher = valueDelimiterMatcher2;
        return this;
    }

    public StrSubstitutor setValueDelimiter(char valueDelimiter) {
        return setValueDelimiterMatcher(StrMatcher.charMatcher(valueDelimiter));
    }

    public StrSubstitutor setValueDelimiter(String valueDelimiter) {
        if (!StringUtils.isEmpty(valueDelimiter)) {
            return setValueDelimiterMatcher(StrMatcher.stringMatcher(valueDelimiter));
        }
        setValueDelimiterMatcher((StrMatcher) null);
        return this;
    }

    public StrLookup<?> getVariableResolver() {
        return this.variableResolver;
    }

    public void setVariableResolver(StrLookup<?> variableResolver2) {
        this.variableResolver = variableResolver2;
    }

    public boolean isEnableSubstitutionInVariables() {
        return this.enableSubstitutionInVariables;
    }

    public void setEnableSubstitutionInVariables(boolean enableSubstitutionInVariables2) {
        this.enableSubstitutionInVariables = enableSubstitutionInVariables2;
    }

    public boolean isPreserveEscapes() {
        return this.preserveEscapes;
    }

    public void setPreserveEscapes(boolean preserveEscapes2) {
        this.preserveEscapes = preserveEscapes2;
    }
}
