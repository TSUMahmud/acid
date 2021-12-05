package nodomain.freeyourgadget.gadgetbridge.util;

public class Version implements Comparable<Version> {
    private String version;

    public final String get() {
        return this.version;
    }

    public Version(String version2) {
        if (version2 != null) {
            String version3 = version2.trim();
            if (version3.matches("[0-9]+(\\.[0-9]+)*")) {
                this.version = version3;
                return;
            }
            throw new IllegalArgumentException("Invalid version format");
        }
        throw new IllegalArgumentException("Version can not be null");
    }

    public int compareTo(Version that) {
        if (that == null) {
            return 1;
        }
        String[] thisParts = get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        int i = 0;
        while (true) {
            int thatPart = 0;
            if (i >= length) {
                return 0;
            }
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            if (i < thatParts.length) {
                thatPart = Integer.parseInt(thatParts[i]);
            }
            if (thisPart < thatPart) {
                return -1;
            }
            if (thisPart > thatPart) {
                return 1;
            }
            i++;
        }
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that != null && getClass() == that.getClass() && compareTo((Version) that) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.version.hashCode();
    }
}
