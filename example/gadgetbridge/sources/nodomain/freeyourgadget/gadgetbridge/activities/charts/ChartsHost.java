package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.view.ViewGroup;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public interface ChartsHost {
    public static final String DATE_NEXT_DAY = ChartsActivity.class.getName().concat(".date_next_day");
    public static final String DATE_NEXT_MONTH = ChartsActivity.class.getName().concat(".date_next_month");
    public static final String DATE_NEXT_WEEK = ChartsActivity.class.getName().concat(".date_next_week");
    public static final String DATE_PREV_DAY = ChartsActivity.class.getName().concat(".date_prev_day");
    public static final String DATE_PREV_MONTH = ChartsActivity.class.getName().concat(".date_prev_month");
    public static final String DATE_PREV_WEEK = ChartsActivity.class.getName().concat(".date_prev_week");
    public static final String REFRESH = ChartsActivity.class.getName().concat(".refresh");

    ViewGroup getDateBar();

    GBDevice getDevice();

    Date getEndDate();

    Date getStartDate();

    void setDateInfo(String str);

    void setEndDate(Date date);

    void setStartDate(Date date);
}
