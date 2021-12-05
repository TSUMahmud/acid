package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimestampValueFormatter extends ValueFormatter {
    private final Calendar cal;
    private DateFormat dateFormat;

    public TimestampValueFormatter() {
        this(new SimpleDateFormat("HH:mm"));
    }

    public TimestampValueFormatter(DateFormat dateFormat2) {
        this.dateFormat = dateFormat2;
        this.cal = GregorianCalendar.getInstance();
        this.cal.clear();
    }

    public String getFormattedValue(float value) {
        this.cal.setTimeInMillis(((long) ((int) value)) * 1000);
        return this.dateFormat.format(this.cal.getTime());
    }
}
