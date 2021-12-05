package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Base64;
import cyanogenmod.app.ProfileManager;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppMessage;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.pebble.GBDeviceEventDataLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PebbleKitSupport {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleKitSupport.class);
    private static final String PEBBLEKIT_ACTION_APP_ACK = "com.getpebble.action.app.ACK";
    private static final String PEBBLEKIT_ACTION_APP_NACK = "com.getpebble.action.app.NACK";
    private static final String PEBBLEKIT_ACTION_APP_RECEIVE = "com.getpebble.action.app.RECEIVE";
    private static final String PEBBLEKIT_ACTION_APP_RECEIVE_ACK = "com.getpebble.action.app.RECEIVE_ACK";
    private static final String PEBBLEKIT_ACTION_APP_SEND = "com.getpebble.action.app.SEND";
    private static final String PEBBLEKIT_ACTION_APP_START = "com.getpebble.action.app.START";
    private static final String PEBBLEKIT_ACTION_APP_STOP = "com.getpebble.action.app.STOP";
    private static final String PEBBLEKIT_ACTION_DL_ACK_DATA = "com.getpebble.action.dl.ACK_DATA";
    private static final String PEBBLEKIT_ACTION_DL_FINISH_SESSION = "com.getpebble.action.dl.FINISH_SESSION_NEW";
    private static final String PEBBLEKIT_ACTION_DL_RECEIVE_DATA_NEW = "com.getpebble.action.dl.RECEIVE_DATA_NEW";
    private static final String PEBBLEKIT_EXTRA_REOPEN_LAST_APP = "com.getpebble.action.app.REOPEN_LAST_APP";
    private int dataLogTransactionId = 1;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final PebbleIoThread mPebbleIoThread;
    private final BroadcastReceiver mPebbleKitReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r12, android.content.Intent r13) {
            /*
                r11 = this;
                java.lang.String r0 = r13.getAction()
                if (r0 != 0) goto L_0x0010
                org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.LOG
                java.lang.String r2 = "got empty action from PebbleKit Intent - ignoring"
                r1.warn(r2)
                return
            L_0x0010:
                org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.LOG
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Got action: "
                r2.append(r3)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                r1.info(r2)
                int r1 = r0.hashCode()
                r2 = 0
                java.lang.String r3 = "com.getpebble.action.app.STOP"
                java.lang.String r4 = "com.getpebble.action.app.START"
                r5 = 4
                r6 = 3
                r7 = 2
                r8 = 1
                r9 = -1
                switch(r1) {
                    case -1587964492: goto L_0x0060;
                    case -1396082085: goto L_0x0056;
                    case -1094330331: goto L_0x004c;
                    case -328333354: goto L_0x0042;
                    case -328318896: goto L_0x003a;
                    default: goto L_0x0039;
                }
            L_0x0039:
                goto L_0x0068
            L_0x003a:
                boolean r1 = r0.equals(r3)
                if (r1 == 0) goto L_0x0039
                r1 = 1
                goto L_0x0069
            L_0x0042:
                java.lang.String r1 = "com.getpebble.action.app.SEND"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0039
                r1 = 2
                goto L_0x0069
            L_0x004c:
                java.lang.String r1 = "com.getpebble.action.dl.ACK_DATA"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0039
                r1 = 4
                goto L_0x0069
            L_0x0056:
                java.lang.String r1 = "com.getpebble.action.app.ACK"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0039
                r1 = 3
                goto L_0x0069
            L_0x0060:
                boolean r1 = r0.equals(r4)
                if (r1 == 0) goto L_0x0039
                r1 = 0
                goto L_0x0069
            L_0x0068:
                r1 = -1
            L_0x0069:
                java.lang.String r10 = "uuid"
                if (r1 == 0) goto L_0x0132
                if (r1 == r8) goto L_0x0132
                java.lang.String r2 = "transaction_id"
                if (r1 == r7) goto L_0x00e1
                if (r1 == r6) goto L_0x009c
                if (r1 == r5) goto L_0x0091
                org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.LOG
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Unhandled PebbleKit action: "
                r2.append(r3)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                r1.warn(r2)
                goto L_0x0169
            L_0x0091:
                org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.LOG
                java.lang.String r2 = "GOT DL DATA ACK"
                r1.info(r2)
                goto L_0x0169
            L_0x009c:
                int r1 = r13.getIntExtra(r2, r9)
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r2 = r2.mPebbleProtocol
                boolean r2 = r2.mAlwaysACKPebbleKit
                if (r2 != 0) goto L_0x0169
                if (r1 < 0) goto L_0x00c7
                r2 = 255(0xff, float:3.57E-43)
                if (r1 > r2) goto L_0x00c7
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread r2 = r2.mPebbleIoThread
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r3 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r3 = r3.mPebbleProtocol
                r4 = 0
                byte r5 = (byte) r1
                byte[] r3 = r3.encodeApplicationMessageAck(r4, r5)
                r2.write(r3)
                goto L_0x0169
            L_0x00c7:
                org.slf4j.Logger r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.LOG
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "illegal transaction id "
                r3.append(r4)
                r3.append(r1)
                java.lang.String r3 = r3.toString()
                r2.warn(r3)
                goto L_0x0169
            L_0x00e1:
                int r1 = r13.getIntExtra(r2, r9)
                java.io.Serializable r2 = r13.getSerializableExtra(r10)
                java.util.UUID r2 = (java.util.UUID) r2
                java.lang.String r3 = "msg_data"
                java.lang.String r3 = r13.getStringExtra(r3)
                org.slf4j.Logger r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.LOG
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "json string: "
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                r4.info(r5)
                org.json.JSONArray r4 = new org.json.JSONArray     // Catch:{ JSONException -> 0x0127 }
                r4.<init>(r3)     // Catch:{ JSONException -> 0x0127 }
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r5 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this     // Catch:{ JSONException -> 0x0127 }
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread r5 = r5.mPebbleIoThread     // Catch:{ JSONException -> 0x0127 }
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this     // Catch:{ JSONException -> 0x0127 }
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r6 = r6.mPebbleProtocol     // Catch:{ JSONException -> 0x0127 }
                byte[] r6 = r6.encodeApplicationMessageFromJSON(r2, r4)     // Catch:{ JSONException -> 0x0127 }
                r5.write(r6)     // Catch:{ JSONException -> 0x0127 }
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r5 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this     // Catch:{ JSONException -> 0x0127 }
                r5.sendAppMessageAck(r1)     // Catch:{ JSONException -> 0x0127 }
                goto L_0x0169
            L_0x0127:
                r4 = move-exception
                org.slf4j.Logger r5 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.LOG
                java.lang.String r6 = "failed decoding JSON"
                r5.error((java.lang.String) r6, (java.lang.Throwable) r4)
                goto L_0x0169
            L_0x0132:
                java.io.Serializable r1 = r13.getSerializableExtra(r10)
                java.util.UUID r1 = (java.util.UUID) r1
                if (r1 == 0) goto L_0x0169
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0152
                java.lang.String r3 = "com.getpebble.action.app.REOPEN_LAST_APP"
                boolean r2 = r13.getBooleanExtra(r3, r2)
                if (r2 == 0) goto L_0x0152
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread r2 = r2.mPebbleIoThread
                r2.reopenLastApp(r1)
                goto L_0x0169
            L_0x0152:
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleIoThread r2 = r2.mPebbleIoThread
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport r3 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.this
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r3 = r3.mPebbleProtocol
                boolean r4 = r0.equals(r4)
                byte[] r3 = r3.encodeAppStart(r1, r4)
                r2.write(r3)
            L_0x0169:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleKitSupport.C12111.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    /* access modifiers changed from: private */
    public final PebbleProtocol mPebbleProtocol;

    PebbleKitSupport(Context context, PebbleIoThread pebbleIoThread, PebbleProtocol pebbleProtocol) {
        this.mContext = context;
        this.mPebbleIoThread = pebbleIoThread;
        this.mPebbleProtocol = pebbleProtocol;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PEBBLEKIT_ACTION_APP_ACK);
        intentFilter.addAction(PEBBLEKIT_ACTION_APP_NACK);
        intentFilter.addAction(PEBBLEKIT_ACTION_APP_SEND);
        intentFilter.addAction(PEBBLEKIT_ACTION_APP_START);
        intentFilter.addAction(PEBBLEKIT_ACTION_APP_STOP);
        intentFilter.addAction(PEBBLEKIT_ACTION_DL_ACK_DATA);
        this.mContext.registerReceiver(this.mPebbleKitReceiver, intentFilter);
    }

    /* access modifiers changed from: package-private */
    public void sendAppMessageIntent(GBDeviceEventAppMessage appMessage) {
        Intent intent = new Intent();
        intent.setAction(PEBBLEKIT_ACTION_APP_RECEIVE);
        intent.putExtra(ProfileManager.EXTRA_PROFILE_UUID, appMessage.appUUID);
        intent.putExtra("msg_data", appMessage.message);
        intent.putExtra("transaction_id", appMessage.f124id);
        Logger logger = LOG;
        logger.info("broadcasting to uuid " + appMessage.appUUID + " transaction id: " + appMessage.f124id + " JSON: " + appMessage.message);
        this.mContext.sendBroadcast(intent);
    }

    /* access modifiers changed from: private */
    public void sendAppMessageAck(int transactionId) {
        Intent intent = new Intent();
        intent.setAction(PEBBLEKIT_ACTION_APP_RECEIVE_ACK);
        intent.putExtra("transaction_id", transactionId);
        Logger logger = LOG;
        logger.info("broadcasting ACK (transaction id " + transactionId + ")");
        this.mContext.sendBroadcast(intent);
    }

    /* access modifiers changed from: package-private */
    public void close() {
        try {
            this.mContext.unregisterReceiver(this.mPebbleKitReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void sendDataLoggingIntent(GBDeviceEventDataLogging dataLogging) {
        Intent intent = new Intent();
        intent.putExtra("data_log_timestamp", dataLogging.timestamp);
        intent.putExtra(ProfileManager.EXTRA_PROFILE_UUID, dataLogging.appUUID);
        intent.putExtra("data_log_uuid", dataLogging.appUUID);
        intent.putExtra("data_log_tag", dataLogging.tag);
        int i = dataLogging.command;
        if (i == 1) {
            intent.setAction(PEBBLEKIT_ACTION_DL_RECEIVE_DATA_NEW);
            intent.putExtra("pbl_data_type", dataLogging.pebbleDataType);
            for (Object dataObject : dataLogging.data) {
                int i2 = this.dataLogTransactionId;
                this.dataLogTransactionId = i2 + 1;
                intent.putExtra("pbl_data_id", i2);
                byte b = dataLogging.pebbleDataType;
                if (b == 0) {
                    intent.putExtra("pbl_data_object", Base64.encodeToString((byte[]) dataObject, 2));
                } else if (b == 2) {
                    intent.putExtra("pbl_data_object", (Long) dataObject);
                } else if (b == 3) {
                    intent.putExtra("pbl_data_object", (Integer) dataObject);
                }
                LOG.info("broadcasting datalogging to uuid " + dataLogging.appUUID + " tag: " + dataLogging.tag + "transaction id: " + this.dataLogTransactionId + " type: " + dataLogging.pebbleDataType);
                this.mContext.sendBroadcast(intent);
            }
        } else if (i != 2) {
            LOG.warn("invalid datalog command");
        } else {
            intent.setAction(PEBBLEKIT_ACTION_DL_FINISH_SESSION);
            LOG.info("broadcasting datalogging finish session to uuid " + dataLogging.appUUID + " tag: " + dataLogging.tag);
            this.mContext.sendBroadcast(intent);
        }
    }
}
