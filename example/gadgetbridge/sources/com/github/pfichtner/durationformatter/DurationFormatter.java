package com.github.pfichtner.durationformatter;

import com.github.pfichtner.durationformatter.TimeValues;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import p005ch.qos.logback.core.rolling.helper.DateTokenConverter;

public interface DurationFormatter {
    public static final DurationFormatter DIGITS = Builder.DIGITS.build();
    public static final DurationFormatter SYMBOLS = Builder.SYMBOLS.build();

    public interface Strategy {
        public static final Strategy NULL = new Strategy() {
            public final TimeValues apply(TimeValues values) {
                return values;
            }
        };

        TimeValues apply(TimeValues timeValues);
    }

    public enum SuppressZeros {
        LEADING,
        TRAILING,
        MIDDLE
    }

    String format(long j, TimeUnit timeUnit);

    String formatMillis(long j);

    public static class StrategyBuilder {
        private List<Strategy> strategies = new ArrayList();

        public StrategyBuilder add(Strategy strategy) {
            this.strategies.add(strategy);
            return this;
        }

        public Strategy build() {
            return new ComposedStrategy(this.strategies);
        }
    }

    public static class ComposedStrategy implements Strategy {
        private List<Strategy> strategies;

        ComposedStrategy(List<Strategy> strategies2) {
            this.strategies = strategies2;
        }

        public TimeValues apply(TimeValues result) {
            for (Strategy apply : this.strategies) {
                result = apply.apply(result);
            }
            return result;
        }
    }

    public static class Builder implements Cloneable {
        private static final Builder BASE;
        private static final EnumSet<SuppressZeros> DEFAULT_SUPPRESS_MODE = EnumSet.noneOf(SuppressZeros.class);
        public static final Builder DIGITS;
        public static final Builder SYMBOLS = BASE.separator(StringUtils.SPACE).format("%d").symbol(TimeUnit.NANOSECONDS, "ns").symbol(TimeUnit.MICROSECONDS, "μs").symbol(TimeUnit.MILLISECONDS, "ms").symbol(TimeUnit.SECONDS, "s").symbol(TimeUnit.MINUTES, "min").symbol(TimeUnit.HOURS, "h").symbol(TimeUnit.DAYS, DateTokenConverter.CONVERTER_KEY);
        /* access modifiers changed from: private */
        public Map<TimeUnit, String> formats = new HashMap();
        /* access modifiers changed from: private */
        public TimeUnit maximum = TimeUnit.HOURS;
        /* access modifiers changed from: private */
        public int maximumAmountOfUnitsToShow = Integer.MAX_VALUE;
        /* access modifiers changed from: private */
        public TimeUnit minimum = TimeUnit.MILLISECONDS;
        /* access modifiers changed from: private */
        public boolean round = true;
        /* access modifiers changed from: private */
        public String separator = ":";
        /* access modifiers changed from: private */
        public Set<SuppressZeros> suppressZeros = DEFAULT_SUPPRESS_MODE;
        /* access modifiers changed from: private */
        public Map<TimeUnit, String> symbols = new HashMap();
        /* access modifiers changed from: private */
        public String valueSymbolSeparator = "";

        static {
            Builder maximum2 = new Builder().minimum(TimeUnit.SECONDS).maximum(TimeUnit.HOURS);
            BASE = maximum2;
            DIGITS = maximum2;
        }

        private static class DefaultDurationFormatter implements DurationFormatter {
            private final Map<TimeUnit, String> formats;
            private final String separator;
            private final Strategy strategy;
            private final Map<TimeUnit, String> symbols;
            private final String valueSymbolSeparator;

            private static class SetUnusedTimeUnitsInvisibleStrategy implements Strategy {
                private final TimeUnit maximum;
                private final TimeUnit minimum;

                public SetUnusedTimeUnitsInvisibleStrategy(TimeUnit minimum2, TimeUnit maximum2) {
                    this.minimum = minimum2;
                    this.maximum = maximum2;
                }

