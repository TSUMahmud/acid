package nodomain.freeyourgadget.gadgetbridge.service.serial;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.AbstractDeviceSupport;

public abstract class AbstractSerialDeviceSupport extends AbstractDeviceSupport {
    protected GBDeviceIoThread gbDeviceIOThread;
    private GBDeviceProtocol gbDeviceProtocol;

    /* access modifiers changed from: protected */
    public abstract GBDeviceIoThread createDeviceIOThread();

    /* access modifiers changed from: protected */
    public abstract GBDeviceProtocol createDeviceProtocol();

    public void dispose() {
        GBDeviceIoThread gBDeviceIoThread = this.gbDeviceIOThread;
        if (gBDeviceIoThread != null) {
            gBDeviceIoThread.quit();
            this.gbDeviceIOThread.interrupt();
            this.gbDeviceIOThread = null;
        }
    }

    /* access modifiers changed from: protected */
    public synchronized GBDeviceProtocol getDeviceProtocol() {
        if (this.gbDeviceProtocol == null) {
            this.gbDeviceProtocol = createDeviceProtocol();
        }
        return this.gbDeviceProtocol;
    }

    public synchronized GBDeviceIoThread getDeviceIOThread() {
        if (this.gbDeviceIOThread == null) {
            this.gbDeviceIOThread = createDeviceIOThread();
        }
        return this.gbDeviceIOThread;
    }

    private void sendToDevice(byte[] bytes) {
        GBDeviceIoThread gBDeviceIoThread;
        if (bytes != null && (gBDeviceIoThread = this.gbDeviceIOThread) != null) {
            gBDeviceIoThread.write(bytes);
        }
    }

    private void handleGBDeviceEvent(GBDeviceEventSendBytes sendBytes) {
        sendToDevice(sendBytes.encodedBytes);
    }

    public void evaluateGBDeviceEvent(GBDeviceEvent deviceEvent) {
        if (deviceEvent instanceof GBDeviceEventSendBytes) {
            handleGBDeviceEvent((GBDeviceEventSendBytes) deviceEvent);
        } else {
            super.evaluateGBDeviceEvent(deviceEvent);
        }
    }

    public void onNotification(NotificationSpec notificationSpec) {
        sendToDevice(this.gbDeviceProtocol.encodeNotification(notificationSpec));
    }

    public void onDeleteNotification(int id) {
        sendToDevice(this.gbDeviceProtocol.encodeDeleteNotification(id));
    }

    public void onSetTime() {
        sendToDevice(this.gbDeviceProtocol.encodeSetTime());
    }

    public void onSetCallState(CallSpec callSpec) {
        sendToDevice(this.gbDeviceProtocol.encodeSetCallState(callSpec.number, callSpec.name, callSpec.command));
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        sendToDevice(this.gbDeviceProtocol.encodeSetCannedMessages(cannedMessagesSpec));
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
        sendToDevice(this.gbDeviceProtocol.encodeSetMusicState(stateSpec.state, stateSpec.position, stateSpec.playRate, stateSpec.shuffle, stateSpec.repeat));
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        sendToDevice(this.gbDeviceProtocol.encodeSetMusicInfo(musicSpec.artist, musicSpec.album, musicSpec.track, musicSpec.duration, musicSpec.trackCount, musicSpec.trackNr));
    }

    public void onAppInfoReq() {
        sendToDevice(this.gbDeviceProtocol.encodeAppInfoReq());
    }

    public void onAppStart(UUID uuid, boolean start) {
        sendToDevice(this.gbDeviceProtocol.encodeAppStart(uuid, start));
    }

    public void onAppDelete(UUID uuid) {
        sendToDevice(this.gbDeviceProtocol.encodeAppDelete(uuid));
    }

    public void onAppReorder(UUID[] uuids) {
        sendToDevice(this.gbDeviceProtocol.encodeAppReorder(uuids));
    }

    public void onFetchRecordedData(int dataTypes) {
        sendToDevice(this.gbDeviceProtocol.encodeSynchronizeActivityData());
    }

    public void onReset(int flags) {
        sendToDevice(this.gbDeviceProtocol.encodeReset(flags));
    }

    public void onFindDevice(boolean start) {
        sendToDevice(this.gbDeviceProtocol.encodeFindDevice(start));
    }

    public void onScreenshotReq() {
        sendToDevice(this.gbDeviceProtocol.encodeScreenshotReq());
    }

    public void onEnableRealtimeSteps(boolean enable) {
        sendToDevice(this.gbDeviceProtocol.encodeEnableRealtimeSteps(enable));
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
        sendToDevice(this.gbDeviceProtocol.encodeEnableHeartRateSleepSupport(enable));
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        sendToDevice(this.gbDeviceProtocol.encodeEnableRealtimeHeartRateMeasurement(enable));
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        sendToDevice(this.gbDeviceProtocol.encodeAddCalendarEvent(calendarEventSpec));
    }

    public void onDeleteCalendarEvent(byte type, long id) {
        sendToDevice(this.gbDeviceProtocol.encodeDeleteCalendarEvent(type, id));
    }

    public void onSendConfiguration(String config) {
        sendToDevice(this.gbDeviceProtocol.encodeSendConfiguration(config));
    }

    public void onTestNewFunction() {
        sendToDevice(this.gbDeviceProtocol.encodeTestNewFunction());
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
        sendToDevice(this.gbDeviceProtocol.encodeSendWeather(weatherSpec));
    }

    public void onSetFmFrequency(float frequency) {
        sendToDevice(this.gbDeviceProtocol.encodeFmFrequency(frequency));
    }

    public void onSetLedColor(int color) {
        sendToDevice(this.gbDeviceProtocol.encodeLedColor(color));
    }
}
