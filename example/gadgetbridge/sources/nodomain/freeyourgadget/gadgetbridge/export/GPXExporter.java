package nodomain.freeyourgadget.gadgetbridge.export;

import cyanogenmod.providers.ThemesContract;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.activities.HeartRateUtils;
import nodomain.freeyourgadget.gadgetbridge.export.ActivityTrackExporter;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityPoint;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack;
import nodomain.freeyourgadget.gadgetbridge.model.GPSCoordinate;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import org.xmlpull.v1.XmlSerializer;

public class GPXExporter implements ActivityTrackExporter {
    private static final String NS_GPX_PREFIX = "";
    private static final String NS_GPX_URI = "http://www.topografix.com/GPX/1/1";
    private static final String NS_TRACKPOINT_EXTENSION = "gpxtpx";
    private static final String NS_TRACKPOINT_EXTENSION_URI = "http://www.garmin.com/xmlschemas/TrackPointExtension/v1";
    private static final String NS_XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private String creator;
    private boolean includeHeartRate = true;
    private boolean includeHeartRateOfNearestSample = true;

    public String getDefaultFileName(ActivityTrack track) {
        return FileUtils.makeValidFileName(track.getName());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0063, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0064, code lost:
        r0.addSuppressed(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0067, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x005e, code lost:
        r1 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performExport(nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack r10, java.io.File r11) throws java.io.IOException, nodomain.freeyourgadget.gadgetbridge.export.ActivityTrackExporter.GPXTrackEmptyException {
        /*
            r9 = this;
            java.lang.String r0 = "gpx"
            java.lang.String r1 = "http://www.w3.org/2001/XMLSchema-instance"
            java.lang.String r2 = "http://www.topografix.com/GPX/1/1"
            java.nio.charset.Charset r3 = java.nio.charset.StandardCharsets.UTF_8
            java.lang.String r3 = r3.name()
            org.xmlpull.v1.XmlSerializer r4 = android.util.Xml.newSerializer()
            java.io.FileOutputStream r5 = new java.io.FileOutputStream
            r5.<init>(r11)
            r4.setOutput(r5, r3)     // Catch:{ all -> 0x005c }
            java.lang.Boolean r6 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x005c }
            r4.startDocument(r3, r6)     // Catch:{ all -> 0x005c }
            java.lang.String r6 = "xsi"
            r4.setPrefix(r6, r1)     // Catch:{ all -> 0x005c }
            java.lang.String r6 = "gpxtpx"
            java.lang.String r7 = "http://www.garmin.com/xmlschemas/TrackPointExtension/v1"
            r4.setPrefix(r6, r7)     // Catch:{ all -> 0x005c }
            java.lang.String r6 = ""
            r4.setPrefix(r6, r2)     // Catch:{ all -> 0x005c }
            r4.startTag(r2, r0)     // Catch:{ all -> 0x005c }
            java.lang.String r6 = "version"
            java.lang.String r7 = "1.1"
            r8 = 0
            r4.attribute(r8, r6, r7)     // Catch:{ all -> 0x005c }
            java.lang.String r6 = "creator"
            java.lang.String r7 = r9.getCreator()     // Catch:{ all -> 0x005c }
            r4.attribute(r8, r6, r7)     // Catch:{ all -> 0x005c }
            java.lang.String r6 = "schemaLocation"
            java.lang.String r7 = "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd"
            r4.attribute(r1, r6, r7)     // Catch:{ all -> 0x005c }
            r9.exportMetadata(r4, r10)     // Catch:{ all -> 0x005c }
            r9.exportTrack(r4, r10)     // Catch:{ all -> 0x005c }
            r4.endTag(r2, r0)     // Catch:{ all -> 0x005c }
            r4.endDocument()     // Catch:{ all -> 0x005c }
            r4.flush()     // Catch:{ all -> 0x005c }
            r5.close()
            return
        L_0x005c:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x005e }
        L_0x005e:
            r1 = move-exception
            r5.close()     // Catch:{ all -> 0x0063 }
            goto L_0x0067
        L_0x0063:
            r2 = move-exception
            r0.addSuppressed(r2)
        L_0x0067:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.export.GPXExporter.performExport(nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack, java.io.File):void");
    }

    private void exportMetadata(XmlSerializer ser, ActivityTrack track) throws IOException {
        ser.startTag(NS_GPX_URI, "metadata");
        ser.startTag(NS_GPX_URI, "name").text(track.getName()).endTag(NS_GPX_URI, "name");
        ser.startTag(NS_GPX_URI, ThemesContract.ThemesColumns.AUTHOR);
        ser.startTag(NS_GPX_URI, "name").text(track.getUser().getName()).endTag(NS_GPX_URI, "name");
        ser.endTag(NS_GPX_URI, ThemesContract.ThemesColumns.AUTHOR);
        ser.startTag(NS_GPX_URI, "time").text(formatTime(new Date())).endTag(NS_GPX_URI, "time");
        ser.endTag(NS_GPX_URI, "metadata");
    }

    private String formatTime(Date date) {
        return DateTimeUtils.formatIso8601(date);
    }

    private void exportTrack(XmlSerializer ser, ActivityTrack track) throws IOException, ActivityTrackExporter.GPXTrackEmptyException {
        ser.startTag(NS_GPX_URI, "trk");
        ser.startTag(NS_GPX_URI, "trkseg");
        List<ActivityPoint> trackPoints = track.getTrackPoints();
        String source = getSource(track);
        boolean atLeastOnePointExported = false;
        for (ActivityPoint point : trackPoints) {
            atLeastOnePointExported |= exportTrackPoint(ser, point, source, trackPoints);
        }
        if (atLeastOnePointExported) {
            ser.endTag(NS_GPX_URI, "trkseg");
            ser.endTag(NS_GPX_URI, "trk");
            return;
        }
        throw new ActivityTrackExporter.GPXTrackEmptyException();
    }

    private String getSource(ActivityTrack track) {
        return track.getDevice().getName();
    }

    private boolean exportTrackPoint(XmlSerializer ser, ActivityPoint point, String source, List<ActivityPoint> trackPoints) throws IOException {
        GPSCoordinate location = point.getLocation();
        if (location == null) {
            return false;
        }
        ser.startTag(NS_GPX_URI, "trkpt");
        ser.attribute((String) null, "lon", formatLocation(location.getLongitude()));
        ser.attribute((String) null, "lat", formatLocation(location.getLatitude()));
        ser.startTag(NS_GPX_URI, "ele").text(formatLocation(location.getAltitude())).endTag(NS_GPX_URI, "ele");
        ser.startTag(NS_GPX_URI, "time").text(DateTimeUtils.formatIso8601UTC(point.getTime())).endTag(NS_GPX_URI, "time");
        String description = point.getDescription();
        if (description != null) {
            ser.startTag(NS_GPX_URI, "desc").text(description).endTag(NS_GPX_URI, "desc");
        }
        exportTrackpointExtensions(ser, point, trackPoints);
        ser.endTag(NS_GPX_URI, "trkpt");
        return true;
    }

    private void exportTrackpointExtensions(XmlSerializer ser, ActivityPoint point, List<ActivityPoint> trackPoints) throws IOException {
        ActivityPoint closestPointItem;
        if (this.includeHeartRate) {
            int hr = point.getHeartRate();
            if (!HeartRateUtils.getInstance().isValidHeartRateValue(hr)) {
                if (this.includeHeartRateOfNearestSample && (closestPointItem = findClosestSensibleActivityPoint(point.getTime(), trackPoints)) != null) {
                    hr = closestPointItem.getHeartRate();
                    if (!HeartRateUtils.getInstance().isValidHeartRateValue(hr)) {
                        return;
                    }
                } else {
                    return;
                }
            }
            ser.startTag(NS_GPX_URI, "extensions");
            ser.setPrefix(NS_TRACKPOINT_EXTENSION, NS_TRACKPOINT_EXTENSION_URI);
            ser.startTag(NS_TRACKPOINT_EXTENSION_URI, "TrackPointExtension");
            ser.startTag(NS_TRACKPOINT_EXTENSION_URI, "hr").text(String.valueOf(hr)).endTag(NS_TRACKPOINT_EXTENSION_URI, "hr");
            ser.endTag(NS_TRACKPOINT_EXTENSION_URI, "TrackPointExtension");
            ser.endTag(NS_GPX_URI, "extensions");
        }
    }

    private ActivityPoint findClosestSensibleActivityPoint(Date time, List<ActivityPoint> trackPoints) {
        ActivityPoint closestPointItem = null;
        HeartRateUtils heartRateUtilsInstance = HeartRateUtils.getInstance();
        long lowestDifference = 120000;
        for (ActivityPoint pointItem : trackPoints) {
            if (heartRateUtilsInstance.isValidHeartRateValue(pointItem.getHeartRate())) {
                Date timeItem = pointItem.getTime();
                if (timeItem.after(time) || timeItem.equals(time)) {
                    break;
                }
                long difference = time.getTime() - timeItem.getTime();
                if (difference < lowestDifference) {
                    lowestDifference = difference;
                    closestPointItem = pointItem;
                }
            }
        }
        return closestPointItem;
    }

    private String formatLocation(double value) {
        return new BigDecimal(value).setScale(6, RoundingMode.HALF_UP).toPlainString();
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator2) {
        this.creator = creator2;
    }

    public void setIncludeHeartRate(boolean includeHeartRate2) {
        this.includeHeartRate = includeHeartRate2;
    }

    public boolean isIncludeHeartRate() {
        return this.includeHeartRate;
    }
}