                public final TimeValues apply(TimeValues values) {
                    Iterator i$ = values.iterator();
                    while (i$.hasNext()) {
                        TimeValues.Bucket next = i$.next();
                        TimeValues.Bucket bucket = next;
                        TimeUnit timeUnit = next.getTimeUnit();
                        TimeUnit timeUnit2 = timeUnit;
                        boolean z = true;
                        boolean b1 = timeUnit.compareTo(this.minimum) >= 0;
                        boolean b2 = timeUnit2.compareTo(this.maximum) <= 0;
                        if (!b1 || !b2) {
                            z = false;
                        }
                        bucket.setVisible(z);
                    }
                    return values;
                }
            }

            private static abstract class RemoveZerosStrategy implements Strategy {
                private final TimeUnit maximum;
                private final TimeUnit minimum;

                public RemoveZerosStrategy(TimeUnit minimum2, TimeUnit maximum2) {
                    this.minimum = minimum2;
                    this.maximum = maximum2;
                }

                /* access modifiers changed from: protected */
                public final TimeUnit getMinimum() {
                    return this.minimum;
                }

                /* access modifiers changed from: protected */
                public final TimeUnit getMaximum() {
                    return this.maximum;
                }

                protected static TimeValues removeZeros(TimeValues values, Iterable<TimeValues.Bucket> buckets) {
                    for (TimeValues.Bucket next : buckets) {
                        TimeValues.Bucket bucket = next;
                        if (next.isVisible() && bucket.getValue() != 0) {
                            break;
                        }
                        bucket.setVisible(false);
                    }
                    return values;
                }
            }

            private static class RemoveLeadingZerosStrategy extends RemoveZerosStrategy {
                public RemoveLeadingZerosStrategy(TimeUnit minimum, TimeUnit maximum) {
                    super(minimum, maximum);
                }

                public final TimeValues apply(TimeValues values) {
                    return removeZeros(values, values.sequence(getMaximum(), getMinimum()));
                }
            }

            private static class RemoveTrailingZerosStrategy extends RemoveZerosStrategy {
                public RemoveTrailingZerosStrategy(TimeUnit minimum, TimeUnit maximum) {
                    super(minimum, maximum);
                }

                public final TimeValues apply(TimeValues values) {
                    return removeZeros(values, values.sequence(getMinimum(), getMaximum()));
                }
            }

            private static class RemoveMiddleZerosStrategy extends RemoveZerosStrategy {
                public RemoveMiddleZerosStrategy(TimeUnit minimum, TimeUnit maximum) {
                    super(minimum, maximum);
                }

                public final TimeValues apply(TimeValues values) {
                    TimeUnit firstNonZero = findFirstVisibleNonZero(values.sequence(getMaximum(), getMinimum()));
                    TimeUnit lastNonZero = findFirstVisibleNonZero(values.sequence(getMinimum(), getMaximum()));
                    if (!(firstNonZero == null || lastNonZero == null)) {
                        for (TimeValues.Bucket next : values.sequenceInclude(firstNonZero, lastNonZero)) {
                            TimeValues.Bucket bucket = next;
                            if (next.isVisible() && bucket.getValue() == 0) {
                                bucket.setVisible(false);
                            }
                        }
                    }
                    return values;
                }

                private static TimeUnit findFirstVisibleNonZero(Iterable<TimeValues.Bucket> buckets) {
                    for (TimeValues.Bucket next : buckets) {
                        TimeValues.Bucket bucket = next;
                        if (next.isVisible() && bucket.getValue() != 0) {
                            return bucket.getTimeUnit();
                        }
                    }
                    return null;
                }
            }

            private static class LimitStrategy implements Strategy {
                private final int limit;

                public LimitStrategy(int limit2) {
                    this.limit = limit2;
                }

                public final TimeValues apply(TimeValues values) {
                    int visibles = 0;
                    Iterator i$ = values.iterator();
                    while (i$.hasNext()) {
                        TimeValues.Bucket next = i$.next();
                        TimeValues.Bucket bucket = next;
                        boolean z = next.isVisible() && visibles < this.limit;
                        boolean visible = z;
                        if (z) {
                            visibles++;
                        }
                        bucket.setVisible(visible);
                    }
                    return values;
                }
            }

            private static class RoundingStrategy implements Strategy {
                private RoundingStrategy() {
                }

