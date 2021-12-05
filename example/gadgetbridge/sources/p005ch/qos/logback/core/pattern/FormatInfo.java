package p005ch.qos.logback.core.pattern;

/* renamed from: ch.qos.logback.core.pattern.FormatInfo */
public class FormatInfo {
    private boolean leftPad = true;
    private boolean leftTruncate = true;
    private int max = Integer.MAX_VALUE;
    private int min = Integer.MIN_VALUE;

    public FormatInfo() {
    }

    public FormatInfo(int i, int i2) {
        this.min = i;
        this.max = i2;
    }

    public FormatInfo(int i, int i2, boolean z, boolean z2) {
        this.min = i;
        this.max = i2;
        this.leftPad = z;
        this.leftTruncate = z2;
    }

    public static FormatInfo valueOf(String str) throws IllegalArgumentException {
        if (str != null) {
            FormatInfo formatInfo = new FormatInfo();
            int indexOf = str.indexOf(46);
            String str2 = null;
            if (indexOf != -1) {
                String substring = str.substring(0, indexOf);
                int i = indexOf + 1;
                if (i != str.length()) {
                    String str3 = substring;
                    str2 = str.substring(i);
                    str = str3;
                } else {
                    throw new IllegalArgumentException("Formatting string [" + str + "] should not end with '.'");
                }
            }
            if (str != null && str.length() > 0) {
                int parseInt = Integer.parseInt(str);
                if (parseInt >= 0) {
                    formatInfo.min = parseInt;
                } else {
                    formatInfo.min = -parseInt;
                    formatInfo.leftPad = false;
                }
            }
            if (str2 != null && str2.length() > 0) {
                int parseInt2 = Integer.parseInt(str2);
                if (parseInt2 >= 0) {
                    formatInfo.max = parseInt2;
                } else {
                    formatInfo.max = -parseInt2;
                    formatInfo.leftTruncate = false;
                }
            }
            return formatInfo;
        }
        throw new NullPointerException("Argument cannot be null");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FormatInfo)) {
            return false;
        }
        FormatInfo formatInfo = (FormatInfo) obj;
        return this.min == formatInfo.min && this.max == formatInfo.max && this.leftPad == formatInfo.leftPad && this.leftTruncate == formatInfo.leftTruncate;
    }

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }

    public int hashCode() {
        return (((((this.min * 31) + this.max) * 31) + (this.leftPad ? 1 : 0)) * 31) + (this.leftTruncate ? 1 : 0);
    }

    public boolean isLeftPad() {
        return this.leftPad;
    }

    public boolean isLeftTruncate() {
        return this.leftTruncate;
    }

    public void setLeftPad(boolean z) {
        this.leftPad = z;
    }

    public void setLeftTruncate(boolean z) {
        this.leftTruncate = z;
    }

    public void setMax(int i) {
        this.max = i;
    }

    public void setMin(int i) {
        this.min = i;
    }

    public String toString() {
        return "FormatInfo(" + this.min + ", " + this.max + ", " + this.leftPad + ", " + this.leftTruncate + ")";
    }
}
