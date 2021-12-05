package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.AbstractGattListenerWriteAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceBusyAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.AbstractHuamiOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFetchOperation extends AbstractHuamiOperation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractFetchOperation.class);
    protected BluetoothGattCharacteristic characteristicActivityData;
    protected BluetoothGattCharacteristic characteristicFetch;
    int fetchCount;
    protected byte lastPacketCounter;
    Calendar startTimestamp;

    /* access modifiers changed from: protected */
    public abstract void bufferActivityData(byte[] bArr);

    /* access modifiers changed from: protected */
    public abstract String getLastSyncTimeKey();

    /* access modifiers changed from: protected */
    public abstract void handleActivityNotif(byte[] bArr);

    /* access modifiers changed from: protected */
    public abstract void startFetching(TransactionBuilder transactionBuilder);

    public AbstractFetchOperation(HuamiSupport support) {
        super(support);
    }

    /* access modifiers changed from: protected */
    public void enableNeededNotifications(TransactionBuilder builder, boolean enable) {
        if (!enable) {
            builder.notify(this.characteristicFetch, enable);
            builder.notify(this.characteristicActivityData, enable);
        }
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        startFetching();
    }

    /* access modifiers changed from: protected */
    public void startFetching() throws IOException {
        this.lastPacketCounter = -1;
        TransactionBuilder builder = performInitialized(getName());
        ((HuamiSupport) getSupport()).setLowLatency(builder);
        if (this.fetchCount == 0) {
            builder.add(new SetDeviceBusyAction(getDevice(), getContext().getString(C0889R.string.busy_task_fetch_activity_data), getContext()));
        }
        this.fetchCount++;
        this.characteristicActivityData = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_5_ACTIVITY_DATA);
        builder.notify(this.characteristicActivityData, false);
        this.characteristicFetch = getCharacteristic(HuamiService.UUID_UNKNOWN_CHARACTERISTIC4);
        builder.notify(this.characteristicFetch, true);
        startFetching(builder);
        builder.queue(getQueue());
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID characteristicUUID = characteristic.getUuid();
        if (HuamiService.UUID_CHARACTERISTIC_5_ACTIVITY_DATA.equals(characteristicUUID)) {
            handleActivityNotif(characteristic.getValue());
            return true;
        } else if (!HuamiService.UUID_UNKNOWN_CHARACTERISTIC4.equals(characteristicUUID)) {
            return super.onCharacteristicChanged(gatt, characteristic);
        } else {
            handleActivityMetadata(characteristic.getValue());
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void handleActivityFetchFinish(boolean success) {
        C1238GB.updateTransferNotification((String) null, "", false, 100, getContext());
        operationFinished();
        unsetBusy();
    }

    /* access modifiers changed from: protected */
    public void startFetching(TransactionBuilder builder, byte fetchType, GregorianCalendar sinceWhen) {
        String taskName = StringUtils.ensureNotNull(builder.getTaskName());
        final String str = taskName;
        builder.add(new AbstractGattListenerWriteAction(getQueue(), this.characteristicFetch, BLETypeConversions.join(new byte[]{1, fetchType}, ((HuamiSupport) getSupport()).getTimeBytes(sinceWhen, TimeUnit.MINUTES))) {
            /* access modifiers changed from: protected */
            public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                if (HuamiService.UUID_UNKNOWN_CHARACTERISTIC4.equals(characteristic.getUuid())) {
                    byte[] value = characteristic.getValue();
                    if (ArrayUtils.equals(value, HuamiService.RESPONSE_ACTIVITY_DATA_START_DATE_SUCCESS, 0)) {
                        AbstractFetchOperation.this.handleActivityMetadata(value);
                        AbstractFetchOperation abstractFetchOperation = AbstractFetchOperation.this;
                        TransactionBuilder newBuilder = abstractFetchOperation.createTransactionBuilder(str + " Step 2");
                        newBuilder.notify(AbstractFetchOperation.this.characteristicActivityData, true);
                        newBuilder.write(AbstractFetchOperation.this.characteristicFetch, new byte[]{2});
                        try {
                            AbstractFetchOperation.this.performImmediately(newBuilder);
                        } catch (IOException ex) {
                            Context access$100 = AbstractFetchOperation.this.getContext();
                            C1238GB.toast(access$100, "Error fetching debug logs: " + ex.getMessage(), 1, 3, ex);
                        }
                        return true;
                    }
                    AbstractFetchOperation.this.handleActivityMetadata(value);
                }
                return false;
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleActivityMetadata(byte[] value) {
        if (value.length == 15) {
            if (ArrayUtils.equals(value, HuamiService.RESPONSE_ACTIVITY_DATA_START_DATE_SUCCESS, 0)) {
                Calendar startTimestamp2 = ((HuamiSupport) getSupport()).fromTimeBytes(Arrays.copyOfRange(value, 7, value.length));
                setStartTimestamp(startTimestamp2);
                C1238GB.updateTransferNotification(getContext().getString(C0889R.string.busy_task_fetch_activity_data), getContext().getString(C0889R.string.FetchActivityOperation_about_to_transfer_since, new Object[]{DateFormat.getDateTimeInstance().format(startTimestamp2.getTime())}), true, 0, getContext());
                return;
            }
            Logger logger = LOG;
            logger.warn("Unexpected activity metadata: " + Logging.formatBytes(value));
            handleActivityFetchFinish(false);
        } else if (value.length != 3) {
            Logger logger2 = LOG;
            logger2.warn("Unexpected activity metadata: " + Logging.formatBytes(value));
            handleActivityFetchFinish(false);
        } else if (Arrays.equals(HuamiService.RESPONSE_FINISH_SUCCESS, value)) {
            handleActivityFetchFinish(true);
        } else {
            Logger logger3 = LOG;
            logger3.warn("Unexpected activity metadata: " + Logging.formatBytes(value));
            handleActivityFetchFinish(false);
        }
    }

    private void setStartTimestamp(Calendar startTimestamp2) {
        this.startTimestamp = startTimestamp2;
    }

    /* access modifiers changed from: package-private */
    public Calendar getLastStartTimestamp() {
        return this.startTimestamp;
    }

    /* access modifiers changed from: package-private */
    public void saveLastSyncTimestamp(GregorianCalendar timestamp) {
        SharedPreferences.Editor editor = GBApplication.getDeviceSpecificSharedPrefs(getDevice().getAddress()).edit();
        editor.putLong(getLastSyncTimeKey(), timestamp.getTimeInMillis());
        editor.apply();
    }

    /* access modifiers changed from: protected */
    public GregorianCalendar getLastSuccessfulSyncTime() {
        long timeStampMillis = GBApplication.getDeviceSpecificSharedPrefs(getDevice().getAddress()).getLong(getLastSyncTimeKey(), 0);
        if (timeStampMillis != 0) {
            GregorianCalendar calendar = BLETypeConversions.createCalendar();
            calendar.setTimeInMillis(timeStampMillis);
            return calendar;
        }
        GregorianCalendar calendar2 = BLETypeConversions.createCalendar();
        calendar2.add(5, -100);
        return calendar2;
    }
}
