package org.apache.commons.lang3.builder;

final class IDKey {

    /* renamed from: id */
    private final int f211id;
    private final Object value;

    IDKey(Object _value) {
        this.f211id = System.identityHashCode(_value);
        this.value = _value;
    }

    public int hashCode() {
        return this.f211id;
    }

    public boolean equals(Object other) {
        if (!(other instanceof IDKey)) {
            return false;
        }
        IDKey idKey = (IDKey) other;
        if (this.f211id == idKey.f211id && this.value == idKey.value) {
            return true;
        }
        return false;
    }
}
