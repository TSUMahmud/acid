package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public class FileCloseAndPutRequest extends FileCloseRequest {
    FossilWatchAdapter adapter;
    byte[] data;

    public FileCloseAndPutRequest(short fileHandle, byte[] data2, FossilWatchAdapter adapter2) {
        super(fileHandle);
        this.adapter = adapter2;
        this.data = data2;
    }

    public void onPrepare() {
        super.onPrepare();
        this.adapter.queueWrite((FossilRequest) new FilePutRequest(getHandle(), this.data, this.adapter) {
            public void onFilePut(boolean success) {
                super.onFilePut(success);
                FileCloseAndPutRequest.this.onFilePut(success);
            }
        }, false);
    }

    public void onFilePut(boolean success) {
    }
}
