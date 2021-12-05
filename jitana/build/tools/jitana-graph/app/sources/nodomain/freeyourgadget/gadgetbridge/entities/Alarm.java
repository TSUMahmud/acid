package nodomain.freeyourgadget.gadgetbridge.entities;

import p008de.greenrobot.dao.DaoException;

public class Alarm implements nodomain.freeyourgadget.gadgetbridge.model.Alarm {
    private transient DaoSession daoSession;
    private Device device;
    private long deviceId;
    private Long device__resolvedKey;
    private boolean enabled;
    private int hour;
    private int minute;
    private transient AlarmDao myDao;
    private int position;
    private int repetition;
    private boolean smartWakeup;
    private boolean snooze;
    private boolean unused;
    private User user;
    private long userId;
    private Long user__resolvedKey;

    public Alarm() {
    }

    public Alarm(long deviceId2, long userId2, int position2, boolean enabled2, boolean smartWakeup2, boolean snooze2, int repetition2, int hour2, int minute2, boolean unused2) {
        this.deviceId = deviceId2;
        this.userId = userId2;
        this.position = position2;
        this.enabled = enabled2;
        this.smartWakeup = smartWakeup2;
        this.snooze = snooze2;
        this.repetition = repetition2;
        this.hour = hour2;
        this.minute = minute2;
        this.unused = unused2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getAlarmDao() : null;
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

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position2) {
        this.position = position2;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled2) {
        this.enabled = enabled2;
    }

    public boolean getSmartWakeup() {
        return this.smartWakeup;
    }

    public void setSmartWakeup(boolean smartWakeup2) {
        this.smartWakeup = smartWakeup2;
    }

    public boolean getSnooze() {
        return this.snooze;
    }

    public void setSnooze(boolean snooze2) {
        this.snooze = snooze2;
    }

    public boolean getRepetition(int dow) {
        return (this.repetition & dow) > 0;
    }

    public boolean isRepetitive() {
        return getRepetition() != 0;
    }

    public int getRepetition() {
        return this.repetition;
    }

    public void setRepetition(int repetition2) {
        this.repetition = repetition2;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour2) {
        this.hour = hour2;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute2) {
        this.minute = minute2;
    }

    public boolean getUnused() {
        return this.unused;
    }

    public void setUnused(boolean unused2) {
        this.unused = unused2;
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

    public void delete() {
        AlarmDao alarmDao = this.myDao;
        if (alarmDao != null) {
            alarmDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        AlarmDao alarmDao = this.myDao;
        if (alarmDao != null) {
            alarmDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        AlarmDao alarmDao = this.myDao;
        if (alarmDao != null) {
            alarmDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
