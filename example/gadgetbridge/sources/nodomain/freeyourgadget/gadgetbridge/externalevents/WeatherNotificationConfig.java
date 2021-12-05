package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.os.Bundle;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;

public class WeatherNotificationConfig extends AbstractGBActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_weather_notification);
    }
}
