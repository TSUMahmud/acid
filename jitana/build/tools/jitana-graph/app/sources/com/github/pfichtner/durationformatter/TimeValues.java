package com.github.pfichtner.durationformatter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class TimeValues implements Iterable<Bucket> {
    private static final Map<TimeUnit, Long> maxValues = TimeUnits.maxValues;
    private static final List<TimeUnit> timeUnits = TimeUnits.timeUnits;
    private final Bucket[] buckets = initialize();

    public static class Bucket {
        private final long maxValue;
        private final Bucket previous;
        private final TimeUnit timeUnit;
        private long value;
        private boolean visible = true;

        Bucket(Bucket previous2, TimeUnit timeUnit2, long maxValue2) {
            this.previous = previous2;
            this.timeUnit = timeUnit2;
            this.maxValue = maxValue2;
        }

        /* access modifiers changed from: private */
        public void addToValue(long toadd) {
            Bucket bucket;
            Bucket bucket2 = this;
            while (true) {
                long newValue = bucket2.value + toadd;
                bucket2.value = newValue % bucket2.maxValue;
                long newValue2 = newValue - bucket2.value;
                long rest = newValue2;
                if (newValue2 > 0 && (bucket = bucket2.previous) != null) {
                    Bucket bucket3 = bucket2.previous;
                    toadd = bucket.timeUnit.convert(rest, bucket2.timeUnit);
                    bucket2 = bucket3;
                } else {
                    return;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public final void pushLeftRounded() {
            long j = this.maxValue;
            long half = j / 2;
            if (this.value + half >= j) {
                addToValue(half);
            } else {
                this.value = 0;
            }
        }

        /* access modifiers changed from: package-private */
        public final void pollFromLeft() {
            Bucket bucket = this.previous;
            if (bucket != null) {
                bucket.pollFromLeft();
                long j = this.value;
                TimeUnit timeUnit2 = this.timeUnit;
                Bucket bucket2 = this.previous;
                this.value = j + timeUnit2.convert(bucket2.value, bucket2.timeUnit);
                this.previous.value = 0;
            }
        }

        public final long getValue() {
            return this.value;
        }

        public final TimeUnit getTimeUnit() {
            return this.timeUnit;
        }

        public final boolean isVisible() {
            return this.visible;
        }

        public final void setVisible(boolean visible2) {
            this.visible = visible2;
        }

        public final String toString() {
            return "Bucket [timeUnit=" + this.timeUnit + ", value=" + this.value + ", visible=" + this.visible + "]";
        }
    }

    public TimeValues() {
    }

    public TimeValues(long value, TimeUnit timeUnit) {
        getBucket(timeUnit).addToValue(value);
    }

    public final Bucket getBucket(TimeUnit timeUnit) {
        return this.buckets[bucketIdx(timeUnit)];
    }

    private static int bucketIdx(TimeUnit timeUnit) {
        return timeUnits.indexOf(timeUnit);
    }

    private static Bucket[] initialize() {
        Bucket[] buckets2 = new Bucket[timeUnits.size()];
        Bucket previous = null;
        for (int i = 0; i < timeUnits.size(); i++) {
            TimeUnit timeUnit = timeUnits.get(i);
            buckets2[i] = new Bucket(previous, timeUnit, maxValues.get(timeUnit).longValue());
            previous = buckets2[i];
        }
        return buckets2;
    }

    public final Iterator<Bucket> iterator() {
        return Arrays.asList(this.buckets).iterator();
    }

    public final Iterable<Bucket> sequence(TimeUnit timeUnit1, TimeUnit timeUnit2) {
        int startIdx = bucketIdx(timeUnit1);
        int endIdx = bucketIdx(timeUnit2);
        return startIdx <= endIdx ? Arrays.asList(this.buckets).subList(startIdx, endIdx) : new ReverseIterable(Arrays.asList(this.buckets).subList(endIdx + 1, startIdx + 1));
    }

    public final Iterable<Bucket> sequenceInclude(TimeUnit timeUnit1, TimeUnit timeUnit2) {
        int startIdx = bucketIdx(timeUnit1);
        int endIdx = bucketIdx(timeUnit2);
        return startIdx <= endIdx ? Arrays.asList(this.buckets).subList(startIdx, endIdx + 1) : new ReverseIterable(Arrays.asList(this.buckets).subList(endIdx, startIdx + 1));
    }

    public final String toString() {
        return "Buckets [buckets=" + Arrays.toString(this.buckets) + "]";
    }
}
