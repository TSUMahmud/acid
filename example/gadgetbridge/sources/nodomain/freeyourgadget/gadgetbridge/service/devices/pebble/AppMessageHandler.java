package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;
import org.json.JSONException;
import org.json.JSONObject;

class AppMessageHandler {
    final PebbleProtocol mPebbleProtocol;
    final UUID mUUID;
    Map<String, Integer> messageKeys;

    AppMessageHandler(UUID uuid, PebbleProtocol pebbleProtocol) {
        this.mUUID = uuid;
        this.mPebbleProtocol = pebbleProtocol;
    }

    public boolean isEnabled() {
        return true;
    }

    public UUID getUUID() {
        return this.mUUID;
    }

    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> arrayList) {
        GBDeviceEventSendBytes sendBytesAck = new GBDeviceEventSendBytes();
        PebbleProtocol pebbleProtocol = this.mPebbleProtocol;
        sendBytesAck.encodedBytes = pebbleProtocol.encodeApplicationMessageAck(this.mUUID, pebbleProtocol.last_id);
        return new GBDeviceEvent[]{sendBytesAck};
    }

    public GBDeviceEvent[] onAppStart() {
        return null;
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return null;
    }

    /* access modifiers changed from: protected */
    public GBDevice getDevice() {
        return this.mPebbleProtocol.getDevice();
    }

    /* access modifiers changed from: package-private */
    public JSONObject getAppKeys() throws IOException, JSONException {
        File destDir = PebbleUtils.getPbwCacheDir();
        File configurationFile = new File(destDir, this.mUUID.toString() + ".json");
        if (configurationFile.exists()) {
            return new JSONObject(FileUtils.getStringFromFile(configurationFile)).getJSONObject("appKeys");
        }
        throw new IOException();
    }
}
