package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import java.util.Arrays;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventDisplayMessage;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceBusyAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetProgressAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.AbstractHuamiOperation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateFirmwareOperation extends AbstractHuamiOperation {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) UpdateFirmwareOperation.class);
    protected HuamiFirmwareInfo firmwareInfo;
    final BluetoothGattCharacteristic fwCControlChar;
    private final BluetoothGattCharacteristic fwCDataChar;
    protected final Prefs prefs = GBApplication.getPrefs();
    protected final Uri uri;

    public UpdateFirmwareOperation(Uri uri2, HuamiSupport support) {
        super(support);
        this.uri = uri2;
        this.fwCControlChar = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_FIRMWARE);
        this.fwCDataChar = getCharacteristic(HuamiService.UUID_CHARACTERISTIC_FIRMWARE_DATA);
    }

    /* access modifiers changed from: protected */
    public void enableNeededNotifications(TransactionBuilder builder, boolean enable) {
        builder.notify(this.fwCControlChar, enable);
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        this.firmwareInfo = createFwInfo(this.uri, getContext());
        if (!this.firmwareInfo.isGenerallyCompatibleWith(getDevice())) {
            throw new IOException("Firmware is not compatible with the given device: " + getDevice().getAddress());
        } else if (!sendFwInfo()) {
            displayMessage(getContext(), "Error sending firmware info, aborting.", 1, 3);
            done();
        }
    }

    private HuamiFirmwareInfo createFwInfo(Uri uri2, Context context) throws IOException {
        return ((HuamiSupport) getSupport()).createFWHelper(uri2, context).getFirmwareInfo();
    }

    /* access modifiers changed from: protected */
    public void done() {
        LOG.info("Operation done.");
        operationFinished();
        unsetBusy();
    }

    public boolean onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status != 0) {
            operationFailed();
        }
        return super.onCharacteristicWrite(gatt, characteristic, status);
    }

    private void operationFailed() {
        C1238GB.updateInstallNotification(getContext().getString(C0889R.string.updatefirmwareoperation_write_failed), false, 0, getContext());
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (this.fwCControlChar.getUuid().equals(characteristic.getUuid())) {
            handleNotificationNotif(characteristic.getValue());
            return true;
        }
        super.onCharacteristicChanged(gatt, characteristic);
        return false;
    }

    private void handleNotificationNotif(byte[] value) {
        if (value.length == 3 || value.length == 11) {
            boolean success = value[2] == 1;
            if (value[0] != 16 || !success) {
                LOG.error("Unexpected notification during firmware update: ");
                operationFailed();
                ((HuamiSupport) getSupport()).logMessageContent(value);
                displayMessage(getContext(), getContext().getString(C0889R.string.updatefirmwareoperation_metadata_updateproblem), 1, 3);
                done();
                return;
            }
            try {
                byte b = value[1];
                if (b == 1) {
                    sendFirmwareData(getFirmwareInfo());
                } else if (b == 3) {
                    sendChecksum(getFirmwareInfo());
                } else if (b != 4) {
                    if (b != 5) {
                        LOG.error("Unexpected response during firmware update: ");
                        ((HuamiSupport) getSupport()).logMessageContent(value);
                        operationFailed();
                        displayMessage(getContext(), getContext().getString(C0889R.string.updatefirmwareoperation_updateproblem_do_not_reboot), 1, 3);
                        done();
                        return;
                    }
                    LOG.info("Reboot command successfully sent.");
                    C1238GB.updateInstallNotification(getContext().getString(C0889R.string.updatefirmwareoperation_update_complete), false, 100, getContext());
                    done();
                } else if (getFirmwareInfo().getFirmwareType() == HuamiFirmwareType.FIRMWARE) {
                    TransactionBuilder builder = performInitialized("reboot");
                    ((HuamiSupport) getSupport()).sendReboot(builder);
                    builder.queue(getQueue());
                } else {
                    C1238GB.updateInstallNotification(getContext().getString(C0889R.string.updatefirmwareoperation_update_complete), false, 100, getContext());
                    done();
                }
            } catch (Exception e) {
                displayMessage(getContext(), getContext().getString(C0889R.string.updatefirmwareoperation_updateproblem_do_not_reboot), 1, 3);
                done();
            }
        } else {
            LOG.error("Notifications should be 3 or 11 bytes long.");
            ((HuamiSupport) getSupport()).logMessageContent(value);
        }
    }

    private void displayMessage(Context context, String message, int duration, int severity) {
        ((HuamiSupport) getSupport()).handleGBDeviceEvent(new GBDeviceEventDisplayMessage(message, duration, severity));
    }

    public boolean sendFwInfo() {
        try {
            TransactionBuilder builder = performInitialized("send firmware info");
            builder.add(new SetDeviceBusyAction(getDevice(), getContext().getString(C0889R.string.updating_firmware), getContext()));
            byte[] sizeBytes = BLETypeConversions.fromUint24(getFirmwareInfo().getSize());
            int arraySize = 4;
            boolean isFirmwareCode = getFirmwareInfo().getFirmwareType() == HuamiFirmwareType.FIRMWARE;
            if (!isFirmwareCode) {
                arraySize = 4 + 1;
            }
            byte[] bytes = new byte[arraySize];
            int i = 0 + 1;
            bytes[0] = 1;
            int i2 = i + 1;
            bytes[i] = sizeBytes[0];
            int i3 = i2 + 1;
            bytes[i2] = sizeBytes[1];
            int i4 = i3 + 1;
            bytes[i3] = sizeBytes[2];
            if (!isFirmwareCode) {
                bytes[i4] = getFirmwareInfo().getFirmwareType().getValue();
            }
            builder.write(this.fwCControlChar, bytes);
            builder.queue(getQueue());
            return true;
        } catch (IOException e) {
            Logger logger = LOG;
            logger.error("Error sending firmware info: " + e.getLocalizedMessage(), (Throwable) e);
            return false;
        }
    }

    private boolean sendFirmwareData(HuamiFirmwareInfo info) {
        byte[] fwbytes = info.getBytes();
        int len = fwbytes.length;
        int packetLength = ((HuamiSupport) getSupport()).getMTU() - 3;
        int packets = len / packetLength;
        int firmwareProgress = 0;
        try {
            TransactionBuilder builder = performInitialized("send firmware packet");
            builder.write(this.fwCControlChar, getFirmwareStartCommand());
            for (int i = 0; i < packets; i++) {
                builder.write(this.fwCDataChar, Arrays.copyOfRange(fwbytes, i * packetLength, (i * packetLength) + packetLength));
                firmwareProgress += packetLength;
                int progressPercent = (int) ((((float) firmwareProgress) / ((float) len)) * 100.0f);
                if (i > 0 && i % 100 == 0) {
                    builder.write(this.fwCControlChar, new byte[]{0});
                    builder.add(new SetProgressAction(getContext().getString(C0889R.string.updatefirmwareoperation_update_in_progress), true, progressPercent, getContext()));
                }
            }
            if (firmwareProgress < len) {
                builder.write(this.fwCDataChar, Arrays.copyOfRange(fwbytes, packets * packetLength, len));
            }
            builder.write(this.fwCControlChar, new byte[]{0});
            builder.queue(getQueue());
            return true;
        } catch (IOException ex) {
            LOG.error("Unable to send fw to device", (Throwable) ex);
            C1238GB.updateInstallNotification(getContext().getString(C0889R.string.updatefirmwareoperation_firmware_not_sent), false, 0, getContext());
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void sendChecksum(HuamiFirmwareInfo firmwareInfo2) throws IOException {
        TransactionBuilder builder = performInitialized("send firmware checksum");
        byte[] bytes = BLETypeConversions.fromUint16(firmwareInfo2.getCrc16());
        builder.write(this.fwCControlChar, new byte[]{4, bytes[0], bytes[1]});
        builder.queue(getQueue());
    }

    /* access modifiers changed from: package-private */
    public HuamiFirmwareInfo getFirmwareInfo() {
        return this.firmwareInfo;
    }

    /* access modifiers changed from: protected */
    public byte[] getFirmwareStartCommand() {
        return new byte[]{3};
    }
}
