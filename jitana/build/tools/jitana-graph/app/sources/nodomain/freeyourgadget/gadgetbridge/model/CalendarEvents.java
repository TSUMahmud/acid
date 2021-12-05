package nodomain.freeyourgadget.gadgetbridge.model;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.Time;
import cyanogenmod.providers.ThemesContract;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarEvents {
    private static final String[] EVENT_INSTANCE_PROJECTION = {"_id", "begin", "end", C1238GB.DISPLAY_MESSAGE_DURATION, ThemesContract.ThemesColumns.TITLE, "description", "eventLocation", "calendar_displayName", "allDay"};
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CalendarEvents.class);
    private static final int lookahead_days = 7;
    private List<CalendarEvent> calendarEventList = new ArrayList();

    public List<CalendarEvent> getCalendarEventList(Context mContext) {
        fetchSystemEvents(mContext);
        return this.calendarEventList;
    }

    private boolean fetchSystemEvents(Context mContext) {
        Throwable th;
        Throwable th2;
        long end;
        Calendar cal;
        Calendar cal2 = GregorianCalendar.getInstance();
        long dtStart = cal2.getTimeInMillis();
        int i = 7;
        int i2 = 5;
        cal2.add(5, 7);
        long dtEnd = cal2.getTimeInMillis();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, dtStart);
        ContentUris.appendId(eventsUriBuilder, dtEnd);
        Cursor evtCursor = mContext.getContentResolver().query(eventsUriBuilder.build(), EVENT_INSTANCE_PROJECTION, (String) null, (String[]) null, "begin ASC");
        boolean z = false;
        if (evtCursor != null) {
            try {
                if (evtCursor.getCount() == 0) {
                    Calendar calendar = cal2;
                } else {
                    while (evtCursor.moveToNext()) {
                        long start = evtCursor.getLong(1);
                        long end2 = evtCursor.getLong(2);
                        if (end2 == 0) {
                            try {
                                LOG.info("no end time, will parse duration string");
                                Time time = new Time();
                                time.parse(evtCursor.getString(3));
                                end = start + time.toMillis(z);
                            } catch (Throwable th3) {
                                Calendar calendar2 = cal2;
                                th = th3;
                                try {
                                    throw th;
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            }
                        } else {
                            end = end2;
                        }
                        CalendarEvent calEvent = new CalendarEvent(start, end, evtCursor.getLong(z ? 1 : 0), evtCursor.getString(4), evtCursor.getString(i2), evtCursor.getString(6), evtCursor.getString(i), !evtCursor.getString(8).equals("0"));
                        if (!GBApplication.calendarIsBlacklisted(calEvent.getCalName())) {
                            try {
                                this.calendarEventList.add(calEvent);
                                cal = cal2;
                            } catch (Throwable th5) {
                                Calendar calendar3 = cal2;
                                th = th5;
                                throw th;
                            }
                        } else {
                            try {
                                Logger logger = LOG;
                                StringBuilder sb = new StringBuilder();
                                cal = cal2;
                                try {
                                    sb.append("calendar ");
                                    sb.append(calEvent.getCalName());
                                    sb.append(" skipped because it's blacklisted");
                                    logger.debug(sb.toString());
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                Calendar calendar4 = cal2;
                                th = th;
                                throw th;
                            }
                        }
                        cal2 = cal;
                        i = 7;
                        i2 = 5;
                        z = false;
                    }
                    Calendar calendar5 = cal2;
                    if (evtCursor == null) {
                        return true;
                    }
                    evtCursor.close();
                    return true;
                }
            } catch (Throwable th8) {
                th = th8;
                Calendar calendar42 = cal2;
                th = th;
                throw th;
            }
        } else {
            Calendar calendar6 = cal2;
        }
        if (evtCursor == null) {
            return false;
        }
        evtCursor.close();
        return false;
        throw th2;
    }

    public static class CalendarEvent {
        private boolean allDay;
        private long begin;
        private String calName;
        private String description;
        private long end;

        /* renamed from: id */
        private long f157id;
        private String location;
        private String title;

        public CalendarEvent(long begin2, long end2, long id, String title2, String description2, String location2, String calName2, boolean allDay2) {
            this.begin = begin2;
            this.end = end2;
            this.f157id = id;
            this.title = title2;
            this.description = description2;
            this.location = location2;
            this.calName = calName2;
            this.allDay = allDay2;
        }

        public long getBegin() {
            return this.begin;
        }

        public int getBeginSeconds() {
            return (int) (this.begin / 1000);
        }

        public long getEnd() {
            return this.end;
        }

        public long getDuration() {
            return this.end - this.begin;
        }

        public int getDurationSeconds() {
            return (int) (getDuration() / 1000);
        }

        public short getDurationMinutes() {
            return (short) (getDurationSeconds() / 60);
        }

        public long getId() {
            return this.f157id;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }

        public String getLocation() {
            return this.location;
        }

        public String getCalName() {
            return this.calName;
        }

        public boolean isAllDay() {
            return this.allDay;
        }

        public boolean equals(Object other) {
            if (!(other instanceof CalendarEvent)) {
                return false;
            }
            CalendarEvent e = (CalendarEvent) other;
            if (getId() != e.getId() || !Objects.equals(getTitle(), e.getTitle()) || getBegin() != e.getBegin() || !Objects.equals(getLocation(), e.getLocation()) || !Objects.equals(getDescription(), e.getDescription()) || getEnd() != e.getEnd() || !Objects.equals(getCalName(), e.getCalName()) || isAllDay() != e.isAllDay()) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (((((((((((((((int) this.f157id) * 31) + Objects.hash(new Object[]{this.title})) * 31) + Long.valueOf(this.begin).hashCode()) * 31) + Objects.hash(new Object[]{this.location})) * 31) + Objects.hash(new Object[]{this.description})) * 31) + Long.valueOf(this.end).hashCode()) * 31) + Objects.hash(new Object[]{this.calName})) * 31) + Boolean.valueOf(this.allDay).hashCode();
        }
    }
}
