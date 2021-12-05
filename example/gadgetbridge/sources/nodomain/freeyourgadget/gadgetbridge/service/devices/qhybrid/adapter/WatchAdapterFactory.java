package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter;

import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.misfit.MisfitWatchAdapter;

public final class WatchAdapterFactory {
    public final WatchAdapter createWatchAdapter(String firmwareVersion, QHybridSupport deviceSupport) {
        char major = firmwareVersion.charAt(6);
        if (major == '1') {
            return new MisfitWatchAdapter(deviceSupport);
        }
        if (major == '2') {
            return new FossilWatchAdapter(deviceSupport);
        }
        throw new UnsupportedOperationException("Firmware " + firmwareVersion + " not supported");
    }
}
