package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number> {
    private static final long serialVersionUID = -2135791679;
    private short value;

    public MutableShort() {
    }

    public MutableShort(short value2) {
        this.value = value2;
    }

    public MutableShort(Number value2) {
        this.value = value2.shortValue();
    }

    public MutableShort(String value2) throws NumberFormatException {
        this.value = Short.parseShort(value2);
    }

    public Short getValue() {
        return Short.valueOf(this.value);
    }

    public void setValue(short value2) {
        this.value = value2;
    }

    public void setValue(Number value2) {
        this.value = value2.shortValue();
    }

    public void increment() {
        this.value = (short) (this.value + 1);
    }

    public short getAndIncrement() {
        short last = this.value;
        this.value = (short) (this.value + 1);
        return last;
    }

    public short incrementAndGet() {
        this.value = (short) (this.value + 1);
        return this.value;
    }

    public void decrement() {
        this.value = (short) (this.value - 1);
    }

    public short getAndDecrement() {
        short last = this.value;
        this.value = (short) (this.value - 1);
        return last;
    }

    public short decrementAndGet() {
        this.value = (short) (this.value - 1);
        return this.value;
    }

    public void add(short operand) {
        this.value = (short) (this.value + operand);
    }

    public void add(Number operand) {
        this.value = (short) (this.value + operand.shortValue());
    }

    public void subtract(short operand) {
        this.value = (short) (this.value - operand);
    }

    public void subtract(Number operand) {
        this.value = (short) (this.value - operand.shortValue());
    }

    public short addAndGet(short operand) {
        this.value = (short) (this.value + operand);
        return this.value;
    }

    public short addAndGet(Number operand) {
        this.value = (short) (this.value + operand.shortValue());
        return this.value;
    }

    public short getAndAdd(short operand) {
        short last = this.value;
        this.value = (short) (this.value + operand);
        return last;
    }

    public short getAndAdd(Number operand) {
        short last = this.value;
        this.value = (short) (this.value + operand.shortValue());
        return last;
    }

    public short shortValue() {
        return this.value;
    }

    public int intValue() {
        return this.value;
    }

    public long longValue() {
        return (long) this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public double doubleValue() {
        return (double) this.value;
    }

    public Short toShort() {
        return Short.valueOf(shortValue());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MutableShort) || this.value != ((MutableShort) obj).shortValue()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.value;
    }

    public int compareTo(MutableShort other) {
        return NumberUtils.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
