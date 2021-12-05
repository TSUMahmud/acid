package org.apache.commons.lang3.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public class DiffBuilder implements Builder<DiffResult> {
    private final List<Diff<?>> diffs;
    private final Object left;
    private final boolean objectsTriviallyEqual;
    private final Object right;
    private final ToStringStyle style;

    public DiffBuilder(Object lhs, Object rhs, ToStringStyle style2, boolean testTriviallyEqual) {
        boolean z = true;
        Validate.isTrue(lhs != null, "lhs cannot be null", new Object[0]);
        Validate.isTrue(rhs != null, "rhs cannot be null", new Object[0]);
        this.diffs = new ArrayList();
        this.left = lhs;
        this.right = rhs;
        this.style = style2;
        if (!testTriviallyEqual || (lhs != rhs && !lhs.equals(rhs))) {
            z = false;
        }
        this.objectsTriviallyEqual = z;
    }

    public DiffBuilder(Object lhs, Object rhs, ToStringStyle style2) {
        this(lhs, rhs, style2, true);
    }

    public DiffBuilder append(String fieldName, final boolean lhs, final boolean rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Boolean>(fieldName) {
                private static final long serialVersionUID = 1;

                public Boolean getLeft() {
                    return Boolean.valueOf(lhs);
                }

                public Boolean getRight() {
                    return Boolean.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final boolean[] lhs, final boolean[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Boolean[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Boolean[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Boolean[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final byte lhs, final byte rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Byte>(fieldName) {
                private static final long serialVersionUID = 1;

                public Byte getLeft() {
                    return Byte.valueOf(lhs);
                }

                public Byte getRight() {
                    return Byte.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final byte[] lhs, final byte[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Byte[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Byte[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Byte[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final char lhs, final char rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Character>(fieldName) {
                private static final long serialVersionUID = 1;

                public Character getLeft() {
                    return Character.valueOf(lhs);
                }

                public Character getRight() {
                    return Character.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final char[] lhs, final char[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Character[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Character[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Character[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, double lhs, double rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && Double.doubleToLongBits(lhs) != Double.doubleToLongBits(rhs)) {
            final double d = lhs;
            final double d2 = rhs;
            this.diffs.add(new Diff<Double>(fieldName) {
                private static final long serialVersionUID = 1;

                public Double getLeft() {
                    return Double.valueOf(d);
                }

                public Double getRight() {
                    return Double.valueOf(d2);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final double[] lhs, final double[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Double[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Double[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Double[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final float lhs, final float rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && Float.floatToIntBits(lhs) != Float.floatToIntBits(rhs)) {
            this.diffs.add(new Diff<Float>(fieldName) {
                private static final long serialVersionUID = 1;

                public Float getLeft() {
                    return Float.valueOf(lhs);
                }

                public Float getRight() {
                    return Float.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final float[] lhs, final float[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Float[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Float[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Float[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final int lhs, final int rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Integer>(fieldName) {
                private static final long serialVersionUID = 1;

                public Integer getLeft() {
                    return Integer.valueOf(lhs);
                }

                public Integer getRight() {
                    return Integer.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final int[] lhs, final int[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Integer[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Integer[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Integer[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, long lhs, long rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            final long j = lhs;
            final long j2 = rhs;
            this.diffs.add(new Diff<Long>(fieldName) {
                private static final long serialVersionUID = 1;

                public Long getLeft() {
                    return Long.valueOf(j);
                }

                public Long getRight() {
                    return Long.valueOf(j2);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final long[] lhs, final long[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Long[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Long[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Long[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final short lhs, final short rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Short>(fieldName) {
                private static final long serialVersionUID = 1;

                public Short getLeft() {
                    return Short.valueOf(lhs);
                }

                public Short getRight() {
                    return Short.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final short[] lhs, final short[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Short[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Short[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }

                public Short[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, final Object lhs, final Object rhs) {
        Object objectToTest;
        validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            objectToTest = lhs;
        } else {
            objectToTest = rhs;
        }
        if (objectToTest.getClass().isArray()) {
            if (objectToTest instanceof boolean[]) {
                return append(fieldName, (boolean[]) lhs, (boolean[]) rhs);
            }
            if (objectToTest instanceof byte[]) {
                return append(fieldName, (byte[]) lhs, (byte[]) rhs);
            }
            if (objectToTest instanceof char[]) {
                return append(fieldName, (char[]) lhs, (char[]) rhs);
            }
            if (objectToTest instanceof double[]) {
                return append(fieldName, (double[]) lhs, (double[]) rhs);
            }
            if (objectToTest instanceof float[]) {
                return append(fieldName, (float[]) lhs, (float[]) rhs);
            }
            if (objectToTest instanceof int[]) {
                return append(fieldName, (int[]) lhs, (int[]) rhs);
            }
            if (objectToTest instanceof long[]) {
                return append(fieldName, (long[]) lhs, (long[]) rhs);
            }
            if (objectToTest instanceof short[]) {
                return append(fieldName, (short[]) lhs, (short[]) rhs);
            }
            return append(fieldName, (Object[]) lhs, (Object[]) rhs);
        } else if (lhs != null && lhs.equals(rhs)) {
            return this;
        } else {
            this.diffs.add(new Diff<Object>(fieldName) {
                private static final long serialVersionUID = 1;

                public Object getLeft() {
                    return lhs;
                }

                public Object getRight() {
                    return rhs;
                }
            });
            return this;
        }
    }

    public DiffBuilder append(String fieldName, final Object[] lhs, final Object[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && !Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Object[]>(fieldName) {
                private static final long serialVersionUID = 1;

                public Object[] getLeft() {
                    return lhs;
                }

                public Object[] getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }

    public DiffBuilder append(String fieldName, DiffResult diffResult) {
        validateFieldNameNotNull(fieldName);
        Validate.isTrue(diffResult != null, "Diff result cannot be null", new Object[0]);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        for (Diff<?> diff : diffResult.getDiffs()) {
            append(fieldName + "." + diff.getFieldName(), diff.getLeft(), diff.getRight());
        }
        return this;
    }

    public DiffResult build() {
        return new DiffResult(this.left, this.right, this.diffs, this.style);
    }

    private void validateFieldNameNotNull(String fieldName) {
        Validate.isTrue(fieldName != null, "Field name cannot be null", new Object[0]);
    }
}
