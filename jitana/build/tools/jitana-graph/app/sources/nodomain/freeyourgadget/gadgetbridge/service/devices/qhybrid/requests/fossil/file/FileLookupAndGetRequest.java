package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public abstract class FileLookupAndGetRequest extends FileLookupRequest {
    public abstract void handleFileData(byte[] bArr);

    public FileLookupAndGetRequest(byte fileType, FossilWatchAdapter adapter) {
        super(fileType, adapter);
    }

    public void handleFileLookup(short fileHandle) {
        getAdapter().queueWrite((FossilRequest) new FileGetRequest(getHandle(), getAdapter()) {
            public void handleFileData(byte[] fileData) {
                FileLookupAndGetRequest.this.handleFileData(fileData);
            }
        }, true);
    }
}
