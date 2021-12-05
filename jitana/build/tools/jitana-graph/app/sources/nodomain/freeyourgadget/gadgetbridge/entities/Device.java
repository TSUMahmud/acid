package nodomain.freeyourgadget.gadgetbridge.entities;

import java.util.List;
import p008de.greenrobot.dao.DaoException;

public class Device {
    private transient DaoSession daoSession;
    private List<DeviceAttributes> deviceAttributesList;

    /* renamed from: id */
    private Long f141id;
    private String identifier;
    private String manufacturer;
    private String model;
    private transient DeviceDao myDao;
    private String name;
    private int type;

    public Device() {
    }

    public Device(Long id) {
        this.f141id = id;
    }

    public Device(Long id, String name2, String manufacturer2, String identifier2, int type2, String model2) {
        this.f141id = id;
        this.name = name2;
        this.manufacturer = manufacturer2;
        this.identifier = identifier2;
        this.type = type2;
        this.model = model2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getDeviceDao() : null;
    }

    public Long getId() {
        return this.f141id;
    }

    public void setId(Long id) {
        this.f141id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer2) {
        this.manufacturer = manufacturer2;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier2) {
        this.identifier = identifier2;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type2) {
        this.type = type2;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model2) {
        this.model = model2;
    }

    public List<DeviceAttributes> getDeviceAttributesList() {
        if (this.deviceAttributesList == null) {
            DaoSession daoSession2 = this.daoSession;
            if (daoSession2 != null) {
                List<DeviceAttributes> deviceAttributesListNew = daoSession2.getDeviceAttributesDao()._queryDevice_DeviceAttributesList(this.f141id.longValue());
                synchronized (this) {
                    if (this.deviceAttributesList == null) {
                        this.deviceAttributesList = deviceAttributesListNew;
                    }
                }
            } else {
                throw new DaoException("Entity is detached from DAO context");
            }
        }
        return this.deviceAttributesList;
    }

    public synchronized void resetDeviceAttributesList() {
        this.deviceAttributesList = null;
    }

    public void delete() {
        DeviceDao deviceDao = this.myDao;
        if (deviceDao != null) {
            deviceDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        DeviceDao deviceDao = this.myDao;
        if (deviceDao != null) {
            deviceDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        DeviceDao deviceDao = this.myDao;
        if (deviceDao != null) {
            deviceDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
