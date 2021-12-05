package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo;

import android.os.Parcel;
import android.os.Parcelable;
import p005ch.qos.logback.core.CoreConstants;

public class DeviceInfo implements Parcelable {
    public static final Parcelable.Creator<DeviceInfo> CREATOR = new Parcelable.Creator<DeviceInfo>() {
        public DeviceInfo createFromParcel(Parcel in) {
            return new DeviceInfo(in);
        }

        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };
    private String firmwareRevision;
    private String hardwareRevision;
    private String manufacturerName;
    private String modelNumber;
    private String pnpId;
    private String regulatoryCertificationDataList;
    private String serialNumber;
    private String softwareRevision;
    private String systemId;

    public DeviceInfo() {
    }

    protected DeviceInfo(Parcel in) {
        this.manufacturerName = in.readString();
        this.modelNumber = in.readString();
        this.serialNumber = in.readString();
        this.hardwareRevision = in.readString();
        this.firmwareRevision = in.readString();
        this.softwareRevision = in.readString();
        this.systemId = in.readString();
        this.regulatoryCertificationDataList = in.readString();
        this.pnpId = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.manufacturerName);
        dest.writeString(this.modelNumber);
        dest.writeString(this.serialNumber);
        dest.writeString(this.hardwareRevision);
        dest.writeString(this.firmwareRevision);
        dest.writeString(this.softwareRevision);
        dest.writeString(this.systemId);
        dest.writeString(this.regulatoryCertificationDataList);
        dest.writeString(this.pnpId);
    }

    public int describeContents() {
        return 0;
    }

    public String getManufacturerName() {
        return this.manufacturerName;
    }

    public void setManufacturerName(String manufacturerName2) {
        this.manufacturerName = manufacturerName2;
    }

    public String getModelNumber() {
        return this.modelNumber;
    }

    public void setModelNumber(String modelNumber2) {
        this.modelNumber = modelNumber2;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber2) {
        this.serialNumber = serialNumber2;
    }

    public String getHardwareRevision() {
        return this.hardwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision2) {
        this.hardwareRevision = hardwareRevision2;
    }

    public String getFirmwareRevision() {
        return this.firmwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision2) {
        this.firmwareRevision = firmwareRevision2;
    }

    public String getSoftwareRevision() {
        return this.softwareRevision;
    }

    public void setSoftwareRevision(String softwareRevision2) {
        this.softwareRevision = softwareRevision2;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(String systemId2) {
        this.systemId = systemId2;
    }

    public String getRegulatoryCertificationDataList() {
        return this.regulatoryCertificationDataList;
    }

    public void setRegulatoryCertificationDataList(String regulatoryCertificationDataList2) {
        this.regulatoryCertificationDataList = regulatoryCertificationDataList2;
    }

    public String getPnpId() {
        return this.pnpId;
    }

    public void setPnpId(String pnpId2) {
        this.pnpId = pnpId2;
    }

    public String toString() {
        return "DeviceInfo{manufacturerName='" + this.manufacturerName + CoreConstants.SINGLE_QUOTE_CHAR + ", modelNumber='" + this.modelNumber + CoreConstants.SINGLE_QUOTE_CHAR + ", serialNumber='" + this.serialNumber + CoreConstants.SINGLE_QUOTE_CHAR + ", hardwareRevision='" + this.hardwareRevision + CoreConstants.SINGLE_QUOTE_CHAR + ", firmwareRevision='" + this.firmwareRevision + CoreConstants.SINGLE_QUOTE_CHAR + ", softwareRevision='" + this.softwareRevision + CoreConstants.SINGLE_QUOTE_CHAR + ", systemId='" + this.systemId + CoreConstants.SINGLE_QUOTE_CHAR + ", regulatoryCertificationDataList='" + this.regulatoryCertificationDataList + CoreConstants.SINGLE_QUOTE_CHAR + ", pnpId='" + this.pnpId + CoreConstants.SINGLE_QUOTE_CHAR + CoreConstants.CURLY_RIGHT;
    }
}
