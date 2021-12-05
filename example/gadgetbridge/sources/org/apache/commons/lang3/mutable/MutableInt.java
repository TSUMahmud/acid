package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number> {
    private static final long serialVersionUID = 512176391864L;
    private int value;

    public MutableInt() {
    }

    public MutableInt(int value2) {
        this.value = value2;
    }

    public MutableInt(Number value2) {
        this.value = value2.intValue();
    }

    public MutableInt(String value2) throws NumberFormatException {
        this.value = Integer.parseInt(value2);
    }

    public Integer getValue() {
        return Integer.valueOf(this.value);
    }

    public void setValue(int value2) {
        this.value = value2;
    }

    public void setValue(Number value2) {
        this.value = value2.intValue();
    }

    public void increment() {
        this.value++;
    }

    public int getAndIncrement() {
        int last = this.value;
        this.value++;
        return last;
    }

    public int incrementAndGet() {
        this.value++;
        return this.value;
    }

    public void decrement() {
        this.value--;
    }

    public int getAndDecrement() {
        this.value--;
        return this.value;
    }

    public int decrementAndGet() {
        this.value--;
        return this.value;
    }

    public void add(int operand) {
        this.value += operand;
    }

    public void add(Number operand) {
        this.value += operand.intValue();
    }

    public void subtract(int operand) {
        this.value -= operand;
    }

    public void subtract(Number operand) {
        this.value -= operand.intValue();
    }

    public int addAndGet(int operand) {
        this.value += operand;
        return this.value;
    }

    public int addAndGet(Number operand) {
        this.value += operand.intValue();
        return this.value;
    }

    public int getAndAdd(int operand) {
        int last = this.value;
        this.value += operand;
        return last;
    }

    public int getAndAdd(Number operand) {
        int last = this.value;
        this.value += operand.intValue();
        return last;
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

    public Integer toInteger() {
        return Integer.valueOf(intValue());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MutableInt) || this.value != ((MutableInt) obj).intValue()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.value;
    }

    public int compareTo(MutableInt other) {
        return NumberUtils.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
