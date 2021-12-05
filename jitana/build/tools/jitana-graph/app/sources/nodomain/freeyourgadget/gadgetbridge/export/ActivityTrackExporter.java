package nodomain.freeyourgadget.gadgetbridge.export;

import java.io.File;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack;

public interface ActivityTrackExporter {

    public static class GPXTrackEmptyException extends Exception {
    }

    String getDefaultFileName(ActivityTrack activityTrack);

    void performExport(ActivityTrack activityTrack, File file) throws IOException, GPXTrackEmptyException;
}
