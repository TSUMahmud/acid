package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;

public class SleepAnalysis {
    public static final long MAX_WAKE_PHASE_LENGTH = 7200;
    public static final long MIN_SESSION_LENGTH = 300;

    public List<SleepSession> calculateSleepSessions(List<? extends ActivitySample> samples) {
        ActivitySample sample;
        SleepAnalysis sleepAnalysis = this;
        List<SleepSession> result = new ArrayList<>();
        ActivitySample previousSample = null;
        Date sleepStart = null;
        Date sleepEnd = null;
        long lightSleepDuration = 0;
        long deepSleepDuration = 0;
        long durationSinceLastSleep = 0;
        for (ActivitySample sample2 : samples) {
            if (sleepAnalysis.isSleep(sample2)) {
                if (sleepStart == null) {
                    sleepStart = sleepAnalysis.getDateFromSample(sample2);
                }
                sleepEnd = sleepAnalysis.getDateFromSample(sample2);
                durationSinceLastSleep = 0;
            }
            if (previousSample != null) {
                long durationSinceLastSample = (long) (sample2.getTimestamp() - previousSample.getTimestamp());
                if (sample2.getKind() == 2) {
                    lightSleepDuration += durationSinceLastSample;
                    sample = sample2;
                } else if (sample2.getKind() == 4) {
                    deepSleepDuration += durationSinceLastSample;
                    sample = sample2;
                } else {
                    durationSinceLastSleep += durationSinceLastSample;
                    if (sleepStart == null || durationSinceLastSleep <= MAX_WAKE_PHASE_LENGTH) {
                        sample = sample2;
                    } else {
                        if (lightSleepDuration + deepSleepDuration > 300) {
                            long j = durationSinceLastSample;
                            sample = sample2;
                            result.add(new SleepSession(sleepStart, sleepEnd, lightSleepDuration, deepSleepDuration));
                        } else {
                            sample = sample2;
                        }
                        deepSleepDuration = 0;
                        lightSleepDuration = 0;
                        sleepEnd = null;
                        sleepStart = null;
                    }
                }
            } else {
                sample = sample2;
            }
            previousSample = sample;
            sleepAnalysis = this;
        }
        if (lightSleepDuration + deepSleepDuration > 300) {
            result.add(new SleepSession(sleepStart, sleepEnd, lightSleepDuration, deepSleepDuration));
        }
        return result;
    }

    private boolean isSleep(ActivitySample sample) {
        return sample.getKind() == 4 || sample.getKind() == 2;
    }

    private Date getDateFromSample(ActivitySample sample) {
        return new Date(((long) sample.getTimestamp()) * 1000);
    }

    public static class SleepSession {
        private final long deepSleepDuration;
        private final long lightSleepDuration;
        private final Date sleepEnd;
        private final Date sleepStart;

        private SleepSession(Date sleepStart2, Date sleepEnd2, long lightSleepDuration2, long deepSleepDuration2) {
            this.sleepStart = sleepStart2;
            this.sleepEnd = sleepEnd2;
            this.lightSleepDuration = lightSleepDuration2;
            this.deepSleepDuration = deepSleepDuration2;
        }

        public Date getSleepStart() {
            return this.sleepStart;
        }

        public Date getSleepEnd() {
            return this.sleepEnd;
        }

        public long getLightSleepDuration() {
            return this.lightSleepDuration;
        }

        public long getDeepSleepDuration() {
            return this.deepSleepDuration;
        }
    }
}
