package org.apache.commons.lang3.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.Builder;

@Deprecated
public class StrBuilder implements CharSequence, Appendable, Serializable, Builder<String> {
    static final int CAPACITY = 32;
    private static final long serialVersionUID = 7628716375283629643L;
    protected char[] buffer;
    private String newLine;
    private String nullText;
    protected int size;

    public StrBuilder() {
        this(32);
    }

    public StrBuilder(int initialCapacity) {
        this.buffer = new char[(initialCapacity <= 0 ? 32 : initialCapacity)];
    }

    public StrBuilder(String str) {
        if (str == null) {
            this.buffer = new char[32];
            return;
        }
        this.buffer = new char[(str.length() + 32)];
        append(str);
    }

    public String getNewLineText() {
        return this.newLine;
    }

    public StrBuilder setNewLineText(String newLine2) {
        this.newLine = newLine2;
        return this;
    }

    public String getNullText() {
        return this.nullText;
    }

    public StrBuilder setNullText(String nullText2) {
        if (nullText2 != null && nullText2.isEmpty()) {
            nullText2 = null;
        }
        this.nullText = nullText2;
        return this;
    }

    public int length() {
        return this.size;
    }

    public StrBuilder setLength(int length) {
        if (length >= 0) {
            int i = this.size;
            if (length < i) {
                this.size = length;
            } else if (length > i) {
                ensureCapacity(length);
                int oldEnd = this.size;
                int newEnd = length;
                this.size = length;
                for (int i2 = oldEnd; i2 < newEnd; i2++) {
                    this.buffer[i2] = 0;
                }
            }
            return this;
        }
        throw new StringIndexOutOfBoundsException(length);
    }

    public int capacity() {
        return this.buffer.length;
    }

    public StrBuilder ensureCapacity(int capacity) {
        if (capacity > this.buffer.length) {
            char[] old = this.buffer;
            this.buffer = new char[(capacity * 2)];
            System.arraycopy(old, 0, this.buffer, 0, this.size);
        }
        return this;
    }

