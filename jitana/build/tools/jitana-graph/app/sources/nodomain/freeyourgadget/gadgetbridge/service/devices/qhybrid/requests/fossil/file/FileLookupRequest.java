package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.zip.CRC32;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public class FileLookupRequest extends FossilRequest {
    private FossilWatchAdapter adapter;
    private ByteBuffer fileBuffer;
    private byte[] fileData;
    private byte fileType;
    protected boolean finished = false;
    private short handle = -1;

    public FileLookupRequest(byte fileType2, FossilWatchAdapter adapter2) {
        this.fileType = fileType2;
        this.adapter = adapter2;
        this.data = createBuffer().put(fileType2).array();
    }

    /* access modifiers changed from: protected */
    public FossilWatchAdapter getAdapter() {
        return this.adapter;
    }

    public short getHandle() {
        if (this.finished) {
            return this.handle;
        }
        throw new UnsupportedOperationException("File lookup not finished");
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        byte first = value[0];
        if (characteristic.getUuid().toString().equals("3dda0003-957f-7d4a-34a6-74696673696d")) {
            if ((first & 15) == 2) {
                ByteBuffer buffer = ByteBuffer.wrap(value);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                short s = buffer.getShort(1);
                int size = buffer.getInt(4);
                byte status = buffer.get(3);
                if (status == 0) {
                    short s2 = this.handle;
                    log("file size: " + size);
                    this.fileBuffer = ByteBuffer.allocate(size);
                    return;
                }
                throw new RuntimeException("file lookup error: " + status);
            } else if ((first & 15) == 8) {
                this.finished = true;
                ByteBuffer buffer2 = ByteBuffer.wrap(value);
                buffer2.order(ByteOrder.LITTLE_ENDIAN);
                CRC32 crc = new CRC32();
                crc.update(this.fileData);
                if (((int) crc.getValue()) == buffer2.getInt(8)) {
                    ByteBuffer dataBuffer = ByteBuffer.wrap(this.fileData);
                    dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    this.handle = dataBuffer.getShort(0);
                    handleFileLookup(this.handle);
                    return;
                }
                throw new RuntimeException("handle: " + this.handle + "   expected: " + this.handle);
            }
        } else if (characteristic.getUuid().toString().equals("3dda0004-957f-7d4a-34a6-74696673696d")) {
            this.fileBuffer.put(value, 1, value.length - 1);
            if ((first & 128) == 128) {
                this.fileData = this.fileBuffer.array();
            }
        }
    }

    public void handleFileLookup(short fileHandle) {
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0003-957f-7d4a-34a6-74696673696d");
    }

    public byte[] getStartSequence() {
        return new byte[]{2, -1};
    }

    public int getPayloadLength() {
        return 3;
    }
}
