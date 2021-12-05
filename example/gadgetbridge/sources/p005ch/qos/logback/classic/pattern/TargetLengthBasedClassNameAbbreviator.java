package p005ch.qos.logback.classic.pattern;

import java.io.PrintStream;

/* renamed from: ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator */
public class TargetLengthBasedClassNameAbbreviator implements Abbreviator {
    final int targetLength;

    public TargetLengthBasedClassNameAbbreviator(int i) {
        this.targetLength = i;
    }

    static int computeDotIndexes(String str, int[] iArr) {
        int i = 0;
        int i2 = 0;
        while (true) {
            int indexOf = str.indexOf(46, i);
            if (indexOf == -1 || i2 >= 16) {
                return i2;
            }
            iArr[i2] = indexOf;
            i2++;
            i = indexOf + 1;
        }
        return i2;
    }

    static void printArray(String str, int[] iArr) {
        System.out.print(str);
        for (int i = 0; i < iArr.length; i++) {
            PrintStream printStream = System.out;
            if (i == 0) {
                printStream.print(iArr[i]);
            } else {
                printStream.print(", " + iArr[i]);
            }
        }
        System.out.println();
    }

    public String abbreviate(String str) {
        String str2;
        StringBuilder sb = new StringBuilder(this.targetLength);
        if (str == null) {
            throw new IllegalArgumentException("Class name may not be null");
        } else if (str.length() < this.targetLength) {
            return str;
        } else {
            int[] iArr = new int[16];
            int[] iArr2 = new int[17];
            int computeDotIndexes = computeDotIndexes(str, iArr);
            if (computeDotIndexes == 0) {
                return str;
            }
            computeLengthArray(str, iArr, iArr2, computeDotIndexes);
            for (int i = 0; i <= computeDotIndexes; i++) {
                if (i == 0) {
                    str2 = str.substring(0, iArr2[i] - 1);
                } else {
                    int i2 = i - 1;
                    str2 = str.substring(iArr[i2], iArr[i2] + iArr2[i]);
                }
                sb.append(str2);
            }
            return sb.toString();
        }
    }

    /* access modifiers changed from: package-private */
    public void computeLengthArray(String str, int[] iArr, int[] iArr2, int i) {
        int length = str.length() - this.targetLength;
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = -1;
            if (i2 > 0) {
                i3 = iArr[i2 - 1];
            }
            int i4 = (iArr[i2] - i3) - 1;
            int i5 = (length <= 0 || i4 < 1) ? i4 : 1;
            length -= i4 - i5;
            iArr2[i2] = i5 + 1;
        }
        iArr2[i] = str.length() - iArr[i - 1];
    }
}
