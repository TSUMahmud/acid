package nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import java.util.Arrays;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventDisplayMessage;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.PlainAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceBusyAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetProgressAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.AbstractMiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.CheckSums;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateFirmwareOperation extends AbstractMiBand1Operation {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) UpdateFirmwareOperation.class);
    /* access modifiers changed from: private */
    public boolean firmwareInfoSent = false;
    final Prefs prefs = GBApplication.getPrefs();
    private UpdateCoordinator updateCoordinator;
    private final Uri uri;

    enum State {
        INITIAL,
        SEND_FW2,
        SEND_FW1,
        FINISHED,
        UNKNOWN
    }

    public UpdateFirmwareOperation(Uri uri2, MiBandSupport support) {
        super(support);
        this.uri = uri2;
    }

    /* access modifiers changed from: protected */
    public void enableNeededNotifications(TransactionBuilder builder, boolean enable) {
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        MiBandFWHelper mFwHelper = new MiBandFWHelper(this.uri, getContext());
        AbstractMiFirmwareInfo firmwareInfo = mFwHelper.getFirmwareInfo();
        if (firmwareInfo.isGenerallyCompatibleWith(getDevice())) {
            if (((MiBandSupport) getSupport()).supportsHeartRate()) {
                this.updateCoordinator = prepareFirmwareInfo1S(firmwareInfo);
            } else {
                this.updateCoordinator = prepareFirmwareInfo(mFwHelper.getFw(), mFwHelper.getFirmwareVersion());
            }
            this.updateCoordinator.initNextOperation();
            if (!this.updateCoordinator.sendFwInfo()) {
                displayMessage(getContext(), "Error sending firmware info, aborting.", 1, 3);
                done();
                return;
            }
            return;
        }
        throw new IOException("Firmware is not compatible with the given device: " + getDevice().getAddress());
    }

    private void done() {
        LOG.info("Operation done.");
        this.updateCoordinator = null;
        operationFinished();
        unsetBusy();
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (MiBandService.UUID_CHARACTERISTIC_NOTIFICATION.equals(characteristic.getUuid())) {
            handleNotificationNotif(characteristic.getValue());
            return false;
        }
        super.onCharacteristicChanged(gatt, characteristic);
        return false;
    }

    private void handleNotificationNotif(byte[] value) {
        if (value.length != 1) {
            LOG.error("Notifications should be 1 byte long.");
            ((MiBandSupport) getSupport()).logMessageContent(value);
            return;
        }
        UpdateCoordinator updateCoordinator2 = this.updateCoordinator;
        if (updateCoordinator2 == null) {
            LOG.error("received notification when updateCoordinator is null, ignoring (notification content follows):");
            ((MiBandSupport) getSupport()).logMessageContent(value);
            return;
        }
        byte b = value[0];
        if (b == 1) {
            displayMessage(getContext(), getContext().getString(C0889R.string.updatefirmwareoperation_updateproblem_do_not_reboot), 1, 3);
            C1238GB.updateInstallNotification(getContext().getString(C0889R.string.updatefirmwareoperation_write_failed), false, 0, getContext());
            done();
        } else if (b != 2) {
            if (b == 11) {
                displayMessage(getContext(), getContext().getString(C0889R.string.updatefirmwareoperation_metadata_updateproblem), 1, 3);
                this.firmwareInfoSent = false;
                done();
            } else if (b != 12) {
                ((MiBandSupport) getSupport()).logMessageContent(value);
            } else if (this.firmwareInfoSent) {
                displayMessage(getContext(), "Firmware metadata successfully sent.", 1, 1);
                if (!this.updateCoordinator.sendFwData()) {
                    displayMessage(getContext(), getContext().getString(C0889R.string.updatefirmwareoperation_updateproblem_do_not_reboot), 1, 3);
                    done();
                }
                this.firmwareInfoSent = false;
            } else {
                LOG.warn("firmwareInfoSent is false -- not sending firmware data even though we got meta data success notification");
            }
        } else if (updateCoordinator2.initNextOperation()) {
            displayMessage(getContext(), "Heart Rate Firmware successfully updated, now updating Mi Band Firmware", 1, 1);
            if (!this.updateCoordinator.sendFwInfo()) {
                displayMessage(getContext(), "Error sending firmware info, aborting.", 1, 3);
                done();
            }
        } else {
            if (this.updateCoordinator.needsReboot()) {
                displayMessage(getContext(), getContext().getString(C0889R.string.updatefirmwareoperation_update_complete_rebooting), 1, 1);
                C1238GB.updateInstallNotification(getContext().getString(C0889R.string.updatefirmwareoperation_update_complete), false, 100, getContext());
                ((MiBandSupport) getSupport()).onReset(1);
            } else {
                LOG.error("BUG: Successful firmware update without reboot???");
            }
            done();
        }
    }

    private void displayMessage(Context context, String message, int duration, int severity) {
        ((MiBandSupport) getSupport()).handleGBDeviceEvent(new GBDeviceEventDisplayMessage(message, duration, severity));
    }

    private UpdateCoordinator prepareFirmwareInfo(byte[] fwBytes, int newFwVersion) throws IOException {
        int newFwSize = fwBytes.length;
        String[] mMacOctets = getDevice().getAddress().split(":");
        int currentFwVersion = ((MiBandSupport) getSupport()).getDeviceInfo().getFirmwareVersion();
        return new SingleUpdateCoordinator(prepareFirmwareUpdateA(currentFwVersion, newFwVersion, newFwSize, ((Integer.decode("0x" + mMacOctets[4]).intValue() << 8) | Integer.decode("0x" + mMacOctets[5]).intValue()) ^ CheckSums.getCRC16(fwBytes)), fwBytes, true);
    }

    private UpdateCoordinator prepareFirmwareInfo1S(AbstractMiFirmwareInfo info) {
        if (!info.isSingleMiBandFirmware()) {
            int fw2Version = info.getSecond().getFirmwareVersion();
            int fw1Version = info.getFirst().getFirmwareVersion();
            String[] mMacOctets = getDevice().getAddress().split(":");
            int encodedMac = (Integer.decode("0x" + mMacOctets[4]).intValue() << 8) | Integer.decode("0x" + mMacOctets[5]).intValue();
            byte[] fw2Bytes = info.getSecond().getFirmwareBytes();
            int fw2Checksum = CheckSums.getCRC16(fw2Bytes) ^ encodedMac;
            byte[] fw1Bytes = info.getFirst().getFirmwareBytes();
            int fw1Checksum = encodedMac ^ CheckSums.getCRC16(fw1Bytes);
            int fw1OldVersion = ((MiBandSupport) getSupport()).getDeviceInfo().getFirmwareVersion();
            int fw2OldVersion = ((MiBandSupport) getSupport()).getDeviceInfo().getHeartrateFirmwareVersion();
            if (info.isSingleMiBandFirmware()) {
                LOG.info("is single Mi Band firmware");
                return new SingleUpdateCoordinator(prepareFirmwareInfo(fw1Bytes, fw1OldVersion, fw1Version, fw1Checksum, 0, true), fw1Bytes, true);
            }
            LOG.info("is multi Mi Band firmware, sending fw2 (hr) first");
            byte[] fw2Info = prepareFirmwareInfo(fw2Bytes, fw2OldVersion, fw2Version, fw2Checksum, 1, true);
            return new DoubleUpdateCoordinator(prepareFirmwareInfo(fw1Bytes, fw1OldVersion, fw1Version, fw1Checksum, 0, true), fw1Bytes, fw2Info, fw2Bytes, true);
        }
        throw new IllegalArgumentException("preparing single fw not allowed for 1S");
    }

    private byte[] prepareFirmwareInfo(byte[] fwBytes, int currentFwVersion, int newFwVersion, int checksum, int something, boolean reboot) {
        if (something == -2) {
            return prepareFirmwareUpdateB(currentFwVersion, newFwVersion, fwBytes.length, checksum, 0);
        } else if (something == -1) {
            return prepareFirmwareUpdateA(currentFwVersion, newFwVersion, fwBytes.length, checksum);
        } else {
            return prepareFirmwareUpdateB(currentFwVersion, newFwVersion, fwBytes.length, checksum, something);
        }
    }

    private byte[] prepareFirmwareUpdateA(int currentFwVersion, int newFwVersion, int newFwSize, int checksum) {
        return new byte[]{7, (byte) currentFwVersion, (byte) (currentFwVersion >> 8), (byte) (currentFwVersion >> 16), (byte) (currentFwVersion >> 24), (byte) newFwVersion, (byte) (newFwVersion >> 8), (byte) (newFwVersion >> 16), (byte) (newFwVersion >> 24), (byte) newFwSize, (byte) (newFwSize >> 8), (byte) checksum, (byte) (checksum >> 8)};
    }

    private byte[] prepareFirmwareUpdateB(int currentFwVersion, int newFwVersion, int newFwSize, int checksum, int something) {
        return new byte[]{7, (byte) currentFwVersion, (byte) (currentFwVersion >> 8), (byte) (currentFwVersion >> 16), (byte) (currentFwVersion >> 24), (byte) newFwVersion, (byte) (newFwVersion >> 8), (byte) (newFwVersion >> 16), (byte) (newFwVersion >> 24), (byte) newFwSize, (byte) (newFwSize >> 8), (byte) checksum, (byte) (checksum >> 8), (byte) something};
    }

    /* access modifiers changed from: private */
    public boolean sendFirmwareData(byte[] fwbytes) {
        byte[] bArr = fwbytes;
        int len = bArr.length;
        int packets = len / 20;
        BluetoothGattCharacteristic characteristicControlPoint = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT);
        BluetoothGattCharacteristic characteristicFWData = getCharacteristic(MiBandService.UUID_CHARACTERISTIC_FIRMWARE_DATA);
        int firmwareProgress = 0;
        char c = 0;
        try {
            TransactionBuilder builder = performInitialized("send firmware packet");
            if (GBApplication.getDeviceSpecificSharedPrefs(getDevice().getAddress()).getBoolean("low_latency_fw_update", true)) {
                ((MiBandSupport) getSupport()).setLowLatency(builder);
            }
            int i = 0;
            while (i < packets) {
                builder.write(characteristicFWData, Arrays.copyOfRange(bArr, i * 20, (i * 20) + 20));
                firmwareProgress += 20;
                int progressPercent = (int) ((((float) firmwareProgress) / ((float) len)) * 100.0f);
                if (i > 0 && i % 50 == 0) {
                    byte[] bArr2 = new byte[1];
                    bArr2[c] = 11;
                    builder.write(characteristicControlPoint, bArr2);
                    builder.add(new SetProgressAction(getContext().getString(C0889R.string.updatefirmwareoperation_update_in_progress), true, progressPercent, getContext()));
                }
                i++;
                c = 0;
            }
            if (firmwareProgress < len) {
                builder.write(characteristicFWData, Arrays.copyOfRange(bArr, packets * 20, len));
                int firmwareProgress2 = len;
            }
            builder.write(characteristicControlPoint, new byte[]{11});
            builder.queue(getQueue());
            return true;
        } catch (IOException ex) {
            LOG.error("Unable to send fw to MI", (Throwable) ex);
            C1238GB.updateInstallNotification(getContext().getString(C0889R.string.updatefirmwareoperation_firmware_not_sent), false, 0, getContext());
            return false;
        }
    }

    private abstract class UpdateCoordinator {
        private final boolean reboot;

        public abstract byte[] getFirmwareBytes();

        /* access modifiers changed from: package-private */
        public abstract byte[] getFirmwareInfo();

        public abstract boolean initNextOperation();

        public UpdateCoordinator(boolean needsReboot) {
            this.reboot = needsReboot;
        }

        public boolean sendFwInfo() {
            try {
                TransactionBuilder builder = UpdateFirmwareOperation.this.performInitialized("send firmware info");
                builder.add(new SetDeviceBusyAction(UpdateFirmwareOperation.this.getDevice(), UpdateFirmwareOperation.this.getContext().getString(C0889R.string.updating_firmware), UpdateFirmwareOperation.this.getContext()));
                builder.add(new FirmwareInfoSentAction(UpdateFirmwareOperation.this, (C12041) null));
                builder.write(UpdateFirmwareOperation.this.getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), getFirmwareInfo());
                builder.queue(UpdateFirmwareOperation.this.getQueue());
                return true;
            } catch (IOException e) {
                Logger access$600 = UpdateFirmwareOperation.LOG;
                access$600.error("Error sending firmware info: " + e.getLocalizedMessage(), (Throwable) e);
                return false;
            }
        }

        public boolean sendFwData() {
            return UpdateFirmwareOperation.this.sendFirmwareData(getFirmwareBytes());
        }

        public boolean needsReboot() {
            return this.reboot;
        }
    }

    private class SingleUpdateCoordinator extends UpdateCoordinator {
        private final byte[] fwData;
        private final byte[] fwInfo;

        public SingleUpdateCoordinator(byte[] fwInfo2, byte[] fwData2, boolean reboot) {
            super(reboot);
            this.fwInfo = fwInfo2;
            this.fwData = fwData2;
        }

        public byte[] getFirmwareInfo() {
            return this.fwInfo;
        }

        public byte[] getFirmwareBytes() {
            return this.fwData;
        }

        public boolean initNextOperation() {
            return false;
        }
    }

    private class DoubleUpdateCoordinator extends UpdateCoordinator {
        private byte[] currentFwData;
        private byte[] currentFwInfo;
        private final byte[] fw1Data;
        private final byte[] fw1Info;
        private final byte[] fw2Data;
        private final byte[] fw2Info;
        private State state = State.INITIAL;

        public DoubleUpdateCoordinator(byte[] fw1Info2, byte[] fw1Data2, byte[] fw2Info2, byte[] fw2Data2, boolean reboot) {
            super(reboot);
            this.fw1Info = fw1Info2;
            this.fw1Data = fw1Data2;
            this.fw2Info = fw2Info2;
            this.fw2Data = fw2Data2;
            this.currentFwInfo = fw2Info2;
            this.currentFwData = fw2Data2;
        }

        public byte[] getFirmwareInfo() {
            return this.currentFwInfo;
        }

        public byte[] getFirmwareBytes() {
            return this.currentFwData;
        }

        public boolean initNextOperation() {
            int i = C12041.f190x8705c9a0[this.state.ordinal()];
            if (i == 1) {
                this.currentFwInfo = this.fw2Info;
                this.currentFwData = this.fw2Data;
                this.state = State.SEND_FW2;
                return true;
            } else if (i == 2) {
                this.currentFwInfo = this.fw1Info;
                this.currentFwData = this.fw1Data;
                this.state = State.SEND_FW1;
                if (this.fw1Info == null || this.fw1Data == null) {
                    return false;
                }
                return true;
            } else if (i != 3) {
                this.state = State.UNKNOWN;
                return false;
            } else {
                this.currentFwInfo = null;
                this.currentFwData = null;
                this.state = State.FINISHED;
                return false;
            }
        }
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations.UpdateFirmwareOperation$1 */
    static /* synthetic */ class C12041 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$service$devices$miband$operations$UpdateFirmwareOperation$State */
        static final /* synthetic */ int[] f190x8705c9a0 = new int[State.values().length];

        static {
            try {
                f190x8705c9a0[State.INITIAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f190x8705c9a0[State.SEND_FW2.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f190x8705c9a0[State.SEND_FW1.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private class FirmwareInfoSentAction extends PlainAction {
        private FirmwareInfoSentAction() {
        }

        /* synthetic */ FirmwareInfoSentAction(UpdateFirmwareOperation x0, C12041 x1) {
            this();
        }

        public boolean run(BluetoothGatt gatt) {
            if (UpdateFirmwareOperation.this.isOperationRunning()) {
                boolean unused = UpdateFirmwareOperation.this.firmwareInfoSent = true;
            }
            return true;
        }
    }
}
