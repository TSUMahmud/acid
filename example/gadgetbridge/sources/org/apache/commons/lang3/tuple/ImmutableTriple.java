package org.apache.commons.lang3.tuple;

public final class ImmutableTriple<L, M, R> extends Triple<L, M, R> {
    private static final ImmutableTriple NULL = m48of((Object) null, (Object) null, (Object) null);
    private static final long serialVersionUID = 1;
    public final L left;
    public final M middle;
    public final R right;

    public static <L, M, R> ImmutableTriple<L, M, R> nullTriple() {
        return NULL;
    }

    /* renamed from: of */
    public static <L, M, R> ImmutableTriple<L, M, R> m48of(L left2, M middle2, R right2) {
        return new ImmutableTriple<>(left2, middle2, right2);
    }

    public ImmutableTriple(L left2, M middle2, R right2) {
        this.left = left2;
        this.middle = middle2;
        this.right = right2;
    }

    public L getLeft() {
        return this.left;
    }

    public M getMiddle() {
        return this.middle;
    }

    public R getRight() {
        return this.right;
    }
}
