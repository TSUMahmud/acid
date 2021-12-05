package nodomain.freeyourgadget.gadgetbridge.service.devices.huami;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityPoint;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack;
import nodomain.freeyourgadget.gadgetbridge.model.GPSCoordinate;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuamiActivityDetailsParser {
    private static final BigDecimal HUAMI_TO_DECIMAL_DEGREES_DIVISOR = new BigDecimal(3000000.0d);
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HuamiActivityDetailsParser.class);
    private static final byte TYPE_GPS = 0;
    private static final byte TYPE_HR = 1;
    private static final byte TYPE_PAUSE = 2;
    private static final byte TYPE_RESUME = 3;
    private static final byte TYPE_SPEED4 = 4;
    private static final byte TYPE_SPEED5 = 5;
    private static final byte TYPE_SPEED6 = 6;
    private static final byte TYPE_SWIMMING = 8;
    private final ActivityTrack activityTrack = new ActivityTrack();
    private int baseAltitude;
    private final Date baseDate;
    private long baseLatitude;
    private long baseLongitude;
    private ActivityPoint lastActivityPoint;
    private boolean skipCounterByte;

    public void setSkipCounterByte(boolean skipCounterByte2) {
        this.skipCounterByte = skipCounterByte2;
    }

    public HuamiActivityDetailsParser(BaseActivitySummary summary) {
        this.baseLongitude = (long) summary.getBaseLongitude().intValue();
        this.baseLatitude = (long) summary.getBaseLatitude().intValue();
        this.baseAltitude = summary.getBaseAltitude().intValue();
        this.baseDate = summary.getStartTime();
        this.activityTrack.setUser(summary.getUser());
        this.activityTrack.setDevice(summary.getDevice());
        ActivityTrack activityTrack2 = this.activityTrack;
        activityTrack2.setName(summary.getName() + "-" + summary.getId());
    }

    public ActivityTrack parse(byte[] bytes) throws GBException {
        int i = 0;
        long totalTimeOffset = 0;
        int lastTimeOffset = 0;
        while (i < bytes.length) {
            try {
                if (this.skipCounterByte && i % 17 == 0) {
                    i++;
                }
                int i2 = i + 1;
                try {
                    byte i3 = bytes[i];
                    int i4 = i2 + 1;
                    try {
                        int timeOffset = BLETypeConversions.toUnsigned(bytes[i2]);
                        if (lastTimeOffset <= timeOffset) {
                            timeOffset -= lastTimeOffset;
                            lastTimeOffset += timeOffset;
                        } else {
                            lastTimeOffset = timeOffset;
                        }
                        totalTimeOffset += (long) timeOffset;
                        switch (i3) {
                            case 0:
                                i = i4 + consumeGPSAndUpdateBaseLocation(bytes, i4, totalTimeOffset);
                                break;
                            case 1:
                                i = i4 + consumeHeartRate(bytes, i4, totalTimeOffset);
                                break;
                            case 2:
                                i = i4 + consumePause(bytes, i4);
                                break;
                            case 3:
                                i = i4 + consumeResume(bytes, i4);
                                break;
                            case 4:
                                i = i4 + consumeSpeed4(bytes, i4);
                                break;
                            case 5:
                                i = i4 + consumeSpeed5(bytes, i4);
                                break;
                            case 6:
                                i = i4 + consumeSpeed6(bytes, i4);
                                break;
                            case 8:
                                i = i4 + consumeSwimming(bytes, i4);
                                break;
                            default:
                                Logger logger = LOG;
                                logger.warn("unknown packet type" + i3);
                                i = i4 + 6;
                                break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e = e;
                        IndexOutOfBoundsException ex = e;
                        throw new GBException("Error parsing activity details: " + ex.getMessage(), ex);
                    }
                } catch (IndexOutOfBoundsException e2) {
                    e = e2;
                    int i5 = i2;
                    IndexOutOfBoundsException ex2 = e;
                    throw new GBException("Error parsing activity details: " + ex2.getMessage(), ex2);
                }
            } catch (IndexOutOfBoundsException e3) {
                e = e3;
                int i6 = i;
                IndexOutOfBoundsException ex22 = e;
                throw new GBException("Error parsing activity details: " + ex22.getMessage(), ex22);
            }
        }
        fixupMissingTimestamps(this.activityTrack);
        return this.activityTrack;
    }

    private void fixupMissingTimestamps(ActivityTrack activityTrack2) {
        int pointer = 0;
        try {
            List<ActivityPoint> activityPointList = activityTrack2.getTrackPoints();
            Date gpsStartTime = null;
            List<ActivityPoint> entriesToFixUp = new ArrayList<>();
            while (true) {
                if (pointer >= activityPointList.size() - 1) {
                    break;
                }
                ActivityPoint activityPoint = activityPointList.get(pointer);
                if (activityPoint.getLocation() != null) {
                    if (!activityPoint.getTime().equals(activityPointList.get(pointer + 1).getTime())) {
                        entriesToFixUp.add(activityPoint);
                        gpsStartTime = activityPointList.get(pointer + 1).getTime();
                        break;
                    }
                    entriesToFixUp.add(activityPoint);
                    pointer++;
                } else {
                    pointer++;
                }
            }
            if (gpsStartTime != null) {
                double convert = (double) TimeUnit.SECONDS.convert(Math.abs(gpsStartTime.getTime() - this.baseDate.getTime()), TimeUnit.MILLISECONDS);
                double size = (double) entriesToFixUp.size();
                Double.isNaN(convert);
                Double.isNaN(size);
                double multiplier = convert / size;
                for (int j = 0; j < entriesToFixUp.size(); j++) {
                    double d = (double) j;
                    Double.isNaN(d);
                    entriesToFixUp.get(j).setTime(makeAbsolute(Math.round(d * multiplier)));
                }
            }
        } catch (Exception ex) {
            LOG.warn("Error cleaning activity details", (Throwable) ex);
        }
    }

    private int consumeGPSAndUpdateBaseLocation(byte[] bytes, int offset, long timeOffset) {
        int i = 0 + 1;
        int i2 = i + 1;
        int longitudeDelta = BLETypeConversions.toInt16(bytes[0 + offset], bytes[i + offset]);
        int i3 = i2 + 1;
        int i4 = i3 + 1;
        int latitudeDelta = BLETypeConversions.toInt16(bytes[i2 + offset], bytes[i3 + offset]);
        int i5 = i4 + 1;
        int i6 = i5 + 1;
        int altitudeDelta = BLETypeConversions.toInt16(bytes[i4 + offset], bytes[offset + i5]);
        this.baseLongitude += (long) longitudeDelta;
        this.baseLatitude += (long) latitudeDelta;
        this.baseAltitude += altitudeDelta;
        GPSCoordinate gPSCoordinate = new GPSCoordinate(convertHuamiValueToDecimalDegrees(this.baseLongitude), convertHuamiValueToDecimalDegrees(this.baseLatitude), (double) this.baseAltitude);
        ActivityPoint ap = getActivityPointFor(timeOffset, gPSCoordinate);
        ap.setLocation(gPSCoordinate);
        add(ap);
        return i6;
    }

    private double convertHuamiValueToDecimalDegrees(long huamiValue) {
        return new BigDecimal(huamiValue).divide(HUAMI_TO_DECIMAL_DEGREES_DIVISOR, 6, RoundingMode.HALF_UP).doubleValue();
    }

    private int consumeHeartRate(byte[] bytes, int offset, long timeOffsetSeconds) {
        int v1 = BLETypeConversions.toUint16(bytes[offset]);
        int v2 = BLETypeConversions.toUint16(bytes[offset + 1]);
        int v3 = BLETypeConversions.toUint16(bytes[offset + 2]);
        int v4 = BLETypeConversions.toUint16(bytes[offset + 3]);
        int v5 = BLETypeConversions.toUint16(bytes[offset + 4]);
        int v6 = BLETypeConversions.toUint16(bytes[offset + 5]);
        if (v2 == 0 && v3 == 0 && v4 == 0 && v5 == 0 && v6 == 0) {
            LOG.info("detected heart rate in 'new' version format");
            ActivityPoint ap = getActivityPointFor(timeOffsetSeconds);
            ap.setHeartRate(v1);
            add(ap);
            return 6;
        }
        ActivityPoint ap2 = getActivityPointFor((long) v1);
        ap2.setHeartRate(v2);
        add(ap2);
        ActivityPoint ap3 = getActivityPointFor((long) v3);
        ap3.setHeartRate(v4);
        add(ap3);
        ActivityPoint ap4 = getActivityPointFor((long) v5);
        ap4.setHeartRate(v6);
        add(ap4);
        return 6;
    }

    private ActivityPoint getActivityPointFor(long timeOffsetSeconds) {
        Date time = makeAbsolute(timeOffsetSeconds);
        ActivityPoint activityPoint = this.lastActivityPoint;
        if (activityPoint == null || !activityPoint.getTime().equals(time)) {
            return new ActivityPoint(time);
        }
        return this.lastActivityPoint;
    }

    private ActivityPoint getActivityPointFor(long timeOffsetSeconds, GPSCoordinate gpsCoordinate) {
        Date time = makeAbsolute(timeOffsetSeconds);
        ActivityPoint activityPoint = this.lastActivityPoint;
        if (activityPoint == null || !activityPoint.getTime().equals(time)) {
            return new ActivityPoint(time);
        }
        if (this.lastActivityPoint.getLocation() == null || this.lastActivityPoint.getLocation().equals(gpsCoordinate)) {
            return this.lastActivityPoint;
        }
        return new ActivityPoint(time);
    }

    private Date makeAbsolute(long timeOffsetSeconds) {
        return new Date(this.baseDate.getTime() + (1000 * timeOffsetSeconds));
    }

    private void add(ActivityPoint ap) {
        if (ap != this.lastActivityPoint) {
            this.lastActivityPoint = ap;
            this.activityTrack.addTrackPoint(ap);
            return;
        }
        LOG.info("skipping point!");
    }

    private int consumePause(byte[] bytes, int offset) {
        Logger logger = LOG;
        logger.debug("got pause packet: " + C1238GB.hexdump(bytes, offset, 6));
        return 6;
    }

    private int consumeResume(byte[] bytes, int offset) {
        Logger logger = LOG;
        logger.debug("got resume package: " + C1238GB.hexdump(bytes, offset, 6));
        return 6;
    }

    private int consumeSpeed4(byte[] bytes, int offset) {
        Logger logger = LOG;
        logger.debug("got packet type 4 (speed): " + C1238GB.hexdump(bytes, offset, 6));
        return 6;
    }

    private int consumeSpeed5(byte[] bytes, int offset) {
        Logger logger = LOG;
        logger.debug("got packet type 5 (speed): " + C1238GB.hexdump(bytes, offset, 6));
        return 6;
    }

    private int consumeSpeed6(byte[] bytes, int offset) {
        Logger logger = LOG;
        logger.debug("got packet type 6 (speed): " + C1238GB.hexdump(bytes, offset, 6));
        return 6;
    }

    private int consumeSwimming(byte[] bytes, int offset) {
        Logger logger = LOG;
        logger.debug("got packet type 8 (swimming?): " + C1238GB.hexdump(bytes, offset, 6));
        return 6;
    }
}
