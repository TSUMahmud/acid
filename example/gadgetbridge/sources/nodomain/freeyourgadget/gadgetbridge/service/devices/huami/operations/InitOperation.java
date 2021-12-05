package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.BFH16Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.liveview.LiveviewConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEOperation;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitOperation extends AbstractBTLEOperation<HuamiSupport> {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) InitOperation.class);
    private final byte authFlags;
    private final TransactionBuilder builder;
    private final byte cryptFlags;
    private final HuamiSupport huamiSupport;
    private final boolean needsAuth;

    public InitOperation(boolean needsAuth2, byte authFlags2, byte cryptFlags2, HuamiSupport support, TransactionBuilder builder2) {
        super(support);
        this.huamiSupport = support;
        this.needsAuth = needsAuth2;
        this.authFlags = authFlags2;
        this.cryptFlags = cryptFlags2;
        this.builder = builder2;
        builder2.setGattCallback(this);
    }

    /* access modifiers changed from: protected */
    public void doPerform() {
        this.huamiSupport.enableNotifications(this.builder, true);
        if (this.needsAuth) {
            this.builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.AUTHENTICATING, getContext()));
            this.builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_AUTH), ArrayUtils.addAll(new byte[]{1, this.authFlags}, getSecretKey()));
            return;
        }
        this.builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        this.builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_AUTH), requestAuthNumber());
    }

    private byte[] requestAuthNumber() {
        byte b = this.cryptFlags;
        if (b == 0) {
            return new byte[]{2, this.authFlags};
        }
        return new byte[]{(byte) (b | 2), this.authFlags, 2};
    }

    private byte[] getSecretKey() {
        byte[] authKeyBytes = {ZeTimeConstants.CMD_USER_INFO, 49, 50, HPlusConstants.DATA_STATS, 52, HPlusConstants.CMD_SET_ALLDAY_HRM, 54, BFH16Constants.CMD_SET_DISCONNECT_REMIND, 56, 57, 64, 65, LiveviewConstants.MSG_GETSCREENMODE, 67, 68, 69};
        String authKey = GBApplication.getDeviceSpecificSharedPrefs(getDevice().getAddress()).getString("authkey", (String) null);
        if (authKey != null && !authKey.isEmpty()) {
            byte[] srcBytes = authKey.trim().getBytes();
            if (authKey.length() == 34 && authKey.substring(0, 2).equals("0x")) {
                srcBytes = C1238GB.hexStringToByteArray(authKey.substring(2));
            }
            System.arraycopy(srcBytes, 0, authKeyBytes, 0, Math.min(srcBytes.length, 16));
        }
        return authKeyBytes;
    }

    public TransactionBuilder performInitialized(String taskName) {
        throw new UnsupportedOperationException("This IS the initialization class, you cannot call this method");
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID characteristicUUID = characteristic.getUuid();
        if (HuamiService.UUID_CHARACTERISTIC_AUTH.equals(characteristicUUID)) {
            try {
                byte[] value = characteristic.getValue();
                this.huamiSupport.logMessageContent(value);
                if (value[0] == 16 && value[1] == 1 && value[2] == 1) {
                    TransactionBuilder builder2 = createTransactionBuilder("Sending the secret key to the device");
                    builder2.write(characteristic, requestAuthNumber());
                    this.huamiSupport.performImmediately(builder2);
                } else if (value[0] == 16 && (value[1] & 15) == 2 && value[2] == 1) {
                    byte[] eValue = handleAESAuth(value, getSecretKey());
                    byte[] responseValue = ArrayUtils.addAll(new byte[]{(byte) (this.cryptFlags | 3), this.authFlags}, eValue);
                    TransactionBuilder builder3 = createTransactionBuilder("Sending the encrypted random key to the device");
                    builder3.write(characteristic, responseValue);
                    this.huamiSupport.setCurrentTimeWithService(builder3);
                    this.huamiSupport.performImmediately(builder3);
                } else if (value[0] != 16 || (value[1] & 15) != 3 || value[2] != 1) {
                    return super.onCharacteristicChanged(gatt, characteristic);
                } else {
                    TransactionBuilder builder4 = createTransactionBuilder("Authenticated, now initialize phase 2");
                    builder4.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
                    this.huamiSupport.enableFurtherNotifications(builder4, true);
                    this.huamiSupport.requestDeviceInfo(builder4);
                    this.huamiSupport.phase2Initialize(builder4);
                    this.huamiSupport.phase3Initialize(builder4);
                    this.huamiSupport.setInitialized(builder4);
                    this.huamiSupport.performImmediately(builder4);
                }
            } catch (Exception e) {
                C1238GB.toast(getContext(), "Error authenticating Huami device", 1, 3, e);
            }
            return true;
        }
        Logger logger = LOG;
        logger.info("Unhandled characteristic changed: " + characteristicUUID);
        return super.onCharacteristicChanged(gatt, characteristic);
    }

    private byte[] handleAESAuth(byte[] value, byte[] secretKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        byte[] mValue = Arrays.copyOfRange(value, 3, 19);
        Cipher ecipher = Cipher.getInstance("AES/ECB/NoPadding");
        ecipher.init(1, new SecretKeySpec(secretKey, "AES"));
        return ecipher.doFinal(mValue);
    }
}
