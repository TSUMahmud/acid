package com.github.pfichtner.durationformatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class TimeUnits {
    public static final Map<TimeUnit, Long> maxValues;
    public static final List<TimeUnit> timeUnits;

    static {
        ArrayList arrayList = new ArrayList(Arrays.asList(TimeUnit.values()));
        Collections.sort(arrayList, Collections.reverseOrder());
        List<TimeUnit> unmodifiableList = Collections.unmodifiableList(arrayList);
        timeUnits = unmodifiableList;
        maxValues = Collections.unmodifiableMap(maxValuesFor(unmodifiableList));
    }

    private static Map<TimeUnit, Long> maxValuesFor(List<TimeUnit> list) {
        Map<TimeUnit, Long> maxValues2 = new HashMap<>(list.size());
        TimeUnit previous = null;
        for (TimeUnit timeUnit : list) {
            maxValues2.put(timeUnit, Long.valueOf(previous == null ? Long.MAX_VALUE : timeUnit.convert(1, previous)));
            previous = timeUnit;
        }
        return maxValues2;
    }
}