    public StrBuilder minimizeCapacity() {
        if (this.buffer.length > length()) {
            char[] old = this.buffer;
            this.buffer = new char[length()];
            System.arraycopy(old, 0, this.buffer, 0, this.size);
        }
        return this;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public StrBuilder clear() {
        this.size = 0;
        return this;
    }

    public char charAt(int index) {
        if (index >= 0 && index < length()) {
            return this.buffer[index];
        }
        throw new StringIndexOutOfBoundsException(index);
    }

    public StrBuilder setCharAt(int index, char ch) {
        if (index < 0 || index >= length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        this.buffer[index] = ch;
        return this;
    }

    public StrBuilder deleteCharAt(int index) {
        if (index < 0 || index >= this.size) {
            throw new StringIndexOutOfBoundsException(index);
        }
        deleteImpl(index, index + 1, 1);
        return this;
    }

    public char[] toCharArray() {
        int i = this.size;
        if (i == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        char[] chars = new char[i];
        System.arraycopy(this.buffer, 0, chars, 0, i);
        return chars;
    }

    public char[] toCharArray(int startIndex, int endIndex) {
        int len = validateRange(startIndex, endIndex) - startIndex;
        if (len == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        char[] chars = new char[len];
        System.arraycopy(this.buffer, startIndex, chars, 0, len);
        return chars;
    }

    public char[] getChars(char[] destination) {
        int len = length();
        if (destination == null || destination.length < len) {
            destination = new char[len];
        }
        System.arraycopy(this.buffer, 0, destination, 0, len);
        return destination;
    }

    public void getChars(int startIndex, int endIndex, char[] destination, int destinationIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        } else if (endIndex < 0 || endIndex > length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        } else if (startIndex <= endIndex) {
            System.arraycopy(this.buffer, startIndex, destination, destinationIndex, endIndex - startIndex);
        } else {
            throw new StringIndexOutOfBoundsException("end < start");
        }
    }

    public int readFrom(Readable readable) throws IOException {
        int oldSize = this.size;
        if (readable instanceof Reader) {
            Reader r = (Reader) readable;
            ensureCapacity(this.size + 1);
            while (true) {
                char[] cArr = this.buffer;
                int i = this.size;
                int read = r.read(cArr, i, cArr.length - i);
                int read2 = read;
                if (read == -1) {
                    break;
                }
                this.size += read2;
                ensureCapacity(this.size + 1);
            }
        } else if (readable instanceof CharBuffer) {
            CharBuffer cb = (CharBuffer) readable;
            int remaining = cb.remaining();
            ensureCapacity(this.size + remaining);
            cb.get(this.buffer, this.size, remaining);
            this.size += remaining;
        } else {
            while (true) {
                ensureCapacity(this.size + 1);
                char[] cArr2 = this.buffer;
                int i2 = this.size;
                int read3 = readable.read(CharBuffer.wrap(cArr2, i2, cArr2.length - i2));
                if (read3 == -1) {
                    break;
                }
                this.size += read3;
            }
        }
        return this.size - oldSize;
    }

    public StrBuilder appendNewLine() {
        String str = this.newLine;
        if (str != null) {
            return append(str);
        }
        append(System.lineSeparator());
        return this;
    }

    public StrBuilder appendNull() {
        String str = this.nullText;
        if (str == null) {
            return this;
        }
        return append(str);
    }

    public StrBuilder append(Object obj) {
        if (obj == null) {
            return appendNull();
        }
        if (obj instanceof CharSequence) {
            return append((CharSequence) obj);
        }
        return append(obj.toString());
    }

    public StrBuilder append(CharSequence seq) {
        if (seq == null) {
            return appendNull();
        }
        if (seq instanceof StrBuilder) {
            return append((StrBuilder) seq);
        }
        if (seq instanceof StringBuilder) {
            return append((StringBuilder) seq);
        }
        if (seq instanceof StringBuffer) {
            return append((StringBuffer) seq);
        }
        if (seq instanceof CharBuffer) {
            return append((CharBuffer) seq);
        }
        return append(seq.toString());
    }

    public StrBuilder append(CharSequence seq, int startIndex, int length) {
        if (seq == null) {
            return appendNull();
        }
        return append(seq.toString(), startIndex, length);
    }

    public StrBuilder append(String str) {
        if (str == null) {
            return appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = length();
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, this.buffer, len);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(String str, int startIndex, int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        } else if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        } else {
            if (length > 0) {
                int len = length();
                ensureCapacity(len + length);
                str.getChars(startIndex, startIndex + length, this.buffer, len);
                this.size += length;
            }
            return this;
        }
    }

    public StrBuilder append(String format, Object... objs) {
        return append(String.format(format, objs));
    }

    public StrBuilder append(CharBuffer buf) {
        if (buf == null) {
            return appendNull();
        }
        if (buf.hasArray()) {
            int length = buf.remaining();
            int len = length();
            ensureCapacity(len + length);
            System.arraycopy(buf.array(), buf.arrayOffset() + buf.position(), this.buffer, len, length);
            this.size += length;
        } else {
            append(buf.toString());
        }
        return this;
    }

    public StrBuilder append(CharBuffer buf, int startIndex, int length) {
        if (buf == null) {
            return appendNull();
        }
        if (buf.hasArray()) {
            int totalLength = buf.remaining();
            if (startIndex < 0 || startIndex > totalLength) {
                throw new StringIndexOutOfBoundsException("startIndex must be valid");
            } else if (length < 0 || startIndex + length > totalLength) {
                throw new StringIndexOutOfBoundsException("length must be valid");
            } else {
                int len = length();
                ensureCapacity(len + length);
                System.arraycopy(buf.array(), buf.arrayOffset() + buf.position() + startIndex, this.buffer, len, length);
                this.size += length;
            }
        } else {
            append(buf.toString(), startIndex, length);
        }
        return this;
    }

    public StrBuilder append(StringBuffer str) {
        if (str == null) {
            return appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = length();
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, this.buffer, len);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(StringBuffer str, int startIndex, int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        } else if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        } else {
            if (length > 0) {
                int len = length();
                ensureCapacity(len + length);
                str.getChars(startIndex, startIndex + length, this.buffer, len);
                this.size += length;
            }
            return this;
        }
    }

    public StrBuilder append(StringBuilder str) {
        if (str == null) {
            return appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = length();
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, this.buffer, len);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(StringBuilder str, int startIndex, int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        } else if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        } else {
            if (length > 0) {
                int len = length();
                ensureCapacity(len + length);
                str.getChars(startIndex, startIndex + length, this.buffer, len);
                this.size += length;
            }
            return this;
        }
    }

    public StrBuilder append(StrBuilder str) {
        if (str == null) {
            return appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = length();
            ensureCapacity(len + strLen);
            System.arraycopy(str.buffer, 0, this.buffer, len, strLen);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(StrBuilder str, int startIndex, int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        } else if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        } else {
            if (length > 0) {
                int len = length();
                ensureCapacity(len + length);
                str.getChars(startIndex, startIndex + length, this.buffer, len);
                this.size += length;
            }
            return this;
        }
    }

