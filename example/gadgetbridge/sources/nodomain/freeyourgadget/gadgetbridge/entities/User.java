package nodomain.freeyourgadget.gadgetbridge.entities;

import java.util.Date;
import java.util.List;
import p008de.greenrobot.dao.DaoException;

public class User {
    private Date birthday;
    private transient DaoSession daoSession;
    private int gender;

    /* renamed from: id */
    private Long f151id;
    private transient UserDao myDao;
    private String name;
    private List<UserAttributes> userAttributesList;

    public User() {
    }

    public User(Long id) {
        this.f151id = id;
    }

    public User(Long id, String name2, Date birthday2, int gender2) {
        this.f151id = id;
        this.name = name2;
        this.birthday = birthday2;
        this.gender = gender2;
    }

    public void __setDaoSession(DaoSession daoSession2) {
        this.daoSession = daoSession2;
        this.myDao = daoSession2 != null ? daoSession2.getUserDao() : null;
    }

    public Long getId() {
        return this.f151id;
    }

    public void setId(Long id) {
        this.f151id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday2) {
        this.birthday = birthday2;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender2) {
        this.gender = gender2;
    }

    public List<UserAttributes> getUserAttributesList() {
        if (this.userAttributesList == null) {
            DaoSession daoSession2 = this.daoSession;
            if (daoSession2 != null) {
                List<UserAttributes> userAttributesListNew = daoSession2.getUserAttributesDao()._queryUser_UserAttributesList(this.f151id.longValue());
                synchronized (this) {
                    if (this.userAttributesList == null) {
                        this.userAttributesList = userAttributesListNew;
                    }
                }
            } else {
                throw new DaoException("Entity is detached from DAO context");
            }
        }
        return this.userAttributesList;
    }

    public synchronized void resetUserAttributesList() {
        this.userAttributesList = null;
    }

    public void delete() {
        UserDao userDao = this.myDao;
        if (userDao != null) {
            userDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void update() {
        UserDao userDao = this.myDao;
        if (userDao != null) {
            userDao.update(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void refresh() {
        UserDao userDao = this.myDao;
        if (userDao != null) {
            userDao.refresh(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }
}
