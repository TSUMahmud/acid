package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelUuid;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.ExternalPebbleJSActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AbstractAppManagerFragment;
import nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppManagement;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppMessage;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.pebble.GBDeviceEventDataLogging;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWReader;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceIoThread;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import nodomain.freeyourgadget.gadgetbridge.util.WebViewSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PebbleIoThread extends GBDeviceIoThread {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleIoThread.class);
    private int mAppInstallToken = -1;
    private int mBinarySize = -1;
    private BluetoothAdapter mBtAdapter = null;
    private BluetoothSocket mBtSocket = null;
    private int mBytesWritten = -1;
    private int mCRC = -1;
    private int mCurrentInstallableIndex = -1;
    private GBDeviceApp mCurrentlyInstallingApp = null;
    private final boolean mEnablePebblekit;
    private InputStream mFis = null;
    private InputStream mInStream = null;
    private int mInstallSlot = -2;
    private PebbleAppInstallState mInstallState = PebbleAppInstallState.UNKNOWN;
    private boolean mIsConnected = false;
    private boolean mIsInstalling = false;
    private boolean mIsTCP = false;
    private OutputStream mOutStream = null;
    private PBWReader mPBWReader = null;
    private final PebbleActiveAppTracker mPebbleActiveAppTracker;
    private PebbleInstallable[] mPebbleInstallables = null;
    private PebbleKitSupport mPebbleKitSupport;
    private PebbleLESupport mPebbleLESupport;
    private final PebbleProtocol mPebbleProtocol;
    private final PebbleSupport mPebbleSupport;
    private boolean mQuit = false;
    private Socket mTCPSocket = null;
    private final Prefs prefs = GBApplication.getPrefs();

    private enum PebbleAppInstallState {
        UNKNOWN,
        WAIT_SLOT,
        START_INSTALL,
        WAIT_TOKEN,
        UPLOAD_CHUNK,
        UPLOAD_COMMIT,
        WAIT_COMMIT,
        UPLOAD_COMPLETE,
        APP_REFRESH
    }

    private void sendAppMessageJS(GBDeviceEventAppMessage appMessage) {
        sendAppMessage(appMessage);
        if (appMessage.type == GBDeviceEventAppMessage.TYPE_APPMESSAGE) {
            write(this.mPebbleProtocol.encodeApplicationMessageAck(appMessage.appUUID, (byte) appMessage.f124id));
        }
    }

    private static void sendAppMessage(GBDeviceEventAppMessage message) {
        final String jsEvent;
        String str;
        StringBuilder sb;
        try {
            WebViewSingleton.getInstance().checkAppRunning(message.appUUID);
            if (message.type != GBDeviceEventAppMessage.TYPE_APPMESSAGE) {
                if (GBDeviceEventAppMessage.TYPE_NACK == GBDeviceEventAppMessage.TYPE_APPMESSAGE) {
                    sb = new StringBuilder();
                    str = "NACK";
                } else {
                    sb = new StringBuilder();
                    str = "ACK";
                }
                sb.append(str);
                sb.append(message.f124id);
                jsEvent = sb.toString();
                Logger logger = LOG;
                logger.debug("WEBVIEW received ACK/NACK:" + message.message + " for uuid: " + message.appUUID + " ID: " + message.f124id);
            } else {
                jsEvent = "appmessage";
            }
            final String appMessage = PebbleUtils.parseIncomingAppMessage(message.message, message.appUUID, message.f124id);
            Logger logger2 = LOG;
            logger2.debug("to WEBVIEW: event: " + jsEvent + " message: " + appMessage);
            WebViewSingleton.getInstance().invokeWebview(new WebViewSingleton.WebViewRunnable() {
                public void invoke(WebView webView) {
                    webView.evaluateJavascript("if (typeof Pebble == 'object') Pebble.evaluate('" + jsEvent + "',[" + appMessage + "]);", new ValueCallback<String>() {
                        public void onReceiveValue(String s) {
                            Logger access$000 = PebbleIoThread.LOG;
                            access$000.debug("Callback from appmessage: " + s);
                        }
                    });
                }
            });
        } catch (IllegalStateException ex) {
            Logger logger3 = LOG;
            logger3.warn("Unable to send app message: " + message, (Throwable) ex);
        }
    }

    PebbleIoThread(PebbleSupport pebbleSupport, GBDevice gbDevice, GBDeviceProtocol gbDeviceProtocol, BluetoothAdapter btAdapter, Context context) {
        super(gbDevice, context);
        this.mPebbleProtocol = (PebbleProtocol) gbDeviceProtocol;
        this.mBtAdapter = btAdapter;
        this.mPebbleSupport = pebbleSupport;
        this.mEnablePebblekit = this.prefs.getBoolean("pebble_enable_pebblekit", false);
        this.mPebbleProtocol.setAlwaysACKPebbleKit(this.prefs.getBoolean("pebble_always_ack_pebblekit", false));
        this.mPebbleProtocol.setEnablePebbleKit(this.mEnablePebblekit);
        this.mPebbleActiveAppTracker = new PebbleActiveAppTracker();
    }

    private int readWithException(InputStream inputStream, byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int ret = inputStream.read(buffer, byteOffset, byteCount);
        if (ret != -1) {
            return ret;
        }
        throw new IOException("broken pipe");
    }

    /* access modifiers changed from: protected */
    public boolean connect() {
        String deviceAddress = this.gbDevice.getAddress();
        GBDevice.State originalState = this.gbDevice.getState();
        this.gbDevice.setState(GBDevice.State.CONNECTING);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        try {
            int firstColon = deviceAddress.indexOf(":");
            if (firstColon == deviceAddress.lastIndexOf(":")) {
                this.mIsTCP = true;
                this.mTCPSocket = new Socket(InetAddress.getByName(deviceAddress.substring(0, firstColon)), Integer.parseInt(deviceAddress.substring(firstColon + 1)));
                this.mInStream = this.mTCPSocket.getInputStream();
                this.mOutStream = this.mTCPSocket.getOutputStream();
            } else {
                this.mIsTCP = false;
                if (this.gbDevice.getVolatileAddress() != null && this.prefs.getBoolean("pebble_force_le", false)) {
                    deviceAddress = this.gbDevice.getVolatileAddress();
                }
                BluetoothDevice btDevice = this.mBtAdapter.getRemoteDevice(deviceAddress);
                if (btDevice.getType() == 2) {
                    LOG.info("This is a Pebble 2 or Pebble-LE/Pebble Time LE, will use BLE");
                    this.mInStream = new PipedInputStream();
                    this.mOutStream = new PipedOutputStream();
                    this.mPebbleLESupport = new PebbleLESupport(getContext(), btDevice, (PipedInputStream) this.mInStream, (PipedOutputStream) this.mOutStream);
                } else {
                    ParcelUuid[] uuids = btDevice.getUuids();
                    if (uuids == null) {
                        return false;
                    }
                    for (ParcelUuid uuid : uuids) {
                        Logger logger = LOG;
                        logger.info("found service UUID " + uuid);
                    }
                    this.mBtSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
                    this.mBtSocket.connect();
                    this.mInStream = this.mBtSocket.getInputStream();
                    this.mOutStream = this.mBtSocket.getOutputStream();
                }
            }
            if (GBApplication.getGBPrefs().isBackgroundJsEnabled()) {
                Intent startIntent = new Intent(getContext(), ExternalPebbleJSActivity.class);
                startIntent.setFlags(268435456);
                startIntent.putExtra(ExternalPebbleJSActivity.START_BG_WEBVIEW, true);
                getContext().startActivity(startIntent);
            } else {
                LOG.debug("Not enabling background Webview, is disabled in preferences.");
            }
            this.mPebbleProtocol.setForceProtocol(this.prefs.getBoolean("pebble_force_protocol", false));
            this.mIsConnected = true;
            write(this.mPebbleProtocol.encodeFirmwareVersionReq());
            this.gbDevice.setState(GBDevice.State.CONNECTED);
            this.gbDevice.sendDeviceUpdateIntent(getContext());
            return true;
        } catch (IOException e) {
            LOG.warn("error while connecting: " + e.getMessage(), (Throwable) e);
            this.gbDevice.setState(originalState);
            this.gbDevice.sendDeviceUpdateIntent(getContext());
            this.mInStream = null;
            this.mOutStream = null;
            this.mBtSocket = null;
            return false;
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x01c2 A[Catch:{ IOException -> 0x0288 }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01d2 A[Catch:{ IOException -> 0x0288 }, LOOP:2: B:62:0x01d0->B:63:0x01d2, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01ef A[Catch:{ IOException -> 0x0288 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x027f A[Catch:{ IOException -> 0x0288 }, LOOP:5: B:93:0x0277->B:95:0x027f, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r14 = this;
            boolean r0 = r14.connect()
            r14.mIsConnected = r0
            boolean r0 = r14.mIsConnected
            if (r0 != 0) goto L_0x0029
            nodomain.freeyourgadget.gadgetbridge.util.GBPrefs r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getGBPrefs()
            boolean r0 = r0.getAutoReconnect()
            if (r0 == 0) goto L_0x0028
            boolean r0 = r14.mQuit
            if (r0 != 0) goto L_0x0028
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r14.gbDevice
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice$State r1 = nodomain.freeyourgadget.gadgetbridge.impl.GBDevice.State.WAITING_FOR_RECONNECT
            r0.setState(r1)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r14.gbDevice
            android.content.Context r1 = r14.getContext()
            r0.sendDeviceUpdateIntent(r1)
        L_0x0028:
            return
        L_0x0029:
            r0 = 8192(0x2000, float:1.14794E-41)
            byte[] r1 = new byte[r0]
            r2 = 1
            r14.enablePebbleKitSupport(r2)
            r3 = 0
            r14.mQuit = r3
        L_0x0034:
            boolean r4 = r14.mQuit
            r5 = 0
            if (r4 != 0) goto L_0x02be
            boolean r4 = r14.mIsInstalling     // Catch:{ IOException -> 0x0288 }
            if (r4 == 0) goto L_0x01be
            int[] r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.C12102.f197xf0e4bdee     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r6 = r14.mInstallState     // Catch:{ IOException -> 0x0288 }
            int r6 = r6.ordinal()     // Catch:{ IOException -> 0x0288 }
            r4 = r4[r6]     // Catch:{ IOException -> 0x0288 }
            java.lang.String r6 = "got token "
            r7 = -1
            switch(r4) {
                case 1: goto L_0x01ac;
                case 2: goto L_0x0161;
                case 3: goto L_0x0141;
                case 4: goto L_0x00dd;
                case 5: goto L_0x00c8;
                case 6: goto L_0x00a8;
                case 7: goto L_0x0087;
                case 8: goto L_0x004f;
                default: goto L_0x004d;
            }
        L_0x004d:
            goto L_0x01be
        L_0x004f:
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWReader r4 = r14.mPBWReader     // Catch:{ IOException -> 0x0288 }
            boolean r4 = r4.isFirmware()     // Catch:{ IOException -> 0x0288 }
            if (r4 == 0) goto L_0x0065
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r4 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            byte[] r4 = r4.encodeInstallFirmwareComplete()     // Catch:{ IOException -> 0x0288 }
            r14.writeInstallApp(r4)     // Catch:{ IOException -> 0x0288 }
            r14.finishInstall(r3)     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x0065:
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWReader r4 = r14.mPBWReader     // Catch:{ IOException -> 0x0288 }
            boolean r4 = r4.isLanguage()     // Catch:{ IOException -> 0x0288 }
            if (r4 != 0) goto L_0x0082
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r4 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            int r4 = r4.mFwMajor     // Catch:{ IOException -> 0x0288 }
            r6 = 3
            if (r4 < r6) goto L_0x0075
            goto L_0x0082
        L_0x0075:
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r4 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.mInstallSlot     // Catch:{ IOException -> 0x0288 }
            byte[] r4 = r4.encodeAppRefresh(r6)     // Catch:{ IOException -> 0x0288 }
            r14.writeInstallApp(r4)     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x0082:
            r14.finishInstall(r3)     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x0087:
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r4 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.mAppInstallToken     // Catch:{ IOException -> 0x0288 }
            byte[] r4 = r4.encodeUploadComplete(r6)     // Catch:{ IOException -> 0x0288 }
            r14.writeInstallApp(r4)     // Catch:{ IOException -> 0x0288 }
            int r4 = r14.mCurrentInstallableIndex     // Catch:{ IOException -> 0x0288 }
            int r4 = r4 + r2
            r14.mCurrentInstallableIndex = r4     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable[] r6 = r14.mPebbleInstallables     // Catch:{ IOException -> 0x0288 }
            int r6 = r6.length     // Catch:{ IOException -> 0x0288 }
            if (r4 >= r6) goto L_0x00a2
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.START_INSTALL     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r4     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x00a2:
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.APP_REFRESH     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r4     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x00a8:
            int r4 = r14.mAppInstallToken     // Catch:{ IOException -> 0x0288 }
            if (r4 == r7) goto L_0x01be
            org.slf4j.Logger r4 = LOG     // Catch:{ IOException -> 0x0288 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0288 }
            r7.<init>()     // Catch:{ IOException -> 0x0288 }
            r7.append(r6)     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.mAppInstallToken     // Catch:{ IOException -> 0x0288 }
            r7.append(r6)     // Catch:{ IOException -> 0x0288 }
            java.lang.String r6 = r7.toString()     // Catch:{ IOException -> 0x0288 }
            r4.info(r6)     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.UPLOAD_COMPLETE     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r4     // Catch:{ IOException -> 0x0288 }
            goto L_0x0034
        L_0x00c8:
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r4 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.mAppInstallToken     // Catch:{ IOException -> 0x0288 }
            int r8 = r14.mCRC     // Catch:{ IOException -> 0x0288 }
            byte[] r4 = r4.encodeUploadCommit(r6, r8)     // Catch:{ IOException -> 0x0288 }
            r14.writeInstallApp(r4)     // Catch:{ IOException -> 0x0288 }
            r14.mAppInstallToken = r7     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.WAIT_COMMIT     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r4     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x00dd:
            r4 = r3
        L_0x00de:
            java.io.InputStream r6 = r14.mFis     // Catch:{ IOException -> 0x0288 }
            r8 = 2000(0x7d0, float:2.803E-42)
            int r9 = 2000 - r4
            int r6 = r6.read(r1, r4, r9)     // Catch:{ IOException -> 0x0288 }
            if (r6 > 0) goto L_0x00eb
            goto L_0x00ee
        L_0x00eb:
            int r4 = r4 + r6
            if (r4 < r8) goto L_0x00de
        L_0x00ee:
            if (r4 <= 0) goto L_0x013b
            android.content.Context r6 = r14.getContext()     // Catch:{ IOException -> 0x0288 }
            r8 = 2131755318(0x7f100136, float:1.9141512E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ IOException -> 0x0288 }
            int r10 = r14.mCurrentInstallableIndex     // Catch:{ IOException -> 0x0288 }
            int r10 = r10 + r2
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ IOException -> 0x0288 }
            r9[r3] = r10     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable[] r10 = r14.mPebbleInstallables     // Catch:{ IOException -> 0x0288 }
            int r10 = r10.length     // Catch:{ IOException -> 0x0288 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ IOException -> 0x0288 }
            r9[r2] = r10     // Catch:{ IOException -> 0x0288 }
            java.lang.String r6 = r6.getString(r8, r9)     // Catch:{ IOException -> 0x0288 }
            int r8 = r14.mBytesWritten     // Catch:{ IOException -> 0x0288 }
            float r8 = (float) r8     // Catch:{ IOException -> 0x0288 }
            int r9 = r14.mBinarySize     // Catch:{ IOException -> 0x0288 }
            float r9 = (float) r9     // Catch:{ IOException -> 0x0288 }
            float r8 = r8 / r9
            r9 = 1120403456(0x42c80000, float:100.0)
            float r8 = r8 * r9
            int r8 = (int) r8     // Catch:{ IOException -> 0x0288 }
            android.content.Context r9 = r14.getContext()     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateInstallNotification(r6, r2, r8, r9)     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r6 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            int r8 = r14.mAppInstallToken     // Catch:{ IOException -> 0x0288 }
            byte[] r6 = r6.encodeUploadChunk(r8, r1, r4)     // Catch:{ IOException -> 0x0288 }
            r14.writeInstallApp(r6)     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.mBytesWritten     // Catch:{ IOException -> 0x0288 }
            int r6 = r6 + r4
            r14.mBytesWritten = r6     // Catch:{ IOException -> 0x0288 }
            r14.mAppInstallToken = r7     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.WAIT_TOKEN     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r6     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x013b:
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.UPLOAD_COMMIT     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r6     // Catch:{ IOException -> 0x0288 }
            goto L_0x0034
        L_0x0141:
            int r4 = r14.mAppInstallToken     // Catch:{ IOException -> 0x0288 }
            if (r4 == r7) goto L_0x01be
            org.slf4j.Logger r4 = LOG     // Catch:{ IOException -> 0x0288 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0288 }
            r7.<init>()     // Catch:{ IOException -> 0x0288 }
            r7.append(r6)     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.mAppInstallToken     // Catch:{ IOException -> 0x0288 }
            r7.append(r6)     // Catch:{ IOException -> 0x0288 }
            java.lang.String r6 = r7.toString()     // Catch:{ IOException -> 0x0288 }
            r4.info(r6)     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.UPLOAD_CHUNK     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r4     // Catch:{ IOException -> 0x0288 }
            goto L_0x0034
        L_0x0161:
            org.slf4j.Logger r4 = LOG     // Catch:{ IOException -> 0x0288 }
            java.lang.String r6 = "start installing app binary"
            r4.info(r6)     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleInstallable[] r4 = r14.mPebbleInstallables     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.mCurrentInstallableIndex     // Catch:{ IOException -> 0x0288 }
            r4 = r4[r6]     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWReader r6 = r14.mPBWReader     // Catch:{ IOException -> 0x0288 }
            java.lang.String r8 = r4.getFileName()     // Catch:{ IOException -> 0x0288 }
            java.io.InputStream r6 = r6.getInputStreamFile(r8)     // Catch:{ IOException -> 0x0288 }
            r14.mFis = r6     // Catch:{ IOException -> 0x0288 }
            int r6 = r4.getCRC()     // Catch:{ IOException -> 0x0288 }
            r14.mCRC = r6     // Catch:{ IOException -> 0x0288 }
            int r6 = r4.getFileSize()     // Catch:{ IOException -> 0x0288 }
            r14.mBinarySize = r6     // Catch:{ IOException -> 0x0288 }
            r14.mBytesWritten = r3     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r6 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            byte r8 = r4.getType()     // Catch:{ IOException -> 0x0288 }
            int r9 = r14.mInstallSlot     // Catch:{ IOException -> 0x0288 }
            int r10 = r14.mBinarySize     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.devices.pebble.PBWReader r11 = r14.mPBWReader     // Catch:{ IOException -> 0x0288 }
            boolean r11 = r11.isLanguage()     // Catch:{ IOException -> 0x0288 }
            if (r11 == 0) goto L_0x019d
            java.lang.String r11 = "lang"
            goto L_0x019e
        L_0x019d:
            r11 = r5
        L_0x019e:
            byte[] r6 = r6.encodeUploadStart(r8, r9, r10, r11)     // Catch:{ IOException -> 0x0288 }
            r14.writeInstallApp(r6)     // Catch:{ IOException -> 0x0288 }
            r14.mAppInstallToken = r7     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.WAIT_TOKEN     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r6     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x01ac:
            int r4 = r14.mInstallSlot     // Catch:{ IOException -> 0x0288 }
            if (r4 != r7) goto L_0x01b4
            r14.finishInstall(r2)     // Catch:{ IOException -> 0x0288 }
            goto L_0x01be
        L_0x01b4:
            int r4 = r14.mInstallSlot     // Catch:{ IOException -> 0x0288 }
            if (r4 < 0) goto L_0x01be
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.START_INSTALL     // Catch:{ IOException -> 0x0288 }
            r14.mInstallState = r4     // Catch:{ IOException -> 0x0288 }
            goto L_0x0034
        L_0x01be:
            boolean r4 = r14.mIsTCP     // Catch:{ IOException -> 0x0288 }
            if (r4 == 0) goto L_0x01c9
            java.io.InputStream r4 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            r6 = 6
            r4.skip(r6)     // Catch:{ IOException -> 0x0288 }
        L_0x01c9:
            java.io.InputStream r4 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            r6 = 4
            int r4 = r14.readWithException(r4, r1, r3, r6)     // Catch:{ IOException -> 0x0288 }
        L_0x01d0:
            if (r4 >= r6) goto L_0x01dc
            java.io.InputStream r7 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            int r8 = 4 - r4
            int r7 = r14.readWithException(r7, r1, r4, r8)     // Catch:{ IOException -> 0x0288 }
            int r4 = r4 + r7
            goto L_0x01d0
        L_0x01dc:
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.wrap(r1)     // Catch:{ IOException -> 0x0288 }
            java.nio.ByteOrder r8 = java.nio.ByteOrder.BIG_ENDIAN     // Catch:{ IOException -> 0x0288 }
            r7.order(r8)     // Catch:{ IOException -> 0x0288 }
            short r8 = r7.getShort()     // Catch:{ IOException -> 0x0288 }
            short r9 = r7.getShort()     // Catch:{ IOException -> 0x0288 }
            if (r8 < 0) goto L_0x0261
            if (r8 <= r0) goto L_0x01f3
            goto L_0x0261
        L_0x01f3:
            java.io.InputStream r10 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            int r6 = r14.readWithException(r10, r1, r6, r8)     // Catch:{ IOException -> 0x0288 }
            r4 = r6
        L_0x01fa:
            if (r4 >= r8) goto L_0x0208
            java.io.InputStream r6 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            int r10 = r4 + 4
            int r11 = r8 - r4
            int r6 = r14.readWithException(r6, r1, r10, r11)     // Catch:{ IOException -> 0x0288 }
            int r4 = r4 + r6
            goto L_0x01fa
        L_0x0208:
            boolean r6 = r14.mIsTCP     // Catch:{ IOException -> 0x0288 }
            if (r6 == 0) goto L_0x0213
            java.io.InputStream r6 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            r10 = 2
            r6.skip(r10)     // Catch:{ IOException -> 0x0288 }
        L_0x0213:
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r6 = r14.mPebbleProtocol     // Catch:{ IOException -> 0x0288 }
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent[] r6 = r6.decodeResponse(r1)     // Catch:{ IOException -> 0x0288 }
            if (r6 != 0) goto L_0x023f
            org.slf4j.Logger r10 = LOG     // Catch:{ IOException -> 0x0288 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0288 }
            r11.<init>()     // Catch:{ IOException -> 0x0288 }
            java.lang.String r12 = "unhandled message to endpoint "
            r11.append(r12)     // Catch:{ IOException -> 0x0288 }
            r11.append(r9)     // Catch:{ IOException -> 0x0288 }
            java.lang.String r12 = " ("
            r11.append(r12)     // Catch:{ IOException -> 0x0288 }
            r11.append(r8)     // Catch:{ IOException -> 0x0288 }
            java.lang.String r12 = " bytes)"
            r11.append(r12)     // Catch:{ IOException -> 0x0288 }
            java.lang.String r11 = r11.toString()     // Catch:{ IOException -> 0x0288 }
            r10.info(r11)     // Catch:{ IOException -> 0x0288 }
            goto L_0x0256
        L_0x023f:
            int r10 = r6.length     // Catch:{ IOException -> 0x0288 }
            r11 = 0
        L_0x0241:
            if (r11 >= r10) goto L_0x0256
            r12 = r6[r11]     // Catch:{ IOException -> 0x0288 }
            if (r12 != 0) goto L_0x0248
            goto L_0x0253
        L_0x0248:
            boolean r13 = r14.evaluateGBDeviceEventPebble(r12)     // Catch:{ IOException -> 0x0288 }
            if (r13 != 0) goto L_0x0253
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleSupport r13 = r14.mPebbleSupport     // Catch:{ IOException -> 0x0288 }
            r13.evaluateGBDeviceEvent(r12)     // Catch:{ IOException -> 0x0288 }
        L_0x0253:
            int r11 = r11 + 1
            goto L_0x0241
        L_0x0256:
            r10 = 100
            java.lang.Thread.sleep(r10)     // Catch:{ InterruptedException -> 0x025c }
            goto L_0x02bc
        L_0x025c:
            r10 = move-exception
            r10.printStackTrace()     // Catch:{ IOException -> 0x0288 }
            goto L_0x02bc
        L_0x0261:
            org.slf4j.Logger r6 = LOG     // Catch:{ IOException -> 0x0288 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0288 }
            r10.<init>()     // Catch:{ IOException -> 0x0288 }
            java.lang.String r11 = "invalid length "
            r10.append(r11)     // Catch:{ IOException -> 0x0288 }
            r10.append(r8)     // Catch:{ IOException -> 0x0288 }
            java.lang.String r10 = r10.toString()     // Catch:{ IOException -> 0x0288 }
            r6.info(r10)     // Catch:{ IOException -> 0x0288 }
        L_0x0277:
            java.io.InputStream r6 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            int r6 = r6.available()     // Catch:{ IOException -> 0x0288 }
            if (r6 <= 0) goto L_0x0286
            java.io.InputStream r6 = r14.mInStream     // Catch:{ IOException -> 0x0288 }
            int r10 = r1.length     // Catch:{ IOException -> 0x0288 }
            r14.readWithException(r6, r1, r3, r10)     // Catch:{ IOException -> 0x0288 }
            goto L_0x0277
        L_0x0286:
            goto L_0x0034
        L_0x0288:
            r4 = move-exception
            java.lang.String r6 = r4.getMessage()
            if (r6 == 0) goto L_0x02bc
            java.lang.String r6 = r4.getMessage()
            java.lang.String r7 = "broken pipe"
            boolean r6 = r6.equals(r7)
            if (r6 != 0) goto L_0x02a7
            java.lang.String r6 = r4.getMessage()
            java.lang.String r7 = "socket closed"
            boolean r6 = r6.contains(r7)
            if (r6 == 0) goto L_0x02bc
        L_0x02a7:
            org.slf4j.Logger r0 = LOG
            java.lang.String r2 = r4.getMessage()
            r0.info(r2)
            r14.mIsConnected = r3
            r14.mBtSocket = r5
            org.slf4j.Logger r0 = LOG
            java.lang.String r2 = "Bluetooth socket closed, will quit IO Thread"
            r0.info(r2)
            goto L_0x02be
        L_0x02bc:
            goto L_0x0034
        L_0x02be:
            r14.mIsConnected = r3
            android.bluetooth.BluetoothSocket r0 = r14.mBtSocket
            if (r0 == 0) goto L_0x02ce
            r0.close()     // Catch:{ IOException -> 0x02c8 }
            goto L_0x02cc
        L_0x02c8:
            r0 = move-exception
            r0.printStackTrace()
        L_0x02cc:
            r14.mBtSocket = r5
        L_0x02ce:
            r14.enablePebbleKitSupport(r3)
            boolean r0 = r14.mQuit
            if (r0 != 0) goto L_0x02e8
            nodomain.freeyourgadget.gadgetbridge.util.GBPrefs r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getGBPrefs()
            boolean r0 = r0.getAutoReconnect()
            if (r0 != 0) goto L_0x02e0
            goto L_0x02e8
        L_0x02e0:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r14.gbDevice
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice$State r2 = nodomain.freeyourgadget.gadgetbridge.impl.GBDevice.State.WAITING_FOR_RECONNECT
            r0.setState(r2)
            goto L_0x02ef
        L_0x02e8:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r14.gbDevice
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice$State r2 = nodomain.freeyourgadget.gadgetbridge.impl.GBDevice.State.NOT_CONNECTED
            r0.setState(r2)
        L_0x02ef:
            nodomain.freeyourgadget.gadgetbridge.util.GBPrefs r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getGBPrefs()
            boolean r0 = r0.isBackgroundJsEnabled()
            if (r0 == 0) goto L_0x0300
            nodomain.freeyourgadget.gadgetbridge.util.WebViewSingleton r0 = nodomain.freeyourgadget.gadgetbridge.util.WebViewSingleton.getInstance()
            r0.disposeWebView()
        L_0x0300:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r14.gbDevice
            android.content.Context r2 = r14.getContext()
            r0.sendDeviceUpdateIntent(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.run():void");
    }

    private void enablePebbleKitSupport(boolean enable) {
        if (!enable || !this.mEnablePebblekit) {
            PebbleKitSupport pebbleKitSupport = this.mPebbleKitSupport;
            if (pebbleKitSupport != null) {
                pebbleKitSupport.close();
                this.mPebbleKitSupport = null;
                return;
            }
            return;
        }
        this.mPebbleKitSupport = new PebbleKitSupport(getContext(), this, this.mPebbleProtocol);
    }

    private void write_real(byte[] bytes) {
        try {
            if (this.mIsTCP) {
                ByteBuffer buf = ByteBuffer.allocate(bytes.length + 8);
                buf.order(ByteOrder.BIG_ENDIAN);
                buf.putShort(-275);
                buf.putShort(1);
                buf.putShort((short) bytes.length);
                buf.put(bytes);
                buf.putShort(-16657);
                this.mOutStream.write(buf.array());
                this.mOutStream.flush();
            } else {
                this.mOutStream.write(bytes);
                this.mOutStream.flush();
            }
        } catch (IOException e) {
            LOG.error("Error writing.", (Throwable) e);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e2) {
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0021, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void write(byte[] r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r3 != 0) goto L_0x0005
            monitor-exit(r2)
            return
        L_0x0005:
            boolean r0 = r2.mIsConnected     // Catch:{ all -> 0x0022 }
            if (r0 == 0) goto L_0x0020
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r0 = r2.mPebbleProtocol     // Catch:{ all -> 0x0022 }
            int r0 = r0.mFwMajor     // Catch:{ all -> 0x0022 }
            r1 = 3
            if (r0 >= r1) goto L_0x001b
            boolean r0 = r2.mIsInstalling     // Catch:{ all -> 0x0022 }
            if (r0 == 0) goto L_0x001b
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r0 = r2.mInstallState     // Catch:{ all -> 0x0022 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$PebbleAppInstallState r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.PebbleAppInstallState.WAIT_SLOT     // Catch:{ all -> 0x0022 }
            if (r0 == r1) goto L_0x001b
            goto L_0x0020
        L_0x001b:
            r2.write_real(r3)     // Catch:{ all -> 0x0022 }
            monitor-exit(r2)
            return
        L_0x0020:
            monitor-exit(r2)
            return
        L_0x0022:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread.write(byte[]):void");
    }

    private boolean evaluateGBDeviceEventPebble(GBDeviceEvent deviceEvent) {
        if (deviceEvent instanceof GBDeviceEventVersionInfo) {
            if (this.prefs.getBoolean("datetime_synconconnect", true)) {
                LOG.info("syncing time");
                write(this.mPebbleProtocol.encodeSetTime());
            }
            write(this.mPebbleProtocol.encodeEnableAppLogs(this.prefs.getBoolean("pebble_enable_applogs", false)));
            write(this.mPebbleProtocol.encodeReportDataLogSessions());
            this.gbDevice.setState(GBDevice.State.INITIALIZED);
            return false;
        } else if (deviceEvent instanceof GBDeviceEventAppManagement) {
            GBDeviceEventAppManagement appMgmt = (GBDeviceEventAppManagement) deviceEvent;
            int i = C12102.f196xf7223e0b[appMgmt.type.ordinal()];
            if (i == 1) {
                int i2 = C12102.f195xab5d3b31[appMgmt.event.ordinal()];
                if (i2 != 1) {
                    if (i2 == 2) {
                        if (!this.mIsInstalling) {
                            LOG.info("successfully removed app");
                            write(this.mPebbleProtocol.encodeAppInfoReq());
                        } else if (this.mInstallState == PebbleAppInstallState.WAIT_SLOT) {
                            writeInstallApp(this.mPebbleProtocol.encodeAppInfoReq());
                        } else {
                            finishInstall(false);
                            write(this.mPebbleProtocol.encodeAppInfoReq());
                        }
                    }
                } else if (!this.mIsInstalling) {
                    LOG.info("failure removing app");
                } else if (this.mInstallState == PebbleAppInstallState.WAIT_SLOT) {
                    writeInstallApp(this.mPebbleProtocol.encodeAppInfoReq());
                } else {
                    finishInstall(true);
                }
            } else if (i == 2) {
                int i3 = C12102.f195xab5d3b31[appMgmt.event.ordinal()];
                if (i3 == 1) {
                    LOG.info("failure installing app");
                    finishInstall(true);
                } else if (i3 == 2) {
                    setToken(appMgmt.token);
                } else if (i3 == 3) {
                    Logger logger = LOG;
                    logger.info("APPFETCH request: " + appMgmt.uuid + " / " + appMgmt.token);
                    try {
                        File pbwCacheDir = PebbleUtils.getPbwCacheDir();
                        installApp(Uri.fromFile(new File(pbwCacheDir, appMgmt.uuid.toString() + ".pbw")), appMgmt.token);
                    } catch (IOException e) {
                        Logger logger2 = LOG;
                        logger2.error("Error installing app: " + e.getMessage(), (Throwable) e);
                    }
                }
            } else if (i == 3) {
                Logger logger3 = LOG;
                logger3.info("got GBDeviceEventAppManagement START event for uuid: " + appMgmt.uuid);
                if (GBApplication.getGBPrefs().isBackgroundJsEnabled()) {
                    if (this.mPebbleProtocol.hasAppMessageHandler(appMgmt.uuid)) {
                        WebViewSingleton.getInstance().stopJavascriptInterface();
                    } else {
                        WebViewSingleton.getInstance().runJavascriptInterface(this.gbDevice, appMgmt.uuid);
                    }
                }
                this.mPebbleActiveAppTracker.markAppOpened(appMgmt.uuid);
            } else if (i == 4) {
                this.mPebbleActiveAppTracker.markAppClosed(appMgmt.uuid);
            }
            return true;
        } else if (deviceEvent instanceof GBDeviceEventAppInfo) {
            LOG.info("Got event for APP_INFO");
            setInstallSlot(((GBDeviceEventAppInfo) deviceEvent).freeSlot);
            return false;
        } else {
            if (deviceEvent instanceof GBDeviceEventAppMessage) {
                if (GBApplication.getGBPrefs().isBackgroundJsEnabled()) {
                    sendAppMessageJS((GBDeviceEventAppMessage) deviceEvent);
                }
                if (this.mEnablePebblekit) {
                    LOG.info("Got AppMessage event");
                    if (this.mPebbleKitSupport != null && ((GBDeviceEventAppMessage) deviceEvent).type == GBDeviceEventAppMessage.TYPE_APPMESSAGE) {
                        this.mPebbleKitSupport.sendAppMessageIntent((GBDeviceEventAppMessage) deviceEvent);
                    }
                }
            } else if ((deviceEvent instanceof GBDeviceEventDataLogging) && this.mEnablePebblekit) {
                LOG.info("Got Datalogging event");
                PebbleKitSupport pebbleKitSupport = this.mPebbleKitSupport;
                if (pebbleKitSupport != null) {
                    pebbleKitSupport.sendDataLoggingIntent((GBDeviceEventDataLogging) deviceEvent);
                }
            }
            return false;
        }
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread$2 */
    static /* synthetic */ class C12102 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$deviceevents$GBDeviceEventAppManagement$Event */
        static final /* synthetic */ int[] f195xab5d3b31 = new int[GBDeviceEventAppManagement.Event.values().length];

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$deviceevents$GBDeviceEventAppManagement$EventType */
        static final /* synthetic */ int[] f196xf7223e0b = new int[GBDeviceEventAppManagement.EventType.values().length];

        static {
            try {
                f196xf7223e0b[GBDeviceEventAppManagement.EventType.DELETE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f196xf7223e0b[GBDeviceEventAppManagement.EventType.INSTALL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f196xf7223e0b[GBDeviceEventAppManagement.EventType.START.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f196xf7223e0b[GBDeviceEventAppManagement.EventType.STOP.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f195xab5d3b31[GBDeviceEventAppManagement.Event.FAILURE.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f195xab5d3b31[GBDeviceEventAppManagement.Event.SUCCESS.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f195xab5d3b31[GBDeviceEventAppManagement.Event.REQUEST.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            f197xf0e4bdee = new int[PebbleAppInstallState.values().length];
            try {
                f197xf0e4bdee[PebbleAppInstallState.WAIT_SLOT.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f197xf0e4bdee[PebbleAppInstallState.START_INSTALL.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f197xf0e4bdee[PebbleAppInstallState.WAIT_TOKEN.ordinal()] = 3;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f197xf0e4bdee[PebbleAppInstallState.UPLOAD_CHUNK.ordinal()] = 4;
            } catch (NoSuchFieldError e11) {
            }
            try {
                f197xf0e4bdee[PebbleAppInstallState.UPLOAD_COMMIT.ordinal()] = 5;
            } catch (NoSuchFieldError e12) {
            }
            try {
                f197xf0e4bdee[PebbleAppInstallState.WAIT_COMMIT.ordinal()] = 6;
            } catch (NoSuchFieldError e13) {
            }
            try {
                f197xf0e4bdee[PebbleAppInstallState.UPLOAD_COMPLETE.ordinal()] = 7;
            } catch (NoSuchFieldError e14) {
            }
            try {
                f197xf0e4bdee[PebbleAppInstallState.APP_REFRESH.ordinal()] = 8;
            } catch (NoSuchFieldError e15) {
            }
        }
    }

    private void setToken(int token) {
        this.mAppInstallToken = token;
    }

    private void setInstallSlot(int slot) {
        if (this.mIsInstalling) {
            this.mInstallSlot = slot;
        }
    }

    private synchronized void writeInstallApp(byte[] bytes) {
        if (this.mIsInstalling) {
            Logger logger = LOG;
            logger.info("got " + bytes.length + "bytes for writeInstallApp()");
            write_real(bytes);
        }
    }

    /* access modifiers changed from: package-private */
    public void installApp(Uri uri, int appId) {
        if (!this.mIsInstalling) {
            try {
                this.mPBWReader = new PBWReader(uri, getContext(), PebbleUtils.getPlatformName(this.gbDevice.getModel()));
                this.mPebbleInstallables = this.mPBWReader.getPebbleInstallables();
                this.mCurrentInstallableIndex = 0;
                if (this.mPBWReader.isFirmware()) {
                    LOG.info("starting firmware installation");
                    this.mIsInstalling = true;
                    this.mInstallSlot = 0;
                    writeInstallApp(this.mPebbleProtocol.encodeInstallFirmwareStart());
                    this.mInstallState = PebbleAppInstallState.START_INSTALL;
                    writeInstallApp(this.mPebbleProtocol.encodeGetTime());
                    return;
                }
                this.mCurrentlyInstallingApp = this.mPBWReader.getGBDeviceApp();
                if (this.mPebbleProtocol.mFwMajor < 3 || this.mPBWReader.isLanguage()) {
                    this.mIsInstalling = true;
                    if (this.mPBWReader.isLanguage()) {
                        this.mInstallSlot = 0;
                        this.mInstallState = PebbleAppInstallState.START_INSTALL;
                        writeInstallApp(this.mPebbleProtocol.encodeGetTime());
                        return;
                    }
                    this.mInstallState = PebbleAppInstallState.WAIT_SLOT;
                    writeInstallApp(this.mPebbleProtocol.encodeAppDelete(this.mCurrentlyInstallingApp.getUUID()));
                } else if (appId == 0) {
                    write(this.mPebbleProtocol.encodeInstallMetadata(this.mCurrentlyInstallingApp.getUUID(), this.mCurrentlyInstallingApp.getName(), this.mPBWReader.getAppVersion(), this.mPBWReader.getSdkVersion(), this.mPBWReader.getFlags(), this.mPBWReader.getIconId()));
                    write(this.mPebbleProtocol.encodeAppStart(this.mCurrentlyInstallingApp.getUUID(), true));
                } else {
                    this.mIsInstalling = true;
                    this.mInstallSlot = appId;
                    this.mInstallState = PebbleAppInstallState.START_INSTALL;
                    writeInstallApp(this.mPebbleProtocol.encodeAppFetchAck());
                }
            } catch (FileNotFoundException e) {
                Logger logger = LOG;
                logger.warn("file not found: " + e.getMessage(), (Throwable) e);
            } catch (IOException e2) {
                Logger logger2 = LOG;
                logger2.warn("unable to read file: " + e2.getMessage(), (Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void reopenLastApp(UUID assumedCurrentApp) {
        UUID currentApp = this.mPebbleActiveAppTracker.getCurrentRunningApp();
        UUID previousApp = this.mPebbleActiveAppTracker.getPreviousRunningApp();
        if (previousApp == null || !assumedCurrentApp.equals(currentApp)) {
            write(this.mPebbleProtocol.encodeAppStart(assumedCurrentApp, false));
        } else {
            write(this.mPebbleProtocol.encodeAppStart(previousApp, true));
        }
    }

    private void finishInstall(boolean hadError) {
        int i;
        GBDeviceApp gBDeviceApp;
        String filenameSuffix;
        if (this.mIsInstalling) {
            if (hadError) {
                C1238GB.updateInstallNotification(getContext().getString(C0889R.string.installation_failed_), false, 0, getContext());
            } else {
                C1238GB.updateInstallNotification(getContext().getString(C0889R.string.installation_successful), false, 0, getContext());
                if (this.mPebbleProtocol.mFwMajor >= 3 && (gBDeviceApp = this.mCurrentlyInstallingApp) != null) {
                    if (gBDeviceApp.getType() == GBDeviceApp.Type.WATCHFACE) {
                        filenameSuffix = ".watchfaces";
                    } else {
                        filenameSuffix = ".watchapps";
                    }
                    AppManagerActivity.addToAppOrderFile(this.gbDevice.getAddress() + filenameSuffix, this.mCurrentlyInstallingApp.getUUID());
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(AbstractAppManagerFragment.ACTION_REFRESH_APPLIST));
                }
            }
            this.mInstallState = PebbleAppInstallState.UNKNOWN;
            if (hadError && (i = this.mAppInstallToken) != -1) {
                writeInstallApp(this.mPebbleProtocol.encodeUploadCancel(i));
            }
            this.mPBWReader = null;
            this.mIsInstalling = false;
            this.mCurrentlyInstallingApp = null;
            InputStream inputStream = this.mFis;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            this.mFis = null;
            this.mAppInstallToken = -1;
            this.mInstallSlot = -2;
        }
    }

    public void quit() {
        this.mQuit = true;
        BluetoothSocket bluetoothSocket = this.mBtSocket;
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
            this.mBtSocket = null;
        }
        Socket socket = this.mTCPSocket;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e2) {
            }
            this.mTCPSocket = null;
        }
        PebbleLESupport pebbleLESupport = this.mPebbleLESupport;
        if (pebbleLESupport != null) {
            pebbleLESupport.close();
            this.mPebbleLESupport = null;
        }
    }
}