    public StrBuilder append(char[] chars) {
        if (chars == null) {
            return appendNull();
        }
        int strLen = chars.length;
        if (strLen > 0) {
            int len = length();
            ensureCapacity(len + strLen);
            System.arraycopy(chars, 0, this.buffer, len, strLen);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(char[] chars, int startIndex, int length) {
        if (chars == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid startIndex: " + length);
        } else if (length < 0 || startIndex + length > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid length: " + length);
        } else {
            if (length > 0) {
                int len = length();
                ensureCapacity(len + length);
                System.arraycopy(chars, startIndex, this.buffer, len, length);
                this.size += length;
            }
            return this;
        }
    }

    public StrBuilder append(boolean value) {
        if (value) {
            ensureCapacity(this.size + 4);
            char[] cArr = this.buffer;
            int i = this.size;
            this.size = i + 1;
            cArr[i] = 't';
            int i2 = this.size;
            this.size = i2 + 1;
            cArr[i2] = 'r';
            int i3 = this.size;
            this.size = i3 + 1;
            cArr[i3] = 'u';
            int i4 = this.size;
            this.size = i4 + 1;
            cArr[i4] = 'e';
        } else {
            ensureCapacity(this.size + 5);
            char[] cArr2 = this.buffer;
            int i5 = this.size;
            this.size = i5 + 1;
            cArr2[i5] = 'f';
            int i6 = this.size;
            this.size = i6 + 1;
            cArr2[i6] = 'a';
            int i7 = this.size;
            this.size = i7 + 1;
            cArr2[i7] = 'l';
            int i8 = this.size;
            this.size = i8 + 1;
            cArr2[i8] = 's';
            int i9 = this.size;
            this.size = i9 + 1;
            cArr2[i9] = 'e';
        }
        return this;
    }

    public StrBuilder append(char ch) {
        ensureCapacity(length() + 1);
        char[] cArr = this.buffer;
        int i = this.size;
        this.size = i + 1;
        cArr[i] = ch;
        return this;
    }

    public StrBuilder append(int value) {
        return append(String.valueOf(value));
    }

    public StrBuilder append(long value) {
        return append(String.valueOf(value));
    }

    public StrBuilder append(float value) {
        return append(String.valueOf(value));
    }

    public StrBuilder append(double value) {
        return append(String.valueOf(value));
    }

    public StrBuilder appendln(Object obj) {
        return append(obj).appendNewLine();
    }

    public StrBuilder appendln(String str) {
        return append(str).appendNewLine();
    }

    public StrBuilder appendln(String str, int startIndex, int length) {
        return append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(String format, Object... objs) {
        return append(format, objs).appendNewLine();
    }

    public StrBuilder appendln(StringBuffer str) {
        return append(str).appendNewLine();
    }

    public StrBuilder appendln(StringBuilder str) {
        return append(str).appendNewLine();
    }

    public StrBuilder appendln(StringBuilder str, int startIndex, int length) {
        return append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(StringBuffer str, int startIndex, int length) {
        return append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(StrBuilder str) {
        return append(str).appendNewLine();
    }

    public StrBuilder appendln(StrBuilder str, int startIndex, int length) {
        return append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(char[] chars) {
        return append(chars).appendNewLine();
    }

    public StrBuilder appendln(char[] chars, int startIndex, int length) {
        return append(chars, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(boolean value) {
        return append(value).appendNewLine();
    }

    public StrBuilder appendln(char ch) {
        return append(ch).appendNewLine();
    }

    public StrBuilder appendln(int value) {
        return append(value).appendNewLine();
    }

    public StrBuilder appendln(long value) {
        return append(value).appendNewLine();
    }

    public StrBuilder appendln(float value) {
        return append(value).appendNewLine();
    }

    public StrBuilder appendln(double value) {
        return append(value).appendNewLine();
    }

    public <T> StrBuilder appendAll(T... array) {
        if (array != null && array.length > 0) {
            for (Object element : array) {
                append(element);
            }
        }
        return this;
    }

    public StrBuilder appendAll(Iterable<?> iterable) {
        if (iterable != null) {
            for (Object o : iterable) {
                append(o);
            }
        }
        return this;
    }

    public StrBuilder appendAll(Iterator<?> it) {
        if (it != null) {
            while (it.hasNext()) {
                append((Object) it.next());
            }
        }
        return this;
    }

    public StrBuilder appendWithSeparators(Object[] array, String separator) {
        if (array != null && array.length > 0) {
            String sep = Objects.toString(separator, "");
            append(array[0]);
            for (int i = 1; i < array.length; i++) {
                append(sep);
                append(array[i]);
            }
        }
        return this;
    }

    public StrBuilder appendWithSeparators(Iterable<?> iterable, String separator) {
        if (iterable != null) {
            String sep = Objects.toString(separator, "");
            Iterator<?> it = iterable.iterator();
            while (it.hasNext()) {
                append((Object) it.next());
                if (it.hasNext()) {
                    append(sep);
                }
            }
        }
        return this;
    }

    public StrBuilder appendWithSeparators(Iterator<?> it, String separator) {
        if (it != null) {
            String sep = Objects.toString(separator, "");
            while (it.hasNext()) {
                append((Object) it.next());
                if (it.hasNext()) {
                    append(sep);
                }
            }
        }
        return this;
    }

    public StrBuilder appendSeparator(String separator) {
        return appendSeparator(separator, (String) null);
    }

    public StrBuilder appendSeparator(String standard, String defaultIfEmpty) {
        String str = isEmpty() ? defaultIfEmpty : standard;
        if (str != null) {
            append(str);
        }
        return this;
    }

    public StrBuilder appendSeparator(char separator) {
        if (size() > 0) {
            append(separator);
        }
        return this;
    }

    public StrBuilder appendSeparator(char standard, char defaultIfEmpty) {
        if (size() > 0) {
            append(standard);
        } else {
            append(defaultIfEmpty);
        }
        return this;
    }

    public StrBuilder appendSeparator(String separator, int loopIndex) {
        if (separator != null && loopIndex > 0) {
            append(separator);
        }
        return this;
    }

    public StrBuilder appendSeparator(char separator, int loopIndex) {
        if (loopIndex > 0) {
            append(separator);
        }
        return this;
    }

    public StrBuilder appendPadding(int length, char padChar) {
        if (length >= 0) {
            ensureCapacity(this.size + length);
            for (int i = 0; i < length; i++) {
                char[] cArr = this.buffer;
                int i2 = this.size;
                this.size = i2 + 1;
                cArr[i2] = padChar;
            }
        }
        return this;
    }

    public StrBuilder appendFixedWidthPadLeft(Object obj, int width, char padChar) {
        if (width > 0) {
            ensureCapacity(this.size + width);
            String str = obj == null ? getNullText() : obj.toString();
            if (str == null) {
                str = "";
            }
            int strLen = str.length();
            if (strLen >= width) {
                str.getChars(strLen - width, strLen, this.buffer, this.size);
            } else {
                int padLen = width - strLen;
                for (int i = 0; i < padLen; i++) {
                    this.buffer[this.size + i] = padChar;
                }
                str.getChars(0, strLen, this.buffer, this.size + padLen);
            }
            this.size += width;
        }
        return this;
    }

    public StrBuilder appendFixedWidthPadLeft(int value, int width, char padChar) {
        return appendFixedWidthPadLeft((Object) String.valueOf(value), width, padChar);
    }

    public StrBuilder appendFixedWidthPadRight(Object obj, int width, char padChar) {
        if (width > 0) {
            ensureCapacity(this.size + width);
            String str = obj == null ? getNullText() : obj.toString();
            if (str == null) {
                str = "";
            }
            int strLen = str.length();
            if (strLen >= width) {
                str.getChars(0, width, this.buffer, this.size);
            } else {
                int padLen = width - strLen;
                str.getChars(0, strLen, this.buffer, this.size);
                for (int i = 0; i < padLen; i++) {
                    this.buffer[this.size + strLen + i] = padChar;
                }
            }
            this.size += width;
        }
        return this;
    }

    public StrBuilder appendFixedWidthPadRight(int value, int width, char padChar) {
        return appendFixedWidthPadRight((Object) String.valueOf(value), width, padChar);
    }

    public StrBuilder insert(int index, Object obj) {
        if (obj == null) {
            return insert(index, this.nullText);
        }
        return insert(index, obj.toString());
    }

    public StrBuilder insert(int index, String str) {
        int strLen;
        validateIndex(index);
        if (str == null) {
            str = this.nullText;
        }
        if (str != null && (strLen = str.length()) > 0) {
            int newSize = this.size + strLen;
            ensureCapacity(newSize);
            char[] cArr = this.buffer;
            System.arraycopy(cArr, index, cArr, index + strLen, this.size - index);
            this.size = newSize;
            str.getChars(0, strLen, this.buffer, index);
        }
        return this;
    }

    public StrBuilder insert(int index, char[] chars) {
        validateIndex(index);
        if (chars == null) {
            return insert(index, this.nullText);
        }
        int len = chars.length;
        if (len > 0) {
            ensureCapacity(this.size + len);
            char[] cArr = this.buffer;
            System.arraycopy(cArr, index, cArr, index + len, this.size - index);
            System.arraycopy(chars, 0, this.buffer, index, len);
            this.size += len;
        }
        return this;
    }

    public StrBuilder insert(int index, char[] chars, int offset, int length) {
        validateIndex(index);
        if (chars == null) {
            return insert(index, this.nullText);
        }
        if (offset < 0 || offset > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid offset: " + offset);
        } else if (length < 0 || offset + length > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid length: " + length);
        } else {
            if (length > 0) {
                ensureCapacity(this.size + length);
                char[] cArr = this.buffer;
                System.arraycopy(cArr, index, cArr, index + length, this.size - index);
                System.arraycopy(chars, offset, this.buffer, index, length);
                this.size += length;
            }
            return this;
        }
    }

    public StrBuilder insert(int index, boolean value) {
        validateIndex(index);
        if (value) {
            ensureCapacity(this.size + 4);
            char[] cArr = this.buffer;
            System.arraycopy(cArr, index, cArr, index + 4, this.size - index);
            char[] cArr2 = this.buffer;
            int index2 = index + 1;
            cArr2[index] = 't';
            int index3 = index2 + 1;
            cArr2[index2] = 'r';
            int index4 = index3 + 1;
            cArr2[index3] = 'u';
            cArr2[index4] = 'e';
            this.size += 4;
            int i = index4;
        } else {
            ensureCapacity(this.size + 5);
            char[] cArr3 = this.buffer;
            System.arraycopy(cArr3, index, cArr3, index + 5, this.size - index);
            char[] cArr4 = this.buffer;
            int index5 = index + 1;
            cArr4[index] = 'f';
            int index6 = index5 + 1;
            cArr4[index5] = 'a';
            int index7 = index6 + 1;
            cArr4[index6] = 'l';
            cArr4[index7] = 's';
            cArr4[index7 + 1] = 'e';
            this.size += 5;
        }
        return this;
    }

    public StrBuilder insert(int index, char value) {
        validateIndex(index);
        ensureCapacity(this.size + 1);
        char[] cArr = this.buffer;
        System.arraycopy(cArr, index, cArr, index + 1, this.size - index);
        this.buffer[index] = value;
        this.size++;
        return this;
    }

    public StrBuilder insert(int index, int value) {
        return insert(index, String.valueOf(value));
    }

    public StrBuilder insert(int index, long value) {
        return insert(index, String.valueOf(value));
    }

    public StrBuilder insert(int index, float value) {
        return insert(index, String.valueOf(value));
    }

    public StrBuilder insert(int index, double value) {
        return insert(index, String.valueOf(value));
    }

    private void deleteImpl(int startIndex, int endIndex, int len) {
        char[] cArr = this.buffer;
        System.arraycopy(cArr, endIndex, cArr, startIndex, this.size - endIndex);
        this.size -= len;
    }

    public StrBuilder delete(int startIndex, int endIndex) {
        int endIndex2 = validateRange(startIndex, endIndex);
        int len = endIndex2 - startIndex;
        if (len > 0) {
            deleteImpl(startIndex, endIndex2, len);
        }
        return this;
    }

    public StrBuilder deleteAll(char ch) {
        int i = 0;
        while (i < this.size) {
            if (this.buffer[i] == ch) {
                int start = i;
                do {
                    i++;
                    if (i >= this.size || this.buffer[i] != ch) {
                        int len = i - start;
                        deleteImpl(start, i, len);
                        i -= len;
                    }
                    i++;
                    break;
                } while (this.buffer[i] != ch);
                int len2 = i - start;
                deleteImpl(start, i, len2);
                i -= len2;
            }
            i++;
        }
        return this;
    }

    public StrBuilder deleteFirst(char ch) {
        int i = 0;
        while (true) {
            if (i >= this.size) {
                break;
            } else if (this.buffer[i] == ch) {
                deleteImpl(i, i + 1, 1);
                break;
            } else {
                i++;
            }
        }
        return this;
    }

    public StrBuilder deleteAll(String str) {
        int len = str == null ? 0 : str.length();
        if (len > 0) {
            int index = indexOf(str, 0);
            while (index >= 0) {
                deleteImpl(index, index + len, len);
                index = indexOf(str, index);
            }
        }
        return this;
    }

    public StrBuilder deleteFirst(String str) {
        int index;
        int len = str == null ? 0 : str.length();
        if (len > 0 && (index = indexOf(str, 0)) >= 0) {
            deleteImpl(index, index + len, len);
        }
        return this;
    }

    public StrBuilder deleteAll(StrMatcher matcher) {
        return replace(matcher, (String) null, 0, this.size, -1);
    }

    public StrBuilder deleteFirst(StrMatcher matcher) {
        return replace(matcher, (String) null, 0, this.size, 1);
    }

    private void replaceImpl(int startIndex, int endIndex, int removeLen, String insertStr, int insertLen) {
        int newSize = (this.size - removeLen) + insertLen;
        if (insertLen != removeLen) {
            ensureCapacity(newSize);
            char[] cArr = this.buffer;
            System.arraycopy(cArr, endIndex, cArr, startIndex + insertLen, this.size - endIndex);
            this.size = newSize;
        }
        if (insertLen > 0) {
            insertStr.getChars(0, insertLen, this.buffer, startIndex);
        }
    }

    public StrBuilder replace(int startIndex, int endIndex, String replaceStr) {
        int endIndex2 = validateRange(startIndex, endIndex);
        replaceImpl(startIndex, endIndex2, endIndex2 - startIndex, replaceStr, replaceStr == null ? 0 : replaceStr.length());
        return this;
    }

    public StrBuilder replaceAll(char search, char replace) {
        if (search != replace) {
            for (int i = 0; i < this.size; i++) {
                char[] cArr = this.buffer;
                if (cArr[i] == search) {
                    cArr[i] = replace;
                }
            }
        }
        return this;
    }

    public StrBuilder replaceFirst(char search, char replace) {
        if (search != replace) {
            int i = 0;
            while (true) {
                if (i >= this.size) {
                    break;
                }
                char[] cArr = this.buffer;
                if (cArr[i] == search) {
                    cArr[i] = replace;
                    break;
                }
                i++;
            }
        }
        return this;
    }

    public StrBuilder replaceAll(String searchStr, String replaceStr) {
        int searchLen = searchStr == null ? 0 : searchStr.length();
        if (searchLen > 0) {
            int replaceLen = replaceStr == null ? 0 : replaceStr.length();
            int index = indexOf(searchStr, 0);
            while (index >= 0) {
                replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceLen);
                index = indexOf(searchStr, index + replaceLen);
            }
        }
        return this;
    }

    public StrBuilder replaceFirst(String searchStr, String replaceStr) {
        int index;
        int searchLen = searchStr == null ? 0 : searchStr.length();
        if (searchLen > 0 && (index = indexOf(searchStr, 0)) >= 0) {
            replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceStr == null ? 0 : replaceStr.length());
        }
        return this;
    }

    public StrBuilder replaceAll(StrMatcher matcher, String replaceStr) {
        return replace(matcher, replaceStr, 0, this.size, -1);
    }

    public StrBuilder replaceFirst(StrMatcher matcher, String replaceStr) {
        return replace(matcher, replaceStr, 0, this.size, 1);
    }

    public StrBuilder replace(StrMatcher matcher, String replaceStr, int startIndex, int endIndex, int replaceCount) {
        return replaceImpl(matcher, replaceStr, startIndex, validateRange(startIndex, endIndex), replaceCount);
    }

    private StrBuilder replaceImpl(StrMatcher matcher, String replaceStr, int from, int to, int replaceCount) {
        if (matcher == null || this.size == 0) {
            return this;
        }
        int replaceLen = replaceStr == null ? 0 : replaceStr.length();
        int replaceCount2 = replaceCount;
        int to2 = to;
        int i = from;
        while (i < to2 && replaceCount2 != 0) {
            int removeLen = matcher.isMatch(this.buffer, i, from, to2);
            if (removeLen > 0) {
                replaceImpl(i, i + removeLen, removeLen, replaceStr, replaceLen);
                int to3 = (to2 - removeLen) + replaceLen;
                i = (i + replaceLen) - 1;
                if (replaceCount2 > 0) {
                    replaceCount2--;
                    to2 = to3;
                } else {
                    to2 = to3;
                }
            }
            i++;
        }
        return this;
    }

    public StrBuilder reverse() {
        int i = this.size;
        if (i == 0) {
            return this;
        }
        int half = i / 2;
        char[] buf = this.buffer;
        int leftIdx = 0;
        int rightIdx = i - 1;
        while (leftIdx < half) {
            char swap = buf[leftIdx];
            buf[leftIdx] = buf[rightIdx];
            buf[rightIdx] = swap;
            leftIdx++;
            rightIdx--;
        }
        return this;
    }

    public StrBuilder trim() {
        if (this.size == 0) {
            return this;
        }
        int len = this.size;
        char[] buf = this.buffer;
        int pos = 0;
        while (pos < len && buf[pos] <= ' ') {
            pos++;
        }
        while (pos < len && buf[len - 1] <= ' ') {
            len--;
        }
        int i = this.size;
        if (len < i) {
            delete(len, i);
        }
        if (pos > 0) {
            delete(0, pos);
        }
        return this;
    }

    public boolean startsWith(String str) {
        if (str == null) {
            return false;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        if (len > this.size) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (this.buffer[i] != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean endsWith(String str) {
        if (str == null) {
            return false;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        int i = this.size;
        if (len > i) {
            return false;
        }
        int pos = i - len;
        int i2 = 0;
        while (i2 < len) {
            if (this.buffer[pos] != str.charAt(i2)) {
                return false;
            }
            i2++;
            pos++;
        }
        return true;
    }

    public CharSequence subSequence(int startIndex, int endIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        } else if (endIndex > this.size) {
            throw new StringIndexOutOfBoundsException(endIndex);
        } else if (startIndex <= endIndex) {
            return substring(startIndex, endIndex);
        } else {
            throw new StringIndexOutOfBoundsException(endIndex - startIndex);
        }
    }

    public String substring(int start) {
        return substring(start, this.size);
    }

    public String substring(int startIndex, int endIndex) {
        return new String(this.buffer, startIndex, validateRange(startIndex, endIndex) - startIndex);
    }

    public String leftString(int length) {
        if (length <= 0) {
            return "";
        }
        int i = this.size;
        if (length >= i) {
            return new String(this.buffer, 0, i);
        }
        return new String(this.buffer, 0, length);
    }

    public String rightString(int length) {
        if (length <= 0) {
            return "";
        }
        int i = this.size;
        if (length >= i) {
            return new String(this.buffer, 0, i);
        }
        return new String(this.buffer, i - length, length);
    }

    public String midString(int index, int length) {
        int i;
        if (index < 0) {
            index = 0;
        }
        if (length <= 0 || index >= (i = this.size)) {
            return "";
        }
        if (i <= index + length) {
            return new String(this.buffer, index, i - index);
        }
        return new String(this.buffer, index, length);
    }

    public boolean contains(char ch) {
        char[] thisBuf = this.buffer;
        for (int i = 0; i < this.size; i++) {
            if (thisBuf[i] == ch) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String str) {
        return indexOf(str, 0) >= 0;
    }

    public boolean contains(StrMatcher matcher) {
        return indexOf(matcher, 0) >= 0;
    }

    public int indexOf(char ch) {
        return indexOf(ch, 0);
    }

    public int indexOf(char ch, int startIndex) {
        int startIndex2 = startIndex < 0 ? 0 : startIndex;
        if (startIndex2 >= this.size) {
            return -1;
        }
        char[] thisBuf = this.buffer;
        for (int i = startIndex2; i < this.size; i++) {
            if (thisBuf[i] == ch) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public int indexOf(String str, int startIndex) {
        int startIndex2 = startIndex < 0 ? 0 : startIndex;
        if (str == null || startIndex2 >= this.size) {
            return -1;
        }
        int strLen = str.length();
        if (strLen == 1) {
            return indexOf(str.charAt(0), startIndex2);
        }
        if (strLen == 0) {
            return startIndex2;
        }
        int i = this.size;
        if (strLen > i) {
            return -1;
        }
        char[] thisBuf = this.buffer;
        int len = (i - strLen) + 1;
        int i2 = startIndex2;
        while (i2 < len) {
            int j = 0;
            while (j < strLen) {
                if (str.charAt(j) != thisBuf[i2 + j]) {
                    i2++;
                } else {
                    j++;
                }
            }
            return i2;
        }
        return -1;
    }

    public int indexOf(StrMatcher matcher) {
        return indexOf(matcher, 0);
    }

    public int indexOf(StrMatcher matcher, int startIndex) {
        int startIndex2 = startIndex < 0 ? 0 : startIndex;
        if (matcher == null || startIndex2 >= this.size) {
            return -1;
        }
        int len = this.size;
        char[] buf = this.buffer;
        for (int i = startIndex2; i < len; i++) {
            if (matcher.isMatch(buf, i, startIndex2, len) > 0) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(char ch) {
        return lastIndexOf(ch, this.size - 1);
    }

    public int lastIndexOf(char ch, int startIndex) {
        int i = this.size;
        int startIndex2 = startIndex >= i ? i - 1 : startIndex;
        if (startIndex2 < 0) {
            return -1;
        }
        for (int i2 = startIndex2; i2 >= 0; i2--) {
            if (this.buffer[i2] == ch) {
                return i2;
            }
        }
        return -1;
    }

    public int lastIndexOf(String str) {
        return lastIndexOf(str, this.size - 1);
    }

    public int lastIndexOf(String str, int startIndex) {
        int i = this.size;
        int startIndex2 = startIndex >= i ? i - 1 : startIndex;
        if (str == null || startIndex2 < 0) {
            return -1;
        }
        int strLen = str.length();
        if (strLen <= 0 || strLen > this.size) {
            if (strLen == 0) {
                return startIndex2;
            }
        } else if (strLen == 1) {
            return lastIndexOf(str.charAt(0), startIndex2);
        } else {
            int i2 = (startIndex2 - strLen) + 1;
            while (i2 >= 0) {
                int j = 0;
                while (j < strLen) {
                    if (str.charAt(j) != this.buffer[i2 + j]) {
                        i2--;
                    } else {
                        j++;
                    }
                }
                return i2;
            }
        }
        return -1;
    }

    public int lastIndexOf(StrMatcher matcher) {
        return lastIndexOf(matcher, this.size);
    }

    public int lastIndexOf(StrMatcher matcher, int startIndex) {
        int i = this.size;
        int startIndex2 = startIndex >= i ? i - 1 : startIndex;
        if (matcher == null || startIndex2 < 0) {
            return -1;
        }
        char[] buf = this.buffer;
        int endIndex = startIndex2 + 1;
        for (int i2 = startIndex2; i2 >= 0; i2--) {
            if (matcher.isMatch(buf, i2, 0, endIndex) > 0) {
                return i2;
            }
        }
        return -1;
    }

    public StrTokenizer asTokenizer() {
        return new StrBuilderTokenizer();
    }

    public Reader asReader() {
        return new StrBuilderReader();
    }

    public Writer asWriter() {
        return new StrBuilderWriter();
    }

    public void appendTo(Appendable appendable) throws IOException {
        if (appendable instanceof Writer) {
            ((Writer) appendable).write(this.buffer, 0, this.size);
        } else if (appendable instanceof StringBuilder) {
            ((StringBuilder) appendable).append(this.buffer, 0, this.size);
        } else if (appendable instanceof StringBuffer) {
            ((StringBuffer) appendable).append(this.buffer, 0, this.size);
        } else if (appendable instanceof CharBuffer) {
            ((CharBuffer) appendable).put(this.buffer, 0, this.size);
        } else {
            appendable.append(this);
        }
    }

    public boolean equalsIgnoreCase(StrBuilder other) {
        if (this == other) {
            return true;
        }
        int i = this.size;
        if (i != other.size) {
            return false;
        }
        char[] thisBuf = this.buffer;
        char[] otherBuf = other.buffer;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            char c1 = thisBuf[i2];
            char c2 = otherBuf[i2];
            if (c1 != c2 && Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(StrBuilder other) {
        int i;
        if (this == other) {
            return true;
        }
        if (other == null || (i = this.size) != other.size) {
            return false;
        }
        char[] thisBuf = this.buffer;
        char[] otherBuf = other.buffer;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (thisBuf[i2] != otherBuf[i2]) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        return (obj instanceof StrBuilder) && equals((StrBuilder) obj);
    }

    public int hashCode() {
        char[] buf = this.buffer;
        int hash = 0;
        for (int i = this.size - 1; i >= 0; i--) {
            hash = (hash * 31) + buf[i];
        }
        return hash;
    }

    public String toString() {
        return new String(this.buffer, 0, this.size);
    }

    public StringBuffer toStringBuffer() {
        StringBuffer stringBuffer = new StringBuffer(this.size);
        stringBuffer.append(this.buffer, 0, this.size);
        return stringBuffer;
    }

    public StringBuilder toStringBuilder() {
        StringBuilder sb = new StringBuilder(this.size);
        sb.append(this.buffer, 0, this.size);
        return sb;
    }

    public String build() {
        return toString();
    }

    /* access modifiers changed from: protected */
    public int validateRange(int startIndex, int endIndex) {
        if (startIndex >= 0) {
            if (endIndex > this.size) {
                endIndex = this.size;
            }
            if (startIndex <= endIndex) {
                return endIndex;
            }
            throw new StringIndexOutOfBoundsException("end < start");
        }
        throw new StringIndexOutOfBoundsException(startIndex);
    }

    /* access modifiers changed from: protected */
    public void validateIndex(int index) {
        if (index < 0 || index > this.size) {
            throw new StringIndexOutOfBoundsException(index);
        }
    }

    class StrBuilderTokenizer extends StrTokenizer {
        StrBuilderTokenizer() {
        }

        /* access modifiers changed from: protected */
        public List<String> tokenize(char[] chars, int offset, int count) {
            if (chars == null) {
                return super.tokenize(StrBuilder.this.buffer, 0, StrBuilder.this.size());
            }
            return super.tokenize(chars, offset, count);
        }

        public String getContent() {
            String str = super.getContent();
            if (str == null) {
                return StrBuilder.this.toString();
            }
            return str;
        }
    }

    class StrBuilderReader extends Reader {
        private int mark;
        private int pos;

        StrBuilderReader() {
        }

        public void close() {
        }

        public int read() {
            if (!ready()) {
                return -1;
            }
            StrBuilder strBuilder = StrBuilder.this;
            int i = this.pos;
            this.pos = i + 1;
            return strBuilder.charAt(i);
        }

        public int read(char[] b, int off, int len) {
            if (off < 0 || len < 0 || off > b.length || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            } else {
                if (this.pos >= StrBuilder.this.size()) {
                    return -1;
                }
                if (this.pos + len > StrBuilder.this.size()) {
                    len = StrBuilder.this.size() - this.pos;
                }
                StrBuilder strBuilder = StrBuilder.this;
                int i = this.pos;
                strBuilder.getChars(i, i + len, b, off);
                this.pos += len;
                return len;
            }
        }

        public long skip(long n) {
            if (((long) this.pos) + n > ((long) StrBuilder.this.size())) {
                n = (long) (StrBuilder.this.size() - this.pos);
            }
            if (n < 0) {
                return 0;
            }
            this.pos = (int) (((long) this.pos) + n);
            return n;
        }

        public boolean ready() {
            return this.pos < StrBuilder.this.size();
        }

        public boolean markSupported() {
            return true;
        }

        public void mark(int readAheadLimit) {
            this.mark = this.pos;
        }

        public void reset() {
            this.pos = this.mark;
        }
    }

    class StrBuilderWriter extends Writer {
        StrBuilderWriter() {
        }

        public void close() {
        }

        public void flush() {
        }

        public void write(int c) {
            StrBuilder.this.append((char) c);
        }

        public void write(char[] cbuf) {
            StrBuilder.this.append(cbuf);
        }

        public void write(char[] cbuf, int off, int len) {
            StrBuilder.this.append(cbuf, off, len);
        }

        public void write(String str) {
            StrBuilder.this.append(str);
        }

        public void write(String str, int off, int len) {
            StrBuilder.this.append(str, off, len);
        }
    }
}
