package nodomain.freeyourgadget.gadgetbridge.devices;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import java.util.Collection;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public interface DeviceCoordinator {
    public static final int BONDING_STYLE_ASK = 2;
    public static final int BONDING_STYLE_BOND = 1;
    public static final int BONDING_STYLE_NONE = 0;
    public static final int BONDING_STYLE_REQUIRE_KEY = 3;
    public static final String EXTRA_DEVICE_CANDIDATE = "nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate.EXTRA_DEVICE_CANDIDATE";

    boolean allowFetchActivityData(GBDevice gBDevice);

    Collection<? extends ScanFilter> createBLEScanFilters();

    GBDevice createDevice(GBDeviceCandidate gBDeviceCandidate);

    void deleteDevice(GBDevice gBDevice) throws GBException;

    InstallHandler findInstallHandler(Uri uri, Context context);

    int getAlarmSlotCount();

    Class<? extends Activity> getAppsManagementActivity();

    int getBondingStyle();

    int[] getColorPresets();

    DeviceType getDeviceType();

    String getManufacturer();

    Class<? extends Activity> getPairingActivity();

    SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice gBDevice, DaoSession daoSession);

    int[] getSupportedDeviceSpecificSettings(GBDevice gBDevice);

    DeviceType getSupportedType(GBDeviceCandidate gBDeviceCandidate);

    boolean supports(GBDevice gBDevice);

    boolean supports(GBDeviceCandidate gBDeviceCandidate);

    boolean supportsActivityDataFetching();

    boolean supportsActivityTracking();

    boolean supportsActivityTracks();

    boolean supportsAlarmSnoozing();

    boolean supportsAppsManagement();

    boolean supportsCalendarEvents();

    boolean supportsFindDevice();

    boolean supportsHeartRateMeasurement(GBDevice gBDevice);

    boolean supportsLedColor();

    boolean supportsMusicInfo();

    boolean supportsRealtimeData();

    boolean supportsRgbLedColor();

    boolean supportsScreenshots();

    boolean supportsSmartWakeup(GBDevice gBDevice);

    boolean supportsUnicodeEmojis();

    boolean supportsWeather();
}
