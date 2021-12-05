package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.CRC32;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class UploadFileRequest extends Request {
    public ArrayList<byte[]> packets = new ArrayList<>();
    public UploadState state;

    public enum UploadState {
        INITIALIZED,
        UPLOAD,
        UPLOADED,
        ERROR
    }

    public UploadFileRequest(short handle, byte[] file) {
        int fileLength = file.length + 4;
        ByteBuffer buffer = createBuffer();
        buffer.putShort(1, handle);
        buffer.putInt(3, 0);
        buffer.putInt(7, fileLength);
        buffer.putInt(11, fileLength);
        this.data = buffer.array();
        prepareFilePackets(file);
        this.state = UploadState.INITIALIZED;
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value.length == 4) {
            if (value[1] != 0) {
                this.state = UploadState.ERROR;
            } else {
                this.state = UploadState.UPLOAD;
            }
        } else if (value.length != 9) {
        } else {
            if (value[1] != 0) {
                this.state = UploadState.ERROR;
            } else {
                this.state = UploadState.UPLOADED;
            }
        }
    }

    private void prepareFilePackets(byte[] file) {
        byte[] packet;
        ByteBuffer buffer = ByteBuffer.allocate(file.length + 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(file);
        CRC32 crc = new CRC32();
        crc.update(file);
        buffer.putInt((int) crc.getValue());
        byte[] fileFull = buffer.array();
        int i = 0;
        int sequence = 0;
        while (i < fileFull.length + 4) {
            if (i + 18 >= fileFull.length) {
                packet = new byte[((fileFull.length - i) + 2)];
                System.arraycopy(fileFull, i, packet, 2, fileFull.length - i);
            } else {
                packet = new byte[20];
                System.arraycopy(fileFull, i, packet, 2, 18);
            }
            packet[0] = 18;
            packet[1] = (byte) sequence;
            this.packets.add(packet);
            i += 18;
            sequence++;
        }
        byte[] bArr = this.packets.get(0);
        bArr[1] = (byte) (bArr[1] | 64);
        if (this.packets.size() > 1) {
            ArrayList<byte[]> arrayList = this.packets;
            byte[] bArr2 = arrayList.get(arrayList.size() - 1);
            bArr2[1] = (byte) (bArr2[1] | 128);
        }
    }

    public byte[] getStartSequence() {
        return new byte[]{17};
    }

    public int getPayloadLength() {
        return 15;
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0007-957f-7d4a-34a6-74696673696d");
    }
}
