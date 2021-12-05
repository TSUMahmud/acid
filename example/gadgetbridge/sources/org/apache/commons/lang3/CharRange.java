package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import p005ch.qos.logback.core.CoreConstants;

final class CharRange implements Iterable<Character>, Serializable {
    private static final long serialVersionUID = 8270183163158333422L;
    /* access modifiers changed from: private */
    public final char end;
    private transient String iToString;
    /* access modifiers changed from: private */
    public final boolean negated;
    /* access modifiers changed from: private */
    public final char start;

    private CharRange(char start2, char end2, boolean negated2) {
        if (start2 > end2) {
            char temp = start2;
            start2 = end2;
            end2 = temp;
        }
        this.start = start2;
        this.end = end2;
        this.negated = negated2;
    }

    /* renamed from: is */
    public static CharRange m42is(char ch) {
        return new CharRange(ch, ch, false);
    }

    public static CharRange isNot(char ch) {
        return new CharRange(ch, ch, true);
    }

    public static CharRange isIn(char start2, char end2) {
        return new CharRange(start2, end2, false);
    }

    public static CharRange isNotIn(char start2, char end2) {
        return new CharRange(start2, end2, true);
    }

    public char getStart() {
        return this.start;
    }

    public char getEnd() {
        return this.end;
    }

    public boolean isNegated() {
        return this.negated;
    }

    public boolean contains(char ch) {
        return (ch >= this.start && ch <= this.end) != this.negated;
    }

    public boolean contains(CharRange range) {
        Validate.isTrue(range != null, "The Range must not be null", new Object[0]);
        if (this.negated) {
            if (range.negated) {
                if (this.start < range.start || this.end > range.end) {
                    return false;
                }
                return true;
            } else if (range.end < this.start || range.start > this.end) {
                return true;
            } else {
                return false;
            }
        } else if (range.negated) {
            if (this.start == 0 && this.end == 65535) {
                return true;
            }
            return false;
        } else if (this.start > range.start || this.end < range.end) {
            return false;
        } else {
            return true;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CharRange)) {
            return false;
        }
        CharRange other = (CharRange) obj;
        if (this.start == other.start && this.end == other.end && this.negated == other.negated) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.start + 'S' + (this.end * 7) + (this.negated ? 1 : 0);
    }

    public String toString() {
        if (this.iToString == null) {
            StringBuilder buf = new StringBuilder(4);
            if (isNegated()) {
                buf.append('^');
            }
            buf.append(this.start);
            if (this.start != this.end) {
                buf.append(CoreConstants.DASH_CHAR);
                buf.append(this.end);
            }
            this.iToString = buf.toString();
        }
        return this.iToString;
    }

    public Iterator<Character> iterator() {
        return new CharacterIterator();
    }

    private static class CharacterIterator implements Iterator<Character> {
        private char current;
        private boolean hasNext;
        private final CharRange range;

        private CharacterIterator(CharRange r) {
            this.range = r;
            this.hasNext = true;
            if (!this.range.negated) {
                this.current = this.range.start;
            } else if (this.range.start != 0) {
                this.current = 0;
            } else if (this.range.end == 65535) {
                this.hasNext = false;
            } else {
                this.current = (char) (this.range.end + 1);
            }
        }

        private void prepareNext() {
            if (this.range.negated) {
                char c = this.current;
                if (c == 65535) {
                    this.hasNext = false;
                } else if (c + 1 != this.range.start) {
                    this.current = (char) (this.current + 1);
                } else if (this.range.end == 65535) {
                    this.hasNext = false;
                } else {
                    this.current = (char) (this.range.end + 1);
                }
            } else if (this.current < this.range.end) {
                this.current = (char) (this.current + 1);
            } else {
                this.hasNext = false;
            }
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public Character next() {
            if (this.hasNext) {
                char cur = this.current;
                prepareNext();
                return Character.valueOf(cur);
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
