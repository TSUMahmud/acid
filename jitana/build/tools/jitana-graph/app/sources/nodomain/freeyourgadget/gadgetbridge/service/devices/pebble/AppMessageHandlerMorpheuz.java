package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleMorpheuzSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMorpheuzSample;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppMessageHandlerMorpheuz extends AppMessageHandler {
    private static final int CTRL_DO_NEXT = 8;
    private static final int CTRL_GONEOFF_DONE = 4;
    private static final int CTRL_LAZARUS = 32;
    private static final int CTRL_SET_LAST_SENT = 16;
    private static final int CTRL_SNOOZES_DONE = 64;
    private static final int CTRL_TRANSMIT_DONE = 1;
    private static final int CTRL_VERSION_DONE = 2;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AppMessageHandlerMorpheuz.class);
    private int alarm_gone_off = -1;
    private Integer keyAutoReset;
    private Integer keyBase;
    private Integer keyCtrl;
    private Integer keyFault;
    private Integer keyFrom;
    private Integer keyGoneoff;
    private Integer keyPoint;
    private Integer keySnoozes;
    private Integer keyTo;
    private Integer keyTransmit;
    private Integer keyVersion;
    private int recording_base_timestamp = -1;
    private int smartalarm_from = -1;
    private int smartalarm_to = -1;

    AppMessageHandlerMorpheuz(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
        try {
            JSONObject appKeys = getAppKeys();
            this.keyPoint = Integer.valueOf(appKeys.getInt("keyPoint"));
            this.keyCtrl = Integer.valueOf(appKeys.getInt("keyCtrl"));
            this.keyFrom = Integer.valueOf(appKeys.getInt("keyFrom"));
            this.keyTo = Integer.valueOf(appKeys.getInt("keyTo"));
            this.keyBase = Integer.valueOf(appKeys.getInt("keyBase"));
            this.keyVersion = Integer.valueOf(appKeys.getInt("keyVersion"));
            this.keyGoneoff = Integer.valueOf(appKeys.getInt("keyGoneoff"));
            this.keyTransmit = Integer.valueOf(appKeys.getInt("keyTransmit"));
            this.keyAutoReset = Integer.valueOf(appKeys.getInt("keyAutoReset"));
            this.keySnoozes = Integer.valueOf(appKeys.getInt("keySnoozes"));
            this.keyFault = Integer.valueOf(appKeys.getInt("keyFault"));
        } catch (JSONException e) {
            C1238GB.toast("There was an error accessing the morpheuz watchapp configuration.", 1, 3);
        } catch (IOException e2) {
        }
    }

    private byte[] encodeMorpheuzMessage(int key, int value) {
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        pairs.add(new Pair(Integer.valueOf(key), Integer.valueOf(value)));
        return this.mPebbleProtocol.encodeApplicationMessagePush(48, this.mUUID, pairs, (Integer) null);
    }

    public boolean isEnabled() {
        return GBApplication.getPrefs().getBoolean("pebble_sync_morpheuz", true);
    }

    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        Throwable th;
        Iterator<Pair<Integer, Object>> it = pairs.iterator();
        int ctrl_message = 0;
        while (it.hasNext() != 0) {
            Pair<Integer, Object> pair = it.next();
            if (Objects.equals(pair.first, this.keyTransmit)) {
                C1238GB.signalActivityDataFinish();
                ctrl_message |= 1;
            } else if (((Integer) pair.first).equals(this.keyGoneoff)) {
                this.alarm_gone_off = ((Integer) pair.second).intValue();
                LOG.info("got gone off: " + (this.alarm_gone_off / 60) + ":" + (this.alarm_gone_off % 60));
                ctrl_message |= 12;
            } else if (((Integer) pair.first).equals(this.keyPoint)) {
                if (this.recording_base_timestamp == -1) {
                    ctrl_message = 23;
                } else {
                    int index = ((Integer) pair.second).intValue() >> 16;
                    int intensity = 65535 & ((Integer) pair.second).intValue();
                    LOG.info("got point:" + index + StringUtils.SPACE + intensity);
                    if (index >= 0) {
                        try {
                            DBHandler db = GBApplication.acquireDB();
                            try {
                                Long userId = DBHelper.getUser(db.getDaoSession()).getId();
                                Long deviceId = DBHelper.getDevice(getDevice(), db.getDaoSession()).getId();
                                PebbleMorpheuzSampleProvider sampleProvider = new PebbleMorpheuzSampleProvider(getDevice(), db.getDaoSession());
                                long longValue = deviceId.longValue();
                                long longValue2 = userId.longValue();
                                Long l = userId;
                                PebbleMorpheuzSampleProvider sampleProvider2 = sampleProvider;
                                PebbleMorpheuzSample sample = new PebbleMorpheuzSample((index * 600) + this.recording_base_timestamp, longValue, longValue2, intensity);
                                sample.setProvider(sampleProvider2);
                                sampleProvider2.addGBActivitySample(sample);
                                if (db != null) {
                                    db.close();
                                }
                            } catch (Throwable th2) {
                                Throwable th3 = th2;
                                if (db != null) {
                                    db.close();
                                }
                                throw th3;
                            }
                        } catch (Exception e) {
                            LOG.error("Error acquiring database", (Throwable) e);
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    }
                    ctrl_message |= 24;
                }
            } else if (((Integer) pair.first).equals(this.keyFrom)) {
                this.smartalarm_from = ((Integer) pair.second).intValue();
                LOG.info("got from: " + (this.smartalarm_from / 60) + ":" + (this.smartalarm_from % 60));
                ctrl_message |= 24;
            } else if (((Integer) pair.first).equals(this.keyTo)) {
                this.smartalarm_to = ((Integer) pair.second).intValue();
                LOG.info("got to: " + (this.smartalarm_to / 60) + ":" + (this.smartalarm_to % 60));
                ctrl_message |= 24;
            } else if (((Integer) pair.first).equals(this.keyVersion)) {
                int version = ((Integer) pair.second).intValue();
                LOG.info("got version: " + (((float) version) / 10.0f));
                ctrl_message |= 2;
            } else if (((Integer) pair.first).equals(this.keyBase)) {
                this.recording_base_timestamp = ((Integer) pair.second).intValue();
                if (this.mPebbleProtocol.mFwMajor < 3) {
                    this.recording_base_timestamp -= SimpleTimeZone.getDefault().getOffset(((long) this.recording_base_timestamp) * 1000) / 1000;
                }
                LOG.info("got base: " + this.recording_base_timestamp);
                ctrl_message |= 24;
            } else if (((Integer) pair.first).equals(this.keyAutoReset)) {
                ctrl_message |= 24;
            } else if (((Integer) pair.first).equals(this.keySnoozes)) {
                ctrl_message |= 72;
            } else if (((Integer) pair.first).equals(this.keyFault)) {
                LOG.info("fault code: " + ((Integer) pair.second).intValue());
                ctrl_message |= 8;
            } else {
                LOG.info("unhandled key: " + pair.first);
            }
        }
        GBDeviceEventSendBytes sendBytesAck = new GBDeviceEventSendBytes();
        sendBytesAck.encodedBytes = this.mPebbleProtocol.encodeApplicationMessageAck(this.mUUID, this.mPebbleProtocol.last_id);
        GBDeviceEventSendBytes sendBytesCtrl = null;
        if (ctrl_message > 0) {
            sendBytesCtrl = new GBDeviceEventSendBytes();
            sendBytesCtrl.encodedBytes = encodeMorpheuzMessage(this.keyCtrl.intValue(), ctrl_message);
        }
        return new GBDeviceEvent[]{sendBytesAck, sendBytesCtrl};
    }
}
