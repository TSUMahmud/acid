package nodomain.freeyourgadget.gadgetbridge.entities;

import p008de.greenrobot.dao.DaoException;

public class CalendarSyncState {
    private long calendarEntryId;
    private transient DaoSession daoSession;
    private Device device;
    private long deviceId;
    private Long device__resolvedKey;
    private int hash;

    /* renamed from: id */
    private Long f139id;
    private transient CalendarSyncStateDao myDao;

    public CalendarSyncState() {
    }

    public CalendarSyncState(Long id) {
        this.f139id = id;
    }

    public CalendarSyncState(Long id, long deviceId2, long calendarEntryId2, int hash2) {
        this.f139id = id;
        this.deviceId = deviceId2;
        this.calendarEntryId = calendarEntryId2;
        this.hash = hash2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getCalendarSyncStateDao() : null;
    }

    public Long getId() {
        return this.f139id;
    }

    public void setId(Long id) {
        this.f139id = id;
    }

    public long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(long deviceId2) {
        this.deviceId = deviceId2;
    }

    public long getCalendarEntryId() {
        return this.calendarEntryId;
    }

    public void setCalendarEntryId(long calendarEntryId2) {
        this.calendarEntryId = calendarEntryId2;
    }

    public int getHash() {
        return this.hash;
    }

    public void setHash(int hash2) {
        this.hash = hash2;
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
        CalendarSyncStateDao calendarSyncStateDao = this.myDao;
        if (calendarSyncStateDao != null) {
            calendarSyncStateDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        CalendarSyncStateDao calendarSyncStateDao = this.myDao;
        if (calendarSyncStateDao != null) {
            calendarSyncStateDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        CalendarSyncStateDao calendarSyncStateDao = this.myDao;
        if (calendarSyncStateDao != null) {
            calendarSyncStateDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