                /* synthetic */ RoundingStrategy(byte b) {
                    this();
                }

                public final TimeValues apply(TimeValues values) {
                    boolean visibleFound = false;
                    Iterator i$ = values.iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        TimeValues.Bucket next = i$.next();
                        TimeValues.Bucket bucket = next;
                        boolean isVisible = next.isVisible();
                        boolean bucketIsVisible = isVisible;
                        if (!isVisible && visibleFound) {
                            bucket.pushLeftRounded();
                            break;
                        }
                        visibleFound |= bucketIsVisible;
                    }
                    return values;
                }
            }

            private static class PullFromLeftStrategy implements Strategy {
                private PullFromLeftStrategy() {
                }

                /* synthetic */ PullFromLeftStrategy(byte b) {
                    this();
                }

                public final TimeValues apply(TimeValues values) {
                    Iterator i$ = values.iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        TimeValues.Bucket next = i$.next();
                        TimeValues.Bucket bucket = next;
                        if (next.isVisible()) {
                            bucket.pollFromLeft();
                            break;
                        }
                    }
                    return values;
                }
            }

            private static class SetAtLeastOneBucketVisibleStrategy implements Strategy {
                private final TimeUnit minimum;

                public SetAtLeastOneBucketVisibleStrategy(TimeUnit minimum2) {
                    this.minimum = minimum2;
                }

                public final TimeValues apply(TimeValues values) {
                    Iterator i$ = values.iterator();
                    while (i$.hasNext()) {
                        if (i$.next().isVisible()) {
                            return values;
                        }
                    }
                    values.getBucket(this.minimum).setVisible(true);
                    return values;
                }
            }

            public DefaultDurationFormatter(Builder builder) {
                boolean z = true;
                Builder.checkState(builder.minimum.compareTo(builder.maximum) <= 0, "maximum must not be smaller than minimum");
                int idxMin = Builder.indexOf(TimeUnits.timeUnits, builder.minimum);
                int idxMax = Builder.indexOf(TimeUnits.timeUnits, builder.maximum);
                Builder.checkState(idxMin <= idxMax ? false : z, "min must not be greater than max");
                this.separator = builder.separator;
                this.valueSymbolSeparator = builder.valueSymbolSeparator;
                StrategyBuilder add = new StrategyBuilder().add(new SetUnusedTimeUnitsInvisibleStrategy(builder.minimum, builder.maximum));
                add = builder.suppressZeros.contains(SuppressZeros.LEADING) ? add.add(new RemoveLeadingZerosStrategy(builder.minimum, builder.maximum)) : add;
                add = builder.suppressZeros.contains(SuppressZeros.TRAILING) ? add.add(new RemoveTrailingZerosStrategy(builder.minimum, builder.maximum)) : add;
                add = builder.suppressZeros.contains(SuppressZeros.MIDDLE) ? add.add(new RemoveMiddleZerosStrategy(builder.minimum, builder.maximum)) : add;
                add = builder.maximumAmountOfUnitsToShow > 0 ? add.add(new LimitStrategy(builder.maximumAmountOfUnitsToShow)) : add;
                this.strategy = (builder.round ? add.add(new RoundingStrategy((byte) 0)) : add).add(new PullFromLeftStrategy((byte) 0)).add(new SetAtLeastOneBucketVisibleStrategy(builder.minimum)).build();
                this.formats = Collections.unmodifiableMap(createFormats(builder, idxMin, idxMax));
                this.symbols = Collections.unmodifiableMap(new HashMap(builder.symbols));
            }

            private static Map<TimeUnit, String> createFormats(Builder builder, int idxMin, int idxMax) {
                Map<TimeUnit, String> formats2 = new HashMap<>(builder.formats);
                for (TimeUnit timeUnit : TimeUnits.timeUnits.subList(idxMax, idxMin + 1)) {
                    String format = (String) builder.formats.get(timeUnit);
                    if (format == null) {
                        format = Builder.formatFor(timeUnit);
                    }
                    formats2.put(timeUnit, format);
                }
                return formats2;
            }

            public final String formatMillis(long value) {
                return format(value, TimeUnit.MILLISECONDS);
            }

