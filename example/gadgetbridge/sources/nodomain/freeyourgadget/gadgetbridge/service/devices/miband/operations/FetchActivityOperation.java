package nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandDateConverter;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandService;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceBusyAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchActivityOperation extends AbstractMiBand1Operation {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) FetchActivityOperation.class);
    private static final byte[] fetch = {6};
    private final int activityMetadataLength = 11;
    private ActivityStruct activityStruct;
    private final boolean hasExtendedActivityData;
    private final boolean hasPacketCounter;

    private class ActivityStruct {
        /* access modifiers changed from: private */
        public final byte[] activityDataHolder;
        /* access modifiers changed from: private */
        public int activityDataHolderProgress = 0;
        private final int activityDataHolderSize;
        /* access modifiers changed from: private */
        public int activityDataRemainingBytes = 0;
        /* access modifiers changed from: private */
        public GregorianCalendar activityDataTimestampProgress = null;
        /* access modifiers changed from: private */
        public GregorianCalendar activityDataTimestampToAck = null;
        /* access modifiers changed from: private */
        public int activityDataUntilNextHeader = 0;
        /* access modifiers changed from: private */
        public int lastNotifiedProgress;
        private int maxDataPacketLength;

        ActivityStruct(int activityDataHolderSize2, int maxDataPacketLength2) {
            this.activityDataHolderSize = activityDataHolderSize2;
            this.maxDataPacketLength = maxDataPacketLength2;
            this.activityDataHolder = new byte[activityDataHolderSize2];
        }

        /* access modifiers changed from: package-private */
        public boolean hasRoomFor(byte[] value) {
            return this.activityDataRemainingBytes >= value.length;
        }

        /* access modifiers changed from: package-private */
        public boolean isValidData(byte[] value) {
            return value.length == this.maxDataPacketLength || value.length == this.activityDataRemainingBytes;
        }

        /* access modifiers changed from: package-private */
        public boolean isBufferFull() {
            return this.activityDataHolderSize == this.activityDataHolderProgress;
        }

        public void buffer(byte[] value) {
            System.arraycopy(value, 0, this.activityDataHolder, this.activityDataHolderProgress, value.length);
            this.activityDataHolderProgress += value.length;
            this.activityDataRemainingBytes -= value.length;
            validate();
        }

        private void validate() {
            C1238GB.assertThat(this.activityDataRemainingBytes >= 0, "Illegal state, remaining bytes is negative");
        }

        /* access modifiers changed from: package-private */
        public boolean isFirstChunk() {
            return this.activityDataTimestampProgress == null;
        }

        /* access modifiers changed from: package-private */
        public void startNewBlock(GregorianCalendar timestamp, int dataUntilNextHeader) {
            C1238GB.assertThat(timestamp != null, "Timestamp must not be null");
            if (isFirstChunk()) {
                this.activityDataTimestampProgress = timestamp;
            } else if (timestamp.getTimeInMillis() >= this.activityDataTimestampProgress.getTimeInMillis()) {
                this.activityDataTimestampProgress = timestamp;
            } else {
                Logger access$000 = FetchActivityOperation.LOG;
                access$000.warn("Got bogus timestamp: " + timestamp.getTime() + " that is smaller than the previous timestamp: " + this.activityDataTimestampProgress.getTime());
            }
            this.activityDataTimestampToAck = (GregorianCalendar) timestamp.clone();
            this.activityDataUntilNextHeader = dataUntilNextHeader;
            this.activityDataRemainingBytes = dataUntilNextHeader;
            validate();
        }

        /* access modifiers changed from: package-private */
        public boolean isBlockFinished() {
            return this.activityDataRemainingBytes == 0;
        }

        /* access modifiers changed from: package-private */
        public void bufferFlushed(int minutes) {
            this.activityDataTimestampProgress.add(12, minutes);
            this.activityDataHolderProgress = 0;
            this.lastNotifiedProgress = 0;
        }
    }

    public FetchActivityOperation(MiBandSupport support) {
        super(support);
        this.hasExtendedActivityData = support.getDeviceInfo().supportsHeartrate();
        this.hasPacketCounter = support.getDeviceInfo().getProfileVersion() >= 33556224;
        this.activityStruct = new ActivityStruct(getBytesPerMinuteOfActivityData() * 60 * 4, this.hasPacketCounter ? this.hasExtendedActivityData ? 16 : 18 : 20);
    }

    /* access modifiers changed from: protected */
    public void enableNeededNotifications(TransactionBuilder builder, boolean enable) {
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        TransactionBuilder builder = performInitialized("fetch activity data");
        ((MiBandSupport) getSupport()).setLowLatency(builder);
        builder.add(new SetDeviceBusyAction(getDevice(), getContext().getString(C0889R.string.busy_task_fetch_activity_data), getContext()));
        builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), fetch);
        builder.queue(getQueue());
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (!MiBandService.UUID_CHARACTERISTIC_ACTIVITY_DATA.equals(characteristic.getUuid())) {
            return super.onCharacteristicChanged(gatt, characteristic);
        }
        handleActivityNotif(characteristic.getValue());
        return true;
    }

    private void handleActivityFetchFinish() throws IOException {
        LOG.info("Fetching activity data has finished.");
        this.activityStruct = null;
        operationFinished();
        unsetBusy();
        C1238GB.signalActivityDataFinish();
    }

    private void handleActivityNotif(byte[] value) {
        if (!isOperationRunning()) {
            Logger logger = LOG;
            logger.error("ignoring activity data notification because operation is not running. Data length: " + value.length);
            ((MiBandSupport) getSupport()).logMessageContent(value);
            return;
        }
        if (value.length == 11) {
            handleActivityMetadata(value);
        } else if (this.hasPacketCounter) {
            byte[] valueChopped = new byte[(value.length - 1)];
            System.arraycopy(value, 1, valueChopped, 0, value.length - 1);
            bufferActivityData(valueChopped);
        } else {
            bufferActivityData(value);
        }
        if (LOG.isDebugEnabled()) {
            Logger logger2 = LOG;
            logger2.debug("activity data: length: " + value.length + ", remaining bytes: " + this.activityStruct.activityDataRemainingBytes);
        }
        int progress = (int) ((((float) (this.activityStruct.activityDataUntilNextHeader - this.activityStruct.activityDataRemainingBytes)) / ((float) this.activityStruct.activityDataUntilNextHeader)) * 100.0f);
        if (progress - this.activityStruct.lastNotifiedProgress >= 8) {
            int unused = this.activityStruct.lastNotifiedProgress = progress;
            C1238GB.updateTransferNotification((String) null, getContext().getString(C0889R.string.busy_task_fetch_activity_data), true, progress, getContext());
        }
        if (this.activityStruct.isBlockFinished()) {
            sendAckDataTransfer(this.activityStruct.activityDataTimestampToAck, this.activityStruct.activityDataUntilNextHeader);
            C1238GB.updateTransferNotification((String) null, "", false, 100, getContext());
        }
    }

    private void handleActivityMetadata(byte[] value) {
        if (value.length == 11) {
            byte dataType = value[0];
            GregorianCalendar timestamp = MiBandDateConverter.rawBytesToCalendar(value, 1, getDevice().getAddress());
            int totalDataToRead = ((value[7] & 255) | ((value[8] & 255) << 8)) * (dataType == 1 ? getBytesPerMinuteOfActivityData() : 1);
            int dataUntilNextHeader = (((value[10] & 255) << 8) | (value[9] & 255)) * (dataType == 1 ? getBytesPerMinuteOfActivityData() : 1);
            if (this.activityStruct.isFirstChunk() && dataUntilNextHeader != 0) {
                C1238GB.updateTransferNotification(getContext().getString(C0889R.string.busy_task_fetch_activity_data), getContext().getString(C0889R.string.user_feedback_miband_activity_data_transfer, new Object[]{DateTimeUtils.formatDurationHoursMinutes((long) (totalDataToRead / getBytesPerMinuteOfActivityData()), TimeUnit.MINUTES), DateFormat.getDateTimeInstance().format(timestamp.getTime())}), true, 0, getContext());
            }
            Logger logger = LOG;
            logger.info("total data to read: " + totalDataToRead + " len: " + (totalDataToRead / getBytesPerMinuteOfActivityData()) + " minute(s)");
            Logger logger2 = LOG;
            logger2.info("data to read until next header: " + dataUntilNextHeader + " len: " + (dataUntilNextHeader / getBytesPerMinuteOfActivityData()) + " minute(s)");
            Logger logger3 = LOG;
            logger3.info("TIMESTAMP: " + DateFormat.getDateTimeInstance().format(timestamp.getTime()) + " magic byte: " + dataUntilNextHeader);
            this.activityStruct.startNewBlock(timestamp, dataUntilNextHeader);
        }
    }

    private int getBytesPerMinuteOfActivityData() {
        return this.hasExtendedActivityData ? 4 : 3;
    }

    private void bufferActivityData(byte[] value) {
        if (!this.activityStruct.hasRoomFor(value)) {
            Context context = getContext();
            C1238GB.toast(context, "error buffering activity data: remaining bytes: " + this.activityStruct.activityDataRemainingBytes + ", received: " + value.length, 1, 3);
            try {
                TransactionBuilder builder = performInitialized("send stop sync data");
                builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), new byte[]{17});
                builder.queue(getQueue());
                C1238GB.updateTransferNotification((String) null, "Data transfer failed", false, 0, getContext());
                handleActivityFetchFinish();
            } catch (IOException e) {
                LOG.error("error stopping activity sync", (Throwable) e);
            }
        } else if (this.activityStruct.isValidData(value)) {
            this.activityStruct.buffer(value);
            if (this.activityStruct.isBufferFull()) {
                flushActivityDataHolder();
            }
        } else {
            Logger logger = LOG;
            logger.warn("GOT UNEXPECTED ACTIVITY DATA WITH LENGTH: " + value.length + ", EXPECTED LENGTH: " + this.activityStruct.activityDataRemainingBytes);
            ((MiBandSupport) getSupport()).logMessageContent(value);
        }
    }

    private void flushActivityDataHolder() {
        byte heartrate;
        Throwable th;
        MiBandActivitySample sample;
        User user;
        Device device;
        byte heartrate2;
        if (this.activityStruct == null) {
            LOG.debug("nothing to flush, struct is already null");
            return;
        }
        int bpm = getBytesPerMinuteOfActivityData();
        Logger logger = LOG;
        logger.debug("flushing activity data samples: " + (this.activityStruct.activityDataHolderProgress / bpm));
        byte heartrate3 = 0;
        try {
            DBHandler dbHandler = GBApplication.acquireDB();
            try {
                MiBandSampleProvider provider = new MiBandSampleProvider(getDevice(), dbHandler.getDaoSession());
                User user2 = DBHelper.getUser(dbHandler.getDaoSession());
                Device device2 = DBHelper.getDevice(getDevice(), dbHandler.getDaoSession());
                int minutes = 0;
                try {
                    int timestampInSeconds = (int) (this.activityStruct.activityDataTimestampProgress.getTimeInMillis() / 1000);
                    if (this.activityStruct.activityDataHolderProgress % bpm == 0) {
                        MiBandActivitySample[] samples = new MiBandActivitySample[(this.activityStruct.activityDataHolderProgress / bpm)];
                        int i = 0;
                        while (i < this.activityStruct.activityDataHolderProgress) {
                            byte category = this.activityStruct.activityDataHolder[i];
                            byte intensity = this.activityStruct.activityDataHolder[i + 1];
                            byte steps = this.activityStruct.activityDataHolder[i + 2];
                            if (this.hasExtendedActivityData) {
                                try {
                                    heartrate3 = this.activityStruct.activityDataHolder[i + 3];
                                } catch (Throwable th2) {
                                    th = th2;
                                    User user3 = user2;
                                    Device device3 = device2;
                                    this.activityStruct.bufferFlushed(minutes);
                                    throw th;
                                }
                            }
                            try {
                                sample = ((MiBandSupport) getSupport()).createActivitySample(device2, user2, timestampInSeconds, provider);
                                user = user2;
                            } catch (Throwable th3) {
                                th = th3;
                                byte b = heartrate3;
                                User user4 = user2;
                                Device device4 = device2;
                                this.activityStruct.bufferFlushed(minutes);
                                throw th;
                            }
                            try {
                                sample.setRawIntensity(intensity & 255);
                                sample.setSteps(steps & 255);
                                sample.setRawKind(category & 255);
                                sample.setHeartRate(heartrate3 & 255);
                                samples[minutes] = sample;
                                if (LOG.isDebugEnabled()) {
                                    Logger logger2 = LOG;
                                    heartrate2 = heartrate3;
                                    try {
                                        StringBuilder sb = new StringBuilder();
                                        device = device2;
                                        try {
                                            sb.append("sample: ");
                                            sb.append(samples[minutes]);
                                            logger2.debug(sb.toString());
                                        } catch (Throwable th4) {
                                            th = th4;
                                            heartrate3 = heartrate2;
                                        }
                                    } catch (Throwable th5) {
                                        th = th5;
                                        Device device5 = device2;
                                        heartrate3 = heartrate2;
                                        this.activityStruct.bufferFlushed(minutes);
                                        throw th;
                                    }
                                } else {
                                    heartrate2 = heartrate3;
                                    device = device2;
                                }
                                minutes++;
                                timestampInSeconds += 60;
                                i += bpm;
                                user2 = user;
                                heartrate3 = heartrate2;
                                device2 = device;
                            } catch (Throwable th6) {
                                th = th6;
                                byte b2 = heartrate3;
                                Device device6 = device2;
                                this.activityStruct.bufferFlushed(minutes);
                                throw th;
                            }
                        }
                        Device device7 = device2;
                        try {
                            provider.addGBActivitySamples(samples);
                            this.activityStruct.bufferFlushed(minutes);
                            if (dbHandler != null) {
                                dbHandler.close();
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            this.activityStruct.bufferFlushed(minutes);
                            throw th;
                        }
                    } else {
                        User user5 = user2;
                        Device device8 = device2;
                        throw new IllegalStateException("Unexpected data, progress should be multiple of " + bpm + ": " + this.activityStruct.activityDataHolderProgress);
                    }
                } catch (Throwable th8) {
                    th = th8;
                    User user6 = user2;
                    Device device9 = device2;
                    this.activityStruct.bufferFlushed(minutes);
                    throw th;
                }
            } catch (Throwable th9) {
                Throwable th10 = th9;
                if (dbHandler != null) {
                    try {
                        dbHandler.close();
                    } catch (Exception e) {
                        ex = e;
                        byte b3 = heartrate;
                        C1238GB.toast(getContext(), ex.getMessage(), 1, 3, ex);
                        return;
                    } catch (Throwable th11) {
                        th.addSuppressed(th11);
                    }
                }
                throw th10;
            }
        } catch (Exception e2) {
            ex = e2;
        }
    }

    private void sendAckDataTransfer(Calendar time, int bytesTransferred) {
        byte[] ackTime = MiBandDateConverter.calendarToRawBytes(time, getDevice().getAddress());
        Prefs prefs = GBApplication.getPrefs();
        byte[] ackChecksum = {(byte) (bytesTransferred & 255), (byte) ((bytesTransferred >> 8) & 255)};
        if (prefs.getBoolean(MiBandConst.PREF_MIBAND_DONT_ACK_TRANSFER, false)) {
            ackChecksum = new byte[]{(byte) ((bytesTransferred ^ -1) & 255), (byte) (((bytesTransferred ^ -1) >> 8) & 255)};
        }
        byte[] ack = {10, ackTime[0], ackTime[1], ackTime[2], ackTime[3], ackTime[4], ackTime[5], ackChecksum[0], ackChecksum[1]};
        try {
            TransactionBuilder builder = performInitialized("send acknowledge");
            builder.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), ack);
            builder.queue(getQueue());
            flushActivityDataHolder();
            if (getDevice().isBusy() && bytesTransferred == 0) {
                if (prefs.getBoolean(MiBandConst.PREF_MIBAND_DONT_ACK_TRANSFER, false)) {
                    TransactionBuilder builder2 = performInitialized("send acknowledge");
                    builder2.write(getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), new byte[]{17});
                    ((MiBandSupport) getSupport()).setHighLatency(builder2);
                    builder2.queue(getQueue());
                }
                handleActivityFetchFinish();
            }
        } catch (IOException ex) {
            LOG.error("Unable to send ack to MI", (Throwable) ex);
        }
    }
}
