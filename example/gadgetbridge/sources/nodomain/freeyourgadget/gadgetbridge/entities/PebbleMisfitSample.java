package nodomain.freeyourgadget.gadgetbridge.entities;

import p008de.greenrobot.dao.DaoException;

public class PebbleMisfitSample extends AbstractPebbleMisfitActivitySample {
    private transient DaoSession daoSession;
    private Device device;
    private long deviceId;
    private Long device__resolvedKey;
    private transient PebbleMisfitSampleDao myDao;
    private int rawPebbleMisfitSample;
    private int timestamp;
    private User user;
    private long userId;
    private Long user__resolvedKey;

    public PebbleMisfitSample() {
    }

    public PebbleMisfitSample(int timestamp2, long deviceId2) {
        this.timestamp = timestamp2;
        this.deviceId = deviceId2;
    }

    public PebbleMisfitSample(int timestamp2, long deviceId2, long userId2, int rawPebbleMisfitSample2) {
        this.timestamp = timestamp2;
        this.deviceId = deviceId2;
        this.userId = userId2;
        this.rawPebbleMisfitSample = rawPebbleMisfitSample2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getPebbleMisfitSampleDao() : null;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp2) {
        this.timestamp = timestamp2;
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

    public int getRawPebbleMisfitSample() {
        return this.rawPebbleMisfitSample;
    }

    public void setRawPebbleMisfitSample(int rawPebbleMisfitSample2) {
        this.rawPebbleMisfitSample = rawPebbleMisfitSample2;
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
        PebbleMisfitSampleDao pebbleMisfitSampleDao = this.myDao;
        if (pebbleMisfitSampleDao != null) {
            pebbleMisfitSampleDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        PebbleMisfitSampleDao pebbleMisfitSampleDao = this.myDao;
        if (pebbleMisfitSampleDao != null) {
            pebbleMisfitSampleDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        PebbleMisfitSampleDao pebbleMisfitSampleDao = this.myDao;
        if (pebbleMisfitSampleDao != null) {
            pebbleMisfitSampleDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
