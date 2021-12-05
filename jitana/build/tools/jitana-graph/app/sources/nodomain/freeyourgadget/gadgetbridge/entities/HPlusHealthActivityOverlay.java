package nodomain.freeyourgadget.gadgetbridge.entities;

import p008de.greenrobot.dao.DaoException;

public class HPlusHealthActivityOverlay {
    private transient DaoSession daoSession;
    private Device device;
    private long deviceId;
    private Long device__resolvedKey;
    private transient HPlusHealthActivityOverlayDao myDao;
    private byte[] rawHPlusHealthData;
    private int rawKind;
    private int timestampFrom;
    private int timestampTo;
    private User user;
    private long userId;
    private Long user__resolvedKey;

    public HPlusHealthActivityOverlay() {
    }

    public HPlusHealthActivityOverlay(int timestampFrom2, int timestampTo2, int rawKind2, long deviceId2) {
        this.timestampFrom = timestampFrom2;
        this.timestampTo = timestampTo2;
        this.rawKind = rawKind2;
        this.deviceId = deviceId2;
    }

    public HPlusHealthActivityOverlay(int timestampFrom2, int timestampTo2, int rawKind2, long deviceId2, long userId2, byte[] rawHPlusHealthData2) {
        this.timestampFrom = timestampFrom2;
        this.timestampTo = timestampTo2;
        this.rawKind = rawKind2;
        this.deviceId = deviceId2;
        this.userId = userId2;
        this.rawHPlusHealthData = rawHPlusHealthData2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getHPlusHealthActivityOverlayDao() : null;
    }

    public int getTimestampFrom() {
        return this.timestampFrom;
    }

    public void setTimestampFrom(int timestampFrom2) {
        this.timestampFrom = timestampFrom2;
    }

    public int getTimestampTo() {
        return this.timestampTo;
    }

    public void setTimestampTo(int timestampTo2) {
        this.timestampTo = timestampTo2;
    }

    public int getRawKind() {
        return this.rawKind;
    }

    public void setRawKind(int rawKind2) {
        this.rawKind = rawKind2;
    }

    public long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(long deviceId2) {
        this.deviceId = deviceId2;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId2) {
        this.userId = userId2;
    }

    public byte[] getRawHPlusHealthData() {
        return this.rawHPlusHealthData;
    }

    public void setRawHPlusHealthData(byte[] rawHPlusHealthData2) {
        this.rawHPlusHealthData = rawHPlusHealthData2;
    }

    public Device getDevice() {
        long __key = this.deviceId;
        Long l = this.device__resolvedKey;
        if (l == null || !l.equals(Long.valueOf(__key))) {
            DaoSession daoSession2 = this.daoSession;
            if (daoSession2 != null) {
                Device deviceNew = (Device) daoSession2.getDeviceDao().load(Long.valueOf(__key));
                synchronized (this) {
                    this.device = deviceNew;
                    this.device__resolvedKey = Long.valueOf(__key);
                }
            } else {
                throw new DaoException("Entity is detached from DAO context");
            }
        }
        return this.device;
    }

    public void setDevice(Device device2) {
        if (device2 != null) {
            synchronized (this) {
                this.device = device2;
                this.deviceId = device2.getId().longValue();
                this.device__resolvedKey = Long.valueOf(this.deviceId);
            }
            return;
        }
        throw new DaoException("To-one property 'deviceId' has not-null constraint; cannot set to-one to null");
    }

    public User getUser() {
        long __key = this.userId;
        Long l = this.user__resolvedKey;
        if (l == null || !l.equals(Long.valueOf(__key))) {
            DaoSession daoSession2 = this.daoSession;
            if (daoSession2 != null) {
                User userNew = (User) daoSession2.getUserDao().load(Long.valueOf(__key));
                synchronized (this) {
                    this.user = userNew;
                    this.user__resolvedKey = Long.valueOf(__key);
                }
            } else {
                throw new DaoException("Entity is detached from DAO context");
            }
        }
        return this.user;
    }

    public void setUser(User user2) {
        if (user2 != null) {
            synchronized (this) {
                this.user = user2;
                this.userId = user2.getId().longValue();
                this.user__resolvedKey = Long.valueOf(this.userId);
            }
            return;
        }
        throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
    }

    public void delete() {
        HPlusHealthActivityOverlayDao hPlusHealthActivityOverlayDao = this.myDao;
        if (hPlusHealthActivityOverlayDao != null) {
            hPlusHealthActivityOverlayDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        HPlusHealthActivityOverlayDao hPlusHealthActivityOverlayDao = this.myDao;
        if (hPlusHealthActivityOverlayDao != null) {
            hPlusHealthActivityOverlayDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        HPlusHealthActivityOverlayDao hPlusHealthActivityOverlayDao = this.myDao;
        if (hPlusHealthActivityOverlayDao != null) {
            hPlusHealthActivityOverlayDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
