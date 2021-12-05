package nodomain.freeyourgadget.gadgetbridge.model;

import java.io.Serializable;
import java.util.Date;

public interface ActivitySummary extends Serializable {
    int getActivityKind();

    long getDeviceId();

    Date getEndTime();

    String getGpxTrack();

    String getName();

    Date getStartTime();

    long getUserId();
}
