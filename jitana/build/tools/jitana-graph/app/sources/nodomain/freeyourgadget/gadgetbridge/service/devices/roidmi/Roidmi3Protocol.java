package nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi;

import androidx.core.view.MotionEventCompat;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFmFrequency;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventLEDColor;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Roidmi3Protocol extends RoidmiProtocol {
    private static final byte[] COMMAND_DENOISE_OFF = {5, 6, 0};
    private static final byte[] COMMAND_DENOISE_ON = {5, 6, 18};
    private static final byte[] COMMAND_GET_COLOR = {2, -127};
    private static final byte[] COMMAND_GET_FREQUENCY = {5, -127};
    private static final byte[] COMMAND_GET_VOLTAGE = {6, -127};
    private static final byte[] COMMAND_SET_COLOR = {2, 1, 0, 0, 0};
    private static final byte[] COMMAND_SET_FREQUENCY = {5, 1, 9, 100};
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Roidmi3Protocol.class);
    private static final int PACKET_MIN_LENGTH = 4;
    private static final byte RESPONSE_COLOR = 2;
    private static final byte RESPONSE_FREQUENCY = 5;
    private static final byte RESPONSE_VOLTAGE = 6;

    public Roidmi3Protocol(GBDevice device) {
        super(device);
    }

    public GBDeviceEvent[] decodeResponse(byte[] res) {
        if (res.length <= 4) {
            LOG.info("Response too small");
            return null;
        } else if (calcChecksum(res) != res[res.length - 1]) {
            LOG.info("Invalid response checksum");
            return null;
        } else if (res[0] + 2 != res.length) {
            LOG.info("Packet length doesn't match");
            return null;
        } else {
            if (res[2] != -127) {
                Logger logger = LOG;
                logger.warn("Potentially unsupported response: " + C1238GB.hexdump(res, 0, res.length));
            }
            if (res[1] == 6) {
                float voltage = Float.valueOf(C1238GB.hexdump(res, 3, 2)).floatValue() / 100.0f;
                Logger logger2 = LOG;
                logger2.debug("Got voltage: " + voltage);
                GBDeviceEventBatteryInfo evBattery = new GBDeviceEventBatteryInfo();
                evBattery.state = BatteryState.NO_BATTERY;
                evBattery.level = -1;
                evBattery.voltage = voltage;
                return new GBDeviceEvent[]{evBattery};
            } else if (res[1] == 2) {
                Logger logger3 = LOG;
                logger3.debug("Got color: #" + C1238GB.hexdump(res, 3, 3));
                int color = ((res[4] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | -16777216 | ((res[3] << 16) & 16711680) | (res[5] & 255);
                GBDeviceEventLEDColor evColor = new GBDeviceEventLEDColor();
                evColor.color = color;
                return new GBDeviceEvent[]{evColor};
            } else if (res[1] == 5) {
                float frequency = Float.valueOf(C1238GB.hexdump(res, 3, 2)).floatValue() / 10.0f;
                Logger logger4 = LOG;
                logger4.debug("Got frequency: " + frequency);
                GBDeviceEventFmFrequency evFrequency = new GBDeviceEventFmFrequency();
                evFrequency.frequency = frequency;
                return new GBDeviceEvent[]{evFrequency};
            } else {
                Logger logger5 = LOG;
                logger5.error("Unrecognized response: " + C1238GB.hexdump(res, 0, res.length));
                return null;
            }
        }
    }

    public byte[] encodeLedColor(int color) {
        byte[] cmd = (byte[]) COMMAND_SET_COLOR.clone();
        cmd[2] = (byte) (color >> 16);
        cmd[3] = (byte) (color >> 8);
        cmd[4] = (byte) color;
        return encodeCommand(cmd);
    }

    public byte[] encodeFmFrequency(float frequency) {
        if (((double) frequency) < 87.5d || ((double) frequency) > 108.0d) {
            throw new IllegalArgumentException("Frequency must be >= 87.5 and <= 180.0");
        }
        byte[] cmd = (byte[]) COMMAND_SET_FREQUENCY.clone();
        byte[] freq = frequencyToBytes(frequency);
        cmd[2] = freq[0];
        cmd[3] = freq[1];
        return encodeCommand(cmd);
    }

    public byte[] encodeGetLedColor() {
        return encodeCommand(COMMAND_GET_COLOR);
    }

    public byte[] encodeGetFmFrequency() {
        return encodeCommand(COMMAND_GET_FREQUENCY);
    }

    public byte[] packetHeader() {
        return new byte[0];
    }

    public byte[] packetTrailer() {
        return new byte[0];
    }

    public byte[] encodeGetVoltage() {
        return encodeCommand(COMMAND_GET_VOLTAGE);
    }

    public byte[] encodeDenoise(boolean enabled) {
        return encodeCommand(enabled ? COMMAND_DENOISE_ON : COMMAND_DENOISE_OFF);
    }
}
