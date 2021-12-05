package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import org.apache.commons.lang3.time.DateUtils;

public class GBAutoFetchReceiver extends BroadcastReceiver {
    private Date lastSync = new Date();

    public void onReceive(Context context, Intent intent) {
        if (DateUtils.addMinutes(this.lastSync, GBApplication.getPrefs().getInt("auto_fetch_interval_limit", 0)).before(new Date())) {
            GBApplication.deviceService().onFetchRecordedData(RecordedDataTypes.TYPE_ACTIVITY);
            this.lastSync = new Date();
        }
    }
}
