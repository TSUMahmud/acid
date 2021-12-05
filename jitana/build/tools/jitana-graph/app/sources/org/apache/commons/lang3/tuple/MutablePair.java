package org.apache.commons.lang3.tuple;

public class MutablePair<L, R> extends Pair<L, R> {
    private static final long serialVersionUID = 4954918890077093841L;
    public L left;
    public R right;

    /* renamed from: of */
    public static <L, R> MutablePair<L, R> m49of(L left2, R right2) {
        return new MutablePair<>(left2, right2);
    }

    public MutablePair() {
    }

    public MutablePair(L left2, R right2) {
        this.left = left2;
        this.right = right2;
    }

    public L getLeft() {
        return this.left;
    }

    public void setLeft(L left2) {
        this.left = left2;
    }

    public R getRight() {
        return this.right;
    }

    public void setRight(R right2) {
        this.right = right2;
    }

    public R setValue(R value) {
        R result = getRight();
        setRight(value);
        return result;
    }
}
