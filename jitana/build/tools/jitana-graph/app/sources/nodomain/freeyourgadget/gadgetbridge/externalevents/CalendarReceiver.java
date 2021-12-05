package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.entities.CalendarSyncState;
import nodomain.freeyourgadget.gadgetbridge.entities.CalendarSyncStateDao;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p008de.greenrobot.dao.query.QueryBuilder;
import p008de.greenrobot.dao.query.WhereCondition;

public class CalendarReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CalendarReceiver.class);
    private Hashtable<Long, EventSyncState> eventState = new Hashtable<>();
    private GBDevice mGBDevice;

    private class EventSyncState {
        /* access modifiers changed from: private */
        public CalendarEvents.CalendarEvent event;
        private int state;

        EventSyncState(CalendarEvents.CalendarEvent event2, int state2) {
            this.state = state2;
            this.event = event2;
        }

        public int getState() {
            return this.state;
        }

        public void setState(int state2) {
            this.state = state2;
        }

        public CalendarEvents.CalendarEvent getEvent() {
            return this.event;
        }

        public void setEvent(CalendarEvents.CalendarEvent event2) {
            this.event = event2;
        }
    }

    private static class EventState {
        private static final int NEEDS_DELETE = 3;
        private static final int NEEDS_UPDATE = 2;
        private static final int NOT_SYNCED = 0;
        private static final int SYNCED = 1;

        private EventState() {
        }
    }

    public CalendarReceiver(GBDevice gbDevice) {
        LOG.info("Created calendar receiver.");
        this.mGBDevice = gbDevice;
        onReceive(GBApplication.getContext(), new Intent());
    }

    public void onReceive(Context context, Intent intent) {
        LOG.info("got calendar changed broadcast");
        syncCalendar(new CalendarEvents().getCalendarEventList(GBApplication.getContext()));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0013, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0014, code lost:
        if (r0 != null) goto L_0x0016;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001e, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void syncCalendar(java.util.List<nodomain.freeyourgadget.gadgetbridge.model.CalendarEvents.CalendarEvent> r5) {
        /*
            r4 = this;
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x001f }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r1 = r0.getDaoSession()     // Catch:{ all -> 0x0011 }
            r4.syncCalendar(r5, r1)     // Catch:{ all -> 0x0011 }
            if (r0 == 0) goto L_0x0010
            r0.close()     // Catch:{ Exception -> 0x001f }
        L_0x0010:
            goto L_0x0027
        L_0x0011:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0013 }
        L_0x0013:
            r2 = move-exception
            if (r0 == 0) goto L_0x001e
            r0.close()     // Catch:{ all -> 0x001a }
            goto L_0x001e
        L_0x001a:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ Exception -> 0x001f }
        L_0x001e:
            throw r2     // Catch:{ Exception -> 0x001f }
        L_0x001f:
            r0 = move-exception
            r1 = 0
            r2 = 3
            java.lang.String r3 = "Database Error while syncing Calendar"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r3, r1, r2)
        L_0x0027:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.externalevents.CalendarReceiver.syncCalendar(java.util.List):void");
    }

    public void syncCalendar(List<CalendarEvents.CalendarEvent> eventList, DaoSession session) {
        DaoSession daoSession = session;
        LOG.info("Syncing with calendar.");
        Hashtable<Long, CalendarEvents.CalendarEvent> eventTable = new Hashtable<>();
        Long deviceId = DBHelper.getDevice(this.mGBDevice, daoSession).getId();
        QueryBuilder<CalendarSyncState> qb = session.getCalendarSyncStateDao().queryBuilder();
        for (CalendarEvents.CalendarEvent e : eventList) {
            long id = e.getId();
            eventTable.put(Long.valueOf(id), e);
            if (!this.eventState.containsKey(Long.valueOf(e.getId()))) {
                qb = session.getCalendarSyncStateDao().queryBuilder();
                CalendarSyncState calendarSyncState = qb.where(qb.and(CalendarSyncStateDao.Properties.DeviceId.mo14989eq(deviceId), CalendarSyncStateDao.Properties.CalendarEntryId.mo14989eq(Long.valueOf(id)), new WhereCondition[0]), new WhereCondition[0]).build().unique();
                if (calendarSyncState == null) {
                    this.eventState.put(Long.valueOf(id), new EventSyncState(e, 0));
                    Logger logger = LOG;
                    logger.info("event id=" + id + " is yet unknown to device id=" + deviceId);
                } else if (calendarSyncState.getHash() == e.hashCode()) {
                    this.eventState.put(Long.valueOf(id), new EventSyncState(e, 1));
                    Logger logger2 = LOG;
                    logger2.info("event id=" + id + " is up to date on device id=" + deviceId);
                } else {
                    this.eventState.put(Long.valueOf(id), new EventSyncState(e, 2));
                    Logger logger3 = LOG;
                    logger3.info("event id=" + id + " is not up to date on device id=" + deviceId);
                }
            }
        }
        for (CalendarSyncState CalendarSyncState : qb.where(CalendarSyncStateDao.Properties.DeviceId.mo14989eq(deviceId), new WhereCondition[0]).build().list()) {
            if (!this.eventState.containsKey(Long.valueOf(CalendarSyncState.getCalendarEntryId()))) {
                this.eventState.put(Long.valueOf(CalendarSyncState.getCalendarEntryId()), new EventSyncState((CalendarEvents.CalendarEvent) null, 3));
                Logger logger4 = LOG;
                logger4.info("insert null event for orphanded calendar id=" + CalendarSyncState.getCalendarEntryId() + " for device=" + this.mGBDevice.getName());
            }
        }
        Enumeration<Long> ids = this.eventState.keys();
        while (ids.hasMoreElements()) {
            QueryBuilder<CalendarSyncState> qb2 = session.getCalendarSyncStateDao().queryBuilder();
            Long i = ids.nextElement();
            EventSyncState es = this.eventState.get(i);
            if (eventTable.containsKey(i)) {
                if (es.getState() == 1 && !es.getEvent().equals(eventTable.get(i))) {
                    this.eventState.put(i, new EventSyncState(eventTable.get(i), 2));
                }
            } else if (es.getState() == 0) {
                qb2.where(qb2.and(CalendarSyncStateDao.Properties.DeviceId.mo14989eq(deviceId), CalendarSyncStateDao.Properties.CalendarEntryId.mo14989eq(i), new WhereCondition[0]), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
                this.eventState.remove(i);
            } else {
                es.setState(3);
                this.eventState.put(i, es);
            }
            updateEvents(deviceId, daoSession);
        }
    }

    private void updateEvents(Long deviceId, DaoSession session) {
        Enumeration<Long> ids = this.eventState.keys();
        while (ids.hasMoreElements()) {
            Long i = ids.nextElement();
            EventSyncState es = this.eventState.get(i);
            int syncState = es.getState();
            if (syncState == 0) {
                Long l = deviceId;
            } else if (syncState == 2) {
                Long l2 = deviceId;
            } else if (syncState == 3) {
                GBApplication.deviceService().onDeleteCalendarEvent((byte) 0, i.longValue());
                this.eventState.remove(i);
                QueryBuilder<CalendarSyncState> qb = session.getCalendarSyncStateDao().queryBuilder();
                qb.where(qb.and(CalendarSyncStateDao.Properties.DeviceId.mo14989eq(deviceId), CalendarSyncStateDao.Properties.CalendarEntryId.mo14989eq(i), new WhereCondition[0]), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
                DaoSession daoSession = session;
            } else {
                Long l3 = deviceId;
                DaoSession daoSession2 = session;
            }
            CalendarEvents.CalendarEvent calendarEvent = es.getEvent();
            CalendarEventSpec calendarEventSpec = new CalendarEventSpec();
            calendarEventSpec.f156id = i.longValue();
            calendarEventSpec.title = calendarEvent.getTitle();
            calendarEventSpec.allDay = calendarEvent.isAllDay();
            calendarEventSpec.timestamp = calendarEvent.getBeginSeconds();
            calendarEventSpec.durationInSeconds = calendarEvent.getDurationSeconds();
            if (calendarEvent.isAllDay()) {
                Calendar c = GregorianCalendar.getInstance();
                c.setTimeInMillis(calendarEvent.getBegin());
                c.set(10, 0);
                calendarEventSpec.timestamp = (int) (c.getTimeInMillis() / 1000);
                calendarEventSpec.durationInSeconds = 86400;
            }
            calendarEventSpec.description = calendarEvent.getDescription();
            calendarEventSpec.location = calendarEvent.getLocation();
            calendarEventSpec.type = 0;
            if (syncState == 2) {
                GBApplication.deviceService().onDeleteCalendarEvent((byte) 0, i.longValue());
            }
            GBApplication.deviceService().onAddCalendarEvent(calendarEventSpec);
            es.setState(1);
            this.eventState.put(i, es);
            session.insertOrReplace(new CalendarSyncState((Long) null, deviceId.longValue(), i.longValue(), es.event.hashCode()));
        }
        Long l4 = deviceId;
        DaoSession daoSession3 = session;
    }
}
