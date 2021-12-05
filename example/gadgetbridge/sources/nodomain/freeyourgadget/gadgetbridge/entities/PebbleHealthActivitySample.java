package nodomain.freeyourgadget.gadgetbridge.entities;

import p008de.greenrobot.dao.DaoException;

public class PebbleHealthActivitySample extends AbstractPebbleHealthActivitySample {
    private transient DaoSession daoSession;
    private Device device;
    private long deviceId;
    private Long device__resolvedKey;
    private int heartRate;
    private transient PebbleHealthActivitySampleDao myDao;
    private int rawIntensity;
    private byte[] rawPebbleHealthData;
    private int steps;
    private int timestamp;
    private User user;
    private long userId;
    private Long user__resolvedKey;

    public PebbleHealthActivitySample() {
    }

    public PebbleHealthActivitySample(int timestamp2, long deviceId2) {
        this.timestamp = timestamp2;
        this.deviceId = deviceId2;
    }

    public PebbleHealthActivitySample(int timestamp2, long deviceId2, long userId2, byte[] rawPebbleHealthData2, int rawIntensity2, int steps2, int heartRate2) {
        this.timestamp = timestamp2;
        this.deviceId = deviceId2;
        this.userId = userId2;
        this.rawPebbleHealthData = rawPebbleHealthData2;
        this.rawIntensity = rawIntensity2;
        this.steps = steps2;
        this.heartRate = heartRate2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getPebbleHealthActivitySampleDao() : null;
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

    public byte[] getRawPebbleHealthData() {
        return this.rawPebbleHealthData;
    }

    public void setRawPebbleHealthData(byte[] rawPebbleHealthData2) {
        this.rawPebbleHealthData = rawPebbleHealthData2;
    }

    public int getRawIntensity() {
        return this.rawIntensity;
    }

    public void setRawIntensity(int rawIntensity2) {
        this.rawIntensity = rawIntensity2;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setSteps(int steps2) {
        this.steps = steps2;
    }

    public int getHeartRate() {
        return this.heartRate;
    }

    public void setHeartRate(int heartRate2) {
        this.heartRate = heartRate2;
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
        PebbleHealthActivitySampleDao pebbleHealthActivitySampleDao = this.myDao;
        if (pebbleHealthActivitySampleDao != null) {
            pebbleHealthActivitySampleDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        PebbleHealthActivitySampleDao pebbleHealthActivitySampleDao = this.myDao;
        if (pebbleHealthActivitySampleDao != null) {
            pebbleHealthActivitySampleDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        PebbleHealthActivitySampleDao pebbleHealthActivitySampleDao = this.myDao;
        if (pebbleHealthActivitySampleDao != null) {
            pebbleHealthActivitySampleDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
