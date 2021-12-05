package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

public class DownloadFileRequest extends FileRequest {
    ByteBuffer buffer = null;
    public byte[] file = null;
    public int fileHandle;
    public int size;
    public long timeStamp;

    public DownloadFileRequest(short handle) {
        init(handle, 0, 65535);
    }

    public DownloadFileRequest(short handle, int offset, int length) {
        init(handle, offset, length);
    }

    private void init(short handle, int offset, int length) {
        ByteBuffer buffer2 = createBuffer();
        buffer2.putShort(handle);
        buffer2.putInt(offset);
        buffer2.putInt(length);
        this.data = buffer2.array();
        this.fileHandle = handle;
        this.timeStamp = System.currentTimeMillis();
    }

    public byte[] getStartSequence() {
        return new byte[]{1};
    }

    public int getPayloadLength() {
        return 11;
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        byte[] data = characteristic.getValue();
        if (characteristic.getUuid().toString().equals("3dda0003-957f-7d4a-34a6-74696673696d")) {
            if (this.buffer == null) {
                this.buffer = ByteBuffer.allocate(4096);
                ByteBuffer buffer1 = ByteBuffer.wrap(data);
                buffer1.order(ByteOrder.LITTLE_ENDIAN);
                this.status = buffer1.get(3);
                short realHandle = buffer1.getShort(1);
                if (this.status != 0) {
                    log("wrong status: " + this.status);
                } else if (realHandle != this.fileHandle) {
                    log("wrong handle: " + realHandle);
                    this.completed = true;
                } else {
                    log("handle: " + realHandle);
                }
            } else {
                this.completed = true;
            }
        } else if (characteristic.getUuid().toString().equals("3dda0004-957f-7d4a-34a6-74696673696d")) {
            this.buffer.put(data, 1, data.length - 1);
            if ((data[0] & Byte.MIN_VALUE) != 0) {
                ByteBuffer buffer12 = ByteBuffer.allocate(this.buffer.position());
                buffer12.put(this.buffer.array(), 0, this.buffer.position());
                buffer12.order(ByteOrder.LITTLE_ENDIAN);
                this.file = buffer12.array();
                CRC32 crc = new CRC32();
                byte[] bArr = this.file;
                crc.update(bArr, 0, bArr.length - 4);
                this.size = this.file.length;
                log("file content: " + bytesToString(this.file));
                if (crc.getValue() != cutBits(buffer12.getInt(this.size - 4))) {
                    log("checksum invalid    expected: " + buffer12.getInt(this.size - 4) + "   actual: " + crc.getValue());
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public long cutBits(int value) {
        return ((long) value) & 4294967295L;
    }

    private String bytesToString(byte[] bytes) {
        String s = "";
        for (byte b : bytes) {
            s = (s + "0123456789ABCDEF".charAt((b >> 4) & 15)) + "0123456789ABCDEF".charAt((b >> 0) & 15);
        }
        return s;
    }
}
