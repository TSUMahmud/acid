package nodomain.freeyourgadget.gadgetbridge.entities;

import p008de.greenrobot.dao.DaoException;

public class NotificationFilterEntry {
    private transient DaoSession daoSession;

    /* renamed from: id */
    private Long f147id;
    private transient NotificationFilterEntryDao myDao;
    private NotificationFilter notificationFilter;
    private String notificationFilterContent;
    private long notificationFilterId;
    private Long notificationFilter__resolvedKey;

    public NotificationFilterEntry() {
    }

    public NotificationFilterEntry(Long id) {
        this.f147id = id;
    }

    public NotificationFilterEntry(Long id, long notificationFilterId2, String notificationFilterContent2) {
        this.f147id = id;
        this.notificationFilterId = notificationFilterId2;
        this.notificationFilterContent = notificationFilterContent2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getNotificationFilterEntryDao() : null;
    }

    public Long getId() {
        return this.f147id;
    }

    public void setId(Long id) {
        this.f147id = id;
    }

    public long getNotificationFilterId() {
        return this.notificationFilterId;
    }

    public void setNotificationFilterId(long notificationFilterId2) {
        this.notificationFilterId = notificationFilterId2;
    }

    public String getNotificationFilterContent() {
        return this.notificationFilterContent;
    }

    public void setNotificationFilterContent(String notificationFilterContent2) {
        this.notificationFilterContent = notificationFilterContent2;
    }

    public NotificationFilter getNotificationFilter() {
        long __key = this.notificationFilterId;
        Long l = this.notificationFilter__resolvedKey;
        if (l == null || !l.equals(Long.valueOf(__key))) {
            DaoSession daoSession2 = this.daoSession;
            if (daoSession2 != null) {
                NotificationFilter notificationFilterNew = (NotificationFilter) daoSession2.getNotificationFilterDao().load(Long.valueOf(__key));
                synchronized (this) {
                    this.notificationFilter = notificationFilterNew;
                    this.notificationFilter__resolvedKey = Long.valueOf(__key);
                }
            } else {
                throw new DaoException("Entity is detached from DAO context");
            }
        }
        return this.notificationFilter;
    }

    public void setNotificationFilter(NotificationFilter notificationFilter2) {
        if (notificationFilter2 != null) {
            synchronized (this) {
                this.notificationFilter = notificationFilter2;
                this.notificationFilterId = notificationFilter2.getId().longValue();
                this.notificationFilter__resolvedKey = Long.valueOf(this.notificationFilterId);
            }
            return;
        }
        throw new DaoException("To-one property 'notificationFilterId' has not-null constraint; cannot set to-one to null");
    }

    public void delete() {
        NotificationFilterEntryDao notificationFilterEntryDao = this.myDao;
        if (notificationFilterEntryDao != null) {
            notificationFilterEntryDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        NotificationFilterEntryDao notificationFilterEntryDao = this.myDao;
        if (notificationFilterEntryDao != null) {
            notificationFilterEntryDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        NotificationFilterEntryDao notificationFilterEntryDao = this.myDao;
        if (notificationFilterEntryDao != null) {
            notificationFilterEntryDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
