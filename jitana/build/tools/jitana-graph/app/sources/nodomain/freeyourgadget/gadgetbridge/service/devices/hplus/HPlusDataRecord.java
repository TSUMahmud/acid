package nodomain.freeyourgadget.gadgetbridge.service.devices.hplus;

public class HPlusDataRecord {
    public static final int TYPE_DAY_SLOT = 102;
    public static final int TYPE_DAY_SUMMARY = 101;
    public static final int TYPE_REALTIME = 103;
    public static final int TYPE_SLEEP = 100;
    public static final int TYPE_UNKNOWN = 0;
    public int activityKind = 0;
    public byte[] rawData;
    public int timestamp;
    public int type = 0;

    protected HPlusDataRecord() {
    }

    protected HPlusDataRecord(byte[] data, int type2) {
        this.rawData = data;
        this.type = type2;
    }

    public byte[] getRawData() {
        return this.rawData;
    }

    public class RecordInterval {
        public int activityKind;
        public int timestampFrom;
        public int timestampTo;

        RecordInterval(int timestampFrom2, int timestampTo2, int activityKind2) {
            this.timestampFrom = timestampFrom2;
            this.timestampTo = timestampTo2;
            this.activityKind = activityKind2;
        }
    }
}
