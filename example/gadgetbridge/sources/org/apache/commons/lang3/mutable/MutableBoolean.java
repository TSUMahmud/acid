package org.apache.commons.lang3.mutable;

import java.io.Serializable;
import org.apache.commons.lang3.BooleanUtils;

public class MutableBoolean implements Mutable<Boolean>, Serializable, Comparable<MutableBoolean> {
    private static final long serialVersionUID = -4830728138360036487L;
    private boolean value;

    public MutableBoolean() {
    }

    public MutableBoolean(boolean value2) {
        this.value = value2;
    }

    public MutableBoolean(Boolean value2) {
        this.value = value2.booleanValue();
    }

    public Boolean getValue() {
        return Boolean.valueOf(this.value);
    }

    public void setValue(boolean value2) {
        this.value = value2;
    }

    public void setFalse() {
        this.value = false;
    }

    public void setTrue() {
        this.value = true;
    }

    public void setValue(Boolean value2) {
        this.value = value2.booleanValue();
    }

    public boolean isTrue() {
        return this.value;
    }

    public boolean isFalse() {
        return !this.value;
    }

    public boolean booleanValue() {
        return this.value;
    }

    public Boolean toBoolean() {
        return Boolean.valueOf(booleanValue());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MutableBoolean) || this.value != ((MutableBoolean) obj).booleanValue()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.value ? Boolean.TRUE : Boolean.FALSE).hashCode();
    }

    public int compareTo(MutableBoolean other) {
        return BooleanUtils.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
