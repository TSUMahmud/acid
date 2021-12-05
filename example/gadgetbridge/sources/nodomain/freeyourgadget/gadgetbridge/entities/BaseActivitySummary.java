package nodomain.freeyourgadget.gadgetbridge.entities;

import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySummary;
import p008de.greenrobot.dao.DaoException;

public class BaseActivitySummary implements ActivitySummary {
    private int activityKind;
    private Integer baseAltitude;
    private Integer baseLatitude;
    private Integer baseLongitude;
    private transient DaoSession daoSession;
    private Device device;
    private long deviceId;
    private Long device__resolvedKey;
    private Date endTime;
    private String gpxTrack;

    /* renamed from: id */
    private Long f137id;
    private transient BaseActivitySummaryDao myDao;
    private String name;
    private Date startTime;
    private User user;
    private long userId;
    private Long user__resolvedKey;

    public BaseActivitySummary() {
    }

    public BaseActivitySummary(Long id) {
        this.f137id = id;
    }

    public BaseActivitySummary(Long id, String name2, Date startTime2, Date endTime2, int activityKind2, Integer baseLongitude2, Integer baseLatitude2, Integer baseAltitude2, String gpxTrack2, long deviceId2, long userId2) {
        this.f137id = id;
        this.name = name2;
        this.startTime = startTime2;
        this.endTime = endTime2;
        this.activityKind = activityKind2;
        this.baseLongitude = baseLongitude2;
        this.baseLatitude = baseLatitude2;
        this.baseAltitude = baseAltitude2;
        this.gpxTrack = gpxTrack2;
        this.deviceId = deviceId2;
        this.userId = userId2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getBaseActivitySummaryDao() : null;
    }

    public Long getId() {
        return this.f137id;
    }

    public void setId(Long id) {
        this.f137id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime2) {
        this.startTime = startTime2;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime2) {
        this.endTime = endTime2;
    }

    public int getActivityKind() {
        return this.activityKind;
    }

    public void setActivityKind(int activityKind2) {
        this.activityKind = activityKind2;
    }

    public Integer getBaseLongitude() {
        return this.baseLongitude;
    }

    public void setBaseLongitude(Integer baseLongitude2) {
        this.baseLongitude = baseLongitude2;
    }

    public Integer getBaseLatitude() {
        return this.baseLatitude;
    }

    public void setBaseLatitude(Integer baseLatitude2) {
        this.baseLatitude = baseLatitude2;
    }

    public Integer getBaseAltitude() {
        return this.baseAltitude;
    }

    public void setBaseAltitude(Integer baseAltitude2) {
        this.baseAltitude = baseAltitude2;
    }

    public String getGpxTrack() {
        return this.gpxTrack;
    }

    public void setGpxTrack(String gpxTrack2) {
        this.gpxTrack = gpxTrack2;
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
        BaseActivitySummaryDao baseActivitySummaryDao = this.myDao;
        if (baseActivitySummaryDao != null) {
            baseActivitySummaryDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        BaseActivitySummaryDao baseActivitySummaryDao = this.myDao;
        if (baseActivitySummaryDao != null) {
            baseActivitySummaryDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        BaseActivitySummaryDao baseActivitySummaryDao = this.myDao;
        if (baseActivitySummaryDao != null) {
            baseActivitySummaryDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
