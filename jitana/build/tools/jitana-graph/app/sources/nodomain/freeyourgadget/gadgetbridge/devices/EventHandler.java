package nodomain.freeyourgadget.gadgetbridge.devices;

import android.net.Uri;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

public interface EventHandler {
    void onAddCalendarEvent(CalendarEventSpec calendarEventSpec);

    void onAppConfiguration(UUID uuid, String str, Integer num);

    void onAppDelete(UUID uuid);

    void onAppInfoReq();

    void onAppReorder(UUID[] uuidArr);

    void onAppStart(UUID uuid, boolean z);

    void onDeleteCalendarEvent(byte b, long j);

    void onDeleteNotification(int i);

    void onEnableHeartRateSleepSupport(boolean z);

    void onEnableRealtimeHeartRateMeasurement(boolean z);

    void onEnableRealtimeSteps(boolean z);

    void onFetchRecordedData(int i);

    void onFindDevice(boolean z);

    void onHeartRateTest();

    void onInstallApp(Uri uri);

    void onNotification(NotificationSpec notificationSpec);

    void onReadConfiguration(String str);

    void onReset(int i);

    void onScreenshotReq();

    void onSendConfiguration(String str);

    void onSendWeather(WeatherSpec weatherSpec);

    void onSetAlarms(ArrayList<? extends Alarm> arrayList);

    void onSetCallState(CallSpec callSpec);

    void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec);

    void onSetConstantVibration(int i);

    void onSetFmFrequency(float f);

    void onSetHeartRateMeasurementInterval(int i);

    void onSetLedColor(int i);

    void onSetMusicInfo(MusicSpec musicSpec);

    void onSetMusicState(MusicStateSpec musicStateSpec);

    void onSetTime();

    void onTestNewFunction();
}
