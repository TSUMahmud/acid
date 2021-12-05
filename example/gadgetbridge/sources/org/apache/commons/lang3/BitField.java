package org.apache.commons.lang3;

public class BitField {
    private final int _mask;
    private final int _shift_count;

    public BitField(int mask) {
        this._mask = mask;
        this._shift_count = mask != 0 ? Integer.numberOfTrailingZeros(mask) : 0;
    }

    public int getValue(int holder) {
        return getRawValue(holder) >> this._shift_count;
    }

    public short getShortValue(short holder) {
        return (short) getValue(holder);
    }

    public int getRawValue(int holder) {
        return this._mask & holder;
    }

    public short getShortRawValue(short holder) {
        return (short) getRawValue(holder);
    }

    public boolean isSet(int holder) {
        return (this._mask & holder) != 0;
    }

    public boolean isAllSet(int holder) {
        int i = this._mask;
        return (holder & i) == i;
    }

    public int setValue(int holder, int value) {
        int i = this._mask;
        return (i & (value << this._shift_count)) | ((i ^ -1) & holder);
    }

    public short setShortValue(short holder, short value) {
        return (short) setValue(holder, value);
    }

    public int clear(int holder) {
        return (this._mask ^ -1) & holder;
    }

    public short clearShort(short holder) {
        return (short) clear(holder);
    }

    public byte clearByte(byte holder) {
        return (byte) clear(holder);
    }

    public int set(int holder) {
        return this._mask | holder;
    }

    public short setShort(short holder) {
        return (short) set(holder);
    }

    public byte setByte(byte holder) {
        return (byte) set(holder);
    }

    public int setBoolean(int holder, boolean flag) {
        return flag ? set(holder) : clear(holder);
    }

    public short setShortBoolean(short holder, boolean flag) {
        return flag ? setShort(holder) : clearShort(holder);
    }

    public byte setByteBoolean(byte holder, boolean flag) {
        return flag ? setByte(holder) : clearByte(holder);
    }
}
