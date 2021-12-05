package nodomain.freeyourgadget.gadgetbridge.service.devices.liveview;

import android.net.Uri;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.service.serial.AbstractSerialDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceIoThread;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;

public class LiveviewSupport extends AbstractSerialDeviceSupport {
    public boolean connect() {
        getDeviceIOThread().start();
        return true;
    }

    /* access modifiers changed from: protected */
    public GBDeviceProtocol createDeviceProtocol() {
        return new LiveviewProtocol(getDevice());
    }

    /* access modifiers changed from: protected */
    public GBDeviceIoThread createDeviceIOThread() {
        return new LiveviewIoThread(getDevice(), getContext(), getDeviceProtocol(), this, getBluetoothAdapter());
    }

    public boolean useAutoConnect() {
        return false;
    }

    public void onInstallApp(Uri uri) {
    }

    public void onAppConfiguration(UUID uuid, String config, Integer id) {
    }

    public void onHeartRateTest() {
    }

    public void onSetConstantVibration(int intensity) {
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
    }

    public synchronized LiveviewIoThread getDeviceIOThread() {
        return (LiveviewIoThread) super.getDeviceIOThread();
    }

    public void onNotification(NotificationSpec notificationSpec) {
        super.onNotification(notificationSpec);
    }

    public void onSetCallState(CallSpec callSpec) {
    }

    public void onSetMusicState(MusicStateSpec musicStateSpec) {
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
    }

    public void onSetAlarms(ArrayList<? extends Alarm> arrayList) {
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    public void onTestNewFunction() {
    }

    public void onReadConfiguration(String config) {
    }
}
