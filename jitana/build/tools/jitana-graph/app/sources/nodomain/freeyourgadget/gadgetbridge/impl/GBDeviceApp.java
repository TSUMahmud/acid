package nodomain.freeyourgadget.gadgetbridge.impl;

import cyanogenmod.app.ProfileManager;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import p008de.cketti.library.changelog.ChangeLog;

public class GBDeviceApp {
    private final boolean configurable;
    private final String creator;
    private final boolean inCache;
    private boolean isOnDevice;
    private final String name;
    private final Type type;
    private final UUID uuid;
    private final String version;

    public enum Type {
        UNKNOWN,
        WATCHFACE,
        WATCHFACE_SYSTEM,
        APP_GENERIC,
        APP_ACTIVITYTRACKER,
        APP_SYSTEM
    }

    public GBDeviceApp(UUID uuid2, String name2, String creator2, String version2, Type type2) {
        this.uuid = uuid2;
        this.name = name2;
        this.creator = creator2;
        this.version = version2;
        this.type = type2;
        this.inCache = false;
        this.configurable = false;
        this.isOnDevice = false;
    }

    public GBDeviceApp(JSONObject json, boolean configurable2) {
        UUID uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000000");
        String name2 = "";
        String creator2 = "";
        String version2 = "";
        Type type2 = Type.UNKNOWN;
        try {
            uuid2 = UUID.fromString(json.getString(ProfileManager.EXTRA_PROFILE_UUID));
            name2 = json.getString("name");
            creator2 = json.getString("creator");
            version2 = json.getString(ChangeLog.ReleaseTag.ATTRIBUTE_VERSION);
            type2 = Type.valueOf(json.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.uuid = uuid2;
        this.name = name2;
        this.creator = creator2;
        this.version = version2;
        this.type = type2;
        this.inCache = true;
        this.configurable = configurable2;
    }

    public void setOnDevice(boolean isOnDevice2) {
        this.isOnDevice = isOnDevice2;
    }

    public boolean isInCache() {
        return this.inCache;
    }

    public boolean isOnDevice() {
        return this.isOnDevice;
    }

    public String getName() {
        return this.name;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getVersion() {
        return this.version;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Type getType() {
        return this.type;
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put(ProfileManager.EXTRA_PROFILE_UUID, this.uuid.toString());
            json.put("name", this.name);
            json.put("creator", this.creator);
            json.put(ChangeLog.ReleaseTag.ATTRIBUTE_VERSION, this.version);
            json.put("type", this.type.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean isConfigurable() {
        return this.configurable;
    }
}
