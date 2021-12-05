package nodomain.freeyourgadget.gadgetbridge.devices.qhybrid;

import android.util.Log;
import java.io.Serializable;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest;

public class NotificationConfiguration implements Serializable {
    private String appName;
    private short hour;

    /* renamed from: id */
    private long f132id;
    private short min;
    private String packageName;
    private boolean respectSilentMode;
    private short subEye;
    private PlayNotificationRequest.VibrationType vibration;

    NotificationConfiguration(short min2, short hour2, String packageName2, String appName2, boolean respectSilentMode2, PlayNotificationRequest.VibrationType vibration2) {
        this.subEye = -1;
        this.f132id = -1;
        this.min = min2;
        this.hour = hour2;
        this.packageName = packageName2;
        this.appName = appName2;
        this.respectSilentMode = respectSilentMode2;
        this.vibration = vibration2;
    }

    public NotificationConfiguration(short min2, short hour2, short subEye2, PlayNotificationRequest.VibrationType vibration2) {
        this.subEye = -1;
        this.f132id = -1;
        this.min = min2;
        this.hour = hour2;
        this.subEye = subEye2;
        this.vibration = vibration2;
    }

    public NotificationConfiguration(short min2, short hour2, String packageName2, String appName2, boolean respectSilentMode2, PlayNotificationRequest.VibrationType vibration2, long id) {
        this.subEye = -1;
        this.f132id = -1;
        this.min = min2;
        this.hour = hour2;
        this.packageName = packageName2;
        this.appName = appName2;
        this.respectSilentMode = respectSilentMode2;
        this.vibration = vibration2;
        this.f132id = id;
    }

    NotificationConfiguration(String packageName2, String appName2) {
        this.subEye = -1;
        this.f132id = -1;
        this.min = -1;
        this.hour = -1;
        this.packageName = packageName2;
        this.appName = appName2;
        this.respectSilentMode = false;
        this.vibration = PlayNotificationRequest.VibrationType.SINGLE_NORMAL;
        this.f132id = -1;
    }

    public PlayNotificationRequest.VibrationType getVibration() {
        return this.vibration;
    }

    public void setVibration(PlayNotificationRequest.VibrationType vibration2) {
        this.vibration = vibration2;
    }

    public long getId() {
        return this.f132id;
    }

    public void setId(long id) {
        this.f132id = id;
    }

    public boolean getRespectSilentMode() {
        Log.d("Config", "respect: " + this.respectSilentMode);
        return this.respectSilentMode;
    }

    public void setRespectSilentMode(boolean respectSilentMode2) {
        this.respectSilentMode = respectSilentMode2;
    }

    public void setMin(short min2) {
        this.min = min2;
    }

    public void setHour(short hour2) {
        this.hour = hour2;
    }

    public void setPackageName(String packageName2) {
        this.packageName = packageName2;
    }

    public void setAppName(String appName2) {
        this.appName = appName2;
    }

    public short getMin() {
        return this.min;
    }

    public short getHour() {
        return this.hour;
    }

    public short getSubEye() {
        return this.subEye;
    }

    public void setSubEye(short subEye2) {
        this.subEye = subEye2;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getAppName() {
        return this.appName;
    }
}
