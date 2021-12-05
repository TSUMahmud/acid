package nodomain.freeyourgadget.gadgetbridge.service.btle;

import java.io.IOException;

public interface BTLEOperation {
    String getName();

    void perform() throws IOException;
}
