package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.CRC32;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;
import nodomain.freeyourgadget.gadgetbridge.util.CRC32C;

public class FilePutRequest extends FossilRequest {
    private FossilWatchAdapter adapter;
    private byte[] file;
    private int fullCRC;
    private short handle;
    private ArrayList<byte[]> packets = new ArrayList<>();
    public UploadState state;

    public enum UploadState {
        INITIALIZED,
        UPLOADING,
        CLOSING,
        UPLOADED
    }

    public FilePutRequest(short handle2, byte[] file2, FossilWatchAdapter adapter2) {
        this.handle = handle2;
        this.adapter = adapter2;
        int fileLength = file2.length + 16;
        ByteBuffer buffer = createBuffer();
        buffer.putShort(1, handle2);
        buffer.putInt(3, 0);
        buffer.putInt(7, fileLength);
        buffer.putInt(11, fileLength);
        this.data = buffer.array();
        this.file = file2;
        this.state = UploadState.INITIALIZED;
    }

    public short getHandle() {
        return this.handle;
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (characteristic.getUuid().toString().equals("3dda0003-957f-7d4a-34a6-74696673696d")) {
            int responseType = value[0] & 15;
            log("response: " + responseType);
            if (responseType != 3) {
                if (responseType != 4) {
                    if (responseType != 8) {
                        if (responseType == 9) {
                            onFilePut(false);
                            throw new RuntimeException("file put timeout");
                        }
                    } else if (value.length != 4) {
                        ByteBuffer buffer = ByteBuffer.wrap(value);
                        buffer.order(ByteOrder.LITTLE_ENDIAN);
                        short handle2 = buffer.getShort(1);
                        int crc = buffer.getInt(8);
                        byte status = value[3];
                        if (status != 0) {
                            throw new RuntimeException("upload status: " + status);
                        } else if (handle2 != this.handle) {
                            throw new RuntimeException("wrong response handle");
                        } else if (crc == this.fullCRC) {
                            ByteBuffer buffer2 = ByteBuffer.allocate(3);
                            buffer2.order(ByteOrder.LITTLE_ENDIAN);
                            buffer2.put((byte) 4);
                            buffer2.putShort(this.handle);
                            new TransactionBuilder("file close").write(this.adapter.getDeviceSupport().getCharacteristic(UUID.fromString("3dda0003-957f-7d4a-34a6-74696673696d")), buffer2.array()).queue(this.adapter.getDeviceSupport().getQueue());
                            this.state = UploadState.CLOSING;
                        } else {
                            throw new RuntimeException("file upload exception: wrong crc");
                        }
                    }
                } else if (value.length != 9) {
                    if (value.length == 4 && (value[0] & 15) == 4) {
                        ByteBuffer buffer3 = ByteBuffer.wrap(value);
                        buffer3.order(ByteOrder.LITTLE_ENDIAN);
                        if (buffer3.getShort(1) == this.handle) {
                            byte status2 = buffer3.get(3);
                            if (status2 == 0) {
                                this.state = UploadState.UPLOADED;
                                onFilePut(true);
                                log("uploaded file");
                                return;
                            }
                            onFilePut(false);
                            throw new RuntimeException("wrong closing status: " + status2);
                        }
                        onFilePut(false);
                        throw new RuntimeException("wrong file closing handle");
                    }
                    throw new RuntimeException("wrong file closing header");
                }
            } else if (value.length == 5 && (value[0] & 15) == 3) {
                this.state = UploadState.UPLOADING;
                TransactionBuilder transactionBuilder = new TransactionBuilder("file upload");
                BluetoothGattCharacteristic uploadCharacteristic = this.adapter.getDeviceSupport().getCharacteristic(UUID.fromString("3dda0004-957f-7d4a-34a6-74696673696d"));
                prepareFilePackets(this.file);
                Iterator<byte[]> it = this.packets.iterator();
                while (it.hasNext()) {
                    transactionBuilder.write(uploadCharacteristic, it.next());
                }
                transactionBuilder.queue(this.adapter.getDeviceSupport().getQueue());
            } else {
                throw new RuntimeException("wrong answer header");
            }
        }
    }

    public boolean isFinished() {
        return this.state == UploadState.UPLOADED;
    }

    private void prepareFilePackets(byte[] file2) {
        int maxPacketSize = this.adapter.getMTU() - 4;
        ByteBuffer buffer = ByteBuffer.allocate(file2.length + 12 + 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(this.handle);
        buffer.put((byte) 2);
        buffer.put((byte) 0);
        buffer.putInt(0);
        buffer.putInt(file2.length);
        buffer.put(file2);
        CRC32C crc = new CRC32C();
        crc.update(file2, 0, file2.length);
        buffer.putInt((int) crc.getValue());
        byte[] data = buffer.array();
        CRC32 fullCRC2 = new CRC32();
        fullCRC2.update(data);
        this.fullCRC = (int) fullCRC2.getValue();
        int packetCount = (int) Math.ceil((double) (((float) data.length) / ((float) maxPacketSize)));
        for (int i = 0; i < packetCount; i++) {
            int currentPacketLength = Math.min(maxPacketSize, data.length - (i * maxPacketSize));
            byte[] packet = new byte[(currentPacketLength + 1)];
            packet[0] = (byte) i;
            System.arraycopy(data, i * maxPacketSize, packet, 1, currentPacketLength);
            this.packets.add(packet);
        }
    }

    public void onFilePut(boolean success) {
    }

    public byte[] getStartSequence() {
        return new byte[]{3};
    }

    public int getPayloadLength() {
        return 15;
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0003-957f-7d4a-34a6-74696673696d");
    }
}
