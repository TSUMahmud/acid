package p005ch.qos.logback.core.rolling.helper;

/* renamed from: ch.qos.logback.core.rolling.helper.PeriodicityType */
public enum PeriodicityType {
    ERRONEOUS,
    TOP_OF_MILLISECOND,
    TOP_OF_SECOND,
    TOP_OF_MINUTE,
    TOP_OF_HOUR,
    HALF_DAY,
    TOP_OF_DAY,
    TOP_OF_WEEK,
    TOP_OF_MONTH;
    
    static PeriodicityType[] VALID_ORDERED_LIST;

    static {
        PeriodicityType periodicityType;
        PeriodicityType periodicityType2;
        PeriodicityType periodicityType3;
        PeriodicityType periodicityType4;
        PeriodicityType periodicityType5;
        PeriodicityType periodicityType6;
        PeriodicityType periodicityType7;
        VALID_ORDERED_LIST = new PeriodicityType[]{periodicityType, periodicityType2, periodicityType3, periodicityType4, periodicityType5, periodicityType6, periodicityType7};
    }
}
