package nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi;

import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RoidmiProtocol extends GBDeviceProtocol {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) RoidmiProtocol.class);

    public abstract GBDeviceEvent[] decodeResponse(byte[] bArr);

    public abstract byte[] encodeFmFrequency(float f);

    public abstract byte[] encodeGetFmFrequency();

    public abstract byte[] encodeGetLedColor();

    public abstract byte[] encodeGetVoltage();

    public abstract byte[] encodeLedColor(int i);

    public abstract byte[] packetHeader();

    public abstract byte[] packetTrailer();

    public RoidmiProtocol(GBDevice device) {
        super(device);
    }

    public byte[] encodeCommand(byte... params) {
        byte[] cmd = new byte[(packetHeader().length + packetTrailer().length + params.length + 2)];
        for (int i = 0; i < packetHeader().length; i++) {
            cmd[i] = packetHeader()[i];
        }
        for (int i2 = 0; i2 < packetTrailer().length; i2++) {
            cmd[(cmd.length - packetTrailer().length) + i2] = packetTrailer()[i2];
        }
        cmd[packetHeader().length] = (byte) params.length;
        for (int i3 = 0; i3 < params.length; i3++) {
            cmd[packetHeader().length + 1 + i3] = params[i3];
        }
        cmd[(cmd.length - packetTrailer().length) - 1] = calcChecksum(cmd);
        return cmd;
    }

    public byte calcChecksum(byte[] packet) {
        int chk = 0;
        for (int i = packetHeader().length; i < (packet.length - packetTrailer().length) - 1; i++) {
            chk += packet[i] & 255;
        }
        return (byte) chk;
    }

    public byte[] frequencyToBytes(float frequency) {
        byte[] res = new byte[2];
        String format = String.format(Locale.getDefault(), "%04d", new Object[]{Integer.valueOf((int) (10.0f * frequency))});
        try {
            res[0] = (byte) (Integer.parseInt(format.substring(0, 2), 16) & 255);
            res[1] = (byte) (Integer.parseInt(format.substring(2), 16) & 255);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return res;
    }
}
