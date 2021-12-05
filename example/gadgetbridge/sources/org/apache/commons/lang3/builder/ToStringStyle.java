package org.apache.commons.lang3.builder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import p005ch.qos.logback.core.CoreConstants;

public abstract class ToStringStyle implements Serializable {
    public static final ToStringStyle DEFAULT_STYLE = new DefaultToStringStyle();
    public static final ToStringStyle JSON_STYLE = new JsonToStringStyle();
    public static final ToStringStyle MULTI_LINE_STYLE = new MultiLineToStringStyle();
    public static final ToStringStyle NO_CLASS_NAME_STYLE = new NoClassNameToStringStyle();
    public static final ToStringStyle NO_FIELD_NAMES_STYLE = new NoFieldNameToStringStyle();
    private static final ThreadLocal<WeakHashMap<Object, Object>> REGISTRY = new ThreadLocal<>();
    public static final ToStringStyle SHORT_PREFIX_STYLE = new ShortPrefixToStringStyle();
    public static final ToStringStyle SIMPLE_STYLE = new SimpleToStringStyle();
    private static final long serialVersionUID = -2587890625525655916L;
    private boolean arrayContentDetail = true;
    private String arrayEnd = "}";
    private String arraySeparator = ",";
    private String arrayStart = "{";
    private String contentEnd = "]";
    private String contentStart = "[";
    private boolean defaultFullDetail = true;
    private String fieldNameValueSeparator = "=";
    private String fieldSeparator = ",";
    private boolean fieldSeparatorAtEnd = false;
    private boolean fieldSeparatorAtStart = false;
    private String nullText = "<null>";
    private String sizeEndText = ">";
    private String sizeStartText = "<size=";
    private String summaryObjectEndText = ">";
    private String summaryObjectStartText = "<";
    private boolean useClassName = true;
    private boolean useFieldNames = true;
    private boolean useIdentityHashCode = true;
    private boolean useShortClassName = false;

    static Map<Object, Object> getRegistry() {
        return REGISTRY.get();
    }

    static boolean isRegistered(Object value) {
        Map<Object, Object> m = getRegistry();
        return m != null && m.containsKey(value);
    }

    static void register(Object value) {
        if (value != null) {
            if (getRegistry() == null) {
                REGISTRY.set(new WeakHashMap());
            }
            getRegistry().put(value, (Object) null);
        }
    }

    static void unregister(Object value) {
        Map<Object, Object> m;
        if (value != null && (m = getRegistry()) != null) {
            m.remove(value);
            if (m.isEmpty()) {
                REGISTRY.remove();
            }
        }
    }

    protected ToStringStyle() {
    }

    public void appendSuper(StringBuffer buffer, String superToString) {
        appendToString(buffer, superToString);
    }

    public void appendToString(StringBuffer buffer, String toString) {
        int pos1;
        int pos2;
        if (toString != null && (pos1 = toString.indexOf(this.contentStart) + this.contentStart.length()) != (pos2 = toString.lastIndexOf(this.contentEnd)) && pos1 >= 0 && pos2 >= 0) {
            if (this.fieldSeparatorAtStart) {
                removeLastFieldSeparator(buffer);
            }
            buffer.append(toString, pos1, pos2);
            appendFieldSeparator(buffer);
        }
    }

    public void appendStart(StringBuffer buffer, Object object) {
        if (object != null) {
            appendClassName(buffer, object);
            appendIdentityHashCode(buffer, object);
            appendContentStart(buffer);
            if (this.fieldSeparatorAtStart) {
                appendFieldSeparator(buffer);
            }
        }
    }

    public void appendEnd(StringBuffer buffer, Object object) {
        if (!this.fieldSeparatorAtEnd) {
            removeLastFieldSeparator(buffer);
        }
        appendContentEnd(buffer);
        unregister(object);
    }

