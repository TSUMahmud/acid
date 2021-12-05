package nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi;

import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFmFrequency;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventLEDColor;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.roidmi.RoidmiConst;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Roidmi1Protocol extends RoidmiProtocol {
    private static final byte COMMAND_GET_COLOR = -127;
    private static final byte COMMAND_GET_FREQUENCY = Byte.MIN_VALUE;
    private static final byte[] COMMAND_PERIODIC = {-86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, 2, 1, MakibesHR3Constants.CMD_85, -120, -61, 60};
    private static final byte COMMAND_SET_COLOR = 17;
    private static final byte COMMAND_SET_FREQUENCY = 16;
    private static final int LED_COLOR_BLUE = 3;
    private static final int LED_COLOR_GREEN = 2;
    private static final int LED_COLOR_OFF = 8;
    private static final int LED_COLOR_PINK = 6;
    private static final int LED_COLOR_RED = 1;
    private static final int LED_COLOR_SKY_BLUE = 5;
    private static final int LED_COLOR_WHITE = 7;
    private static final int LED_COLOR_YELLOW = 4;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Roidmi1Protocol.class);
    private static final byte[] PACKET_HEADER = {-86, ZeTimeConstants.CMD_DELETE_SLEEP_DATA};
    private static final int PACKET_MIN_LENGTH = 6;
    private static final byte[] PACKET_TRAILER = {-61, 60};

    public Roidmi1Protocol(GBDevice device) {
        super(device);
    }

    public GBDeviceEvent[] decodeResponse(byte[] responseData) {
        if (responseData.length <= 6) {
            LOG.info("Response too small");
            return null;
        }
        for (int i = 0; i < packetHeader().length; i++) {
            if (responseData[i] != packetHeader()[i]) {
                LOG.info("Invalid response header");
                return null;
            }
        }
        for (int i2 = 0; i2 < packetTrailer().length; i2++) {
            if (responseData[(responseData.length - packetTrailer().length) + i2] != packetTrailer()[i2]) {
                LOG.info("Invalid response trailer");
                return null;
            }
        }
        if (calcChecksum(responseData) != responseData[(responseData.length - packetTrailer().length) - 1]) {
            LOG.info("Invalid response checksum");
            return null;
        }
        byte b = responseData[3];
        if (b == Byte.MIN_VALUE) {
            float frequency = Float.valueOf(C1238GB.hexdump(responseData, 4, 2)).floatValue() / 10.0f;
            Logger logger = LOG;
            logger.debug("Got frequency: " + frequency);
            GBDeviceEventFmFrequency evFrequency = new GBDeviceEventFmFrequency();
            evFrequency.frequency = frequency;
            return new GBDeviceEvent[]{evFrequency};
        } else if (b != -127) {
            Logger logger2 = LOG;
            logger2.error("Unrecognized response type 0x" + C1238GB.hexdump(responseData, packetHeader().length, 1));
            return null;
        } else {
            byte color = responseData[5];
            Logger logger3 = LOG;
            logger3.debug("Got color: " + color);
            GBDeviceEventLEDColor evColor = new GBDeviceEventLEDColor();
            evColor.color = RoidmiConst.COLOR_PRESETS[color + -1];
            return new GBDeviceEvent[]{evColor};
        }
    }

    public byte[] encodeLedColor(int color) {
        int[] presets = RoidmiConst.COLOR_PRESETS;
        int color_id = -1;
        int i = 0;
        while (true) {
            if (i >= presets.length) {
                break;
            } else if (presets[i] == color) {
                color_id = (i + 1) & 255;
                break;
            } else {
                i++;
            }
        }
        if (color_id < 0 || color_id > 8) {
            throw new IllegalArgumentException("color must belong to RoidmiConst.COLOR_PRESETS");
        }
        return encodeCommand(17, 0, (byte) color_id);
    }

    public byte[] encodeFmFrequency(float frequency) {
        if (((double) frequency) < 87.5d || ((double) frequency) > 108.0d) {
            throw new IllegalArgumentException("Frequency must be >= 87.5 and <= 180.0");
        }
        byte[] freq = frequencyToBytes(frequency);
        return encodeCommand(16, freq[0], freq[1]);
    }

    public byte[] encodeGetLedColor() {
        return encodeCommand(COMMAND_GET_COLOR, 0, 0);
    }

    public byte[] encodeGetFmFrequency() {
        return encodeCommand(Byte.MIN_VALUE, 0, 0);
    }

    public byte[] encodeGetVoltage() {
        return null;
    }

    public byte[] packetHeader() {
        return PACKET_HEADER;
    }

    public byte[] packetTrailer() {
        return PACKET_TRAILER;
    }
}
