package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.AbstractDaoSession;
import p008de.greenrobot.dao.identityscope.IdentityScopeType;
import p008de.greenrobot.dao.internal.DaoConfig;

public class DaoSession extends AbstractDaoSession {
    private final ActivityDescTagLinkDao activityDescTagLinkDao = new ActivityDescTagLinkDao(this.activityDescTagLinkDaoConfig, this);
    private final DaoConfig activityDescTagLinkDaoConfig;
    private final ActivityDescriptionDao activityDescriptionDao = new ActivityDescriptionDao(this.activityDescriptionDaoConfig, this);
    private final DaoConfig activityDescriptionDaoConfig;
    private final AlarmDao alarmDao = new AlarmDao(this.alarmDaoConfig, this);
    private final DaoConfig alarmDaoConfig;
    private final BaseActivitySummaryDao baseActivitySummaryDao = new BaseActivitySummaryDao(this.baseActivitySummaryDaoConfig, this);
    private final DaoConfig baseActivitySummaryDaoConfig;
    private final CalendarSyncStateDao calendarSyncStateDao = new CalendarSyncStateDao(this.calendarSyncStateDaoConfig, this);
    private final DaoConfig calendarSyncStateDaoConfig;
    private final DeviceAttributesDao deviceAttributesDao = new DeviceAttributesDao(this.deviceAttributesDaoConfig, this);
    private final DaoConfig deviceAttributesDaoConfig;
    private final DeviceDao deviceDao = new DeviceDao(this.deviceDaoConfig, this);
    private final DaoConfig deviceDaoConfig;
    private final HPlusHealthActivityOverlayDao hPlusHealthActivityOverlayDao = new HPlusHealthActivityOverlayDao(this.hPlusHealthActivityOverlayDaoConfig, this);
    private final DaoConfig hPlusHealthActivityOverlayDaoConfig;
    private final HPlusHealthActivitySampleDao hPlusHealthActivitySampleDao = new HPlusHealthActivitySampleDao(this.hPlusHealthActivitySampleDaoConfig, this);
    private final DaoConfig hPlusHealthActivitySampleDaoConfig;
    private final ID115ActivitySampleDao iD115ActivitySampleDao = new ID115ActivitySampleDao(this.iD115ActivitySampleDaoConfig, this);
    private final DaoConfig iD115ActivitySampleDaoConfig;
    private final JYouActivitySampleDao jYouActivitySampleDao = new JYouActivitySampleDao(this.jYouActivitySampleDaoConfig, this);
    private final DaoConfig jYouActivitySampleDaoConfig;
    private final MakibesHR3ActivitySampleDao makibesHR3ActivitySampleDao = new MakibesHR3ActivitySampleDao(this.makibesHR3ActivitySampleDaoConfig, this);
    private final DaoConfig makibesHR3ActivitySampleDaoConfig;
    private final MiBandActivitySampleDao miBandActivitySampleDao = new MiBandActivitySampleDao(this.miBandActivitySampleDaoConfig, this);
    private final DaoConfig miBandActivitySampleDaoConfig;
    private final No1F1ActivitySampleDao no1F1ActivitySampleDao = new No1F1ActivitySampleDao(this.no1F1ActivitySampleDaoConfig, this);
    private final DaoConfig no1F1ActivitySampleDaoConfig;
    private final NotificationFilterDao notificationFilterDao = new NotificationFilterDao(this.notificationFilterDaoConfig, this);
    private final DaoConfig notificationFilterDaoConfig;
    private final NotificationFilterEntryDao notificationFilterEntryDao = new NotificationFilterEntryDao(this.notificationFilterEntryDaoConfig, this);
    private final DaoConfig notificationFilterEntryDaoConfig;
    private final PebbleHealthActivityOverlayDao pebbleHealthActivityOverlayDao = new PebbleHealthActivityOverlayDao(this.pebbleHealthActivityOverlayDaoConfig, this);
    private final DaoConfig pebbleHealthActivityOverlayDaoConfig;
    private final PebbleHealthActivitySampleDao pebbleHealthActivitySampleDao = new PebbleHealthActivitySampleDao(this.pebbleHealthActivitySampleDaoConfig, this);
    private final DaoConfig pebbleHealthActivitySampleDaoConfig;
    private final PebbleMisfitSampleDao pebbleMisfitSampleDao = new PebbleMisfitSampleDao(this.pebbleMisfitSampleDaoConfig, this);
    private final DaoConfig pebbleMisfitSampleDaoConfig;
    private final PebbleMorpheuzSampleDao pebbleMorpheuzSampleDao = new PebbleMorpheuzSampleDao(this.pebbleMorpheuzSampleDaoConfig, this);
    private final DaoConfig pebbleMorpheuzSampleDaoConfig;
    private final TagDao tagDao = new TagDao(this.tagDaoConfig, this);
    private final DaoConfig tagDaoConfig;
    private final UserAttributesDao userAttributesDao = new UserAttributesDao(this.userAttributesDaoConfig, this);
    private final DaoConfig userAttributesDaoConfig;
    private final UserDao userDao = new UserDao(this.userDaoConfig, this);
    private final DaoConfig userDaoConfig;
    private final XWatchActivitySampleDao xWatchActivitySampleDao = new XWatchActivitySampleDao(this.xWatchActivitySampleDaoConfig, this);
    private final DaoConfig xWatchActivitySampleDaoConfig;
    private final ZeTimeActivitySampleDao zeTimeActivitySampleDao = new ZeTimeActivitySampleDao(this.zeTimeActivitySampleDaoConfig, this);
    private final DaoConfig zeTimeActivitySampleDaoConfig;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
        super(db);
        this.userAttributesDaoConfig = daoConfigMap.get(UserAttributesDao.class).clone();
        this.userAttributesDaoConfig.initIdentityScope(type);
        this.userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        this.userDaoConfig.initIdentityScope(type);
        this.deviceAttributesDaoConfig = daoConfigMap.get(DeviceAttributesDao.class).clone();
        this.deviceAttributesDaoConfig.initIdentityScope(type);
        this.deviceDaoConfig = daoConfigMap.get(DeviceDao.class).clone();
        this.deviceDaoConfig.initIdentityScope(type);
        this.tagDaoConfig = daoConfigMap.get(TagDao.class).clone();
        this.tagDaoConfig.initIdentityScope(type);
        this.activityDescriptionDaoConfig = daoConfigMap.get(ActivityDescriptionDao.class).clone();
        this.activityDescriptionDaoConfig.initIdentityScope(type);
        this.activityDescTagLinkDaoConfig = daoConfigMap.get(ActivityDescTagLinkDao.class).clone();
        this.activityDescTagLinkDaoConfig.initIdentityScope(type);
        this.makibesHR3ActivitySampleDaoConfig = daoConfigMap.get(MakibesHR3ActivitySampleDao.class).clone();
        this.makibesHR3ActivitySampleDaoConfig.initIdentityScope(type);
        this.miBandActivitySampleDaoConfig = daoConfigMap.get(MiBandActivitySampleDao.class).clone();
        this.miBandActivitySampleDaoConfig.initIdentityScope(type);
        this.pebbleHealthActivitySampleDaoConfig = daoConfigMap.get(PebbleHealthActivitySampleDao.class).clone();
        this.pebbleHealthActivitySampleDaoConfig.initIdentityScope(type);
        this.pebbleHealthActivityOverlayDaoConfig = daoConfigMap.get(PebbleHealthActivityOverlayDao.class).clone();
        this.pebbleHealthActivityOverlayDaoConfig.initIdentityScope(type);
        this.pebbleMisfitSampleDaoConfig = daoConfigMap.get(PebbleMisfitSampleDao.class).clone();
        this.pebbleMisfitSampleDaoConfig.initIdentityScope(type);
        this.pebbleMorpheuzSampleDaoConfig = daoConfigMap.get(PebbleMorpheuzSampleDao.class).clone();
        this.pebbleMorpheuzSampleDaoConfig.initIdentityScope(type);
        this.hPlusHealthActivityOverlayDaoConfig = daoConfigMap.get(HPlusHealthActivityOverlayDao.class).clone();
        this.hPlusHealthActivityOverlayDaoConfig.initIdentityScope(type);
        this.hPlusHealthActivitySampleDaoConfig = daoConfigMap.get(HPlusHealthActivitySampleDao.class).clone();
        this.hPlusHealthActivitySampleDaoConfig.initIdentityScope(type);
        this.no1F1ActivitySampleDaoConfig = daoConfigMap.get(No1F1ActivitySampleDao.class).clone();
        this.no1F1ActivitySampleDaoConfig.initIdentityScope(type);
        this.xWatchActivitySampleDaoConfig = daoConfigMap.get(XWatchActivitySampleDao.class).clone();
        this.xWatchActivitySampleDaoConfig.initIdentityScope(type);
        this.zeTimeActivitySampleDaoConfig = daoConfigMap.get(ZeTimeActivitySampleDao.class).clone();
        this.zeTimeActivitySampleDaoConfig.initIdentityScope(type);
        this.iD115ActivitySampleDaoConfig = daoConfigMap.get(ID115ActivitySampleDao.class).clone();
        this.iD115ActivitySampleDaoConfig.initIdentityScope(type);
        this.jYouActivitySampleDaoConfig = daoConfigMap.get(JYouActivitySampleDao.class).clone();
        this.jYouActivitySampleDaoConfig.initIdentityScope(type);
        this.calendarSyncStateDaoConfig = daoConfigMap.get(CalendarSyncStateDao.class).clone();
        this.calendarSyncStateDaoConfig.initIdentityScope(type);
        this.alarmDaoConfig = daoConfigMap.get(AlarmDao.class).clone();
        this.alarmDaoConfig.initIdentityScope(type);
        this.notificationFilterDaoConfig = daoConfigMap.get(NotificationFilterDao.class).clone();
        this.notificationFilterDaoConfig.initIdentityScope(type);
        this.notificationFilterEntryDaoConfig = daoConfigMap.get(NotificationFilterEntryDao.class).clone();
        this.notificationFilterEntryDaoConfig.initIdentityScope(type);
        this.baseActivitySummaryDaoConfig = daoConfigMap.get(BaseActivitySummaryDao.class).clone();
        this.baseActivitySummaryDaoConfig.initIdentityScope(type);
        registerDao(UserAttributes.class, this.userAttributesDao);
        registerDao(User.class, this.userDao);
        registerDao(DeviceAttributes.class, this.deviceAttributesDao);
        registerDao(Device.class, this.deviceDao);
        registerDao(Tag.class, this.tagDao);
        registerDao(ActivityDescription.class, this.activityDescriptionDao);
        registerDao(ActivityDescTagLink.class, this.activityDescTagLinkDao);
        registerDao(MakibesHR3ActivitySample.class, this.makibesHR3ActivitySampleDao);
        registerDao(MiBandActivitySample.class, this.miBandActivitySampleDao);
        registerDao(PebbleHealthActivitySample.class, this.pebbleHealthActivitySampleDao);
        registerDao(PebbleHealthActivityOverlay.class, this.pebbleHealthActivityOverlayDao);
        registerDao(PebbleMisfitSample.class, this.pebbleMisfitSampleDao);
        registerDao(PebbleMorpheuzSample.class, this.pebbleMorpheuzSampleDao);
        registerDao(HPlusHealthActivityOverlay.class, this.hPlusHealthActivityOverlayDao);
        registerDao(HPlusHealthActivitySample.class, this.hPlusHealthActivitySampleDao);
        registerDao(No1F1ActivitySample.class, this.no1F1ActivitySampleDao);
        registerDao(XWatchActivitySample.class, this.xWatchActivitySampleDao);
        registerDao(ZeTimeActivitySample.class, this.zeTimeActivitySampleDao);
        registerDao(ID115ActivitySample.class, this.iD115ActivitySampleDao);
        registerDao(JYouActivitySample.class, this.jYouActivitySampleDao);
        registerDao(CalendarSyncState.class, this.calendarSyncStateDao);
        registerDao(Alarm.class, this.alarmDao);
        registerDao(NotificationFilter.class, this.notificationFilterDao);
        registerDao(NotificationFilterEntry.class, this.notificationFilterEntryDao);
        registerDao(BaseActivitySummary.class, this.baseActivitySummaryDao);
    }

    public void clear() {
        this.userAttributesDaoConfig.getIdentityScope().clear();
        this.userDaoConfig.getIdentityScope().clear();
        this.deviceAttributesDaoConfig.getIdentityScope().clear();
        this.deviceDaoConfig.getIdentityScope().clear();
        this.tagDaoConfig.getIdentityScope().clear();
        this.activityDescriptionDaoConfig.getIdentityScope().clear();
        this.activityDescTagLinkDaoConfig.getIdentityScope().clear();
        this.makibesHR3ActivitySampleDaoConfig.getIdentityScope().clear();
        this.miBandActivitySampleDaoConfig.getIdentityScope().clear();
        this.pebbleHealthActivitySampleDaoConfig.getIdentityScope().clear();
        this.pebbleHealthActivityOverlayDaoConfig.getIdentityScope().clear();
        this.pebbleMisfitSampleDaoConfig.getIdentityScope().clear();
        this.pebbleMorpheuzSampleDaoConfig.getIdentityScope().clear();
        this.hPlusHealthActivityOverlayDaoConfig.getIdentityScope().clear();
        this.hPlusHealthActivitySampleDaoConfig.getIdentityScope().clear();
        this.no1F1ActivitySampleDaoConfig.getIdentityScope().clear();
        this.xWatchActivitySampleDaoConfig.getIdentityScope().clear();
        this.zeTimeActivitySampleDaoConfig.getIdentityScope().clear();
        this.iD115ActivitySampleDaoConfig.getIdentityScope().clear();
        this.jYouActivitySampleDaoConfig.getIdentityScope().clear();
        this.calendarSyncStateDaoConfig.getIdentityScope().clear();
        this.alarmDaoConfig.getIdentityScope().clear();
        this.notificationFilterDaoConfig.getIdentityScope().clear();
        this.notificationFilterEntryDaoConfig.getIdentityScope().clear();
        this.baseActivitySummaryDaoConfig.getIdentityScope().clear();
    }

    public UserAttributesDao getUserAttributesDao() {
        return this.userAttributesDao;
    }

    public UserDao getUserDao() {
        return this.userDao;
    }

    public DeviceAttributesDao getDeviceAttributesDao() {
        return this.deviceAttributesDao;
    }

    public DeviceDao getDeviceDao() {
        return this.deviceDao;
    }

    public TagDao getTagDao() {
        return this.tagDao;
    }

    public ActivityDescriptionDao getActivityDescriptionDao() {
        return this.activityDescriptionDao;
    }

    public ActivityDescTagLinkDao getActivityDescTagLinkDao() {
        return this.activityDescTagLinkDao;
    }

    public MakibesHR3ActivitySampleDao getMakibesHR3ActivitySampleDao() {
        return this.makibesHR3ActivitySampleDao;
    }

    public MiBandActivitySampleDao getMiBandActivitySampleDao() {
        return this.miBandActivitySampleDao;
    }

    public PebbleHealthActivitySampleDao getPebbleHealthActivitySampleDao() {
        return this.pebbleHealthActivitySampleDao;
    }

    public PebbleHealthActivityOverlayDao getPebbleHealthActivityOverlayDao() {
        return this.pebbleHealthActivityOverlayDao;
    }

    public PebbleMisfitSampleDao getPebbleMisfitSampleDao() {
        return this.pebbleMisfitSampleDao;
    }

    public PebbleMorpheuzSampleDao getPebbleMorpheuzSampleDao() {
        return this.pebbleMorpheuzSampleDao;
    }

    public HPlusHealthActivityOverlayDao getHPlusHealthActivityOverlayDao() {
        return this.hPlusHealthActivityOverlayDao;
    }

    public HPlusHealthActivitySampleDao getHPlusHealthActivitySampleDao() {
        return this.hPlusHealthActivitySampleDao;
    }

    public No1F1ActivitySampleDao getNo1F1ActivitySampleDao() {
        return this.no1F1ActivitySampleDao;
    }

    public XWatchActivitySampleDao getXWatchActivitySampleDao() {
        return this.xWatchActivitySampleDao;
    }

    public ZeTimeActivitySampleDao getZeTimeActivitySampleDao() {
        return this.zeTimeActivitySampleDao;
    }

    public ID115ActivitySampleDao getID115ActivitySampleDao() {
        return this.iD115ActivitySampleDao;
    }

    public JYouActivitySampleDao getJYouActivitySampleDao() {
        return this.jYouActivitySampleDao;
    }

    public CalendarSyncStateDao getCalendarSyncStateDao() {
        return this.calendarSyncStateDao;
    }

    public AlarmDao getAlarmDao() {
        return this.alarmDao;
    }

    public NotificationFilterDao getNotificationFilterDao() {
        return this.notificationFilterDao;
    }

    public NotificationFilterEntryDao getNotificationFilterEntryDao() {
        return this.notificationFilterEntryDao;
    }

    public BaseActivitySummaryDao getBaseActivitySummaryDao() {
        return this.baseActivitySummaryDao;
    }
}
