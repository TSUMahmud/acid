package nodomain.freeyourgadget.gadgetbridge.entities;

import java.util.List;
import p008de.greenrobot.dao.DaoException;

public class ActivityDescription {
    private transient DaoSession daoSession;
    private String details;

    /* renamed from: id */
    private Long f135id;
    private transient ActivityDescriptionDao myDao;
    private List<Tag> tagList;
    private int timestampFrom;
    private int timestampTo;
    private User user;
    private long userId;
    private Long user__resolvedKey;

    public ActivityDescription() {
    }

    public ActivityDescription(Long id) {
        this.f135id = id;
    }

    public ActivityDescription(Long id, int timestampFrom2, int timestampTo2, String details2, long userId2) {
        this.f135id = id;
        this.timestampFrom = timestampFrom2;
        this.timestampTo = timestampTo2;
        this.details = details2;
        this.userId = userId2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getActivityDescriptionDao() : null;
    }

    public Long getId() {
        return this.f135id;
    }

    public void setId(Long id) {
        this.f135id = id;
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

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details2) {
        this.details = details2;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId2) {
        this.userId = userId2;
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

    public List<Tag> getTagList() {
        if (this.tagList == null) {
            DaoSession daoSession2 = this.daoSession;
            if (daoSession2 != null) {
                List<Tag> tagListNew = daoSession2.getTagDao()._queryActivityDescription_TagList(this.f135id.longValue());
                synchronized (this) {
                    if (this.tagList == null) {
                        this.tagList = tagListNew;
                    }
                }
            } else {
                throw new DaoException("Entity is detached from DAO context");
            }
        }
        return this.tagList;
    }

    public synchronized void resetTagList() {
        this.tagList = null;
    }

    public void delete() {
        ActivityDescriptionDao activityDescriptionDao = this.myDao;
        if (activityDescriptionDao != null) {
            activityDescriptionDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        ActivityDescriptionDao activityDescriptionDao = this.myDao;
        if (activityDescriptionDao != null) {
            activityDescriptionDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        ActivityDescriptionDao activityDescriptionDao = this.myDao;
        if (activityDescriptionDao != null) {
            activityDescriptionDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