            public final String format(long longVal, TimeUnit timeUnit) {
                TimeValues apply = this.strategy.apply(new TimeValues(longVal, timeUnit));
                StringBuilder sb = new StringBuilder();
                Iterator<TimeValues.Bucket> it = apply.iterator();
                while (it.hasNext()) {
                    TimeValues.Bucket next = it.next();
                    if (next.isVisible()) {
                        long value = next.getValue();
                        TimeUnit timeUnit2 = next.getTimeUnit();
                        String str = this.symbols.get(timeUnit2);
                        String format = String.format(this.formats.get(timeUnit2), new Object[]{Long.valueOf(value)});
                        if (str != null) {
                            format = format + this.valueSymbolSeparator + str;
                        }
                        sb.append(format);
                        sb.append(this.separator);
                    }
                }
                int length = sb.length();
                return length == 0 ? "" : sb.delete(length - this.separator.length(), length).toString();
            }
        }

        /* access modifiers changed from: private */
        public static String formatFor(TimeUnit timeUnit) {
            int i = timeUnit == TimeUnit.DAYS ? 2 : String.valueOf(TimeUnits.maxValues.get(timeUnit).longValue() - 1).length();
            return "%0" + i + DateTokenConverter.CONVERTER_KEY;
        }

        public DurationFormatter build() {
            return new DefaultDurationFormatter(this);
        }

        public Builder maximum(TimeUnit maximum2) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.maximum = maximum2;
            return clone2;
        }

        public Builder minimum(TimeUnit minimum2) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.minimum = minimum2;
            return clone2;
        }

        public Builder useOnly(TimeUnit timeUnit) {
            return minimum(timeUnit).maximum(timeUnit);
        }

        public Builder round(boolean round2) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.round = round2;
            return clone2;
        }

        public Builder separator(String separator2) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.separator = separator2;
            return clone2;
        }

        public Builder valueSymbolSeparator(String valueSymbolSeparator2) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.valueSymbolSeparator = valueSymbolSeparator2;
            return clone2;
        }

        public Builder format(String format) {
            Builder clone = clone();
            for (TimeUnit timeUnit : TimeUnits.timeUnits) {
                clone = clone.format(timeUnit, format);
            }
            return clone;
        }

        public Builder format(TimeUnit timeUnit, String format) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.formats.put(timeUnit, format);
            return clone2;
        }

        public Builder symbol(TimeUnit timeUnit, String symbol) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.symbols.put(timeUnit, symbol);
            return clone2;
        }

        public Builder suppressZeros(SuppressZeros suppressZeros2) {
            return suppressZeros((Collection<SuppressZeros>) suppressZeros2 == null ? DEFAULT_SUPPRESS_MODE : EnumSet.of(suppressZeros2));
        }

        public Builder suppressZeros(SuppressZeros... suppressZeros2) {
            return suppressZeros((Collection<SuppressZeros>) Arrays.asList(suppressZeros2));
        }

        public Builder suppressZeros(Collection<SuppressZeros> suppressZeros2) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.suppressZeros = suppressZeros2 == null ? DEFAULT_SUPPRESS_MODE : EnumSet.copyOf(suppressZeros2);
            return clone2;
        }

        public Builder maximumAmountOfUnitsToShow(int maximumAmountOfUnitsToShow2) {
            Builder clone = clone();
            Builder clone2 = clone;
            clone.maximumAmountOfUnitsToShow = maximumAmountOfUnitsToShow2;
            return clone2;
        }

        /* access modifiers changed from: protected */
        public Builder clone() {
            try {
                Builder builder = (Builder) super.clone();
                Builder clone = builder;
                builder.formats = new HashMap(this.formats);
                clone.symbols = new HashMap(this.symbols);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        /* access modifiers changed from: private */
        public static void checkState(boolean state, String errorMessage) {
            if (!state) {
                throw new IllegalStateException(errorMessage);
            }
        }

        /* access modifiers changed from: private */
        public static <T> int indexOf(List<T> ts, Object search) {
            int i = 0;
            for (T t : ts) {
                if (search.equals(t)) {
                    return i;
                }
                i++;
            }
            return -1;
        }
    }
}
