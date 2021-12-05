package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.net.Uri;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.serial.AbstractSerialDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceIoThread;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PebbleSupport extends AbstractSerialDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleSupport.class);

    public boolean connect() {
        getDeviceIOThread().start();
        return true;
    }

    /* access modifiers changed from: protected */
    public GBDeviceProtocol createDeviceProtocol() {
        return new PebbleProtocol(getDevice());
    }

    /* access modifiers changed from: protected */
    public GBDeviceIoThread createDeviceIOThread() {
        return new PebbleIoThread(this, getDevice(), getDeviceProtocol(), getBluetoothAdapter(), getContext());
    }

    public boolean useAutoConnect() {
        return true;
    }

    public void onInstallApp(Uri uri) {
        PebbleProtocol pebbleProtocol = (PebbleProtocol) getDeviceProtocol();
        PebbleIoThread pebbleIoThread = getDeviceIOThread();
        if (uri.equals(Uri.parse("fake://health"))) {
            getDeviceIOThread().write(pebbleProtocol.encodeActivateHealth(true));
            if (GBApplication.getPrefs().getString(SettingsActivity.PREF_MEASUREMENT_SYSTEM, getContext().getString(C0889R.string.p_unit_metric)).equals(getContext().getString(C0889R.string.p_unit_metric))) {
                pebbleIoThread.write(pebbleProtocol.encodeSetSaneDistanceUnit(true));
            } else {
                pebbleIoThread.write(pebbleProtocol.encodeSetSaneDistanceUnit(false));
            }
        } else if (uri.equals(Uri.parse("fake://hrm"))) {
            getDeviceIOThread().write(pebbleProtocol.encodeActivateHRM(true));
        } else if (uri.equals(Uri.parse("fake://weather"))) {
            getDeviceIOThread().write(pebbleProtocol.encodeActivateWeather(true));
        } else {
            pebbleIoThread.installApp(uri, 0);
        }
    }

    public void onAppConfiguration(UUID uuid, String config, Integer id) {
        try {
            ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
            JSONObject json = new JSONObject(config);
            Iterator<String> keysIterator = json.keys();
            while (keysIterator.hasNext()) {
                String keyStr = keysIterator.next();
                Object object = json.get(keyStr);
                int i = 0;
                if (object instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) object;
                    byte[] byteArray = new byte[jsonArray.length()];
                    while (i < jsonArray.length()) {
                        byteArray[i] = ((Integer) jsonArray.get(i)).byteValue();
                        i++;
                    }
                    object = byteArray;
                } else if (object instanceof Boolean) {
                    if (((Boolean) object).booleanValue()) {
                        i = 1;
                    }
                    object = Short.valueOf((short) i);
                } else if (object instanceof Double) {
                    object = Integer.valueOf(((Double) object).intValue());
                }
                pairs.add(new Pair(Integer.valueOf(Integer.parseInt(keyStr)), object));
            }
            getDeviceIOThread().write(((PebbleProtocol) getDeviceProtocol()).encodeApplicationMessagePush(48, uuid, pairs, id));
        } catch (JSONException e) {
            LOG.error("Error while parsing JSON", (Throwable) e);
        }
    }

    public void onHeartRateTest() {
    }

    public void onSetConstantVibration(int intensity) {
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
    }

    public synchronized PebbleIoThread getDeviceIOThread() {
        return (PebbleIoThread) super.getDeviceIOThread();
    }

    private boolean reconnect() {
        if (isConnected() || !useAutoConnect() || getDevice().getState() != GBDevice.State.WAITING_FOR_RECONNECT) {
            return true;
        }
        this.gbDeviceIOThread.quit();
        this.gbDeviceIOThread.interrupt();
        this.gbDeviceIOThread = null;
        if (!connect()) {
            return false;
        }
        try {
            Thread.sleep(4000);
            return true;
        } catch (InterruptedException e) {
            return true;
        }
    }

    public void onNotification(NotificationSpec notificationSpec) {
        String currentPrivacyMode = GBApplication.getPrefs().getString("pebble_pref_privacy_mode", getContext().getString(C0889R.string.p_pebble_privacy_mode_off));
        if (getContext().getString(C0889R.string.p_pebble_privacy_mode_complete).equals(currentPrivacyMode)) {
            notificationSpec.body = null;
            notificationSpec.sender = null;
            notificationSpec.subject = null;
            notificationSpec.title = null;
            notificationSpec.phoneNumber = null;
        } else if (getContext().getString(C0889R.string.p_pebble_privacy_mode_content).equals(currentPrivacyMode)) {
            if (notificationSpec.sender != null) {
                notificationSpec.sender = "\n\n\n\n\n" + notificationSpec.sender;
            } else if (notificationSpec.title != null) {
                notificationSpec.title = "\n\n\n\n\n" + notificationSpec.title;
            } else if (notificationSpec.subject != null) {
                notificationSpec.subject = "\n\n\n\n\n" + notificationSpec.subject;
            }
        }
        if (reconnect()) {
            super.onNotification(notificationSpec);
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        if (!reconnect()) {
            return;
        }
        if (callSpec.command != 3 || GBApplication.getPrefs().getBoolean("pebble_enable_outgoing_call", true)) {
            super.onSetCallState(callSpec);
        }
    }

    public void onSetMusicState(MusicStateSpec musicStateSpec) {
        if (reconnect()) {
            super.onSetMusicState(musicStateSpec);
        }
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        if (reconnect()) {
            super.onSetMusicInfo(musicSpec);
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> arrayList) {
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        if (reconnect()) {
            super.onAddCalendarEvent(calendarEventSpec);
        }
    }

    public void onDeleteCalendarEvent(byte type, long id) {
        if (reconnect()) {
            super.onDeleteCalendarEvent(type, id);
        }
    }

    public void onSendConfiguration(String config) {
        if (reconnect()) {
            super.onSendConfiguration(config);
        }
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
        if (reconnect()) {
            super.onTestNewFunction();
        }
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
        if (reconnect()) {
            super.onSendWeather(weatherSpec);
        }
    }
}
