package org.apache.commons.lang3.tuple;

public class MutableTriple<L, M, R> extends Triple<L, M, R> {
    private static final long serialVersionUID = 1;
    public L left;
    public M middle;
    public R right;

    /* renamed from: of */
    public static <L, M, R> MutableTriple<L, M, R> m50of(L left2, M middle2, R right2) {
        return new MutableTriple<>(left2, middle2, right2);
    }

    public MutableTriple() {
    }

    public MutableTriple(L left2, M middle2, R right2) {
        this.left = left2;
        this.middle = middle2;
        this.right = right2;
    }

    public L getLeft() {
        return this.left;
    }

    public void setLeft(L left2) {
        this.left = left2;
    }

    public M getMiddle() {
        return this.middle;
    }

    public void setMiddle(M middle2) {
        this.middle = middle2;
    }

    public R getRight() {
        return this.right;
    }

    public void setRight(R right2) {
        this.right = right2;
    }
}
