package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.zip.CRC32;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public abstract class FileGetRequest extends FossilRequest {
    private FossilWatchAdapter adapter;
    private ByteBuffer fileBuffer;
    private byte[] fileData;
    private boolean finished = false;
    private short handle;

    public abstract void handleFileData(byte[] bArr);

    public FileGetRequest(short handle2, FossilWatchAdapter adapter2) {
        this.handle = handle2;
        this.adapter = adapter2;
        this.data = createBuffer().putShort(handle2).putInt(0).putInt(-1).array();
    }

    public FossilWatchAdapter getAdapter() {
        return this.adapter;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        byte first = value[0];
        if (characteristic.getUuid().toString().equals("3dda0003-957f-7d4a-34a6-74696673696d")) {
            if ((first & 15) == 1) {
                ByteBuffer buffer = ByteBuffer.wrap(value);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                short handle2 = buffer.getShort(1);
                int size = buffer.getInt(4);
                byte status = buffer.get(3);
                if (status != 0) {
                    throw new RuntimeException("FileGet error: " + status);
                } else if (this.handle == handle2) {
                    log("file size: " + size);
                    this.fileBuffer = ByteBuffer.allocate(size);
                } else {
                    throw new RuntimeException("handle: " + handle2 + "   expected: " + this.handle);
                }
            } else if ((first & 15) == 8) {
                this.finished = true;
                ByteBuffer buffer2 = ByteBuffer.wrap(value);
                buffer2.order(ByteOrder.LITTLE_ENDIAN);
                short handle3 = buffer2.getShort(1);
                if (this.handle == handle3) {
                    CRC32 crc = new CRC32();
                    crc.update(this.fileData);
                    if (((int) crc.getValue()) == buffer2.getInt(8)) {
                        handleFileData(this.fileData);
                        return;
                    }
                    throw new RuntimeException("handle: " + handle3 + "   expected: " + this.handle);
                }
                throw new RuntimeException("handle: " + handle3 + "   expected: " + this.handle);
            }
        } else if (characteristic.getUuid().toString().equals("3dda0004-957f-7d4a-34a6-74696673696d")) {
            this.fileBuffer.put(value, 1, value.length - 1);
            if ((first & 128) == 128) {
                this.fileData = this.fileBuffer.array();
            }
        }
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0003-957f-7d4a-34a6-74696673696d");
    }

    public byte[] getStartSequence() {
        return new byte[]{1};
    }

    public int getPayloadLength() {
        return 11;
    }
}
