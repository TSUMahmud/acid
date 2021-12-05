package nodomain.freeyourgadget.gadgetbridge.service.btle;

import java.util.UUID;

public class GattDescriptor {
    public static final UUID UUID_DESCRIPTOR_ES_CONFIGURATION = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"290B"}));
    public static final UUID UUID_DESCRIPTOR_ES_MEASUREMENT = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"290C"}));
    public static final UUID UUID_DESCRIPTOR_ES_TRIGGER_SETTING = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"290D"}));
    public static final UUID UUID_DESCRIPTOR_EXTERNAL_REPORT_REFERENCE = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2907"}));
    public static final UUID UUID_DESCRIPTOR_GATT_CHARACTERISTIC_AGGREGATE_FORMAT = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2905"}));
    public static final UUID UUID_DESCRIPTOR_GATT_CHARACTERISTIC_EXTENDED_PROPERTIES = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2900"}));
    public static final UUID UUID_DESCRIPTOR_GATT_CHARACTERISTIC_PRESENTATION_FORMAT = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2904"}));
    public static final UUID UUID_DESCRIPTOR_GATT_CHARACTERISTIC_USER_DESCRIPTION = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2901"}));
    public static final UUID UUID_DESCRIPTOR_GATT_CLIENT_CHARACTERISTIC_CONFIGURATION = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2902"}));
    public static final UUID UUID_DESCRIPTOR_GATT_SERVER_CHARACTERISTIC_CONFIGURATION = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2903"}));
    public static final UUID UUID_DESCRIPTOR_NUMBER_OF_DIGITALS = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2909"}));
    public static final UUID UUID_DESCRIPTOR_REPORT_REFERENCE = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2908"}));
    public static final UUID UUID_DESCRIPTOR_TIME_TRIGGER_SETTING = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"290E"}));
    public static final UUID UUID_DESCRIPTOR_VALID_RANGE = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"2906"}));
    public static final UUID UUID_DESCRIPTOR_VALUE_TRIGGER_SETTING = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"290A"}));
}
