package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Base64;
import android.util.Pair;
import cyanogenmod.providers.DataUsageContract;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppMessage;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventNotificationControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventScreenshot;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.pebble.GBDeviceEventDataLogging;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PebbleProtocol extends GBDeviceProtocol {
    private static final byte APPLICATIONMESSAGE_ACK = -1;
    private static final byte APPLICATIONMESSAGE_NACK = Byte.MAX_VALUE;
    private static final byte APPLICATIONMESSAGE_PUSH = 1;
    private static final byte APPLICATIONMESSAGE_REQUEST = 2;
    private static final byte APPMANAGER_GETAPPBANKSTATUS = 1;
    private static final byte APPMANAGER_GETUUIDS = 5;
    private static final byte APPMANAGER_REFRESHAPP = 3;
    private static final byte APPMANAGER_REMOVEAPP = 2;
    private static final int APPMANAGER_RES_SUCCESS = 1;
    private static final byte APPRUNSTATE_START = 1;
    private static final byte APPRUNSTATE_STOP = 2;
    private static final byte BLOBDB_APP = 2;
    private static final byte BLOBDB_APPGLANCE = 11;
    private static final byte BLOBDB_APPSETTINGS = 9;
    private static final byte BLOBDB_CANNED_MESSAGES = 6;
    private static final byte BLOBDB_CLEAR = 5;
    private static final byte BLOBDB_DATABASEFULL = 7;
    private static final byte BLOBDB_DATASTALE = 8;
    private static final byte BLOBDB_DELETE = 4;
    private static final byte BLOBDB_GENERALFAILURE = 2;
    private static final byte BLOBDB_INSERT = 1;
    private static final byte BLOBDB_INVALIDDATA = 5;
    private static final byte BLOBDB_INVALIDDATABASEID = 4;
    private static final byte BLOBDB_INVALIDOPERATION = 3;
    private static final byte BLOBDB_KEYDOESNOTEXIST = 6;
    private static final byte BLOBDB_NOTIFICATION = 4;
    private static final byte BLOBDB_PIN = 1;
    private static final byte BLOBDB_PREFERENCES = 7;
    private static final byte BLOBDB_REMINDER = 3;
    private static final byte BLOBDB_SUCCESS = 1;
    private static final byte BLOBDB_WEATHER = 5;
    private static final byte DATALOG_ACK = -123;
    private static final byte DATALOG_CLOSE = 3;
    private static final byte DATALOG_NACK = -122;
    private static final byte DATALOG_OPENSESSION = 1;
    private static final byte DATALOG_REPORTSESSIONS = -124;
    private static final byte DATALOG_SENDDATA = 2;
    private static final byte DATALOG_TIMEOUT = 7;
    private static final short ENDPOINT_APP = 2004;
    private static final short ENDPOINT_APPFETCH = 6001;
    static final short ENDPOINT_APPLICATIONMESSAGE = 48;
    private static final short ENDPOINT_APPLOGS = 2006;
    private static final short ENDPOINT_APPMANAGER = 6000;
    private static final short ENDPOINT_APPREORDER = -21555;
    private static final short ENDPOINT_APPRUNSTATE = 52;
    private static final short ENDPOINT_AUDIOSTREAM = 10000;
    private static final short ENDPOINT_BLOBDB = -20005;
    private static final short ENDPOINT_DATALOG = 6778;
    private static final short ENDPOINT_EXTENSIBLENOTIFS = 3010;
    private static final short ENDPOINT_FCTREG = 5001;
    private static final short ENDPOINT_FIRMWAREVERSION = 16;
    private static final short ENDPOINT_LAUNCHER = 49;
    private static final short ENDPOINT_LOGDUMP = 2002;
    private static final short ENDPOINT_LOGS = 2000;
    private static final short ENDPOINT_MUSICCONTROL = 32;
    private static final short ENDPOINT_NOTIFICATION = 3000;
    private static final short ENDPOINT_NOTIFICATIONACTION = 11440;
    private static final short ENDPOINT_PHONECONTROL = 33;
    private static final short ENDPOINT_PHONEVERSION = 17;
    private static final short ENDPOINT_PING = 2001;
    private static final short ENDPOINT_PUTBYTES = -16657;
    private static final short ENDPOINT_RESET = 2003;
    private static final short ENDPOINT_RESOURCE = 4000;
    private static final short ENDPOINT_RUNKEEPER = 7000;
    private static final short ENDPOINT_SCREENSHOT = 8000;
    private static final short ENDPOINT_SYSREG = 5000;
    private static final short ENDPOINT_SYSTEMMESSAGE = 18;
    private static final short ENDPOINT_TIME = 11;
    private static final short ENDPOINT_VOICECONTROL = 11000;
    private static final byte FIRMWAREVERSION_GETVERSION = 0;
    private static final long GB_UUID_MASK = 5145208928029927168L;
    private static final byte LENGTH_UUID = 16;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleProtocol.class);
    private static final byte MUSICCONTROL_GETNOWPLAYING = 8;
    private static final byte MUSICCONTROL_NEXT = 4;
    private static final byte MUSICCONTROL_PAUSE = 2;
    private static final byte MUSICCONTROL_PLAY = 3;
    private static final byte MUSICCONTROL_PLAYPAUSE = 1;
    private static final byte MUSICCONTROL_PREVIOUS = 5;
    private static final byte MUSICCONTROL_SETMUSICINFO = 16;
    private static final byte MUSICCONTROL_SETPLAYSTATE = 17;
    private static final byte MUSICCONTROL_STATE_FASTWORWARDING = 3;
    private static final byte MUSICCONTROL_STATE_PAUSED = 0;
    private static final byte MUSICCONTROL_STATE_PLAYING = 1;
    private static final byte MUSICCONTROL_STATE_REWINDING = 2;
    private static final byte MUSICCONTROL_STATE_UNKNOWN = 4;
    private static final byte MUSICCONTROL_VOLUMEDOWN = 7;
    private static final byte MUSICCONTROL_VOLUMEUP = 6;
    private static final byte NOTIFICATIONACTION_ACK = 0;
    private static final byte NOTIFICATIONACTION_INVOKE = 2;
    private static final byte NOTIFICATIONACTION_NACK = 1;
    private static final byte NOTIFICATIONACTION_RESPONSE = 17;
    private static final byte NOTIFICATION_EMAIL = 0;
    private static final byte NOTIFICATION_FACEBOOK = 3;
    private static final byte NOTIFICATION_SMS = 1;
    private static final byte NOTIFICATION_TWITTER = 2;
    private static final byte PHONECONTROL_ANSWER = 1;
    private static final byte PHONECONTROL_END = 9;
    private static final byte PHONECONTROL_GETSTATE = 3;
    private static final byte PHONECONTROL_HANGUP = 2;
    private static final byte PHONECONTROL_INCOMINGCALL = 4;
    private static final byte PHONECONTROL_MISSEDCALL = 6;
    private static final byte PHONECONTROL_OUTGOINGCALL = 5;
    private static final byte PHONECONTROL_RING = 7;
    private static final byte PHONECONTROL_START = 8;
    private static final byte PHONEVERSION_APPVERSION_MAGIC = 2;
    private static final byte PHONEVERSION_APPVERSION_MAJOR = 2;
    private static final byte PHONEVERSION_APPVERSION_MINOR = 3;
    private static final byte PHONEVERSION_APPVERSION_PATCH = 0;
    private static final int PHONEVERSION_REMOTE_CAPS_ACCEL = 512;
    private static final int PHONEVERSION_REMOTE_CAPS_BTLE = 128;
    private static final int PHONEVERSION_REMOTE_CAPS_COMPASS = 2048;
    private static final int PHONEVERSION_REMOTE_CAPS_GPS = 64;
    private static final int PHONEVERSION_REMOTE_CAPS_GYRO = 1024;
    private static final int PHONEVERSION_REMOTE_CAPS_REARCAMERA = 256;
    private static final int PHONEVERSION_REMOTE_CAPS_SMS = 32;
    private static final int PHONEVERSION_REMOTE_CAPS_TELEPHONY = 16;
    private static final byte PHONEVERSION_REMOTE_OS_ANDROID = 2;
    private static final byte PHONEVERSION_REMOTE_OS_IOS = 1;
    private static final byte PHONEVERSION_REMOTE_OS_LINUX = 4;
    private static final byte PHONEVERSION_REMOTE_OS_OSX = 3;
    private static final byte PHONEVERSION_REMOTE_OS_UNKNOWN = 0;
    private static final byte PHONEVERSION_REMOTE_OS_WINDOWS = 5;
    private static final byte PHONEVERSION_REQUEST = 0;
    private static final int PHONEVERSION_SESSION_CAPS_GAMMARAY = Integer.MIN_VALUE;
    private static final byte PING_PING = 0;
    private static final byte PING_PONG = 1;
    private static final byte PUTBYTES_ABORT = 4;
    private static final byte PUTBYTES_COMMIT = 3;
    private static final byte PUTBYTES_COMPLETE = 5;
    private static final byte PUTBYTES_INIT = 1;
    private static final byte PUTBYTES_SEND = 2;
    public static final byte PUTBYTES_TYPE_BINARY = 5;
    public static final byte PUTBYTES_TYPE_FILE = 6;
    public static final byte PUTBYTES_TYPE_FIRMWARE = 1;
    public static final byte PUTBYTES_TYPE_RECOVERY = 2;
    public static final byte PUTBYTES_TYPE_RESOURCES = 4;
    public static final byte PUTBYTES_TYPE_SYSRESOURCES = 3;
    public static final byte PUTBYTES_TYPE_WORKER = 7;
    private static final byte RESET_REBOOT = 0;
    private static final byte SCREENSHOT_TAKE = 0;
    private static final byte SYSTEMMESSAGE_FIRMWARECOMPLETE = 2;
    private static final byte SYSTEMMESSAGE_FIRMWAREFAIL = 3;
    private static final byte SYSTEMMESSAGE_FIRMWARESTART = 1;
    private static final byte SYSTEMMESSAGE_FIRMWARE_OUTOFDATE = 5;
    private static final byte SYSTEMMESSAGE_FIRMWARE_UPTODATE = 4;
    private static final byte SYSTEMMESSAGE_NEWFIRMWAREAVAILABLE = 0;
    private static final byte SYSTEMMESSAGE_STARTRECONNECTING = 7;
    private static final byte SYSTEMMESSAGE_STOPRECONNECTING = 6;
    private static final byte TIME_GETTIME = 0;
    private static final byte TIME_SETTIME = 2;
    private static final byte TIME_SETTIME_UTC = 3;
    static final byte TYPE_BYTEARRAY = 0;
    private static final byte TYPE_CSTRING = 1;
    static final byte TYPE_INT = 3;
    static final byte TYPE_UINT = 2;
    private static final UUID UUID_GBPEBBLE = UUID.fromString("61476764-7465-7262-6469-656775527a6c");
    private static final UUID UUID_HELTHIFY = UUID.fromString("7ee97b2c-95e8-4720-b94e-70fccd905d98");
    private static final UUID UUID_LOCATION = UUID.fromString("2c7e6a86-51e5-4ddd-b606-db43d1e4ad28");
    private static final UUID UUID_M7S = UUID.fromString("03adc57a-569b-4669-9a80-b505eaea314d");
    private static final UUID UUID_MARIOTIME = UUID.fromString("43caa750-2896-4f46-94dc-1adbd4bc1ff3");
    private static final UUID UUID_MISFIT = UUID.fromString("0b73b76a-cd65-4dc2-9585-aaa213320858");
    private static final UUID UUID_MORPHEUZ = UUID.fromString("5be44f1d-d262-4ea6-aa30-ddbec1e3cab2");
    public static final UUID UUID_NOTIFICATIONS = UUID.fromString("b2cae818-10f8-46df-ad2b-98ad2254a3c1");
    private static final UUID UUID_OBSIDIAN = UUID.fromString("ef42caba-0c65-4879-ab23-edd2bde68824");
    public static final UUID UUID_PEBBLE_HEALTH = UUID.fromString("36d8c6ed-4c83-4fa1-a9e2-8f12dc941f8c");
    private static final UUID UUID_PEBBLE_TIMESTYLE = UUID.fromString("4368ffa4-f0fb-4823-90be-f754b076bdaa");
    private static final UUID UUID_PEBSTYLE = UUID.fromString("da05e84d-e2a2-4020-a2dc-9cdcf265fcdd");
    private static final UUID UUID_REALWEATHER = UUID.fromString("1f0b0701-cc8f-47ec-86e7-7181397f9a52");
    private static final UUID UUID_SIMPLY_LIGHT = UUID.fromString("04a6e68a-42d6-4738-87b2-1c80a994dee4");
    private static final UUID UUID_SQUARE = UUID.fromString("cb332373-4ee5-4c5c-8912-4f62af2d756c");
    private static final UUID UUID_TREKVOLLE = UUID.fromString("2da02267-7a19-4e49-9ed1-439d25db14e4");
    public static final UUID UUID_WEATHER = UUID.fromString("61b22bc8-1e29-460d-a236-3fe409a439ff");
    public static final UUID UUID_WORKOUT = UUID.fromString("fef82c82-7176-4e22-88de-35a3fc18d43f");
    private static final UUID UUID_YWEATHER = UUID.fromString("35a28a4d-0c9f-408f-9c6d-551e65f03186");
    private static final UUID UUID_ZALEWSZCZAK_CROWEX = UUID.fromString("a88b3151-2426-43c6-b1d0-9b288b3ec47e");
    private static final UUID UUID_ZALEWSZCZAK_FANCY = UUID.fromString("014e17bf-5878-4781-8be1-8ef998cee1ba");
    private static final UUID UUID_ZALEWSZCZAK_TALLY = UUID.fromString("abb51965-52e2-440a-b93c-843eeacb697d");
    private static final UUID UUID_ZERO = new UUID(0, 0);
    private static final byte[] clut_pebble = {0, 0, 0, 0, -1, -1, -1, 0};
    private static final byte[] clut_pebbletime = {0, 0, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, 0, -86, 0, 0, 0, -1, 0, 0, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, 0, -86, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, 0, 0, -86, -86, 0, 0, -1, -86, 0, 0, 0, -1, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, 0, 0, -86, -1, 0, 0, -1, -1, 0, 0, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -86, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -1, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -86, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -1, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -86, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -1, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, 0, 0, -86, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -86, 0, -86, 0, -86, 0, -1, 0, -86, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, 0, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, 0, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, 0, 0, -86, -86, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, -86, 0, -86, -86, -86, 0, -1, -86, -86, 0, 0, -1, -86, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, -86, 0, -86, -1, -86, 0, -1, -1, -86, 0, 0, 0, -1, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 0, -1, 0, -86, 0, -1, 0, -1, 0, -1, 0, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, 0, -86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, 0, -1, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, 0, 0, -86, -1, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -86, -1, 0, -86, -86, -1, 0, -1, -86, -1, 0, 0, -1, -1, 0, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, -1, -1, 0, -86, -1, -1, 0, -1, -1, -1, 0};
    private static final String[] hwRevisions = {"silk_bb2", "robert_bb", "silk_bb", "spalding_bb2", "snowy_bb2", "snowy_bb", "bb2", "bb", "unknown", "ev1", "ev2", "ev2_3", "ev2_4", "v1_5", "v2_0", "snowy_evt2", "snowy_dvt", "spalding_dvt", "snowy_s3", "spalding", "silk_evt", "robert_evt", "silk"};
    private static final Random mRandom = new Random();
    private final short LENGTH_PREFIX = 4;
    private UUID currentRunningApp = UUID_ZERO;
    private Integer[] idLookup = new Integer[256];
    byte last_id = -1;
    boolean mAlwaysACKPebbleKit = false;
    private final Map<UUID, AppMessageHandler> mAppMessageHandlers = new HashMap();
    private final HashMap<Byte, DatalogSession> mDatalogSessions = new HashMap<>();
    private GBDeviceEventScreenshot mDevEventScreenshot = null;
    boolean mEnablePebbleKit = false;
    private boolean mForceProtocol = false;
    int mFwMajor = 3;
    private int mScreenshotRemaining = -1;
    private final ArrayList<UUID> tmpUUIDS = new ArrayList<>();

    public PebbleProtocol(GBDevice device) {
        super(device);
        Map<UUID, AppMessageHandler> map = this.mAppMessageHandlers;
        UUID uuid = UUID_MORPHEUZ;
        map.put(uuid, new AppMessageHandlerMorpheuz(uuid, this));
        Map<UUID, AppMessageHandler> map2 = this.mAppMessageHandlers;
        UUID uuid2 = UUID_MISFIT;
        map2.put(uuid2, new AppMessageHandlerMisfit(uuid2, this));
        if (!GBApplication.getGBPrefs().isBackgroundJsEnabled()) {
            Map<UUID, AppMessageHandler> map3 = this.mAppMessageHandlers;
            UUID uuid3 = UUID_PEBBLE_TIMESTYLE;
            map3.put(uuid3, new AppMessageHandlerTimeStylePebble(uuid3, this));
            Map<UUID, AppMessageHandler> map4 = this.mAppMessageHandlers;
            UUID uuid4 = UUID_PEBSTYLE;
            map4.put(uuid4, new AppMessageHandlerPebStyle(uuid4, this));
            Map<UUID, AppMessageHandler> map5 = this.mAppMessageHandlers;
            UUID uuid5 = UUID_MARIOTIME;
            map5.put(uuid5, new AppMessageHandlerMarioTime(uuid5, this));
            Map<UUID, AppMessageHandler> map6 = this.mAppMessageHandlers;
            UUID uuid6 = UUID_HELTHIFY;
            map6.put(uuid6, new AppMessageHandlerHealthify(uuid6, this));
            Map<UUID, AppMessageHandler> map7 = this.mAppMessageHandlers;
            UUID uuid7 = UUID_TREKVOLLE;
            map7.put(uuid7, new AppMessageHandlerTrekVolle(uuid7, this));
            Map<UUID, AppMessageHandler> map8 = this.mAppMessageHandlers;
            UUID uuid8 = UUID_SQUARE;
            map8.put(uuid8, new AppMessageHandlerSquare(uuid8, this));
            Map<UUID, AppMessageHandler> map9 = this.mAppMessageHandlers;
            UUID uuid9 = UUID_ZALEWSZCZAK_CROWEX;
            map9.put(uuid9, new AppMessageHandlerZalewszczak(uuid9, this));
            Map<UUID, AppMessageHandler> map10 = this.mAppMessageHandlers;
            UUID uuid10 = UUID_ZALEWSZCZAK_FANCY;
            map10.put(uuid10, new AppMessageHandlerZalewszczak(uuid10, this));
            Map<UUID, AppMessageHandler> map11 = this.mAppMessageHandlers;
            UUID uuid11 = UUID_ZALEWSZCZAK_TALLY;
            map11.put(uuid11, new AppMessageHandlerZalewszczak(uuid11, this));
            Map<UUID, AppMessageHandler> map12 = this.mAppMessageHandlers;
            UUID uuid12 = UUID_OBSIDIAN;
            map12.put(uuid12, new AppMessageHandlerObsidian(uuid12, this));
            Map<UUID, AppMessageHandler> map13 = this.mAppMessageHandlers;
            UUID uuid13 = UUID_GBPEBBLE;
            map13.put(uuid13, new AppMessageHandlerGBPebble(uuid13, this));
            Map<UUID, AppMessageHandler> map14 = this.mAppMessageHandlers;
            UUID uuid14 = UUID_SIMPLY_LIGHT;
            map14.put(uuid14, new AppMessageHandlerSimplyLight(uuid14, this));
            Map<UUID, AppMessageHandler> map15 = this.mAppMessageHandlers;
            UUID uuid15 = UUID_M7S;
            map15.put(uuid15, new AppMessageHandlerM7S(uuid15, this));
            Map<UUID, AppMessageHandler> map16 = this.mAppMessageHandlers;
            UUID uuid16 = UUID_YWEATHER;
            map16.put(uuid16, new AppMessageHandlerRealWeather(uuid16, this));
            Map<UUID, AppMessageHandler> map17 = this.mAppMessageHandlers;
            UUID uuid17 = UUID_REALWEATHER;
            map17.put(uuid17, new AppMessageHandlerRealWeather(uuid17, this));
        }
    }

    private byte[] encodeSimpleMessage(short endpoint, byte command) {
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(1);
        buf.putShort(endpoint);
        buf.put(command);
        return buf.array();
    }

    private byte[] encodeMessage(short endpoint, byte type, int cookie, String[] parts) {
        int length = 5;
        if (parts != null) {
            int length2 = 5;
            for (String s : parts) {
                if (s == null || s.equals("")) {
                    length2++;
                } else {
                    length2 += s.getBytes().length + 1;
                }
            }
            length = length2;
        }
        if (endpoint == 33) {
            length += 4;
        }
        ByteBuffer buf = ByteBuffer.allocate(length);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) (length - 4));
        buf.putShort(endpoint);
        buf.put(type);
        if (endpoint == 33) {
            buf.putInt(cookie);
        }
        if (parts != null) {
            for (String s2 : parts) {
                if (s2 == null || s2.equals("")) {
                    buf.put((byte) 0);
                } else {
                    int partlength = s2.getBytes().length;
                    if (partlength > 255) {
                        partlength = 255;
                    }
                    buf.put((byte) partlength);
                    buf.put(s2.getBytes(), 0, partlength);
                }
            }
        }
        return buf.array();
    }

    public byte[] encodeNotification(NotificationSpec notificationSpec) {
        String subtitle;
        String subtitle2;
        int id = notificationSpec.getId() != -1 ? notificationSpec.getId() : mRandom.nextInt();
        if (notificationSpec.sender != null) {
            String title = notificationSpec.sender;
            subtitle = notificationSpec.subject;
            subtitle2 = title;
        } else {
            subtitle = null;
            subtitle2 = notificationSpec.title;
        }
        long ts = System.currentTimeMillis();
        if (this.mFwMajor < 3) {
            ts += (long) SimpleTimeZone.getDefault().getOffset(ts);
        }
        long ts2 = ts / 1000;
        if (this.mFwMajor >= 3 || this.mForceProtocol || notificationSpec.type != NotificationType.GENERIC_EMAIL) {
            return encodeNotification(id, (int) (4294967295L & ts2), subtitle2, subtitle, notificationSpec.body, notificationSpec.type, notificationSpec.pebbleColor, notificationSpec.cannedReplies, notificationSpec.attachedActions);
        }
        return encodeMessage(ENDPOINT_NOTIFICATION, (byte) 0, 0, new String[]{subtitle2, notificationSpec.body, String.valueOf(ts2), subtitle});
    }

    public byte[] encodeDeleteNotification(int id) {
        return encodeBlobdb(new UUID(GB_UUID_MASK, (long) id), (byte) 4, (byte) 4, (byte[]) null);
    }

    public byte[] encodeAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        int iconId;
        long id = calendarEventSpec.f156id != -1 ? calendarEventSpec.f156id : mRandom.nextLong();
        ArrayList<Pair<Integer, Object>> attributes = new ArrayList<>();
        attributes.add(new Pair(1, calendarEventSpec.title));
        byte b = calendarEventSpec.type;
        if (b == 1) {
            iconId = 84;
        } else if (b != 2) {
            attributes.add(new Pair(3, calendarEventSpec.description));
            attributes.add(new Pair(11, calendarEventSpec.location));
            iconId = 21;
        } else {
            iconId = 85;
        }
        return encodeTimelinePin(new UUID(GB_UUID_MASK | ((long) calendarEventSpec.type), id), calendarEventSpec.timestamp, (short) (calendarEventSpec.durationInSeconds / 60), iconId, attributes);
    }

    public byte[] encodeDeleteCalendarEvent(byte type, long id) {
        return encodeBlobdb(new UUID(((long) type) | GB_UUID_MASK, id), (byte) 4, (byte) 1, (byte[]) null);
    }

    public byte[] encodeSetTime() {
        ByteBuffer buf;
        long ts = System.currentTimeMillis();
        long ts_offset = (long) SimpleTimeZone.getDefault().getOffset(ts);
        if (this.mFwMajor >= 3) {
            String timezone = SimpleTimeZone.getDefault().getID();
            short length = (short) (timezone.getBytes().length + 5 + 3);
            buf = ByteBuffer.allocate(length + 4);
            buf.order(ByteOrder.BIG_ENDIAN);
            buf.putShort(length);
            buf.putShort(ENDPOINT_TIME);
            buf.put((byte) 3);
            buf.putInt((int) (ts / 1000));
            buf.putShort((short) ((int) (ts_offset / 60000)));
            buf.put((byte) timezone.getBytes().length);
            buf.put(timezone.getBytes());
            LOG.info(timezone);
        } else {
            buf = ByteBuffer.allocate(9);
            buf.order(ByteOrder.BIG_ENDIAN);
            buf.putShort(5);
            buf.putShort(ENDPOINT_TIME);
            buf.put((byte) 2);
            buf.putInt((int) ((ts + ts_offset) / 1000));
        }
        return buf.array();
    }

    public byte[] encodeFindDevice(boolean start) {
        return encodeSetCallState("Where are you?", GBApplication.DATABASE_NAME, start ? 2 : 6);
    }

    private byte[] encodeBlobdb(Object key, byte command, byte db, byte[] blob) {
        int key_length;
        if (key instanceof UUID) {
            key_length = 16;
        } else if ((key instanceof String) != 0) {
            key_length = ((String) key).getBytes().length;
        } else {
            LOG.warn("unknown key type");
            return null;
        }
        if (key_length > 255) {
            LOG.warn("key is too long");
            return null;
        }
        int length = 5 + key_length;
        if (blob != null) {
            length += blob.length + 2;
        }
        ByteBuffer buf = ByteBuffer.allocate(length + 4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) length);
        buf.putShort(ENDPOINT_BLOBDB);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(command);
        buf.putShort((short) mRandom.nextInt());
        buf.put(db);
        buf.put((byte) key_length);
        if (key instanceof UUID) {
            UUID uuid = (UUID) key;
            buf.order(ByteOrder.BIG_ENDIAN);
            buf.putLong(uuid.getMostSignificantBits());
            buf.putLong(uuid.getLeastSignificantBits());
            buf.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            buf.put(((String) key).getBytes());
        }
        if (blob != null) {
            buf.putShort((short) blob.length);
            buf.put(blob);
        }
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeActivateHealth(boolean activate) {
        byte[] blob;
        if (activate) {
            ByteBuffer buf = ByteBuffer.allocate(9);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            ActivityUser activityUser = new ActivityUser();
            buf.putShort((short) (activityUser.getHeightCm() * 10));
            buf.putShort((short) (activityUser.getWeightKg() * 100));
            buf.put((byte) 1);
            buf.put((byte) 0);
            buf.put((byte) 0);
            buf.put((byte) activityUser.getAge());
            buf.put((byte) activityUser.getGender());
            blob = buf.array();
        } else {
            blob = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        }
        return encodeBlobdb("activityPreferences", (byte) 1, (byte) 7, blob);
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeSetSaneDistanceUnit(boolean sane) {
        byte value;
        if (sane) {
            value = 0;
        } else {
            value = 1;
        }
        return encodeBlobdb("unitsDistance", (byte) 1, (byte) 7, new byte[]{value});
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeActivateHRM(boolean activate) {
        byte[] bArr = new byte[1];
        if (activate) {
            bArr[0] = 1;
        } else {
            bArr[0] = 0;
        }
        return encodeBlobdb("hrmPreferences", (byte) 1, (byte) 7, bArr);
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeActivateWeather(boolean activate) {
        if (!activate) {
            return encodeBlobdb("weatherApp", (byte) 4, (byte) 9, (byte[]) null);
        }
        ByteBuffer buf = ByteBuffer.allocate(97);
        buf.put((byte) 1);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putLong(UUID_LOCATION.getMostSignificantBits());
        buf.putLong(UUID_LOCATION.getLeastSignificantBits());
        buf.put(new byte[44]);
        return encodeBlobdb("weatherApp", (byte) 1, (byte) 9, buf.array());
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeReportDataLogSessions() {
        return encodeSimpleMessage(ENDPOINT_DATALOG, (byte) -124);
    }

    private byte[] encodeBlobDBClear(byte database) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(4);
        buf.putShort(ENDPOINT_BLOBDB);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 5);
        buf.putShort((short) mRandom.nextInt());
        buf.put(database);
        return buf.array();
    }

    private byte[] encodeTimelinePin(UUID uuid, int timestamp, short duration, int icon_id, List<Pair<Integer, Object>> attributes) {
        int i = icon_id;
        byte layout_id = 1;
        if (i == 21) {
            layout_id = 2;
        }
        int icon_id2 = i | Integer.MIN_VALUE;
        byte attributes_count = 1;
        int attributes_length = 7;
        for (Pair<Integer, Object> pair : attributes) {
            if (!(pair.first == null || pair.second == null)) {
                attributes_count = (byte) (attributes_count + 1);
                if (pair.second instanceof Integer) {
                    attributes_length += 7;
                } else if (pair.second instanceof Byte) {
                    attributes_length += 4;
                } else if (pair.second instanceof String) {
                    attributes_length += ((String) pair.second).getBytes().length + 3;
                } else if (pair.second instanceof byte[]) {
                    attributes_length += ((byte[]) pair.second).length + 3;
                } else {
                    Logger logger = LOG;
                    logger.warn("unsupported type for timeline attributes: " + pair.second.getClass().toString());
                }
            }
        }
        ByteBuffer buf = ByteBuffer.allocate(attributes_length + 46);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        buf.putLong(0);
        buf.putLong(0);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(timestamp);
        buf.putShort(duration);
        buf.put((byte) 2);
        buf.putShort(1);
        buf.put(layout_id);
        buf.putShort((short) attributes_length);
        buf.put(attributes_count);
        buf.put((byte) 0);
        buf.put((byte) 4);
        buf.putShort(4);
        buf.putInt(icon_id2);
        for (Pair<Integer, Object> pair2 : attributes) {
            if (!(pair2.first == null || pair2.second == null)) {
                buf.put(((Integer) pair2.first).byteValue());
                if (pair2.second instanceof Integer) {
                    buf.putShort(4);
                    buf.putInt(((Integer) pair2.second).intValue());
                } else if (pair2.second instanceof Byte) {
                    buf.putShort(1);
                    buf.put(((Byte) pair2.second).byteValue());
                } else if (pair2.second instanceof String) {
                    buf.putShort((short) ((String) pair2.second).getBytes().length);
                    buf.put(((String) pair2.second).getBytes());
                } else if (pair2.second instanceof byte[]) {
                    buf.putShort((short) ((byte[]) pair2.second).length);
                    buf.put((byte[]) pair2.second);
                }
            }
        }
        return encodeBlobdb(uuid, (byte) 1, (byte) 1, buf.array());
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0276  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02a7  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x025c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] encodeNotification(int r28, int r29, java.lang.String r30, java.lang.String r31, java.lang.String r32, nodomain.freeyourgadget.gadgetbridge.model.NotificationType r33, byte r34, java.lang.String[] r35, java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec.Action> r36) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            r2 = r29
            r3 = r35
            r4 = r36
            r5 = 46
            r6 = 6
            r7 = 3
            java.lang.String[] r8 = new java.lang.String[r7]
            r9 = 0
            r8[r9] = r30
            r10 = 1
            r8[r10] = r31
            r11 = 2
            r8[r11] = r32
            if (r33 != 0) goto L_0x001e
            nodomain.freeyourgadget.gadgetbridge.model.NotificationType r12 = nodomain.freeyourgadget.gadgetbridge.model.NotificationType.UNKNOWN
            goto L_0x0020
        L_0x001e:
            r12 = r33
        L_0x0020:
            int r13 = r12.icon
            r14 = 0
            r15 = 0
            r16 = 0
            if (r3 == 0) goto L_0x0042
            int r9 = r3.length
            if (r9 <= 0) goto L_0x0042
            int r9 = r3.length
            r7 = 0
        L_0x002d:
            if (r7 >= r9) goto L_0x003d
            r19 = r3[r7]
            byte[] r11 = r19.getBytes()
            int r11 = r11.length
            int r11 = r11 + r10
            int r16 = r16 + r11
            int r7 = r7 + 1
            r11 = 2
            goto L_0x002d
        L_0x003d:
            int r16 = r16 + -1
            r7 = r16
            goto L_0x0044
        L_0x0042:
            r7 = r16
        L_0x0044:
            if (r4 == 0) goto L_0x008a
            int r11 = r36.size()
            if (r11 <= 0) goto L_0x008a
            java.util.Iterator r11 = r36.iterator()
        L_0x0050:
            boolean r16 = r11.hasNext()
            if (r16 == 0) goto L_0x0087
            java.lang.Object r16 = r11.next()
            r10 = r16
            nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec$Action r10 = (nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec.Action) r10
            int r14 = r14 + 1
            java.lang.String r9 = r10.title
            byte[] r9 = r9.getBytes()
            int r9 = r9.length
            r16 = 6
            int r9 = r9 + 6
            short r9 = (short) r9
            int r9 = r9 + r15
            short r9 = (short) r9
            int r15 = r10.type
            r16 = r5
            r5 = 1
            if (r15 == r5) goto L_0x007d
            int r5 = r10.type
            r15 = 2
            if (r5 != r15) goto L_0x007b
            goto L_0x007d
        L_0x007b:
            r15 = r9
            goto L_0x0083
        L_0x007d:
            short r5 = (short) r7
            r15 = 3
            int r5 = r5 + r15
            int r5 = r5 + r9
            short r5 = (short) r5
            r15 = r5
        L_0x0083:
            r5 = r16
            r10 = 1
            goto L_0x0050
        L_0x0087:
            r16 = r5
            goto L_0x008c
        L_0x008a:
            r16 = r5
        L_0x008c:
            r5 = 0
            r9 = r15
            int r10 = r0.mFwMajor
            r11 = 3
            if (r10 < r11) goto L_0x0099
            int r10 = r5 + 2
            byte r5 = (byte) r10
            int r10 = r9 + 11
            short r9 = (short) r10
        L_0x0099:
            int r10 = r8.length
            r11 = r5
            r5 = 0
        L_0x009c:
            r20 = r6
            java.lang.String r6 = ""
            if (r5 >= r10) goto L_0x00c6
            r21 = r10
            r10 = r8[r5]
            if (r10 == 0) goto L_0x00bf
            boolean r6 = r10.equals(r6)
            if (r6 == 0) goto L_0x00af
            goto L_0x00bf
        L_0x00af:
            int r6 = r11 + 1
            byte r6 = (byte) r6
            byte[] r11 = r10.getBytes()
            int r11 = r11.length
            r18 = 3
            int r11 = r11 + 3
            short r11 = (short) r11
            int r11 = r11 + r9
            short r9 = (short) r11
            r11 = r6
        L_0x00bf:
            int r5 = r5 + 1
            r6 = r20
            r10 = r21
            goto L_0x009c
        L_0x00c6:
            int r5 = r0.mFwMajor
            r10 = 3
            if (r5 < r10) goto L_0x00e4
            int r5 = r9 + 46
            short r5 = (short) r5
            r10 = 512(0x200, float:7.175E-43)
            r21 = 2
            java.nio.ByteBuffer r22 = java.nio.ByteBuffer.allocate(r5)
            r26 = r10
            r10 = r5
            r5 = r22
            r22 = r15
            r15 = r21
            r21 = r12
            r12 = r26
            goto L_0x00fe
        L_0x00e4:
            int r5 = r9 + 21
            short r5 = (short) r5
            r10 = 256(0x100, float:3.59E-43)
            r21 = 4
            int r22 = r5 + 4
            java.nio.ByteBuffer r22 = java.nio.ByteBuffer.allocate(r22)
            r26 = r10
            r10 = r5
            r5 = r22
            r22 = r15
            r15 = r21
            r21 = r12
            r12 = r26
        L_0x00fe:
            java.nio.ByteOrder r3 = java.nio.ByteOrder.BIG_ENDIAN
            r5.order(r3)
            int r3 = r0.mFwMajor
            r23 = r7
            r7 = 3
            if (r3 < r7) goto L_0x0143
            r3 = 5145208928029927168(0x4767744272646700, double:9.742468137737654E35)
            r5.putLong(r3)
            long r3 = (long) r1
            r5.putLong(r3)
            java.util.UUID r3 = UUID_NOTIFICATIONS
            long r3 = r3.getMostSignificantBits()
            r5.putLong(r3)
            java.util.UUID r3 = UUID_NOTIFICATIONS
            long r3 = r3.getLeastSignificantBits()
            r5.putLong(r3)
            java.nio.ByteOrder r3 = java.nio.ByteOrder.LITTLE_ENDIAN
            r5.order(r3)
            r5.putInt(r2)
            r3 = 0
            r5.putShort(r3)
            r3 = 1
            r5.put(r3)
            r5.putShort(r3)
            r3 = 4
            r5.put(r3)
            r5.putShort(r9)
            goto L_0x0167
        L_0x0143:
            r5.putShort(r10)
            r3 = 3010(0xbc2, float:4.218E-42)
            r5.putShort(r3)
            java.nio.ByteOrder r3 = java.nio.ByteOrder.LITTLE_ENDIAN
            r5.order(r3)
            r3 = 0
            r5.put(r3)
            r4 = 1
            r5.put(r4)
            r5.putInt(r3)
            r5.putInt(r1)
            r5.putInt(r3)
            r5.putInt(r2)
            r5.put(r4)
        L_0x0167:
            r5.put(r11)
            byte r3 = (byte) r14
            r5.put(r3)
            r3 = 0
            int r4 = r8.length
            r7 = r3
            r3 = 0
        L_0x0172:
            if (r3 >= r4) goto L_0x01ad
            r1 = r8[r3]
            int r2 = r7 + 1
            byte r7 = (byte) r2
            if (r1 == 0) goto L_0x01a0
            boolean r2 = r1.equals(r6)
            if (r2 == 0) goto L_0x0184
            r24 = r4
            goto L_0x01a4
        L_0x0184:
            byte[] r2 = r1.getBytes()
            int r2 = r2.length
            if (r2 <= r12) goto L_0x018c
            r2 = r12
        L_0x018c:
            r5.put(r7)
            r24 = r4
            short r4 = (short) r2
            r5.putShort(r4)
            byte[] r4 = r1.getBytes()
            r25 = r1
            r1 = 0
            r5.put(r4, r1, r2)
            goto L_0x01a4
        L_0x01a0:
            r25 = r1
            r24 = r4
        L_0x01a4:
            int r3 = r3 + 1
            r1 = r28
            r2 = r29
            r4 = r24
            goto L_0x0172
        L_0x01ad:
            int r1 = r0.mFwMajor
            r2 = 3
            if (r1 < r2) goto L_0x01ce
            r1 = 4
            r5.put(r1)
            r5.putShort(r1)
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 | r13
            r5.putInt(r1)
            r1 = 28
            r5.put(r1)
            r1 = 1
            r5.putShort(r1)
            r1 = r34
            r5.put(r1)
            goto L_0x01d0
        L_0x01ce:
            r1 = r34
        L_0x01d0:
            r2 = r36
            if (r2 == 0) goto L_0x02b5
            int r3 = r36.size()
            if (r3 <= 0) goto L_0x02b5
            r3 = 0
        L_0x01db:
            int r4 = r36.size()
            if (r3 >= r4) goto L_0x02b2
            java.lang.Object r4 = r2.get(r3)
            nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec$Action r4 = (nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec.Action) r4
            int r6 = r4.type
            r1 = 3
            if (r6 == r1) goto L_0x0211
            r1 = 4
            if (r6 == r1) goto L_0x020a
            r1 = 5
            if (r6 == r1) goto L_0x0203
            r1 = 6
            if (r6 == r1) goto L_0x01fd
            int r6 = r3 + 5
            byte r6 = (byte) r6
            r5.put(r6)
            r6 = 2
            goto L_0x0217
        L_0x01fd:
            r6 = 1
            r5.put(r6)
            r6 = 2
            goto L_0x0217
        L_0x0203:
            r1 = 6
            r6 = 4
            r5.put(r6)
            r6 = 2
            goto L_0x0217
        L_0x020a:
            r1 = 6
            r6 = 3
            r5.put(r6)
            r6 = 2
            goto L_0x0217
        L_0x0211:
            r1 = 6
            r6 = 2
            r5.put(r6)
        L_0x0217:
            int r1 = r4.type
            r6 = 1
            if (r1 == r6) goto L_0x0237
            int r1 = r4.type
            r6 = 2
            if (r1 != r6) goto L_0x0224
            r1 = 2
            r6 = 1
            goto L_0x0238
        L_0x0224:
            int r1 = r4.type
            r6 = 3
            if (r1 != r6) goto L_0x022e
            r5.put(r15)
            r1 = 2
            goto L_0x0232
        L_0x022e:
            r1 = 2
            r5.put(r1)
        L_0x0232:
            r6 = 1
            r5.put(r6)
            goto L_0x023f
        L_0x0237:
            r1 = 2
        L_0x0238:
            r6 = 3
            r5.put(r6)
            r5.put(r1)
        L_0x023f:
            r1 = 1
            r5.put(r1)
            java.lang.String r1 = r4.title
            byte[] r1 = r1.getBytes()
            int r1 = r1.length
            short r1 = (short) r1
            r5.putShort(r1)
            java.lang.String r1 = r4.title
            byte[] r1 = r1.getBytes()
            r5.put(r1)
            int r1 = r4.type
            r6 = 1
            if (r1 == r6) goto L_0x0266
            int r1 = r4.type
            r6 = 2
            if (r1 != r6) goto L_0x0262
            goto L_0x0267
        L_0x0262:
            r6 = r35
            r2 = 0
            goto L_0x02aa
        L_0x0266:
            r6 = 2
        L_0x0267:
            r1 = 8
            r5.put(r1)
            r1 = r23
            short r6 = (short) r1
            r5.putShort(r6)
            r6 = r35
            if (r6 == 0) goto L_0x02a7
            r23 = r1
            int r1 = r6.length
            if (r1 <= 0) goto L_0x02a5
            r1 = 0
        L_0x027c:
            int r2 = r6.length
            r19 = 1
            int r2 = r2 + -1
            if (r1 >= r2) goto L_0x0295
            r2 = r6[r1]
            byte[] r2 = r2.getBytes()
            r5.put(r2)
            r2 = 0
            r5.put(r2)
            int r1 = r1 + 1
            r2 = r36
            goto L_0x027c
        L_0x0295:
            r2 = 0
            int r1 = r6.length
            r17 = 1
            int r1 = r1 + -1
            r1 = r6[r1]
            byte[] r1 = r1.getBytes()
            r5.put(r1)
            goto L_0x02aa
        L_0x02a5:
            r2 = 0
            goto L_0x02aa
        L_0x02a7:
            r23 = r1
            r2 = 0
        L_0x02aa:
            int r3 = r3 + 1
            r1 = r34
            r2 = r36
            goto L_0x01db
        L_0x02b2:
            r6 = r35
            goto L_0x02b7
        L_0x02b5:
            r6 = r35
        L_0x02b7:
            int r1 = r0.mFwMajor
            r2 = 3
            if (r1 < r2) goto L_0x02cb
            java.util.UUID r1 = java.util.UUID.randomUUID()
            byte[] r2 = r5.array()
            r3 = 4
            r4 = 1
            byte[] r1 = r0.encodeBlobdb(r1, r4, r3, r2)
            return r1
        L_0x02cb:
            byte[] r1 = r5.array()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.encodeNotification(int, int, java.lang.String, java.lang.String, java.lang.String, nodomain.freeyourgadget.gadgetbridge.model.NotificationType, byte, java.lang.String[], java.util.ArrayList):byte[]");
    }

    private byte[] encodeActionResponse2x(int id, byte actionId, int iconId, String caption) {
        short length = (short) (caption.getBytes().length + 18);
        ByteBuffer buf = ByteBuffer.allocate(length + 4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(length);
        buf.putShort(ENDPOINT_EXTENSIBLENOTIFS);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 17);
        buf.putInt(id);
        buf.put(actionId);
        buf.put((byte) 0);
        buf.put((byte) 2);
        buf.put((byte) 6);
        buf.putShort(4);
        buf.putInt(iconId);
        buf.put((byte) 2);
        buf.putShort((short) caption.getBytes().length);
        buf.put(caption.getBytes());
        return buf.array();
    }

    private byte[] encodeWeatherPin(int timestamp, String title, String subtitle, String body, String location, int iconId) {
        String str;
        String str2;
        short ACTION_LENGTH_MIN;
        int i = timestamp;
        short NOTIFICATION_PIN_LENGTH = 46;
        short ACTION_LENGTH_MIN2 = 6;
        String[] parts = {title, subtitle, body, location, "test", "test"};
        short attributes_length = (short) (((short) ((1 * 6) + "Remove".getBytes().length)) + 21);
        int length = parts.length;
        byte attributes_count = 3;
        int i2 = 0;
        while (true) {
            str = "";
            if (i2 >= length) {
                break;
            }
            String s = parts[i2];
            if (s != null && !s.equals(str)) {
                attributes_length = (short) (((short) (s.getBytes().length + 3)) + attributes_length);
                attributes_count = (byte) (attributes_count + 1);
            }
            i2++;
        }
        UUID uuid = UUID.fromString("61b22bc8-1e29-460d-a236-3fe409a43901");
        short pin_length = (short) (attributes_length + 46);
        ByteBuffer buf = ByteBuffer.allocate(pin_length);
        buf.order(ByteOrder.BIG_ENDIAN);
        short s2 = pin_length;
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits() | 255);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(i);
        buf.putShort(0);
        buf.put((byte) 2);
        buf.putShort(1);
        buf.put((byte) 6);
        buf.putShort(attributes_length);
        buf.put(attributes_count);
        buf.put((byte) 1);
        int length2 = parts.length;
        byte attribute_id = 0;
        int i3 = 0;
        while (i3 < length2) {
            String s3 = parts[i3];
            short NOTIFICATION_PIN_LENGTH2 = NOTIFICATION_PIN_LENGTH;
            attribute_id = (byte) (attribute_id + 1);
            if (s3 == null) {
                ACTION_LENGTH_MIN = ACTION_LENGTH_MIN2;
                str2 = str;
            } else if (s3.equals(str)) {
                ACTION_LENGTH_MIN = ACTION_LENGTH_MIN2;
                str2 = str;
            } else {
                int partlength = s3.getBytes().length;
                ACTION_LENGTH_MIN = ACTION_LENGTH_MIN2;
                if (partlength > 512) {
                    partlength = 512;
                }
                if (attribute_id == 4) {
                    buf.put((byte) 11);
                } else if (attribute_id == 5) {
                    buf.put((byte) 25);
                } else if (attribute_id == 6) {
                    buf.put((byte) 26);
                } else {
                    buf.put(attribute_id);
                }
                buf.putShort((short) partlength);
                str2 = str;
                buf.put(s3.getBytes(), 0, partlength);
            }
            i3++;
            NOTIFICATION_PIN_LENGTH = NOTIFICATION_PIN_LENGTH2;
            ACTION_LENGTH_MIN2 = ACTION_LENGTH_MIN;
            str = str2;
        }
        short s4 = ACTION_LENGTH_MIN2;
        buf.put((byte) 4);
        buf.putShort(4);
        buf.putInt(iconId | Integer.MIN_VALUE);
        buf.put((byte) 6);
        buf.putShort(4);
        buf.putInt(iconId | Integer.MIN_VALUE);
        buf.put((byte) 14);
        buf.putShort(4);
        buf.putInt(i);
        buf.put(HPlusConstants.INCOMING_CALL_STATE_DISABLED_THRESHOLD);
        buf.put((byte) 9);
        buf.put((byte) 1);
        buf.put((byte) 1);
        buf.putShort((short) "Remove".getBytes().length);
        buf.put("Remove".getBytes());
        return encodeBlobdb(uuid, (byte) 1, (byte) 1, buf.array());
    }

    public byte[] encodeSendWeather(WeatherSpec weatherSpec) {
        byte[] forecastProtocol = null;
        byte[] watchfaceProtocol = null;
        int length = 0;
        if (this.mFwMajor >= 4) {
            forecastProtocol = encodeWeatherForecast(weatherSpec);
            length = 0 + forecastProtocol.length;
        }
        AppMessageHandler handler = this.mAppMessageHandlers.get(this.currentRunningApp);
        if (!(handler == null || (watchfaceProtocol = handler.encodeUpdateWeather(weatherSpec)) == null)) {
            length += watchfaceProtocol.length;
        }
        ByteBuffer buf = ByteBuffer.allocate(length);
        if (forecastProtocol != null) {
            buf.put(forecastProtocol);
        }
        if (watchfaceProtocol != null) {
            buf.put(watchfaceProtocol);
        }
        return buf.array();
    }

    private byte[] encodeWeatherForecast(WeatherSpec weatherSpec) {
        short todayMax;
        short currentTemp;
        WeatherSpec weatherSpec2 = weatherSpec;
        short currentTemp2 = (short) (weatherSpec2.currentTemp - 273);
        short todayMax2 = (short) (weatherSpec2.todayMaxTemp - 273);
        short todayMin = (short) (weatherSpec2.todayMinTemp - 273);
        short tomorrowMax = 0;
        short tomorrowMin = 0;
        int tomorrowConditionCode = 0;
        if (weatherSpec2.forecasts.size() > 0) {
            WeatherSpec.Forecast tomorrow = weatherSpec2.forecasts.get(0);
            tomorrowMax = (short) (tomorrow.maxTemp - 273);
            tomorrowMin = (short) (tomorrow.minTemp - 273);
            tomorrowConditionCode = tomorrow.conditionCode;
        }
        if (GBApplication.getPrefs().getString(SettingsActivity.PREF_MEASUREMENT_SYSTEM, GBApplication.getContext().getString(C0889R.string.p_unit_metric)).equals(GBApplication.getContext().getString(C0889R.string.p_unit_imperial))) {
            currentTemp2 = (short) ((int) ((((float) currentTemp2) * 1.8f) + 32.0f));
            todayMax2 = (short) ((int) ((((float) todayMax2) * 1.8f) + 32.0f));
            todayMin = (short) ((int) ((((float) todayMin) * 1.8f) + 32.0f));
            tomorrowMax = (short) ((int) ((((float) tomorrowMax) * 1.8f) + 32.0f));
            tomorrowMin = (short) ((int) ((((float) tomorrowMin) * 1.8f) + 32.0f));
        }
        String[] parts = {weatherSpec2.location, weatherSpec2.currentCondition};
        short attributes_length = 0;
        for (String s : parts) {
            if (s != null && !s.equals("")) {
                attributes_length = (short) (((short) (s.getBytes().length + 2)) + attributes_length);
            }
        }
        ByteBuffer buf = ByteBuffer.allocate((short) (attributes_length + 20));
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 3);
        buf.putShort(currentTemp2);
        buf.put(Weather.mapToPebbleCondition(weatherSpec2.currentConditionCode));
        buf.putShort(todayMax2);
        buf.putShort(todayMin);
        buf.put(Weather.mapToPebbleCondition(tomorrowConditionCode));
        buf.putShort(tomorrowMax);
        buf.putShort(tomorrowMin);
        buf.putInt(weatherSpec2.timestamp);
        buf.put((byte) 0);
        buf.putShort(attributes_length);
        int length = parts.length;
        int i = 0;
        while (i < length) {
            String s2 = parts[i];
            if (s2 == null) {
                currentTemp = currentTemp2;
                todayMax = todayMax2;
            } else if (s2.equals("")) {
                currentTemp = currentTemp2;
                todayMax = todayMax2;
            } else {
                currentTemp = currentTemp2;
                int partlength = s2.getBytes().length;
                todayMax = todayMax2;
                if (partlength > 512) {
                    partlength = 512;
                }
                buf.putShort((short) partlength);
                String str = s2;
                buf.put(s2.getBytes(), 0, partlength);
            }
            i++;
            WeatherSpec weatherSpec3 = weatherSpec;
            currentTemp2 = currentTemp;
            todayMax2 = todayMax;
        }
        short s3 = todayMax2;
        return encodeBlobdb(UUID_LOCATION, (byte) 1, (byte) 5, buf.array());
    }

    private byte[] encodeActionResponse(UUID uuid, int iconId, String caption) {
        short length = (short) (caption.getBytes().length + 29);
        ByteBuffer buf = ByteBuffer.allocate(length + 4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(length);
        buf.putShort(ENDPOINT_NOTIFICATIONACTION);
        buf.put((byte) 17);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 0);
        buf.put((byte) 2);
        buf.put((byte) 6);
        buf.putShort(4);
        buf.putInt(Integer.MIN_VALUE | iconId);
        buf.put((byte) 2);
        buf.putShort((short) caption.getBytes().length);
        buf.put(caption.getBytes());
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeInstallMetadata(UUID uuid, String appName, short appVersion, short sdkVersion, int flags, int iconId) {
        byte[] name_buf = new byte[96];
        System.arraycopy(appName.getBytes(), 0, name_buf, 0, appName.getBytes().length);
        ByteBuffer buf = ByteBuffer.allocate(126);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(flags);
        buf.putInt(iconId);
        buf.putShort(appVersion);
        buf.putShort(sdkVersion);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put(name_buf);
        return encodeBlobdb(uuid, (byte) 1, (byte) 2, buf.array());
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeAppFetchAck() {
        ByteBuffer buf = ByteBuffer.allocate(6);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(2);
        buf.putShort(ENDPOINT_APPFETCH);
        buf.put((byte) 1);
        buf.put((byte) 1);
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeGetTime() {
        return encodeSimpleMessage(ENDPOINT_TIME, (byte) 0);
    }

    public byte[] encodeSetCallState(String number, String name, int command) {
        byte pebbleCmd;
        String[] parts = {number, name};
        if (command == 2) {
            pebbleCmd = 4;
        } else if (command == 3) {
            byte[] callmsg = encodeMessage(ENDPOINT_PHONECONTROL, (byte) 4, 0, parts);
            byte[] startmsg = encodeMessage(ENDPOINT_PHONECONTROL, (byte) 8, 0, parts);
            byte[] msg = new byte[(callmsg.length + startmsg.length)];
            System.arraycopy(callmsg, 0, msg, 0, callmsg.length);
            System.arraycopy(startmsg, 0, msg, startmsg.length, startmsg.length);
            return msg;
        } else if (command == 5) {
            pebbleCmd = 8;
        } else if (command != 6) {
            return null;
        } else {
            pebbleCmd = 9;
        }
        return encodeMessage(ENDPOINT_PHONECONTROL, pebbleCmd, 0, parts);
    }

    public byte[] encodeSetMusicState(byte state, int position, int playRate, byte shuffle, byte repeat) {
        byte playState;
        if (this.mFwMajor < 3) {
            return null;
        }
        if (state == 0) {
            playState = 1;
        } else if (state != 1) {
            playState = 4;
        } else {
            playState = 0;
        }
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) (16 - 4));
        buf.putShort(ENDPOINT_MUSICCONTROL);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 17);
        buf.put(playState);
        buf.putInt(position * 1000);
        buf.putInt(playRate);
        buf.put(shuffle);
        buf.put(repeat);
        return buf.array();
    }

    public byte[] encodeSetMusicInfo(String artist, String album, String track, int duration, int trackCount, int trackNr) {
        int i = duration;
        String[] parts = {artist, album, track};
        if (i == 0 || this.mFwMajor < 3) {
            return encodeMessage(ENDPOINT_MUSICCONTROL, (byte) 16, 0, parts);
        }
        int length = 13;
        for (String s : parts) {
            if (s == null || s.equals("")) {
                length++;
            } else {
                length += s.getBytes().length + 1;
            }
        }
        ByteBuffer buf = ByteBuffer.allocate(length);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) (length - 4));
        buf.putShort(ENDPOINT_MUSICCONTROL);
        buf.put((byte) 16);
        for (String s2 : parts) {
            if (s2 == null || s2.equals("")) {
                buf.put((byte) 0);
            } else {
                int partlength = s2.getBytes().length;
                if (partlength > 255) {
                    partlength = 255;
                }
                buf.put((byte) partlength);
                buf.put(s2.getBytes(), 0, partlength);
            }
        }
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(i * 1000);
        buf.putShort((short) (trackCount & 65535));
        buf.putShort((short) (trackNr & 65535));
        return buf.array();
    }

    public byte[] encodeFirmwareVersionReq() {
        return encodeSimpleMessage(ENDPOINT_FIRMWAREVERSION, (byte) 0);
    }

    public byte[] encodeAppInfoReq() {
        if (this.mFwMajor >= 3) {
            return null;
        }
        return encodeSimpleMessage(ENDPOINT_APPMANAGER, (byte) 5);
    }

    public byte[] encodeAppStart(UUID uuid, boolean start) {
        byte b = 1;
        if (this.mFwMajor >= 3) {
            ByteBuffer buf = ByteBuffer.allocate(21);
            buf.order(ByteOrder.BIG_ENDIAN);
            buf.putShort(ENDPOINT_PHONEVERSION);
            buf.putShort(ENDPOINT_APPRUNSTATE);
            if (!start) {
                b = 2;
            }
            buf.put(b);
            buf.putLong(uuid.getMostSignificantBits());
            buf.putLong(uuid.getLeastSignificantBits());
            return buf.array();
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        pairs.add(new Pair(1, Integer.valueOf((int) start)));
        return encodeApplicationMessagePush(ENDPOINT_LAUNCHER, uuid, pairs, (Integer) null);
    }

    public byte[] encodeAppDelete(UUID uuid) {
        if (this.mFwMajor < 3) {
            ByteBuffer buf = ByteBuffer.allocate(21);
            buf.order(ByteOrder.BIG_ENDIAN);
            buf.putShort(ENDPOINT_PHONEVERSION);
            buf.putShort(ENDPOINT_APPMANAGER);
            buf.put((byte) 2);
            buf.putLong(uuid.getMostSignificantBits());
            buf.putLong(uuid.getLeastSignificantBits());
            return buf.array();
        } else if (UUID_PEBBLE_HEALTH.equals(uuid)) {
            return encodeActivateHealth(false);
        } else {
            if (UUID_WORKOUT.equals(uuid)) {
                return encodeActivateHRM(false);
            }
            if (UUID_WEATHER.equals(uuid)) {
                return encodeActivateWeather(false);
            }
            return encodeBlobdb(uuid, (byte) 4, (byte) 2, (byte[]) null);
        }
    }

    private byte[] encodePhoneVersion2x(byte os) {
        ByteBuffer buf = ByteBuffer.allocate(21);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(ENDPOINT_PHONEVERSION);
        buf.putShort(ENDPOINT_PHONEVERSION);
        buf.put((byte) 1);
        buf.putInt(-1);
        if (os == 2) {
            buf.putInt(Integer.MIN_VALUE);
        } else {
            buf.putInt(0);
        }
        buf.putInt(os | ZeTimeConstants.CMD_USER_INFO);
        buf.put((byte) 2);
        buf.put((byte) 2);
        buf.put((byte) 3);
        buf.put((byte) 0);
        return buf.array();
    }

    private byte[] encodePhoneVersion3x(byte os) {
        ByteBuffer buf = ByteBuffer.allocate(29);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(25);
        buf.putShort(ENDPOINT_PHONEVERSION);
        buf.put((byte) 1);
        buf.putInt(-1);
        buf.putInt(0);
        buf.putInt(os);
        buf.put((byte) 2);
        buf.put((byte) 4);
        buf.put((byte) 1);
        buf.put((byte) 1);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(10671);
        return buf.array();
    }

    private byte[] encodePhoneVersion(byte os) {
        return encodePhoneVersion3x(os);
    }

    public byte[] encodeReset(int flags) {
        return encodeSimpleMessage(ENDPOINT_RESET, (byte) 0);
    }

    public byte[] encodeScreenshotReq() {
        return encodeSimpleMessage(ENDPOINT_SCREENSHOT, (byte) 0);
    }

    public byte[] encodeAppReorder(UUID[] uuids) {
        int length = (uuids.length * 16) + 2;
        ByteBuffer buf = ByteBuffer.allocate(length + 4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) length);
        buf.putShort(ENDPOINT_APPREORDER);
        buf.put((byte) 1);
        buf.put((byte) uuids.length);
        for (UUID uuid : uuids) {
            buf.putLong(uuid.getMostSignificantBits());
            buf.putLong(uuid.getLeastSignificantBits());
        }
        return buf.array();
    }

    public byte[] encodeSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        String blobDBKey;
        if (cannedMessagesSpec.cannedMessages == null || cannedMessagesSpec.cannedMessages.length == 0) {
            return null;
        }
        int i = cannedMessagesSpec.type;
        if (i == 1) {
            blobDBKey = "com.pebble.android.phone";
        } else if (i != 2) {
            return null;
        } else {
            blobDBKey = "com.pebble.sendText";
        }
        int replies_length = -1;
        for (String reply : cannedMessagesSpec.cannedMessages) {
            replies_length += reply.getBytes().length + 1;
        }
        ByteBuffer buf = ByteBuffer.allocate(replies_length + 12);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(0);
        buf.put((byte) 0);
        buf.put((byte) 1);
        buf.put((byte) 0);
        buf.put((byte) 3);
        buf.put((byte) 1);
        buf.put((byte) 8);
        buf.putShort((short) replies_length);
        for (int i2 = 0; i2 < cannedMessagesSpec.cannedMessages.length - 1; i2++) {
            buf.put(cannedMessagesSpec.cannedMessages[i2].getBytes());
            buf.put((byte) 0);
        }
        buf.put(cannedMessagesSpec.cannedMessages[cannedMessagesSpec.cannedMessages.length - 1].getBytes());
        return encodeBlobdb(blobDBKey, (byte) 1, (byte) 6, buf.array());
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeUploadStart(byte type, int app_id, int size, String filename) {
        short length;
        if (this.mFwMajor < 3 || type == 6) {
            length = 7;
        } else {
            length = 10;
            type = (byte) (type | 128);
        }
        if (type == 6 && filename != null) {
            length = (short) (((short) filename.getBytes().length) + 1 + length);
        }
        ByteBuffer buf = ByteBuffer.allocate(length + 4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(length);
        buf.putShort(ENDPOINT_PUTBYTES);
        buf.put((byte) 1);
        buf.putInt(size);
        buf.put(type);
        if (this.mFwMajor < 3 || type == 6) {
            buf.put((byte) app_id);
        } else {
            buf.putInt(app_id);
        }
        if (type == 6 && filename != null) {
            buf.put(filename.getBytes());
            buf.put((byte) 0);
        }
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeUploadChunk(int token, byte[] buffer, int size) {
        ByteBuffer buf = ByteBuffer.allocate(size + 13);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) (size + 9));
        buf.putShort(ENDPOINT_PUTBYTES);
        buf.put((byte) 2);
        buf.putInt(token);
        buf.putInt(size);
        buf.put(buffer, 0, size);
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeUploadCommit(int token, int crc) {
        ByteBuffer buf = ByteBuffer.allocate(13);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(9);
        buf.putShort(ENDPOINT_PUTBYTES);
        buf.put((byte) 3);
        buf.putInt(token);
        buf.putInt(crc);
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeUploadComplete(int token) {
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(5);
        buf.putShort(ENDPOINT_PUTBYTES);
        buf.put((byte) 5);
        buf.putInt(token);
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeUploadCancel(int token) {
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(5);
        buf.putShort(ENDPOINT_PUTBYTES);
        buf.put((byte) 4);
        buf.putInt(token);
        return buf.array();
    }

    private byte[] encodeSystemMessage(byte systemMessage) {
        ByteBuffer buf = ByteBuffer.allocate(6);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(2);
        buf.putShort(ENDPOINT_SYSTEMMESSAGE);
        buf.put((byte) 0);
        buf.put(systemMessage);
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeInstallFirmwareStart() {
        return encodeSystemMessage((byte) 1);
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeInstallFirmwareComplete() {
        return encodeSystemMessage((byte) 2);
    }

    public byte[] encodeInstallFirmwareError() {
        return encodeSystemMessage((byte) 3);
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeAppRefresh(int index) {
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(5);
        buf.putShort(ENDPOINT_APPMANAGER);
        buf.put((byte) 3);
        buf.putInt(index);
        return buf.array();
    }

    private byte[] encodeDatalog(byte handle, byte reply) {
        ByteBuffer buf = ByteBuffer.allocate(6);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(2);
        buf.putShort(ENDPOINT_DATALOG);
        buf.put(reply);
        buf.put(handle);
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeApplicationMessageAck(UUID uuid, byte id) {
        if (uuid == null) {
            uuid = this.currentRunningApp;
        }
        ByteBuffer buf = ByteBuffer.allocate(22);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(ENDPOINT_SYSTEMMESSAGE);
        buf.putShort(ENDPOINT_APPLICATIONMESSAGE);
        buf.put((byte) -1);
        buf.put(id);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        return buf.array();
    }

    private byte[] encodePing(byte command, int cookie) {
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(5);
        buf.putShort(ENDPOINT_PING);
        buf.put(command);
        buf.putInt(cookie);
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeEnableAppLogs(boolean enable) {
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(1);
        buf.putShort(ENDPOINT_APPLOGS);
        buf.put(enable ? (byte) 1 : 0);
        return buf.array();
    }

    private ArrayList<Pair<Integer, Object>> decodeDict(ByteBuffer buf) {
        ArrayList<Pair<Integer, Object>> dict = new ArrayList<>();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte dictSize = buf.get();
        while (true) {
            byte dictSize2 = (byte) (dictSize - 1);
            if (dictSize <= 0) {
                return dict;
            }
            Integer key = Integer.valueOf(buf.getInt());
            byte type = buf.get();
            int length = buf.getShort();
            if (type == 0 || type == 1) {
                byte[] bytes = new byte[length];
                buf.get(bytes);
                if (type == 0) {
                    dict.add(new Pair(key, bytes));
                } else {
                    dict.add(new Pair(key, new String(bytes)));
                }
            } else if (type == 2 || type == 3) {
                if (length == 1) {
                    dict.add(new Pair(key, Byte.valueOf(buf.get())));
                } else if (length == 2) {
                    dict.add(new Pair(key, Short.valueOf(buf.getShort())));
                } else {
                    dict.add(new Pair(key, Integer.valueOf(buf.getInt())));
                }
            }
            dictSize = dictSize2;
        }
    }

    private GBDeviceEvent[] decodeDictToJSONAppMessage(UUID uuid, ByteBuffer buf) throws JSONException {
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte dictSize = buf.get();
        if (dictSize == 0) {
            LOG.info("dict size is 0, ignoring");
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        while (true) {
            byte dictSize2 = (byte) (dictSize - 1);
            if (dictSize > 0) {
                JSONObject jsonObject = new JSONObject();
                Integer key = Integer.valueOf(buf.getInt());
                byte type = buf.get();
                int length = buf.getShort();
                jsonObject.put("key", key);
                if (type == 1) {
                    length = (short) (length - 1);
                }
                jsonObject.put("length", length);
                if (type == 0 || type == 1) {
                    byte[] bytes = new byte[length];
                    buf.get(bytes);
                    if (type == 0) {
                        jsonObject.put("type", DataUsageContract.BYTES);
                        jsonObject.put("value", new String(Base64.encode(bytes, 2)));
                    } else {
                        jsonObject.put("type", "string");
                        jsonObject.put("value", new String(bytes));
                        buf.get();
                    }
                } else if (type == 2) {
                    jsonObject.put("type", "uint");
                    if (length == 1) {
                        jsonObject.put("value", buf.get() & 255);
                    } else if (length == 2) {
                        jsonObject.put("value", buf.getShort() & GBDevice.BATTERY_UNKNOWN);
                    } else {
                        jsonObject.put("value", ((long) buf.getInt()) & 4294967295L);
                    }
                } else if (type != 3) {
                    LOG.info("unknown type in appmessage, ignoring");
                    return null;
                } else {
                    jsonObject.put("type", "int");
                    if (length == 1) {
                        jsonObject.put("value", buf.get());
                    } else if (length == 2) {
                        jsonObject.put("value", buf.getShort());
                    } else {
                        jsonObject.put("value", buf.getInt());
                    }
                }
                jsonArray.put(jsonObject);
                dictSize = dictSize2;
            } else {
                GBDeviceEventSendBytes sendBytesAck = null;
                if (this.mAlwaysACKPebbleKit) {
                    sendBytesAck = new GBDeviceEventSendBytes();
                    sendBytesAck.encodedBytes = encodeApplicationMessageAck(uuid, this.last_id);
                }
                GBDeviceEventAppMessage appMessage = new GBDeviceEventAppMessage();
                appMessage.appUUID = uuid;
                appMessage.f124id = this.last_id & 255;
                appMessage.message = jsonArray.toString();
                return new GBDeviceEvent[]{appMessage, sendBytesAck};
            }
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] encodeApplicationMessagePush(short endpoint, UUID uuid, ArrayList<Pair<Integer, Object>> pairs, Integer ext_id) {
        int length = 19;
        Iterator<Pair<Integer, Object>> it = pairs.iterator();
        while (it.hasNext()) {
            Pair<Integer, Object> pair = it.next();
            if (!(pair.first == null || pair.second == null)) {
                length += 7;
                if (pair.second instanceof Integer) {
                    length += 4;
                } else if (pair.second instanceof Short) {
                    length += 2;
                } else if (pair.second instanceof Byte) {
                    length++;
                } else if (pair.second instanceof String) {
                    length += ((String) pair.second).getBytes().length + 1;
                } else if (pair.second instanceof byte[]) {
                    length += ((byte[]) pair.second).length;
                } else {
                    Logger logger = LOG;
                    logger.warn("unknown type: " + pair.second.getClass().toString());
                }
            }
        }
        ByteBuffer buf = ByteBuffer.allocate(length + 4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) length);
        buf.putShort(endpoint);
        buf.put((byte) 1);
        byte b = (byte) (this.last_id + 1);
        this.last_id = b;
        buf.put(b);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        buf.put((byte) pairs.size());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        Iterator<Pair<Integer, Object>> it2 = pairs.iterator();
        while (it2.hasNext()) {
            Pair<Integer, Object> pair2 = it2.next();
            if (!(pair2.first == null || pair2.second == null)) {
                buf.putInt(((Integer) pair2.first).intValue());
                if (pair2.second instanceof Integer) {
                    buf.put((byte) 3);
                    buf.putShort(4);
                    buf.putInt(((Integer) pair2.second).intValue());
                } else if (pair2.second instanceof Short) {
                    buf.put((byte) 3);
                    buf.putShort(2);
                    buf.putShort(((Short) pair2.second).shortValue());
                } else if (pair2.second instanceof Byte) {
                    buf.put((byte) 3);
                    buf.putShort(1);
                    buf.put(((Byte) pair2.second).byteValue());
                } else if (pair2.second instanceof String) {
                    String str = (String) pair2.second;
                    buf.put((byte) 1);
                    buf.putShort((short) (str.getBytes().length + 1));
                    buf.put(str.getBytes());
                    buf.put((byte) 0);
                } else if (pair2.second instanceof byte[]) {
                    byte[] bytes = (byte[]) pair2.second;
                    buf.put((byte) 0);
                    buf.putShort((short) bytes.length);
                    buf.put(bytes);
                }
            }
        }
        this.idLookup[this.last_id & 255] = ext_id;
        return buf.array();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] encodeApplicationMessageFromJSON(java.util.UUID r16, org.json.JSONArray r17) {
        /*
            r15 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = r0
            r0 = 0
            r2 = r0
        L_0x0008:
            int r0 = r17.length()
            r3 = 0
            if (r2 >= r0) goto L_0x0107
            r4 = r17
            java.lang.Object r0 = r4.get(r2)     // Catch:{ JSONException -> 0x00fe }
            org.json.JSONObject r0 = (org.json.JSONObject) r0     // Catch:{ JSONException -> 0x00fe }
            java.lang.String r5 = "type"
            java.lang.Object r5 = r0.get(r5)     // Catch:{ JSONException -> 0x00fe }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ JSONException -> 0x00fe }
            java.lang.String r6 = "key"
            int r6 = r0.getInt(r6)     // Catch:{ JSONException -> 0x00fe }
            java.lang.String r7 = "length"
            int r7 = r0.getInt(r7)     // Catch:{ JSONException -> 0x00fe }
            r8 = -1
            int r9 = r5.hashCode()     // Catch:{ JSONException -> 0x00fe }
            java.lang.String r10 = "uint"
            r11 = 3
            r12 = 2
            r13 = 1
            switch(r9) {
                case -891985903: goto L_0x0055;
                case 104431: goto L_0x004b;
                case 3589978: goto L_0x0043;
                case 94224491: goto L_0x0039;
                default: goto L_0x0038;
            }
        L_0x0038:
            goto L_0x005e
        L_0x0039:
            java.lang.String r9 = "bytes"
            boolean r9 = r5.equals(r9)     // Catch:{ JSONException -> 0x00fe }
            if (r9 == 0) goto L_0x0038
            r8 = 3
            goto L_0x005e
        L_0x0043:
            boolean r9 = r5.equals(r10)     // Catch:{ JSONException -> 0x00fe }
            if (r9 == 0) goto L_0x0038
            r8 = 0
            goto L_0x005e
        L_0x004b:
            java.lang.String r9 = "int"
            boolean r9 = r5.equals(r9)     // Catch:{ JSONException -> 0x00fe }
            if (r9 == 0) goto L_0x0038
            r8 = 1
            goto L_0x005e
        L_0x0055:
            java.lang.String r9 = "string"
            boolean r9 = r5.equals(r9)     // Catch:{ JSONException -> 0x00fe }
            if (r9 == 0) goto L_0x0038
            r8 = 2
        L_0x005e:
            java.lang.String r9 = "value"
            if (r8 == 0) goto L_0x0091
            if (r8 == r13) goto L_0x0091
            if (r8 == r12) goto L_0x0080
            if (r8 == r11) goto L_0x006a
            goto L_0x00f9
        L_0x006a:
            java.lang.String r8 = r0.getString(r9)     // Catch:{ JSONException -> 0x00fe }
            byte[] r8 = android.util.Base64.decode(r8, r12)     // Catch:{ JSONException -> 0x00fe }
            android.util.Pair r9 = new android.util.Pair     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x00fe }
            r9.<init>(r10, r8)     // Catch:{ JSONException -> 0x00fe }
            r1.add(r9)     // Catch:{ JSONException -> 0x00fe }
            goto L_0x00f9
        L_0x0080:
            android.util.Pair r8 = new android.util.Pair     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x00fe }
            java.lang.String r9 = r0.getString(r9)     // Catch:{ JSONException -> 0x00fe }
            r8.<init>(r10, r9)     // Catch:{ JSONException -> 0x00fe }
            r1.add(r8)     // Catch:{ JSONException -> 0x00fe }
            goto L_0x00f9
        L_0x0091:
            if (r7 != r13) goto L_0x00a9
            android.util.Pair r8 = new android.util.Pair     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x00fe }
            int r9 = r0.getInt(r9)     // Catch:{ JSONException -> 0x00fe }
            byte r9 = (byte) r9     // Catch:{ JSONException -> 0x00fe }
            java.lang.Byte r9 = java.lang.Byte.valueOf(r9)     // Catch:{ JSONException -> 0x00fe }
            r8.<init>(r10, r9)     // Catch:{ JSONException -> 0x00fe }
            r1.add(r8)     // Catch:{ JSONException -> 0x00fe }
            goto L_0x00f9
        L_0x00a9:
            if (r7 != r12) goto L_0x00c1
            android.util.Pair r8 = new android.util.Pair     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x00fe }
            int r9 = r0.getInt(r9)     // Catch:{ JSONException -> 0x00fe }
            short r9 = (short) r9     // Catch:{ JSONException -> 0x00fe }
            java.lang.Short r9 = java.lang.Short.valueOf(r9)     // Catch:{ JSONException -> 0x00fe }
            r8.<init>(r10, r9)     // Catch:{ JSONException -> 0x00fe }
            r1.add(r8)     // Catch:{ JSONException -> 0x00fe }
            goto L_0x00f9
        L_0x00c1:
            boolean r8 = r5.equals(r10)     // Catch:{ JSONException -> 0x00fe }
            if (r8 == 0) goto L_0x00e4
            android.util.Pair r8 = new android.util.Pair     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x00fe }
            int r9 = r0.getInt(r9)     // Catch:{ JSONException -> 0x00fe }
            long r11 = (long) r9     // Catch:{ JSONException -> 0x00fe }
            r13 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r11 = r11 & r13
            int r9 = (int) r11     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ JSONException -> 0x00fe }
            r8.<init>(r10, r9)     // Catch:{ JSONException -> 0x00fe }
            r1.add(r8)     // Catch:{ JSONException -> 0x00fe }
            goto L_0x00f9
        L_0x00e4:
            android.util.Pair r8 = new android.util.Pair     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x00fe }
            int r9 = r0.getInt(r9)     // Catch:{ JSONException -> 0x00fe }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ JSONException -> 0x00fe }
            r8.<init>(r10, r9)     // Catch:{ JSONException -> 0x00fe }
            r1.add(r8)     // Catch:{ JSONException -> 0x00fe }
        L_0x00f9:
            int r2 = r2 + 1
            goto L_0x0008
        L_0x00fe:
            r0 = move-exception
            org.slf4j.Logger r5 = LOG
            java.lang.String r6 = "error decoding JSON"
            r5.error((java.lang.String) r6, (java.lang.Throwable) r0)
            return r3
        L_0x0107:
            r4 = r17
            r0 = 48
            r2 = r15
            r5 = r16
            byte[] r0 = r15.encodeApplicationMessagePush(r0, r5, r1, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.encodeApplicationMessageFromJSON(java.util.UUID, org.json.JSONArray):byte[]");
    }

    private byte reverseBits(byte in) {
        byte out = 0;
        for (int i = 0; i < 8; i++) {
            out = (byte) ((out << 1) | ((byte) (in & 1)));
            in = (byte) (in >> 1);
        }
        return out;
    }

    private GBDeviceEventScreenshot decodeScreenshot(ByteBuffer buf, int length) {
        byte corrected;
        if (this.mDevEventScreenshot == null) {
            byte result = buf.get();
            this.mDevEventScreenshot = new GBDeviceEventScreenshot();
            int version = buf.getInt();
            if (result != 0) {
                return null;
            }
            this.mDevEventScreenshot.width = buf.getInt();
            this.mDevEventScreenshot.height = buf.getInt();
            if (version == 1) {
                GBDeviceEventScreenshot gBDeviceEventScreenshot = this.mDevEventScreenshot;
                gBDeviceEventScreenshot.bpp = 1;
                gBDeviceEventScreenshot.clut = clut_pebble;
            } else {
                GBDeviceEventScreenshot gBDeviceEventScreenshot2 = this.mDevEventScreenshot;
                gBDeviceEventScreenshot2.bpp = 8;
                gBDeviceEventScreenshot2.clut = clut_pebbletime;
            }
            this.mScreenshotRemaining = ((this.mDevEventScreenshot.width * this.mDevEventScreenshot.height) * this.mDevEventScreenshot.bpp) / 8;
            this.mDevEventScreenshot.data = new byte[this.mScreenshotRemaining];
            length -= 13;
        }
        if (this.mScreenshotRemaining == -1) {
            return null;
        }
        for (int i = 0; i < length; i++) {
            byte corrected2 = buf.get();
            if (this.mDevEventScreenshot.bpp == 1) {
                corrected = reverseBits(corrected2);
            } else {
                corrected = (byte) (corrected2 & 63);
            }
            this.mDevEventScreenshot.data[(this.mDevEventScreenshot.data.length - this.mScreenshotRemaining) + i] = corrected;
        }
        this.mScreenshotRemaining -= length;
        LOG.info("Screenshot remaining bytes " + this.mScreenshotRemaining);
        if (this.mScreenshotRemaining != 0) {
            return null;
        }
        this.mScreenshotRemaining = -1;
        LOG.info("Got screenshot : " + this.mDevEventScreenshot.width + "x" + this.mDevEventScreenshot.height + "  pixels");
        GBDeviceEventScreenshot devEventScreenshot = this.mDevEventScreenshot;
        this.mDevEventScreenshot = null;
        return devEventScreenshot;
    }

    private GBDeviceEvent[] decodeAction(ByteBuffer buf) {
        int id;
        ByteBuffer byteBuffer = buf;
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        if (buf.get() == 2) {
            UUID uuid = new UUID(0, 0);
            if (this.mFwMajor >= 3) {
                uuid = getUUID(buf);
                id = (int) (uuid.getLeastSignificantBits() & 4294967295L);
            } else {
                id = buf.getInt();
            }
            byte action = buf.get();
            if (action < 0 || action > 15) {
                Logger logger = LOG;
                logger.info("unexpected action: " + action);
            } else {
                GBDeviceEventNotificationControl devEvtNotificationControl = new GBDeviceEventNotificationControl();
                devEvtNotificationControl.handle = (long) id;
                String caption = "undefined";
                int icon_id = 1;
                boolean needsAck2x = true;
                if (action == 1) {
                    devEvtNotificationControl.event = GBDeviceEventNotificationControl.Event.OPEN;
                    caption = "Opened";
                    icon_id = 49;
                } else if (action == 2) {
                    devEvtNotificationControl.event = GBDeviceEventNotificationControl.Event.DISMISS;
                    caption = "Dismissed";
                    icon_id = 51;
                    needsAck2x = false;
                } else if (action == 3) {
                    devEvtNotificationControl.event = GBDeviceEventNotificationControl.Event.DISMISS_ALL;
                    caption = "All dismissed";
                    icon_id = 51;
                    needsAck2x = false;
                } else if (action != 4) {
                    boolean failed = true;
                    if (buf.get() <= 0) {
                        icon_id = 55;
                        caption = "EXECUTED";
                        failed = false;
                    } else if (buf.get() == 1) {
                        int length = buf.getShort();
                        if (length > 64) {
                            length = 64;
                        }
                        byte[] reply = new byte[length];
                        byteBuffer.get(reply);
                        devEvtNotificationControl.phoneNumber = null;
                        if (buf.remaining() > 1 && buf.get() == 12) {
                            byte[] phoneNumberBytes = new byte[buf.getShort()];
                            byteBuffer.get(phoneNumberBytes);
                            devEvtNotificationControl.phoneNumber = new String(phoneNumberBytes);
                        }
                        devEvtNotificationControl.reply = new String(reply);
                        caption = "SENT";
                        icon_id = 47;
                        failed = false;
                    }
                    devEvtNotificationControl.event = GBDeviceEventNotificationControl.Event.REPLY;
                    devEvtNotificationControl.handle = ((devEvtNotificationControl.handle << 4) + ((long) action)) - 4;
                    if (failed) {
                        caption = "FAILED";
                        icon_id = 62;
                        devEvtNotificationControl = null;
                    }
                } else {
                    devEvtNotificationControl.event = GBDeviceEventNotificationControl.Event.MUTE;
                    caption = "Muted";
                    icon_id = 46;
                }
                GBDeviceEventSendBytes sendBytesAck = null;
                if (this.mFwMajor >= 3 || needsAck2x) {
                    sendBytesAck = new GBDeviceEventSendBytes();
                    if (this.mFwMajor >= 3) {
                        sendBytesAck.encodedBytes = encodeActionResponse(uuid, icon_id, caption);
                    } else {
                        sendBytesAck.encodedBytes = encodeActionResponse2x(id, action, 6, caption);
                    }
                }
                return new GBDeviceEvent[]{sendBytesAck, devEvtNotificationControl};
            }
        }
        return null;
    }

    private GBDeviceEventSendBytes decodePing(ByteBuffer buf) {
        if (buf.get() != 0) {
            return null;
        }
        int cookie = buf.getInt();
        LOG.info("Received PING - will reply");
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodePing((byte) 1, cookie);
        return sendBytes;
    }

    private void decodeAppLogs(ByteBuffer buf) {
        UUID uuid = getUUID(buf);
        int i = buf.getInt();
        String fileName = getFixedString(buf, 16);
        String message = getFixedString(buf, buf.get() & 255);
        Logger logger = LOG;
        logger.debug("APP_LOGS (" + (buf.get() & 255) + ") from uuid " + uuid.toString() + " in " + fileName + ":" + (buf.getShort() & 65535) + StringUtils.SPACE + message);
    }

    private GBDeviceEvent decodeSystemMessage(ByteBuffer buf) {
        buf.get();
        byte command = buf.get();
        if (command == 6) {
            LOG.info("SYSTEM MESSAGE: stop reconnecting");
            return null;
        } else if (command != 7) {
            Logger logger = LOG;
            logger.info("SYSTEM MESSAGE: " + command);
            return null;
        } else {
            LOG.info("SYSTEM MESSAGE: start reconnecting");
            return null;
        }
    }

    private GBDeviceEvent[] decodeAppRunState(ByteBuffer buf) {
        byte command = buf.get();
        UUID uuid = getUUID(buf);
        if (command == 1) {
            Logger logger = LOG;
            logger.info("APPRUNSTATE: started " + uuid);
            AppMessageHandler handler = this.mAppMessageHandlers.get(uuid);
            if (handler != null) {
                this.currentRunningApp = uuid;
                return handler.onAppStart();
            } else if (!uuid.equals(this.currentRunningApp)) {
                this.currentRunningApp = uuid;
                GBDeviceEventAppManagement gbDeviceEventAppManagement = new GBDeviceEventAppManagement();
                gbDeviceEventAppManagement.uuid = uuid;
                gbDeviceEventAppManagement.type = GBDeviceEventAppManagement.EventType.START;
                gbDeviceEventAppManagement.event = GBDeviceEventAppManagement.Event.SUCCESS;
                return new GBDeviceEvent[]{gbDeviceEventAppManagement};
            }
        } else if (command != 2) {
            Logger logger2 = LOG;
            logger2.info("APPRUNSTATE: (cmd:" + command + ")" + uuid);
        } else {
            Logger logger3 = LOG;
            logger3.info("APPRUNSTATE: stopped " + uuid);
            GBDeviceEventAppManagement gbDeviceEventAppManagement2 = new GBDeviceEventAppManagement();
            gbDeviceEventAppManagement2.uuid = uuid;
            gbDeviceEventAppManagement2.type = GBDeviceEventAppManagement.EventType.STOP;
            gbDeviceEventAppManagement2.event = GBDeviceEventAppManagement.Event.SUCCESS;
            return new GBDeviceEvent[]{gbDeviceEventAppManagement2};
        }
        return new GBDeviceEvent[]{null};
    }

    private GBDeviceEvent decodeBlobDb(ByteBuffer buf) {
        String[] statusString = {"unknown", "success", "general failure", "invalid operation", "invalid database id", "invalid data", "key does not exist", "database full", "data stale"};
        buf.order(ByteOrder.LITTLE_ENDIAN);
        short token = buf.getShort();
        byte status = buf.get();
        if (status < 0 || status >= statusString.length) {
            Logger logger = LOG;
            logger.warn("BLOBDB: unknown status " + status + " (token " + (65535 & token) + ")");
            return null;
        }
        Logger logger2 = LOG;
        logger2.info("BLOBDB: " + statusString[status] + " (token " + (65535 & token) + ")");
        return null;
    }

    private GBDeviceEventAppManagement decodeAppFetch(ByteBuffer buf) {
        if (buf.get() != 1) {
            return null;
        }
        UUID uuid = getUUID(buf);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        int app_id = buf.getInt();
        GBDeviceEventAppManagement fetchRequest = new GBDeviceEventAppManagement();
        fetchRequest.type = GBDeviceEventAppManagement.EventType.INSTALL;
        fetchRequest.event = GBDeviceEventAppManagement.Event.REQUEST;
        fetchRequest.token = app_id;
        fetchRequest.uuid = uuid;
        return fetchRequest;
    }

    private GBDeviceEvent[] decodeDatalog(ByteBuffer buf, short length) {
        ByteBuffer byteBuffer = buf;
        byte command = buf.get();
        byte id = buf.get();
        GBDeviceEvent[] devEvtsDataLogging = null;
        if (command != 1) {
            if (command == 2) {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int items_left = buf.getInt();
                int i = buf.getInt();
                DatalogSession datalogSession = this.mDatalogSessions.get(Byte.valueOf(id));
                Logger logger = LOG;
                StringBuilder sb = new StringBuilder();
                sb.append("DATALOG SENDDATA. id=");
                sb.append(id & 255);
                sb.append(", items_left=");
                sb.append(items_left);
                sb.append(", total length=");
                sb.append(length - 10);
                logger.info(sb.toString());
                if (datalogSession != null) {
                    Logger logger2 = LOG;
                    logger2.info("DATALOG UUID=" + datalogSession.uuid + ", tag=" + datalogSession.tag + datalogSession.getTaginfo() + ", itemSize=" + datalogSession.itemSize + ", itemType=" + datalogSession.itemType);
                    if (datalogSession.uuid.equals(UUID_ZERO) || !datalogSession.getClass().equals(DatalogSession.class) || !this.mEnablePebbleKit) {
                        devEvtsDataLogging = datalogSession.handleMessage(byteBuffer, length - 10);
                        byte b = command;
                    } else {
                        devEvtsDataLogging = datalogSession.handleMessageForPebbleKit(byteBuffer, length - 10);
                        byte b2 = command;
                    }
                }
            } else if (command == 3) {
                Logger logger3 = LOG;
                logger3.info("DATALOG_CLOSE. id=" + (id & 255));
                DatalogSession datalogSession2 = this.mDatalogSessions.get(Byte.valueOf(id));
                if (datalogSession2 != null) {
                    if (!datalogSession2.uuid.equals(UUID_ZERO) && datalogSession2.getClass().equals(DatalogSession.class) && this.mEnablePebbleKit) {
                        GBDeviceEventDataLogging dataLogging = new GBDeviceEventDataLogging();
                        dataLogging.command = 2;
                        dataLogging.appUUID = datalogSession2.uuid;
                        dataLogging.tag = (long) datalogSession2.tag;
                        devEvtsDataLogging = new GBDeviceEvent[]{dataLogging, 0};
                    }
                    if (datalogSession2.uuid.equals(UUID_ZERO) && (datalogSession2.tag == 81 || datalogSession2.tag == 83 || datalogSession2.tag == 84)) {
                        C1238GB.signalActivityDataFinish();
                    }
                    this.mDatalogSessions.remove(Byte.valueOf(id));
                    byte b3 = command;
                }
            } else if (command != 7) {
                Logger logger4 = LOG;
                logger4.info("unknown DATALOG command: " + (command & 255));
            } else {
                Logger logger5 = LOG;
                logger5.info("DATALOG TIMEOUT. id=" + (id & 255) + " - ignoring");
                return null;
            }
            byte b4 = command;
        } else {
            UUID uuid = getUUID(buf);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            int timestamp = buf.getInt();
            int log_tag = buf.getInt();
            byte item_type = buf.get();
            short item_size = buf.getShort();
            Logger logger6 = LOG;
            logger6.info("DATALOG OPENSESSION. id=" + (id & 255) + ", App UUID=" + uuid.toString() + ", log_tag=" + log_tag + ", item_type=" + item_type + ", itemSize=" + item_size);
            if (this.mDatalogSessions.containsKey(Byte.valueOf(id))) {
                short s = item_size;
                byte b5 = item_type;
                int i2 = log_tag;
                UUID uuid2 = uuid;
            } else if (!uuid.equals(UUID_ZERO) || log_tag != 78) {
                short item_size2 = item_size;
                byte item_type2 = item_type;
                int log_tag2 = log_tag;
                UUID uuid3 = uuid;
                if (uuid3.equals(UUID_ZERO) && log_tag2 == 81) {
                    this.mDatalogSessions.put(Byte.valueOf(id), new DatalogSessionHealthSteps(id, uuid3, timestamp, log_tag2, item_type2, item_size2, getDevice()));
                } else if (uuid3.equals(UUID_ZERO) && log_tag2 == 83) {
                    this.mDatalogSessions.put(Byte.valueOf(id), new DatalogSessionHealthSleep(id, uuid3, timestamp, log_tag2, item_type2, item_size2, getDevice()));
                } else if (uuid3.equals(UUID_ZERO) && log_tag2 == 84) {
                    this.mDatalogSessions.put(Byte.valueOf(id), new DatalogSessionHealthOverlayData(id, uuid3, timestamp, log_tag2, item_type2, item_size2, getDevice()));
                } else if (!uuid3.equals(UUID_ZERO) || log_tag2 != 85) {
                    this.mDatalogSessions.put(Byte.valueOf(id), new DatalogSession(id, uuid3, timestamp, log_tag2, item_type2, item_size2));
                } else {
                    this.mDatalogSessions.put(Byte.valueOf(id), new DatalogSessionHealthHR(id, uuid3, timestamp, log_tag2, item_type2, item_size2, getDevice()));
                }
            } else {
                byte b6 = item_type;
                int i3 = log_tag;
                byte b7 = command;
                UUID uuid4 = uuid;
                this.mDatalogSessions.put(Byte.valueOf(id), new DatalogSessionAnalytics(id, uuid, timestamp, log_tag, item_type, item_size, getDevice()));
            }
            devEvtsDataLogging = new GBDeviceEvent[]{null};
        }
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        if (devEvtsDataLogging != null) {
            LOG.info("sending ACK (0x85)");
            sendBytes.encodedBytes = encodeDatalog(id, (byte) -123);
            devEvtsDataLogging[devEvtsDataLogging.length - 1] = sendBytes;
            return devEvtsDataLogging;
        }
        LOG.info("sending NACK (0x86)");
        sendBytes.encodedBytes = encodeDatalog(id, DATALOG_NACK);
        return new GBDeviceEvent[]{sendBytes};
    }

    private GBDeviceEvent decodeAppReorder(ByteBuffer buf) {
        byte status = buf.get();
        if (status == 1) {
            LOG.info("app reordering successful");
            return null;
        }
        Logger logger = LOG;
        logger.info("app reordering returned status " + status);
        return null;
    }

    private GBDeviceEvent decodeVoiceControl(ByteBuffer buf) {
        ByteBuffer byteBuffer = buf;
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byte command = buf.get();
        int flags = buf.getInt();
        byte session_type = buf.get();
        short session_id = buf.getShort();
        byte b = buf.get();
        byte b2 = buf.get();
        short s = buf.getShort();
        byteBuffer.get(new byte[20]);
        int i = buf.getInt();
        short s2 = buf.getShort();
        byte b3 = buf.get();
        short s3 = buf.getShort();
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        if (command == 1) {
            ByteBuffer repl = ByteBuffer.allocate(7 + 4);
            short s4 = session_id;
            repl.order(ByteOrder.BIG_ENDIAN);
            repl.putShort((short) 7);
            repl.putShort(ENDPOINT_VOICECONTROL);
            repl.put(command);
            repl.putInt(flags);
            repl.put(session_type);
            repl.put((byte) 5);
            sendBytes.encodedBytes = repl.array();
        } else {
            if (command == 2) {
                sendBytes.encodedBytes = null;
            }
        }
        return sendBytes;
    }

    private GBDeviceEvent decodeAudioStream(ByteBuffer buf) {
        return null;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, boolean], vars: [r9v0 ?, r9v36 ?, r9v39 ?]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:102)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:78)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:69)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:51)
        	at jadx.core.dex.visitors.InitCodeVariables.visit(InitCodeVariables.java:32)
        */
    public nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] decodeResponse(byte[] r28) {
        /*
            r27 = this;
            r1 = r27
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.wrap(r28)
            java.nio.ByteOrder r0 = java.nio.ByteOrder.BIG_ENDIAN
            r2.order(r0)
            short r3 = r2.getShort()
            short r4 = r2.getShort()
            r5 = 0
            r0 = 48
            r6 = 32
            r7 = 2
            r8 = 0
            r9 = 1
            switch(r4) {
                case -21555: goto L_0x03f0;
                case -20005: goto L_0x03e5;
                case -16657: goto L_0x03b7;
                case 16: goto L_0x0360;
                case 17: goto L_0x033d;
                case 18: goto L_0x0331;
                case 32: goto L_0x02f8;
                case 33: goto L_0x02c8;
                case 48: goto L_0x0190;
                case 49: goto L_0x0190;
                case 52: goto L_0x018a;
                case 2001: goto L_0x017d;
                case 2006: goto L_0x0178;
                case 3010: goto L_0x0172;
                case 6000: goto L_0x0052;
                case 6001: goto L_0x0047;
                case 6778: goto L_0x0041;
                case 8000: goto L_0x0036;
                case 10000: goto L_0x002b;
                case 11000: goto L_0x0020;
                case 11440: goto L_0x0172;
                default: goto L_0x001e;
            }
        L_0x001e:
            goto L_0x03fb
        L_0x0020:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent r6 = r1.decodeVoiceControl(r2)
            r0[r8] = r6
            r5 = r0
            goto L_0x03fb
        L_0x002b:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent r6 = r1.decodeAudioStream(r2)
            r0[r8] = r6
            r5 = r0
            goto L_0x03fb
        L_0x0036:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventScreenshot r6 = r1.decodeScreenshot(r2, r3)
            r0[r8] = r6
            r5 = r0
            goto L_0x03fb
        L_0x0041:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r5 = r1.decodeDatalog(r2, r3)
            goto L_0x03fb
        L_0x0047:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement r6 = r1.decodeAppFetch(r2)
            r0[r8] = r6
            r5 = r0
            goto L_0x03fb
        L_0x0052:
            byte r0 = r2.get()
            if (r0 == r9) goto L_0x00d7
            if (r0 == r7) goto L_0x00b7
            r6 = 5
            if (r0 == r6) goto L_0x0075
            org.slf4j.Logger r6 = LOG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Unknown APPMANAGER event"
            r7.append(r8)
            r7.append(r0)
            java.lang.String r7 = r7.toString()
            r6.info(r7)
            goto L_0x03fb
        L_0x0075:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes r6 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes
            r6.<init>()
            r7 = 6000(0x1770, float:8.408E-42)
            byte[] r7 = r1.encodeSimpleMessage(r7, r9)
            r6.encodedBytes = r7
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r7 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            r7[r8] = r6
            r5 = r7
            java.util.ArrayList<java.util.UUID> r7 = r1.tmpUUIDS
            r7.clear()
            int r7 = r2.getInt()
            r8 = 0
        L_0x0091:
            if (r8 >= r7) goto L_0x00b5
            java.util.UUID r9 = r1.getUUID(r2)
            org.slf4j.Logger r10 = LOG
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "found uuid: "
            r11.append(r12)
            r11.append(r9)
            java.lang.String r11 = r11.toString()
            r10.info(r11)
            java.util.ArrayList<java.util.UUID> r10 = r1.tmpUUIDS
            r10.add(r9)
            int r8 = r8 + 1
            goto L_0x0091
        L_0x00b5:
            goto L_0x03fb
        L_0x00b7:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement r6 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement
            r6.<init>()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$EventType r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.EventType.DELETE
            r6.type = r7
            int r7 = r2.getInt()
            if (r7 == r9) goto L_0x00cb
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$Event r10 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.Event.FAILURE
            r6.event = r10
            goto L_0x00d0
        L_0x00cb:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$Event r10 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.Event.SUCCESS
            r6.event = r10
        L_0x00d0:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r9 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            r9[r8] = r6
            r5 = r9
            goto L_0x03fb
        L_0x00d7:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppInfo r7 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppInfo
            r7.<init>()
            int r10 = r2.getInt()
            int r11 = r2.getInt()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp[] r12 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp[r11]
            r7.apps = r12
            boolean[] r12 = new boolean[r10]
            r13 = 0
        L_0x00eb:
            if (r13 >= r11) goto L_0x0143
            int r14 = r2.getInt()
            int r15 = r2.getInt()
            r12[r15] = r9
            java.lang.String r22 = r1.getFixedString(r2, r6)
            java.lang.String r23 = r1.getFixedString(r2, r6)
            int r24 = r2.getInt()
            r6 = r24 & 16
            r8 = 16
            if (r6 != r8) goto L_0x010c
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r6 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_ACTIVITYTRACKER
            goto L_0x0115
        L_0x010c:
            r6 = r24 & 1
            if (r6 != r9) goto L_0x0113
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r6 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.WATCHFACE
            goto L_0x0115
        L_0x0113:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r6 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_GENERIC
        L_0x0115:
            short r8 = r2.getShort()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp[] r9 = r7.apps
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r25 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            r26 = r0
            java.util.ArrayList<java.util.UUID> r0 = r1.tmpUUIDS
            java.lang.Object r0 = r0.get(r13)
            r17 = r0
            java.util.UUID r17 = (java.util.UUID) r17
            java.lang.String r20 = java.lang.String.valueOf(r8)
            r16 = r25
            r18 = r22
            r19 = r23
            r21 = r6
            r16.<init>(r17, r18, r19, r20, r21)
            r9[r13] = r25
            int r13 = r13 + 1
            r0 = r26
            r6 = 32
            r8 = 0
            r9 = 1
            goto L_0x00eb
        L_0x0143:
            r26 = r0
            r0 = 0
        L_0x0146:
            if (r0 >= r10) goto L_0x0169
            boolean r6 = r12[r0]
            if (r6 != 0) goto L_0x0166
            byte r6 = (byte) r0
            r7.freeSlot = r6
            org.slf4j.Logger r6 = LOG
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "found free slot "
            r8.append(r9)
            r8.append(r0)
            java.lang.String r8 = r8.toString()
            r6.info(r8)
            goto L_0x0169
        L_0x0166:
            int r0 = r0 + 1
            goto L_0x0146
        L_0x0169:
            r6 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r6]
            r6 = 0
            r0[r6] = r7
            r5 = r0
            goto L_0x03fb
        L_0x0172:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r5 = r1.decodeAction(r2)
            goto L_0x03fb
        L_0x0178:
            r1.decodeAppLogs(r2)
            goto L_0x03fb
        L_0x017d:
            r6 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r6]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes r6 = r1.decodePing(r2)
            r7 = 0
            r0[r7] = r6
            r5 = r0
            goto L_0x03fb
        L_0x018a:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r5 = r1.decodeAppRunState(r2)
            goto L_0x03fb
        L_0x0190:
            byte r6 = r2.get()
            byte r8 = r2.get()
            r1.last_id = r8
            java.util.UUID r8 = r1.getUUID(r2)
            java.lang.String r9 = "got APPLICATIONMESSAGE/LAUNCHER (EP "
            r10 = -1
            if (r6 == r10) goto L_0x025a
            r11 = 127(0x7f, float:1.78E-43)
            if (r6 == r11) goto L_0x025a
            r10 = 0
            r11 = 1
            if (r6 == r11) goto L_0x01d1
            if (r6 == r7) goto L_0x01af
            goto L_0x03fb
        L_0x01af:
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r9)
            r7.append(r4)
            java.lang.String r9 = ")  REQUEST"
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r0.info(r7)
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            r7 = 0
            r0[r7] = r10
            r5 = r0
            goto L_0x03fb
        L_0x01d1:
            org.slf4j.Logger r7 = LOG
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r11 = 49
            if (r4 != r11) goto L_0x01df
            java.lang.String r11 = "got LAUNCHER PUSH from UUID : "
            goto L_0x01e1
        L_0x01df:
            java.lang.String r11 = "got APPLICATIONMESSAGE PUSH from UUID : "
        L_0x01e1:
            r9.append(r11)
            r9.append(r8)
            java.lang.String r9 = r9.toString()
            r7.info(r9)
            java.util.Map<java.util.UUID, nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.AppMessageHandler> r7 = r1.mAppMessageHandlers
            java.lang.Object r7 = r7.get(r8)
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.AppMessageHandler r7 = (nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.AppMessageHandler) r7
            if (r7 == 0) goto L_0x021a
            r1.currentRunningApp = r8
            boolean r9 = r7.isEnabled()
            if (r9 == 0) goto L_0x0212
            if (r4 != r0) goto L_0x020c
            java.util.ArrayList r0 = r1.decodeDict(r2)
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = r7.handleMessage(r0)
            r5 = r0
            goto L_0x0256
        L_0x020c:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = r7.onAppStart()
            r5 = r0
            goto L_0x0256
        L_0x0212:
            r9 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            r9 = 0
            r0[r9] = r10
            r5 = r0
            goto L_0x0256
        L_0x021a:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = r1.decodeDictToJSONAppMessage(r8, r2)     // Catch:{ JSONException -> 0x0220 }
            r5 = r0
            goto L_0x022c
        L_0x0220:
            r0 = move-exception
            r9 = r0
            r0 = r9
            org.slf4j.Logger r9 = LOG
            java.lang.String r10 = r0.getMessage()
            r9.error(r10)
        L_0x022c:
            java.util.UUID r0 = r1.currentRunningApp
            boolean r0 = r8.equals(r0)
            if (r0 != 0) goto L_0x0256
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement
            r0.<init>()
            r0.uuid = r8
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$EventType r9 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.EventType.START
            r0.type = r9
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$Event r9 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.Event.SUCCESS
            r0.event = r9
            if (r5 == 0) goto L_0x0247
            int r9 = r5.length
            goto L_0x0248
        L_0x0247:
            r9 = 0
        L_0x0248:
            r10 = 1
            int r9 = r9 + r10
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r9 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            r11 = 0
            r9[r11] = r0
            if (r5 == 0) goto L_0x0255
            int r12 = r5.length
            java.lang.System.arraycopy(r5, r11, r9, r10, r12)
        L_0x0255:
            r5 = r9
        L_0x0256:
            r1.currentRunningApp = r8
            goto L_0x03fb
        L_0x025a:
            if (r6 != r10) goto L_0x0276
            org.slf4j.Logger r7 = LOG
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r9)
            r11.append(r4)
            java.lang.String r9 = ") ACK"
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r7.info(r9)
            goto L_0x028f
        L_0x0276:
            org.slf4j.Logger r7 = LOG
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r9)
            r11.append(r4)
            java.lang.String r9 = ") NACK"
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r7.info(r9)
        L_0x028f:
            r7 = 0
            if (r4 != r0) goto L_0x02bf
            java.lang.Integer[] r0 = r1.idLookup
            byte r9 = r1.last_id
            r9 = r9 & 255(0xff, float:3.57E-43)
            r0 = r0[r9]
            if (r0 == 0) goto L_0x02bf
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppMessage r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppMessage
            r0.<init>()
            r7 = r0
            if (r6 != r10) goto L_0x02a9
            int r0 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppMessage.TYPE_ACK
            r7.type = r0
            goto L_0x02ad
        L_0x02a9:
            int r0 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppMessage.TYPE_NACK
            r7.type = r0
        L_0x02ad:
            java.lang.Integer[] r0 = r1.idLookup
            byte r9 = r1.last_id
            r9 = r9 & 255(0xff, float:3.57E-43)
            r0 = r0[r9]
            int r0 = r0.intValue()
            r7.f124id = r0
            java.util.UUID r0 = r1.currentRunningApp
            r7.appUUID = r0
        L_0x02bf:
            r9 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            r9 = 0
            r0[r9] = r7
            r5 = r0
            goto L_0x03fb
        L_0x02c8:
            byte r0 = r2.get()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl r6 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl
            r6.<init>()
            if (r0 == r7) goto L_0x02ea
            org.slf4j.Logger r7 = LOG
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Unknown PHONECONTROL event"
            r8.append(r9)
            r8.append(r0)
            java.lang.String r8 = r8.toString()
            r7.info(r8)
            goto L_0x02ef
        L_0x02ea:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl.Event.END
            r6.event = r7
        L_0x02ef:
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r7 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            r8 = 0
            r7[r8] = r6
            r5 = r7
            goto L_0x03fb
        L_0x02f8:
            byte r0 = r2.get()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl r6 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl
            r6.<init>()
            switch(r0) {
                case 1: goto L_0x0323;
                case 2: goto L_0x031e;
                case 3: goto L_0x0319;
                case 4: goto L_0x0314;
                case 5: goto L_0x030f;
                case 6: goto L_0x030a;
                case 7: goto L_0x0305;
                default: goto L_0x0304;
            }
        L_0x0304:
            goto L_0x0328
        L_0x0305:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.VOLUMEDOWN
            r6.event = r7
            goto L_0x0328
        L_0x030a:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.VOLUMEUP
            r6.event = r7
            goto L_0x0328
        L_0x030f:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.PREVIOUS
            r6.event = r7
            goto L_0x0328
        L_0x0314:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.NEXT
            r6.event = r7
            goto L_0x0328
        L_0x0319:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.PLAY
            r6.event = r7
            goto L_0x0328
        L_0x031e:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.PAUSE
            r6.event = r7
            goto L_0x0328
        L_0x0323:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.PLAYPAUSE
            r6.event = r7
        L_0x0328:
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r7 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            r8 = 0
            r7[r8] = r6
            r5 = r7
            goto L_0x03fb
        L_0x0331:
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent r6 = r1.decodeSystemMessage(r2)
            r0[r8] = r6
            r5 = r0
            goto L_0x03fb
        L_0x033d:
            byte r0 = r2.get()
            if (r0 == 0) goto L_0x0345
            goto L_0x03fb
        L_0x0345:
            org.slf4j.Logger r6 = LOG
            java.lang.String r8 = "Pebble asked for Phone/App Version - repLYING!"
            r6.info(r8)
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes r6 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes
            r6.<init>()
            byte[] r7 = r1.encodePhoneVersion(r7)
            r6.encodedBytes = r7
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r7 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            r8 = 0
            r7[r8] = r6
            r5 = r7
            goto L_0x03fb
        L_0x0360:
            byte r6 = r2.get()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo r7 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo
            r7.<init>()
            r2.getInt()
            r8 = 32
            java.lang.String r8 = r1.getFixedString(r2, r8)
            r7.fwVersion = r8
            java.lang.String r8 = r7.fwVersion
            r9 = 1
            char r8 = r8.charAt(r9)
            int r8 = r8 - r0
            r1.mFwMajor = r8
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Pebble firmware major detected as "
            r8.append(r9)
            int r9 = r1.mFwMajor
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r0.info(r8)
            r0 = 9
            byte[] r8 = new byte[r0]
            r9 = 0
            r2.get(r8, r9, r0)
            byte r0 = r2.get()
            int r0 = r0 + 8
            if (r0 < 0) goto L_0x03af
            java.lang.String[] r9 = hwRevisions
            int r10 = r9.length
            if (r0 >= r10) goto L_0x03af
            r9 = r9[r0]
            r7.hwVersion = r9
        L_0x03af:
            r9 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r9 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r9]
            r10 = 0
            r9[r10] = r7
            r5 = r9
            goto L_0x03fb
        L_0x03b7:
            byte r0 = r2.get()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement r6 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement
            r6.<init>()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$EventType r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.EventType.INSTALL
            r6.type = r7
            r7 = 1
            if (r0 == r7) goto L_0x03d2
            int r7 = r2.getInt()
            r6.token = r7
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.Event.FAILURE
            r6.event = r7
            goto L_0x03dd
        L_0x03d2:
            int r7 = r2.getInt()
            r6.token = r7
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement$Event r7 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement.Event.SUCCESS
            r6.event = r7
        L_0x03dd:
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r7 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            r8 = 0
            r7[r8] = r6
            r5 = r7
            goto L_0x03fb
        L_0x03e5:
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent r6 = r1.decodeBlobDb(r2)
            r0[r8] = r6
            r5 = r0
            goto L_0x03fb
        L_0x03f0:
            r7 = 1
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[r7]
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent r6 = r1.decodeAppReorder(r2)
            r0[r8] = r6
            r5 = r0
        L_0x03fb:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.decodeResponse(byte[]):nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[]");
    }

    /* access modifiers changed from: package-private */
    public void setForceProtocol(boolean force) {
        Logger logger = LOG;
        logger.info("setting force protocol to " + force);
        this.mForceProtocol = force;
    }

    /* access modifiers changed from: package-private */
    public void setAlwaysACKPebbleKit(boolean alwaysACKPebbleKit) {
        Logger logger = LOG;
        logger.info("setting always ACK PebbleKit to " + alwaysACKPebbleKit);
        this.mAlwaysACKPebbleKit = alwaysACKPebbleKit;
    }

    /* access modifiers changed from: package-private */
    public void setEnablePebbleKit(boolean enablePebbleKit) {
        Logger logger = LOG;
        logger.info("setting enable PebbleKit support to " + enablePebbleKit);
        this.mEnablePebbleKit = enablePebbleKit;
    }

    /* access modifiers changed from: package-private */
    public boolean hasAppMessageHandler(UUID uuid) {
        return this.mAppMessageHandlers.containsKey(uuid);
    }

    private String getFixedString(ByteBuffer buf, int length) {
        byte[] tmp = new byte[length];
        buf.get(tmp, 0, length);
        return new String(tmp).trim();
    }

    private UUID getUUID(ByteBuffer buf) {
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.BIG_ENDIAN);
        long uuid_high = buf.getLong();
        long uuid_low = buf.getLong();
        buf.order(byteOrder);
        return new UUID(uuid_high, uuid_low);
    }
}