    /* access modifiers changed from: protected */
    public void removeLastFieldSeparator(StringBuffer buffer) {
        int len = buffer.length();
        int sepLen = this.fieldSeparator.length();
        if (len > 0 && sepLen > 0 && len >= sepLen) {
            boolean match = true;
            int i = 0;
            while (true) {
                if (i >= sepLen) {
                    break;
                } else if (buffer.charAt((len - 1) - i) != this.fieldSeparator.charAt((sepLen - 1) - i)) {
                    match = false;
                    break;
                } else {
                    i++;
                }
            }
            if (match) {
                buffer.setLength(len - sepLen);
            }
        }
    }

    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (value == null) {
            appendNullText(buffer, fieldName);
        } else {
            appendInternal(buffer, fieldName, value, isFullDetail(fullDetail));
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendInternal(StringBuffer buffer, String fieldName, Object value, boolean detail) {
        if (!isRegistered(value) || (value instanceof Number) || (value instanceof Boolean) || (value instanceof Character)) {
            register(value);
            try {
                if (value instanceof Collection) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (Collection<?>) (Collection) value);
                    } else {
                        appendSummarySize(buffer, fieldName, ((Collection) value).size());
                    }
                } else if (value instanceof Map) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (Map<?, ?>) (Map) value);
                    } else {
                        appendSummarySize(buffer, fieldName, ((Map) value).size());
                    }
                } else if (value instanceof long[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (long[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (long[]) value);
                    }
                } else if (value instanceof int[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (int[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (int[]) value);
                    }
                } else if (value instanceof short[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (short[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (short[]) value);
                    }
                } else if (value instanceof byte[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (byte[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (byte[]) value);
                    }
                } else if (value instanceof char[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (char[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (char[]) value);
                    }
                } else if (value instanceof double[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (double[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (double[]) value);
                    }
                } else if (value instanceof float[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (float[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (float[]) value);
                    }
                } else if (value instanceof boolean[]) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (boolean[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (boolean[]) value);
                    }
                } else if (value.getClass().isArray()) {
                    if (detail) {
                        appendDetail(buffer, fieldName, (Object[]) value);
                    } else {
                        appendSummary(buffer, fieldName, (Object[]) value);
                    }
                } else if (detail) {
                    appendDetail(buffer, fieldName, value);
                } else {
                    appendSummary(buffer, fieldName, value);
                }
            } finally {
                unregister(value);
            }
        } else {
            appendCyclicObject(buffer, fieldName, value);
        }
    }

    /* access modifiers changed from: protected */
    public void appendCyclicObject(StringBuffer buffer, String fieldName, Object value) {
        ObjectUtils.identityToString(buffer, value);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        buffer.append(value);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, Collection<?> coll) {
        buffer.append(coll);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, Map<?, ?> map) {
        buffer.append(map);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, Object value) {
        buffer.append(this.summaryObjectStartText);
        buffer.append(getShortClassName(value.getClass()));
        buffer.append(this.summaryObjectEndText);
    }

    public void append(StringBuffer buffer, String fieldName, long value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, long value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, int value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, int value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, short value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, short value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, byte value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, byte value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, char value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, char value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, double value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, double value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, float value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, float value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, boolean value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, boolean value) {
        buffer.append(value);
    }

    public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, Object[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            Object item = array[i];
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            if (item == null) {
                appendNullText(buffer, fieldName);
            } else {
                appendInternal(buffer, fieldName, item, this.arrayContentDetail);
            }
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array) {
        buffer.append(this.arrayStart);
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(array, i);
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            if (item == null) {
                appendNullText(buffer, fieldName);
            } else {
                appendInternal(buffer, fieldName, item, this.arrayContentDetail);
            }
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, Object[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, long[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, long[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, long[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, int[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, int[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, int[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, short[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, short[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, short[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, byte[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, byte[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, byte[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, char[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, char[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, char[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, double[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, double[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, double[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, float[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, float[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, float[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    public void append(StringBuffer buffer, String fieldName, boolean[] array, Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);
        if (array == null) {
            appendNullText(buffer, fieldName);
        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);
        } else {
            appendSummary(buffer, fieldName, array);
        }
        appendFieldEnd(buffer, fieldName);
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, boolean[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /* access modifiers changed from: protected */
    public void appendSummary(StringBuffer buffer, String fieldName, boolean[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    /* access modifiers changed from: protected */
    public void appendClassName(StringBuffer buffer, Object object) {
        if (this.useClassName && object != null) {
            register(object);
            if (this.useShortClassName) {
                buffer.append(getShortClassName(object.getClass()));
            } else {
                buffer.append(object.getClass().getName());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void appendIdentityHashCode(StringBuffer buffer, Object object) {
        if (isUseIdentityHashCode() && object != null) {
            register(object);
            buffer.append('@');
            buffer.append(Integer.toHexString(System.identityHashCode(object)));
        }
    }

    /* access modifiers changed from: protected */
    public void appendContentStart(StringBuffer buffer) {
        buffer.append(this.contentStart);
    }

    /* access modifiers changed from: protected */
    public void appendContentEnd(StringBuffer buffer) {
        buffer.append(this.contentEnd);
    }

    /* access modifiers changed from: protected */
    public void appendNullText(StringBuffer buffer, String fieldName) {
        buffer.append(this.nullText);
    }

    /* access modifiers changed from: protected */
    public void appendFieldSeparator(StringBuffer buffer) {
        buffer.append(this.fieldSeparator);
    }

    /* access modifiers changed from: protected */
    public void appendFieldStart(StringBuffer buffer, String fieldName) {
        if (this.useFieldNames && fieldName != null) {
            buffer.append(fieldName);
            buffer.append(this.fieldNameValueSeparator);
        }
    }

    /* access modifiers changed from: protected */
    public void appendFieldEnd(StringBuffer buffer, String fieldName) {
        appendFieldSeparator(buffer);
    }

    /* access modifiers changed from: protected */
    public void appendSummarySize(StringBuffer buffer, String fieldName, int size) {
        buffer.append(this.sizeStartText);
        buffer.append(size);
        buffer.append(this.sizeEndText);
    }

    /* access modifiers changed from: protected */
    public boolean isFullDetail(Boolean fullDetailRequest) {
        if (fullDetailRequest == null) {
            return this.defaultFullDetail;
        }
        return fullDetailRequest.booleanValue();
    }

    /* access modifiers changed from: protected */
    public String getShortClassName(Class<?> cls) {
        return ClassUtils.getShortClassName(cls);
    }

    /* access modifiers changed from: protected */
    public boolean isUseClassName() {
        return this.useClassName;
    }

    /* access modifiers changed from: protected */
    public void setUseClassName(boolean useClassName2) {
        this.useClassName = useClassName2;
    }

    /* access modifiers changed from: protected */
    public boolean isUseShortClassName() {
        return this.useShortClassName;
    }

    /* access modifiers changed from: protected */
    public void setUseShortClassName(boolean useShortClassName2) {
        this.useShortClassName = useShortClassName2;
    }

    /* access modifiers changed from: protected */
    public boolean isUseIdentityHashCode() {
        return this.useIdentityHashCode;
    }

    /* access modifiers changed from: protected */
    public void setUseIdentityHashCode(boolean useIdentityHashCode2) {
        this.useIdentityHashCode = useIdentityHashCode2;
    }

    /* access modifiers changed from: protected */
    public boolean isUseFieldNames() {
        return this.useFieldNames;
    }

    /* access modifiers changed from: protected */
    public void setUseFieldNames(boolean useFieldNames2) {
        this.useFieldNames = useFieldNames2;
    }

    /* access modifiers changed from: protected */
    public boolean isDefaultFullDetail() {
        return this.defaultFullDetail;
    }

    /* access modifiers changed from: protected */
    public void setDefaultFullDetail(boolean defaultFullDetail2) {
        this.defaultFullDetail = defaultFullDetail2;
    }

    /* access modifiers changed from: protected */
    public boolean isArrayContentDetail() {
        return this.arrayContentDetail;
    }

    /* access modifiers changed from: protected */
    public void setArrayContentDetail(boolean arrayContentDetail2) {
        this.arrayContentDetail = arrayContentDetail2;
    }

    /* access modifiers changed from: protected */
    public String getArrayStart() {
        return this.arrayStart;
    }

    /* access modifiers changed from: protected */
    public void setArrayStart(String arrayStart2) {
        if (arrayStart2 == null) {
            arrayStart2 = "";
        }
        this.arrayStart = arrayStart2;
    }

    /* access modifiers changed from: protected */
    public String getArrayEnd() {
        return this.arrayEnd;
    }

    /* access modifiers changed from: protected */
    public void setArrayEnd(String arrayEnd2) {
        if (arrayEnd2 == null) {
            arrayEnd2 = "";
        }
        this.arrayEnd = arrayEnd2;
    }

    /* access modifiers changed from: protected */
    public String getArraySeparator() {
        return this.arraySeparator;
    }

    /* access modifiers changed from: protected */
    public void setArraySeparator(String arraySeparator2) {
        if (arraySeparator2 == null) {
            arraySeparator2 = "";
        }
        this.arraySeparator = arraySeparator2;
    }

    /* access modifiers changed from: protected */
    public String getContentStart() {
        return this.contentStart;
    }

    /* access modifiers changed from: protected */
    public void setContentStart(String contentStart2) {
        if (contentStart2 == null) {
            contentStart2 = "";
        }
        this.contentStart = contentStart2;
    }

    /* access modifiers changed from: protected */
    public String getContentEnd() {
        return this.contentEnd;
    }

    /* access modifiers changed from: protected */
    public void setContentEnd(String contentEnd2) {
        if (contentEnd2 == null) {
            contentEnd2 = "";
        }
        this.contentEnd = contentEnd2;
    }

    /* access modifiers changed from: protected */
    public String getFieldNameValueSeparator() {
        return this.fieldNameValueSeparator;
    }

    /* access modifiers changed from: protected */
    public void setFieldNameValueSeparator(String fieldNameValueSeparator2) {
        if (fieldNameValueSeparator2 == null) {
            fieldNameValueSeparator2 = "";
        }
        this.fieldNameValueSeparator = fieldNameValueSeparator2;
    }

    /* access modifiers changed from: protected */
    public String getFieldSeparator() {
        return this.fieldSeparator;
    }

    /* access modifiers changed from: protected */
    public void setFieldSeparator(String fieldSeparator2) {
        if (fieldSeparator2 == null) {
            fieldSeparator2 = "";
        }
        this.fieldSeparator = fieldSeparator2;
    }

    /* access modifiers changed from: protected */
    public boolean isFieldSeparatorAtStart() {
        return this.fieldSeparatorAtStart;
    }

    /* access modifiers changed from: protected */
    public void setFieldSeparatorAtStart(boolean fieldSeparatorAtStart2) {
        this.fieldSeparatorAtStart = fieldSeparatorAtStart2;
    }

    /* access modifiers changed from: protected */
    public boolean isFieldSeparatorAtEnd() {
        return this.fieldSeparatorAtEnd;
    }

    /* access modifiers changed from: protected */
    public void setFieldSeparatorAtEnd(boolean fieldSeparatorAtEnd2) {
        this.fieldSeparatorAtEnd = fieldSeparatorAtEnd2;
    }

    /* access modifiers changed from: protected */
    public String getNullText() {
        return this.nullText;
    }

    /* access modifiers changed from: protected */
    public void setNullText(String nullText2) {
        if (nullText2 == null) {
            nullText2 = "";
        }
        this.nullText = nullText2;
    }

    /* access modifiers changed from: protected */
    public String getSizeStartText() {
        return this.sizeStartText;
    }

    /* access modifiers changed from: protected */
    public void setSizeStartText(String sizeStartText2) {
        if (sizeStartText2 == null) {
            sizeStartText2 = "";
        }
        this.sizeStartText = sizeStartText2;
    }

    /* access modifiers changed from: protected */
    public String getSizeEndText() {
        return this.sizeEndText;
    }

    /* access modifiers changed from: protected */
    public void setSizeEndText(String sizeEndText2) {
        if (sizeEndText2 == null) {
            sizeEndText2 = "";
        }
        this.sizeEndText = sizeEndText2;
    }

    /* access modifiers changed from: protected */
    public String getSummaryObjectStartText() {
        return this.summaryObjectStartText;
    }

    /* access modifiers changed from: protected */
    public void setSummaryObjectStartText(String summaryObjectStartText2) {
        if (summaryObjectStartText2 == null) {
            summaryObjectStartText2 = "";
        }
        this.summaryObjectStartText = summaryObjectStartText2;
    }

    /* access modifiers changed from: protected */
    public String getSummaryObjectEndText() {
        return this.summaryObjectEndText;
    }

    /* access modifiers changed from: protected */
    public void setSummaryObjectEndText(String summaryObjectEndText2) {
        if (summaryObjectEndText2 == null) {
            summaryObjectEndText2 = "";
        }
        this.summaryObjectEndText = summaryObjectEndText2;
    }

    private static final class DefaultToStringStyle extends ToStringStyle {
        private static final long serialVersionUID = 1;

        DefaultToStringStyle() {
        }

        private Object readResolve() {
            return ToStringStyle.DEFAULT_STYLE;
        }
    }

    private static final class NoFieldNameToStringStyle extends ToStringStyle {
        private static final long serialVersionUID = 1;

        NoFieldNameToStringStyle() {
            setUseFieldNames(false);
        }

        private Object readResolve() {
            return ToStringStyle.NO_FIELD_NAMES_STYLE;
        }
    }

    private static final class ShortPrefixToStringStyle extends ToStringStyle {
        private static final long serialVersionUID = 1;

        ShortPrefixToStringStyle() {
            setUseShortClassName(true);
            setUseIdentityHashCode(false);
        }

        private Object readResolve() {
            return ToStringStyle.SHORT_PREFIX_STYLE;
        }
    }

    private static final class SimpleToStringStyle extends ToStringStyle {
        private static final long serialVersionUID = 1;

        SimpleToStringStyle() {
            setUseClassName(false);
            setUseIdentityHashCode(false);
            setUseFieldNames(false);
            setContentStart("");
            setContentEnd("");
        }

        private Object readResolve() {
            return ToStringStyle.SIMPLE_STYLE;
        }
    }

    private static final class MultiLineToStringStyle extends ToStringStyle {
        private static final long serialVersionUID = 1;

        MultiLineToStringStyle() {
            setContentStart("[");
            setFieldSeparator(System.lineSeparator() + "  ");
            setFieldSeparatorAtStart(true);
            setContentEnd(System.lineSeparator() + "]");
        }

        private Object readResolve() {
            return ToStringStyle.MULTI_LINE_STYLE;
        }
    }

    private static final class NoClassNameToStringStyle extends ToStringStyle {
        private static final long serialVersionUID = 1;

        NoClassNameToStringStyle() {
            setUseClassName(false);
            setUseIdentityHashCode(false);
        }

        private Object readResolve() {
            return ToStringStyle.NO_CLASS_NAME_STYLE;
        }
    }

    private static final class JsonToStringStyle extends ToStringStyle {
        private static final String FIELD_NAME_QUOTE = "\"";
        private static final long serialVersionUID = 1;

        JsonToStringStyle() {
            setUseClassName(false);
            setUseIdentityHashCode(false);
            setContentStart("{");
            setContentEnd("}");
            setArrayStart("[");
            setArrayEnd("]");
            setFieldSeparator(",");
            setFieldNameValueSeparator(":");
            setNullText("null");
            setSummaryObjectStartText("\"<");
            setSummaryObjectEndText(">\"");
            setSizeStartText("\"<size=");
            setSizeEndText(">\"");
        }

        public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, long[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, int[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, short[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, byte[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, char[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, double[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, float[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, boolean[] array, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, array, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            } else if (isFullDetail(fullDetail)) {
                ToStringStyle.super.append(buffer, fieldName, value, fullDetail);
            } else {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
        }

        /* access modifiers changed from: protected */
        public void appendDetail(StringBuffer buffer, String fieldName, char value) {
            appendValueAsString(buffer, String.valueOf(value));
        }

        /* access modifiers changed from: protected */
        public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
            if (value == null) {
                appendNullText(buffer, fieldName);
            } else if ((value instanceof String) || (value instanceof Character)) {
                appendValueAsString(buffer, value.toString());
            } else if ((value instanceof Number) || (value instanceof Boolean)) {
                buffer.append(value);
            } else {
                String valueAsString = value.toString();
                if (isJsonObject(valueAsString) || isJsonArray(valueAsString)) {
                    buffer.append(value);
                } else {
                    appendDetail(buffer, fieldName, (Object) valueAsString);
                }
            }
        }

        private boolean isJsonArray(String valueAsString) {
            return valueAsString.startsWith(getArrayStart()) && valueAsString.startsWith(getArrayEnd());
        }

        private boolean isJsonObject(String valueAsString) {
            return valueAsString.startsWith(getContentStart()) && valueAsString.endsWith(getContentEnd());
        }

        private void appendValueAsString(StringBuffer buffer, String value) {
            buffer.append(CoreConstants.DOUBLE_QUOTE_CHAR);
            buffer.append(value);
            buffer.append(CoreConstants.DOUBLE_QUOTE_CHAR);
        }

        /* access modifiers changed from: protected */
        public void appendFieldStart(StringBuffer buffer, String fieldName) {
            if (fieldName != null) {
                ToStringStyle.super.appendFieldStart(buffer, FIELD_NAME_QUOTE + fieldName + FIELD_NAME_QUOTE);
                return;
            }
            throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
        }

        private Object readResolve() {
            return ToStringStyle.JSON_STYLE;
        }
    }
}
