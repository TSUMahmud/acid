package nodomain.freeyourgadget.gadgetbridge.entities;

import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.model.ValidByDate;

public class DeviceAttributes implements ValidByDate {
    private long deviceId;
    private String firmwareVersion1;
    private String firmwareVersion2;

    /* renamed from: id */
    private Long f142id;
    private Date validFromUTC;
    private Date validToUTC;
    private String volatileIdentifier;

    public DeviceAttributes() {
    }

    public DeviceAttributes(Long id) {
        this.f142id = id;
    }

    public DeviceAttributes(Long id, String firmwareVersion12, String firmwareVersion22, Date validFromUTC2, Date validToUTC2, long deviceId2, String volatileIdentifier2) {
        this.f142id = id;
        this.firmwareVersion1 = firmwareVersion12;
        this.firmwareVersion2 = firmwareVersion22;
        this.validFromUTC = validFromUTC2;
        this.validToUTC = validToUTC2;
        this.deviceId = deviceId2;
        this.volatileIdentifier = volatileIdentifier2;
    }

    public Long getId() {
        return this.f142id;
    }

    public void setId(Long id) {
        this.f142id = id;
    }

    public String getFirmwareVersion1() {
        return this.firmwareVersion1;
    }

    public void setFirmwareVersion1(String firmwareVersion12) {
        this.firmwareVersion1 = firmwareVersion12;
    }

    public String getFirmwareVersion2() {
        return this.firmwareVersion2;
    }

    public void setFirmwareVersion2(String firmwareVersion22) {
        this.firmwareVersion2 = firmwareVersion22;
    }

    public Date getValidFromUTC() {
        return this.validFromUTC;
    }

    public void setValidFromUTC(Date validFromUTC2) {
        this.validFromUTC = validFromUTC2;
    }

    public Date getValidToUTC() {
        return this.validToUTC;
    }

    public void setValidToUTC(Date validToUTC2) {
        this.validToUTC = validToUTC2;
    }

    public long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(long deviceId2) {
        this.deviceId = deviceId2;
    }

    public String getVolatileIdentifier() {
        return this.volatileIdentifier;
    }

    public void setVolatileIdentifier(String volatileIdentifier2) {
        this.volatileIdentifier = volatileIdentifier2;
    }
}
